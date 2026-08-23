package com.dajeom.analyzer;

import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingProminence;
import com.dajeom.model.ConsultationGroundingSource;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 22차.
 *
 * 하나의 Grounding Slice 안에 존재하는
 * ConsultationGroundingEvidence들의
 * 서술 우선순위를 계산한다.
 *
 * =================================================
 * Slice
 * =================================================
 *
 * 각각 독립적으로 계산한다.
 *
 * - Natal Evidence 목록
 * - Current Timeline Scenario 한 개의 Evidence 목록
 * - Timeline Year 한 개의 Evidence 목록
 *
 * 서로 다른 연도나 Scenario를 섞어서
 * 우선순위를 계산하지 않는다.
 *
 * =================================================
 * 우선순위 판단 기준
 * =================================================
 *
 * 종합점수를 만들지 않는다.
 *
 * 다음 기준을 순서대로 비교한다.
 *
 * 1.
 * 비보조 독립 Provenance Family 다양성
 *
 * 2.
 * 현재 Rule이 직접 사용한
 * 독립 Provenance Anchor 수
 *
 * 3.
 * 같은 방향 Rule 전체의
 * 독립 Provenance Anchor 수
 *
 * 4.
 * 같은 방향이 반복된
 * 독립 Provenance Rule Group 수
 *
 * 5.
 * AUXILIARY Source 의존이 적은 쪽
 *
 * 6.
 * 원래 Evidence 순서
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * SUPPORTIVE가 PRESSURE보다 높은 우선순위를
 * 자동으로 갖지 않는다.
 *
 * Status 자체는 우열 기준이 아니다.
 *
 * 같은 방향 반복 역시
 * "확률이 높다"는 뜻이 아니다.
 *
 * 단지 상담문에서
 * 더 먼저 깊게 설명할 가치가 있는
 * deterministic provenance를 나타낸다.
 *
 * =================================================
 * 파생 Evidence 독립성 - Annual
 * =================================================
 *
 * ANNUAL_CONTEXT
 *
 * 와
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * 는 LifeDomain Rule 실행에서는
 * 서로 다른 typed Evidence로 모두 보존한다.
 *
 * 하지만 둘이 동시에 존재한다는 이유만으로
 * 독립된 세운 근거 두 개로 prominence를
 * 올리면 안 된다.
 *
 * Annual Functional은 동일 AnnualLuckAnalysis /
 * Annual Context에서 파생된 후속 분석이기 때문이다.
 *
 * 따라서 Prominence의 독립성 계산에서만:
 *
 * ANNUAL_CONTEXT
 *
 * +
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * 를 같은 ANNUAL provenance family로 정규화한다.
 *
 * =================================================
 * 파생 Evidence 독립성 - Monthly
 * =================================================
 *
 * MONTHLY_CONTEXT
 *
 * 와
 *
 * MONTHLY_FUNCTIONAL_CONTEXT
 *
 * 역시 LifeDomain Rule 실행에서는
 * 서로 다른 typed Evidence로 모두 보존한다.
 *
 * Monthly Functional은:
 *
 * - 기존 MonthlyLuckAnalysis
 * - 기존 MonthlyLuckContextAnalysis
 * - 고정된 Annual Functional Baseline
 *
 * 을 기반으로 만들어진 derived temporal analysis다.
 *
 * 따라서 같은 명리연도 / 같은 월에서:
 *
 * MONTHLY_CONTEXT
 *
 * +
 *
 * MONTHLY_FUNCTIONAL_CONTEXT
 *
 * 가 함께 존재한다고 해서
 * 독립 월운 근거 두 개로 prominence를
 * 올리면 안 된다.
 *
 * Prominence의 독립성 계산에서만:
 *
 * MONTHLY_CONTEXT
 *
 * +
 *
 * MONTHLY_FUNCTIONAL_CONTEXT
 *
 * 를 같은 MONTHLY provenance family로 정규화한다.
 *
 * =================================================
 *
 * 이 정규화가 하지 않는 것:
 *
 * - Match 삭제
 * - Rule 삭제
 * - Status 변경
 * - SUPPORTIVE/PRESSURE 합산
 * - Annual Functional 정보 삭제
 * - Annual Context 정보 삭제
 * - Monthly Functional 정보 삭제
 * - Monthly Context 정보 삭제
 * - 실제 Grounding Source 목록 변경
 * - Annual과 Monthly를 하나의 Family로 합치기
 *
 * 오직 narrative prominence 계산에서만
 * 파생 근거의 독립성 중복을 제거한다.
 */
@Component
public class ConsultationGroundingProminenceAnalyzer {

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
     * Analyze
     * =================================================
     */

    public List<ConsultationGroundingEvidence> analyze(

            List<ConsultationGroundingEvidence> source

    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        List<ConsultationGroundingEvidence> evidences =
                normalize(
                        source
                );

        List<ProminenceMetrics> metrics =
                new ArrayList<>(
                        evidences.size()
                );

        for (
                int index = 0;
                index < evidences.size();
                index++
        ) {

            ConsultationGroundingEvidence evidence =
                    evidences.get(
                            index
                    );

            metrics.add(
                    createMetrics(
                            evidences,
                            evidence,
                            index
                    )
            );
        }

        /*
         * =================================================
         * Priority
         * =================================================
         */

        List<ProminenceMetrics> ordered =
                new ArrayList<>(
                        metrics
                );

        ordered.sort(
                Comparator
                        .comparingInt(
                                ProminenceMetrics
                                        ::nonAuxiliarySourceTypeCount
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                ProminenceMetrics
                                                        ::nonAuxiliarySourceCount
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                ProminenceMetrics
                                                        ::sameDirectionSourceCount
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                ProminenceMetrics
                                                        ::sameDirectionRuleCount
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                ProminenceMetrics
                                        ::auxiliarySourceCount
                        )

                        .thenComparingInt(
                                ProminenceMetrics
                                        ::originalIndex
                        )
        );

        int[] priorities =
                new int[
                        evidences.size()
                        ];

        for (
                int order = 0;
                order < ordered.size();
                order++
        ) {

            ProminenceMetrics item =
                    ordered.get(
                            order
                    );

            priorities[
                    item.originalIndex()
                    ] =
                    order + 1;
        }

        /*
         * =================================================
         * Result
         * =================================================
         */

        List<ConsultationGroundingEvidence> result =
                new ArrayList<>(
                        evidences.size()
                );

        for (
                int index = 0;
                index < evidences.size();
                index++
        ) {

            ConsultationGroundingEvidence evidence =
                    evidences.get(
                            index
                    );

            ProminenceMetrics item =
                    metrics.get(
                            index
                    );

            ConsultationGroundingProminence prominence =
                    new ConsultationGroundingProminence(
                            priorities[index],
                            item.nonAuxiliarySourceCount(),
                            item.nonAuxiliarySourceTypeCount(),
                            item.sameDirectionRuleCount(),
                            item.sameDirectionSourceCount(),
                            item.auxiliarySourceCount()
                    );

            /*
             * 실제 Grounding Source는 변경하지 않는다.
             *
             * ANNUAL_CONTEXT
             * ANNUAL_FUNCTIONAL_CONTEXT
             *
             * MONTHLY_CONTEXT
             * MONTHLY_FUNCTIONAL_CONTEXT
             *
             * 모두 원래 Source 목록 그대로
             * 외부 Grounding에 남는다.
             *
             * 위에서 계산한 prominence 숫자만
             * 독립 Provenance 기준으로 정규화된다.
             */

            result.add(
                    new ConsultationGroundingEvidence(
                            evidence.domain(),
                            evidence.ruleCode(),
                            evidence.status(),
                            evidence.summary(),
                            evidence.sources(),
                            evidence.reasons(),
                            prominence
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Metrics
     * =================================================
     */

    private ProminenceMetrics createMetrics(

            List<ConsultationGroundingEvidence> evidences,

            ConsultationGroundingEvidence evidence,

            int originalIndex

    ) {

        Set<String> currentSourceKeys =
                nonAuxiliarySourceKeys(
                        evidence
                );

        Set<String> currentSourceTypes =
                nonAuxiliarySourceTypes(
                        evidence
                );

        int auxiliaryCount =
                auxiliarySourceCount(
                        evidence
                );

        /*
         * =================================================
         * 같은 Domain + 같은 Status
         * =================================================
         */

        Set<String> sameDirectionIndependentRuleGroups =
                new LinkedHashSet<>();

        Set<String> sameDirectionSourceKeys =
                new LinkedHashSet<>();

        for (
                ConsultationGroundingEvidence candidate
                : evidences
        ) {

            if (
                    candidate.domain()
                            != evidence.domain()
            ) {

                continue;
            }

            if (
                    candidate.status()
                            != evidence.status()
            ) {

                continue;
            }

            /*
             * =================================================
             * 독립 Provenance Rule Group
             * =================================================
             *
             * 단순 ruleCode 수를 세지 않는다.
             *
             * 같은 독립 Source Anchor 집합에서
             * 파생된 여러 Rule은
             *
             * "독립 근거가 반복됐다"
             *
             * 고 계산하지 않는다.
             */

            sameDirectionIndependentRuleGroups.add(
                    independentRuleGroupKey(
                            candidate
                    )
            );

            /*
             * =================================================
             * 독립 Source Anchor
             * =================================================
             */

            sameDirectionSourceKeys.addAll(
                    nonAuxiliarySourceKeys(
                            candidate
                    )
            );
        }

        return new ProminenceMetrics(
                originalIndex,
                currentSourceKeys.size(),
                currentSourceTypes.size(),
                sameDirectionIndependentRuleGroups.size(),
                sameDirectionSourceKeys.size(),
                auxiliaryCount
        );
    }


    /*
     * =================================================
     * Non-Auxiliary Source Keys
     * =================================================
     */

    private Set<String> nonAuxiliarySourceKeys(

            ConsultationGroundingEvidence evidence

    ) {

        Set<String> result =
                new LinkedHashSet<>();

        for (
                ConsultationGroundingSource source
                : evidence.sources()
        ) {

            if (
                    source.auxiliary()
            ) {

                continue;
            }

            result.add(
                    independentSourceKey(
                            source
                    )
            );
        }

        return result;
    }


    /*
     * =================================================
     * Non-Auxiliary Source Types
     * =================================================
     */

    private Set<String> nonAuxiliarySourceTypes(

            ConsultationGroundingEvidence evidence

    ) {

        Set<String> result =
                new LinkedHashSet<>();

        for (
                ConsultationGroundingSource source
                : evidence.sources()
        ) {

            if (
                    source.auxiliary()
            ) {

                continue;
            }

            result.add(
                    independentSourceType(
                            source
                    )
            );
        }

        return result;
    }


    /*
     * =================================================
     * Independent Source Type
     * =================================================
     */

    private String independentSourceType(

            ConsultationGroundingSource source

    ) {

        Objects.requireNonNull(
                source,
                "Independent Source Type 계산에는 ConsultationGroundingSource가 필요합니다."
        );

        LifeDomainEvidenceSourceType sourceType =
                source
                        .sourceType();

        /*
         * =================================================
         * Annual Functional은
         * Annual Context의 derived provenance.
         * =================================================
         */

        if (
                sourceType
                        == LifeDomainEvidenceSourceType
                        .ANNUAL_CONTEXT

                        || sourceType
                        == LifeDomainEvidenceSourceType
                        .ANNUAL_FUNCTIONAL_CONTEXT
        ) {

            return ANNUAL_PROVENANCE_FAMILY;
        }

        /*
         * =================================================
         * Monthly Functional은
         * Monthly Context의 derived provenance.
         * =================================================
         */

        if (
                sourceType
                        == LifeDomainEvidenceSourceType
                        .MONTHLY_CONTEXT

                        || sourceType
                        == LifeDomainEvidenceSourceType
                        .MONTHLY_FUNCTIONAL_CONTEXT
        ) {

            return MONTHLY_PROVENANCE_FAMILY;
        }

        return String.valueOf(
                sourceType
        );
    }


    /*
     * =================================================
     * Independent Source Key
     * =================================================
     *
     * Annual:
     *
     * ANNUAL:2026
     *
     * ANNUAL_FUNCTIONAL:2026
     *
     * ->
     *
     * ANNUAL_CONTEXT_FAMILY:2026
     *
     * =================================================
     *
     * Monthly:
     *
     * MONTHLY:2026:7
     *
     * MONTHLY_FUNCTIONAL:2026:7
     *
     * ->
     *
     * MONTHLY_CONTEXT_FAMILY:2026:7
     *
     * =================================================
     *
     * 실제 SourceKey는 변경하지 않는다.
     */

    private String independentSourceKey(

            ConsultationGroundingSource source

    ) {

        Objects.requireNonNull(
                source,
                "Independent Source Key 계산에는 ConsultationGroundingSource가 필요합니다."
        );

        String sourceKey =
                Objects.requireNonNull(
                        source.sourceKey(),
                        "Grounding Source Key는 null일 수 없습니다."
                );

        LifeDomainEvidenceSourceType sourceType =
                source
                        .sourceType();

        /*
         * =================================================
         * Annual
         * =================================================
         */

        if (
                sourceType
                        == LifeDomainEvidenceSourceType
                        .ANNUAL_CONTEXT
        ) {

            return provenanceIndependentKey(
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

            return provenanceIndependentKey(
                    sourceKey,
                    ANNUAL_FUNCTIONAL_KEY_PREFIX,
                    ANNUAL_PROVENANCE_FAMILY
            );
        }

        /*
         * =================================================
         * Monthly
         * =================================================
         */

        if (
                sourceType
                        == LifeDomainEvidenceSourceType
                        .MONTHLY_CONTEXT
        ) {

            return provenanceIndependentKey(
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

            return provenanceIndependentKey(
                    sourceKey,
                    MONTHLY_FUNCTIONAL_KEY_PREFIX,
                    MONTHLY_PROVENANCE_FAMILY
            );
        }

        return sourceKey;
    }


    /*
     * =================================================
     * Derived Temporal Independent Anchor
     * =================================================
     *
     * 정상 예:
     *
     * ANNUAL:2026
     *
     * +
     *
     * ANNUAL_FUNCTIONAL:2026
     *
     * ->
     *
     * ANNUAL_CONTEXT_FAMILY:2026
     *
     * =================================================
     *
     * MONTHLY:2026:7
     *
     * +
     *
     * MONTHLY_FUNCTIONAL:2026:7
     *
     * ->
     *
     * MONTHLY_CONTEXT_FAMILY:2026:7
     *
     * =================================================
     *
     * 예상하지 못한 SourceKey 형식은
     * 억지로 다른 Evidence와 합치지 않는다.
     *
     * 모르는 provenance를
     * "같은 근거"라고 추측하지 않는다.
     */

    private String provenanceIndependentKey(

            String sourceKey,

            String expectedPrefix,

            String provenanceFamily

    ) {

        Objects.requireNonNull(
                sourceKey,
                "Independent Source Key가 필요합니다."
        );

        Objects.requireNonNull(
                expectedPrefix,
                "Independent Source Key Prefix가 필요합니다."
        );

        Objects.requireNonNull(
                provenanceFamily,
                "Independent Provenance Family가 필요합니다."
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
                            );

            if (
                    !anchor.isBlank()
            ) {

                return provenanceFamily
                        + ":"
                        + anchor;
            }
        }

        /*
         * 예상하지 못한 Key 형식.
         *
         * 원본 Key를 그대로 보존해서
         * 다른 provenance와
         * 강제로 합치지 않는다.
         */

        return sourceKey;
    }


    /*
     * =================================================
     * Independent Rule Group
     * =================================================
     *
     * 같은 방향의 Rule 수를
     * 단순 ruleCode 개수로 세지 않는다.
     *
     * 실제 독립 Provenance Anchor 집합을
     * Signature로 사용한다.
     *
     * =================================================
     *
     * Annual 예:
     *
     * WEALTH_ANNUAL_CONTEXT
     *
     * sources:
     *
     * ANNUAL:2026
     *
     * ->
     *
     * ANNUAL_CONTEXT_FAMILY:2026
     *
     * =================================================
     *
     * WEALTH_ANNUAL_FUNCTIONAL
     *
     * sources:
     *
     * ANNUAL_FUNCTIONAL:2026
     *
     * ->
     *
     * ANNUAL_CONTEXT_FAMILY:2026
     *
     * =================================================
     *
     * Monthly 예:
     *
     * WEALTH_MONTHLY_CONTEXT
     *
     * sources:
     *
     * MONTHLY:2026:7
     *
     * ->
     *
     * MONTHLY_CONTEXT_FAMILY:2026:7
     *
     * =================================================
     *
     * WEALTH_MONTHLY_FUNCTIONAL
     *
     * sources:
     *
     * MONTHLY_FUNCTIONAL:2026:7
     *
     * ->
     *
     * MONTHLY_CONTEXT_FAMILY:2026:7
     *
     * =================================================
     *
     * 따라서 같은 Status이고
     * 같은 원본 temporal anchor에서 파생되었다면
     *
     * independent same-direction group = 1.
     *
     * 각각의 Match 자체는 모두 그대로 남는다.
     */

    private String independentRuleGroupKey(

            ConsultationGroundingEvidence evidence

    ) {

        Objects.requireNonNull(
                evidence,
                "Independent Rule Group 계산에는 Grounding Evidence가 필요합니다."
        );

        Set<String> sourceKeys =
                nonAuxiliarySourceKeys(
                        evidence
                );

        /*
         * =================================================
         * 비보조 Source가 없는 경우
         * =================================================
         *
         * AUXILIARY-only는 앞 계층 Policy에서
         * 원칙적으로 차단되지만,
         *
         * 이 Analyzer는 별도의 명리 정책을
         * 새로 만들지 않는다.
         *
         * 방어적으로 ruleCode를 사용한다.
         */

        if (
                sourceKeys.isEmpty()
        ) {

            return "RULE:"
                    + evidence.ruleCode();
        }

        List<String> ordered =
                sourceKeys
                        .stream()
                        .sorted()
                        .toList();

        return String.join(
                "\u001F",
                ordered
        );
    }


    /*
     * =================================================
     * Auxiliary
     * =================================================
     */

    private int auxiliarySourceCount(

            ConsultationGroundingEvidence evidence

    ) {

        int count =
                0;

        for (
                ConsultationGroundingSource source
                : evidence.sources()
        ) {

            if (
                    source.auxiliary()
            ) {

                count++;
            }
        }

        return count;
    }


    /*
     * =================================================
     * Normalize
     * =================================================
     */

    private List<ConsultationGroundingEvidence> normalize(

            List<ConsultationGroundingEvidence> source

    ) {

        List<ConsultationGroundingEvidence> result =
                new ArrayList<>(
                        source.size()
                );

        for (
                ConsultationGroundingEvidence evidence
                : source
        ) {

            result.add(
                    Objects.requireNonNull(
                            evidence,
                            "Grounding Prominence 분석 대상 Evidence는 null일 수 없습니다."
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Metrics
     * =================================================
     */

    private record ProminenceMetrics(

            int originalIndex,

            int nonAuxiliarySourceCount,

            int nonAuxiliarySourceTypeCount,

            int sameDirectionRuleCount,

            int sameDirectionSourceCount,

            int auxiliarySourceCount

    ) {
    }
}