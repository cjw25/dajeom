package com.dajeom.model;

import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.OutputCompositePatternLevel;
import com.dajeom.type.OutputCompositePatternType;
import com.dajeom.type.StructureTenGodType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-3.
 *
 * 식신·상관 복합구조 최종 분석.
 *
 * =================================================
 *
 * 이 단계는 특수 십성 Pattern을
 * 최종 확정하지 않는다.
 *
 * 예:
 *
 * 상관견관
 * 식신제살
 * 효신탈식
 * 상관패인
 *
 * 은 10-8에서 교차 분석한다.
 */
public record OutputCompositeAnalysis(

        HeavenlyStem dayMaster,

        TenGodDistributionAnalysis tenGodDistributionAnalysis,

        DayMasterStrengthAnalysis dayMasterStrengthAnalysis,

        TenGodPowerSummary eatingGodSummary,

        TenGodPowerSummary hurtingOfficerSummary,

        int outputFamilyPower,

        int resourceFamilyPower,

        int officerFamilyPower,

        int outputFamilyShareBasisPoints,

        List<OutputCompositeSignal> signals,

        boolean completeChart

) {

    public OutputCompositeAnalysis {

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
                eatingGodSummary,
                "식신 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                hurtingOfficerSummary,
                "상관 Summary가 필요합니다."
        );

        if (
                eatingGodSummary.tenGodType()
                        != StructureTenGodType.EATING_GOD
        ) {

            throw new IllegalArgumentException(
                    "eatingGodSummary는 식신 Summary여야 합니다."
            );
        }

        if (
                hurtingOfficerSummary.tenGodType()
                        != StructureTenGodType.HURTING_OFFICER
        ) {

            throw new IllegalArgumentException(
                    "hurtingOfficerSummary는 상관 Summary여야 합니다."
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
                outputFamilyPower
                        != eatingGodSummary.allocatedPower()
                        + hurtingOfficerSummary.allocatedPower()
        ) {

            throw new IllegalArgumentException(
                    "식상 전체 세력이 식신+상관 Summary와 일치하지 않습니다."
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
                officerFamilyPower
                        != tenGodDistributionAnalysis
                        .officerFamilyPower()
        ) {

            throw new IllegalArgumentException(
                    "관살 전체 세력이 10-1 분석과 일치하지 않습니다."
            );
        }

        if (
                outputFamilyShareBasisPoints < 0
                        || outputFamilyShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "식상 점유율은 0~10000bp 범위여야 합니다."
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

    public int eatingGodPower() {

        return eatingGodSummary
                .allocatedPower();
    }

    public int hurtingOfficerPower() {

        return hurtingOfficerSummary
                .allocatedPower();
    }

    public int eatingGodOccurrenceCount() {

        return eatingGodSummary
                .occurrenceCount();
    }

    public int hurtingOfficerOccurrenceCount() {

        return hurtingOfficerSummary
                .occurrenceCount();
    }

    public int totalOutputOccurrenceCount() {

        return eatingGodOccurrenceCount()
                + hurtingOfficerOccurrenceCount();
    }

    public boolean eatingGodPresent() {

        return eatingGodSummary
                .present();
    }

    public boolean hurtingOfficerPresent() {

        return hurtingOfficerSummary
                .present();
    }

    public boolean eatingGodVisible() {

        return eatingGodSummary
                .visible();
    }

    public boolean hurtingOfficerVisible() {

        return hurtingOfficerSummary
                .visible();
    }

    public boolean eatingGodMonthPresent() {

        return eatingGodSummary
                .monthPresent();
    }

    public boolean hurtingOfficerMonthPresent() {

        return hurtingOfficerSummary
                .monthPresent();
    }

    public boolean outputPresent() {

        return eatingGodPresent()
                || hurtingOfficerPresent();
    }

    public boolean eatingGodAndHurtingOfficerCoexist() {

        return eatingGodPresent()
                && hurtingOfficerPresent();
    }

    public boolean resourcePresent() {

        return resourceFamilyPower > 0;
    }

    public boolean officerPresent() {

        return officerFamilyPower > 0;
    }

    public double outputFamilySharePercent() {

        return outputFamilyShareBasisPoints
                / 100.0;
    }

    /*
     * =================================================
     * Pattern 조회
     * =================================================
     */

    public List<OutputCompositeSignal> signalsOf(
            OutputCompositePatternType patternType
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

    public Optional<OutputCompositeSignal> signalOf(
            OutputCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .findFirst();
    }

    public boolean hasPattern(
            OutputCompositePatternType patternType
    ) {

        return !signalsOf(
                patternType
        )
                .isEmpty();
    }

    public boolean hasMeaningfulPattern(
            OutputCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        OutputCompositeSignal
                                ::meaningful
                );
    }

    public boolean hasStrongPattern(
            OutputCompositePatternType patternType
    ) {

        return signalsOf(
                patternType
        )
                .stream()
                .anyMatch(
                        OutputCompositeSignal
                                ::strong
                );
    }

    public List<OutputCompositeSignal> meaningfulSignals() {

        return signals
                .stream()
                .filter(
                        OutputCompositeSignal
                                ::meaningful
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        (
                                                OutputCompositeSignal signal
                                        ) ->
                                                signal.level()
                                                        .rank()
                                )
                                .reversed()
                )
                .toList();
    }

    public List<OutputCompositeSignal> strongSignals() {

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.level()
                                        == OutputCompositePatternLevel.STRONG
                )
                .toList();
    }

    /*
     * =================================================
     * 주요 Pattern
     * =================================================
     */

    public boolean outputDeveloped() {

        return hasMeaningfulPattern(
                OutputCompositePatternType
                        .OUTPUT_DEVELOPED
        );
    }

    public boolean eatingGodCentered() {

        return hasMeaningfulPattern(
                OutputCompositePatternType
                        .EATING_GOD_CENTERED
        );
    }

    public boolean hurtingOfficerCentered() {

        return hasMeaningfulPattern(
                OutputCompositePatternType
                        .HURTING_OFFICER_CENTERED
        );
    }

    public boolean mixedOutput() {

        return hasMeaningfulPattern(
                OutputCompositePatternType
                        .MIXED_OUTPUT
        );
    }

    public boolean weakDayMasterExcessiveDrain() {

        return hasMeaningfulPattern(
                OutputCompositePatternType
                        .WEAK_DAY_MASTER_EXCESSIVE_DRAIN
        );
    }

    public boolean resourceConstrainedOutput() {

        return hasMeaningfulPattern(
                OutputCompositePatternType
                        .RESOURCE_CONSTRAINED_OUTPUT
        );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}