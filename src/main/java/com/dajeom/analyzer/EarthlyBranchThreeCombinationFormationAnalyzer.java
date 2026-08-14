package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchThreeCombinationAnalysis;
import com.dajeom.model.EarthlyBranchThreeCombinationEvidence;
import com.dajeom.model.EarthlyBranchThreeCombinationFormationAnalysis;
import com.dajeom.model.EarthlyBranchThreeCombinationFormationEvidence;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.rule.EarthlyBranchThreeCombinationFormationRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchThreeCombinationFormationReasonType;
import com.dajeom.type.EarthlyBranchThreeCombinationFormationState;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-5.
 *
 * 8-4에서 탐지된 삼합·반합·부분조합 중
 * 독립 관계만 대상으로 국 성립도를 판단한다.
 *
 * =================================================
 *
 * 입력:
 *
 * RelationAnalysis
 * +
 * ElementStrengthAnalysis
 *
 * =================================================
 *
 * 처리 대상:
 *
 * threeCombinationAnalysis.independentRelations()
 *
 * =================================================
 *
 * 따라서 완전 삼합이 존재하는 경우:
 *
 * COMPLETE는 평가
 *
 * 그 내부의:
 *
 * HALF_WITH_CARDINAL
 * PARTIAL_WITHOUT_CARDINAL
 *
 * covered Evidence는 별도 평가하지 않는다.
 *
 * =================================================
 *
 * 아직:
 *
 * 沖
 * 刑
 * 破
 * 害
 *
 * 및 六合/方合과의 경쟁은
 * 최종 반영하지 않는다.
 */
@Component
public class EarthlyBranchThreeCombinationFormationAnalyzer {

    private final EarthlyBranchThreeCombinationFormationRule
            earthlyBranchThreeCombinationFormationRule;

    public EarthlyBranchThreeCombinationFormationAnalyzer(
            EarthlyBranchThreeCombinationFormationRule
                    earthlyBranchThreeCombinationFormationRule
    ) {

        this.earthlyBranchThreeCombinationFormationRule =
                Objects.requireNonNull(
                        earthlyBranchThreeCombinationFormationRule,
                        "EarthlyBranchThreeCombinationFormationRule이 필요합니다."
                );
    }

    /**
     * 8차 RelationAnalysis를 사용하는 정식 경로.
     */
    public EarthlyBranchThreeCombinationFormationAnalysis analyze(

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
                relationAnalysis.threeCombinationAnalysis(),
                elementStrengthAnalysis
        );
    }

    /**
     * 하위 분석 결과를 직접 전달하는 재사용 경로.
     */
    public EarthlyBranchThreeCombinationFormationAnalysis analyze(

            HeavenlyStem dayMaster,

            EarthlyBranch monthBranch,

            EarthlyBranch dayBranch,

            EarthlyBranchThreeCombinationAnalysis
                    threeCombinationAnalysis,

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
                threeCombinationAnalysis,
                "8-4 삼합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 오행 세력 분석이 필요합니다."
        );

        if (
                threeCombinationAnalysis.dayBranch()
                        != dayBranch
        ) {

            throw new IllegalArgumentException(
                    "8-4 삼합 분석의 일지가 전달된 일지와 일치하지 않습니다."
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
                threeCombinationAnalysis.completeChart()
                        != elementStrengthAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "8-4 삼합 분석과 3차 오행 세력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        List<EarthlyBranchThreeCombinationFormationEvidence>
                evidences =
                new ArrayList<>();

        /*
         * =================================================
         * 핵심
         * =================================================
         *
         * raw evidences()가 아니라
         * independentRelations()를 사용한다.
         *
         * 완전 삼합 내부 Pair의 중복 평가를 막는다.
         */
        for (
                EarthlyBranchThreeCombinationEvidence sourceEvidence
                : threeCombinationAnalysis.independentRelations()
        ) {

            FiveElement targetElement =
                    sourceEvidence
                            .nominalTransformationElement();

            /*
             * =================================================
             * 합화 전 목표 오행 기존 세력
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

            EarthlyBranchThreeCombinationFormationState state =
                    earthlyBranchThreeCombinationFormationRule
                            .determineState(
                                    sourceEvidence,
                                    targetShareBasisPoints,
                                    targetSeasonalRate,
                                    monthBranchPresenceWeight
                            );

            List<EarthlyBranchThreeCombinationFormationReasonType>
                    reasons =
                    earthlyBranchThreeCombinationFormationRule
                            .buildReasons(
                                    sourceEvidence,
                                    state,
                                    targetShareBasisPoints,
                                    targetSeasonalRate,
                                    monthBranchPresenceWeight
                            );

            boolean targetSeasonallySupported =
                    targetSeasonalRate
                            >= EarthlyBranchThreeCombinationFormationRule
                            .SEASONAL_SUPPORTED_RATE;

            boolean targetPresentInMonthBranch =
                    monthBranchPresenceWeight > 0;

            boolean targetStrongInChart =
                    targetShareBasisPoints
                            >= EarthlyBranchThreeCombinationFormationRule
                            .TARGET_STRONG_BASIS_POINTS;

            boolean targetPresentInChart =
                    targetShareBasisPoints
                            >= EarthlyBranchThreeCombinationFormationRule
                            .TARGET_PRESENT_BASIS_POINTS;

            evidences.add(
                    new EarthlyBranchThreeCombinationFormationEvidence(
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
         * 1. 국 성립 상태
         * 2. 삼합 구성 형태
         * 3. 원국 위치
         *
         * 실제 최종 우선순위는 아니다.
         *
         * 9-7 / 9-8 이후 다시 검토한다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchThreeCombinationFormationEvidence evidence
                                ) ->
                                        evidence
                                                .state()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator.comparingInt(
                                                (
                                                        EarthlyBranchThreeCombinationFormationEvidence evidence
                                                ) ->
                                                        evidence
                                                                .form()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .positions()
                                                .get(0)
                                                .ordinal()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .positions()
                                                .get(1)
                                                .ordinal()
                        )
        );

        return new EarthlyBranchThreeCombinationFormationAnalysis(
                dayMaster,
                monthBranch,
                dayBranch,
                threeCombinationAnalysis,
                elementStrengthAnalysis,
                evidences,
                threeCombinationAnalysis.completeChart()
        );
    }
}