package com.dajeom.adapter;

import com.dajeom.diagnostic.StoryGenerationDiagnosticLogger;
import com.dajeom.model.ConsultationGroundingDeterministicConclusion;
import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingProminence;
import com.dajeom.model.ConsultationGroundingTimelineScenario;
import com.dajeom.model.ConsultationGroundingTimelineYear;
import com.dajeom.model.ConsultationGroundingTimelineYearDeterministicContract;
import com.dajeom.model.ConsultationInterpretationPromptContext;
import com.dajeom.model.ConsultationInterpretationPromptSection;
import com.dajeom.model.ConsultationStoryGeneration;
import com.dajeom.model.NatalPersonalityStatementBundleContract;
import com.dajeom.model.NatalPersonalityStatementContract;
import com.dajeom.model.NatalPersonalityStatementFact;
import com.dajeom.model.NatalPersonalityStatementSelection;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityFindingSynthesisType;
import com.dajeom.type.PersonalityFindingTheme;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;
import com.dajeom.type.PersonalityStatementExpressionMode;
import com.dajeom.type.PersonalityStatementFactType;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OpenAiConsultationStoryGenerator
        implements ConsultationStoryGenerator {

    private static final Logger log =
            LoggerFactory.getLogger(
                    OpenAiConsultationStoryGenerator.class
            );

    private static final String PROVIDER =
            "OPENAI";

    private static final String OPENAI_BASE_URL =
            "https://api.openai.com";

    private static final String RESPONSES_PATH =
            "/v1/responses";

    private static final String HUMAN_GROUNDING_VERSION =
            "DAJEOM_HUMAN_PATTERN_REVIEW_29_TIMELINE_YEAR_CONTRACT";

    private static final int STORY_TARGET_MIN_LENGTH =
            1200;

    private static final int STORY_TARGET_MAX_LENGTH =
            1900;

    private static final int WRITER_OUTPUT_TOKEN_LIMIT =
            3200;

    private static final int REVIEWER_OUTPUT_TOKEN_LIMIT =
            4500;

    private static final int GPT_4O_MINI_MAX_OUTPUT_TOKENS =
            16_384;

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

    private static final int
            PERSONALITY_WRITER_LEGACY_EVIDENCE_LIMIT =
            3;

    private static final int
            PERSONALITY_REVIEWER_LEGACY_EVIDENCE_LIMIT =
            3;

    private static final int WRITER_REASON_LIMIT =
            2;

    private static final int
            WRITER_DETERMINISTIC_REASON_LIMIT =
            3;

    private static final int MAX_EMPHASIS_COUNT =
            4;

    private static final int MAX_RATE_LIMIT_RETRIES =
            3;

    /*
     * =================================================
     * Story Category Parallelism
     * =================================================
     *
     * Category끼리는 Grounding이 이미 독립적으로
     * 완성되어 있으므로 병렬 생성할 수 있다.
     *
     * 단일 Category 내부에서는:
     *
     * Writer
     * ->
     * Reviewer
     * ->
     * 필요 시 Repair
     *
     * 순서를 그대로 유지한다.
     *
     * OpenAI 요청을 과도하게 동시에 보내지 않기 위해
     * 최대 3개 Category만 병렬 실행한다.
     */
    private static final int MAX_PARALLEL_CATEGORY_GENERATIONS =
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

    private static final String TECHNICAL_STORY_FALLBACK =
            "이 장의 계산 근거는 준비되어 있지만 상담문을 정리하는 과정에서 "
                    +
                    "일시적인 오류가 발생했습니다. "
                    +
                    "계산되지 않은 내용을 임의로 추정해서 채우지 않았습니다.";

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

    private final StoryGenerationDiagnosticLogger diagnosticLogger;

    private final String apiKey;

    private final String model;

    private final int maxOutputTokens;

    public OpenAiConsultationStoryGenerator(

            JsonMapper jsonMapper,

            StoryGenerationDiagnosticLogger diagnosticLogger,

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


    @Override
    public Optional<ConsultationStoryGeneration> generate(

            ConsultationInterpretationPromptContext promptContext

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

        /*
         * =================================================
         * Category Parallel Generation
         * =================================================
         *
         * 각 Category는 deterministic Grounding이 이미
         * 독립적으로 완성된 뒤 Story Generator에 들어온다.
         *
         * 따라서 Category끼리는 병렬로 생성할 수 있다.
         *
         * 단,
         * 하나의 Category 내부에서는 반드시:
         *
         * Writer
         * ->
         * Reviewer
         * ->
         * 필요 시 Repair
         *
         * 순서를 그대로 유지한다.
         *
         * OpenAI 요청을 과도하게 동시에 보내지 않도록
         * 최대 3개 Category만 병렬 실행한다.
         *
         * Future 결과는 expectedCategories 순서대로 회수하여
         * 기존 Category 결과 수집 의미를 유지한다.
         */

        int parallelism =
                Math.min(
                        MAX_PARALLEL_CATEGORY_GENERATIONS,
                        expectedCategories.size()
                );

        ExecutorService executorService =
                Executors.newFixedThreadPool(
                        parallelism
                );

        List<Future<CategoryGenerationResult>> futures =
                new ArrayList<>(
                        expectedCategories.size()
                );

        try {

            for (
                    ConsultationCategory category
                    : expectedCategories
            ) {

                futures.add(
                        executorService.submit(
                                () ->
                                        generateCategory(
                                                generationId,
                                                promptContext,
                                                category
                                        )
                        )
                );
            }

            /*
             * Future를 expectedCategories 등록 순서대로 회수한다.
             *
             * 실제 실행 완료 순서는 달라도
             * 결과 Map의 Category 의미와 기존 정렬 기준은 유지된다.
             */
            for (
                    Future<CategoryGenerationResult> future
                    : futures
            ) {

                CategoryGenerationResult result =
                        future.get();

                stories.put(
                        result.category(),
                        result.storyText()
                );

                emphasisByCategory.put(
                        result.category(),
                        result.emphasis()
                );
            }

        }
        catch (
                InterruptedException exception
        ) {

            Thread
                    .currentThread()
                    .interrupt();

            throw new IllegalStateException(
                    "Story Category 병렬 생성 대기 중 Thread가 중단되었습니다.",
                    exception
            );

        }
        catch (
                ExecutionException exception
        ) {

            Throwable cause =
                    exception.getCause();

            if (
                    cause instanceof Error error
            ) {

                throw error;
            }

            if (
                    cause instanceof RuntimeException runtimeException
            ) {

                throw runtimeException;
            }

            throw new IllegalStateException(
                    "Story Category 병렬 생성 중 예상하지 못한 오류가 발생했습니다.",
                    cause
            );

        }
        finally {

            executorService
                    .shutdownNow();
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
     * Category Generation
     * =========================================================
     *
     * Category 단위 작업을 별도 메서드로 분리한다.
     *
     * 병렬화되는 범위는 이 메서드 전체이며
     * 이 안의 Writer -> Reviewer -> Repair 순서는
     * generateHumanReviewedStory()에서 기존 그대로 유지한다.
     *
     * 기존 generate()의 Category별 fallback 정책도
     * 그대로 이 메서드에서 보존한다.
     */
    private CategoryGenerationResult generateCategory(

            String generationId,

            ConsultationInterpretationPromptContext promptContext,

            ConsultationCategory category

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

            return new CategoryGenerationResult(
                    category,
                    storyText,
                    finalStory.emphasis()
            );

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

            return new CategoryGenerationResult(
                    category,
                    TECHNICAL_STORY_FALLBACK,
                    List.of()
            );
        }
    }


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

        if (
                category
                        == ConsultationCategory.PERSONALITY

                        && !section
                        .hasPersonalityStatementContract()
        ) {

            throw new IllegalStateException(
                    "PERSONALITY Story Generation에는 "
                            + "Personality Statement Contract가 필요합니다."
            );
        }

        /*
         * =================================================
         * PAST/FUTURE Runtime 구조 진단
         * =================================================
         *
         * Contract 검증보다 먼저 찍는다.
         *
         * 따라서 Contract가 누락되어 이후
         * IllegalStateException이 발생하더라도
         * 어떤 연도에서 연결이 끊겼는지 로그로 확인 가능하다.
         */

        diagnoseTimelineYearDeterministicContracts(
                generationId,
                section
        );

        /*
         * =================================================
         * PAST/FUTURE Timeline Contract 마지막 검증
         * =================================================
         */

        validateTimelineYearDeterministicContracts(
                section
        );

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
     * Timeline Contract Runtime Diagnosis
     * =========================================================
     */

    private void diagnoseTimelineYearDeterministicContracts(

            String generationId,

            ConsultationInterpretationPromptSection section

    ) {

        if (
                !timelineRangeCategory(
                        section.category()
                )
        ) {

            return;
        }

        if (
                !section.hasTimelineYears()
        ) {

            return;
        }

        for (
                ConsultationGroundingTimelineYear year
                : section.timelineYears()
        ) {

            if (
                    !year.hasDeterministicContract()
            ) {

                diagnosticLogger
                        .timelineYearContractState(
                                generationId,
                                model,
                                section.category(),
                                year.sajuYear(),
                                false,
                                "NONE",
                                "NONE",
                                false,
                                false,
                                false,
                                false,
                                year.evidenceCount(),
                                year.timelineUncertaintyExists()
                        );

                continue;
            }

            ConsultationGroundingTimelineYearDeterministicContract contract =
                    year.requireDeterministicContract();

            diagnosticLogger
                    .timelineYearContractState(
                            generationId,
                            model,
                            section.category(),
                            year.sajuYear(),
                            true,
                            contract
                                    .overallNarrative()
                                    .status()
                                    .name(),
                            contract
                                    .bigFlow()
                                    .status()
                                    .name(),
                            contract
                                    .hasPrimaryActiveDomain(),
                            contract
                                    .hasPrimaryPressureDomain(),
                            contract
                                    .hasCrossDomainPattern(),
                            contract
                                    .hasTurningPoint(),
                            year.evidenceCount(),
                            year.timelineUncertaintyExists()
                    );
        }
    }


    /*
     * =========================================================
     * PAST / FUTURE Timeline Contract Validation
     * =========================================================
     */

    private void validateTimelineYearDeterministicContracts(

            ConsultationInterpretationPromptSection section

    ) {

        if (
                !timelineRangeCategory(
                        section.category()
                )
        ) {

            return;
        }

        if (
                !section.hasTimelineYears()
        ) {

            return;
        }

        for (
                ConsultationGroundingTimelineYear year
                : section.timelineYears()
        ) {

            if (
                    !year.hasDeterministicContract()
            ) {

                throw new IllegalStateException(
                        section.category()
                                + " Story Generation에서 Timeline Year Deterministic Contract가 누락되었습니다. sajuYear="
                                + year.sajuYear()
                );
            }

            ConsultationGroundingTimelineYearDeterministicContract contract =
                    year.requireDeterministicContract();

            Objects.requireNonNull(
                    contract.overallNarrative(),
                    "Timeline Year OVERALL_NARRATIVE는 필수입니다."
            );

            Objects.requireNonNull(
                    contract.bigFlow(),
                    "Timeline Year BIG_FLOW는 필수입니다."
            );
        }
    }


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

        if (
                section.category()
                        == ConsultationCategory.PERSONALITY
        ) {

            return createPersonalityWriterGroundingPayload(
                    section
            );
        }

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
                "적용규칙",
                section.constraints()
        );

        if (
                timelineRangeCategory(
                        section.category()
                )
        ) {

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

        if (
                section.category()
                        == ConsultationCategory.PERSONALITY
        ) {

            return createPersonalityReviewerGroundingPayload(
                    section
            );
        }

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
                "적용규칙",
                section.constraints()
        );

        if (
                timelineRangeCategory(
                        section.category()
                )
        ) {

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
     * PERSONALITY Writer Payload
     * =========================================================
     */

    private Map<String, Object>
    createPersonalityWriterGroundingPayload(

            ConsultationInterpretationPromptSection section

    ) {

        NatalPersonalityStatementContract contract =
                section
                        .requirePersonalityStatementContract();

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "자료버전",
                HUMAN_GROUNDING_VERSION
        );

        result.put(
                "상담주제",
                categoryDisplayName(
                        ConsultationCategory.PERSONALITY
                )
        );

        result.put(
                "적용규칙",
                section.constraints()
        );

        result.put(
                "공식기본성향계약",
                createPersonalityContractPayload(
                        contract,
                        true
                )
        );

        result.put(
                "기존보조검증",
                Map.of(
                        "사용원칙",
                        "공식기본성향계약을 보조해서 확인할 때만 사용하며, 계약에 없는 새 성향을 만들거나 표현 강도를 올리는 데 사용하지 않는다.",
                        "판단",
                        createCompactEvidencePayload(
                                section.natalEvidences(),
                                ConsultationCategory.PERSONALITY,
                                PERSONALITY_WRITER_LEGACY_EVIDENCE_LIMIT,
                                false
                        )
                )
        );

        return result;
    }


    /*
     * =========================================================
     * PERSONALITY Reviewer Payload
     * =========================================================
     */

    private Map<String, Object>
    createPersonalityReviewerGroundingPayload(

            ConsultationInterpretationPromptSection section

    ) {

        NatalPersonalityStatementContract contract =
                section
                        .requirePersonalityStatementContract();

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "자료버전",
                HUMAN_GROUNDING_VERSION
        );

        result.put(
                "상담주제",
                categoryDisplayName(
                        ConsultationCategory.PERSONALITY
                )
        );

        result.put(
                "적용규칙",
                section.constraints()
        );

        result.put(
                "공식기본성향검토계약",
                createPersonalityContractPayload(
                        contract,
                        false
                )
        );

        result.put(
                "기존보조검증",
                Map.of(
                        "사용원칙",
                        "공식기본성향검토계약의 의미를 뒤집지 않는 범위의 보조 확인 자료다.",
                        "판단",
                        createCompactEvidencePayload(
                                section.natalEvidences(),
                                ConsultationCategory.PERSONALITY,
                                PERSONALITY_REVIEWER_LEGACY_EVIDENCE_LIMIT,
                                false
                        )
                )
        );

        return result;
    }


    /*
     * =========================================================
     * Personality Contract
     * =========================================================
     */

    private Map<String, Object>
    createPersonalityContractPayload(

            NatalPersonalityStatementContract contract,

            boolean writer

    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "원국완전성",
                contract.completeChart()
                        ? "출생시간을 포함한 원국"
                        : "출생시간이 없어 일부 판단 강도가 제한된 원국"
        );

        result.put(
                "전체배경",
                contract
                        .globalBackgroundContexts()
                        .stream()
                        .map(
                                this::createPersonalityBackgroundPayload
                        )
                        .toList()
        );

        result.put(
                "성향묶음",
                contract
                        .bundleContracts()
                        .stream()
                        .map(
                                bundle ->
                                        createPersonalityBundlePayload(
                                                bundle,
                                                writer
                                        )
                        )
                        .toList()
        );

        result.put(
                "해석원칙",
                List.of(
                        "반드시 설명할 판단과 보조 판단의 역할을 바꾸지 않는다.",
                        "강하게 표현 가능한 판단만 강한 문장으로 쓴다.",
                        "조건 설명이 필요한 묶음은 충돌과 조절 또는 반대 작용을 함께 보존한다.",
                        "내부 참고 근거는 직접적인 성격 단정으로 사용하지 않는다.",
                        "전체 배경은 독립적인 성격 단정이 아니라 다른 판단을 이해하는 맥락으로만 사용한다."
                )
        );

        return result;
    }


    private Map<String, Object>
    createPersonalityBundlePayload(

            NatalPersonalityStatementBundleContract bundle,

            boolean writer

    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "설명의무",
                bundle.required()
                        ? "반드시 설명"
                        : "보조 설명 가능"
        );

        result.put(
                "관련기능",
                bundle
                        .involvedFunctions()
                        .stream()
                        .map(
                                this::personalityFunctionMeaning
                        )
                        .toList()
        );

        if (
                !bundle.situations()
                        .isEmpty()
        ) {

            result.put(
                    "관련상황",
                    bundle
                            .situations()
                            .stream()
                            .map(
                                    this::personalitySituationMeaning
                            )
                            .toList()
            );
        }

        result.put(
                "중심판단",
                bundle
                        .mandatorySelections()
                        .stream()
                        .map(
                                selection ->
                                        createPersonalitySelectionPayload(
                                                selection,
                                                bundle.strongSelections()
                                                        .contains(
                                                                selection
                                                        )
                                        )
                        )
                        .toList()
        );

        result.put(
                "보조판단",
                bundle
                        .supportingSelections()
                        .stream()
                        .map(
                                selection ->
                                        createPersonalitySelectionPayload(
                                                selection,
                                                bundle.strongSelections()
                                                        .contains(
                                                                selection
                                                        )
                                        )
                        )
                        .toList()
        );

        result.put(
                "조건설명필수",
                bundle.qualificationRequired()
        );

        if (
                bundle.qualificationRequired()
        ) {

            result.put(
                    "조건설명원칙",
                    "한쪽 특징만 떼어 단정하지 말고 함께 존재하는 충돌, 조절, 중재 또는 조건을 같은 설명 안에 보존한다."
            );
        }

        List<Map<String, Object>> internalReferences =
                bundle
                        .internalReferenceSelections()
                        .stream()
                        .map(
                                this::createPersonalityInternalReferencePayload
                        )
                        .toList();

        if (
                !internalReferences.isEmpty()
        ) {

            result.put(
                    "내부참고",
                    internalReferences
            );

            result.put(
                    "내부참고사용원칙",
                    writer
                            ? "중심판단이나 보조판단의 문맥을 확인하는 데만 사용하고 독립 성향문장을 만들지 않는다."
                            : "Writer가 이 근거를 독립적인 성향 단정으로 사용했는지 검토하며, 사용했다면 제거한다."
            );
        }

        return result;
    }


    private Map<String, Object>
    createPersonalitySelectionPayload(

            NatalPersonalityStatementSelection selection,

            boolean strongAllowed

    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        NatalPersonalityStatementFact fact =
                selection.fact();

        result.put(
                "판단형태",
                personalityFactTypeMeaning(
                        fact.factType()
                )
        );

        result.put(
                "핵심의미",
                personalityFactMeaning(
                        fact
                )
        );

        if (
                !fact.involvedFunctions()
                        .isEmpty()
        ) {

            result.put(
                    "관련기능",
                    fact
                            .involvedFunctions()
                            .stream()
                            .map(
                                    this::personalityFunctionMeaning
                            )
                            .toList()
            );
        }

        if (
                !fact.situations()
                        .isEmpty()
        ) {

            result.put(
                    "관련상황",
                    fact
                            .situations()
                            .stream()
                            .map(
                                    this::personalitySituationMeaning
                            )
                            .toList()
            );
        }

        result.put(
                "근거수준",
                personalityGroundingMeaning(
                        fact.groundingLevel()
                )
        );

        result.put(
                "표현허용",
                personalityExpressionMeaning(
                        fact.expressionMode()
                )
        );

        result.put(
                "강한표현허용",
                strongAllowed
        );

        if (
                selection.qualificationRequired()
        ) {

            result.put(
                    "조건부설명",
                    "필수"
            );
        }

        return result;
    }


    private Map<String, Object>
    createPersonalityInternalReferencePayload(

            NatalPersonalityStatementSelection selection

    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "의미",
                personalityFactMeaning(
                        selection.fact()
                )
        );

        result.put(
                "근거수준",
                personalityGroundingMeaning(
                        selection
                                .fact()
                                .groundingLevel()
                )
        );

        result.put(
                "직접출력",
                "금지"
        );

        if (
                selection.qualificationRequired()
        ) {

            result.put(
                    "조건",
                    "다른 구조와 함께 볼 때만 참고"
            );
        }

        return result;
    }


    private Map<String, Object>
    createPersonalityBackgroundPayload(

            NatalPersonalityStatementSelection selection

    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "배경의미",
                personalityFactMeaning(
                        selection.fact()
                )
        );

        result.put(
                "사용원칙",
                "독립적인 성격 단정으로 바꾸지 않고 다른 직접판단의 작동 배경으로만 사용한다."
        );

        if (
                selection.qualificationRequired()
        ) {

            result.put(
                    "조건",
                    "배경 구조 안에도 함께 고려해야 할 제한 또는 혼재 요소가 존재한다."
            );
        }

        return result;
    }


    private String personalityFactMeaning(

            NatalPersonalityStatementFact fact

    ) {

        if (
                fact.factType()
                        == PersonalityStatementFactType.SYNTHESIS
        ) {

            PersonalityFindingSynthesisType synthesisType =
                    fact
                            .synthesisType()
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "SYNTHESIS Personality Fact에 synthesisType이 없습니다."
                                            )
                            );

            return personalitySynthesisMeaning(
                    synthesisType
            );
        }

        PersonalityFindingTheme theme =
                fact
                        .findingTheme()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Personality Finding Fact에 findingTheme이 없습니다."
                                        )
                        );

        return personalityFindingMeaning(
                theme
        );
    }


    private String personalityFactTypeMeaning(

            PersonalityStatementFactType factType

    ) {

        return switch (
                factType
                ) {

            case CORE_SELF ->
                    "자기기반 배경";

            case FUNCTION ->
                    "개별 기능의 작동";

            case CROSS_PATTERN ->
                    "기능 사이의 직접 상호작용";

            case SYNTHESIS ->
                    "둘 이상의 상호작용이 연결된 복합 구조";

            case INTERPERSONAL_CONTEXT ->
                    "대인 관계 구조의 배경";
        };
    }


    private String personalityFindingMeaning(

            PersonalityFindingTheme theme

    ) {

        return switch (
                theme
                ) {

            case CORE_OPERATING_CAPACITY ->
                    "외부 요구와 자기 선택을 받아들이고 운용할 때의 기본적인 자기 수용 여력";

            case SELF_ASSERTION_FUNCTION ->
                    "자기 기준을 세우고 자기 몫을 지키거나 주도권을 행사하려는 기능";

            case EXPRESSION_FUNCTION ->
                    "생각이나 판단을 밖으로 표현하고 결과물이나 행동으로 드러내는 기능";

            case RESOURCE_REALITY_FUNCTION ->
                    "현실적인 자원, 성과, 교환가치와 실제 결과를 확인하고 다루는 기능";

            case RESPONSIBILITY_FUNCTION ->
                    "규칙, 역할, 책임, 외부 기준을 받아들이고 대응하는 기능";

            case PROCESSING_FUNCTION ->
                    "정보와 경험을 받아들이고 안에서 정리하고 이해하며 판단을 준비하는 기능";

            case PRESSURE_REGULATED_BY_EXPRESSION ->
                    "압박이나 강한 요구를 받았을 때 표현과 실행을 통해 압력을 조절하려는 구조";

            case EXPRESSION_STANDARD_CONFLICT ->
                    "자기 표현 방식과 외부 기준 또는 요구가 서로 부딪힐 수 있는 구조";

            case EXPRESSION_REGULATED_BY_PROCESSING ->
                    "바로 표현하기보다 내부에서 이해하고 정리하는 작용이 표현을 조절하는 구조";

            case OUTPUT_RESTRAINED_BY_PROCESSING ->
                    "생각과 검토가 많아질수록 표현이나 결과물의 속도가 제한될 수 있는 구조";

            case REALITY_GENERATES_RESPONSIBILITY ->
                    "현실적인 결과와 자원 문제를 확인한 뒤 책임과 역할을 더 분명하게 받아들이는 구조";

            case RESPONSIBILITY_GENERATES_PROCESSING ->
                    "책임이나 기준이 생길수록 먼저 이해하고 정리하고 준비하려는 작용이 커지는 구조";

            case PRESSURE_GENERATES_PROCESSING ->
                    "압박이나 긴장이 커질수록 즉시 반응하기보다 안에서 더 많이 판단하고 정리하려는 구조";

            case REALITY_CONTROLS_PROCESSING ->
                    "현실적인 결과와 실용성 판단이 생각과 검토의 범위를 제한하거나 방향을 바꾸는 구조";

            case SELF_ASSERTION_COMPETES_FOR_RESOURCE ->
                    "자기 몫과 주도권을 지키려는 힘이 현실적인 자원 배분이나 교환 문제와 경쟁할 수 있는 구조";

            case RESOURCE_LOADS_CORE_SELF ->
                    "현실적인 성과나 자원 요구가 커질 때 자기 수용 여력보다 부담이 앞설 수 있는 구조";

            case RESPONSIBILITY_INTERNAL_MIXTURE ->
                    "책임과 외부 기준을 받아들이는 방식 안에 서로 다른 압력과 역할 기준이 함께 존재하는 구조";

            case INTERPERSONAL_RELATION_CONTEXT ->
                    "사람 사이의 연결과 긴장 구조가 다른 성향 판단에 영향을 줄 수 있는 배경";
        };
    }


    private String personalitySynthesisMeaning(

            PersonalityFindingSynthesisType synthesisType

    ) {

        return switch (
                synthesisType
                ) {

            case EXPRESSION_STANDARD_TENSION_WITH_PROCESSING_REGULATION ->
                    "자기 표현과 외부 기준이 부딪힐 수 있지만, 동시에 내부 검토와 정리 작용이 표현을 조절하는 복합 구조";

            case REALITY_RESPONSIBILITY_PROCESSING_CHAIN ->
                    "현실적인 결과를 확인하는 판단이 책임 수용으로 이어지고, 책임이 다시 내부 검토와 준비를 강화하는 연결 구조";

            case PRESSURE_PROCESSING_EXPRESSION_CHAIN ->
                    "압박이 커질수록 내부 판단과 정리가 활성화되고, 그 과정을 거쳐 표현 방식이 조절되는 연결 구조";

            case RESPONSIBILITY_MIXTURE_WITH_PROCESSING_PATH ->
                    "책임과 외부 기준을 받아들이는 방식에 혼재가 있으면서도, 그것을 내부에서 정리하고 이해하려는 통로가 함께 존재하는 구조";

            case REALITY_AND_RESPONSIBILITY_CONVERGE_ON_PROCESSING ->
                    "현실적인 결과 판단과 책임의식이 서로 다른 방향에서 내부 검토와 판단 과정에 함께 영향을 주는 구조";

            case RESOURCE_LOAD_WITH_SELF_ASSERTION_COMPETITION ->
                    "현실적인 자원과 성과 부담이 커지는 상황에서 자기 몫과 주도권을 지키려는 힘도 함께 작동하는 복합 부담 구조";
        };
    }


    private String personalityFunctionMeaning(

            NatalTenGodFunctionType functionType

    ) {

        return switch (
                functionType
                ) {

            case SELF_ASSERTION ->
                    "자기 기준과 주도권";

            case EXPRESSION ->
                    "표현과 실행";

            case RESOURCE_REALITY ->
                    "현실 결과와 자원";

            case RESPONSIBILITY ->
                    "책임과 외부 기준";

            case PROCESSING ->
                    "이해와 내부 정리";
        };
    }


    private String personalitySituationMeaning(

            NatalResponseSituationType situationType

    ) {

        return switch (
                situationType
                ) {

            case PRESSURE_AND_RESPONSIBILITY ->
                    "압박과 책임이 동시에 커지는 상황";

            case RULES_AND_EXPRESSION ->
                    "외부 기준과 자기 표현이 함께 작동하는 상황";

            case PROCESSING_AND_EXPRESSION ->
                    "생각을 정리하는 과정과 표현이 함께 작동하는 상황";

            case REALITY_AND_RESPONSIBILITY ->
                    "현실적인 결과와 책임을 함께 판단하는 상황";

            case REALITY_AND_PROCESSING ->
                    "실용성 판단과 내부 검토가 함께 필요한 상황";

            case COMPETITION_AND_RESOURCE ->
                    "자기 몫과 자원 배분이 함께 걸린 상황";

            case RESOURCE_CAPACITY ->
                    "현실적인 요구와 자기 수용 여력의 균형이 필요한 상황";

            case MIXED_RESPONSIBILITY ->
                    "서로 다른 역할 기준과 책임 압력이 함께 들어오는 상황";
        };
    }


    private String personalityGroundingMeaning(

            PersonalityJudgmentGroundingLevel groundingLevel

    ) {

        return switch (
                groundingLevel
                ) {

            case REFERENCE ->
                    "내부 참고 수준";

            case CONTEXTUAL ->
                    "조건과 문맥을 함께 볼 수준";

            case GROUNDED ->
                    "직접 설명 가능한 수준";

            case STRONGLY_GROUNDED ->
                    "구조적 근거가 강해 분명하게 설명 가능한 수준";
        };
    }


    private String personalityExpressionMeaning(

            PersonalityStatementExpressionMode mode

    ) {

        return switch (
                mode
                ) {

            case INTERNAL_REFERENCE ->
                    "직접 사용자 문장 금지";

            case CONTEXT_ONLY ->
                    "배경과 문맥에서만 사용";

            case DIRECT ->
                    "직접 설명 가능";

            case DIRECT_WITH_QUALIFICATION ->
                    "직접 설명 가능하나 조건이나 반대 작용을 함께 설명";

            case STRONG_DIRECT ->
                    "분명한 직접 표현 가능";

            case STRONG_DIRECT_WITH_QUALIFICATION ->
                    "분명하게 표현하되 조건이나 반대 작용도 반드시 함께 설명";
        };
    }


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
                        limit,
                        category
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


    private List<ConsultationGroundingEvidence>
    selectImportantEvidences(

            List<ConsultationGroundingEvidence> source,

            int limit,

            ConsultationCategory category

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

        Objects.requireNonNull(
                category,
                "ConsultationCategory가 필요합니다."
        );

        LinkedHashMap<String, ConsultationGroundingEvidence> unique =
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

        if (
                timelineRangeCategory(
                        category
                )
        ) {

            return selectTimelineRangeImportantEvidences(
                    ordered,
                    limit
            );
        }

        return selectStatusDiverseImportantEvidences(
                ordered,
                limit
        );
    }


    private List<ConsultationGroundingEvidence>
    selectTimelineRangeImportantEvidences(

            List<ConsultationGroundingEvidence> ordered,

            int limit

    ) {

        List<ConsultationGroundingEvidence> selected =
                new ArrayList<>(
                        limit
                );

        Set<String> selectedDomains =
                new LinkedHashSet<>();

        Set<String> selectedStatuses =
                new LinkedHashSet<>();

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
                    selectedDomains.add(
                            evidence
                                    .domain()
                                    .name()
                    )
            ) {

                selected.add(
                        evidence
                );

                selectedStatuses.add(
                        evidence
                                .status()
                                .name()
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
                    selected.contains(
                            evidence
                    )
            ) {

                continue;
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


    private List<ConsultationGroundingEvidence>
    selectStatusDiverseImportantEvidences(

            List<ConsultationGroundingEvidence> ordered,

            int limit

    ) {

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

            item.put(
                    "월인덱스",
                    scenario.monthIndex()
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

            item.put(
                    "월인덱스들",
                    year.monthIndices()
            );

            if (
                    year.timelineUncertaintyExists()
            ) {

                item.put(
                        "시점안내",
                        "경계 불확실성이 있으므로 단일 사건으로 확정하지 않는다."
                );
            }

            if (
                    timelineRangeCategory(
                            category
                    )
            ) {

                if (
                        !year.hasDeterministicContract()
                ) {

                    throw new IllegalStateException(
                            category
                                    + " Year Payload에서 Deterministic Contract가 누락되었습니다. sajuYear="
                                    + year.sajuYear()
                    );
                }

                item.put(
                        "연도종합판정",
                        createTimelineYearDeterministicContractPayload(
                                year.requireDeterministicContract(),
                                includeReasons
                        )
                );

                item.put(
                        "연도세부근거",
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

                continue;
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


    private Map<String, Object>
    createTimelineYearDeterministicContractPayload(

            ConsultationGroundingTimelineYearDeterministicContract contract,

            boolean includeReasons

    ) {

        Objects.requireNonNull(
                contract,
                "Timeline Year Deterministic Contract가 필요합니다."
        );

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "사용원칙",
                List.of(
                        "최종연도판정과 전체흐름이 해당 연도의 전체 방향을 결정한다.",
                        "주된작동축은 가장 좋은 분야나 가장 나쁜 분야가 아니라 시간적 작동 자격이 상대적으로 가장 분명한 생활영역이다.",
                        "주된부담축은 전체 연도를 부담 방향으로 바꾸는 값이 아니라 명시적 부담 가운데 상대적으로 분명한 축이다.",
                        "영역간구조는 여러 생활영역의 방향이 함께 존재하는 방식을 설명하며 전체 방향을 다시 계산하지 않는다.",
                        "전환점은 전후 연도 구조의 변화이며 구체적인 사건 발생을 의미하지 않는다.",
                        "연도세부근거를 다시 세거나 합산해서 이 종합판정을 변경하지 않는다."
                )
        );

        result.put(
                "최종연도판정",
                createDeterministicConclusionPayload(
                        contract.overallNarrative(),
                        includeReasons
                )
        );

        result.put(
                "전체흐름",
                createDeterministicConclusionPayload(
                        contract.bigFlow(),
                        includeReasons
                )
        );

        contract
                .primaryActiveDomain()
                .ifPresent(
                        conclusion ->
                                result.put(
                                        "주된작동축",
                                        createDeterministicConclusionPayload(
                                                conclusion,
                                                includeReasons
                                        )
                                )
                );

        contract
                .primaryPressureDomain()
                .ifPresent(
                        conclusion ->
                                result.put(
                                        "주된부담축",
                                        createDeterministicConclusionPayload(
                                                conclusion,
                                                includeReasons
                                        )
                                )
                );

        contract
                .crossDomainPattern()
                .ifPresent(
                        conclusion ->
                                result.put(
                                        "영역간구조",
                                        createDeterministicConclusionPayload(
                                                conclusion,
                                                includeReasons
                                        )
                                )
                );

        contract
                .turningPoint()
                .ifPresent(
                        conclusion ->
                                result.put(
                                        "전환점",
                                        createDeterministicConclusionPayload(
                                                conclusion,
                                                includeReasons
                                        )
                                )
                );

        List<String> unavailableComponents =
                new ArrayList<>();

        if (
                !contract.hasPrimaryActiveDomain()
        ) {

            unavailableComponents.add(
                    "단일 주된 작동축"
            );
        }

        if (
                !contract.hasPrimaryPressureDomain()
        ) {

            unavailableComponents.add(
                    "단일 주된 부담축"
            );
        }

        if (
                !contract.hasCrossDomainPattern()
        ) {

            unavailableComponents.add(
                    "별도 영역간 구조"
            );
        }

        if (
                !contract.hasTurningPoint()
        ) {

            unavailableComponents.add(
                    "구조적 전환점"
            );
        }

        if (
                !unavailableComponents.isEmpty()
        ) {

            result.put(
                    "확정하지않은판단",
                    unavailableComponents
            );

            result.put(
                    "누락판단사용원칙",
                    "확정하지 않은 판단은 연도세부근거를 이용해 새로 추정하거나 채우지 않는다."
            );
        }

        return result;
    }


    private Map<String, Object>
    createDeterministicConclusionPayload(

            ConsultationGroundingDeterministicConclusion conclusion,

            boolean includeReasons

    ) {

        Objects.requireNonNull(
                conclusion,
                "Deterministic Conclusion이 필요합니다."
        );

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "반응방향",
                userFacingDirection(
                        conclusion
                                .status()
                                .name()
                )
        );

        result.put(
                "판단메모",
                conclusion.summary()
        );

        if (
                !conclusion
                        .sourceDomainStatuses()
                        .isEmpty()
        ) {

            List<Map<String, Object>> domainStructures =
                    conclusion
                            .sourceDomainStatuses()
                            .entrySet()
                            .stream()
                            .sorted(
                                    Comparator.comparing(
                                            entry ->
                                                    entry
                                                            .getKey()
                                                            .ordinal()
                                    )
                            )
                            .map(
                                    entry -> {

                                        Map<String, Object> domainItem =
                                                new LinkedHashMap<>();

                                        domainItem.put(
                                                "생활주제",
                                                userFacingArea(
                                                        entry
                                                                .getKey()
                                                                .name()
                                                )
                                        );

                                        domainItem.put(
                                                "관련방향",
                                                entry
                                                        .getValue()
                                                        .stream()
                                                        .sorted(
                                                                Comparator.comparingInt(
                                                                        Enum::ordinal
                                                                )
                                                        )
                                                        .map(
                                                                status ->
                                                                        userFacingDirection(
                                                                                status.name()
                                                                        )
                                                        )
                                                        .toList()
                                        );

                                        return domainItem;
                                    }
                            )
                            .toList();

            result.put(
                    "관련생활구조",
                    domainStructures
            );
        }

        List<Integer> dependencyYears =
                conclusion
                        .dependencies()
                        .stream()
                        .map(
                                dependency ->
                                        dependency.sajuYear()
                        )
                        .filter(
                                Objects::nonNull
                        )
                        .distinct()
                        .sorted()
                        .toList();

        if (
                dependencyYears.size()
                        >= 2
        ) {

            result.put(
                    "구조비교연도들",
                    dependencyYears
            );
        }

        if (
                includeReasons
                        &&
                        !conclusion
                                .reasons()
                                .isEmpty()
        ) {

            result.put(
                    "판단보조",
                    conclusion
                            .reasons()
                            .stream()
                            .limit(
                                    WRITER_DETERMINISTIC_REASON_LIMIT
                            )
                            .toList()
            );
        }

        return result;
    }


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

                단,
                지나온 운과 앞으로의 흐름에서
                연도종합판정이 제공되면
                그것이 raw 연도세부근거보다 우선합니다.

                현재 장의 서술 구조:
                %s

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

                첫 문단 시작 규칙:
                %s

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

                각 문단은 현재 장의 서술 구조를 따르면서
                이미 제공된 근거를 한 단계 더 깊게 설명합니다.

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
                        categoryWritingStructureInstruction(
                                category
                        ),
                        categoryOpeningInstruction(
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

                %s

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
                        correctionSection,
                        writerInternalChecklist(
                                category
                        )
                );
    }


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

                지나온 운 또는 앞으로의 흐름에서
                연도종합판정이 제공되었다면
                Writer가 raw 연도세부근거를 이용해
                그 결론을 다시 계산하거나 뒤집지 않았는지
                반드시 확인하세요.

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

                현재 장의 검토 구조:

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
                        ),
                        categoryReviewStructureInstruction(
                                category
                        )
                );
    }


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
                "dajeom_writer_story_v29"
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
                "dajeom_story_review_v29"
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
                        "Reviewer finalStoryText는 필수입니다."
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


    private String categoryWritingStructureInstruction(

            ConsultationCategory category

    ) {

        if (
                category
                        ==
                        ConsultationCategory.PERSONALITY
        ) {

            return """
                    공식기본성향계약의 성향묶음을 기준으로 작성합니다.

                    '반드시 설명'으로 표시된 묶음이 있으면
                    그것을 먼저 충분히 설명하세요.

                    한 묶음 안에서는 중심판단을 먼저 설명하고
                    보조판단은 중심판단이 실제 생활에서
                    어떻게 드러날 수 있는지를 구체화하는 데 사용하세요.

                    강한표현허용이 참인 판단만
                    비교적 분명하고 단정적인 어조를 사용할 수 있습니다.

                    강한표현허용이 아닌 다른 판단까지
                    같은 강도로 확대하지 마세요.

                    조건설명필수가 참이면
                    한쪽 특징만 떼어 쓰지 말고
                    함께 존재하는 충돌, 조절, 중재 또는 조건을
                    같은 문단 안에서 설명하세요.

                    내부참고는 독립 문장으로 만들지 않습니다.

                    전체배경은 성격 자체로 단정하지 말고
                    중심판단이 어떤 조건에서 작동하는지를
                    이해하는 배경으로만 사용합니다.

                    서로 연결된 판단을
                    성격 항목 여러 개로 잘게 쪼개 나열하지 말고
                    한 사람이 실제로 판단하고 반응하는
                    하나의 과정으로 설명하세요.
                    """;
        }

        if (
                timelineRangeCategory(
                        category
                )
        ) {

            return """
                    이 장에서는 여러 해를 하나의 공통 성향이나
                    하나의 생활주제로 먼저 묶지 않습니다.

                    공식 Grounding의 연도흐름에 있는
                    각 연도를 제공된 순서대로 설명하세요.

                    각 연도에서는 반드시
                    연도종합판정을 먼저 읽고
                    그 연도의 전체 방향을 고정하세요.

                    최종연도판정과 전체흐름은
                    raw 연도세부근거보다 상위의 공식 결론입니다.

                    주된작동축이 있으면
                    그 해에서 상대적으로 작동이 분명한 생활영역으로 설명합니다.

                    주된부담축이 있으면
                    전체 연도를 부담 방향으로 바꾸지 말고
                    이미 정해진 전체 흐름 안의 부담축으로 설명합니다.

                    영역간구조가 있으면
                    서로 다른 생활영역의 방향이
                    어떻게 함께 존재하는지 설명합니다.

                    전환점이 있으면
                    전후 연도와 비교한 구조 변화로만 설명하고
                    특정 사건으로 번역하지 않습니다.

                    확정하지않은판단에 들어 있는 항목은
                    연도세부근거를 이용해 새로 만들어 채우지 마세요.

                    연도세부근거는
                    연도종합판정을 이해시키는 구체적인 설명에만 사용하세요.

                    문단 또는 문단의 첫 문장은
                    Grounding에 실제로 있는 해당 연도를
                    자연스럽게 드러내세요.

                    Grounding에 없는 연도는 만들지 마세요.

                    한 해의 근거를
                    다른 해의 결론을 설명하는 데 섞지 마세요.
                    """;
        }

        return """
                모든 판단을 하나씩 소개하지 마세요.

                가장 의미가 큰 2~4개의 패턴만 골라
                각각을 충분히 깊게 설명하세요.
                """;
    }


    private String categoryOpeningInstruction(

            ConsultationCategory category

    ) {

        if (
                category
                        ==
                        ConsultationCategory.PERSONALITY
        ) {

            return """
                    공식기본성향계약에 '반드시 설명' 묶음이 있다면
                    첫 문단부터 그 묶음의 중심판단을
                    사용자가 일상에서 알아볼 수 있는 반응 방식으로 설명하세요.

                    '당신은 이런 사람입니다'처럼
                    추상적인 형용사 하나로 시작하지 마세요.

                    판단과 반응이 어떤 조건에서 달라지는지를
                    처음부터 구체적으로 보여주세요.
                    """;
        }

        if (
                category
                        ==
                        ConsultationCategory.PAST_LUCK
        ) {

            return """
                    전체 과거를 한 문장으로 총평하지 마세요.

                    연도흐름에서 실제 판단이 있는
                    첫 번째 연도부터 바로 시작하세요.

                    첫 연도의 연도종합판정을 먼저 기준으로 삼고
                    연도세부근거는 그 결론을 설명하는 데만 사용하세요.

                    여러 해를 관계나 하나의 공통 주제로 묶는
                    도입문을 쓰지 마세요.
                    """;
        }

        if (
                category
                        ==
                        ConsultationCategory.FUTURE_LUCK
        ) {

            return """
                    앞으로의 여러 해를 한 문장으로
                    먼저 좋다거나 나쁘다고 총평하지 마세요.

                    연도흐름에서 실제 판단이 있는
                    첫 번째 미래 연도부터 시작하세요.

                    첫 연도의 연도종합판정을 기준으로
                    전체 방향과 주된 작동축을 설명하고,
                    연도세부근거는 그 결론을 구체화하는 데만 사용하세요.

                    여러 미래 연도를 하나의 사건 예측이나
                    하나의 공통 결과로 묶지 마세요.
                    """;
        }

        return """
                첫 문단부터
                이 사람에게 가장 강하게 나타나는
                구체적인 반응 패턴을 설명하세요.
                """;
    }


    private String writerInternalChecklist(

            ConsultationCategory category

    ) {

        if (
                category
                        ==
                        ConsultationCategory.PERSONALITY
        ) {

            return """
                    1.
                    '반드시 설명' 성향묶음의 중심판단을
                    빠뜨리지 않았는가.

                    2.
                    보조판단을 새로운 독립 성향으로
                    과장하지 않았는가.

                    3.
                    강한표현허용이 참인 판단만
                    강한 어조로 표현했는가.

                    4.
                    조건설명필수인 묶음에서
                    충돌과 조절 또는 반대 작용 중
                    하나를 지우지 않았는가.

                    5.
                    내부참고를 직접 성격 단정으로
                    사용하지 않았는가.

                    6.
                    전체배경을
                    고집, 소심함, 사교성 같은
                    단순 심리문장으로 직접 바꾸지 않았는가.

                    7.
                    기존보조검증을 이용해
                    공식기본성향계약에 없는
                    새로운 성향을 만들지 않았는가.

                    8.
                    같은 의미의 개별 판단과 복합 판단을
                    여러 번 반복하지 않았는가.
                    """;
        }

        if (
                timelineRangeCategory(
                        category
                )
        ) {

            return """
                    1.
                    각 연도의 최종연도판정과 전체흐름을
                    그 연도의 전체 방향으로 그대로 사용했는가.

                    2.
                    연도세부근거의 개수나 반복을 보고
                    전체 방향을 다시 계산하지 않았는가.

                    3.
                    주된작동축을 가장 좋은 분야나
                    가장 나쁜 분야로 바꾸지 않았는가.

                    4.
                    주된부담축만 보고
                    연도 전체를 부담 방향으로 바꾸지 않았는가.

                    5.
                    영역간구조를
                    전체흐름을 덮어쓰는 결론으로 사용하지 않았는가.

                    6.
                    전환점을 특정 사건 발생이나
                    반드시 큰일이 생기는 시기로 바꾸지 않았는가.

                    7.
                    확정하지않은판단을
                    raw 연도세부근거로 새로 만들어 채우지 않았는가.

                    8.
                    각 연도의 근거를 다른 연도와 섞지 않았는가.

                    9.
                    실제 과거 사건 또는 미래 특정 사건을
                    새로 만들지 않았는가.
                    """;
        }

        return """
                1.
                가장 중요한 판단 2~4개는 무엇인가.

                2.
                서로 다른 방향의 판단이 동시에 있는가.

                3.
                한 특징을 단순 형용사가 아니라
                반응 과정으로 설명할 수 있는 근거가 있는가.

                4.
                현재 사람에게만 상대적으로 특징적인 부분은 무엇인가.
                """;
    }


    private String categoryReviewStructureInstruction(

            ConsultationCategory category

    ) {

        if (
                category
                        ==
                        ConsultationCategory.PERSONALITY
        ) {

            return """
                    Writer가 공식기본성향검토계약을
                    실제로 지켰는지 확인하세요.

                    반드시 설명할 중심판단이 빠졌다면
                    REVISE합니다.

                    중심판단보다 보조판단을
                    더 강한 성향처럼 썼다면 수정합니다.

                    강한표현이 허용되지 않은 판단을
                    '항상', '분명히', '확실히' 같은 방식으로
                    과도하게 단정했다면 수정합니다.

                    조건설명필수인 성향묶음에서
                    충돌만 말하고 조절 작용을 없앴거나,
                    조절만 말하고 긴장을 지웠다면
                    REVISE합니다.

                    내부참고가 독립적인 성격문장으로
                    사용됐다면 제거합니다.

                    전체배경이 그 자체로
                    고집, 소심함, 사람을 못 믿음 같은
                    심리판정으로 바뀌었다면 수정합니다.

                    기존보조검증은 Contract를 확인하는 데만 사용합니다.
                    Contract에 없는 성향을
                    기존보조검증만으로 추가하지 마세요.

                    Writer가 복합 구조를
                    서로 관계없는 성격 형용사 목록으로
                    잘게 쪼갰다면,
                    같은 Grounding 안에서 반응 과정이 보이도록
                    다시 연결하세요.
                    """;
        }

        if (
                timelineRangeCategory(
                        category
                )
        ) {

            return """
                    연도흐름의 각 연도에서
                    Writer가 연도종합판정을 실제 상위 계약으로
                    사용했는지 확인하세요.

                    최종연도판정과 전체흐름이 혼재 방향인데
                    raw 연도세부근거의 부담 판단이 많다는 이유로
                    전체를 부담 방향으로 바꿨다면 REVISE합니다.

                    반대로 전체흐름이 부담 방향인데
                    일부 지지 근거만 보고
                    전체를 수월한 시기로 바꿨다면 REVISE합니다.

                    주된작동축은
                    해당 연도에서 상대적으로 작동이 분명한 영역이지
                    가장 좋은 영역 또는 가장 나쁜 영역이 아닙니다.

                    주된부담축이 존재해도
                    그것만으로 전체 연도 방향을 변경하지 마세요.

                    영역간구조는
                    여러 생활영역의 공존 방식으로만 사용합니다.

                    전환점은
                    전후 연도 deterministic 구조 변화로만 설명합니다.

                    확정하지않은판단에 있는 항목을
                    raw Evidence를 보고 Writer가 새로 만들었다면
                    제거하세요.

                    각 연도는 해당 연도의 근거만 사용해야 합니다.

                    연도별 차이가 잘 살아 있다면
                    여러 해를 하나의 공통 성향이나
                    하나의 결과 예측으로 다시 줄이지 마세요.
                    """;
        }

        return """
                중요도가 높은 판단이 중심에 살아 있는지 확인하고,
                현재 장의 범위를 벗어난 독립 상담으로
                확장하지 않았는지 확인하세요.
                """;
    }


    private String categoryFocusInstruction(

            ConsultationCategory category

    ) {

        return switch (
                category
                ) {

            case PERSONALITY ->
                    """
                    재물, 직업, 관계, 학습을 각각 별도 상담처럼 나열하지 않습니다.

                    공식기본성향계약에서 직접 설명이 허용된 구조를 중심으로,
                    여러 상황을 관통하는 판단 방식과 선택 방식,
                    압박이 들어왔을 때 무엇이 달라지는지,
                    자기 표현과 외부 기준이 부딪힐 때 어떻게 조절되는지,
                    현실적인 결과와 책임을 어떤 순서의 기능관계로 받아들이는지를
                    일반적인 행동 언어로 설명합니다.

                    단,
                    기능의 구조적 연결 방향을
                    실제 심리적 시간순서라고 단정하지 않습니다.
                    """;

            case PAST_LUCK ->
                    """
                    특정 과거 사건을 맞히지 않습니다.

                    연도흐름의 각 명리연도에서
                    연도종합판정을 공식 결론으로 먼저 사용합니다.

                    최종연도판정과 전체흐름을
                    raw 연도세부근거로 다시 계산하지 않습니다.

                    주된작동축,
                    주된부담축,
                    영역간구조,
                    전환점이 실제로 존재할 때만
                    각각의 제한된 의미를 설명합니다.

                    여러 연도를 하나의 관계 성향이나
                    공통 성향으로 합치지 않습니다.

                    실제 사건을 만들지 않고
                    당시 계산된 생활 흐름과
                    선택·부담·활동 방향만 설명합니다.
                    """;

            case SOCIAL_RELATIONSHIP ->
                    """
                    사람을 받아들이고 가까워지는 방식,
                    기대와 책임이 생긴 뒤의 반응,
                    부담이 반복될 때 거리와 태도가
                    달라지는 방식에 집중합니다.
                    """;

            case LOVE_STYLE ->
                    """
                    마음이 생겼을 때 표현 방식,
                    상대의 반응을 확인하는 방식,
                    가까워질수록 태도와 기대가
                    어떻게 달라지는지 설명합니다.
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
                    성과를 만들기 위해 판단하고
                    움직이는 방식에 집중합니다.
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

                    연도흐름의 각 미래 명리연도에서
                    연도종합판정을 공식 결론으로 먼저 사용합니다.

                    최종연도판정과 전체흐름을
                    raw 연도세부근거로 다시 계산하지 않습니다.

                    주된작동축은
                    미래에 반드시 사건이 일어날 분야가 아닙니다.

                    주된부담축은
                    실패가 예정된 분야가 아닙니다.

                    영역간구조는
                    여러 생활영역의 방향 공존 구조입니다.

                    전환점은
                    전후 연도 계산 구조의 변화이며
                    특정 사건의 발생 시점이 아닙니다.

                    각 연도에서 계산된 판단,
                    선택, 책임, 관계 조율,
                    자원 운용의 흐름을 설명합니다.
                    """;
        };
    }


    private String behavioralTranslationInstruction(

            ConsultationCategory category

    ) {

        return switch (
                category
                ) {

            case PERSONALITY ->
                    "공식 기본성향 계약이 허용한 범위에서, 성격 형용사보다 어떤 조건에서 무엇을 중요하게 판단하고 표현·책임·현실 판단·내부 정리가 어떻게 함께 작동하는지 설명합니다.";

            case PAST_LUCK ->
                    "실제 사건을 만들지 않고, 각 과거 연도의 공식 종합판정을 먼저 유지한 뒤 그 안에서 계산된 생활주제와 선택 속도, 부담 체감, 행동 방식의 차이를 설명합니다.";

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
                    "미래 사건을 만들지 않고, 각 연도의 공식 종합판정을 유지한 상태에서 시기별로 달라지는 행동, 판단, 부담과 활동 흐름을 설명합니다.";
        };
    }


    private String categorySafetyInstruction(

            ConsultationCategory category

    ) {

        return switch (
                category
                ) {

            case PERSONALITY ->
                    """
                    다른 열 개 상담 장을 하나씩 미리 설명하지 마세요.

                    공식기본성향계약에 없는 성향을
                    기존보조검증이나 일반적인 사주 지식으로 추가하지 마세요.

                    내부참고를 독립적인 성격 단정으로 사용하지 마세요.

                    전체배경을 직접 심리판정으로 바꾸지 마세요.

                    강한표현허용이 아닌 판단을
                    강한 단정으로 확대하지 마세요.
                    """;

            case PAST_LUCK ->
                    """
                    특정 과거 사건이 실제로 있었다고 단정하지 마세요.

                    raw 연도세부근거를 다시 합산하여
                    최종연도판정이나 전체흐름을 바꾸지 마세요.

                    확정하지않은판단을
                    새로 만들어 채우지 마세요.

                    여러 연도를 하나의 대인관계 이야기나
                    하나의 공통 성향으로 묶지 마세요.
                    """;

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
                    """
                    미래 특정 사건이나 결과를 확정하지 마세요.

                    raw 연도세부근거를 다시 합산하여
                    최종연도판정이나 전체흐름을 바꾸지 마세요.

                    주된작동축을
                    반드시 사건이 생길 분야로 바꾸지 마세요.

                    주된부담축을
                    실패가 예정된 분야로 바꾸지 마세요.

                    전환점을
                    이직, 결혼, 이별, 손실, 사고 등의
                    발생 시점으로 바꾸지 마세요.

                    확정하지않은판단을
                    새로 만들어 채우지 마세요.
                    """;
        };
    }


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


    private boolean timelineRangeCategory(

            ConsultationCategory category

    ) {

        return category
                ==
                ConsultationCategory.PAST_LUCK

                ||

                category
                        ==
                        ConsultationCategory.FUTURE_LUCK;
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


    /*
     * =========================================================
     * Category Parallel Result
     * =========================================================
     */
    private record CategoryGenerationResult(

            ConsultationCategory category,

            String storyText,

            List<String> emphasis

    ) {

        private CategoryGenerationResult {

            Objects.requireNonNull(
                    category,
                    "CategoryGenerationResult Category는 필수입니다."
            );

            storyText =
                    requireText(
                            storyText,
                            "CategoryGenerationResult storyText는 필수입니다."
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