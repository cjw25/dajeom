package com.dajeom.dto;

import com.dajeom.type.CalendarType;
import com.dajeom.type.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 사주 계산 요청 객체다.
 */
public record SajuAnalysisRequest(

        @NotNull(
                message = "양력 또는 음력을 선택해 주세요."
        )
        CalendarType calendarType,

        @NotNull(
                message = "생년월일을 입력해 주세요."
        )
        @PastOrPresent(
                message = "생년월일은 미래일 수 없습니다."
        )
        LocalDate birthDate,

        @NotNull(
                message = "출생시간을 입력해 주세요."
        )
        LocalTime birthTime,

        @NotNull(
                message = "성별을 선택해 주세요."
        )
        Gender gender,

        Boolean leapMonth,

        @Min(
                value = 1900,
                message = "대상 연도는 1900년 이상이어야 합니다."
        )
        @Max(
                value = 2099,
                message = "대상 연도는 2099년 이하여야 합니다."
        )
        Integer targetYear
) {
}