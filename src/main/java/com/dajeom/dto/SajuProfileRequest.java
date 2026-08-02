package com.dajeom.dto;

import com.dajeom.entity.CalendarType;
import com.dajeom.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record SajuProfileRequest(

        @NotBlank(message = "이름을 입력해주세요.")
        @Size(
                min = 2,
                max = 20,
                message = "이름은 2자 이상 20자 이하로 입력해주세요."
        )
        String name,

        @NotNull(message = "성별을 선택해주세요.")
        Gender gender,

        @NotNull(message = "양력 또는 음력을 선택해주세요.")
        CalendarType calendarType,

        boolean lunarLeapMonth,

        @NotNull(message = "생년월일을 입력해주세요.")
        @PastOrPresent(
                message = "생년월일은 오늘 이후일 수 없습니다."
        )
        LocalDate birthDate,

        LocalTime birthTime,

        boolean birthTimeUnknown
) {
}