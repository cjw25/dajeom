package com.dajeom;

import com.dajeom.hosting.*;
import com.dajeom.service.*;
import com.dajeom.service.SajuReadingService.Chapter;
import com.sun.net.httpserver.HttpServer;
import jakarta.servlet.http.Cookie;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties="dajeom.hosted.enabled=true")
@AutoConfigureMockMvc
class HostedSajuTests {
    static final JsonMapper JSON=JsonMapper.builder().build();
    static final Map<String,Row> DATABASE=new HashMap<>();
    record Row(JsonNode value,Instant expires) {}
    static final AtomicBoolean correctHeaders=new AtomicBoolean(true);
    static final HttpServer SERVER=startServer();
    static final String API="/api/saju/consultations";
    static final String START="""
        {"name":"최정원","birth":{"calendarType":"SOLAR","birthDate":"1990-05-15","birthTime":"10:30","gender":"MALE","leapMonth":false},"concern":"이직 고민"}
        """;
    @Autowired MockMvc mvc;
    @Autowired HostedStateStore store;
    @Autowired SajuService saju;
    @Autowired SajuConsultationService consultations;
    @MockitoBean SajuReadingService readings;
    @MockitoBean OpenAiLifeInterpretationTextGenerator generator;

    @DynamicPropertySource static void properties(DynamicPropertyRegistry registry) {
        registry.add("SUPABASE_URL",()->"http://127.0.0.1:"+SERVER.getAddress().getPort());
        registry.add("SUPABASE_SECRET_KEY",()->"sb_secret_test_only");
    }
    @BeforeEach void setup() {
        synchronized(DATABASE) { DATABASE.clear(); }
        when(readings.generateStep(any(),anyString(),anyString(),anyInt())).thenAnswer(call -> {
            int index=call.getArgument(3);
            if(index==18)return new Chapter("concern-answer","concern","고민","고민 풀이","최정원님의 고민 풀이","READY");
            var specs=SajuReadingService.GROUPS.stream().flatMap(group->group.chapters().stream()).toList();
            var spec=specs.get(index);
            var group=SajuReadingService.GROUPS.stream().filter(item->item.chapters().contains(spec)).findFirst().orElseThrow();
            return new Chapter(spec.id(),group.id(),group.title(),spec.title(),"최정원님의 "+spec.title(),"READY");
        });
        when(generator.generateText(anyString(),anyString(),eq("SAJU_CONSULTATION"))).thenReturn("정원님께 이어 설명합니다.");
    }
    private Cookie begin() throws Exception {
        var response=mvc.perform(post(API+"/reading").contentType("application/json").content(START))
            .andExpect(status().isAccepted()).andExpect(jsonPath("$.completed").value(0)).andReturn().getResponse();
        String header=response.getHeader("Set-Cookie");assertNotNull(header);
        return new Cookie(HostedVisitorFilter.COOKIE,header.split(";",2)[0].split("=",2)[1]);
    }
    private String visitor(Cookie cookie) throws Exception {
        return HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(cookie.getValue().getBytes(StandardCharsets.UTF_8)));
    }
    @Test void chaptersAndConversationSurviveNewServerInstancesAndHttpSessions() throws Exception {
        Cookie cookie=begin();
        verifyNoInteractions(readings,generator);
        mvc.perform(post(API+"/reading/advance").cookie(cookie)).andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(1));
        var restarted=new HostedSajuService(store,saju,readings,consultations);
        assertEquals(1,restarted.status(visitor(cookie)).completed());
        for(int i=1;i<19;i++)mvc.perform(post(API+"/reading/advance").cookie(cookie)).andExpect(status().isOk());
        mvc.perform(get(API+"/current").cookie(cookie)).andExpect(status().isOk())
            .andExpect(jsonPath("$.reading.chapters.length()").value(18)).andExpect(jsonPath("$.reading.concernReading.id").value("concern-answer"));
        verify(readings,times(19)).generateStep(any(),anyString(),anyString(),anyInt());
        // Completed requests are idempotent and don't generate another paid chapter.
        mvc.perform(post(API+"/reading/advance").cookie(cookie)).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("COMPLETE"));
        verify(readings,times(19)).generateStep(any(),anyString(),anyString(),anyInt());
        mvc.perform(post(API+"/messages").cookie(cookie).contentType("application/json").content("{\"message\":\"더 설명해주세요\",\"topic\":\"CAREER\",\"depth\":\"SHORT\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.messages[1].text").value("최정원님께 이어 설명합니다."));
        assertEquals(2,restarted.current(visitor(cookie)).messages().size());
        mvc.perform(get(API+"/current")).andExpect(status().isNoContent());
        assertTrue(correctHeaders.get());
    }
    @Test void failedStepKeepsEarlierChaptersAndRetryStartsAtThatStep() throws Exception {
        Cookie cookie=begin();
        mvc.perform(post(API+"/reading/advance").cookie(cookie)).andExpect(status().isOk());
        when(readings.generateStep(any(),anyString(),anyString(),eq(1))).thenThrow(new IllegalStateException("temporary"));
        mvc.perform(post(API+"/reading/advance").cookie(cookie)).andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("RETRY")).andExpect(jsonPath("$.completed").value(1));
        when(readings.generateStep(any(),anyString(),anyString(),eq(1))).thenReturn(new Chapter("self-strength","self","나","강점","본문","READY"));
        mvc.perform(post(API+"/reading/advance").cookie(cookie)).andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(2));
        verify(readings,times(1)).generateStep(any(),anyString(),anyString(),eq(0));
    }
    @Test void leasesPreventConcurrentWorkAndRejectStaleWriters() throws Exception {
        Cookie cookie=begin();String key="session:"+visitor(cookie);
        assertTrue(store.acquire("lock:"+key,"worker-one",300));
        mvc.perform(post(API+"/reading/advance").cookie(cookie)).andExpect(status().isConflict());
        assertFalse(store.commit(key,"{}",Instant.now().plusSeconds(100),"lock:"+key,"worker-two"));
        store.release("lock:"+key,"worker-two");
        assertFalse(store.acquire("lock:"+key,"worker-three",300));
        store.release("lock:"+key,"worker-one");
        assertTrue(store.acquire("lock:"+key,"worker-three",300));
        verifyNoInteractions(readings,generator);
    }
    @Test void expiryClearingAndOriginChecksProtectStoredBirthData() throws Exception {
        Cookie cookie=begin();String key="session:"+visitor(cookie);
        mvc.perform(post(API+"/reading/advance").cookie(cookie).header("Origin","https://evil.example")).andExpect(status().isForbidden());
        mvc.perform(delete(API+"/current").cookie(cookie)).andExpect(status().isNoContent());
        mvc.perform(get(API+"/reading/progress").cookie(cookie)).andExpect(status().isNoContent());
        assertTrue(store.acquire("lock:"+key,"expired",300));
        assertTrue(store.commit(key,"{}",Instant.now().minusSeconds(1),"lock:"+key,"expired"));
        assertNull(store.get(key));
    }
    @Test void legacyGenerationAndDailyOveruseAreBlockedBeforeAi() throws Exception {
        mvc.perform(post("/api/saju/calculate").contentType("application/json").content("{}")).andExpect(status().isNotFound());
        assertTrue(store.consume("test-budget",1));assertFalse(store.consume("test-budget",1));
        verifyNoInteractions(readings,generator);
    }
    @AfterAll static void stop() { SERVER.stop(0); }

    private static HttpServer startServer() {
        try {
            var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
            server.createContext("/rest/v1/",exchange->{
                correctHeaders.compareAndSet(true,"sb_secret_test_only".equals(exchange.getRequestHeaders().getFirst("apikey")) && exchange.getRequestHeaders().getFirst("Authorization")==null);
                Object result=null;
                synchronized(DATABASE) {
                    String path=exchange.getRequestURI().getPath();
                    if(path.endsWith("dajeom_state")) {
                        var parameters=new HashMap<String,String>();
                        for(String pair:exchange.getRequestURI().getRawQuery().split("&")) {
                            var parts=pair.split("=",2);parameters.put(parts[0],URLDecoder.decode(parts[1],StandardCharsets.UTF_8));
                        }
                        String key=parameters.get("key").substring(3);
                        if(exchange.getRequestMethod().equals("DELETE"))DATABASE.remove(key);
                        else { var row=DATABASE.get(key);result=row==null || !row.expires().isAfter(Instant.now())?List.of():List.of(Map.of("value",row.value())); }
                    } else {
                        var input=JSON.readTree(new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8));
                        String key=input.get("p_key").asString();var row=DATABASE.get(key);
                        if(path.endsWith("dajeom_lock_acquire")) {
                            boolean available=row==null || !row.expires().isAfter(Instant.now());
                            if(available)DATABASE.put(key,new Row(JSON.valueToTree(Map.of("owner",input.get("p_owner").asString())),Instant.now().plusSeconds(input.get("p_seconds").asInt())));
                            result=available;
                        } else if(path.endsWith("dajeom_state_commit")) {
                            var lease=DATABASE.get(input.get("p_lock_key").asString());
                            boolean valid=lease!=null && lease.expires().isAfter(Instant.now()) && lease.value().get("owner").asString().equals(input.get("p_owner").asString());
                            if(valid)DATABASE.put(key,new Row(input.get("p_value"),Instant.parse(input.get("p_expires_at").asString())));
                            result=valid;
                        } else if(path.endsWith("dajeom_state_delete")) {
                            var lease=DATABASE.get(input.get("p_lock_key").asString());
                            boolean valid=lease!=null && lease.expires().isAfter(Instant.now()) && lease.value().get("owner").asString().equals(input.get("p_owner").asString());
                            if(valid)DATABASE.remove(key);
                            result=valid;
                        } else if(path.endsWith("dajeom_lock_release")) {
                            if(row!=null && row.value().get("owner").asString().equals(input.get("p_owner").asString()))DATABASE.remove(key);
                        } else if(path.endsWith("dajeom_budget_consume")) {
                            int count=row==null?0:row.value().get("count").asInt();boolean allowed=count<input.get("p_limit").asInt();
                            if(allowed)DATABASE.put(key,new Row(JSON.valueToTree(Map.of("count",count+1)),Instant.now().plusSeconds(86400)));
                            result=allowed;
                        }
                    }
                }
                byte[] body=JSON.writeValueAsBytes(result);exchange.getResponseHeaders().set("Content-Type","application/json");
                exchange.sendResponseHeaders(200,body.length);exchange.getResponseBody().write(body);exchange.close();
            });server.start();return server;
        } catch(java.io.IOException failure) { throw new ExceptionInInitializerError(failure); }
    }
}
