package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-8.
 *
 * 하나의 세운 안의
 * 12개 최종 월운 분석 Sequence.
 *
 * =================================================
 *
 * MonthlyLuckSequenceAnalysis
 *
 * =
 * 절입경계 + 월운 간지만 가진
 * 15-2 Sequence.
 *
 * =================================================
 *
 * MonthlyLuckAnalysisSequence
 *
 * =
 * 각 월운에 대해:
 *
 * 15-3 십성
 * 15-4 원국 관계
 * 15-5 대운 관계
 * 15-6 세운 관계
 * 15-7 Context
 *
 * 까지 모두 완료된 최종 Sequence.
 */
public record MonthlyLuckAnalysisSequence(

        AnnualLuckAnalysis annualLuckAnalysis,

        MonthlyLuckSequenceAnalysis periodSequence,

        List<MonthlyLuckAnalysis> analyses

) {

    private static final int
            MONTH_COUNT = 12;

    public MonthlyLuckAnalysisSequence {

        Objects.requireNonNull(
                annualLuckAnalysis,
                "월운 최종 Sequence에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                periodSequence,
                "월운 최종 Sequence에는 MonthlyLuckSequenceAnalysis가 필요합니다."
        );

        analyses =
                analyses == null
                        ? List.of()
                        : List.copyOf(
                        analyses
                );

        /*
         * =================================================
         * 동일 세운 Identity
         * =================================================
         */

        if (
                periodSequence.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "월운 Period Sequence와 AnnualLuckAnalysis가 서로 다른 세운을 참조합니다."
            );
        }

        /*
         * =================================================
         * 정확히 12개월
         * =================================================
         */

        if (
                analyses.size()
                        != MONTH_COUNT
        ) {

            throw new IllegalArgumentException(
                    "하나의 세운에는 정확히 12개의 MonthlyLuckAnalysis가 필요합니다: "
                            + analyses.size()
            );
        }

        if (
                periodSequence.periodCount()
                        != MONTH_COUNT
        ) {

            throw new IllegalArgumentException(
                    "MonthlyLuckSequenceAnalysis가 12개 월운으로 구성되지 않았습니다."
            );
        }

        /*
         * =================================================
         * 각 월운 Identity
         * =================================================
         */

        for (
                int index = 0;
                index < analyses.size();
                index++
        ) {

            MonthlyLuckAnalysis analysis =
                    Objects.requireNonNull(
                            analyses.get(index),
                            "MonthlyLuckAnalysis는 null일 수 없습니다."
                    );

            if (
                    analysis.annualLuckAnalysis()
                            != annualLuckAnalysis
            ) {

                throw new IllegalArgumentException(
                        "다른 AnnualLuckAnalysis의 월운 분석이 포함되었습니다."
                );
            }

            if (
                    analysis.monthIndex()
                            != index
            ) {

                throw new IllegalArgumentException(
                        "월운 분석 순서가 잘못되었습니다. "
                                + "예상 monthIndex="
                                + index
                                + ", 실제="
                                + analysis.monthIndex()
                );
            }

            MonthlyLuckPeriod expectedPeriod =
                    periodSequence
                            .periodByIndex(
                                    index
                            )
                            .orElseThrow();

            if (
                    analysis.period()
                            != expectedPeriod
            ) {

                throw new IllegalArgumentException(
                        "최종 월운 분석이 15-2 Sequence의 해당 MonthlyLuckPeriod를 참조하지 않습니다: "
                                + index
                );
            }
        }
    }

    public int sajuYear() {

        return annualLuckAnalysis
                .targetYear();
    }

    public SexagenaryPillar annualPillar() {

        return annualLuckAnalysis
                .annualPillar();
    }

    public int analysisCount() {

        return analyses.size();
    }

    /*
     * =================================================
     * monthIndex 0~11
     * =================================================
     */

    public Optional<MonthlyLuckAnalysis>
    analysisByIndex(
            int monthIndex
    ) {

        if (
                monthIndex < 0
                        || monthIndex >= MONTH_COUNT
        ) {

            return Optional.empty();
        }

        return Optional.of(
                analyses.get(
                        monthIndex
                )
        );
    }

    /*
     * =================================================
     * 순번 1~12
     * =================================================
     */

    public Optional<MonthlyLuckAnalysis>
    analysisByOrder(
            int monthOrder
    ) {

        if (
                monthOrder < 1
                        || monthOrder > MONTH_COUNT
        ) {

            return Optional.empty();
        }

        return analysisByIndex(
                monthOrder - 1
        );
    }

    /*
     * =================================================
     * 특정 시점의 월운 분석
     * =================================================
     */

    public Optional<MonthlyLuckAnalysis>
    analysisAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !annualLuckAnalysis.contains(
                        dateTime
                )
        ) {

            return Optional.empty();
        }

        List<MonthlyLuckAnalysis> matched =
                analyses
                        .stream()
                        .filter(
                                analysis ->
                                        analysis.contains(
                                                dateTime
                                        )
                        )
                        .toList();

        if (
                matched.size()
                        > 1
        ) {

            throw new IllegalStateException(
                    "동일 시각에 MonthlyLuckAnalysis가 2개 이상 존재합니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }

    /*
     * =================================================
     * 대운 Context 상태
     * =================================================
     */

    public boolean allDecadeContextsAvailable() {

        return analyses
                .stream()
                .allMatch(
                        MonthlyLuckAnalysis
                                ::decadeContextAvailable
                );
    }

    public boolean hasDecadeContextUnavailableMonth() {

        return analyses
                .stream()
                .anyMatch(
                        analysis ->
                                !analysis
                                        .decadeContextAvailable()
                );
    }

    public List<MonthlyLuckAnalysis>
    decadeContextUnavailableAnalyses() {

        return analyses
                .stream()
                .filter(
                        analysis ->
                                !analysis
                                        .decadeContextAvailable()
                )
                .toList();
    }

    /*
     * =================================================
     * 대운 교체 월
     * =================================================
     */

    public List<MonthlyLuckAnalysis>
    decadeTransitionMonths() {

        return analyses
                .stream()
                .filter(
                        MonthlyLuckAnalysis
                                ::multipleDecadesInMonthlyPeriod
                )
                .toList();
    }

    public boolean hasDecadeTransitionMonth() {

        return !decadeTransitionMonths()
                .isEmpty();
    }

    /*
     * =================================================
     * 시간 불확실 월
     * =================================================
     */

    public List<MonthlyLuckAnalysis>
    timingUncertainMonths() {

        return analyses
                .stream()
                .filter(
                        MonthlyLuckAnalysis
                                ::decadeTimingUncertain
                )
                .toList();
    }

    public boolean hasTimingUncertainty() {

        return !timingUncertainMonths()
                .isEmpty();
    }
}