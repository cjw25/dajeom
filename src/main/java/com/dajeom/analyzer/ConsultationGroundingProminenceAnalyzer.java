package com.dajeom.analyzer;

import com.dajeom.model.ConsultationGroundingEvidence;
import com.dajeom.model.ConsultationGroundingProminence;
import com.dajeom.model.ConsultationGroundingSource;
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
 * 1. 비보조 Source Type 다양성
 * 2. 현재 Rule이 직접 사용한 독립 SourceKey 수
 * 3. 같은 방향 Rule 전체의 독립 SourceKey 수
 * 4. 같은 방향이 반복된 서로 다른 Rule 수
 * 5. AUXILIARY Source 의존이 적은 쪽
 * 6. 원래 Evidence 순서
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
 */
@Component
public class ConsultationGroundingProminenceAnalyzer {

    public List<ConsultationGroundingEvidence> analyze(

            List<ConsultationGroundingEvidence> source

    ) {

        if (
                source == null
                        ||
                        source.isEmpty()
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

        Set<String> sameDirectionRules =
                new LinkedHashSet<>();

        Set<String> sameDirectionSourceKeys =
                new LinkedHashSet<>();

        for (
                ConsultationGroundingEvidence candidate
                : evidences
        ) {

            if (
                    candidate.domain()
                            !=
                            evidence.domain()
            ) {

                continue;
            }

            if (
                    candidate.status()
                            !=
                            evidence.status()
            ) {

                continue;
            }

            sameDirectionRules.add(
                    candidate.ruleCode()
            );

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
                sameDirectionRules.size(),
                sameDirectionSourceKeys.size(),
                auxiliaryCount
        );
    }

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
                    source.sourceKey()
            );
        }

        return result;
    }

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
                    String.valueOf(
                            source.sourceType()
                    )
            );
        }

        return result;
    }

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