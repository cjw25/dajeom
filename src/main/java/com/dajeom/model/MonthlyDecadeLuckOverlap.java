package com.dajeom.model;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-5.
 *
 * 월운과 대운이 실제로 겹치는 시간구간.
 *
 * =================================================
 *
 * possible
 *
 * 출생시간 또는 대운 시작시점 불확실성을
 * 포함했을 때 해당 대운일 가능성이 있는
 * 최대 구간.
 *
 * =================================================
 *
 * definite
 *
 * 가능한 모든 교운시점을 고려해도
 * 해당 대운이라고 확정할 수 있는 구간.
 *
 * =================================================
 *
 * 정확한 대운시점이라면:
 *
 * possible == definite
 */
public record MonthlyDecadeLuckOverlap(

        DecadeLuckPeriod decadePeriod,

        ZonedDateTime possibleStartInclusive,

        ZonedDateTime possibleEndExclusive,

        Optional<ZonedDateTime> definiteStartInclusive,

        Optional<ZonedDateTime> definiteEndExclusive

) {

    public MonthlyDecadeLuckOverlap {

        Objects.requireNonNull(
                decadePeriod,
                "월운 Overlap에는 DecadeLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                possibleStartInclusive,
                "possible 시작시각이 필요합니다."
        );

        Objects.requireNonNull(
                possibleEndExclusive,
                "possible 종료시각이 필요합니다."
        );

        definiteStartInclusive =
                definiteStartInclusive == null
                        ? Optional.empty()
                        : definiteStartInclusive;

        definiteEndExclusive =
                definiteEndExclusive == null
                        ? Optional.empty()
                        : definiteEndExclusive;

        if (
                !possibleEndExclusive
                        .isAfter(
                                possibleStartInclusive
                        )
        ) {

            throw new IllegalArgumentException(
                    "possible 종료시각은 시작시각보다 늦어야 합니다."
            );
        }

        if (
                definiteStartInclusive.isPresent()
                        != definiteEndExclusive.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "definite 시작과 종료는 함께 존재해야 합니다."
            );
        }

        if (
                definiteStartInclusive.isPresent()
        ) {

            ZonedDateTime definiteStart =
                    definiteStartInclusive
                            .orElseThrow();

            ZonedDateTime definiteEnd =
                    definiteEndExclusive
                            .orElseThrow();

            if (
                    !definiteEnd
                            .isAfter(
                                    definiteStart
                            )
            ) {

                throw new IllegalArgumentException(
                        "definite 종료시각은 시작시각보다 늦어야 합니다."
                );
            }

            if (
                    definiteStart
                            .isBefore(
                                    possibleStartInclusive
                            )

                            || definiteEnd
                            .isAfter(
                                    possibleEndExclusive
                            )
            ) {

                throw new IllegalArgumentException(
                        "definite 범위는 possible 범위를 벗어날 수 없습니다."
                );
            }
        }
    }

    public int decadeSequenceNumber() {

        return decadePeriod
                .sequenceNumber();
    }

    public SexagenaryPillar decadePillar() {

        return decadePeriod
                .pillar();
    }

    public boolean hasDefiniteWindow() {

        return definiteStartInclusive
                .isPresent();
    }

    public boolean uncertaintyExists() {

        if (
                !hasDefiniteWindow()
        ) {

            return true;
        }

        return !possibleStartInclusive
                .equals(
                        definiteStartInclusive
                                .orElseThrow()
                )

                || !possibleEndExclusive
                .equals(
                        definiteEndExclusive
                                .orElseThrow()
                );
    }

    public Duration possibleDuration() {

        return Duration.between(
                possibleStartInclusive,
                possibleEndExclusive
        );
    }

    public Optional<Duration> definiteDuration() {

        if (
                !hasDefiniteWindow()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                Duration.between(
                        definiteStartInclusive
                                .orElseThrow(),
                        definiteEndExclusive
                                .orElseThrow()
                )
        );
    }

    public boolean possiblyContains(
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
                                possibleStartInclusive
                                        .getZone()
                        );

        return !normalized
                .isBefore(
                        possibleStartInclusive
                )

                && normalized
                .isBefore(
                        possibleEndExclusive
                );
    }

    public boolean definitelyContains(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !hasDefiniteWindow()
        ) {

            return false;
        }

        ZonedDateTime start =
                definiteStartInclusive
                        .orElseThrow();

        ZonedDateTime end =
                definiteEndExclusive
                        .orElseThrow();

        ZonedDateTime normalized =
                dateTime
                        .withZoneSameInstant(
                                start.getZone()
                        );

        return !normalized
                .isBefore(
                        start
                )

                && normalized
                .isBefore(
                        end
                );
    }
}