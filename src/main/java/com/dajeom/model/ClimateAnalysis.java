package com.dajeom.model;

import com.dajeom.type.ClimateState;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;

/**
 * 원국의 계절적 한난조습 분석 결과다.
 */
public record ClimateAnalysis(
        ClimateState temperatureState,
        ClimateState moistureState,
        double heatScore,
        double coldScore,
        double dryScore,
        double moistureScore,
        List<FiveElement> requiredElements,
        List<String> evidence,
        String ruleVersion
) {

    public ClimateAnalysis {
        Objects.requireNonNull(
                temperatureState,
                "온도 상태는 필수입니다."
        );

        Objects.requireNonNull(
                moistureState,
                "습도 상태는 필수입니다."
        );

        Objects.requireNonNull(
                requiredElements,
                "조후 필요 오행은 필수입니다."
        );

        Objects.requireNonNull(
                evidence,
                "조후 근거는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "조후 규칙 버전은 필수입니다."
        );

        requiredElements = List.copyOf(requiredElements);
        evidence = List.copyOf(evidence);
    }
}