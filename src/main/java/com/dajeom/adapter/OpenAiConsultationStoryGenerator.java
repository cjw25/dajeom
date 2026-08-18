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
 * 정밀화 25차.
 *
 * =========================================================
 * 목적
 * =========================================================
 *
 * deterministic Java Grounding을
 * 사용자가 실제로 체감할 수 있는
 * 자연스러운 상담문으로 번역한다.
 *
 * OpenAI는 새로운 사주 계산을 하지 않는다.
 *
 * =========================================================
 * Pipeline
 * =========================================================
 *
 * Java Grounding
 *
 * ↓
 *
 * Prominence
 *
 * ↓
 *
 * Compact Writer Grounding
 *
 * ↓
 *
 * Writer
 *
 * {
 *   storyText,
 *   emphasis[]
 * }
 *
 * ↓
 *
 * Compact Reviewer Grounding
 *
 * ↓
 *
 * Reviewer
 *
 * {
 *   decision,
 *   finalStoryText,
 *   emphasis[],
 *   reviewSummary
 * }
 *
 * ↓
 *
 * Java Hard Safety
 *
 * ↓
 *
 * Java Emphasis Verification
 *
 * ↓
 *
 * ConsultationStoryGeneration
 *
 * - storyText 순수 텍스트
 * - emphasis 별도 메타데이터
 *
 * =========================================================
 * 25차 핵심
 * =========================================================
 *
 * OpenAI가 Markdown 강조를 만들지 않는다.
 *
 * Java도 storyText에 Markdown 강조를 삽입하지 않는다.
 *
 * 강조 대상 문자열은
 * emphasisByCategory로 끝까지 별도로 보존한다.
 *
 * Frontend가 검증된 문자열만
 * 실제 strong 태그로 렌더링한다.
 *
 * 따라서 사용자에게:
 *
 * - 별표
 * - Markdown
 * - 작성용 Placeholder
 *
 * 가 노출되지 않는다.
 *
 * =========================================================
 * 중요한 원칙
 * =========================================================
 *
 * prominence는:
 *
 * - 정확도 확률
 * - 적중 확률
 * - 길흉 점수
 * - 성공 확률
 *
 * 이 아니다.
 *
 * deterministic Source provenance를 이용한
 * 상담문 서술 우선순위다.
 *
 * =========================================================
 * 하지 않는 것
 * =========================================================
 *
 * - 없는 과거 사건 생성
 * - 미래 사건 예언
 * - 특정 직업 추측
 * - 특정 사람 추측
 * - 특정 경험 추측
 * - 의료 진단
 * - 법률 결과 예측
 * - prominence 숫자 사용자 노출
 * - Source 숫자 사용자 노출
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
            "DAJEOM_HUMAN_PATTERN_REVIEW_25";


    /*
     * =========================================================
     * Story Soft Target
     * =========================================================
     */

    private static final int STORY_TARGET_MIN_LENGTH =
            1200;

    private static final int STORY_TARGET_MAX_LENGTH =
            1900;


    /*
     * =========================================================
     * OpenAI Output Limit
     * =========================================================
     */

    private static final int WRITER_OUTPUT_TOKEN_LIMIT =
            3200;

    private static final int REVIEWER_OUTPUT_TOKEN_LIMIT =
            3200;

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
            4;

    private static final int REVIEWER_SCENARIO_EVIDENCE_LIMIT =
            2;

    private static final int REVIEWER_YEAR_EVIDENCE_LIMIT =
            2;

    private static final int WRITER_REASON_LIMIT =
            2;


    /*
     * =========================================================
     * Emphasis
     * =========================================================
     */

    private static final int MAX_EMPHASIS_COUNT =
            4;


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
     * Category Technical Fallback
     * =========================================================
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
     * 최종 사용자 출력 금지 명리 용어
     * =========================================================
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
     * 전체 Generation
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
                promptContext.categoryResolutionRequired()
        ) {

            diagnosticLogger
                    .generationSkippedCategoryResolutionRequired(
                            generationId,
                            model
                    );

            return Optional.empty();
        }

        if (
                !promptContext.hasSections()
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

        List<ConsultationCategory> expectedCategories =
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

        EnumMap<ConsultationCategory, String> stories =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        EnumMap<ConsultationCategory, List<String>> emphasisByCategory =
                new EnumMap<>(
                        ConsultationCategory.class
                );

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

                FinalStory finalStory =
                        generateHumanReviewedStory(
                                generationId,
                                section,
                                category,
                                categoryStartedAt
                        );

                String storyText =
                        finalStory.storyText();

                stories.put(
                        category,
                        storyText
                );

                emphasisByCategory.put(
                        category,
                        finalStory.emphasis()
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

                int visibleLength =
                        visibleStoryLength(
                                storyText
                        );

                if (
                        visibleLength
                                <
                                STORY_TARGET_MIN_LENGTH
                                ||
                                visibleLength
                                        >
                                        STORY_TARGET_MAX_LENGTH
                ) {

                    diagnosticLogger
                            .storyLengthOutOfRange(
                                    generationId,
                                    model,
                                    category,
                                    visibleLength,
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

                stories.put(
                        category,
                        TECHNICAL_STORY_FALLBACK
                );

                emphasisByCategory.put(
                        category,
                        List.of()
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
                        stories,
                        emphasisByCategory
                )
        );
    }


    /*
     * =========================================================
     * Human Review Pipeline
     * =========================================================
     */

    private FinalStory generateHumanReviewedStory(

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

        String writerGroundingJson =
                jsonMapper.writeValueAsString(
                        createWriterGroundingPayload(
                                section
                        )
                );

        DraftResult writerResult =
                requestWriterStory(
                        generationId,
                        writerGroundingJson,
                        category,
                        categoryStartedAt,
                        ""
                );

        StoryValidationResult writerValidation =
                validatePlainStoryHardSafety(
                        category,
                        writerResult.storyText()
                );

        String reviewerGroundingJson =
                jsonMapper.writeValueAsString(
                        createReviewerGroundingPayload(
                                section
                        )
                );

        ReviewResult reviewResult;

        try {

            reviewResult =
                    requestReview(
                            generationId,
                            reviewerGroundingJson,
                            category,
                            writerResult,
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

                return finalizeStory(
                        writerResult
                );
            }

            throw reviewerException;
        }

        StoryValidationResult reviewedValidation =
                validatePlainStoryHardSafety(
                        category,
                        reviewResult.finalStoryText()
                );

        if (
                reviewedValidation.valid()
        ) {

            return finalizeStory(
                    new DraftResult(
                            reviewResult.finalStoryText(),
                            reviewResult.emphasis()
                    )
            );
        }

        if (
                writerValidation.valid()
        ) {

            return finalizeStory(
                    writerResult
            );
        }

        String repairReason =
                firstNonBlank(
                        reviewedValidation.message(),
                        writerValidation.message(),
                        reviewResult.reviewSummary()
                );

        DraftResult repaired =
                requestWriterStory(
                        generationId,
                        writerGroundingJson,
                        category,
                        categoryStartedAt,
                        """
                        직전 작성문과 검토문 모두
                        최종 출력 안전조건을 통과하지 못했습니다.

                        문제:
                        %s

                        같은 계산 근거만 사용해서 다시 작성하세요.

                        새로운 과거 사건,
                        새로운 미래 사건,
                        새로운 사람,
                        새로운 경험,
                        새로운 해결책을 만들지 마세요.

                        상담 내용의 구체성을 불필요하게 줄이지 말고
                        위 출력 문제만 제거하세요.
                        """
                                .formatted(
                                        repairReason
                                )
                );

        StoryValidationResult repairedValidation =
                validatePlainStoryHardSafety(
                        category,
                        repaired.storyText()
                );

        if (
                repairedValidation.valid()
        ) {

            return finalizeStory(
                    repaired
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
     * Final Story
     * =========================================================
     */

    private FinalStory finalizeStory(

            DraftResult result

    ) {

        String plain =
                normalizeGeneratedStoryText(
                        result.storyText()
                );

        List<String> verifiedEmphasis =
                verifyEmphasis(
                        plain,
                        result.emphasis()
                );

        return new FinalStory(
                plain,
                verifiedEmphasis
        );
    }


    /*
     * =========================================================
     * Verified Emphasis
     * =========================================================
     */

    private List<String> verifyEmphasis(

            String storyText,

            List<String> emphasisSource

    ) {

        if (
                emphasisSource == null
                        ||
                        emphasisSource.isEmpty()
        ) {

            return List.of();
        }

        List<VerifiedEmphasis> verified =
                new ArrayList<>();

        LinkedHashSet<String> unique =
                new LinkedHashSet<>();

        for (
                String raw
                : emphasisSource
        ) {

            if (
                    raw == null
            ) {

                continue;
            }

            String phrase =
                    raw.trim();

            if (
                    phrase.isBlank()
            ) {

                continue;
            }

            if (
                    phrase.contains(
                            "\n"
                    )
            ) {

                continue;
            }

            if (
                    !unique.add(
                            phrase
                    )
            ) {

                continue;
            }

            int start =
                    storyText.indexOf(
                            phrase
                    );

            if (
                    start < 0
            ) {

                continue;
            }

            int end =
                    start
                            +
                            phrase.length();

            boolean overlaps =
                    verified
                            .stream()
                            .anyMatch(
                                    item ->
                                            start < item.end()
                                                    &&
                                                    item.start() < end
                            );

            if (
                    overlaps
            ) {

                continue;
            }

            verified.add(
                    new VerifiedEmphasis(
                            start,
                            end,
                            phrase
                    )
            );

            if (
                    verified.size()
                            >=
                            MAX_EMPHASIS_COUNT
            ) {

                break;
            }
        }

        verified.sort(
                Comparator.comparingInt(
                        VerifiedEmphasis::start
                )
        );

        return verified
                .stream()
                .map(
                        VerifiedEmphasis::phrase
                )
                .toList();
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
                "타고난판단",
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
                "타고난검토근거",
                createCompactEvidencePayload(
                        section.natalEvidences(),
                        section.category(),
                        REVIEWER_NATAL_EVIDENCE_LIMIT,
                        false
                )
        );

        result.put(
                "현재검토근거",
                createScenarioPayload(
                        section.timelineScenarios(),
                        section.category(),
                        REVIEWER_SCENARIO_EVIDENCE_LIMIT,
                        false
                )
        );

        result.put(
                "연도검토근거",
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
                    "중요도",
                    narrativeRole(
                            prominence
                    )
            );

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
                        "판단보조",
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

        ConsultationGroundingEvidence first =
                ordered.get(0);

        selected.add(
                first
        );

        selectedStatuses.add(
                first.status().name()
        );

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
                    selectedStatuses.add(
                            evidence
                                    .status()
                                    .name()
                    )
            ) {

                selected.add(
                        evidence
                );
            }
        }

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
     * Scenario
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
                        "시점안내",
                        "경계 불확실성이 있으므로 단일 사건으로 확정하지 않는다."
                );
            }

            item.put(
                    "판단",
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
     * Timeline Year
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
                        "시점안내",
                        "경계 불확실성이 있으므로 단일 사건으로 확정하지 않는다."
                );
            }

            item.put(
                    "판단",
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
     * Prominence Translation
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

            return "보완 판단";
        }

        return switch (
                prominence.narrativePriority()
                ) {

            case 1 ->
                    "가장 먼저 볼 판단";

            case 2 ->
                    "함께 깊게 볼 판단";

            case 3,
                 4 ->
                    "앞선 판단을 보완할 판단";

            default ->
                    "필요할 때만 사용할 보완 판단";
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

            return "서로 다른 직접 계산축과 반복 판단이 함께 뒷받침한다.";
        }

        if (
                diverse
        ) {

            return "서로 다른 종류의 직접 계산 근거가 함께 뒷받침한다.";
        }

        if (
                multiple
                        &&
                        repeated
        ) {

            return "여러 직접 근거와 반복 판단이 함께 뒷받침한다.";
        }

        if (
                multiple
        ) {

            return "여러 직접 계산 근거가 함께 뒷받침한다.";
        }

        if (
                repeated
        ) {

            return "같은 방향 판단이 반복된다.";
        }

        return "직접 계산 근거가 존재한다.";
    }


    /*
     * =========================================================
     * Writer API
     * =========================================================
     */

    private DraftResult requestWriterStory(

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

        return parseWriterResult(
                generationId,
                responseBody,
                category
        );
    }


    /*
     * =========================================================
     * Reviewer API
     * =========================================================
     */

    private ReviewResult requestReview(

            String generationId,

            String groundingJson,

            ConsultationCategory category,

            DraftResult writerResult,

            String hardValidationIssue,

            long categoryStartedAt

    ) throws Exception {

        Map<String, Object> requestBody =
                createReviewerRequestBody(
                        groundingJson,
                        category,
                        writerResult,
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

        long header =
                parseRetryAfterHeaderMillis(
                        exception
                );

        long body =
                parseBodyRetryMillis(
                        exception
                );

        long result =
                Math.max(
                        fallback,
                        Math.max(
                                header,
                                body
                        )
                );

        result +=
                300L;

        return Math.min(
                result,
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

            return seconds <= 0
                    ?
                    0L
                    :
                    (long) Math.ceil(
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
                    exception.getResponseBodyAsString();

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

            return seconds <= 0
                    ?
                    0L
                    :
                    (long) Math.ceil(
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
     *
     * 중요:
     *
     * createReviewerInput() 안에서
     * JsonMapper 직렬화를 수행하므로
     * checked Exception을 상위 requestReview()까지 전달한다.
     */
    private Map<String, Object> createReviewerRequestBody(

            String groundingJson,

            ConsultationCategory category,

            DraftResult writerResult,

            String hardValidationIssue

    ) throws Exception {

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
                        writerResult,
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
     * Writer Instructions
     * =========================================================
     */

    private String createWriterInstructions(

            ConsultationCategory category

    ) {

        return """
                당신은 한 사람에게 직접 이야기하는
                한국어 사주 상담문 작성자입니다.

                모든 사주 계산과 판단은
                Java deterministic 엔진에서 이미 끝났습니다.

                새로운 사주 판단을 만들지 마세요.

                현재 상담 장:
                %s

                현재 장의 범위:
                %s

                입력의 중요도가 높은 판단을 먼저 사용하세요.

                모든 판단을 하나씩 소개하지 마세요.

                가장 의미가 큰 2~4개의 패턴만 골라
                각각을 충분히 깊게 설명하세요.

                근거가 지원하는 범위에서 다음을 연결합니다.

                - 평소에는 어떻게 반응하는가
                - 무엇이 달라질 때 반응도 달라지는가
                - 그 순간 안에서 무엇을 다시 판단하는가
                - 그 판단이 밖에서 어떤 행동으로 보일 수 있는가
                - 비슷한 상황에서 어떤 방식이 반복되기 쉬운가

                다섯 요소를 억지로 모두 넣지 마세요.

                하지만 한 문장을
                단순한 성격 형용사로 끝내지 마세요.

                서로 다른 방향의 판단이 함께 있다면
                한쪽을 없애지 마세요.

                실제 Grounding이 지원하는 경우에만
                평소와 압박 이후의 반응 차이를 설명하세요.

                인사말,
                사주 일반론,
                장황한 안내문으로 시작하지 마세요.

                첫 문단부터
                이 사람에게 가장 강하게 나타나는
                구체적인 반응 패턴을 설명하세요.

                다음을 새로 만들지 마세요.

                - 실제로 있었다고 확인되지 않은 과거 사건
                - 미래 특정 사건
                - 특정 직장
                - 특정 사람
                - 특정 장소
                - 특정 소비 경험
                - 특정 연애 경험
                - 특정 가족 사건
                - 특정 건강 사건
                - 특정 법적 사건
                - 입력에 없는 생활 습관
                - 입력에 없는 해결 방법

                최종 storyText에는 다음 표현을 사용하지 마세요.

                %s

                영어 알파벳,
                밑줄,
                내부 필드명,
                중요도,
                근거 개수,
                계산 횟수도 노출하지 마세요.

                storyText에는
                별표,
                마크다운,
                HTML을 절대 넣지 마세요.

                storyText는 순수한 한국어 상담문만 작성합니다.

                화면에서 진하게 표시할 실제 상담 내용이 있다면
                emphasis 배열에
                storyText에 실제로 존재하는 문장이나 짧은 구절을
                그대로 복사해서 넣습니다.

                emphasis는 사용자에게 따로 보이는 라벨이 아닙니다.

                별도의 제목,
                작성용 이름,
                설명용 단어를 만들어 넣지 마세요.

                다음과 같은 작성 과정 표현을
                storyText나 emphasis의 독립 라벨로 출력하지 마세요.

                핵심 문장
                핵심 특징
                핵심 패턴
                최우선
                두 번째 핵심

                emphasis는 0~4개까지 가능합니다.

                강조할 만한 문장이 없으면
                빈 배열을 반환해도 됩니다.

                4~6개 정도의 자연스러운 문단을 목표로 합니다.

                각 문단은 새로운 근거를 추가하는 대신
                이미 선택한 중요한 패턴을 한 단계 더 깊게 설명합니다.

                같은 뜻을 다른 표현으로 반복해서
                길이를 늘리지 마세요.

                1200~1900자를 우선 목표로 합니다.

                단,
                분량보다 Grounding 충실도가 우선입니다.

                입력 근거가 부족하면
                없는 내용을 추가해서 분량을 채우지 마세요.

                Category 추가 규칙:

                %s

                반환:
                storyText
                emphasis

                두 항목만 반환하세요.
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

                        이전 출력에서 수정해야 할 점:

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

                공식 deterministic Grounding:
                %s

                %s

                작성 전 내부적으로만 확인하세요.

                1.
                가장 중요한 판단 2~4개는 무엇인가.

                2.
                서로 다른 방향의 판단이 동시에 있는가.

                3.
                한 특징을 단순 형용사가 아니라
                반응 과정으로 설명할 수 있는 근거가 있는가.

                4.
                현재 사람에게만 상대적으로 특징적인 부분은 무엇인가.

                이 검토 과정은 사용자에게 말하지 마세요.

                storyText에는 작성용 제목이나 라벨을 넣지 마세요.

                emphasis에는 storyText에 실제로 존재하는
                문장 또는 구절만 넣으세요.
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
     * Reviewer Instructions
     * =========================================================
     */

    private String createReviewerInstructions(

            ConsultationCategory category

    ) {

        return """
                당신은 새로운 사주 계산을 하지 않는
                한국어 상담문 편집자입니다.

                현재 상담 장:
                %s

                검토할 계산 근거와 Writer 초안이 제공됩니다.

                특정 단어 하나의 존재만 보고
                기계적으로 오류라고 판단하지 마세요.

                전체 문장의 의미를 봅니다.

                중요한 주장들이
                제공된 Grounding으로 실제 뒷받침되는지 확인하세요.

                Grounding에 없는:

                - 과거 사건
                - 미래 사건
                - 특정 경험
                - 특정 사람
                - 특정 직업
                - 특정 장소
                - 특정 생활 습관
                - 특정 해결책

                을 만들었다면 제거합니다.

                초안이 단순한 성격 형용사나
                무난한 일반론에서 끝난다면
                Grounding이 허용하는 범위에서 더 깊게 다듬으세요.

                가능한 경우:

                평소 반응
                →
                변화 조건
                →
                안쪽 판단
                →
                밖에서 보이는 행동
                →
                반복 패턴

                을 연결합니다.

                중요도가 높은 판단이
                글의 중심에 실제로 살아 있는지 확인하세요.

                반대 방향 또는 다른 방향 판단이 존재한다면
                하나를 삭제해서 단순한 사람으로 만들지 마세요.

                현재 장의 범위:

                %s

                다른 장을 독립된 상담처럼 확장하지 마세요.

                Writer 초안이 충분히 구체적인데
                문장을 짧고 무난한 표현으로 요약하지 마세요.

                REVISE는
                짧게 정리하는 것이 아니라
                의미를 바로잡으면서 더 정확하게 편집하는 것입니다.

                사용자 상담문에는:

                핵심 문장
                핵심 특징
                핵심 패턴
                최우선
                두 번째 핵심

                같은 작성 과정의 라벨을 넣지 마세요.

                finalStoryText에는
                별표나 마크다운을 넣지 마세요.

                화면에서 진하게 표시할 실제 상담 문장 또는 구절을
                emphasis 배열로 반환합니다.

                emphasis의 각 값은
                finalStoryText 안에 정확히 존재해야 합니다.

                emphasis는 별도의 사용자 문구가 아니라
                화면 표시 위치 데이터입니다.

                초안이 이미 정확하고 자연스러우면:

                decision = KEEP

                finalStoryText에는 초안을 그대로 넣고,
                emphasis도 적절하면 그대로 유지하세요.

                실제 의미상 문제가 있을 때만:

                decision = REVISE

                같은 Grounding만 사용하여 수정하세요.

                단순히 더 예쁘게 쓰기 위해
                매번 REVISE하지 마세요.

                새로운 사실은 만들지 마세요.
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


    /*
     * =========================================================
     * 여기에서 checked Exception 발생 가능
     * =========================================================
     */

    private String createReviewerInput(

            String groundingJson,

            ConsultationCategory category,

            DraftResult writerResult,

            String hardValidationIssue

    ) throws Exception {

        String writerJson =
                jsonMapper.writeValueAsString(
                        Map.of(
                                "storyText",
                                writerResult.storyText(),
                                "emphasis",
                                writerResult.emphasis()
                        )
                );

        String hardNotice =
                hardValidationIssue == null
                        ||
                        hardValidationIssue.isBlank()
                        ?
                        "명백한 출력 형식 오류는 없습니다."
                        :
                        """
                        Java 출력 검사에서 확인된 문제:

                        %s

                        finalStoryText에서는 반드시 해결하세요.
                        """
                                .formatted(
                                        hardValidationIssue
                                );

        return """
                상담 주제:
                %s

                검토에 필요한 Grounding:
                %s

                Writer 초안:
                %s

                추가 확인:
                %s

                초안을 처음부터 새로 요약하려 하지 마세요.

                먼저 초안을 살릴 수 있는지 판단하고,
                실제 의미 문제가 있을 때만 수정하세요.

                emphasis에는 finalStoryText의 실제 문자열만 넣으세요.
                """
                .formatted(
                        categoryDisplayName(
                                category
                        ),
                        groundingJson,
                        writerJson,
                        hardNotice
                );
    }


    /*
     * =========================================================
     * Writer Structured Output
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

        Map<String, Object> emphasisItems =
                new LinkedHashMap<>();

        emphasisItems.put(
                "type",
                "string"
        );

        Map<String, Object> emphasis =
                new LinkedHashMap<>();

        emphasis.put(
                "type",
                "array"
        );

        emphasis.put(
                "items",
                emphasisItems
        );

        Map<String, Object> properties =
                new LinkedHashMap<>();

        properties.put(
                "storyText",
                storyText
        );

        properties.put(
                "emphasis",
                emphasis
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
                        "storyText",
                        "emphasis"
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
                "dajeom_writer_story_v25"
        );

        format.put(
                "description",
                categoryDisplayName(
                        category
                )
                        +
                        " 상담문과 강조 후보"
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
     * Reviewer Structured Output
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

        Map<String, Object> emphasisItems =
                new LinkedHashMap<>();

        emphasisItems.put(
                "type",
                "string"
        );

        Map<String, Object> emphasis =
                new LinkedHashMap<>();

        emphasis.put(
                "type",
                "array"
        );

        emphasis.put(
                "items",
                emphasisItems
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
                "emphasis",
                emphasis
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
                        "emphasis",
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
                "dajeom_story_review_v25"
        );

        format.put(
                "description",
                categoryDisplayName(
                        category
                )
                        +
                        " 상담문 의미 검토"
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

    private DraftResult parseWriterResult(

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
                requireText(
                        root
                                .path(
                                        "storyText"
                                )
                                .asString(
                                        ""
                                ),
                        "Writer storyText가 필요합니다."
                );

        List<String> emphasis =
                readStringArray(
                        root.path(
                                "emphasis"
                        )
                );

        return new DraftResult(
                normalizeGeneratedStoryText(
                        storyText
                ),
                emphasis
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

        List<String> emphasis =
                readStringArray(
                        root.path(
                                "emphasis"
                        )
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
                emphasis,
                reviewSummary
        );
    }


    private List<String> readStringArray(

            JsonNode node

    ) {

        if (
                node == null
                        ||
                        !node.isArray()
        ) {

            return List.of();
        }

        LinkedHashSet<String> result =
                new LinkedHashSet<>();

        for (
                JsonNode item
                : node
        ) {

            String value =
                    item
                            .asString(
                                    ""
                            )
                            .trim();

            if (
                    value.isBlank()
            ) {

                continue;
            }

            result.add(
                    value
            );

            if (
                    result.size()
                            >=
                            MAX_EMPHASIS_COUNT
            ) {

                break;
            }
        }

        return List.copyOf(
                result
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
     * Plain Story Hard Safety
     * =========================================================
     */

    private StoryValidationResult validatePlainStoryHardSafety(

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

        if (
                normalized.contains(
                        "**"
                )
        ) {

            violations.add(
                    "모델이 Markdown 강조 마커를 storyText에 직접 생성함"
            );
        }

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
                    재물, 직업, 관계, 학습을 각각 별도 상담처럼 나열하지 않습니다.
                    여러 상황을 관통하는 판단 방식, 선택 방식,
                    압박을 받을 때의 반응,
                    겉과 속의 차이와 반복되는 선택 습관을 설명합니다.
                    """;

            case PAST_LUCK ->
                    """
                    특정 과거 사건을 맞히지 않습니다.
                    지나온 시기마다 무엇이 상대적으로 수월하거나 부담스러웠고
                    선택과 반응 방식이 어떻게 달라질 수 있었는지 설명합니다.
                    """;

            case SOCIAL_RELATIONSHIP ->
                    """
                    사람을 받아들이고 가까워지는 방식,
                    기대와 책임이 생긴 뒤의 반응,
                    부담이 반복될 때 거리와 태도가 달라지는 방식에 집중합니다.
                    """;

            case LOVE_STYLE ->
                    """
                    마음이 생겼을 때 표현 방식,
                    상대의 반응을 확인하는 방식,
                    가까워질수록 태도와 기대가 어떻게 달라지는지 설명합니다.
                    """;

            case PARTNER_LUCK ->
                    """
                    오래 함께하는 관계에서 역할과 책임을 나누는 방식,
                    관계의 안정감을 판단하는 기준,
                    반복된 부담을 받아들이는 방식에 집중합니다.
                    """;

            case WEALTH ->
                    """
                    돈과 자원을 선택하고 유지하고 사용하는 판단에 집중합니다.
                    단순 절약형이나 소비형으로 분류하지 말고
                    무엇을 확인한 뒤 움직이며
                    확신 전후 태도가 어떻게 달라지는지 설명합니다.
                    """;

            case CAREER ->
                    """
                    일을 맡는 방식,
                    책임을 받아들이는 방식,
                    성과를 만들기 위해 판단하고 움직이는 방식에 집중합니다.
                    """;

            case LEGAL_RISK ->
                    """
                    법적 결과를 예측하지 않습니다.
                    약속, 계약, 확인, 기록,
                    책임 범위를 다루는 태도에 집중합니다.
                    """;

            case HEALTH_TENDENCY ->
                    """
                    질병을 예측하지 않습니다.
                    활동과 휴식,
                    피로 누적,
                    자기 페이스,
                    회복 리듬을 다루는 생활 반응에 집중합니다.
                    """;

            case CURRENT_YEAR_LUCK ->
                    """
                    올해 특정 사건을 맞히지 않습니다.
                    현재 시기에 달라지기 쉬운 선택과 반응,
                    부담과 활동의 흐름을 설명합니다.
                    """;

            case FUTURE_LUCK ->
                    """
                    미래 특정 사건을 예언하지 않습니다.
                    연도별로 달라질 수 있는 판단,
                    선택, 책임, 관계 조율,
                    자원 운용의 흐름을 설명합니다.
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
                    "성격 형용사보다 중요한 선택과 압박 속에서 무엇을 먼저 판단하고 언제 태도가 달라지는지 설명합니다.";

            case PAST_LUCK ->
                    "실제 사건 대신 시기별 선택 속도, 부담 체감과 행동 방식의 차이를 설명합니다.";

            case SOCIAL_RELATIONSHIP ->
                    "관계가 시작되고 가까워지고 기대가 누적되는 과정에 따라 반응이 어떻게 달라지는지 설명합니다.";

            case LOVE_STYLE ->
                    "마음이 커지는 과정에서 표현과 확인 방식, 거리 조절이 어떻게 달라지는지 설명합니다.";

            case PARTNER_LUCK ->
                    "장기 관계에서 처음 역할을 받아들이는 방식과 반복된 책임 뒤의 반응 차이를 설명합니다.";

            case WEALTH ->
                    "돈을 움직이기 전 무엇을 확인하고 어떤 상태를 부담스럽게 느끼며 확신 전후 행동이 어떻게 달라지는지 설명합니다.";

            case CAREER ->
                    "책임을 맡기 전과 맡은 뒤의 판단과 행동 차이를 설명합니다.";

            case LEGAL_RISK ->
                    "법적 결과가 아니라 확인, 기록, 책임 범위를 받아들이는 생활 습관을 설명합니다.";

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
                    "다른 열 개 상담 장을 하나씩 미리 설명하지 말고 공통적인 행동과 판단 패턴으로 통합하세요.";

            case PAST_LUCK ->
                    "특정 과거 사건이 실제로 있었다고 단정하지 마세요.";

            case SOCIAL_RELATIONSHIP ->
                    "대인관계 장을 연애, 결혼, 재물, 직업 상담으로 바꾸지 마세요.";

            case LOVE_STYLE ->
                    "결혼 시기나 배우자의 미래를 예언하지 마세요.";

            case PARTNER_LUCK ->
                    "결혼 시기나 특정 인연의 등장을 예언하지 마세요.";

            case WEALTH ->
                    "관계나 직업 성공을 별도 상담으로 확장하지 마세요.";

            case CAREER ->
                    "재물 관리나 연애를 별도 상담으로 확장하지 마세요.";

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
     * User Facing
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
     * Helpers
     * =========================================================
     */

    private int normalizedNarrativePriority(

            ConsultationGroundingProminence prominence

    ) {

        if (
                prominence == null
                        ||
                        prominence.narrativePriority()
                                <= 0
        ) {

            return Integer.MAX_VALUE;
        }

        return prominence.narrativePriority();
    }


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


    private int visibleStoryLength(

            String storyText

    ) {

        if (
                storyText == null
        ) {

            return 0;
        }

        return storyText.length();
    }


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


    private record DraftResult(

            String storyText,

            List<String> emphasis

    ) {

        private DraftResult {

            storyText =
                    requireText(
                            storyText,
                            "Draft storyText는 필수입니다."
                    );

            emphasis =
                    emphasis == null
                            ?
                            List.of()
                            :
                            List.copyOf(
                                    emphasis
                            );
        }
    }


    private record ReviewResult(

            ReviewDecision decision,

            String finalStoryText,

            List<String> emphasis,

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

            emphasis =
                    emphasis == null
                            ?
                            List.of()
                            :
                            List.copyOf(
                                    emphasis
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


    private record VerifiedEmphasis(

            int start,

            int end,

            String phrase

    ) {

        private VerifiedEmphasis {

            if (
                    start < 0
                            ||
                            end <= start
            ) {

                throw new IllegalArgumentException(
                        "VerifiedEmphasis 범위가 올바르지 않습니다."
                );
            }

            phrase =
                    requireText(
                            phrase,
                            "VerifiedEmphasis phrase는 필수입니다."
                    );
        }
    }


    private record FinalStory(

            String storyText,

            List<String> emphasis

    ) {

        private FinalStory {

            storyText =
                    requireText(
                            storyText,
                            "Final storyText는 필수입니다."
                    );

            emphasis =
                    emphasis == null
                            ?
                            List.of()
                            :
                            List.copyOf(
                                    emphasis
                            );
        }
    }
}