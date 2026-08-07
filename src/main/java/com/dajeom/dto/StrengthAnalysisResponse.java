package com.dajeom.dto;

import com.dajeom.model.StrengthAnalysis;

import java.util.List;

/**
 * 일간 신강·신약 종합 판정 Response다.
 */
public record StrengthAnalysisResponse(
        String dayMaster,
        String dayMasterKorean,

        String dayMasterElement,
        String dayMasterElementKorean,

        String strengthLevel,
        String strengthLevelKorean,
        String strengthLevelChinese,

        double strengthScore,

        double supportPercentage,
        double pressurePercentage,

        boolean gainsSeason,

        boolean hasRoot,
        int rootCount,

        double relationAdjustmentScore,

        List<StrengthEvidenceResponse> evidence,

        String ruleVersion
) {

    public StrengthAnalysisResponse {
        evidence = List.copyOf(evidence);
    }

    public static StrengthAnalysisResponse from(
            StrengthAnalysis analysis
    ) {
        return new StrengthAnalysisResponse(
                analysis
                        .dayMaster()
                        .getChineseCharacter(),

                analysis
                        .dayMaster()
                        .getKoreanName(),

                analysis
                        .dayMasterElement()
                        .name(),

                analysis
                        .dayMasterElement()
                        .getKoreanName(),

                analysis
                        .level()
                        .name(),

                analysis
                        .level()
                        .getKoreanName(),

                analysis
                        .level()
                        .getChineseName(),

                analysis.score(),

                analysis.supportPercentage(),

                analysis.pressurePercentage(),

                analysis.gainsSeason(),

                analysis.hasRoot(),

                analysis.rootCount(),

                analysis.relationAdjustmentScore(),

                analysis.evidence()
                        .stream()
                        .map(
                                StrengthEvidenceResponse::from
                        )
                        .toList(),

                analysis.ruleVersion()
        );
    }
}