package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchSixCombinationAnalysis;
import com.dajeom.model.EarthlyBranchSixCombinationEvidence;
import com.dajeom.model.EarthlyBranchSixCombinationTransformationAnalysis;
import com.dajeom.model.EarthlyBranchSixCombinationTransformationEvidence;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.rule.EarthlyBranchSixCombinationTransformationRule;
import com.dajeom.type.EarthlyBranchSixCombinationTransformationReasonType;
import com.dajeom.type.EarthlyBranchSixCombinationTransformationState;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-4.
 *
 * 8-3에서 탐지된 모든 지지 六合에:
 *
 * - 위치
 * - 인접성
 * - 월지 관여
 * - 일지 관여
 * - 합화 목표 오행 기존 세력
 * - 목표 오행 계절 지지
 * - 월지 내부 목표 오행 기반
 *
 * 을 연결하여
 * 방해 검토 전 기본 합화 상태를 결정한다.
 *
 * =================================================
 *
 * 이 Analyzer에서는 아직:
 *
 * 沖
 * 刑
 * 破
 * 害
 *
 * 를 합화 방해로 적용하지 않는다.
 *
 * 해당 작업은 9-7 책임이다.
 *
 * =================================================
 *
 * 또한:
 *
 * 三合
 * 方合
 * 다른 六合
 *
 * 과의 우선순위 경쟁도
 * 아직 확정하지 않는다.
 *
 * 해당 작업은 9-8 책임이다.
 */
@Component
public class EarthlyBranchSixCombinationTransformationAnalyzer {

    private final EarthlyBranchSixCombinationTransformationRule
            earthlyBranchSixCombinationTransformationRule;

    public EarthlyBranchSixCombinationTransformationAnalyzer(
            EarthlyBranchSixCombinationTransformationRule
                    earthlyBranchSixCombinationTransformationRule
    ) {

        this.earthlyBranchSixCombinationTransformationRule =
                Objects.requireNonNull(
                        earthlyBranchSixCombinationTransformationRule,
                        "EarthlyBranchSixCombinationTransformationRule이 필요합니다."
                );
    }

    /**
     * 8차 RelationAnalysis를 사용하는 정식 경로.
     */
    public EarthlyBranchSixCombinationTransformationAnalysis analyze(

            RelationAnalysis relationAnalysis,

            ElementStrengthAnalysis elementStrengthAnalysis

    ) {

        Objects.requireNonNull(
                relationAnalysis,
                "8차 관계 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 오행 세력 분석이 필요합니다."
        );

        if (
                relationAnalysis.dayMaster()
                        != elementStrengthAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "8차 관계 분석과 3차 오행 세력 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                relationAnalysis.completeChart()
                        != elementStrengthAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "8차 관계 분석과 3차 오행 세력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        return analyze(
                relationAnalysis.dayMaster(),
                relationAnalysis.monthBranch(),
                relationAnalysis.dayBranch(),
                relationAnalysis.sixCombinationAnalysis(),
                elementStrengthAnalysis
        );
    }

    /**
     * 하위 분석 결과를 직접 전달하는 재사용 경로.
     */
    public EarthlyBranchSixCombinationTransformationAnalysis analyze(

            com.dajeom.type.HeavenlyStem dayMaster,

            com.dajeom.type.EarthlyBranch monthBranch,

            com.dajeom.type.EarthlyBranch dayBranch,

            EarthlyBranchSixCombinationAnalysis
                    sixCombinationAnalysis,

            ElementStrengthAnalysis
                    elementStrengthAnalysis

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                dayBranch,
                "일지가 필요합니다."
        );

        Objects.requireNonNull(
                sixCombinationAnalysis,
                "8-3 지지 육합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 오행 세력 분석이 필요합니다."
        );

        if (
                sixCombinationAnalysis.dayBranch()
                        != dayBranch
        ) {

            throw new IllegalArgumentException(
                    "8-3 육합 분석의 일지가 전달된 일지와 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "3차 오행 세력 분석의 일간이 전달된 일간과 일치하지 않습니다."
            );
        }

        if (
                sixCombinationAnalysis.completeChart()
                        != elementStrengthAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "8-3 육합 분석과 3차 오행 세력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        List<EarthlyBranchSixCombinationTransformationEvidence>
                evidences =
                new ArrayList<>();

        for (
                EarthlyBranchSixCombinationEvidence sourceEvidence
                : sixCombinationAnalysis.evidences()
        ) {

            FiveElement targetElement =
                    sourceEvidence
                            .nominalTransformationElement();

            /*
             * =================================================
             * 합화 전 원국 목표 오행 비율
             * =================================================
             */
            int targetShareBasisPoints =
                    elementStrengthAnalysis
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    targetElement
                            );

            /*
             * =================================================
             * 목표 오행 계절 배율
             * =================================================
             */
            int targetSeasonalRate =
                    elementStrengthAnalysis
                            .seasonalElementPowerAnalysis()
                            .seasonalRateOf(
                                    targetElement
                            );

            /*
             * =================================================
             * 월지 내부 목표 오행 기반
             * =================================================
             */
            int monthBranchPresenceWeight =
                    elementStrengthAnalysis
                            .seasonalElementPowerAnalysis()
                            .monthBranchPresenceWeightOf(
                                    targetElement
                            );

            EarthlyBranchSixCombinationTransformationState state =
                    earthlyBranchSixCombinationTransformationRule
                            .determineState(
                                    sourceEvidence,
                                    targetShareBasisPoints,
                                    targetSeasonalRate,
                                    monthBranchPresenceWeight
                            );

            List<EarthlyBranchSixCombinationTransformationReasonType>
                    reasons =
                    earthlyBranchSixCombinationTransformationRule
                            .buildReasons(
                                    sourceEvidence,
                                    state,
                                    targetShareBasisPoints,
                                    targetSeasonalRate,
                                    monthBranchPresenceWeight
                            );

            boolean targetSeasonallySupported =
                    targetSeasonalRate
                            >= EarthlyBranchSixCombinationTransformationRule
                            .SEASONAL_SUPPORTED_RATE;

            boolean targetPresentInMonthBranch =
                    monthBranchPresenceWeight > 0;

            boolean targetStrongInChart =
                    targetShareBasisPoints
                            >= EarthlyBranchSixCombinationTransformationRule
                            .TARGET_STRONG_BASIS_POINTS;

            boolean targetPresentInChart =
                    targetShareBasisPoints
                            >= EarthlyBranchSixCombinationTransformationRule
                            .TARGET_PRESENT_BASIS_POINTS;

            evidences.add(
                    new EarthlyBranchSixCombinationTransformationEvidence(
                            sourceEvidence,
                            state,
                            targetElement,
                            targetShareBasisPoints,
                            targetSeasonalRate,
                            monthBranchPresenceWeight,
                            targetSeasonallySupported,
                            targetPresentInMonthBranch,
                            targetStrongInChart,
                            targetPresentInChart,
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 1. 잠정 합화 상태
         * 2. 인접 관계 우선
         * 3. 원국 위치 순
         *
         * 최종 합화 우선순위가 아니다.
         *
         * 실제 경쟁 조정은 9-8에서 한다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchSixCombinationTransformationEvidence evidence
                                ) ->
                                        evidence
                                                .state()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator.comparing(
                                                EarthlyBranchSixCombinationTransformationEvidence
                                                        ::adjacent
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .firstPosition()
                                                .ordinal()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .secondPosition()
                                                .ordinal()
                        )
        );

        return new EarthlyBranchSixCombinationTransformationAnalysis(
                dayMaster,
                monthBranch,
                dayBranch,
                sixCombinationAnalysis,
                elementStrengthAnalysis,
                evidences,
                sixCombinationAnalysis.completeChart()
        );
    }
}