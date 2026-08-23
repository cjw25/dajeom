package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationGroundingConstraint;
import com.dajeom.type.LifeDomain;

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
 * Grounding RuleCode 선택 경계 확장.
 *
 * 하나의 ConsultationCategory를
 * LLM이 설명할 때 적용할 Grounding Policy.
 *
 * =================================================
 * allowedDomains
 * =================================================
 *
 * 해당 상담 Category가
 * 전체 Rule을 사용할 수 있는 LifeDomain.
 *
 * 예:
 *
 * WEALTH
 * ->
 * LifeDomain.WEALTH 전체
 *
 * =================================================
 * selectedRuleCodes
 * =================================================
 *
 * 해당 Category가
 * 특정 LifeDomain 전체를 허용하지 않고
 * 그 Domain의 일부 공식 RuleCode만
 * 사용할 때 적용한다.
 *
 * 예:
 *
 * CAREER
 *
 * ->
 *
 * CAREER Domain 전체
 *
 * +
 *
 * LEARNING_GROWTH 중
 * 정확히 선별된 RuleCode만 허용
 *
 * =================================================
 * 왜 필요한가
 * =================================================
 *
 * 단순히:
 *
 * CAREER allowedDomains
 * =
 * {CAREER, LEARNING_GROWTH}
 *
 * 로 만들면
 * LEARNING_GROWTH의 모든 Rule이
 * Grounding 경계를 통과하게 된다.
 *
 * 하지만 Evidence Routing에서 실제로
 * 선별한 것은 일부 RuleCode뿐이다.
 *
 * 따라서 Grounding Policy 역시
 * 동일한 RuleCode 경계를 가져야 한다.
 *
 * =================================================
 * timelineAllowed
 * =================================================
 *
 * 현재 Timeline Scenario 또는
 * Timeline Year Grounding을
 * 해당 상담 Category에서 사용할 수 있는지.
 *
 * =================================================
 * constraints
 * =================================================
 *
 * 자연어 설명 과정에서 반드시 지켜야 할
 * 안전/정밀화 제약조건.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 객체는:
 *
 * - 점수를 계산하지 않는다.
 * - 명리 결과를 다시 판정하지 않는다.
 * - Status를 변경하지 않는다.
 * - 사용자 문장을 생성하지 않는다.
 * - 서로 다른 Evidence를 합성하지 않는다.
 * - 서로 다른 Rule의 관계를 생성하지 않는다.
 *
 * 단지:
 *
 * "이 Category Grounding에서
 * 어떤 Domain / RuleCode를 사용할 수 있는가"
 *
 * 를 구조적으로 표현한다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * selectedRuleCodes에 여러 Rule이
 * 등록되어 있다는 이유로:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 같이 있으므로 C다
 * - 여러 Rule이 같은 방향이므로 더 강하다
 *
 * 같은 의미를 만들지 않는다.
 *
 * RuleCode 선택은 Routing / Validation 경계일 뿐이다.
 *
 * =================================================
 * full Domain과 selected Rule의 차이
 * =================================================
 *
 * allowedDomains에 포함:
 *
 * ->
 *
 * 해당 LifeDomain의 모든 정상 RuleCode 허용.
 *
 * -------------------------------------------------
 *
 * selectedRuleCodes에 포함:
 *
 * ->
 *
 * 해당 LifeDomain에서는
 * 등록된 정확한 RuleCode만 허용.
 *
 * =================================================
 * RuleCode 기본 경계
 * =================================================
 *
 * Full Domain 여부와 관계없이:
 *
 * - null RuleCode
 * - blank RuleCode
 *
 * 는 허용하지 않는다.
 *
 * Full Domain은
 * "RuleCode 검증이 필요 없다"는 뜻이 아니라
 * "정상적인 RuleCode라면 그 Domain의
 * 모든 RuleCode를 허용한다"는 뜻이다.
 *
 * =================================================
 * 중복 정책 금지
 * =================================================
 *
 * 동일 LifeDomain을:
 *
 * allowedDomains
 *
 * 와
 *
 * selectedRuleCodes
 *
 * 양쪽에 동시에 등록하지 않는다.
 *
 * 전체 Domain을 허용하면서
 * 일부 Rule도 따로 선택하는 것은
 * 의미상 중복이고 경계를 모호하게 만든다.
 */
public record ConsultationGroundingPolicy(

        ConsultationCategory category,

        Set<LifeDomain> allowedDomains,

        Map<LifeDomain, Set<String>>
        selectedRuleCodes,

        boolean timelineAllowed,

        Set<ConsultationGroundingConstraint> constraints

) {

    /*
     * =================================================
     * Full Domain Constructor
     * =================================================
     *
     * 특정 LifeDomain 전체를 허용하는 Policy에서
     * selectedRuleCodes를 별도로 사용하지 않을 때
     * 사용하는 생성자다.
     */

    public ConsultationGroundingPolicy(

            ConsultationCategory category,

            Set<LifeDomain> allowedDomains,

            boolean timelineAllowed,

            Set<ConsultationGroundingConstraint> constraints

    ) {

        this(
                category,
                allowedDomains,
                Map.of(),
                timelineAllowed,
                constraints
        );
    }


    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

    public ConsultationGroundingPolicy {

        Objects.requireNonNull(
                category,
                "Grounding Policy의 ConsultationCategory는 필수입니다."
        );

        allowedDomains =
                immutableDomains(
                        allowedDomains
                );

        selectedRuleCodes =
                immutableSelectedRuleCodes(
                        selectedRuleCodes
                );

        constraints =
                immutableConstraints(
                        constraints
                );

        /*
         * =================================================
         * Full Domain + Selected Rule 중복 금지
         * =================================================
         */

        for (
                LifeDomain selectedDomain
                : selectedRuleCodes.keySet()
        ) {

            if (
                    allowedDomains.contains(
                            selectedDomain
                    )
            ) {

                throw new IllegalArgumentException(
                        "Grounding Policy에서 동일 LifeDomain을 "
                                + "allowedDomains와 selectedRuleCodes에 "
                                + "동시에 등록할 수 없습니다. category="
                                + category
                                + ", domain="
                                + selectedDomain
                );
            }
        }

        /*
         * =================================================
         * 모든 상담 분야에 필요한 기본 제약
         * =================================================
         */

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint
                        .NO_NEW_SAJU_CALCULATION
        );

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint
                        .NO_DIRECT_EVENT_ASSERTION
        );

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint
                        .PRESERVE_MIXED_EVIDENCE
        );

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint
                        .PRESERVE_TIMELINE_UNCERTAINTY
        );

        requireConstraint(
                constraints,
                ConsultationGroundingConstraint
                        .AUXILIARY_NOT_STANDALONE
        );
    }


    /*
     * =================================================
     * Full Domain 허용
     * =================================================
     */

    public boolean allowsDomain(
            LifeDomain domain
    ) {

        return domain != null
                && allowedDomains.contains(
                domain
        );
    }


    /*
     * =================================================
     * Selected Rule 존재
     * =================================================
     */

    public boolean hasSelectedRules() {

        return !selectedRuleCodes
                .isEmpty();
    }


    public boolean hasSelectedRulesFor(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return false;
        }

        Set<String> ruleCodes =
                selectedRuleCodes.get(
                        domain
                );

        return ruleCodes != null
                && !ruleCodes.isEmpty();
    }


    /*
     * =================================================
     * Grounding Rule 허용
     * =================================================
     *
     * 1.
     * domain과 ruleCode는 반드시 유효해야 한다.
     *
     * 2.
     * Domain 전체가 허용되면
     * 해당 Domain의 모든 정상 RuleCode 허용.
     *
     * 3.
     * Domain 전체가 허용되지 않으면
     * selectedRuleCodes에서
     * 정확한 RuleCode 일치가 있어야 허용.
     *
     * 문자열 prefix / contains 판정은 하지 않는다.
     */

    public boolean allowsRule(

            LifeDomain domain,

            String ruleCode

    ) {

        /*
         * =================================================
         * 기본 입력 경계
         * =================================================
         */

        if (
                domain == null
        ) {

            return false;
        }

        if (
                ruleCode == null
        ) {

            return false;
        }

        String normalizedRuleCode =
                ruleCode.trim();

        if (
                normalizedRuleCode.isBlank()
        ) {

            return false;
        }

        /*
         * =================================================
         * Full Domain
         * =================================================
         */

        if (
                allowsDomain(
                        domain
                )
        ) {

            return true;
        }

        /*
         * =================================================
         * Selected Rule
         * =================================================
         */

        Set<String> selected =
                selectedRuleCodes.get(
                        domain
                );

        return selected != null
                && selected.contains(
                normalizedRuleCode
        );
    }


    /*
     * =================================================
     * Domain이 어떤 방식으로든 허용되는가
     * =================================================
     *
     * Full Domain 또는 Selected Rule Domain.
     *
     * 이것은 Rule 자체 허용을 의미하지 않는다.
     *
     * 실제 Evidence 검증에는
     * allowsRule(domain, ruleCode)를 사용해야 한다.
     */

    public boolean hasGroundingAccessForDomain(
            LifeDomain domain
    ) {

        return allowsDomain(
                domain
        )
                || hasSelectedRulesFor(
                domain
        );
    }


    /*
     * =================================================
     * Constraint
     * =================================================
     */

    public boolean hasConstraint(
            ConsultationGroundingConstraint constraint
    ) {

        return constraint != null
                && constraints.contains(
                constraint
        );
    }


    /*
     * =================================================
     * Immutable Domain
     * =================================================
     */

    private static Set<LifeDomain>
    immutableDomains(
            Set<LifeDomain> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Set.of();
        }

        EnumSet<LifeDomain> copied =
                EnumSet.noneOf(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : source
        ) {

            copied.add(
                    Objects.requireNonNull(
                            domain,
                            "Grounding Policy의 LifeDomain은 null일 수 없습니다."
                    )
            );
        }

        return Collections.unmodifiableSet(
                copied
        );
    }


    /*
     * =================================================
     * Immutable Selected RuleCode
     * =================================================
     */

    private static Map<LifeDomain, Set<String>>
    immutableSelectedRuleCodes(

            Map<LifeDomain, Set<String>> source

    ) {

        if (
                source == null
                        || source.isEmpty()
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
                            "Grounding selectedRuleCodes Entry는 null일 수 없습니다."
                    );

            LifeDomain domain =
                    Objects.requireNonNull(
                            normalizedEntry.getKey(),
                            "Grounding selectedRuleCodes의 LifeDomain은 null일 수 없습니다."
                    );

            Set<String> sourceRuleCodes =
                    Objects.requireNonNull(
                            normalizedEntry.getValue(),
                            "Grounding selectedRuleCodes의 RuleCode Set은 null일 수 없습니다. domain="
                                    + domain
                    );

            if (
                    sourceRuleCodes.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Grounding selectedRuleCodes의 RuleCode Set은 비어 있을 수 없습니다. domain="
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
                        "Grounding selected RuleCode는 null일 수 없습니다. domain="
                                + domain
                );

                String normalized =
                        ruleCode.trim();

                if (
                        normalized.isBlank()
                ) {

                    throw new IllegalArgumentException(
                            "Grounding selected RuleCode는 비어 있을 수 없습니다. domain="
                                    + domain
                    );
                }

                if (
                        !normalizedRuleCodes.add(
                                normalized
                        )
                ) {

                    throw new IllegalArgumentException(
                            "Grounding selected RuleCode가 중복되었습니다. domain="
                                    + domain
                                    + ", ruleCode="
                                    + normalized
                    );
                }
            }

            Set<String> previous =
                    result.put(
                            domain,
                            Collections.unmodifiableSet(
                                    new LinkedHashSet<>(
                                            normalizedRuleCodes
                                    )
                            )
                    );

            if (
                    previous != null
            ) {

                throw new IllegalArgumentException(
                        "Grounding selectedRuleCodes의 LifeDomain이 중복되었습니다: "
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
     * Immutable Constraint
     * =================================================
     */

    private static Set<ConsultationGroundingConstraint>
    immutableConstraints(
            Set<ConsultationGroundingConstraint> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Set.of();
        }

        EnumSet<ConsultationGroundingConstraint> copied =
                EnumSet.noneOf(
                        ConsultationGroundingConstraint.class
                );

        for (
                ConsultationGroundingConstraint constraint
                : source
        ) {

            copied.add(
                    Objects.requireNonNull(
                            constraint,
                            "Grounding Constraint는 null일 수 없습니다."
                    )
            );
        }

        return Collections.unmodifiableSet(
                copied
        );
    }


    /*
     * =================================================
     * 필수 Constraint
     * =================================================
     */

    private static void requireConstraint(

            Set<ConsultationGroundingConstraint> constraints,

            ConsultationGroundingConstraint required

    ) {

        if (
                !constraints.contains(
                        required
                )
        ) {

            throw new IllegalArgumentException(
                    "필수 Grounding Constraint가 누락되었습니다: "
                            + required
            );
        }
    }
}