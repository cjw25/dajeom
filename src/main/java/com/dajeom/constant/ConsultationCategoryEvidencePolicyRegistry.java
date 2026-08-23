package com.dajeom.constant;

import com.dajeom.model.ConsultationCategoryEvidencePolicy;
import com.dajeom.type.ConsultationCategory;
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
 * ConsultationCategory와 LifeDomainAnalysis 사이의
 * 공식 Evidence Routing Policy.
 *
 * 핵심 원칙:
 *
 * - Category마다 필요한 Source만 Routing한다.
 * - 여러 Source가 함께 Routing되었다는 이유만으로
 *   관계·인과·순서·합성 의미를 만들지 않는다.
 * - selected RuleCode는 허용 경계다.
 */
@Component
public class ConsultationCategoryEvidencePolicyRegistry {

    /*
     * =================================================
     * RELATIONSHIP Category Selected Rule Codes
     * =================================================
     */

    private static final String
            RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE =
            "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE";

    private static final String
            RELATIONSHIP_NATAL_RELATION_OVERLAP =
            "RELATIONSHIP_NATAL_RELATION_OVERLAP";


    /*
     * =================================================
     * CAREER Selected Learning Rule Codes
     * =================================================
     */

    private static final String
            LEARNING_RESOURCE_OUTPUT =
            "LEARNING_RESOURCE_OUTPUT";

    private static final String
            LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE =
            "LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE";

    private static final String
            LEARNING_DECADE_CONTEXT =
            "LEARNING_DECADE_CONTEXT";

    private static final String
            LEARNING_ANNUAL_CONTEXT =
            "LEARNING_ANNUAL_CONTEXT";

    private static final String
            LEARNING_MONTHLY_CONTEXT =
            "LEARNING_MONTHLY_CONTEXT";

    private static final String
            LEARNING_GROWTH_ANNUAL_FUNCTIONAL =
            "LEARNING_GROWTH_ANNUAL_FUNCTIONAL";

    private static final String
            LEARNING_GROWTH_MONTHLY_FUNCTIONAL =
            "LEARNING_GROWTH_MONTHLY_FUNCTIONAL";


    /*
     * =================================================
     * Temporal LifeDomain Rule Codes
     * =================================================
     */

    /*
     * WEALTH
     */

    private static final String
            WEALTH_DECADE_CONTEXT =
            "WEALTH_DECADE_CONTEXT";

    private static final String
            WEALTH_ANNUAL_CONTEXT =
            "WEALTH_ANNUAL_CONTEXT";

    private static final String
            WEALTH_ANNUAL_FUNCTIONAL =
            "WEALTH_ANNUAL_FUNCTIONAL";

    private static final String
            WEALTH_MONTHLY_CONTEXT =
            "WEALTH_MONTHLY_CONTEXT";

    private static final String
            WEALTH_MONTHLY_FUNCTIONAL =
            "WEALTH_MONTHLY_FUNCTIONAL";


    /*
     * CAREER
     */

    private static final String
            CAREER_DECADE_CONTEXT =
            "CAREER_DECADE_CONTEXT";

    private static final String
            CAREER_ANNUAL_CONTEXT =
            "CAREER_ANNUAL_CONTEXT";

    private static final String
            CAREER_ANNUAL_FUNCTIONAL =
            "CAREER_ANNUAL_FUNCTIONAL";

    private static final String
            CAREER_MONTHLY_CONTEXT =
            "CAREER_MONTHLY_CONTEXT";

    private static final String
            CAREER_MONTHLY_FUNCTIONAL =
            "CAREER_MONTHLY_FUNCTIONAL";


    /*
     * RELATIONSHIP
     */

    private static final String
            RELATIONSHIP_DECADE_CONTEXT =
            "RELATIONSHIP_DECADE_CONTEXT";

    private static final String
            RELATIONSHIP_ANNUAL_CONTEXT =
            "RELATIONSHIP_ANNUAL_CONTEXT";

    private static final String
            RELATIONSHIP_MONTHLY_CONTEXT =
            "RELATIONSHIP_MONTHLY_CONTEXT";

    private static final String
            RELATIONSHIP_MONTHLY_FUNCTIONAL =
            "RELATIONSHIP_MONTHLY_FUNCTIONAL";


    /*
     * HEALTH_LIFESTYLE
     */

    private static final String
            HEALTH_DECADE_CONTEXT =
            "HEALTH_DECADE_CONTEXT";

    private static final String
            HEALTH_ANNUAL_CONTEXT =
            "HEALTH_ANNUAL_CONTEXT";

    private static final String
            HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL =
            "HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL";

    private static final String
            HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL =
            "HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL";


    private final Map<
            ConsultationCategory,
            ConsultationCategoryEvidencePolicy
            > policies;


    public ConsultationCategoryEvidencePolicyRegistry() {

        EnumMap<
                ConsultationCategory,
                ConsultationCategoryEvidencePolicy
                > configured =
                new EnumMap<>(
                        ConsultationCategory.class
                );


        /*
         * =================================================
         * 1. PERSONALITY
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

                        domains(),

                        false,
                        false,
                        false
                )
        );


        /*
         * =================================================
         * 2. PAST_LUCK
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.PAST_LUCK,

                        domains(),

                        domains(),

                        true,
                        false,
                        false
                )
        );


        /*
         * =================================================
         * 3. SOCIAL_RELATIONSHIP
         * =================================================
         *
         * RELATIONSHIP Domain 전체 Routing 금지.
         *
         * NATAL:
         *
         * - RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
         * - RELATIONSHIP_NATAL_RELATION_OVERLAP
         *
         * CURRENT_TIMELINE:
         *
         * - RELATIONSHIP_DECADE_CONTEXT
         * - RELATIONSHIP_ANNUAL_CONTEXT
         * - RELATIONSHIP_MONTHLY_CONTEXT
         * - RELATIONSHIP_MONTHLY_FUNCTIONAL
         */

        put(
                configured,
                policy(
                        ConsultationCategory.SOCIAL_RELATIONSHIP,

                        domains(),

                        domains(),

                        selectedRules(
                                LifeDomain.RELATIONSHIP,

                                RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE,

                                RELATIONSHIP_NATAL_RELATION_OVERLAP
                        ),

                        selectedRules(
                                LifeDomain.RELATIONSHIP,

                                RELATIONSHIP_DECADE_CONTEXT,

                                RELATIONSHIP_ANNUAL_CONTEXT,

                                RELATIONSHIP_MONTHLY_CONTEXT,

                                RELATIONSHIP_MONTHLY_FUNCTIONAL
                        ),

                        false,
                        false,
                        false
                )
        );


        /*
         * =================================================
         * 4. LOVE_STYLE
         * =================================================
         *
         * RELATIONSHIP Domain 전체 Routing 금지.
         *
         * 현재 LOVE_STYLE 공식 NATAL Source:
         *
         * RELATIONSHIP_NATAL_RELATION_OVERLAP
         *
         * 하나만 허용한다.
         *
         * Timeline Source는 현재 없다.
         */

        put(
                configured,
                policy(
                        ConsultationCategory.LOVE_STYLE,

                        domains(),

                        domains(),

                        selectedRules(
                                LifeDomain.RELATIONSHIP,

                                RELATIONSHIP_NATAL_RELATION_OVERLAP
                        ),

                        Map.of(),

                        false,
                        false,
                        false
                )
        );


        /*
         * =================================================
         * 5. PARTNER_LUCK
         * =================================================
         *
         * RELATIONSHIP Domain 전체 Routing 금지.
         *
         * 현재 PARTNER_LUCK이 실제 deterministic
         * Conclusion으로 소비하는 정확한
         * 6개 Source RuleCode만 허용한다.
         *
         * -------------------------------------------------
         * NATAL
         * -------------------------------------------------
         *
         * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
         *
         * ->
         *
         * PARTNER_LUCK_PRIMARY_WEALTH_OFFICER_STRUCTURE
         *
         * -------------------------------------------------
         *
         * RELATIONSHIP_NATAL_RELATION_OVERLAP
         *
         * ->
         *
         * PARTNER_LUCK_NATAL_RELATION_OVERLAP
         *
         * -------------------------------------------------
         * CURRENT_TIMELINE
         * -------------------------------------------------
         *
         * RELATIONSHIP_DECADE_CONTEXT
         *
         * ->
         *
         * PARTNER_LUCK_CURRENT_DECADE_CONTEXT
         *
         * -------------------------------------------------
         *
         * RELATIONSHIP_ANNUAL_CONTEXT
         *
         * ->
         *
         * PARTNER_LUCK_CURRENT_ANNUAL_CONTEXT
         *
         * -------------------------------------------------
         *
         * RELATIONSHIP_MONTHLY_CONTEXT
         *
         * ->
         *
         * PARTNER_LUCK_CURRENT_MONTHLY_CONTEXT
         *
         * -------------------------------------------------
         *
         * RELATIONSHIP_MONTHLY_FUNCTIONAL
         *
         * ->
         *
         * PARTNER_LUCK_CURRENT_MONTHLY_FUNCTIONAL
         *
         * =================================================
         * 현재 제외
         * =================================================
         *
         * RELATIONSHIP_TEN_GOD_CONTEXT
         *
         * RELATIONSHIP_SPOUSE_PALACE
         *
         * RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY
         *
         * 및 기타 RELATIONSHIP Rule은
         * 현재 PARTNER_LUCK Category View에
         * Routing하지 않는다.
         *
         * upstream LifeDomain 결과 자체를
         * 삭제하는 것은 아니다.
         *
         * =================================================
         * 독립성
         * =================================================
         *
         * 이 6개 Source가 같은 Category에
         * Routing된다는 이유만으로:
         *
         * - 역할·책임 + 관계망
         * - 대운 + 세운
         * - 세운 + 월운
         * - 월운 Context + Monthly Functional
         *
         * 을 결합하지 않는다.
         */

        put(
                configured,
                policy(
                        ConsultationCategory.PARTNER_LUCK,

                        /*
                         * Natal Domain 전체 차단.
                         */
                        domains(),

                        /*
                         * Segment Domain 전체 차단.
                         */
                        domains(),

                        /*
                         * selectedNatalRuleCodes
                         */
                        selectedRules(
                                LifeDomain.RELATIONSHIP,

                                RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE,

                                RELATIONSHIP_NATAL_RELATION_OVERLAP
                        ),

                        /*
                         * selectedSegmentRuleCodes
                         */
                        selectedRules(
                                LifeDomain.RELATIONSHIP,

                                RELATIONSHIP_DECADE_CONTEXT,

                                RELATIONSHIP_ANNUAL_CONTEXT,

                                RELATIONSHIP_MONTHLY_CONTEXT,

                                RELATIONSHIP_MONTHLY_FUNCTIONAL
                        ),

                        false,
                        false,
                        false
                )
        );


        /*
         * =================================================
         * 6. WEALTH
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.WEALTH,

                        domains(
                                LifeDomain.WEALTH
                        ),

                        domains(
                                LifeDomain.WEALTH
                        ),

                        false,
                        false,
                        false
                )
        );


        /*
         * =================================================
         * 7. CAREER
         * =================================================
         *
         * CAREER Domain 전체
         *
         * +
         *
         * LEARNING_GROWTH의 선별된 RuleCode만 사용.
         */

        put(
                configured,
                policy(
                        ConsultationCategory.CAREER,

                        domains(
                                LifeDomain.CAREER
                        ),

                        domains(
                                LifeDomain.CAREER
                        ),

                        selectedRules(
                                LifeDomain.LEARNING_GROWTH,

                                LEARNING_RESOURCE_OUTPUT,

                                LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE
                        ),

                        selectedRules(
                                LifeDomain.LEARNING_GROWTH,

                                LEARNING_DECADE_CONTEXT,

                                LEARNING_ANNUAL_CONTEXT,

                                LEARNING_MONTHLY_CONTEXT,

                                LEARNING_GROWTH_ANNUAL_FUNCTIONAL,

                                LEARNING_GROWTH_MONTHLY_FUNCTIONAL
                        ),

                        false,
                        false,
                        false
                )
        );


        /*
         * =================================================
         * 8. LEGAL_RISK
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.LEGAL_RISK,

                        domains(),

                        domains(),

                        false,
                        false,
                        true
                )
        );


        /*
         * =================================================
         * 9. HEALTH_TENDENCY
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.HEALTH_TENDENCY,

                        domains(
                                LifeDomain.HEALTH_LIFESTYLE
                        ),

                        domains(
                                LifeDomain.HEALTH_LIFESTYLE
                        ),

                        false,
                        false,
                        false
                )
        );


        /*
         * =================================================
         * 10. CURRENT_YEAR_LUCK
         * =================================================
         *
         * 원국 전체 Routing 없음.
         * Segment Domain 전체 Routing 없음.
         *
         * 실제 공식 Temporal RuleCode 23개만
         * selectedSegmentRuleCodes로 허용한다.
         */

        put(
                configured,
                policy(
                        ConsultationCategory.CURRENT_YEAR_LUCK,

                        domains(),

                        domains(),

                        Map.of(),

                        selectedRulesByDomain(
                                Map.of(
                                        LifeDomain.WEALTH,
                                        ruleCodes(
                                                WEALTH_DECADE_CONTEXT,
                                                WEALTH_ANNUAL_CONTEXT,
                                                WEALTH_ANNUAL_FUNCTIONAL,
                                                WEALTH_MONTHLY_CONTEXT,
                                                WEALTH_MONTHLY_FUNCTIONAL
                                        ),

                                        LifeDomain.CAREER,
                                        ruleCodes(
                                                CAREER_DECADE_CONTEXT,
                                                CAREER_ANNUAL_CONTEXT,
                                                CAREER_ANNUAL_FUNCTIONAL,
                                                CAREER_MONTHLY_CONTEXT,
                                                CAREER_MONTHLY_FUNCTIONAL
                                        ),

                                        LifeDomain.RELATIONSHIP,
                                        ruleCodes(
                                                RELATIONSHIP_DECADE_CONTEXT,
                                                RELATIONSHIP_ANNUAL_CONTEXT,
                                                RELATIONSHIP_MONTHLY_CONTEXT,
                                                RELATIONSHIP_MONTHLY_FUNCTIONAL
                                        ),

                                        LifeDomain.LEARNING_GROWTH,
                                        ruleCodes(
                                                LEARNING_DECADE_CONTEXT,
                                                LEARNING_ANNUAL_CONTEXT,
                                                LEARNING_GROWTH_ANNUAL_FUNCTIONAL,
                                                LEARNING_MONTHLY_CONTEXT,
                                                LEARNING_GROWTH_MONTHLY_FUNCTIONAL
                                        ),

                                        LifeDomain.HEALTH_LIFESTYLE,
                                        ruleCodes(
                                                HEALTH_DECADE_CONTEXT,
                                                HEALTH_ANNUAL_CONTEXT,
                                                HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL,
                                                HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
                                        )
                                )
                        ),

                        false,
                        false,
                        false
                )
        );


        /*
         * =================================================
         * 11. FUTURE_LUCK
         * =================================================
         */

        put(
                configured,
                policy(
                        ConsultationCategory.FUTURE_LUCK,

                        domains(),

                        domains(),

                        false,
                        true,
                        false
                )
        );


        /*
         * =================================================
         * 모든 Category 정의 검증
         * =================================================
         */

        if (
                configured.size()
                        != ConsultationCategory
                        .values()
                        .length
        ) {

            throw new IllegalStateException(
                    "모든 ConsultationCategory에 대한 Evidence Policy가 정의되어야 합니다. "
                            + "configured="
                            + configured.size()
                            + ", expected="
                            + ConsultationCategory
                            .values()
                            .length
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
                        "ConsultationCategory Evidence Policy가 누락되었습니다: "
                                + category
                );
            }
        }

        this.policies =
                Collections.unmodifiableMap(
                        configured
                );
    }


    /*
     * =================================================
     * 공식 조회 API
     * =================================================
     */

    public ConsultationCategoryEvidencePolicy policyOf(
            ConsultationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "ConsultationCategory가 필요합니다."
        );

        ConsultationCategoryEvidencePolicy policy =
                policies.get(
                        category
                );

        if (
                policy == null
        ) {

            throw new IllegalArgumentException(
                    "ConsultationCategory Evidence Policy를 찾을 수 없습니다: "
                            + category
            );
        }

        return policy;
    }


    public boolean acceptsNatalDomain(

            ConsultationCategory category,

            LifeDomain domain

    ) {

        return policyOf(
                category
        )
                .acceptsNatalDomain(
                        domain
                );
    }


    public boolean acceptsSegmentDomain(

            ConsultationCategory category,

            LifeDomain domain

    ) {

        return policyOf(
                category
        )
                .acceptsSegmentDomain(
                        domain
                );
    }


    public boolean acceptsNatalRule(

            ConsultationCategory category,

            LifeDomain domain,

            String ruleCode

    ) {

        return policyOf(
                category
        )
                .acceptsNatalRule(
                        domain,
                        ruleCode
                );
    }


    public boolean acceptsSegmentRule(

            ConsultationCategory category,

            LifeDomain domain,

            String ruleCode

    ) {

        return policyOf(
                category
        )
                .acceptsSegmentRule(
                        domain,
                        ruleCode
                );
    }


    public Map<
            ConsultationCategory,
            ConsultationCategoryEvidencePolicy
            > allPolicies() {

        return policies;
    }


    /*
     * =================================================
     * Domain 전체 Routing Policy Builder
     * =================================================
     */

    private static ConsultationCategoryEvidencePolicy policy(

            ConsultationCategory category,

            EnumSet<LifeDomain> natalDomains,

            EnumSet<LifeDomain> segmentDomains,

            boolean requiresPastTimelineRange,

            boolean requiresFutureTimelineRange,

            boolean requiresDedicatedCrossDomainRule

    ) {

        return new ConsultationCategoryEvidencePolicy(
                category,
                natalDomains,
                segmentDomains,
                requiresPastTimelineRange,
                requiresFutureTimelineRange,
                requiresDedicatedCrossDomainRule
        );
    }


    /*
     * =================================================
     * Selected RuleCode Policy Builder
     * =================================================
     */

    private static ConsultationCategoryEvidencePolicy policy(

            ConsultationCategory category,

            EnumSet<LifeDomain> natalDomains,

            EnumSet<LifeDomain> segmentDomains,

            Map<LifeDomain, Set<String>>
                    selectedNatalRuleCodes,

            Map<LifeDomain, Set<String>>
                    selectedSegmentRuleCodes,

            boolean requiresPastTimelineRange,

            boolean requiresFutureTimelineRange,

            boolean requiresDedicatedCrossDomainRule

    ) {

        return new ConsultationCategoryEvidencePolicy(
                category,
                natalDomains,
                segmentDomains,
                selectedNatalRuleCodes,
                selectedSegmentRuleCodes,
                requiresPastTimelineRange,
                requiresFutureTimelineRange,
                requiresDedicatedCrossDomainRule
        );
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
                            "LifeDomain은 null일 수 없습니다."
                    )
            );
        }

        return result;
    }


    /*
     * =================================================
     * Selected RuleCode Builder
     * =================================================
     */

    private static Map<LifeDomain, Set<String>>
    selectedRules(

            LifeDomain domain,

            String... ruleCodes

    ) {

        LifeDomain normalizedDomain =
                Objects.requireNonNull(
                        domain,
                        "Selected RuleCode에는 LifeDomain이 필요합니다."
                );

        return Map.of(
                normalizedDomain,
                ruleCodes(
                        ruleCodes
                )
        );
    }


    /*
     * =================================================
     * Multi-Domain Selected RuleCode Builder
     * =================================================
     */

    private static Map<LifeDomain, Set<String>>
    selectedRulesByDomain(
            Map<LifeDomain, Set<String>> source
    ) {

        Objects.requireNonNull(
                source,
                "Selected RuleCode Domain Map이 필요합니다."
        );

        if (
                source.isEmpty()
        ) {

            return Map.of();
        }

        EnumMap<LifeDomain, Set<String>> result =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                Map.Entry<LifeDomain, Set<String>> entry
                : source.entrySet()
        ) {

            Map.Entry<LifeDomain, Set<String>> normalizedEntry =
                    Objects.requireNonNull(
                            entry,
                            "Selected RuleCode Domain Entry는 null일 수 없습니다."
                    );

            LifeDomain domain =
                    Objects.requireNonNull(
                            normalizedEntry.getKey(),
                            "Selected RuleCode Domain은 null일 수 없습니다."
                    );

            Set<String> sourceRuleCodes =
                    Objects.requireNonNull(
                            normalizedEntry.getValue(),
                            "Selected RuleCode Set은 null일 수 없습니다: "
                                    + domain
                    );

            if (
                    sourceRuleCodes.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Selected RuleCode Set은 비어 있을 수 없습니다: "
                                + domain
                );
            }

            LinkedHashSet<String> normalizedRuleCodes =
                    new LinkedHashSet<>();

            for (
                    String ruleCode
                    : sourceRuleCodes
            ) {

                Objects.requireNonNull(
                        ruleCode,
                        "Selected RuleCode는 null일 수 없습니다: "
                                + domain
                );

                String normalized =
                        ruleCode.trim();

                if (
                        normalized.isBlank()
                ) {

                    throw new IllegalArgumentException(
                            "Selected RuleCode는 비어 있을 수 없습니다: "
                                    + domain
                    );
                }

                if (
                        !normalizedRuleCodes.add(
                                normalized
                        )
                ) {

                    throw new IllegalArgumentException(
                            "Selected RuleCode가 중복되었습니다: "
                                    + domain
                                    + " / "
                                    + normalized
                    );
                }
            }

            Set<String> previous =
                    result.put(
                            domain,
                            Collections.unmodifiableSet(
                                    normalizedRuleCodes
                            )
                    );

            if (
                    previous != null
            ) {

                throw new IllegalArgumentException(
                        "Selected RuleCode Domain이 중복되었습니다: "
                                + domain
                );
            }
        }

        return Collections.unmodifiableMap(
                result
        );
    }


    /*
     * =================================================
     * RuleCode Set Builder
     * =================================================
     */

    private static Set<String> ruleCodes(
            String... ruleCodes
    ) {

        if (
                ruleCodes == null
                        ||
                        ruleCodes.length == 0
        ) {

            throw new IllegalArgumentException(
                    "Selected RuleCode에는 최소 하나의 ruleCode가 필요합니다."
            );
        }

        LinkedHashSet<String> normalizedRuleCodes =
                new LinkedHashSet<>();

        for (
                String ruleCode
                : ruleCodes
        ) {

            Objects.requireNonNull(
                    ruleCode,
                    "Selected RuleCode는 null일 수 없습니다."
            );

            String normalized =
                    ruleCode.trim();

            if (
                    normalized.isBlank()
            ) {

                throw new IllegalArgumentException(
                        "Selected RuleCode는 비어 있을 수 없습니다."
                );
            }

            if (
                    !normalizedRuleCodes.add(
                            normalized
                    )
            ) {

                throw new IllegalArgumentException(
                        "Selected RuleCode가 중복되었습니다: "
                                + normalized
                );
            }
        }

        return Collections.unmodifiableSet(
                normalizedRuleCodes
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
                    ConsultationCategoryEvidencePolicy
                    > target,

            ConsultationCategoryEvidencePolicy policy

    ) {

        Objects.requireNonNull(
                target,
                "Policy Registry Map이 필요합니다."
        );

        Objects.requireNonNull(
                policy,
                "ConsultationCategoryEvidencePolicy가 필요합니다."
        );

        ConsultationCategoryEvidencePolicy previous =
                target.put(
                        policy.category(),
                        policy
                );

        if (
                previous != null
        ) {

            throw new IllegalStateException(
                    "ConsultationCategory Evidence Policy가 중복 정의되었습니다: "
                            + policy.category()
            );
        }
    }
}