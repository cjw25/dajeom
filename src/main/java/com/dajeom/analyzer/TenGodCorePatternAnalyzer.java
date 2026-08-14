package com.dajeom.analyzer;

import com.dajeom.model.CompanionCompositeAnalysis;
import com.dajeom.model.OfficerCompositeAnalysis;
import com.dajeom.model.ResourceCompositeAnalysis;
import com.dajeom.model.TenGodCorePatternAnalysis;
import com.dajeom.model.TenGodCorePatternSignal;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodFamilyChainAnalysis;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.model.WealthCompositeAnalysis;
import com.dajeom.rule.TenGodCorePatternRule;
import com.dajeom.type.CompanionCompositePatternType;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.OfficerCompositePatternType;
import com.dajeom.type.ResourceCompositePatternType;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodCorePatternLevel;
import com.dajeom.type.TenGodCorePatternReasonType;
import com.dajeom.type.TenGodCorePatternType;
import com.dajeom.type.TenGodFamilyInteractionType;
import com.dajeom.type.WealthCompositePatternType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-8.
 *
 * 핵심 십성 복합 Pattern Analyzer.
 *
 * =================================================
 *
 * 입력은 10-7 TenGodFamilyChainAnalysis 하나로
 * 통합한다.
 *
 * 그 안에서:
 *
 * 10-1
 * 10-2
 * 10-3
 * 10-4
 * 10-5
 * 10-6
 *
 * 결과를 모두 다시 접근할 수 있다.
 *
 * =================================================
 *
 * Pattern:
 *
 * 식신제살
 * 상관견관
 * 상관패인
 * 편인도식·효신탈식
 * 재생관
 * 관인상생
 * 살인상생
 * 재극인
 * 비겁쟁재
 * 재다신약
 * 관살혼재
 *
 * =================================================
 *
 * 길흉과 최종 성패는 10-9의 책임이다.
 */
@Component
public class TenGodCorePatternAnalyzer {

    private final TenGodCorePatternRule
            tenGodCorePatternRule;

    public TenGodCorePatternAnalyzer(
            TenGodCorePatternRule tenGodCorePatternRule
    ) {

        this.tenGodCorePatternRule =
                Objects.requireNonNull(
                        tenGodCorePatternRule,
                        "TenGodCorePatternRule이 필요합니다."
                );
    }

    public TenGodCorePatternAnalysis analyze(
            TenGodFamilyChainAnalysis familyChainAnalysis
    ) {

        Objects.requireNonNull(
                familyChainAnalysis,
                "10-7 TenGodFamilyChainAnalysis가 필요합니다."
        );

        TenGodDistributionAnalysis distribution =
                familyChainAnalysis
                        .tenGodDistributionAnalysis();

        CompanionCompositeAnalysis companion =
                familyChainAnalysis
                        .companionCompositeAnalysis();

        WealthCompositeAnalysis wealth =
                familyChainAnalysis
                        .wealthCompositeAnalysis();

        OfficerCompositeAnalysis officer =
                familyChainAnalysis
                        .officerCompositeAnalysis();

        ResourceCompositeAnalysis resource =
                familyChainAnalysis
                        .resourceCompositeAnalysis();

        /*
         * =================================================
         * 개별 십성
         * =================================================
         */

        TenGodPowerSummary eatingGod =
                summary(
                        distribution,
                        StructureTenGodType.EATING_GOD
                );

        TenGodPowerSummary hurtingOfficer =
                summary(
                        distribution,
                        StructureTenGodType.HURTING_OFFICER
                );

        TenGodPowerSummary directWealth =
                summary(
                        distribution,
                        StructureTenGodType.DIRECT_WEALTH
                );

        TenGodPowerSummary indirectWealth =
                summary(
                        distribution,
                        StructureTenGodType.INDIRECT_WEALTH
                );

        TenGodPowerSummary directOfficer =
                summary(
                        distribution,
                        StructureTenGodType.DIRECT_OFFICER
                );

        TenGodPowerSummary sevenKillings =
                summary(
                        distribution,
                        StructureTenGodType.SEVEN_KILLINGS
                );

        TenGodPowerSummary directResource =
                summary(
                        distribution,
                        StructureTenGodType.DIRECT_RESOURCE
                );

        TenGodPowerSummary indirectResource =
                summary(
                        distribution,
                        StructureTenGodType.INDIRECT_RESOURCE
                );

        TenGodPowerSummary companionSummary =
                summary(
                        distribution,
                        StructureTenGodType.COMPANION
                );

        TenGodPowerSummary robWealth =
                summary(
                        distribution,
                        StructureTenGodType.ROB_WEALTH
                );

        /*
         * =================================================
         * Family share
         * =================================================
         */

        int totalPower =
                distribution
                        .totalExternalTenGodPower();

        int wealthShare =
                share(
                        wealth.wealthFamilyPower(),
                        totalPower
                );

        int resourceShare =
                share(
                        resource.resourceFamilyPower(),
                        totalPower
                );

        int companionShare =
                share(
                        companion.companionFamilyPower(),
                        totalPower
                );

        List<TenGodCorePatternSignal> signals =
                new ArrayList<>();

        /*
         * =================================================
         * 1. 식신제살
         * =================================================
         */

        TenGodCorePatternLevel level =
                tenGodCorePatternRule
                        .determineEatingGodControlsSevenKillings(
                                eatingGod,
                                sevenKillings,
                                familyChainAnalysis
                        );

        addIndividualPairSignal(
                signals,
                TenGodCorePatternType
                        .EATING_GOD_CONTROLS_SEVEN_KILLINGS,
                level,
                eatingGod,
                sevenKillings,
                familyChainAnalysis,
                DayMasterElementRole.OUTPUT,
                DayMasterElementRole.OFFICER,
                TenGodFamilyInteractionType.CONTROL,
                officer.outputControlContext()
        );

        /*
         * =================================================
         * 2. 상관견관
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineHurtingOfficerMeetsDirectOfficer(
                                hurtingOfficer,
                                directOfficer,
                                familyChainAnalysis
                        );

        addIndividualPairSignal(
                signals,
                TenGodCorePatternType
                        .HURTING_OFFICER_MEETS_DIRECT_OFFICER,
                level,
                hurtingOfficer,
                directOfficer,
                familyChainAnalysis,
                DayMasterElementRole.OUTPUT,
                DayMasterElementRole.OFFICER,
                TenGodFamilyInteractionType.CONTROL,
                officer.outputControlContext()
        );

        /*
         * =================================================
         * 3. 상관패인
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineHurtingOfficerWithResource(
                                hurtingOfficer,
                                directResource,
                                familyChainAnalysis
                        );

        addIndividualPairSignal(
                signals,
                TenGodCorePatternType
                        .HURTING_OFFICER_WITH_RESOURCE,
                level,
                directResource,
                hurtingOfficer,
                familyChainAnalysis,
                DayMasterElementRole.RESOURCE,
                DayMasterElementRole.OUTPUT,
                TenGodFamilyInteractionType.CONTROL,
                resource.outputConstraintContext()
        );

        /*
         * =================================================
         * 4. 편인도식 / 효신탈식
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineIndirectResourceSeizesEatingGod(
                                indirectResource,
                                eatingGod,
                                familyChainAnalysis
                        );

        addIndividualPairSignal(
                signals,
                TenGodCorePatternType
                        .INDIRECT_RESOURCE_SEIZES_EATING_GOD,
                level,
                indirectResource,
                eatingGod,
                familyChainAnalysis,
                DayMasterElementRole.RESOURCE,
                DayMasterElementRole.OUTPUT,
                TenGodFamilyInteractionType.CONTROL,
                resource.outputConstraintContext()
        );

        /*
         * =================================================
         * 5. 재생관
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineWealthGeneratesDirectOfficer(
                                wealth.wealthFamilyPower(),
                                wealthShare,
                                directOfficer,
                                familyChainAnalysis
                        );

        addFamilyToIndividualSignal(
                signals,
                TenGodCorePatternType
                        .WEALTH_GENERATES_DIRECT_OFFICER,
                level,
                presentTypes(
                        directWealth,
                        indirectWealth,
                        directOfficer
                ),
                wealth.wealthFamilyPower(),
                directOfficer.allocatedPower(),
                wealthShare,
                directOfficer.shareBasisPoints(),
                directWealth.visible()
                        || indirectWealth.visible(),
                directOfficer.visible(),
                directWealth.monthPresent()
                        || indirectWealth.monthPresent(),
                directOfficer.monthPresent(),
                familyChainAnalysis,
                DayMasterElementRole.WEALTH,
                DayMasterElementRole.OFFICER,
                TenGodFamilyInteractionType.GENERATION,
                directOfficer.meaningful(),
                wealth.wealthDeveloped(),
                false
        );

        /*
         * =================================================
         * 6. 관인상생
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineDirectOfficerGeneratesResource(
                                directOfficer,
                                directResource,
                                familyChainAnalysis
                        );

        addIndividualPairSignal(
                signals,
                TenGodCorePatternType
                        .DIRECT_OFFICER_GENERATES_RESOURCE,
                level,
                directOfficer,
                directResource,
                familyChainAnalysis,
                DayMasterElementRole.OFFICER,
                DayMasterElementRole.RESOURCE,
                TenGodFamilyInteractionType.GENERATION,
                officer.resourceMediationContext()
                        && resource.officerGenerationContext()
        );

        /*
         * =================================================
         * 7. 살인상생
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineSevenKillingsGeneratesResource(
                                sevenKillings,
                                resource.resourceFamilyPower(),
                                resourceShare,
                                familyChainAnalysis
                        );

        addFamilyToIndividualSignal(
                signals,
                TenGodCorePatternType
                        .SEVEN_KILLINGS_GENERATES_RESOURCE,
                level,
                presentTypes(
                        sevenKillings,
                        directResource,
                        indirectResource
                ),
                sevenKillings.allocatedPower(),
                resource.resourceFamilyPower(),
                sevenKillings.shareBasisPoints(),
                resourceShare,
                sevenKillings.visible(),
                directResource.visible()
                        || indirectResource.visible(),
                sevenKillings.monthPresent(),
                directResource.monthPresent()
                        || indirectResource.monthPresent(),
                familyChainAnalysis,
                DayMasterElementRole.OFFICER,
                DayMasterElementRole.RESOURCE,
                TenGodFamilyInteractionType.GENERATION,
                sevenKillings.meaningful(),
                officer.resourceMediationContext()
                        && resource.officerGenerationContext(),
                false
        );

        /*
         * =================================================
         * 8. 재극인
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineWealthControlsResource(
                                wealth.wealthFamilyPower(),
                                resource.resourceFamilyPower(),
                                wealthShare,
                                resourceShare,
                                familyChainAnalysis
                        );

        addFamilyToIndividualSignal(
                signals,
                TenGodCorePatternType
                        .WEALTH_CONTROLS_RESOURCE,
                level,
                presentTypes(
                        directWealth,
                        indirectWealth,
                        directResource,
                        indirectResource
                ),
                wealth.wealthFamilyPower(),
                resource.resourceFamilyPower(),
                wealthShare,
                resourceShare,
                directWealth.visible()
                        || indirectWealth.visible(),
                directResource.visible()
                        || indirectResource.visible(),
                directWealth.monthPresent()
                        || indirectWealth.monthPresent(),
                directResource.monthPresent()
                        || indirectResource.monthPresent(),
                familyChainAnalysis,
                DayMasterElementRole.WEALTH,
                DayMasterElementRole.RESOURCE,
                TenGodFamilyInteractionType.CONTROL,
                wealth.wealthDeveloped(),
                resource.wealthPressureContext(),
                false
        );

        /*
         * =================================================
         * 9. 비겁쟁재
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineCompanionContestsWealth(
                                companion,
                                wealth,
                                robWealth,
                                familyChainAnalysis
                        );

        addFamilyToIndividualSignal(
                signals,
                TenGodCorePatternType
                        .COMPANION_CONTESTS_WEALTH,
                level,
                presentTypes(
                        companionSummary,
                        robWealth,
                        directWealth,
                        indirectWealth
                ),
                companion.companionFamilyPower(),
                wealth.wealthFamilyPower(),
                companionShare,
                wealthShare,
                companionSummary.visible()
                        || robWealth.visible(),
                directWealth.visible()
                        || indirectWealth.visible(),
                companionSummary.monthPresent()
                        || robWealth.monthPresent(),
                directWealth.monthPresent()
                        || indirectWealth.monthPresent(),
                familyChainAnalysis,
                DayMasterElementRole.COMPANION,
                DayMasterElementRole.WEALTH,
                TenGodFamilyInteractionType.CONTROL,
                robWealth.meaningful(),
                companion.hasMeaningfulPattern(
                        CompanionCompositePatternType.WEALTH_CONTEST
                )
                        && wealth.hasMeaningfulPattern(
                        WealthCompositePatternType
                                .COMPANION_PRESSURED_WEALTH
                ),
                false
        );

        /*
         * =================================================
         * 10. 재다신약
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineWealthHeavyWeakDayMaster(
                                wealth
                        );

        if (
                level != null
        ) {

            boolean weakDayMaster =
                    tenGodCorePatternRule
                            .weakSide(
                                    wealth.dayMasterStrengthLevel()
                            );

            List<TenGodCorePatternReasonType> reasons =
                    tenGodCorePatternRule
                            .buildReasons(
                                    TenGodCorePatternType
                                            .WEALTH_HEAVY_WEAK_DAY_MASTER,
                                    level,
                                    wealth.wealthFamilyPower(),
                                    0,
                                    wealthShare,
                                    0,
                                    wealth.wealthDeveloped(),
                                    false,
                                    directWealth.visible()
                                            || indirectWealth.visible(),
                                    false,
                                    directWealth.monthPresent()
                                            || indirectWealth.monthPresent(),
                                    false,
                                    false,
                                    false,
                                    false,
                                    weakDayMaster,
                                    wealth.hasMeaningfulPattern(
                                            WealthCompositePatternType
                                                    .WEAK_DAY_MASTER_WEALTH_BURDEN
                                    )
                            );

            signals.add(
                    new TenGodCorePatternSignal(
                            TenGodCorePatternType
                                    .WEALTH_HEAVY_WEAK_DAY_MASTER,
                            level,
                            presentTypes(
                                    directWealth,
                                    indirectWealth
                            ),
                            wealth.wealthFamilyPower(),
                            0,
                            wealthShare,
                            0,
                            directWealth.visible()
                                    || indirectWealth.visible(),
                            false,
                            directWealth.monthPresent()
                                    || indirectWealth.monthPresent(),
                            false,
                            false,
                            false,
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 11. 관살혼재
         * =================================================
         */

        level =
                tenGodCorePatternRule
                        .determineMixedOfficerKillings(
                                officer
                        );

        if (
                level != null
        ) {

            List<TenGodCorePatternReasonType> reasons =
                    tenGodCorePatternRule
                            .buildReasons(
                                    TenGodCorePatternType
                                            .MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS,
                                    level,
                                    directOfficer.allocatedPower(),
                                    sevenKillings.allocatedPower(),
                                    directOfficer.shareBasisPoints(),
                                    sevenKillings.shareBasisPoints(),
                                    directOfficer.meaningful(),
                                    sevenKillings.meaningful(),
                                    directOfficer.visible(),
                                    sevenKillings.visible(),
                                    directOfficer.monthPresent(),
                                    sevenKillings.monthPresent(),
                                    false,
                                    false,
                                    false,
                                    false,
                                    officer.hasMeaningfulPattern(
                                            OfficerCompositePatternType
                                                    .MIXED_OFFICER_KILLINGS
                                    )
                            );

            signals.add(
                    new TenGodCorePatternSignal(
                            TenGodCorePatternType
                                    .MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS,
                            level,
                            List.of(
                                    StructureTenGodType.DIRECT_OFFICER,
                                    StructureTenGodType.SEVEN_KILLINGS
                            ),
                            directOfficer.allocatedPower(),
                            sevenKillings.allocatedPower(),
                            directOfficer.shareBasisPoints(),
                            sevenKillings.shareBasisPoints(),
                            directOfficer.visible(),
                            sevenKillings.visible(),
                            directOfficer.monthPresent(),
                            sevenKillings.monthPresent(),
                            false,
                            false,
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         */

        signals.sort(
                Comparator
                        .comparingInt(
                                (
                                        TenGodCorePatternSignal signal
                                ) ->
                                        signal.level()
                                                .rank()
                        )
                        .reversed()

                        .thenComparingInt(
                                signal ->
                                        signal.patternType()
                                                .ordinal()
                        )
        );

        return new TenGodCorePatternAnalysis(
                distribution.dayMaster(),
                familyChainAnalysis,
                signals,
                distribution.completeChart()
        );
    }

    /*
     * =================================================
     * 개별 십성 Pair Signal
     * =================================================
     */

    private void addIndividualPairSignal(

            List<TenGodCorePatternSignal> signals,

            TenGodCorePatternType patternType,

            TenGodCorePatternLevel level,

            TenGodPowerSummary primary,

            TenGodPowerSummary secondary,

            TenGodFamilyChainAnalysis chainAnalysis,

            DayMasterElementRole sourceRole,

            DayMasterElementRole targetRole,

            TenGodFamilyInteractionType interactionType,

            boolean crossAnalysisCorroborated

    ) {

        if (
                level == null
        ) {

            return;
        }

        boolean interactionPresent =
                chainAnalysis
                        .hasInteraction(
                                sourceRole,
                                targetRole,
                                interactionType
                        );

        boolean interactionMeaningful =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                sourceRole,
                                targetRole,
                                interactionType
                        );

        boolean interactionStrong =
                chainAnalysis
                        .hasStrongInteraction(
                                sourceRole,
                                targetRole,
                                interactionType
                        );

        List<TenGodCorePatternReasonType> reasons =
                tenGodCorePatternRule
                        .buildReasons(
                                patternType,
                                level,
                                primary.allocatedPower(),
                                secondary.allocatedPower(),
                                primary.shareBasisPoints(),
                                secondary.shareBasisPoints(),
                                primary.meaningful(),
                                secondary.meaningful(),
                                primary.visible(),
                                secondary.visible(),
                                primary.monthPresent(),
                                secondary.monthPresent(),
                                interactionPresent,
                                interactionMeaningful,
                                interactionStrong,
                                false,
                                crossAnalysisCorroborated
                        );

        signals.add(
                new TenGodCorePatternSignal(
                        patternType,
                        level,
                        List.of(
                                primary.tenGodType(),
                                secondary.tenGodType()
                        ),
                        primary.allocatedPower(),
                        secondary.allocatedPower(),
                        primary.shareBasisPoints(),
                        secondary.shareBasisPoints(),
                        primary.visible(),
                        secondary.visible(),
                        primary.monthPresent(),
                        secondary.monthPresent(),
                        interactionPresent,
                        interactionMeaningful,
                        reasons
                )
        );
    }

    /*
     * =================================================
     * Family 포함 Signal
     * =================================================
     */

    private void addFamilyToIndividualSignal(

            List<TenGodCorePatternSignal> signals,

            TenGodCorePatternType patternType,

            TenGodCorePatternLevel level,

            List<StructureTenGodType> involvedTypes,

            int primaryPower,

            int secondaryPower,

            int primaryShare,

            int secondaryShare,

            boolean primaryVisible,

            boolean secondaryVisible,

            boolean primaryMonth,

            boolean secondaryMonth,

            TenGodFamilyChainAnalysis chainAnalysis,

            DayMasterElementRole sourceRole,

            DayMasterElementRole targetRole,

            TenGodFamilyInteractionType interactionType,

            boolean primaryMeaningful,

            boolean crossAnalysisCorroborated,

            boolean weakDayMaster

    ) {

        if (
                level == null
        ) {

            return;
        }

        boolean interactionPresent =
                chainAnalysis
                        .hasInteraction(
                                sourceRole,
                                targetRole,
                                interactionType
                        );

        boolean interactionMeaningful =
                chainAnalysis
                        .hasMeaningfulInteraction(
                                sourceRole,
                                targetRole,
                                interactionType
                        );

        boolean interactionStrong =
                chainAnalysis
                        .hasStrongInteraction(
                                sourceRole,
                                targetRole,
                                interactionType
                        );

        boolean secondaryMeaningful =
                secondaryShare
                        >= TenGodCorePatternRule
                        .MEANINGFUL_FAMILY_SHARE_BASIS_POINTS;

        List<TenGodCorePatternReasonType> reasons =
                tenGodCorePatternRule
                        .buildReasons(
                                patternType,
                                level,
                                primaryPower,
                                secondaryPower,
                                primaryShare,
                                secondaryShare,
                                primaryMeaningful,
                                secondaryMeaningful,
                                primaryVisible,
                                secondaryVisible,
                                primaryMonth,
                                secondaryMonth,
                                interactionPresent,
                                interactionMeaningful,
                                interactionStrong,
                                weakDayMaster,
                                crossAnalysisCorroborated
                        );

        signals.add(
                new TenGodCorePatternSignal(
                        patternType,
                        level,
                        involvedTypes,
                        primaryPower,
                        secondaryPower,
                        primaryShare,
                        secondaryShare,
                        primaryVisible,
                        secondaryVisible,
                        primaryMonth,
                        secondaryMonth,
                        interactionPresent,
                        interactionMeaningful,
                        reasons
                )
        );
    }

    /*
     * =================================================
     * Summary
     * =================================================
     */

    private TenGodPowerSummary summary(

            TenGodDistributionAnalysis distribution,

            StructureTenGodType type

    ) {

        return distribution
                .summaryOf(
                        type
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "10-1 십성 Summary가 없습니다: "
                                                + type
                                )
                );
    }

    /*
     * =================================================
     * 실제 존재하는 십성만 목록화
     * =================================================
     */

    private List<StructureTenGodType> presentTypes(
            TenGodPowerSummary... summaries
    ) {

        List<StructureTenGodType> result =
                new ArrayList<>();

        for (
                TenGodPowerSummary summary
                : summaries
        ) {

            if (
                    summary != null
                            && summary.present()
                            && !result.contains(
                            summary.tenGodType()
                    )
            ) {

                result.add(
                        summary.tenGodType()
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Family share
     * =================================================
     */

    private int share(

            int power,

            int totalPower

    ) {

        if (
                power <= 0
                        || totalPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                10_000,
                (int) Math.round(
                        power
                                * 10_000.0
                                / totalPower
                )
        );
    }
}