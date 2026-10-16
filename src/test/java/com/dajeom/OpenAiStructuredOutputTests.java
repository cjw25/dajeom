package com.dajeom;

import com.dajeom.service.OpenAiLifeInterpretationTextGenerator;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.*;

class OpenAiStructuredOutputTests {
    @Test void readingSchemaIsSentThroughTheResponsesApiWithoutChangingPlainTextCalls() throws Exception {
        var json=JsonMapper.builder().build();
        var captured=new AtomicReference<String>();
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/v1/responses",exchange->{
            captured.set(new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8));
            byte[] body=json.writeValueAsBytes(Map.of("status","completed","output",java.util.List.of(Map.of("type","message","content",java.util.List.of(Map.of("type","output_text","text","{\"chapters\":[]}"))))));
            exchange.getResponseHeaders().set("Content-Type","application/json");exchange.sendResponseHeaders(200,body.length);exchange.getResponseBody().write(body);exchange.close();
        });
        server.start();
        try {
            var generator=new OpenAiLifeInterpretationTextGenerator("test-only","gpt-4o-mini","http://127.0.0.1:"+server.getAddress().getPort(),5);
            String schema="{\"type\":\"object\",\"properties\":{\"chapters\":{\"type\":\"array\"}},\"required\":[\"chapters\"],\"additionalProperties\":false}";
            assertEquals("{\"chapters\":[]}",generator.generateStructured("JSON으로 작성","장 목록","TEST",schema));
            var structured=json.readTree(captured.get());
            assertEquals("json_schema",structured.at("/text/format/type").asString());
            assertTrue(structured.at("/text/format/strict").asBoolean());
            assertEquals("object",structured.at("/text/format/schema/type").asString());
            generator.generateText("일반 텍스트","풀이","TEST");
            assertFalse(json.readTree(captured.get()).has("text"));
        } finally { server.stop(0); }
    }
}
