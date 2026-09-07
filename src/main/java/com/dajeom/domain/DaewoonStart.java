package com.dajeom.domain;

import com.dajeom.solar.SolarTerm;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 대운 시작거리 및 시작시점 계산 결과.
 *
 * 원본 절입거리와
 * 전통 표시용 대운수,
 * 정밀 첫대운 시작시점을 모두 분리해서 보존한다.
 */
public record DaewoonStart(
        DaewoonDirection direction,

        SolarTerm referenceTerm,

        Instant referenceInstant,
        LocalDateTime referenceKoreaDateTime,

        Instant birthInstant,
        LocalDateTime birthKoreaDateTime,

        long distanceMillis,

        BigDecimal preciseStartAgeYears,

        int daewoonNumber,

        int displayStartYears,
        int displayStartMonths,
        int displayStartDays,

        Instant firstDaewoonInstant,
        LocalDateTime firstDaewoonKoreaDateTime,

        LocalDate firstDaewoonApproxDate
) {

    public DaewoonStart {

        Objects.requireNonNull(
                direction,
                "대운 방향이 필요합니다."
        );

        Objects.requireNonNull(
                referenceTerm,
                "대운 시작거리 기준 절기가 필요합니다."
        );

        Objects.requireNonNull(
                referenceInstant,
                "기준 절입 순간이 필요합니다."
        );

        Objects.requireNonNull(
                referenceKoreaDateTime,
                "기준 절입 한국 시간이 필요합니다."
        );

        Objects.requireNonNull(
                birthInstant,
                "출생 순간이 필요합니다."
        );

        Objects.requireNonNull(
                birthKoreaDateTime,
                "출생 한국 시간이 필요합니다."
        );

        Objects.requireNonNull(
                preciseStartAgeYears,
                "정밀 대운 시작 나이가 필요합니다."
        );

        Objects.requireNonNull(
                firstDaewoonInstant,
                "첫대운 시작 순간이 필요합니다."
        );

        Objects.requireNonNull(
                firstDaewoonKoreaDateTime,
                "첫대운 한국 시간이 필요합니다."
        );

        Objects.requireNonNull(
                firstDaewoonApproxDate,
                "첫대운 표시 날짜가 필요합니다."
        );

        if (distanceMillis < 0) {

            throw new IllegalArgumentException(
                    "대운 시작거리는 음수가 될 수 없습니다."
            );
        }

        if (preciseStartAgeYears.signum() < 0) {

            throw new IllegalArgumentException(
                    "정밀 대운 시작 나이는 음수가 될 수 없습니다."
            );
        }

        if (!referenceTerm.isMonthBoundary()) {

            throw new IllegalArgumentException(
                    "대운 시작거리 기준은 12절입이어야 합니다. term="
                            + referenceTerm
            );
        }

        if (daewoonNumber < 0) {

            throw new IllegalArgumentException(
                    "대운수는 음수가 될 수 없습니다."
            );
        }

        if (displayStartYears < 0) {

            throw new IllegalArgumentException(
                    "대운 시작 년수는 음수가 될 수 없습니다."
            );
        }

        if (displayStartMonths < 0
                || displayStartMonths > 11) {

            throw new IllegalArgumentException(
                    "대운 시작 개월수는 0~11이어야 합니다."
            );
        }

        if (displayStartDays < 0
                || displayStartDays > 29) {

            throw new IllegalArgumentException(
                    "대운 시작 일수는 0~29이어야 합니다."
            );
        }
    }
}