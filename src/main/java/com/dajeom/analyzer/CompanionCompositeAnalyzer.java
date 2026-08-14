package com.dajeom.analyzer;

import com.dajeom.model.CompanionCompositeAnalysis;
import com.dajeom.model.CompanionCompositeSignal;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.rule.CompanionCompositeRule;
import com.dajeom.type.CompanionCompositePatternLevel;
import com.dajeom.type.CompanionCompositePatternType;
import com.dajeom.type.CompanionCompositeReasonType;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-2.
 *
 * 비견·겁재 복합구조 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * 10-1 TenGodDistributionAnalysis
 *
 * +
 *
 * 4차 DayMasterStrengthAnalysis
 *
 * =================================================
 *
 * 분석:
 *
 * - 신약 보조형 비겁
 * - 비겁 군집
 * - 겁재 경쟁
 * - 재성 쟁탈
 * - 신강 비겁 과다
 * - 비견·겁재 혼재
 *
 * =================================================
 *
 * 여러 Pattern은 동시에 존재할 수 있다.
 */
@Component
public class CompanionCompositeAnalyzer {

    private final CompanionCompositeRule
            companionCompositeRule;

    public CompanionCompositeAnalyzer(
            CompanionCompositeRule companionCompositeRule
    ) {

        this.companionCompositeRule =
                Objects.requireNonNull(
                        companionCompositeRule,
                        "CompanionCompositeRule이 필요합니다."
                );
    }

    public CompanionCompositeAnalysis analyze(

            TenGodDistributionAnalysis
                    tenGodDistributionAnalysis,

            DayMasterStrengthAnalysis
                    dayMasterStrengthAnalysis

    ) {

        Objects.requireNonNull(
                tenGodDistributionAnalysis,
                "10-1 TenGodDistributionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterStrengthAnalysis,
                "4차 DayMasterStrengthAnalysis가 필요합니다."
        );

        validateIdentity(
                tenGodDistributionAnalysis,
                dayMasterStrengthAnalysis
        );

        TenGodPowerSummary companionSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.COMPANION
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 비견 Summary가 없습니다."
                                        )
                        );

        TenGodPowerSummary robWealthSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.ROB_WEALTH
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 겁재 Summary가 없습니다."
                                        )
                        );

        int companionFamilyPower =
                companionSummary.allocatedPower()
                        + robWealthSummary.allocatedPower();

        int wealthFamilyPower =
                tenGodDistributionAnalysis
                        .wealthFamilyPower();

        int familyShareBasisPoints =
                calculateFamilyShareBasisPoints(
                        companionFamilyPower,
                        tenGodDistributionAnalysis
                                .totalExternalTenGodPower()
                );

        DayMasterStrengthLevel strengthLevel =
                dayMasterStrengthAnalysis
                        .level();

        int totalOccurrenceCount =
                companionSummary.occurrenceCount()
                        + robWealthSummary.occurrenceCount();

        List<CompanionCompositeSignal> signals =
                new ArrayList<>();

        /*
         * =================================================
         * 1. 신약 보조형 비겁
         * =================================================
         */
        addSignalIfPresent(
                signals,
                CompanionCompositePatternType
                        .SUPPORTIVE_COMPANION,

                companionCompositeRule
                        .determineSupportiveLevel(
                                strengthLevel,
                                familyShareBasisPoints,
                                totalOccurrenceCount
                        ),

                companionSummary,
                robWealthSummary,
                strengthLevel,
                companionFamilyPower,
                wealthFamilyPower,
                familyShareBasisPoints
        );

        /*
         * =================================================
         * 2. 비겁 군집
         * =================================================
         */
        addSignalIfPresent(
                signals,
                CompanionCompositePatternType
                        .COMPANION_CLUSTER,

                companionCompositeRule
                        .determineClusterLevel(
                                familyShareBasisPoints,
                                totalOccurrenceCount,
                                companionSummary.visible(),
                                robWealthSummary.visible()
                        ),

                companionSummary,
                robWealthSummary,
                strengthLevel,
                companionFamilyPower,
                wealthFamilyPower,
                familyShareBasisPoints
        );

        /*
         * =================================================
         * 3. 겁재 경쟁 구조
         * =================================================
         */
        addSignalIfPresent(
                signals,
                CompanionCompositePatternType
                        .ROB_WEALTH_COMPETITION,

                companionCompositeRule
                        .determineRobWealthCompetitionLevel(
                                robWealthSummary,
                                familyShareBasisPoints
                        ),

                companionSummary,
                robWealthSummary,
                strengthLevel,
                companionFamilyPower,
                wealthFamilyPower,
                familyShareBasisPoints
        );

        /*
         * =================================================
         * 4. 재성 쟁탈 구조
         * =================================================
         */
        addSignalIfPresent(
                signals,
                CompanionCompositePatternType
                        .WEALTH_CONTEST,

                companionCompositeRule
                        .determineWealthContestLevel(
                                companionFamilyPower,
                                wealthFamilyPower,
                                robWealthSummary
                        ),

                companionSummary,
                robWealthSummary,
                strengthLevel,
                companionFamilyPower,
                wealthFamilyPower,
                familyShareBasisPoints
        );

        /*
         * =================================================
         * 5. 신강 비겁 과다
         * =================================================
         */
        addSignalIfPresent(
                signals,
                CompanionCompositePatternType
                        .EXCESSIVE_COMPANION,

                companionCompositeRule
                        .determineExcessiveLevel(
                                strengthLevel,
                                familyShareBasisPoints,
                                totalOccurrenceCount
                        ),

                companionSummary,
                robWealthSummary,
                strengthLevel,
                companionFamilyPower,
                wealthFamilyPower,
                familyShareBasisPoints
        );

        /*
         * =================================================
         * 6. 비견·겁재 혼재
         * =================================================
         */
        addSignalIfPresent(
                signals,
                CompanionCompositePatternType
                        .MIXED_COMPANION_ROB_WEALTH,

                companionCompositeRule
                        .determineMixedLevel(
                                companionSummary,
                                robWealthSummary
                        ),

                companionSummary,
                robWealthSummary,
                strengthLevel,
                companionFamilyPower,
                wealthFamilyPower,
                familyShareBasisPoints
        );

        /*
         * =================================================
         * 정렬
         *
         * 강한 Pattern 우선.
         *
         * 길흉 순위가 아니다.
         * =================================================
         */
        signals.sort(
                Comparator
                        .comparingInt(
                                (
                                        CompanionCompositeSignal signal
                                ) ->
                                        signal.level()
                                                .rank()
                        )
                        .reversed()

                        .thenComparingInt(
                                signal ->
                                        signal
                                                .patternType()
                                                .ordinal()
                        )
        );

        return new CompanionCompositeAnalysis(
                tenGodDistributionAnalysis
                        .dayMaster(),
                tenGodDistributionAnalysis,
                dayMasterStrengthAnalysis,
                companionSummary,
                robWealthSummary,
                companionFamilyPower,
                wealthFamilyPower,
                familyShareBasisPoints,
                signals,
                tenGodDistributionAnalysis
                        .completeChart()
        );
    }

    /*
     * =================================================
     * Signal 생성
     * =================================================
     */

    private void addSignalIfPresent(

            List<CompanionCompositeSignal> signals,

            CompanionCompositePatternType patternType,

            CompanionCompositePatternLevel level,

            TenGodPowerSummary companionSummary,

            TenGodPowerSummary robWealthSummary,

            DayMasterStrengthLevel strengthLevel,

            int companionFamilyPower,

            int wealthFamilyPower,

            int familyShareBasisPoints

    ) {

        if (
                level == null
        ) {

            return;
        }

        List<CompanionCompositeReasonType> reasons =
                companionCompositeRule
                        .buildReasons(
                                patternType,
                                companionSummary,
                                robWealthSummary,
                                strengthLevel,
                                familyShareBasisPoints,
                                companionFamilyPower,
                                wealthFamilyPower
                        );

        signals.add(
                new CompanionCompositeSignal(
                        patternType,
                        level,
                        companionSummary.allocatedPower(),
                        robWealthSummary.allocatedPower(),
                        companionFamilyPower,
                        wealthFamilyPower,
                        familyShareBasisPoints,
                        companionSummary.occurrenceCount(),
                        robWealthSummary.occurrenceCount(),
                        companionSummary.visible(),
                        robWealthSummary.visible(),
                        companionSummary.monthPresent(),
                        robWealthSummary.monthPresent(),
                        reasons
                )
        );
    }

    /*
     * =================================================
     * 비겁 전체 비중
     * =================================================
     */

    private int calculateFamilyShareBasisPoints(

            int companionFamilyPower,

            int totalExternalTenGodPower

    ) {

        if (
                companionFamilyPower <= 0
                        || totalExternalTenGodPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                10_000,
                (int) Math.round(
                        companionFamilyPower
                                * 10_000.0
                                / totalExternalTenGodPower
                )
        );
    }

    /*
     * =================================================
     * 동일 원국 검증
     * =================================================
     */

    private void validateIdentity(

            TenGodDistributionAnalysis
                    tenGodDistributionAnalysis,

            DayMasterStrengthAnalysis
                    dayMasterStrengthAnalysis

    ) {

        if (
                tenGodDistributionAnalysis
                        .dayMaster()

                        != dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-1 십성 분석과 4차 신강·신약 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodDistributionAnalysis
                        .completeChart()

                        != dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "10-1 십성 분석과 4차 신강·신약 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }
}