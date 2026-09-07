package com.dajeom.dto;

import com.dajeom.domain.CalendarType;
import com.dajeom.domain.Gender;

import java.time.LocalTime;

public record SajuRequest(
        CalendarType calendarType,
        String birthDate,
        LocalTime birthTime,
        Gender gender,
        boolean leapMonth
) {
}