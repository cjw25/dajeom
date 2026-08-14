package com.dajeom.model;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.ResourceCompositePatternLevel;
import com.dajeom.type.ResourceCompositePatternType;
import com.dajeom.type.StructureTenGodType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-6.
 *
 * 정인·편인 복합구조 분석.
 *
 * =================================================
 *
 * 이번 단계에서는:
 *
 * - 인성 발달
 * - 정인 중심
 * - 편인 중심
 * - 정인·편인 혼재
 * - 신약 인성 보조
 * - 신강 인성 과다
 * - 식상 제약 Context
 * - 관살 생인 Context
 * - 재성 압박 Context
 *
 * 까지만 다룬다.
 */
public record ResourceCompositeAnalysis(

        HeavenlyStem dayMaster,

        TenGodDistributionAnalysis tenGodDistributionAnalysis,

        DayMasterStrengthAnalysis dayMasterStrengthAnalysis,

        TenGodPowerSummary directResourceSummary,

        TenGodPowerSummary indirectResourceSummary,

        int resourceFamilyPower,

        int outputFamilyPower,

        int officerFamilyPower,

        int wealthFamilyPower,

        int resourceFamilyShareBasisPoints,

        List<ResourceCompositeSignal> signals,

        boolean completeChart

) {

    public ResourceCompositeAnalysis {

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
                directResourceSummary,
                "정인 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectResourceSummary,
                "편인 Summary가 필요합니다."
        );

        if (
                directResourceSummary.tenGodType()
                        != StructureTenGodType.DIRECT_RESOURCE
        ) {

            throw new IllegalArgumentException(
                    "directResourceSummary는 정인 Summary여야 합니다."
            );
        }

        if (
                indirectResourceSummary.tenGodType()
                        != StructureTenGodType.INDIRECT_RESOURCE
        ) {

            throw new IllegalArgumentException(
                    "indirectResourceSummary는 편인 Summary여야 합니다."
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
                resourceFamilyPower
                        != directResourceSummary.allocatedPower()
                        + indirectResourceSummary.allocatedPower()
        ) {

            throw new IllegalArgumentException(
                    "인성 전체 세력이 정인+편인 Summary와 일치하지 않습니다."
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
                outputFamilyPower
                        != tenGodDistributionAnalysis
                        .outputFamilyPower()
        ) {

            throw new IllegalArgumentException(
                    "식상 전체 세력이 10-1 분석과 일치하지 않습니다."
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
                wealthFamilyPower
                        != tenGodDistributionAnalysis
                        .wealthFamilyPower()
        ) {

            throw new IllegalArgumentException(
                    "재성 전체 세력이 10-1 분석과 일치하지 않습니다."
            );
        }

        if (
                resourceFamilyShareBasisPoints < 0
                        || resourceFamilyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "인성 점유율은 0~10000bp 범위여야 합니다."
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

    public int directResourcePower() {

        return directResourceSummary
                .allocatedPower();
    }

    public int indirectResourcePower() {

        return indirectResourceSummary
                .allocatedPower();
    }

    public int directResourceOccurrenceCount() {

        return directResourceSummary
                .occurrenceCount();
    }

    public int indirectResourceOccurrenceCount() {

        return indirectResourceSummary
                .occurrenceCount();
    }

    public int totalResourceOccurrenceCount() {

        return directResourceOccurrenceCount()
                + indirectResourceOccurrenceCount();
    }

    public boolean directResourcePresent() {

        return directResourceSummary
                .present();
    }

    public boolean indirectResourcePresent() {

        return indirectResourceSummary
                .present();
    }

    public boolean directResourceVisible() {

        return directResourceSummary
                .visible();
    }

    public boolean indirectResourceVisible() {

        return indirectResourceSummary
                .visible();
    }

    public boolean directResourceMonthPresent() {

        return directResourceSummary
                .monthPresent();
    }

    public boolean indirectResourceMonthPresent() {

        return indirectResourceSummary
                .monthPresent();
    }

    public boolean resourcePresent() {

        return directResourcePresent()
                || indirectResourcePresent();
    }

    public boolean directAndIndirectResourceCoexist() {

        return directResourcePresent()
                && indirectResourcePresent();
    }

    public boolean outputPresent() {

        return outputFamilyPower > 0;
    }

    public boolean officerPresent() {

        return officerFamilyPower > 0;
    }

    public boolean wealthPresent() {

        return wealthFamilyPower > 0;
    }

    public double resourceFamilySharePercent() {

        return resourceFamilyShareBasisPoints
                / 100.0;
    }

    /*
     * =================================================
     * Pattern 조회
     * =================================================
     */

    public List<ResourceCompositeSignal> signalsOf(
            ResourceCompositePatternType patternType
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

    public Optional<ResourceCompositeSignal> signalOf(
            ResourceCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .findFirst();
    }

    public boolean hasPattern(
            ResourceCompositePatternType patternType
    ) {

        return !signalsOf(
                patternType
        )
                .isEmpty();
    }

    public boolean hasMeaningfulPattern(
            ResourceCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        ResourceCompositeSignal
                                ::meaningful
                );
    }

    public boolean hasStrongPattern(
            ResourceCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        ResourceCompositeSignal
                                ::strong
                );
    }

    public List<ResourceCompositeSignal> meaningfulSignals() {

        return signals
                .stream()
                .filter(
                        ResourceCompositeSignal
                                ::meaningful
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        (
                                                ResourceCompositeSignal signal
                                        ) ->
                                                signal.level()
                                                        .rank()
                                )
                                .reversed()
                )
                .toList();
    }

    public List<ResourceCompositeSignal> strongSignals() {

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.level()
                                        == ResourceCompositePatternLevel.STRONG
                )
                .toList();
    }

    /*
     * =================================================
     * Shortcut
     * =================================================
     */

    public boolean resourceDeveloped() {

        return hasMeaningfulPattern(
                ResourceCompositePatternType
                        .RESOURCE_DEVELOPED
        );
    }

    public boolean directResourceCentered() {

        return hasMeaningfulPattern(
                ResourceCompositePatternType
                        .DIRECT_RESOURCE_CENTERED
        );
    }

    public boolean indirectResourceCentered() {

        return hasMeaningfulPattern(
                ResourceCompositePatternType
                        .INDIRECT_RESOURCE_CENTERED
        );
    }

    public boolean mixedResource() {

        return hasMeaningfulPattern(
                ResourceCompositePatternType
                        .MIXED_RESOURCE
        );
    }

    public boolean weakDayMasterResourceSupport() {

        return hasMeaningfulPattern(
                ResourceCompositePatternType
                        .WEAK_DAY_MASTER_RESOURCE_SUPPORT
        );
    }

    public boolean strongDayMasterExcessiveResource() {

        return hasMeaningfulPattern(
                ResourceCompositePatternType
                        .STRONG_DAY_MASTER_EXCESSIVE_RESOURCE
        );
    }

    public boolean outputConstraintContext() {

        return hasMeaningfulPattern(
                ResourceCompositePatternType
                        .OUTPUT_CONSTRAINT_CONTEXT
        );
    }

    public boolean officerGenerationContext() {

        return hasMeaningfulPattern(
                ResourceCompositePatternType
                        .OFFICER_GENERATION_CONTEXT
        );
    }

    public boolean wealthPressureContext() {

        return hasMeaningfulPattern(
                ResourceCompositePatternType
                        .WEALTH_PRESSURE_CONTEXT
        );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}