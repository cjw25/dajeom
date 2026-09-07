package com.dajeom.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

public record BirthDateTime(
        CalendarType calendarType,
        LocalDate birthDate,
        LocalTime birthTime,
        Gender gender,
        boolean leapMonth
) {

    public BirthDateTime {

        Objects.requireNonNull(
                calendarType,
                "양력/음력 구분이 필요합니다."
        );

        Objects.requireNonNull(
                birthDate,
                "출생일이 필요합니다."
        );

        Objects.requireNonNull(
                gender,
                "성별이 필요합니다."
        );

        if (calendarType.isSolar() && leapMonth) {
            throw new IllegalArgumentException(
                    "양력 날짜에는 윤달을 지정할 수 없습니다."
            );
        }
    }

    public boolean hasKnownTime() {
        return birthTime != null;
    }

    public boolean hasUnknownTime() {
        return birthTime == null;
    }

    public Optional<LocalTime> optionalBirthTime() {
        return Optional.ofNullable(
                birthTime
        );
    }

    public LocalDateTime requireDateTime() {

        if (birthTime == null) {
            throw new IllegalStateException(
                    "출생시간을 알 수 없어 LocalDateTime을 만들 수 없습니다."
            );
        }

        return LocalDateTime.of(
                birthDate,
                birthTime
        );
    }

    public boolean isSolar() {
        return calendarType.isSolar();
    }

    public boolean isLunar() {
        return calendarType.isLunar();
    }
}