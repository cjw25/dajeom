package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchDirectionalCombinationAnalysis;
import com.dajeom.model.EarthlyBranchDirectionalCombinationEvidence;
import com.dajeom.model.EarthlyBranchDirectionalCombinationFormationAnalysis;
import com.dajeom.model.EarthlyBranchDirectionalCombinationFormationEvidence;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.rule.EarthlyBranchDirectionalCombinationFormationRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchDirectionalCombinationFormationReasonType;
import com.dajeom.type.EarthlyBranchDirectionalCombinationFormationState;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-6.
 *
 * 8-5의 방합·부분방합 중
 * 독립 관계만 대상으로
 * 方局 성립도를 판단한다.
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
 * 평가 대상:
 *
 * directionalCombinationAnalysis.independentRelations()
 *
 * =================================================
 *
 * 방합의 특성상:
 *
 * 목표 오행과 현재 계절 방향이
 * 旺 수준으로 일치하는지
 *
 * 를 삼합보다 중요하게 평가한다.
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
 * 및 六合 / 三合과의 경쟁은
 * 최종 반영하지 않는다.
 */
@Component
public class EarthlyBranchDirectionalCombinationFormationAnalyzer {

    private final EarthlyBranchDirectionalCombinationFormationRule
            earthlyBranchDirectionalCombinationFormationRule;

    public EarthlyBranchDirectionalCombinationFormationAnalyzer(
            EarthlyBranchDirectionalCombinationFormationRule
                    earthlyBranchDirectionalCombinationFormationRule
    ) {

        this.earthlyBranchDirectionalCombinationFormationRule =
                Objects.requireNonNull(
                        earthlyBranchDirectionalCombinationFormationRule,
                        "EarthlyBranchDirectionalCombinationFormationRule이 필요합니다."
                );
    }

    /**
     * 8차 RelationAnalysis를 사용하는 정식 경로.
     */
    public EarthlyBranchDirectionalCombinationFormationAnalysis analyze(

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
                relationAnalysis.directionalCombinationAnalysis(),
                elementStrengthAnalysis
        );
    }

    /**
     * 하위 결과 직접 전달 경로.
     */
    public EarthlyBranchDirectionalCombinationFormationAnalysis analyze(

            HeavenlyStem dayMaster,

            EarthlyBranch monthBranch,

            EarthlyBranch dayBranch,

            EarthlyBranchDirectionalCombinationAnalysis
                    directionalCombinationAnalysis,

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
                directionalCombinationAnalysis,
                "8-5 방합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 오행 세력 분석이 필요합니다."
        );

        if (
                directionalCombinationAnalysis
                        .dayBranch()
                        != dayBranch
        ) {

            throw new IllegalArgumentException(
                    "8-5 방합 분석의 일지가 전달된 일지와 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "3차 오행 세력 분석의 일간이 전달된 일간과 일치하지 않습니다."
            );
        }

        if (
                directionalCombinationAnalysis
                        .completeChart()
                        != elementStrengthAnalysis
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "8-5 방합 분석과 3차 오행 세력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        List<EarthlyBranchDirectionalCombinationFormationEvidence>
                evidences =
                new ArrayList<>();

        /*
         * =================================================
         * 핵심
         * =================================================
         *
         * 8-5 raw evidences()가 아니라
         * independentRelations()만 사용한다.
         *
         * 완전 方合 내부 Pair를
         * 별도 方局으로 중복 평가하지 않는다.
         */
        for (
                EarthlyBranchDirectionalCombinationEvidence sourceEvidence
                : directionalCombinationAnalysis
                .independentRelations()
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

            EarthlyBranchDirectionalCombinationFormationState state =
                    earthlyBranchDirectionalCombinationFormationRule
                            .determineState(
                                    sourceEvidence,
                                    targetShareBasisPoints,
                                    targetSeasonalRate,
                                    monthBranchPresenceWeight
                            );

            List<EarthlyBranchDirectionalCombinationFormationReasonType>
                    reasons =
                    earthlyBranchDirectionalCombinationFormationRule
                            .buildReasons(
                                    sourceEvidence,
                                    state,
                                    targetShareBasisPoints,
                                    targetSeasonalRate,
                                    monthBranchPresenceWeight
                            );

            boolean seasonDirectionAligned =
                    targetSeasonalRate
                            >= EarthlyBranchDirectionalCombinationFormationRule
                            .SEASON_DIRECTION_ALIGNED_RATE;

            boolean targetSeasonallySupported =
                    targetSeasonalRate
                            >= EarthlyBranchDirectionalCombinationFormationRule
                            .SEASONAL_SUPPORTED_RATE;

            boolean targetPresentInMonthBranch =
                    monthBranchPresenceWeight > 0;

            boolean targetStrongInChart =
                    targetShareBasisPoints
                            >= EarthlyBranchDirectionalCombinationFormationRule
                            .TARGET_STRONG_BASIS_POINTS;

            boolean targetPresentInChart =
                    targetShareBasisPoints
                            >= EarthlyBranchDirectionalCombinationFormationRule
                            .TARGET_PRESENT_BASIS_POINTS;

            evidences.add(
                    new EarthlyBranchDirectionalCombinationFormationEvidence(
                            sourceEvidence,
                            state,
                            targetElement,
                            targetShareBasisPoints,
                            targetSeasonalRate,
                            monthBranchPresenceWeight,
                            seasonDirectionAligned,
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
         * 1. 方局 성립 상태
         * 2. 방합 구성 형태
         * 3. 계절 방향 일치 여부
         * 4. 원국 위치
         *
         * 최종 경쟁 우선순위가 아니다.
         *
         * 최종 경쟁은 9-8에서 처리한다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        EarthlyBranchDirectionalCombinationFormationEvidence evidence
                                ) ->
                                        evidence
                                                .state()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator.comparingInt(
                                                (
                                                        EarthlyBranchDirectionalCombinationFormationEvidence evidence
                                                ) ->
                                                        evidence
                                                                .form()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator.comparing(
                                                EarthlyBranchDirectionalCombinationFormationEvidence
                                                        ::seasonDirectionAligned
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

        return new EarthlyBranchDirectionalCombinationFormationAnalysis(
                dayMaster,
                monthBranch,
                dayBranch,
                directionalCombinationAnalysis,
                elementStrengthAnalysis,
                evidences,
                directionalCombinationAnalysis.completeChart()
        );
    }
}