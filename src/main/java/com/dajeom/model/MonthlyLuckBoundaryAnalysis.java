package com.dajeom.model;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 15차-1.
 *
 * 하나의 월운이 실제로 작동하는 절입 시간구간.
 *
 * =================================================
 *
 * 월운은 양력 1일을 기준으로 하지 않는다.
 *
 * 시작:
 * 해당 월의 절입 실제 시각
 *
 * 종료:
 * 다음 절입 실제 시각
 *
 * =================================================
 *
 * 범위:
 *
 * [start, end)
 *
 * 즉 시작 절입 순간은 현재 월운에 포함되고,
 * 다음 절입 순간부터 다음 월운으로 넘어간다.
 *
 * =================================================
 *
 * monthIndex:
 *
 * 0  = 입춘 월
 * 1  = 경칩 월
 * 2  = 청명 월
 * 3  = 입하 월
 * 4  = 망종 월
 * 5  = 소서 월
 * 6  = 입추 월
 * 7  = 백로 월
 * 8  = 한로 월
 * 9  = 입동 월
 * 10 = 대설 월
 * 11 = 소한 월
 *
 * 실제 월지와 월간 계산은 15-2에서 처리한다.
 */
public record MonthlyLuckBoundaryAnalysis(

        int sajuYear,

        int monthIndex,

        ZoneId zoneId,

        SolarTermPoint startBoundaryPoint,

        SolarTermPoint endBoundaryPoint

) {

    private static final int MONTH_COUNT = 12;

    public MonthlyLuckBoundaryAnalysis {

        if (
                sajuYear < 1
        ) {

            throw new IllegalArgumentException(
                    "월운 명리연도는 1 이상이어야 합니다."
            );
        }

        if (
                monthIndex < 0
                        || monthIndex >= MONTH_COUNT
        ) {

            throw new IllegalArgumentException(
                    "월운 monthIndex는 0~11이어야 합니다: "
                            + monthIndex
            );
        }

        Objects.requireNonNull(
                zoneId,
                "월운 경계 분석에는 ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                startBoundaryPoint,
                "월운 시작 절입이 필요합니다."
        );

        Objects.requireNonNull(
                endBoundaryPoint,
                "월운 종료 절입이 필요합니다."
        );

        /*
         * =================================================
         * 시작 절입의 monthIndex 검증
         * =================================================
         */

        if (
                startBoundaryPoint.monthIndex()
                        != monthIndex
        ) {

            throw new IllegalArgumentException(
                    "월운 monthIndex와 시작 절입 monthIndex가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 다음 절입 순서 검증
         * =================================================
         */

        int expectedNextMonthIndex =
                Math.floorMod(
                        monthIndex + 1,
                        MONTH_COUNT
                );

        if (
                endBoundaryPoint.monthIndex()
                        != expectedNextMonthIndex
        ) {

            throw new IllegalArgumentException(
                    "월운 종료 절입이 다음 monthIndex가 아닙니다. "
                            + "현재="
                            + monthIndex
                            + ", 종료="
                            + endBoundaryPoint.monthIndex()
            );
        }

        /*
         * =================================================
         * Zone 검증
         * =================================================
         */

        if (
                !startBoundaryPoint
                        .dateTime()
                        .getZone()
                        .equals(
                                zoneId
                        )
        ) {

            throw new IllegalArgumentException(
                    "월운 시작 절입 ZoneId가 분석 ZoneId와 일치하지 않습니다."
            );
        }

        if (
                !endBoundaryPoint
                        .dateTime()
                        .getZone()
                        .equals(
                                zoneId
                        )
        ) {

            throw new IllegalArgumentException(
                    "월운 종료 절입 ZoneId가 분석 ZoneId와 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 시간 순서
         * =================================================
         */

        if (
                !endBoundaryPoint
                        .dateTime()
                        .isAfter(
                                startBoundaryPoint.dateTime()
                        )
        ) {

            throw new IllegalArgumentException(
                    "월운 종료 절입은 시작 절입보다 늦어야 합니다."
            );
        }

        /*
         * 한 절입월은 대략 30일 전후다.
         *
         * 정확한 일수를 강제하면 안 되지만
         * 데이터 오류만 방어하기 위해
         * 넓은 범위로 검증한다.
         */

        long durationDays =
                Duration.between(
                                startBoundaryPoint.dateTime(),
                                endBoundaryPoint.dateTime()
                        )
                        .toDays();

        if (
                durationDays < 25
                        || durationDays > 35
        ) {

            throw new IllegalArgumentException(
                    "월운 절입 간격이 비정상적입니다: "
                            + durationDays
                            + "일"
            );
        }
    }

    /**
     * 1~12 순번.
     *
     * 1 = 입춘월
     * ...
     * 12 = 소한월
     */
    public int monthOrder() {

        return monthIndex + 1;
    }

    public ZonedDateTime startDateTimeInclusive() {

        return startBoundaryPoint
                .dateTime();
    }

    public ZonedDateTime endDateTimeExclusive() {

        return endBoundaryPoint
                .dateTime();
    }

    public Duration duration() {

        return Duration.between(
                startDateTimeInclusive(),
                endDateTimeExclusive()
        );
    }

    /**
     * 특정 시각이 현재 월운에 속하는가.
     */
    public boolean contains(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
        ) {

            return false;
        }

        ZonedDateTime normalized =
                dateTime.withZoneSameInstant(
                        zoneId
                );

        return !normalized
                .isBefore(
                        startDateTimeInclusive()
                )

                && normalized
                .isBefore(
                        endDateTimeExclusive()
                );
    }

    /**
     * 정확히 해당 절입 순간인가.
     */
    public boolean startsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
        ) {

            return false;
        }

        return dateTime
                .withZoneSameInstant(
                        zoneId
                )
                .equals(
                        startDateTimeInclusive()
                );
    }

    public boolean firstMonthOfSajuYear() {

        return monthIndex == 0;
    }

    public boolean lastMonthOfSajuYear() {

        return monthIndex == 11;
    }
}