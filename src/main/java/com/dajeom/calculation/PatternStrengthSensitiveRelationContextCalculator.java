package com.dajeom.calculation;

import com.dajeom.domain.HurtingOfficerPatternRelationContext;
import com.dajeom.domain.PatternRelationContextBundle;
import com.dajeom.domain.PatternStrengthSensitiveContextAnalysis;
import com.dajeom.domain.ResourcePatternRelationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 신강/신약 민감형 Context를
 * 기존 격별 RelationContext에 반영한다.
 *
 * =================================================
 *
 * 印格:
 *
 * - excessiveResourceContext
 * - wealthModeratesExcessResource
 * - outputDrainWeakStructureActive
 *
 * =================================================
 *
 * 傷官格:
 *
 * - hurtingOfficerTooLightForResourceRoute
 */
@Component
public class PatternStrengthSensitiveRelationContextCalculator {

    public PatternRelationContextBundle apply(

            PatternStrengthSensitiveContextAnalysis analysis,

            PatternRelationContextBundle base

    ) {

        Objects.requireNonNull(
                analysis,
                "PatternStrengthSensitiveContextAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                base,
                "PatternRelationContextBundle은 필수입니다."
        );

        return new PatternRelationContextBundle(

                base.directOfficer(),

                base.wealth(),

                enrichResource(
                        base.resource(),
                        analysis
                ),

                base.eatingGod(),

                enrichHurtingOfficer(
                        base.hurtingOfficer(),
                        analysis
                ),

                base.sevenKillings()
        );
    }

    /*
     * =================================================
     * 印格
     * =================================================
     */

    private ResourcePatternRelationContext enrichResource(

            ResourcePatternRelationContext base,

            PatternStrengthSensitiveContextAnalysis analysis

    ) {

        return new ResourcePatternRelationContext(

                base.resourceFoundationStable(),

                base.officerResourceChainUsable(),

                base.killingResourceChainUsable(),

                base.excessiveResourceContext()
                        ||
                        analysis.excessiveResourceContext(),

                base.mixedSevenKillingsClearedByCombination(),

                base.mixedOfficerKillingsControlled(),

                base.wealthResourceConflictActive(),

                base.wealthModeratesExcessResource()
                        ||
                        analysis.wealthModeratesExcessResource(),

                base.wealthCombinedAwayPreservingResource(),

                base.indirectResourceSuppressionActive(),

                base.outputDrainWeakStructureActive()
                        ||
                        analysis.outputDrainWeakStructureActive()
        );
    }

    /*
     * =================================================
     * 傷官格
     * =================================================
     */

    private HurtingOfficerPatternRelationContext enrichHurtingOfficer(

            HurtingOfficerPatternRelationContext base,

            PatternStrengthSensitiveContextAnalysis analysis

    ) {

        return new HurtingOfficerPatternRelationContext(

                base.hurtingOfficerFoundationStable(),

                base.wealthFoundationStable(),

                base.wealthGenerationRouteUsable(),

                base.resourceFoundationStable(),

                base.resourceControlRouteUsable(),

                base.hurtingOfficerTooLightForResourceRoute()
                        ||
                        analysis.hurtingOfficerTooLightForResourceRoute(),

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
}