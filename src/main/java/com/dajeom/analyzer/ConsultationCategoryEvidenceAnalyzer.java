package com.dajeom.analyzer;

import com.dajeom.constant.ConsultationCategoryEvidencePolicyRegistry;
import com.dajeom.model.ConsultationCategoryEvidenceAnalysis;
import com.dajeom.model.ConsultationCategoryEvidencePolicy;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.LifeDomainSegmentAnalysis;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 19차.
 *
 * 18차 LifeDomainAnalysis를
 * 11개 ConsultationCategory Evidence View로
 * deterministic하게 Routing한다.
 *
 * =================================================
 *
 * 입력:
 *
 * LifeDomainAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * ConsultationCategoryEvidenceAnalysis
 *
 * =================================================
 *
 * 역할:
 *
 * 18차가 계산한 Match를
 * 사용자 상담 주제별로:
 *
 * - 선별
 * - 배치
 * - 시간층 분리
 *
 * 한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 새로운 명리 계산
 * - support/caution 점수 계산
 * - polarity 계산
 * - intensity 계산
 * - 사용자 상담문 생성
 * - LLM용 문장 생성
 *
 * =================================================
 *
 * 특히 하지 않는 것:
 *
 * YEAR
 * -> PAST_LUCK
 *
 * HOUR
 * -> FUTURE_LUCK
 *
 * 충
 * -> LEGAL_RISK
 *
 * PRESSURE
 * -> LEGAL_RISK
 *
 * 같은 V1 고정 변환.
 *
 * =================================================
 *
 * 불확실성:
 *
 * 현재 조회가 여러 가능한 Timeline Segment를
 * 생성하면 전부 유지한다.
 *
 * 하나를 대표 Segment로 선택하지 않는다.
 */
@Component
public class ConsultationCategoryEvidenceAnalyzer {

    private final ConsultationCategoryEvidencePolicyRegistry
            policyRegistry;

    public ConsultationCategoryEvidenceAnalyzer(
            ConsultationCategoryEvidencePolicyRegistry
                    policyRegistry
    ) {

        this.policyRegistry =
                Objects.requireNonNull(
                        policyRegistry,
                        "ConsultationCategoryEvidencePolicyRegistry가 필요합니다."
                );
    }

    /*
     * =================================================
     * 공식 19차 Routing API
     * =================================================
     */

    public ConsultationCategoryEvidenceAnalysis analyze(
            LifeDomainAnalysis lifeDomainAnalysis
    ) {

        Objects.requireNonNull(
                lifeDomainAnalysis,
                "ConsultationCategoryEvidenceAnalyzer에는 LifeDomainAnalysis가 필요합니다."
        );

        List<ConsultationCategoryEvidenceView> views =
                new ArrayList<>(
                        ConsultationCategory
                                .values()
                                .length
                );

        for (
                ConsultationCategory category
                : ConsultationCategory.values()
        ) {

            ConsultationCategoryEvidencePolicy policy =
                    policyRegistry
                            .policyOf(
                                    category
                            );

            views.add(
                    routeCategory(
                            lifeDomainAnalysis,
                            policy
                    )
            );
        }

        views.sort(
                Comparator.comparingInt(
                        view ->
                                view
                                        .category()
                                        .getOrder()
                )
        );

        return new ConsultationCategoryEvidenceAnalysis(
                lifeDomainAnalysis,
                views
        );
    }

    /*
     * =================================================
     * Category Routing
     * =================================================
     */

    private ConsultationCategoryEvidenceView routeCategory(

            LifeDomainAnalysis lifeDomainAnalysis,

            ConsultationCategoryEvidencePolicy policy

    ) {

        /*
         * =================================================
         * PAST_LUCK
         * =================================================
         *
         * 현재 기준시점의 Timeline Segment를
         * 지나온 전체 흐름으로 오용하지 않는다.
         */

        if (
                policy.requiresPastTimelineRange()
        ) {

            return unavailable(
                    policy.category(),
                    "PAST_LUCK은 현재 기준시점의 단일 Timeline 분석이 아니라 실제 과거 Timeline Range 분석이 필요합니다."
            );
        }

        /*
         * =================================================
         * FUTURE_LUCK
         * =================================================
         *
         * 현재 활성 대운 하나나
         * HOUR Palace 하나를
         * 미래 전체 흐름으로 사용하지 않는다.
         */

        if (
                policy.requiresFutureTimelineRange()
        ) {

            return unavailable(
                    policy.category(),
                    "FUTURE_LUCK은 현재 Segment 하나가 아니라 실제 미래 대운·세운·월운 Timeline Range 분석이 필요합니다."
            );
        }

        /*
         * =================================================
         * LEGAL_RISK
         * =================================================
         *
         * 단일 PRESSURE나 충/형 하나로
         * 법률·분쟁 View를 만들지 않는다.
         */

        if (
                policy.requiresDedicatedCrossDomainRule()
        ) {

            return unavailable(
                    policy.category(),
                    "LEGAL_RISK는 단일 LifeDomain 또는 하나의 관계 Evidence로 생성하지 않고 전용 교차 Evidence Rule이 필요합니다."
            );
        }

        /*
         * =================================================
         * 원국 Match Routing
         * =================================================
         */

        List<LifeDomainRuleMatch> natalMatches =
                collectNatalMatches(
                        lifeDomainAnalysis,
                        policy
                );

        /*
         * =================================================
         * 현재 Timeline Segment Routing
         * =================================================
         *
         * 날짜/교운 불확실성으로 Segment가 여러 개면
         * 모든 Segment를 동일 순서로 유지한다.
         */

        List<ConsultationCategorySegmentEvidence>
                segmentEvidences =
                collectSegmentEvidences(
                        lifeDomainAnalysis,
                        policy
                );

        return new ConsultationCategoryEvidenceView(
                policy.category(),
                natalMatches,
                segmentEvidences,
                Optional.empty()
        );
    }

    /*
     * =================================================
     * Natal
     * =================================================
     */

    private List<LifeDomainRuleMatch> collectNatalMatches(

            LifeDomainAnalysis lifeDomainAnalysis,

            ConsultationCategoryEvidencePolicy policy

    ) {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            if (
                    !policy.acceptsNatalDomain(
                            domain
                    )
            ) {

                continue;
            }

            List<LifeDomainRuleMatch> matches =
                    lifeDomainAnalysis
                            .natalMatchesOf(
                                    domain
                            );

            validateDomain(
                    domain,
                    matches
            );

            result.addAll(
                    matches
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Segment
     * =================================================
     *
     * 중요한 점:
     *
     * 여기서는:
     *
     * lifeDomainAnalysis.effectiveMatchesOf(...)
     *
     * 를 사용하지 않는다.
     *
     * effectiveMatchesOf는:
     *
     * natal
     * +
     * segment
     *
     * 를 반환한다.
     *
     * 그러면 원국 Match가 Segment마다 반복된다.
     *
     * 따라서:
     *
     * LifeDomainSegmentAnalysis.matchesOf(domain)
     *
     * 만 사용한다.
     */

    private List<ConsultationCategorySegmentEvidence>
    collectSegmentEvidences(

            LifeDomainAnalysis lifeDomainAnalysis,

            ConsultationCategoryEvidencePolicy policy

    ) {

        if (
                policy.segmentDomains()
                        .isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationCategorySegmentEvidence> result =
                new ArrayList<>(
                        lifeDomainAnalysis
                                .segmentCount()
                );

        for (
                LifeDomainSegmentAnalysis segmentAnalysis
                : lifeDomainAnalysis.segmentAnalyses()
        ) {

            List<LifeDomainRuleMatch> matches =
                    new ArrayList<>();

            for (
                    LifeDomain domain
                    : LifeDomain.values()
            ) {

                if (
                        !policy.acceptsSegmentDomain(
                                domain
                        )
                ) {

                    continue;
                }

                List<LifeDomainRuleMatch> domainMatches =
                        segmentAnalysis
                                .matchesOf(
                                        domain
                                );

                validateDomain(
                        domain,
                        domainMatches
                );

                matches.addAll(
                        domainMatches
                );
            }

            /*
             * =================================================
             * Match가 0개여도 Segment 객체는 유지한다.
             * =================================================
             *
             * "해당 가능 Timeline Scenario에는
             *  이 Category의 추가 Evidence가 없다"
             *
             * 는 정보도 deterministic 결과다.
             */

            result.add(
                    new ConsultationCategorySegmentEvidence(
                            segmentAnalysis
                                    .segmentAnalysis(),
                            List.copyOf(
                                    matches
                            )
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Match Domain 검증
     * =================================================
     */

    private void validateDomain(

            LifeDomain expectedDomain,

            List<LifeDomainRuleMatch> matches

    ) {

        Objects.requireNonNull(
                expectedDomain,
                "expectedDomain이 필요합니다."
        );

        Objects.requireNonNull(
                matches,
                "LifeDomainRuleMatch 목록은 null일 수 없습니다."
        );

        for (
                LifeDomainRuleMatch match
                : matches
        ) {

            Objects.requireNonNull(
                    match,
                    "LifeDomainRuleMatch는 null일 수 없습니다."
            );

            if (
                    match.domain()
                            != expectedDomain
            ) {

                throw new IllegalStateException(
                        "19차 Consultation Routing 중 LifeDomain이 일치하지 않습니다. "
                                + "expected="
                                + expectedDomain
                                + ", actual="
                                + match.domain()
                                + ", rule="
                                + match.ruleCode()
                );
            }
        }
    }

    /*
     * =================================================
     * unavailable View
     * =================================================
     */

    private ConsultationCategoryEvidenceView unavailable(

            ConsultationCategory category,

            String reason

    ) {

        Objects.requireNonNull(
                category,
                "ConsultationCategory가 필요합니다."
        );

        if (
                reason == null
                        || reason.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "unavailable ConsultationCategory에는 reason이 필요합니다."
            );
        }

        return new ConsultationCategoryEvidenceView(
                category,
                List.of(),
                List.of(),
                Optional.of(
                        reason.trim()
                )
        );
    }
}