package com.dajeom.model;

import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalType;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 정밀화 17차-1.
 *
 * 하나의 ShinsalRuleContext에 대해
 * 등록된 모든 Rule을 실행한 raw 결과.
 *
 * =================================================
 *
 * 아직:
 *
 * 원국 신살 최종 Aggregate
 * 운 Segment별 신살 Aggregate
 *
 * 로 분리하기 전 단계다.
 */
public record ShinsalRuleEvaluation(

        ShinsalRuleContext context,

        List<ShinsalRuleMatch> matches

) {

    public ShinsalRuleEvaluation {

        Objects.requireNonNull(
                context,
                "신살 평가에는 ShinsalRuleContext가 필요합니다."
        );

        matches =
                matches == null
                        ? List.of()
                        : List.copyOf(
                        matches
                );

        for (
                ShinsalRuleMatch match
                : matches
        ) {

            Objects.requireNonNull(
                    match,
                    "ShinsalRuleMatch는 null일 수 없습니다."
            );
        }

        /*
         * 완전히 동일한 Match 중복 금지.
         */
        for (
                int first = 0;
                first < matches.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < matches.size();
                    second++
            ) {

                ShinsalRuleMatch a =
                        matches.get(first);

                ShinsalRuleMatch b =
                        matches.get(second);

                if (
                        a.type() == b.type()

                                && a.ruleCode()
                                .equals(
                                        b.ruleCode()
                                )

                                && a.basis()
                                == b.basis()

                                && a.referencePoints()
                                .equals(
                                        b.referencePoints()
                                )

                                && a.matchedPoints()
                                .equals(
                                        b.matchedPoints()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "동일 신살 Match가 중복되었습니다: "
                                    + a.ruleCode()
                    );
                }
            }
        }
    }

    public boolean hasMatches() {

        return !matches
                .isEmpty();
    }

    public boolean has(
            ShinsalType type
    ) {

        if (
                type == null
        ) {

            return false;
        }

        return matches
                .stream()
                .anyMatch(
                        match ->
                                match.type()
                                        == type
                );
    }

    public List<ShinsalRuleMatch>
    matchesOf(
            ShinsalType type
    ) {

        if (
                type == null
        ) {

            return List.of();
        }

        return matches
                .stream()
                .filter(
                        match ->
                                match.type()
                                        == type
                )
                .toList();
    }

    public List<ShinsalRuleMatch>
    matchesOf(
            ShinsalLayer layer
    ) {

        if (
                layer == null
        ) {

            return List.of();
        }

        return matches
                .stream()
                .filter(
                        match ->
                                match
                                        .matchedPoints()
                                        .stream()
                                        .anyMatch(
                                                point ->
                                                        point.layer()
                                                                == layer
                                        )
                )
                .toList();
    }

    public Set<ShinsalType>
    detectedTypes() {

        return matches
                .stream()
                .map(
                        ShinsalRuleMatch::type
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    public boolean hasNatalMatches() {

        return matches
                .stream()
                .anyMatch(
                        ShinsalRuleMatch
                                ::natalMatch
                );
    }

    public boolean hasLuckMatches() {

        return matches
                .stream()
                .anyMatch(
                        ShinsalRuleMatch
                                ::luckMatch
                );
    }
}