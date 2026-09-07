package com.dajeom.calculation;

import com.dajeom.domain.DirectOfficerPatternRelationContext;
import com.dajeom.domain.EatingGodPatternRelationContext;
import com.dajeom.domain.HurtingOfficerPatternRelationContext;
import com.dajeom.domain.PatternRelationContextBundle;
import com.dajeom.domain.PatternWealthResourceInteractionAnalysis;
import com.dajeom.domain.ResourcePatternRelationContext;
import com.dajeom.domain.SevenKillingsPatternRelationContext;
import com.dajeom.domain.WealthPatternRelationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 財印 위치 관계 분석을
 * 격별 RelationContext에 반영한다.
 *
 * =================================================
 *
 * 자동 반영:
 *
 * 正官格
 * - wealthAndResourceMutuallyNonObstructing
 *
 * 財格
 * - wealthAndResourceMutuallyNonObstructing
 *
 * 印格
 * - wealthResourceConflictActive
 *
 * =================================================
 *
 * 아직 자동 반영하지 않음:
 *
 * - wealthModeratesExcessResource
 *
 * 이유:
 *
 * 印이 과다한 상태에서 財가 오히려
 * 적절한 用으로 작동하는지는
 * 단순 위치 관계가 아니라
 * 신강/印旺 구조까지 함께 봐야 하기 때문이다.
 *
 * =================================================
 *
 * - wealthCombinedAwayPreservingResource
 *
 * 는 앞의 천간합 Preservation 계층에서 처리.
 */
@Component
public class PatternWealthResourceRelationContextCalculator {

    public PatternRelationContextBundle apply(

            PatternWealthResourceInteractionAnalysis analysis,

            PatternRelationContextBundle base

    ) {

        Objects.requireNonNull(
                analysis,
                "PatternWealthResourceInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                base,
                "PatternRelationContextBundle은 필수입니다."
        );

        boolean mutuallyNonObstructing =
                analysis
                        .visibleWealthAndResourceMutuallyNonObstructing();

        boolean resourceConflictActive =
                analysis
                        .resourcePatternConflictActive();

        return new PatternRelationContextBundle(

                enrichDirectOfficer(
                        base.directOfficer(),
                        mutuallyNonObstructing
                ),

                enrichWealth(
                        base.wealth(),
                        mutuallyNonObstructing
                ),

                enrichResource(
                        base.resource(),
                        resourceConflictActive
                ),

                /*
                 * 食神格의 財護食은
                 * 별도의 食神-梟-財 동일 노드 구조에서 처리.
                 */
                base.eatingGod(),

                /*
                 * 傷官佩印에서
                 * "그 印"이 財에게 맞았는지는
                 * 傷官을 제어하는 동일 印 노드까지
                 * 연결해야 하므로 여기서 올리지 않는다.
                 */
                base.hurtingOfficer(),

                base.sevenKillings()
        );
    }

    /*
     * =================================================
     * 正官格
     * =================================================
     */

    private DirectOfficerPatternRelationContext enrichDirectOfficer(

            DirectOfficerPatternRelationContext base,

            boolean mutuallyNonObstructing

    ) {

        return new DirectOfficerPatternRelationContext(

                base.officerStemCombined(),

                base.monthCommandClashed(),

                base.monthCommandPunished(),

                base.monthCommandBroken(),

                base.monthCommandHarmed(),

                base.resourcePlacementSupportsHurtingOfficerResolution(),

                base.mixedSevenKillingsClearedByCombination(),

                base.monthCommandDisruptionResolvedByCombination(),

                base.wealthAndResourceMutuallyNonObstructing()
                        ||
                        mutuallyNonObstructing
        );
    }

    /*
     * =================================================
     * 財格
     * =================================================
     */

    private WealthPatternRelationContext enrichWealth(

            WealthPatternRelationContext base,

            boolean mutuallyNonObstructing

    ) {

        return new WealthPatternRelationContext(

                base.wealthAndResourceMutuallyNonObstructing()
                        ||
                        mutuallyNonObstructing,

                base.sevenKillingsCombinedAwayPreservingWealth(),

                base.generatedOfficerCombined()
        );
    }

    /*
     * =================================================
     * 印格
     * =================================================
     */

    private ResourcePatternRelationContext enrichResource(

            ResourcePatternRelationContext base,

            boolean resourceConflictActive

    ) {

        return new ResourcePatternRelationContext(

                base.resourceFoundationStable(),

                base.officerResourceChainUsable(),

                base.killingResourceChainUsable(),

                base.excessiveResourceContext(),

                base.mixedSevenKillingsClearedByCombination(),

                base.mixedOfficerKillingsControlled(),

                base.wealthResourceConflictActive()
                        ||
                        resourceConflictActive,

                /*
                 * 印多用財는
                 * 신강/印旺 조건까지 필요.
                 */
                base.wealthModeratesExcessResource(),

                base.wealthCombinedAwayPreservingResource(),

                base.indirectResourceSuppressionActive(),

                base.outputDrainWeakStructureActive()
        );
    }
}