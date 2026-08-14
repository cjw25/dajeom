package com.dajeom.analyzer;

import com.dajeom.model.ClimateAdjustmentNeedAnalysis;
import com.dajeom.model.ClimateAdjustmentNeedEvidence;
import com.dajeom.model.ClimateEnvironmentAnalysis;
import com.dajeom.model.ClimateEnvironmentEvidence;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.ClimateAdjustmentNeedRule;
import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.ClimateCorrectionInteraction;
import com.dajeom.type.ClimateMitigationEffect;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 6차-2.
 *
 * 6-1에서 확정된 한난조습 환경을 이용해
 * 실제 조후 조정 필요성을 판정한다.
 *
 * 처리:
 *
 * 6-1 ClimateEnvironmentAnalysis
 *
 * ↓
 *
 * 한난 편차
 * 조습 편차
 *
 * ↓
 *
 * 한난 필요도
 * 조습 필요도
 *
 * ↓
 *
 * 월령 대비 원국 자체 완화/악화
 *
 * ↓
 *
 * 복합축 보정 충돌 여부
 *
 * ↓
 *
 * ClimateAdjustmentNeedAnalysis
 *
 * 중요한 원칙:
 *
 * 실제 조후 오행 후보는
 * 아직 계산하지 않는다.
 *
 * 그 책임은 6-3이다.
 */
@Component
public class ClimateAdjustmentNeedAnalyzer {

    private final ClimateEnvironmentAnalyzer
            climateEnvironmentAnalyzer;

    private final ClimateAdjustmentNeedRule
            climateAdjustmentNeedRule;

    public ClimateAdjustmentNeedAnalyzer(

            ClimateEnvironmentAnalyzer
                    climateEnvironmentAnalyzer,

            ClimateAdjustmentNeedRule
                    climateAdjustmentNeedRule

    ) {

        this.climateEnvironmentAnalyzer =
                Objects.requireNonNull(
                        climateEnvironmentAnalyzer,
                        "ClimateEnvironmentAnalyzer가 필요합니다."
                );

        this.climateAdjustmentNeedRule =
                Objects.requireNonNull(
                        climateAdjustmentNeedRule,
                        "ClimateAdjustmentNeedRule이 필요합니다."
                );
    }

    /**
     * 5차 StructureAnalysis에서
     * 6-1과 6-2를 연속 실행한다.
     */
    public ClimateAdjustmentNeedAnalysis analyze(
            StructureAnalysis structure
    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석 결과가 필요합니다."
        );

        ClimateEnvironmentAnalysis climate =
                climateEnvironmentAnalyzer
                        .analyze(
                                structure
                        );

        return analyze(
                climate
        );
    }

    /**
     * 6-1 결과가 이미 존재하면
     * 재사용한다.
     *
     * 6차 전체 Engine에서는
     * 이 메서드를 사용한다.
     */
    public ClimateAdjustmentNeedAnalysis analyze(
            ClimateEnvironmentAnalysis climate
    ) {

        Objects.requireNonNull(
                climate,
                "한난조습 환경 분석이 필요합니다."
        );

        ClimateEnvironmentEvidence climateEvidence =
                climate.evidence();

        int finalTemperature =
                climateEvidence
                        .finalTemperatureBasisPoints();

        int finalMoisture =
                climateEvidence
                        .finalMoistureBasisPoints();

        int temperatureDeviation =
                Math.abs(
                        finalTemperature
                );

        int moistureDeviation =
                Math.abs(
                        finalMoisture
                );

        /*
         * ================================================
         * 한난 조정 필요도
         * ================================================
         */
        ClimateAdjustmentNeedLevel temperatureNeed =
                climateAdjustmentNeedRule
                        .determineTemperatureNeed(
                                finalTemperature
                        );

        /*
         * ================================================
         * 조습 조정 필요도
         * ================================================
         */
        ClimateAdjustmentNeedLevel moistureNeed =
                climateAdjustmentNeedRule
                        .determineMoistureNeed(
                                finalMoisture
                        );

        /*
         * ================================================
         * 전체 필요도
         * ================================================
         *
         * 두 축 가운데 높은 것을 사용한다.
         *
         * 복합 불균형이라는 이유만으로
         * 임의로 한 단계 올리지 않는다.
         */
        ClimateAdjustmentNeedLevel overallNeed =
                climateAdjustmentNeedRule
                        .determineOverallNeed(
                                temperatureNeed,
                                moistureNeed
                        );

        /*
         * ================================================
         * 월령 한난 대비
         * 원국 오행의 완화/악화
         * ================================================
         */
        ClimateMitigationEffect
                temperatureMitigationEffect =
                climateAdjustmentNeedRule
                        .determineMitigationEffect(
                                climateEvidence
                                        .seasonalTemperatureBasisPoints(),

                                climateEvidence
                                        .elementalTemperatureAdjustmentBasisPoints(),

                                finalTemperature
                        );

        /*
         * ================================================
         * 월령 조습 대비
         * 원국 오행의 완화/악화
         * ================================================
         */
        ClimateMitigationEffect
                moistureMitigationEffect =
                climateAdjustmentNeedRule
                        .determineMitigationEffect(
                                climateEvidence
                                        .seasonalMoistureBasisPoints(),

                                climateEvidence
                                        .elementalMoistureAdjustmentBasisPoints(),

                                finalMoisture
                        );

        /*
         * ================================================
         * 두 조후 축의 보정 관계
         * ================================================
         */
        ClimateCorrectionInteraction
                correctionInteraction =
                climateAdjustmentNeedRule
                        .determineCorrectionInteraction(
                                climate
                        );

        boolean dualAxisImbalance =
                temperatureNeed.required()
                        && moistureNeed.required();

        ClimateAdjustmentNeedEvidence evidence =
                new ClimateAdjustmentNeedEvidence(
                        climate,
                        temperatureDeviation,
                        moistureDeviation,
                        temperatureNeed,
                        moistureNeed,
                        overallNeed,
                        temperatureMitigationEffect,
                        moistureMitigationEffect,
                        correctionInteraction,
                        dualAxisImbalance
                );

        return new ClimateAdjustmentNeedAnalysis(
                climate.dayMaster(),
                climate.monthBranch(),
                temperatureNeed,
                moistureNeed,
                overallNeed,
                correctionInteraction,
                temperatureMitigationEffect,
                moistureMitigationEffect,
                evidence,
                climate.completeChart()
        );
    }
}