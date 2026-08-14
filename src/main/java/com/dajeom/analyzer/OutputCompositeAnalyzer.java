package com.dajeom.analyzer;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.OutputCompositeAnalysis;
import com.dajeom.model.OutputCompositeSignal;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.rule.OutputCompositeRule;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.OutputCompositePatternLevel;
import com.dajeom.type.OutputCompositePatternType;
import com.dajeom.type.OutputCompositeReasonType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-3.
 *
 * 식신·상관 복합구조 Analyzer.
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
 * - 식상 발달
 * - 식신 중심
 * - 상관 중심
 * - 식신·상관 혼재
 * - 신약 식상 과설
 * - 인성 제약 식상
 *
 * =================================================
 *
 * 주의:
 *
 * 상관견관
 * 식신제살
 * 상관패인
 * 효신탈식
 *
 * 은 여기에서 확정하지 않는다.
 *
 * 10-8의 책임이다.
 */
@Component
public class OutputCompositeAnalyzer {

    private final OutputCompositeRule
            outputCompositeRule;

    public OutputCompositeAnalyzer(
            OutputCompositeRule outputCompositeRule
    ) {

        this.outputCompositeRule =
                Objects.requireNonNull(
                        outputCompositeRule,
                        "OutputCompositeRule이 필요합니다."
                );
    }

    public OutputCompositeAnalysis analyze(

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

        TenGodPowerSummary eatingGodSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.EATING_GOD
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 식신 Summary가 없습니다."
                                        )
                        );

        TenGodPowerSummary hurtingOfficerSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.HURTING_OFFICER
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 상관 Summary가 없습니다."
                                        )
                        );

        int outputFamilyPower =
                eatingGodSummary.allocatedPower()
                        + hurtingOfficerSummary.allocatedPower();

        int resourceFamilyPower =
                tenGodDistributionAnalysis
                        .resourceFamilyPower();

        int officerFamilyPower =
                tenGodDistributionAnalysis
                        .officerFamilyPower();

        int outputFamilyShareBasisPoints =
                calculateFamilyShareBasisPoints(
                        outputFamilyPower,
                        tenGodDistributionAnalysis
                                .totalExternalTenGodPower()
                );

        DayMasterStrengthLevel strengthLevel =
                dayMasterStrengthAnalysis
                        .level();

        int totalOutputOccurrenceCount =
                eatingGodSummary.occurrenceCount()
                        + hurtingOfficerSummary.occurrenceCount();

        List<OutputCompositeSignal> signals =
                new ArrayList<>();

        /*
         * =================================================
         * 1. 식상 발달
         * =================================================
         */

        addSignalIfPresent(
                signals,
                OutputCompositePatternType
                        .OUTPUT_DEVELOPED,

                outputCompositeRule
                        .determineOutputDevelopedLevel(
                                outputFamilyShareBasisPoints,
                                totalOutputOccurrenceCount,
                                eatingGodSummary.visible(),
                                hurtingOfficerSummary.visible()
                        ),

                eatingGodSummary,
                hurtingOfficerSummary,
                strengthLevel,
                outputFamilyPower,
                resourceFamilyPower,
                officerFamilyPower,
                outputFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 2. 식신 중심
         * =================================================
         */

        addSignalIfPresent(
                signals,
                OutputCompositePatternType
                        .EATING_GOD_CENTERED,

                outputCompositeRule
                        .determineEatingGodCenteredLevel(
                                eatingGodSummary,
                                hurtingOfficerSummary
                        ),

                eatingGodSummary,
                hurtingOfficerSummary,
                strengthLevel,
                outputFamilyPower,
                resourceFamilyPower,
                officerFamilyPower,
                outputFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 3. 상관 중심
         * =================================================
         */

        addSignalIfPresent(
                signals,
                OutputCompositePatternType
                        .HURTING_OFFICER_CENTERED,

                outputCompositeRule
                        .determineHurtingOfficerCenteredLevel(
                                eatingGodSummary,
                                hurtingOfficerSummary
                        ),

                eatingGodSummary,
                hurtingOfficerSummary,
                strengthLevel,
                outputFamilyPower,
                resourceFamilyPower,
                officerFamilyPower,
                outputFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 4. 식신·상관 혼재
         * =================================================
         */

        addSignalIfPresent(
                signals,
                OutputCompositePatternType
                        .MIXED_OUTPUT,

                outputCompositeRule
                        .determineMixedOutputLevel(
                                eatingGodSummary,
                                hurtingOfficerSummary
                        ),

                eatingGodSummary,
                hurtingOfficerSummary,
                strengthLevel,
                outputFamilyPower,
                resourceFamilyPower,
                officerFamilyPower,
                outputFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 5. 신약 식상 과설
         * =================================================
         */

        addSignalIfPresent(
                signals,
                OutputCompositePatternType
                        .WEAK_DAY_MASTER_EXCESSIVE_DRAIN,

                outputCompositeRule
                        .determineWeakDayMasterExcessiveDrainLevel(
                                strengthLevel,
                                outputFamilyShareBasisPoints,
                                totalOutputOccurrenceCount
                        ),

                eatingGodSummary,
                hurtingOfficerSummary,
                strengthLevel,
                outputFamilyPower,
                resourceFamilyPower,
                officerFamilyPower,
                outputFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 6. 인성 제약 식상
         * =================================================
         */

        addSignalIfPresent(
                signals,
                OutputCompositePatternType
                        .RESOURCE_CONSTRAINED_OUTPUT,

                outputCompositeRule
                        .determineResourceConstrainedLevel(
                                outputFamilyPower,
                                resourceFamilyPower,
                                outputFamilyShareBasisPoints
                        ),

                eatingGodSummary,
                hurtingOfficerSummary,
                strengthLevel,
                outputFamilyPower,
                resourceFamilyPower,
                officerFamilyPower,
                outputFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 정렬
         *
         * 구조 강도 기준.
         * 길흉 순위가 아니다.
         * =================================================
         */

        signals.sort(
                Comparator
                        .comparingInt(
                                (
                                        OutputCompositeSignal signal
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

        return new OutputCompositeAnalysis(
                tenGodDistributionAnalysis
                        .dayMaster(),
                tenGodDistributionAnalysis,
                dayMasterStrengthAnalysis,
                eatingGodSummary,
                hurtingOfficerSummary,
                outputFamilyPower,
                resourceFamilyPower,
                officerFamilyPower,
                outputFamilyShareBasisPoints,
                signals,
                tenGodDistributionAnalysis
                        .completeChart()
        );
    }

    /*
     * =================================================
     * Signal
     * =================================================
     */

    private void addSignalIfPresent(

            List<OutputCompositeSignal> signals,

            OutputCompositePatternType patternType,

            OutputCompositePatternLevel level,

            TenGodPowerSummary eatingGodSummary,

            TenGodPowerSummary hurtingOfficerSummary,

            DayMasterStrengthLevel strengthLevel,

            int outputFamilyPower,

            int resourceFamilyPower,

            int officerFamilyPower,

            int outputFamilyShareBasisPoints

    ) {

        if (
                level == null
        ) {

            return;
        }

        List<OutputCompositeReasonType> reasons =
                outputCompositeRule
                        .buildReasons(
                                patternType,
                                eatingGodSummary,
                                hurtingOfficerSummary,
                                strengthLevel,
                                outputFamilyShareBasisPoints,
                                outputFamilyPower,
                                resourceFamilyPower,
                                officerFamilyPower
                        );

        signals.add(
                new OutputCompositeSignal(
                        patternType,
                        level,
                        eatingGodSummary.allocatedPower(),
                        hurtingOfficerSummary.allocatedPower(),
                        outputFamilyPower,
                        resourceFamilyPower,
                        officerFamilyPower,
                        outputFamilyShareBasisPoints,
                        eatingGodSummary.occurrenceCount(),
                        hurtingOfficerSummary.occurrenceCount(),
                        eatingGodSummary.visible(),
                        hurtingOfficerSummary.visible(),
                        eatingGodSummary.monthPresent(),
                        hurtingOfficerSummary.monthPresent(),
                        reasons
                )
        );
    }

    /*
     * =================================================
     * Family share
     * =================================================
     */

    private int calculateFamilyShareBasisPoints(

            int outputFamilyPower,

            int totalExternalTenGodPower

    ) {

        if (
                outputFamilyPower <= 0
                        || totalExternalTenGodPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                10_000,
                (int) Math.round(
                        outputFamilyPower
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