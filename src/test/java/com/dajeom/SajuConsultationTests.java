package com.dajeom;

import com.dajeom.service.OpenAiLifeInterpretationTextGenerator;
import com.dajeom.service.SajuConsultationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SajuConsultationTests {
    @Autowired MockMvc mvc;
    @MockitoBean OpenAiLifeInterpretationTextGenerator generator;
    static final String API="/api/saju/consultations";
    static final String START="""
        {"name":"테스트","birth":{"calendarType":"SOLAR","birthDate":"1990-05-15","birthTime":"10:30","gender":"MALE","leapMonth":false},
        "topic":"CAREER","depth":"STANDARD","situation":"직장 3년 차입니다.","question":"새 일을 준비할 때 무엇을 돌아보면 좋을까요?"}
        """;
    @BeforeEach void answer() {
        when(generator.generateText(anyString(),anyString(),eq("SAJU_CONSULTATION"))).thenReturn("나의 패턴을 이해하며 작은 변화를 준비해보세요.\n\n사주에서 읽은 패턴\n일을 이어가는 방식에 주목해보세요.\n\n오늘 해볼 작은 행동\n내가 중요하게 생각하는 업무 조건을 세 가지 적어보세요.");
    }

    @Test void startIsGroundedAndUsesOneCallInsteadOfSeven() throws Exception {
        mvc.perform(post(API).contentType("application/json").content(START))
                .andExpect(status().isOk()).andExpect(jsonPath("$.messages.length()").value(2))
                .andExpect(jsonPath("$.turnsRemaining").value(11)).andExpect(jsonPath("$.readingAvailable").value(true))
                .andExpect(jsonPath("$.pillars.day.stem").value("庚"));
        var system=ArgumentCaptor.forClass(String.class);
        var user=ArgumentCaptor.forClass(String.class);
        verify(generator,times(1)).generateText(system.capture(),user.capture(),eq("SAJU_CONSULTATION"));
        assertTrue(system.getValue().contains("확정 해석 재료"));
        assertTrue(system.getValue().contains("세운·월운"));
        assertFalse(system.getValue().contains("[최종 작성 요구]"));
        assertTrue(user.getValue().contains("직장 3년 차"));
        verify(generator,never()).generate(any());
    }

    @Test void followupsRememberServerHistoryAndCanChangeTopicAndDepth() throws Exception {
        var session=new MockHttpSession();
        mvc.perform(post(API).session(session).contentType("application/json").content(START)).andExpect(status().isOk());
        mvc.perform(post(API+"/messages").session(session).contentType("application/json")
                .content("{\"message\":\"연애에서도 비슷한가요?\",\"topic\":\"LOVE\",\"depth\":\"SHORT\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.messages.length()").value(4))
                .andExpect(jsonPath("$.topic").value("LOVE")).andExpect(jsonPath("$.depth").value("SHORT"));
        var system=ArgumentCaptor.forClass(String.class); var user=ArgumentCaptor.forClass(String.class);
        verify(generator,times(2)).generateText(system.capture(),user.capture(),eq("SAJU_CONSULTATION"));
        assertTrue(user.getAllValues().get(1).contains("새 일을 준비할 때"));
        assertTrue(system.getAllValues().get(1).contains("250~450자"));
        mvc.perform(get(API+"/current").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.messages.length()").value(4));
        mvc.perform(get(API+"/current").session(new MockHttpSession())).andExpect(status().isNoContent());
    }

    @Test void failuresDoNotConsumeTurnsOrLeavePartialMessages() throws Exception {
        var session=new MockHttpSession();
        mvc.perform(post(API).session(session).contentType("application/json").content(START)).andExpect(status().isOk());
        when(generator.generateText(anyString(),anyString(),eq("SAJU_CONSULTATION"))).thenThrow(new IllegalStateException("upstream unavailable"));
        var request="{\"message\":\"더 알려주세요\",\"topic\":\"CAREER\",\"depth\":\"STANDARD\"}";
        mvc.perform(post(API+"/messages").session(session).contentType("application/json").content(request)).andExpect(status().isServiceUnavailable());
        mvc.perform(get(API+"/current").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.length()").value(2)).andExpect(jsonPath("$.turnsRemaining").value(11));
        answer();
        mvc.perform(post(API+"/messages").session(session).contentType("application/json").content(request)).andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.length()").value(4));
    }

    @Test void unknownTimeDoesNotCallAiAndCanStillShowSixElements() throws Exception {
        mvc.perform(post(API).contentType("application/json").content(START.replace("\"10:30\"","null")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.readingAvailable").value(false))
                .andExpect(jsonPath("$.availableTopics.length()").value(0)).andExpect(jsonPath("$.pillars.hour").isEmpty());
        verifyNoInteractions(generator);
    }

    @Test void staleSessionAndInvalidInputDoNotInvokeAi() throws Exception {
        mvc.perform(post(API+"/messages").contentType("application/json").content("{\"message\":\"질문\",\"topic\":\"SELF\",\"depth\":\"SHORT\"}"))
                .andExpect(status().isGone());
        mvc.perform(post(API).contentType("application/json").content(START.replace("\"CAREER\"","\"UNSUPPORTED\""))).andExpect(status().isBadRequest());
        mvc.perform(post(API).contentType("application/json").content(START.replace("직장 3년 차입니다.","가".repeat(1001)))).andExpect(status().isBadRequest());
        mvc.perform(post(API).contentType("application/json").content("{}" )).andExpect(status().isBadRequest());
        verifyNoInteractions(generator);
    }

    @Test void fullReportIsLazyAndCachedInTheSameSession() throws Exception {
        var session=new MockHttpSession();
        mvc.perform(post(API).session(session).contentType("application/json").content(START)).andExpect(status().isOk());
        verify(generator,never()).generate(any());
        when(generator.generate(any())).thenReturn("실제 생성 결과를 대신하는 테스트 본문입니다.");
        mvc.perform(post(API+"/report").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.interpretations.length()").value(7));
        mvc.perform(post(API+"/report").session(session)).andExpect(status().isOk());
        verify(generator,times(7)).generate(any());
        mvc.perform(delete(API+"/current").session(session)).andExpect(status().isNoContent());
        mvc.perform(get(API+"/current").session(session)).andExpect(status().isNoContent());
    }

    @Test void turnLimitStopsFurtherCallsAndKeepsTranscript() throws Exception {
        var session=new MockHttpSession();
        mvc.perform(post(API).session(session).contentType("application/json").content(START)).andExpect(status().isOk());
        for(int turn=0;turn<11;turn++) mvc.perform(post(API+"/messages").session(session).contentType("application/json")
                .content("{\"message\":\"지금 무엇을 해볼까요?\",\"topic\":\"SELF\",\"depth\":\"SHORT\"}")).andExpect(status().isOk());
        mvc.perform(post(API+"/messages").session(session).contentType("application/json")
                .content("{\"message\":\"한 번 더\",\"topic\":\"SELF\",\"depth\":\"SHORT\"}")).andExpect(status().isTooManyRequests());
        verify(generator,times(12)).generateText(anyString(),anyString(),eq("SAJU_CONSULTATION"));
    }

    @Test void pastFlowWithoutAnyPastPeriodsNeverInvokesAi() throws Exception {
        String recent=java.time.LocalDate.now(java.time.ZoneId.of("Asia/Seoul")).minusDays(2).toString();
        mvc.perform(post(API).contentType("application/json").content(START.replace("1990-05-15",recent).replace("\"CAREER\"","\"PAST\"")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.messages[1].text").value(org.hamcrest.Matchers.containsString("근거가 충분하지")));
        verifyNoInteractions(generator);
    }
}
