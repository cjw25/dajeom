package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.LuckTimelineDecadeState;
import com.dajeom.type.LuckUncertaintyCause;
import com.dajeom.type.LuckUncertaintyScope;
import com.dajeom.type.PalacePosition;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-7.
 *
 * 원국 + 대운 + 세운 + 월운
 * 최종 통합 분석 Aggregate.
 *
 * =================================================
 *
 * 포함:
 *
 * 원국 PalaceAnalysis
 * 13차 DecadeLuckAnalysis
 * 16-1 LuckReferenceContext
 * 16-2 DecadeLuckSelectionAnalysis
 * 16-3 AnnualLuckSelectionAnalysis
 * 16-4 MonthlyLuckSelectionAnalysis
 * 16-5 LuckTimelineAnalysis
 * 16-6 LuckUncertaintyAnalysis
 * 14차 AnnualLuckAnalysis
 * 15차 MonthlyLuckAnalysis
 *
 * =================================================
 *
 * 이후:
 *
 * 17차 신살
 * 18차 생활영역
 * 19차 고민분석
 * 20차 AI Grounding
 *
 * 의 운 분석 입력 Anchor가 된다.
 */
public record LuckIntegratedAnalysis(

        PalaceAnalysis natalAnalysis,

        DecadeLuckAnalysis decadeLuckAnalysis,

        LuckTimelineAnalysis timelineAnalysis,

        LuckUncertaintyAnalysis uncertaintyAnalysis,

        List<LuckIntegratedSegmentAnalysis> segmentAnalyses

) {

    public LuckIntegratedAnalysis {

        Objects.requireNonNull(
                natalAnalysis,
                "통합 운 분석에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "통합 운 분석에는 DecadeLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                timelineAnalysis,
                "통합 운 분석에는 LuckTimelineAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                uncertaintyAnalysis,
                "통합 운 분석에는 LuckUncertaintyAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 원국 Identity
         * =================================================
         */

        if (
                timelineAnalysis.natalAnalysis()
                        != natalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "16-5 Timeline과 최종 통합 분석이 서로 다른 원국을 참조합니다."
            );
        }

        /*
         * =================================================
         * 대운 Identity
         * =================================================
         */

        if (
                timelineAnalysis
                        .decadeSelectionAnalysis()
                        .decadeLuckAnalysis()
                        != decadeLuckAnalysis
        ) {

            throw new IllegalArgumentException(
                    "16-5 Timeline과 최종 통합 분석이 서로 다른 DecadeLuckAnalysis를 참조합니다."
            );
        }

        /*
         * =================================================
         * Uncertainty Identity
         * =================================================
         */

        if (
                uncertaintyAnalysis.timelineAnalysis()
                        != timelineAnalysis
        ) {

            throw new IllegalArgumentException(
                    "16-6 불확실성 분석이 현재 LuckTimelineAnalysis를 참조하지 않습니다."
            );
        }

        segmentAnalyses =
                segmentAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        segmentAnalyses
                );

        if (
                segmentAnalyses.size()
                        != timelineAnalysis
                        .segments()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "Timeline Segment 수와 통합 Segment 분석 수가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Timeline 순서와 정확히 일치
         * =================================================
         */

        for (
                int index = 0;
                index < segmentAnalyses.size();
                index++
        ) {

            LuckIntegratedSegmentAnalysis integrated =
                    Objects.requireNonNull(
                            segmentAnalyses.get(index),
                            "LuckIntegratedSegmentAnalysis는 null일 수 없습니다."
                    );

            LuckTimelineSegment timelineSegment =
                    timelineAnalysis
                            .segments()
                            .get(index);

            if (
                    integrated.timelineSegment()
                            != timelineSegment
            ) {

                throw new IllegalArgumentException(
                        "통합 Segment 분석 순서가 16-5 Timeline 순서와 일치하지 않습니다: "
                                + index
                );
            }

            if (
                    integrated.natalAnalysis()
                            != natalAnalysis
            ) {

                throw new IllegalArgumentException(
                        "통합 Segment가 다른 원국을 참조합니다."
                );
            }

            if (
                    integrated.decadeLuckAnalysis()
                            != decadeLuckAnalysis
            ) {

                throw new IllegalArgumentException(
                        "통합 Segment가 다른 DecadeLuckAnalysis를 참조합니다."
                );
            }
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public HeavenlyStem dayMaster() {

        return natalAnalysis
                .dayMaster();
    }

    public LuckReferenceContext referenceContext() {

        return timelineAnalysis
                .referenceContext();
    }

    public ZoneId zoneId() {

        return referenceContext()
                .zoneId();
    }

    public boolean completeChart() {

        return natalAnalysis
                .completeChart();
    }

    /*
     * =================================================
     * Selection
     * =================================================
     */

    public DecadeLuckSelectionAnalysis
    decadeSelectionAnalysis() {

        return timelineAnalysis
                .decadeSelectionAnalysis();
    }

    public AnnualLuckSelectionAnalysis
    annualSelectionAnalysis() {

        return timelineAnalysis
                .annualSelectionAnalysis();
    }

    public MonthlyLuckSelectionAnalysis
    monthlySelectionAnalysis() {

        return timelineAnalysis
                .monthlySelectionAnalysis();
    }

    /*
     * =================================================
     * Timeline
     * =================================================
     */

    public List<LuckTimelineSegment>
    timelineSegments() {

        return timelineAnalysis
                .segments();
    }

    public List<LuckIntegratedSegmentAnalysis>
    activeDecadeSegments() {

        return segmentAnalyses
                .stream()
                .filter(
                        LuckIntegratedSegmentAnalysis
                                ::hasActiveDecade
                )
                .toList();
    }

    public List<LuckIntegratedSegmentAnalysis>
    segmentsOf(
            LuckTimelineDecadeState state
    ) {

        if (
                state == null
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                segment.decadeState()
                                        == state
                )
                .toList();
    }

    /*
     * =================================================
     * 특정 시각
     * =================================================
     */

    public List<LuckIntegratedSegmentAnalysis>
    possibleSegmentsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
        ) {

            return List.of();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                segment.possiblyActiveAt(
                                        dateTime
                                )
                )
                .toList();
    }

    public Optional<LuckIntegratedSegmentAnalysis>
    definiteSegmentAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
        ) {

            return Optional.empty();
        }

        List<LuckIntegratedSegmentAnalysis> matched =
                segmentAnalyses
                        .stream()
                        .filter(
                                segment ->
                                        segment.definitelyActiveAt(
                                                dateTime
                                        )
                        )
                        .toList();

        if (
                matched.size() > 1
        ) {

            throw new IllegalStateException(
                    "동일 시각에 확정 LuckIntegratedSegmentAnalysis가 2개 이상 존재합니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }

    /*
     * =================================================
     * 정확한 조회
     * =================================================
     */

    public List<LuckIntegratedSegmentAnalysis>
    exactPossibleSegments() {

        if (
                !referenceContext()
                        .exact()
        ) {

            return List.of();
        }

        return possibleSegmentsAt(
                referenceContext()
                        .requireExactDateTime()
        );
    }

    public Optional<LuckIntegratedSegmentAnalysis>
    exactDefiniteSegment() {

        if (
                !referenceContext()
                        .exact()
        ) {

            return Optional.empty();
        }

        return definiteSegmentAt(
                referenceContext()
                        .requireExactDateTime()
        );
    }

    /*
     * =================================================
     * 14차 분석
     * =================================================
     *
     * Timeline에 같은 세운이 여러 Segment에서
     * 반복될 수 있으므로 Identity 기준으로 중복 제거.
     */

    public List<AnnualLuckAnalysis>
    annualLuckAnalyses() {

        List<AnnualLuckAnalysis> result =
                new ArrayList<>();

        for (
                LuckIntegratedSegmentAnalysis segment
                : segmentAnalyses
        ) {

            AnnualLuckAnalysis analysis =
                    segment
                            .annualLuckAnalysis();

            boolean exists =
                    result
                            .stream()
                            .anyMatch(
                                    existing ->
                                            existing
                                                    == analysis
                            );

            if (
                    !exists
            ) {

                result.add(
                        analysis
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 15차 분석
     * =================================================
     */

    public List<MonthlyLuckAnalysis>
    monthlyLuckAnalyses() {

        List<MonthlyLuckAnalysis> result =
                new ArrayList<>();

        for (
                LuckIntegratedSegmentAnalysis segment
                : segmentAnalyses
        ) {

            MonthlyLuckAnalysis analysis =
                    segment
                            .monthlyLuckAnalysis();

            boolean exists =
                    result
                            .stream()
                            .anyMatch(
                                    existing ->
                                            existing
                                                    == analysis
                            );

            if (
                    !exists
            ) {

                result.add(
                        analysis
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 특정 세운
     * =================================================
     */

    public Optional<AnnualLuckAnalysis>
    annualAnalysisOf(
            int sajuYear
    ) {

        return annualLuckAnalyses()
                .stream()
                .filter(
                        analysis ->
                                analysis.targetYear()
                                        == sajuYear
                )
                .findFirst();
    }

    /*
     * =================================================
     * 특정 월운
     * =================================================
     */

    public Optional<MonthlyLuckAnalysis>
    monthlyAnalysisOf(

            int sajuYear,

            int monthIndex

    ) {

        return monthlyLuckAnalyses()
                .stream()
                .filter(
                        analysis ->
                                analysis.sajuYear()
                                        == sajuYear

                                        && analysis.monthIndex()
                                        == monthIndex
                )
                .findFirst();
    }

    /*
     * =================================================
     * 불확실성
     * =================================================
     */

    public boolean uncertaintyExists() {

        return uncertaintyAnalysis
                .uncertaintyExists();
    }

    public boolean fullyDeterminate() {

        return uncertaintyAnalysis
                .fullyDeterminate();
    }

    public boolean hasLimitations() {

        return uncertaintyAnalysis
                .hasLimitations();
    }

    public boolean hasUncertaintyCause(
            LuckUncertaintyCause cause
    ) {

        return uncertaintyAnalysis
                .hasCause(
                        cause
                );
    }

    public List<LuckUncertaintyEvidence>
    uncertaintiesOf(
            LuckUncertaintyScope scope
    ) {

        return uncertaintyAnalysis
                .evidencesOf(
                        scope
                );
    }

    /*
     * =================================================
     * 원국 Palace
     * =================================================
     */

    public boolean hasHourPalace() {

        return completeChart();
    }

    public boolean involvesHourPalace(
            PalacePosition position
    ) {

        return position
                == PalacePosition.HOUR
                && completeChart();
    }
}