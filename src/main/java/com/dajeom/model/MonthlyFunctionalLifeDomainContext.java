package com.dajeom.model;

import com.dajeom.type.MonthlyFunctionalActivationStatus;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Monthly Functional Activation을
 * 기존 18차 LifeDomain Timeline Segment에
 * 연결하기 위한 typed adapter.
 *
 * =================================================
 *
 * Source:
 *
 * MonthlyFunctionalActivationAnalysis
 *
 * =================================================
 *
 * Timeline Anchor:
 *
 * LuckIntegratedSegmentAnalysis
 *
 * =================================================
 *
 * 중요한 이유:
 *
 * MonthlyFunctionalActivationAnalysis는
 * 하나의 실제 MonthlyLuckAnalysis 전체를 다루며,
 * 교운 월에는 여러 Annual-Decade Segment가
 * 존재할 수 있다.
 *
 * 반면 18차 LifeDomain은
 * 16차 LuckIntegratedSegmentAnalysis 단위로 실행된다.
 *
 * 따라서 단순히:
 *
 * MonthlyFunctionalActivationAnalysis
 *
 * 만 Evidence Source로 넣지 않고,
 *
 * 현재 16차 Timeline Segment와 함께 묶어서
 * temporal provenance를 보존한다.
 *
 * =================================================
 *
 * 이 Adapter가 담당하는 것:
 *
 * 현재 Timeline Segment가 실제 보유한:
 *
 * - MonthlyLuckAnalysis Identity
 * - AnnualLuckAnalysis Identity
 * - 대운 Sequence
 *
 * 를 이용해 Monthly Functional Activation 내부의
 * 정확한 Segment를 조회한다.
 *
 * =================================================
 *
 * 절대 하지 않는 것:
 *
 * - 날짜로 월운 재계산
 * - 연도/월 숫자로 MonthlyLuckAnalysis 재조회
 * - 여러 대운 Segment 평균
 * - 첫 Segment 임의 선택
 * - Function 간 관계 새 생성
 * - Function → LifeDomain 길흉 직접 변환
 * - Monthly Contact를 Annual Active로 승격
 *
 * =================================================
 *
 * Timeline에 활성 대운이 없거나
 * Monthly Functional Segment가 존재하지 않는 경우에도
 *
 * Annual Common Scope의
 * Monthly Functional Effect는 유지된다.
 *
 * 즉:
 *
 * Segment Functional unavailable
 * !=
 * Monthly Functional 전체 unavailable
 */
public record MonthlyFunctionalLifeDomainContext(

        LuckIntegratedSegmentAnalysis segmentAnalysis,

        MonthlyFunctionalActivationAnalysis
        monthlyFunctionalActivation

) {

    public MonthlyFunctionalLifeDomainContext {

        Objects.requireNonNull(
                segmentAnalysis,
                "Monthly Functional LifeDomain Context에는 Timeline Segment가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyFunctionalActivation,
                "Monthly Functional LifeDomain Context에는 MonthlyFunctionalActivationAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * MonthlyLuckAnalysis Identity
         * =================================================
         */

        if (
                monthlyFunctionalActivation
                        .monthlyLuckAnalysis()
                        != segmentAnalysis
                        .monthlyLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "Timeline Segment와 Monthly Functional Activation이 "
                            + "서로 다른 MonthlyLuckAnalysis Identity를 참조합니다. "
                            + "sajuYear="
                            + segmentAnalysis
                            .monthlyLuckAnalysis()
                            .sajuYear()
                            + ", monthIndex="
                            + segmentAnalysis
                            .monthlyLuckAnalysis()
                            .monthIndex()
            );
        }

        /*
         * =================================================
         * AnnualLuckAnalysis Identity
         * =================================================
         *
         * Monthly Functional은 공식 Annual Functional Baseline을
         * 기반으로 생성되므로 그 AnnualLuckAnalysis도
         * 현재 Timeline Segment와 동일해야 한다.
         */

        if (
                monthlyFunctionalActivation
                        .annualFunctionalBaseline()
                        .annualLuckAnalysis()
                        != segmentAnalysis
                        .annualLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "Timeline Segment와 Monthly Functional Activation의 "
                            + "Annual Functional Baseline이 서로 다른 AnnualLuckAnalysis를 참조합니다. "
                            + "targetYear="
                            + segmentAnalysis
                            .annualLuckAnalysis()
                            .targetYear()
            );
        }

        /*
         * =================================================
         * MonthlyLuckPeriod Identity
         * =================================================
         */

        if (
                monthlyFunctionalActivation
                        .period()
                        != segmentAnalysis
                        .monthlyLuckAnalysis()
                        .period()
        ) {

            throw new IllegalArgumentException(
                    "Timeline Segment와 Monthly Functional Activation이 "
                            + "서로 다른 MonthlyLuckPeriod를 참조합니다."
            );
        }

        /*
         * =================================================
         * completeChart
         * =================================================
         */

        if (
                monthlyFunctionalActivation
                        .completeChart()
                        != segmentAnalysis
                        .natalAnalysis()
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "Monthly Functional Activation과 Timeline Segment 원국의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 현재 대운 Segment Coverage
         * =================================================
         */

        validateCurrentDecadeSegmentCoverage(
                segmentAnalysis,
                monthlyFunctionalActivation
        );
    }

    /*
     * =================================================
     * Identity
     * =================================================
     */

    public int targetYear() {

        return monthlyFunctionalActivation
                .targetYear();
    }

    public int sajuYear() {

        return monthlyFunctionalActivation
                .sajuYear();
    }

    public int monthIndex() {

        return monthlyFunctionalActivation
                .monthIndex();
    }

    public MonthlyLuckPeriod monthlyPeriod() {

        return monthlyFunctionalActivation
                .period();
    }

    public MonthlyLuckAnalysis monthlyLuckAnalysis() {

        return monthlyFunctionalActivation
                .monthlyLuckAnalysis();
    }

    public AnnualFunctionalBaselineAnalysis
    annualFunctionalBaseline() {

        return monthlyFunctionalActivation
                .annualFunctionalBaseline();
    }

    public boolean completeChart() {

        return monthlyFunctionalActivation
                .completeChart();
    }

    /*
     * =================================================
     * 현재 Timeline 대운
     * =================================================
     */

    public boolean timelineHasActiveDecade() {

        return segmentAnalysis
                .timelineSegment()
                .hasDecade();
    }

    public Optional<Integer>
    timelineDecadeSequenceNumber() {

        return segmentAnalysis
                .timelineSegment()
                .decadeSequenceNumber();
    }

    /*
     * =================================================
     * Annual Common Scope
     * =================================================
     *
     * 대운 Segment 존재 여부와 무관하게
     * 공식 Annual Functional Baseline 위에서
     * 월운의 공통 Function Contact는 유지된다.
     */

    public List<MonthlyFunctionalActivationEffect>
    annualEffects() {

        return monthlyFunctionalActivation
                .annualEffects();
    }

    public Optional<MonthlyFunctionalActivationEffect>
    annualEffectOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return monthlyFunctionalActivation
                .annualEffectOf(
                        functionType
                );
    }

    public MonthlyFunctionalActivationEffect
    requireAnnualEffect(
            NatalTenGodFunctionType functionType
    ) {

        return monthlyFunctionalActivation
                .requireAnnualEffect(
                        Objects.requireNonNull(
                                functionType,
                                "Monthly Functional Annual Effect 조회에는 Function Type이 필요합니다."
                        )
                );
    }

    public List<MonthlyFunctionalActivationEffect>
    contactedAnnualEffects() {

        return monthlyFunctionalActivation
                .contactedAnnualEffects();
    }

    public List<MonthlyFunctionalActivationEffect>
    annualBaselineContacts() {

        return monthlyFunctionalActivation
                .annualBaselineContacts();
    }

    public List<MonthlyFunctionalActivationEffect>
    inactiveBaselineMonthlyContacts() {

        return monthlyFunctionalActivation
                .inactiveBaselineMonthlyContacts();
    }

    /*
     * =================================================
     * Global Monthly Context
     * =================================================
     *
     * 특정 Function으로 강제 연결하지 않은
     * Global Signal도 삭제하지 않는다.
     */

    public List<MonthlyLuckContextSignal>
    globalMonthlySignals() {

        return monthlyFunctionalActivation
                .globalMonthlySignals();
    }

    /*
     * =================================================
     * 현재 Monthly Functional Segment
     * =================================================
     *
     * 현재 16차 Timeline의 대운 Sequence를
     * 그대로 사용한다.
     *
     * 날짜 재계산 없음.
     * 첫 Segment 선택 없음.
     * 평균 없음.
     */

    public Optional<MonthlyFunctionalActivationSegmentAnalysis>
    currentActivationSegment() {

        Optional<Integer> sequenceNumber =
                timelineDecadeSequenceNumber();

        if (
                sequenceNumber.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                !monthlyFunctionalActivation
                        .decadeSegmentAvailable()
        ) {

            return Optional.empty();
        }

        return monthlyFunctionalActivation
                .segmentOfDecade(
                        sequenceNumber
                                .orElseThrow()
                );
    }

    public MonthlyFunctionalActivationSegmentAnalysis
    requireCurrentActivationSegment() {

        int sequenceNumber =
                timelineDecadeSequenceNumber()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "현재 Timeline Segment에는 활성 대운 Sequence가 없습니다. "
                                                        + "sajuYear="
                                                        + sajuYear()
                                                        + ", monthIndex="
                                                        + monthIndex()
                                        )
                        );

        if (
                !monthlyFunctionalActivation
                        .decadeSegmentAvailable()
        ) {

            throw new IllegalStateException(
                    "현재 Monthly Functional Activation에는 "
                            + "Decade Segment 분석이 없습니다. "
                            + "sajuYear="
                            + sajuYear()
                            + ", monthIndex="
                            + monthIndex()
                            + ", sequence="
                            + sequenceNumber
            );
        }

        return monthlyFunctionalActivation
                .segmentOfDecade(
                        sequenceNumber
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "현재 Timeline 대운 Sequence에 대응하는 "
                                                + "Monthly Functional Segment가 없습니다. "
                                                + "sajuYear="
                                                + sajuYear()
                                                + ", monthIndex="
                                                + monthIndex()
                                                + ", sequence="
                                                + sequenceNumber
                                )
                );
    }

    public boolean currentActivationSegmentAvailable() {

        return currentActivationSegment()
                .isPresent();
    }

    public boolean currentActivationSegmentTimingUncertain() {

        return currentActivationSegment()
                .map(
                        MonthlyFunctionalActivationSegmentAnalysis
                                ::timingUncertain
                )
                .orElse(
                        false
                );
    }

    /*
     * =================================================
     * 현재 Segment Function
     * =================================================
     */

    public Optional<MonthlyFunctionalActivationEffect>
    currentEffectOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return currentActivationSegment()
                .flatMap(
                        segment ->
                                segment.effectOf(
                                        functionType
                                )
                );
    }

    public boolean currentMonthlyContactExists(
            NatalTenGodFunctionType functionType
    ) {

        return currentEffectOf(
                functionType
        )
                .map(
                        MonthlyFunctionalActivationEffect
                                ::monthlyContactExists
                )
                .orElse(
                        false
                );
    }

    public Optional<MonthlyFunctionalActivationStatus>
    currentActivationStatusOf(
            NatalTenGodFunctionType functionType
    ) {

        return currentEffectOf(
                functionType
        )
                .map(
                        MonthlyFunctionalActivationEffect
                                ::activationStatus
                );
    }

    public List<MonthlyFunctionalActivationEffect>
    currentContactedEffects() {

        return currentActivationSegment()
                .map(
                        MonthlyFunctionalActivationSegmentAnalysis
                                ::contactedEffects
                )
                .orElse(
                        List.of()
                );
    }

    public List<MonthlyFunctionalActivationEffect>
    currentContactsOnActiveAnnualBaseline() {

        return currentActivationSegment()
                .map(
                        MonthlyFunctionalActivationSegmentAnalysis
                                ::contactsOnActiveAnnualBaseline
                )
                .orElse(
                        List.of()
                );
    }

    public List<MonthlyFunctionalActivationEffect>
    currentContactsAgainstInactiveAnnualBaseline() {

        return currentActivationSegment()
                .map(
                        MonthlyFunctionalActivationSegmentAnalysis
                                ::contactsAgainstInactiveAnnualBaseline
                )
                .orElse(
                        List.of()
                );
    }

    public List<MonthlyFunctionalActivationEffect>
    currentQualifiedEffects() {

        return currentActivationSegment()
                .map(
                        MonthlyFunctionalActivationSegmentAnalysis
                                ::qualifiedEffects
                )
                .orElse(
                        List.of()
                );
    }

    /*
     * =================================================
     * 현재 Segment Global Signal
     * =================================================
     */

    public List<MonthlyLuckContextSignal>
    currentGlobalSegmentSignals() {

        return currentActivationSegment()
                .map(
                        MonthlyFunctionalActivationSegmentAnalysis
                                ::globalSegmentSignals
                )
                .orElse(
                        List.of()
                );
    }

    /*
     * =================================================
     * Qualification
     * =================================================
     */

    public boolean qualificationRequired() {

        return monthlyFunctionalActivation
                .qualificationRequired();
    }

    public boolean currentSegmentQualificationRequired() {

        return currentActivationSegment()
                .map(
                        MonthlyFunctionalActivationSegmentAnalysis
                                ::qualificationRequired
                )
                .orElse(
                        false
                );
    }

    /*
     * =================================================
     * Internal Validation
     * =================================================
     */

    private static void
    validateCurrentDecadeSegmentCoverage(

            LuckIntegratedSegmentAnalysis segmentAnalysis,

            MonthlyFunctionalActivationAnalysis activationAnalysis

    ) {

        Optional<Integer> sequenceOptional =
                segmentAnalysis
                        .timelineSegment()
                        .decadeSequenceNumber();

        /*
         * Timeline에 활성 대운이 없다.
         *
         * Annual Common Scope의
         * Monthly Functional은 여전히 사용 가능.
         */

        if (
                sequenceOptional.isEmpty()
        ) {

            return;
        }

        /*
         * Monthly Functional 자체에
         * Segment 분석이 없다.
         *
         * Annual Common Scope는 유지한다.
         */

        if (
                !activationAnalysis
                        .decadeSegmentAvailable()
        ) {

            return;
        }

        int sequenceNumber =
                sequenceOptional
                        .orElseThrow();

        MonthlyFunctionalActivationSegmentAnalysis
                activationSegment =
                activationAnalysis
                        .segmentOfDecade(
                                sequenceNumber
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "16차 Timeline의 활성 대운 Sequence에 대응하는 "
                                                        + "Monthly Functional Segment가 없습니다. "
                                                        + "sajuYear="
                                                        + activationAnalysis.sajuYear()
                                                        + ", monthIndex="
                                                        + activationAnalysis.monthIndex()
                                                        + ", sequence="
                                                        + sequenceNumber
                                        )
                        );

        if (
                activationSegment
                        .monthlyLuckAnalysis()
                        != segmentAnalysis
                        .monthlyLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "현재 Monthly Functional Segment가 "
                            + "Timeline의 MonthlyLuckAnalysis Identity를 보존하지 않았습니다. "
                            + "sequence="
                            + sequenceNumber
            );
        }

        if (
                activationSegment
                        .decadeSequenceNumber()
                        != sequenceNumber
        ) {

            throw new IllegalArgumentException(
                    "현재 Monthly Functional Segment의 대운 Sequence가 "
                            + "16차 Timeline Sequence와 일치하지 않습니다. "
                            + "timelineSequence="
                            + sequenceNumber
                            + ", functionalSequence="
                            + activationSegment.decadeSequenceNumber()
            );
        }

        if (
                activationSegment
                        .completeChart()
                        != activationAnalysis
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "현재 Monthly Functional Segment와 "
                            + "Monthly Functional Activation의 completeChart가 일치하지 않습니다."
            );
        }
    }
}