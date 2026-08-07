package com.dajeom.dto;

import com.dajeom.model.LuckPeriodEvaluation;

import java.util.List;

public record LuckPeriodEvaluationResponse(
        String periodType,
        PillarResponse pillar,

        String stemElement,
        String stemElementKorean,

        String branchElement,
        String branchElementKorean,

        String stemTenGod,
        String stemTenGodKorean,

        String branchTenGod,
        String branchTenGodKorean,

        String stemUsefulGodType,
        String stemUsefulGodTypeKorean,

        String branchUsefulGodType,
        String branchUsefulGodTypeKorean,

        double elementCompatibilityScore,
        double tenGodScore,
        double relationScore,
        double totalScore,

        String level,
        String levelKorean,

        List<LuckInteractionResponse> interactions,
        List<String> evidence
) {

    public LuckPeriodEvaluationResponse {
        interactions =
                List.copyOf(interactions);

        evidence =
                List.copyOf(evidence);
    }

    public static LuckPeriodEvaluationResponse from(
            LuckPeriodEvaluation evaluation
    ) {
        return new LuckPeriodEvaluationResponse(
                evaluation.periodType(),

                PillarResponse.from(
                        evaluation.pillar()
                ),

                evaluation
                        .stemElement()
                        .name(),

                evaluation
                        .stemElement()
                        .getKoreanName(),

                evaluation
                        .branchElement()
                        .name(),

                evaluation
                        .branchElement()
                        .getKoreanName(),

                evaluation
                        .stemTenGod()
                        .name(),

                evaluation
                        .stemTenGod()
                        .getKoreanName(),

                evaluation
                        .branchTenGod()
                        .name(),

                evaluation
                        .branchTenGod()
                        .getKoreanName(),

                evaluation
                        .stemUsefulGodType()
                        .name(),

                evaluation
                        .stemUsefulGodType()
                        .getKoreanName(),

                evaluation
                        .branchUsefulGodType()
                        .name(),

                evaluation
                        .branchUsefulGodType()
                        .getKoreanName(),

                evaluation.elementCompatibilityScore(),

                evaluation.tenGodScore(),

                evaluation.relationScore(),

                evaluation.totalScore(),

                evaluation.level().name(),

                evaluation
                        .level()
                        .getKoreanName(),

                evaluation.interactions()
                        .stream()
                        .map(
                                LuckInteractionResponse::from
                        )
                        .toList(),

                evaluation.evidence()
        );
    }
}