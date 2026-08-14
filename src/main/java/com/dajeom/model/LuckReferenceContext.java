package com.dajeom.model;

import com.dajeom.type.LuckReferencePrecision;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-1.
 *
 * 대운·세운·월운을 선택하기 위한
 * 사용자 조회 기준시간 Context.
 *
 * =================================================
 *
 * EXACT_DATE_TIME
 *
 * 예:
 *
 * 2026-08-20T15:30+09:00[Asia/Seoul]
 *
 * 정확한 한 시각을 의미한다.
 *
 * =================================================
 *
 * DATE_ONLY
 *
 * 예:
 *
 * 2026-08-20
 *
 * 정확한 시각을 알 수 없으므로
 *
 * [해당 날짜의 시작,
 *  다음 날짜의 시작)
 *
 * 전체가 가능한 조회범위다.
 *
 * =================================================
 *
 * 중요:
 *
 * DATE_ONLY를 정오나 자정으로
 * 대체하지 않는다.
 */
public record LuckReferenceContext(

        LuckReferencePrecision precision,

        ZoneId zoneId,

        LocalDate referenceDate,

        Optional<ZonedDateTime> exactDateTime

) {

    public LuckReferenceContext {

        Objects.requireNonNull(
                precision,
                "운 분석 기준의 Precision이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "운 분석 기준에는 ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                referenceDate,
                "운 분석 기준 날짜가 필요합니다."
        );

        exactDateTime =
                exactDateTime == null
                        ? Optional.empty()
                        : exactDateTime;

        /*
         * =================================================
         * 정확한 일시
         * =================================================
         */

        if (
                precision.exactDateTime()
        ) {

            if (
                    exactDateTime.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "EXACT_DATE_TIME에는 exactDateTime이 필요합니다."
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
                        "exactDateTime의 ZoneId와 분석 ZoneId가 일치하지 않습니다."
                );
            }

            if (
                    !exact.toLocalDate()
                            .equals(
                                    referenceDate
                            )
            ) {

                throw new IllegalArgumentException(
                        "exactDateTime의 날짜와 referenceDate가 일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * 날짜만 입력
         * =================================================
         */

        if (
                precision.dateOnly()
                        && exactDateTime.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "DATE_ONLY에는 exactDateTime이 존재하면 안 됩니다."
            );
        }
    }

    /*
     * =================================================
     * Factory
     * =================================================
     */

    public static LuckReferenceContext exact(

            ZonedDateTime dateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                dateTime,
                "정확한 기준일시가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "분석 ZoneId가 필요합니다."
        );

        ZonedDateTime normalized =
                dateTime
                        .withZoneSameInstant(
                                zoneId
                        );

        return new LuckReferenceContext(
                LuckReferencePrecision.EXACT_DATE_TIME,
                zoneId,
                normalized.toLocalDate(),
                Optional.of(
                        normalized
                )
        );
    }

    public static LuckReferenceContext dateOnly(

            LocalDate date,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                date,
                "기준 날짜가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "분석 ZoneId가 필요합니다."
        );

        return new LuckReferenceContext(
                LuckReferencePrecision.DATE_ONLY,
                zoneId,
                date,
                Optional.empty()
        );
    }

    /*
     * =================================================
     * 정밀도
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

    public ZonedDateTime requireExactDateTime() {

        if (
                !exact()
        ) {

            throw new IllegalStateException(
                    "DATE_ONLY 기준에서는 정확한 시각을 요구할 수 없습니다."
            );
        }

        return exactDateTime
                .orElseThrow();
    }

    /*
     * =================================================
     * DATE_ONLY 가능범위
     * =================================================
     *
     * LocalDate.atStartOfDay(zoneId)를 사용한다.
     *
     * 따라서 DST가 존재하는 Zone에서도
     * 해당 지역의 실제 civil-day 시작시각을
     * Java ZoneRules에 따라 사용한다.
     */

    public ZonedDateTime possibleStartInclusive() {

        if (
                exact()
        ) {

            return requireExactDateTime();
        }

        return referenceDate
                .atStartOfDay(
                        zoneId
                );
    }

    public ZonedDateTime possibleEndExclusive() {

        if (
                exact()
        ) {

            /*
             * EXACT_DATE_TIME은 시간구간이 아니라
             * 하나의 점이다.
             *
             * 따라서 실제 interval 판정에서는
             * 이 값을 직접 이용하지 않고
             * possiblyIntersects()가 정확한 점 판정을 한다.
             */
            return requireExactDateTime();
        }

        return referenceDate
                .plusDays(1)
                .atStartOfDay(
                        zoneId
                );
    }

    public Optional<Duration>
    possibleDuration() {

        if (
                exact()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                Duration.between(
                        possibleStartInclusive(),
                        possibleEndExclusive()
                )
        );
    }

    /*
     * =================================================
     * Interval 접촉 여부
     * =================================================
     *
     * 운 Period:
     *
     * [intervalStartInclusive,
     *  intervalEndExclusive)
     *
     * 와 현재 조회기준이
     * 하나라도 겹칠 가능성이 있는지 확인한다.
     *
     * =================================================
     *
     * EXACT:
     *
     * 정확한 한 시각이 Period 안에 있는가.
     *
     * =================================================
     *
     * DATE_ONLY:
     *
     * 그 날짜 전체 가능범위 중 일부가
     * Period와 겹치는가.
     */

    public boolean possiblyIntersects(

            ZonedDateTime intervalStartInclusive,

            ZonedDateTime intervalEndExclusive

    ) {

        validateInterval(
                intervalStartInclusive,
                intervalEndExclusive
        );

        ZonedDateTime intervalStart =
                intervalStartInclusive
                        .withZoneSameInstant(
                                zoneId
                        );

        ZonedDateTime intervalEnd =
                intervalEndExclusive
                        .withZoneSameInstant(
                                zoneId
                        );

        if (
                exact()
        ) {

            ZonedDateTime exact =
                    requireExactDateTime();

            return !exact
                    .isBefore(
                            intervalStart
                    )

                    && exact
                    .isBefore(
                            intervalEnd
                    );
        }

        ZonedDateTime possibleStart =
                possibleStartInclusive();

        ZonedDateTime possibleEnd =
                possibleEndExclusive();

        /*
         * 두 half-open interval의 교집합:
         *
         * A.start < B.end
         * &&
         * B.start < A.end
         */

        return possibleStart
                .isBefore(
                        intervalEnd
                )

                && intervalStart
                .isBefore(
                        possibleEnd
                );
    }

    /*
     * =================================================
     * 확정 포함 여부
     * =================================================
     *
     * EXACT:
     *
     * 정확한 시각이 해당 Period에 들어가면
     * 확정이다.
     *
     * =================================================
     *
     * DATE_ONLY:
     *
     * 날짜 전체의 가능한 시간범위가
     * 하나의 Period 안에 완전히 포함될 때만
     * 확정이다.
     *
     * 예:
     *
     * 2026-08-20 전체가
     * 丙申월 안에 들어가면 true.
     *
     * 하지만 8월 20일 15시에 절입이 있다면
     * 그 날짜는 두 월운에 걸리므로
     * 두 월운 모두 definite=false.
     */

    public boolean definitelyContainedIn(

            ZonedDateTime intervalStartInclusive,

            ZonedDateTime intervalEndExclusive

    ) {

        validateInterval(
                intervalStartInclusive,
                intervalEndExclusive
        );

        ZonedDateTime intervalStart =
                intervalStartInclusive
                        .withZoneSameInstant(
                                zoneId
                        );

        ZonedDateTime intervalEnd =
                intervalEndExclusive
                        .withZoneSameInstant(
                                zoneId
                        );

        if (
                exact()
        ) {

            ZonedDateTime exact =
                    requireExactDateTime();

            return !exact
                    .isBefore(
                            intervalStart
                    )

                    && exact
                    .isBefore(
                            intervalEnd
                    );
        }

        ZonedDateTime possibleStart =
                possibleStartInclusive();

        ZonedDateTime possibleEnd =
                possibleEndExclusive();

        return !possibleStart
                .isBefore(
                        intervalStart
                )

                && !possibleEnd
                .isAfter(
                        intervalEnd
                );
    }

    /*
     * =================================================
     * exact match
     * =================================================
     */

    public boolean matchesExactInstant(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !exact()
        ) {

            return false;
        }

        return requireExactDateTime()
                .toInstant()
                .equals(
                        dateTime
                                .toInstant()
                );
    }

    /*
     * =================================================
     * Interval 검증
     * =================================================
     */

    private void validateInterval(

            ZonedDateTime intervalStartInclusive,

            ZonedDateTime intervalEndExclusive

    ) {

        Objects.requireNonNull(
                intervalStartInclusive,
                "Interval 시작시각이 필요합니다."
        );

        Objects.requireNonNull(
                intervalEndExclusive,
                "Interval 종료시각이 필요합니다."
        );

        if (
                !intervalEndExclusive
                        .toInstant()
                        .isAfter(
                                intervalStartInclusive
                                        .toInstant()
                        )
        ) {

            throw new IllegalArgumentException(
                    "Interval 종료시각은 시작시각보다 늦어야 합니다."
            );
        }
    }
}