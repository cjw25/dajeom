package com.dajeom.constant;

import com.dajeom.model.ConsultationCategoryUserMeaningPolicy;
import com.dajeom.type.ConsultationCategory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Category deterministic NATAL Rule의
 * 사용자 Meaning 직접 출력 권한 Registry.
 *
 * =================================================
 * Fail Closed
 * =================================================
 *
 * 감사 대상 Category의 NATAL Rule은
 * 반드시 이 Registry에:
 *
 * - allow
 * 또는
 * - block
 *
 * 으로 명시되어야 한다.
 *
 * 등록되지 않은 새로운 Rule은
 * 자동으로 사용자 Meaning이 되지 않는다.
 *
 * =================================================
 * 감사 대상
 * =================================================
 *
 * - LOVE_STYLE
 * - PARTNER_LUCK
 * - SOCIAL_RELATIONSHIP
 * - WEALTH
 * - CAREER
 * - LEGAL_RISK
 * - HEALTH_TENDENCY
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 내부 계산을 없애는 Registry가 아니다.
 *
 * directOutput=false라도:
 *
 * - deterministic calculation
 * - evidence
 * - provenance
 * - dependency
 *
 * 는 그대로 유지된다.
 *
 * 사용자 Story Meaning으로의 승격만 차단한다.
 */
public final class ConsultationCategoryUserMeaningPolicyRegistry {

    private static final Set<ConsultationCategory>
            AUDITED_CATEGORIES =
            Set.of(
                    ConsultationCategory.LOVE_STYLE,
                    ConsultationCategory.PARTNER_LUCK,
                    ConsultationCategory.SOCIAL_RELATIONSHIP,
                    ConsultationCategory.WEALTH,
                    ConsultationCategory.CAREER,
                    ConsultationCategory.LEGAL_RISK,
                    ConsultationCategory.HEALTH_TENDENCY
            );

    private static final List<String>
            RELATIONSHIP_FORBIDDEN =
            List.of(
                    "연애 시작·이별·재회·결혼 같은 실제 관계 사건으로 확대하지 말 것.",
                    "상대방의 성격이나 행동을 확정하지 말 것.",
                    "관계 구조 하나만으로 관계의 성공·실패를 단정하지 말 것."
            );

    private static final List<String>
            WEALTH_FORBIDDEN =
            List.of(
                    "실제 소득·수익·손실·투자·횡재·재산 사건으로 확대하지 말 것.",
                    "재성 구조의 존재를 돈이 많거나 적다는 결과로 변환하지 말 것.",
                    "특정 투자·사업·재테크 행동을 권하지 말 것."
            );

    private static final List<String>
            CAREER_FORBIDDEN =
            List.of(
                    "취업·승진·퇴사·이직·해고 같은 실제 직업 사건으로 확대하지 말 것.",
                    "특정 직업이나 직종을 자동 추천하지 말 것.",
                    "구조 하나만으로 직업 성공·실패를 단정하지 말 것."
            );

    private static final List<String>
            HEALTH_FORBIDDEN =
            List.of(
                    "질병·발병·진단·치료·입원·수술로 확대하지 말 것.",
                    "특정 장기의 의학적 상태를 단정하지 말 것.",
                    "수명이나 의료적 건강 상태를 예측하지 말 것."
            );

    private static final Map<String, ConsultationCategoryUserMeaningPolicy>
            POLICIES =
            createPolicies();

    private ConsultationCategoryUserMeaningPolicyRegistry() {
    }

    public static boolean audits(
            ConsultationCategory category
    ) {

        return category != null
                &&
                AUDITED_CATEGORIES.contains(
                        category
                );
    }

    /**
     * 감사 대상 NATAL Rule은 반드시 등록되어 있어야 한다.
     *
     * 새로운 Rule이 추가됐는데 Policy 등록이 없다면
     * 조용히 internal summary를 사용자 Meaning으로 쓰지 않고
     * 즉시 실패시킨다.
     */
    public static ConsultationCategoryUserMeaningPolicy requireAudited(

            ConsultationCategory category,

            String ruleCode

    ) {

        Objects.requireNonNull(
                category,
                "Category User Meaning Policy 조회에는 category가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        ruleCode,
                        "Category User Meaning Policy 조회에는 ruleCode가 필요합니다."
                );

        if (
                !audits(
                        category
                )
        ) {

            throw new IllegalArgumentException(
                    "Category User Meaning 감사 대상이 아닙니다: "
                            + category
            );
        }

        ConsultationCategoryUserMeaningPolicy policy =
                POLICIES.get(
                        key(
                                category,
                                normalizedRuleCode
                        )
                );

        if (
                policy == null
        ) {

            throw new IllegalStateException(
                    "감사되지 않은 Category NATAL Rule이 "
                            + "사용자 Meaning 경계에 진입했습니다. category="
                            + category
                            + ", ruleCode="
                            + normalizedRuleCode
            );
        }

        return policy;
    }

    private static Map<String, ConsultationCategoryUserMeaningPolicy>
    createPolicies() {

        LinkedHashMap<
                String,
                ConsultationCategoryUserMeaningPolicy
                > result =
                new LinkedHashMap<>();

        /*
         * =================================================
         * LOVE_STYLE
         * =================================================
         */

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.allow(
                        ConsultationCategory.LOVE_STYLE,
                        "LOVE_STYLE_NATAL_RELATION_OVERLAP",
                        "가까운 관계에서 연결 방식에 영향을 주는 기본 관계 구조",
                        "실제로 계산된 관계 구조를 보존하되 연애 사건이나 상대방 특성으로 확대하지 않습니다.",
                        RELATIONSHIP_FORBIDDEN
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.LOVE_STYLE,
                        "LOVE_STYLE_CLOSENESS_RESPONSE",
                        "관계 구조의 deterministic status를 '친밀감이 높아질 때의 반응'이라는 "
                                + "별도 심리 Meaning으로 승격시키지 않습니다."
                )
        );

        /*
         * =================================================
         * PARTNER_LUCK
         * =================================================
         *
         * 현재 Rule들은:
         *
         * - 일반 관계망
         * - 재성/관성 구조
         * - 역할/책임
         *
         * 을 PARTNER_LUCK Category로 옮긴 결과다.
         *
         * 하지만 배우자성 선택 Contract와
         * 배우자 사건 Contract 없이
         * 배우자복/결혼운 Meaning으로 직접 출력하지 않는다.
         */

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.PARTNER_LUCK,
                        "PARTNER_LUCK_NATAL_RELATION_OVERLAP",
                        "일반 관계망 구조만으로 배우자운을 직접 판단하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.PARTNER_LUCK,
                        "PARTNER_LUCK_PRIMARY_WEALTH_OFFICER_STRUCTURE",
                        "일반 재성·관성 구조를 배우자성으로 자동 치환하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.PARTNER_LUCK,
                        "PARTNER_LUCK_RESPONSIBILITY_PRESSURE",
                        "역할·책임 구조를 배우자 관계의 부담이나 안정으로 자동 확대하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.PARTNER_LUCK,
                        "PARTNER_LUCK_LONG_TERM_STRUCTURE",
                        "일반 관계망 구조를 장기 관계 결과나 결혼 지속성으로 자동 확대하지 않습니다."
                )
        );

        /*
         * =================================================
         * SOCIAL_RELATIONSHIP
         * =================================================
         *
         * 현재 NATAL Rule은 대부분
         * RELATIONSHIP Domain의 DAY 관계망 또는
         * 재성/관성 구조를 다시 Social Category로 옮긴 것이다.
         *
         * 이를 일반 인간관계 성격으로 자동 승격하지 않는다.
         */

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.SOCIAL_RELATIONSHIP,
                        "SOCIAL_RELATIONSHIP_NATAL_RELATION_OVERLAP",
                        "가까운 관계에 사용된 DAY 관계망 구조를 일반 사회관계 전체의 성향으로 확대하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.SOCIAL_RELATIONSHIP,
                        "SOCIAL_RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE",
                        "재성·관성 중심 구조를 일반 인간관계 특성으로 자동 치환하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.SOCIAL_RELATIONSHIP,
                        "SOCIAL_RELATIONSHIP_EXPECTATION_PRESSURE",
                        "재성·관성 구조를 타인의 기대나 대인 압박이라는 별도 심리 Meaning으로 확대하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.SOCIAL_RELATIONSHIP,
                        "SOCIAL_RELATIONSHIP_INTERACTION_PATTERN",
                        "DAY 관계망 구조를 일반적인 사람 대 사람의 상호작용 성격으로 확대하지 않습니다."
                )
        );

        /*
         * =================================================
         * WEALTH
         * =================================================
         */

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.allow(
                        ConsultationCategory.WEALTH,
                        "WEALTH_CAPACITY_STRUCTURE",
                        "현실 자원과 결과를 감당하고 운용하는 기본 수용 여력",
                        "재성 구조와 일간의 수용 가능 범위를 직접 평가한 계산이므로 재물 영역의 구조 Meaning으로 허용합니다.",
                        WEALTH_FORBIDDEN
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.allow(
                        ConsultationCategory.WEALTH,
                        "WEALTH_PRIMARY_WEALTH_STRUCTURE_CONTEXT",
                        "현실 자원과 결과를 다루는 기능이 삶의 중심 구조에 관여하는 정도",
                        "정재·편재 계열이 실제 중심 구조에 관여하는지 계산한 결과만 보존합니다.",
                        WEALTH_FORBIDDEN
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.WEALTH,
                        "WEALTH_CORE_STRUCTURE_CAPACITY",
                        "Primary Wealth Structure와 Capacity를 별도 사용자 Meaning으로 다시 결합하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.WEALTH,
                        "WEALTH_CROSS_VALIDATED_PATTERN_CONTEXT",
                        "실제 Pattern 종류가 사용자 Meaning 경계까지 보존되지 않은 generic Pattern Context는 직접 출력하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.WEALTH,
                        "WEALTH_FLOW_PRESSURE_PATTERN",
                        "여러 재물 관련 기능의 흐름을 프로젝트식 종합 Pattern Meaning으로 직접 출력하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.WEALTH,
                        "WEALTH_FUNCTION_DISTRIBUTION_CONTEXT",
                        "5개 기능축 수준의 분포 Context는 정확한 10십성 의미보다 상위 사용자 Meaning으로 사용하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.WEALTH,
                        "WEALTH_PALACE_DISTRIBUTION_CONTEXT_VIEW",
                        "궁위 배치의 위치 Context만으로 재물의 방향이나 결과를 직접 설명하지 않습니다."
                )
        );

        /*
         * =================================================
         * CAREER
         * =================================================
         */

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.allow(
                        ConsultationCategory.CAREER,
                        "CAREER_CORE_ROLE_STRUCTURE",
                        "규칙과 역할, 책임을 받아들이고 운용하는 기능이 삶의 중심 구조에 관여하는 정도",
                        "관성 계열의 중심 구조 관여를 역할·책임 구조 수준에서만 설명합니다.",
                        CAREER_FORBIDDEN
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.allow(
                        ConsultationCategory.CAREER,
                        "CAREER_LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE_CONTEXT",
                        "정보를 받아들이고 정리하거나 표현·산출하는 기능이 삶의 중심 구조에 관여하는 정도",
                        "인성·식상 계열이 실제 중심 구조에 관여하는 경우에 한해 직업 영역의 기능 Context로 사용합니다.",
                        CAREER_FORBIDDEN
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.CAREER,
                        "CAREER_PRIMARY_OFFICER_STRUCTURE_CONTEXT",
                        "CAREER_CORE_ROLE_STRUCTURE와 동일한 관성 중심 근거를 중복 사용자 Meaning으로 만들지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.CAREER,
                        "CAREER_OFFICER_STRUCTURE_CONTEXT",
                        "관성 구조의 보조 Context를 별도의 직업 성격이나 직업 결과로 승격시키지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.CAREER,
                        "CAREER_CROSS_VALIDATED_PATTERN_CONTEXT",
                        "실제 전통 Pattern 종류가 사용자 Meaning 경계에 보존되지 않은 generic Pattern Context는 직접 출력하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.CAREER,
                        "CAREER_FUNCTION_INTEGRATION_PATTERN",
                        "여러 기능축의 동시 존재를 새로운 직업 적성 Pattern으로 합성하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.CAREER,
                        "CAREER_ENVIRONMENTAL_OPERATION_CONTEXT",
                        "궁위·환경 Context 자체를 직업 적성이나 직업 결과로 직접 변환하지 않습니다."
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.CAREER,
                        "CAREER_LEARNING_RESOURCE_OUTPUT_CONTEXT",
                        "인성·식상 일반 기능 분포를 새로운 학습·직업 적성 Meaning으로 자동 합성하지 않습니다."
                )
        );

        /*
         * =================================================
         * HEALTH_TENDENCY
         * =================================================
         */

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.allow(
                        ConsultationCategory.HEALTH_TENDENCY,
                        "HEALTH_BASELINE_BALANCE_CONTEXT",
                        "생활 리듬과 컨디션을 지탱하는 기본적인 균형과 소모 여력",
                        "기본 균형 구조만 건강 경향의 Context로 사용하며 의료적 상태로 변환하지 않습니다.",
                        HEALTH_FORBIDDEN
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.allow(
                        ConsultationCategory.HEALTH_TENDENCY,
                        "HEALTH_CLIMATE_ADJUSTMENT_PRESSURE",
                        "생활 리듬의 균형을 맞추기 위해 조정이 필요한 정도",
                        "기존 균형 조정 계산의 부담 방향만 보존하고 질병 의미를 부여하지 않습니다.",
                        HEALTH_FORBIDDEN
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.allow(
                        ConsultationCategory.HEALTH_TENDENCY,
                        "HEALTH_ADJUSTMENT_DIRECTION_CONTEXT",
                        "생활 리듬의 균형을 유지할 때 보완이 필요한 방향",
                        "기존 조정 방향 계산을 생활 균형 Context로만 설명합니다.",
                        HEALTH_FORBIDDEN
                )
        );

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.HEALTH_TENDENCY,
                        "HEALTH_CLIMATE_DISEASE_FLOW_CONTEXT",
                        "내부 명칭과 계산 흐름에 질병 관련 표현이 포함된 Context를 "
                                + "사용자 의료적 건강 Meaning으로 직접 승격하지 않습니다."
                )
        );

        /*
         * =================================================
         * LEGAL_RISK
         * =================================================
         *
         * 복수 생활영역의 PRESSURE 근거가
         * 동시에 존재한다는 이유만으로
         * '법적 위험'을 사용자에게 예측하지 않는다.
         */

        register(
                result,
                ConsultationCategoryUserMeaningPolicy.block(
                        ConsultationCategory.LEGAL_RISK,
                        "LEGAL_RISK_CROSS_ESTABLISHED",
                        "재물·직업·관계 영역의 주의 근거가 복수로 존재한다는 사실만으로 "
                                + "소송·분쟁·처벌 같은 법적 위험을 별도 사용자 Meaning으로 만들지 않습니다."
                )
        );

        return Map.copyOf(
                result
        );
    }

    private static void register(

            Map<String, ConsultationCategoryUserMeaningPolicy> target,

            ConsultationCategoryUserMeaningPolicy policy

    ) {

        String key =
                key(
                        policy.category(),
                        policy.ruleCode()
                );

        ConsultationCategoryUserMeaningPolicy previous =
                target.put(
                        key,
                        policy
                );

        if (
                previous != null
        ) {

            throw new IllegalStateException(
                    "Category User Meaning Policy가 중복 등록되었습니다: "
                            + key
            );
        }
    }

    private static String key(

            ConsultationCategory category,

            String ruleCode

    ) {

        return category.name()
                + "|"
                + ruleCode;
    }

    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}