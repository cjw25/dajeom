package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 7개 최종 해석 카테고리별 AI 서술 규칙.
 */
public record LifeCategoryNarrativeDirective(

        LifeInterpretationCategory category,

        String focus,

        List<String> requiredDirections,

        List<String> prohibitedExpansions

) {

    public LifeCategoryNarrativeDirective {

        Objects.requireNonNull(
                category,
                "해석 카테고리는 필수입니다."
        );

        if (
                focus == null
                        ||
                        focus.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "category focus는 비어 있을 수 없습니다."
            );
        }

        focus =
                focus.trim();

        requiredDirections =
                requiredDirections == null
                        ? List.of()
                        : List.copyOf(requiredDirections);

        prohibitedExpansions =
                prohibitedExpansions == null
                        ? List.of()
                        : List.copyOf(prohibitedExpansions);
    }
}