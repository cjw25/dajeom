package com.dajeom.analyzer;

import com.dajeom.constant.ConsultationCategoryEvidencePolicyRegistry;
import com.dajeom.model.ConsultationCategoryEvidenceAnalysis;
import com.dajeom.model.ConsultationCategoryEvidencePolicy;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.ConsultationCategoryTimelineYearEvidence;
import com.dajeom.model.ConsultationTimelineRangeAnalysis;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.LifeDomainSegmentAnalysis;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/**
 * 정밀화 19차 / 21차 확장.
 *
 * 18차 LifeDomainAnalysis를
 * 11개 ConsultationCategory Evidence View로
 * deterministic하게 Routing한다.
 *
 * =================================================
 *
 * 일반 Category:
 *
 * 현재 LifeDomainAnalysis.
 *
 * =================================================
 *
 * PAST_LUCK:
 *
 * 실제 과거 Timeline Range.
 *
 * =================================================
 *
 * FUTURE_LUCK:
 *
 * 실제 미래 Timeline Range.
 *
 * =================================================
 *
 * LEGAL_RISK:
 *
 * ConsultationLegalRiskAnalyzer의
 * 전용 Cross-Domain Rule.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 새로운 명리 계산을 하지 않는다.
 * - 상태를 합산하지 않는다.
 * - 대표 polarity를 만들지 않는다.
 * - 현재 Segment를 과거/미래 전체로 사용하지 않는다.
 * - LEGAL_RISK를 단일 Match로 생성하지 않는다.
 */
@Component
public class ConsultationCategoryEvidenceAnalyzer {

    private final ConsultationCategoryEvidencePolicyRegistry
            policyRegistry;

    private final ConsultationLegalRiskAnalyzer
            consultationLegalRiskAnalyzer;

    public ConsultationCategoryEvidenceAnalyzer(

            ConsultationCategoryEvidencePolicyRegistry
                    policyRegistry,

            ConsultationLegalRiskAnalyzer
                    consultationLegalRiskAnalyzer

    ) {

        this.policyRegistry =
                Objects.requireNonNull(
                        policyRegistry,
                        "ConsultationCategoryEvidencePolicyRegistry가 필요합니다."
                );

        this.consultationLegalRiskAnalyzer =
                Objects.requireNonNull(
                        consultationLegalRiskAnalyzer,
                        "ConsultationLegalRiskAnalyzer가 필요합니다."
                );
    }

    /*
     * =================================================
     * 기존 공식 API
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
     * Timeline Range 연결 API
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
         * LEGAL_RISK
         * =================================================
         */

        if (
                policy.requiresDedicatedCrossDomainRule()
        ) {

            if (
                    policy.category()
                            != ConsultationCategory.LEGAL_RISK
            ) {

                throw new IllegalStateException(
                        "현재 전용 Cross-Domain Rule은 LEGAL_RISK에만 구현되어 있습니다: "
                                + policy.category()
                );
            }

            return consultationLegalRiskAnalyzer
                    .analyze(
                            currentLifeDomainAnalysis
                    );
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
     * Range -> Year
     * =================================================
     */

    private List<ConsultationCategoryTimelineYearEvidence>
    collectTimelineYearEvidences(

            List<LifeDomainAnalysis> analyses

    ) {

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

                accumulator.addMonthIndex(
                        monthIndex
                );

                if (
                        analysis
                                .uncertaintyExists()

                                || segmentAnalysis
                                .segmentAnalysis()
                                .timelineUncertaintyExists()
                ) {

                    accumulator
                            .markTimelineUncertainty();
                }

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
     * Natal
     * =================================================
     */

    private List<LifeDomainRuleMatch>
    collectNatalMatches(

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
     * Current Segment
     * =================================================
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
     * Domain Validation
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
     * Year Accumulator
     * =================================================
     */

    private static final class TimelineYearAccumulator {

        private final int sajuYear;

        private final Set<Integer>
                monthIndices =
                new LinkedHashSet<>();

        private final Set<LifeDomainRuleMatch>
                matches =
                new LinkedHashSet<>();

        private boolean
                timelineUncertaintyExists;

        private TimelineYearAccumulator(
                int sajuYear
        ) {

            this.sajuYear =
                    sajuYear;
        }

        private void addMonthIndex(
                int monthIndex
        ) {

            monthIndices.add(
                    monthIndex
            );
        }

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

                matches.add(
                        Objects.requireNonNull(
                                match,
                                "Timeline Year Accumulator Match는 null일 수 없습니다."
                        )
                );
            }
        }

        private void markTimelineUncertainty() {

            timelineUncertaintyExists =
                    true;
        }

        private ConsultationCategoryTimelineYearEvidence
        toEvidence() {

            return new ConsultationCategoryTimelineYearEvidence(
                    sajuYear,

                    new ArrayList<>(
                            monthIndices
                    ),

                    timelineUncertaintyExists,

                    new ArrayList<>(
                            matches
                    )
            );
        }
    }
}