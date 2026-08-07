package com.dajeom.dto;

import com.dajeom.model.ElementStrength;
import com.dajeom.type.FiveElement;

import java.util.List;

/**
 * 계절과 지장간 가중치를 반영한
 * 오행 세력 분석 결과다.
 */
public record ElementStrengthResponse(
        List<ElementStrengthItemResponse> elements,
        String strongestElement,
        String strongestElementKorean,
        String weakestElement,
        String weakestElementKorean,
        double totalScore,
        String ruleVersion
) {

    public ElementStrengthResponse {
        elements = List.copyOf(elements);
    }

    public static ElementStrengthResponse from(
            ElementStrength strength
    ) {
        List<ElementStrengthItemResponse> elements =
                List.of(FiveElement.values())
                        .stream()
                        .map(element ->
                                ElementStrengthItemResponse.of(
                                        element,
                                        strength.scoreOf(element),
                                        strength.percentageOf(element)
                                )
                        )
                        .toList();

        return new ElementStrengthResponse(
                elements,

                strength
                        .strongestElement()
                        .name(),

                strength
                        .strongestElement()
                        .getKoreanName(),

                strength
                        .weakestElement()
                        .name(),

                strength
                        .weakestElement()
                        .getKoreanName(),

                strength.totalScore(),

                strength.ruleVersion()
        );
    }
}