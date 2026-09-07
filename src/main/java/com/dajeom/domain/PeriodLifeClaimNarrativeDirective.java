package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 대운 / 세운 / 월운의 PeriodLifeClaim을
 * AI가 어떤 범위에서 설명해야 하는지 정의하는 서술 지침.
 *
 * =================================================
 *
 * coreMeaning
 *
 * 해당 기간 Claim의 가장 좁은 의미.
 *
 * =================================================
 *
 * requiredDirections
 *
 * AI가 반드시 반영해야 하는 방향.
 *
 * =================================================
 *
 * prohibitedExpansions
 *
 * 해당 Claim만으로 절대 확대하면 안 되는 사건 단정.
 *
 * =================================================
 *
 * 예:
 *
 * RESPONSIBILITY_AND_EVALUATION_PRESSURE_INCREASED
 *
 * 핵심:
 * 책임·평가·역할 요구가 이전보다 중요해지는 흐름.
 *
 * 금지:
 * - 취업했다
 * - 승진했다
 * - 시험에 합격했다
 * - 상사와 싸웠다
 */
public record PeriodLifeClaimNarrativeDirective(

        PeriodLifeClaimCode claimCode,

        String coreMeaning,

        List<String> requiredDirections,

        List<String> prohibitedExpansions

) {

    public PeriodLifeClaimNarrativeDirective {

        Objects.requireNonNull(
                claimCode,
                "PeriodLifeClaimCode는 필수입니다."
        );

        coreMeaning =
                requireText(
                        coreMeaning,
                        "coreMeaning"
                );

        requiredDirections =
                requiredDirections == null
                        ? List.of()
                        : List.copyOf(requiredDirections);

        prohibitedExpansions =
                prohibitedExpansions == null
                        ? List.of()
                        : List.copyOf(prohibitedExpansions);

        for (
                String direction
                : requiredDirections
        ) {

            requireText(
                    direction,
                    "requiredDirections"
            );
        }

        for (
                String prohibition
                : prohibitedExpansions
        ) {

            requireText(
                    prohibition,
                    "prohibitedExpansions"
            );
        }
    }

    private static String requireText(

            String value,

            String name

    ) {

        if (
                value == null
                        ||
                        value.isBlank()
        ) {

            throw new IllegalArgumentException(
                    name + "은 비어 있을 수 없습니다."
            );
        }

        return value.trim();
    }
}