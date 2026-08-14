package com.dajeom.model;

import com.dajeom.type.CompanionCompositePatternLevel;
import com.dajeom.type.CompanionCompositePatternType;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-2.
 *
 * 비견·겁재 복합구조 최종 분석.
 */
public record CompanionCompositeAnalysis(

        HeavenlyStem dayMaster,

        TenGodDistributionAnalysis tenGodDistributionAnalysis,

        DayMasterStrengthAnalysis dayMasterStrengthAnalysis,

        TenGodPowerSummary companionSummary,

        TenGodPowerSummary robWealthSummary,

        int companionFamilyPower,

        int wealthFamilyPower,

        int familyShareBasisPoints,

        List<CompanionCompositeSignal> signals,

        boolean completeChart

) {

    public CompanionCompositeAnalysis {

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
                companionSummary,
                "비견 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                robWealthSummary,
                "겁재 Summary가 필요합니다."
        );

        if (
                companionSummary.tenGodType()
                        != StructureTenGodType.COMPANION
        ) {

            throw new IllegalArgumentException(
                    "companionSummary는 비견 Summary여야 합니다."
            );
        }

        if (
                robWealthSummary.tenGodType()
                        != StructureTenGodType.ROB_WEALTH
        ) {

            throw new IllegalArgumentException(
                    "robWealthSummary는 겁재 Summary여야 합니다."
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
                companionFamilyPower
                        != companionSummary.allocatedPower()
                        + robWealthSummary.allocatedPower()
        ) {

            throw new IllegalArgumentException(
                    "비겁 전체 세력이 비견+겁재 Summary와 일치하지 않습니다."
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
                familyShareBasisPoints < 0
                        || familyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "비겁 점유율은 0~10000bp 범위여야 합니다."
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

    public int companionPower() {

        return companionSummary
                .allocatedPower();
    }

    public int robWealthPower() {

        return robWealthSummary
                .allocatedPower();
    }

    public int companionOccurrenceCount() {

        return companionSummary
                .occurrenceCount();
    }

    public int robWealthOccurrenceCount() {

        return robWealthSummary
                .occurrenceCount();
    }

    public int totalCompanionOccurrenceCount() {

        return companionOccurrenceCount()
                + robWealthOccurrenceCount();
    }

    public boolean companionPresent() {

        return companionSummary
                .present();
    }

    public boolean robWealthPresent() {

        return robWealthSummary
                .present();
    }

    public boolean companionVisible() {

        return companionSummary
                .visible();
    }

    public boolean robWealthVisible() {

        return robWealthSummary
                .visible();
    }

    public boolean companionMonthPresent() {

        return companionSummary
                .monthPresent();
    }

    public boolean robWealthMonthPresent() {

        return robWealthSummary
                .monthPresent();
    }

    public boolean hasCompanionFamily() {

        return companionPresent()
                || robWealthPresent();
    }

    public boolean companionAndRobWealthCoexist() {

        return companionPresent()
                && robWealthPresent();
    }

    public boolean wealthPresent() {

        return wealthFamilyPower > 0;
    }

    public double familySharePercent() {

        return familyShareBasisPoints
                / 100.0;
    }

    /*
     * =================================================
     * Pattern 조회
     * =================================================
     */

    public List<CompanionCompositeSignal> signalsOf(
            CompanionCompositePatternType patternType
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

    public Optional<CompanionCompositeSignal> signalOf(
            CompanionCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .findFirst();
    }

    public boolean hasPattern(
            CompanionCompositePatternType patternType
    ) {

        return !signalsOf(
                patternType
        )
                .isEmpty();
    }

    public boolean hasMeaningfulPattern(
            CompanionCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        CompanionCompositeSignal
                                ::meaningful
                );
    }

    public boolean hasStrongPattern(
            CompanionCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        CompanionCompositeSignal
                                ::strong
                );
    }

    public List<CompanionCompositeSignal> meaningfulSignals() {

        return signals
                .stream()
                .filter(
                        CompanionCompositeSignal
                                ::meaningful
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        (
                                                CompanionCompositeSignal signal
                                        ) ->
                                                signal.level()
                                                        .rank()
                                )
                                .reversed()
                )
                .toList();
    }

    public List<CompanionCompositeSignal> strongSignals() {

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.level()
                                        == CompanionCompositePatternLevel.STRONG
                )
                .toList();
    }

    public boolean supportiveCompanion() {

        return hasMeaningfulPattern(
                CompanionCompositePatternType
                        .SUPPORTIVE_COMPANION
        );
    }

    public boolean companionCluster() {

        return hasMeaningfulPattern(
                CompanionCompositePatternType
                        .COMPANION_CLUSTER
        );
    }

    public boolean robWealthCompetition() {

        return hasMeaningfulPattern(
                CompanionCompositePatternType
                        .ROB_WEALTH_COMPETITION
        );
    }

    public boolean wealthContest() {

        return hasMeaningfulPattern(
                CompanionCompositePatternType
                        .WEALTH_CONTEST
        );
    }

    public boolean excessiveCompanion() {

        return hasMeaningfulPattern(
                CompanionCompositePatternType
                        .EXCESSIVE_COMPANION
        );
    }

    public boolean mixedCompanionRobWealth() {

        return hasMeaningfulPattern(
                CompanionCompositePatternType
                        .MIXED_COMPANION_ROB_WEALTH
        );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}