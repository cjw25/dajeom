package com.dajeom.analyzer;

import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.LifeDomainSegmentAnalysis;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * LEGAL_RISK 전용 Evidence Analyzer.
 *
 * =================================================
 * 역할
 * =================================================
 *
 * LifeDomainAnalysis에서:
 *
 * WEALTH
 * CAREER
 * RELATIONSHIP
 *
 * 의 LEGAL_RISK 후보를 수집하고,
 *
 * ConsultationLegalRiskCrossEvaluator가
 * 공식적으로 계산한 Cross 자격 결과에 따라
 * LEGAL_RISK Evidence View를 구성한다.
 *
 * =================================================
 * 중요한 구조
 * =================================================
 *
 * 이 클래스는 더 이상:
 *
 * - Domain 개수 계산
 * - independent provenance 개수 계산
 * - Annual provenance family 정규화
 * - Monthly provenance family 정규화
 *
 * 를 직접 하지 않는다.
 *
 * 위 계산은 모두:
 *
 * ConsultationLegalRiskCrossEvaluator
 *
 * 한 곳의 공식 계산을 사용한다.
 *
 * =================================================
 * Cross 조건
 * =================================================
 *
 * 공식 Cross 자격 조건은:
 *
 * ConsultationLegalRiskCrossEvaluator
 *
 * 가 소유한다.
 *
 * 이 Analyzer는 그 결과를 이용해:
 *
 * - Natal Evidence를 보존할지
 * - Segment-local Evidence를 보존할지
 * - LEGAL_RISK를 unavailable로 만들지
 *
 * 만 결정한다.
 *
 * =================================================
 * NATAL
 * =================================================
 *
 * Natal Candidate 자체가
 * Legal Cross 조건을 만족하면
 * natalMatches에 보존한다.
 *
 * =================================================
 * CURRENT_TIMELINE
 * =================================================
 *
 * 각 Segment에서는:
 *
 * Natal Candidate
 * +
 * 해당 Segment Candidate
 *
 * 를 공식 Cross Evaluator에 전달한다.
 *
 * 단:
 *
 * Segment-local Candidate가 하나도 없으면
 * Natal Cross만으로 모든 현재 Segment에
 * 같은 Legal 결과를 반복하지 않는다.
 *
 * =================================================
 * Provenance 보존
 * =================================================
 *
 * Segment Cross가
 * Natal + Segment 조합으로 성립하면
 * 실제 계산에 참여한 Natal Candidate도
 * 상위 View.natalMatches에 보존한다.
 *
 * ConsultationCategorySegmentEvidence에는
 * 현재 Segment-local Match만 저장한다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 여기서 Cross는:
 *
 * 여러 독립 Evidence가
 * LEGAL_RISK 전용 자격 조건을
 * 동시에 충족했는지를 계산하는 것이다.
 *
 * 다음을 계산하지 않는다.
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A가 B를 강화한다
 * - 먼저 A 이후 B
 * - 특정 사건으로 이어진다
 *
 * =================================================
 * 사건 예측 금지
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 소송 발생
 * - 형사 사건
 * - 민사 사건
 * - 고소
 * - 고발
 * - 계약 분쟁
 * - 세무 사건
 * - 승소
 * - 패소
 * - 처벌
 *
 * 을 생성하거나 예측하지 않는다.
 */
@Component
public class ConsultationLegalRiskAnalyzer {

    private final ConsultationLegalRiskCrossEvaluator
            crossEvaluator;


    /*
     * =================================================
     * Constructor
     * =================================================
     */

    public ConsultationLegalRiskAnalyzer(

            ConsultationLegalRiskCrossEvaluator
                    crossEvaluator

    ) {

        this.crossEvaluator =
                Objects.requireNonNull(
                        crossEvaluator,
                        "ConsultationLegalRiskCrossEvaluator가 필요합니다."
                );
    }


    /*
     * =================================================
     * Official API
     * =================================================
     */

    public ConsultationCategoryEvidenceView analyze(

            LifeDomainAnalysis lifeDomainAnalysis

    ) {

        Objects.requireNonNull(
                lifeDomainAnalysis,
                "LEGAL_RISK 분석에는 LifeDomainAnalysis가 필요합니다."
        );


        /*
         * =================================================
         * Natal Candidate
         * =================================================
         */

        List<LifeDomainRuleMatch> natalCandidates =
                collectNatalCandidates(
                        lifeDomainAnalysis
                );

        boolean natalCrossEstablished =
                crossEvaluator
                        .established(
                                natalCandidates
                        );

        Set<LifeDomainRuleMatch> selectedNatalMatches =
                new LinkedHashSet<>();

        if (
                natalCrossEstablished
        ) {

            selectedNatalMatches.addAll(
                    natalCandidates
            );
        }


        /*
         * =================================================
         * Current Timeline
         * =================================================
         */

        List<ConsultationCategorySegmentEvidence>
                segmentEvidences =
                new ArrayList<>(
                        lifeDomainAnalysis
                                .segmentCount()
                );

        boolean anySegmentCrossEstablished =
                false;

        for (
                LifeDomainSegmentAnalysis segmentAnalysis
                : lifeDomainAnalysis.segmentAnalyses()
        ) {

            LifeDomainSegmentAnalysis normalizedSegment =
                    Objects.requireNonNull(
                            segmentAnalysis,
                            "LEGAL_RISK Timeline Segment는 null일 수 없습니다."
                    );

            List<LifeDomainRuleMatch> segmentCandidates =
                    collectSegmentCandidates(
                            normalizedSegment
                    );

            List<LifeDomainRuleMatch> effectiveCandidates =
                    new ArrayList<>(
                            natalCandidates.size()
                                    + segmentCandidates.size()
                    );

            effectiveCandidates.addAll(
                    natalCandidates
            );

            effectiveCandidates.addAll(
                    segmentCandidates
            );


            /*
             * =================================================
             * Segment Cross
             * =================================================
             *
             * Segment Candidate가 실제로 있어야
             * CURRENT_TIMELINE Legal Cross를 인정한다.
             */

            boolean segmentCrossEstablished =
                    !segmentCandidates.isEmpty()

                            && crossEvaluator
                            .established(
                                    effectiveCandidates
                            );

            List<LifeDomainRuleMatch> selectedSegmentMatches;

            if (
                    segmentCrossEstablished
            ) {

                anySegmentCrossEstablished =
                        true;

                /*
                 * Segment Cross 계산에 실제 사용된
                 * Natal 후보를 상위 View에도 보존한다.
                 */

                selectedNatalMatches.addAll(
                        natalCandidates
                );

                /*
                 * Segment 객체에는
                 * Segment-local Match만 저장한다.
                 */

                selectedSegmentMatches =
                        List.copyOf(
                                segmentCandidates
                        );

            } else {

                selectedSegmentMatches =
                        List.of();
            }


            /*
             * =================================================
             * Scenario Identity 보존
             * =================================================
             *
             * Legal Cross가 없는 가능한 Scenario도
             * Timeline 구조 자체는 유지한다.
             */

            segmentEvidences.add(
                    new ConsultationCategorySegmentEvidence(
                            normalizedSegment
                                    .segmentAnalysis(),

                            selectedSegmentMatches
                    )
            );
        }


        /*
         * =================================================
         * Unavailable
         * =================================================
         */

        if (
                !natalCrossEstablished
                        && !anySegmentCrossEstablished
        ) {

            return unavailable(
                    "현재 계산된 LifeDomain Evidence에서는 "
                            + "LEGAL_RISK 전용 복수 Domain 교차 조건이 성립하지 않았습니다."
            );
        }


        /*
         * =================================================
         * Available
         * =================================================
         */

        return new ConsultationCategoryEvidenceView(

                ConsultationCategory.LEGAL_RISK,

                List.copyOf(
                        selectedNatalMatches
                ),

                List.copyOf(
                        segmentEvidences
                ),

                List.of(),

                Optional.empty()
        );
    }


    /*
     * =================================================
     * Natal Candidate
     * =================================================
     */

    private List<LifeDomainRuleMatch>
    collectNatalCandidates(

            LifeDomainAnalysis lifeDomainAnalysis

    ) {

        Objects.requireNonNull(
                lifeDomainAnalysis,
                "LEGAL_RISK Natal 분석에는 LifeDomainAnalysis가 필요합니다."
        );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomain domain
                : crossEvaluator.relevantDomains()
        ) {

            List<LifeDomainRuleMatch> matches =
                    lifeDomainAnalysis
                            .natalMatchesOf(
                                    domain
                            );

            addCandidates(
                    result,
                    domain,
                    matches
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Segment Candidate
     * =================================================
     */

    private List<LifeDomainRuleMatch>
    collectSegmentCandidates(

            LifeDomainSegmentAnalysis segmentAnalysis

    ) {

        Objects.requireNonNull(
                segmentAnalysis,
                "LEGAL_RISK Segment 분석에는 LifeDomainSegmentAnalysis가 필요합니다."
        );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomain domain
                : crossEvaluator.relevantDomains()
        ) {

            List<LifeDomainRuleMatch> matches =
                    segmentAnalysis
                            .matchesOf(
                                    domain
                            );

            addCandidates(
                    result,
                    domain,
                    matches
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Candidate Filtering
     * =================================================
     *
     * 실제 LEGAL_RISK Candidate 자격은
     * Cross Evaluator에 위임한다.
     *
     * 이 메서드는 expectedDomain 정합성과
     * 실제 후보 Collection만 담당한다.
     */

    private void addCandidates(

            List<LifeDomainRuleMatch> target,

            LifeDomain expectedDomain,

            List<LifeDomainRuleMatch> matches

    ) {

        Objects.requireNonNull(
                target,
                "LEGAL_RISK Candidate 대상 목록이 필요합니다."
        );

        Objects.requireNonNull(
                expectedDomain,
                "LEGAL_RISK expectedDomain이 필요합니다."
        );

        Objects.requireNonNull(
                matches,
                "LEGAL_RISK LifeDomain Match 목록은 null일 수 없습니다."
        );

        for (
                LifeDomainRuleMatch match
                : matches
        ) {

            LifeDomainRuleMatch normalizedMatch =
                    Objects.requireNonNull(
                            match,
                            "LEGAL_RISK LifeDomainRuleMatch는 null일 수 없습니다."
                    );

            if (
                    normalizedMatch.domain()
                            != expectedDomain
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK 분석 중 Match Domain이 일치하지 않습니다. "
                                + "expected="
                                + expectedDomain
                                + ", actual="
                                + normalizedMatch.domain()
                                + ", rule="
                                + normalizedMatch.ruleCode()
                );
            }

            if (
                    crossEvaluator
                            .eligibleCandidate(
                                    normalizedMatch
                            )
            ) {

                target.add(
                        normalizedMatch
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

            String reason

    ) {

        if (
                reason == null
                        || reason.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "LEGAL_RISK unavailable 사유가 필요합니다."
            );
        }

        return new ConsultationCategoryEvidenceView(

                ConsultationCategory.LEGAL_RISK,

                List.of(),

                List.of(),

                List.of(),

                Optional.of(
                        reason.trim()
                )
        );
    }
}