package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Monthly Functional Activation 최종 Aggregate.
 *
 * =================================================
 *
 * 공식 기준선:
 *
 * AnnualFunctionalBaselineAnalysis
 *
 * =================================================
 *
 * 실제 월운:
 *
 * MonthlyLuckAnalysis
 *
 * =================================================
 *
 * 결과:
 *
 * 1.
 * annualEffects
 *
 * 세운 자체 5 Function과
 * 월운 공통 입력 / 공통 Context의 접촉.
 *
 * 2.
 * segmentAnalyses
 *
 * 현재 월운 기간과 실제로 겹치는
 * 각 Annual-Decade Segment별
 * 5 Function Activation.
 *
 * =================================================
 *
 * 중요 원칙:
 *
 * - Annual Functional Baseline을 변경하지 않는다.
 * - 월운으로 신강신약을 재계산하지 않는다.
 * - 월운으로 용신을 재선정하지 않는다.
 * - 월운 TenGod 개수를 강도 점수로 사용하지 않는다.
 * - 교운 시 여러 대운 Segment를 평균내지 않는다.
 * - 임의의 대운 Segment를 선택하지 않는다.
 * - Global Context는 특정 Function으로 강제 연결하지 않는다.
 */
public record MonthlyFunctionalActivationAnalysis(

        AnnualFunctionalBaselineAnalysis
        annualFunctionalBaseline,

        MonthlyLuckAnalysis
        monthlyLuckAnalysis,

        List<MonthlyFunctionalActivationEffect>
        annualEffects,

        List<MonthlyFunctionalActivationSegmentAnalysis>
        segmentAnalyses,

        boolean completeChart

) {

    public MonthlyFunctionalActivationAnalysis {

        Objects.requireNonNull(
                annualFunctionalBaseline,
                "Monthly Functional Activation에는 Annual Functional Baseline이 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckAnalysis,
                "Monthly Functional Activation에는 MonthlyLuckAnalysis가 필요합니다."
        );


        annualEffects =
                annualEffects == null
                        ? List.of()
                        : List.copyOf(
                        annualEffects
                );


        segmentAnalyses =
                segmentAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        segmentAnalyses
                );


        /*
         * =================================================
         * AnnualLuckAnalysis Identity
         * =================================================
         */

        if (
                annualFunctionalBaseline
                        .annualLuckAnalysis()
                        != monthlyLuckAnalysis
                        .annualLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Activation의 Annual Baseline과 MonthlyLuckAnalysis가 "
                            + "서로 다른 AnnualLuckAnalysis Identity를 참조합니다."
            );
        }


        if (
                annualFunctionalBaseline.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Baseline과 Monthly Functional Activation의 completeChart가 일치하지 않습니다."
            );
        }


        /*
         * =================================================
         * 5 Annual Common Function Coverage
         * =================================================
         */

        if (
                annualEffects.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Activation에는 정확히 5개의 Annual Common Effect가 필요합니다. actual="
                            + annualEffects.size()
            );
        }


        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            List<MonthlyFunctionalActivationEffect> matched =
                    annualEffects
                            .stream()
                            .filter(
                                    effect ->
                                            effect.functionType()
                                                    == functionType
                            )
                            .toList();


            if (
                    matched.size()
                            != 1
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Annual Effect Coverage가 올바르지 않습니다. function="
                                + functionType
                                + ", actual="
                                + matched.size()
                );
            }


            MonthlyFunctionalActivationEffect effect =
                    matched.get(0);


            if (
                    !effect.annualCommonScoped()
            ) {

                throw new IllegalArgumentException(
                        "Annual Common Effect에는 Segment Resolved Effect를 연결할 수 없습니다. function="
                                + functionType
                );
            }


            if (
                    effect.monthlyLuckAnalysis()
                            != monthlyLuckAnalysis
            ) {

                throw new IllegalArgumentException(
                        "Annual Common Effect가 현재 MonthlyLuckAnalysis Identity를 보존하지 않았습니다."
                );
            }


            if (
                    effect.annualFunctionContext()
                            != annualFunctionalBaseline
                            .requireAnnualFunctionContext(
                                    functionType
                            )
            ) {

                throw new IllegalArgumentException(
                        "Annual Common Effect가 공식 Annual Function Context Identity를 보존하지 않았습니다. function="
                                + functionType
                );
            }


            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual Common Effect의 completeChart가 일치하지 않습니다."
                );
            }
        }


        /*
         * =================================================
         * Segment Coverage
         * =================================================
         *
         * Monthly Context가 사용 가능한 경우,
         * 현재 월운에 실제로 겹치는 decadeContexts와
         * 정확히 1:1로 연결되어야 한다.
         */

        if (
                monthlyLuckAnalysis
                        .decadeContextAvailable()
        ) {

            if (
                    segmentAnalyses.size()
                            != monthlyLuckAnalysis
                            .decadeContexts()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Monthly Decade Context 수와 Monthly Functional Segment 수가 일치하지 않습니다."
                );
            }


            for (
                    MonthlyLuckDecadeContextAnalysis monthlyContext
                    : monthlyLuckAnalysis
                    .decadeContexts()
            ) {

                List<MonthlyFunctionalActivationSegmentAnalysis>
                        matched =
                        segmentAnalyses
                                .stream()
                                .filter(
                                        segment ->
                                                segment.monthlyDecadeContext()
                                                        == monthlyContext
                                )
                                .toList();


                if (
                        matched.size()
                                != 1
                ) {

                    throw new IllegalArgumentException(
                            "Monthly Decade Context에 대응하는 Monthly Functional Segment가 정확히 하나여야 합니다. sequence="
                                    + monthlyContext
                                    .decadeSequenceNumber()
                    );
                }
            }
        }


        if (
                !monthlyLuckAnalysis
                        .decadeContextAvailable()

                        && !segmentAnalyses
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Monthly Decade Context 사용 불가 상태에서는 Segment Functional Activation을 생성할 수 없습니다."
            );
        }


        /*
         * =================================================
         * Segment Identity
         * =================================================
         */

        for (
                MonthlyFunctionalActivationSegmentAnalysis segment
                : segmentAnalyses
        ) {

            Objects.requireNonNull(
                    segment,
                    "Monthly Functional Segment는 null일 수 없습니다."
            );


            if (
                    segment.monthlyLuckAnalysis()
                            != monthlyLuckAnalysis
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Segment가 현재 MonthlyLuckAnalysis Identity를 보존하지 않았습니다."
                );
            }


            Optional<AnnualCrossFunctionalSynthesisSegmentAnalysis>
                    expectedAnnualSegment =
                    annualFunctionalBaseline
                            .segmentOfDecade(
                                    segment
                                            .decadeSequenceNumber()
                            );


            if (
                    expectedAnnualSegment.isEmpty()
                            ||
                            expectedAnnualSegment.get()
                                    != segment.annualSegment()
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Segment가 공식 Annual Functional Segment Identity를 보존하지 않았습니다. sequence="
                                + segment.decadeSequenceNumber()
                );
            }


            if (
                    segment.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Segment의 completeChart가 일치하지 않습니다."
                );
            }
        }
    }


    /*
     * =================================================
     * Identity
     * =================================================
     */

    public int targetYear() {

        return annualFunctionalBaseline
                .targetYear();
    }


    public int sajuYear() {

        return monthlyLuckAnalysis
                .sajuYear();
    }


    public int monthIndex() {

        return monthlyLuckAnalysis
                .monthIndex();
    }


    public MonthlyLuckPeriod period() {

        return monthlyLuckAnalysis
                .period();
    }


    /*
     * =================================================
     * Annual Common Function
     * =================================================
     */

    public Optional<MonthlyFunctionalActivationEffect>
    annualEffectOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }


        return annualEffects
                .stream()
                .filter(
                        effect ->
                                effect.functionType()
                                        == functionType
                )
                .findFirst();
    }


    public MonthlyFunctionalActivationEffect
    requireAnnualEffect(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "Monthly Functional Annual Effect 조회에는 Function Type이 필요합니다."
        );


        return annualEffectOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Monthly Functional Annual Effect를 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }


    public List<MonthlyFunctionalActivationEffect>
    contactedAnnualEffects() {

        return annualEffects
                .stream()
                .filter(
                        MonthlyFunctionalActivationEffect
                                ::monthlyContactExists
                )
                .toList();
    }


    public List<MonthlyFunctionalActivationEffect>
    annualBaselineContacts() {

        return annualEffects
                .stream()
                .filter(
                        MonthlyFunctionalActivationEffect
                                ::contactOnActiveAnnualBaseline
                )
                .toList();
    }


    public List<MonthlyFunctionalActivationEffect>
    inactiveBaselineMonthlyContacts() {

        return annualEffects
                .stream()
                .filter(
                        MonthlyFunctionalActivationEffect
                                ::monthlyContactAgainstInactiveAnnualBaseline
                )
                .toList();
    }


    /*
     * =================================================
     * Global Monthly Context
     * =================================================
     *
     * tenGodTypes가 비어 있어 특정 Function에
     * 강제로 연결하지 않은 Signal.
     *
     * 삭제하지 않고 Aggregate 수준에서 보존한다.
     */

    public List<MonthlyLuckContextSignal>
    globalMonthlySignals() {

        return monthlyLuckAnalysis
                .monthlyContextSignals()
                .stream()
                .filter(
                        signal ->
                                signal
                                        .tenGodTypes()
                                        .isEmpty()
                )
                .toList();
    }


    /*
     * =================================================
     * Segment
     * =================================================
     */

    public boolean decadeSegmentAvailable() {

        return !segmentAnalyses
                .isEmpty();
    }


    public Optional<MonthlyFunctionalActivationSegmentAnalysis>
    segmentOfDecade(
            int sequenceNumber
    ) {

        if (
                sequenceNumber < 1
        ) {

            return Optional.empty();
        }


        return segmentAnalyses
                .stream()
                .filter(
                        segment ->
                                segment
                                        .decadeSequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }


    /*
     * =================================================
     * 특정 시점 Possible Segment
     * =================================================
     *
     * Monthly Context와 Annual Functional Baseline 양쪽에서
     * 실제 가능한 Segment만 반환한다.
     */

    public List<MonthlyFunctionalActivationSegmentAnalysis>
    possibleSegmentsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !period()
                        .contains(
                                dateTime
                        )
                        || segmentAnalyses
                        .isEmpty()
        ) {

            return List.of();
        }


        List<MonthlyLuckDecadeContextAnalysis>
                monthlyPossible =
                monthlyLuckAnalysis
                        .possibleContextsAt(
                                dateTime
                        );


        if (
                monthlyPossible.isEmpty()
        ) {

            return List.of();
        }


        List<AnnualCrossFunctionalSynthesisSegmentAnalysis>
                annualPossible =
                annualFunctionalBaseline
                        .possibleSegmentsAt(
                                dateTime
                        );


        List<MonthlyFunctionalActivationSegmentAnalysis>
                result =
                new ArrayList<>();


        for (
                MonthlyLuckDecadeContextAnalysis monthlyContext
                : monthlyPossible
        ) {

            Optional<MonthlyFunctionalActivationSegmentAnalysis>
                    activationSegment =
                    segmentOfDecade(
                            monthlyContext
                                    .decadeSequenceNumber()
                    );


            if (
                    activationSegment.isEmpty()
            ) {

                throw new IllegalStateException(
                        "Possible Monthly Decade Context에 대응하는 Monthly Functional Segment가 없습니다. sequence="
                                + monthlyContext
                                .decadeSequenceNumber()
                );
            }


            AnnualCrossFunctionalSynthesisSegmentAnalysis
                    sourceAnnualSegment =
                    activationSegment
                            .get()
                            .annualSegment();


            boolean annualPossibleExists =
                    annualPossible
                            .stream()
                            .anyMatch(
                                    annualSegment ->
                                            annualSegment
                                                    == sourceAnnualSegment
                            );


            if (
                    !annualPossibleExists
            ) {

                throw new IllegalStateException(
                        "Monthly Possible Segment와 Annual Functional Possible Segment가 일치하지 않습니다. sequence="
                                + monthlyContext
                                .decadeSequenceNumber()
                );
            }


            result.add(
                    activationSegment.get()
            );
        }


        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * 특정 시점 Definite Segment
     * =================================================
     *
     * 양쪽 모두 동일 Sequence를 확정할 때만 반환한다.
     */

    public Optional<MonthlyFunctionalActivationSegmentAnalysis>
    definiteSegmentAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !period()
                        .contains(
                                dateTime
                        )
                        || segmentAnalyses
                        .isEmpty()
        ) {

            return Optional.empty();
        }


        Optional<MonthlyLuckDecadeContextAnalysis>
                monthlyDefinite =
                monthlyLuckAnalysis
                        .definiteContextAt(
                                dateTime
                        );


        Optional<AnnualCrossFunctionalSynthesisSegmentAnalysis>
                annualDefinite =
                annualFunctionalBaseline
                        .definiteSegmentAt(
                                dateTime
                        );


        if (
                monthlyDefinite.isEmpty()
                        || annualDefinite.isEmpty()
        ) {

            return Optional.empty();
        }


        int monthlySequence =
                monthlyDefinite
                        .get()
                        .decadeSequenceNumber();


        int annualSequence =
                annualDefinite
                        .get()
                        .decadeSequenceNumber();


        if (
                monthlySequence
                        != annualSequence
        ) {

            throw new IllegalStateException(
                    "Monthly Definite Segment와 Annual Functional Definite Segment의 Sequence가 일치하지 않습니다. "
                            + "monthly="
                            + monthlySequence
                            + ", annual="
                            + annualSequence
            );
        }


        Optional<MonthlyFunctionalActivationSegmentAnalysis>
                result =
                segmentOfDecade(
                        monthlySequence
                );


        if (
                result.isEmpty()
        ) {

            throw new IllegalStateException(
                    "Definite Sequence에 대응하는 Monthly Functional Segment가 없습니다. sequence="
                            + monthlySequence
            );
        }


        if (
                result.get()
                        .annualSegment()
                        != annualDefinite.get()
        ) {

            throw new IllegalStateException(
                    "Definite Monthly Functional Segment가 Annual Functional Segment Identity를 보존하지 않았습니다."
            );
        }


        return result;
    }


    /*
     * =================================================
     * Qualification
     * =================================================
     */

    public boolean qualificationRequired() {

        return annualEffects
                .stream()
                .anyMatch(
                        MonthlyFunctionalActivationEffect
                                ::qualificationRequired
                )

                || segmentAnalyses
                .stream()
                .anyMatch(
                        MonthlyFunctionalActivationSegmentAnalysis
                                ::qualificationRequired
                );
    }
}