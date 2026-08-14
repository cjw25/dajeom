package com.dajeom.rule;

import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.PalaceRelationReference;
import com.dajeom.model.PalaceTransformationReference;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.PalacePosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 관계 영역의 DAY Palace 구조 Rule.
 *
 * =================================================
 *
 * 입력 Source:
 *
 * 12차 PalaceAnalysis
 * 8차 RelationAnalysis
 * 9차 CombinationTransformationAnalysis
 *
 * =================================================
 *
 * 중요 원칙:
 *
 * DAY Palace를:
 *
 * "배우자 그 자체"
 *
 * 로 기계적으로 고정하지 않는다.
 *
 * 12차 PalaceAnalysis의 기존 정책대로
 * DAY Palace는 관계·밀접 상호작용을 해석할 때
 * 중요하게 참조하는 위치 Context로 사용한다.
 *
 * =================================================
 *
 * 이 Rule이 읽는 것:
 *
 * - DAY Palace의 십성 Context
 * - DAY Palace가 관여하는 원국 관계
 * - DAY Palace가 관여하는 합화
 * - 실제 합화 활성 여부
 * - 원래 기능 감소 여부
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - DAY = 배우자 확정
 * - 합 = 결혼 확정
 * - 충 = 이별 확정
 * - 합화 활성 = 관계 성사 확정
 * - 원래 기능 감소 = 관계 악화 확정
 *
 * =================================================
 *
 * 관계 종류별 SUPPORTIVE / PRESSURE 판단은
 * 관계 종류와 중첩을 별도로 읽는 후속 Rule에서
 * 처리한다.
 *
 * 이 Rule은 궁위 관여 사실과
 * 합화 작동 Context를 보존한다.
 */
@Component
public class RelationshipSpousePalaceRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_SPOUSE_PALACE";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.PALACE,
                    LifeDomainEvidenceSourceType.NATAL_RELATION,
                    LifeDomainEvidenceSourceType.TRANSFORMATION
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
                "RelationshipSpousePalaceRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipSpousePalaceRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
            );
        }

        LifeDomainEvidenceReference<?>
                palaceReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.PALACE
                );

        LifeDomainEvidenceReference<?>
                relationReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.NATAL_RELATION
                );

        LifeDomainEvidenceReference<?>
                transformationReference =
                requireSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.TRANSFORMATION
                );

        PalaceAnalysis palaceAnalysis =
                palaceReference
                        .requireSource(
                                PalaceAnalysis.class
                        );

        RelationAnalysis relationAnalysis =
                relationReference
                        .requireSource(
                                RelationAnalysis.class
                        );

        CombinationTransformationAnalysis
                transformationAnalysis =
                transformationReference
                        .requireSource(
                                CombinationTransformationAnalysis.class
                        );

        /*
         * =================================================
         * Upstream Identity 검증
         * =================================================
         */

        if (
                palaceAnalysis.dayMaster()
                        != relationAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "12차 PalaceAnalysis와 8차 RelationAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                palaceAnalysis.dayMaster()
                        != transformationAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "12차 PalaceAnalysis와 9차 CombinationTransformationAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                transformationAnalysis
                        .relationAnalysis()
                        != relationAnalysis
        ) {

            throw new IllegalArgumentException(
                    "9차 CombinationTransformationAnalysis가 "
                            + "현재 8차 RelationAnalysis를 참조하지 않습니다."
            );
        }

        if (
                palaceAnalysis.completeChart()
                        != relationAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "12차 PalaceAnalysis와 8차 RelationAnalysis의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        if (
                palaceAnalysis.completeChart()
                        != transformationAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "12차 PalaceAnalysis와 9차 CombinationTransformationAnalysis의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        List<LifeDomainRuleMatch> matches =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * DAY Palace 기본 Context
         * =================================================
         *
         * DAY Palace는 항상 존재한다.
         *
         * 그러나:
         *
         * DAY = 배우자
         *
         * 로 단정하지 않는다.
         *
         * 현재 DAY 궁위에 어떤 십성 Context가
         * 연결되어 있는지 보존한다.
         */

        int dayTenGodEvidenceCount =
                palaceAnalysis
                        .tenGodEvidencesOf(
                                PalacePosition.DAY
                        )
                        .size();

        matches.add(
                new LifeDomainRuleMatch(
                        LifeDomain.RELATIONSHIP,
                        RULE_CODE,
                        LifeDomainSignalStatus.CONTEXTUAL,
                        "DAY 궁위는 관계 영역에서 중요하게 참조되는 위치 Context입니다.",
                        List.of(
                                palaceReference
                        ),
                        List.of(
                                "12차 PalaceAnalysis의 DAY 궁위 지지는 "
                                        + palaceAnalysis
                                        .dayBranchEvidence()
                                        .branch()
                                        .name()
                                        + "입니다.",
                                "DAY 궁위에 연결된 십성 Evidence는 "
                                        + dayTenGodEvidenceCount
                                        + "개입니다.",
                                "DAY 궁위를 배우자 자체로 고정하지 않고 십성·관계·운 흐름과 함께 해석합니다.",
                                "12운성 상태 또한 단독 길흉으로 사용하지 않습니다: "
                                        + palaceAnalysis
                                        .twelveLifeStageOf(
                                                PalacePosition.DAY
                                        )
                                        .name()
                        )
                )
        );

        /*
         * =================================================
         * 2.
         * DAY Palace 관계 관여
         * =================================================
         *
         * 합/충/형/파/해 등의 구체 종류를
         * 여기서 새로 분류하지 않는다.
         *
         * PalaceCrossContext가 이미 8차 관계를
         * 위치 기준으로 정규화했다.
         */

        List<PalaceRelationReference> dayRelations =
                palaceAnalysis
                        .relationsOf(
                                PalacePosition.DAY
                        );

        if (
                !dayRelations.isEmpty()
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "DAY 궁위가 원국의 하나 이상의 관계 구조에 직접 관여합니다.",
                            List.of(
                                    palaceReference,
                                    relationReference
                            ),
                            List.of(
                                    "12차 PalaceCrossContext에서 DAY 궁위와 연결된 관계 Reference가 "
                                            + dayRelations.size()
                                            + "개 존재합니다.",
                                    "8차 관계 분석은 합·충·형·파·해 등의 중첩을 단일 관계로 축약하지 않습니다.",
                                    "관계가 존재한다는 사실 자체를 연애·결혼의 성사 또는 실패로 환산하지 않습니다.",
                                    "구체적인 지원·부담 방향은 관계 종류와 중첩을 읽는 후속 관계 Rule에서 판단합니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 3.
         * DAY Palace 합화 Context
         * =================================================
         *
         * 합 존재와 실제 합화 활성은 구분한다.
         *
         * 9차에서 이미 결정한:
         *
         * activated
         * originalFunctionReduced
         *
         * 상태를 그대로 읽는다.
         */

        List<PalaceTransformationReference>
                dayTransformations =
                palaceAnalysis
                        .transformationsOf(
                                PalacePosition.DAY
                        );

        if (
                !dayTransformations.isEmpty()
        ) {

            long activatedCount =
                    dayTransformations
                            .stream()
                            .filter(
                                    PalaceTransformationReference
                                            ::activated
                            )
                            .count();

            long reducedOriginalFunctionCount =
                    dayTransformations
                            .stream()
                            .filter(
                                    PalaceTransformationReference
                                            ::originalFunctionReduced
                            )
                            .count();

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "DAY 궁위가 하나 이상의 합화 후보 또는 합화 작용에 관여합니다.",
                            List.of(
                                    palaceReference,
                                    transformationReference
                            ),
                            List.of(
                                    "12차 PalaceAnalysis에서 DAY 궁위와 연결된 Transformation Reference가 "
                                            + dayTransformations.size()
                                            + "개 존재합니다.",
                                    "그중 실제 activated 상태는 "
                                            + activatedCount
                                            + "개입니다.",
                                    "원래 기능 감소가 확인된 Reference는 "
                                            + reducedOriginalFunctionCount
                                            + "개입니다.",
                                    "합의 존재와 실제 합화 활성 여부를 구분하여 보존합니다.",
                                    "합화 작용을 관계 성사·결혼 등의 사건 결과로 직접 환산하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 4.
         * DAY Palace 실제 합화 활성
         * =================================================
         *
         * activated=true가 하나라도 있을 경우
         * 별도 Context로 보존한다.
         *
         * SUPPORTIVE로 올리지 않는다.
         *
         * 어떤 오행으로 변하고
         * 원국 전체에서 어떤 역할을 하는지는
         * 별도 Evidence와 함께 판단해야 하기 때문이다.
         */

        if (
                palaceAnalysis
                        .hasActivatedTransformation(
                                PalacePosition.DAY
                        )
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "DAY 궁위에 실제 활성된 합화 작용이 존재합니다.",
                            List.of(
                                    palaceReference,
                                    transformationReference
                            ),
                            List.of(
                                    "9차 합화 분석을 거쳐 DAY 궁위 관련 Transformation 중 activated=true인 결과가 존재합니다.",
                                    "단순 합 존재를 실제 합화 활성으로 취급하지 않습니다.",
                                    "활성 합화의 존재만으로 관계영역을 긍정 또는 부정으로 확정하지 않습니다."
                            )
                    )
            );
        }

        /*
         * =================================================
         * 5.
         * DAY Palace 원래 기능 감소
         * =================================================
         *
         * originalFunctionReduced=true도
         * 곧바로 PRESSURE로 바꾸지 않는다.
         *
         * 원래 기능 감소는
         * 합화 이후 기능 재편 Evidence이며
         * 그 자체가 관계 악화를 뜻하지 않는다.
         */

        if (
                palaceAnalysis
                        .hasReducedOriginalFunction(
                                PalacePosition.DAY
                        )
        ) {

            matches.add(
                    new LifeDomainRuleMatch(
                            LifeDomain.RELATIONSHIP,
                            RULE_CODE,
                            LifeDomainSignalStatus.CONTEXTUAL,
                            "DAY 궁위 관련 합화에서 원래 기능이 감소하는 작용이 확인됩니다.",
                            List.of(
                                    palaceReference,
                                    transformationReference
                            ),
                            List.of(
                                    "9차 합화 효과 분석을 통해 DAY 궁위 관련 Transformation의 originalFunctionReduced=true가 확인됩니다.",
                                    "원래 기능 감소와 합화 후 목표 오행 활성은 서로 구분하여 해석해야 합니다.",
                                    "원래 기능 감소를 관계 단절·이별 등의 사건으로 직접 해석하지 않습니다."
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
                    "RelationshipSpousePalaceRule에는 정확히 하나의 "
                            + sourceType
                            + " Evidence가 필요합니다. actual="
                            + evidences.size()
            );
        }

        return evidences
                .get(0);
    }
}