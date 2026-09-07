package com.dajeom.calculation;

import com.dajeom.domain.EatingGodPatternRelationContext;
import com.dajeom.domain.HurtingOfficerPatternRelationContext;
import com.dajeom.domain.PatternRelationContextBundle;
import com.dajeom.domain.PatternTenGodFoundationProfile;
import com.dajeom.domain.PatternTenGodRouteAnalysis;
import com.dajeom.domain.PatternTenGodRouteType;
import com.dajeom.domain.ResourcePatternRelationContext;
import com.dajeom.domain.SevenKillingsPatternRelationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * raw 십성 route와 foundation을 결합해
 * RelationContext의 routeUsable 계열을 자동 생성한다.
 *
 * =================================================
 *
 * 단순 route 존재만으로 usable=true가 아니다.
 *
 * 예:
 *
 * 食神 → 七殺
 *
 * 이 있어도 食神 foundation이 확인되지 않으면
 * eatingGodControlRouteUsable을 올리지 않는다.
 *
 * =================================================
 */
@Component
public class PatternTenGodRouteRelationContextCalculator {

    public PatternRelationContextBundle apply(

            PatternTenGodRouteAnalysis routes,

            PatternTenGodFoundationProfile foundations,

            PatternRelationContextBundle base

    ) {

        Objects.requireNonNull(
                routes,
                "PatternTenGodRouteAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                foundations,
                "PatternTenGodFoundationProfile은 필수입니다."
        );

        Objects.requireNonNull(
                base,
                "PatternRelationContextBundle은 필수입니다."
        );

        if (
                routes.dayMaster()
                        != foundations.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "RouteAnalysis와 FoundationProfile의 일간이 일치하지 않습니다."
            );
        }

        return new PatternRelationContextBundle(

                base.directOfficer(),

                base.wealth(),

                enrichResource(
                        base.resource(),
                        routes,
                        foundations
                ),

                enrichEatingGod(
                        base.eatingGod(),
                        routes,
                        foundations
                ),

                enrichHurtingOfficer(
                        base.hurtingOfficer(),
                        routes,
                        foundations
                ),

                enrichSevenKillings(
                        base.sevenKillings(),
                        routes,
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

            PatternTenGodRouteAnalysis routes,

            PatternTenGodFoundationProfile foundations

    ) {

        boolean resourceStable =
                base.resourceFoundationStable()
                        ||
                        foundations.resourceFoundationStable();

        boolean officerResourceChainUsable =
                base.officerResourceChainUsable()
                        ||
                        (
                                resourceStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .RESOURCE_PATTERN_OFFICER_RESOURCE_DAY_MASTER
                                        )
                        );

        boolean killingResourceChainUsable =
                base.killingResourceChainUsable()
                        ||
                        (
                                resourceStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .RESOURCE_PATTERN_KILLING_RESOURCE_DAY_MASTER
                                        )
                        );

        return new ResourcePatternRelationContext(

                resourceStable,

                officerResourceChainUsable,

                killingResourceChainUsable,

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

            PatternTenGodRouteAnalysis routes,

            PatternTenGodFoundationProfile foundations

    ) {

        boolean eatingGodStable =
                base.eatingGodFoundationStable()
                        ||
                        foundations.eatingGodFoundationStable();

        boolean wealthStable =
                base.wealthFoundationStable()
                        ||
                        foundations.wealthFoundationStable();

        boolean indirectResourceSuppression =
                base.indirectResourceSuppressionActive()
                        ||
                        (
                                eatingGodStable
                                        &&
                                        foundations.resourceFoundationStable()
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .EATING_GOD_PATTERN_INDIRECT_RESOURCE_CONTROLS_EATING_GOD
                                        )
                        );

        boolean wealthProtection =
                base.wealthProtectionRouteUsable()
                        ||
                        (
                                indirectResourceSuppression
                                        &&
                                        wealthStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .EATING_GOD_PATTERN_WEALTH_PROTECTS_EATING_GOD
                                        )
                        );

        /*
         * 棄食就殺而透印은
         * 단순 殺印 route만으로 확정하지 않는다.
         *
         * "棄食"이라는 격국 선택이 추가로 필요하므로
         * 기존 값을 보존한다.
         */

        return new EatingGodPatternRelationContext(

                eatingGodStable,

                wealthStable,

                indirectResourceSuppression,

                wealthProtection,

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

            PatternTenGodRouteAnalysis routes,

            PatternTenGodFoundationProfile foundations

    ) {

        boolean hurtingOfficerStable =
                base.hurtingOfficerFoundationStable()
                        ||
                        foundations.hurtingOfficerFoundationStable();

        boolean wealthStable =
                base.wealthFoundationStable()
                        ||
                        foundations.wealthFoundationStable();

        boolean resourceStable =
                base.resourceFoundationStable()
                        ||
                        foundations.resourceFoundationStable();

        boolean wealthGenerationRoute =
                base.wealthGenerationRouteUsable()
                        ||
                        (
                                hurtingOfficerStable
                                        &&
                                        wealthStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .HURTING_OFFICER_PATTERN_GENERATES_WEALTH
                                        )
                        );

        boolean resourceControlRoute =
                base.resourceControlRouteUsable()
                        ||
                        (
                                hurtingOfficerStable
                                        &&
                                        resourceStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .HURTING_OFFICER_PATTERN_RESOURCE_CONTROLS_HURTING_OFFICER
                                        )
                        );

        boolean killingResourceChain =
                base.killingResourceChainUsable()
                        ||
                        (
                                resourceStable
                                        &&
                                        foundations
                                                .sevenKillingsFoundationStable()
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .VISIBLE_KILLING_RESOURCE_DAY_MASTER
                                        )
                        );

        boolean hurtingOfficerKillingWithoutWealth =
                base.hurtingOfficerKillingRouteUsableWithoutWealth()
                        ||
                        (
                                hurtingOfficerStable
                                        &&
                                        foundations
                                                .sevenKillingsFoundationStable()
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .HURTING_OFFICER_PATTERN_CONTROLS_SEVEN_KILLINGS
                                        )
                        );

        boolean directOfficerConflict =
                base.directOfficerConflictActive()
                        ||
                        (
                                hurtingOfficerStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .HURTING_OFFICER_PATTERN_CONTROLS_DIRECT_OFFICER
                                        )
                        );

        boolean wealthBridge =
                base.officerConflictWealthBridgeUsable()
                        ||
                        (
                                directOfficerConflict
                                        &&
                                        wealthStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .HURTING_OFFICER_PATTERN_WEALTH_BRIDGES_DIRECT_OFFICER
                                        )
                        );

        return new HurtingOfficerPatternRelationContext(

                hurtingOfficerStable,

                wealthStable,

                wealthGenerationRoute,

                resourceStable,

                resourceControlRoute,

                base.hurtingOfficerTooLightForResourceRoute(),

                killingResourceChain,

                hurtingOfficerKillingWithoutWealth,

                directOfficerConflict,

                wealthBridge,

                /*
                 * 金水傷官은 조후 판정 필요.
                 */
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

            PatternTenGodRouteAnalysis routes,

            PatternTenGodFoundationProfile foundations

    ) {

        boolean killingStable =
                base.sevenKillingsFoundationStable()
                        ||
                        foundations
                                .sevenKillingsFoundationStable();

        boolean eatingGodStable =
                base.eatingGodFoundationStable()
                        ||
                        foundations
                                .eatingGodFoundationStable();

        boolean resourceStable =
                base.resourceFoundationStable()
                        ||
                        foundations
                                .resourceFoundationStable();

        boolean wealthStable =
                foundations
                        .wealthFoundationStable();

        boolean eatingGodControl =
                base.eatingGodControlRouteUsable()
                        ||
                        (
                                killingStable
                                        &&
                                        eatingGodStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .SEVEN_KILLINGS_PATTERN_EATING_GOD_CONTROLS_KILLING
                                        )
                        );

        boolean killingResource =
                base.killingResourceChainUsable()
                        ||
                        (
                                killingStable
                                        &&
                                        resourceStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .SEVEN_KILLINGS_PATTERN_KILLING_RESOURCE_DAY_MASTER
                                        )
                        );

        boolean resourceSuppression =
                base.resourceSuppressionActive()
                        ||
                        (
                                eatingGodControl
                                        &&
                                        resourceStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .SEVEN_KILLINGS_PATTERN_RESOURCE_SUPPRESSES_EATING_GOD
                                        )
                        );

        boolean wealthRemoval =
                base.wealthRemovalRouteUsable()
                        ||
                        (
                                resourceSuppression
                                        &&
                                        wealthStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .SEVEN_KILLINGS_PATTERN_WEALTH_REMOVES_RESOURCE_PRESERVING_EATING_GOD
                                        )
                        );

        boolean wealthStrengthensKilling =
                base.wealthStrengtheningRouteActive()
                        ||
                        (
                                killingStable
                                        &&
                                        wealthStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .SEVEN_KILLINGS_PATTERN_WEALTH_GENERATES_KILLING
                                        )
                        );

        boolean wealthKillingResourceChain =
                base.wealthKillingResourceChainUsable()
                        ||
                        (
                                killingResource
                                        &&
                                        wealthStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .SEVEN_KILLINGS_PATTERN_WEALTH_KILLING_RESOURCE_DAY_MASTER
                                        )
                        );

        boolean wealthDamagesResourceRoute =
                base.wealthDamageResourceRouteActive()
                        ||
                        (
                                killingResource
                                        &&
                                        wealthStable
                                        &&
                                        routes.hasRoute(
                                                PatternTenGodRouteType
                                                        .SEVEN_KILLINGS_PATTERN_WEALTH_DAMAGES_KILLING_RESOURCE
                                        )
                        );

        return new SevenKillingsPatternRelationContext(

                killingStable,

                eatingGodStable,

                eatingGodControl,

                resourceStable,

                killingResource,

                resourceSuppression,

                wealthRemoval,

                wealthKillingResourceChain,

                wealthStrengthensKilling,

                wealthDamagesResourceRoute,

                base.officerKillingsMixtureCleared(),

                base.sevenKillingsStemCombined(),

                base.monthCommandDisrupted(),

                base.monthCommandDisruptionResolved()
        );
    }
}