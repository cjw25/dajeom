package com.dajeom;

import com.dajeom.service.OpenAiLifeInterpretationTextGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;
import java.util.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class SajuReadingTests {
    @Autowired MockMvc mvc;
    @MockitoBean OpenAiLifeInterpretationTextGenerator generator;
    private final JsonMapper json=JsonMapper.builder().build();
    static final String API="/api/saju/consultations";
    static final String START="""
        {"name":"종합풀이","birth":{"calendarType":"SOLAR","birthDate":"1990-05-15","birthTime":"10:30","gender":"MALE","leapMonth":false}}
        """;
    @BeforeEach void validChapters(){
        when(generator.generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString())).thenAnswer(invocation->{
            var specs=json.readTree((String)invocation.getArgument(1)).get("chapters");
            List<Map<String,Object>> chapters=new ArrayList<>();
            var keys=json.readTree((String)invocation.getArgument(3)).at("/properties/chapters/items/properties/content/required");
            for(var spec:specs){
                var paragraphs=new LinkedHashMap<String,String>();
                int index=0;
                for(var key:keys){
                    String prefix=index++==0 ? json.readTree((String)invocation.getArgument(1)).get("name").asString()+"님, " : "";
                    paragraphs.put(key.asString(),prefix+key.asString()+"가".repeat(spec.get("id").asString().equals("concern-answer")?170:160));
                }
                chapters.add(Map.of("id",spec.get("id").asString(),"content",paragraphs));
            }
            return json.writeValueAsString(Map.of("chapters",chapters));
        });
    }

    @Test void birthAloneGeneratesAllEighteenChaptersWithNoInitialQuestion() throws Exception {
        var result=mvc.perform(post(API+"/reading").contentType("application/json").content(START))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reading.chapters.length()").value(18))
                .andExpect(jsonPath("$.reading.totalCharacters").value(org.hamcrest.Matchers.greaterThanOrEqualTo(27000)))
                .andExpect(jsonPath("$.messages.length()").value(0)).andExpect(jsonPath("$.turnsRemaining").value(12))
                .andReturn();
        var chapters=json.readTree(result.getResponse().getContentAsString()).get("reading").get("chapters");
        Set<String> groups=new HashSet<>(),ids=new HashSet<>();
        for(var chapter:chapters){groups.add(chapter.get("groupId").asString());ids.add(chapter.get("id").asString());assertEquals("READY",chapter.get("status").asString());}
        assertEquals(7,groups.size());assertEquals(18,ids.size());
        verify(generator,times(18)).generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString());
        verify(generator,never()).generateText(anyString(),anyString(),eq("SAJU_CONSULTATION"));
    }

    @Test void completedReadingCanBeResumedAndQuestionsComeAfterIt() throws Exception {
        var session=new MockHttpSession();
        mvc.perform(post(API+"/reading").session(session).contentType("application/json").content(START)).andExpect(status().isOk());
        mvc.perform(get(API+"/current").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.reading.chapters.length()").value(18));
        when(generator.generateText(anyString(),anyString(),eq("SAJU_CONSULTATION"))).thenReturn("풀이의 내용을 이어서 설명합니다.");
        mvc.perform(post(API+"/messages").session(session).contentType("application/json").content("{\"message\":\"강점 부분을 더 설명해주세요\",\"topic\":\"SELF\",\"depth\":\"SHORT\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reading.chapters.length()").value(18))
                .andExpect(jsonPath("$.messages.length()").value(2)).andExpect(jsonPath("$.turnsRemaining").value(11));
        verify(generator,times(18)).generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString());
    }

    @Test void concernOnlyReachesOneFinalReadingAndFollowup() throws Exception {
        String concern="이직과 생활비 때문에 고민입니다.";
        var data=new LinkedHashMap<String,Object>();
        data.put("name","고민풀이"); data.put("birth",json.readTree(START).get("birth")); data.put("concern",concern);
        var session=new MockHttpSession();
        mvc.perform(post(API+"/reading").session(session).contentType("application/json").content(json.writeValueAsString(data)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.reading.chapters.length()").value(18));
        verify(generator,times(18)).generateStructured(anyString(),argThat(payload -> !json.readTree(payload).has("concern") && json.readTree(payload).path("name").asString().equals("고민풀이")),startsWith("SAJU_READING_"),anyString());
        verify(generator).generateStructured(anyString(),argThat(payload -> json.readTree(payload).path("concern").asString().equals(concern)),eq("SAJU_READING_concern"),anyString());
        mvc.perform(get(API+"/current").session(session)).andExpect(jsonPath("$.reading.concernReading.id").value("concern-answer"));
        when(generator.generateText(anyString(),anyString(),eq("SAJU_CONSULTATION"))).thenReturn("답변입니다.");
        mvc.perform(post(API+"/messages").session(session).contentType("application/json").content("{\"message\":\"어떤 기준으로 결정할까요?\",\"topic\":\"CAREER\",\"depth\":\"STANDARD\"}"))
            .andExpect(status().isOk());
        verify(generator).generateText(anyString(),contains(concern),eq("SAJU_CONSULTATION"));
    }

    @Test void fullNameStaysConsistentInEveryChapterConcernAndFollowup() throws Exception {
        when(generator.generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString())).thenAnswer(call -> {
            var payload=json.readTree((String)call.getArgument(1));
            String id=payload.get("chapters").get(0).get("id").asString();
            String content="최정원님, 정원님께 [[상담자호칭]]의 풀이를 설명합니다."+"나".repeat(id.equals("concern-answer")?2400:1600);
            return json.writeValueAsString(Map.of("chapters",List.of(Map.of("id",id,"content",content))));
        });
        var data=new LinkedHashMap<String,Object>();
        data.put("name","최정원");data.put("birth",json.readTree(START).get("birth"));data.put("concern","이직 고민");
        var session=new MockHttpSession();
        var result=mvc.perform(post(API+"/reading").session(session).contentType("application/json").content(json.writeValueAsString(data))).andExpect(status().isOk()).andReturn();
        var reading=json.readTree(result.getResponse().getContentAsString()).get("reading");
        for(var chapter:reading.get("chapters"))assertTrue(chapter.get("content").asString().startsWith("최정원님, 최정원님께 최정원님의"));
        assertTrue(reading.get("concernReading").get("content").asString().startsWith("최정원님, 최정원님께 최정원님의"));
        when(generator.generateText(anyString(),anyString(),eq("SAJU_CONSULTATION"))).thenReturn("정원님께 이어 설명합니다.");
        mvc.perform(post(API+"/messages").session(session).contentType("application/json").content("{\"message\":\"더 알려주세요\",\"topic\":\"SELF\",\"depth\":\"STANDARD\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.messages[1].text").value("최정원님께 이어 설명합니다."));
    }

    @Test void oversizedConcernIsRejectedBeforeAi() throws Exception {
        var data=new LinkedHashMap<String,Object>();
        data.put("name","검증"); data.put("birth",json.readTree(START).get("birth")); data.put("concern","가".repeat(1001));
        mvc.perform(post(API+"/reading").contentType("application/json").content(json.writeValueAsString(data))).andExpect(status().isBadRequest());
        verifyNoInteractions(generator);
    }

    @Test void aShortDraftIsRewrittenAsOneCompletePersonalReading() throws Exception {
        when(generator.generateStructured(anyString(),anyString(),eq("SAJU_READING_self"),anyString())).thenAnswer(call -> {
            var payload=json.readTree((String)call.getArgument(1));
            String id=payload.get("chapters").get(0).get("id").asString();
            String text=id.equals("self-nature") && !payload.has("previousDraft") ? "종합풀이님, 짧은 초안입니다." : "종합풀이님, 완전히 다시 쓴 개인 풀이입니다."+"나".repeat(1600);
            return json.writeValueAsString(Map.of("chapters",List.of(Map.of("id",id,"content",text))));
        });
        var result=mvc.perform(post(API+"/reading").contentType("application/json").content(START)).andExpect(status().isOk()).andReturn();
        var reading=json.readTree(result.getResponse().getContentAsString()).get("reading");
        String content=reading.get("chapters").get(0).get("content").asString();
        assertTrue(content.startsWith("종합풀이님, 완전히 다시 쓴"));assertFalse(content.contains("짧은 초안"));
        verify(generator,times(19)).generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString());
    }

    @Test void collectiveAddressIsRewrittenInsteadOfReplaced() throws Exception {
        when(generator.generateStructured(anyString(),anyString(),eq("SAJU_READING_self"),anyString())).thenAnswer(call -> {
            var payload=json.readTree((String)call.getArgument(1));
            String id=payload.get("chapters").get(0).get("id").asString();
            String text=payload.has("previousDraft") ? "종합풀이님, 본인의 사주를 설명합니다." : "여러분은 정보를 수집하는 사람입니다.";
            return json.writeValueAsString(Map.of("chapters",List.of(Map.of("id",id,"content",text+"나".repeat(1600)))));
        });
        var result=mvc.perform(post(API+"/reading").contentType("application/json").content(START)).andExpect(status().isOk()).andReturn();
        String content=json.readTree(result.getResponse().getContentAsString()).get("reading").get("chapters").get(0).get("content").asString();
        assertTrue(content.startsWith("종합풀이님"));assertFalse(content.contains("여러분"));
        verify(generator,times(21)).generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString());
    }

    @Test void structuredParagraphsAreWrittenTogetherAndReadInOrder() throws Exception {
        when(generator.generateStructured(anyString(),anyString(),eq("SAJU_READING_self"),anyString())).thenAnswer(call -> {
            var payload=json.readTree((String)call.getArgument(1));
            String id=payload.get("chapters").get(0).get("id").asString();
            var paragraphs=new LinkedHashMap<String,String>();
            for(int i=10;i>=1;i--)paragraphs.put("p%02d".formatted(i),(i==1?"종합풀이님, ":"")+i+"번째 문단입니다. "+"나".repeat(150));
            return json.writeValueAsString(Map.of("chapters",List.of(Map.of("id",id,"content",paragraphs))));
        });
        var result=mvc.perform(post(API+"/reading").contentType("application/json").content(START)).andExpect(status().isOk()).andReturn();
        String content=json.readTree(result.getResponse().getContentAsString()).get("reading").get("chapters").get(0).get("content").asString();
        assertTrue(content.startsWith("종합풀이님, 1번째"));
        assertTrue(content.indexOf("2번째")<content.indexOf("10번째"));
        assertEquals(10,content.split("\\n\\n").length);
    }

    @Test void missingNarrativeParagraphCannotBePublished() throws Exception {
        when(generator.generateStructured(anyString(),anyString(),eq("SAJU_READING_self"),anyString())).thenReturn("{\"chapters\":[{\"id\":\"self-nature\",\"content\":{\"p01\":\"종합풀이님, 초안입니다.\"}}]}");
        mvc.perform(post(API+"/reading").contentType("application/json").content(START)).andExpect(status().isServiceUnavailable());
    }

    @Test void persistentCollectiveVoiceIsNeverPublished() throws Exception {
        when(generator.generateStructured(anyString(),anyString(),eq("SAJU_READING_self"),anyString())).thenAnswer(call -> {
            String id=json.readTree((String)call.getArgument(1)).get("chapters").get(0).get("id").asString();
            return json.writeValueAsString(Map.of("chapters",List.of(Map.of("id",id,"content","여러분에게 드리는 이야기입니다."+"나".repeat(1600)))));
        });
        var session=new MockHttpSession();
        mvc.perform(post(API+"/reading").session(session).contentType("application/json").content(START)).andExpect(status().isServiceUnavailable());
        mvc.perform(get(API+"/current").session(session)).andExpect(status().isNoContent());
    }

    @Test void temporaryProviderFailureRetriesOnlyThatRequest() throws Exception {
        var first=new java.util.concurrent.atomic.AtomicBoolean(true);
        when(generator.generateStructured(anyString(),anyString(),eq("SAJU_READING_self"),anyString())).thenAnswer(call -> {
            if(first.compareAndSet(true,false))throw new IllegalStateException("provider status=520");
            String id=json.readTree((String)call.getArgument(1)).get("chapters").get(0).get("id").asString();
            return json.writeValueAsString(Map.of("chapters",List.of(Map.of("id",id,"content","종합풀이님, 개인 풀이입니다."+"나".repeat(1600)))));
        });
        mvc.perform(post(API+"/reading").contentType("application/json").content(START)).andExpect(status().isOk());
        verify(generator,times(19)).generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString());
    }

    @Test void shortChaptersAreRetriedAndNeverPublishedAsComplete() throws Exception {
        when(generator.generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString())).thenAnswer(call -> {
            String id=json.readTree((String)call.getArgument(1)).get("chapters").get(0).get("id").asString();
            return json.writeValueAsString(Map.of("chapters",List.of(Map.of("id",id,"content","너무 짧은 본문"))));
        });
        var session=new MockHttpSession();
        mvc.perform(post(API+"/reading").session(session).contentType("application/json").content(START)).andExpect(status().isServiceUnavailable());
        mvc.perform(get(API+"/current").session(session)).andExpect(status().isNoContent());
        verify(generator,atLeast(3)).generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString());
    }

    @Test void unknownTimeShowsExplicitUnavailableChaptersWithoutAi() throws Exception {
        mvc.perform(post(API+"/reading").contentType("application/json").content(START.replace("\"10:30\"","null")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reading.chapters.length()").value(18))
                .andExpect(jsonPath("$.reading.chapters[0].status").value("UNAVAILABLE")).andExpect(jsonPath("$.readingAvailable").value(false));
        verifyNoInteractions(generator);
    }

    @Test void malformedChapterOutputDoesNotPublishAPartialBook() throws Exception {
        when(generator.generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString())).thenReturn("{\"chapters\":[]}");
        var session=new MockHttpSession();
        mvc.perform(post(API+"/reading").session(session).contentType("application/json").content(START)).andExpect(status().isServiceUnavailable());
        mvc.perform(get(API+"/current").session(session)).andExpect(status().isNoContent());
    }

    @Test void duplicateChapterIdsAreRejected() throws Exception {
        when(generator.generateStructured(anyString(),anyString(),eq("SAJU_READING_self"),anyString())).thenReturn("""
            {"chapters":[{"id":"self-nature","content":"본문"},{"id":"self-nature","content":"본문"},{"id":"self-balance","content":"본문"}]}
            """);
        mvc.perform(post(API+"/reading").contentType("application/json").content(START)).andExpect(status().isServiceUnavailable());
    }

    @Test void welcomeDoesNotAskUserToChooseAQuestion() throws Exception {
        String html=mvc.perform(get("/saju")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(html.contains("오늘의 질문"));assertFalse(html.contains("id=\"initialQuestion\""));assertFalse(html.contains("id=\"topicGrid\""));
        assertTrue(html.contains("/img/saju_main_banner.png"));assertTrue(html.contains("나의 천명록 펼치기"));
    }
}
