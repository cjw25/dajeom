package com.dajeom.analyzer;

import com.dajeom.constant.ConsultationCategoryEvidencePolicyRegistry;
import com.dajeom.model.ConsultationCategoryEvidenceAnalysis;
import com.dajeom.model.ConsultationCategoryEvidencePolicy;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.ConsultationCategoryTimelineYearEvidence;
import com.dajeom.model.ConsultationTimelineRangeAnalysis;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.LifeDomainSegmentAnalysis;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * 정밀화 19차
 * / 21차 Range
 * / Category RuleCode 선택 Routing
 * / LEGAL_RISK 전용 Cross-Domain Analyzer 연결.
 *
 * 18차 LifeDomainAnalysis를
 * ConsultationCategory Evidence View로
 * deterministic하게 Routing한다.
 *
 * =================================================
 * 일반 Category
 * =================================================
 *
 * 일반 Category는 정책에 따라:
 *
 * - LifeDomain 전체
 * - 특정 LifeDomain의 특정 RuleCode
 *
 * 를 선별할 수 있다.
 *
 * =================================================
 * PAST_LUCK / FUTURE_LUCK
 * =================================================
 *
 * 실제 Timeline Range Analysis를 사용한다.
 *
 * 명리연도 단위로:
 *
 * - month coverage
 * - timeline uncertainty
 * - deterministic Match
 *
 * 를 압축한다.
 *
 * 기존 Range 계약을 유지한다.
 *
 * =================================================
 * LEGAL_RISK
 * =================================================
 *
 * LEGAL_RISK는 일반 Domain Routing을 사용하지 않는다.
 *
 * ConsultationCategoryEvidencePolicyRegistry에서:
 *
 * requiresDedicatedCrossDomainRule
 * =
 * true
 *
 * 로 선언되어 있으며,
 *
 * 이 Analyzer는 해당 Category를:
 *
 * ConsultationLegalRiskAnalyzer
 *
 * 에 위임한다.
 *
 * =================================================
 * LEGAL_RISK 의미
 * =================================================
 *
 * ConsultationLegalRiskAnalyzer가 Java에서
 * 명시적으로 계산한:
 *
 * - WEALTH
 * - CAREER
 * - RELATIONSHIP
 *
 * 의 PRESSURE / MIXED Evidence,
 *
 * 복수 Domain,
 *
 * 복수 independent provenance
 *
 * 조건만 사용한다.
 *
 * 따라서 이 클래스에서:
 *
 * - Domain 관계 생성
 * - Cross 조건 재계산
 * - Status 재계산
 * - 사건 추론
 *
 * 을 하지 않는다.
 *
 * =================================================
 * LEGAL_RISK Provenance
 * =================================================
 *
 * LEGAL_RISK의 독립 Source 판정은
 * ConsultationLegalRiskAnalyzer 내부의
 * 공식 계산을 그대로 사용한다.
 *
 * 따라서 이 Routing Analyzer에서:
 *
 * ANNUAL_CONTEXT
 * +
 * ANNUAL_FUNCTIONAL
 *
 * 또는:
 *
 * MONTHLY_CONTEXT
 * +
 * MONTHLY_FUNCTIONAL
 *
 * 을 다시 독립 Source로 세지 않는다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 새로운 명리 계산
 * - Status 재판정
 * - 다수결
 * - 반복 횟수 점수화
 * - 대표 월 선택
 * - 서로 다른 Evidence 관계 합성
 *
 * 을 하지 않는다.
 *
 * 오직:
 *
 * - 일반 Category
 *   -> Policy Routing
 *
 * - PAST/FUTURE
 *   -> Timeline Range Routing
 *
 * - LEGAL_RISK
 *   -> 전용 Java Analyzer
 *
 * 로 이미 계산된 결과를
 * 올바른 Category View로 전달한다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 일반 Category Routing만으로:
 *
 * A 때문에 B
 * A가 B로 이어진다
 * A와 B가 함께 있으므로 C다
 *
 * 같은 의미를 만들지 않는다.
 *
 * LEGAL_RISK의 Cross는 예외적으로
 * 별도 Java Analyzer가 명시적으로 계산한
 * 공식 Cross-Domain Contract이므로
 * 그 결과만 인정한다.
 */
@Component
public class ConsultationCategoryEvidenceAnalyzer {

    private final ConsultationCategoryEvidencePolicyRegistry
            policyRegistry;

    private final ConsultationLegalRiskAnalyzer
            legalRiskAnalyzer;


    public ConsultationCategoryEvidenceAnalyzer(

            ConsultationCategoryEvidencePolicyRegistry
                    policyRegistry,

            ConsultationLegalRiskAnalyzer
                    legalRiskAnalyzer

    ) {

        this.policyRegistry =
                Objects.requireNonNull(
                        policyRegistry,
                        "ConsultationCategoryEvidencePolicyRegistry가 필요합니다."
                );

        this.legalRiskAnalyzer =
                Objects.requireNonNull(
                        legalRiskAnalyzer,
                        "ConsultationLegalRiskAnalyzer가 필요합니다."
                );
    }


    /*
     * =================================================
     * 기존 공식 19차 API
     * =================================================
     */

    public ConsultationCategoryEvidenceAnalysis analyze(
            LifeDomainAnalysis lifeDomainAnalysis
    ) {

        return analyze(
                lifeDomainAnalysis,
                null
        );
    }


    /*
     * =================================================
     * 21차 Timeline Range 연결 API
     * =================================================
     */

    public ConsultationCategoryEvidenceAnalysis analyze(

            LifeDomainAnalysis lifeDomainAnalysis,

            ConsultationTimelineRangeAnalysis
                    timelineRangeAnalysis

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
                            timelineRangeAnalysis,
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

            LifeDomainAnalysis currentLifeDomainAnalysis,

            ConsultationTimelineRangeAnalysis
                    timelineRangeAnalysis,

            ConsultationCategoryEvidencePolicy policy

    ) {

        Objects.requireNonNull(
                currentLifeDomainAnalysis,
                "Category Routing에는 LifeDomainAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                policy,
                "Category Routing에는 ConsultationCategoryEvidencePolicy가 필요합니다."
        );

        /*
         * =================================================
         * PAST_LUCK
         * =================================================
         */

        if (
                policy.requiresPastTimelineRange()
        ) {

            if (
                    timelineRangeAnalysis == null
            ) {

                return unavailable(
                        policy.category(),
                        "PAST_LUCK은 실제 과거 Timeline Range 분석이 필요합니다."
                );
            }

            if (
                    !timelineRangeAnalysis
                            .hasPastRange()
            ) {

                return unavailable(
                        policy.category(),
                        "PAST_LUCK에 사용할 과거 Timeline Range가 존재하지 않습니다."
                );
            }

            return routeTimelineRange(
                    policy.category(),
                    timelineRangeAnalysis
                            .pastAnalyses()
            );
        }

        /*
         * =================================================
         * FUTURE_LUCK
         * =================================================
         */

        if (
                policy.requiresFutureTimelineRange()
        ) {

            if (
                    timelineRangeAnalysis == null
            ) {

                return unavailable(
                        policy.category(),
                        "FUTURE_LUCK은 실제 미래 Timeline Range 분석이 필요합니다."
                );
            }

            if (
                    !timelineRangeAnalysis
                            .hasFutureRange()
            ) {

                return unavailable(
                        policy.category(),
                        "FUTURE_LUCK에 사용할 미래 Timeline Range가 존재하지 않습니다."
                );
            }

            return routeTimelineRange(
                    policy.category(),
                    timelineRangeAnalysis
                            .futureAnalyses()
            );
        }

        /*
         * =================================================
         * Dedicated Cross-Domain Category
         * =================================================
         *
         * 현재 공식적으로 전용 Cross-Domain Analyzer를
         * 사용하는 Category는 LEGAL_RISK다.
         *
         * 여기서는 Cross 계산을 재현하지 않고
         * ConsultationLegalRiskAnalyzer의
         * 공식 결과만 사용한다.
         */

        if (
                policy.requiresDedicatedCrossDomainRule()
        ) {

            if (
                    policy.category()
                            != ConsultationCategory.LEGAL_RISK
            ) {

                throw new IllegalStateException(
                        "전용 Cross-Domain Rule이 필요한 Category에 연결된 Analyzer가 없습니다: "
                                + policy.category()
                );
            }

            ConsultationCategoryEvidenceView legalRiskView =
                    legalRiskAnalyzer
                            .analyze(
                                    currentLifeDomainAnalysis
                            );

            /*
             * =================================================
             * Category Contract 검증
             * =================================================
             *
             * 전용 Analyzer가 잘못된 Category View를
             * 반환하는 구조 오류를 즉시 차단한다.
             */

            if (
                    legalRiskView.category()
                            != ConsultationCategory.LEGAL_RISK
            ) {

                throw new IllegalStateException(
                        "ConsultationLegalRiskAnalyzer가 LEGAL_RISK가 아닌 Category View를 반환했습니다: "
                                + legalRiskView.category()
                );
            }

            return legalRiskView;
        }

        /*
         * =================================================
         * 일반 Category
         * =================================================
         */

        List<LifeDomainRuleMatch> natalMatches =
                collectNatalMatches(
                        currentLifeDomainAnalysis,
                        policy
                );

        List<ConsultationCategorySegmentEvidence>
                segmentEvidences =
                collectSegmentEvidences(
                        currentLifeDomainAnalysis,
                        policy
                );

        return new ConsultationCategoryEvidenceView(
                policy.category(),
                natalMatches,
                segmentEvidences,
                List.of(),
                Optional.empty()
        );
    }


    /*
     * =================================================
     * PAST / FUTURE Timeline Range
     * =================================================
     */

    private ConsultationCategoryEvidenceView
    routeTimelineRange(

            ConsultationCategory category,

            List<LifeDomainAnalysis> analyses

    ) {

        Objects.requireNonNull(
                category,
                "Timeline Range Routing에는 ConsultationCategory가 필요합니다."
        );

        Objects.requireNonNull(
                analyses,
                "Timeline Range Routing에는 LifeDomainAnalysis 목록이 필요합니다."
        );

        List<ConsultationCategoryTimelineYearEvidence>
                yearEvidences =
                collectTimelineYearEvidences(
                        analyses
                );

        if (
                yearEvidences.isEmpty()
        ) {

            return unavailable(
                    category,
                    category
                            + " Timeline Range에서 사용할 수 있는 연도 Evidence를 생성하지 못했습니다."
            );
        }

        return new ConsultationCategoryEvidenceView(
                category,
                List.of(),
                List.of(),
                yearEvidences,
                Optional.empty()
        );
    }


    /*
     * =================================================
     * Range -> 명리연도 Group
     * =================================================
     *
     * 기존 21차 Range 압축 계약을 그대로 유지한다.
     *
     * LifeDomainRuleMatch 객체 Identity가 아니라
     * deterministic 논리 Signature로 중복을 압축한다.
     */

    private List<ConsultationCategoryTimelineYearEvidence>
    collectTimelineYearEvidences(

            List<LifeDomainAnalysis> analyses

    ) {

        Objects.requireNonNull(
                analyses,
                "Timeline Range Routing에는 LifeDomainAnalysis 목록이 필요합니다."
        );

        Map<
                Integer,
                TimelineYearAccumulator
                > grouped =
                new TreeMap<>();

        for (
                LifeDomainAnalysis analysis
                : analyses
        ) {

            Objects.requireNonNull(
                    analysis,
                    "Timeline Range의 LifeDomainAnalysis는 null일 수 없습니다."
            );

            for (
                    LifeDomainSegmentAnalysis segmentAnalysis
                    : analysis.segmentAnalyses()
            ) {

                Objects.requireNonNull(
                        segmentAnalysis,
                        "Timeline Range의 LifeDomainSegmentAnalysis는 null일 수 없습니다."
                );

                int sajuYear =
                        segmentAnalysis
                                .segmentAnalysis()
                                .annualCandidate()
                                .targetYear();

                int monthIndex =
                        segmentAnalysis
                                .segmentAnalysis()
                                .monthlyCandidate()
                                .monthIndex();

                TimelineYearAccumulator accumulator =
                        grouped.computeIfAbsent(
                                sajuYear,
                                TimelineYearAccumulator::new
                        );

                /*
                 * =================================================
                 * Month Coverage
                 * =================================================
                 */

                accumulator.addMonthIndex(
                        monthIndex
                );

                /*
                 * =================================================
                 * Timeline Uncertainty
                 * =================================================
                 */

                if (
                        analysis
                                .uncertaintyExists()

                                || segmentAnalysis
                                .segmentAnalysis()
                                .timelineUncertaintyExists()
                ) {

                    accumulator.markTimelineUncertainty();
                }

                /*
                 * =================================================
                 * 모든 LifeDomain
                 * =================================================
                 *
                 * PAST_LUCK / FUTURE_LUCK은
                 * 특정 하나의 LifeDomain이 아니라
                 * 전체 생활영역 Timeline View다.
                 *
                 * selected RuleCode Routing 변경은
                 * Range View에는 적용하지 않는다.
                 */

                for (
                        LifeDomain domain
                        : LifeDomain.values()
                ) {

                    List<LifeDomainRuleMatch> matches =
                            segmentAnalysis
                                    .matchesOf(
                                            domain
                                    );

                    validateDomain(
                            domain,
                            matches
                    );

                    accumulator.addMatches(
                            matches
                    );
                }
            }
        }

        List<ConsultationCategoryTimelineYearEvidence>
                result =
                new ArrayList<>(
                        grouped.size()
                );

        for (
                TimelineYearAccumulator accumulator
                : grouped.values()
        ) {

            result.add(
                    accumulator.toEvidence()
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Natal Routing
     * =================================================
     *
     * 기존:
     *
     * natalDomains에 포함된 Domain 전체만 Routing.
     *
     * =================================================
     *
     * 확장:
     *
     * 1.
     * Domain 전체가 허용되면
     * 해당 Domain의 모든 Match를 보존한다.
     *
     * 2.
     * Domain 전체가 허용되지 않았더라도
     * selectedNatalRuleCodes에
     * 정확한 RuleCode가 등록되어 있으면
     * 그 Match만 보존한다.
     *
     * =================================================
     * 중요
     * =================================================
     *
     * - Status 변경 없음
     * - summary 파싱 없음
     * - reasons 파싱 없음
     * - RuleCode 정확 일치만 사용
     */

    private List<LifeDomainRuleMatch>
    collectNatalMatches(

            LifeDomainAnalysis lifeDomainAnalysis,

            ConsultationCategoryEvidencePolicy policy

    ) {

        Objects.requireNonNull(
                lifeDomainAnalysis,
                "Natal Routing에는 LifeDomainAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                policy,
                "Natal Routing에는 ConsultationCategoryEvidencePolicy가 필요합니다."
        );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            boolean fullDomainAccepted =
                    policy.acceptsNatalDomain(
                            domain
                    );

            boolean selectedRulesExist =
                    policy.hasSelectedNatalRulesFor(
                            domain
                    );

            /*
             * Domain 전체도 아니고
             * 해당 Domain의 selected Rule도 없다면
             * 조회 자체를 하지 않는다.
             */

            if (
                    !fullDomainAccepted
                            && !selectedRulesExist
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

            for (
                    LifeDomainRuleMatch match
                    : matches
            ) {

                LifeDomainRuleMatch normalized =
                        Objects.requireNonNull(
                                match,
                                "Natal Routing의 LifeDomainRuleMatch는 null일 수 없습니다."
                        );

                /*
                 * =================================================
                 * 공식 Policy 판정
                 * =================================================
                 *
                 * acceptsNatalRule():
                 *
                 * Domain 전체 허용
                 * -> true
                 *
                 * 또는:
                 *
                 * selected RuleCode 정확 일치
                 * -> true
                 */

                if (
                        !policy.acceptsNatalRule(
                                domain,
                                normalized.ruleCode()
                        )
                ) {

                    continue;
                }

                result.add(
                        normalized
                );
            }
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Current Segment Routing
     * =================================================
     *
     * 기존:
     *
     * segmentDomains에 포함된 Domain 전체만 Routing.
     *
     * =================================================
     *
     * 확장:
     *
     * selectedSegmentRuleCodes만 존재하는 Policy도
     * 정상적으로 Segment Evidence를 생성한다.
     *
     * 1.
     * Domain 전체 허용
     * -> 모든 Match 보존
     *
     * 2.
     * Domain 전체 미허용
     * +
     * selected RuleCode 등록
     * -> 해당 Match만 보존
     */

    private List<ConsultationCategorySegmentEvidence>
    collectSegmentEvidences(

            LifeDomainAnalysis lifeDomainAnalysis,

            ConsultationCategoryEvidencePolicy policy

    ) {

        Objects.requireNonNull(
                lifeDomainAnalysis,
                "Segment Routing에는 LifeDomainAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                policy,
                "Segment Routing에는 ConsultationCategoryEvidencePolicy가 필요합니다."
        );

        /*
         * =================================================
         * 아무 Segment Routing도 없는 경우
         * =================================================
         *
         * 기존에는 segmentDomains.isEmpty()만 봤다.
         *
         * 이제 selectedSegmentRuleCodes만 존재할 수도 있으므로
         * 둘 다 비어 있을 때만 종료한다.
         */

        if (
                policy.segmentDomains()
                        .isEmpty()

                        && !policy
                        .hasSelectedSegmentRules()
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

            Objects.requireNonNull(
                    segmentAnalysis,
                    "Segment Routing의 LifeDomainSegmentAnalysis는 null일 수 없습니다."
            );

            List<LifeDomainRuleMatch> matches =
                    new ArrayList<>();

            for (
                    LifeDomain domain
                    : LifeDomain.values()
            ) {

                boolean fullDomainAccepted =
                        policy.acceptsSegmentDomain(
                                domain
                        );

                boolean selectedRulesExist =
                        policy.hasSelectedSegmentRulesFor(
                                domain
                        );

                if (
                        !fullDomainAccepted
                                && !selectedRulesExist
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

                for (
                        LifeDomainRuleMatch match
                        : domainMatches
                ) {

                    LifeDomainRuleMatch normalized =
                            Objects.requireNonNull(
                                    match,
                                    "Segment Routing의 LifeDomainRuleMatch는 null일 수 없습니다."
                            );

                    if (
                            !policy.acceptsSegmentRule(
                                    domain,
                                    normalized.ruleCode()
                            )
                    ) {

                        continue;
                    }

                    matches.add(
                            normalized
                    );
                }
            }

            /*
             * Segment 자체의 Timeline 좌표와
             * uncertainty 정보는 기존과 동일하게 보존한다.
             *
             * 선택된 Match가 없는 Segment라도
             * 기존 Domain Routing과 동일하게
             * Segment Evidence 구조는 유지한다.
             */

            result.add(
                    new ConsultationCategorySegmentEvidence(
                            segmentAnalysis
                                    .segmentAnalysis(),
                            matches
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Domain 검증
     * =================================================
     */

    private void validateDomain(

            LifeDomain expectedDomain,

            List<LifeDomainRuleMatch> matches

    ) {

        Objects.requireNonNull(
                expectedDomain,
                "LifeDomain 검증에는 expectedDomain이 필요합니다."
        );

        Objects.requireNonNull(
                matches,
                "LifeDomain Match 목록은 null일 수 없습니다."
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
                        "LifeDomainRuleMatch의 Domain이 조회 Domain과 일치하지 않습니다. "
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
     * Unavailable
     * =================================================
     */

    private ConsultationCategoryEvidenceView unavailable(

            ConsultationCategory category,

            String reason

    ) {

        Objects.requireNonNull(
                category,
                "Unavailable View에는 ConsultationCategory가 필요합니다."
        );

        if (
                reason == null
                        || reason.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Unavailable View에는 사유가 필요합니다."
            );
        }

        return new ConsultationCategoryEvidenceView(
                category,
                List.of(),
                List.of(),
                List.of(),
                Optional.of(
                        reason.trim()
                )
        );
    }


    /*
     * =================================================
     * Timeline Logical Match Key
     * =================================================
     *
     * LifeDomainRuleMatch의 upstream source 객체
     * Identity를 Year 압축 기준에서 제거한다.
     *
     * 보존:
     *
     * - domain
     * - ruleCode
     * - status
     * - summary
     * - Evidence provenance metadata
     * - reasons
     *
     * 제거:
     *
     * - LifeDomainEvidenceReference.source() 객체 Identity
     */

    private record TimelineMatchKey(

            LifeDomain domain,

            String ruleCode,

            String status,

            String summary,

            List<TimelineEvidenceKey> evidences,

            List<String> reasons

    ) {

        private TimelineMatchKey {

            Objects.requireNonNull(
                    domain,
                    "Timeline Match Key에는 LifeDomain이 필요합니다."
            );

            Objects.requireNonNull(
                    ruleCode,
                    "Timeline Match Key에는 ruleCode가 필요합니다."
            );

            Objects.requireNonNull(
                    status,
                    "Timeline Match Key에는 status가 필요합니다."
            );

            Objects.requireNonNull(
                    summary,
                    "Timeline Match Key에는 summary가 필요합니다."
            );

            evidences =
                    evidences == null
                            ? List.of()
                            : List.copyOf(
                            evidences
                    );

            reasons =
                    reasons == null
                            ? List.of()
                            : List.copyOf(
                            reasons
                    );
        }


        private static TimelineMatchKey of(
                LifeDomainRuleMatch match
        ) {

            Objects.requireNonNull(
                    match,
                    "Timeline Match Key 생성에는 LifeDomainRuleMatch가 필요합니다."
            );

            List<TimelineEvidenceKey> evidenceKeys =
                    match
                            .evidences()
                            .stream()
                            .map(
                                    TimelineEvidenceKey::of
                            )
                            .toList();

            return new TimelineMatchKey(
                    match.domain(),
                    match.ruleCode(),
                    String.valueOf(
                            match.status()
                    ),
                    match.summary(),
                    evidenceKeys,
                    match.reasons()
            );
        }
    }


    /*
     * =================================================
     * Timeline Evidence Logical Key
     * =================================================
     *
     * 실제 source() 객체는 사용하지 않는다.
     *
     * deterministic provenance metadata:
     *
     * - sourceType
     * - tier
     * - sourceKey
     *
     * 만 사용한다.
     */

    private record TimelineEvidenceKey(

            String sourceType,

            String tier,

            String sourceKey

    ) {

        private TimelineEvidenceKey {

            Objects.requireNonNull(
                    sourceType,
                    "Timeline Evidence Key에는 sourceType이 필요합니다."
            );

            Objects.requireNonNull(
                    tier,
                    "Timeline Evidence Key에는 tier가 필요합니다."
            );

            Objects.requireNonNull(
                    sourceKey,
                    "Timeline Evidence Key에는 sourceKey가 필요합니다."
            );
        }


        private static TimelineEvidenceKey of(
                LifeDomainEvidenceReference<?> reference
        ) {

            Objects.requireNonNull(
                    reference,
                    "Timeline Evidence Key 생성에는 LifeDomainEvidenceReference가 필요합니다."
            );

            return new TimelineEvidenceKey(
                    String.valueOf(
                            reference.sourceType()
                    ),
                    String.valueOf(
                            reference.tier()
                    ),
                    reference.sourceKey()
            );
        }
    }


    /*
     * =================================================
     * Timeline Year Accumulator
     * =================================================
     *
     * 이미 계산된 월별 Match를
     * 하나의 명리연도 아래에 구조적으로 모은다.
     *
     * 동일 deterministic Signature 반복:
     * -> 1회
     *
     * 실제 월별 변화:
     * -> 보존
     *
     * 실제 교운 변화:
     * -> 보존
     *
     * Timeline uncertainty:
     * -> 보존
     */

    private static final class TimelineYearAccumulator {

        private final int sajuYear;

        private final Set<Integer>
                monthIndices =
                new LinkedHashSet<>();

        private final Map<
                TimelineMatchKey,
                LifeDomainRuleMatch
                > matches =
                new LinkedHashMap<>();

        private boolean
                timelineUncertaintyExists;


        private TimelineYearAccumulator(
                int sajuYear
        ) {

            this.sajuYear =
                    sajuYear;
        }


        /*
         * =================================================
         * Month
         * =================================================
         */

        private void addMonthIndex(
                int monthIndex
        ) {

            monthIndices.add(
                    monthIndex
            );
        }


        /*
         * =================================================
         * Match
         * =================================================
         */

        private void addMatches(
                List<LifeDomainRuleMatch> values
        ) {

            Objects.requireNonNull(
                    values,
                    "Timeline Year Accumulator Match 목록은 null일 수 없습니다."
            );

            for (
                    LifeDomainRuleMatch match
                    : values
            ) {

                LifeDomainRuleMatch normalized =
                        Objects.requireNonNull(
                                match,
                                "Timeline Year Accumulator Match는 null일 수 없습니다."
                        );

                TimelineMatchKey key =
                        TimelineMatchKey.of(
                                normalized
                        );

                matches.putIfAbsent(
                        key,
                        normalized
                );
            }
        }


        /*
         * =================================================
         * Uncertainty
         * =================================================
         */

        private void markTimelineUncertainty() {

            timelineUncertaintyExists =
                    true;
        }


        /*
         * =================================================
         * Evidence
         * =================================================
         */

        private ConsultationCategoryTimelineYearEvidence
        toEvidence() {

            return new ConsultationCategoryTimelineYearEvidence(
                    sajuYear,
                    new ArrayList<>(
                            monthIndices
                    ),
                    timelineUncertaintyExists,
                    new ArrayList<>(
                            matches.values()
                    )
            );
        }
    }
}