package com.dajeom.model;

import com.dajeom.type.LuckTimelineDecadeState;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-5.
 *
 * 실제 시간교집합으로 만들어진
 * 하나의 통합 운 Timeline Segment.
 *
 * =================================================
 *
 * 항상 존재:
 *
 * - 원국은 상위 LuckTimelineAnalysis에 존재
 * - 세운 Candidate
 * - 월운 Candidate
 *
 * =================================================
 *
 * 대운:
 *
 * ACTIVE
 * -> DecadeLuckCandidate 존재
 *
 * BEFORE_FIRST
 * -> 대운 Candidate 없음
 *
 * AFTER_GENERATED_RANGE
 * -> 대운 Candidate 없음
 *
 * UNAVAILABLE
 * -> 대운 Candidate 없음
 *
 * =================================================
 *
 * Timeline Segment는 단순 Candidate Cartesian Product가
 * 아니라 실제 시간 교집합을 가진다.
 */
public record LuckTimelineSegment(

        LuckReferenceContext referenceContext,

        MonthlyLuckCandidate monthlyCandidate,

        LuckTimelineDecadeState decadeState,

        Optional<DecadeLuckCandidate> decadeCandidate,

        LuckTimelineExtent extent

) {

    public LuckTimelineSegment {

        Objects.requireNonNull(
                referenceContext,
                "Timeline Segment에는 LuckReferenceContext가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyCandidate,
                "Timeline Segment에는 MonthlyLuckCandidate가 필요합니다."
        );

        Objects.requireNonNull(
                decadeState,
                "Timeline Segment에는 대운 상태가 필요합니다."
        );

        decadeCandidate =
                decadeCandidate == null
                        ? Optional.empty()
                        : decadeCandidate;

        Objects.requireNonNull(
                extent,
                "Timeline Segment에는 LuckTimelineExtent가 필요합니다."
        );

        /*
         * =================================================
         * 대운 State / Candidate
         * =================================================
         */

        if (
                decadeState.active()
                        && decadeCandidate.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "ACTIVE Timeline Segment에는 DecadeLuckCandidate가 필요합니다."
            );
        }

        if (
                !decadeState.active()
                        && decadeCandidate.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "ACTIVE가 아닌 Timeline Segment에는 DecadeLuckCandidate를 넣을 수 없습니다."
            );
        }

        /*
         * =================================================
         * Reference precision
         * =================================================
         */

        if (
                extent.precision()
                        != referenceContext.precision()
        ) {

            throw new IllegalArgumentException(
                    "Timeline Extent와 LuckReferenceContext의 Precision이 일치하지 않습니다."
            );
        }

        if (
                !extent.zoneId()
                        .equals(
                                referenceContext.zoneId()
                        )
        ) {

            throw new IllegalArgumentException(
                    "Timeline Extent와 LuckReferenceContext의 ZoneId가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 월운 Zone
         * =================================================
         */

        if (
                !monthlyCandidate
                        .zoneId()
                        .equals(
                                referenceContext.zoneId()
                        )
        ) {

            throw new IllegalArgumentException(
                    "Timeline 월운 Candidate의 ZoneId가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * EXACT
         * =================================================
         */

        if (
                referenceContext.exact()
        ) {

            ZonedDateTime reference =
                    referenceContext
                            .requireExactDateTime();

            ZonedDateTime extentTime =
                    extent
                            .exactDateTime()
                            .orElseThrow();

            if (
                    !reference
                            .toInstant()
                            .equals(
                                    extentTime
                                            .toInstant()
                            )
            ) {

                throw new IllegalArgumentException(
                        "Timeline exact 시점이 LuckReferenceContext와 일치하지 않습니다."
                );
            }

            if (
                    !monthlyCandidate
                            .period()
                            .contains(
                                    reference
                            )
            ) {

                throw new IllegalArgumentException(
                        "Timeline exact 시점이 현재 월운에 포함되지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * DATE_ONLY
         * =================================================
         */

        if (
                referenceContext.dateOnly()
        ) {

            ZonedDateTime possibleStart =
                    extent
                            .possibleStartInclusive()
                            .orElseThrow();

            ZonedDateTime possibleEnd =
                    extent
                            .possibleEndExclusive()
                            .orElseThrow();

            /*
             * 조회 날짜 범위 안.
             */

            if (
                    possibleStart
                            .isBefore(
                                    referenceContext
                                            .possibleStartInclusive()
                            )

                            || possibleEnd
                            .isAfter(
                                    referenceContext
                                            .possibleEndExclusive()
                            )
            ) {

                throw new IllegalArgumentException(
                        "Timeline Segment가 조회 날짜 범위를 벗어났습니다."
                );
            }

            /*
             * 월운 범위 안.
             */

            if (
                    possibleStart
                            .isBefore(
                                    monthlyCandidate
                                            .startDateTimeInclusive()
                            )

                            || possibleEnd
                            .isAfter(
                                    monthlyCandidate
                                            .endDateTimeExclusive()
                            )
            ) {

                throw new IllegalArgumentException(
                        "Timeline Segment가 현재 MonthlyLuckPeriod 범위를 벗어났습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 세운
     * =================================================
     */

    public AnnualLuckCandidate annualCandidate() {

        return monthlyCandidate
                .annualCandidate();
    }

    public AnnualLuckPeriod annualPeriod() {

        return annualCandidate()
                .period();
    }

    public int sajuYear() {

        return annualCandidate()
                .targetYear();
    }

    public SexagenaryPillar annualPillar() {

        return annualCandidate()
                .pillar();
    }

    /*
     * =================================================
     * 월운
     * =================================================
     */

    public MonthlyLuckPeriod monthlyPeriod() {

        return monthlyCandidate
                .period();
    }

    public int monthIndex() {

        return monthlyCandidate
                .monthIndex();
    }

    public SexagenaryPillar monthlyPillar() {

        return monthlyCandidate
                .monthlyPillar();
    }

    /*
     * =================================================
     * 대운
     * =================================================
     */

    public boolean hasDecade() {

        return decadeCandidate
                .isPresent();
    }

    public Optional<DecadeLuckPeriod>
    decadePeriod() {

        return decadeCandidate
                .map(
                        DecadeLuckCandidate
                                ::period
                );
    }

    public Optional<SexagenaryPillar>
    decadePillar() {

        return decadeCandidate
                .map(
                        DecadeLuckCandidate
                                ::pillar
                );
    }

    public Optional<Integer>
    decadeSequenceNumber() {

        return decadeCandidate
                .map(
                        DecadeLuckCandidate
                                ::sequenceNumber
                );
    }

    /*
     * =================================================
     * 시간
     * =================================================
     */

    public ZoneId zoneId() {

        return referenceContext
                .zoneId();
    }

    public boolean exact() {

        return extent
                .exact();
    }

    public boolean dateOnly() {

        return extent
                .dateOnly();
    }

    public boolean fullyDefinite() {

        return extent
                .fullyDefinite();
    }

    public boolean uncertaintyExists() {

        return extent
                .uncertaintyExists()

                || decadeState.unavailable()

                || decadeState.afterGeneratedRange();
    }
}