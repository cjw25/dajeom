package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 하나의 월운 기간 안에서
 * 실제로 겹치는 하나의 Annual-Decade Segment에 대한
 * Monthly Functional Activation 결과.
 *
 * =================================================
 *
 * Source:
 *
 * AnnualCrossFunctionalSynthesisSegmentAnalysis
 *
 * +
 *
 * MonthlyLuckDecadeContextAnalysis
 *
 * =================================================
 *
 * 현재 월운과 실제로 겹치는 대운 Segment만
 * 이 Aggregate를 생성한다.
 *
 * Annual Functional Baseline에 존재하지만
 * 현재 월운 기간과 겹치지 않는 다른 대운 Segment는
 * 생성하지 않는다.
 *
 * =================================================
 *
 * 중요:
 *
 * - 임의의 대운 하나를 선택하지 않는다.
 * - 교운 시 여러 Segment가 실제 겹치면 모두 보존한다.
 * - Segment별 5 Function을 별도로 보존한다.
 */
public record MonthlyFunctionalActivationSegmentAnalysis(

        AnnualCrossFunctionalSynthesisSegmentAnalysis
        annualSegment,

        MonthlyLuckAnalysis
        monthlyLuckAnalysis,

        MonthlyLuckDecadeContextAnalysis
        monthlyDecadeContext,

        List<MonthlyFunctionalActivationEffect>
        functionEffects,

        boolean completeChart

) {

    public MonthlyFunctionalActivationSegmentAnalysis {

        Objects.requireNonNull(
                annualSegment,
                "Monthly Functional Segment에는 Annual Functional Segment가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckAnalysis,
                "Monthly Functional Segment에는 MonthlyLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyDecadeContext,
                "Monthly Functional Segment에는 Monthly Decade Context가 필요합니다."
        );


        functionEffects =
                functionEffects == null
                        ? List.of()
                        : List.copyOf(
                        functionEffects
                );


        if (
                annualSegment.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Segment와 Monthly Functional Segment의 completeChart가 일치하지 않습니다."
            );
        }


        /*
         * =================================================
         * Decade Sequence
         * =================================================
         */

        if (
                annualSegment
                        .decadeSequenceNumber()
                        != monthlyDecadeContext
                        .decadeSequenceNumber()
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Segment와 Monthly Decade Context의 대운 Sequence가 일치하지 않습니다."
            );
        }


        /*
         * =================================================
         * Monthly Context Identity
         * =================================================
         */

        Optional<MonthlyLuckDecadeContextAnalysis>
                expectedMonthlyContext =
                monthlyLuckAnalysis
                        .contextOfDecade(
                                annualSegment
                                        .decadeSequenceNumber()
                        );


        if (
                expectedMonthlyContext.isEmpty()
                        ||
                        expectedMonthlyContext.get()
                                != monthlyDecadeContext
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Segment가 현재 MonthlyLuckAnalysis의 Decade Context Identity를 보존하지 않았습니다."
            );
        }


        /*
         * =================================================
         * 5 Function Coverage
         * =================================================
         */

        if (
                functionEffects.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Segment에는 정확히 5개 Function Effect가 필요합니다. actual="
                            + functionEffects.size()
            );
        }


        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            List<MonthlyFunctionalActivationEffect> matched =
                    functionEffects
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
                        "Monthly Functional Segment의 Function Coverage가 올바르지 않습니다. function="
                                + functionType
                                + ", actual="
                                + matched.size()
                );
            }


            MonthlyFunctionalActivationEffect effect =
                    matched.get(0);


            if (
                    !effect.segmentScoped()
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Segment에는 Segment Scope Effect만 연결할 수 있습니다. function="
                                + functionType
                );
            }


            if (
                    effect.monthlyLuckAnalysis()
                            != monthlyLuckAnalysis
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Segment Effect가 현재 MonthlyLuckAnalysis Identity를 보존하지 않았습니다."
                );
            }


            AnnualResolvedFunctionEffect
                    resolvedEffect =
                    effect
                            .segmentResolvedEffectOptional()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Monthly Functional Segment Effect에 Annual Resolved Function이 없습니다."
                                            )
                            );


            if (
                    resolvedEffect
                            .sourceSegment()
                            != annualSegment
                            .sourceSegment()
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Segment Effect가 현재 Annual Segment Identity를 보존하지 않았습니다. function="
                                + functionType
                );
            }


            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Monthly Functional Segment Effect의 completeChart가 일치하지 않습니다."
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

        return annualSegment
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


    public int decadeSequenceNumber() {

        return annualSegment
                .decadeSequenceNumber();
    }


    public boolean timingUncertain() {

        return annualSegment
                .timingUncertain()

                || monthlyDecadeContext
                .timingUncertain();
    }


    /*
     * =================================================
     * Function
     * =================================================
     */

    public Optional<MonthlyFunctionalActivationEffect>
    effectOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }


        return functionEffects
                .stream()
                .filter(
                        effect ->
                                effect.functionType()
                                        == functionType
                )
                .findFirst();
    }


    public MonthlyFunctionalActivationEffect
    requireEffect(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "Monthly Functional Segment Effect 조회에는 Function Type이 필요합니다."
        );


        return effectOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Monthly Functional Segment Effect를 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }


    public List<MonthlyFunctionalActivationEffect>
    contactedEffects() {

        return functionEffects
                .stream()
                .filter(
                        MonthlyFunctionalActivationEffect
                                ::monthlyContactExists
                )
                .toList();
    }


    public List<MonthlyFunctionalActivationEffect>
    contactsOnActiveAnnualBaseline() {

        return functionEffects
                .stream()
                .filter(
                        MonthlyFunctionalActivationEffect
                                ::contactOnActiveAnnualBaseline
                )
                .toList();
    }


    public List<MonthlyFunctionalActivationEffect>
    contactsAgainstInactiveAnnualBaseline() {

        return functionEffects
                .stream()
                .filter(
                        MonthlyFunctionalActivationEffect
                                ::monthlyContactAgainstInactiveAnnualBaseline
                )
                .toList();
    }


    public List<MonthlyFunctionalActivationEffect>
    qualifiedEffects() {

        return functionEffects
                .stream()
                .filter(
                        MonthlyFunctionalActivationEffect
                                ::qualificationRequired
                )
                .toList();
    }


    /*
     * =================================================
     * Segment Global Context
     * =================================================
     *
     * 특정 TenGod Function에 직접 연결할 수 없는
     * Global Context도 삭제하지 않는다.
     */

    public List<MonthlyLuckContextSignal>
    globalSegmentSignals() {

        return monthlyDecadeContext
                .segmentSignals()
                .stream()
                .filter(
                        signal ->
                                signal
                                        .tenGodTypes()
                                        .isEmpty()
                )
                .toList();
    }


    public boolean qualificationRequired() {

        return timingUncertain()

                || !qualifiedEffects()
                .isEmpty();
    }
}