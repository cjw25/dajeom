package com.dajeom.analyzer;

import com.dajeom.model.ClimateAnalysis;
import com.dajeom.model.ElementStrength;
import com.dajeom.model.FourPillars;
import com.dajeom.type.ClimateState;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 월지와 오행 세력을 기반으로 한난조습을 판정한다.
 *
 * DAJEOM_CLIMATE_V1은 조후의 초기 후보 계산 규칙이다.
 */
@Component
public class ClimateAnalyzer {

    public static final String RULE_VERSION =
            "DAJEOM_CLIMATE_V1";

    public ClimateAnalysis analyze(
            FourPillars fourPillars,
            ElementStrength elementStrength
    ) {
        EarthlyBranch monthBranch =
                fourPillars.monthPillar().branch();

        double fire =
                elementStrength.percentageOf(
                        FiveElement.FIRE
                );

        double water =
                elementStrength.percentageOf(
                        FiveElement.WATER
                );

        double wood =
                elementStrength.percentageOf(
                        FiveElement.WOOD
                );

        double metal =
                elementStrength.percentageOf(
                        FiveElement.METAL
                );

        double earth =
                elementStrength.percentageOf(
                        FiveElement.EARTH
                );

        double seasonalHeat =
                seasonalHeatScore(monthBranch);

        double seasonalCold =
                seasonalColdScore(monthBranch);

        double seasonalDry =
                seasonalDryScore(monthBranch);

        double seasonalMoisture =
                seasonalMoistureScore(monthBranch);

        double heatScore =
                round(
                        seasonalHeat
                                + fire
                                + wood * 0.25
                );

        double coldScore =
                round(
                        seasonalCold
                                + water
                                + metal * 0.15
                );

        double dryScore =
                round(
                        seasonalDry
                                + metal
                                + earth * 0.25
                );

        double moistureScore =
                round(
                        seasonalMoisture
                                + water
                                + wood * 0.15
                );

        ClimateState temperatureState =
                determineTemperatureState(
                        heatScore,
                        coldScore
                );

        ClimateState moistureState =
                determineMoistureState(
                        dryScore,
                        moistureScore
                );

        List<FiveElement> requiredElements =
                determineRequiredElements(
                        temperatureState,
                        moistureState
                );

        List<String> evidence =
                createEvidence(
                        monthBranch,
                        temperatureState,
                        moistureState,
                        heatScore,
                        coldScore,
                        dryScore,
                        moistureScore
                );

        return new ClimateAnalysis(
                temperatureState,
                moistureState,
                heatScore,
                coldScore,
                dryScore,
                moistureScore,
                requiredElements,
                evidence,
                RULE_VERSION
        );
    }

    private double seasonalHeatScore(
            EarthlyBranch branch
    ) {
        return switch (branch) {
            case SA, O -> 35.0;
            case MI -> 25.0;
            case IN, MYO, JIN -> 15.0;
            case SIN, YU, SUL -> 5.0;
            case HAE, JA, CHUK -> 0.0;
        };
    }

    private double seasonalColdScore(
            EarthlyBranch branch
    ) {
        return switch (branch) {
            case HAE, JA -> 35.0;
            case CHUK -> 25.0;
            case SIN, YU, SUL -> 15.0;
            case IN, MYO, JIN -> 5.0;
            case SA, O, MI -> 0.0;
        };
    }

    private double seasonalDryScore(
            EarthlyBranch branch
    ) {
        return switch (branch) {
            case SIN, YU, SUL -> 30.0;
            case SA, O, MI -> 20.0;
            case CHUK, JIN -> 10.0;
            case IN, MYO, HAE, JA -> 5.0;
        };
    }

    private double seasonalMoistureScore(
            EarthlyBranch branch
    ) {
        return switch (branch) {
            case HAE, JA, CHUK -> 30.0;
            case JIN -> 25.0;
            case IN, MYO -> 15.0;
            case SIN, YU, SUL -> 5.0;
            case SA, O, MI -> 0.0;
        };
    }

    private ClimateState determineTemperatureState(
            double heatScore,
            double coldScore
    ) {
        double difference =
                heatScore - coldScore;

        if (difference <= -35.0) {
            return ClimateState.VERY_COLD;
        }

        if (difference <= -15.0) {
            return ClimateState.COLD;
        }

        if (difference < 15.0) {
            return ClimateState
                    .BALANCED_TEMPERATURE;
        }

        if (difference < 35.0) {
            return ClimateState.HOT;
        }

        return ClimateState.VERY_HOT;
    }

    private ClimateState determineMoistureState(
            double dryScore,
            double moistureScore
    ) {
        double difference =
                dryScore - moistureScore;

        if (difference >= 35.0) {
            return ClimateState.VERY_DRY;
        }

        if (difference >= 15.0) {
            return ClimateState.DRY;
        }

        if (difference > -15.0) {
            return ClimateState
                    .BALANCED_MOISTURE;
        }

        if (difference > -35.0) {
            return ClimateState.HUMID;
        }

        return ClimateState.VERY_HUMID;
    }

    private List<FiveElement>
    determineRequiredElements(
            ClimateState temperatureState,
            ClimateState moistureState
    ) {
        List<FiveElement> result =
                new ArrayList<>();

        if (temperatureState
                == ClimateState.VERY_COLD
                || temperatureState
                == ClimateState.COLD) {

            result.add(FiveElement.FIRE);
        }

        if (temperatureState
                == ClimateState.VERY_HOT
                || temperatureState
                == ClimateState.HOT) {

            result.add(FiveElement.WATER);
        }

        if (moistureState
                == ClimateState.VERY_DRY
                || moistureState
                == ClimateState.DRY) {

            if (!result.contains(FiveElement.WATER)) {
                result.add(FiveElement.WATER);
            }
        }

        if (moistureState
                == ClimateState.VERY_HUMID
                || moistureState
                == ClimateState.HUMID) {

            if (!result.contains(FiveElement.FIRE)) {
                result.add(FiveElement.FIRE);
            }

            result.add(FiveElement.EARTH);
        }

        return List.copyOf(result);
    }

    private List<String> createEvidence(
            EarthlyBranch monthBranch,
            ClimateState temperatureState,
            ClimateState moistureState,
            double heatScore,
            double coldScore,
            double dryScore,
            double moistureScore
    ) {
        return List.of(
                "월지 "
                        + monthBranch.getChineseCharacter()
                        + "을 계절 기준으로 사용함",

                "온도 상태는 "
                        + temperatureState.getKoreanName()
                        + "으로 판정됨",

                "습도 상태는 "
                        + moistureState.getKoreanName()
                        + "으로 판정됨",

                "열 점수 "
                        + heatScore
                        + ", 냉 점수 "
                        + coldScore,

                "건조 점수 "
                        + dryScore
                        + ", 습윤 점수 "
                        + moistureScore
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0)
                / 100.0;
    }
}