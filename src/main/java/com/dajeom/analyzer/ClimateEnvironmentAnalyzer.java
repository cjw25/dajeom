package com.dajeom.analyzer;

import com.dajeom.model.ClimateEnvironmentAnalysis;
import com.dajeom.model.ClimateEnvironmentEvidence;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.ClimateEnvironmentRule;
import com.dajeom.type.ClimateEnvironmentType;
import com.dajeom.type.ClimateMoistureState;
import com.dajeom.type.ClimateTemperatureState;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 6차-1.
 *
 * 월령의 계절 한난조습과
 * 3차에서 계산된 원국 전체 오행 세력을 결합해
 * 현재 원국의 기후 환경을 분석한다.
 *
 * 처리:
 *
 * 월령
 * ↓
 * 계절 baseline
 * ↓
 * 오행 세력 보정
 * ↓
 * 한난
 * ↓
 * 조습
 * ↓
 * 전체 환경 유형
 *
 * 중요한 원칙:
 *
 * 1.
 * 오행 세력을 다시 계산하지 않는다.
 *
 * 2.
 * 월령의 계절 권위를 기본축으로 둔다.
 *
 * 3.
 * 원국 오행 세력은 계절을 제한적으로
 *    보정하는 역할만 한다.
 *
 * 4.
 * 조후 필요 오행은 아직 결정하지 않는다.
 *
 * 5.
 * 출생시간 미상일 경우
 *    알려진 원국 기준으로 그대로 계산하고
 *    completeChart=false를 보존한다.
 */
@Component
public class ClimateEnvironmentAnalyzer {

    private final ClimateEnvironmentRule
            climateEnvironmentRule;

    public ClimateEnvironmentAnalyzer(
            ClimateEnvironmentRule
                    climateEnvironmentRule
    ) {

        this.climateEnvironmentRule =
                Objects.requireNonNull(
                        climateEnvironmentRule,
                        "ClimateEnvironmentRule이 필요합니다."
                );
    }

    /**
     * 5차 StructureAnalysis 이후
     * 6차-1을 수행한다.
     */
    public ClimateEnvironmentAnalysis analyze(
            StructureAnalysis structure
    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석 결과가 필요합니다."
        );

        ElementStrengthAnalysis elementStrength =
                structure
                        .dayMasterStrengthAnalysis()
                        .elementStrengthAnalysis();

        EarthlyBranch monthBranch =
                structure.monthBranch();

        int woodShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                FiveElement.WOOD
                        );

        int fireShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                FiveElement.FIRE
                        );

        int earthShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                FiveElement.EARTH
                        );

        int metalShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                FiveElement.METAL
                        );

        int waterShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                FiveElement.WATER
                        );

        validateElementShares(
                woodShare,
                fireShare,
                earthShare,
                metalShare,
                waterShare
        );

        ClimateEnvironmentRule.SeasonalClimateBaseline
                baseline =
                climateEnvironmentRule
                        .seasonalBaseline(
                                monthBranch
                        );

        int temperatureAdjustment =
                climateEnvironmentRule
                        .calculateTemperatureAdjustment(
                                woodShare,
                                fireShare,
                                metalShare,
                                waterShare
                        );

        int moistureAdjustment =
                climateEnvironmentRule
                        .calculateMoistureAdjustment(
                                woodShare,
                                fireShare,
                                earthShare,
                                metalShare,
                                waterShare
                        );

        int finalTemperature =
                climateEnvironmentRule
                        .calculateFinalTemperature(
                                baseline
                                        .temperatureBasisPoints(),
                                temperatureAdjustment
                        );

        int finalMoisture =
                climateEnvironmentRule
                        .calculateFinalMoisture(
                                baseline
                                        .moistureBasisPoints(),
                                moistureAdjustment
                        );

        ClimateTemperatureState temperatureState =
                climateEnvironmentRule
                        .determineTemperatureState(
                                finalTemperature
                        );

        ClimateMoistureState moistureState =
                climateEnvironmentRule
                        .determineMoistureState(
                                finalMoisture
                        );

        ClimateEnvironmentType environmentType =
                climateEnvironmentRule
                        .determineEnvironmentType(
                                temperatureState,
                                moistureState
                        );

        ClimateEnvironmentEvidence evidence =
                new ClimateEnvironmentEvidence(
                        monthBranch,
                        baseline.temperatureBasisPoints(),
                        temperatureAdjustment,
                        finalTemperature,
                        baseline.moistureBasisPoints(),
                        moistureAdjustment,
                        finalMoisture,
                        woodShare,
                        fireShare,
                        earthShare,
                        metalShare,
                        waterShare
                );

        return new ClimateEnvironmentAnalysis(
                structure.dayMaster(),
                monthBranch,
                temperatureState,
                moistureState,
                environmentType,
                evidence,
                structure.completeChart()
        );
    }

    private void validateElementShares(

            int wood,

            int fire,

            int earth,

            int metal,

            int water

    ) {

        int total =
                wood
                        + fire
                        + earth
                        + metal
                        + water;

        if (
                total != 10_000
        ) {

            throw new IllegalStateException(
                    "3차 오행 비중 합계가 10000bp가 아닙니다: "
                            + total
            );
        }
    }
}