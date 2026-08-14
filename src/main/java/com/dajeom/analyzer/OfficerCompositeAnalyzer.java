package com.dajeom.analyzer;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.OfficerCompositeAnalysis;
import com.dajeom.model.OfficerCompositeSignal;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.rule.OfficerCompositeRule;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.OfficerCompositePatternLevel;
import com.dajeom.type.OfficerCompositePatternType;
import com.dajeom.type.OfficerCompositeReasonType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-5.
 *
 * 정관·칠살 복합구조 Analyzer.
 */
@Component
public class OfficerCompositeAnalyzer {

    private final OfficerCompositeRule
            officerCompositeRule;

    public OfficerCompositeAnalyzer(
            OfficerCompositeRule officerCompositeRule
    ) {

        this.officerCompositeRule =
                Objects.requireNonNull(
                        officerCompositeRule,
                        "OfficerCompositeRule이 필요합니다."
                );
    }

    public OfficerCompositeAnalysis analyze(

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
         * 정관
         * =================================================
         */

        TenGodPowerSummary directOfficerSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.DIRECT_OFFICER
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 정관 Summary가 없습니다."
                                        )
                        );

        /*
         * =================================================
         * 칠살
         * =================================================
         */

        TenGodPowerSummary sevenKillingsSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.SEVEN_KILLINGS
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 칠살 Summary가 없습니다."
                                        )
                        );

        /*
         * =================================================
         * Family Power
         * =================================================
         */

        int officerFamilyPower =
                directOfficerSummary.allocatedPower()
                        + sevenKillingsSummary.allocatedPower();

        int outputFamilyPower =
                tenGodDistributionAnalysis
                        .outputFamilyPower();

        int resourceFamilyPower =
                tenGodDistributionAnalysis
                        .resourceFamilyPower();

        int wealthFamilyPower =
                tenGodDistributionAnalysis
                        .wealthFamilyPower();

        int officerFamilyShareBasisPoints =
                calculateFamilyShareBasisPoints(
                        officerFamilyPower,
                        tenGodDistributionAnalysis
                                .totalExternalTenGodPower()
                );

        DayMasterStrengthLevel strengthLevel =
                dayMasterStrengthAnalysis
                        .level();

        int totalOccurrenceCount =
                directOfficerSummary.occurrenceCount()
                        + sevenKillingsSummary.occurrenceCount();

        List<OfficerCompositeSignal> signals =
                new ArrayList<>();

        /*
         * =================================================
         * 1. 관살 발달
         * =================================================
         */

        addSignalIfPresent(
                signals,

                OfficerCompositePatternType
                        .OFFICER_DEVELOPED,

                officerCompositeRule
                        .determineOfficerDevelopedLevel(
                                officerFamilyShareBasisPoints,
                                totalOccurrenceCount,
                                directOfficerSummary.visible(),
                                sevenKillingsSummary.visible()
                        ),

                directOfficerSummary,
                sevenKillingsSummary,
                strengthLevel,
                officerFamilyPower,
                outputFamilyPower,
                resourceFamilyPower,
                wealthFamilyPower,
                officerFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 2. 정관 중심
         * =================================================
         */

        addSignalIfPresent(
                signals,

                OfficerCompositePatternType
                        .DIRECT_OFFICER_CENTERED,

                officerCompositeRule
                        .determineDirectOfficerCenteredLevel(
                                directOfficerSummary,
                                sevenKillingsSummary
                        ),

                directOfficerSummary,
                sevenKillingsSummary,
                strengthLevel,
                officerFamilyPower,
                outputFamilyPower,
                resourceFamilyPower,
                wealthFamilyPower,
                officerFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 3. 칠살 중심
         * =================================================
         */

        addSignalIfPresent(
                signals,

                OfficerCompositePatternType
                        .SEVEN_KILLINGS_CENTERED,

                officerCompositeRule
                        .determineSevenKillingsCenteredLevel(
                                directOfficerSummary,
                                sevenKillingsSummary
                        ),

                directOfficerSummary,
                sevenKillingsSummary,
                strengthLevel,
                officerFamilyPower,
                outputFamilyPower,
                resourceFamilyPower,
                wealthFamilyPower,
                officerFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 4. 정관·칠살 혼재
         * =================================================
         */

        addSignalIfPresent(
                signals,

                OfficerCompositePatternType
                        .MIXED_OFFICER_KILLINGS,

                officerCompositeRule
                        .determineMixedOfficerKillingsLevel(
                                directOfficerSummary,
                                sevenKillingsSummary
                        ),

                directOfficerSummary,
                sevenKillingsSummary,
                strengthLevel,
                officerFamilyPower,
                outputFamilyPower,
                resourceFamilyPower,
                wealthFamilyPower,
                officerFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 5. 신약 관살 압박
         * =================================================
         */

        addSignalIfPresent(
                signals,

                OfficerCompositePatternType
                        .WEAK_DAY_MASTER_OFFICER_PRESSURE,

                officerCompositeRule
                        .determineWeakDayMasterPressureLevel(
                                strengthLevel,
                                officerFamilyShareBasisPoints,
                                totalOccurrenceCount
                        ),

                directOfficerSummary,
                sevenKillingsSummary,
                strengthLevel,
                officerFamilyPower,
                outputFamilyPower,
                resourceFamilyPower,
                wealthFamilyPower,
                officerFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 6. 식상 제어 Context
         * =================================================
         */

        addSignalIfPresent(
                signals,

                OfficerCompositePatternType
                        .OUTPUT_CONTROL_CONTEXT,

                officerCompositeRule
                        .determineOutputControlContextLevel(
                                officerFamilyPower,
                                outputFamilyPower
                        ),

                directOfficerSummary,
                sevenKillingsSummary,
                strengthLevel,
                officerFamilyPower,
                outputFamilyPower,
                resourceFamilyPower,
                wealthFamilyPower,
                officerFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 7. 인성 연결 Context
         * =================================================
         */

        addSignalIfPresent(
                signals,

                OfficerCompositePatternType
                        .RESOURCE_MEDIATION_CONTEXT,

                officerCompositeRule
                        .determineResourceMediationContextLevel(
                                officerFamilyPower,
                                resourceFamilyPower
                        ),

                directOfficerSummary,
                sevenKillingsSummary,
                strengthLevel,
                officerFamilyPower,
                outputFamilyPower,
                resourceFamilyPower,
                wealthFamilyPower,
                officerFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 8. 재성 생조 Context
         * =================================================
         */

        addSignalIfPresent(
                signals,

                OfficerCompositePatternType
                        .WEALTH_SUPPORT_CONTEXT,

                officerCompositeRule
                        .determineWealthSupportContextLevel(
                                officerFamilyPower,
                                wealthFamilyPower
                        ),

                directOfficerSummary,
                sevenKillingsSummary,
                strengthLevel,
                officerFamilyPower,
                outputFamilyPower,
                resourceFamilyPower,
                wealthFamilyPower,
                officerFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 정렬
         *
         * 구조 강도 순.
         * 길흉 순위가 아니다.
         * =================================================
         */

        signals.sort(
                Comparator
                        .comparingInt(
                                (
                                        OfficerCompositeSignal signal
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

        return new OfficerCompositeAnalysis(
                tenGodDistributionAnalysis
                        .dayMaster(),
                tenGodDistributionAnalysis,
                dayMasterStrengthAnalysis,
                directOfficerSummary,
                sevenKillingsSummary,
                officerFamilyPower,
                outputFamilyPower,
                resourceFamilyPower,
                wealthFamilyPower,
                officerFamilyShareBasisPoints,
                signals,
                tenGodDistributionAnalysis
                        .completeChart()
        );
    }

    private void addSignalIfPresent(

            List<OfficerCompositeSignal> signals,

            OfficerCompositePatternType patternType,

            OfficerCompositePatternLevel level,

            TenGodPowerSummary directOfficerSummary,

            TenGodPowerSummary sevenKillingsSummary,

            DayMasterStrengthLevel strengthLevel,

            int officerFamilyPower,

            int outputFamilyPower,

            int resourceFamilyPower,

            int wealthFamilyPower,

            int officerFamilyShareBasisPoints

    ) {

        if (
                level == null
        ) {

            return;
        }

        List<OfficerCompositeReasonType> reasons =
                officerCompositeRule
                        .buildReasons(
                                patternType,
                                directOfficerSummary,
                                sevenKillingsSummary,
                                strengthLevel,
                                officerFamilyShareBasisPoints,
                                officerFamilyPower,
                                outputFamilyPower,
                                resourceFamilyPower,
                                wealthFamilyPower
                        );

        signals.add(
                new OfficerCompositeSignal(
                        patternType,
                        level,
                        directOfficerSummary.allocatedPower(),
                        sevenKillingsSummary.allocatedPower(),
                        officerFamilyPower,
                        outputFamilyPower,
                        resourceFamilyPower,
                        wealthFamilyPower,
                        officerFamilyShareBasisPoints,
                        directOfficerSummary.occurrenceCount(),
                        sevenKillingsSummary.occurrenceCount(),
                        directOfficerSummary.visible(),
                        sevenKillingsSummary.visible(),
                        directOfficerSummary.monthPresent(),
                        sevenKillingsSummary.monthPresent(),
                        reasons
                )
        );
    }

    private int calculateFamilyShareBasisPoints(

            int officerFamilyPower,

            int totalExternalTenGodPower

    ) {

        if (
                officerFamilyPower <= 0
                        || totalExternalTenGodPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                10_000,
                (int) Math.round(
                        officerFamilyPower
                                * 10_000.0
                                / totalExternalTenGodPower
                )
        );
    }

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