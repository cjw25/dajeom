package com.dajeom.model;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 14차-1.
 *
 * 세운 1년의 실제 시간 범위.
 *
 * =================================================
 *
 * 세운 연도:
 *
 * targetYear 입춘
 *
 * inclusive
 *
 * ~
 *
 * targetYear + 1 입춘
 *
 * exclusive
 *
 * =================================================
 *
 * 예:
 *
 * 2026 세운
 *
 * [2026년 입춘 실제시각,
 *  2027년 입춘 실제시각)
 *
 * =================================================
 *
 * 중요:
 *
 * 양력:
 *
 * 2026-01-01
 * ~
 * 2026-12-31
 *
 * 로 계산하지 않는다.
 *
 * =================================================
 *
 * 이 객체에서는 아직:
 *
 * - 세운 간지
 * - 십성
 * - 원국 관계
 * - 대운 관계
 * - 용신 Context
 *
 * 를 계산하지 않는다.
 *
 * 오직 세운의 시간 경계만 확정한다.
 */
public record AnnualLuckBoundaryAnalysis(

        int targetYear,

        ZoneId zoneId,

        ZonedDateTime startDateTimeInclusive,

        ZonedDateTime endDateTimeExclusive

) {

    public AnnualLuckBoundaryAnalysis {

        if (
                targetYear < 1
        ) {

            throw new IllegalArgumentException(
                    "세운 targetYear는 1 이상이어야 합니다."
            );
        }

        Objects.requireNonNull(
                zoneId,
                "세운 경계 계산에는 ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                startDateTimeInclusive,
                "세운 시작 입춘시각이 필요합니다."
        );

        Objects.requireNonNull(
                endDateTimeExclusive,
                "세운 종료 입춘시각이 필요합니다."
        );

        if (
                !startDateTimeInclusive
                        .getZone()
                        .equals(
                                zoneId
                        )
        ) {

            throw new IllegalArgumentException(
                    "세운 시작시각의 ZoneId가 분석 ZoneId와 일치하지 않습니다."
            );
        }

        if (
                !endDateTimeExclusive
                        .getZone()
                        .equals(
                                zoneId
                        )
        ) {

            throw new IllegalArgumentException(
                    "세운 종료시각의 ZoneId가 분석 ZoneId와 일치하지 않습니다."
            );
        }

        if (
                !endDateTimeExclusive
                        .isAfter(
                                startDateTimeInclusive
                        )
        ) {

            throw new IllegalArgumentException(
                    "세운 종료시각은 시작시각보다 늦어야 합니다."
            );
        }

        /*
         * 세운 기간은 입춘~다음 입춘이므로
         * 정상적인 경우 약 1년이다.
         *
         * 정확히 365일이라고 강제하지 않는다.
         */
        long days =
                Duration.between(
                                startDateTimeInclusive,
                                endDateTimeExclusive
                        )
                        .toDays();

        if (
                days < 360
                        || days > 370
        ) {

            throw new IllegalArgumentException(
                    "세운 입춘 경계 간격이 비정상적입니다: "
                            + days
                            + "일"
            );
        }
    }

    /**
     * =================================================
     * 특정 시각이 이 세운에 포함되는가
     * =================================================
     *
     * [start, end)
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
                dateTime
                        .withZoneSameInstant(
                                zoneId
                        );

        return !normalized
                .isBefore(
                        startDateTimeInclusive
                )

                && normalized
                .isBefore(
                        endDateTimeExclusive
                );
    }

    /**
     * 입춘 순간과 정확히 같은 경우도
     * 새 세운에 포함된다.
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
                        startDateTimeInclusive
                );
    }

    public Duration duration() {

        return Duration.between(
                startDateTimeInclusive,
                endDateTimeExclusive
        );
    }

    public long durationDays() {

        return duration()
                .toDays();
    }

    public boolean beforeStart(
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
                .isBefore(
                        startDateTimeInclusive
                );
    }

    public boolean afterOrAtEnd(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
        ) {

            return false;
        }

        ZonedDateTime normalized =
                dateTime
                        .withZoneSameInstant(
                                zoneId
                        );

        return !normalized
                .isBefore(
                        endDateTimeExclusive
                );
    }
}