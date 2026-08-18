package com.dajeom.adapter;

import com.dajeom.diagnostic.StoryGenerationDiagnosticLogger;
import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingTimelineScenario;
import com.dajeom.model.ConsultationGroundingTimelineYear;
import com.dajeom.model.ConsultationInterpretationPromptContext;
import com.dajeom.model.ConsultationInterpretationPromptSection;
import com.dajeom.model.ConsultationStoryGeneration;
import com.dajeom.type.ConsultationCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * OpenAI Responses API 기반
 * ConsultationStoryGenerator.
 *
 * =================================================
 * 역할
 * =================================================
 *
 * Java deterministic 명리엔진이 생성한
 * ConsultationInterpretationPromptContext를
 * 사용자용 한국어 장문 풀이로 문장화한다.
 *
 * =================================================
 * 중요
 * =================================================
 *
 * Java 내부 Grounding 전체를
 * OpenAI에 그대로 직렬화하지 않는다.
 *
 * 이유:
 *
 * PAST_LUCK / FUTURE_LUCK처럼
 * 다중 연도 × 월별 계산이 존재하는 Category에서는
 * Source / Reason까지 모두 전달하면
 * 모델 Context Window를 초과할 수 있다.
 *
 * 따라서:
 *
 * Java deterministic Grounding
 *        ↓
 * LLM Compact Projection
 *        ↓
 * OpenAI
 *
 * 구조를 사용한다.
 *
 * =================================================
 *
 * Compact Projection은 계산이 아니다.
 *
 * 기존 Grounding을:
 *
 * - domain
 * - status
 * - summary
 * - timeline 구조
 *
 * 중심으로 축약할 뿐이다.
 *
 * Java Grounding 원본은 변경하지 않는다.
 */
@Component
public class OpenAiConsultationStoryGenerator
        implements ConsultationStoryGenerator {

    private static final String PROVIDER =
            "OPENAI";

    private static final String OPENAI_BASE_URL =
            "https://api.openai.com";

    private static final String RESPONSES_PATH =
            "/v1/responses";

    private static final String COMPACT_GROUNDING_VERSION =
            "DAJEOM_LLM_USER_FACING_2";

    /*
     * Category별 목표 한국어 글자 수.
     */
    private static final int STORY_TARGET_MIN_LENGTH =
            1800;

    private static final int STORY_TARGET_MAX_LENGTH =
            2200;

    /*
     * GPT-4o-mini 최대 출력 토큰 상한.
     */
    private static final int GPT_4O_MINI_MAX_OUTPUT_TOKENS =
            16_384;

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
                        && maxOutputTokens
                        > GPT_4O_MINI_MAX_OUTPUT_TOKENS
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


    /*
     * =================================================
     * 공식 Story Generation
     * =================================================
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
                                        !section
                                                .unavailable()
                        )
                        .map(
                                ConsultationInterpretationPromptSection
                                        ::category
                        )
                        .distinct()
                        .sorted(
                                (
                                        left,
                                        right
                                ) ->
                                        Integer.compare(
                                                left.getOrder(),
                                                right.getOrder()
                                        )
                        )
                        .toList();

        int availableCount =
                expectedCategories
                        .size();

        diagnosticLogger
                .generationStarted(
                        generationId,
                        model,
                        availableCount,
                        unavailableCount
                );

        if (
                expectedCategories
                        .isEmpty()
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
         * =================================================
         * Category별 독립 생성
         * =================================================
         */

        for (
                ConsultationCategory category
                : expectedCategories
        ) {

            ConsultationInterpretationPromptSection
                    section =
                    findSection(
                            promptContext,
                            category
                    );

            if (
                    section.unavailable()
            ) {

                throw new IllegalStateException(
                        "available Category 목록에 unavailable Section이 포함되었습니다: "
                                + category
                );
            }

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
                        generateSingleCategoryStory(
                                generationId,
                                section,
                                category,
                                categoryStartedAt
                        );

                stories.put(
                        category,
                        storyText
                );

                long elapsedMillis =
                        elapsedMillis(
                                categoryStartedAt
                        );

                diagnosticLogger
                        .categorySucceeded(
                                generationId,
                                model,
                                category,
                                storyText.length(),
                                elapsedMillis
                        );

                /*
                 * 분량은 품질 목표일 뿐
                 * 생성 실패 조건은 아니다.
                 */
                if (
                        storyText.length()
                                < STORY_TARGET_MIN_LENGTH
                                || storyText.length()
                                > STORY_TARGET_MAX_LENGTH
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

            } catch (
                    Exception exception
            ) {

                long elapsedMillis =
                        elapsedMillis(
                                categoryStartedAt
                        );

                diagnosticLogger
                        .categoryFailed(
                                generationId,
                                model,
                                category,
                                elapsedMillis,
                                exception
                        );

                /*
                 * 현재 Generation 계약은
                 * all-or-nothing 그대로 유지한다.
                 */
                diagnosticLogger
                        .generationFallback(
                                generationId,
                                model,
                                "CATEGORY_GENERATION_FAILED:"
                                        + category.name()
                        );

                return Optional.empty();
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

        long totalElapsedMillis =
                elapsedMillis(
                        generationStartedAt
                );

        diagnosticLogger
                .generationSucceeded(
                        generationId,
                        model,
                        stories.size(),
                        totalElapsedMillis
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
     * =================================================
     * Single Category Generation
     * =================================================
     */

    private String generateSingleCategoryStory(

            String generationId,

            ConsultationInterpretationPromptSection
                    section,

            ConsultationCategory category,

            long categoryStartedAt

    ) throws Exception {

        Objects.requireNonNull(
                generationId,
                "generationId가 필요합니다."
        );

        Objects.requireNonNull(
                section,
                "OpenAI Story Generation에는 Prompt Section이 필요합니다."
        );

        Objects.requireNonNull(
                category,
                "OpenAI Story Generation에는 ConsultationCategory가 필요합니다."
        );

        if (
                section.category()
                        != category
        ) {

            throw new IllegalArgumentException(
                    "Prompt Section Category가 요청 Category와 일치하지 않습니다. expected="
                            + category
                            + ", actual="
                            + section.category()
            );
        }

        if (
                section.unavailable()
        ) {

            throw new IllegalArgumentException(
                    "unavailable Section은 OpenAI Story Generation 대상이 아닙니다: "
                            + category
            );
        }

        /*
         * =================================================
         * 핵심 변경
         * =================================================
         *
         * 기존:
         *
         * jsonMapper.writeValueAsString(section)
         *
         * ↓
         *
         * Grounding의 sources / reasons까지
         * 전부 전달되어 Timeline Range에서
         * Context Window가 과도하게 커졌다.
         *
         * 현재:
         *
         * createCompactSectionPayload(section)
         *
         * ↓
         *
         * LLM 문장화에 필요한 최소 Grounding만 전달한다.
         */

        Map<String, Object> compactPayload =
                createCompactSectionPayload(
                        section
                );

        String sectionJson =
                jsonMapper
                        .writeValueAsString(
                                compactPayload
                        );

        Map<String, Object>
                requestBody =
                createRequestBody(
                        sectionJson,
                        category
                );

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
                        || responseBody.isBlank()
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

        return parseSingleStory(
                generationId,
                responseBody,
                category
        );
    }


    /*
     * =================================================
     * LLM Compact Projection
     * =================================================
     *
     * Java Grounding 원본은 변경하지 않는다.
     *
     * OpenAI로 전송할 때만:
     *
     * - Source
     * - Source Key
     * - Tier
     * - 내부 Reason
     * - Rule Code
     *
     * 를 제거한다.
     *
     * 문장화에 필요한:
     *
     * - Domain
     * - Status
     * - Summary
     * - Timeline
     *
     * 만 유지한다.
     */

    private Map<String, Object>
    createCompactSectionPayload(

            ConsultationInterpretationPromptSection
                    section

    ) {

        Objects.requireNonNull(
                section,
                "사용자용 해석 자료 생성에는 Prompt Section이 필요합니다."
        );

        Map<String, Object> result =
                new LinkedHashMap<>();

        /*
         * 내부 식별자 / Enum / Constraint는
         * OpenAI 입력에 노출하지 않는다.
         *
         * LLM이 보는 것은 오직
         * 사용자용 주제와 생활언어 의미뿐이다.
         */
        result.put(
                "주제",
                section.title()
        );

        result.put(
                "기본적으로 드러나는 흐름",
                compactEvidences(
                        section.natalEvidences()
                )
        );

        result.put(
                "현재 시점에 추가되는 흐름",
                compactTimelineScenarios(
                        section.timelineScenarios()
                )
        );

        result.put(
                "연도별 흐름",
                compactTimelineYears(
                        section.timelineYears()
                )
        );

        return result;
    }


    /*
     * =================================================
     * Compact Natal / Evidence
     * =================================================
     *
     * 동일:
     *
     * domain
     * +
     * status
     * +
     * summary
     *
     * 조합은 한 번만 전달한다.
     *
     * Source가 여러 개였다는 이유만으로
     * LLM에게 같은 의미를 반복해서 보내지 않는다.
     */

    private List<Map<String, Object>>
    compactEvidences(

            List<ConsultationGroundingEvidence>
                    evidences

    ) {

        if (
                evidences == null
                        || evidences.isEmpty()
        ) {

            return List.of();
        }

        LinkedHashMap<
                String,
                Map<String, Object>
                > unique =
                new LinkedHashMap<>();

        for (
                ConsultationGroundingEvidence evidence
                : evidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "사용자용 해석 Evidence는 null일 수 없습니다."
            );

            String domainName =
                    evidence
                            .domain()
                            .name();

            String statusName =
                    evidence
                            .status()
                            .name();

            String area =
                    userFacingArea(
                            domainName
                    );

            String direction =
                    userFacingDirection(
                            statusName
                    );

            String meaning =
                    userFacingMeaning(
                            domainName,
                            statusName,
                            evidence.summary()
                    );

            /*
             * OpenAI에는 backend summary 자체를 보내지 않는다.
             *
             * 동일한 생활영역 / 흐름 / 생활언어 의미만
             * 중복 제거한다.
             */
            String signature =
                    area
                            + "\u001F"
                            + direction
                            + "\u001F"
                            + meaning;

            if (
                    unique.containsKey(
                            signature
                    )
            ) {

                continue;
            }

            Map<String, Object> compact =
                    new LinkedHashMap<>();

            compact.put(
                    "생활에서 살펴볼 부분",
                    area
            );

            compact.put(
                    "흐름의 방향",
                    direction
            );

            compact.put(
                    "생활에서의 의미",
                    meaning
            );

            unique.put(
                    signature,
                    compact
            );
        }

        return List.copyOf(
                unique.values()
        );
    }


    /*
     * =================================================
     * Current Timeline Scenario Compact
     * =================================================
     */

    private List<Map<String, Object>>
    compactTimelineScenarios(

            List<ConsultationGroundingTimelineScenario>
                    scenarios

    ) {

        if (
                scenarios == null
                        || scenarios.isEmpty()
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

            Objects.requireNonNull(
                    scenario,
                    "사용자용 현재 흐름은 null일 수 없습니다."
            );

            Map<String, Object> compact =
                    new LinkedHashMap<>();

            compact.put(
                    "가능한 흐름 순서",
                    scenario.scenarioOrder()
            );

            compact.put(
                    "연도",
                    scenario.sajuYear()
            );

            compact.put(
                    "시점 안내",
                    userFacingTimelineNotice(
                            scenario.fullyDefinite(),
                            scenario.timelineUncertaintyExists()
                    )
            );

            compact.put(
                    "생활 흐름",
                    compactEvidences(
                            scenario.evidences()
                    )
            );

            result.add(
                    compact
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * PAST / FUTURE Timeline Year Compact
     * =================================================
     *
     * monthIndices는 OpenAI에 보내지 않는다.
     *
     * 이유:
     *
     * PAST_LUCK / FUTURE_LUCK의 출력 목적은
     * 연도별 전체 흐름 설명이며,
     * 내부 월 인덱스는 문장화를 위해 필요하지 않다.
     *
     * 또한 LLM이 monthIndex를 달력 월로
     * 잘못 해석하는 문제도 막는다.
     */

    private List<Map<String, Object>>
    compactTimelineYears(

            List<ConsultationGroundingTimelineYear>
                    timelineYears

    ) {

        if (
                timelineYears == null
                        || timelineYears.isEmpty()
        ) {

            return List.of();
        }

        List<Map<String, Object>> result =
                new ArrayList<>(
                        timelineYears.size()
                );

        for (
                ConsultationGroundingTimelineYear timelineYear
                : timelineYears
        ) {

            Objects.requireNonNull(
                    timelineYear,
                    "사용자용 연도 흐름은 null일 수 없습니다."
            );

            Map<String, Object> compact =
                    new LinkedHashMap<>();

            compact.put(
                    "연도",
                    timelineYear.sajuYear()
            );

            compact.put(
                    "시점 안내",
                    timelineYear.timelineUncertaintyExists()
                            ? "시점 경계에 불확실성이 있어 하나의 결과로 단정하지 않습니다."
                            : "해당 연도의 전체적인 생활 흐름으로 참고합니다."
            );

            compact.put(
                    "생활 흐름",
                    compactEvidences(
                            timelineYear.evidences()
                    )
            );

            result.add(
                    compact
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Request Body
     * =================================================
     */

    private Map<String, Object> createRequestBody(

            String sectionJson,

            ConsultationCategory category

    ) {

        Map<String, Object>
                requestBody =
                new LinkedHashMap<>();

        requestBody.put(
                "model",
                model
        );

        requestBody.put(
                "store",
                false
        );

        requestBody.put(
                "max_output_tokens",
                maxOutputTokens
        );

        requestBody.put(
                "instructions",
                createDeveloperInstructions(
                        category
                )
        );

        requestBody.put(
                "input",
                createUserInput(
                        sectionJson,
                        category
                )
        );

        Map<String, Object>
                text =
                new LinkedHashMap<>();

        text.put(
                "format",
                createStructuredOutputFormat(
                        category
                )
        );

        requestBody.put(
                "text",
                text
        );

        return requestBody;
    }


    /*
     * =================================================
     * Developer Instructions
     * =================================================
     */

    private String createDeveloperInstructions(

            ConsultationCategory category

    ) {

        String topic =
                categoryDisplayName(
                        category
                );

        String categorySafety =
                categorySafetyInstruction(
                        category
                );

        return """
                너는 한 사람에게 사주 상담 결과를 설명하는 한국어 상담자다.

                계산과 판단은 이미 끝났다.
                너는 제공된 자료의 뜻을 바꾸지 말고
                실제 생활에서 어떻게 느껴질 수 있는지 자연스럽게 풀어 쓴다.

                이번에 설명할 주제는 다음과 같다.

                %s

                반드시 지킬 원칙:

                1. 제공된 자료 밖의 새로운 판단을 만들지 않는다.

                2. 입력에 없는 사건, 사람, 날짜, 나이, 결과를 만들어내지 않는다.

                3. 좋은 흐름과 부담스러운 흐름이 함께 있으면 한쪽을 지우지 않는다.

                4. 시점이 불확실하다고 적혀 있으면 하나의 결과로 확정하지 않는다.

                5. 돈을 번다, 결혼한다, 이혼한다, 합격한다, 취업한다,
                   소송이 생긴다, 병이 생긴다, 사고가 난다처럼
                   실제 사건을 예언하지 않는다.

                6. 전문 명리 용어, 개발 용어, 내부 계산 용어를 사용자 문장에 쓰지 않는다.
                   계산 과정을 설명하지 말고 생활에서 체감되는 의미만 쓴다.

                7. 영어 표현을 한국어 문장 사이에 섞지 않는다.

                8. 사용자를 '귀하', '여러분'이라고 부르지 않는다.
                   필요하지 않으면 주어를 자연스럽게 생략한다.

                9. 보고서처럼 '분석 결과', '근거에 따르면', '입력 자료에서는'으로 시작하지 않는다.
                   첫 문장부터 이 주제의 실제 생활 모습으로 들어간다.

                10. 5~8개 문단의 하나로 이어지는 상담 이야기로 작성한다.
                    소제목, 번호 목록, 표는 사용하지 않는다.

                11. 약 1800~2200자의 충분한 분량을 목표로 한다.
                    같은 말을 반복해서 분량을 채우지 않는다.

                12. 마지막 문단에서는 운명을 확정하지 말고
                    현실적으로 어떻게 활용하고 조절하면 좋은지 정리한다.

                이 주제에서 특히 지킬 사항:

                %s

                응답 형식에는 storyText 하나만 넣는다.
                """
                .formatted(
                        topic,
                        categorySafety
                );
    }


    /*
     * =================================================
     * User Input
     * =================================================
     */

    private String createUserInput(

            String sectionJson,

            ConsultationCategory category

    ) {

        return """
                아래 자료는 계산이 끝난 결과를
                사용자가 이해하기 쉬운 생활언어로 바꾸기 위해 정리한 참고 자료다.

                설명할 주제:
                %s

                참고 자료:

                %s

                자료에 적힌 내용만 사용해서
                자연스러운 한국어 상담문을 작성하라.

                내부 계산 과정이나 자료 구조를 설명하지 않는다.
                전문용어를 되풀이하지 않는다.
                생활에서 느껴질 수 있는 생각, 행동, 선택, 부담,
                관계 방식과 관리 방법으로 풀어 쓴다.

                연도가 들어 있다면 그 연도만 언급할 수 있다.
                지나간 연도는 실제 사건 기록처럼 쓰지 않는다.
                앞으로의 연도는 확정된 미래 사건처럼 쓰지 않는다.

                자료에 없는 사실은 보충하거나 추측하지 않는다.
                """
                .formatted(
                        categoryDisplayName(
                                category
                        ),
                        sectionJson
                );
    }


    /*
     * =================================================
     * Structured Output Schema
     * =================================================
     */

    private Map<String, Object>
    createStructuredOutputFormat(

            ConsultationCategory category

    ) {

        Objects.requireNonNull(
                category,
                "응답 형식 생성에는 ConsultationCategory가 필요합니다."
        );

        Map<String, Object>
                storyTextSchema =
                new LinkedHashMap<>();

        storyTextSchema.put(
                "type",
                "string"
        );

        Map<String, Object>
                properties =
                new LinkedHashMap<>();

        properties.put(
                "storyText",
                storyTextSchema
        );

        Map<String, Object>
                schema =
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

        Map<String, Object>
                format =
                new LinkedHashMap<>();

        format.put(
                "type",
                "json_schema"
        );

        format.put(
                "name",
                "dajeom_story"
        );

        format.put(
                "description",
                categoryDisplayName(category)
                        + " 상담문"
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
     * =================================================
     * Response Parser
     * =================================================
     */

    private String parseSingleStory(

            String generationId,

            String responseBody,

            ConsultationCategory expectedCategory

    ) throws Exception {

        Objects.requireNonNull(
                generationId,
                "generationId가 필요합니다."
        );

        Objects.requireNonNull(
                responseBody,
                "OpenAI Response Body가 필요합니다."
        );

        Objects.requireNonNull(
                expectedCategory,
                "검증할 ConsultationCategory가 필요합니다."
        );

        JsonNode response =
                jsonMapper
                        .readTree(
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
                        expectedCategory,
                        status
                );

        if (
                !"completed"
                        .equals(
                                status
                        )
        ) {

            String incompleteReason =
                    response
                            .path(
                                    "incomplete_details"
                            )
                            .path(
                                    "reason"
                            )
                            .asString(
                                    ""
                            )
                            .trim();

            String errorMessage =
                    response
                            .path(
                                    "error"
                            )
                            .path(
                                    "message"
                            )
                            .asString(
                                    ""
                            )
                            .trim();

            String detail =
                    firstNonBlank(
                            errorMessage,
                            incompleteReason,
                            "UNKNOWN"
                    );

            throw new IllegalStateException(
                    "OpenAI Responses API가 completed 상태가 아닙니다. "
                            + "status="
                            + (
                            status.isBlank()
                                    ? "UNKNOWN"
                                    : status
                    )
                            + ", detail="
                            + detail
            );
        }

        String structuredText =
                extractOutputText(
                        response
                );

        diagnosticLogger
                .structuredOutputExtracted(
                        generationId,
                        model,
                        expectedCategory,
                        structuredText.length()
                );

        JsonNode structuredRoot =
                jsonMapper
                        .readTree(
                                structuredText
                        );

        String storyText =
                structuredRoot
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
                    "OpenAI Story Output에 storyText가 없습니다. category="
                            + expectedCategory
            );
        }

        return sanitizeGeneratedStoryText(
                storyText
        );
    }


    /*
     * =================================================
     * Output Text Extractor
     * =================================================
     */

    private String extractOutputText(

            JsonNode response

    ) {

        JsonNode output =
                response
                        .path(
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

            String outputType =
                    outputItem
                            .path(
                                    "type"
                            )
                            .asString(
                                    ""
                            )
                            .trim();

            if (
                    !"message"
                            .equals(
                                    outputType
                            )
            ) {

                continue;
            }

            JsonNode content =
                    outputItem
                            .path(
                                    "content"
                            );

            if (
                    !content.isArray()
            ) {

                continue;
            }

            for (
                    JsonNode contentPart
                    : content
            ) {

                String contentType =
                        contentPart
                                .path(
                                        "type"
                                )
                                .asString(
                                        ""
                                )
                                .trim();

                if (
                        !"output_text"
                                .equals(
                                        contentType
                                )
                ) {

                    continue;
                }

                String text =
                        contentPart
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
     * =================================================
     * 사용자용 의미 변환
     * =================================================
     *
     * 여기서는 새로운 사주 판단을 하지 않는다.
     *
     * 18차가 이미 확정한:
     *
     * - 생활영역
     * - 방향 상태
     * - 요약
     *
     * 를 사용자에게 노출 가능한 말로만 바꾼다.
     */

    private String userFacingArea(
            String domainName
    ) {

        return switch (
                domainName
                ) {

            case "WEALTH" ->
                    "돈과 자원을 계획하고 관리하는 방식";

            case "CAREER" ->
                    "일과 책임을 맡고 성과를 만들어가는 방식";

            case "RELATIONSHIP" ->
                    "사람과 관계를 맺고 거리를 조절하는 방식";

            case "LEARNING_GROWTH" ->
                    "배우고 표현하며 성장하는 방식";

            case "HEALTH_LIFESTYLE" ->
                    "생활 리듬과 회복을 관리하는 방식";

            default ->
                    "생활 전반에서 선택하고 대응하는 방식";
        };
    }


    private String userFacingDirection(
            String statusName
    ) {

        return switch (
                statusName
                ) {

            case "SUPPORTIVE" ->
                    "힘을 쓰기 비교적 수월한 흐름";

            case "PRESSURE" ->
                    "부담을 조절할 필요가 있는 흐름";

            case "MIXED" ->
                    "수월함과 부담이 함께 나타나는 흐름";

            case "CONTEXTUAL" ->
                    "상황에 따라 체감이 달라질 수 있는 흐름";

            case "NEUTRAL" ->
                    "크게 한쪽으로 치우치지 않는 흐름";

            case "CAUTION" ->
                    "조금 더 신중하게 다루면 좋은 흐름";

            default ->
                    "상황을 살펴가며 조절할 흐름";
        };
    }


    private String userFacingMeaning(

            String domainName,

            String statusName,

            String backendSummary

    ) {

        String normalizedSummary =
                backendSummary == null
                        ? ""
                        : backendSummary.trim();

        /*
         * 이미 생활언어로 작성된 Summary라면
         * 그 의미를 살릴 수 있다.
         *
         * 반대로 backend/명리 용어가 섞여 있으면
         * OpenAI에 원문을 보내지 않고
         * Domain + Status 기반의 안전한 생활언어로 대체한다.
         */
        if (
                isUserFacingSummarySafe(
                        normalizedSummary
                )
        ) {

            return normalizedSummary;
        }

        return fallbackUserFacingMeaning(
                domainName,
                statusName
        );
    }


    private boolean isUserFacingSummarySafe(
            String value
    ) {

        if (
                value == null
                        || value.isBlank()
        ) {

            return false;
        }

        if (
                value.matches(
                        ".*[A-Za-z_].*"
                )
        ) {

            return false;
        }

        List<String> backendTerms =
                List.of(
                        "원국",
                        "명리",
                        "십성",
                        "재성",
                        "관성",
                        "관살",
                        "정재",
                        "편재",
                        "비겁",
                        "식상",
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
                        "지지",
                        "합화",
                        "궁위",
                        "신살",
                        "대운",
                        "세운",
                        "월운",
                        "오행",
                        "음양",
                        "12운성"
                );

        for (
                String backendTerm
                : backendTerms
        ) {

            if (
                    value.contains(
                            backendTerm
                    )
            ) {

                return false;
            }
        }

        return true;
    }


    private String fallbackUserFacingMeaning(

            String domainName,

            String statusName

    ) {

        if (
                "WEALTH".equals(
                        domainName
                )
        ) {

            return switch (
                    statusName
                    ) {

                case "SUPPORTIVE" ->
                        "돈을 계획하고 유지하는 과정에서 가진 선택지를 비교적 안정적으로 활용하기 좋습니다.";

                case "PRESSURE" ->
                        "기회가 보여도 감당할 범위와 이후의 유지 부담을 함께 따져보는 편이 좋습니다.";

                case "MIXED" ->
                        "움직일 여지는 있지만 지출이나 책임도 함께 커질 수 있어 확장과 유지 사이의 균형이 중요합니다.";

                default ->
                        "돈과 자원을 움직일 때 당장의 이익보다 지속해서 감당할 수 있는지를 함께 살펴보는 것이 중요합니다.";
            };
        }

        if (
                "CAREER".equals(
                        domainName
                )
        ) {

            return switch (
                    statusName
                    ) {

                case "SUPPORTIVE" ->
                        "맡은 역할을 정리하고 자신의 방식으로 성과를 만들어가는 데 힘을 쓰기 수월한 편입니다.";

                case "PRESSURE" ->
                        "책임이나 요구가 늘어날수록 혼자 모두 떠안기보다 우선순위와 역할 범위를 분명히 하는 것이 중요합니다.";

                case "MIXED" ->
                        "성과를 낼 기회와 책임의 무게가 함께 커질 수 있어 속도보다 역할 조절이 중요합니다.";

                default ->
                        "일에서는 상황에 맞춰 책임의 범위와 자신의 표현 방식을 조절할수록 안정적으로 힘을 쓰기 좋습니다.";
            };
        }

        if (
                "RELATIONSHIP".equals(
                        domainName
                )
        ) {

            return switch (
                    statusName
                    ) {

                case "SUPPORTIVE" ->
                        "사람들과 주고받는 과정에서 자신의 뜻을 비교적 자연스럽게 표현하고 관계의 균형을 잡기 좋습니다.";

                case "PRESSURE" ->
                        "상대의 기대나 관계의 책임을 지나치게 떠안지 않도록 거리와 역할을 분명히 하는 것이 중요합니다.";

                case "MIXED" ->
                        "가까워질 기회와 피로가 함께 생길 수 있어 친밀함과 개인적인 여유 사이의 균형이 필요합니다.";

                default ->
                        "관계에서는 상대의 반응만 따르기보다 자신의 속도와 경계를 함께 살피는 것이 도움이 됩니다.";
            };
        }

        if (
                "LEARNING_GROWTH".equals(
                        domainName
                )
        ) {

            return switch (
                    statusName
                    ) {

                case "SUPPORTIVE" ->
                        "배우고 익힌 것을 자신의 방식으로 정리하고 표현하는 데 힘을 쓰기 좋은 편입니다.";

                case "PRESSURE" ->
                        "배우거나 준비해야 할 것이 많아질수록 한꺼번에 완성하려 하기보다 우선순위를 나누는 것이 중요합니다.";

                case "MIXED" ->
                        "성장할 자극은 충분하지만 동시에 부담도 커질 수 있어 속도보다 꾸준함을 유지하는 것이 중요합니다.";

                default ->
                        "배움과 표현에서는 주변 속도보다 자신에게 맞는 방식으로 정리하고 반복하는 것이 도움이 됩니다.";
            };
        }

        if (
                "HEALTH_LIFESTYLE".equals(
                        domainName
                )
        ) {

            return switch (
                    statusName
                    ) {

                case "SUPPORTIVE" ->
                        "생활 리듬을 일정하게 유지하면 회복과 일상의 균형을 비교적 안정적으로 가져가기 좋습니다.";

                case "PRESSURE" ->
                        "일정이나 책임이 몰릴수록 쉬는 시간을 뒤로 미루지 않고 회복 시간을 먼저 확보하는 것이 중요합니다.";

                case "MIXED" ->
                        "활동량이 늘어날 여지와 피로가 쌓일 가능성이 함께 있어 움직임과 휴식의 균형을 의식하는 편이 좋습니다.";

                default ->
                        "몸 상태를 단정하기보다 수면, 식사, 휴식 같은 기본 생활 리듬을 꾸준히 점검하는 것이 도움이 됩니다.";
            };
        }

        return "상황이 달라질 때 자신의 부담 범위와 선택의 속도를 함께 살피는 것이 도움이 됩니다.";
    }


    private String userFacingTimelineNotice(

            boolean fullyDefinite,

            boolean uncertaintyExists

    ) {

        if (
                uncertaintyExists
        ) {

            return "기준 시점에 불확실성이 있어 하나의 결과로 단정하지 않습니다.";
        }

        if (
                fullyDefinite
        ) {

            return "기준 시점이 비교적 명확한 흐름입니다.";
        }

        return "가능한 흐름 가운데 하나로 참고하며 단정하지 않습니다.";
    }


    private String categoryDisplayName(
            ConsultationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "상담 주제가 필요합니다."
        );

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
                    "배우자와 인연의 흐름";

            case WEALTH ->
                    "재물 운용 성향";

            case CAREER ->
                    "직업과 일의 흐름";

            case LEGAL_RISK ->
                    "계약과 분쟁에서 주의할 점";

            case HEALTH_TENDENCY ->
                    "건강과 생활 리듬";

            case CURRENT_YEAR_LUCK ->
                    "올해의 흐름";

            case FUTURE_LUCK ->
                    "앞으로의 흐름";
        };
    }


    private String categorySafetyInstruction(
            ConsultationCategory category
    ) {

        return switch (
                category
                ) {

            case PAST_LUCK ->
                    "지나간 시기는 실제로 어떤 사건이 일어났다고 단정하지 말고, 당시 체감할 수 있었던 생활 분위기와 선택의 흐름으로만 설명한다.";

            case FUTURE_LUCK ->
                    "앞으로의 시기는 특정 사건이 생긴다고 단정하지 말고, 어떤 선택과 부담을 더 세심하게 관리하면 좋은지 중심으로 설명한다.";

            case CURRENT_YEAR_LUCK ->
                    "올해의 흐름도 결과 예언이 아니라 현재 생활에서 힘을 쓰기 좋은 부분과 조절할 부분으로 설명한다.";

            case SOCIAL_RELATIONSHIP,
                 LOVE_STYLE,
                 PARTNER_LUCK ->
                    "특정 사람과의 만남, 결혼, 이별, 재회를 단정하지 말고 관계를 맺고 감정을 표현하며 거리를 조절하는 방식으로 설명한다.";

            case WEALTH ->
                    "수입이나 손실을 단정하지 말고 돈을 계획하고 유지하며 부담을 관리하는 방식으로 설명한다.";

            case CAREER ->
                    "취업, 합격, 승진을 단정하지 말고 역할, 책임, 업무 방식과 조직 안에서 힘을 쓰는 방식으로 설명한다.";

            case LEGAL_RISK ->
                    "소송이나 처벌의 발생과 결과를 단정하지 말고 계약, 약속, 책임 범위, 기록과 확인을 꼼꼼히 하는 생활 관리 방향으로만 설명한다.";

            case HEALTH_TENDENCY ->
                    "질병이나 장기 상태를 진단하지 말고 수면, 피로, 활동과 휴식, 회복 같은 생활 관리 수준에서만 설명한다.";

            case PERSONALITY ->
                    "사람의 성격을 한 문장으로 고정하지 말고 상황에 따라 강점과 부담이 어떻게 다르게 드러날 수 있는지 함께 설명한다.";
        };
    }


    private String sanitizeGeneratedStoryText(
            String storyText
    ) {

        String sanitized =
                requireText(
                        storyText,
                        "생성된 Story Text는 필수입니다."
                );

        /*
         * 혹시 모델이 내부 영문 표식을 그대로 사용해도
         * 사용자에게 노출되지 않도록 Adapter 경계에서 정리한다.
         *
         * 한국어 명리 단어를 기계적으로 치환하지는 않는다.
         * 그런 단어는 애초에 입력 자료에서 제거하고 Prompt로 금지한다.
         */
        return sanitized
                .replace("SUPPORTIVE", "도움이 되는 흐름")
                .replace("PRESSURE", "부담이 커질 수 있는 흐름")
                .replace("MIXED", "도움과 부담이 함께 있는 흐름")
                .replace("CONTEXTUAL", "상황에 따라 달라지는 흐름")
                .replace("NEUTRAL", "크게 치우치지 않는 흐름")
                .replace("CAUTION", "조금 더 신중하게 볼 흐름")
                .replace("Evidence", "근거")
                .replace("evidence", "근거")
                .replace("Grounding", "참고 자료")
                .replace("grounding", "참고 자료")
                .replace("Context", "상황")
                .replace("context", "상황")
                .replace("Domain", "생활 영역")
                .replace("domain", "생활 영역")
                .replace("Timeline", "시간 흐름")
                .replace("timeline", "시간 흐름")
                .replace("Java", "계산 과정")
                .replace("JSON", "입력 자료")
                .trim();
    }


    /*
     * =================================================
     * Prompt Section Finder
     * =================================================
     */

    private ConsultationInterpretationPromptSection
    findSection(

            ConsultationInterpretationPromptContext
                    promptContext,

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


    /*
     * =================================================
     * Utility
     * =================================================
     */

    private static boolean isGpt4oMiniModel(

            String model

    ) {

        if (
                model == null
        ) {

            return false;
        }

        return model
                .trim()
                .startsWith(
                        "gpt-4o-mini"
                );
    }


    private static long elapsedMillis(

            long startedAtNanos

    ) {

        long elapsedNanos =
                System.nanoTime()
                        - startedAtNanos;

        if (
                elapsedNanos <= 0L
        ) {

            return 0L;
        }

        return elapsedNanos
                / 1_000_000L;
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
                            && !value
                            .isBlank()
            ) {

                return value.trim();
            }
        }

        return "";
    }
}