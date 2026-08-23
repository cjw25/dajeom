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
            "DAJEOM_HUMAN_PATTERN_REVIEW_34_MEMO_ONLY";

    private static final int STORY_TARGET_MIN_LENGTH =
            450;

    private static final int STORY_TARGET_MAX_LENGTH =
            1100;

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
            PERSONALITY_WRITER_SUPPORTING_EVIDENCE_LIMIT =
            3;

    private static final int
            PERSONALITY_REVIEWER_SUPPORTING_EVIDENCE_LIMIT =
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
                    + "일시적인 오류가 발생했습니다. "
                    + "계산되지 않은 내용을 임의로 추정해서 채우지 않았습니다.";

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
                            + GPT_4O_MINI_MAX_OUTPUT_TOKENS
                            + " 이하이어야 합니다. current="
                            + maxOutputTokens
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

        EnumMap<ConsultationCategory, List<String>>
                emphasisByCategory =
                new EnumMap<>(
                        ConsultationCategory.class
                );

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
                        != category
        ) {

            throw new IllegalArgumentException(
                    "Prompt Section Category가 일치하지 않습니다. expected="
                            + category
                            + ", actual="
                            + section.category()
            );
        }

        if (
                section.unavailable()
        ) {

            throw new IllegalArgumentException(
                    "unavailable Section은 Story Generation 대상이 아닙니다: "
                            + category
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

        diagnoseTimelineYearDeterministicContracts(
                generationId,
                section
        );

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
                sanitizeDraftResult(
                        category,
                        requestWriterStory(
                                generationId,
                                writerGroundingJson,
                                category,
                                categoryStartedAt,
                                ""
                        )
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
                            modelSafeValidationHint(
                                    writerValidation
                            ),
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

        String reviewedStoryText =
                sanitizeGeneratedStoryTextForUser(
                        category,
                        reviewResult.finalStoryText()
                );

        StoryValidationResult reviewedValidation =
                validatePlainStoryHardSafety(
                        category,
                        reviewedStoryText
                );

        if (
                reviewedValidation.valid()
        ) {

            return finalizeStory(
                    new DraftResult(
                            reviewedStoryText,
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

        String repairInstruction =
                modelSafeValidationHint(
                        reviewedValidation.valid()
                                ? writerValidation
                                : reviewedValidation
                );

        DraftResult repaired =
                sanitizeDraftResult(
                        category,
                        requestWriterStory(
                                generationId,
                                writerGroundingJson,
                                category,
                                categoryStartedAt,
                                """
                                직전 작성문과 검토문 모두
                                최종 출력 안전조건을 통과하지 못했습니다.
        
                                수정 조건:
                                %s
        
                                같은 공식 deterministic 판단메모만 사용해서 다시 작성하세요.
        
                                새로운 과거 사건,
                                새로운 미래 사건,
                                새로운 사람,
                                새로운 경험,
                                새로운 해결책을 만들지 마세요.
        
                                독립 판단 사이에
                                새로운 관계,
                                인과,
                                시간순서,
                                단계,
                                강화·약화 조건도 만들지 마세요.
        
                                상담 내용의 구체성을 불필요하게 줄이지 말고
                                위 출력 문제만 제거하세요.
                                """
                                        .formatted(
                                                repairInstruction
                                        )
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
                        + category
                        + ", reason="
                        + repairedValidation.message()
        );
    }

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

            ConsultationGroundingTimelineYearDeterministicContract contract =
                    Objects.requireNonNull(
                            year.deterministicContract(),
                            "Timeline Year Deterministic Contract는 필수입니다. sajuYear="
                                    + year.sajuYear()
                    );

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

            ConsultationGroundingTimelineYearDeterministicContract contract =
                    Objects.requireNonNull(
                            year.deterministicContract(),
                            section.category()
                                    + " Story Generation에서 Timeline Year Deterministic Contract가 필요합니다. sajuYear="
                                    + year.sajuYear()
                    );

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
                            0,
                            false
                    )
            );

            return result;
        }

        if (
                section.hasNatalDeterministicConclusions()
        ) {

            result.put(
                    "공식원국판단",
                    createDeterministicConclusionsPayload(
                            section.natalDeterministicConclusions(),
                            false
                    )
            );
        }
        else {

            /*
             * Category Deterministic Conclusion이 없는 예외 상황에서만
             * 세부 Grounding을 제한적으로 사용한다.
             *
             * 정상 11개 Category 경로에서는
             * 공식 deterministic 판단이 Meaning Contract다.
             */
            result.put(
                    "원국세부근거",
                    createCompactEvidencePayload(
                            section.natalEvidences(),
                            section.category(),
                            WRITER_NATAL_EVIDENCE_LIMIT,
                            false
                    )
            );
        }

        result.put(
                "현재흐름",
                createScenarioPayload(
                        section.timelineScenarios(),
                        section.category(),
                        0,
                        false
                )
        );

        if (
                categoryAllowsYear(
                        section.category()
                )
        ) {

            result.put(
                    "연도흐름",
                    createYearPayload(
                            section.timelineYears(),
                            section.category(),
                            0,
                            false
                    )
            );
        }

        return result;
    }

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
                            0,
                            false
                    )
            );

            return result;
        }

        if (
                section.hasNatalDeterministicConclusions()
        ) {

            result.put(
                    "공식원국검토판단",
                    createDeterministicConclusionsPayload(
                            section.natalDeterministicConclusions(),
                            false
                    )
            );
        }
        else {

            result.put(
                    "원국검토세부근거",
                    createCompactEvidencePayload(
                            section.natalEvidences(),
                            section.category(),
                            REVIEWER_NATAL_EVIDENCE_LIMIT,
                            false
                    )
            );
        }

        result.put(
                "현재검토근거",
                createScenarioPayload(
                        section.timelineScenarios(),
                        section.category(),
                        0,
                        false
                )
        );

        if (
                categoryAllowsYear(
                        section.category()
                )
        ) {

            result.put(
                    "연도검토근거",
                    createYearPayload(
                            section.timelineYears(),
                            section.category(),
                            0,
                            false
                    )
            );
        }

        return result;
    }

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
                "공식기본성향계약",
                createPersonalityContractPayload(
                        contract,
                        true
                )
        );

        result.put(
                "보조확인근거",
                Map.of(
                        "사용원칙",
                        "공식기본성향계약을 보조해서 확인할 때만 사용하며, 계약에 없는 새 성향을 만들거나 표현 강도를 올리는 데 사용하지 않는다.",
                        "판단",
                        createCompactEvidencePayload(
                                section.natalEvidences(),
                                ConsultationCategory.PERSONALITY,
                                PERSONALITY_WRITER_SUPPORTING_EVIDENCE_LIMIT,
                                false
                        )
                )
        );

        return result;
    }

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
                "공식기본성향검토계약",
                createPersonalityContractPayload(
                        contract,
                        false
                )
        );

        result.put(
                "보조확인근거",
                Map.of(
                        "사용원칙",
                        "공식기본성향검토계약의 의미를 뒤집지 않는 범위의 보조 확인 자료다.",
                        "판단",
                        createCompactEvidencePayload(
                                section.natalEvidences(),
                                ConsultationCategory.PERSONALITY,
                                PERSONALITY_REVIEWER_SUPPORTING_EVIDENCE_LIMIT,
                                false
                        )
                )
        );

        return result;
    }

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
                        "조건 설명이 필요한 판단은 Contract가 실제로 제공한 제한이나 반대 작용만 보존한다.",
                        "서로 다른 독립 Selection을 새 관계, 인과, 순서 또는 하나의 심리 과정으로 조합하지 않는다.",
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
                    "이 묶음에 실제 제공된 판단의 제한, 반대 작용 또는 조절 의미만 보존하고 제공되지 않은 조건, 인과, 순서 또는 관계를 새로 만들지 않는다."
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
                "독립적인 성격 단정으로 바꾸지 않고 다른 직접판단의 배경으로만 사용한다."
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
                    "Java에서 확정된 기능 상호작용 판단";

            case SYNTHESIS ->
                    "Java에서 확정된 복합 판단";

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
                    "정보와 경험을 받아들이고 안에서 정리하고 이해하는 기능";

            case PRESSURE_REGULATED_BY_EXPRESSION ->
                    "표현과 실행 기능이 압박이나 강한 요구를 조절하는 직접 상호작용 구조";

            case EXPRESSION_STANDARD_CONFLICT ->
                    "자기 표현 기능과 외부 기준 또는 요구가 충돌할 수 있는 직접 상호작용 구조";

            case EXPRESSION_REGULATED_BY_PROCESSING ->
                    "내부 이해와 정리 기능이 표현 기능을 조절하는 직접 상호작용 구조";

            case OUTPUT_RESTRAINED_BY_PROCESSING ->
                    "내부 검토 기능이 표현이나 결과물의 작동 범위를 제한할 수 있는 직접 상호작용 구조";

            case REALITY_GENERATES_RESPONSIBILITY ->
                    "현실 결과와 자원 판단 기능이 책임과 역할 수용 기능을 뒷받침하는 직접 상호작용 구조";

            case RESPONSIBILITY_GENERATES_PROCESSING ->
                    "책임과 외부 기준 기능이 내부 이해와 정리 기능을 뒷받침하는 직접 상호작용 구조";

            case PRESSURE_GENERATES_PROCESSING ->
                    "압박과 강한 요구가 내부 판단과 정리 기능을 활성화하는 직접 상호작용 구조";

            case REALITY_CONTROLS_PROCESSING ->
                    "현실 결과와 실용성 판단 기능이 내부 검토의 범위나 방향을 제한하는 직접 상호작용 구조";

            case SELF_ASSERTION_COMPETES_FOR_RESOURCE ->
                    "자기 몫과 주도권 기능이 현실적인 자원 배분이나 교환 기능과 경쟁하는 직접 상호작용 구조";

            case RESOURCE_LOADS_CORE_SELF ->
                    "현실적인 성과와 자원 요구가 자기 수용 여력에 부담으로 작용할 수 있는 구조";

            case RESPONSIBILITY_INTERNAL_MIXTURE ->
                    "책임과 외부 기준을 받아들이는 기능 안에 서로 다른 압력과 역할 기준이 함께 존재하는 구조";

            case INTERPERSONAL_RELATION_CONTEXT ->
                    "사람 사이의 연결과 긴장에 관한 대인 관계 배경 구조";
        };
    }

    private String personalitySynthesisMeaning(

            PersonalityFindingSynthesisType synthesisType

    ) {

        return switch (
                synthesisType
                ) {

            case EXPRESSION_STANDARD_TENSION_WITH_PROCESSING_REGULATION ->
                    "자기 표현과 외부 기준의 충돌 구조와 내부 검토의 표현 조절 구조가 하나의 복합 판단으로 계산된 상태";

            case REALITY_RESPONSIBILITY_PROCESSING_CHAIN ->
                    "현실 결과와 자원 판단, 책임 기능, 내부 검토 기능 사이의 구조가 하나의 복합 판단으로 계산된 상태";

            case PRESSURE_PROCESSING_EXPRESSION_CHAIN ->
                    "압박, 내부 판단과 정리, 표현 조절 구조가 하나의 복합 판단으로 계산된 상태";

            case RESPONSIBILITY_MIXTURE_WITH_PROCESSING_PATH ->
                    "책임과 외부 기준의 혼재 구조와 내부 이해·정리 기능이 하나의 복합 판단으로 계산된 상태";

            case REALITY_AND_RESPONSIBILITY_CONVERGE_ON_PROCESSING ->
                    "현실 결과 판단과 책임 기능이 내부 검토와 판단 기능에 관련되는 구조가 하나의 복합 판단으로 계산된 상태";

            case RESOURCE_LOAD_WITH_SELF_ASSERTION_COMPETITION ->
                    "현실적인 자원·성과 부담 구조와 자기 몫·주도권의 자원 경쟁 구조가 하나의 복합 판단으로 계산된 상태";
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
                    "압박과 책임이 함께 있는 상황";

            case RULES_AND_EXPRESSION ->
                    "외부 기준과 자기 표현이 함께 있는 상황";

            case PROCESSING_AND_EXPRESSION ->
                    "내부 정리와 표현이 함께 관련된 상황";

            case REALITY_AND_RESPONSIBILITY ->
                    "현실적인 결과와 책임이 함께 관련된 상황";

            case REALITY_AND_PROCESSING ->
                    "실용성 판단과 내부 검토가 함께 관련된 상황";

            case COMPETITION_AND_RESOURCE ->
                    "자기 몫과 자원 배분이 함께 관련된 상황";

            case RESOURCE_CAPACITY ->
                    "현실적인 요구와 자기 수용 여력이 함께 관련된 상황";

            case MIXED_RESPONSIBILITY ->
                    "서로 다른 역할 기준과 책임 압력이 함께 있는 상황";
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
                    "직접 설명 가능하나 Contract가 제공한 제한을 함께 설명";

            case STRONG_DIRECT ->
                    "분명한 직접 표현 가능";

            case STRONG_DIRECT_WITH_QUALIFICATION ->
                    "분명하게 표현하되 Contract가 제공한 제한도 함께 설명";
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
                            != ConsultationCategory.PERSONALITY
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
                    userFacingGroundingText(
                            evidence.summary()
                    )
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
                                .map(
                                        this::userFacingGroundingText
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
                                            + "\u001F"
                                            + evidence.status().name()
                                            + "\u001F"
                                            + evidence.summary();

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
                        <= limit
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
                            >= limit
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
                            >= limit
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
                            >= limit
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
                            >= limit
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
                            >= limit
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

            if (
                    categoryAllowsYear(
                            category
                    )
            ) {

                item.put(
                        "연도",
                        scenario.sajuYear()
                );

                item.put(
                        "월인덱스",
                        scenario.monthIndex()
                );
            }
            else {

                item.put(
                        "시점",
                        "현재 분석 시점"
                );
            }

            if (
                    scenario.timelineUncertaintyExists()
            ) {

                item.put(
                        "시점안내",
                        "경계가 불확실하므로 하나의 실제 사건으로 확정하지 않는다."
                );
            }

            if (
                    scenario.hasDeterministicConclusions()
            ) {

                item.put(
                        "공식현재판단",
                        createDeterministicConclusionsPayload(
                                scenario.deterministicConclusions(),
                                includeReasons
                        )
                );

                item.put(
                        "판단사용원칙",
                        "각 판단메모는 독립된 확정 계산 결과다. 서로 다른 판단메모를 새 관계, 인과, 순서, 강화, 약화 또는 하나의 종합 상태로 합치지 않는다."
                );
            }

            if (
                    evidenceLimit > 0
            ) {

                item.put(
                        "현재세부근거",
                        createCompactEvidencePayload(
                                scenario.evidences(),
                                category,
                                evidenceLimit,
                                includeReasons
                        )
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

                ConsultationGroundingTimelineYearDeterministicContract contract =
                        Objects.requireNonNull(
                                year.deterministicContract(),
                                category
                                        + " Year Payload에서 Deterministic Contract가 필요합니다. sajuYear="
                                        + year.sajuYear()
                        );

                item.put(
                        "연도종합판정",
                        createTimelineYearDeterministicContractPayload(
                                contract,
                                includeReasons
                        )
                );

                if (
                        evidenceLimit > 0
                ) {

                    item.put(
                            "연도세부근거",
                            createCompactEvidencePayload(
                                    year.evidences(),
                                    category,
                                    evidenceLimit,
                                    includeReasons
                            )
                    );
                }

                result.add(
                        item
                );

                continue;
            }

            if (
                    evidenceLimit > 0
            ) {

                item.put(
                        "판단",
                        createCompactEvidencePayload(
                                year.evidences(),
                                category,
                                evidenceLimit,
                                includeReasons
                        )
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
                        "최종연도판정과 전체흐름의 판단메모 범위만 설명한다.",
                        "주된작동축은 사건이 생길 분야나 가장 좋은 분야를 뜻하지 않는다.",
                        "주된부담축은 실패가 예정된 분야를 뜻하지 않는다.",
                        "영역간구조가 있으면 계산된 영역간 구조의 판단메모 자체만 설명하고 다른 관계를 추가하지 않는다.",
                        "전환점은 계산에서 확정된 전후 구조 차이만 뜻하며 구체적인 사건 발생을 뜻하지 않는다.",
                        "전환점이 없는 연도 사이의 지속, 강화, 약화 또는 인과를 새로 만들지 않는다."
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
                    "확정하지 않은 판단은 새로 추정하거나 채우지 않는다."
            );
        }

        return result;
    }

    private List<Map<String, Object>>
    createDeterministicConclusionsPayload(

            List<ConsultationGroundingDeterministicConclusion>
                    conclusions,

            boolean includeReasons

    ) {

        if (
                conclusions == null
                        ||
                        conclusions.isEmpty()
        ) {

            return List.of();
        }

        List<Map<String, Object>> result =
                new ArrayList<>(
                        conclusions.size()
                );

        for (
                ConsultationGroundingDeterministicConclusion conclusion
                : conclusions
        ) {

            result.add(
                    createDeterministicConclusionPayload(
                            Objects.requireNonNull(
                                    conclusion,
                                    "Deterministic Conclusion은 null일 수 없습니다."
                            ),
                            includeReasons
                    )
            );
        }

        return List.copyOf(
                result
        );
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
                "판단메모",
                userFacingGroundingText(
                        conclusion.summary()
                )
        );

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
                            .map(
                                    this::userFacingGroundingText
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
                    "가장 먼저 볼 세부근거";

            case 2 ->
                    "우선 확인할 세부근거";

            case 3,
                 4 ->
                    "보완 세부근거";

            default ->
                    "필요할 때만 사용할 세부근거";
        };
    }

    private String prominenceSignal(

            ConsultationGroundingProminence prominence

    ) {

        if (
                prominence == null
        ) {

            return "직접 계산 provenance가 존재한다.";
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

            return "서로 다른 직접 계산 provenance가 존재하며 반복 계산 흔적도 있다. 반복 자체를 의미 강화 근거로 사용하지 않는다.";
        }

        if (
                diverse
        ) {

            return "서로 다른 종류의 직접 계산 provenance가 존재한다.";
        }

        if (
                multiple
                        &&
                        repeated
        ) {

            return "여러 직접 계산 provenance가 존재하며 반복 계산 흔적도 있다. 반복 자체를 의미 강화 근거로 사용하지 않는다.";
        }

        if (
                multiple
        ) {

            return "여러 직접 계산 provenance가 존재한다.";
        }

        if (
                repeated
        ) {

            return "반복 계산 흔적이 존재한다. 반복 자체를 의미 강화 근거로 사용하지 않는다.";
        }

        return "직접 계산 provenance가 존재한다.";
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
                                                + apiKey
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
                                    + category
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
                                >= MAX_RATE_LIMIT_RETRIES
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
                            == null
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
                    ? 0L
                    : (long) Math.ceil(
                    seconds
                            * 1000.0
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
                    ? 0L
                    : (long) Math.ceil(
                    seconds
                            * 1000.0
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

                모든 계산과 판단은 이미 끝났습니다.
                새로운 계산이나 새로운 뜻을 만들지 마세요.

                현재 상담 장:
                %s

                상담 장 이름은 서술 범위를 정하는 표식일 뿐
                판단의 뜻을 만드는 근거가 아닙니다.

                입력 자료에서 실제 의미 근거는
                '판단메모'입니다.

                판단메모 하나를 하나의 완성된 계산 결과로 보고
                그 문장에 들어 있는 의미만 자연스러운 한국어로 바꾸세요.

                판단메모에 없는 행동, 심리, 사건, 결과, 해결책,
                관계 방식, 조건, 전후 변화는 추가하지 마세요.

                서로 다른 판단메모가 함께 있다는 이유만으로
                원인과 결과, 전후 단계, 시간순서, 강화, 약화, 상쇄,
                하나의 종합 상태를 만들지 마세요.

                특히 다음과 같은 확대 번역은 금지합니다.

                - 관계망에 참여한다는 판단을 친밀감, 유대감, 원활한 소통, 관계 유지, 새로운 사람과의 연결로 바꾸지 마세요.
                - 마찰이 있다는 판단을 감정 문제, 관계 악화, 갈등 발생, 소통 필요, 서로 이해해야 한다는 조언으로 바꾸지 마세요.
                - 지원 방향이라는 뜻을 기회, 성공, 발전, 회복, 도움이 생긴다는 결과로 바꾸지 마세요.
                - 부담 방향이라는 뜻을 실패, 손실, 갈등, 피로, 문제가 생긴다는 사건으로 바꾸지 마세요.
                - 혼합 방향이라는 뜻을 긍정과 부정이 번갈아 나타난다거나 신중하게 대응해야 한다는 생활 시나리오로 바꾸지 마세요.
                - 전환점이라는 판단을 다음 해까지 영향이 이어진다거나 새로운 기회가 생긴다는 의미로 바꾸지 마세요.

                단, 해당 의미가 판단메모에 직접 적혀 있는 경우에는
                그 적힌 범위 안에서만 표현할 수 있습니다.

                명리 전문용어, 내부 계산용 표현, 영어 알파벳, 밑줄,
                내부 필드명, 근거 개수, 계산 횟수는 본문에 쓰지 마세요.

                별표, 마크다운, 웹 태그도 본문에 넣지 마세요.

                2~4개의 자연스러운 문단을 목표로 하되
                한 문단에서는 판단메모 하나를 중심으로 설명하세요.

                450~1100자 정도면 충분합니다.
                근거가 적으면 더 짧아도 됩니다.
                분량을 채우려고 일반론을 추가하지 마세요.

                %s

                화면에서 진하게 보여 줄 부분이 필요하면
                실제 본문에 존재하는 문장이나 짧은 구절만 별도 강조 목록에 넣으세요.
                강조할 부분이 없으면 빈 목록으로 두세요.
                """
                .formatted(
                        categoryDisplayName(
                                category
                        ),
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
                        ? ""
                        : """

                        이전 출력에서 반드시 고칠 점:

                        %s
                        """
                        .formatted(
                                correction
                        );

        return """
                상담 주제:
                %s

                공식 판단 자료:
                %s

                %s

                자료의 판단메모만 의미 근거로 사용하세요.
                판단메모에 없는 뜻은 일반적인 사주 지식으로 보충하지 마세요.

                서로 다른 판단을 자연스럽게 이어 쓰기 위해
                관계, 인과, 순서, 강화, 약화, 상쇄 또는 하나의 이야기를 만들지 마세요.

                본문에는 제목이나 작성 과정의 라벨을 넣지 마세요.
                강조 목록에는 본문에 실제 존재하는 구절만 넣으세요.
                """
                .formatted(
                        categoryDisplayName(
                                category
                        ),
                        groundingJson,
                        correctionSection
                );
    }

    private String createReviewerInstructions(

            ConsultationCategory category

    ) {

        return """
                당신은 새로운 사주 계산을 하지 않는
                한국어 상담문 검토자입니다.

                현재 상담 장:
                %s

                검토 기준은 오직 입력 자료의 판단메모입니다.
                상담 장 이름은 의미 근거가 아닙니다.

                초안의 각 중요한 문장이
                판단메모에 직접 들어 있는 의미를 자연스럽게 바꾼 것인지 확인하세요.

                판단메모에 없는 행동, 심리, 사건, 결과, 해결책,
                관계 방식, 조건, 전후 변화가 추가되었다면 제거하세요.

                서로 다른 판단메모 사이에 새 관계, 인과, 순서,
                강화, 약화, 상쇄, 하나의 종합 상태가 생겼다면 제거하세요.

                특히 다음 확대를 허용하지 마세요.

                - 관계망 참여를 친밀감, 유대감, 소통, 관계 유지, 새로운 사람과의 연결로 확대
                - 마찰을 감정 문제, 관계 악화, 갈등 발생, 소통 필요라는 조언으로 확대
                - 지원 방향을 기회, 성공, 발전, 회복, 도움이라는 결과로 확대
                - 부담 방향을 실패, 손실, 갈등, 피로라는 사건으로 확대
                - 혼합 방향을 긍정과 부정의 생활 시나리오나 조언으로 확대
                - 전환점을 다음 해 영향, 새로운 기회, 특정 사건으로 확대

                해당 의미가 판단메모에 직접 적혀 있을 때만 유지할 수 있습니다.

                명리 전문용어, 내부 계산용 표현, 영어 알파벳, 밑줄,
                허용되지 않은 연도 표현, 마크다운, 웹 태그도 최종 본문에서 제거하세요.

                초안이 판단메모 범위 안에서 이미 정확하면 그대로 유지하고,
                의미가 넓어졌을 때만 수정하세요.

                수정할 때도 새 의미를 추가하지 말고
                판단메모에 더 가까운 문장으로 되돌리세요.

                %s
                """
                .formatted(
                        categoryDisplayName(
                                category
                        ),
                        categorySafetyInstruction(
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
                        ? "명백한 출력 형식 오류는 없습니다."
                        : """
                        출력 검사에서 수정이 필요한 형식 문제가 확인되었습니다.

                        %s

                        최종 본문에서는 반드시 해결하세요.
                        """
                        .formatted(
                                hardValidationIssue
                        );

        return """
                상담 주제:
                %s

                공식 판단 자료:
                %s

                작성된 초안:
                %s

                추가 확인:
                %s

                초안을 처음부터 새 이야기로 만들지 마세요.
                판단메모 범위를 벗어난 문장만 고치세요.
                강조 목록에는 최종 본문에 실제 존재하는 문자열만 넣으세요.
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
                "dajeom_writer_story_v31"
        );

        format.put(
                "description",
                categoryDisplayName(
                        category
                )
                        + " 상담문과 강조 후보"
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
                "dajeom_story_review_v31"
        );

        format.put(
                "description",
                categoryDisplayName(
                        category
                )
                        + " 상담문 의미 검토"
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
                            >= MAX_EMPHASIS_COUNT
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
                            + firstNonBlank(
                            status,
                            "UNKNOWN"
                    )
                            + ", detail="
                            + detail
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

    private DraftResult sanitizeDraftResult(

            ConsultationCategory category,

            DraftResult source

    ) {

        Objects.requireNonNull(
                source,
                "Sanitize 대상 DraftResult가 필요합니다."
        );

        return new DraftResult(
                sanitizeGeneratedStoryTextForUser(
                        category,
                        source.storyText()
                ),
                source.emphasis()
        );
    }

    private String sanitizeGeneratedStoryTextForUser(

            ConsultationCategory category,

            String storyText

    ) {

        Objects.requireNonNull(
                category,
                "Sanitize 대상 ConsultationCategory가 필요합니다."
        );

        String result =
                normalizeGeneratedStoryText(
                        storyText
                );

        result = result
                .replace(
                        "**",
                        ""
                )
                .replace(
                        "__",
                        ""
                );

        result = result.replaceAll(
                "<[^>]+>",
                ""
        );

        result = result.replace(
                "SUPPORTIVE",
                "지원 방향"
        );
        result = result.replace(
                "PRESSURE",
                "부담 방향"
        );
        result = result.replace(
                "MIXED",
                "혼합 방향"
        );
        result = result.replace(
                "CONTEXTUAL",
                "문맥 판단"
        );

        result = result.replace(
                "HEALTH_TENDENCY",
                "건강 경향"
        );
        result = result.replace(
                "HEALTH_LIFESTYLE",
                "건강·생활 영역"
        );
        result = result.replace(
                "SOCIAL_RELATIONSHIP",
                "대인관계"
        );
        result = result.replace(
                "LOVE_STYLE",
                "연애 성향"
        );
        result = result.replace(
                "PARTNER_LUCK",
                "배우자운"
        );
        result = result.replace(
                "CURRENT_YEAR_LUCK",
                "올해의 흐름"
        );
        result = result.replace(
                "FUTURE_LUCK",
                "앞으로의 흐름"
        );
        result = result.replace(
                "PAST_LUCK",
                "지나온 흐름"
        );
        result = result.replace(
                "LEARNING_GROWTH",
                "학습·성장 영역"
        );
        result = result.replace(
                "RELATIONSHIP",
                "관계 영역"
        );
        result = result.replace(
                "WEALTH",
                "재물 영역"
        );
        result = result.replace(
                "CAREER",
                "직업 영역"
        );

        result = result.replaceAll(
                "(?i)Context",
                "문맥"
        );
        result = result.replaceAll(
                "(?i)Annual",
                "연도"
        );
        result = result.replaceAll(
                "(?i)Decade",
                "장기 시기"
        );
        result = result.replaceAll(
                "(?i)Monthly",
                "월 시기"
        );
        result = result.replaceAll(
                "(?i)Functional",
                "기능 판단"
        );

        result = replaceSajuTermsForUser(
                result
        );

        if (
                !categoryAllowsYear(
                        category
                )
        ) {

            result = YEAR_PATTERN
                    .matcher(
                            result
                    )
                    .replaceAll(
                            "현재 시점"
                    );
        }

        result = LATIN_TOKEN_PATTERN
                .matcher(
                        result
                )
                .replaceAll(
                        ""
                );

        result = result
                .replaceAll(
                        "[ \t]+",
                        " "
                )
                .replaceAll(
                        " ?\n ?",
                        "\n"
                )
                .replaceAll(
                        "\n{3,}",
                        "\n\n"
                )
                .trim();

        return result;
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
                                < 5
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
                            + String.join(
                            ", ",
                            latinTokens
                    )
                            + "'"
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
                                + term
                                + "'"
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
                                + phrase
                                + "'"
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
                        == ConsultationCategory.LEGAL_RISK
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
                                    + phrase
                                    + "'"
                    );
                }
            }
        }

        if (
                category
                        == ConsultationCategory.HEALTH_TENDENCY
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
                                    + phrase
                                    + "'"
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
                                + Pattern.quote(
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
                        == ConsultationCategory.PERSONALITY
        ) {

            return """
                    공식기본성향계약의 성향묶음을 기준으로 작성합니다.

                    '반드시 설명'으로 표시된 묶음이 있으면
                    그 묶음의 중심판단을 우선 설명하세요.

                    중심판단과 보조판단은
                    Contract가 지정한 역할을 그대로 유지하세요.

                    보조판단을 중심판단의 원인,
                    결과,
                    전후 단계,
                    진행 과정으로 새로 연결하지 마세요.

                    강한표현허용이 참인 판단만
                    비교적 분명한 어조를 사용할 수 있습니다.

                    조건설명필수가 참이면
                    Contract가 실제로 제공한 제한이나 반대 작용만
                    함께 보존하세요.

                    제공되지 않은 조건이나 인과는 만들지 마세요.

                    내부참고는 독립 문장으로 만들지 않습니다.

                    전체배경은 독립적인 성격 판단으로 만들지 말고
                    배경 Context로만 사용합니다.

                    CROSS_PATTERN 또는 SYNTHESIS Selection도
                    하나의 완성된 Selection으로 취급합니다.

                    다른 독립 Selection과 다시 조합하여
                    새로운 관계나 심리적 순서를 만들지 마세요.
                    """;
        }

        if (
                timelineRangeCategory(
                        category
                )
        ) {

            return """
                    이 장에서는 여러 해를 하나의 공통 성향이나
                    하나의 생활주제로 묶지 않습니다.

                    공식 Grounding의 연도흐름에 있는
                    각 연도를 제공된 순서대로 설명하세요.

                    각 연도에서는
                    연도종합판정을 먼저 사용하세요.

                    최종연도판정과 전체흐름은
                    raw 연도세부근거보다 상위의 공식 결론입니다.

                    주된작동축이 있으면
                    그 값 자체의 의미만 설명합니다.

                    주된부담축이 있으면
                    전체 연도 방향으로 확대하지 않습니다.

                    영역간구조가 있으면
                    Java가 계산한 그 결과 자체만 설명합니다.

                    전환점이 있으면
                    Java가 계산한 구조 변화 범위만 설명하고
                    특정 사건으로 번역하지 않습니다.

                    확정하지않은판단에 들어 있는 항목은
                    연도세부근거를 이용해 새로 만들어 채우지 마세요.

                    연도세부근거는
                    연도종합판정을 설명하는 세부 자료로만 사용하세요.

                    Grounding에 없는 연도는 만들지 마세요.

                    한 해의 근거를
                    다른 해의 결론에 섞지 마세요.

                    전환점 계산이 없는 연도 사이에는
                    지속,
                    강화,
                    약화,
                    인과,
                    전후 단계를 새로 만들지 마세요.
                    """;
        }

        return """
                공식원국판단이 있으면
                각 Conclusion을 독립적인 Java 계산 결과로 설명하세요.

                현재흐름의 Scenario에 공식현재판단이 있으면
                각 Conclusion을 해당 Scenario의 독립 계산 결과로 설명하세요.

                raw 세부근거는
                공식 판단을 설명하는 근거로만 사용하세요.

                공식 판단이 여러 개라는 이유만으로
                하나의 과정,
                관계,
                원인,
                결과,
                전후 단계,
                강화·약화,
                종합 상태를 만들지 마세요.

                공식 판단이 없는 경우에도
                raw Evidence 각각을 독립적으로 설명하고
                Evidence끼리 새 관계를 만들지 마세요.
                """;
    }

    private String categoryOpeningInstruction(

            ConsultationCategory category

    ) {

        if (
                category
                        == ConsultationCategory.PERSONALITY
        ) {

            return """
                    공식기본성향계약에 '반드시 설명' 묶음이 있다면
                    첫 문단부터 그 묶음의 중심판단 하나를
                    사용자가 이해할 수 있는 일상 언어로 설명하세요.

                    추상적인 형용사 하나만으로 시작하지 마세요.

                    첫 문단에 다른 독립 판단을 끌어와
                    원인,
                    결과,
                    조건,
                    다음 단계처럼 연결하지 마세요.
                    """;
        }

        if (
                category
                        == ConsultationCategory.PAST_LUCK
        ) {

            return """
                    전체 과거를 한 문장으로 총평하지 마세요.

                    연도흐름에서 실제 판단이 있는
                    첫 번째 연도부터 시작하세요.

                    첫 연도의 연도종합판정을 기준으로 삼고
                    연도세부근거는 그 결론을 설명하는 데만 사용하세요.

                    여러 해를 하나의 관계,
                    하나의 원인,
                    하나의 공통 결과로 묶지 마세요.
                    """;
        }

        if (
                category
                        == ConsultationCategory.FUTURE_LUCK
        ) {

            return """
                    앞으로의 여러 해를 한 문장으로
                    좋다거나 나쁘다고 총평하지 마세요.

                    연도흐름에서 실제 판단이 있는
                    첫 번째 미래 연도부터 시작하세요.

                    첫 연도의 연도종합판정을 기준으로 설명하고
                    연도세부근거는 그 결론을 설명하는 데만 사용하세요.

                    여러 미래 연도를
                    하나의 사건,
                    원인,
                    결과,
                    공통 흐름으로 묶지 마세요.
                    """;
        }

        return """
                공식원국판단이 있으면
                첫 문단부터 그중 하나의 판단메모를
                일상 언어로 정확하게 설명하세요.

                공식원국판단이 없고
                현재흐름에 공식현재판단이 있으면
                첫 번째 Scenario의 공식현재판단 하나에서 시작하세요.

                공식 deterministic 판단이 전혀 없는 경우에만
                raw Evidence 하나의 범위에서 시작하세요.

                첫 문단부터 서로 다른 독립 판단을
                하나의 과정이나 인과관계로 묶지 마세요.
                """;
    }

    private String writerInternalChecklist(

            ConsultationCategory category

    ) {

        if (
                category
                        == ConsultationCategory.PERSONALITY
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
                    조건설명필수인 판단에서
                    Contract가 실제로 제공한 제한을
                    삭제하거나 새 조건으로 바꾸지 않았는가.

                    5.
                    내부참고를 직접 성격 단정으로
                    사용하지 않았는가.

                    6.
                    전체배경을
                    새로운 독립 심리판정으로 바꾸지 않았는가.

                    7.
                    보조확인근거를 이용해
                    공식기본성향계약에 없는
                    새로운 성향을 만들지 않았는가.

                    8.
                    서로 다른 독립 Selection을
                    원인과 결과,
                    전후 단계,
                    심리적 시간순서,
                    하나의 반응 과정으로 연결하지 않았는가.

                    9.
                    CROSS_PATTERN 또는 SYNTHESIS Selection을
                    다른 독립 Selection과 다시 조합하지 않았는가.

                    10.
                    같은 의미를 불필요하게 반복하지 않았는가.
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
                    그대로 사용했는가.

                    2.
                    연도세부근거의 개수나 반복을 보고
                    전체 방향을 다시 계산하지 않았는가.

                    3.
                    주된작동축을
                    가장 좋은 분야나 가장 나쁜 분야로
                    바꾸지 않았는가.

                    4.
                    주된부담축만 보고
                    연도 전체 방향을 바꾸지 않았는가.

                    5.
                    영역간구조보다 넓은
                    새로운 Cross-Domain 의미를 만들지 않았는가.

                    6.
                    전환점을 특정 사건 발생으로
                    바꾸지 않았는가.

                    7.
                    확정하지않은판단을
                    raw 연도세부근거로 새로 만들어 채우지 않았는가.

                    8.
                    각 연도의 근거를
                    다른 연도와 섞지 않았는가.

                    9.
                    전환점 계산이 없는 연도 사이에
                    지속,
                    강화,
                    약화,
                    전후 인과를 만들지 않았는가.

                    10.
                    실제 과거 사건 또는 미래 특정 사건을
                    새로 만들지 않았는가.
                    """;
        }

        return """
                1.
                공식원국판단을
                raw 원국세부근거보다 우선해서 사용했는가.

                2.
                Scenario의 공식현재판단을
                현재세부근거보다 우선해서 사용했는가.

                3.
                서로 다른 독립 Conclusion을
                원인과 결과로 연결하지 않았는가.

                4.
                서로 다른 시간층의 Conclusion을
                전후 단계나 강화·약화 관계로 연결하지 않았는가.

                5.
                반대 방향의 Conclusion을
                평균내거나 새로운 MIXED로 합성하지 않았는가.

                6.
                raw Evidence에서
                공식 판단에 없는 새로운 관계,
                조건,
                순서를 만들지 않았는가.
                """;
    }

    private String categoryReviewStructureInstruction(

            ConsultationCategory category

    ) {

        if (
                category
                        == ConsultationCategory.PERSONALITY
        ) {

            return """
                    Writer가 공식기본성향검토계약을
                    실제로 지켰는지 확인하세요.

                    반드시 설명할 중심판단이 빠졌다면
                    REVISE합니다.

                    중심판단보다 보조판단을
                    더 강한 성향처럼 썼다면 수정합니다.

                    강한표현이 허용되지 않은 판단을
                    과도하게 단정했다면 수정합니다.

                    조건설명필수인 판단에서
                    Contract가 제공한 제한을 삭제했거나
                    다른 조건으로 바꿨다면 REVISE합니다.

                    내부참고가 독립적인 성격문장으로
                    사용됐다면 제거합니다.

                    전체배경이
                    새로운 독립 심리판정으로 바뀌었다면 수정합니다.

                    보조확인근거는 Contract를 확인하는 데만 사용합니다.

                    Contract에 없는 성향을
                    보조확인근거만으로 추가하지 마세요.

                    서로 다른 독립 Selection을
                    원인과 결과,
                    전후 단계,
                    심리적 시간순서,
                    하나의 반응 과정으로 연결했다면 분리하세요.

                    CROSS_PATTERN 또는 SYNTHESIS Selection도
                    다른 독립 Selection과 새로 결합하지 마세요.
                    """;
        }

        if (
                timelineRangeCategory(
                        category
                )
        ) {

            return """
                    연도흐름의 각 연도에서
                    Writer가 연도종합판정을
                    상위 계약으로 사용했는지 확인하세요.

                    raw 연도세부근거의 개수나 반복으로
                    전체 방향을 다시 계산했다면 REVISE합니다.

                    주된작동축은
                    가장 좋은 영역 또는 가장 나쁜 영역이 아닙니다.

                    주된부담축이 존재해도
                    그것만으로 전체 연도 방향을 변경하지 마세요.

                    영역간구조는
                    Java가 계산한 결과 범위로만 사용합니다.

                    전환점은
                    Java가 계산한 구조 변화 범위로만 설명합니다.

                    확정하지않은판단에 있는 항목을
                    raw Evidence로 새로 만들었다면 제거하세요.

                    각 연도는 해당 연도의 근거만 사용해야 합니다.

                    전환점 계산이 없는데
                    다른 연도와의 지속,
                    강화,
                    약화,
                    전후 인과를 만들었다면 제거하세요.

                    여러 해를
                    하나의 공통 성향이나 결과 예측으로 줄이지 마세요.
                    """;
        }

        return """
                공식원국판단과 공식현재판단이
                raw 세부근거보다 우선되었는지 확인하세요.

                서로 다른 독립 판단 사이에
                Grounding에 없는 관계,
                인과,
                순서,
                강화·약화,
                종합 상태가 추가되었다면
                REVISE합니다.

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
                    재물, 직업, 관계, 학습을
                    각각 별도 상담처럼 나열하지 않습니다.

                    공식기본성향계약에서 직접 설명이 허용된
                    각 Selection을 하나의 완성된 판단으로 취급하고
                    Selection의 핵심의미를 일상 언어로 번역합니다.

                    CROSS_PATTERN 또는 SYNTHESIS Selection도
                    다른 Selection과 다시 조합하지 않고
                    그 Selection 자체의 핵심의미만 설명합니다.

                    서로 다른 독립 Selection 사이에
                    새로운 원인과 결과,
                    실제 심리적 시간순서,
                    전후 단계,
                    누적 조건을 만들지 않습니다.
                    """;

            case PAST_LUCK ->
                    """
                    특정 과거 사건을 맞히지 않습니다.

                    연도흐름의 각 명리연도에서
                    연도종합판정을 공식 결론으로 사용합니다.

                    최종연도판정과 전체흐름을
                    raw 연도세부근거로 다시 계산하지 않습니다.

                    주된작동축,
                    주된부담축,
                    영역간구조,
                    전환점이 실제로 존재할 때만
                    각각의 Java 계산 결과 자체를 설명합니다.

                    전환점이 없는 연도끼리
                    지속,
                    강화,
                    약화,
                    전후 인과를 만들지 않습니다.

                    실제 사건을 만들지 않고
                    각 연도에서 계산된 독립 결과를 설명합니다.
                    """;

            case SOCIAL_RELATIONSHIP ->
                    """
                    이 장에서는 대인관계 Category에 대해
                    Java가 제공한 공식 deterministic 판단메모의 의미만 설명합니다.

                    카테고리 이름,
                    일반적인 사주 지식,
                    일반적인 인간관계 상식만으로
                    공식 판단에 없는 행동,
                    심리,
                    관계 방식,
                    조건,
                    사건을 추가하지 않습니다.

                    여러 독립 판단이 존재해도
                    각각을 별도로 설명하고
                    서로 사이에 새로운 관계,
                    인과,
                    순서,
                    강화,
                    약화,
                    종합 의미를 만들지 않습니다.
                    """;

            case LOVE_STYLE ->
                    """
                    이 장에서는 연애 성향 Category에 대해
                    Java가 제공한 공식 deterministic 판단메모의 의미만 설명합니다.

                    카테고리 이름이나 일반적인 연애 해석을 근거로
                    공식 판단에 없는 행동,
                    심리,
                    관계 방식,
                    조건,
                    사건을 추가하지 않습니다.

                    여러 독립 판단이 존재해도
                    각각을 별도로 설명하고
                    서로 사이에 새로운 관계,
                    인과,
                    순서,
                    강화,
                    약화,
                    종합 의미를 만들지 않습니다.
                    """;

            case PARTNER_LUCK ->
                    """
                    이 장에서는 배우자운 Category에 대해
                    Java가 제공한 공식 deterministic 판단메모의 의미만 설명합니다.

                    카테고리 이름이나 일반적인 배우자운 해석을 근거로
                    공식 판단에 없는 행동,
                    심리,
                    관계 방식,
                    조건,
                    사건을 추가하지 않습니다.

                    여러 독립 판단이 존재해도
                    각각을 별도로 설명하고
                    서로 사이에 새로운 관계,
                    인과,
                    순서,
                    강화,
                    약화,
                    종합 의미를 만들지 않습니다.
                    """;

            case WEALTH ->
                    """
                    이 장에서는 재물 Category에 대해
                    Java가 제공한 공식 deterministic 판단메모의 의미만 설명합니다.

                    카테고리 이름이나 일반적인 재물 해석을 근거로
                    공식 판단에 없는 행동,
                    심리,
                    조건,
                    사건,
                    운용 방식을 추가하지 않습니다.

                    여러 독립 판단이 존재해도
                    각각을 별도로 설명하고
                    서로 사이에 새로운 관계,
                    인과,
                    순서,
                    강화,
                    약화,
                    종합 의미를 만들지 않습니다.
                    """;

            case CAREER ->
                    """
                    이 장에서는 직업 Category에 대해
                    Java가 제공한 공식 deterministic 판단메모의 의미만 설명합니다.

                    카테고리 이름이나 일반적인 직업 해석을 근거로
                    공식 판단에 없는 행동,
                    심리,
                    역할,
                    조건,
                    사건을 추가하지 않습니다.

                    여러 독립 판단이 존재해도
                    각각을 별도로 설명하고
                    서로 사이에 새로운 관계,
                    인과,
                    순서,
                    강화,
                    약화,
                    종합 의미를 만들지 않습니다.
                    """;

            case LEGAL_RISK ->
                    """
                    법적 결과를 예측하지 않습니다.

                    이 장에서는 LEGAL_RISK 전용 Java 분석이
                    실제로 성립시킨 공식 deterministic 판단메모의 의미만 설명합니다.

                    카테고리 이름이나 일반적인 법률 상식을 근거로
                    공식 판단에 없는 행동,
                    조건,
                    사건,
                    인과,
                    대응 방법을 추가하지 않습니다.

                    여러 독립 판단이 존재해도
                    각각을 별도로 설명하고
                    서로 사이에 새로운 관계,
                    순서,
                    강화,
                    약화,
                    종합 의미를 만들지 않습니다.
                    """;

            case HEALTH_TENDENCY ->
                    """
                    질병을 예측하지 않습니다.

                    이 장에서는 건강 경향 Category에 대해
                    Java가 제공한 공식 deterministic 판단메모의 의미만 설명합니다.

                    카테고리 이름이나 일반적인 건강 상식을 근거로
                    공식 판단에 없는 증상,
                    생활 습관,
                    원인,
                    회복 과정,
                    대응 방법을 추가하지 않습니다.

                    여러 독립 판단이 존재해도
                    각각을 별도로 설명하고
                    서로 사이에 새로운 관계,
                    인과,
                    순서,
                    강화,
                    약화,
                    종합 의미를 만들지 않습니다.
                    """;

            case CURRENT_YEAR_LUCK ->
                    """
                    올해 특정 사건을 맞히지 않습니다.

                    현재 시기의 여러 생활영역과 시간층에서
                    Java가 계산한 deterministic Conclusion을
                    각각 독립적으로 설명합니다.

                    여러 Domain 결과를
                    하나의 올해 전체 Status로 합성하지 않습니다.

                    서로 다른 Domain 결과를
                    하나의 공통 원인이나 결과로 묶지 않습니다.

                    서로 다른 시간층 결과를
                    먼저,
                    이후,
                    강화,
                    약화 관계로 연결하지 않습니다.

                    카테고리 이름만으로
                    공식 Conclusion에 없는 생활 의미나 사건을 추가하지 않습니다.
                    """;

            case FUTURE_LUCK ->
                    """
                    미래 특정 사건을 예언하지 않습니다.

                    연도흐름의 각 미래 명리연도에서
                    연도종합판정을 공식 결론으로 사용합니다.

                    최종연도판정과 전체흐름을
                    raw 연도세부근거로 다시 계산하지 않습니다.

                    주된작동축은
                    미래에 반드시 사건이 일어날 분야가 아닙니다.

                    주된부담축은
                    실패가 예정된 분야가 아닙니다.

                    영역간구조는
                    Java가 실제 계산한 결과 범위입니다.

                    전환점은
                    Java가 실제 계산한 구조 변화이며
                    특정 사건의 발생 시점이 아닙니다.

                    전환점이 없는 연도끼리
                    지속,
                    강화,
                    약화,
                    전후 인과를 만들지 않습니다.
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
                    "공식 기본성향 계약의 각 Selection을 하나의 완성된 판단으로 보고 핵심의미를 그대로 일상 언어로 번역합니다. 서로 다른 독립 Selection 사이에 새로운 관계, 인과, 조건 또는 심리적 시간순서를 만들지 않습니다.";

            case PAST_LUCK ->
                    "실제 사건을 만들지 않고 각 과거 연도의 공식 종합판정과 그 연도의 독립 계산 결과를 그대로 설명합니다. 전환점 계산이 없는 연도 사이의 지속, 강화, 약화, 인과를 만들지 않습니다.";

            case SOCIAL_RELATIONSHIP ->
                    "대인관계 Category는 서술 범위만 정합니다. 실제 의미는 각 공식 deterministic 판단메모에서만 가져와 독립적으로 일상 언어로 번역하며, 카테고리 이름만으로 새 행동·심리·관계 방식·조건을 추가하지 않습니다.";

            case LOVE_STYLE ->
                    "연애 성향 Category는 서술 범위만 정합니다. 실제 의미는 각 공식 deterministic 판단메모에서만 가져와 독립적으로 일상 언어로 번역하며, 카테고리 이름만으로 새 행동·심리·관계 방식·조건을 추가하지 않습니다.";

            case PARTNER_LUCK ->
                    "배우자운 Category는 서술 범위만 정합니다. 실제 의미는 각 공식 deterministic 판단메모에서만 가져와 독립적으로 일상 언어로 번역하며, 카테고리 이름만으로 새 행동·심리·관계 방식·조건을 추가하지 않습니다.";

            case WEALTH ->
                    "재물 Category는 서술 범위만 정합니다. 실제 의미는 각 공식 deterministic 판단메모에서만 가져와 독립적으로 일상 언어로 번역하며, 카테고리 이름만으로 새 행동·심리·조건·사건을 추가하지 않습니다.";

            case CAREER ->
                    "직업 Category는 서술 범위만 정합니다. 실제 의미는 각 공식 deterministic 판단메모에서만 가져와 독립적으로 일상 언어로 번역하며, 카테고리 이름만으로 새 행동·심리·역할·조건·사건을 추가하지 않습니다.";

            case LEGAL_RISK ->
                    "법적 위험 Category는 서술 범위만 정합니다. 전용 Java 분석이 실제로 성립시킨 공식 deterministic 판단메모만 일상 언어로 번역하며, 카테고리 이름만으로 새 법적 인과·사건·대응 방법을 추가하지 않습니다.";

            case HEALTH_TENDENCY ->
                    "건강 경향 Category는 서술 범위만 정합니다. 실제 의미는 각 공식 deterministic 판단메모에서만 가져와 독립적으로 일상 언어로 번역하며, 카테고리 이름만으로 새 증상·생활 습관·원인·회복 과정·대응 방법을 추가하지 않습니다.";

            case CURRENT_YEAR_LUCK ->
                    "현재 시기의 각 생활영역과 시간층 deterministic Conclusion을 독립적으로 설명합니다. 여러 결과를 하나의 현재연도 전체 상태나 전후·강화 관계로 합성하지 않고, 카테고리 이름만으로 새 생활 의미나 사건을 추가하지 않습니다.";

            case FUTURE_LUCK ->
                    "미래 사건을 만들지 않고 각 연도의 공식 종합판정과 독립 계산 결과를 그대로 설명합니다. 전환점 계산이 없는 연도 사이의 지속, 강화, 약화, 인과를 만들지 않습니다.";
        };
    }

    private String categorySafetyInstruction(

            ConsultationCategory category

    ) {

        return switch (
                category
                ) {

            case PERSONALITY ->
                    "공식 기본성향 계약에 없는 성향을 추가하지 말고, 서로 다른 독립 성향 판단을 원인과 결과나 하나의 심리 과정으로 연결하지 마세요.";

            case PAST_LUCK ->
                    "과거에 특정 사건이 실제로 있었다고 단정하지 말고, 서로 다른 연도를 하나의 원인과 결과나 지속되는 이야기로 연결하지 마세요.";

            case SOCIAL_RELATIONSHIP ->
                    "판단메모에 없는 친밀감, 유대감, 소통 방식, 거리 조절, 관계 유지, 갈등 발생을 새로 만들지 말고 연애나 결혼 이야기로 확장하지 마세요.";

            case LOVE_STYLE ->
                    "판단메모에 없는 감정 반응, 친밀감, 유대감, 소통 방식, 거리 조절, 관계 유지 또는 갈등 해결 조언을 새로 만들지 말고 결혼 시기나 특정 인연을 예언하지 마세요.";

            case PARTNER_LUCK ->
                    "판단메모에 없는 장기 관계 행동, 역할, 책임감, 소통 방식, 거리 조절 또는 관계 지속성을 새로 만들지 말고 결혼 시기나 특정 인연의 등장을 예언하지 마세요.";

            case WEALTH ->
                    "판단메모에 없는 수입, 손실, 투자, 재산 증가, 돈 관리 행동, 성공과 실패를 새로 만들지 마세요.";

            case CAREER ->
                    "판단메모에 없는 취업, 합격, 승진, 퇴사, 이직, 조직 행동, 역할 변화, 직업 성공과 실패를 새로 만들지 마세요.";

            case LEGAL_RISK ->
                    "판단메모에 없는 법적 사건, 승패, 처벌, 계약 행동, 대응 방법 또는 법률 조언을 새로 만들지 마세요.";

            case HEALTH_TENDENCY ->
                    "판단메모에 없는 증상, 질병, 진단, 치료, 피로, 회복, 생활 습관, 원인 또는 건강 관리 방법을 새로 만들지 마세요.";

            case CURRENT_YEAR_LUCK ->
                    "여러 생활영역의 독립 판단을 하나의 올해 전체 상태로 합치지 말고, 지원이나 부담을 기회·성공·갈등·손실 같은 사건으로 바꾸지 마세요.";

            case FUTURE_LUCK ->
                    "각 연도를 독립적으로 설명하고, 전환점 계산이 없는 연도 사이의 지속·강화·약화·영향을 만들지 말며 미래 사건이나 결과를 확정하지 마세요.";
        };
    }

    private String modelSafeValidationHint(

            StoryValidationResult validation

    ) {

        if (
                validation == null
                        ||
                        validation.valid()
        ) {

            return "";
        }

        /*
         * 실제 위반 문자열을 Reviewer / Repair Prompt에 다시 넣으면
         * 모델이 MIXED, CAREER, 신살, 대운 같은 금지 표현을
         * 그대로 재출력할 수 있다.
         *
         * 따라서 로그에는 원래 Validation Message를 남기되
         * 모델에는 위반 종류만 일반화해서 전달한다.
         */
        return "최종 상담문에 사용자 비노출 표현 또는 출력 형식 위반이 남아 있습니다. "
                + "내부 영문·밑줄 표현, 명리 전문용어, 허용되지 않은 연도 표현, "
                + "마크다운·HTML·금지 호칭·금지 결과 단정을 모두 제거하고 "
                + "공식 판단메모의 의미 범위만 순수 한국어로 다시 작성하세요.";
    }

    private String userFacingGroundingText(

            String source

    ) {

        if (
                source == null
                        ||
                        source.isBlank()
        ) {

            return "해당 계산에서 확정된 판단";
        }

        String result =
                source.trim();

        /*
         * RuleCode 형태의 내부 라벨 제거.
         */
        result =
                result.replaceAll(
                        "\\[[A-Z][A-Z0-9_\\-]*]",
                        ""
                );

        /*
         * 공식 Enum / 내부 처리명은 사용자용 중립 표현으로 번역한다.
         * 계산 의미를 새로 만들지 않고 표면 용어만 바꾼다.
         */
        result = result.replace(
                "SUPPORTIVE",
                "지원 방향"
        );
        result = result.replace(
                "PRESSURE",
                "부담 방향"
        );
        result = result.replace(
                "MIXED",
                "혼합 방향"
        );
        result = result.replace(
                "CONTEXTUAL",
                "문맥 판단"
        );

        result = result.replace(
                "LEARNING_GROWTH",
                "학습·성장 영역"
        );
        result = result.replace(
                "HEALTH_LIFESTYLE",
                "건강·생활 영역"
        );
        result = result.replace(
                "RELATIONSHIP",
                "관계 영역"
        );
        result = result.replace(
                "WEALTH",
                "재물 영역"
        );
        result = result.replace(
                "CAREER",
                "직업 영역"
        );

        result = result.replaceAll(
                "(?i)Annual",
                "연도"
        );
        result = result.replaceAll(
                "(?i)Decade",
                "장기 시기"
        );
        result = result.replaceAll(
                "(?i)Monthly",
                "월 시기"
        );
        result = result.replaceAll(
                "(?i)Functional",
                "기능 판단"
        );
        result = result.replaceAll(
                "(?i)Context",
                "문맥"
        );
        result = result.replaceAll(
                "(?i)Signal",
                "판단 근거"
        );
        result = result.replaceAll(
                "(?i)Source",
                "근거"
        );
        result = result.replaceAll(
                "(?i)Match",
                "판단"
        );
        result = result.replaceAll(
                "(?i)Rule",
                "계산 규칙"
        );
        result = result.replaceAll(
                "(?i)LifeDomain",
                "생활 영역"
        );
        result = result.replaceAll(
                "(?i)deterministic",
                "확정 계산"
        );
        result = result.replaceAll(
                "(?i)Java",
                "계산 시스템"
        );

        result = replaceSajuTermsForUser(
                result
        );

        /*
         * 알려지지 않은 잔여 내부 영문 토큰은
         * 사용자 문장 재료로 전달하지 않는다.
         */
        result =
                LATIN_TOKEN_PATTERN
                        .matcher(
                                result
                        )
                        .replaceAll(
                                ""
                        );

        result =
                result
                        .replaceAll(
                                "\\s+",
                                " "
                        )
                        .replaceAll(
                                "\\s+([,.:;])",
                                "$1"
                        )
                        .trim();

        if (
                result.isBlank()
        ) {

            return "해당 계산에서 확정된 판단";
        }

        return result;
    }

    private String replaceSajuTermsForUser(

            String source

    ) {

        String result =
                source;

        for (
                String term
                : FORBIDDEN_SAJU_TERMS
        ) {

            String replacement =
                    switch (
                            term
                            ) {

                        case "칠살",
                             "편관",
                             "정관",
                             "관살",
                             "관성" ->
                                "책임·외부 기준 기능";

                        case "정재",
                             "편재",
                             "재성" ->
                                "재물 관련 기능";

                        case "비견",
                             "겁재",
                             "비겁" ->
                                "자기 기준·주도권 기능";

                        case "식신",
                             "식상" ->
                                "표현·실행 기능";

                        case "정인",
                             "편인",
                             "인성" ->
                                "이해·내부 정리 기능";

                        case "일간" ->
                                "중심 기준";

                        case "신강",
                             "신약" ->
                                "중심 기준의 수용 여력 상태";

                        case "용신",
                             "희신",
                             "기신" ->
                                "균형 조정 기준";

                        case "격국" ->
                                "중심 구조";

                        case "통근",
                             "투간",
                             "지장간",
                             "천간",
                             "득령",
                             "득지",
                             "득세" ->
                                "원국 구조 근거";

                        case "합화",
                             "삼합",
                             "방합",
                             "육합" ->
                                "결합 관계";

                        case "신살" ->
                                "보조 표지";

                        case "대운" ->
                                "장기 시기 흐름";

                        case "세운" ->
                                "연도 흐름";

                        case "월운" ->
                                "월 흐름";

                        case "오행" ->
                                "기본 요소 구조";

                        case "십성",
                             "십신" ->
                                "기능 관계";

                        case "십이운성",
                             "12운성" ->
                                "생애 단계 보조 지표";

                        default ->
                                "계산 근거";
                    };

            result =
                    result.replace(
                            term,
                            replacement
                    );
        }

        return result;
    }

    private String userFacingArea(

            String domain

    ) {

        return switch (
                domain
                ) {

            case "WEALTH" ->
                    "재물 영역";

            case "CAREER" ->
                    "직업 영역";

            case "RELATIONSHIP" ->
                    "관계 영역";

            case "LEARNING_GROWTH" ->
                    "학습·성장 영역";

            case "HEALTH_LIFESTYLE" ->
                    "건강·생활 영역";

            default ->
                    "생활 영역";
        };
    }

    private String userFacingDirection(

            String status

    ) {

        return switch (
                status
                ) {

            case "SUPPORTIVE" ->
                    "지원 방향으로 계산된 판단";

            case "PRESSURE" ->
                    "부담 방향으로 계산된 판단";

            case "MIXED" ->
                    "혼합 방향으로 계산된 판단";

            case "CONTEXTUAL" ->
                    "단독 결론이 아닌 문맥 판단";

            default ->
                    "해당 계산 범위의 판단";
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
                    "배우자운";

            case WEALTH ->
                    "재물";

            case CAREER ->
                    "직업";

            case LEGAL_RISK ->
                    "법적 위험";

            case HEALTH_TENDENCY ->
                    "건강 경향";

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
                == ConsultationCategory.PAST_LUCK

                ||

                category
                        == ConsultationCategory.FUTURE_LUCK;
    }

    private boolean categoryAllowsYear(

            ConsultationCategory category

    ) {

        return category
                == ConsultationCategory.PAST_LUCK

                ||

                category
                        == ConsultationCategory.CURRENT_YEAR_LUCK

                ||

                category
                        == ConsultationCategory.FUTURE_LUCK;
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
                                        == category
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Prompt Context에서 Category Section을 찾을 수 없습니다: "
                                                + category
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
                        > 300
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
                            ? List.of()
                            : List.copyOf(
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
                            ? List.of()
                            : List.copyOf(
                            emphasis
                    );

            reviewSummary =
                    reviewSummary == null
                            ? ""
                            : reviewSummary.trim();
        }
    }

    private record StoryValidationResult(

            boolean valid,

            String message

    ) {

        private StoryValidationResult {

            message =
                    message == null
                            ? ""
                            : message.trim();
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
                            ? List.of()
                            : List.copyOf(
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
                            ? List.of()
                            : List.copyOf(
                            emphasis
                    );
        }
    }
}