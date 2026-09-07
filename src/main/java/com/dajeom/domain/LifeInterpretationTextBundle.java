package com.dajeom.domain;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * AI가 최종 생성한 7개 장문풀이 본문 묶음.
 *
 * =================================================
 *
 * 이 객체에는 최종 사용자에게 보여줄
 * "본문 텍스트"만 보관한다.
 *
 * 포함:
 *
 * - 성향·기질 본문
 * - 지나온 흐름 본문
 * - 가족·인간관계 본문
 * - 연애·결혼 본문
 * - 재물 본문
 * - 일·성취 본문
 * - 건강·생활 본문
 *
 * =================================================
 *
 * 포함하지 않는 것:
 *
 * - system prompt
 * - user prompt
 * - LifeClaim
 * - LifeClaimBasis
 * - NarrativeDirective
 * - 대운/세운 계산값
 * - 명리 내부 판정값
 *
 * =================================================
 *
 * 최종 API Response를 만들기 위해서는
 * 7개 카테고리 모두 본문이 존재해야 한다.
 */
public record LifeInterpretationTextBundle(

        Map<
                LifeInterpretationCategory,
                String
                > texts

) {

    public LifeInterpretationTextBundle {

        Objects.requireNonNull(
                texts,
                "해석 본문 Map은 필수입니다."
        );

        EnumMap<
                LifeInterpretationCategory,
                String
                > normalized =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                Map.Entry<
                        LifeInterpretationCategory,
                        String
                        > entry
                : texts.entrySet()
        ) {

            LifeInterpretationCategory category =
                    Objects.requireNonNull(
                            entry.getKey(),
                            "해석 카테고리는 null일 수 없습니다."
                    );

            String text =
                    requireText(
                            entry.getValue(),
                            category.name()
                    );

            normalized.put(
                    category,
                    text
            );
        }

        /*
         * =================================================
         * 최종 응답에서는 반드시 7개 모두 필요
         * =================================================
         */

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            if (
                    !normalized.containsKey(
                            category
                    )
            ) {

                throw new IllegalArgumentException(
                        "누락된 최종 해석 본문이 있습니다: "
                                + category
                );
            }
        }

        if (
                normalized.size()
                        != LifeInterpretationCategory.values().length
        ) {

            throw new IllegalArgumentException(
                    "지원하지 않는 해석 카테고리가 포함되어 있습니다."
            );
        }

        texts =
                Map.copyOf(
                        normalized
                );
    }

    /**
     * 특정 카테고리의 최종 본문.
     */
    public String textFor(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "LifeInterpretationCategory는 필수입니다."
        );

        String result =
                texts.get(
                        category
                );

        if (
                result == null
        ) {

            throw new IllegalStateException(
                    "최종 해석 본문이 존재하지 않습니다: "
                            + category
            );
        }

        return result;
    }

    /**
     * 7개 본문이 모두 존재하는지 확인.
     *
     * 생성자에서 이미 보장되므로
     * 정상 생성된 객체는 항상 true다.
     */
    public boolean complete() {

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            String text =
                    texts.get(
                            category
                    );

            if (
                    text == null
                            ||
                            text.isBlank()
            ) {

                return false;
            }
        }

        return true;
    }

    private static String requireText(

            String value,

            String name

    ) {

        Objects.requireNonNull(
                value,
                name + " 해석 본문은 필수입니다."
        );

        String normalized =
                value.trim();

        if (
                normalized.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    name
                            + " 해석 본문은 비어 있을 수 없습니다."
            );
        }

        return normalized;
    }
}