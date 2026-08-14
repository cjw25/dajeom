package com.dajeom.model;

import com.dajeom.type.LifeDomain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 18차.
 *
 * 하나의 16차 Timeline Segment에 대해
 * 18차 생활영역 Rule을 실행한 결과.
 *
 * =================================================
 *
 * 중요한 의미:
 *
 * 이 객체의 matchesByDomain에는
 * 원국 Match 전체를 다시 복제하지 않는다.
 *
 * 현재 Segment에서 실제로 사용되는:
 *
 * - DECADE_CONTEXT
 * - ANNUAL_CONTEXT
 * - MONTHLY_CONTEXT
 * - 해당 Segment 운 SHINSAL
 *
 * 중 하나 이상을 사용한 Match만 저장한다.
 *
 * =================================================
 *
 * 따라서 최종 해석 시:
 *
 * LifeDomainAnalysis.natalMatchesOf(domain)
 *
 * +
 *
 * 현재 Segment의 matchesOf(domain)
 *
 * 을 함께 사용하면:
 *
 * 원국 기본 구조
 * +
 * 현재 운에서 추가된 변화
 *
 * 를 분리해서 읽을 수 있다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - SUPPORTIVE/PRESSURE를 하나의 점수로 합산
 * - Segment 전체를 길/흉 하나로 축약
 * - 서로 다른 RuleMatch 삭제
 * - 원국 Match를 매 Segment마다 복제
 */
public record LifeDomainSegmentAnalysis(

        LuckIntegratedSegmentAnalysis segmentAnalysis,

        Map<
                LifeDomain,
                List<LifeDomainRuleMatch>
                > matchesByDomain

) {

    public LifeDomainSegmentAnalysis {

        Objects.requireNonNull(
                segmentAnalysis,
                "LifeDomainSegmentAnalysis에는 LuckIntegratedSegmentAnalysis가 필요합니다."
        );

        matchesByDomain =
                normalizeMatches(
                        matchesByDomain
                );
    }

    /*
     * =================================================
     * Domain 조회
     * =================================================
     */

    public List<LifeDomainRuleMatch> matchesOf(
            LifeDomain domain
    ) {

        if (
                domain == null
        ) {

            return List.of();
        }

        return matchesByDomain
                .getOrDefault(
                        domain,
                        List.of()
                );
    }

    public boolean hasMatches(
            LifeDomain domain
    ) {

        return !matchesOf(
                domain
        )
                .isEmpty();
    }

    /*
     * =================================================
     * 전체 Match
     * =================================================
     *
     * Domain enum 순서를 유지한다.
     *
     * 상태를 합치거나 대표 Match를 선택하지 않는다.
     */

    public List<LifeDomainRuleMatch> allMatches() {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomain domain
                : LifeDomain.values()
        ) {

            result.addAll(
                    matchesOf(
                            domain
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    public int matchCount() {

        return allMatches()
                .size();
    }

    /*
     * =================================================
     * Normalize
     * =================================================
     */

    private static Map<
            LifeDomain,
            List<LifeDomainRuleMatch>
            > normalizeMatches(

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

        /*
         * 모든 Domain Key를 명시적으로 가진다.
         *
         * Rule Match가 없는 Domain도
         * 빈 List로 보존한다.
         */

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
                        "LifeDomainSegmentAnalysis의 Match는 null일 수 없습니다."
                );

                if (
                        match.domain()
                                != domain
                ) {

                    throw new IllegalArgumentException(
                            "Segment Match의 Domain과 Map Key가 일치하지 않습니다. "
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

        /*
         * 정의되지 않은 null Key 등도 허용하지 않는다.
         */

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
                        "LifeDomainSegmentAnalysis의 Domain Key는 null일 수 없습니다."
                );
            }
        }

        return Collections.unmodifiableMap(
                normalized
        );
    }
}