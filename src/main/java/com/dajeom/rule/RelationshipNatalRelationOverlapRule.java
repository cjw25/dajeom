package com.dajeom.rule;

import com.dajeom.model.EarthlyBranchPairRelation;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 관계 영역의 원국 지지 관계망 중첩 Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 8차 RelationAnalysis
 *
 * =================================================
 *
 * 핵심 대상:
 *
 * DAY 지지가 관여하는
 * EarthlyBranchPairRelation.
 *
 * =================================================
 *
 * 사용하는 8차 공식 API:
 *
 * dayBranchPairRelations()
 *
 * overlappingEarthlyBranchRelations()
 *
 * mixedEarthlyBranchRelationPairs()
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 하나의 지지 Pair에:
 *
 * - 합
 * - 충
 * - 형
 * - 파
 * - 해
 *
 * 등이 동시에 존재할 수 있다.
 *
 * 따라서 하나의 BranchRelation으로
 * 축약하지 않는다.
 *
 * =================================================
 *
 * 이 Rule에서 사용하는 관계는
 * 8차 RelationAnalysis가 실제 계산한
 * EarthlyBranchPairRelation에 한정한다.
 *
 * 독립된 다른 Evidence를 가져와:
 *
 * - 관계
 * - 인과
 * - 순서
 * - 강화
 * - 약화
 * - 상쇄
 * - 추가 종합 의미
 *
 * 를 만들지 않는다.
 *
 * =================================================
 *
 * 또한:
 *
 * 合 존재
 * -> 좋은 관계
 *
 * 沖 존재
 * -> 이별
 *
 * 刑 존재
 * -> 관계 실패
 *
 * 破 / 害 존재
 * -> 관계 악화
 *
 * 같은 사건 결과를 만들지 않는다.
 *
 * =================================================
 *
 * 상태 해석:
 *
 * CONTEXTUAL
 *
 * DAY 지지가 실제 관계망에 관여.
 *
 * MIXED
 *
 * 동일 DAY Pair에
 * 결합 계열과 충·형·파·해 계열이
 * 함께 존재.
 *
 * PRESSURE
 *
 * DAY Pair에 沖 또는 刑이 존재.
 *
 * 파·해 단독은 강한 Pressure로
 * 자동 승격하지 않고 Context로 보존.
 *
 * =================================================
 *
 * SUPPORTIVE는 이 Rule에서 생성하지 않는다.
 *
 * raw 합 관계의 존재만으로는
 * 실제 합화 활성이나 관계영역의
 * 긍정 방향을 확정할 수 없기 때문이다.
 */
@Component
public class RelationshipNatalRelationOverlapRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_NATAL_RELATION_OVERLAP";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType
                            .NATAL_RELATION
            );

    @Override
    public LifeDomain domain() {

        return LifeDomain.RELATIONSHIP;
    }

    @Override
    public String code() {

        return RULE_CODE;
    }

    @Override
    public Set<LifeDomainEvidenceSourceType>
    supportedSources() {

        return SUPPORTED_SOURCES;
    }

    @Override
    public List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    ) {

        Objects.requireNonNull(
                bundle,
                "RelationshipNatalRelationOverlapRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipNatalRelationOverlapRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
            );
        }

        LifeDomainEvidenceReference<?>
                relationReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType
                                .NATAL_RELATION
                );

        RelationAnalysis relationAnalysis =
                relationReference
                        .requireSource(
                                RelationAnalysis.class
                        );

        List<EarthlyBranchPairRelation>
                dayRelations =
                relationAnalysis
                        .dayBranchPairRelations();

        /*
         * =================================================
         * DAY 지지가 아무 관계 Pair에도
         * 관여하지 않는 경우.
         * =================================================
         *
         * 관계 부재나 배우자 인연 부재를
         * 의미하지 않는다.
         *
         * 이번 Rule의 Match만 생성하지 않는다.
         */

        if (
                dayRelations.isEmpty()
        ) {

            return List.of();
        }

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * DAY 지지 관계망 기본 Context
         * =================================================
         */

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.RELATIONSHIP,
                        RULE_CODE,
                        LifeDomainSignalStatus.CONTEXTUAL,
                        "DAY 지지가 원국의 지지 관계망에 직접 참여하고 있습니다.",
                        List.of(
                                relationReference
                        ),
                        List.of(
                                "8차 RelationAnalysis에서 DAY 지지가 관여하는 EarthlyBranchPairRelation이 "
                                        + dayRelations.size()
                                        + "개 존재합니다.",
                                "관여 Pair는 "
                                        + pairSummary(
                                        dayRelations
                                )
                                        + "입니다.",
                                "각 Pair에 존재하는 복수 관계를 하나의 합·충 관계로 축약하지 않습니다.",
                                "DAY 지지의 관계망 참여 자체를 연애·결혼의 성사 또는 실패로 환산하지 않습니다."
                        )
                )
        );

        /*
         * =================================================
         * 2.
         * 동일 Pair의 합계열 + 압박계열 혼재
         * =================================================
         *
         * RelationAnalysis가 이미 제공하는
         * mixedRelationFamilies() 결과를 사용한다.
         *
         * 18차에서 관계 종류를 다시 계산하지 않는다.
         */

        List<EarthlyBranchPairRelation>
                mixedDayPairs =
                relationAnalysis
                        .mixedEarthlyBranchRelationPairs()
                        .stream()
                        .filter(
                                relation ->
                                        relation.involves(
                                                EarthlyBranchPosition.DAY
                                        )
                        )
                        .toList();

        if (
                !mixedDayPairs.isEmpty()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.MIXED,
                            "DAY 지지가 관여하는 동일 Pair 안에서 결합과 압박 관계가 함께 존재합니다.",
                            List.of(
                                    relationReference
                            ),
                            List.of(
                                    "8차 RelationAnalysis의 mixedEarthlyBranchRelationPairs() 중 DAY 지지가 관여하는 Pair가 "
                                            + mixedDayPairs.size()
                                            + "개 존재합니다.",
                                    "혼재 Pair는 "
                                            + pairSummary(
                                            mixedDayPairs
                                    )
                                            + "입니다.",
                                    "같은 Pair에 결합 계열과 충·형·파·해 계열이 동시에 존재하는 구조를 그대로 보존합니다.",
                                    "결합과 압박 중 어느 하나가 다른 하나를 자동으로 삭제하거나 무효화하지 않습니다.",
                                    "관계영역에서는 한 방향의 SUPPORTIVE 또는 PRESSURE로 축약하지 않고 MIXED로 유지합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 3.
         * 沖 / 刑 Pressure
         * =================================================
         *
         * 9차의 기존 방해 정밀화에서도
         * 沖과 刑은 의미 있는 방해 조건으로
         * 취급한다.
         *
         * 여기서는 DAY가 직접 관여하는 경우만
         * 관계영역 Pressure Context로 연결한다.
         */

        List<EarthlyBranchPairRelation>
                meaningfulPressurePairs =
                dayRelations
                        .stream()
                        .filter(
                                relation ->
                                        relation.hasClash()
                                                || relation
                                                .hasPunishment()
                        )
                        .toList();

        if (
                !meaningfulPressurePairs
                        .isEmpty()
        ) {

            long clashCount =
                    meaningfulPressurePairs
                            .stream()
                            .filter(
                                    EarthlyBranchPairRelation
                                            ::hasClash
                            )
                            .count();

            long punishmentCount =
                    meaningfulPressurePairs
                            .stream()
                            .filter(
                                    EarthlyBranchPairRelation
                                            ::hasPunishment
                            )
                            .count();

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.PRESSURE,
                            "DAY 지지가 관여하는 관계망에 충 또는 형의 직접 압박 Context가 존재합니다.",
                            List.of(
                                    relationReference
                            ),
                            List.of(
                                    "DAY 지지가 직접 관여하는 沖/刑 Pair가 "
                                            + meaningfulPressurePairs.size()
                                            + "개 존재합니다.",
                                    "그중 沖이 포함된 Pair는 "
                                            + clashCount
                                            + "개입니다.",
                                    "刑이 포함된 Pair는 "
                                            + punishmentCount
                                            + "개입니다.",
                                    "해당 Pair는 "
                                            + pairSummary(
                                            meaningfulPressurePairs
                                    )
                                            + "입니다.",
                                    "沖과 刑을 관계 단절·이별·갈등 사건으로 확정하지 않고 관계 구조의 Pressure Evidence로 보존합니다.",
                                    "동일 Pair의 결합 관계가 함께 존재할 경우 해당 MIXED Evidence도 별도로 유지합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 4.
         * 破 / 害 단독 Context
         * =================================================
         *
         * 沖/刑 없이
         * 破 또는 害만 존재하는 Pair.
         *
         * 9차 방해 정책에서도
         * 단독/원거리 破·害는 상대적으로
         * 약한 방해 가능성을 가지므로
         *
         * 여기서 즉시 PRESSURE로
         * 평준화하지 않는다.
         */

        List<EarthlyBranchPairRelation>
                breakOrHarmOnlyPairs =
                dayRelations
                        .stream()
                        .filter(
                                relation ->
                                        !relation.hasClash()
                                                && !relation
                                                .hasPunishment()
                                                && (
                                                relation.hasBreak()
                                                        || relation
                                                        .hasHarm()
                                        )
                        )
                        .toList();

        if (
                !breakOrHarmOnlyPairs
                        .isEmpty()
        ) {

            long breakCount =
                    breakOrHarmOnlyPairs
                            .stream()
                            .filter(
                                    EarthlyBranchPairRelation
                                            ::hasBreak
                            )
                            .count();

            long harmCount =
                    breakOrHarmOnlyPairs
                            .stream()
                            .filter(
                                    EarthlyBranchPairRelation
                                            ::hasHarm
                            )
                            .count();

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "DAY 지지가 관여하는 관계망에 파 또는 해의 마찰 Context가 존재합니다.",
                            List.of(
                                    relationReference
                            ),
                            List.of(
                                    "沖/刑 없이 破 또는 害가 존재하는 DAY Pair가 "
                                            + breakOrHarmOnlyPairs.size()
                                            + "개 존재합니다.",
                                    "破가 포함된 Pair는 "
                                            + breakCount
                                            + "개입니다.",
                                    "害가 포함된 Pair는 "
                                            + harmCount
                                            + "개입니다.",
                                    "해당 Pair는 "
                                            + pairSummary(
                                            breakOrHarmOnlyPairs
                                    )
                                            + "입니다.",
                                    "破·害 단독을 沖·刑과 동일한 강도의 Pressure로 자동 평준화하지 않습니다.",
                                    "이 Match는 破·害가 존재한다는 계산 결과와 CONTEXTUAL 상태만 보존하며, 다른 독립 Evidence와의 추가 관계나 종합 의미를 생성하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 5.
         * 동일 Pair 복수관계 중첩
         * =================================================
         *
         * mixedRelationFamilies가 아니더라도
         * 동일 Pair에 둘 이상의 관계가
         * 중첩될 수 있다.
         *
         * 예:
         *
         * 압박계열 내부 복수관계
         *
         * 또는
         *
         * 결합계열 내부 복수관계.
         *
         * 이를 별도 Context로 보존한다.
         */

        List<EarthlyBranchPairRelation>
                overlappingDayPairs =
                relationAnalysis
                        .overlappingEarthlyBranchRelations()
                        .stream()
                        .filter(
                                relation ->
                                        relation.involves(
                                                EarthlyBranchPosition.DAY
                                        )
                        )
                        .filter(
                                relation ->
                                        !relation
                                                .mixedRelationFamilies()
                        )
                        .toList();

        if (
                !overlappingDayPairs
                        .isEmpty()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "DAY 지지가 관여하는 동일 Pair에 복수 관계가 중첩되어 있습니다.",
                            List.of(
                                    relationReference
                            ),
                            List.of(
                                    "8차 RelationAnalysis에서 DAY 지지가 관여하면서 둘 이상의 관계를 가진 Pair가 "
                                            + overlappingDayPairs.size()
                                            + "개 존재합니다.",
                                    "중첩 Pair는 "
                                            + pairSummary(
                                            overlappingDayPairs
                                    )
                                            + "입니다.",
                                    "결합/압박 Family 혼재 Pair는 별도의 MIXED Match에서 이미 보존하므로 여기서는 제외했습니다.",
                                    "동일 Family 내부의 복수 관계 역시 하나의 관계로 축약하지 않고 별도 Context로 유지합니다."
                            )
                    )
            );
        }

        return List.copyOf(
                matches
        );
    }

    /*
     * =================================================
     * Evidence 조회
     * =================================================
     */

    private LifeDomainEvidenceReference<?>
    requireSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

        List<LifeDomainEvidenceReference<?>> evidences =
                bundle
                        .evidencesOf(
                                sourceType
                        );

        if (
                evidences.size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "RelationshipNatalRelationOverlapRule에는 정확히 하나의 "
                            + sourceType
                            + " Evidence가 필요합니다. actual="
                            + evidences.size()
            );
        }

        return evidences
                .get(0);
    }

    /*
     * =================================================
     * Pair 표시
     * =================================================
     */

    private String pairSummary(
            List<EarthlyBranchPairRelation> relations
    ) {

        return relations
                .stream()
                .map(
                        relation ->
                                relation
                                        .firstPosition()
                                        .name()
                                        + "-"
                                        + relation
                                        .secondPosition()
                                        .name()
                )
                .distinct()
                .toList()
                .toString();
    }
}