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
 * 정밀화 19차 / 21차 Range 확장.
 *
 * 18차 LifeDomainAnalysis를
 * 11개 ConsultationCategory Evidence View로
 * deterministic하게 Routing한다.
 *
 * =================================================
 *
 * 일반 Category:
 *
 * 현재 18차 LifeDomainAnalysis를 사용한다.
 *
 * =================================================
 *
 * PAST_LUCK:
 *
 * ConsultationTimelineRangeAnalysis.pastAnalyses()
 * 를 사용한다.
 *
 * =================================================
 *
 * FUTURE_LUCK:
 *
 * ConsultationTimelineRangeAnalysis.futureAnalyses()
 * 를 사용한다.
 *
 * =================================================
 *
 * LEGAL_RISK:
 *
 * 아직 전용 Cross-Domain Rule이 없으므로
 * unavailable을 유지한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 새로운 명리 계산을 하지 않는다.
 * - 월별 LifeDomainRuleMatch를 다시 판정하지 않는다.
 * - SUPPORTIVE/PRESSURE/MIXED/CONTEXTUAL을 합산하지 않는다.
 * - 하나의 대표 월을 선택하지 않는다.
 * - 여러 월 Match를 하나의 Segment인 것처럼 위장하지 않는다.
 *
 * =================================================
 *
 * Range 압축:
 *
 * 기존 16 -> 17 -> 18차가 계산한
 * 실제 월별 Segment Match를
 * 명리연도 단위로 그룹화한다.
 *
 * =================================================
 *
 * 기존 문제:
 *
 * Timeline Range는 하나의 명리연도에 대해
 *
 * 2월 ~ 12월
 * +
 * 다음 Gregorian 연도 1월
 *
 * 총 12개 대표 시점을 계산한다.
 *
 * 각 대표 시점은 별도의:
 *
 * LuckIntegratedAnalysis
 * ShinsalAnalysis
 * LifeDomainAnalysis
 *
 * 객체를 생성한다.
 *
 * 따라서 논리적으로 같은:
 *
 * ANNUAL_CONTEXT
 * ANNUAL_FUNCTIONAL_CONTEXT
 * DECADE_CONTEXT
 *
 * 기반 Match도
 *
 * upstream 객체 Identity가 다르다는 이유로
 * LifeDomainRuleMatch equals에서 달라질 수 있다.
 *
 * =================================================
 *
 * 잘못된 결과:
 *
 * 같은 2026 세운 Functional 판단이
 * 12개 대표월에 존재한다는 이유만으로
 *
 * 12개의 독립 근거처럼
 * Timeline Year에 남을 수 있다.
 *
 * =================================================
 *
 * 이번 정밀화:
 *
 * Timeline Year 압축에서는
 * LifeDomainRuleMatch 객체 equality가 아니라
 *
 * "논리적 Match Signature"
 *
 * 를 사용한다.
 *
 * Signature:
 *
 * - domain
 * - ruleCode
 * - status
 * - summary
 * - Evidence SourceType
 * - Evidence Tier
 * - Evidence sourceKey
 * - reasons
 *
 * =================================================
 *
 * 매우 중요:
 *
 * Evidence의 실제 source() 객체 Identity는
 * Year 압축 중복 판단에서 제외한다.
 *
 * sourceKey가 공식 deterministic provenance
 * Anchor이기 때문이다.
 *
 * =================================================
 *
 * 하지만 월운은 삭제하지 않는다.
 *
 * 예:
 *
 * MONTHLY:2026:1
 * MONTHLY:2026:2
 * MONTHLY:2026:3
 *
 * 는 sourceKey가 서로 다르므로
 * 각각 별도의 Match로 남는다.
 *
 * =================================================
 *
 * 대운 교운도 삭제하지 않는다.
 *
 * 같은 Annual Functional sourceKey라 하더라도
 *
 * 현재 대운 Sequence
 * Resolved Function
 * Cross Pattern
 * Qualification
 *
 * 이 달라져 Rule의 summary/reasons/status가
 * 달라지면 별도의 Match로 남는다.
 *
 * =================================================
 *
 * 따라서:
 *
 * 동일 결과 반복
 * -> 1회
 *
 * 월별 실제 변화
 * -> 보존
 *
 * 교운 Segment별 실제 변화
 * -> 보존
 *
 * Timeline uncertainty
 * -> 보존
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 다수결
 * - 반복 횟수 점수화
 * - 대표 월 선택
 * - 가장 많이 나온 Status 선택
 * - Annual/Monthly 평균
 * - 서로 다른 Status 상쇄
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
     * 21차 Range 연결 API
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

            return unavailable(
                    policy.category(),
                    "LEGAL_RISK는 단일 LifeDomain 또는 하나의 관계 Evidence로 생성하지 않고 전용 교차 Evidence Rule이 필요합니다."
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
     * Range -> Year Group
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

                /*
                 * =================================================
                 * Month Coverage
                 * =================================================
                 *
                 * Match 중복 압축과 별개로
                 * 실제 Timeline Range가 확인한
                 * 월 Index는 모두 보존한다.
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
                 * 전체 생활영역의 시간 흐름 View다.
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

                    /*
                     * =================================================
                     * Logical Dedup
                     * =================================================
                     *
                     * 여기서 객체 Identity가 아니라
                     * deterministic Match Signature로 압축한다.
                     */

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
     * =================================================
     *
     * 보존하는 것:
     *
     * domain
     * ruleCode
     * status
     * summary
     * Evidence provenance metadata
     * Rule reasons
     *
     * =================================================
     *
     * 제거하는 것:
     *
     * LifeDomainEvidenceReference.source()
     *
     * 즉 같은 연도 계산을 다시 수행하면서
     * 새로 생성된 upstream 객체 Identity만 다르고
     * deterministic 결과가 완전히 같다면
     * 하나의 Match로 압축된다.
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
     * Evidence Logical Key
     * =================================================
     *
     * source() 객체 자체는 넣지 않는다.
     *
     * 공식 provenance metadata:
     *
     * sourceType
     * tier
     * sourceKey
     *
     * 만 사용한다.
     *
     * =================================================
     *
     * 예:
     *
     * ANNUAL_CONTEXT
     * TEMPORAL_CONTEXT
     * ANNUAL:2026
     *
     * =================================================
     *
     * 또는:
     *
     * ANNUAL_FUNCTIONAL_CONTEXT
     * TEMPORAL_CONTEXT
     * ANNUAL_FUNCTIONAL:2026
     *
     * =================================================
     *
     * 월운:
     *
     * MONTHLY_CONTEXT
     * TEMPORAL_CONTEXT
     * MONTHLY:2026:3
     *
     * 처럼 sourceKey 자체가 다르므로
     * 월별 Evidence는 유지된다.
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
     * Year Accumulator
     * =================================================
     *
     * 계산 객체가 아니다.
     *
     * 이미 계산된 월별 Match를
     * 하나의 명리연도 아래에 구조적으로 모은다.
     *
     * =================================================
     *
     * 기존:
     *
     * Set<LifeDomainRuleMatch>
     *
     * =================================================
     *
     * 변경:
     *
     * Map<
     *     TimelineMatchKey,
     *     LifeDomainRuleMatch
     * >
     *
     * =================================================
     *
     * 따라서:
     *
     * 동일 deterministic 결과
     * +
     * 서로 다른 upstream Java 객체
     *
     * 는 하나로 압축된다.
     *
     * =================================================
     *
     * 반면:
     *
     * 다른 월 SourceKey
     * 다른 Status
     * 다른 Summary
     * 다른 Reasons
     * 다른 Cross Segment 결과
     *
     * 는 별개로 남는다.
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

                /*
                 * =================================================
                 * 첫 deterministic 대표 객체 보존
                 * =================================================
                 *
                 * 대표 월을 선택한다는 의미가 아니다.
                 *
                 * 동일 Signature Match가 이미 존재할 때
                 * 새 객체를 한 번 더 추가하지 않는 것이다.
                 *
                 * 값 자체가 달라지면 Signature가 달라지므로
                 * 별도 Match로 남는다.
                 */

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