package com.dajeom.model;

import com.dajeom.type.LuckTimelineDecadeState;
import com.dajeom.type.PalacePosition;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-5.
 *
 * 원국 + 대운 + 세운 + 월운
 * 통합 시간 Timeline.
 *
 * =================================================
 *
 * 원국:
 *
 * PalaceAnalysis
 *
 * =================================================
 *
 * 시간 선택:
 *
 * DecadeLuckSelectionAnalysis
 * AnnualLuckSelectionAnalysis
 * MonthlyLuckSelectionAnalysis
 *
 * =================================================
 *
 * 결과:
 *
 * 실제 시간교집합 기반
 * LuckTimelineSegment
 *
 * =================================================
 *
 * possible Segment끼리는
 * 교운 불확실 구간에서 서로 겹칠 수 있다.
 *
 * 이는 오류가 아니다.
 *
 * 예:
 *
 * 대운 A POSSIBLE
 * 대운 B POSSIBLE
 *
 * 가 같은 시간대에 존재할 수 있기 때문이다.
 *
 * 실제 불확실성의 원인/종류 통합은
 * 16-6에서 수행한다.
 */
public record LuckTimelineAnalysis(

        PalaceAnalysis natalAnalysis,

        LuckReferenceContext referenceContext,

        DecadeLuckSelectionAnalysis decadeSelectionAnalysis,

        AnnualLuckSelectionAnalysis annualSelectionAnalysis,

        MonthlyLuckSelectionAnalysis monthlySelectionAnalysis,

        List<LuckTimelineSegment> segments

) {

    public LuckTimelineAnalysis {

        Objects.requireNonNull(
                natalAnalysis,
                "통합 Timeline에는 원국 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                referenceContext,
                "통합 Timeline에는 LuckReferenceContext가 필요합니다."
        );

        Objects.requireNonNull(
                decadeSelectionAnalysis,
                "통합 Timeline에는 DecadeLuckSelectionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualSelectionAnalysis,
                "통합 Timeline에는 AnnualLuckSelectionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlySelectionAnalysis,
                "통합 Timeline에는 MonthlyLuckSelectionAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 동일 Reference Context
         * =================================================
         */

        if (
                decadeSelectionAnalysis.referenceContext()
                        != referenceContext
        ) {

            throw new IllegalArgumentException(
                    "대운 Selection이 다른 LuckReferenceContext를 참조합니다."
            );
        }

        if (
                annualSelectionAnalysis.referenceContext()
                        != referenceContext
        ) {

            throw new IllegalArgumentException(
                    "세운 Selection이 다른 LuckReferenceContext를 참조합니다."
            );
        }

        if (
                monthlySelectionAnalysis.referenceContext()
                        != referenceContext
        ) {

            throw new IllegalArgumentException(
                    "월운 Selection이 다른 LuckReferenceContext를 참조합니다."
            );
        }

        if (
                monthlySelectionAnalysis
                        .annualSelectionAnalysis()
                        != annualSelectionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "16-4 월운 Selection이 현재 16-3 세운 Selection을 참조하지 않습니다."
            );
        }

        segments =
                segments == null
                        ? List.of()
                        : segments
                        .stream()
                        .peek(
                                segment ->
                                        Objects.requireNonNull(
                                                segment,
                                                "LuckTimelineSegment는 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                LuckTimelineAnalysis
                                        ::compareSegments
                        )
                        .toList();

        if (
                segments.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "통합 Timeline에는 최소 하나의 Segment가 필요합니다."
            );
        }

        /*
         * =================================================
         * Segment Identity
         * =================================================
         */

        for (
                LuckTimelineSegment segment
                : segments
        ) {

            if (
                    segment.referenceContext()
                            != referenceContext
            ) {

                throw new IllegalArgumentException(
                        "Timeline Segment가 다른 LuckReferenceContext를 참조합니다."
                );
            }

            boolean knownMonthly =
                    monthlySelectionAnalysis
                            .candidates()
                            .stream()
                            .anyMatch(
                                    candidate ->
                                            candidate
                                                    == segment
                                                    .monthlyCandidate()
                            );

            if (
                    !knownMonthly
            ) {

                throw new IllegalArgumentException(
                        "16-4에 존재하지 않는 MonthlyLuckCandidate가 Timeline에 포함되었습니다."
                );
            }

            if (
                    segment.decadeState()
                            .active()
            ) {

                DecadeLuckCandidate decadeCandidate =
                        segment
                                .decadeCandidate()
                                .orElseThrow();

                boolean knownDecade =
                        decadeSelectionAnalysis
                                .candidates()
                                .stream()
                                .anyMatch(
                                        candidate ->
                                                candidate
                                                        == decadeCandidate
                                );

                if (
                        !knownDecade
                ) {

                    throw new IllegalArgumentException(
                            "16-2에 존재하지 않는 DecadeLuckCandidate가 Timeline에 포함되었습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * 모든 월운 Candidate가 최소 하나의
         * Timeline Segment를 가져야 한다.
         * =================================================
         */

        for (
                MonthlyLuckCandidate monthlyCandidate
                : monthlySelectionAnalysis.candidates()
        ) {

            boolean found =
                    segments
                            .stream()
                            .anyMatch(
                                    segment ->
                                            segment
                                                    .monthlyCandidate()
                                                    == monthlyCandidate
                            );

            if (
                    !found
            ) {

                throw new IllegalArgumentException(
                        "MonthlyLuckCandidate에 대응하는 Timeline Segment가 없습니다: "
                                + monthlyCandidate.sajuYear()
                                + " / "
                                + monthlyCandidate.monthIndex()
                );
            }
        }

        /*
         * =================================================
         * DATE_ONLY라면 possible Segment들의 합집합이
         * 조회 civil-day 전체를 덮어야 한다.
         * =================================================
         */

        if (
                referenceContext.dateOnly()
        ) {

            validatePossibleCoverage(
                    referenceContext,
                    segments
            );
        }
    }

    /*
     * =================================================
     * 조회
     * =================================================
     */

    public List<LuckTimelineSegment>
    activeDecadeSegments() {

        return segments
                .stream()
                .filter(
                        segment ->
                                segment
                                        .decadeState()
                                        .active()
                )
                .toList();
    }

    public List<LuckTimelineSegment>
    segmentsOf(
            LuckTimelineDecadeState state
    ) {

        if (
                state == null
        ) {

            return List.of();
        }

        return segments
                .stream()
                .filter(
                        segment ->
                                segment
                                        .decadeState()
                                        == state
                )
                .toList();
    }

    public List<LuckTimelineSegment>
    segmentsOfMonthly(
            MonthlyLuckCandidate monthlyCandidate
    ) {

        if (
                monthlyCandidate == null
        ) {

            return List.of();
        }

        return segments
                .stream()
                .filter(
                        segment ->
                                segment
                                        .monthlyCandidate()
                                        == monthlyCandidate
                )
                .toList();
    }

    public Optional<LuckTimelineSegment>
    definiteSegmentAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
        ) {

            return Optional.empty();
        }

        List<LuckTimelineSegment> matched =
                segments
                        .stream()
                        .filter(
                                segment ->
                                        segment
                                                .extent()
                                                .definitelyContains(
                                                        dateTime
                                                )
                        )
                        .toList();

        if (
                matched.size() > 1
        ) {

            throw new IllegalStateException(
                    "동일 시각에 확정 Timeline Segment가 2개 이상 존재합니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }

    public List<LuckTimelineSegment>
    possibleSegmentsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
        ) {

            return List.of();
        }

        return segments
                .stream()
                .filter(
                        segment ->
                                segment
                                        .extent()
                                        .possiblyContains(
                                                dateTime
                                        )
                )
                .toList();
    }

    public boolean hasBeforeFirstSegment() {

        return !segmentsOf(
                LuckTimelineDecadeState.BEFORE_FIRST
        )
                .isEmpty();
    }

    public boolean hasAfterGeneratedRangeSegment() {

        return !segmentsOf(
                LuckTimelineDecadeState.AFTER_GENERATED_RANGE
        )
                .isEmpty();
    }

    public boolean hasUnavailableDecadeSegment() {

        return !segmentsOf(
                LuckTimelineDecadeState.UNAVAILABLE
        )
                .isEmpty();
    }

    /*
     * =================================================
     * 원국 convenience
     * =================================================
     */

    public boolean completeChart() {

        return natalAnalysis
                .completeChart();
    }

    public boolean hasHourPalace() {

        return completeChart();
    }

    public boolean natalHourUnavailable() {

        return !completeChart();
    }

    /*
     * =================================================
     * Coverage 검증
     * =================================================
     */

    private static void validatePossibleCoverage(

            LuckReferenceContext referenceContext,

            List<LuckTimelineSegment> segments

    ) {

        List<Interval> intervals =
                new ArrayList<>();

        for (
                LuckTimelineSegment segment
                : segments
        ) {

            LuckTimelineExtent extent =
                    segment
                            .extent();

            intervals.add(
                    new Interval(
                            extent
                                    .possibleStartInclusive()
                                    .orElseThrow(),
                            extent
                                    .possibleEndExclusive()
                                    .orElseThrow()
                    )
            );
        }

        intervals.sort(
                Comparator.comparing(
                        interval ->
                                interval
                                        .start()
                                        .toInstant()
                )
        );

        ZonedDateTime requiredStart =
                referenceContext
                        .possibleStartInclusive();

        ZonedDateTime requiredEnd =
                referenceContext
                        .possibleEndExclusive();

        ZonedDateTime mergedStart =
                intervals
                        .get(0)
                        .start();

        ZonedDateTime mergedEnd =
                intervals
                        .get(0)
                        .end();

        if (
                mergedStart
                        .isAfter(
                                requiredStart
                        )
        ) {

            throw new IllegalArgumentException(
                    "Timeline possible Segment가 조회 날짜 시작부분을 덮지 못합니다."
            );
        }

        for (
                int index = 1;
                index < intervals.size();
                index++
        ) {

            Interval next =
                    intervals.get(
                            index
                    );

            /*
             * Gap.
             */
            if (
                    next.start()
                            .isAfter(
                                    mergedEnd
                            )
            ) {

                throw new IllegalArgumentException(
                        "통합 Timeline possible 시간축에 Gap이 존재합니다: "
                                + mergedEnd
                                + " ~ "
                                + next.start()
                );
            }

            if (
                    next.end()
                            .isAfter(
                                    mergedEnd
                            )
            ) {

                mergedEnd =
                        next.end();
            }
        }

        if (
                mergedEnd
                        .isBefore(
                                requiredEnd
                        )
        ) {

            throw new IllegalArgumentException(
                    "Timeline possible Segment가 조회 날짜 종료까지 덮지 못합니다."
            );
        }
    }

    private static int compareSegments(

            LuckTimelineSegment first,

            LuckTimelineSegment second

    ) {

        if (
                first.extent().exact()
                        && second.extent().exact()
        ) {

            int stateCompare =
                    Integer.compare(
                            first.decadeState().ordinal(),
                            second.decadeState().ordinal()
                    );

            if (
                    stateCompare != 0
            ) {

                return stateCompare;
            }

            return Integer.compare(
                    first.decadeSequenceNumber()
                            .orElse(
                                    Integer.MAX_VALUE
                            ),
                    second.decadeSequenceNumber()
                            .orElse(
                                    Integer.MAX_VALUE
                            )
            );
        }

        if (
                first.extent().dateOnly()
                        && second.extent().dateOnly()
        ) {

            int timeCompare =
                    first
                            .extent()
                            .possibleStartInclusive()
                            .orElseThrow()
                            .toInstant()
                            .compareTo(
                                    second
                                            .extent()
                                            .possibleStartInclusive()
                                            .orElseThrow()
                                            .toInstant()
                            );

            if (
                    timeCompare != 0
            ) {

                return timeCompare;
            }

            return Integer.compare(
                    first.decadeSequenceNumber()
                            .orElse(
                                    Integer.MAX_VALUE
                            ),
                    second.decadeSequenceNumber()
                            .orElse(
                                    Integer.MAX_VALUE
                            )
            );
        }

        return first.extent().exact()
                ? -1
                : 1;
    }

    private record Interval(

            ZonedDateTime start,

            ZonedDateTime end

    ) {
    }
}