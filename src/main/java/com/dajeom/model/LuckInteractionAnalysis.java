package com.dajeom.model;

import com.dajeom.type.LuckEvaluationLevel;

import java.util.List;
import java.util.Objects;

/**
 * 원국·대운·세운을 결합한 최종 운세 분석 결과다.
 */
public record LuckInteractionAnalysis(
        LuckPeriodEvaluation decadeEvaluation,
        LuckPeriodEvaluation annualEvaluation,
        List<LuckInteraction> decadeAnnualInteractions,
        double decadeAnnualRelationScore,
        double combinedScore,
        LuckEvaluationLevel combinedLevel,
        List<String> summarySignals,
        String ruleVersion
) {

    public LuckInteractionAnalysis {
        Objects.requireNonNull(
                annualEvaluation,
                "세운 평가는 필수입니다."
        );

        Objects.requireNonNull(
                decadeAnnualInteractions,
                "대운-세운 상호작용 목록은 필수입니다."
        );

        Objects.requireNonNull(
                combinedLevel,
                "종합 운 평가 단계는 필수입니다."
        );

        Objects.requireNonNull(
                summarySignals,
                "종합 운 신호는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "운 상호작용 규칙 버전은 필수입니다."
        );

        decadeAnnualInteractions =
                List.copyOf(decadeAnnualInteractions);

        summarySignals =
                List.copyOf(summarySignals);
    }
}