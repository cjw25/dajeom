package com.dajeom.service;

import com.dajeom.domain.LifeInterpretationPrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * OpenAI Responses API 기반
 * LifeInterpretationTextGenerator 구현체.
 *
 * =================================================
 * 기본 모델
 * =================================================
 *
 * gpt-4o-mini
 *
 * =================================================
 * 외부 JSON 라이브러리 사용 안 함
 * =================================================
 *
 * 이 프로젝트에 Jackson 의존성이 없어도
 * 컴파일될 수 있도록:
 *
 * - java.net.http.HttpClient
 * - 내부 JSON Encoder
 * - 내부 최소 JSON Parser
 *
 * 만 사용한다.
 *
 * =================================================
 * 역할
 * =================================================
 *
 * Java 명리 Rule Engine이 완성한:
 *
 * - systemPrompt
 * - userPrompt
 *
 * 를 OpenAI Responses API에 전달하고
 * 최종 한국어 본문만 반환한다.
 *
 * AI가 명리 판정을 다시 하지 않는다.
 */
@Component
public class OpenAiLifeInterpretationTextGenerator
        implements LifeInterpretationTextGenerator {

    private static final String RESPONSES_PATH =
            "/v1/responses";

    private final HttpClient httpClient;

    private final String apiKey;

    private final String model;

    private final String endpoint;

    private final Duration requestTimeout;

    public OpenAiLifeInterpretationTextGenerator(

            @Value(
                    "${openai.api-key:${OPENAI_API_KEY:}}"
            )
            String apiKey,

            @Value(
                    "${openai.model:${OPENAI_MODEL:gpt-4o-mini}}"
            )
            String model,

            @Value(
                    "${openai.base-url:https://api.openai.com}"
            )
            String baseUrl,

            @Value(
                    "${openai.timeout-seconds:90}"
            )
            long timeoutSeconds

    ) {

        this.apiKey =
                normalizeOptional(
                        apiKey
                );

        this.model =
                requireText(
                        model,
                        "OpenAI model"
                );

        String normalizedBaseUrl =
                normalizeBaseUrl(
                        baseUrl
                );

        this.endpoint =
                normalizedBaseUrl
                        + RESPONSES_PATH;

        if (
                timeoutSeconds <= 0
        ) {

            throw new IllegalArgumentException(
                    "OpenAI timeout-seconds는 1 이상이어야 합니다."
            );
        }

        this.requestTimeout =
                Duration.ofSeconds(
                        timeoutSeconds
                );

        this.httpClient =
                HttpClient
                        .newBuilder()
                        .connectTimeout(
                                requestTimeout
                        )
                        .build();
    }

    /*
     * =================================================
     * 실제 AI 생성
     * =================================================
     */

    @Override
    public String generate(
            LifeInterpretationPrompt prompt
    ) {

        Objects.requireNonNull(
                prompt,
                "LifeInterpretationPrompt는 필수입니다."
        );

        /*
         * 허용되지 않은 Prompt는
         * 외부 API까지 보내지 않는다.
         */
        if (
                !prompt.generationAllowed()
        ) {

            throw new IllegalArgumentException(
                    "generationAllowed=false인 Prompt는 AI에 전달할 수 없습니다. category="
                            + prompt.category()
            );
        }

        return generateText(prompt.systemPrompt(), prompt.userPrompt(), prompt.category().name());
    }

    public String generateText(String systemPrompt, String userPrompt, String context) {
        return generateOutput(systemPrompt,userPrompt,context,null);
    }

    public String generateStructured(String systemPrompt,String userPrompt,String context,String schema) {
        return generateOutput(systemPrompt,userPrompt,context,requireText(schema,"JSON schema"));
    }

    private String generateOutput(String systemPrompt,String userPrompt,String context,String schema) {
        validateApiKey();
        systemPrompt = requireText(systemPrompt, "systemPrompt");
        userPrompt = requireText(userPrompt, "userPrompt");
        String requestBody =
                createRequestBody(
                        systemPrompt,
                        userPrompt,
                        schema
                );

        HttpRequest request =
                HttpRequest
                        .newBuilder()
                        .uri(
                                URI.create(
                                        endpoint
                                )
                        )
                        .timeout(
                                requestTimeout
                        )
                        .header(
                                "Authorization",
                                "Bearer " + apiKey
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .header(
                                "Accept",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(
                                                requestBody,
                                                StandardCharsets.UTF_8
                                        )
                        )
                        .build();

        HttpResponse<String> response =
                send(
                        request,
                        context
                );

        validateStatus(
                response,
                context
        );

        return extractOutputText(
                response.body(),
                context
        );
    }

    /*
     * =================================================
     * Request JSON 생성
     * =================================================
     *
     * gpt-4o-mini 사용.
     *
     * reasoning 옵션은 사용하지 않는다.
     */

    private String createRequestBody(

            String systemPrompt,

            String userPrompt,

            String schema

    ) {

        StringBuilder json =
                new StringBuilder();

        json.append(
                "{"
        );

        json.append(
                "\"model\":"
        );

        appendJsonString(
                json,
                model
        );

        json.append(
                ","
        );

        json.append(
                "\"instructions\":"
        );

        appendJsonString(
                json,
                systemPrompt
        );

        json.append(
                ","
        );

        json.append(
                "\"input\":"
        );

        appendJsonString(
                json,
                userPrompt
        );

        if (schema != null) {
            json.append(",\"text\":{\"format\":{\"type\":\"json_schema\",\"name\":\"saju_chapters\",\"strict\":true,\"schema\":");
            json.append(schema);
            json.append("}}");
        }

        json.append(
                "}"
        );

        return json.toString();
    }

    /*
     * =================================================
     * HTTP 실행
     * =================================================
     */

    private HttpResponse<String> send(

            HttpRequest request,

            String context

    ) {

        try {

            return httpClient.send(

                    request,

                    HttpResponse.BodyHandlers
                            .ofString(
                                    StandardCharsets.UTF_8
                            )
            );

        } catch (InterruptedException exception) {

            Thread
                    .currentThread()
                    .interrupt();

            throw new IllegalStateException(
                    "OpenAI 요청이 중단되었습니다. category="
                            + context,
                    exception
            );

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "OpenAI 요청 중 네트워크 오류가 발생했습니다. category="
                            + context,
                    exception
            );
        }
    }

    /*
     * =================================================
     * HTTP 상태 검사
     * =================================================
     */

    private void validateStatus(

            HttpResponse<String> response,

            String context

    ) {

        int statusCode =
                response.statusCode();

        if (
                statusCode >= 200
                        &&
                        statusCode < 300
        ) {

            return;
        }

        String errorMessage =
                extractApiError(
                        response.body()
                );

        throw new IllegalStateException(
                "OpenAI Responses API 호출에 실패했습니다. "
                        + "category="
                        + context
                        + ", model="
                        + model
                        + ", status="
                        + statusCode
                        + ", error="
                        + errorMessage
        );
    }

    /*
     * =================================================
     * output_text 추출
     * =================================================
     *
     * Responses API의 output 배열 전체를 확인한다.
     *
     * output[0] 같은 위치 고정 접근을 하지 않는다.
     */

    private String extractOutputText(

            String responseBody,

            String context

    ) {

        if (
                responseBody == null
                        ||
                        responseBody.isBlank()
        ) {

            throw new IllegalStateException(
                    "OpenAI 응답 본문이 비어 있습니다. category="
                            + context
            );
        }

        Object parsed;

        try {

            parsed =
                    new JsonParser(
                            responseBody
                    ).parse();

        } catch (RuntimeException exception) {

            throw new IllegalStateException(
                    "OpenAI 응답 JSON을 읽을 수 없습니다. category="
                            + context,
                    exception
            );
        }

        if (
                !(parsed instanceof Map<?, ?> root)
        ) {

            throw new IllegalStateException(
                    "OpenAI 응답 JSON의 최상위 객체가 올바르지 않습니다. category="
                            + context
            );
        }

        Object outputValue =
                root.get(
                        "output"
                );

        if (
                !(outputValue instanceof List<?> outputItems)
        ) {

            throw new IllegalStateException(
                    "OpenAI 응답에 output 배열이 없습니다. "
                            + responseDescription(root)
                            + ", category="
                            + context
            );
        }

        List<String> textParts =
                new ArrayList<>();

        for (
                Object outputValueItem
                : outputItems
        ) {

            if (
                    !(outputValueItem instanceof Map<?, ?> outputItem)
            ) {

                continue;
            }

            String outputType =
                    stringValue(
                            outputItem.get(
                                    "type"
                            )
                    );

            /*
             * message만 확인.
             */
            if (
                    !"message".equals(
                            outputType
                    )
            ) {

                continue;
            }

            Object contentValue =
                    outputItem.get(
                            "content"
                    );

            if (
                    !(contentValue instanceof List<?> contents)
            ) {

                continue;
            }

            for (
                    Object contentValueItem
                    : contents
            ) {

                if (
                        !(contentValueItem instanceof Map<?, ?> content)
                ) {

                    continue;
                }

                String contentType =
                        stringValue(
                                content.get(
                                        "type"
                                )
                        );

                /*
                 * 실제 사용자 본문만 추출.
                 */
                if (
                        !"output_text".equals(
                                contentType
                        )
                ) {

                    continue;
                }

                String text =
                        stringValue(
                                content.get(
                                        "text"
                                )
                        );

                if (
                        text == null
                ) {

                    continue;
                }

                String normalized =
                        text.trim();

                if (
                        !normalized.isEmpty()
                ) {

                    textParts.add(
                            normalized
                    );
                }
            }
        }

        if (
                textParts.isEmpty()
        ) {

            throw new IllegalStateException(
                    "OpenAI 응답에서 output_text를 찾을 수 없습니다. "
                            + responseDescription(root)
                            + ", category="
                            + context
            );
        }

        String generated =
                String.join(
                                "\n\n",
                                textParts
                        )
                        .trim();

        if (
                generated.isEmpty()
        ) {

            throw new IllegalStateException(
                    "OpenAI가 빈 해석 본문을 반환했습니다. category="
                            + context
            );
        }

        return generated;
    }

    /*
     * =================================================
     * OpenAI API Error 추출
     * =================================================
     */

    private String extractApiError(
            String responseBody
    ) {

        if (
                responseBody == null
                        ||
                        responseBody.isBlank()
        ) {

            return "empty response body";
        }

        try {

            Object parsed =
                    new JsonParser(
                            responseBody
                    ).parse();

            if (
                    !(parsed instanceof Map<?, ?> root)
            ) {

                return abbreviate(
                        responseBody,
                        500
                );
            }

            Object errorValue =
                    root.get(
                            "error"
                    );

            if (
                    !(errorValue instanceof Map<?, ?> error)
            ) {

                return responseDescription(
                        root
                );
            }

            String type =
                    stringValue(
                            error.get(
                                    "type"
                            )
                    );

            String code =
                    stringValue(
                            error.get(
                                    "code"
                            )
                    );

            String message =
                    stringValue(
                            error.get(
                                    "message"
                            )
                    );

            StringBuilder result =
                    new StringBuilder();

            appendDescription(
                    result,
                    "type",
                    type
            );

            appendDescription(
                    result,
                    "code",
                    code
            );

            appendDescription(
                    result,
                    "message",
                    message
            );

            if (
                    result.length() == 0
            ) {

                return responseDescription(
                        root
                );
            }

            return result.toString();

        } catch (RuntimeException exception) {

            /*
             * JSON이 아닌 proxy 오류 HTML 등이
             * 반환되는 경우.
             */
            return abbreviate(
                    responseBody,
                    500
            );
        }
    }

    /*
     * =================================================
     * Response metadata
     * =================================================
     */

    private String responseDescription(
            Map<?, ?> root
    ) {

        String id =
                stringValue(
                        root.get(
                                "id"
                        )
                );

        String status =
                stringValue(
                        root.get(
                                "status"
                        )
                );

        StringBuilder result =
                new StringBuilder();

        appendDescription(
                result,
                "responseId",
                id
        );

        appendDescription(
                result,
                "status",
                status
        );

        if (
                result.length() == 0
        ) {

            return "response metadata unavailable";
        }

        return result.toString();
    }

    private void appendDescription(

            StringBuilder target,

            String key,

            String value

    ) {

        if (
                value == null
                        ||
                        value.isBlank()
        ) {

            return;
        }

        if (
                target.length() > 0
        ) {

            target.append(
                    ", "
            );
        }

        target.append(
                key
        );

        target.append(
                "="
        );

        target.append(
                value
        );
    }

    /*
     * =================================================
     * API Key
     * =================================================
     */

    private void validateApiKey() {

        if (
                apiKey == null
                        ||
                        apiKey.isBlank()
        ) {

            throw new IllegalStateException(
                    "OpenAI API Key가 설정되어 있지 않습니다. "
                            + "OPENAI_API_KEY 환경변수 또는 "
                            + "openai.api-key 설정이 필요합니다."
            );
        }
    }

    /*
     * =================================================
     * JSON String Encoder
     * =================================================
     */

    private static void appendJsonString(

            StringBuilder target,

            String value

    ) {

        target.append(
                '"'
        );

        for (
                int i = 0;
                i < value.length();
                i++
        ) {

            char c =
                    value.charAt(
                            i
                    );

            switch (c) {

                case '"' ->

                        target.append(
                                "\\\""
                        );

                case '\\' ->

                        target.append(
                                "\\\\"
                        );

                case '\b' ->

                        target.append(
                                "\\b"
                        );

                case '\f' ->

                        target.append(
                                "\\f"
                        );

                case '\n' ->

                        target.append(
                                "\\n"
                        );

                case '\r' ->

                        target.append(
                                "\\r"
                        );

                case '\t' ->

                        target.append(
                                "\\t"
                        );

                default -> {

                    if (
                            c < 0x20
                    ) {

                        target.append(
                                String.format(
                                        "\\u%04x",
                                        (int) c
                                )
                        );

                    } else {

                        target.append(
                                c
                        );
                    }
                }
            }
        }

        target.append(
                '"'
        );
    }

    /*
     * =================================================
     * 문자열 Helper
     * =================================================
     */

    private static String stringValue(
            Object value
    ) {

        if (
                value instanceof String text
        ) {

            return text;
        }

        return null;
    }

    private static String normalizeBaseUrl(
            String value
    ) {

        String normalized =
                requireText(
                        value,
                        "OpenAI base URL"
                );

        while (
                normalized.endsWith("/")
        ) {

            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 1
                    );
        }

        return normalized;
    }

    private static String normalizeOptional(
            String value
    ) {

        if (
                value == null
        ) {

            return "";
        }

        return value.trim();
    }

    private static String requireText(

            String value,

            String name

    ) {

        Objects.requireNonNull(
                value,
                name + "은 필수입니다."
        );

        String normalized =
                value.trim();

        if (
                normalized.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    name + "은 비어 있을 수 없습니다."
            );
        }

        return normalized;
    }

    private static String abbreviate(

            String value,

            int maximumLength

    ) {

        if (
                value == null
        ) {

            return "";
        }

        String normalized =
                value.trim();

        if (
                normalized.length()
                        <= maximumLength
        ) {

            return normalized;
        }

        return normalized.substring(
                0,
                maximumLength
        )
                + "...";
    }

    /*
     * =================================================
     * 최소 JSON Parser
     * =================================================
     *
     * 외부 JSON 라이브러리 없이
     * OpenAI Response JSON을 읽기 위한 내부 Parser.
     *
     * 지원:
     *
     * - object
     * - array
     * - string
     * - number
     * - true
     * - false
     * - null
     *
     * 이 클래스 외부에서는 사용하지 않는다.
     */

    private static final class JsonParser {

        private final String source;

        private int index;

        private JsonParser(
                String source
        ) {

            this.source =
                    Objects.requireNonNull(
                            source,
                            "JSON source는 필수입니다."
                    );

            this.index = 0;
        }

        private Object parse() {

            skipWhitespace();

            Object value =
                    parseValue();

            skipWhitespace();

            if (
                    index != source.length()
            ) {

                throw error(
                        "JSON 뒤에 처리되지 않은 문자가 존재합니다."
                );
            }

            return value;
        }

        private Object parseValue() {

            skipWhitespace();

            if (
                    index >= source.length()
            ) {

                throw error(
                        "JSON 값이 필요합니다."
                );
            }

            char current =
                    source.charAt(
                            index
                    );

            return switch (current) {

                case '{' ->
                        parseObject();

                case '[' ->
                        parseArray();

                case '"' ->
                        parseString();

                case 't' -> {
                    parseLiteral(
                            "true"
                    );
                    yield Boolean.TRUE;
                }

                case 'f' -> {
                    parseLiteral(
                            "false"
                    );
                    yield Boolean.FALSE;
                }

                case 'n' -> {
                    parseLiteral(
                            "null"
                    );
                    yield null;
                }

                default -> {

                    if (
                            current == '-'
                                    ||
                                    Character.isDigit(
                                            current
                                    )
                    ) {

                        yield parseNumber();
                    }

                    throw error(
                            "지원하지 않는 JSON 값입니다."
                    );
                }
            };
        }

        private Map<String, Object> parseObject() {

            expect(
                    '{'
            );

            skipWhitespace();

            Map<String, Object> result =
                    new LinkedHashMap<>();

            if (
                    peek(
                            '}'
                    )
            ) {

                index++;

                return result;
            }

            while (true) {

                skipWhitespace();

                if (
                        !peek(
                                '"'
                        )
                ) {

                    throw error(
                            "JSON Object key는 문자열이어야 합니다."
                    );
                }

                String key =
                        parseString();

                skipWhitespace();

                expect(
                        ':'
                );

                skipWhitespace();

                Object value =
                        parseValue();

                result.put(
                        key,
                        value
                );

                skipWhitespace();

                if (
                        peek(
                                '}'
                        )
                ) {

                    index++;

                    break;
                }

                expect(
                        ','
                );
            }

            return result;
        }

        private List<Object> parseArray() {

            expect(
                    '['
            );

            skipWhitespace();

            List<Object> result =
                    new ArrayList<>();

            if (
                    peek(
                            ']'
                    )
            ) {

                index++;

                return result;
            }

            while (true) {

                Object value =
                        parseValue();

                result.add(
                        value
                );

                skipWhitespace();

                if (
                        peek(
                                ']'
                        )
                ) {

                    index++;

                    break;
                }

                expect(
                        ','
                );

                skipWhitespace();
            }

            return result;
        }

        private String parseString() {

            expect(
                    '"'
            );

            StringBuilder result =
                    new StringBuilder();

            while (
                    index < source.length()
            ) {

                char current =
                        source.charAt(
                                index++
                        );

                if (
                        current == '"'
                ) {

                    return result.toString();
                }

                if (
                        current != '\\'
                ) {

                    result.append(
                            current
                    );

                    continue;
                }

                if (
                        index >= source.length()
                ) {

                    throw error(
                            "잘못된 JSON escape입니다."
                    );
                }

                char escaped =
                        source.charAt(
                                index++
                        );

                switch (escaped) {

                    case '"' ->
                            result.append(
                                    '"'
                            );

                    case '\\' ->
                            result.append(
                                    '\\'
                            );

                    case '/' ->
                            result.append(
                                    '/'
                            );

                    case 'b' ->
                            result.append(
                                    '\b'
                            );

                    case 'f' ->
                            result.append(
                                    '\f'
                            );

                    case 'n' ->
                            result.append(
                                    '\n'
                            );

                    case 'r' ->
                            result.append(
                                    '\r'
                            );

                    case 't' ->
                            result.append(
                                    '\t'
                            );

                    case 'u' ->
                            result.append(
                                    parseUnicodeEscape()
                            );

                    default ->
                            throw error(
                                    "지원하지 않는 JSON escape입니다: \\"
                                            + escaped
                            );
                }
            }

            throw error(
                    "JSON 문자열이 종료되지 않았습니다."
            );
        }

        private char parseUnicodeEscape() {

            if (
                    index + 4
                            > source.length()
            ) {

                throw error(
                        "잘못된 Unicode escape입니다."
                );
            }

            String hex =
                    source.substring(
                            index,
                            index + 4
                    );

            index += 4;

            try {

                return (char)
                        Integer.parseInt(
                                hex,
                                16
                        );

            } catch (NumberFormatException exception) {

                throw error(
                        "잘못된 Unicode escape입니다: "
                                + hex
                );
            }
        }

        private Number parseNumber() {

            int start =
                    index;

            if (
                    peek(
                            '-'
                    )
            ) {

                index++;
            }

            consumeDigits();

            if (
                    peek(
                            '.'
                    )
            ) {

                index++;

                consumeDigits();
            }

            if (
                    peek(
                            'e'
                    )
                            ||
                            peek(
                                    'E'
                            )
            ) {

                index++;

                if (
                        peek(
                                '+'
                        )
                                ||
                                peek(
                                        '-'
                                )
                ) {

                    index++;
                }

                consumeDigits();
            }

            String raw =
                    source.substring(
                            start,
                            index
                    );

            try {

                return new BigDecimal(
                        raw
                );

            } catch (NumberFormatException exception) {

                throw error(
                        "잘못된 JSON 숫자입니다: "
                                + raw
                );
            }
        }

        private void consumeDigits() {

            int start =
                    index;

            while (
                    index < source.length()
                            &&
                            Character.isDigit(
                                    source.charAt(
                                            index
                                    )
                            )
            ) {

                index++;
            }

            if (
                    start == index
            ) {

                throw error(
                        "JSON 숫자에 숫자가 필요합니다."
                );
            }
        }

        private void parseLiteral(
                String literal
        ) {

            if (
                    index + literal.length()
                            > source.length()
            ) {

                throw error(
                        "잘못된 JSON literal입니다."
                );
            }

            if (
                    !source.regionMatches(
                            index,
                            literal,
                            0,
                            literal.length()
                    )
            ) {

                throw error(
                        "잘못된 JSON literal입니다."
                );
            }

            index +=
                    literal.length();
        }

        private void expect(
                char expected
        ) {

            skipWhitespace();

            if (
                    index >= source.length()
                            ||
                            source.charAt(
                                    index
                            ) != expected
            ) {

                throw error(
                        "'"
                                + expected
                                + "' 문자가 필요합니다."
                );
            }

            index++;
        }

        private boolean peek(
                char expected
        ) {

            return index < source.length()
                    &&
                    source.charAt(
                            index
                    ) == expected;
        }

        private void skipWhitespace() {

            while (
                    index < source.length()
            ) {

                char current =
                        source.charAt(
                                index
                        );

                if (
                        current != ' '
                                &&
                                current != '\n'
                                &&
                                current != '\r'
                                &&
                                current != '\t'
                ) {

                    break;
                }

                index++;
            }
        }

        private IllegalArgumentException error(
                String message
        ) {

            return new IllegalArgumentException(
                    message
                            + " index="
                            + index
            );
        }
    }
}
