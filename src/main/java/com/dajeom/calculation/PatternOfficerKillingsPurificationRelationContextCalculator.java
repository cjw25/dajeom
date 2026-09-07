package com.dajeom.calculation;

import com.dajeom.domain.EatingGodPatternRelationContext;
import com.dajeom.domain.HurtingOfficerPatternRelationContext;
import com.dajeom.domain.PatternOfficerKillingsPurificationAnalysis;
import com.dajeom.domain.PatternRelationContextBundle;
import com.dajeom.domain.ResourcePatternRelationContext;
import com.dajeom.domain.SevenKillingsPatternRelationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 官殺 制化取清 분석을
 * 기존 격별 RelationContext에 반영한다.
 *
 * =================================================
 *
 * 특히:
 *
 * ResourcePatternRelationContext의
 * mixedOfficerKillingsControlled는
 * 이 Calculator가 control 기반 판정의
 * authoritative source다.
 */
@Component
public class PatternOfficerKillingsPurificationRelationContextCalculator {

    public PatternRelationContextBundle apply(

            PatternOfficerKillingsPurificationAnalysis analysis,

            PatternRelationContextBundle base

    ) {

        Objects.requireNonNull(
                analysis,
                "PatternOfficerKillingsPurificationAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                base,
                "PatternRelationContextBundle은 필수입니다."
        );

        boolean cleared =
                analysis.mixtureCleared();

        return new PatternRelationContextBundle(

                /*
                 * 正官格의
                 * mixedSevenKillingsClearedByCombination은
                 * 이름 그대로 合殺 전용이므로
                 * 食神制殺 결과를 섞지 않는다.
                 */
                base.directOfficer(),

                base.wealth(),

                enrichResource(
                        base.resource(),
                        cleared
                ),

                enrichEatingGod(
                        base.eatingGod(),
                        cleared
                ),

                enrichHurtingOfficer(
                        base.hurtingOfficer(),
                        cleared
                ),

                enrichSevenKillings(
                        base.sevenKillings(),
                        cleared
                )
        );
    }

    private ResourcePatternRelationContext enrichResource(

            ResourcePatternRelationContext base,

            boolean cleared

    ) {

        return new ResourcePatternRelationContext(

                base.resourceFoundationStable(),

                base.officerResourceChainUsable(),

                base.killingResourceChainUsable(),

                base.excessiveResourceContext(),

                base.mixedSevenKillingsClearedByCombination(),

                /*
                 * 이 필드는 制化 기반이므로
                 * 이번 분석을 직접 반영.
                 */
                cleared,

                base.wealthResourceConflictActive(),

                base.wealthModeratesExcessResource(),

                base.wealthCombinedAwayPreservingResource(),

                base.indirectResourceSuppressionActive(),

                base.outputDrainWeakStructureActive()
        );
    }

    private EatingGodPatternRelationContext enrichEatingGod(

            EatingGodPatternRelationContext base,

            boolean cleared

    ) {

        return new EatingGodPatternRelationContext(

                base.eatingGodFoundationStable(),

                base.wealthFoundationStable(),

                base.indirectResourceSuppressionActive(),

                base.wealthProtectionRouteUsable(),

                base.abandoningEatingGodForKillingWithResourceUsable(),

                base.eatingGodCombinesKillingPreservingWealth(),

                base.eatingGodSeparatesWealthAndKilling(),

                base.wealthDamageKillingResourceRouteActive(),

                base.officerKillingsMixtureUsable()
                        ||
                        cleared
        );
    }

    private HurtingOfficerPatternRelationContext enrichHurtingOfficer(

            HurtingOfficerPatternRelationContext base,

            boolean cleared

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

                base.sevenKillingsCombinedAway(),

                base.wealthDamageKillingResourceRouteActive(),

                base.officerKillingsMixtureCleared()
                        ||
                        cleared
        );
    }

    private SevenKillingsPatternRelationContext enrichSevenKillings(

            SevenKillingsPatternRelationContext base,

            boolean cleared

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
                        cleared,

                base.sevenKillingsStemCombined(),

                base.monthCommandDisrupted(),

                base.monthCommandDisruptionResolved()
        );
    }
}