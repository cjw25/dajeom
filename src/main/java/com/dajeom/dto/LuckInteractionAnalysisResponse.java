package com.dajeom.dto;

import com.dajeom.model.LuckInteractionAnalysis;

import java.util.List;

public record LuckInteractionAnalysisResponse(
        LuckPeriodEvaluationResponse decadeEvaluation,
        LuckPeriodEvaluationResponse annualEvaluation,

        List<LuckInteractionResponse>
        decadeAnnualInteractions,

        double decadeAnnualRelationScore,

        double combinedScore,

        String combinedLevel,
        String combinedLevelKorean,

        List<String> summarySignals,

        String ruleVersion
) {

    public LuckInteractionAnalysisResponse {
        decadeAnnualInteractions =
                List.copyOf(
                        decadeAnnualInteractions
                );

        summarySignals =
                List.copyOf(summarySignals);
    }

    public static LuckInteractionAnalysisResponse from(
            LuckInteractionAnalysis analysis
    ) {
        return new LuckInteractionAnalysisResponse(
                analysis.decadeEvaluation()
                        == null
                        ? null
                        : LuckPeriodEvaluationResponse.from(
                        analysis.decadeEvaluation()
                ),

                LuckPeriodEvaluationResponse.from(
                        analysis.annualEvaluation()
                ),

                analysis
                        .decadeAnnualInteractions()
                        .stream()
                        .map(
                                LuckInteractionResponse::from
                        )
                        .toList(),

                analysis.decadeAnnualRelationScore(),

                analysis.combinedScore(),

                analysis.combinedLevel().name(),

                analysis
                        .combinedLevel()
                        .getKoreanName(),

                analysis.summarySignals(),

                analysis.ruleVersion()
        );
    }
}