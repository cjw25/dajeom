package com.dajeom.analyzer;

import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.ElementStrength;
import com.dajeom.model.MediationAnalysis;
import com.dajeom.model.MediationCase;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.rule.MediationRule;
import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.FiveElement;
import com.dajeom.type.MediationStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 오행 사이의 상극 충돌과
 * 통관 가능성을 분석한다.
 *
 * 예:
 *
 * 木과 土가 모두 강함
 *
 * 木克土
 *
 * 이때 火가 충분하면:
 *
 * 木生火
 * 火生土
 *
 * 로 흐름을 연결할 수 있다고 본다.
 */
@Component
public class MediationAnalyzer {

    public MediationAnalysis analyze(
            NatalChartAnalysis natalChart,
            CombinationTransformationAnalysis
                    combinationAnalysis
    ) {

        Objects.requireNonNull(
                natalChart,
                "원국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                combinationAnalysis,
                "합화 분석 결과는 필수입니다."
        );

        ElementStrength strength =
                natalChart.elementStrength();

        List<MediationCase> result =
                new ArrayList<>();

        for (
                FiveElement controller
                : FiveElement.values()
        ) {

            FiveElement controlled =
                    MediationRule
                            .controlledElementOf(
                                    controller
                            );

            double controllerPercentage =
                    strength.percentageOf(
                            controller
                    );

            double controlledPercentage =
                    strength.percentageOf(
                            controlled
                    );

            if (
                    !MediationRule
                            .isConflictRelevant(
                                    controllerPercentage,
                                    controlledPercentage
                            )
            ) {

                continue;
            }

            FiveElement mediator =
                    MediationRule
                            .mediatorElementOf(
                                    controller
                            );

            double mediatorPercentage =
                    strength.percentageOf(
                            mediator
                    );

            MediationStatus status =
                    MediationRule.classify(
                            mediatorPercentage
                    );

            AnalysisConfidence confidence =
                    determineConfidence(
                            natalChart,
                            combinationAnalysis
                    );

            result.add(
                    new MediationCase(
                            controller,
                            controlled,
                            mediator,
                            controllerPercentage,
                            controlledPercentage,
                            mediatorPercentage,
                            status,
                            confidence
                    )
            );
        }

        return new MediationAnalysis(
                result,
                MediationRule.RULE_VERSION
        );
    }

    private AnalysisConfidence determineConfidence(
            NatalChartAnalysis natalChart,
            CombinationTransformationAnalysis
                    combinationAnalysis
    ) {

        if (
                !natalChart.hasHourPillar()
        ) {

            return AnalysisConfidence.LOW;
        }

        /*
         * 합화에 의해 실제 오행 세력이
         * 달라질 가능성이 있다면
         * 기존 ElementStrength만으로 계산한 통관을
         * 확정적으로 보지 않는다.
         */
        if (
                combinationAnalysis
                        .requiresElementAdjustment()
        ) {

            return AnalysisConfidence.LOW;
        }

        return AnalysisConfidence.MEDIUM;
    }
}