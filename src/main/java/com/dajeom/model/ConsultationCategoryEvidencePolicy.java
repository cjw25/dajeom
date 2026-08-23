package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.LifeDomain;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * LifeDomainAnalysis를
 * ConsultationCategory에 연결하기 위한
 * 공식 deterministic Evidence Routing Policy.
 *
 * =================================================
 * 기본 구조
 * =================================================
 *
 * natalDomains
 *
 * 원국에서 해당 Domain 전체를
 * Category View에 허용한다.
 *
 * -------------------------------------------------
 *
 * segmentDomains
 *
 * 현재 Timeline Segment에서 해당 Domain 전체를
 * Category View에 허용한다.
 *
 * =================================================
 * RuleCode 선택 Routing
 * =================================================
 *
 * selectedNatalRuleCodes
 *
 * natalDomains에 포함되지 않은 Domain이라도
 * 특정 LifeDomainRuleMatch.ruleCode만
 * 원국 Category View에 추가할 수 있다.
 *
 * -------------------------------------------------
 *
 * selectedSegmentRuleCodes
 *
 * segmentDomains에 포함되지 않은 Domain이라도
 * 특정 LifeDomainRuleMatch.ruleCode만
 * 현재 Segment Category View에 추가할 수 있다.
 *
 * =================================================
 * 사용 목적
 * =================================================
 *
 * 대표적으로 CAREER는:
 *
 * CAREER Domain 전체
 *
 * 를 기본 입력으로 사용하면서,
 *
 * LEARNING_GROWTH Domain 전체를 복사하지 않고
 * 직업 계산에 실제로 필요한 특정
 * Learning Rule만 선별할 수 있어야 한다.
 *
 * 예:
 *
 * CAREER natalDomains
 * =
 * CAREER
 *
 * +
 *
 * selectedNatalRuleCodes
 * [
 *     LEARNING_GROWTH
 *     ->
 *     특정 RuleCode
 * ]
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * selected RuleCode는:
 *
 * 다른 Domain의 모든 Match를
 * Category에 복사하기 위한 우회 수단이 아니다.
 *
 * 실제 Category 계산에 필요한
 * 명시적인 Rule만 선별하기 위한 정책이다.
 *
 * =================================================
 * 이 모델이 하지 않는 것
 * =================================================
 *
 * - Category Status 계산
 * - LifeDomain Status 변경
 * - 서로 다른 Match 합성
 * - 관계 생성
 * - 인과 생성
 * - 순서 생성
 * - Grounding 강도 계산
 * - 독립 Evidence 개수 계산
 *
 * 이 모델은 오직:
 *
 * "어떤 LifeDomain Match가
 *  해당 Category의 입력 View에 들어갈 수 있는가"
 *
 * 만 결정한다.
 *
 * =================================================
 * Timeline Range
 * =================================================
 *
 * requiresPastTimelineRange
 *
 * 현재 단일 Segment가 아니라
 * 실제 과거 Timeline Range가 필요하다.
 *
 * -------------------------------------------------
 *
 * requiresFutureTimelineRange
 *
 * 현재 단일 Segment가 아니라
 * 실제 미래 Timeline Range가 필요하다.
 *
 * =================================================
 * Dedicated Cross Domain
 * =================================================
 *
 * requiresDedicatedCrossDomainRule
 *
 * 단순 Domain 또는 RuleCode Routing으로
 * Category를 만들면 안 되고
 * 별도의 전용 계산이 필요한 경우다.
 *
 * 대표:
 *
 * LEGAL_RISK
 *
 * =================================================
 * 생성 방식
 * =================================================
 *
 * selected RuleCode가 필요하지 않은
 * Domain 전체 Routing 정책은
 * 6개 인자 편의 생성자를 사용할 수 있다.
 *
 * selected RuleCode가 필요한 정책은
 * canonical 생성자를 사용한다.
 */
public record ConsultationCategoryEvidencePolicy(

        ConsultationCategory category,

        Set<LifeDomain> natalDomains,

        Set<LifeDomain> segmentDomains,

        Map<LifeDomain, Set<String>>
        selectedNatalRuleCodes,

        Map<LifeDomain, Set<String>>
        selectedSegmentRuleCodes,

        boolean requiresPastTimelineRange,

        boolean requiresFutureTimelineRange,

        boolean requiresDedicatedCrossDomainRule

) {

    /*
     * =================================================
     * Domain 전체 Routing용 편의 생성자
     * =================================================
     *
     * selected RuleCode를 사용하지 않는 정책에서 사용한다.
     *
     * selectedNatalRuleCodes와
     * selectedSegmentRuleCodes는
     * 빈 Map으로 확정한다.
     */

    public ConsultationCategoryEvidencePolicy(

            ConsultationCategory category,

            Set<LifeDomain> natalDomains,

            Set<LifeDomain> segmentDomains,

            boolean requiresPastTimelineRange,

            boolean requiresFutureTimelineRange,

            boolean requiresDedicatedCrossDomainRule

    ) {

        this(
                category,
                natalDomains,
                segmentDomains,
                Map.of(),
                Map.of(),
                requiresPastTimelineRange,
                requiresFutureTimelineRange,
                requiresDedicatedCrossDomainRule
        );
    }


    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

    public ConsultationCategoryEvidencePolicy {

        Objects.requireNonNull(
                category,
                "ConsultationCategoryEvidencePolicy에는 ConsultationCategory가 필요합니다."
        );

        natalDomains =
                immutableDomains(
                        natalDomains
                );

        segmentDomains =
                immutableDomains(
                        segmentDomains
                );

        selectedNatalRuleCodes =
                immutableRuleCodeMap(
                        selectedNatalRuleCodes,
                        "selectedNatalRuleCodes"
                );

        selectedSegmentRuleCodes =
                immutableRuleCodeMap(
                        selectedSegmentRuleCodes,
                        "selectedSegmentRuleCodes"
                );

        /*
         * =================================================
         * 중복 Routing 금지
         * =================================================
         *
         * Domain 전체를 이미 허용했다면
         * 같은 Domain의 selected RuleCode를
         * 다시 등록하지 않는다.
         *
         * 예:
         *
         * natalDomains = CAREER
         *
         * +
         *
         * selectedNatalRuleCodes[CAREER]
         *
         * 금지.
         *
         * selected RuleCode는
         * 기본 Domain 밖의 선별 추가용이다.
         */

        validateNoRedundantSelectors(
                natalDomains,
                selectedNatalRuleCodes,
                "Natal"
        );

        validateNoRedundantSelectors(
                segmentDomains,
                selectedSegmentRuleCodes,
                "Segment"
        );

        /*
         * =================================================
         * 과거/미래 Range 동시 요구 금지
         * =================================================
         */

        if (
                requiresPastTimelineRange
                        && requiresFutureTimelineRange
        ) {

            throw new IllegalArgumentException(
                    "하나의 ConsultationCategory가 과거/미래 전체 Timeline Range를 동시에 요구할 수 없습니다: "
                            + category
            );
        }

        /*
         * =================================================
         * Range 전용 View
         * =================================================
         *
         * PAST_LUCK / FUTURE_LUCK 같은
         * Timeline Range Category에는:
         *
         * - 현재 Segment Domain
         * - selected Segment Rule
         *
         * 을 직접 연결하지 않는다.
         *
         * 또한 현재 구조에서는
         * Range Category에 selected Natal Rule도
         * 추가하지 않는다.
         */

        if (
                requiresPastTimelineRange
                        || requiresFutureTimelineRange
        ) {

            if (
                    !segmentDomains.isEmpty()
                            || !selectedSegmentRuleCodes.isEmpty()
                            || !selectedNatalRuleCodes.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Timeline Range가 필요한 ConsultationCategory에는 직접 Segment 또는 selected RuleCode Routing을 연결하지 않습니다: "
                                + category
                );
            }
        }

        /*
         * =================================================
         * 전용 교차 Rule View
         * =================================================
         *
         * LEGAL_RISK 같은 Category는:
         *
         * - natalDomains
         * - segmentDomains
         * - selectedNatalRuleCodes
         * - selectedSegmentRuleCodes
         *
         * 어느 방식으로도
         * 단순 Routing하지 않는다.
         */

        if (
                requiresDedicatedCrossDomainRule
        ) {

            if (
                    !natalDomains.isEmpty()
                            || !segmentDomains.isEmpty()
                            || !selectedNatalRuleCodes.isEmpty()
                            || !selectedSegmentRuleCodes.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "전용 교차 Rule이 필요한 ConsultationCategory에는 단순 LifeDomain 또는 RuleCode Routing을 두지 않습니다: "
                                + category
                );
            }
        }
    }


    /*
     * =================================================
     * Domain 전체 허용
     * =================================================
     */

    public boolean acceptsNatalDomain(
            LifeDomain domain
    ) {

        return domain != null
                && natalDomains.contains(
                domain
        );
    }


    public boolean acceptsSegmentDomain(
            LifeDomain domain
    ) {

        return domain != null
                && segmentDomains.contains(
                domain
        );
    }


    /*
     * =================================================
     * Natal Match 허용
     * =================================================
     *
     * 우선순위:
     *
     * 1.
     * Domain 전체가 허용되어 있으면
     * 모든 RuleCode 허용.
     *
     * 2.
     * Domain 전체가 허용되지 않았다면
     * selectedNatalRuleCodes에서
     * 정확한 RuleCode가 등록된 경우만 허용.
     */

    public boolean acceptsNatalRule(

            LifeDomain domain,

            String ruleCode

    ) {

        if (
                domain == null
        ) {

            return false;
        }

        if (
                acceptsNatalDomain(
                        domain
                )
        ) {

            return true;
        }

        return selectedRuleExists(
                selectedNatalRuleCodes,
                domain,
                ruleCode
        );
    }


    /*
     * =================================================
     * Segment Match 허용
     * =================================================
     */

    public boolean acceptsSegmentRule(

            LifeDomain domain,

            String ruleCode

    ) {

        if (
                domain == null
        ) {

            return false;
        }

        if (
                acceptsSegmentDomain(
                        domain
                )
        ) {

            return true;
        }

        return selectedRuleExists(
                selectedSegmentRuleCodes,
                domain,
                ruleCode
        );
    }


    /*
     * =================================================
     * Selected Rule 조회
     * =================================================
     */

    public Set<String> selectedNatalRuleCodesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return Set.of();
        }

        return selectedNatalRuleCodes
                .getOrDefault(
                        domain,
                        Set.of()
                );
    }


    public Set<String> selectedSegmentRuleCodesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return Set.of();
        }

        return selectedSegmentRuleCodes
                .getOrDefault(
                        domain,
                        Set.of()
                );
    }


    public boolean hasSelectedNatalRules() {

        return !selectedNatalRuleCodes
                .isEmpty();
    }


    public boolean hasSelectedSegmentRules() {

        return !selectedSegmentRuleCodes
                .isEmpty();
    }


    public boolean hasSelectedNatalRulesFor(
            LifeDomain domain
    ) {

        return !selectedNatalRuleCodesOf(
                domain
        )
                .isEmpty();
    }


    public boolean hasSelectedSegmentRulesFor(
            LifeDomain domain
    ) {

        return !selectedSegmentRuleCodesOf(
                domain
        )
                .isEmpty();
    }


    /*
     * =================================================
     * View Type
     * =================================================
     */

    public boolean directLifeDomainView() {

        return !natalDomains.isEmpty()
                || !segmentDomains.isEmpty()
                || !selectedNatalRuleCodes.isEmpty()
                || !selectedSegmentRuleCodes.isEmpty();
    }


    public boolean timelineRangeView() {

        return requiresPastTimelineRange
                || requiresFutureTimelineRange;
    }


    /*
     * =================================================
     * Selected Rule 판별
     * =================================================
     */

    private static boolean selectedRuleExists(

            Map<LifeDomain, Set<String>> ruleCodeMap,

            LifeDomain domain,

            String ruleCode

    ) {

        Objects.requireNonNull(
                ruleCodeMap,
                "RuleCode Map은 null일 수 없습니다."
        );

        if (
                domain == null
                        || ruleCode == null
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

        return ruleCodeMap
                .getOrDefault(
                        domain,
                        Set.of()
                )
                .contains(
                        normalizedRuleCode
                );
    }


    /*
     * =================================================
     * Domain 정규화
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
                            "ConsultationCategoryEvidencePolicy의 LifeDomain은 null일 수 없습니다."
                    )
            );
        }

        return Collections.unmodifiableSet(
                copied
        );
    }


    /*
     * =================================================
     * RuleCode Map 정규화
     * =================================================
     */

    private static Map<LifeDomain, Set<String>>
    immutableRuleCodeMap(

            Map<LifeDomain, Set<String>> source,

            String fieldName

    ) {

        Objects.requireNonNull(
                fieldName,
                "RuleCode Map fieldName이 필요합니다."
        );

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Map.of();
        }

        EnumMap<LifeDomain, Set<String>> copied =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                Map.Entry<LifeDomain, Set<String>> entry
                : source.entrySet()
        ) {

            LifeDomain domain =
                    Objects.requireNonNull(
                            entry.getKey(),
                            fieldName
                                    + "의 LifeDomain은 null일 수 없습니다."
                    );

            Set<String> sourceRuleCodes =
                    Objects.requireNonNull(
                            entry.getValue(),
                            fieldName
                                    + "의 RuleCode Set은 null일 수 없습니다: "
                                    + domain
                    );

            if (
                    sourceRuleCodes.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        fieldName
                                + "에는 빈 RuleCode Set을 등록할 수 없습니다: "
                                + domain
                );
            }

            LinkedHashSet<String> normalizedRuleCodes =
                    new LinkedHashSet<>();

            for (
                    String ruleCode
                    : sourceRuleCodes
            ) {

                normalizedRuleCodes.add(
                        requireRuleCode(
                                ruleCode,
                                fieldName,
                                domain
                        )
                );
            }

            copied.put(
                    domain,
                    Collections.unmodifiableSet(
                            normalizedRuleCodes
                    )
            );
        }

        return Collections.unmodifiableMap(
                copied
        );
    }


    /*
     * =================================================
     * 중복 Selector 검증
     * =================================================
     */

    private static void validateNoRedundantSelectors(

            Set<LifeDomain> fullDomains,

            Map<LifeDomain, Set<String>> selectedRuleCodes,

            String layerName

    ) {

        Objects.requireNonNull(
                fullDomains,
                "Full Domain Set이 필요합니다."
        );

        Objects.requireNonNull(
                selectedRuleCodes,
                "Selected RuleCode Map이 필요합니다."
        );

        Objects.requireNonNull(
                layerName,
                "Layer Name이 필요합니다."
        );

        for (
                LifeDomain selectedDomain
                : selectedRuleCodes.keySet()
        ) {

            if (
                    fullDomains.contains(
                            selectedDomain
                    )
            ) {

                throw new IllegalArgumentException(
                        layerName
                                + " Domain 전체가 이미 허용된 상태에서 같은 Domain의 selected RuleCode를 중복 등록할 수 없습니다: "
                                + selectedDomain
                );
            }
        }
    }


    /*
     * =================================================
     * RuleCode Text 검증
     * =================================================
     */

    private static String requireRuleCode(

            String value,

            String fieldName,

            LifeDomain domain

    ) {

        Objects.requireNonNull(
                value,
                fieldName
                        + "의 RuleCode는 null일 수 없습니다: "
                        + domain
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    fieldName
                            + "의 RuleCode는 비어 있을 수 없습니다: "
                            + domain
            );
        }

        return normalized;
    }
}