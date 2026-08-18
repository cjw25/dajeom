package com.dajeom.analyzer;

import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.LifeDomainAnalysis;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.model.LifeDomainSegmentAnalysis;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 21차-LEGAL.
 *
 * LEGAL_RISK 전용 Cross-Domain Evidence Analyzer.
 *
 * =================================================
 *
 * 목적:
 *
 * 단일:
 *
 * - PRESSURE
 * - 충
 * - 형
 * - 관살
 * - 신살
 *
 * 하나만으로 법률/분쟁 위험을 만들지 않는다.
 *
 * =================================================
 *
 * 공식 교차 대상 Domain:
 *
 * WEALTH
 * CAREER
 * RELATIONSHIP
 *
 * =================================================
 *
 * Cross Evidence 성립 조건:
 *
 * 1.
 * PRESSURE 또는 MIXED 상태의
 * 실제 18차 LifeDomainRuleMatch.
 *
 * 2.
 * 최소 2개 서로 다른 LifeDomain.
 *
 * 3.
 * 최소 2개 서로 다른
 * non-AUXILIARY sourceKey.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 새로운 LifeDomainRuleMatch 생성 금지
 * - 기존 Match 상태 변경 금지
 * - 점수 계산 금지
 * - 단일 Evidence로 Legal Risk 생성 금지
 * - 실제 법률 사건 예측 금지
 */
@Component
public class ConsultationLegalRiskAnalyzer {

    private static final Set<LifeDomain>
            LEGAL_RELEVANT_DOMAINS =
            EnumSet.of(
                    LifeDomain.WEALTH,
                    LifeDomain.CAREER,
                    LifeDomain.RELATIONSHIP
            );

    private static final Set<String>
            CAUTION_STATUS_NAMES =
            Set.of(
                    "PRESSURE",
                    "MIXED"
            );

    /*
     * =================================================
     * 공식 API
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
         * Natal 후보
         * =================================================
         */

        List<LifeDomainRuleMatch> natalCandidates =
                collectNatalCandidates(
                        lifeDomainAnalysis
                );

        boolean natalCrossEstablished =
                crossEvidenceEstablished(
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
         * Current Timeline Segment
         * =================================================
         *
         * 각 Segment마다:
         *
         * Natal Candidate
         * +
         * Segment Candidate
         *
         * 를 결합해서 실제 Cross 조건을 검증한다.
         *
         * 단,
         * ConsultationCategorySegmentEvidence에는
         * Segment-local Match만 저장한다.
         */

        List<ConsultationCategorySegmentEvidence>
                segmentEvidences =
                new ArrayList<>(
                        lifeDomainAnalysis
                                .segmentCount()
                );

        boolean segmentCrossEstablished =
                false;

        for (
                LifeDomainSegmentAnalysis segmentAnalysis
                : lifeDomainAnalysis.segmentAnalyses()
        ) {

            Objects.requireNonNull(
                    segmentAnalysis,
                    "LEGAL_RISK Timeline Segment는 null일 수 없습니다."
            );

            List<LifeDomainRuleMatch> segmentCandidates =
                    collectSegmentCandidates(
                            segmentAnalysis
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
             * Segment 쪽 실제 변화 Match가 하나도 없는데
             * Natal Cross만으로 모든 Segment가
             * 반복 표시되는 것을 방지한다.
             */
            boolean thisSegmentCrossEstablished =
                    !segmentCandidates.isEmpty()
                            && crossEvidenceEstablished(
                            effectiveCandidates
                    );

            List<LifeDomainRuleMatch> selectedSegmentMatches;

            if (
                    thisSegmentCrossEstablished
            ) {

                segmentCrossEstablished =
                        true;

                /*
                 * 이 Segment의 Legal Cross가
                 * Natal + Segment 조합으로 성립했다면
                 * 실제 사용된 Natal 후보도 상위 View에 보존한다.
                 */
                selectedNatalMatches.addAll(
                        natalCandidates
                );

                selectedSegmentMatches =
                        List.copyOf(
                                segmentCandidates
                        );

            } else {

                selectedSegmentMatches =
                        List.of();
            }

            /*
             * 가능한 Timeline Scenario 자체는 유지한다.
             *
             * 해당 Scenario에서 Legal 관련 추가 Evidence가
             * 없다는 것 역시 deterministic 정보다.
             */
            segmentEvidences.add(
                    new ConsultationCategorySegmentEvidence(
                            segmentAnalysis
                                    .segmentAnalysis(),

                            selectedSegmentMatches
                    )
            );
        }

        /*
         * =================================================
         * Cross 조건 없음
         * =================================================
         *
         * 이것은:
         *
         * "법적 문제가 없다"
         *
         * 라는 의미가 아니다.
         *
         * 단지 현재 deterministic 계산 결과만으로
         * LEGAL_RISK 독립 해석을 만들 정도의
         * 교차 Evidence가 성립하지 않았다는 뜻이다.
         */

        if (
                !natalCrossEstablished
                        && !segmentCrossEstablished
        ) {

            return unavailable(
                    "현재 계산된 LifeDomain Evidence에서는 "
                            + "LEGAL_RISK 전용 복수 Domain 교차 조건이 성립하지 않았습니다."
            );
        }

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
                : LEGAL_RELEVANT_DOMAINS
        ) {

            List<LifeDomainRuleMatch> matches =
                    lifeDomainAnalysis
                            .natalMatchesOf(
                                    domain
                            );

            addCautionCandidates(
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
                : LEGAL_RELEVANT_DOMAINS
        ) {

            List<LifeDomainRuleMatch> matches =
                    segmentAnalysis
                            .matchesOf(
                                    domain
                            );

            addCautionCandidates(
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
     */

    private void addCautionCandidates(

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

            Objects.requireNonNull(
                    match,
                    "LEGAL_RISK LifeDomainRuleMatch는 null일 수 없습니다."
            );

            if (
                    match.domain()
                            != expectedDomain
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK 분석 중 Match Domain이 일치하지 않습니다. "
                                + "expected="
                                + expectedDomain
                                + ", actual="
                                + match.domain()
                                + ", rule="
                                + match.ruleCode()
                );
            }

            /*
             * 18차 RuleEngine에서도 막고 있지만
             * Legal 전용 경계에서 한 번 더 검증한다.
             */
            if (
                    match.auxiliaryOnly()
            ) {

                throw new IllegalStateException(
                        "LEGAL_RISK에 AUXILIARY-only Match를 사용할 수 없습니다. "
                                + "domain="
                                + match.domain()
                                + ", rule="
                                + match.ruleCode()
                );
            }

            if (
                    cautionBearing(
                            match
                    )
            ) {

                target.add(
                        match
                );
            }
        }
    }

    /*
     * =================================================
     * Status
     * =================================================
     *
     * 현재 LifeDomainSignalStatus의 실제 enum 상수를
     * 직접 추측하지 않고 name()을 사용한다.
     *
     * PRESSURE:
     * 주의 방향 존재.
     *
     * MIXED:
     * 지원과 압력이 함께 존재하므로
     * Legal Cross 후보로 사용할 수 있다.
     *
     * 나머지 Status는 현재 Legal 후보에서 제외한다.
     */

    private boolean cautionBearing(
            LifeDomainRuleMatch match
    ) {

        Objects.requireNonNull(
                match,
                "LEGAL_RISK Status 확인에는 LifeDomainRuleMatch가 필요합니다."
        );

        return CAUTION_STATUS_NAMES
                .contains(
                        match
                                .status()
                                .name()
                );
    }

    /*
     * =================================================
     * Cross Evidence
     * =================================================
     */

    private boolean crossEvidenceEstablished(
            List<LifeDomainRuleMatch> matches
    ) {

        if (
                matches == null
                        || matches.size() < 2
        ) {

            return false;
        }

        Set<LifeDomain> domains =
                EnumSet.noneOf(
                        LifeDomain.class
                );

        Set<String> primarySourceKeys =
                new LinkedHashSet<>();

        for (
                LifeDomainRuleMatch match
                : matches
        ) {

            if (
                    match == null
                            || !cautionBearing(
                            match
                    )
            ) {

                continue;
            }

            if (
                    !LEGAL_RELEVANT_DOMAINS
                            .contains(
                                    match.domain()
                            )
            ) {

                continue;
            }

            domains.add(
                    match.domain()
            );

            for (
                    LifeDomainEvidenceReference<?> evidence
                    : match.evidences()
            ) {

                Objects.requireNonNull(
                        evidence,
                        "LEGAL_RISK Match Evidence는 null일 수 없습니다."
                );

                /*
                 * AUXILIARY Source는
                 * 독립 교차 Source 개수에 포함하지 않는다.
                 */
                if (
                        evidence.auxiliary()
                ) {

                    continue;
                }

                String sourceKey =
                        evidence.sourceKey();

                if (
                        sourceKey == null
                                || sourceKey.isBlank()
                ) {

                    throw new IllegalStateException(
                            "LEGAL_RISK 교차 검증에 사용하는 non-AUXILIARY Evidence에는 sourceKey가 필요합니다. "
                                    + "domain="
                                    + match.domain()
                                    + ", rule="
                                    + match.ruleCode()
                    );
                }

                primarySourceKeys.add(
                        sourceKey.trim()
                );
            }
        }

        return domains.size() >= 2
                && primarySourceKeys.size() >= 2;
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