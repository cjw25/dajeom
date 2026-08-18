package com.dajeom.adapter;

import com.dajeom.diagnostic.StoryGenerationDiagnosticLogger;
import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingProminence;
import com.dajeom.model.ConsultationGroundingTimelineScenario;
import com.dajeom.model.ConsultationGroundingTimelineYear;
import com.dajeom.model.ConsultationInterpretationPromptContext;
import com.dajeom.model.ConsultationInterpretationPromptSection;
import com.dajeom.model.ConsultationStoryGeneration;
import com.dajeom.type.ConsultationCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 정밀화 23차.
 *
 * OpenAI Responses API 기반 Consultation Story Generator.
 *
 * =========================================================
 * 전체 구조
 * =========================================================
 *
 * deterministic Java Grounding
 *
 * ↓
 *
 * 22차 prominence
 *
 * ↓
 *
 * Writer용 Compact Grounding
 *
 * ↓
 *
 * Writer 1회
 *
 * ↓
 *
 * Reviewer용 더 작은 Core Grounding
 *
 * ↓
 *
 * Reviewer 1회
 *
 * ↓
 *
 * Hard Safety
 *
 * ↓
 *
 * 정말 필요한 경우에만 Repair 1회
 *
 * =========================================================
 * 23차 핵심 변경
 * =========================================================
 *
 * 1.
 * Prompt Diet.
 *
 * Source 전체 / Reason 전체 / 중복 provenance 숫자를
 * OpenAI에 반복 전송하지 않는다.
 *
 * 2.
 * Reviewer Diet.
 *
 * Reviewer는 Writer와 동일한 거대 Grounding을
 * 다시 받지 않는다.
 *
 * 핵심 Evidence만 다시 확인한다.
 *
 * 3.
 * Rate Limit Recovery.
 *
 * HTTP 429는 콘텐츠 실패가 아니다.
 *
 * 해당 HTTP 요청만:
 *
 * 약 2초
 * 약 4초
 * 약 8초
 *
 * 순서로 재시도한다.
 *
 * API가 retry 시간을 알려주면
 * 그 시간도 함께 반영한다.
 *
 * 4.
 * Writer Survival.
 *
 * Writer가 Hard Safety를 통과했는데
 * Reviewer 요청만 외부 장애로 실패하면
 * 정상 Writer를 버리지 않는다.
 *
 * 5.
 * Category Isolation.
 *
 * 한 Category의 외부 LLM 실패 때문에
 * 앞에서 성공한 10개 Category를 버리지 않는다.
 *
 * 해당 Category만 안전한 기술적 fallback을 넣는다.
 *
 * =========================================================
 * 하지 않는 것
 * =========================================================
 *
 * - 새로운 사주 계산
 * - prominence를 정확도 확률로 변환
 * - prominence를 길흉 점수로 변환
 * - 없는 과거 사건 생성
 * - 미래 사건 예언
 * - 특정 사람/직업/장소 임의 생성
 * - 의료 진단
 * - 법률 결과 예측
 */
@Component
public class OpenAiConsultationStoryGenerator
        implements ConsultationStoryGenerator {

    private static final Logger log =
            LoggerFactory.getLogger(
                    OpenAiConsultationStoryGenerator.class
            );


    /*
     * =========================================================
     * API
     * =========================================================
     */

    private static final String PROVIDER =
            "OPENAI";

    private static final String OPENAI_BASE_URL =
            "https://api.openai.com";

    private static final String RESPONSES_PATH =
            "/v1/responses";

    private static final String HUMAN_GROUNDING_VERSION =
            "DAJEOM_HUMAN_COMPACT_REVIEW_23";


    /*
     * =========================================================
     * Story Soft Target
     * =========================================================
     *
     * 길이 때문에 재생성하지 않는다.
     */

    private static final int STORY_TARGET_MIN_LENGTH =
            1200;

    private static final int STORY_TARGET_MAX_LENGTH =
            1900;


    /*
     * =========================================================
     * Request Output Limit
     * =========================================================
     *
     * application 설정이 6000이어도
     * 실제 개별 호출에서는 더 작은 상한을 사용한다.
     */

    private static final int WRITER_OUTPUT_TOKEN_LIMIT =
            3000;

    private static final int REVIEWER_OUTPUT_TOKEN_LIMIT =
            3000;


    /*
     * gpt-4o-mini 자체 상한 검증용.
     */
    private static final int GPT_4O_MINI_MAX_OUTPUT_TOKENS =
            16_384;


    /*
     * =========================================================
     * Prompt Diet
     * =========================================================
     */

    private static final int WRITER_NATAL_EVIDENCE_LIMIT =
            6;

    private static final int WRITER_SCENARIO_EVIDENCE_LIMIT =
            3;

    private static final int WRITER_YEAR_EVIDENCE_LIMIT =
            3;

    private static final int REVIEWER_NATAL_EVIDENCE_LIMIT =
            3;

    private static final int REVIEWER_SCENARIO_EVIDENCE_LIMIT =
            2;

    private static final int REVIEWER_YEAR_EVIDENCE_LIMIT =
            2;

    private static final int WRITER_REASON_LIMIT =
            2;


    /*
     * =========================================================
     * 429 Retry
     * =========================================================
     */

    private static final int MAX_RATE_LIMIT_RETRIES =
            3;

    private static final long[] RATE_LIMIT_BACKOFF_MILLIS = {
            2_000L,
            4_000L,
            8_000L
    };

    private static final long MAX_RATE_LIMIT_WAIT_MILLIS =
            15_000L;

    private static final Pattern RETRY_SECONDS_PATTERN =
            Pattern.compile(
                    "try\\s+again\\s+in\\s+([0-9]+(?:\\.[0-9]+)?)s",
                    Pattern.CASE_INSENSITIVE
            );


    /*
     * =========================================================
     * Technical Fallback
     * =========================================================
     *
     * 이것은 deterministic 근거 부족 fallback과 다르다.
     *
     * 계산 근거가 없다는 거짓말을 하지 않는다.
     */

    private static final String TECHNICAL_STORY_FALLBACK =
            "이 장의 계산 근거는 준비되어 있지만 상담문을 정리하는 과정에서 "
                    +
                    "일시적인 오류가 발생했습니다. "
                    +
                    "계산되지 않은 내용을 임의로 추정해서 채우지 않았습니다.";


    /*
     * =========================================================
     * Hard Safety
     * =========================================================
     */

    private static final Pattern YEAR_PATTERN =
            Pattern.compile(
                    "(?<!\\d)(?:19|20)\\d{2}년"
            );

    private static final Pattern LATIN_TOKEN_PATTERN =
            Pattern.compile(
                    "[A-Za-z_][A-Za-z0-9_-]*"
            );

    private static final Pattern HTML_TAG_PATTERN =
            Pattern.compile(
                    "<\\s*/?\\s*[A-Za-z][^>]*>"
            );


    /*
     * =========================================================
     * 사용자 출력 금지 사주 전문용어
     * =========================================================
     *
     * 단순 contains() 대신
     * 한국어 단어 중간에 포함된 경우를 피하기 위해
     * 앞쪽 경계를 검사한다.
     *
     * 예:
     *
     * 관성
     *
     * 은 잡되
     *
     * 일관성
     *
     * 의 중간 "관성"은 잡지 않는다.
     */

    private static final List<String>
            FORBIDDEN_SAJU_TERMS =
            List.of(
                    "칠살",
                    "편관",
                    "정관",
                    "관살",
                    "관성",

                    "정재",
                    "편재",
                    "재성",

                    "비견",
                    "겁재",
                    "비겁",

                    "식신",
                    "식상",

                    "정인",
                    "편인",
                    "인성",

                    "일간",

                    "신강",
                    "신약",

                    "용신",
                    "희신",
                    "기신",

                    "격국",

                    "통근",
                    "투간",
                    "지장간",
                    "천간",

                    "합화",
                    "삼합",
                    "방합",
                    "육합",

                    "신살",

                    "대운",
                    "세운",
                    "월운",

                    "오행",

                    "십성",
                    "십신",

                    "십이운성",
                    "12운성",

                    "득령",
                    "득지",
                    "득세"
            );

    private static final String FORBIDDEN_SAJU_TERMS_PROMPT =
            String.join(
                    ", ",
                    FORBIDDEN_SAJU_TERMS
            );


    private static final List<String>
            FORBIDDEN_ADDRESS_PHRASES =
            List.of(
                    "귀하",
                    "여러분"
            );


    private static final List<String>
            FORBIDDEN_LEGAL_RESULT_PHRASES =
            List.of(
                    "승소할",
                    "승소합니다",
                    "승소하게",
                    "패소할",
                    "패소합니다",
                    "패소하게",
                    "처벌받을",
                    "처벌받습니다",
                    "처벌받게"
            );


    private static final List<String>
            FORBIDDEN_MEDICAL_ASSERTION_PHRASES =
            List.of(
                    "질병이 생길",
                    "질병이 생깁니다",
                    "병이 생길",
                    "병이 생깁니다",
                    "진단받을",
                    "진단받습니다",
                    "치료가 필요합니다",
                    "치료를 받아야"
            );


    private final JsonMapper jsonMapper;

    private final RestClient restClient;

    private final StoryGenerationDiagnosticLogger
            diagnosticLogger;

    private final String apiKey;

    private final String model;

    private final int maxOutputTokens;


    public OpenAiConsultationStoryGenerator(

            JsonMapper jsonMapper,

            StoryGenerationDiagnosticLogger
                    diagnosticLogger,

            @Value("${OPENAI_API_KEY:}")
            String apiKey,

            @Value("${OPENAI_MODEL:gpt-4o-mini}")
            String model,

            @Value("${OPENAI_MAX_OUTPUT_TOKENS:6000}")
            int maxOutputTokens

    ) {

        this.jsonMapper =
                Objects.requireNonNull(
                        jsonMapper,
                        "JsonMapper가 필요합니다."
                );

        this.diagnosticLogger =
                Objects.requireNonNull(
                        diagnosticLogger,
                        "StoryGenerationDiagnosticLogger가 필요합니다."
                );

        this.apiKey =
                apiKey == null
                        ? ""
                        : apiKey.trim();

        this.model =
                requireText(
                        model,
                        "OpenAI Model은 필수입니다."
                );

        if (
                maxOutputTokens < 1
        ) {

            throw new IllegalArgumentException(
                    "OPENAI_MAX_OUTPUT_TOKENS는 1 이상이어야 합니다."
            );
        }

        if (
                isGpt4oMiniModel(
                        this.model
                )
                        &&
                        maxOutputTokens
                                >
                                GPT_4O_MINI_MAX_OUTPUT_TOKENS
        ) {

            throw new IllegalArgumentException(
                    "GPT-4o-mini의 OPENAI_MAX_OUTPUT_TOKENS는 "
                            +
                            GPT_4O_MINI_MAX_OUTPUT_TOKENS
                            +
                            " 이하이어야 합니다. current="
                            +
                            maxOutputTokens
            );
        }

        this.maxOutputTokens =
                maxOutputTokens;

        this.restClient =
                RestClient
                        .builder()
                        .baseUrl(
                                OPENAI_BASE_URL
                        )
                        .build();
    }


    /*
     * =========================================================
     * Generation
     * =========================================================
     */

    @Override
    public Optional<ConsultationStoryGeneration> generate(

            ConsultationInterpretationPromptContext
                    promptContext

    ) {

        Objects.requireNonNull(
                promptContext,
                "Story Generation에는 ConsultationInterpretationPromptContext가 필요합니다."
        );

        String generationId =
                diagnosticLogger
                        .nextGenerationId();

        long generationStartedAt =
                System.nanoTime();

        if (
                promptContext
                        .categoryResolutionRequired()
        ) {

            diagnosticLogger
                    .generationSkippedCategoryResolutionRequired(
                            generationId,
                            model
                    );

            return Optional.empty();
        }

        if (
                !promptContext
                        .hasSections()
        ) {

            diagnosticLogger
                    .generationSkippedNoSections(
                            generationId,
                            model
                    );

            return Optional.empty();
        }

        int unavailableCount =
                (int) promptContext
                        .sections()
                        .stream()
                        .filter(
                                ConsultationInterpretationPromptSection
                                        ::unavailable
                        )
                        .count();

        List<ConsultationCategory>
                expectedCategories =
                promptContext
                        .sections()
                        .stream()
                        .filter(
                                section ->
                                        !section.unavailable()
                        )
                        .map(
                                ConsultationInterpretationPromptSection
                                        ::category
                        )
                        .distinct()
                        .sorted(
                                Comparator.comparingInt(
                                        ConsultationCategory
                                                ::getOrder
                                )
                        )
                        .toList();

        diagnosticLogger
                .generationStarted(
                        generationId,
                        model,
                        expectedCategories.size(),
                        unavailableCount
                );

        if (
                expectedCategories.isEmpty()
        ) {

            diagnosticLogger
                    .generationSkippedNoAvailableCategory(
                            generationId,
                            model
                    );

            return Optional.empty();
        }

        if (
                apiKey.isBlank()
        ) {

            diagnosticLogger
                    .generationSkippedMissingApiKey(
                            generationId,
                            model
                    );

            return Optional.empty();
        }

        EnumMap<
                ConsultationCategory,
                String
                > stories =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        /*
         * =====================================================
         * Category Isolation
         * =====================================================
         *
         * 하나 실패했다고
         * 앞에서 생성한 Story를 버리지 않는다.
         */
        for (
                ConsultationCategory category
                : expectedCategories
        ) {

            ConsultationInterpretationPromptSection section =
                    findSection(
                            promptContext,
                            category
                    );

            long categoryStartedAt =
                    System.nanoTime();

            diagnosticLogger
                    .categoryStarted(
                            generationId,
                            model,
                            category
                    );

            try {

                String storyText =
                        generateHumanReviewedStory(
                                generationId,
                                section,
                                category,
                                categoryStartedAt
                        );

                stories.put(
                        category,
                        storyText
                );

                diagnosticLogger
                        .categorySucceeded(
                                generationId,
                                model,
                                category,
                                storyText.length(),
                                elapsedMillis(
                                        categoryStartedAt
                                )
                        );

                if (
                        storyText.length()
                                <
                                STORY_TARGET_MIN_LENGTH
                                ||
                                storyText.length()
                                        >
                                        STORY_TARGET_MAX_LENGTH
                ) {

                    diagnosticLogger
                            .storyLengthOutOfRange(
                                    generationId,
                                    model,
                                    category,
                                    storyText.length(),
                                    STORY_TARGET_MIN_LENGTH,
                                    STORY_TARGET_MAX_LENGTH
                            );
                }

            }
            catch (
                    Exception exception
            ) {

                diagnosticLogger
                        .categoryFailed(
                                generationId,
                                model,
                                category,
                                elapsedMillis(
                                        categoryStartedAt
                                ),
                                exception
                        );

                /*
                 * 중요:
                 *
                 * 다른 10개 성공 결과는 유지한다.
                 *
                 * Legacy narrationText로 빠지지 않게
                 * 이 Category에도 명시적인 안전 fallback을 넣는다.
                 */
                stories.put(
                        category,
                        TECHNICAL_STORY_FALLBACK
                );
            }
        }

        if (
                stories.isEmpty()
        ) {

            diagnosticLogger
                    .generationFallback(
                            generationId,
                            model,
                            "GENERATED_STORY_MAP_EMPTY"
                    );

            return Optional.empty();
        }

        diagnosticLogger
                .generationSucceeded(
                        generationId,
                        model,
                        stories.size(),
                        elapsedMillis(
                                generationStartedAt
                        )
                );

        return Optional.of(
                new ConsultationStoryGeneration(
                        promptContext,
                        PROVIDER,
                        model,
                        stories
                )
        );
    }


    /*
     * =========================================================
     * Category Human Review Pipeline
     * =========================================================
     */

    private String generateHumanReviewedStory(

            String generationId,

            ConsultationInterpretationPromptSection section,

            ConsultationCategory category,

            long categoryStartedAt

    ) throws Exception {

        Objects.requireNonNull(
                section,
                "Prompt Section이 필요합니다."
        );

        Objects.requireNonNull(
                category,
                "ConsultationCategory가 필요합니다."
        );

        if (
                section.category()
                        !=
                        category
        ) {

            throw new IllegalArgumentException(
                    "Prompt Section Category가 일치하지 않습니다. expected="
                            +
                            category
                            +
                            ", actual="
                            +
                            section.category()
            );
        }

        if (
                section.unavailable()
        ) {

            throw new IllegalArgumentException(
                    "unavailable Section은 Story Generation 대상이 아닙니다: "
                            +
                            category
            );
        }


        /*
         * =====================================================
         * Writer Grounding
         * =====================================================
         */

        String writerGroundingJson =
                jsonMapper.writeValueAsString(
                        createWriterGroundingPayload(
                                section
                        )
                );


        /*
         * =====================================================
         * Writer
         * =====================================================
         */

        String writerStory =
                requestWriterStory(
                        generationId,
                        writerGroundingJson,
                        category,
                        categoryStartedAt,
                        ""
                );

        StoryValidationResult writerValidation =
                validateHardSafety(
                        category,
                        writerStory
                );


        /*
         * =====================================================
         * Reviewer Grounding
         * =====================================================
         *
         * Writer Grounding을 그대로 다시 전송하지 않는다.
         */

        String reviewerGroundingJson =
                jsonMapper.writeValueAsString(
                        createReviewerGroundingPayload(
                                section
                        )
                );


        /*
         * =====================================================
         * Reviewer
         * =====================================================
         *
         * Reviewer API 자체가 실패하더라도
         * Writer가 정상이라면 Writer를 살린다.
         */

        ReviewResult reviewResult;

        try {

            reviewResult =
                    requestReview(
                            generationId,
                            reviewerGroundingJson,
                            category,
                            writerStory,
                            writerValidation.valid()
                                    ? ""
                                    : writerValidation.message(),
                            categoryStartedAt
                    );

        }
        catch (
                Exception reviewerException
        ) {

            if (
                    writerValidation.valid()
            ) {

                log.warn(
                        "Story Reviewer 호출 실패. 정상 Writer를 사용합니다. category={}, reason={}",
                        category,
                        safeExceptionMessage(
                                reviewerException
                        )
                );

                return normalizeGeneratedStoryText(
                        writerStory
                );
            }

            throw reviewerException;
        }


        String reviewedStory =
                normalizeGeneratedStoryText(
                        reviewResult.finalStoryText()
                );

        StoryValidationResult reviewedValidation =
                validateHardSafety(
                        category,
                        reviewedStory
                );


        if (
                reviewedValidation.valid()
        ) {

            return reviewedStory;
        }


        /*
         * Reviewer가 Hard Safety를 깨뜨렸지만
         * Writer는 정상이라면
         * Writer를 보존한다.
         */
        if (
                writerValidation.valid()
        ) {

            return normalizeGeneratedStoryText(
                    writerStory
            );
        }


        /*
         * =====================================================
         * Repair
         * =====================================================
         *
         * Writer와 Reviewer 모두
         * 명백한 사용자 출력 오류가 있을 때만 실행한다.
         */

        String repairReason =
                firstNonBlank(
                        reviewedValidation.message(),
                        writerValidation.message(),
                        reviewResult.reviewSummary()
                );

        String repairedStory =
                requestWriterStory(
                        generationId,
                        writerGroundingJson,
                        category,
                        categoryStartedAt,
                        """
                        직전 작성문과 검토문 모두 최종 출력 안전조건을 통과하지 못했습니다.

                        문제:
                        %s

                        같은 계산 근거만 사용해서 처음부터 다시 작성하세요.

                        새로운 사주 판단,
                        새로운 과거 사건,
                        새로운 미래 사건,
                        새로운 생활 사례를 만들지 마세요.

                        위 문제만 제거하면서 핵심 행동 패턴은 유지하세요.
                        """
                                .formatted(
                                        repairReason
                                )
                );

        StoryValidationResult repairedValidation =
                validateHardSafety(
                        category,
                        repairedStory
                );

        if (
                repairedValidation.valid()
        ) {

            return normalizeGeneratedStoryText(
                    repairedStory
            );
        }

        throw new IllegalStateException(
                "Writer/Reviewer/Repair 모두 Hard Safety를 통과하지 못했습니다. category="
                        +
                        category
                        +
                        ", reason="
                        +
                        repairedValidation.message()
        );
    }


    /*
     * =========================================================
     * Writer Grounding
     * =========================================================
     */

    private Map<String, Object> createWriterGroundingPayload(

            ConsultationInterpretationPromptSection section

    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "자료버전",
                HUMAN_GROUNDING_VERSION
        );

        result.put(
                "상담주제",
                categoryDisplayName(
                        section.category()
                )
        );

        result.put(
                "타고난핵심",
                createCompactEvidencePayload(
                        section.natalEvidences(),
                        section.category(),
                        WRITER_NATAL_EVIDENCE_LIMIT,
                        true
                )
        );

        result.put(
                "현재흐름",
                createScenarioPayload(
                        section.timelineScenarios(),
                        section.category(),
                        WRITER_SCENARIO_EVIDENCE_LIMIT,
                        true
                )
        );

        result.put(
                "연도흐름",
                createYearPayload(
                        section.timelineYears(),
                        section.category(),
                        WRITER_YEAR_EVIDENCE_LIMIT,
                        true
                )
        );

        return result;
    }


    /*
     * =========================================================
     * Reviewer Grounding
     * =========================================================
     *
     * Reviewer에게는 핵심만 준다.
     */

    private Map<String, Object> createReviewerGroundingPayload(

            ConsultationInterpretationPromptSection section

    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "상담주제",
                categoryDisplayName(
                        section.category()
                )
        );

        result.put(
                "핵심타고난근거",
                createCompactEvidencePayload(
                        section.natalEvidences(),
                        section.category(),
                        REVIEWER_NATAL_EVIDENCE_LIMIT,
                        false
                )
        );

        result.put(
                "핵심현재근거",
                createScenarioPayload(
                        section.timelineScenarios(),
                        section.category(),
                        REVIEWER_SCENARIO_EVIDENCE_LIMIT,
                        false
                )
        );

        result.put(
                "핵심연도근거",
                createYearPayload(
                        section.timelineYears(),
                        section.category(),
                        REVIEWER_YEAR_EVIDENCE_LIMIT,
                        false
                )
        );

        return result;
    }


    /*
     * =========================================================
     * Compact Evidence
     * =========================================================
     */

    private List<Map<String, Object>>
    createCompactEvidencePayload(

            List<ConsultationGroundingEvidence> evidences,

            ConsultationCategory category,

            int limit,

            boolean includeReasons

    ) {

        List<ConsultationGroundingEvidence> selected =
                selectImportantEvidences(
                        evidences,
                        limit
                );

        if (
                selected.isEmpty()
        ) {

            return List.of();
        }

        List<Map<String, Object>> result =
                new ArrayList<>(
                        selected.size()
                );

        for (
                ConsultationGroundingEvidence evidence
                : selected
        ) {

            Map<String, Object> item =
                    new LinkedHashMap<>();

            ConsultationGroundingProminence prominence =
                    evidence.prominence();

            item.put(
                    "서술역할",
                    narrativeRole(
                            prominence
                    )
            );

            /*
             * PERSONALITY에서는
             * Domain 이름이 여러 미니 장을 만들 수 있으므로 숨긴다.
             */
            if (
                    category
                            !=
                            ConsultationCategory.PERSONALITY
            ) {

                item.put(
                        "생활주제",
                        userFacingArea(
                                evidence
                                        .domain()
                                        .name()
                        )
                );
            }

            item.put(
                    "반응방향",
                    userFacingDirection(
                            evidence
                                    .status()
                                    .name()
                    )
            );

            item.put(
                    "근거형태",
                    prominenceSignal(
                            prominence
                    )
            );

            item.put(
                    "판단메모",
                    evidence.summary()
            );

            /*
             * Writer에게만,
             * 상위 Evidence의 Reason 일부만 전달한다.
             *
             * 전체 Reasons를 반복 전송하지 않는다.
             */
            if (
                    includeReasons
                            &&
                            prominence != null
                            &&
                            prominence.narrativePriority()
                                    <= 2
                            &&
                            !evidence.reasons().isEmpty()
            ) {

                item.put(
                        "핵심이유",
                        evidence
                                .reasons()
                                .stream()
                                .limit(
                                        WRITER_REASON_LIMIT
                                )
                                .toList()
                );
            }

            result.add(
                    item
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =========================================================
     * Evidence Selection
     * =========================================================
     *
     * 단순 top-N만 하지 않는다.
     *
     * prominence 상위 Evidence를 먼저 선택하면서
     * 서로 다른 Status가 존재하면
     * 가능한 범위 안에서 반대 방향도 보존한다.
     */

    private List<ConsultationGroundingEvidence>
    selectImportantEvidences(

            List<ConsultationGroundingEvidence> source,

            int limit

    ) {

        if (
                source == null
                        ||
                        source.isEmpty()
                        ||
                        limit < 1
        ) {

            return List.of();
        }

        LinkedHashMap<
                String,
                ConsultationGroundingEvidence
                > unique =
                new LinkedHashMap<>();

        source
                .stream()
                .filter(
                        Objects::nonNull
                )
                .sorted(
                        Comparator.comparingInt(
                                evidence ->
                                        normalizedNarrativePriority(
                                                evidence.prominence()
                                        )
                        )
                )
                .forEach(
                        evidence -> {

                            String signature =
                                    evidence.domain().name()
                                            +
                                            "\u001F"
                                            +
                                            evidence.status().name()
                                            +
                                            "\u001F"
                                            +
                                            evidence.summary();

                            unique.putIfAbsent(
                                    signature,
                                    evidence
                            );
                        }
                );

        List<ConsultationGroundingEvidence> ordered =
                new ArrayList<>(
                        unique.values()
                );

        if (
                ordered.size()
                        <=
                        limit
        ) {

            return List.copyOf(
                    ordered
            );
        }

        List<ConsultationGroundingEvidence> selected =
                new ArrayList<>(
                        limit
                );

        Set<String> selectedStatuses =
                new LinkedHashSet<>();


        /*
         * 가장 높은 prominence 먼저.
         */
        ConsultationGroundingEvidence first =
                ordered.get(0);

        selected.add(
                first
        );

        selectedStatuses.add(
                first.status().name()
        );


        /*
         * 서로 다른 방향이 존재하면
         * 반대 방향을 가능한 한 보존.
         */
        for (
                ConsultationGroundingEvidence evidence
                : ordered
        ) {

            if (
                    selected.size()
                            >=
                            limit
            ) {

                break;
            }

            String status =
                    evidence.status().name();

            if (
                    selectedStatuses.add(
                            status
                    )
            ) {

                selected.add(
                        evidence
                );
            }
        }


        /*
         * 남은 자리는 prominence 순서대로.
         */
        for (
                ConsultationGroundingEvidence evidence
                : ordered
        ) {

            if (
                    selected.size()
                            >=
                            limit
            ) {

                break;
            }

            if (
                    !selected.contains(
                            evidence
                    )
            ) {

                selected.add(
                        evidence
                );
            }
        }

        selected.sort(
                Comparator.comparingInt(
                        evidence ->
                                normalizedNarrativePriority(
                                        evidence.prominence()
                                )
                )
        );

        return List.copyOf(
                selected
        );
    }


    /*
     * =========================================================
     * Scenario Payload
     * =========================================================
     */

    private List<Map<String, Object>>
    createScenarioPayload(

            List<ConsultationGroundingTimelineScenario> scenarios,

            ConsultationCategory category,

            int evidenceLimit,

            boolean includeReasons

    ) {

        if (
                scenarios == null
                        ||
                        scenarios.isEmpty()
        ) {

            return List.of();
        }

        List<Map<String, Object>> result =
                new ArrayList<>(
                        scenarios.size()
                );

        for (
                ConsultationGroundingTimelineScenario scenario
                : scenarios
        ) {

            Map<String, Object> item =
                    new LinkedHashMap<>();

            item.put(
                    "순서",
                    scenario.scenarioOrder()
            );

            item.put(
                    "연도",
                    scenario.sajuYear()
            );

            if (
                    scenario.timelineUncertaintyExists()
            ) {

                item.put(
                        "시점",
                        "경계 불확실성 있음"
                );
            }

            item.put(
                    "패턴",
                    createCompactEvidencePayload(
                            scenario.evidences(),
                            category,
                            evidenceLimit,
                            includeReasons
                    )
            );

            result.add(
                    item
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =========================================================
     * Timeline Year Payload
     * =========================================================
     */

    private List<Map<String, Object>>
    createYearPayload(

            List<ConsultationGroundingTimelineYear> years,

            ConsultationCategory category,

            int evidenceLimit,

            boolean includeReasons

    ) {

        if (
                years == null
                        ||
                        years.isEmpty()
        ) {

            return List.of();
        }

        List<Map<String, Object>> result =
                new ArrayList<>(
                        years.size()
                );

        for (
                ConsultationGroundingTimelineYear year
                : years
        ) {

            Map<String, Object> item =
                    new LinkedHashMap<>();

            item.put(
                    "연도",
                    year.sajuYear()
            );

            if (
                    year.timelineUncertaintyExists()
            ) {

                item.put(
                        "시점",
                        "경계 불확실성 있음"
                );
            }

            item.put(
                    "패턴",
                    createCompactEvidencePayload(
                            year.evidences(),
                            category,
                            evidenceLimit,
                            includeReasons
                    )
            );

            result.add(
                    item
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =========================================================
     * Prominence -> Human Internal Label
     * =========================================================
     */

    private String narrativeRole(

            ConsultationGroundingProminence prominence

    ) {

        if (
                prominence == null
                        ||
                        prominence.narrativePriority()
                                <= 0
        ) {

            return "보완 후보";
        }

        return switch (
                prominence.narrativePriority()
                ) {

            case 1 ->
                    "최우선 핵심";

            case 2 ->
                    "두 번째 핵심";

            case 3,
                 4 ->
                    "핵심 보완";

            default ->
                    "필요할 때만 보완";
        };
    }


    private String prominenceSignal(

            ConsultationGroundingProminence prominence

    ) {

        if (
                prominence == null
        ) {

            return "직접 계산 근거";
        }

        boolean diverse =
                prominence
                        .diverseDirectSourceTypes();

        boolean multiple =
                prominence
                        .multipleDirectSources();

        boolean repeated =
                prominence
                        .repeatedAcrossRules();

        if (
                diverse
                        &&
                        repeated
        ) {

            return "서로 다른 직접 계산축과 반복 판단이 함께 받침";
        }

        if (
                diverse
        ) {

            return "서로 다른 종류의 직접 계산 근거가 함께 받침";
        }

        if (
                multiple
                        &&
                        repeated
        ) {

            return "여러 직접 근거와 반복 판단이 함께 받침";
        }

        if (
                multiple
        ) {

            return "여러 직접 계산 근거가 함께 받침";
        }

        if (
                repeated
        ) {

            return "같은 방향 판단이 반복됨";
        }

        return "직접 계산 근거";
    }


    /*
     * =========================================================
     * Writer Request
     * =========================================================
     */

    private String requestWriterStory(

            String generationId,

            String groundingJson,

            ConsultationCategory category,

            long categoryStartedAt,

            String correction

    ) throws Exception {

        Map<String, Object> requestBody =
                createWriterRequestBody(
                        groundingJson,
                        category,
                        correction
                );

        String responseBody =
                callOpenAi(
                        requestBody,
                        generationId,
                        category,
                        categoryStartedAt
                );

        return parseWriterStory(
                generationId,
                responseBody,
                category
        );
    }


    /*
     * =========================================================
     * Reviewer Request
     * =========================================================
     */

    private ReviewResult requestReview(

            String generationId,

            String reviewerGroundingJson,

            ConsultationCategory category,

            String writerStory,

            String hardValidationIssue,

            long categoryStartedAt

    ) throws Exception {

        Map<String, Object> requestBody =
                createReviewerRequestBody(
                        reviewerGroundingJson,
                        category,
                        writerStory,
                        hardValidationIssue
                );

        String responseBody =
                callOpenAi(
                        requestBody,
                        generationId,
                        category,
                        categoryStartedAt
                );

        return parseReviewResult(
                generationId,
                responseBody,
                category
        );
    }


    /*
     * =========================================================
     * HTTP + 429 Retry
     * =========================================================
     */

    private String callOpenAi(

            Map<String, Object> requestBody,

            String generationId,

            ConsultationCategory category,

            long categoryStartedAt

    ) {

        int rateLimitRetry =
                0;

        while (
                true
        ) {

            try {

                String responseBody =
                        restClient
                                .post()
                                .uri(
                                        RESPONSES_PATH
                                )
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer "
                                                +
                                                apiKey
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .body(
                                        requestBody
                                )
                                .retrieve()
                                .body(
                                        String.class
                                );

                if (
                        responseBody == null
                                ||
                                responseBody.isBlank()
                ) {

                    throw new IllegalStateException(
                            "OpenAI Responses API가 빈 응답을 반환했습니다. category="
                                    +
                                    category
                    );
                }

                diagnosticLogger
                        .responseReceived(
                                generationId,
                                model,
                                category,
                                responseBody.length(),
                                elapsedMillis(
                                        categoryStartedAt
                                )
                        );

                return responseBody;

            }
            catch (
                    HttpClientErrorException.TooManyRequests
                            rateLimitException
            ) {

                if (
                        rateLimitRetry
                                >=
                                MAX_RATE_LIMIT_RETRIES
                ) {

                    throw rateLimitException;
                }

                long waitMillis =
                        resolveRateLimitWaitMillis(
                                rateLimitException,
                                rateLimitRetry
                        );

                rateLimitRetry++;

                log.warn(
                        "OpenAI 429 Rate Limit. category={}, retry={}/{}, waitMs={}",
                        category,
                        rateLimitRetry,
                        MAX_RATE_LIMIT_RETRIES,
                        waitMillis
                );

                sleepForRateLimit(
                        waitMillis
                );
            }
        }
    }


    private long resolveRateLimitWaitMillis(

            HttpClientErrorException.TooManyRequests exception,

            int retryIndex

    ) {

        long fallback =
                RATE_LIMIT_BACKOFF_MILLIS[
                        Math.min(
                                retryIndex,
                                RATE_LIMIT_BACKOFF_MILLIS.length - 1
                        )
                        ];

        long retryAfterHeader =
                parseRetryAfterHeaderMillis(
                        exception
                );

        long bodySuggested =
                parseBodyRetryMillis(
                        exception
                );

        long resolved =
                Math.max(
                        fallback,
                        Math.max(
                                retryAfterHeader,
                                bodySuggested
                        )
                );

        /*
         * 너무 정확하게 경계에 걸리지 않도록
         * 작은 여유를 둔다.
         */
        resolved +=
                300L;

        return Math.min(
                resolved,
                MAX_RATE_LIMIT_WAIT_MILLIS
        );
    }


    private long parseRetryAfterHeaderMillis(

            HttpClientErrorException.TooManyRequests exception

    ) {

        try {

            if (
                    exception.getResponseHeaders()
                            ==
                            null
            ) {

                return 0L;
            }

            String value =
                    exception
                            .getResponseHeaders()
                            .getFirst(
                                    "Retry-After"
                            );

            if (
                    value == null
                            ||
                            value.isBlank()
            ) {

                return 0L;
            }

            double seconds =
                    Double.parseDouble(
                            value.trim()
                    );

            if (
                    seconds <= 0
            ) {

                return 0L;
            }

            return (long) Math.ceil(
                    seconds
                            *
                            1000.0
            );

        }
        catch (
                Exception ignored
        ) {

            return 0L;
        }
    }


    private long parseBodyRetryMillis(

            HttpClientErrorException.TooManyRequests exception

    ) {

        try {

            String body =
                    exception
                            .getResponseBodyAsString();

            if (
                    body == null
                            ||
                            body.isBlank()
            ) {

                return 0L;
            }

            Matcher matcher =
                    RETRY_SECONDS_PATTERN
                            .matcher(
                                    body
                            );

            if (
                    !matcher.find()
            ) {

                return 0L;
            }

            double seconds =
                    Double.parseDouble(
                            matcher.group(1)
                    );

            if (
                    seconds <= 0
            ) {

                return 0L;
            }

            return (long) Math.ceil(
                    seconds
                            *
                            1000.0
            );

        }
        catch (
                Exception ignored
        ) {

            return 0L;
        }
    }


    private void sleepForRateLimit(

            long waitMillis

    ) {

        try {

            Thread.sleep(
                    waitMillis
            );

        }
        catch (
                InterruptedException exception
        ) {

            Thread
                    .currentThread()
                    .interrupt();

            throw new IllegalStateException(
                    "OpenAI Rate Limit 대기 중 Thread가 중단되었습니다.",
                    exception
            );
        }
    }


    /*
     * =========================================================
     * Writer Request Body
     * =========================================================
     */

    private Map<String, Object> createWriterRequestBody(

            String groundingJson,

            ConsultationCategory category,

            String correction

    ) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "model",
                model
        );

        request.put(
                "store",
                false
        );

        request.put(
                "max_output_tokens",
                Math.min(
                        maxOutputTokens,
                        WRITER_OUTPUT_TOKEN_LIMIT
                )
        );

        request.put(
                "instructions",
                createWriterInstructions(
                        category
                )
        );

        request.put(
                "input",
                createWriterInput(
                        groundingJson,
                        category,
                        correction
                )
        );

        Map<String, Object> text =
                new LinkedHashMap<>();

        text.put(
                "format",
                createWriterOutputFormat(
                        category
                )
        );

        request.put(
                "text",
                text
        );

        return request;
    }


    /*
     * =========================================================
     * Reviewer Request Body
     * =========================================================
     */

    private Map<String, Object> createReviewerRequestBody(

            String groundingJson,

            ConsultationCategory category,

            String draftStory,

            String hardValidationIssue

    ) {

        Map<String, Object> request =
                new LinkedHashMap<>();

        request.put(
                "model",
                model
        );

        request.put(
                "store",
                false
        );

        request.put(
                "max_output_tokens",
                Math.min(
                        maxOutputTokens,
                        REVIEWER_OUTPUT_TOKEN_LIMIT
                )
        );

        request.put(
                "instructions",
                createReviewerInstructions(
                        category
                )
        );

        request.put(
                "input",
                createReviewerInput(
                        groundingJson,
                        category,
                        draftStory,
                        hardValidationIssue
                )
        );

        Map<String, Object> text =
                new LinkedHashMap<>();

        text.put(
                "format",
                createReviewerOutputFormat(
                        category
                )
        );

        request.put(
                "text",
                text
        );

        return request;
    }


    /*
     * =========================================================
     * Writer Prompt
     * =========================================================
     */

    private String createWriterInstructions(

            ConsultationCategory category

    ) {

        return """
                당신은 한 사람에게 직접 이야기하는 한국어 사주 상담문 작성자입니다.

                사주 계산은 Java에서 이미 끝났습니다.
                새로운 계산이나 새로운 명리 판단을 만들지 마세요.

                현재 장:
                %s

                현재 장의 범위:
                %s

                입력에는 deterministic 판단이 중요도 순으로 정리되어 있습니다.

                "최우선 핵심"과 "두 번째 핵심"을 먼저 깊게 설명하세요.
                Evidence를 전부 나열하지 마세요.
                핵심 행동 패턴 2~4개에 집중하세요.

                적중감은 사건을 맞히는 방식으로 만들지 않습니다.

                가능한 경우 한 특징을:

                평소 반응
                → 반응이 달라지는 조건
                → 안쪽 판단 변화
                → 밖으로 드러나는 행동
                → 반복되는 습관

                순서로 연결하세요.

                단, 입력에서 실제로 뒷받침되는 연결만 사용하세요.

                서로 다른 반응방향이 함께 있다면
                한쪽을 삭제하지 마세요.
                근거가 허용할 때만
                "평소에는 이렇지만 부담이 커지면 저렇게 달라진다"
                같은 조건 차이로 설명하세요.

                첫 문단부터 가장 개인적인 핵심 특징을 말하세요.
                누구에게나 맞는 서론을 쓰지 마세요.

                다음 식의 문장만 반복하지 마세요.

                "신중합니다."
                "책임감이 있습니다."
                "균형이 중요합니다."
                "노력이 필요합니다."

                이런 판단이 필요하면
                언제, 무엇 때문에, 어떻게 반응이 달라지는지까지 설명하세요.

                절대 만들지 말 것:

                - 실제로 있었다고 확인되지 않은 과거 사건
                - 미래의 특정 사건
                - 특정 직업
                - 특정 사람
                - 특정 장소
                - 특정 금전 경험
                - 특정 연애 경험
                - 특정 가족 사건
                - 특정 질병
                - 특정 소송
                - 입력에 없는 생활 습관
                - 입력에 없는 해결책

                "예를 들어"라는 표현 자체는 오류가 아닙니다.
                다만 없는 사건을 예시로 만들지 마세요.

                최종 storyText에 다음 사주 전문용어를 사용하지 마세요.

                %s

                영어 알파벳, 밑줄, 내부 필드명, 근거 수, 서술 순위도 노출하지 마세요.

                HTML은 사용하지 마세요.

                가장 개인적으로 체감될 핵심 문장 2~4곳 정도만
                **핵심 문장**
                형태로 강조할 수 있습니다.

                평범한 조언은 강조하지 마세요.

                1200~1900자를 우선 목표로 하지만
                길이를 맞추기 위해 새 내용을 만들거나 반복하지 마세요.

                Category 추가 규칙:
                %s

                반환값에는 storyText 하나만 넣으세요.
                """
                .formatted(
                        categoryDisplayName(
                                category
                        ),
                        categoryFocusInstruction(
                                category
                        ),
                        FORBIDDEN_SAJU_TERMS_PROMPT,
                        categorySafetyInstruction(
                                category
                        )
                );
    }


    private String createWriterInput(

            String groundingJson,

            ConsultationCategory category,

            String correction

    ) {

        String correctionSection =
                correction == null
                        ||
                        correction.isBlank()
                        ?
                        ""
                        :
                        """

                        이전 출력 교정사항:

                        %s
                        """
                                .formatted(
                                        correction
                                );

        return """
                상담 주제:
                %s

                행동 번역 방향:
                %s

                공식 계산 Grounding:
                %s

                %s

                작성 전에 내부적으로만 확인하세요.

                - 최우선 핵심은 무엇인가.
                - 그 특징이 여러 직접 계산축에서 받쳐지는가.
                - 반대 방향 판단이 함께 존재하는가.
                - 행동의 조건과 반응까지 연결할 근거가 있는가.

                이 검토 과정은 사용자에게 말하지 마세요.

                계산 근거의 의미보다 더 강하게 단정하지 마세요.
                """
                .formatted(
                        categoryDisplayName(
                                category
                        ),
                        behavioralTranslationInstruction(
                                category
                        ),
                        groundingJson,
                        correctionSection
                );
    }


    /*
     * =========================================================
     * Reviewer Prompt
     * =========================================================
     */

    private String createReviewerInstructions(

            ConsultationCategory category

    ) {

        return """
                당신은 사주를 새로 계산하지 않는 상담문 편집자입니다.

                현재 장:
                %s

                제공되는 핵심 계산 근거와 Writer 초안을 비교하세요.

                단어 하나로 기계적으로 판단하지 마세요.
                문장의 의미를 봅니다.

                다음만 확인하세요.

                1.
                핵심 근거가 상담문의 중심에 살아 있는가.

                2.
                중요한 특징이 단순 형용사가 아니라
                실제 반응 패턴 수준으로 설명됐는가.

                3.
                계산 근거에 없는 과거 사건, 미래 사건,
                사람, 직업, 장소, 경험, 해결책을 만들지 않았는가.

                4.
                서로 다른 반응방향이 있는데
                한쪽만 남겨 사람을 단순화하지 않았는가.

                5.
                현재 장이 다른 상담 분야로 이동하지 않았는가.

                6.
                **...** 강조가 실제 핵심 패턴에 붙어 있는가.

                "예를 들어", "사람", "돈", "일", "책임" 같은
                단어의 존재 자체는 오류가 아닙니다.

                입력 Grounding으로 의미가 뒷받침되는지가 중요합니다.

                초안이 충분히 좋다면:
                decision = KEEP

                finalStoryText에는 초안을 그대로 넣으세요.

                의미상 문제가 있거나 핵심 특징을 제대로 살리지 못했다면:
                decision = REVISE

                같은 Grounding만 사용하여 직접 다듬으세요.

                새로운 판단이나 사건을 추가하지 마세요.

                영어, 내부 개발용어, HTML,
                근거 개수나 우선순위 숫자를 사용자에게 노출하지 마세요.

                현재 장의 범위:
                %s
                """
                .formatted(
                        categoryDisplayName(
                                category
                        ),
                        categoryFocusInstruction(
                                category
                        )
                );
    }


    private String createReviewerInput(

            String groundingJson,

            ConsultationCategory category,

            String draftStory,

            String hardValidationIssue

    ) {

        String hardNotice =
                hardValidationIssue == null
                        ||
                        hardValidationIssue.isBlank()
                        ?
                        "명백한 출력 형식 오류는 발견되지 않았습니다."
                        :
                        """
                        Java Hard Safety가 발견한 명백한 출력 오류:

                        %s

                        이 문제는 최종문에서 반드시 제거하세요.
                        """
                                .formatted(
                                        hardValidationIssue
                                );

        return """
                상담 주제:
                %s

                검토에 필요한 핵심 Grounding:
                %s

                Writer 초안:
                %s

                추가 확인:
                %s

                Grounding 전체를 다시 해석하려 하지 말고
                Writer가 핵심 판단을 정확하게 번역했는지 검토하세요.

                최종적으로 사용자에게 보여도 되는 상담문 하나를
                finalStoryText에 넣으세요.
                """
                .formatted(
                        categoryDisplayName(
                                category
                        ),
                        groundingJson,
                        draftStory,
                        hardNotice
                );
    }


    /*
     * =========================================================
     * Structured Output - Writer
     * =========================================================
     */

    private Map<String, Object> createWriterOutputFormat(

            ConsultationCategory category

    ) {

        Map<String, Object> storyText =
                new LinkedHashMap<>();

        storyText.put(
                "type",
                "string"
        );

        Map<String, Object> properties =
                new LinkedHashMap<>();

        properties.put(
                "storyText",
                storyText
        );

        Map<String, Object> schema =
                new LinkedHashMap<>();

        schema.put(
                "type",
                "object"
        );

        schema.put(
                "properties",
                properties
        );

        schema.put(
                "required",
                List.of(
                        "storyText"
                )
        );

        schema.put(
                "additionalProperties",
                false
        );

        Map<String, Object> format =
                new LinkedHashMap<>();

        format.put(
                "type",
                "json_schema"
        );

        format.put(
                "name",
                "dajeom_writer_story"
        );

        format.put(
                "description",
                categoryDisplayName(
                        category
                )
                        +
                        " 상담문"
        );

        format.put(
                "strict",
                true
        );

        format.put(
                "schema",
                schema
        );

        return format;
    }


    /*
     * =========================================================
     * Structured Output - Reviewer
     * =========================================================
     */

    private Map<String, Object> createReviewerOutputFormat(

            ConsultationCategory category

    ) {

        Map<String, Object> decision =
                new LinkedHashMap<>();

        decision.put(
                "type",
                "string"
        );

        decision.put(
                "enum",
                List.of(
                        "KEEP",
                        "REVISE"
                )
        );


        Map<String, Object> finalStoryText =
                new LinkedHashMap<>();

        finalStoryText.put(
                "type",
                "string"
        );


        Map<String, Object> reviewSummary =
                new LinkedHashMap<>();

        reviewSummary.put(
                "type",
                "string"
        );


        Map<String, Object> properties =
                new LinkedHashMap<>();

        properties.put(
                "decision",
                decision
        );

        properties.put(
                "finalStoryText",
                finalStoryText
        );

        properties.put(
                "reviewSummary",
                reviewSummary
        );


        Map<String, Object> schema =
                new LinkedHashMap<>();

        schema.put(
                "type",
                "object"
        );

        schema.put(
                "properties",
                properties
        );

        schema.put(
                "required",
                List.of(
                        "decision",
                        "finalStoryText",
                        "reviewSummary"
                )
        );

        schema.put(
                "additionalProperties",
                false
        );


        Map<String, Object> format =
                new LinkedHashMap<>();

        format.put(
                "type",
                "json_schema"
        );

        format.put(
                "name",
                "dajeom_story_review"
        );

        format.put(
                "description",
                categoryDisplayName(
                        category
                )
                        +
                        " 상담문 검토"
        );

        format.put(
                "strict",
                true
        );

        format.put(
                "schema",
                schema
        );

        return format;
    }


    /*
     * =========================================================
     * Parse Writer
     * =========================================================
     */

    private String parseWriterStory(

            String generationId,

            String responseBody,

            ConsultationCategory category

    ) throws Exception {

        JsonNode response =
                parseCompletedResponse(
                        generationId,
                        responseBody,
                        category
                );

        String structuredText =
                extractOutputText(
                        response
                );

        diagnosticLogger
                .structuredOutputExtracted(
                        generationId,
                        model,
                        category,
                        structuredText.length()
                );

        JsonNode root =
                jsonMapper.readTree(
                        structuredText
                );

        String storyText =
                root
                        .path(
                                "storyText"
                        )
                        .asString(
                                ""
                        )
                        .trim();

        if (
                storyText.isBlank()
        ) {

            throw new IllegalStateException(
                    "Writer storyText가 비어 있습니다. category="
                            +
                            category
            );
        }

        return normalizeGeneratedStoryText(
                storyText
        );
    }


    /*
     * =========================================================
     * Parse Reviewer
     * =========================================================
     */

    private ReviewResult parseReviewResult(

            String generationId,

            String responseBody,

            ConsultationCategory category

    ) throws Exception {

        JsonNode response =
                parseCompletedResponse(
                        generationId,
                        responseBody,
                        category
                );

        String structuredText =
                extractOutputText(
                        response
                );

        diagnosticLogger
                .structuredOutputExtracted(
                        generationId,
                        model,
                        category,
                        structuredText.length()
                );

        JsonNode root =
                jsonMapper.readTree(
                        structuredText
                );

        String decisionText =
                requireText(
                        root
                                .path(
                                        "decision"
                                )
                                .asString(
                                        ""
                                ),
                        "Reviewer decision이 필요합니다."
                );

        String finalStoryText =
                requireText(
                        root
                                .path(
                                        "finalStoryText"
                                )
                                .asString(
                                        ""
                                ),
                        "Reviewer finalStoryText가 필요합니다."
                );

        String reviewSummary =
                root
                        .path(
                                "reviewSummary"
                        )
                        .asString(
                                ""
                        )
                        .trim();

        return new ReviewResult(
                ReviewDecision.valueOf(
                        decisionText
                ),
                normalizeGeneratedStoryText(
                        finalStoryText
                ),
                reviewSummary
        );
    }


    /*
     * =========================================================
     * Completed Response
     * =========================================================
     */

    private JsonNode parseCompletedResponse(

            String generationId,

            String responseBody,

            ConsultationCategory category

    ) throws Exception {

        JsonNode response =
                jsonMapper.readTree(
                        responseBody
                );

        String status =
                response
                        .path(
                                "status"
                        )
                        .asString(
                                ""
                        )
                        .trim();

        diagnosticLogger
                .responseStatus(
                        generationId,
                        model,
                        category,
                        status
                );

        if (
                !"completed".equals(
                        status
                )
        ) {

            String detail =
                    firstNonBlank(
                            response
                                    .path(
                                            "error"
                                    )
                                    .path(
                                            "message"
                                    )
                                    .asString(
                                            ""
                                    ),

                            response
                                    .path(
                                            "incomplete_details"
                                    )
                                    .path(
                                            "reason"
                                    )
                                    .asString(
                                            ""
                                    ),

                            "UNKNOWN"
                    );

            throw new IllegalStateException(
                    "OpenAI Responses API가 completed 상태가 아닙니다. status="
                            +
                            firstNonBlank(
                                    status,
                                    "UNKNOWN"
                            )
                            +
                            ", detail="
                            +
                            detail
            );
        }

        return response;
    }


    /*
     * =========================================================
     * Output Extract
     * =========================================================
     */

    private String extractOutputText(

            JsonNode response

    ) {

        JsonNode output =
                response.path(
                        "output"
                );

        if (
                !output.isArray()
        ) {

            throw new IllegalStateException(
                    "OpenAI Responses API output 배열이 없습니다."
            );
        }

        List<String> texts =
                new ArrayList<>();

        for (
                JsonNode outputItem
                : output
        ) {

            if (
                    !"message".equals(
                            outputItem
                                    .path(
                                            "type"
                                    )
                                    .asString(
                                            ""
                                    )
                                    .trim()
                    )
            ) {

                continue;
            }

            JsonNode content =
                    outputItem.path(
                            "content"
                    );

            if (
                    !content.isArray()
            ) {

                continue;
            }

            for (
                    JsonNode part
                    : content
            ) {

                if (
                        !"output_text".equals(
                                part
                                        .path(
                                                "type"
                                        )
                                        .asString(
                                                ""
                                        )
                                        .trim()
                        )
                ) {

                    continue;
                }

                String text =
                        part
                                .path(
                                        "text"
                                )
                                .asString(
                                        ""
                                )
                                .trim();

                if (
                        !text.isBlank()
                ) {

                    texts.add(
                            text
                    );
                }
            }
        }

        if (
                texts.isEmpty()
        ) {

            throw new IllegalStateException(
                    "OpenAI Responses API에 output_text가 없습니다."
            );
        }

        return String.join(
                "\n",
                texts
        );
    }


    /*
     * =========================================================
     * Hard Safety
     * =========================================================
     *
     * 의미 판단이 필요한 것은 여기서 검사하지 않는다.
     *
     * 예:
     *
     * "예를 들어"
     * "사람"
     * "돈"
     * "일"
     * "책임"
     * "균형"
     *
     * 등은 Hard Fail 대상이 아니다.
     */

    private StoryValidationResult validateHardSafety(

            ConsultationCategory category,

            String storyText

    ) {

        if (
                storyText == null
                        ||
                        storyText.isBlank()
        ) {

            return StoryValidationResult.failure(
                    "상담문이 비어 있습니다."
            );
        }

        String normalized =
                normalizeGeneratedStoryText(
                        storyText
                );

        Set<String> violations =
                new LinkedHashSet<>();


        /*
         * 영어 / 밑줄
         */
        Matcher latinMatcher =
                LATIN_TOKEN_PATTERN
                        .matcher(
                                normalized
                        );

        LinkedHashSet<String> latinTokens =
                new LinkedHashSet<>();

        while (
                latinMatcher.find()
                        &&
                        latinTokens.size()
                                <
                                5
        ) {

            latinTokens.add(
                    latinMatcher.group()
            );
        }

        if (
                !latinTokens.isEmpty()
        ) {

            violations.add(
                    "영문 또는 밑줄 문자열 '"
                            +
                            String.join(
                                    ", ",
                                    latinTokens
                            )
                            +
                            "'"
            );
        }


        /*
         * HTML
         */
        if (
                HTML_TAG_PATTERN
                        .matcher(
                                normalized
                        )
                        .find()
        ) {

            violations.add(
                    "HTML 태그 포함"
            );
        }


        /*
         * 강조 열고 닫힘
         *
         * 강조 개수는 Hard Fail이 아니다.
         */
        if (
                hasMalformedEmphasisMarkers(
                        normalized
                )
        ) {

            violations.add(
                    "강조 마커 '**'가 올바르게 닫히지 않음"
            );
        }


        /*
         * 명리 전문용어
         *
         * 단순 substring contains가 아니다.
         */
        for (
                String term
                : FORBIDDEN_SAJU_TERMS
        ) {

            if (
                    containsForbiddenKoreanTerm(
                            normalized,
                            term
                    )
            ) {

                violations.add(
                        "명리 전문용어 '"
                                +
                                term
                                +
                                "'"
                );
            }
        }


        /*
         * 사용자 호칭
         */
        for (
                String phrase
                : FORBIDDEN_ADDRESS_PHRASES
        ) {

            if (
                    normalized.contains(
                            phrase
                    )
            ) {

                violations.add(
                        "사용자 금지 호칭 '"
                                +
                                phrase
                                +
                                "'"
                );
            }
        }


        /*
         * 비 Timeline Category의 임의 연도
         */
        if (
                !categoryAllowsYear(
                        category
                )
                        &&
                        YEAR_PATTERN
                                .matcher(
                                        normalized
                                )
                                .find()
        ) {

            violations.add(
                    "현재 상담 장에서 허용되지 않은 연도 표현"
            );
        }


        /*
         * 법률 결과 단정
         */
        if (
                category
                        ==
                        ConsultationCategory.LEGAL_RISK
        ) {

            for (
                    String phrase
                    : FORBIDDEN_LEGAL_RESULT_PHRASES
            ) {

                if (
                        normalized.contains(
                                phrase
                        )
                ) {

                    violations.add(
                            "법률 결과 단정 '"
                                    +
                                    phrase
                                    +
                                    "'"
                    );
                }
            }
        }


        /*
         * 의료 결과 단정
         */
        if (
                category
                        ==
                        ConsultationCategory.HEALTH_TENDENCY
        ) {

            for (
                    String phrase
                    : FORBIDDEN_MEDICAL_ASSERTION_PHRASES
            ) {

                if (
                        normalized.contains(
                                phrase
                        )
                ) {

                    violations.add(
                            "의료 결과 단정 '"
                                    +
                                    phrase
                                    +
                                    "'"
                    );
                }
            }
        }

        if (
                violations.isEmpty()
        ) {

            return StoryValidationResult.success();
        }

        return StoryValidationResult.failure(
                String.join(
                        ", ",
                        violations
                )
        );
    }


    /*
     * =========================================================
     * Korean Forbidden Term
     * =========================================================
     *
     * 앞에 다른 한글이 붙어 있으면
     * 독립 전문용어 시작으로 보지 않는다.
     *
     * 관성:
     *
     * "관성이 강하다" -> 검출
     *
     * "일관성이 있다" -> 검출하지 않음
     */

    private boolean containsForbiddenKoreanTerm(

            String source,

            String term

    ) {

        Pattern pattern =
                Pattern.compile(
                        "(?<![가-힣A-Za-z0-9])"
                                +
                                Pattern.quote(
                                        term
                                )
                );

        return pattern
                .matcher(
                        source
                )
                .find();
    }


    private boolean hasMalformedEmphasisMarkers(

            String source

    ) {

        int markerCount =
                0;

        int index =
                0;

        while (
                true
        ) {

            index =
                    source.indexOf(
                            "**",
                            index
                    );

            if (
                    index < 0
            ) {

                break;
            }

            markerCount++;

            index +=
                    2;
        }

        return markerCount % 2 != 0;
    }


    /*
     * =========================================================
     * Category Focus
     * =========================================================
     */

    private String categoryFocusInstruction(

            ConsultationCategory category

    ) {

        return switch (
                category
                ) {

            case PERSONALITY ->
                    """
                    재물, 직업, 관계, 학습을 각각 별도 장처럼 나열하지 않습니다.
                    여러 상황을 관통하는 판단 방식, 선택 습관,
                    압박을 받을 때의 반응과 겉과 속의 차이를 설명합니다.
                    """;

            case PAST_LUCK ->
                    """
                    과거 특정 사건을 맞히지 않습니다.
                    지나온 시기마다 수월함과 부담,
                    선택과 반응의 차이를 설명합니다.
                    """;

            case SOCIAL_RELATIONSHIP ->
                    """
                    사람을 받아들이고 가까워지고,
                    기대와 책임이 반복될 때
                    거리와 태도를 조정하는 방식에 집중합니다.
                    """;

            case LOVE_STYLE ->
                    """
                    마음이 생겼을 때 표현하는 방식,
                    상대 반응을 확인하는 방식,
                    가까워질수록 달라지는 태도에 집중합니다.
                    """;

            case PARTNER_LUCK ->
                    """
                    오래 함께하는 관계에서 역할과 책임을 나누고,
                    관계의 안정감을 판단하며,
                    부담이 반복될 때 반응하는 방식에 집중합니다.
                    """;

            case WEALTH ->
                    """
                    돈과 자원을 선택하고 유지하고 사용하는 판단에 집중합니다.
                    단순 절약형·소비형 분류보다
                    무엇을 확인하고 언제 움직이는지를 설명합니다.
                    """;

            case CAREER ->
                    """
                    일을 맡는 방식,
                    책임을 받아들이는 방식,
                    성과를 만드는 과정에서 판단하고 움직이는 방식에 집중합니다.
                    """;

            case LEGAL_RISK ->
                    """
                    법적 결과를 예측하지 않습니다.
                    약속, 계약, 확인, 기록, 책임 범위를 다루는 태도에 집중합니다.
                    """;

            case HEALTH_TENDENCY ->
                    """
                    질병을 예측하지 않습니다.
                    활동과 휴식, 피로 누적,
                    자기 페이스와 회복 리듬에 집중합니다.
                    """;

            case CURRENT_YEAR_LUCK ->
                    """
                    올해 특정 사건을 맞히지 않습니다.
                    현재 시기에 달라지기 쉬운 선택과 반응을 설명합니다.
                    """;

            case FUTURE_LUCK ->
                    """
                    미래 사건을 예언하지 않습니다.
                    연도별로 달라질 수 있는 선택,
                    책임, 관계 조율, 자원 운용의 흐름을 설명합니다.
                    """;
        };
    }


    /*
     * =========================================================
     * Behavioral Translation
     * =========================================================
     */

    private String behavioralTranslationInstruction(

            ConsultationCategory category

    ) {

        return switch (
                category
                ) {

            case PERSONALITY ->
                    "성격 형용사보다 중요한 선택과 압박 속에서 무엇을 먼저 판단하고 언제 태도가 바뀌는지 설명합니다.";

            case PAST_LUCK ->
                    "사건이 아니라 시기별 선택의 속도, 부담 체감과 반응 차이를 설명합니다.";

            case SOCIAL_RELATIONSHIP ->
                    "관계가 시작되고 가까워지고 부담이 반복되는 과정에 따라 반응이 어떻게 달라지는지 설명합니다.";

            case LOVE_STYLE ->
                    "마음의 크기와 관계의 거리 변화에 따라 표현과 판단이 달라지는지 설명합니다.";

            case PARTNER_LUCK ->
                    "장기 관계에서 처음 역할을 받아들이는 모습과 반복된 책임 뒤의 반응 차이를 설명합니다.";

            case WEALTH ->
                    "돈을 움직이기 전 무엇을 확인하고 어떤 상태를 부담스럽게 느끼며 확신 전후 행동이 어떻게 달라지는지 설명합니다.";

            case CAREER ->
                    "책임을 맡기 전과 맡은 뒤의 판단과 행동 차이를 설명합니다.";

            case LEGAL_RISK ->
                    "결과 예측 대신 확인, 기록, 책임 범위를 받아들이는 습관을 설명합니다.";

            case HEALTH_TENDENCY ->
                    "질병 대신 활동량과 피로가 달라질 때 생활 리듬을 어떻게 다루는지 설명합니다.";

            case CURRENT_YEAR_LUCK ->
                    "사건 대신 현재 시기의 선택과 반응 방식 변화를 설명합니다.";

            case FUTURE_LUCK ->
                    "사건 대신 앞으로 시기별로 달라지는 행동과 판단 흐름을 설명합니다.";
        };
    }


    /*
     * =========================================================
     * Category Safety
     * =========================================================
     */

    private String categorySafetyInstruction(

            ConsultationCategory category

    ) {

        return switch (
                category
                ) {

            case PERSONALITY ->
                    "다른 열 개 상담 장을 하나씩 미리 설명하지 말고 공통 행동 패턴으로 통합하세요.";

            case PAST_LUCK ->
                    "특정 과거 사건이 실제로 있었다고 단정하지 마세요.";

            case SOCIAL_RELATIONSHIP ->
                    "대인관계 장을 연애·결혼·재물·직업 상담으로 바꾸지 마세요.";

            case LOVE_STYLE ->
                    "결혼 시기나 배우자의 미래를 예언하지 마세요.";

            case PARTNER_LUCK ->
                    "결혼 시기나 특정 인연의 등장을 예언하지 마세요.";

            case WEALTH ->
                    "관계나 직업 성공을 별도 상담으로 확장하지 마세요.";

            case CAREER ->
                    "재물 관리나 연애 이야기를 별도 상담으로 확장하지 마세요.";

            case LEGAL_RISK ->
                    "승패, 처벌, 구체적인 법적 사건을 예측하지 마세요.";

            case HEALTH_TENDENCY ->
                    "질병, 장기, 진단, 치료를 예측하거나 입력에 없는 건강 처방을 만들지 마세요.";

            case CURRENT_YEAR_LUCK ->
                    "올해 특정 사건이 반드시 발생한다고 단정하지 마세요.";

            case FUTURE_LUCK ->
                    "미래 특정 사건이나 결과를 확정하지 마세요.";
        };
    }


    /*
     * =========================================================
     * User Facing Area
     * =========================================================
     */

    private String userFacingArea(

            String domain

    ) {

        return switch (
                domain
                ) {

            case "WEALTH" ->
                    "돈과 자원을 판단하고 다루는 방식";

            case "CAREER" ->
                    "일과 책임을 맡고 성과를 만드는 방식";

            case "RELATIONSHIP" ->
                    "사람과 관계를 맺고 거리를 조절하는 방식";

            case "LEARNING_GROWTH" ->
                    "배우고 이해하고 표현하는 방식";

            case "HEALTH_LIFESTYLE" ->
                    "생활 리듬과 활동, 회복을 다루는 방식";

            default ->
                    "생활에서 선택하고 대응하는 방식";
        };
    }


    /*
     * =========================================================
     * User Facing Direction
     * =========================================================
     */

    private String userFacingDirection(

            String status

    ) {

        return switch (
                status
                ) {

            case "SUPPORTIVE" ->
                    "비교적 자연스럽게 힘을 쓰는 방향";

            case "PRESSURE" ->
                    "부담을 더 크게 체감할 수 있는 방향";

            case "MIXED" ->
                    "수월함과 부담이 함께 나타나는 방향";

            case "CONTEXTUAL" ->
                    "상황에 따라 반응 차이가 커질 수 있는 방향";

            case "NEUTRAL" ->
                    "한 방향으로 크게 치우치지 않는 흐름";

            case "CAUTION" ->
                    "조금 더 신중하게 다뤄야 하는 방향";

            default ->
                    "상황에 따라 달라질 수 있는 방향";
        };
    }


    /*
     * =========================================================
     * Category Name
     * =========================================================
     */

    private String categoryDisplayName(

            ConsultationCategory category

    ) {

        return switch (
                category
                ) {

            case PERSONALITY ->
                    "성격과 기본 성향";

            case PAST_LUCK ->
                    "지나온 운의 흐름";

            case SOCIAL_RELATIONSHIP ->
                    "대인관계";

            case LOVE_STYLE ->
                    "연애 성향";

            case PARTNER_LUCK ->
                    "배우자와 장기 인연";

            case WEALTH ->
                    "재물 운용 성향";

            case CAREER ->
                    "직업과 일의 흐름";

            case LEGAL_RISK ->
                    "계약과 책임에서 주의할 점";

            case HEALTH_TENDENCY ->
                    "건강과 생활 리듬";

            case CURRENT_YEAR_LUCK ->
                    "올해의 흐름";

            case FUTURE_LUCK ->
                    "앞으로의 흐름";
        };
    }


    /*
     * =========================================================
     * Priority
     * =========================================================
     */

    private int normalizedNarrativePriority(

            ConsultationGroundingProminence prominence

    ) {

        if (
                prominence == null
                        ||
                        prominence.narrativePriority()
                                <=
                                0
        ) {

            return Integer.MAX_VALUE;
        }

        return prominence.narrativePriority();
    }


    /*
     * =========================================================
     * Year
     * =========================================================
     */

    private boolean categoryAllowsYear(

            ConsultationCategory category

    ) {

        return category
                ==
                ConsultationCategory.PAST_LUCK

                ||

                category
                        ==
                        ConsultationCategory.CURRENT_YEAR_LUCK

                ||

                category
                        ==
                        ConsultationCategory.FUTURE_LUCK;
    }


    /*
     * =========================================================
     * Normalize
     * =========================================================
     */

    private String normalizeGeneratedStoryText(

            String storyText

    ) {

        return requireText(
                storyText,
                "Story Text는 필수입니다."
        )
                .replace(
                        "\r\n",
                        "\n"
                )
                .replace(
                        "\r",
                        "\n"
                )
                .trim();
    }


    /*
     * =========================================================
     * Section
     * =========================================================
     */

    private ConsultationInterpretationPromptSection
    findSection(

            ConsultationInterpretationPromptContext promptContext,

            ConsultationCategory category

    ) {

        return promptContext
                .sections()
                .stream()
                .filter(
                        section ->
                                section.category()
                                        ==
                                        category
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Prompt Context에서 Category Section을 찾을 수 없습니다: "
                                                +
                                                category
                                )
                );
    }


    /*
     * =========================================================
     * Utility
     * =========================================================
     */

    private static boolean isGpt4oMiniModel(

            String model

    ) {

        return model != null
                &&
                model
                        .trim()
                        .startsWith(
                                "gpt-4o-mini"
                        );
    }


    private static long elapsedMillis(

            long startedAtNanos

    ) {

        long elapsed =
                System.nanoTime()
                        -
                        startedAtNanos;

        if (
                elapsed <= 0L
        ) {

            return 0L;
        }

        return elapsed
                /
                1_000_000L;
    }


    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }


    private static String firstNonBlank(

            String... values

    ) {

        if (
                values == null
        ) {

            return "";
        }

        for (
                String value
                : values
        ) {

            if (
                    value != null
                            &&
                            !value.isBlank()
            ) {

                return value.trim();
            }
        }

        return "";
    }


    private static String safeExceptionMessage(

            Exception exception

    ) {

        if (
                exception == null
        ) {

            return "UNKNOWN";
        }

        String message =
                exception.getMessage();

        if (
                message == null
                        ||
                        message.isBlank()
        ) {

            return exception
                    .getClass()
                    .getSimpleName();
        }

        /*
         * API 응답 전문을 로그에 길게 남기지 않는다.
         */
        if (
                message.length()
                        >
                        300
        ) {

            return message.substring(
                    0,
                    300
            );
        }

        return message;
    }


    /*
     * =========================================================
     * Internal Types
     * =========================================================
     */

    private enum ReviewDecision {

        KEEP,

        REVISE
    }


    private record ReviewResult(

            ReviewDecision decision,

            String finalStoryText,

            String reviewSummary

    ) {

        private ReviewResult {

            Objects.requireNonNull(
                    decision,
                    "ReviewDecision은 필수입니다."
            );

            finalStoryText =
                    requireText(
                            finalStoryText,
                            "Reviewer finalStoryText는 필수입니다."
                    );

            reviewSummary =
                    reviewSummary == null
                            ?
                            ""
                            :
                            reviewSummary.trim();
        }
    }


    private record StoryValidationResult(

            boolean valid,

            String message

    ) {

        private StoryValidationResult {

            message =
                    message == null
                            ?
                            ""
                            :
                            message.trim();
        }

        static StoryValidationResult success() {

            return new StoryValidationResult(
                    true,
                    ""
            );
        }

        static StoryValidationResult failure(

                String message

        ) {

            return new StoryValidationResult(
                    false,
                    requireText(
                            message,
                            "Story Validation 실패 이유가 필요합니다."
                    )
            );
        }
    }
}