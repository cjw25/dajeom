package com.dajeom.constant;

import com.dajeom.model.ConsultationGroundingPolicy;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationGroundingConstraint;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 20차
 * +
 * Category Selected RuleCode Grounding Policy 확장.
 *
 * 11개 ConsultationCategory별
 * LLM Grounding Policy Registry.
 *
 * =================================================
 * 기본 정책
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
 * Full Domain Policy
 * =================================================
 *
 * allowedDomains에 등록된 LifeDomain은
 * 해당 Domain의 공식 Rule 전체를
 * Grounding에 사용할 수 있다.
 *
 * =================================================
 * Selected Rule Policy
 * =================================================
 *
 * 특정 LifeDomain 전체를 허용하지 않고
 * 일부 RuleCode만 허용해야 하는 경우:
 *
 * selectedRuleCodes
 *
 * 를 사용한다.
 *
 * =================================================
 * SOCIAL_RELATIONSHIP
 * =================================================
 *
 * RELATIONSHIP 전체 허용 금지.
 *
 * 정확한 6개 Source RuleCode만 허용한다.
 *
 * NATAL:
 *
 * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * CURRENT_TIMELINE:
 *
 * RELATIONSHIP_DECADE_CONTEXT
 * RELATIONSHIP_ANNUAL_CONTEXT
 * RELATIONSHIP_MONTHLY_CONTEXT
 * RELATIONSHIP_MONTHLY_FUNCTIONAL
 *
 * =================================================
 * LOVE_STYLE
 * =================================================
 *
 * RELATIONSHIP 전체 허용 금지.
 *
 * 현재 공식 Source:
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * 하나만 허용한다.
 *
 * Timeline은 허용하지 않는다.
 *
 * =================================================
 * PARTNER_LUCK
 * =================================================
 *
 * RELATIONSHIP 전체 허용 금지.
 *
 * 현재 PARTNER_LUCK의 deterministic
 * Category Contract가 실제 소비하는
 * 정확한 6개 Source RuleCode만 허용한다.
 *
 * -------------------------------------------------
 * NATAL
 * -------------------------------------------------
 *
 * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * -------------------------------------------------
 * CURRENT_TIMELINE
 * -------------------------------------------------
 *
 * RELATIONSHIP_DECADE_CONTEXT
 *
 * RELATIONSHIP_ANNUAL_CONTEXT
 *
 * RELATIONSHIP_MONTHLY_CONTEXT
 *
 * RELATIONSHIP_MONTHLY_FUNCTIONAL
 *
 * -------------------------------------------------
 *
 * 현재 다음 Source는 허용하지 않는다.
 *
 * RELATIONSHIP_TEN_GOD_CONTEXT
 *
 * RELATIONSHIP_SPOUSE_PALACE
 *
 * RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY
 *
 * 기타 RELATIONSHIP Rule.
 *
 * 이 Rule들이 upstream LifeDomain 계산에서
 * 삭제되는 것은 아니다.
 *
 * PARTNER_LUCK Grounding 경계에서만
 * 소비하지 않는다.
 *
 * =================================================
 * PARTNER_LUCK 독립성
 * =================================================
 *
 * 6개 RuleCode가 같은 Category에
 * 허용된다는 이유만으로:
 *
 * - 역할·책임 + 관계망
 * - 대운 + 세운
 * - 세운 + 월운
 * - 월운 Context + Monthly Functional
 *
 * 을 합성하지 않는다.
 *
 * 각각의 Category deterministic Rule이
 * 자기 Source Family 하나만 소비한다.
 *
 * =================================================
 * CAREER
 * =================================================
 *
 * CAREER Domain 전체
 *
 * +
 *
 * LEARNING_GROWTH Domain의
 * 정확한 7개 RuleCode만 허용한다.
 *
 * =================================================
 * CURRENT_YEAR_LUCK
 * =================================================
 *
 * 모든 LifeDomain 전체를 허용하지 않는다.
 *
 * 현재연도 해석에 필요한
 * 실제 Temporal RuleCode 23개만
 * 정확히 허용한다.
 *
 * =================================================
 * CURRENT_YEAR_LUCK 의미 경계
 * =================================================
 *
 * CURRENT_YEAR_LUCK은
 * 여러 Domain 결과를 하나의 의미로
 * 합성하는 Category가 아니다.
 *
 * 각각의:
 *
 * - 대운 Context
 * - 세운 Context
 * - 세운 Functional
 * - 월운 Context
 * - 월운 Functional
 *
 * 결과를 독립적으로 노출하는
 * current-year routing boundary다.
 *
 * =================================================
 * 건강
 * =================================================
 *
 * 의료 진단 금지.
 *
 * 오행 -> 장기/질병 직접 매핑 금지.
 *
 * =================================================
 * 법률
 * =================================================
 *
 * 법적 결과 예측 금지.
 *
 * 복수 Evidence 교차 확인 필요.
 *
 * 실제 Cross 조건 계산은
 * ConsultationLegalRiskAnalyzer가 담당하며
 * 이 Registry는 계산하지 않는다.
 *
 * LEGAL_RISK Grounding에서 허용하는
 * LifeDomain은:
 *
 * - WEALTH
 * - CAREER
 * - RELATIONSHIP
 *
 * 세 Domain으로 제한한다.
 *
 * HEALTH_LIFESTYLE 또는
 * LEARNING_GROWTH Evidence가
 * LEGAL_RISK에 유입되면
 * Policy 위반으로 차단한다.
 *
 * =================================================
 * 관계
 * =================================================
 *
 * 합/충/궁위/십성을
 * 결혼·이별 사건으로
 * 기계적으로 변환하지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 여러 RuleCode가 같은 Category에
 * 등록되어 있다는 이유만으로:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 * - 먼저 A 이후 B
 * - 여러 결과가 같은 방향이라 더 강하다
 *
 * 같은 의미를 생성하지 않는다.
 *
 * selected RuleCode는
 * 허용/차단 경계일 뿐이다.
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


        /*
         * =================================================
         * PERSONALITY
         * =================================================
         */

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


        /*
         * =================================================
         * PAST_LUCK
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.PAST_LUCK,

                        allDomains(),

                        true,

                        commonConstraints()
                )
        );


        /*
         * =================================================
         * SOCIAL_RELATIONSHIP
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.SOCIAL_RELATIONSHIP,

                        domains(),

                        selectedRulesByDomain(
                                selectedRules(
                                        LifeDomain.RELATIONSHIP,

                                        "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE",

                                        "RELATIONSHIP_NATAL_RELATION_OVERLAP",

                                        "RELATIONSHIP_DECADE_CONTEXT",

                                        "RELATIONSHIP_ANNUAL_CONTEXT",

                                        "RELATIONSHIP_MONTHLY_CONTEXT",

                                        "RELATIONSHIP_MONTHLY_FUNCTIONAL"
                                )
                        ),

                        true,

                        relationConstraints()
                )
        );


        /*
         * =================================================
         * LOVE_STYLE
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.LOVE_STYLE,

                        domains(),

                        selectedRulesByDomain(
                                selectedRules(
                                        LifeDomain.RELATIONSHIP,

                                        "RELATIONSHIP_NATAL_RELATION_OVERLAP"
                                )
                        ),

                        false,

                        relationConstraints()
                )
        );


        /*
         * =================================================
         * PARTNER_LUCK
         * =================================================
         *
         * RELATIONSHIP Domain 전체 허용 금지.
         *
         * 정확한 6개 RuleCode만 허용한다.
         *
         * -------------------------------------------------
         * NATAL
         * -------------------------------------------------
         *
         * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
         *
         * RELATIONSHIP_NATAL_RELATION_OVERLAP
         *
         * -------------------------------------------------
         * CURRENT_TIMELINE
         * -------------------------------------------------
         *
         * RELATIONSHIP_DECADE_CONTEXT
         *
         * RELATIONSHIP_ANNUAL_CONTEXT
         *
         * RELATIONSHIP_MONTHLY_CONTEXT
         *
         * RELATIONSHIP_MONTHLY_FUNCTIONAL
         *
         * -------------------------------------------------
         *
         * 이 Registry는 NATAL과 CURRENT_TIMELINE의
         * 의미를 결합하지 않는다.
         *
         * selectedRuleCodes는
         * 최종 Grounding 허용 경계다.
         */

        put(
                configured,
                policy(
                        ConsultationCategory.PARTNER_LUCK,

                        domains(),

                        selectedRulesByDomain(
                                selectedRules(
                                        LifeDomain.RELATIONSHIP,

                                        "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE",

                                        "RELATIONSHIP_NATAL_RELATION_OVERLAP",

                                        "RELATIONSHIP_DECADE_CONTEXT",

                                        "RELATIONSHIP_ANNUAL_CONTEXT",

                                        "RELATIONSHIP_MONTHLY_CONTEXT",

                                        "RELATIONSHIP_MONTHLY_FUNCTIONAL"
                                )
                        ),

                        true,

                        relationConstraints()
                )
        );


        /*
         * =================================================
         * WEALTH
         * =================================================
         */

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


        /*
         * =================================================
         * CAREER
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.CAREER,

                        domains(
                                LifeDomain.CAREER
                        ),

                        selectedRulesByDomain(
                                selectedRules(
                                        LifeDomain.LEARNING_GROWTH,

                                        "LEARNING_RESOURCE_OUTPUT",

                                        "LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE",

                                        "LEARNING_DECADE_CONTEXT",

                                        "LEARNING_ANNUAL_CONTEXT",

                                        "LEARNING_MONTHLY_CONTEXT",

                                        "LEARNING_GROWTH_ANNUAL_FUNCTIONAL",

                                        "LEARNING_GROWTH_MONTHLY_FUNCTIONAL"
                                )
                        ),

                        true,

                        commonConstraints()
                )
        );


        /*
         * =================================================
         * LEGAL_RISK
         * =================================================
         *
         * ConsultationLegalRiskAnalyzer가 실제로 사용하는:
         *
         * - WEALTH
         * - CAREER
         * - RELATIONSHIP
         *
         * 세 Domain만 Grounding에 허용한다.
         *
         * 이 Registry에서는 새로운 Legal Cross를
         * 계산하지 않는다.
         *
         * upstream에서 계산된 Legal Evidence가
         * 공식 Domain 경계를 벗어나지 않았는지만
         * 차단/검증한다.
         */

        put(
                configured,
                policy(
                        ConsultationCategory.LEGAL_RISK,

                        domains(
                                LifeDomain.WEALTH,
                                LifeDomain.CAREER,
                                LifeDomain.RELATIONSHIP
                        ),

                        true,

                        legalConstraints()
                )
        );


        /*
         * =================================================
         * HEALTH_TENDENCY
         * =================================================
         */

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


        /*
         * =================================================
         * CURRENT_YEAR_LUCK
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.CURRENT_YEAR_LUCK,

                        domains(),

                        selectedRulesByDomain(

                                selectedRules(
                                        LifeDomain.WEALTH,

                                        "WEALTH_DECADE_CONTEXT",

                                        "WEALTH_ANNUAL_CONTEXT",

                                        "WEALTH_ANNUAL_FUNCTIONAL",

                                        "WEALTH_MONTHLY_CONTEXT",

                                        "WEALTH_MONTHLY_FUNCTIONAL"
                                ),

                                selectedRules(
                                        LifeDomain.CAREER,

                                        "CAREER_DECADE_CONTEXT",

                                        "CAREER_ANNUAL_CONTEXT",

                                        "CAREER_ANNUAL_FUNCTIONAL",

                                        "CAREER_MONTHLY_CONTEXT",

                                        "CAREER_MONTHLY_FUNCTIONAL"
                                ),

                                selectedRules(
                                        LifeDomain.RELATIONSHIP,

                                        "RELATIONSHIP_DECADE_CONTEXT",

                                        "RELATIONSHIP_ANNUAL_CONTEXT",

                                        "RELATIONSHIP_MONTHLY_CONTEXT",

                                        "RELATIONSHIP_MONTHLY_FUNCTIONAL"
                                ),

                                selectedRules(
                                        LifeDomain.LEARNING_GROWTH,

                                        "LEARNING_DECADE_CONTEXT",

                                        "LEARNING_ANNUAL_CONTEXT",

                                        "LEARNING_GROWTH_ANNUAL_FUNCTIONAL",

                                        "LEARNING_MONTHLY_CONTEXT",

                                        "LEARNING_GROWTH_MONTHLY_FUNCTIONAL"
                                ),

                                selectedRules(
                                        LifeDomain.HEALTH_LIFESTYLE,

                                        "HEALTH_DECADE_CONTEXT",

                                        "HEALTH_ANNUAL_CONTEXT",

                                        "HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL",

                                        "HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL"
                                )
                        ),

                        true,

                        commonConstraints()
                )
        );


        /*
         * =================================================
         * FUTURE_LUCK
         * =================================================
         */

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
     * Domain Policy Builder
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
     * Selected Rule Policy Builder
     * =================================================
     */

    private static ConsultationGroundingPolicy policy(

            ConsultationCategory category,

            EnumSet<LifeDomain> allowedDomains,

            Map<LifeDomain, Set<String>>
                    selectedRuleCodes,

            boolean timelineAllowed,

            EnumSet<ConsultationGroundingConstraint> constraints

    ) {

        return new ConsultationGroundingPolicy(
                category,
                allowedDomains,
                selectedRuleCodes,
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


    /*
     * =================================================
     * Selected Rule Builder
     * =================================================
     */

    @SafeVarargs
    private static Map<LifeDomain, Set<String>>
    selectedRulesByDomain(

            Map.Entry<LifeDomain, Set<String>>...
                    entries

    ) {

        if (
                entries == null
                        ||
                        entries.length == 0
        ) {

            return Map.of();
        }

        EnumMap<LifeDomain, Set<String>> result =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                Map.Entry<LifeDomain, Set<String>> entry
                : entries
        ) {

            Map.Entry<LifeDomain, Set<String>> normalizedEntry =
                    Objects.requireNonNull(
                            entry,
                            "Grounding selected Rule Entry는 null일 수 없습니다."
                    );

            LifeDomain domain =
                    Objects.requireNonNull(
                            normalizedEntry.getKey(),
                            "Grounding selected Rule의 LifeDomain은 null일 수 없습니다."
                    );

            Set<String> ruleCodes =
                    Objects.requireNonNull(
                            normalizedEntry.getValue(),
                            "Grounding selected RuleCode Set은 null일 수 없습니다. domain="
                                    + domain
                    );

            if (
                    ruleCodes.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Grounding selected RuleCode Set은 비어 있을 수 없습니다. domain="
                                + domain
                );
            }

            Set<String> previous =
                    result.put(
                            domain,
                            Set.copyOf(
                                    ruleCodes
                            )
                    );

            if (
                    previous != null
            ) {

                throw new IllegalStateException(
                        "Grounding selected Rule Domain이 중복되었습니다: "
                                + domain
                );
            }
        }

        return Collections.unmodifiableMap(
                result
        );
    }


    private static Map.Entry<LifeDomain, Set<String>>
    selectedRules(

            LifeDomain domain,

            String... ruleCodes

    ) {

        Objects.requireNonNull(
                domain,
                "Grounding selected Rule에는 LifeDomain이 필요합니다."
        );

        if (
                ruleCodes == null
                        ||
                        ruleCodes.length == 0
        ) {

            throw new IllegalArgumentException(
                    "Grounding selected Rule에는 하나 이상의 RuleCode가 필요합니다. domain="
                            + domain
            );
        }

        LinkedHashSet<String> normalized =
                new LinkedHashSet<>();

        for (
                String ruleCode
                : ruleCodes
        ) {

            Objects.requireNonNull(
                    ruleCode,
                    "Grounding selected RuleCode는 null일 수 없습니다. domain="
                            + domain
            );

            String value =
                    ruleCode.trim();

            if (
                    value.isBlank()
            ) {

                throw new IllegalArgumentException(
                        "Grounding selected RuleCode는 비어 있을 수 없습니다. domain="
                                + domain
                );
            }

            if (
                    !normalized.add(
                            value
                    )
            ) {

                throw new IllegalArgumentException(
                        "Grounding selected RuleCode가 중복되었습니다. domain="
                                + domain
                                + ", ruleCode="
                                + value
                );
            }
        }

        return Map.entry(
                domain,
                Collections.unmodifiableSet(
                        normalized
                )
        );
    }


    /*
     * =================================================
     * Registry Put
     * =================================================
     */

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