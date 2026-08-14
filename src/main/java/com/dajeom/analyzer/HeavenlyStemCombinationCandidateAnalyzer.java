package com.dajeom.analyzer;

import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.HeavenlyStemCombinationAnalysis;
import com.dajeom.model.HeavenlyStemCombinationCandidate;
import com.dajeom.model.HeavenlyStemCombinationCandidateAnalysis;
import com.dajeom.model.HeavenlyStemCombinationEvidence;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.rule.HeavenlyStemCombinationCandidateRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStemCombinationApplicability;
import com.dajeom.type.HeavenlyStemCombinationCandidateReasonType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-1.
 *
 * 8-1에서 발견된 천간합 관계에
 * 기존 오행 세력·계절 Evidence를 연결하여
 * 기본 합화 후보 적용도를 만든다.
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
 * 사용하는 기존 데이터:
 *
 * 8-1
 * HeavenlyStemCombinationAnalysis
 *
 * 3차
 * ElementStrengthAnalysis
 *
 * - ElementPowerAnalysis
 * - SeasonalElementPowerAnalysis
 *
 * =================================================
 *
 * 처리:
 *
 * 각 천간합 Evidence
 *
 * ↓
 *
 * 명목상 합화 목표 오행
 *
 * ↓
 *
 * 원국 기존 오행 비율
 *
 * ↓
 *
 * 목표 오행 계절 배율
 *
 * ↓
 *
 * 월지 목표 오행 존재 가중치
 *
 * ↓
 *
 * 9-1 기본 적용도
 *
 * =================================================
 *
 * 이 Analyzer에서는:
 *
 * - 쟁합
 * - 투합
 * - 다중합 경쟁
 * - 합충 경쟁
 * - 실제 合化
 *
 * 를 판단하지 않는다.
 */
@Component
public class HeavenlyStemCombinationCandidateAnalyzer {

    private final HeavenlyStemCombinationCandidateRule
            heavenlyStemCombinationCandidateRule;

    public HeavenlyStemCombinationCandidateAnalyzer(
            HeavenlyStemCombinationCandidateRule
                    heavenlyStemCombinationCandidateRule
    ) {

        this.heavenlyStemCombinationCandidateRule =
                Objects.requireNonNull(
                        heavenlyStemCombinationCandidateRule,
                        "HeavenlyStemCombinationCandidateRule이 필요합니다."
                );
    }

    public HeavenlyStemCombinationCandidateAnalysis analyze(

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
                    "관계 분석과 오행 세력 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                relationAnalysis.completeChart()
                        != elementStrengthAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "관계 분석과 오행 세력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        HeavenlyStemCombinationAnalysis combinationAnalysis =
                relationAnalysis
                        .heavenlyStemCombinationAnalysis();

        return analyze(
                combinationAnalysis,
                elementStrengthAnalysis
        );
    }

    /**
     * 8-1 결과를 직접 전달하는 경로.
     *
     * 9차 상위 Engine에서 이미
     * RelationAnalysis 내부 결과를 분리해 둔 경우
     * 재사용할 수 있다.
     */
    public HeavenlyStemCombinationCandidateAnalysis analyze(

            HeavenlyStemCombinationAnalysis combinationAnalysis,

            ElementStrengthAnalysis elementStrengthAnalysis

    ) {

        Objects.requireNonNull(
                combinationAnalysis,
                "8-1 천간합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 오행 세력 분석이 필요합니다."
        );

        if (
                combinationAnalysis.dayMaster()
                        != elementStrengthAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "천간합 분석과 오행 세력 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                combinationAnalysis.completeChart()
                        != elementStrengthAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "천간합 분석과 오행 세력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        List<HeavenlyStemCombinationCandidate> candidates =
                new ArrayList<>();

        for (
                HeavenlyStemCombinationEvidence evidence
                : combinationAnalysis.evidences()
        ) {

            FiveElement targetElement =
                    evidence
                            .nominalTransformationElement();

            /*
             * =================================================
             * 원국 기존 목표 오행 비율
             * =================================================
             *
             * 합화 적용 전의 원국 세력을 사용한다.
             */
            int targetShareBasisPoints =
                    elementStrengthAnalysis
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    targetElement
                            );

            /*
             * =================================================
             * 계절 배율
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
             * 월지 목표 오행 기반
             * =================================================
             */
            int monthBranchPresenceWeight =
                    elementStrengthAnalysis
                            .seasonalElementPowerAnalysis()
                            .monthBranchPresenceWeightOf(
                                    targetElement
                            );

            HeavenlyStemCombinationApplicability applicability =
                    heavenlyStemCombinationCandidateRule
                            .determineApplicability(
                                    evidence,
                                    targetShareBasisPoints,
                                    targetSeasonalRate,
                                    monthBranchPresenceWeight
                            );

            List<HeavenlyStemCombinationCandidateReasonType> reasons =
                    heavenlyStemCombinationCandidateRule
                            .buildReasons(
                                    evidence,
                                    applicability,
                                    targetShareBasisPoints,
                                    targetSeasonalRate,
                                    monthBranchPresenceWeight
                            );

            boolean targetSeasonallySupported =
                    targetSeasonalRate
                            >= HeavenlyStemCombinationCandidateRule
                            .SEASONAL_SUPPORTED_RATE;

            boolean targetPresentInMonthBranch =
                    monthBranchPresenceWeight > 0;

            boolean targetStrongInChart =
                    targetShareBasisPoints
                            >= HeavenlyStemCombinationCandidateRule
                            .TARGET_STRONG_BASIS_POINTS;

            boolean targetPresentInChart =
                    targetShareBasisPoints
                            >= HeavenlyStemCombinationCandidateRule
                            .TARGET_PRESENT_BASIS_POINTS;

            candidates.add(
                    new HeavenlyStemCombinationCandidate(
                            evidence,
                            applicability,
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
         * 1.
         * 기본 적용도
         *
         * 2.
         * 원국 위치
         *
         * 이 정렬은 최종 합화 우선순위가 아니다.
         *
         * 실제 쟁합·다중합 우선순위는 9-2에서 처리한다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        HeavenlyStemCombinationCandidate candidate
                                ) ->
                                        candidate
                                                .applicability()
                                                .rank()
                        )
                        .reversed()

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .firstPosition()
                                                .ordinal()
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .secondPosition()
                                                .ordinal()
                        )
        );

        return new HeavenlyStemCombinationCandidateAnalysis(
                combinationAnalysis.dayMaster(),
                combinationAnalysis,
                elementStrengthAnalysis,
                candidates,
                combinationAnalysis.completeChart()
        );
    }
}