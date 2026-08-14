package com.dajeom.model;

import com.dajeom.type.LifeDomain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 18차.
 *
 * 생활영역 분석 최종 실행 Aggregate.
 *
 * =================================================
 *
 * 구성:
 *
 * 1.
 * LifeDomainEvidenceResolution
 *
 * 3~17차에서 연결된 공식 Evidence Source.
 *
 * =================================================
 *
 * 2.
 * natalMatchesByDomain
 *
 * 원국 Evidence만 대상으로 실행한
 * 생활영역 기본 구조 Match.
 *
 * =================================================
 *
 * 3.
 * segmentAnalyses
 *
 * 각 16차 Timeline Segment에서
 * 새롭게 관여하는:
 *
 * - 대운
 * - 세운
 * - 월운
 * - 운 신살
 *
 * 기반 Match.
 *
 * =================================================
 *
 * 중요한 해석 구조:
 *
 * 최종적으로 특정 시점을 설명할 때:
 *
 * natalMatchesOf(domain)
 *
 * +
 *
 * segmentAnalysis.matchesOf(domain)
 *
 * 을 함께 사용한다.
 *
 * =================================================
 *
 * 이렇게 분리하는 이유:
 *
 * Segment EvidenceBundle 자체에는:
 *
 * 원국 Evidence
 * +
 * Temporal Evidence
 *
 * 가 모두 들어 있다.
 *
 * 따라서 RuleEngine 전체 결과를
 * Segment마다 그대로 저장하면
 * 원국 Match가 모든 Segment에 반복된다.
 *
 * 이 Aggregate는:
 *
 * 원국 기본 구조
 * vs
 * 현재 운에서 추가되는 변화
 *
 * 를 명시적으로 분리한다.
 *
 * =================================================
 *
 * 이 Aggregate에서도:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * 을 하나의 점수로 축약하지 않는다.
 */
public record LifeDomainAnalysis(

        LifeDomainEvidenceResolution evidenceResolution,

        Map<
                LifeDomain,
                List<LifeDomainRuleMatch>
                > natalMatchesByDomain,

        List<LifeDomainSegmentAnalysis> segmentAnalyses

) {

    public LifeDomainAnalysis {

        Objects.requireNonNull(
                evidenceResolution,
                "LifeDomainAnalysis에는 LifeDomainEvidenceResolution이 필요합니다."
        );

        natalMatchesByDomain =
                normalizeNatalMatches(
                        natalMatchesByDomain
                );

        segmentAnalyses =
                segmentAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        segmentAnalyses
                );

        /*
         * =================================================
         * Timeline Segment 수
         * =================================================
         */

        if (
                segmentAnalyses.size()
                        != evidenceResolution
                        .segmentCount()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainSegmentAnalysis 수와 EvidenceResolution의 Segment 수가 일치하지 않습니다. "
                            + "analysis="
                            + segmentAnalyses.size()
                            + ", resolution="
                            + evidenceResolution.segmentCount()
            );
        }

        /*
         * =================================================
         * Timeline 순서 / Identity
         * =================================================
         *
         * 16차 → 18-2 → 18차 실행 결과까지
         * 동일 Segment 객체 Identity를 유지한다.
         */

        for (
                int index = 0;
                index < segmentAnalyses.size();
                index++
        ) {

            LifeDomainSegmentAnalysis segmentAnalysis =
                    Objects.requireNonNull(
                            segmentAnalyses.get(index),
                            "LifeDomainSegmentAnalysis는 null일 수 없습니다."
                    );

            LuckIntegratedSegmentAnalysis expected =
                    evidenceResolution
                            .segmentEvidenceAt(
                                    index
                            )
                            .segmentAnalysis();

            if (
                    segmentAnalysis.segmentAnalysis()
                            != expected
            ) {

                throw new IllegalArgumentException(
                        "18차 생활영역 Segment 결과가 EvidenceResolution Timeline 순서와 일치하지 않습니다: "
                                + index
                );
            }
        }
    }

    /*
     * =================================================
     * 원국 Match
     * =================================================
     */

    public List<LifeDomainRuleMatch> natalMatchesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return List.of();
        }

        return natalMatchesByDomain
                .getOrDefault(
                        domain,
                        List.of()
                );
    }

    public boolean hasNatalMatches(
            LifeDomain domain
    ) {

        return !natalMatchesOf(
                domain
        )
                .isEmpty();
    }

    public List<LifeDomainRuleMatch> allNatalMatches() {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            result.addAll(
                    natalMatchesOf(
                            domain
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Segment 조회
     * =================================================
     */

    public Optional<LifeDomainSegmentAnalysis>
    segmentAnalysisOf(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    ) {

        if (
                segmentAnalysis == null
        ) {

            return Optional.empty();
        }

        return segmentAnalyses
                .stream()
                .filter(
                        candidate ->
                                candidate
                                        .segmentAnalysis()
                                        == segmentAnalysis
                )
                .findFirst();
    }

    public LifeDomainSegmentAnalysis
    segmentAnalysisAt(
            int index
    ) {

        if (
                index < 0
                        || index >= segmentAnalyses.size()
        ) {

            throw new IndexOutOfBoundsException(
                    "LifeDomainSegmentAnalysis index가 범위를 벗어났습니다: "
                            + index
            );
        }

        return segmentAnalyses
                .get(index);
    }

    public int segmentCount() {

        return segmentAnalyses
                .size();
    }

    /*
     * =================================================
     * 특정 Segment의 유효 해석 재료
     * =================================================
     *
     * 원국
     * +
     * 현재 Segment 변화분
     *
     * 을 단순 연결해서 반환한다.
     *
     * 상태를 합치거나 대표 상태를 계산하지 않는다.
     */

    public List<LifeDomainRuleMatch>
    effectiveMatchesOf(

            LifeDomain domain,

            LuckIntegratedSegmentAnalysis segmentAnalysis

    ) {

        Objects.requireNonNull(
                domain,
                "effectiveMatchesOf에는 LifeDomain이 필요합니다."
        );

        LifeDomainSegmentAnalysis resolved =
                segmentAnalysisOf(
                        segmentAnalysis
                )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "현재 LifeDomainAnalysis에 존재하지 않는 Timeline Segment입니다."
                                        )
                        );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        result.addAll(
                natalMatchesOf(
                        domain
                )
        );

        result.addAll(
                resolved.matchesOf(
                        domain
                )
        );

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 불확실성 전달
     * =================================================
     */

    public boolean completeChart() {

        return evidenceResolution
                .completeChart();
    }

    public boolean uncertaintyExists() {

        return evidenceResolution
                .uncertaintyExists();
    }

    /*
     * =================================================
     * Normalize Natal Matches
     * =================================================
     */

    private static Map<
            LifeDomain,
            List<LifeDomainRuleMatch>
            > normalizeNatalMatches(

            Map<
                    LifeDomain,
                    List<LifeDomainRuleMatch>
                    > source

    ) {

        EnumMap<
                LifeDomain,
                List<LifeDomainRuleMatch>
                > normalized =
                new EnumMap<>(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            List<LifeDomainRuleMatch> matches =
                    source == null
                            ? List.of()
                            : source.get(
                            domain
                    );

            matches =
                    matches == null
                            ? List.of()
                            : List.copyOf(
                            matches
                    );

            for (
                    LifeDomainRuleMatch match
                    : matches
            ) {

                Objects.requireNonNull(
                        match,
                        "LifeDomainAnalysis의 Natal Match는 null일 수 없습니다."
                );

                if (
                        match.domain()
                                != domain
                ) {

                    throw new IllegalArgumentException(
                            "Natal Match의 Domain과 Map Key가 일치하지 않습니다. "
                                    + "key="
                                    + domain
                                    + ", match="
                                    + match.domain()
                                    + ", rule="
                                    + match.ruleCode()
                    );
                }
            }

            normalized.put(
                    domain,
                    matches
            );
        }

        if (
                source != null
        ) {

            for (
                    Map.Entry<
                            LifeDomain,
                            List<LifeDomainRuleMatch>
                            > entry
                    : source.entrySet()
            ) {

                Objects.requireNonNull(
                        entry.getKey(),
                        "LifeDomainAnalysis의 Domain Key는 null일 수 없습니다."
                );
            }
        }

        return Collections.unmodifiableMap(
                normalized
        );
    }
}