package com.dajeom.analyzer;

import com.dajeom.model.BranchRelation;
import com.dajeom.model.CombinationTransformation;
import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.StemRelation;
import com.dajeom.rule.CombinationRule;
import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.BranchRelationType;
import com.dajeom.type.CombinationScope;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.TransformationStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 원국에 존재하는 천간합/지지합이
 * 실제 합화 조건을 갖추고 있는지 분석한다.
 *
 * 관계의 존재:
 * RelationAnalyzer
 *
 * 실제 합화 가능성:
 * CombinationTransformationAnalyzer
 *
 * 로 책임을 분리한다.
 */
@Component
public class CombinationTransformationAnalyzer {

    public CombinationTransformationAnalysis analyze(
            NatalChartAnalysis natalChart
    ) {

        Objects.requireNonNull(
                natalChart,
                "원국 분석 결과는 필수입니다."
        );

        List<CombinationTransformation> result =
                new ArrayList<>();

        analyzeStemCombinations(
                natalChart,
                result
        );

        analyzeBranchCombinations(
                natalChart,
                result
        );

        return new CombinationTransformationAnalysis(
                result,
                CombinationRule.RULE_VERSION
        );
    }

    private void analyzeStemCombinations(
            NatalChartAnalysis natalChart,
            List<CombinationTransformation> result
    ) {

        for (
                StemRelation relation
                : natalChart
                .relationAnalysis()
                .stemRelations()
        ) {

            List<PillarPosition> positions =
                    List.of(
                            relation.firstPosition(),
                            relation.secondPosition()
                    );

            result.add(
                    createTransformation(
                            natalChart,
                            CombinationScope.STEM,
                            positions,
                            relation.combinationElement()
                    )
            );
        }
    }

    private void analyzeBranchCombinations(
            NatalChartAnalysis natalChart,
            List<CombinationTransformation> result
    ) {

        for (
                BranchRelation relation
                : natalChart
                .relationAnalysis()
                .branchRelations()
        ) {

            if (
                    relation.type()
                            != BranchRelationType.SIX_COMBINATION
                            && relation.type()
                            != BranchRelationType.THREE_HARMONY
            ) {

                continue;
            }

            FiveElement targetElement =
                    relation
                            .optionalCombinationElement()
                            .orElse(null);

            if (
                    targetElement == null
            ) {

                continue;
            }

            CombinationScope scope =
                    relation.type()
                            == BranchRelationType.SIX_COMBINATION
                            ? CombinationScope.SIX_COMBINATION
                            : CombinationScope.THREE_HARMONY;

            result.add(
                    createTransformation(
                            natalChart,
                            scope,
                            relation.positions(),
                            targetElement
                    )
            );
        }
    }

    private CombinationTransformation
    createTransformation(
            NatalChartAnalysis natalChart,
            CombinationScope scope,
            List<PillarPosition> positions,
            FiveElement targetElement
    ) {

        double targetPercentage =
                natalChart
                        .elementStrength()
                        .percentageOf(
                                targetElement
                        );

        FiveElement monthElement =
                natalChart
                        .fourPillars()
                        .monthPillar()
                        .branch()
                        .getElement();

        boolean monthSupported =
                CombinationRule.isMonthSupported(
                        monthElement,
                        targetElement
                );

        boolean disruptedByClash =
                isDisruptedByClash(
                        natalChart,
                        positions
                );

        TransformationStatus status =
                CombinationRule.classify(
                        scope,
                        targetPercentage,
                        monthSupported,
                        disruptedByClash
                );

        AnalysisConfidence confidence =
                determineConfidence(
                        natalChart,
                        status
                );

        return new CombinationTransformation(
                scope,
                positions,
                targetElement,
                status,
                targetPercentage,
                monthSupported,
                disruptedByClash,
                confidence,
                CombinationRule.RULE_VERSION
        );
    }

    /**
     * 합에 참여하는 위치 중 하나라도
     * 다른 지지와 충 관계를 가진 경우
     * 합의 방해 요인이 있다고 본다.
     */
    private boolean isDisruptedByClash(
            NatalChartAnalysis natalChart,
            List<PillarPosition> positions
    ) {

        return natalChart
                .relationAnalysis()
                .branchRelations()
                .stream()
                .filter(
                        relation ->
                                relation.type()
                                        == BranchRelationType.CLASH
                )
                .anyMatch(
                        relation ->
                                relation
                                        .positions()
                                        .stream()
                                        .anyMatch(
                                                positions::contains
                                        )
                );
    }

    private AnalysisConfidence determineConfidence(
            NatalChartAnalysis natalChart,
            TransformationStatus status
    ) {

        if (
                !natalChart.hasHourPillar()
        ) {

            return AnalysisConfidence.LOW;
        }

        if (
                status
                        == TransformationStatus.UNDETERMINED
        ) {

            return AnalysisConfidence.LOW;
        }

        /*
         * 합화 V1 Rule이므로
         * 성립/불성립 판정도 최대 MEDIUM으로 둔다.
         */
        return AnalysisConfidence.MEDIUM;
    }
}