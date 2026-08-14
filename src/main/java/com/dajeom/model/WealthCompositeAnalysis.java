package com.dajeom.model;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.WealthCompositePatternLevel;
import com.dajeom.type.WealthCompositePatternType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-4.
 *
 * 편재·정재 복합구조 최종 분석.
 *
 * =================================================
 *
 * 여기에서는 재성 family 자체의 구조를 분석한다.
 *
 * 다음 구조는 아직 확정하지 않는다.
 *
 * - 재다신약
 * - 비겁쟁재
 * - 재생관
 * - 재극인
 *
 * 해당 구조는 10-7~10-9에서
 * 다른 Analysis와 교차검증한다.
 */
public record WealthCompositeAnalysis(

        HeavenlyStem dayMaster,

        TenGodDistributionAnalysis tenGodDistributionAnalysis,

        DayMasterStrengthAnalysis dayMasterStrengthAnalysis,

        TenGodPowerSummary directWealthSummary,

        TenGodPowerSummary indirectWealthSummary,

        int wealthFamilyPower,

        int companionFamilyPower,

        int officerFamilyPower,

        int resourceFamilyPower,

        int wealthFamilyShareBasisPoints,

        List<WealthCompositeSignal> signals,

        boolean completeChart

) {

    public WealthCompositeAnalysis {

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
                directWealthSummary,
                "정재 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectWealthSummary,
                "편재 Summary가 필요합니다."
        );

        if (
                directWealthSummary.tenGodType()
                        != StructureTenGodType.DIRECT_WEALTH
        ) {

            throw new IllegalArgumentException(
                    "directWealthSummary는 정재 Summary여야 합니다."
            );
        }

        if (
                indirectWealthSummary.tenGodType()
                        != StructureTenGodType.INDIRECT_WEALTH
        ) {

            throw new IllegalArgumentException(
                    "indirectWealthSummary는 편재 Summary여야 합니다."
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
                wealthFamilyPower
                        != directWealthSummary.allocatedPower()
                        + indirectWealthSummary.allocatedPower()
        ) {

            throw new IllegalArgumentException(
                    "재성 전체 세력이 정재+편재 Summary와 일치하지 않습니다."
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
                companionFamilyPower
                        != tenGodDistributionAnalysis
                        .companionFamilyPower()
        ) {

            throw new IllegalArgumentException(
                    "비겁 전체 세력이 10-1 분석과 일치하지 않습니다."
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
                resourceFamilyPower
                        != tenGodDistributionAnalysis
                        .resourceFamilyPower()
        ) {

            throw new IllegalArgumentException(
                    "인성 전체 세력이 10-1 분석과 일치하지 않습니다."
            );
        }

        if (
                wealthFamilyShareBasisPoints < 0
                        || wealthFamilyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "재성 점유율은 0~10000bp 범위여야 합니다."
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

    public int directWealthPower() {

        return directWealthSummary
                .allocatedPower();
    }

    public int indirectWealthPower() {

        return indirectWealthSummary
                .allocatedPower();
    }

    public int directWealthOccurrenceCount() {

        return directWealthSummary
                .occurrenceCount();
    }

    public int indirectWealthOccurrenceCount() {

        return indirectWealthSummary
                .occurrenceCount();
    }

    public int totalWealthOccurrenceCount() {

        return directWealthOccurrenceCount()
                + indirectWealthOccurrenceCount();
    }

    public boolean directWealthPresent() {

        return directWealthSummary
                .present();
    }

    public boolean indirectWealthPresent() {

        return indirectWealthSummary
                .present();
    }

    public boolean directWealthVisible() {

        return directWealthSummary
                .visible();
    }

    public boolean indirectWealthVisible() {

        return indirectWealthSummary
                .visible();
    }

    public boolean directWealthMonthPresent() {

        return directWealthSummary
                .monthPresent();
    }

    public boolean indirectWealthMonthPresent() {

        return indirectWealthSummary
                .monthPresent();
    }

    public boolean wealthPresent() {

        return directWealthPresent()
                || indirectWealthPresent();
    }

    public boolean directAndIndirectWealthCoexist() {

        return directWealthPresent()
                && indirectWealthPresent();
    }

    public boolean companionPresent() {

        return companionFamilyPower > 0;
    }

    public boolean officerPresent() {

        return officerFamilyPower > 0;
    }

    public boolean resourcePresent() {

        return resourceFamilyPower > 0;
    }

    public double wealthFamilySharePercent() {

        return wealthFamilyShareBasisPoints
                / 100.0;
    }

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    public List<WealthCompositeSignal> signalsOf(
            WealthCompositePatternType patternType
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

    public Optional<WealthCompositeSignal> signalOf(
            WealthCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .findFirst();
    }

    public boolean hasPattern(
            WealthCompositePatternType patternType
    ) {

        return !signalsOf(
                patternType
        )
                .isEmpty();
    }

    public boolean hasMeaningfulPattern(
            WealthCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        WealthCompositeSignal
                                ::meaningful
                );
    }

    public boolean hasStrongPattern(
            WealthCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        WealthCompositeSignal
                                ::strong
                );
    }

    public List<WealthCompositeSignal> meaningfulSignals() {

        return signals
                .stream()
                .filter(
                        WealthCompositeSignal
                                ::meaningful
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        (
                                                WealthCompositeSignal signal
                                        ) ->
                                                signal.level()
                                                        .rank()
                                )
                                .reversed()
                )
                .toList();
    }

    public List<WealthCompositeSignal> strongSignals() {

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.level()
                                        == WealthCompositePatternLevel.STRONG
                )
                .toList();
    }

    /*
     * =================================================
     * 주요 Pattern shortcut
     * =================================================
     */

    public boolean wealthDeveloped() {

        return hasMeaningfulPattern(
                WealthCompositePatternType
                        .WEALTH_DEVELOPED
        );
    }

    public boolean directWealthCentered() {

        return hasMeaningfulPattern(
                WealthCompositePatternType
                        .DIRECT_WEALTH_CENTERED
        );
    }

    public boolean indirectWealthCentered() {

        return hasMeaningfulPattern(
                WealthCompositePatternType
                        .INDIRECT_WEALTH_CENTERED
        );
    }

    public boolean mixedWealth() {

        return hasMeaningfulPattern(
                WealthCompositePatternType
                        .MIXED_WEALTH
        );
    }

    public boolean weakDayMasterWealthBurden() {

        return hasMeaningfulPattern(
                WealthCompositePatternType
                        .WEAK_DAY_MASTER_WEALTH_BURDEN
        );
    }

    public boolean companionPressuredWealth() {

        return hasMeaningfulPattern(
                WealthCompositePatternType
                        .COMPANION_PRESSURED_WEALTH
        );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}