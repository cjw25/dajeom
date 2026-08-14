package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodCorePatternLevel;
import com.dajeom.type.TenGodCorePatternType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-8.
 *
 * 핵심 십성 복합 Pattern 분석.
 *
 * =================================================
 *
 * 10-7 결과를 기반으로
 * 개별 십성까지 내려가 Pattern을 판정한다.
 *
 * =================================================
 *
 * 이 단계에서 Pattern의 구조적 성립은 판단하지만
 * 격국·용신·합화 기준의 최종 성패는
 * 10-9에서 검토한다.
 */
public record TenGodCorePatternAnalysis(

        HeavenlyStem dayMaster,

        TenGodFamilyChainAnalysis familyChainAnalysis,

        List<TenGodCorePatternSignal> signals,

        boolean completeChart

) {

    public TenGodCorePatternAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                familyChainAnalysis,
                "10-7 Family Chain 분석이 필요합니다."
        );

        signals =
                signals == null
                        ? List.of()
                        : List.copyOf(
                        signals
                );

        if (
                familyChainAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "10-7과 10-8 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                familyChainAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "10-7과 10-8 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        for (
                int first = 0;
                first < signals.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < signals.size();
                    second++
            ) {

                if (
                        signals.get(first).patternType()
                                == signals.get(second).patternType()
                ) {

                    throw new IllegalArgumentException(
                            "동일 핵심 십성 Pattern이 중복되었습니다: "
                                    + signals.get(first).patternType()
                    );
                }
            }
        }
    }

    public TenGodDistributionAnalysis
    tenGodDistributionAnalysis() {

        return familyChainAnalysis
                .tenGodDistributionAnalysis();
    }

    public DayMasterStrengthAnalysis
    dayMasterStrengthAnalysis() {

        return familyChainAnalysis
                .wealthCompositeAnalysis()
                .dayMasterStrengthAnalysis();
    }

    public List<TenGodCorePatternSignal> signalsOfLevel(
            TenGodCorePatternLevel level
    ) {

        if (
                level == null
        ) {

            return List.of();
        }

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.level()
                                        == level
                )
                .toList();
    }

    public Optional<TenGodCorePatternSignal> signalOf(
            TenGodCorePatternType patternType
    ) {

        if (
                patternType == null
        ) {

            return Optional.empty();
        }

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.patternType()
                                        == patternType
                )
                .findFirst();
    }

    public boolean hasPattern(
            TenGodCorePatternType patternType
    ) {

        return signalOf(
                patternType
        )
                .isPresent();
    }

    public boolean hasMeaningfulPattern(
            TenGodCorePatternType patternType
    ) {

        return signalOf(
                patternType
        )
                .map(
                        TenGodCorePatternSignal
                                ::meaningful
                )
                .orElse(false);
    }

    public boolean hasStrongPattern(
            TenGodCorePatternType patternType
    ) {

        return signalOf(
                patternType
        )
                .map(
                        TenGodCorePatternSignal
                                ::strong
                )
                .orElse(false);
    }

    public List<TenGodCorePatternSignal>
    meaningfulSignals() {

        return signals
                .stream()
                .filter(
                        TenGodCorePatternSignal
                                ::meaningful
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        (
                                                TenGodCorePatternSignal signal
                                        ) ->
                                                signal.level()
                                                        .rank()
                                )
                                .reversed()
                )
                .toList();
    }

    public List<TenGodCorePatternSignal>
    strongSignals() {

        return signals
                .stream()
                .filter(
                        TenGodCorePatternSignal
                                ::strong
                )
                .toList();
    }

    public List<TenGodCorePatternSignal>
    patternsInvolving(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return List.of();
        }

        return signals
                .stream()
                .filter(
                        signal ->
                                signal.involves(
                                        tenGodType
                                )
                )
                .toList();
    }

    /*
     * =================================================
     * Shortcut
     * =================================================
     */

    public boolean eatingGodControlsSevenKillings() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .EATING_GOD_CONTROLS_SEVEN_KILLINGS
        );
    }

    public boolean hurtingOfficerMeetsDirectOfficer() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .HURTING_OFFICER_MEETS_DIRECT_OFFICER
        );
    }

    public boolean hurtingOfficerWithResource() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .HURTING_OFFICER_WITH_RESOURCE
        );
    }

    public boolean indirectResourceSeizesEatingGod() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .INDIRECT_RESOURCE_SEIZES_EATING_GOD
        );
    }

    public boolean wealthGeneratesDirectOfficer() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .WEALTH_GENERATES_DIRECT_OFFICER
        );
    }

    public boolean directOfficerGeneratesResource() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .DIRECT_OFFICER_GENERATES_RESOURCE
        );
    }

    public boolean sevenKillingsGeneratesResource() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .SEVEN_KILLINGS_GENERATES_RESOURCE
        );
    }

    public boolean wealthControlsResource() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .WEALTH_CONTROLS_RESOURCE
        );
    }

    public boolean companionContestsWealth() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .COMPANION_CONTESTS_WEALTH
        );
    }

    public boolean wealthHeavyWeakDayMaster() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .WEALTH_HEAVY_WEAK_DAY_MASTER
        );
    }

    public boolean mixedOfficerKillings() {

        return hasMeaningfulPattern(
                TenGodCorePatternType
                        .MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS
        );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}