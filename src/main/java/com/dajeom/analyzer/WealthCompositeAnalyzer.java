package com.dajeom.analyzer;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.model.WealthCompositeAnalysis;
import com.dajeom.model.WealthCompositeSignal;
import com.dajeom.rule.WealthCompositeRule;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.WealthCompositePatternLevel;
import com.dajeom.type.WealthCompositePatternType;
import com.dajeom.type.WealthCompositeReasonType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-4.
 *
 * 정재·편재 복합구조 Analyzer.
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
 * - 재성 발달
 * - 정재 중심
 * - 편재 중심
 * - 정재·편재 혼재
 * - 신약 재성 부담
 * - 비겁 압박 재성
 *
 * =================================================
 *
 * 주의:
 *
 * 재다신약
 * 비겁쟁재
 * 재생관
 * 재극인
 *
 * 은 여기에서 최종 확정하지 않는다.
 */
@Component
public class WealthCompositeAnalyzer {

    private final WealthCompositeRule
            wealthCompositeRule;

    public WealthCompositeAnalyzer(
            WealthCompositeRule wealthCompositeRule
    ) {

        this.wealthCompositeRule =
                Objects.requireNonNull(
                        wealthCompositeRule,
                        "WealthCompositeRule이 필요합니다."
                );
    }

    public WealthCompositeAnalysis analyze(

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

        /*
         * =================================================
         * 정재
         * =================================================
         */

        TenGodPowerSummary directWealthSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.DIRECT_WEALTH
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 정재 Summary가 없습니다."
                                        )
                        );

        /*
         * =================================================
         * 편재
         * =================================================
         */

        TenGodPowerSummary indirectWealthSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.INDIRECT_WEALTH
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 편재 Summary가 없습니다."
                                        )
                        );

        /*
         * =================================================
         * Family Power
         * =================================================
         */

        int wealthFamilyPower =
                directWealthSummary.allocatedPower()
                        + indirectWealthSummary.allocatedPower();

        int companionFamilyPower =
                tenGodDistributionAnalysis
                        .companionFamilyPower();

        int officerFamilyPower =
                tenGodDistributionAnalysis
                        .officerFamilyPower();

        int resourceFamilyPower =
                tenGodDistributionAnalysis
                        .resourceFamilyPower();

        int wealthFamilyShareBasisPoints =
                calculateFamilyShareBasisPoints(
                        wealthFamilyPower,
                        tenGodDistributionAnalysis
                                .totalExternalTenGodPower()
                );

        DayMasterStrengthLevel strengthLevel =
                dayMasterStrengthAnalysis
                        .level();

        int totalWealthOccurrenceCount =
                directWealthSummary.occurrenceCount()
                        + indirectWealthSummary.occurrenceCount();

        List<WealthCompositeSignal> signals =
                new ArrayList<>();

        /*
         * =================================================
         * 1. 재성 발달
         * =================================================
         */

        addSignalIfPresent(
                signals,

                WealthCompositePatternType
                        .WEALTH_DEVELOPED,

                wealthCompositeRule
                        .determineWealthDevelopedLevel(
                                wealthFamilyShareBasisPoints,
                                totalWealthOccurrenceCount,
                                directWealthSummary.visible(),
                                indirectWealthSummary.visible()
                        ),

                directWealthSummary,
                indirectWealthSummary,
                strengthLevel,
                wealthFamilyPower,
                companionFamilyPower,
                officerFamilyPower,
                resourceFamilyPower,
                wealthFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 2. 정재 중심
         * =================================================
         */

        addSignalIfPresent(
                signals,

                WealthCompositePatternType
                        .DIRECT_WEALTH_CENTERED,

                wealthCompositeRule
                        .determineDirectWealthCenteredLevel(
                                directWealthSummary,
                                indirectWealthSummary
                        ),

                directWealthSummary,
                indirectWealthSummary,
                strengthLevel,
                wealthFamilyPower,
                companionFamilyPower,
                officerFamilyPower,
                resourceFamilyPower,
                wealthFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 3. 편재 중심
         * =================================================
         */

        addSignalIfPresent(
                signals,

                WealthCompositePatternType
                        .INDIRECT_WEALTH_CENTERED,

                wealthCompositeRule
                        .determineIndirectWealthCenteredLevel(
                                directWealthSummary,
                                indirectWealthSummary
                        ),

                directWealthSummary,
                indirectWealthSummary,
                strengthLevel,
                wealthFamilyPower,
                companionFamilyPower,
                officerFamilyPower,
                resourceFamilyPower,
                wealthFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 4. 정재·편재 혼재
         * =================================================
         */

        addSignalIfPresent(
                signals,

                WealthCompositePatternType
                        .MIXED_WEALTH,

                wealthCompositeRule
                        .determineMixedWealthLevel(
                                directWealthSummary,
                                indirectWealthSummary
                        ),

                directWealthSummary,
                indirectWealthSummary,
                strengthLevel,
                wealthFamilyPower,
                companionFamilyPower,
                officerFamilyPower,
                resourceFamilyPower,
                wealthFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 5. 신약 재성 부담
         * =================================================
         */

        addSignalIfPresent(
                signals,

                WealthCompositePatternType
                        .WEAK_DAY_MASTER_WEALTH_BURDEN,

                wealthCompositeRule
                        .determineWeakDayMasterWealthBurdenLevel(
                                strengthLevel,
                                wealthFamilyShareBasisPoints,
                                totalWealthOccurrenceCount
                        ),

                directWealthSummary,
                indirectWealthSummary,
                strengthLevel,
                wealthFamilyPower,
                companionFamilyPower,
                officerFamilyPower,
                resourceFamilyPower,
                wealthFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 6. 비겁 압박 재성
         * =================================================
         */

        addSignalIfPresent(
                signals,

                WealthCompositePatternType
                        .COMPANION_PRESSURED_WEALTH,

                wealthCompositeRule
                        .determineCompanionPressuredWealthLevel(
                                wealthFamilyPower,
                                companionFamilyPower,
                                wealthFamilyShareBasisPoints
                        ),

                directWealthSummary,
                indirectWealthSummary,
                strengthLevel,
                wealthFamilyPower,
                companionFamilyPower,
                officerFamilyPower,
                resourceFamilyPower,
                wealthFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 정렬
         *
         * 구조 강도 순.
         *
         * 길흉 순위가 아니다.
         * =================================================
         */

        signals.sort(
                Comparator
                        .comparingInt(
                                (
                                        WealthCompositeSignal signal
                                ) ->
                                        signal
                                                .level()
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

        return new WealthCompositeAnalysis(
                tenGodDistributionAnalysis
                        .dayMaster(),
                tenGodDistributionAnalysis,
                dayMasterStrengthAnalysis,
                directWealthSummary,
                indirectWealthSummary,
                wealthFamilyPower,
                companionFamilyPower,
                officerFamilyPower,
                resourceFamilyPower,
                wealthFamilyShareBasisPoints,
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

            List<WealthCompositeSignal> signals,

            WealthCompositePatternType patternType,

            WealthCompositePatternLevel level,

            TenGodPowerSummary directWealthSummary,

            TenGodPowerSummary indirectWealthSummary,

            DayMasterStrengthLevel strengthLevel,

            int wealthFamilyPower,

            int companionFamilyPower,

            int officerFamilyPower,

            int resourceFamilyPower,

            int wealthFamilyShareBasisPoints

    ) {

        if (
                level == null
        ) {

            return;
        }

        List<WealthCompositeReasonType> reasons =
                wealthCompositeRule
                        .buildReasons(
                                patternType,
                                directWealthSummary,
                                indirectWealthSummary,
                                strengthLevel,
                                wealthFamilyShareBasisPoints,
                                wealthFamilyPower,
                                companionFamilyPower,
                                officerFamilyPower,
                                resourceFamilyPower
                        );

        signals.add(
                new WealthCompositeSignal(
                        patternType,
                        level,
                        directWealthSummary.allocatedPower(),
                        indirectWealthSummary.allocatedPower(),
                        wealthFamilyPower,
                        companionFamilyPower,
                        officerFamilyPower,
                        resourceFamilyPower,
                        wealthFamilyShareBasisPoints,
                        directWealthSummary.occurrenceCount(),
                        indirectWealthSummary.occurrenceCount(),
                        directWealthSummary.visible(),
                        indirectWealthSummary.visible(),
                        directWealthSummary.monthPresent(),
                        indirectWealthSummary.monthPresent(),
                        reasons
                )
        );
    }

    /*
     * =================================================
     * 재성 family 점유율
     * =================================================
     */

    private int calculateFamilyShareBasisPoints(

            int wealthFamilyPower,

            int totalExternalTenGodPower

    ) {

        if (
                wealthFamilyPower <= 0
                        || totalExternalTenGodPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                10_000,
                (int) Math.round(
                        wealthFamilyPower
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