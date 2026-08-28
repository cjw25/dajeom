package com.dajeom.constant;

import com.dajeom.model.ConsultationTimelineUserMeaningPolicy;
import com.dajeom.model.ConsultationTimelineUserMeaningPolicy.TimelineStage;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 107~111 Timeline User Meaning Audit Registry.
 *
 * CURRENT_TIMELINE / TIMELINE_YEAR의 내부 summary를
 * 사용자 Meaning으로 직접 사용하지 않기 위한 fail-closed 정책이다.
 *
 * 등록되지 않은 Timeline Rule은
 * 사용자 Meaning 경계에 들어오면 즉시 실패한다.
 *
 * =================================================
 * 사용자 표현 원칙
 * =================================================
 *
 * 사용자 Meaning에는:
 *
 * - 대운
 * - 세운
 * - 월운
 *
 * 같은 내부 명리 용어를 직접 노출하지 않는다.
 *
 * 대신:
 *
 * - 장기 흐름
 * - 연간 흐름
 * - 월간 흐름
 *
 * 으로 표현한다.
 *
 * 내부 계산 provenance의 TimelineStage와 RuleCode는
 * 그대로 유지한다.
 */
public final class ConsultationTimelineUserMeaningPolicyRegistry {

    private static final List<String> GENERAL_TIMELINE_FORBIDDEN =
            List.of(
                    "지지·부담·혼재·Context 방향을 실제 사건의 발생이나 결과로 확정하지 말 것.",
                    "대운·세운·월운을 A 때문에 B, A 이후 B 같은 인과·순서 관계로 연결하지 말 것.",
                    "앞선 시간층의 상태가 뒤 시간층을 자동 강화·약화·상쇄한다고 말하지 말 것.",
                    "서로 다른 생활영역이 함께 존재한다는 이유만으로 새로운 사건이나 성격을 만들지 말 것."
            );

    private static final List<String> WEALTH_FORBIDDEN =
            merge(
                    GENERAL_TIMELINE_FORBIDDEN,
                    List.of(
                            "재물 방향을 실제 소득·수익·손실·투자 성공·사업 성공·횡재·재산 사건으로 확대하지 말 것.",
                            "재성이나 재물영역의 활성 자체를 돈이 많아지거나 적어진다는 결과로 변환하지 말 것."
                    )
            );

    private static final List<String> CAREER_FORBIDDEN =
            merge(
                    GENERAL_TIMELINE_FORBIDDEN,
                    List.of(
                            "직업 방향을 취업·승진·이직·퇴사·해고·권력 획득 같은 실제 사건으로 확대하지 말 것.",
                            "관성이나 일·책임영역의 활성 자체를 특정 직업 성취로 변환하지 말 것."
                    )
            );

    private static final List<String> RELATIONSHIP_FORBIDDEN =
            merge(
                    GENERAL_TIMELINE_FORBIDDEN,
                    List.of(
                            "관계 방향을 연애 시작·이별·재회·결혼·배우자 등장 같은 실제 관계 사건으로 확대하지 말 것.",
                            "재성·관성·관계영역의 활성 자체를 배우자성 또는 좋은 인연의 도착으로 자동 치환하지 말 것."
                    )
            );

    private static final List<String> HEALTH_FORBIDDEN =
            merge(
                    GENERAL_TIMELINE_FORBIDDEN,
                    List.of(
                            "생활균형 방향을 질병·발병·진단·치료·입원·수술·장기 상태로 확대하지 말 것.",
                            "명리상 부담 방향을 의료적 건강 상태나 수명 예측으로 변환하지 말 것."
                    )
            );

    private static final List<String> RANGE_YEAR_FORBIDDEN =
            merge(
                    GENERAL_TIMELINE_FORBIDDEN,
                    List.of(
                            "연도 전체 방향을 그 해의 성공·실패나 특정 사건 발생으로 변환하지 말 것.",
                            "전환점은 구조 변화 조건일 뿐 실제 이직·결혼·이별·수익·손실 같은 사건으로 구체화하지 말 것."
                    )
            );

    private static final Map<
            String,
            ConsultationTimelineUserMeaningPolicy
            > POLICIES =
            createPolicies();

    private ConsultationTimelineUserMeaningPolicyRegistry() {
    }

    public static ConsultationTimelineUserMeaningPolicy requireAudited(

            ConsultationCategory category,

            ConsultationCategoryDeterministicLayer layer,

            String ruleCode

    ) {

        Objects.requireNonNull(
                category,
                "Timeline User Meaning Policy 조회에는 category가 필요합니다."
        );

        Objects.requireNonNull(
                layer,
                "Timeline User Meaning Policy 조회에는 layer가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        ruleCode,
                        "Timeline User Meaning Policy 조회에는 ruleCode가 필요합니다."
                );

        if (
                layer
                        == ConsultationCategoryDeterministicLayer.NATAL
        ) {

            throw new IllegalArgumentException(
                    "Timeline User Meaning Policy는 NATAL Layer를 처리하지 않습니다."
            );
        }

        ConsultationTimelineUserMeaningPolicy policy =
                POLICIES.get(
                        key(
                                category,
                                layer,
                                normalizedRuleCode
                        )
                );

        if (
                policy == null
        ) {

            throw new IllegalStateException(
                    "감사되지 않은 Timeline Rule이 사용자 Meaning 경계에 진입했습니다. category="
                            + category
                            + ", layer="
                            + layer
                            + ", ruleCode="
                            + normalizedRuleCode
            );
        }

        return policy;
    }

    private static Map<
            String,
            ConsultationTimelineUserMeaningPolicy
            > createPolicies() {

        LinkedHashMap<
                String,
                ConsultationTimelineUserMeaningPolicy
                > result =
                new LinkedHashMap<>();

        registerCurrentWealth(
                result
        );

        registerCurrentCareer(
                result
        );

        registerCurrentHealth(
                result
        );

        registerCurrentPartner(
                result
        );

        registerCurrentSocial(
                result
        );

        registerCurrentLegalRisk(
                result
        );

        registerCurrentYearLuck(
                result
        );

        registerPastLuck(
                result
        );

        registerFutureLuck(
                result
        );

        return Map.copyOf(
                result
        );
    }

    /*
     * =================================================
     * WEALTH
     * =================================================
     */

    private static void registerCurrentWealth(
            Map<String, ConsultationTimelineUserMeaningPolicy> result
    ) {

        allow(
                result,
                ConsultationCategory.WEALTH,
                "WEALTH_CURRENT_DECADE_CONTEXT",
                TimelineStage.CURRENT_DECADE,
                "현재 장기 흐름의 재물·자원 구조",
                WEALTH_FORBIDDEN
        );

        allow(
                result,
                ConsultationCategory.WEALTH,
                "WEALTH_CURRENT_ANNUAL_CONTEXT",
                TimelineStage.CURRENT_YEAR,
                "현재 연간 흐름의 재물·자원 구조",
                WEALTH_FORBIDDEN
        );

        allow(
                result,
                ConsultationCategory.WEALTH,
                "WEALTH_CURRENT_ANNUAL_FUNCTIONAL",
                TimelineStage.CURRENT_YEAR,
                "현재 연간 흐름의 재물 관련 기능 작동",
                WEALTH_FORBIDDEN
        );

        block(
                result,
                ConsultationCategory.WEALTH,
                "WEALTH_CURRENT_STRUCTURAL_FLOW",
                TimelineStage.CURRENT_YEAR,
                "Annual Context를 다시 상위 재물 Meaning으로 중복 승격하지 않습니다."
        );

        allow(
                result,
                ConsultationCategory.WEALTH,
                "WEALTH_CURRENT_MONTHLY_CONTEXT",
                TimelineStage.CURRENT_MONTH,
                "현재 월간 흐름의 재물·자원 구조",
                WEALTH_FORBIDDEN
        );

        allow(
                result,
                ConsultationCategory.WEALTH,
                "WEALTH_CURRENT_MONTHLY_FUNCTIONAL",
                TimelineStage.CURRENT_MONTH,
                "현재 월간 흐름의 재물 관련 기능 작동",
                WEALTH_FORBIDDEN
        );

        block(
                result,
                ConsultationCategory.WEALTH,
                "WEALTH_CURRENT_MONTHLY_ACTIVATION",
                TimelineStage.CURRENT_MONTH,
                "Monthly Context를 다시 별도 재물 활성 Meaning으로 중복 승격하지 않습니다."
        );
    }

    /*
     * =================================================
     * CAREER
     * =================================================
     */

    private static void registerCurrentCareer(
            Map<String, ConsultationTimelineUserMeaningPolicy> result
    ) {

        allow(
                result,
                ConsultationCategory.CAREER,
                "CAREER_CURRENT_DECADE_CONTEXT",
                TimelineStage.CURRENT_DECADE,
                "현재 장기 흐름의 일·책임 구조",
                CAREER_FORBIDDEN
        );

        allow(
                result,
                ConsultationCategory.CAREER,
                "CAREER_CURRENT_ANNUAL_CONTEXT",
                TimelineStage.CURRENT_YEAR,
                "현재 연간 흐름의 일·책임 구조",
                CAREER_FORBIDDEN
        );

        allow(
                result,
                ConsultationCategory.CAREER,
                "CAREER_CURRENT_ANNUAL_FUNCTIONAL",
                TimelineStage.CURRENT_YEAR,
                "현재 연간 흐름의 일 관련 기능 작동",
                CAREER_FORBIDDEN
        );

        block(
                result,
                ConsultationCategory.CAREER,
                "CAREER_CURRENT_STRUCTURAL_FLOW",
                TimelineStage.CURRENT_YEAR,
                "Annual Context를 다시 상위 직업 Meaning으로 중복 승격하지 않습니다."
        );

        allow(
                result,
                ConsultationCategory.CAREER,
                "CAREER_CURRENT_MONTHLY_CONTEXT",
                TimelineStage.CURRENT_MONTH,
                "현재 월간 흐름의 일·책임 구조",
                CAREER_FORBIDDEN
        );

        allow(
                result,
                ConsultationCategory.CAREER,
                "CAREER_CURRENT_MONTHLY_FUNCTIONAL",
                TimelineStage.CURRENT_MONTH,
                "현재 월간 흐름의 일 관련 기능 작동",
                CAREER_FORBIDDEN
        );

        block(
                result,
                ConsultationCategory.CAREER,
                "CAREER_CURRENT_MONTHLY_ACTIVATION",
                TimelineStage.CURRENT_MONTH,
                "Monthly Context를 다시 별도 직업 활성 Meaning으로 중복 승격하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.CAREER,
                "CAREER_LEARNING_DECADE_CONTEXT",
                TimelineStage.CURRENT_DECADE,
                "학습·성장 Domain Context를 직업 결과 Meaning으로 자동 승격하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.CAREER,
                "CAREER_LEARNING_ANNUAL_CONTEXT",
                TimelineStage.CURRENT_YEAR,
                "학습·성장 Annual Context를 직업 결과 Meaning으로 자동 승격하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.CAREER,
                "CAREER_LEARNING_ANNUAL_FUNCTIONAL_CONTEXT",
                TimelineStage.CURRENT_YEAR,
                "학습·성장 Functional Context를 직업 성취 Meaning으로 자동 승격하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.CAREER,
                "CAREER_LEARNING_MONTHLY_CONTEXT",
                TimelineStage.CURRENT_MONTH,
                "학습·성장 Monthly Context를 직업 사건 Meaning으로 자동 승격하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.CAREER,
                "CAREER_LEARNING_MONTHLY_FUNCTIONAL_CONTEXT",
                TimelineStage.CURRENT_MONTH,
                "학습·성장 Monthly Functional Context를 직업 사건 Meaning으로 자동 승격하지 않습니다."
        );
    }

    /*
     * =================================================
     * HEALTH
     * =================================================
     */

    private static void registerCurrentHealth(
            Map<String, ConsultationTimelineUserMeaningPolicy> result
    ) {

        allow(
                result,
                ConsultationCategory.HEALTH_TENDENCY,
                "HEALTH_CURRENT_DECADE_CONTEXT",
                TimelineStage.CURRENT_DECADE,
                "현재 장기 흐름의 생활균형 구조",
                HEALTH_FORBIDDEN
        );

        allow(
                result,
                ConsultationCategory.HEALTH_TENDENCY,
                "HEALTH_CURRENT_ANNUAL_CONTEXT",
                TimelineStage.CURRENT_YEAR,
                "현재 연간 흐름의 생활균형 구조",
                HEALTH_FORBIDDEN
        );

        allow(
                result,
                ConsultationCategory.HEALTH_TENDENCY,
                "HEALTH_CURRENT_ANNUAL_FUNCTIONAL",
                TimelineStage.CURRENT_YEAR,
                "현재 연간 흐름의 생활균형 관련 기능 작동",
                HEALTH_FORBIDDEN
        );

        allow(
                result,
                ConsultationCategory.HEALTH_TENDENCY,
                "HEALTH_CURRENT_MONTHLY_FUNCTIONAL",
                TimelineStage.CURRENT_MONTH,
                "현재 월간 흐름의 생활균형 관련 기능 작동",
                HEALTH_FORBIDDEN
        );
    }

    /*
     * =================================================
     * PARTNER
     * =================================================
     */

    private static void registerCurrentPartner(
            Map<String, ConsultationTimelineUserMeaningPolicy> result
    ) {

        block(
                result,
                ConsultationCategory.PARTNER_LUCK,
                "PARTNER_LUCK_CURRENT_DECADE_CONTEXT",
                TimelineStage.CURRENT_DECADE,
                "일반 관계 Domain의 대운 Context를 배우자운으로 자동 치환하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.PARTNER_LUCK,
                "PARTNER_LUCK_CURRENT_ANNUAL_CONTEXT",
                TimelineStage.CURRENT_YEAR,
                "일반 관계 Domain의 세운 Context를 배우자운으로 자동 치환하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.PARTNER_LUCK,
                "PARTNER_LUCK_CURRENT_STRUCTURAL_FLOW",
                TimelineStage.CURRENT_YEAR,
                "일반 관계 Annual Source Family를 장기 인연·배우자 결과 Meaning으로 확대하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.PARTNER_LUCK,
                "PARTNER_LUCK_CURRENT_MONTHLY_CONTEXT",
                TimelineStage.CURRENT_MONTH,
                "일반 관계 Domain의 월운 Context를 배우자 사건으로 자동 치환하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.PARTNER_LUCK,
                "PARTNER_LUCK_CURRENT_MONTHLY_FUNCTIONAL",
                TimelineStage.CURRENT_MONTH,
                "일반 관계 Functional 결과를 배우자 사건으로 자동 치환하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.PARTNER_LUCK,
                "PARTNER_LUCK_CURRENT_MONTHLY_ACTIVATION",
                TimelineStage.CURRENT_MONTH,
                "일반 관계 Monthly Context를 배우자 등장·연애·결혼 활성로 확대하지 않습니다."
        );
    }

    /*
     * =================================================
     * SOCIAL
     * =================================================
     */

    private static void registerCurrentSocial(
            Map<String, ConsultationTimelineUserMeaningPolicy> result
    ) {

        block(
                result,
                ConsultationCategory.SOCIAL_RELATIONSHIP,
                "SOCIAL_RELATIONSHIP_CURRENT_DECADE_CONTEXT",
                TimelineStage.CURRENT_DECADE,
                "가까운 관계 Domain의 대운 Context를 일반 사회관계 전체의 성향으로 확대하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.SOCIAL_RELATIONSHIP,
                "SOCIAL_RELATIONSHIP_CURRENT_ANNUAL_CONTEXT",
                TimelineStage.CURRENT_YEAR,
                "가까운 관계 Domain의 세운 Context를 일반 사회관계 전체의 사건으로 확대하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.SOCIAL_RELATIONSHIP,
                "SOCIAL_RELATIONSHIP_CURRENT_STRUCTURAL_FLOW",
                TimelineStage.CURRENT_YEAR,
                "관계 Annual Source Family를 별도 사회관계 Meaning으로 재합성하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.SOCIAL_RELATIONSHIP,
                "SOCIAL_RELATIONSHIP_CURRENT_MONTHLY_CONTEXT",
                TimelineStage.CURRENT_MONTH,
                "가까운 관계 Domain의 월운 Context를 일반 사회관계 사건으로 확대하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.SOCIAL_RELATIONSHIP,
                "SOCIAL_RELATIONSHIP_CURRENT_MONTHLY_FUNCTIONAL",
                TimelineStage.CURRENT_MONTH,
                "관계 Functional 결과를 일반 대인관계 결과 Meaning으로 확대하지 않습니다."
        );

        block(
                result,
                ConsultationCategory.SOCIAL_RELATIONSHIP,
                "SOCIAL_RELATIONSHIP_CURRENT_MONTHLY_ACTIVATION",
                TimelineStage.CURRENT_MONTH,
                "관계 Monthly Context를 별도 사회관계 활성 사건으로 재해석하지 않습니다."
        );
    }

    /*
     * =================================================
     * LEGAL
     * =================================================
     */

    private static void registerCurrentLegalRisk(
            Map<String, ConsultationTimelineUserMeaningPolicy> result
    ) {

        block(
                result,
                ConsultationCategory.LEGAL_RISK,
                "LEGAL_RISK_CROSS_ESTABLISHED",
                TimelineStage.CURRENT_YEAR,
                "서로 다른 생활영역의 PRESSURE 공존만으로 소송·처벌·법적 문제 Meaning을 생성하지 않습니다."
        );
    }

    /*
     * =================================================
     * CURRENT YEAR
     * =================================================
     */

    private static void registerCurrentYearLuck(
            Map<String, ConsultationTimelineUserMeaningPolicy> result
    ) {

        allowDomainAware(
                result,
                ConsultationCategory.CURRENT_YEAR_LUCK,
                "CURRENT_YEAR_DECADE_DOMAIN_CONTEXT",
                TimelineStage.CURRENT_DECADE,
                "현재 장기 흐름에서 계산된 {domain} 영역",
                RANGE_YEAR_FORBIDDEN
        );

        allowDomainAware(
                result,
                ConsultationCategory.CURRENT_YEAR_LUCK,
                "CURRENT_YEAR_ANNUAL_DOMAIN_CONTEXT",
                TimelineStage.CURRENT_YEAR,
                "현재 연간 흐름에서 계산된 {domain} 영역",
                RANGE_YEAR_FORBIDDEN
        );

        allowDomainAware(
                result,
                ConsultationCategory.CURRENT_YEAR_LUCK,
                "CURRENT_YEAR_ANNUAL_FUNCTIONAL_DOMAIN",
                TimelineStage.CURRENT_YEAR,
                "현재 연간 흐름의 {domain} 기능 작동",
                RANGE_YEAR_FORBIDDEN
        );

        allowDomainAware(
                result,
                ConsultationCategory.CURRENT_YEAR_LUCK,
                "CURRENT_YEAR_MONTHLY_DOMAIN_CONTEXT",
                TimelineStage.CURRENT_MONTH,
                "현재 월간 흐름에서 계산된 {domain} 영역",
                RANGE_YEAR_FORBIDDEN
        );

        allowDomainAware(
                result,
                ConsultationCategory.CURRENT_YEAR_LUCK,
                "CURRENT_YEAR_MONTHLY_FUNCTIONAL_DOMAIN",
                TimelineStage.CURRENT_MONTH,
                "현재 월간 흐름의 {domain} 기능 작동",
                RANGE_YEAR_FORBIDDEN
        );
    }

    /*
     * =================================================
     * PAST / FUTURE
     * =================================================
     */

    private static void registerPastLuck(
            Map<String, ConsultationTimelineUserMeaningPolicy> result
    ) {

        registerTimelineYearFamily(
                result,
                ConsultationCategory.PAST_LUCK,
                "PAST_LUCK"
        );
    }

    private static void registerFutureLuck(
            Map<String, ConsultationTimelineUserMeaningPolicy> result
    ) {

        registerTimelineYearFamily(
                result,
                ConsultationCategory.FUTURE_LUCK,
                "FUTURE_LUCK"
        );
    }

    private static void registerTimelineYearFamily(

            Map<String, ConsultationTimelineUserMeaningPolicy> result,

            ConsultationCategory category,

            String prefix

    ) {

        allow(
                result,
                category,
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                prefix + "_TIMELINE_YEAR_OVERALL_NARRATIVE",
                TimelineStage.RANGE_YEAR_OVERALL,
                false,
                "이 해의 전체 흐름",
                "OVERALL_NARRATIVE의 deterministic status만 연도 전체 방향 Anchor로 사용자에게 전달합니다.",
                RANGE_YEAR_FORBIDDEN
        );

        block(
                result,
                category,
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                prefix + "_TIMELINE_YEAR_BIG_FLOW",
                TimelineStage.RANGE_YEAR_OVERALL,
                "BIG_FLOW는 OVERALL_NARRATIVE의 내부 Anchor이므로 같은 연도 전체 Meaning으로 중복 출력하지 않습니다."
        );

        allowDomainAware(
                result,
                category,
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                prefix + "_TIMELINE_YEAR_PRIMARY_ACTIVE_DOMAIN",
                TimelineStage.RANGE_YEAR_ACTIVE_DOMAIN,
                "이 해에서 작동이 비교적 분명한 {domain} 영역",
                "실제 PRIMARY_ACTIVE_DOMAIN으로 식별된 하나의 LifeDomain만 사용자에게 구조 Meaning으로 전달합니다.",
                RANGE_YEAR_FORBIDDEN
        );

        allowDomainAware(
                result,
                category,
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                prefix + "_TIMELINE_YEAR_PRIMARY_PRESSURE_DOMAIN",
                TimelineStage.RANGE_YEAR_PRESSURE_DOMAIN,
                "이 해의 주된 부담축으로 계산된 {domain} 영역",
                "실제 PRIMARY_PRESSURE_DOMAIN으로 식별된 하나의 LifeDomain만 부담축 Meaning으로 전달합니다.",
                RANGE_YEAR_FORBIDDEN
        );

        allow(
                result,
                category,
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                prefix + "_TIMELINE_YEAR_CROSS_DOMAIN_PATTERN",
                TimelineStage.RANGE_YEAR_CROSS_DOMAIN,
                false,
                "이 해의 서로 다른 생활영역에서 함께 확인된 방향 구조",
                "Cross-Domain Rule이 실제 계산한 방향 공존 상태만 전달하고 영역 사이의 원인·결과·강화 관계는 생성하지 않습니다.",
                RANGE_YEAR_FORBIDDEN
        );

        allow(
                result,
                category,
                ConsultationCategoryDeterministicLayer.TIMELINE_YEAR,
                prefix + "_TIMELINE_YEAR_TURNING_POINT",
                TimelineStage.RANGE_YEAR_TURNING_POINT,
                false,
                "전후 연도와 비교해 구조 변화 조건이 확인된 이 시점",
                "TURNING_POINT Rule이 실제 성립한 경우에만 구조 변화 시점 Meaning을 전달하며 현실 사건으로 구체화하지 않습니다.",
                RANGE_YEAR_FORBIDDEN
        );
    }

    /*
     * =================================================
     * Helpers
     * =================================================
     */

    private static void allow(

            Map<String, ConsultationTimelineUserMeaningPolicy> result,

            ConsultationCategory category,

            String ruleCode,

            TimelineStage stage,

            String userSubject,

            List<String> forbiddenExtensions

    ) {

        allow(
                result,
                category,
                ConsultationCategoryDeterministicLayer.CURRENT_TIMELINE,
                ruleCode,
                stage,
                false,
                userSubject,
                "해당 Timeline Rule의 deterministic status를 사건이 아닌 구조·방향 Meaning으로 제한해 전달합니다.",
                forbiddenExtensions
        );
    }

    private static void allowDomainAware(

            Map<String, ConsultationTimelineUserMeaningPolicy> result,

            ConsultationCategory category,

            String ruleCode,

            TimelineStage stage,

            String userSubject,

            List<String> forbiddenExtensions

    ) {

        allowDomainAware(
                result,
                category,
                ConsultationCategoryDeterministicLayer.CURRENT_TIMELINE,
                ruleCode,
                stage,
                userSubject,
                "현재 Source LifeDomain을 그대로 보존해 시간층 Meaning으로 전달하며 다른 Domain과 합성하지 않습니다.",
                forbiddenExtensions
        );
    }

    private static void allowDomainAware(

            Map<String, ConsultationTimelineUserMeaningPolicy> result,

            ConsultationCategory category,

            ConsultationCategoryDeterministicLayer layer,

            String ruleCode,

            TimelineStage stage,

            String userSubject,

            String auditReason,

            List<String> forbiddenExtensions

    ) {

        allow(
                result,
                category,
                layer,
                ruleCode,
                stage,
                true,
                userSubject,
                auditReason,
                forbiddenExtensions
        );
    }

    private static void allow(

            Map<String, ConsultationTimelineUserMeaningPolicy> result,

            ConsultationCategory category,

            ConsultationCategoryDeterministicLayer layer,

            String ruleCode,

            TimelineStage stage,

            boolean sourceDomainAware,

            String userSubject,

            String auditReason,

            List<String> forbiddenExtensions

    ) {

        register(
                result,
                ConsultationTimelineUserMeaningPolicy.allow(
                        category,
                        layer,
                        ruleCode,
                        stage,
                        sourceDomainAware,
                        userSubject,
                        auditReason,
                        forbiddenExtensions
                )
        );
    }

    private static void block(

            Map<String, ConsultationTimelineUserMeaningPolicy> result,

            ConsultationCategory category,

            String ruleCode,

            TimelineStage stage,

            String auditReason

    ) {

        block(
                result,
                category,
                ConsultationCategoryDeterministicLayer.CURRENT_TIMELINE,
                ruleCode,
                stage,
                auditReason
        );
    }

    private static void block(

            Map<String, ConsultationTimelineUserMeaningPolicy> result,

            ConsultationCategory category,

            ConsultationCategoryDeterministicLayer layer,

            String ruleCode,

            TimelineStage stage,

            String auditReason

    ) {

        register(
                result,
                ConsultationTimelineUserMeaningPolicy.block(
                        category,
                        layer,
                        ruleCode,
                        stage,
                        auditReason
                )
        );
    }

    private static void register(

            Map<String, ConsultationTimelineUserMeaningPolicy> result,

            ConsultationTimelineUserMeaningPolicy policy

    ) {

        String key =
                key(
                        policy.category(),
                        policy.layer(),
                        policy.ruleCode()
                );

        if (
                result.putIfAbsent(
                        key,
                        policy
                ) != null
        ) {

            throw new IllegalStateException(
                    "Timeline User Meaning Policy가 중복 등록되었습니다. key="
                            + key
            );
        }
    }

    private static String key(

            ConsultationCategory category,

            ConsultationCategoryDeterministicLayer layer,

            String ruleCode

    ) {

        return category.name()
                + "|"
                + layer.name()
                + "|"
                + ruleCode;
    }

    private static List<String> merge(

            List<String> first,

            List<String> second

    ) {

        java.util.LinkedHashSet<String> result =
                new java.util.LinkedHashSet<>();

        if (
                first != null
        ) {

            result.addAll(
                    first
            );
        }

        if (
                second != null
        ) {

            result.addAll(
                    second
            );
        }

        return List.copyOf(
                result
        );
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