package com.dajeom.dto;

import com.dajeom.model.ClimateAnalysis;

import java.util.List;

public record ClimateAnalysisResponse(
        String temperatureState,
        String temperatureStateKorean,
        String moistureState,
        String moistureStateKorean,
        double heatScore,
        double coldScore,
        double dryScore,
        double moistureScore,
        List<String> requiredElements,
        List<String> requiredElementsKorean,
        List<String> evidence,
        String ruleVersion
) {

    public ClimateAnalysisResponse {
        requiredElements =
                List.copyOf(requiredElements);

        requiredElementsKorean =
                List.copyOf(requiredElementsKorean);

        evidence = List.copyOf(evidence);
    }

    public static ClimateAnalysisResponse from(
            ClimateAnalysis analysis
    ) {
        return new ClimateAnalysisResponse(
                analysis.temperatureState().name(),

                analysis
                        .temperatureState()
                        .getKoreanName(),

                analysis.moistureState().name(),

                analysis
                        .moistureState()
                        .getKoreanName(),

                analysis.heatScore(),

                analysis.coldScore(),

                analysis.dryScore(),

                analysis.moistureScore(),

                analysis.requiredElements()
                        .stream()
                        .map(Enum::name)
                        .toList(),

                analysis.requiredElements()
                        .stream()
                        .map(element ->
                                element.getKoreanName()
                        )
                        .toList(),

                analysis.evidence(),

                analysis.ruleVersion()
        );
    }
}