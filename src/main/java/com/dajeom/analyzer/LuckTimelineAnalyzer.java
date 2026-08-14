package com.dajeom.analyzer;

import com.dajeom.model.AnnualLuckSelectionAnalysis;
import com.dajeom.model.DecadeLuckCandidate;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.DecadeLuckSelectionAnalysis;
import com.dajeom.model.LuckReferenceContext;
import com.dajeom.model.LuckTimelineAnalysis;
import com.dajeom.model.LuckTimelineExtent;
import com.dajeom.model.LuckTimelineSegment;
import com.dajeom.model.MonthlyLuckCandidate;
import com.dajeom.model.MonthlyLuckSelectionAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.type.LuckTimelineDecadeState;
import org.springframework.stereotype.Component;

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
 * 통합 Timeline Analyzer.
 *
 * =================================================
 *
 * 절대 하지 않는 것:
 *
 * 대운 Candidate
 * ×
 * 세운 Candidate
 * ×
 * 월운 Candidate
 *
 * 의 단순 Cartesian Product.
 *
 * =================================================
 *
 * 월운 Candidate가 이미 자신의
 * AnnualLuckCandidate를 보유하므로:
 *
 * 실제로 필요한 조합은:
 *
 * MonthlyLuckCandidate
 * ×
 * 실제 시간적으로 겹치는 DecadeLuckCandidate
 *
 * 뿐이다.
 *
 * =================================================
 *
 * 그리고 각 조합은 반드시:
 *
 * 조회시간
 * ∩
 * 월운
 * ∩
 * 대운 possible
 *
 * 의 실제 교집합이 있어야 한다.
 */
@Component
public class LuckTimelineAnalyzer {

    /**
     * =================================================
     * 공식 16-5 API
     * =================================================
     */
    public LuckTimelineAnalysis analyze(

            PalaceAnalysis natalAnalysis,

            DecadeLuckSelectionAnalysis decadeSelectionAnalysis,

            AnnualLuckSelectionAnalysis annualSelectionAnalysis,

            MonthlyLuckSelectionAnalysis monthlySelectionAnalysis

    ) {

        Objects.requireNonNull(
                natalAnalysis,
                "16-5에는 원국 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeSelectionAnalysis,
                "16-5에는 DecadeLuckSelectionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualSelectionAnalysis,
                "16-5에는 AnnualLuckSelectionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlySelectionAnalysis,
                "16-5에는 MonthlyLuckSelectionAnalysis가 필요합니다."
        );

        LuckReferenceContext referenceContext =
                annualSelectionAnalysis
                        .referenceContext();

        validateSelections(
                referenceContext,
                decadeSelectionAnalysis,
                annualSelectionAnalysis,
                monthlySelectionAnalysis
        );

        List<LuckTimelineSegment> segments =
                new ArrayList<>();

        /*
         * =================================================
         * 대운 분석 자체가 불가능.
         * =================================================
         *
         * 세운/월운 시간축은 그대로 유지하고
         * 대운만 UNAVAILABLE로 표시한다.
         */

        if (
                !decadeSelectionAnalysis
                        .selectionAvailable()
        ) {

            for (
                    MonthlyLuckCandidate monthlyCandidate
                    : monthlySelectionAnalysis.candidates()
            ) {

                createUnavailableSegment(
                        referenceContext,
                        monthlyCandidate
                )
                        .ifPresent(
                                segments::add
                        );
            }

            return buildResult(
                    natalAnalysis,
                    referenceContext,
                    decadeSelectionAnalysis,
                    annualSelectionAnalysis,
                    monthlySelectionAnalysis,
                    segments
            );
        }

        List<DecadeLuckPeriod> decadePeriods =
                decadeSelectionAnalysis
                        .decadeLuckAnalysis()
                        .periods();

        if (
                decadePeriods.isEmpty()
        ) {

            throw new IllegalStateException(
                    "대운 Selection이 available인데 DecadeLuckPeriod가 없습니다."
            );
        }

        DecadeLuckPeriod firstDecade =
                decadePeriods.get(0);

        DecadeLuckPeriod lastDecade =
                decadePeriods.get(
                        decadePeriods.size() - 1
                );

        for (
                MonthlyLuckCandidate monthlyCandidate
                : monthlySelectionAnalysis.candidates()
        ) {

            int before =
                    segments.size();

            /*
             * =================================================
             * ACTIVE 대운 Candidate
             * =================================================
             */

            for (
                    DecadeLuckCandidate decadeCandidate
                    : decadeSelectionAnalysis.candidates()
            ) {

                createActiveSegment(
                        referenceContext,
                        monthlyCandidate,
                        decadeCandidate
                )
                        .ifPresent(
                                segments::add
                        );
            }

            /*
             * =================================================
             * FIRST DECADE 이전 상태
             * =================================================
             *
             * first earliestStart 이전:
             * 확정적으로 첫 대운 이전.
             *
             * earliestStart ~ latestStart:
             *
             * - 첫 대운일 수도 있고
             * - 아직 첫 대운 전일 수도 있다.
             *
             * 따라서 BEFORE_FIRST possible 범위는
             * latestStart까지 유지한다.
             */

            createBeforeFirstSegment(
                    referenceContext,
                    monthlyCandidate,
                    firstDecade
            )
                    .ifPresent(
                            segments::add
                    );

            /*
             * =================================================
             * 생성 대운 범위 이후
             * =================================================
             *
             * last earliestEnd부터:
             *
             * 마지막 생성 대운이 끝났을 가능성.
             *
             * last latestEnd 이후:
             *
             * 확정적으로 생성 데이터 범위를 벗어남.
             */

            createAfterGeneratedRangeSegment(
                    referenceContext,
                    monthlyCandidate,
                    lastDecade
            )
                    .ifPresent(
                            segments::add
                    );

            if (
                    segments.size()
                            == before
            ) {

                throw new IllegalStateException(
                        "MonthlyLuckCandidate에 대응하는 Timeline Segment를 생성하지 못했습니다: "
                                + monthlyCandidate.sajuYear()
                                + " / "
                                + monthlyCandidate.monthIndex()
                );
            }
        }

        return buildResult(
                natalAnalysis,
                referenceContext,
                decadeSelectionAnalysis,
                annualSelectionAnalysis,
                monthlySelectionAnalysis,
                segments
        );
    }

    /*
     * =================================================
     * ACTIVE
     * =================================================
     */

    private Optional<LuckTimelineSegment>
    createActiveSegment(

            LuckReferenceContext referenceContext,

            MonthlyLuckCandidate monthlyCandidate,

            DecadeLuckCandidate decadeCandidate

    ) {

        DecadeLuckPeriod decadePeriod =
                decadeCandidate
                        .period();

        ZonedDateTime possibleDecadeStart =
                decadePeriod
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        ZonedDateTime possibleDecadeEnd =
                decadePeriod
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        ZonedDateTime definiteDecadeStart =
                decadePeriod
                        .latestStartDateTime()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        ZonedDateTime definiteDecadeEnd =
                decadePeriod
                        .earliestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        /*
         * =================================================
         * EXACT
         * =================================================
         */

        if (
                referenceContext.exact()
        ) {

            ZonedDateTime exact =
                    referenceContext
                            .requireExactDateTime();

            if (
                    !contains(
                            exact,
                            possibleDecadeStart,
                            possibleDecadeEnd
                    )
            ) {

                return Optional.empty();
            }

            if (
                    !monthlyCandidate
                            .period()
                            .contains(
                                    exact
                            )
            ) {

                return Optional.empty();
            }

            boolean definite =
                    contains(
                            exact,
                            definiteDecadeStart,
                            definiteDecadeEnd
                    );

            return Optional.of(
                    new LuckTimelineSegment(
                            referenceContext,
                            monthlyCandidate,
                            LuckTimelineDecadeState.ACTIVE,
                            Optional.of(
                                    decadeCandidate
                            ),
                            LuckTimelineExtent.exact(
                                    exact,
                                    referenceContext.zoneId(),
                                    definite
                            )
                    )
            );
        }

        /*
         * =================================================
         * DATE_ONLY
         * =================================================
         */

        BaseInterval base =
                baseInterval(
                        referenceContext,
                        monthlyCandidate
                );

        ZonedDateTime possibleStart =
                max(
                        base.start(),
                        possibleDecadeStart
                );

        ZonedDateTime possibleEnd =
                min(
                        base.end(),
                        possibleDecadeEnd
                );

        if (
                !possibleEnd
                        .isAfter(
                                possibleStart
                        )
        ) {

            return Optional.empty();
        }

        Optional<ZonedDateTime> definiteStart =
                Optional.empty();

        Optional<ZonedDateTime> definiteEnd =
                Optional.empty();

        ZonedDateTime definiteIntersectionStart =
                max(
                        base.start(),
                        definiteDecadeStart
                );

        ZonedDateTime definiteIntersectionEnd =
                min(
                        base.end(),
                        definiteDecadeEnd
                );

        if (
                definiteIntersectionEnd
                        .isAfter(
                                definiteIntersectionStart
                        )
        ) {

            definiteStart =
                    Optional.of(
                            definiteIntersectionStart
                    );

            definiteEnd =
                    Optional.of(
                            definiteIntersectionEnd
                    );
        }

        return Optional.of(
                new LuckTimelineSegment(
                        referenceContext,
                        monthlyCandidate,
                        LuckTimelineDecadeState.ACTIVE,
                        Optional.of(
                                decadeCandidate
                        ),
                        LuckTimelineExtent.dateOnly(
                                referenceContext.zoneId(),
                                possibleStart,
                                possibleEnd,
                                definiteStart,
                                definiteEnd
                        )
                )
        );
    }

    /*
     * =================================================
     * BEFORE FIRST
     * =================================================
     */

    private Optional<LuckTimelineSegment>
    createBeforeFirstSegment(

            LuckReferenceContext referenceContext,

            MonthlyLuckCandidate monthlyCandidate,

            DecadeLuckPeriod firstDecade

    ) {

        ZonedDateTime earliestStart =
                firstDecade
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        ZonedDateTime latestStart =
                firstDecade
                        .latestStartDateTime()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        if (
                referenceContext.exact()
        ) {

            ZonedDateTime exact =
                    referenceContext
                            .requireExactDateTime();

            if (
                    !exact.isBefore(
                            latestStart
                    )
            ) {

                return Optional.empty();
            }

            if (
                    !monthlyCandidate
                            .period()
                            .contains(
                                    exact
                            )
            ) {

                return Optional.empty();
            }

            boolean definite =
                    exact.isBefore(
                            earliestStart
                    );

            return Optional.of(
                    new LuckTimelineSegment(
                            referenceContext,
                            monthlyCandidate,
                            LuckTimelineDecadeState.BEFORE_FIRST,
                            Optional.empty(),
                            LuckTimelineExtent.exact(
                                    exact,
                                    referenceContext.zoneId(),
                                    definite
                            )
                    )
            );
        }

        BaseInterval base =
                baseInterval(
                        referenceContext,
                        monthlyCandidate
                );

        ZonedDateTime possibleStart =
                base.start();

        ZonedDateTime possibleEnd =
                min(
                        base.end(),
                        latestStart
                );

        if (
                !possibleEnd
                        .isAfter(
                                possibleStart
                        )
        ) {

            return Optional.empty();
        }

        Optional<ZonedDateTime> definiteStart =
                Optional.empty();

        Optional<ZonedDateTime> definiteEnd =
                Optional.empty();

        ZonedDateTime definiteEndValue =
                min(
                        base.end(),
                        earliestStart
                );

        if (
                definiteEndValue
                        .isAfter(
                                base.start()
                        )
        ) {

            definiteStart =
                    Optional.of(
                            base.start()
                    );

            definiteEnd =
                    Optional.of(
                            definiteEndValue
                    );
        }

        return Optional.of(
                new LuckTimelineSegment(
                        referenceContext,
                        monthlyCandidate,
                        LuckTimelineDecadeState.BEFORE_FIRST,
                        Optional.empty(),
                        LuckTimelineExtent.dateOnly(
                                referenceContext.zoneId(),
                                possibleStart,
                                possibleEnd,
                                definiteStart,
                                definiteEnd
                        )
                )
        );
    }

    /*
     * =================================================
     * AFTER GENERATED RANGE
     * =================================================
     */

    private Optional<LuckTimelineSegment>
    createAfterGeneratedRangeSegment(

            LuckReferenceContext referenceContext,

            MonthlyLuckCandidate monthlyCandidate,

            DecadeLuckPeriod lastDecade

    ) {

        ZonedDateTime earliestEnd =
                lastDecade
                        .earliestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        ZonedDateTime latestEnd =
                lastDecade
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                referenceContext.zoneId()
                        );

        if (
                referenceContext.exact()
        ) {

            ZonedDateTime exact =
                    referenceContext
                            .requireExactDateTime();

            if (
                    exact.isBefore(
                            earliestEnd
                    )
            ) {

                return Optional.empty();
            }

            if (
                    !monthlyCandidate
                            .period()
                            .contains(
                                    exact
                            )
            ) {

                return Optional.empty();
            }

            boolean definite =
                    !exact.isBefore(
                            latestEnd
                    );

            return Optional.of(
                    new LuckTimelineSegment(
                            referenceContext,
                            monthlyCandidate,
                            LuckTimelineDecadeState.AFTER_GENERATED_RANGE,
                            Optional.empty(),
                            LuckTimelineExtent.exact(
                                    exact,
                                    referenceContext.zoneId(),
                                    definite
                            )
                    )
            );
        }

        BaseInterval base =
                baseInterval(
                        referenceContext,
                        monthlyCandidate
                );

        ZonedDateTime possibleStart =
                max(
                        base.start(),
                        earliestEnd
                );

        ZonedDateTime possibleEnd =
                base.end();

        if (
                !possibleEnd
                        .isAfter(
                                possibleStart
                        )
        ) {

            return Optional.empty();
        }

        Optional<ZonedDateTime> definiteStart =
                Optional.empty();

        Optional<ZonedDateTime> definiteEnd =
                Optional.empty();

        ZonedDateTime definiteStartValue =
                max(
                        base.start(),
                        latestEnd
                );

        if (
                base.end()
                        .isAfter(
                                definiteStartValue
                        )
        ) {

            definiteStart =
                    Optional.of(
                            definiteStartValue
                    );

            definiteEnd =
                    Optional.of(
                            base.end()
                    );
        }

        return Optional.of(
                new LuckTimelineSegment(
                        referenceContext,
                        monthlyCandidate,
                        LuckTimelineDecadeState.AFTER_GENERATED_RANGE,
                        Optional.empty(),
                        LuckTimelineExtent.dateOnly(
                                referenceContext.zoneId(),
                                possibleStart,
                                possibleEnd,
                                definiteStart,
                                definiteEnd
                        )
                )
        );
    }

    /*
     * =================================================
     * UNAVAILABLE
     * =================================================
     */

    private Optional<LuckTimelineSegment>
    createUnavailableSegment(

            LuckReferenceContext referenceContext,

            MonthlyLuckCandidate monthlyCandidate

    ) {

        if (
                referenceContext.exact()
        ) {

            ZonedDateTime exact =
                    referenceContext
                            .requireExactDateTime();

            if (
                    !monthlyCandidate
                            .period()
                            .contains(
                                    exact
                            )
            ) {

                return Optional.empty();
            }

            /*
             * 대운 계층이 없으므로
             * 통합 조합을 확정할 수 없다.
             */
            return Optional.of(
                    new LuckTimelineSegment(
                            referenceContext,
                            monthlyCandidate,
                            LuckTimelineDecadeState.UNAVAILABLE,
                            Optional.empty(),
                            LuckTimelineExtent.exact(
                                    exact,
                                    referenceContext.zoneId(),
                                    false
                            )
                    )
            );
        }

        BaseInterval base =
                baseInterval(
                        referenceContext,
                        monthlyCandidate
                );

        if (
                !base.end()
                        .isAfter(
                                base.start()
                        )
        ) {

            return Optional.empty();
        }

        return Optional.of(
                new LuckTimelineSegment(
                        referenceContext,
                        monthlyCandidate,
                        LuckTimelineDecadeState.UNAVAILABLE,
                        Optional.empty(),
                        LuckTimelineExtent.dateOnly(
                                referenceContext.zoneId(),
                                base.start(),
                                base.end(),
                                Optional.empty(),
                                Optional.empty()
                        )
                )
        );
    }

    /*
     * =================================================
     * 월운 + 조회기준 기본 교집합
     * =================================================
     */

    private BaseInterval baseInterval(

            LuckReferenceContext referenceContext,

            MonthlyLuckCandidate monthlyCandidate

    ) {

        ZonedDateTime start =
                max(
                        referenceContext
                                .possibleStartInclusive(),
                        monthlyCandidate
                                .startDateTimeInclusive()
                );

        ZonedDateTime end =
                min(
                        referenceContext
                                .possibleEndExclusive(),
                        monthlyCandidate
                                .endDateTimeExclusive()
                );

        if (
                !end.isAfter(
                        start
                )
        ) {

            throw new IllegalStateException(
                    "16-4 MonthlyLuckCandidate가 조회 날짜와 실제 시간교집합을 가지지 않습니다."
            );
        }

        return new BaseInterval(
                start,
                end
        );
    }

    /*
     * =================================================
     * Input validation
     * =================================================
     */

    private void validateSelections(

            LuckReferenceContext referenceContext,

            DecadeLuckSelectionAnalysis decadeSelectionAnalysis,

            AnnualLuckSelectionAnalysis annualSelectionAnalysis,

            MonthlyLuckSelectionAnalysis monthlySelectionAnalysis

    ) {

        if (
                decadeSelectionAnalysis.referenceContext()
                        != referenceContext
        ) {

            throw new IllegalArgumentException(
                    "16-2 대운 Selection이 다른 LuckReferenceContext를 참조합니다."
            );
        }

        if (
                monthlySelectionAnalysis.referenceContext()
                        != referenceContext
        ) {

            throw new IllegalArgumentException(
                    "16-4 월운 Selection이 다른 LuckReferenceContext를 참조합니다."
            );
        }

        if (
                monthlySelectionAnalysis.annualSelectionAnalysis()
                        != annualSelectionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "16-4 월운 Selection이 현재 16-3 세운 Selection을 참조하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Build
     * =================================================
     */

    private LuckTimelineAnalysis buildResult(

            PalaceAnalysis natalAnalysis,

            LuckReferenceContext referenceContext,

            DecadeLuckSelectionAnalysis decadeSelectionAnalysis,

            AnnualLuckSelectionAnalysis annualSelectionAnalysis,

            MonthlyLuckSelectionAnalysis monthlySelectionAnalysis,

            List<LuckTimelineSegment> segments

    ) {

        segments.sort(
                Comparator.comparing(
                        segment -> {
                            if (
                                    segment
                                            .extent()
                                            .exact()
                            ) {

                                return segment
                                        .extent()
                                        .exactDateTime()
                                        .orElseThrow()
                                        .toInstant();
                            }

                            return segment
                                    .extent()
                                    .possibleStartInclusive()
                                    .orElseThrow()
                                    .toInstant();
                        }
                )
        );

        return new LuckTimelineAnalysis(
                natalAnalysis,
                referenceContext,
                decadeSelectionAnalysis,
                annualSelectionAnalysis,
                monthlySelectionAnalysis,
                segments
        );
    }

    /*
     * =================================================
     * Helpers
     * =================================================
     */

    private boolean contains(

            ZonedDateTime value,

            ZonedDateTime startInclusive,

            ZonedDateTime endExclusive

    ) {

        return !value
                .isBefore(
                        startInclusive
                )

                && value
                .isBefore(
                        endExclusive
                );
    }

    private ZonedDateTime max(

            ZonedDateTime first,

            ZonedDateTime second

    ) {

        return first.isAfter(
                second
        )
                ? first
                : second;
    }

    private ZonedDateTime min(

            ZonedDateTime first,

            ZonedDateTime second

    ) {

        return first.isBefore(
                second
        )
                ? first
                : second;
    }

    private record BaseInterval(

            ZonedDateTime start,

            ZonedDateTime end

    ) {
    }
}