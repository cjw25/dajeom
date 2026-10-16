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
            List<Map<String,String>> chapters=new ArrayList<>();
            for(var spec:specs)chapters.add(Map.of("id",spec.get("id").asString(),"content",spec.get("title").asString()+"의 테스트 본문입니다. 계산된 재료로 구성한 독립적인 풀이입니다."));
            return json.writeValueAsString(Map.of("chapters",chapters));
        });
    }

    @Test void birthAloneGeneratesAllEighteenChaptersWithNoInitialQuestion() throws Exception {
        var result=mvc.perform(post(API+"/reading").contentType("application/json").content(START))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reading.chapters.length()").value(18))
                .andExpect(jsonPath("$.messages.length()").value(0)).andExpect(jsonPath("$.turnsRemaining").value(12))
                .andReturn();
        var chapters=json.readTree(result.getResponse().getContentAsString()).get("reading").get("chapters");
        Set<String> groups=new HashSet<>(),ids=new HashSet<>();
        for(var chapter:chapters){groups.add(chapter.get("groupId").asString());ids.add(chapter.get("id").asString());assertEquals("READY",chapter.get("status").asString());}
        assertEquals(7,groups.size());assertEquals(18,ids.size());
        verify(generator,times(7)).generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString());
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
        verify(generator,times(7)).generateStructured(anyString(),anyString(),startsWith("SAJU_READING_"),anyString());
    }

    @Test void concernReachesEveryGroupAndFollowup() throws Exception {
        String concern="이직과 생활비 때문에 고민입니다.";
        var data=new LinkedHashMap<String,Object>();
        data.put("name","고민풀이"); data.put("birth",json.readTree(START).get("birth")); data.put("concern",concern);
        var session=new MockHttpSession();
        mvc.perform(post(API+"/reading").session(session).contentType("application/json").content(json.writeValueAsString(data)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.reading.chapters.length()").value(18));
        verify(generator,times(7)).generateStructured(anyString(),argThat(payload -> json.readTree(payload).path("concern").asString().equals(concern)),startsWith("SAJU_READING_"),anyString());
        when(generator.generateText(anyString(),anyString(),eq("SAJU_CONSULTATION"))).thenReturn("답변입니다.");
        mvc.perform(post(API+"/messages").session(session).contentType("application/json").content("{\"message\":\"어떤 기준으로 결정할까요?\",\"topic\":\"CAREER\",\"depth\":\"STANDARD\"}"))
            .andExpect(status().isOk());
        verify(generator).generateText(anyString(),contains(concern),eq("SAJU_CONSULTATION"));
    }

    @Test void oversizedConcernIsRejectedBeforeAi() throws Exception {
        var data=new LinkedHashMap<String,Object>();
        data.put("name","검증"); data.put("birth",json.readTree(START).get("birth")); data.put("concern","가".repeat(1001));
        mvc.perform(post(API+"/reading").contentType("application/json").content(json.writeValueAsString(data))).andExpect(status().isBadRequest());
        verifyNoInteractions(generator);
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
