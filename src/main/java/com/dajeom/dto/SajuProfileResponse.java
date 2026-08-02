package com.dajeom.dto;

import com.dajeom.entity.CalendarType;
import com.dajeom.entity.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record SajuProfileResponse(

        Long profileId,

        String name,

        Gender gender,

        CalendarType calendarType,

        boolean lunarLeapMonth,

        LocalDate birthDate,

        LocalTime birthTime,

        boolean birthTimeUnknown,

        LocalDateTime createdAt,

        String message
) {
}