package com.dajeom.model;

import com.dajeom.type.FiveElement;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 계절과 지장간 역할을 반영한 오행 세력 계산 결과다.
 *
 * rawScores:
 * 가중치가 적용된 오행별 원시 점수
 *
 * percentages:
 * 전체 점수에서 각 오행이 차지하는 백분율
 */
public record ElementStrength(
        Map<FiveElement, Double> rawScores,
        Map<FiveElement, Double> percentages,
        FiveElement strongestElement,
        FiveElement weakestElement,
        double totalScore,
        String ruleVersion
) {

    public ElementStrength {
        Objects.requireNonNull(
                rawScores,
                "오행 원시 세력 점수는 필수입니다."
        );

        Objects.requireNonNull(
                percentages,
                "오행 세력 비율은 필수입니다."
        );

        Objects.requireNonNull(
                strongestElement,
                "가장 강한 오행은 필수입니다."
        );

        Objects.requireNonNull(
                weakestElement,
                "가장 약한 오행은 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "오행 세력 규칙 버전은 필수입니다."
        );

        rawScores = immutableCopy(rawScores);
        percentages = immutableCopy(percentages);
    }

    public double scoreOf(
            FiveElement element
    ) {
        return rawScores.getOrDefault(
                element,
                0.0
        );
    }

    public double percentageOf(
            FiveElement element
    ) {
        return percentages.getOrDefault(
                element,
                0.0
        );
    }

    private static Map<FiveElement, Double> immutableCopy(
            Map<FiveElement, Double> source
    ) {
        EnumMap<FiveElement, Double> copy =
                new EnumMap<>(FiveElement.class);

        for (FiveElement element : FiveElement.values()) {
            copy.put(
                    element,
                    source.getOrDefault(element, 0.0)
            );
        }

        return Collections.unmodifiableMap(copy);
    }
}