package com.dajeom.analyzer;

import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.ResourceCompositeAnalysis;
import com.dajeom.model.ResourceCompositeSignal;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.rule.ResourceCompositeRule;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.ResourceCompositePatternLevel;
import com.dajeom.type.ResourceCompositePatternType;
import com.dajeom.type.ResourceCompositeReasonType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-6.
 *
 * 정인·편인 복합구조 Analyzer.
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
 * 1. 인성 발달
 * 2. 정인 중심
 * 3. 편인 중심
 * 4. 정인·편인 혼재
 * 5. 신약 인성 보조
 * 6. 신강 인성 과다
 * 7. 식상 제약 Context
 * 8. 관살 생인 Context
 * 9. 재성 압박 Context
 *
 * =================================================
 *
 * 특수 고전 Pattern은
 * 여기에서 확정하지 않는다.
 */
@Component
public class ResourceCompositeAnalyzer {

    private final ResourceCompositeRule
            resourceCompositeRule;

    public ResourceCompositeAnalyzer(
            ResourceCompositeRule resourceCompositeRule
    ) {

        this.resourceCompositeRule =
                Objects.requireNonNull(
                        resourceCompositeRule,
                        "ResourceCompositeRule이 필요합니다."
                );
    }

    public ResourceCompositeAnalysis analyze(

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
         * 정인
         * =================================================
         */

        TenGodPowerSummary directResourceSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.DIRECT_RESOURCE
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 정인 Summary가 없습니다."
                                        )
                        );

        /*
         * =================================================
         * 편인
         * =================================================
         */

        TenGodPowerSummary indirectResourceSummary =
                tenGodDistributionAnalysis
                        .summaryOf(
                                StructureTenGodType.INDIRECT_RESOURCE
                        )
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "10-1 분석에 편인 Summary가 없습니다."
                                        )
                        );

        /*
         * =================================================
         * Family Power
         * =================================================
         */

        int resourceFamilyPower =
                directResourceSummary.allocatedPower()
                        + indirectResourceSummary.allocatedPower();

        int outputFamilyPower =
                tenGodDistributionAnalysis
                        .outputFamilyPower();

        int officerFamilyPower =
                tenGodDistributionAnalysis
                        .officerFamilyPower();

        int wealthFamilyPower =
                tenGodDistributionAnalysis
                        .wealthFamilyPower();

        int resourceFamilyShareBasisPoints =
                calculateFamilyShareBasisPoints(
                        resourceFamilyPower,
                        tenGodDistributionAnalysis
                                .totalExternalTenGodPower()
                );

        DayMasterStrengthLevel strengthLevel =
                dayMasterStrengthAnalysis
                        .level();

        int totalOccurrenceCount =
                directResourceSummary.occurrenceCount()
                        + indirectResourceSummary.occurrenceCount();

        List<ResourceCompositeSignal> signals =
                new ArrayList<>();

        /*
         * =================================================
         * 1. 인성 발달
         * =================================================
         */

        addSignalIfPresent(
                signals,

                ResourceCompositePatternType
                        .RESOURCE_DEVELOPED,

                resourceCompositeRule
                        .determineResourceDevelopedLevel(
                                resourceFamilyShareBasisPoints,
                                totalOccurrenceCount,
                                directResourceSummary.visible(),
                                indirectResourceSummary.visible()
                        ),

                directResourceSummary,
                indirectResourceSummary,
                strengthLevel,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 2. 정인 중심
         * =================================================
         */

        addSignalIfPresent(
                signals,

                ResourceCompositePatternType
                        .DIRECT_RESOURCE_CENTERED,

                resourceCompositeRule
                        .determineDirectResourceCenteredLevel(
                                directResourceSummary,
                                indirectResourceSummary
                        ),

                directResourceSummary,
                indirectResourceSummary,
                strengthLevel,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 3. 편인 중심
         * =================================================
         */

        addSignalIfPresent(
                signals,

                ResourceCompositePatternType
                        .INDIRECT_RESOURCE_CENTERED,

                resourceCompositeRule
                        .determineIndirectResourceCenteredLevel(
                                directResourceSummary,
                                indirectResourceSummary
                        ),

                directResourceSummary,
                indirectResourceSummary,
                strengthLevel,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 4. 정인·편인 혼재
         * =================================================
         */

        addSignalIfPresent(
                signals,

                ResourceCompositePatternType
                        .MIXED_RESOURCE,

                resourceCompositeRule
                        .determineMixedResourceLevel(
                                directResourceSummary,
                                indirectResourceSummary
                        ),

                directResourceSummary,
                indirectResourceSummary,
                strengthLevel,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 5. 신약 인성 보조
         * =================================================
         */

        addSignalIfPresent(
                signals,

                ResourceCompositePatternType
                        .WEAK_DAY_MASTER_RESOURCE_SUPPORT,

                resourceCompositeRule
                        .determineWeakDayMasterResourceSupportLevel(
                                strengthLevel,
                                resourceFamilyShareBasisPoints,
                                totalOccurrenceCount
                        ),

                directResourceSummary,
                indirectResourceSummary,
                strengthLevel,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 6. 신강 인성 과다
         * =================================================
         */

        addSignalIfPresent(
                signals,

                ResourceCompositePatternType
                        .STRONG_DAY_MASTER_EXCESSIVE_RESOURCE,

                resourceCompositeRule
                        .determineStrongDayMasterExcessiveResourceLevel(
                                strengthLevel,
                                resourceFamilyShareBasisPoints,
                                totalOccurrenceCount
                        ),

                directResourceSummary,
                indirectResourceSummary,
                strengthLevel,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 7. 식상 제약 Context
         * =================================================
         */

        addSignalIfPresent(
                signals,

                ResourceCompositePatternType
                        .OUTPUT_CONSTRAINT_CONTEXT,

                resourceCompositeRule
                        .determineOutputConstraintContextLevel(
                                resourceFamilyPower,
                                outputFamilyPower
                        ),

                directResourceSummary,
                indirectResourceSummary,
                strengthLevel,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 8. 관살 생인 Context
         * =================================================
         */

        addSignalIfPresent(
                signals,

                ResourceCompositePatternType
                        .OFFICER_GENERATION_CONTEXT,

                resourceCompositeRule
                        .determineOfficerGenerationContextLevel(
                                resourceFamilyPower,
                                officerFamilyPower
                        ),

                directResourceSummary,
                indirectResourceSummary,
                strengthLevel,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints
        );

        /*
         * =================================================
         * 9. 재성 압박 Context
         * =================================================
         */

        addSignalIfPresent(
                signals,

                ResourceCompositePatternType
                        .WEALTH_PRESSURE_CONTEXT,

                resourceCompositeRule
                        .determineWealthPressureContextLevel(
                                resourceFamilyPower,
                                wealthFamilyPower
                        ),

                directResourceSummary,
                indirectResourceSummary,
                strengthLevel,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints
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
                                        ResourceCompositeSignal signal
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

        return new ResourceCompositeAnalysis(
                tenGodDistributionAnalysis
                        .dayMaster(),
                tenGodDistributionAnalysis,
                dayMasterStrengthAnalysis,
                directResourceSummary,
                indirectResourceSummary,
                resourceFamilyPower,
                outputFamilyPower,
                officerFamilyPower,
                wealthFamilyPower,
                resourceFamilyShareBasisPoints,
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

            List<ResourceCompositeSignal> signals,

            ResourceCompositePatternType patternType,

            ResourceCompositePatternLevel level,

            TenGodPowerSummary directResourceSummary,

            TenGodPowerSummary indirectResourceSummary,

            DayMasterStrengthLevel strengthLevel,

            int resourceFamilyPower,

            int outputFamilyPower,

            int officerFamilyPower,

            int wealthFamilyPower,

            int resourceFamilyShareBasisPoints

    ) {

        if (
                level == null
        ) {

            return;
        }

        List<ResourceCompositeReasonType> reasons =
                resourceCompositeRule
                        .buildReasons(
                                patternType,
                                directResourceSummary,
                                indirectResourceSummary,
                                strengthLevel,
                                resourceFamilyShareBasisPoints,
                                resourceFamilyPower,
                                outputFamilyPower,
                                officerFamilyPower,
                                wealthFamilyPower
                        );

        signals.add(
                new ResourceCompositeSignal(
                        patternType,
                        level,
                        directResourceSummary.allocatedPower(),
                        indirectResourceSummary.allocatedPower(),
                        resourceFamilyPower,
                        outputFamilyPower,
                        officerFamilyPower,
                        wealthFamilyPower,
                        resourceFamilyShareBasisPoints,
                        directResourceSummary.occurrenceCount(),
                        indirectResourceSummary.occurrenceCount(),
                        directResourceSummary.visible(),
                        indirectResourceSummary.visible(),
                        directResourceSummary.monthPresent(),
                        indirectResourceSummary.monthPresent(),
                        reasons
                )
        );
    }

    /*
     * =================================================
     * 인성 family 점유율
     * =================================================
     */

    private int calculateFamilyShareBasisPoints(

            int resourceFamilyPower,

            int totalExternalTenGodPower

    ) {

        if (
                resourceFamilyPower <= 0
                        || totalExternalTenGodPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                10_000,
                (int) Math.round(
                        resourceFamilyPower
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