package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 하나의 LifeClaim을 AI가 어떻게 설명해야 하는지
 * Java에서 고정하는 서술 지침.
 *
 * =================================================
 *
 * coreMeaning
 *
 * 해당 Claim의 의미 범위.
 *
 * =================================================
 *
 * requiredDirections
 *
 * AI가 반드시 반영해야 하는 설명 방향.
 *
 * =================================================
 *
 * prohibitedExpansions
 *
 * 해당 Claim만으로는 절대 확대하면 안 되는 내용.
 *
 * 예:
 *
 * - 특정 질병
 * - 반드시 이혼
 * - 반드시 부자
 * - 특정 과거 사건
 * - 실제 부모/배우자의 성격 단정
 *
 * =================================================
 *
 * 이 객체 역시 최종 사용자에게 노출하지 않는다.
 */
public record LifeClaimNarrativeDirective(

        LifeClaimCode claimCode,

        String coreMeaning,

        List<String> requiredDirections,

        List<String> prohibitedExpansions

) {

    public LifeClaimNarrativeDirective {

        Objects.requireNonNull(
                claimCode,
                "LifeClaimCode는 필수입니다."
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

        for (String direction : requiredDirections) {

            requireText(
                    direction,
                    "requiredDirections"
            );
        }

        for (String prohibition : prohibitedExpansions) {

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