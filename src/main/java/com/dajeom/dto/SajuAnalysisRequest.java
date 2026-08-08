package com.dajeom.dto;

import com.dajeom.type.CalendarType;
import com.dajeom.type.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalTime;

/**
 * 사주 분석 API 입력값.
 *
 * birthDate는 양력/음력을 모두 표현할 수 있도록
 * 문자열 yyyy-MM-dd 형식으로 받는다.
 *
 * 실제 날짜 검증은 달력 종류에 따라
 * SajuAnalysisService에서 수행한다.
 */
public record SajuAnalysisRequest(

        @NotNull(
                message = "양력/음력 구분은 필수입니다."
        )
        CalendarType calendarType,

        @NotBlank(
                message = "생년월일은 필수입니다."
        )
        @Pattern(
                regexp = "^\\d{4}-\\d{2}-\\d{2}$",
                message = "생년월일은 yyyy-MM-dd 형식이어야 합니다."
        )
        String birthDate,

        LocalTime birthTime,

        boolean birthTimeUnknown,

        @NotNull(
                message = "성별은 필수입니다."
        )
        Gender gender,

        /**
         * calendarType = LUNAR일 때:
         *
         * false = 평달
         * true = 윤달
         *
         * SOLAR에서는 반드시 false.
         */
        boolean leapMonth,

        @NotNull(
                message = "분석 대상 연도는 필수입니다."
        )
        Integer targetYear

) {
}