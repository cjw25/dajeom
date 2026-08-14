package com.dajeom.constant;

import com.dajeom.model.ConsultationGroundingPolicy;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationGroundingConstraint;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 20차.
 *
 * 11개 ConsultationCategory별
 * LLM Grounding Policy Registry.
 *
 * =================================================
 *
 * 모든 Category는 공통적으로:
 *
 * - 새로운 사주 계산 금지
 * - 직접 사건 단정 금지
 * - MIXED Evidence 보존
 * - Timeline 불확실성 보존
 * - 보조 Evidence 독립 판단 금지
 *
 * 를 적용한다.
 *
 * =================================================
 *
 * 건강:
 *
 * 의료 진단 금지
 * 오행 -> 장기/질병 직접 매핑 금지
 *
 * =================================================
 *
 * 법률:
 *
 * 법적 결과 예측 금지
 * 복수 Evidence 교차 확인 필요
 *
 * =================================================
 *
 * 관계:
 *
 * 합/충/궁위/십성을
 * 결혼·이별 사건으로 기계적으로 변환하지 않는다.
 */
@Component
public class ConsultationGroundingPolicyRegistry {

    private final Map<
            ConsultationCategory,
            ConsultationGroundingPolicy
            > policies;

    public ConsultationGroundingPolicyRegistry() {

        EnumMap<
                ConsultationCategory,
                ConsultationGroundingPolicy
                > configured =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        put(
                configured,
                policy(
                        ConsultationCategory.PERSONALITY,
                        domains(
                                LifeDomain.WEALTH,
                                LifeDomain.CAREER,
                                LifeDomain.RELATIONSHIP,
                                LifeDomain.LEARNING_GROWTH
                        ),
                        false,
                        commonConstraints()
                )
        );

        put(
                configured,
                policy(
                        ConsultationCategory.PAST_LUCK,
                        allDomains(),
                        true,
                        commonConstraints()
                )
        );

        put(
                configured,
                policy(
                        ConsultationCategory.SOCIAL_RELATIONSHIP,
                        domains(
                                LifeDomain.RELATIONSHIP
                        ),
                        true,
                        relationConstraints()
                )
        );

        put(
                configured,
                policy(
                        ConsultationCategory.LOVE_STYLE,
                        domains(
                                LifeDomain.RELATIONSHIP
                        ),
                        false,
                        relationConstraints()
                )
        );

        put(
                configured,
                policy(
                        ConsultationCategory.PARTNER_LUCK,
                        domains(
                                LifeDomain.RELATIONSHIP
                        ),
                        true,
                        relationConstraints()
                )
        );

        put(
                configured,
                policy(
                        ConsultationCategory.WEALTH,
                        domains(
                                LifeDomain.WEALTH
                        ),
                        true,
                        commonConstraints()
                )
        );

        put(
                configured,
                policy(
                        ConsultationCategory.CAREER,
                        domains(
                                LifeDomain.CAREER
                        ),
                        true,
                        commonConstraints()
                )
        );

        /*
         * LEGAL_RISK는 별도 LifeDomain을 만들지 않는다.
         *
         * 향후 전용 교차 Rule이 생성하는 Evidence는
         * 여러 Domain에서 올 수 있으므로
         * Domain 자체는 제한하지 않는다.
         *
         * 대신 반드시 법률 전용 Constraint를 적용한다.
         */
        put(
                configured,
                policy(
                        ConsultationCategory.LEGAL_RISK,
                        allDomains(),
                        true,
                        legalConstraints()
                )
        );

        put(
                configured,
                policy(
                        ConsultationCategory.HEALTH_TENDENCY,
                        domains(
                                LifeDomain.HEALTH_LIFESTYLE
                        ),
                        true,
                        healthConstraints()
                )
        );

        put(
                configured,
                policy(
                        ConsultationCategory.CURRENT_YEAR_LUCK,
                        allDomains(),
                        true,
                        commonConstraints()
                )
        );

        put(
                configured,
                policy(
                        ConsultationCategory.FUTURE_LUCK,
                        allDomains(),
                        true,
                        commonConstraints()
                )
        );

        /*
         * =================================================
         * 11개 Category 완전성 검증
         * =================================================
         */

        if (
                configured.size()
                        != ConsultationCategory.values().length
        ) {

            throw new IllegalStateException(
                    "모든 ConsultationCategory에 Grounding Policy가 필요합니다. "
                            + "actual="
                            + configured.size()
                            + ", expected="
                            + ConsultationCategory.values().length
            );
        }

        for (
                ConsultationCategory category
                : ConsultationCategory.values()
        ) {

            if (
                    !configured.containsKey(
                            category
                    )
            ) {

                throw new IllegalStateException(
                        "Grounding Policy가 누락되었습니다: "
                                + category
                );
            }
        }

        policies =
                Collections.unmodifiableMap(
                        configured
                );
    }

    /*
     * =================================================
     * 공식 API
     * =================================================
     */

    public ConsultationGroundingPolicy policyOf(
            ConsultationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "ConsultationCategory는 필수입니다."
        );

        ConsultationGroundingPolicy policy =
                policies.get(
                        category
                );

        if (
                policy == null
        ) {

            throw new IllegalArgumentException(
                    "Grounding Policy를 찾을 수 없습니다: "
                            + category
            );
        }

        return policy;
    }

    public Map<
            ConsultationCategory,
            ConsultationGroundingPolicy
            > allPolicies() {

        return policies;
    }

    /*
     * =================================================
     * Policy Builder
     * =================================================
     */

    private static ConsultationGroundingPolicy policy(

            ConsultationCategory category,

            EnumSet<LifeDomain> allowedDomains,

            boolean timelineAllowed,

            EnumSet<ConsultationGroundingConstraint> constraints

    ) {

        return new ConsultationGroundingPolicy(
                category,
                allowedDomains,
                timelineAllowed,
                constraints
        );
    }

    /*
     * =================================================
     * 공통 Constraint
     * =================================================
     */

    private static EnumSet<ConsultationGroundingConstraint>
    commonConstraints() {

        return EnumSet.of(
                ConsultationGroundingConstraint
                        .NO_NEW_SAJU_CALCULATION,

                ConsultationGroundingConstraint
                        .NO_DIRECT_EVENT_ASSERTION,

                ConsultationGroundingConstraint
                        .PRESERVE_MIXED_EVIDENCE,

                ConsultationGroundingConstraint
                        .PRESERVE_TIMELINE_UNCERTAINTY,

                ConsultationGroundingConstraint
                        .AUXILIARY_NOT_STANDALONE
        );
    }

    private static EnumSet<ConsultationGroundingConstraint>
    relationConstraints() {

        EnumSet<ConsultationGroundingConstraint> result =
                commonConstraints();

        result.add(
                ConsultationGroundingConstraint
                        .NO_MECHANICAL_RELATIONSHIP_EVENT_MAPPING
        );

        return result;
    }

    private static EnumSet<ConsultationGroundingConstraint>
    healthConstraints() {

        EnumSet<ConsultationGroundingConstraint> result =
                commonConstraints();

        result.add(
                ConsultationGroundingConstraint
                        .NO_MEDICAL_DIAGNOSIS
        );

        result.add(
                ConsultationGroundingConstraint
                        .NO_ORGAN_DISEASE_MAPPING
        );

        return result;
    }

    private static EnumSet<ConsultationGroundingConstraint>
    legalConstraints() {

        EnumSet<ConsultationGroundingConstraint> result =
                commonConstraints();

        result.add(
                ConsultationGroundingConstraint
                        .NO_LEGAL_OUTCOME_PREDICTION
        );

        result.add(
                ConsultationGroundingConstraint
                        .REQUIRE_CROSS_EVIDENCE_FOR_LEGAL_RISK
        );

        return result;
    }

    /*
     * =================================================
     * Domain Builder
     * =================================================
     */

    private static EnumSet<LifeDomain> domains(
            LifeDomain... domains
    ) {

        EnumSet<LifeDomain> result =
                EnumSet.noneOf(
                        LifeDomain.class
                );

        if (
                domains == null
        ) {

            return result;
        }

        for (
                LifeDomain domain
                : domains
        ) {

            result.add(
                    Objects.requireNonNull(
                            domain,
                            "Grounding Policy LifeDomain은 null일 수 없습니다."
                    )
            );
        }

        return result;
    }

    private static EnumSet<LifeDomain> allDomains() {

        return EnumSet.allOf(
                LifeDomain.class
        );
    }

    private static void put(

            EnumMap<
                    ConsultationCategory,
                    ConsultationGroundingPolicy
                    > target,

            ConsultationGroundingPolicy policy

    ) {

        ConsultationGroundingPolicy previous =
                target.put(
                        policy.category(),
                        policy
                );

        if (
                previous != null
        ) {

            throw new IllegalStateException(
                    "Grounding Policy가 중복 정의되었습니다: "
                            + policy.category()
            );
        }
    }
}