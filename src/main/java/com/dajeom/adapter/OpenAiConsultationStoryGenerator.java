package com.dajeom.adapter;

import com.dajeom.model.ConsultationInterpretationPromptContext;
import com.dajeom.model.ConsultationStoryGeneration;
import com.dajeom.type.ConsultationCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * OpenAI Responses API 기반
 * ConsultationStoryGenerator.
 *
 * =================================================
 *
 * 정밀화 20차
 * ConsultationInterpretationPromptContext만 입력으로 사용한다.
 *
 * =================================================
 *
 * 이 Adapter가 하지 않는 것:
 *
 * - 새로운 사주 계산
 * - Evidence 수정
 * - unavailable Category 보완
 * - 불확실 Scenario 임의 선택
 * - 길흉 확률 생성
 *
 * =================================================
 *
 * 환경변수:
 *
 * OPENAI_API_KEY
 *
 * 선택:
 *
 * OPENAI_MODEL
 * 기본값 gpt-5-mini
 *
 * OPENAI_MAX_OUTPUT_TOKENS
 * 기본값 24000
 *
 * =================================================
 *
 * API Key가 없거나 외부 호출이 실패하면
 * Optional.empty()를 반환한다.
 *
 * deterministic 사주 분석 자체는 실패시키지 않는다.
 */
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

    private static final int STORY_TARGET_MIN_LENGTH =
            1800;

    private static final int STORY_TARGET_MAX_LENGTH =
            2200;

    private final JsonMapper jsonMapper;

    private final RestClient restClient;

    private final String apiKey;

    private final String model;

    private final int maxOutputTokens;

    public OpenAiConsultationStoryGenerator(

            JsonMapper jsonMapper,

            @Value("${OPENAI_API_KEY:}")
            String apiKey,

            @Value("${OPENAI_MODEL:gpt-5-mini}")
            String model,

            @Value("${OPENAI_MAX_OUTPUT_TOKENS:24000}")
            int maxOutputTokens

    ) {

        this.jsonMapper =
                Objects.requireNonNull(
                        jsonMapper,
                        "JsonMapper가 필요합니다."
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

        /*
         * Category가 아직 미확정된 자유 질문은
         * LLM에게 임의 Routing을 맡기지 않는다.
         */
        if (
                promptContext.categoryResolutionRequired()
                        || !promptContext.hasSections()
        ) {

            return Optional.empty();
        }

        List<ConsultationCategory> expectedCategories =
                promptContext
                        .sections()
                        .stream()
                        .filter(
                                section ->
                                        !section.unavailable()
                        )
                        .map(
                                section ->
                                        section.category()
                        )
                        .distinct()
                        .sorted(
                                (left, right) ->
                                        Integer.compare(
                                                left.getOrder(),
                                                right.getOrder()
                                        )
                        )
                        .toList();

        if (
                expectedCategories.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                apiKey.isBlank()
        ) {

            log.info(
                    "OPENAI_API_KEY가 설정되지 않아 외부 LLM Story Generation을 건너뜁니다."
            );

            return Optional.empty();
        }

        try {

            String promptContextJson =
                    jsonMapper
                            .writeValueAsString(
                                    promptContext
                            );

            Map<String, Object> requestBody =
                    createRequestBody(
                            promptContextJson,
                            expectedCategories
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
                        "OpenAI Responses API가 빈 응답을 반환했습니다."
                );
            }

            Map<ConsultationCategory, String> stories =
                    parseStories(
                            responseBody,
                            expectedCategories
                    );

            for (
                    Map.Entry<ConsultationCategory, String> entry
                    : stories.entrySet()
            ) {

                int length =
                        entry
                                .getValue()
                                .length();

                if (
                        length < STORY_TARGET_MIN_LENGTH
                                || length > STORY_TARGET_MAX_LENGTH
                ) {

                    log.warn(
                            "OpenAI Story 분량이 목표 범위를 벗어났습니다. category={}, length={}, target={}~{}",
                            entry.getKey(),
                            length,
                            STORY_TARGET_MIN_LENGTH,
                            STORY_TARGET_MAX_LENGTH
                    );
                }
            }

            return Optional.of(
                    new ConsultationStoryGeneration(
                            promptContext,
                            PROVIDER,
                            model,
                            stories
                    )
            );

        } catch (
                Exception exception
        ) {

            /*
             * 외부 LLM 실패는 deterministic 분석 실패가 아니다.
             *
             * API Key / Prompt 원문 / 응답 전문은
             * 서버 로그에 남기지 않는다.
             */
            log.warn(
                    "OpenAI Story Generation 실패. model={}, reason={}",
                    model,
                    exception.getMessage()
            );

            return Optional.empty();
        }
    }

    /**
     * OpenAI Responses API Request Body 생성.
     */
    private Map<String, Object> createRequestBody(

            String promptContextJson,

            List<ConsultationCategory> expectedCategories

    ) {

        Map<String, Object> requestBody =
                new LinkedHashMap<>();

        requestBody.put(
                "model",
                model
        );

        /*
         * 출생정보 기반 상담 데이터이므로
         * Responses API application state 저장을 사용하지 않는다.
         */
        requestBody.put(
                "store",
                false
        );

        requestBody.put(
                "max_output_tokens",
                maxOutputTokens
        );

        requestBody.put(
                "reasoning",
                Map.of(
                        "effort",
                        "low"
                )
        );

        requestBody.put(
                "instructions",
                createDeveloperInstructions(
                        expectedCategories
                )
        );

        requestBody.put(
                "input",
                createUserInput(
                        promptContextJson,
                        expectedCategories
                )
        );

        Map<String, Object> text =
                new LinkedHashMap<>();

        text.put(
                "verbosity",
                "high"
        );

        text.put(
                "format",
                createStructuredOutputFormat(
                        expectedCategories
                )
        );

        requestBody.put(
                "text",
                text
        );

        return requestBody;
    }

    /**
     * LLM 공통 계약.
     *
     * Java deterministic Grounding을 변경하지 못하도록 한다.
     */
    private String createDeveloperInstructions(
            List<ConsultationCategory> expectedCategories
    ) {

        String categoryNames =
                expectedCategories
                        .stream()
                        .map(
                                Enum::name
                        )
                        .toList()
                        .toString();

        return """
                너는 다점의 사주 해석 문장화 Adapter다.

                입력으로 제공되는 ConsultationInterpretationPromptContext는
                Java deterministic 명리엔진이 확정한 공식 Grounding이다.

                반드시 다음 계약을 지켜라.

                1. 새로운 사주 계산을 하지 않는다.

                2. 입력에 없는 간지, 십신, 용신, 합충, 궁위,
                   대운, 세운, 월운, 신살 근거를 만들지 않는다.

                3. SUPPORTIVE, PRESSURE, MIXED, CONTEXTUAL 등의 상태를
                   확률이나 하나의 길흉 점수로 바꾸지 않는다.

                4. 상반되거나 동시에 존재하는 근거는
                   하나의 결과로 평탄화하지 않는다.
                   서로 다른 방향의 근거가 동시에 존재하면
                   그 공존 상태를 그대로 설명한다.

                5. DEFINITE와 POSSIBLE을 혼동하지 않는다.

                6. 복수 Timeline Scenario가 존재할 경우
                   하나를 임의로 실제 결과처럼 선택하지 않는다.

                7. 출생시간 미상은 미상으로 유지한다.
                   시주를 추정하거나 가상으로 생성하지 않는다.

                8. 신살은 보조 근거로만 사용한다.
                   신살 하나로 핵심 구조를 뒤집지 않는다.

                9. 합이 존재한다는 사실과
                   실제 변화 또는 활성화를 동일시하지 않는다.

                10. 돈을 번다,
                    결혼한다,
                    취업한다,
                    합격한다,
                    병이 생긴다 등의
                    직접적인 사건 예언을 하지 않는다.

                11. 재물 분야에서는
                    돈을 벌 가능성,
                    보유 방식,
                    부담,
                    소비 또는 운용 성향처럼
                    계산 근거가 허용하는 수준까지만 설명한다.

                12. 직업 분야에서는
                    역할,
                    책임,
                    압박,
                    업무 방식,
                    조직 적응,
                    표현 방식 등을 중심으로 설명한다.
                    특정 직업 취업을 단정하지 않는다.

                13. 관계와 연애 분야에서는
                    관계를 맺는 방식,
                    기대,
                    거리,
                    감정 표현,
                    갈등 양상을 중심으로 설명한다.
                    특정 인물과의 결혼이나 이별을 단정하지 않는다.

                14. 건강 분야에서는
                    생활 리듬,
                    균형,
                    소진,
                    회복 관리 등의 표현만 사용한다.
                    오행을 장기,
                    질병,
                    병명,
                    진단,
                    치료와 직접 연결하지 않는다.

                15. 법률 분야에서는
                    승소,
                    패소,
                    처벌 결과 등을 예언하지 않는다.

                16. unavailable Section은
                    추정해서 내용을 채우지 않는다.

                17. 내부 명리 용어를 사용자에게 나열하지 않는다.
                    필요할 때만 짧게 제시한 뒤
                    바로 일상적인 언어로 설명한다.

                18. 사용자에게 공포를 주거나
                    운명을 확정하는 어조를 사용하지 않는다.

                19. 같은 주의문이나 면책문을
                    각 문단마다 반복하지 않는다.

                20. 입력 Grounding 안에 존재하지 않는
                    새로운 근거나 판단을 추가하지 않는다.

                출력 대상 Category는 다음과 같다.

                %s

                각 Category마다 정확히 하나의 storyText를 작성한다.

                storyText는 한국어 기준
                약 1800자에서 2200자를 목표로 한다.

                각 storyText는
                약 5개에서 8개의 자연스러운 문단으로 구성한다.

                단순 키워드 나열이나
                명리 용어 설명문이 아니라
                상담자가 한 사람의 삶의 흐름을 차분히 설명하는
                스토리 형식으로 연결한다.

                좋은 근거와 부담 근거가 함께 있으면
                둘 중 하나를 제거하지 말고
                어떤 상황에서 각각이 드러날 수 있는지를 설명한다.

                시간축 Evidence가 있는 경우
                원국의 기본 성향과
                대운,
                세운,
                월운의 층위를 섞지 않는다.

                장기 흐름과 현재 흐름을 구분해서 설명한다.

                다만 문학적 표현이나 자연스러운 문장화를 위해
                Java 계산 근거의 의미를 변경해서는 안 된다.

                Structured Output Schema 외의
                다른 문장은 출력하지 않는다.
                """.formatted(
                categoryNames
        );
    }

    /**
     * 실제 deterministic Prompt Context를
     * JSON 문자열로 LLM에 전달한다.
     */
    private String createUserInput(

            String promptContextJson,

            List<ConsultationCategory> expectedCategories

    ) {

        List<String> expectedCategoryNames =
                expectedCategories
                        .stream()
                        .map(
                                Enum::name
                        )
                        .toList();

        return """
                아래 JSON은 Java deterministic 계층이 만든
                공식 ConsultationInterpretationPromptContext다.

                이 JSON에 존재하는 Evidence와 Scenario만 사용한다.

                EXPECTED_CATEGORIES:

                %s

                PROMPT_CONTEXT_JSON:

                %s

                각 Category의 available Grounding만 사용해서
                해당 Category의 storyText를 작성하라.

                각 storyText는 한국어 기준
                약 1800자에서 2200자를 목표로 한다.

                Grounding에 없는 사건,
                인물,
                시기,
                결과,
                숫자,
                명리 근거를 만들어내지 않는다.
                """.formatted(
                expectedCategoryNames,
                promptContextJson
        );
    }

    /**
     * OpenAI Structured Output Schema.
     *
     * 반환 형식:
     *
     * {
     *   "stories": [
     *     {
     *       "category": "PERSONALITY",
     *       "storyText": "..."
     *     }
     *   ]
     * }
     */
    private Map<String, Object> createStructuredOutputFormat(
            List<ConsultationCategory> expectedCategories
    ) {

        List<String> categoryNames =
                expectedCategories
                        .stream()
                        .map(
                                Enum::name
                        )
                        .toList();

        Map<String, Object> categorySchema =
                new LinkedHashMap<>();

        categorySchema.put(
                "type",
                "string"
        );

        categorySchema.put(
                "enum",
                categoryNames
        );

        Map<String, Object> storyTextSchema =
                new LinkedHashMap<>();

        storyTextSchema.put(
                "type",
                "string"
        );

        Map<String, Object> itemProperties =
                new LinkedHashMap<>();

        itemProperties.put(
                "category",
                categorySchema
        );

        itemProperties.put(
                "storyText",
                storyTextSchema
        );

        Map<String, Object> itemSchema =
                new LinkedHashMap<>();

        itemSchema.put(
                "type",
                "object"
        );

        itemSchema.put(
                "properties",
                itemProperties
        );

        itemSchema.put(
                "required",
                List.of(
                        "category",
                        "storyText"
                )
        );

        itemSchema.put(
                "additionalProperties",
                false
        );

        Map<String, Object> storiesSchema =
                new LinkedHashMap<>();

        storiesSchema.put(
                "type",
                "array"
        );

        storiesSchema.put(
                "items",
                itemSchema
        );

        Map<String, Object> rootProperties =
                new LinkedHashMap<>();

        rootProperties.put(
                "stories",
                storiesSchema
        );

        Map<String, Object> rootSchema =
                new LinkedHashMap<>();

        rootSchema.put(
                "type",
                "object"
        );

        rootSchema.put(
                "properties",
                rootProperties
        );

        rootSchema.put(
                "required",
                List.of(
                        "stories"
                )
        );

        rootSchema.put(
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
                "dajeom_saju_stories"
        );

        format.put(
                "description",
                "Java deterministic 사주 Grounding을 변경하지 않고 Category별 사용자용 장문 Story로 문장화한 결과"
        );

        format.put(
                "strict",
                true
        );

        format.put(
                "schema",
                rootSchema
        );

        return format;
    }

    /**
     * OpenAI Responses API 응답을
     * ConsultationCategory -> storyText로 변환한다.
     */
    private Map<ConsultationCategory, String> parseStories(

            String responseBody,

            List<ConsultationCategory> expectedCategories

    ) throws Exception {

        JsonNode response =
                jsonMapper
                        .readTree(
                                responseBody
                        );

        /*
         * Jackson 3:
         *
         * asText()는 deprecated.
         * asString("") 사용.
         */
        String status =
                response
                        .path(
                                "status"
                        )
                        .asString(
                                ""
                        )
                        .trim();

        if (
                !"completed".equals(
                        status
                )
        ) {

            throw new IllegalStateException(
                    "OpenAI Responses API가 completed 상태가 아닙니다: "
                            + (
                            status.isBlank()
                                    ? "UNKNOWN"
                                    : status
                    )
            );
        }

        String structuredText =
                extractOutputText(
                        response
                );

        JsonNode structuredRoot =
                jsonMapper
                        .readTree(
                                structuredText
                        );

        JsonNode storiesNode =
                structuredRoot
                        .path(
                                "stories"
                        );

        if (
                !storiesNode.isArray()
        ) {

            throw new IllegalStateException(
                    "OpenAI Structured Output에 stories 배열이 없습니다."
            );
        }

        if (
                expectedCategories.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "검증할 ConsultationCategory가 없습니다."
            );
        }

        Set<ConsultationCategory> expected =
                EnumSet.copyOf(
                        expectedCategories
                );

        EnumMap<ConsultationCategory, String> stories =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        for (
                JsonNode storyNode
                : storiesNode
        ) {

            String categoryName =
                    storyNode
                            .path(
                                    "category"
                            )
                            .asString(
                                    ""
                            )
                            .trim();

            String storyText =
                    storyNode
                            .path(
                                    "storyText"
                            )
                            .asString(
                                    ""
                            )
                            .trim();

            if (
                    categoryName.isBlank()
            ) {

                throw new IllegalStateException(
                        "OpenAI Story Output에 category가 없습니다."
                );
            }

            if (
                    storyText.isBlank()
            ) {

                throw new IllegalStateException(
                        "OpenAI Story Output에 storyText가 없습니다. category="
                                + categoryName
                );
            }

            ConsultationCategory category;

            try {

                category =
                        ConsultationCategory
                                .valueOf(
                                        categoryName
                                );

            } catch (
                    IllegalArgumentException exception
            ) {

                throw new IllegalStateException(
                        "알 수 없는 ConsultationCategory가 반환되었습니다: "
                                + categoryName,
                        exception
                );
            }

            if (
                    !expected.contains(
                            category
                    )
            ) {

                throw new IllegalStateException(
                        "요청하지 않은 ConsultationCategory Story가 반환되었습니다: "
                                + category
                );
            }

            String previous =
                    stories.put(
                            category,
                            storyText
                    );

            if (
                    previous != null
            ) {

                throw new IllegalStateException(
                        "동일 ConsultationCategory Story가 중복되었습니다: "
                                + category
                );
            }
        }

        if (
                !stories
                        .keySet()
                        .equals(
                                expected
                        )
        ) {

            EnumSet<ConsultationCategory> missing =
                    EnumSet.copyOf(
                            expected
                    );

            missing.removeAll(
                    stories.keySet()
            );

            throw new IllegalStateException(
                    "OpenAI Story Output에 Category가 누락되었습니다: "
                            + missing
            );
        }

        return stories;
    }

    /**
     * Responses API의:
     *
     * output[]
     * -> message
     * -> content[]
     * -> output_text
     *
     * 내용을 추출한다.
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
                    !"message".equals(
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
                        !"output_text".equals(
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
}