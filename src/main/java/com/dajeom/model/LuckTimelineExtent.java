package com.dajeom.model;

import com.dajeom.type.LuckReferencePrecision;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-5.
 *
 * 하나의 LuckTimelineSegment가
 * 실제로 성립할 수 있는 시간 Extent.
 *
 * =================================================
 *
 * EXACT_DATE_TIME
 *
 * 하나의 정확한 시점이다.
 *
 * Interval로 변환하지 않는다.
 *
 * exactDefinite:
 *
 * true
 * -> 그 정확한 시점에서 해당 조합이 확정됨.
 *
 * false
 * -> 그 시점에서 해당 조합은 가능하지만
 *    대운 교운 불확실성 등으로 확정할 수 없음.
 *
 * =================================================
 *
 * DATE_ONLY
 *
 * possible:
 *
 * 해당 조합이 실제로 존재할 수 있는
 * 시간 교집합.
 *
 * definite:
 *
 * 모든 현재 불확실성을 고려해도
 * 해당 조합임이 확정되는 시간 교집합.
 *
 * =================================================
 *
 * 예:
 *
 * 대운 A possible:
 * 00:00 ~ 18:00
 *
 * 대운 A definite:
 * 00:00 ~ 14:00
 *
 * 이라면:
 *
 * 14:00 ~ 18:00은
 * 대운 A일 가능성은 있지만 확정할 수 없는 구간이다.
 */
public record LuckTimelineExtent(

        LuckReferencePrecision precision,

        ZoneId zoneId,

        Optional<ZonedDateTime> exactDateTime,

        boolean exactDefinite,

        Optional<ZonedDateTime> possibleStartInclusive,

        Optional<ZonedDateTime> possibleEndExclusive,

        Optional<ZonedDateTime> definiteStartInclusive,

        Optional<ZonedDateTime> definiteEndExclusive

) {

    public LuckTimelineExtent {

        Objects.requireNonNull(
                precision,
                "Timeline Extent에는 Precision이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "Timeline Extent에는 ZoneId가 필요합니다."
        );

        exactDateTime =
                exactDateTime == null
                        ? Optional.empty()
                        : exactDateTime;

        possibleStartInclusive =
                possibleStartInclusive == null
                        ? Optional.empty()
                        : possibleStartInclusive;

        possibleEndExclusive =
                possibleEndExclusive == null
                        ? Optional.empty()
                        : possibleEndExclusive;

        definiteStartInclusive =
                definiteStartInclusive == null
                        ? Optional.empty()
                        : definiteStartInclusive;

        definiteEndExclusive =
                definiteEndExclusive == null
                        ? Optional.empty()
                        : definiteEndExclusive;

        /*
         * =================================================
         * EXACT_DATE_TIME
         * =================================================
         */

        if (
                precision.exactDateTime()
        ) {

            if (
                    exactDateTime.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "EXACT_DATE_TIME Timeline Extent에는 exactDateTime이 필요합니다."
                );
            }

            ZonedDateTime exact =
                    exactDateTime
                            .orElseThrow();

            if (
                    !exact.getZone()
                            .equals(
                                    zoneId
                            )
            ) {

                throw new IllegalArgumentException(
                        "Timeline exactDateTime의 ZoneId가 일치하지 않습니다."
                );
            }

            if (
                    possibleStartInclusive.isPresent()
                            || possibleEndExclusive.isPresent()
                            || definiteStartInclusive.isPresent()
                            || definiteEndExclusive.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "EXACT_DATE_TIME Timeline Extent에는 Interval 값이 존재하면 안 됩니다."
                );
            }
        }

        /*
         * =================================================
         * DATE_ONLY
         * =================================================
         */

        if (
                precision.dateOnly()
        ) {

            if (
                    exactDateTime.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "DATE_ONLY Timeline Extent에는 exactDateTime이 존재하면 안 됩니다."
                );
            }

            if (
                    exactDefinite
            ) {

                throw new IllegalArgumentException(
                        "DATE_ONLY Timeline Extent에서는 exactDefinite를 사용할 수 없습니다."
                );
            }

            if (
                    possibleStartInclusive.isEmpty()
                            || possibleEndExclusive.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "DATE_ONLY Timeline Extent에는 possible 시간범위가 필요합니다."
                );
            }

            ZonedDateTime possibleStart =
                    possibleStartInclusive
                            .orElseThrow();

            ZonedDateTime possibleEnd =
                    possibleEndExclusive
                            .orElseThrow();

            validateZone(
                    possibleStart,
                    zoneId
            );

            validateZone(
                    possibleEnd,
                    zoneId
            );

            if (
                    !possibleEnd
                            .isAfter(
                                    possibleStart
                            )
            ) {

                throw new IllegalArgumentException(
                        "Timeline possible 종료시각은 시작시각보다 늦어야 합니다."
                );
            }

            if (
                    definiteStartInclusive.isPresent()
                            != definiteEndExclusive.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "Timeline definite 시작/종료는 함께 존재해야 합니다."
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

                validateZone(
                        definiteStart,
                        zoneId
                );

                validateZone(
                        definiteEnd,
                        zoneId
                );

                if (
                        !definiteEnd
                                .isAfter(
                                        definiteStart
                                )
                ) {

                    throw new IllegalArgumentException(
                            "Timeline definite 종료시각은 시작시각보다 늦어야 합니다."
                    );
                }

                if (
                        definiteStart
                                .isBefore(
                                        possibleStart
                                )

                                || definiteEnd
                                .isAfter(
                                        possibleEnd
                                )
                ) {

                    throw new IllegalArgumentException(
                            "Timeline definite 범위는 possible 범위를 벗어날 수 없습니다."
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * Factory
     * =================================================
     */

    public static LuckTimelineExtent exact(

            ZonedDateTime exactDateTime,

            ZoneId zoneId,

            boolean definite

    ) {

        Objects.requireNonNull(
                exactDateTime,
                "정확한 Timeline 시점이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "Timeline ZoneId가 필요합니다."
        );

        ZonedDateTime normalized =
                exactDateTime
                        .withZoneSameInstant(
                                zoneId
                        );

        return new LuckTimelineExtent(
                LuckReferencePrecision.EXACT_DATE_TIME,
                zoneId,
                Optional.of(
                        normalized
                ),
                definite,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    public static LuckTimelineExtent dateOnly(

            ZoneId zoneId,

            ZonedDateTime possibleStartInclusive,

            ZonedDateTime possibleEndExclusive,

            Optional<ZonedDateTime> definiteStartInclusive,

            Optional<ZonedDateTime> definiteEndExclusive

    ) {

        Objects.requireNonNull(
                zoneId,
                "Timeline ZoneId가 필요합니다."
        );

        return new LuckTimelineExtent(
                LuckReferencePrecision.DATE_ONLY,
                zoneId,
                Optional.empty(),
                false,
                Optional.of(
                        Objects.requireNonNull(
                                possibleStartInclusive,
                                "possible 시작시각이 필요합니다."
                        )
                ),
                Optional.of(
                        Objects.requireNonNull(
                                possibleEndExclusive,
                                "possible 종료시각이 필요합니다."
                        )
                ),
                definiteStartInclusive,
                definiteEndExclusive
        );
    }

    /*
     * =================================================
     * 상태
     * =================================================
     */

    public boolean exact() {

        return precision
                .exactDateTime();
    }

    public boolean dateOnly() {

        return precision
                .dateOnly();
    }

    public boolean hasDefiniteExtent() {

        if (
                exact()
        ) {

            return exactDefinite;
        }

        return definiteStartInclusive
                .isPresent();
    }

    /**
     * possible과 definite가 완전히 동일한가.
     */
    public boolean fullyDefinite() {

        if (
                exact()
        ) {

            return exactDefinite;
        }

        if (
                definiteStartInclusive.isEmpty()
        ) {

            return false;
        }

        return possibleStartInclusive
                .orElseThrow()
                .toInstant()
                .equals(
                        definiteStartInclusive
                                .orElseThrow()
                                .toInstant()
                )

                && possibleEndExclusive
                .orElseThrow()
                .toInstant()
                .equals(
                        definiteEndExclusive
                                .orElseThrow()
                                .toInstant()
                );
    }

    public boolean uncertaintyExists() {

        return !fullyDefinite();
    }

    /*
     * =================================================
     * Duration
     * =================================================
     */

    public Optional<Duration>
    possibleDuration() {

        if (
                exact()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                Duration.between(
                        possibleStartInclusive
                                .orElseThrow(),
                        possibleEndExclusive
                                .orElseThrow()
                )
        );
    }

    public Optional<Duration>
    definiteDuration() {

        if (
                exact()
                        || definiteStartInclusive.isEmpty()
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

    /*
     * =================================================
     * Contains
     * =================================================
     */

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
                                zoneId
                        );

        if (
                exact()
        ) {

            return exactDateTime
                    .orElseThrow()
                    .toInstant()
                    .equals(
                            normalized
                                    .toInstant()
                    );
        }

        ZonedDateTime start =
                possibleStartInclusive
                        .orElseThrow();

        ZonedDateTime end =
                possibleEndExclusive
                        .orElseThrow();

        return !normalized
                .isBefore(
                        start
                )

                && normalized
                .isBefore(
                        end
                );
    }

    public boolean definitelyContains(
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

        if (
                exact()
        ) {

            return exactDefinite
                    && exactDateTime
                    .orElseThrow()
                    .toInstant()
                    .equals(
                            normalized
                                    .toInstant()
                    );
        }

        if (
                definiteStartInclusive.isEmpty()
        ) {

            return false;
        }

        ZonedDateTime start =
                definiteStartInclusive
                        .orElseThrow();

        ZonedDateTime end =
                definiteEndExclusive
                        .orElseThrow();

        return !normalized
                .isBefore(
                        start
                )

                && normalized
                .isBefore(
                        end
                );
    }

    private static void validateZone(

            ZonedDateTime dateTime,

            ZoneId zoneId

    ) {

        if (
                !dateTime.getZone()
                        .equals(
                                zoneId
                        )
        ) {

            throw new IllegalArgumentException(
                    "Timeline 시간의 ZoneId가 일치하지 않습니다."
            );
        }
    }
}