package com.dajeom.calculation;

import com.dajeom.domain.EatingGodPatternRelationContext;
import com.dajeom.domain.HurtingOfficerPatternRelationContext;
import com.dajeom.domain.PatternRelationContextBundle;
import com.dajeom.domain.PatternTenGodFoundationProfile;
import com.dajeom.domain.ResourcePatternRelationContext;
import com.dajeom.domain.SevenKillingsPatternRelationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 십성 foundation 분석을
 * 기존 격별 RelationContext에 반영한다.
 *
 * =================================================
 *
 * 자동화되는 필드:
 *
 * 印格:
 * - resourceFoundationStable
 *
 * 食神格:
 * - eatingGodFoundationStable
 * - wealthFoundationStable
 *
 * 傷官格:
 * - hurtingOfficerFoundationStable
 * - wealthFoundationStable
 * - resourceFoundationStable
 *
 * 七殺格:
 * - sevenKillingsFoundationStable
 * - eatingGodFoundationStable
 * - resourceFoundationStable
 *
 * =================================================
 *
 * 기존 true 값은 false로 덮지 않는다.
 */
@Component
public class PatternTenGodFoundationRelationContextCalculator {

    public PatternRelationContextBundle apply(

            PatternTenGodFoundationProfile foundations,

            PatternRelationContextBundle base

    ) {

        Objects.requireNonNull(
                foundations,
                "PatternTenGodFoundationProfile은 필수입니다."
        );

        Objects.requireNonNull(
                base,
                "PatternRelationContextBundle은 필수입니다."
        );

        return new PatternRelationContextBundle(

                /*
                 * 正官格 StructuralSignals에는
                 * 현재 별도 officerFoundationStable 필드가 없다.
                 */
                base.directOfficer(),

                /*
                 * 財格 StructuralSignals에도
                 * 별도 wealthFoundationStable 필드가 없다.
                 */
                base.wealth(),

                enrichResource(
                        base.resource(),
                        foundations
                ),

                enrichEatingGod(
                        base.eatingGod(),
                        foundations
                ),

                enrichHurtingOfficer(
                        base.hurtingOfficer(),
                        foundations
                ),

                enrichSevenKillings(
                        base.sevenKillings(),
                        foundations
                )
        );
    }

    /*
     * =================================================
     * 印格
     * =================================================
     */

    private ResourcePatternRelationContext enrichResource(

            ResourcePatternRelationContext base,

            PatternTenGodFoundationProfile foundations

    ) {

        return new ResourcePatternRelationContext(

                base.resourceFoundationStable()
                        ||
                        foundations.resourceFoundationStable(),

                base.officerResourceChainUsable(),

                base.killingResourceChainUsable(),

                base.excessiveResourceContext(),

                base.mixedSevenKillingsClearedByCombination(),

                base.mixedOfficerKillingsControlled(),

                base.wealthResourceConflictActive(),

                base.wealthModeratesExcessResource(),

                base.wealthCombinedAwayPreservingResource(),

                base.indirectResourceSuppressionActive(),

                base.outputDrainWeakStructureActive()
        );
    }

    /*
     * =================================================
     * 食神格
     * =================================================
     */

    private EatingGodPatternRelationContext enrichEatingGod(

            EatingGodPatternRelationContext base,

            PatternTenGodFoundationProfile foundations

    ) {

        return new EatingGodPatternRelationContext(

                base.eatingGodFoundationStable()
                        ||
                        foundations.eatingGodFoundationStable(),

                base.wealthFoundationStable()
                        ||
                        foundations.wealthFoundationStable(),

                base.indirectResourceSuppressionActive(),

                base.wealthProtectionRouteUsable(),

                base.abandoningEatingGodForKillingWithResourceUsable(),

                base.eatingGodCombinesKillingPreservingWealth(),

                base.eatingGodSeparatesWealthAndKilling(),

                base.wealthDamageKillingResourceRouteActive(),

                base.officerKillingsMixtureUsable()
        );
    }

    /*
     * =================================================
     * 傷官格
     * =================================================
     */

    private HurtingOfficerPatternRelationContext enrichHurtingOfficer(

            HurtingOfficerPatternRelationContext base,

            PatternTenGodFoundationProfile foundations

    ) {

        return new HurtingOfficerPatternRelationContext(

                base.hurtingOfficerFoundationStable()
                        ||
                        foundations.hurtingOfficerFoundationStable(),

                base.wealthFoundationStable()
                        ||
                        foundations.wealthFoundationStable(),

                base.wealthGenerationRouteUsable(),

                base.resourceFoundationStable()
                        ||
                        foundations.resourceFoundationStable(),

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

                base.sevenKillingsCombinedAway(),

                base.wealthDamageKillingResourceRouteActive(),

                base.officerKillingsMixtureCleared()
        );
    }

    /*
     * =================================================
     * 七殺格
     * =================================================
     */

    private SevenKillingsPatternRelationContext enrichSevenKillings(

            SevenKillingsPatternRelationContext base,

            PatternTenGodFoundationProfile foundations

    ) {

        return new SevenKillingsPatternRelationContext(

                base.sevenKillingsFoundationStable()
                        ||
                        foundations.sevenKillingsFoundationStable(),

                base.eatingGodFoundationStable()
                        ||
                        foundations.eatingGodFoundationStable(),

                base.eatingGodControlRouteUsable(),

                base.resourceFoundationStable()
                        ||
                        foundations.resourceFoundationStable(),

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
        );
    }
}