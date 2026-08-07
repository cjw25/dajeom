package com.dajeom.model;

import com.dajeom.type.CalendarType;
import com.dajeom.type.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 * 프론트 요청값에 백엔드 계산 정책을 적용한
 * 명리 계산 전용 출생정보다.
 */
public record NormalizedBirthData(
        CalendarType calendarType,
        LocalDate birthDate,
        LocalTime birthTime,
        LocalDateTime birthDateTime,
        Gender gender,
        boolean leapMonth,
        ZoneId zoneId
) {

    public NormalizedBirthData {
        Objects.requireNonNull(
                calendarType,
                "달력 유형은 필수입니다."
        );

        Objects.requireNonNull(
                birthDate,
                "생년월일은 필수입니다."
        );

        Objects.requireNonNull(
                birthTime,
                "출생시간은 필수입니다."
        );

        Objects.requireNonNull(
                birthDateTime,
                "출생일시는 필수입니다."
        );

        Objects.requireNonNull(
                gender,
                "성별은 필수입니다."
        );

        Objects.requireNonNull(
                zoneId,
                "시간대는 필수입니다."
        );
    }
}