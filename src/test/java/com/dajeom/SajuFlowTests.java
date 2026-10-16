package com.dajeom;

import com.dajeom.domain.CalendarType;
import com.dajeom.domain.Gender;
import com.dajeom.domain.LifeInterpretationPrompt;
import com.dajeom.dto.SajuRequest;
import com.dajeom.service.OpenAiLifeInterpretationTextGenerator;
import com.dajeom.service.SajuService;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SajuFlowTests {
    @Autowired SajuService service;
    @MockitoBean OpenAiLifeInterpretationTextGenerator generator;
    @Autowired MockMvc mvc;

    @Test void solarBirthProducesSevenGroundedInterpretations() {
        when(generator.generate(any(LifeInterpretationPrompt.class))).thenReturn("검증용 풀이 본문");
        var result = service.calculate(new SajuRequest(CalendarType.SOLAR, "1990-05-15", LocalTime.of(10,30), Gender.MALE, false));
        assertEquals(7, result.interpretations().size());
        var elements = result.fiveElements();
        assertEquals(8, elements.wood()+elements.fire()+elements.earth()+elements.metal()+elements.water());
    }

    @ParameterizedTest
    @CsvSource({"SOLAR,1985-01-01,FEMALE,true", "SOLAR,2000-02-29,MALE,false", "LUNAR,1990-01-01,FEMALE,false", "SOLAR,1970-12-31,MALE,true"})
    void differentBirthsAndUnknownTimesComplete(CalendarType calendar, String birthDate, Gender gender, boolean unknownTime) {
        when(generator.generate(any(LifeInterpretationPrompt.class))).thenReturn("검증용 풀이 본문");
        var result = service.calculate(new SajuRequest(calendar, birthDate, unknownTime ? null : LocalTime.of(23,30), gender, false));
        assertEquals(7, result.interpretations().size());
        assertEquals(unknownTime, result.pillars().hour() == null);
        if (unknownTime) {
            assertTrue(result.interpretations().stream().allMatch(item -> item.status().equals("UNAVAILABLE")));
            org.mockito.Mockito.verifyNoInteractions(generator);
        }
        var elements = result.fiveElements();
        assertEquals(unknownTime ? 6 : 8, elements.wood()+elements.fire()+elements.earth()+elements.metal()+elements.water());
    }

    @ParameterizedTest
    @CsvSource({"1899-01-01", "2099-01-01", "2000-02-30", "invalid"})
    void invalidBirthsAreRejectedBeforeGeneration(String date) {
        assertThrows(IllegalArgumentException.class, () -> service.calculate(new SajuRequest(CalendarType.SOLAR,date,null,Gender.MALE,false)));
        org.mockito.Mockito.verifyNoInteractions(generator);
    }

    @Test void pagesAndInvalidRequestsAreHandled() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/saju")).andExpect(status().isOk());
        mvc.perform(get("/tarot")).andExpect(status().isOk());
        mvc.perform(post("/api/saju/calculate").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").isNotEmpty());
        mvc.perform(post("/api/tarot/reading").contentType("application/json").content("{\"spreadId\":\"one_card\",\"cards\":[{\"id\":99}]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/tarot/reading").contentType("application/json").content("{\"spreadId\":\"three_card\",\"cards\":[{\"id\":0},{\"id\":0},{\"id\":1}]}"))
                .andExpect(status().isBadRequest());
        org.mockito.Mockito.verifyNoInteractions(generator);
    }

    @Test void tarotUsesSelectedCardsAndDirections() throws Exception {
        when(generator.generateText(any(String.class),any(String.class),org.mockito.ArgumentMatchers.eq("TAROT")))
                .thenReturn("마법사 카드의 역방향을 돌아보며 준비를 점검해보세요.");
        mvc.perform(post("/api/tarot/reading").contentType("application/json")
                .content("{\"spreadId\":\"one_card\",\"cards\":[{\"id\":1,\"reversed\":true}]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.cards[0].name").value("마법사"))
                .andExpect(jsonPath("$.cards[0].reversed").value(true)).andExpect(jsonPath("$.content").isNotEmpty());
    }

    @Test void knownTimesAcrossMonthsDoNotBreakTheRulePipeline() {
        when(generator.generate(any(LifeInterpretationPrompt.class))).thenReturn("검증용 풀이 본문");
        for (int month=1; month<=12; month++) {
            for (Gender gender : Gender.values()) {
                String date = String.format("%04d-%02d-15", 1960 + month*3, month);
                var result = service.calculate(new SajuRequest(CalendarType.SOLAR,date,LocalTime.of(month,15),gender,false));
                assertEquals(7, result.interpretations().size(),date+" "+gender);
            }
        }
    }
}
