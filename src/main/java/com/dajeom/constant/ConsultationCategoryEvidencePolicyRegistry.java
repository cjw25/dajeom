package com.dajeom.constant;

import com.dajeom.model.ConsultationCategoryEvidencePolicy;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 19차.
 *
 * 11개 ConsultationCategory와
 * 18차 LifeDomainAnalysis 사이의
 * 공식 Evidence Routing Policy.
 *
 * =================================================
 *
 * 핵심:
 *
 * LifeDomain 5개를
 * ConsultationCategory 11개로 복제하지 않는다.
 *
 * 각 Category의 성격에 따라:
 *
 * - 원국 View
 * - 현재 Segment View
 * - 과거 Range View
 * - 미래 Range View
 * - 전용 교차 Rule View
 *
 * 로 구분한다.
 *
 * =================================================
 *
 * 현재 정책:
 *
 * PERSONALITY
 * -> 원국의 여러 생활 기능을 종합
 *
 * PAST_LUCK
 * -> 과거 Timeline Range 필요
 *
 * SOCIAL_RELATIONSHIP
 * -> RELATIONSHIP
 *
 * LOVE_STYLE
 * -> 원국 RELATIONSHIP 중심
 *
 * PARTNER_LUCK
 * -> RELATIONSHIP + 현재 Timeline
 *
 * WEALTH
 * -> WEALTH
 *
 * CAREER
 * -> CAREER
 *
 * LEGAL_RISK
 * -> 단순 Domain 복사 금지
 * -> 별도 교차 Evidence Rule 필요
 *
 * HEALTH_TENDENCY
 * -> HEALTH_LIFESTYLE
 *
 * CURRENT_YEAR_LUCK
 * -> 현재 Segment의 모든 LifeDomain
 *    + 원국 기준 Context
 *
 * FUTURE_LUCK
 * -> 미래 Timeline Range 필요
 *
 * =================================================
 *
 * 주의:
 *
 * PAST_LUCK을 YEAR 궁위로 만들지 않는다.
 *
 * FUTURE_LUCK을 HOUR 궁위로 만들지 않는다.
 *
 * LEGAL_RISK를 충/형/칠살 존재만으로 만들지 않는다.
 */
@Component
public class ConsultationCategoryEvidencePolicyRegistry {

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
         *
         * 성격이라는 별도 LifeDomain을 만들지 않는다.
         *
         * 재물 처리 방식
         * 역할 처리 방식
         * 관계 방식
         * 학습/표현 방식
         *
         * 같은 원국 생활 기능을 종합한다.
         *
         * HEALTH_LIFESTYLE는 성격 View에서
         * 직접 소비하지 않는다.
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
         *
         * 실제 과거 Timeline 범위가 필요하다.
         *
         * YEAR Palace를 과거운으로 사용하지 않는다.
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
         */

        put(
                configured,
                policy(
                        ConsultationCategory.SOCIAL_RELATIONSHIP,
                        domains(
                                LifeDomain.RELATIONSHIP
                        ),
                        domains(
                                LifeDomain.RELATIONSHIP
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
         * 스타일은 기본적으로 원국 View다.
         *
         * 현재 운의 관계 변화를
         * 연애 "스타일" 자체로 덮어쓰지 않는다.
         */

        put(
                configured,
                policy(
                        ConsultationCategory.LOVE_STYLE,
                        domains(
                                LifeDomain.RELATIONSHIP
                        ),
                        domains(),
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
         * 관계 원국 구조
         * +
         * 현재 Timeline 변화
         *
         * 를 함께 사용할 수 있다.
         *
         * 단:
         *
         * 실제 결혼/이별 사건은
         * 이후 Grounding에서도 확정하지 않는다.
         */

        put(
                configured,
                policy(
                        ConsultationCategory.PARTNER_LUCK,
                        domains(
                                LifeDomain.RELATIONSHIP
                        ),
                        domains(
                                LifeDomain.RELATIONSHIP
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
         * 우선 CAREER Domain만 공식 입력으로 사용한다.
         *
         * LEARNING_GROWTH 중 어느 Rule을
         * 직업 View에 추가할지는
         * 이후 RuleCode 기반 선별 정책으로
         * 별도 정밀화한다.
         *
         * 지금 모든 Learning Match를
         * CAREER에 복사하지 않는다.
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
                        false,
                        false,
                        false
                )
        );

        /*
         * =================================================
         * 8. LEGAL_RISK
         * =================================================
         *
         * 별도 LifeDomain.LEGAL을 만들지 않는다.
         *
         * 또한:
         *
         * 충
         * 형
         * 칠살
         * PRESSURE
         *
         * 하나만으로 분쟁/법률 위험을 만들지 않는다.
         *
         * 19차 전용 교차 Evidence Rule이
         * 별도로 필요하다.
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
         *
         * 의료 진단이 아니라
         * 생활 균형/관리 경향 View.
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
         * "올해"는 LifeDomain이 아니라 시간 View다.
         *
         * 원국 기준선:
         * 5개 Domain
         *
         * +
         *
         * 현재 분석 Segment 변화:
         * 5개 Domain
         *
         * 을 모두 사용할 수 있다.
         *
         * 실제 Segment 선택은
         * 19차 Engine이 담당한다.
         */

        put(
                configured,
                policy(
                        ConsultationCategory.CURRENT_YEAR_LUCK,
                        allDomains(),
                        allDomains(),
                        false,
                        false,
                        false
                )
        );

        /*
         * =================================================
         * 11. FUTURE_LUCK
         * =================================================
         *
         * 현재 대운 하나 또는
         * HOUR Palace 하나만 보고
         * "앞으로의 흐름"을 만들지 않는다.
         *
         * 실제 미래 Timeline Range가 필요하다.
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
         * 11개 전부 정의되었는지 검증
         * =================================================
         */

        if (
                configured.size()
                        != ConsultationCategory.values().length
        ) {

            throw new IllegalStateException(
                    "모든 ConsultationCategory에 대한 Evidence Policy가 정의되어야 합니다. "
                            + "configured="
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

    public Map<
            ConsultationCategory,
            ConsultationCategoryEvidencePolicy
            > allPolicies() {

        return policies;
    }

    /*
     * =================================================
     * Builder
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

    private static EnumSet<LifeDomain> allDomains() {

        return EnumSet.allOf(
                LifeDomain.class
        );
    }

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