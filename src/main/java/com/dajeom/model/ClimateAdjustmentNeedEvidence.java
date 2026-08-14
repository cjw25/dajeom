package com.dajeom.model;

import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.ClimateCorrectionInteraction;
import com.dajeom.type.ClimateMitigationEffect;

import java.util.Objects;

/**
 * 정밀화 6차-2.
 *
 * 조후 필요성 판정의 근거.
 *
 * =================================================
 *
 * temperatureDeviationBasisPoints
 *
 * 6-1 최종 한난값의 절대 크기.
 *
 * =================================================
 *
 * moistureDeviationBasisPoints
 *
 * 6-1 최종 조습값의 절대 크기.
 *
 * =================================================
 *
 * mitigationEffect
 *
 * 월령 baseline과 비교했을 때
 * 원국 전체 오행 구성이:
 *
 * - 완화
 * - 중립
 * - 악화
 * - 과보정
 *
 * 중 어떤 역할을 했는지 보존한다.
 *
 * =================================================
 *
 * correctionInteraction
 *
 * 한난과 조습을 동시에 조정해야 할 때
 * 두 보정 방향이:
 *
 * - 정렬되는지
 * - 경쟁하는지
 *
 * 보존한다.
 *
 * 실제 필요 오행은 아직 결정하지 않는다.
 */
public record ClimateAdjustmentNeedEvidence(

        ClimateEnvironmentAnalysis climateEnvironmentAnalysis,

        int temperatureDeviationBasisPoints,

        int moistureDeviationBasisPoints,

        ClimateAdjustmentNeedLevel temperatureNeed,

        ClimateAdjustmentNeedLevel moistureNeed,

        ClimateAdjustmentNeedLevel overallNeed,

        ClimateMitigationEffect temperatureMitigationEffect,

        ClimateMitigationEffect moistureMitigationEffect,

        ClimateCorrectionInteraction correctionInteraction,

        boolean dualAxisImbalance

) {

    public ClimateAdjustmentNeedEvidence {

        Objects.requireNonNull(
                climateEnvironmentAnalysis,
                "한난조습 환경 분석이 필요합니다."
        );

        Objects.requireNonNull(
                temperatureNeed,
                "한난 조정 필요도가 필요합니다."
        );

        Objects.requireNonNull(
                moistureNeed,
                "조습 조정 필요도가 필요합니다."
        );

        Objects.requireNonNull(
                overallNeed,
                "전체 조후 필요도가 필요합니다."
        );

        Objects.requireNonNull(
                temperatureMitigationEffect,
                "한난 보정 효과가 필요합니다."
        );

        Objects.requireNonNull(
                moistureMitigationEffect,
                "조습 보정 효과가 필요합니다."
        );

        Objects.requireNonNull(
                correctionInteraction,
                "조후 보정 상호작용이 필요합니다."
        );

        if (
                temperatureDeviationBasisPoints < 0
                        || temperatureDeviationBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "한난 편차는 0~10000bp 사이여야 합니다."
            );
        }

        if (
                moistureDeviationBasisPoints < 0
                        || moistureDeviationBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "조습 편차는 0~10000bp 사이여야 합니다."
            );
        }

        int expectedTemperatureDeviation =
                Math.abs(
                        climateEnvironmentAnalysis
                                .evidence()
                                .finalTemperatureBasisPoints()
                );

        int expectedMoistureDeviation =
                Math.abs(
                        climateEnvironmentAnalysis
                                .evidence()
                                .finalMoistureBasisPoints()
                );

        if (
                temperatureDeviationBasisPoints
                        != expectedTemperatureDeviation
        ) {

            throw new IllegalArgumentException(
                    "한난 편차가 6-1 최종 한난값과 일치하지 않습니다."
            );
        }

        if (
                moistureDeviationBasisPoints
                        != expectedMoistureDeviation
        ) {

            throw new IllegalArgumentException(
                    "조습 편차가 6-1 최종 조습값과 일치하지 않습니다."
            );
        }

        ClimateAdjustmentNeedLevel expectedOverall =
                ClimateAdjustmentNeedLevel.max(
                        temperatureNeed,
                        moistureNeed
                );

        if (
                overallNeed != expectedOverall
        ) {

            throw new IllegalArgumentException(
                    "전체 조후 필요도는 한난·조습 필요도 중 높은 단계와 일치해야 합니다."
            );
        }

        boolean expectedDualAxis =
                temperatureNeed.required()
                        && moistureNeed.required();

        if (
                dualAxisImbalance
                        != expectedDualAxis
        ) {

            throw new IllegalArgumentException(
                    "복합 한난조습 불균형 여부가 필요도와 일치하지 않습니다."
            );
        }

        if (
                !temperatureNeed.required()
                        && !moistureNeed.required()
        ) {

            if (
                    !correctionInteraction.none()
            ) {

                throw new IllegalArgumentException(
                        "조후 조정이 필요하지 않으면 보정 관계는 NONE이어야 합니다."
                );
            }

        } else if (
                temperatureNeed.required()
                        ^ moistureNeed.required()
        ) {

            if (
                    !correctionInteraction.singleAxis()
            ) {

                throw new IllegalArgumentException(
                        "단일 축 불균형은 SINGLE_AXIS여야 합니다."
                );
            }

        } else {

            if (
                    !correctionInteraction.dualAxis()
            ) {

                throw new IllegalArgumentException(
                        "복합 불균형은 ALIGNED 또는 COMPETING이어야 합니다."
                );
            }
        }
    }

    public boolean requiresAdjustment() {

        return overallNeed.required();
    }

    public boolean meaningfulAdjustmentRequired() {

        return overallNeed.meaningful();
    }

    public boolean highPriorityAdjustmentRequired() {

        return overallNeed.highPriority();
    }

    public boolean criticalAdjustmentRequired() {

        return overallNeed.critical();
    }

    public boolean temperatureAdjustmentRequired() {

        return temperatureNeed.required();
    }

    public boolean moistureAdjustmentRequired() {

        return moistureNeed.required();
    }

    public boolean temperatureAlreadyMitigated() {

        return temperatureMitigationEffect
                .mitigating();
    }

    public boolean moistureAlreadyMitigated() {

        return moistureMitigationEffect
                .mitigating();
    }

    public boolean anyNaturalMitigation() {

        return temperatureAlreadyMitigated()
                || moistureAlreadyMitigated();
    }

    public boolean bothAxesNaturallyMitigated() {

        return temperatureAlreadyMitigated()
                && moistureAlreadyMitigated();
    }

    public boolean anyElementalAggravation() {

        return temperatureMitigationEffect
                .aggravating()

                || moistureMitigationEffect
                .aggravating();
    }

    public boolean anyOverCorrection() {

        return temperatureMitigationEffect
                .overCorrecting()

                || moistureMitigationEffect
                .overCorrecting();
    }

    public boolean competingCorrection() {

        return correctionInteraction
                .competing();
    }

    public boolean alignedCorrection() {

        return correctionInteraction
                .aligned();
    }
}