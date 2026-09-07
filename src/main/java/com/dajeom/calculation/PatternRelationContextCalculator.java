package com.dajeom.calculation;

import com.dajeom.domain.DirectOfficerPatternRelationContext;
import com.dajeom.domain.EatingGodPatternRelationContext;
import com.dajeom.domain.HurtingOfficerPatternRelationContext;
import com.dajeom.domain.PatternBranchDisruptionAnalysis;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternMonthCommandDisruptionResolutionAnalysis;
import com.dajeom.domain.PatternRelationContextBundle;
import com.dajeom.domain.PatternStemCombinationContext;
import com.dajeom.domain.PatternStemCombinationPreservationAnalysis;
import com.dajeom.domain.ResourcePatternRelationContext;
import com.dajeom.domain.SevenKillingsPatternRelationContext;
import com.dajeom.domain.WealthPatternRelationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 격별 RelationContext를 자동 조립하는 통합 Calculator.
 *
 * =================================================
 *
 * 현재 자동 반영:
 *
 * 1. 天干五合 raw 관계
 *
 * 2. 月令 沖 / 刑 / 破 / 害
 *
 * 3. 月令 교란의 회합 기반 해소
 *
 * 4. active 天干合의 격국 보존/정리 효과
 *
 *    - 合殺留官
 *    - 合官留殺
 *    - 合殺存財
 *    - 合財存印
 *    - 食神合殺存財
 *
 * =================================================
 *
 * 아직 별도 판정:
 *
 * - 合化
 * - 財印 위치 충돌
 * - 통근 안정성
 * - 조후
 */
@Component
public class PatternRelationContextCalculator {

    public PatternRelationContextBundle applyStemCombinations(

            PatternInteractionAnalysis interaction,

            PatternStemCombinationContext stemCombinationContext,

            PatternRelationContextBundle base

    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                stemCombinationContext,
                "PatternStemCombinationContext는 필수입니다."
        );

        Objects.requireNonNull(
                base,
                "기존 PatternRelationContextBundle은 필수입니다."
        );

        validateStemDayMaster(
                interaction,
                stemCombinationContext
        );

        return new PatternRelationContextBundle(

                enrichDirectOfficerWithStem(
                        base.directOfficer(),
                        stemCombinationContext
                ),

                enrichWealthWithStem(
                        base.wealth(),
                        stemCombinationContext
                ),

                base.resource(),

                base.eatingGod(),

                enrichHurtingOfficerWithStem(
                        base.hurtingOfficer(),
                        stemCombinationContext
                ),

                enrichSevenKillingsWithStem(
                        base.sevenKillings(),
                        stemCombinationContext
                )
        );
    }

    public PatternRelationContextBundle applyBranchDisruptions(

            PatternInteractionAnalysis interaction,

            PatternBranchDisruptionAnalysis branchDisruptionAnalysis,

            PatternRelationContextBundle base

    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                branchDisruptionAnalysis,
                "PatternBranchDisruptionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                base,
                "기존 PatternRelationContextBundle은 필수입니다."
        );

        validateMonthBranch(
                interaction,
                branchDisruptionAnalysis
        );

        return new PatternRelationContextBundle(

                enrichDirectOfficerWithBranch(
                        base.directOfficer(),
                        branchDisruptionAnalysis
                ),

                base.wealth(),

                base.resource(),

                base.eatingGod(),

                base.hurtingOfficer(),

                enrichSevenKillingsWithBranch(
                        base.sevenKillings(),
                        branchDisruptionAnalysis
                )
        );
    }

    public PatternRelationContextBundle
    applyMonthCommandDisruptionResolution(

            PatternInteractionAnalysis interaction,

            PatternMonthCommandDisruptionResolutionAnalysis resolution,

            PatternRelationContextBundle base

    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                resolution,
                "월령 교란 해소 분석은 필수입니다."
        );

        Objects.requireNonNull(
                base,
                "기존 PatternRelationContextBundle은 필수입니다."
        );

        if (
                interaction
                        .patternSelection()
                        .monthBranch()

                        !=

                        resolution.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 월령 해소 분석의 월지가 일치하지 않습니다."
            );
        }

        boolean resolvedByCombination =
                resolution
                        .usableAsResolvedByCombination();

        return new PatternRelationContextBundle(

                enrichDirectOfficerWithResolution(
                        base.directOfficer(),
                        resolvedByCombination
                ),

                base.wealth(),

                base.resource(),

                base.eatingGod(),

                base.hurtingOfficer(),

                enrichSevenKillingsWithResolution(
                        base.sevenKillings(),
                        resolvedByCombination
                )
        );
    }

    /**
     * active 天干合이
     * 실제 무엇을 묶고 무엇을 남겼는지를
     * 격별 RelationContext에 반영한다.
     */
    public PatternRelationContextBundle
    applyStemCombinationPreservation(

            PatternInteractionAnalysis interaction,

            PatternStemCombinationPreservationAnalysis preservation,

            PatternRelationContextBundle base

    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                preservation,
                "PatternStemCombinationPreservationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                base,
                "기존 PatternRelationContextBundle은 필수입니다."
        );

        if (
                interaction.dayMaster()
                        != preservation.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 PreservationAnalysis의 일간이 일치하지 않습니다."
            );
        }

        return new PatternRelationContextBundle(

                enrichDirectOfficerWithPreservation(
                        base.directOfficer(),
                        preservation
                ),

                enrichWealthWithPreservation(
                        base.wealth(),
                        preservation
                ),

                enrichResourceWithPreservation(
                        base.resource(),
                        preservation
                ),

                enrichEatingGodWithPreservation(
                        base.eatingGod(),
                        preservation
                ),

                enrichHurtingOfficerWithPreservation(
                        base.hurtingOfficer(),
                        preservation
                ),

                enrichSevenKillingsWithPreservation(
                        base.sevenKillings(),
                        preservation
                )
        );
    }

    /*
     * =================================================
     * raw 天干五合
     * =================================================
     */

    private DirectOfficerPatternRelationContext
    enrichDirectOfficerWithStem(

            DirectOfficerPatternRelationContext base,

            PatternStemCombinationContext stemContext

    ) {

        return new DirectOfficerPatternRelationContext(

                base.officerStemCombined()
                        ||
                        stemContext.directOfficerCombined(),

                base.monthCommandClashed(),

                base.monthCommandPunished(),

                base.monthCommandBroken(),

                base.monthCommandHarmed(),

                base.resourcePlacementSupportsHurtingOfficerResolution(),

                base.mixedSevenKillingsClearedByCombination(),

                base.monthCommandDisruptionResolvedByCombination(),

                base.wealthAndResourceMutuallyNonObstructing()
        );
    }

    private WealthPatternRelationContext
    enrichWealthWithStem(

            WealthPatternRelationContext base,

            PatternStemCombinationContext stemContext

    ) {

        return new WealthPatternRelationContext(

                base.wealthAndResourceMutuallyNonObstructing(),

                base.sevenKillingsCombinedAwayPreservingWealth(),

                base.generatedOfficerCombined()
                        ||
                        stemContext.directOfficerCombined()
        );
    }

    private HurtingOfficerPatternRelationContext
    enrichHurtingOfficerWithStem(

            HurtingOfficerPatternRelationContext base,

            PatternStemCombinationContext stemContext

    ) {

        return new HurtingOfficerPatternRelationContext(

                base.hurtingOfficerFoundationStable(),

                base.wealthFoundationStable(),

                base.wealthGenerationRouteUsable(),

                base.resourceFoundationStable(),

                base.resourceControlRouteUsable(),

                base.hurtingOfficerTooLightForResourceRoute(),

                base.killingResourceChainUsable(),

                base.hurtingOfficerKillingRouteUsableWithoutWealth(),

                base.directOfficerConflictActive(),

                base.officerConflictWealthBridgeUsable(),

                base.metalWaterOfficerRouteUsable(),

                base.wealthCombined()
                        ||
                        stemContext.wealthCombined(),

                base.resourceDamaged(),

                base.wealthKillingRouteDamaging(),

                base.sevenKillingsCombinedAway(),

                base.wealthDamageKillingResourceRouteActive(),

                base.officerKillingsMixtureCleared()
        );
    }

    private SevenKillingsPatternRelationContext
    enrichSevenKillingsWithStem(

            SevenKillingsPatternRelationContext base,

            PatternStemCombinationContext stemContext

    ) {

        return new SevenKillingsPatternRelationContext(

                base.sevenKillingsFoundationStable(),

                base.eatingGodFoundationStable(),

                base.eatingGodControlRouteUsable(),

                base.resourceFoundationStable(),

                base.killingResourceChainUsable(),

                base.resourceSuppressionActive(),

                base.wealthRemovalRouteUsable(),

                base.wealthKillingResourceChainUsable(),

                base.wealthStrengtheningRouteActive(),

                base.wealthDamageResourceRouteActive(),

                base.officerKillingsMixtureCleared(),

                base.sevenKillingsStemCombined()
                        ||
                        stemContext.sevenKillingsCombined(),

                base.monthCommandDisrupted(),

                base.monthCommandDisruptionResolved()
        );
    }

    /*
     * =================================================
     * 地支 교란
     * =================================================
     */

    private DirectOfficerPatternRelationContext
    enrichDirectOfficerWithBranch(

            DirectOfficerPatternRelationContext base,

            PatternBranchDisruptionAnalysis branchContext

    ) {

        return new DirectOfficerPatternRelationContext(

                base.officerStemCombined(),

                base.monthCommandClashed()
                        ||
                        branchContext.monthCommandClashed(),

                base.monthCommandPunished()
                        ||
                        branchContext.monthCommandPunished(),

                base.monthCommandBroken()
                        ||
                        branchContext.monthCommandBroken(),

                base.monthCommandHarmed()
                        ||
                        branchContext.monthCommandHarmed(),

                base.resourcePlacementSupportsHurtingOfficerResolution(),

                base.mixedSevenKillingsClearedByCombination(),

                base.monthCommandDisruptionResolvedByCombination(),

                base.wealthAndResourceMutuallyNonObstructing()
        );
    }

    private SevenKillingsPatternRelationContext
    enrichSevenKillingsWithBranch(

            SevenKillingsPatternRelationContext base,

            PatternBranchDisruptionAnalysis branchContext

    ) {

        return new SevenKillingsPatternRelationContext(

                base.sevenKillingsFoundationStable(),

                base.eatingGodFoundationStable(),

                base.eatingGodControlRouteUsable(),

                base.resourceFoundationStable(),

                base.killingResourceChainUsable(),

                base.resourceSuppressionActive(),

                base.wealthRemovalRouteUsable(),

                base.wealthKillingResourceChainUsable(),

                base.wealthStrengtheningRouteActive(),

                base.wealthDamageResourceRouteActive(),

                base.officerKillingsMixtureCleared(),

                base.sevenKillingsStemCombined(),

                base.monthCommandDisrupted()
                        ||
                        branchContext.monthCommandDisrupted(),

                base.monthCommandDisruptionResolved()
        );
    }

    /*
     * =================================================
     * 月令 교란 해소
     * =================================================
     */

    private DirectOfficerPatternRelationContext
    enrichDirectOfficerWithResolution(

            DirectOfficerPatternRelationContext base,

            boolean resolvedByCombination

    ) {

        return new DirectOfficerPatternRelationContext(

                base.officerStemCombined(),

                base.monthCommandClashed(),

                base.monthCommandPunished(),

                base.monthCommandBroken(),

                base.monthCommandHarmed(),

                base.resourcePlacementSupportsHurtingOfficerResolution(),

                base.mixedSevenKillingsClearedByCombination(),

                base.monthCommandDisruptionResolvedByCombination()
                        ||
                        resolvedByCombination,

                base.wealthAndResourceMutuallyNonObstructing()
        );
    }

    private SevenKillingsPatternRelationContext
    enrichSevenKillingsWithResolution(

            SevenKillingsPatternRelationContext base,

            boolean resolvedByCombination

    ) {

        return new SevenKillingsPatternRelationContext(

                base.sevenKillingsFoundationStable(),

                base.eatingGodFoundationStable(),

                base.eatingGodControlRouteUsable(),

                base.resourceFoundationStable(),

                base.killingResourceChainUsable(),

                base.resourceSuppressionActive(),

                base.wealthRemovalRouteUsable(),

                base.wealthKillingResourceChainUsable(),

                base.wealthStrengtheningRouteActive(),

                base.wealthDamageResourceRouteActive(),

                base.officerKillingsMixtureCleared(),

                base.sevenKillingsStemCombined(),

                base.monthCommandDisrupted(),

                base.monthCommandDisruptionResolved()
                        ||
                        resolvedByCombination
        );
    }

    /*
     * =================================================
     * 天干合 Preservation
     * =================================================
     */

    private DirectOfficerPatternRelationContext
    enrichDirectOfficerWithPreservation(

            DirectOfficerPatternRelationContext base,

            PatternStemCombinationPreservationAnalysis preservation

    ) {

        boolean mixedKillingsCleared =
                base.mixedSevenKillingsClearedByCombination()
                        ||
                        preservation
                                .sevenKillingsClearedLeavingDirectOfficer();

        return new DirectOfficerPatternRelationContext(

                base.officerStemCombined(),

                base.monthCommandClashed(),

                base.monthCommandPunished(),

                base.monthCommandBroken(),

                base.monthCommandHarmed(),

                base.resourcePlacementSupportsHurtingOfficerResolution(),

                mixedKillingsCleared,

                base.monthCommandDisruptionResolvedByCombination(),

                base.wealthAndResourceMutuallyNonObstructing()
        );
    }

    private WealthPatternRelationContext
    enrichWealthWithPreservation(

            WealthPatternRelationContext base,

            PatternStemCombinationPreservationAnalysis preservation

    ) {

        return new WealthPatternRelationContext(

                base.wealthAndResourceMutuallyNonObstructing(),

                base.sevenKillingsCombinedAwayPreservingWealth()
                        ||
                        preservation
                                .sevenKillingsClearedPreservingWealth(),

                base.generatedOfficerCombined()
        );
    }

    private ResourcePatternRelationContext
    enrichResourceWithPreservation(

            ResourcePatternRelationContext base,

            PatternStemCombinationPreservationAnalysis preservation

    ) {

        return new ResourcePatternRelationContext(

                base.resourceFoundationStable(),

                base.officerResourceChainUsable(),

                base.killingResourceChainUsable(),

                base.excessiveResourceContext(),

                base.mixedSevenKillingsClearedByCombination()
                        ||
                        preservation
                                .sevenKillingsClearedLeavingDirectOfficer(),

                base.mixedOfficerKillingsControlled()
                        ||
                        preservation
                                .officerKillingsMixtureCleared(),

                base.wealthResourceConflictActive(),

                base.wealthModeratesExcessResource(),

                base.wealthCombinedAwayPreservingResource()
                        ||
                        preservation
                                .wealthClearedPreservingResource(),

                base.indirectResourceSuppressionActive(),

                base.outputDrainWeakStructureActive()
        );
    }

    private EatingGodPatternRelationContext
    enrichEatingGodWithPreservation(

            EatingGodPatternRelationContext base,

            PatternStemCombinationPreservationAnalysis preservation

    ) {

        return new EatingGodPatternRelationContext(

                base.eatingGodFoundationStable(),

                base.wealthFoundationStable(),

                base.indirectResourceSuppressionActive(),

                base.wealthProtectionRouteUsable(),

                base.abandoningEatingGodForKillingWithResourceUsable(),

                base.eatingGodCombinesKillingPreservingWealth()
                        ||
                        preservation
                                .eatingGodCombinesSevenKillingsPreservingWealth(),

                base.eatingGodSeparatesWealthAndKilling(),

                base.wealthDamageKillingResourceRouteActive(),

                base.officerKillingsMixtureUsable()
                        ||
                        preservation
                                .officerKillingsMixtureCleared()
        );
    }

    private HurtingOfficerPatternRelationContext
    enrichHurtingOfficerWithPreservation(

            HurtingOfficerPatternRelationContext base,

            PatternStemCombinationPreservationAnalysis preservation

    ) {

        return new HurtingOfficerPatternRelationContext(

                base.hurtingOfficerFoundationStable(),

                base.wealthFoundationStable(),

                base.wealthGenerationRouteUsable(),

                base.resourceFoundationStable(),

                base.resourceControlRouteUsable(),

                base.hurtingOfficerTooLightForResourceRoute(),

                base.killingResourceChainUsable(),

                base.hurtingOfficerKillingRouteUsableWithoutWealth(),

                base.directOfficerConflictActive(),

                base.officerConflictWealthBridgeUsable(),

                base.metalWaterOfficerRouteUsable(),

                base.wealthCombined(),

                base.resourceDamaged(),

                base.wealthKillingRouteDamaging(),

                base.sevenKillingsCombinedAway()
                        ||
                        preservation
                                .sevenKillingsFullyBound(),

                base.wealthDamageKillingResourceRouteActive(),

                base.officerKillingsMixtureCleared()
                        ||
                        preservation
                                .officerKillingsMixtureCleared()
        );
    }

    private SevenKillingsPatternRelationContext
    enrichSevenKillingsWithPreservation(

            SevenKillingsPatternRelationContext base,

            PatternStemCombinationPreservationAnalysis preservation

    ) {

        return new SevenKillingsPatternRelationContext(

                base.sevenKillingsFoundationStable(),

                base.eatingGodFoundationStable(),

                base.eatingGodControlRouteUsable(),

                base.resourceFoundationStable(),

                base.killingResourceChainUsable(),

                base.resourceSuppressionActive(),

                base.wealthRemovalRouteUsable(),

                base.wealthKillingResourceChainUsable(),

                base.wealthStrengtheningRouteActive(),

                base.wealthDamageResourceRouteActive(),

                base.officerKillingsMixtureCleared()
                        ||
                        preservation
                                .officerKillingsMixtureCleared(),

                base.sevenKillingsStemCombined(),

                base.monthCommandDisrupted(),

                base.monthCommandDisruptionResolved()
        );
    }

    /*
     * =================================================
     * Validation
     * =================================================
     */

    private void validateStemDayMaster(

            PatternInteractionAnalysis interaction,

            PatternStemCombinationContext stemCombinationContext

    ) {

        if (
                interaction.dayMaster()
                        != stemCombinationContext.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 "
                            +
                            "PatternStemCombinationContext의 일간이 일치하지 않습니다."
            );
        }
    }

    private void validateMonthBranch(

            PatternInteractionAnalysis interaction,

            PatternBranchDisruptionAnalysis branchDisruptionAnalysis

    ) {

        if (
                interaction
                        .patternSelection()
                        .monthBranch()

                        !=

                        branchDisruptionAnalysis
                                .monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis의 월령과 "
                            +
                            "PatternBranchDisruptionAnalysis의 월령이 일치하지 않습니다."
            );
        }
    }
}