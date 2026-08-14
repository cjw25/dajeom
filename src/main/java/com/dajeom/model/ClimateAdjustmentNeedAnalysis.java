package com.dajeom.model;

import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.ClimateCorrectionInteraction;
import com.dajeom.type.ClimateMitigationEffect;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 정밀화 6차-2.
 *
 * 원국의 최종 조후 필요성 분석.
 *
 * 아직:
 *
 * - 火 필요
 * - 水 필요
 * - 木 필요
 * - 조후용신
 *
 * 등을 결정하지 않는다.
 *
 * 오직:
 *
 * 1. 한난 조정이 얼마나 필요한지
 * 2. 조습 조정이 얼마나 필요한지
 * 3. 전체 조후 우선도가 어느 정도인지
 * 4. 원국 자체가 이미 완화 중인지
 * 5. 두 조정 방향이 경쟁하는지
 *
 * 까지만 판정한다.
 */
public record ClimateAdjustmentNeedAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        ClimateAdjustmentNeedLevel temperatureNeed,

        ClimateAdjustmentNeedLevel moistureNeed,

        ClimateAdjustmentNeedLevel overallNeed,

        ClimateCorrectionInteraction correctionInteraction,

        ClimateMitigationEffect temperatureMitigationEffect,

        ClimateMitigationEffect moistureMitigationEffect,

        ClimateAdjustmentNeedEvidence evidence,

        boolean completeChart

) {

    public ClimateAdjustmentNeedAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
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
                correctionInteraction,
                "조후 보정 상호작용이 필요합니다."
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
                evidence,
                "조후 필요성 Evidence가 필요합니다."
        );

        if (
                evidence
                        .climateEnvironmentAnalysis()
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "조후 필요성 Evidence의 일간이 일치하지 않습니다."
            );
        }

        if (
                evidence
                        .climateEnvironmentAnalysis()
                        .monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "조후 필요성 Evidence의 월지가 일치하지 않습니다."
            );
        }

        if (
                evidence.temperatureNeed()
                        != temperatureNeed

                        || evidence.moistureNeed()
                        != moistureNeed

                        || evidence.overallNeed()
                        != overallNeed
        ) {

            throw new IllegalArgumentException(
                    "조후 필요도와 Evidence가 일치하지 않습니다."
            );
        }

        if (
                evidence.correctionInteraction()
                        != correctionInteraction
        ) {

            throw new IllegalArgumentException(
                    "조후 보정 관계와 Evidence가 일치하지 않습니다."
            );
        }

        if (
                evidence.temperatureMitigationEffect()
                        != temperatureMitigationEffect

                        || evidence.moistureMitigationEffect()
                        != moistureMitigationEffect
        ) {

            throw new IllegalArgumentException(
                    "조후 완화 상태와 Evidence가 일치하지 않습니다."
            );
        }

        if (
                evidence
                        .climateEnvironmentAnalysis()
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성 상태가 6-1 결과와 일치하지 않습니다."
            );
        }
    }

    public ClimateEnvironmentAnalysis
    climateEnvironmentAnalysis() {

        return evidence
                .climateEnvironmentAnalysis();
    }

    public boolean adjustmentRequired() {

        return overallNeed
                .required();
    }

    public boolean meaningfulAdjustmentRequired() {

        return overallNeed
                .meaningful();
    }

    public boolean highPriority() {

        return overallNeed
                .highPriority();
    }

    public boolean critical() {

        return overallNeed
                .critical();
    }

    public boolean temperatureAdjustmentRequired() {

        return temperatureNeed
                .required();
    }

    public boolean moistureAdjustmentRequired() {

        return moistureNeed
                .required();
    }

    public boolean dualAxisImbalance() {

        return evidence
                .dualAxisImbalance();
    }

    public boolean correctionConflict() {

        return correctionInteraction
                .competing();
    }

    public boolean correctionAligned() {

        return correctionInteraction
                .aligned();
    }

    public boolean naturalMitigationPresent() {

        return evidence
                .anyNaturalMitigation();
    }

    public boolean elementalAggravationPresent() {

        return evidence
                .anyElementalAggravation();
    }

    public boolean overCorrectionPresent() {

        return evidence
                .anyOverCorrection();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}