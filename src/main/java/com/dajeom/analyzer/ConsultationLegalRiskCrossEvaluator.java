package com.dajeom.analyzer;

import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * LEGAL_RISK 전용 Cross 자격 조건의
 * 단일 deterministic 계산기.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LEGAL_RISK의 Cross 성립 조건을
 * 여러 Analyzer / Rule에 중복 구현하지 않는다.
 *
 * 다음 공식 조건은 이 클래스 한 곳에서만 계산한다.
 *
 * 1.
 * PRESSURE 또는 MIXED LifeDomainRuleMatch.
 *
 * 2.
 * WEALTH / CAREER / RELATIONSHIP 가운데
 * 최소 2개 서로 다른 LifeDomain.
 *
 * 3.
 * 최소 2개 서로 다른
 * non-AUXILIARY independent provenance anchor.
 *
 * =================================================
 * 중요한 경계
 * =================================================
 *
 * 이것은 관계형 풀이가 아니다.
 *
 * 이 클래스는:
 *
 * - WEALTH 때문에 CAREER가 생긴다
 * - CAREER가 RELATIONSHIP으로 이어진다
 * - A와 B가 결합되어 특정 사건이 된다
 *
 * 같은 관계·인과·순서를 계산하지 않는다.
 *
 * 오직:
 *
 * "LEGAL_RISK 전용 Cross 자격 조건을
 * 충족하는 독립 근거 집합인가"
 *
 * 만 계산한다.
 *
 * =================================================
 * Annual Provenance Family
 * =================================================
 *
 * 동일 명리연도의:
 *
 * ANNUAL_CONTEXT
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * 는:
 *
 * ANNUAL_CONTEXT_FAMILY:<year>
 *
 * 하나의 independent provenance anchor로 본다.
 *
 * =================================================
 * Monthly Provenance Family
 * =================================================
 *
 * 동일 명리연도·월의:
 *
 * MONTHLY_CONTEXT
 * MONTHLY_FUNCTIONAL_CONTEXT
 *
 * 는:
 *
 * MONTHLY_CONTEXT_FAMILY:<year>:<month>
 *
 * 하나의 independent provenance anchor로 본다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Evidence 점수화
 * - Status 다수결
 * - Domain 우열 계산
 * - Source 개수로 강도 계산
 * - 사건 예측
 * - 관계 생성
 * - 인과 생성
 * - 시간순서 생성
 */
@Component
public class ConsultationLegalRiskCrossEvaluator {

    /*
     * =================================================
     * Relevant Domain
     * =================================================
     */

    private static final Set<LifeDomain>
            LEGAL_RELEVANT_DOMAINS =
            Set.of(
                    LifeDomain.WEALTH,
                    LifeDomain.CAREER,
                    LifeDomain.RELATIONSHIP
            );


    /*
     * =================================================
     * Annual Provenance
     * =================================================
     */

    private static final String
            ANNUAL_CONTEXT_KEY_PREFIX =
            "ANNUAL:";

    private static final String
            ANNUAL_FUNCTIONAL_KEY_PREFIX =
            "ANNUAL_FUNCTIONAL:";

    private static final String
            ANNUAL_PROVENANCE_FAMILY =
            "ANNUAL_CONTEXT_FAMILY";


    /*
     * =================================================
     * Monthly Provenance
     * =================================================
     */

    private static final String
            MONTHLY_CONTEXT_KEY_PREFIX =
            "MONTHLY:";

    private static final String
            MONTHLY_FUNCTIONAL_KEY_PREFIX =
            "MONTHLY_FUNCTIONAL:";

    private static final String
            MONTHLY_PROVENANCE_FAMILY =
            "MONTHLY_CONTEXT_FAMILY";


    /*
     * =================================================
     * Relevant Domain
     * =================================================
     */

    public Set<LifeDomain> relevantDomains() {

        return LEGAL_RELEVANT_DOMAINS;
    }


    /*
     * =================================================
     * Candidate
     * =================================================
     *
     * LEGAL_RISK Cross 계산에 참여할 수 있는
     * LifeDomainRuleMatch인가.
     *
     * 여기서는:
     *
     * - Relevant Domain
     * - PRESSURE / MIXED
     *
     * 만 확인한다.
     *
     * AUXILIARY-only Match는
     * 공식 LifeDomain 경계 위반이므로
     * 조용히 제외하지 않고 오류로 처리한다.
     */

    public boolean eligibleCandidate(

            LifeDomainRuleMatch match

    ) {

        Objects.requireNonNull(
                match,
                "LEGAL_RISK Cross Candidate 확인에는 LifeDomainRuleMatch가 필요합니다."
        );

        if (
                !LEGAL_RELEVANT_DOMAINS
                        .contains(
                                match.domain()
                        )
        ) {

            return false;
        }

        if (
                match.auxiliaryOnly()
        ) {

            throw new IllegalStateException(
                    "LEGAL_RISK Cross 계산에 AUXILIARY-only Match를 사용할 수 없습니다. "
                            + "domain="
                            + match.domain()
                            + ", rule="
                            + match.ruleCode()
            );
        }

        return match.status()
                == LifeDomainSignalStatus.PRESSURE

                || match.status()
                == LifeDomainSignalStatus.MIXED;
    }


    /*
     * =================================================
     * Cross Established
     * =================================================
     *
     * 공식 LEGAL_RISK Cross 자격 계산.
     *
     * 이 메서드 이외의 곳에서:
     *
     * - Domain 개수
     * - independent provenance 개수
     *
     * 를 다시 계산하지 않는다.
     */

    public boolean established(

            List<LifeDomainRuleMatch> matches

    ) {

        if (
                matches == null
                        || matches.size() < 2
        ) {

            return false;
        }

        Set<LifeDomain> domains =
                new LinkedHashSet<>();

        Set<String> independentProvenanceKeys =
                new LinkedHashSet<>();

        for (
                LifeDomainRuleMatch match
                : matches
        ) {

            LifeDomainRuleMatch normalizedMatch =
                    Objects.requireNonNull(
                            match,
                            "LEGAL_RISK Cross 계산의 LifeDomainRuleMatch는 null일 수 없습니다."
                    );

            if (
                    !eligibleCandidate(
                            normalizedMatch
                    )
            ) {

                continue;
            }

            domains.add(
                    normalizedMatch.domain()
            );

            for (
                    LifeDomainEvidenceReference<?> evidence
                    : normalizedMatch.evidences()
            ) {

                LifeDomainEvidenceReference<?> normalizedEvidence =
                        Objects.requireNonNull(
                                evidence,
                                "LEGAL_RISK Cross 계산의 Evidence는 null일 수 없습니다."
                        );

                /*
                 * AUXILIARY Source는
                 * independent provenance 개수에서 제외한다.
                 */

                if (
                        normalizedEvidence.auxiliary()
                ) {

                    continue;
                }

                independentProvenanceKeys.add(
                        independentProvenanceKey(
                                normalizedEvidence
                        )
                );
            }
        }

        return domains.size() >= 2
                && independentProvenanceKeys.size() >= 2;
    }


    /*
     * =================================================
     * Independent Provenance
     * =================================================
     */

    private String independentProvenanceKey(

            LifeDomainEvidenceReference<?> evidence

    ) {

        Objects.requireNonNull(
                evidence,
                "LEGAL_RISK Independent Provenance 계산에는 Evidence가 필요합니다."
        );

        String sourceKey =
                requireText(
                        evidence.sourceKey(),
                        "LEGAL_RISK non-AUXILIARY Evidence에는 sourceKey가 필요합니다."
                );

        LifeDomainEvidenceSourceType sourceType =
                Objects.requireNonNull(
                        evidence.sourceType(),
                        "LEGAL_RISK Evidence에는 sourceType이 필요합니다."
                );

        if (
                sourceType
                        == LifeDomainEvidenceSourceType
                        .ANNUAL_CONTEXT
        ) {

            return temporalProvenanceFamilyKey(
                    sourceKey,
                    ANNUAL_CONTEXT_KEY_PREFIX,
                    ANNUAL_PROVENANCE_FAMILY
            );
        }

        if (
                sourceType
                        == LifeDomainEvidenceSourceType
                        .ANNUAL_FUNCTIONAL_CONTEXT
        ) {

            return temporalProvenanceFamilyKey(
                    sourceKey,
                    ANNUAL_FUNCTIONAL_KEY_PREFIX,
                    ANNUAL_PROVENANCE_FAMILY
            );
        }

        if (
                sourceType
                        == LifeDomainEvidenceSourceType
                        .MONTHLY_CONTEXT
        ) {

            return temporalProvenanceFamilyKey(
                    sourceKey,
                    MONTHLY_CONTEXT_KEY_PREFIX,
                    MONTHLY_PROVENANCE_FAMILY
            );
        }

        if (
                sourceType
                        == LifeDomainEvidenceSourceType
                        .MONTHLY_FUNCTIONAL_CONTEXT
        ) {

            return temporalProvenanceFamilyKey(
                    sourceKey,
                    MONTHLY_FUNCTIONAL_KEY_PREFIX,
                    MONTHLY_PROVENANCE_FAMILY
            );
        }

        return sourceKey;
    }


    /*
     * =================================================
     * Temporal Provenance Family
     * =================================================
     */

    private String temporalProvenanceFamilyKey(

            String sourceKey,

            String expectedPrefix,

            String provenanceFamily

    ) {

        sourceKey =
                requireText(
                        sourceKey,
                        "LEGAL_RISK provenance sourceKey가 필요합니다."
                );

        expectedPrefix =
                requireText(
                        expectedPrefix,
                        "LEGAL_RISK provenance expectedPrefix가 필요합니다."
                );

        provenanceFamily =
                requireText(
                        provenanceFamily,
                        "LEGAL_RISK provenanceFamily가 필요합니다."
                );

        if (
                sourceKey.startsWith(
                        expectedPrefix
                )
        ) {

            String anchor =
                    sourceKey
                            .substring(
                                    expectedPrefix.length()
                            )
                            .trim();

            if (
                    !anchor.isBlank()
            ) {

                return provenanceFamily
                        + ":"
                        + anchor;
            }
        }

        /*
         * 예상하지 못한 SourceKey 형식은
         * 추측해서 다른 Source와 합치지 않는다.
         */

        return sourceKey;
    }


    /*
     * =================================================
     * Text
     * =================================================
     */

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