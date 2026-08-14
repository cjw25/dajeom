package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 18차.
 *
 * 관계 영역의 재성/관성 주격 격국 Context Rule.
 *
 * =================================================
 *
 * Source:
 *
 * 5차 StructureAnalysis
 *
 * =================================================
 *
 * 현재 직접 판정 대상:
 *
 * 재성 계열:
 *
 * - DIRECT_WEALTH
 * - INDIRECT_WEALTH
 *
 * 관성 계열:
 *
 * - DIRECT_OFFICER
 * - SEVEN_KILLINGS
 *
 * =================================================
 *
 * 관계영역 의미:
 *
 * 재성 계열 주격:
 *
 * - 현실적인 주고받음
 * - 상대에게 실제로 제공하는 것
 * - 관계 유지에 필요한 자원
 * - 생활적인 기대
 * - 관계의 현실적 운영
 *
 * 같은 기능이
 * 원국의 중심 구조에 관여한다.
 *
 * -------------------------------------------------
 *
 * 관성 계열 주격:
 *
 * - 책임
 * - 역할
 * - 약속
 * - 관계 안의 기준
 * - 상대와 맞춰야 하는 요구
 * - 관계에서 감당하는 압력
 *
 * 같은 기능이
 * 원국의 중심 구조에 관여한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * 재격
 * -> 배우자복이 좋다
 *
 * 관격
 * -> 결혼운이 좋다
 *
 * 칠살격
 * -> 관계가 힘들다
 *
 * 정재격
 * -> 안정적인 배우자를 만난다
 *
 * 편재격
 * -> 이성관계가 많다
 *
 * 격 성립
 * -> 결혼한다
 *
 * 파격
 * -> 이별한다
 *
 * 같은 실제 관계 사건이나
 * 상대방 특성을 직접 생성하지 않는다.
 *
 * =================================================
 *
 * 기존 Relationship Rule과의 차이:
 *
 * RelationshipTenGodContextRule:
 *
 * TenGodCompositeAnalysis를 통해
 * 실제 재성/관성 분포와 발달,
 * 일간 수용력 및 부담 구조를 본다.
 *
 * RelationshipSpousePalaceRule:
 *
 * DAY 궁위와 실제 원국 관계,
 * Transformation Context를 본다.
 *
 * RelationshipNatalRelationOverlapRule:
 *
 * 원국 관계의 중첩을 보존한다.
 *
 * 이 Rule:
 *
 * 5차 격국에서
 * 재성 또는 관성이 실제 주격으로
 * 선택되었는지를 추가 STRUCTURE Evidence로 보존한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 격국은 관계영역의 중심 기능을
 * 설명하는 한 축일 뿐이다.
 *
 * 실제 가까운 관계의 작동 방식은:
 *
 * - DAY 궁위
 * - 원국 합충형파해
 * - Transformation
 * - TenGodComposite
 * - 대운
 * - 세운
 * - 월운
 *
 * Evidence와 함께 읽어야 한다.
 *
 * =================================================
 *
 * 상태:
 *
 * SUPPORTIVE
 *
 * 좋은 관계 또는 좋은 결혼을 뜻하지 않는다.
 *
 * 해당 중심 관계 기능이
 * 비교적 구조적으로 성립한다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * PRESSURE
 *
 * 이별·갈등을 뜻하지 않는다.
 *
 * 중심 관계 기능이
 * 부담 또는 강한 손상을 동반한다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * MIXED
 *
 * 중심 기능의 성립과
 * 부담·손상·혼잡 근거가
 * 함께 존재한다는 뜻이다.
 *
 * -------------------------------------------------
 *
 * CONTEXTUAL
 *
 * 해당 기능이 중심축에 관여하지만
 * 실제 관계 방향을 단독으로 확정할 수 없다는 뜻이다.
 *
 * =================================================
 *
 * 보조격:
 *
 * StructureSelectionCandidate의 공식 타입 API를
 * 추측하지 않는다.
 *
 * 따라서 현재는:
 *
 * - 보조격 존재 여부
 * - 보조격 이름
 *
 * 만 Provenance로 보존한다.
 *
 * =================================================
 *
 * 시간 미상:
 *
 * completeChart=false를 그대로 보존한다.
 */
@Component
public class RelationshipPrimaryWealthOfficerStructureRule
        implements LifeDomainRule {

    private static final String RULE_CODE =
            "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE";

    private static final Set<LifeDomainEvidenceSourceType>
            SUPPORTED_SOURCES =
            Set.of(
                    LifeDomainEvidenceSourceType.STRUCTURE
            );

    private static final Set<StructureTenGodType>
            WEALTH_TYPES =
            Set.of(
                    StructureTenGodType.DIRECT_WEALTH,
                    StructureTenGodType.INDIRECT_WEALTH
            );

    private static final Set<StructureTenGodType>
            OFFICER_TYPES =
            Set.of(
                    StructureTenGodType.DIRECT_OFFICER,
                    StructureTenGodType.SEVEN_KILLINGS
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
                "RelationshipPrimaryWealthOfficerStructureRule에는 LifeDomainEvidenceBundle이 필요합니다."
        );

        if (
                bundle.domain()
                        != LifeDomain.RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "RelationshipPrimaryWealthOfficerStructureRule은 RELATIONSHIP Bundle만 평가할 수 있습니다."
            );
        }

        Optional<LifeDomainEvidenceReference<?>>
                structureReferenceOptional =
                optionalSingleEvidence(
                        bundle,
                        LifeDomainEvidenceSourceType.STRUCTURE
                );

        if (
                structureReferenceOptional.isEmpty()
        ) {

            return List.of();
        }

        LifeDomainEvidenceReference<?>
                structureReference =
                structureReferenceOptional
                        .orElseThrow();

        StructureAnalysis structureAnalysis =
                structureReference
                        .requireSource(
                                StructureAnalysis.class
                        );

        /*
         * =================================================
         * 일반격 주격만 현재 직접 판정.
         * =================================================
         *
         * 특수격을 재성/관성 일반격으로
         * 임의 환원하지 않는다.
         */

        if (
                !structureAnalysis
                        .primaryIsStandard()
        ) {

            return List.of();
        }

        Optional<StructureTenGodType>
                primaryTypeOptional =
                structureAnalysis
                        .primaryStandardTenGod();

        if (
                primaryTypeOptional.isEmpty()
        ) {

            throw new IllegalStateException(
                    "primaryIsStandard=true인데 primaryStandardTenGod이 존재하지 않습니다."
            );
        }

        StructureTenGodType primaryType =
                primaryTypeOptional
                        .orElseThrow();

        boolean wealthPrimary =
                WEALTH_TYPES.contains(
                        primaryType
                );

        boolean officerPrimary =
                OFFICER_TYPES.contains(
                        primaryType
                );

        if (
                !wealthPrimary
                        && !officerPrimary
        ) {

            return List.of();
        }

        LifeDomainSignalStatus status =
                resolveStatus(
                        structureAnalysis
                );

        List<String> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 기본 격국 Identity
         * =================================================
         */

        reasons.add(
                "5차 격국 분석에서 주격은 "
                        + structureAnalysis
                        .primaryNameKorean()
                        + "입니다."
        );

        reasons.add(
                "주격 일반격 십성 유형은 "
                        + primaryType.name()
                        + "입니다."
        );

        reasons.add(
                "최종 격 상태는 "
                        + structureAnalysis
                        .statusKorean()
                        + "입니다."
        );

        reasons.add(
                "최종 격 상태 설명: "
                        + structureAnalysis
                        .statusDescription()
        );

        reasons.add(
                "격국 분석 기준 일간은 "
                        + structureAnalysis
                        .dayMaster()
                        + ", 월지는 "
                        + structureAnalysis
                        .monthBranch()
                        + "입니다."
        );

        /*
         * =================================================
         * 관계영역 기능 축
         * =================================================
         */

        if (
                wealthPrimary
        ) {

            reasons.add(
                    "재성 계열이 주격이므로 가까운 관계에서 현실적인 주고받음·생활 자원·실질적인 기대를 다루는 기능이 원국 중심 구조에 관여합니다."
            );
        }

        if (
                officerPrimary
        ) {

            reasons.add(
                    "관성 계열이 주격이므로 가까운 관계에서 책임·약속·역할·기준·상대와 맞춰야 하는 요구를 다루는 기능이 원국 중심 구조에 관여합니다."
            );
        }

        /*
         * =================================================
         * 성립
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryEstablished()
        ) {

            reasons.add(
                    "현재 주격은 성립 계열 상태로 판정되었습니다."
            );

        } else {

            reasons.add(
                    "현재 주격이 선택되어 있으나 성립 계열 상태로 확정된 것은 아닙니다."
            );
        }

        /*
         * =================================================
         * 균형 부담
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryBurdened()
        ) {

            reasons.add(
                    "현재 주격은 일간 강약 및 균형 관점에서 부담 조건을 동반합니다."
            );
        }

        /*
         * =================================================
         * 손상
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryDamaged()
        ) {

            reasons.add(
                    "현재 주격에 의미 있는 손상 Evidence가 존재합니다."
            );
        }

        if (
                structureAnalysis
                        .primaryStronglyDamaged()
        ) {

            reasons.add(
                    "현재 주격에 강한 손상 Evidence가 존재합니다."
            );
        }

        /*
         * =================================================
         * 순도 / 혼잡
         * =================================================
         */

        if (
                structureAnalysis
                        .primaryRelativelyPure()
        ) {

            reasons.add(
                    "현재 주격은 상대적으로 순도가 확보된 구조입니다."
            );
        }

        if (
                structureAnalysis
                        .primaryHighlyMixed()
        ) {

            reasons.add(
                    "현재 주격에는 높은 혼잡도 Evidence가 존재합니다."
            );
        }

        /*
         * =================================================
         * 보조격
         * =================================================
         *
         * 타입을 추측하지 않고
         * 이름만 Provenance로 보존한다.
         */

        if (
                structureAnalysis
                        .hasSecondaryStructure()
        ) {

            reasons.add(
                    "주격 외에 보조격 후보가 존재합니다."
            );

            if (
                    !structureAnalysis
                            .secondaryNamesKorean()
                            .isEmpty()
            ) {

                reasons.add(
                        "보조격 후보: "
                                + String.join(
                                ", ",
                                structureAnalysis
                                        .secondaryNamesKorean()
                        )
                );
            }
        }

        /*
         * =================================================
         * 특수격 후보 / Review
         * =================================================
         */

        if (
                structureAnalysis
                        .hasSpecialStructureCandidate()
        ) {

            reasons.add(
                    "현재 일반격 주격과 별개로 특수격 후보가 존재합니다."
            );
        }

        if (
                structureAnalysis
                        .requiresSpecialReview()
        ) {

            reasons.add(
                    "특수구조 추가 검토가 필요한 상태이므로 현재 주격을 관계영역의 절대적인 단일 구조로 확정하지 않습니다."
            );
        }

        /*
         * =================================================
         * completeChart
         * =================================================
         */

        if (
                !structureAnalysis
                        .completeChart()
        ) {

            reasons.add(
                    "원국이 completeChart=false이므로 시간 미상 등으로 인한 구조 불확실성을 그대로 보존합니다."
            );
        }

        /*
         * =================================================
         * 관계 해석 제한
         * =================================================
         */

        reasons.add(
                "이 격국 Evidence는 가까운 관계에서 어떤 기능을 중요하게 다루는지를 설명하며 특정 상대방의 성격이나 관계 사건을 직접 생성하지 않습니다."
        );

        if (
                wealthPrimary
        ) {

            reasons.add(
                    "재성 주격은 관계에서 현실적 주고받음과 생활적 기대가 중요한 축이라는 뜻이며 연애 성공·결혼·배우자복을 의미하지 않습니다."
            );
        }

        if (
                officerPrimary
        ) {

            reasons.add(
                    "관성 주격은 관계에서 책임·기준·역할과 요구를 다루는 기능이 중요한 축이라는 뜻이며 좋은 배우자·나쁜 배우자 또는 결혼 결과를 의미하지 않습니다."
            );
        }

        reasons.add(
                "격의 성립·부담·손상·혼잡 상태는 관계의 성공 또는 실패 판정이 아니라 해당 관계 기능이 중심 구조에서 작동하는 안정성·부담·복합성을 설명하는 Context입니다."
        );

        reasons.add(
                "실제 가까운 관계의 궁위 Context는 RelationshipSpousePalaceRule이 별도로 담당합니다."
        );

        reasons.add(
                "원국 합충형파해의 중첩은 RelationshipNatalRelationOverlapRule이 별도로 보존합니다."
        );

        reasons.add(
                "실제 재성·관성 분포와 일간 수용력은 RelationshipTenGodContextRule이 담당하며 이 Rule은 5차 주격 정보를 추가 STRUCTURE Context로만 보존합니다."
        );

        reasons.add(
                "대운·세운·월운의 관계 Context는 각각의 Relationship temporal Rule이 별도로 유지하므로 이 Rule에서 시간 사건을 추론하지 않습니다."
        );

        return List.of(
                new LifeDomainRuleMatch(
                        LifeDomain.RELATIONSHIP,
                        RULE_CODE,
                        status,
                        summaryOf(
                                primaryType,
                                status
                        ),
                        List.of(
                                structureReference
                        ),
                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * STRUCTURE -> LifeDomainSignalStatus
     * =================================================
     *
     * 관계 길흉이 아니다.
     *
     * 해당 중심 관계 기능의
     * 구조 상태만 표현한다.
     */

    private LifeDomainSignalStatus resolveStatus(
            StructureAnalysis analysis
    ) {

        Objects.requireNonNull(
                analysis,
                "StructureAnalysis가 필요합니다."
        );

        boolean established =
                analysis.primaryEstablished();

        boolean burdened =
                analysis.primaryBurdened();

        boolean damaged =
                analysis.primaryDamaged();

        boolean stronglyDamaged =
                analysis.primaryStronglyDamaged();

        boolean highlyMixed =
                analysis.primaryHighlyMixed();

        /*
         * =================================================
         * 강한 손상
         * =================================================
         */

        if (
                stronglyDamaged
        ) {

            return established
                    ? LifeDomainSignalStatus.MIXED
                    : LifeDomainSignalStatus.PRESSURE;
        }

        /*
         * =================================================
         * 손상 / 높은 혼잡
         * =================================================
         */

        if (
                damaged
                        || highlyMixed
        ) {

            return LifeDomainSignalStatus.MIXED;
        }

        /*
         * =================================================
         * 균형 부담
         * =================================================
         */

        if (
                burdened
        ) {

            return established
                    ? LifeDomainSignalStatus.MIXED
                    : LifeDomainSignalStatus.PRESSURE;
        }

        /*
         * =================================================
         * 성립
         * =================================================
         */

        if (
                established
        ) {

            return LifeDomainSignalStatus.SUPPORTIVE;
        }

        return LifeDomainSignalStatus.CONTEXTUAL;
    }

    /*
     * =================================================
     * 내부 Semantic Summary
     * =================================================
     *
     * 사용자 최종 점사 문장이 아니다.
     */

    private String summaryOf(

            StructureTenGodType primaryType,

            LifeDomainSignalStatus status

    ) {

        String center =
                switch (
                        primaryType
                        ) {

                    case DIRECT_WEALTH ->
                            "가까운 관계에서 현실적인 주고받음과 생활적 책임을 안정적으로 관리하는 기능";

                    case INDIRECT_WEALTH ->
                            "관계 속 다양한 현실적 요구와 자원을 유연하게 다루는 기능";

                    case DIRECT_OFFICER ->
                            "관계 안의 약속·기준·책임과 정돈된 역할을 다루는 기능";

                    case SEVEN_KILLINGS ->
                            "관계에서 강한 요구·긴장·책임 압력 속 역할을 조율하는 기능";

                    default ->
                            "가까운 관계에서 현실적 기대와 역할을 다루는 기능";
                };

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 비교적 성립된 축으로 관여합니다.";

            case PRESSURE ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 관여하지만 부담 또는 손상 조건이 두드러집니다.";

            case MIXED ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 중요한 축으로 관여하면서 성립 근거와 부담·손상·혼잡 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "원국의 중심 구조에서 "
                            + center
                            + "이 관여하지만 실제 관계 방향을 단독으로 확정할 수준으로 단순화하지 않습니다.";
        };
    }

    /*
     * =================================================
     * 단일 STRUCTURE Evidence
     * =================================================
     */

    private Optional<LifeDomainEvidenceReference<?>>
    optionalSingleEvidence(

            LifeDomainEvidenceBundle bundle,

            LifeDomainEvidenceSourceType sourceType

    ) {

        List<LifeDomainEvidenceReference<?>>
                evidences =
                bundle.evidencesOf(
                        sourceType
                );

        if (
                evidences.isEmpty()
        ) {

            return Optional.empty();
        }

        if (
                evidences.size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "RelationshipPrimaryWealthOfficerStructureRule에는 최대 하나의 "
                            + sourceType
                            + " Evidence가 허용됩니다. actual="
                            + evidences.size()
            );
        }

        return Optional.of(
                evidences.get(
                        0
                )
        );
    }
}