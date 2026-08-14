package com.dajeom.model;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.OfficerCompositePatternLevel;
import com.dajeom.type.OfficerCompositePatternType;
import com.dajeom.type.StructureTenGodType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-5.
 *
 * 정관·칠살 복합구조 분석.
 *
 * =================================================
 *
 * 이번 단계에서는:
 *
 * - 관살 전체 구조
 * - 정관/칠살 중심성
 * - 혼재 여부
 * - 일간 압박
 * - 식상 Context
 * - 인성 Context
 * - 재성 Context
 *
 * 까지만 분석한다.
 */
public record OfficerCompositeAnalysis(

        HeavenlyStem dayMaster,

        TenGodDistributionAnalysis tenGodDistributionAnalysis,

        DayMasterStrengthAnalysis dayMasterStrengthAnalysis,

        TenGodPowerSummary directOfficerSummary,

        TenGodPowerSummary sevenKillingsSummary,

        int officerFamilyPower,

        int outputFamilyPower,

        int resourceFamilyPower,

        int wealthFamilyPower,

        int officerFamilyShareBasisPoints,

        List<OfficerCompositeSignal> signals,

        boolean completeChart

) {

    public OfficerCompositeAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                tenGodDistributionAnalysis,
                "10-1 십성 분포 분석이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterStrengthAnalysis,
                "4차 일간 강약 분석이 필요합니다."
        );

        Objects.requireNonNull(
                directOfficerSummary,
                "정관 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                sevenKillingsSummary,
                "칠살 Summary가 필요합니다."
        );

        if (
                directOfficerSummary.tenGodType()
                        != StructureTenGodType.DIRECT_OFFICER
        ) {

            throw new IllegalArgumentException(
                    "directOfficerSummary는 정관 Summary여야 합니다."
            );
        }

        if (
                sevenKillingsSummary.tenGodType()
                        != StructureTenGodType.SEVEN_KILLINGS
        ) {

            throw new IllegalArgumentException(
                    "sevenKillingsSummary는 칠살 Summary여야 합니다."
            );
        }

        if (
                tenGodDistributionAnalysis.dayMaster()
                        != dayMaster

                        || dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "10-1과 4차 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                officerFamilyPower
                        != directOfficerSummary.allocatedPower()
                        + sevenKillingsSummary.allocatedPower()
        ) {

            throw new IllegalArgumentException(
                    "관살 전체 세력이 정관+칠살 Summary와 일치하지 않습니다."
            );
        }

        if (
                officerFamilyPower
                        != tenGodDistributionAnalysis
                        .officerFamilyPower()
        ) {

            throw new IllegalArgumentException(
                    "관살 전체 세력이 10-1 분석과 일치하지 않습니다."
            );
        }

        if (
                outputFamilyPower
                        != tenGodDistributionAnalysis
                        .outputFamilyPower()
        ) {

            throw new IllegalArgumentException(
                    "식상 전체 세력이 10-1 분석과 일치하지 않습니다."
            );
        }

        if (
                resourceFamilyPower
                        != tenGodDistributionAnalysis
                        .resourceFamilyPower()
        ) {

            throw new IllegalArgumentException(
                    "인성 전체 세력이 10-1 분석과 일치하지 않습니다."
            );
        }

        if (
                wealthFamilyPower
                        != tenGodDistributionAnalysis
                        .wealthFamilyPower()
        ) {

            throw new IllegalArgumentException(
                    "재성 전체 세력이 10-1 분석과 일치하지 않습니다."
            );
        }

        if (
                officerFamilyShareBasisPoints < 0
                        || officerFamilyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "관살 점유율은 0~10000bp 범위여야 합니다."
            );
        }

        if (
                tenGodDistributionAnalysis.completeChart()
                        != completeChart

                        || dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "10-1과 4차 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        signals =
                signals == null
                        ? List.of()
                        : List.copyOf(
                        signals
                );
    }

    public DayMasterStrengthLevel dayMasterStrengthLevel() {

        return dayMasterStrengthAnalysis
                .level();
    }

    public int directOfficerPower() {

        return directOfficerSummary
                .allocatedPower();
    }

    public int sevenKillingsPower() {

        return sevenKillingsSummary
                .allocatedPower();
    }

    public int directOfficerOccurrenceCount() {

        return directOfficerSummary
                .occurrenceCount();
    }

    public int sevenKillingsOccurrenceCount() {

        return sevenKillingsSummary
                .occurrenceCount();
    }

    public int totalOfficerOccurrenceCount() {

        return directOfficerOccurrenceCount()
                + sevenKillingsOccurrenceCount();
    }

    public boolean directOfficerPresent() {

        return directOfficerSummary
                .present();
    }

    public boolean sevenKillingsPresent() {

        return sevenKillingsSummary
                .present();
    }

    public boolean directOfficerVisible() {

        return directOfficerSummary
                .visible();
    }

    public boolean sevenKillingsVisible() {

        return sevenKillingsSummary
                .visible();
    }

    public boolean directOfficerMonthPresent() {

        return directOfficerSummary
                .monthPresent();
    }

    public boolean sevenKillingsMonthPresent() {

        return sevenKillingsSummary
                .monthPresent();
    }

    public boolean officerPresent() {

        return directOfficerPresent()
                || sevenKillingsPresent();
    }

    public boolean directOfficerAndSevenKillingsCoexist() {

        return directOfficerPresent()
                && sevenKillingsPresent();
    }

    public boolean outputPresent() {

        return outputFamilyPower > 0;
    }

    public boolean resourcePresent() {

        return resourceFamilyPower > 0;
    }

    public boolean wealthPresent() {

        return wealthFamilyPower > 0;
    }

    public double officerFamilySharePercent() {

        return officerFamilyShareBasisPoints
                / 100.0;
    }

    /*
     * =================================================
     * Pattern 조회
     * =================================================
     */

    public List<OfficerCompositeSignal> signalsOf(
            OfficerCompositePatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.patternType()
                                        == patternType
                )
                .toList();
    }

    public Optional<OfficerCompositeSignal> signalOf(
            OfficerCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .findFirst();
    }

    public boolean hasPattern(
            OfficerCompositePatternType patternType
    ) {

        return !signalsOf(
                patternType
        )
                .isEmpty();
    }

    public boolean hasMeaningfulPattern(
            OfficerCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        OfficerCompositeSignal
                                ::meaningful
                );
    }

    public boolean hasStrongPattern(
            OfficerCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        OfficerCompositeSignal
                                ::strong
                );
    }

    public List<OfficerCompositeSignal> meaningfulSignals() {

        return signals
                .stream()
                .filter(
                        OfficerCompositeSignal
                                ::meaningful
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        (
                                                OfficerCompositeSignal signal
                                        ) ->
                                                signal.level()
                                                        .rank()
                                )
                                .reversed()
                )
                .toList();
    }

    public List<OfficerCompositeSignal> strongSignals() {

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.level()
                                        == OfficerCompositePatternLevel.STRONG
                )
                .toList();
    }

    /*
     * =================================================
     * Shortcut
     * =================================================
     */

    public boolean officerDeveloped() {

        return hasMeaningfulPattern(
                OfficerCompositePatternType
                        .OFFICER_DEVELOPED
        );
    }

    public boolean directOfficerCentered() {

        return hasMeaningfulPattern(
                OfficerCompositePatternType
                        .DIRECT_OFFICER_CENTERED
        );
    }

    public boolean sevenKillingsCentered() {

        return hasMeaningfulPattern(
                OfficerCompositePatternType
                        .SEVEN_KILLINGS_CENTERED
        );
    }

    public boolean mixedOfficerKillings() {

        return hasMeaningfulPattern(
                OfficerCompositePatternType
                        .MIXED_OFFICER_KILLINGS
        );
    }

    public boolean weakDayMasterOfficerPressure() {

        return hasMeaningfulPattern(
                OfficerCompositePatternType
                        .WEAK_DAY_MASTER_OFFICER_PRESSURE
        );
    }

    public boolean outputControlContext() {

        return hasMeaningfulPattern(
                OfficerCompositePatternType
                        .OUTPUT_CONTROL_CONTEXT
        );
    }

    public boolean resourceMediationContext() {

        return hasMeaningfulPattern(
                OfficerCompositePatternType
                        .RESOURCE_MEDIATION_CONTEXT
        );
    }

    public boolean wealthSupportContext() {

        return hasMeaningfulPattern(
                OfficerCompositePatternType
                        .WEALTH_SUPPORT_CONTEXT
        );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}