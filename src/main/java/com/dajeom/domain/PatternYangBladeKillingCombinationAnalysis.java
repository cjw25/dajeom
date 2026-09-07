package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국의 陽刃合殺 전체 분석.
 */
public record PatternYangBladeKillingCombinationAnalysis(

        HeavenlyStem dayMaster,

        List<PatternYangBladeKillingCombinationOccurrence> combinations

) {

    public PatternYangBladeKillingCombinationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        combinations =
                combinations == null
                        ? List.of()
                        : List.copyOf(combinations);

        for (
                PatternYangBladeKillingCombinationOccurrence combination
                : combinations
        ) {

            Objects.requireNonNull(
                    combination,
                    "陽刃合殺 목록에 null이 포함될 수 없습니다."
            );
        }
    }

    public boolean hasAnyCombination() {

        return !combinations.isEmpty();
    }

    public boolean hasActiveCombination() {

        return combinations
                .stream()
                .anyMatch(
                        PatternYangBladeKillingCombinationOccurrence
                                ::active
                );
    }

    public boolean hasReviewRequiredCombination() {

        return combinations
                .stream()
                .anyMatch(
                        PatternYangBladeKillingCombinationOccurrence
                                ::reviewRequired
                );
    }

    public boolean hasActiveCombinationWith(
            PatternTenGodNode killing
    ) {

        Objects.requireNonNull(
                killing,
                "七殺 노드는 필수입니다."
        );

        return combinations
                .stream()
                .filter(
                        PatternYangBladeKillingCombinationOccurrence
                                ::active
                )
                .anyMatch(
                        combination ->
                                combination
                                        .sevenKillings()
                                        .equals(killing)
                );
    }
}