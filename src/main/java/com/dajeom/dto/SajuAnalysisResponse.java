package com.dajeom.dto;

import com.dajeom.type.CalendarType;
import com.dajeom.entity.Gender;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 사주 전체 분석 API 출력값.
 *
 * birthDate:
 * 사용자가 실제 입력한 날짜.
 *
 * solarBirthDate:
 * 만세력 계산에 사용한 양력 변환 날짜.
 */
public record SajuAnalysisResponse(

        CalendarType calendarType,

        /**
         * 사용자 원본 입력일.
         *
         * 양력/음력 여부와 상관없이
         * yyyy-MM-dd 문자열로 보존한다.
         */
        String birthDate,

        /**
         * 실제 만세력 계산에 사용된
         * Gregorian 양력 날짜.
         */
        LocalDate solarBirthDate,

        LocalTime birthTime,

        boolean birthTimeUnknown,

        Gender gender,

        /**
         * 음력 입력 시 윤달 여부.
         */
        boolean leapMonth,

        int targetYear,

        PillarResponse yearPillar,

        PillarResponse monthPillar,

        PillarResponse dayPillar,

        PillarResponse hourPillar,

        String dayMasterKoreanName,

        String dayMasterChineseName,

        String strengthLevel,

        String structureType,

        List<String> yongshinCandidates,

        List<String> huishinCandidates,

        List<String> gishinCandidates,

        List<String> temperatureCandidates,

        List<String> mediationCandidates,

        DecadeLuckResponse decadeLuck,

        AnnualLuckResponse annualLuck,

        List<ConsultationSectionResponse> consultations,

        String fullNarrationText

) {

    public SajuAnalysisResponse {

        yongshinCandidates =
                List.copyOf(
                        yongshinCandidates
                );

        huishinCandidates =
                List.copyOf(
                        huishinCandidates
                );

        gishinCandidates =
                List.copyOf(
                        gishinCandidates
                );

        temperatureCandidates =
                List.copyOf(
                        temperatureCandidates
                );

        mediationCandidates =
                List.copyOf(
                        mediationCandidates
                );

        consultations =
                List.copyOf(
                        consultations
                );
    }
}