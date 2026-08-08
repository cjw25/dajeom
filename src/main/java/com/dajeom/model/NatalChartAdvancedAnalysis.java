package com.dajeom.model;

import java.util.Objects;

/**
 * 원국의 고차 분석 결과.
 *
 * 현재 포함:
 *
 * - 핵심 원국 분석
 * - 격국
 * - 조후
 * - 합화 가능성
 * - 통관
 * - 용신/희신/기신 후보
 */
public record NatalChartAdvancedAnalysis(

        NatalChartAnalysis coreAnalysis,

        StructureAnalysis structureAnalysis,

        TemperatureAnalysis temperatureAnalysis,

        CombinationTransformationAnalysis
        combinationTransformationAnalysis,

        MediationAnalysis mediationAnalysis,

        UsefulGodAnalysis usefulGodAnalysis

) {

    public NatalChartAdvancedAnalysis {

        Objects.requireNonNull(
                coreAnalysis,
                "원국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "격국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                temperatureAnalysis,
                "조후 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                combinationTransformationAnalysis,
                "합화 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                mediationAnalysis,
                "통관 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                usefulGodAnalysis,
                "용신 분석 결과는 필수입니다."
        );
    }

    public FourPillars fourPillars() {

        return coreAnalysis.fourPillars();
    }

    public boolean hasHourPillar() {

        return coreAnalysis.hasHourPillar();
    }
}