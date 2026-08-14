package com.dajeom.model;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 14차-5.
 *
 * 하나의 세운 Period와
 * 하나의 대운 Period가
 * 실제로 겹치는 시간 범위.
 *
 * =================================================
 *
 * possible window
 *
 * 출생시간 미상을 고려했을 때
 * 이 대운일 가능성이 있는 전체 구간.
 *
 * =================================================
 *
 * definite window
 *
 * 출생시간이 어떤 값이더라도
 * 이 대운이라고 확정할 수 있는 구간.
 *
 * =================================================
 *
 * 정확한 출생시간인 경우:
 *
 * possible == definite
 *
 * =================================================
 *
 * 시간 미상으로 교운 경계에 걸리면:
 *
 * possible은 존재하지만
 * definite가 없을 수도 있다.
 */
public record AnnualDecadeLuckOverlap(

        DecadeLuckPeriod decadePeriod,

        ZonedDateTime possibleStartInclusive,

        ZonedDateTime possibleEndExclusive,

        Optional<ZonedDateTime> definiteStartInclusive,

        Optional<ZonedDateTime> definiteEndExclusive

) {

    public AnnualDecadeLuckOverlap {

        Objects.requireNonNull(
                decadePeriod,
                "Overlap에는 대운 Period가 필요합니다."
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
                    "definite 시작/종료는 둘 다 존재하거나 둘 다 없어야 합니다."
            );
        }

        if (
                definiteStartInclusive.isPresent()
        ) {

            ZonedDateTime start =
                    definiteStartInclusive
                            .orElseThrow();

            ZonedDateTime end =
                    definiteEndExclusive
                            .orElseThrow();

            if (
                    !end.isAfter(
                            start
                    )
            ) {

                throw new IllegalArgumentException(
                        "definite 종료시각은 시작시각보다 늦어야 합니다."
                );
            }

            if (
                    start.isBefore(
                            possibleStartInclusive
                    )

                            || end.isAfter(
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