package com.dajeom.domain;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 최종 7개 장문 해석용 AI Prompt 묶음.
 *
 * =================================================
 *
 * 항상 7개 카테고리를 모두 가진다.
 *
 * 다만 특정 카테고리에 확정된 Claim이 없다면
 * generationAllowed=false다.
 *
 * =================================================
 *
 * 이것은 중요한 안전장치다.
 *
 * Claim이 없는 카테고리에서 AI에게
 * "알아서 2,000자 써줘"
 * 라고 하지 않는다.
 */
public record LifeInterpretationPromptBundle(

        Map<
                LifeInterpretationCategory,
                LifeInterpretationPrompt
                > prompts

) {

    public LifeInterpretationPromptBundle {

        Objects.requireNonNull(
                prompts,
                "카테고리별 Prompt Map은 필수입니다."
        );

        EnumMap<
                LifeInterpretationCategory,
                LifeInterpretationPrompt
                > normalized =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            LifeInterpretationPrompt prompt =
                    prompts.get(category);

            if (prompt == null) {

                throw new IllegalArgumentException(
                        "7개 해석 카테고리의 Prompt가 모두 필요합니다. 누락: "
                                + category
                );
            }

            if (
                    prompt.category()
                            != category
            ) {

                throw new IllegalArgumentException(
                        "Prompt Map의 category와 Prompt 내부 category가 일치하지 않습니다."
                );
            }

            normalized.put(
                    category,
                    prompt
            );
        }

        prompts =
                Map.copyOf(
                        normalized
                );
    }

    public LifeInterpretationPrompt promptFor(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "해석 카테고리는 필수입니다."
        );

        return prompts.get(category);
    }

    /**
     * 사용자 화면 순서:
     *
     * 1 성향·기질
     * 2 지나온 흐름
     * 3 가족·인간관계
     * 4 연애·결혼
     * 5 재물
     * 6 일·성취
     * 7 건강·생활
     */
    public List<LifeInterpretationPrompt> orderedPrompts() {

        List<LifeInterpretationPrompt> result =
                new ArrayList<>();

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            result.add(
                    promptFor(category)
            );
        }

        return List.copyOf(result);
    }

    public List<LifeInterpretationCategory>
    categoriesWithoutClaims() {

        return orderedPrompts()
                .stream()
                .filter(
                        prompt ->
                                !prompt.generationAllowed()
                )
                .map(
                        LifeInterpretationPrompt
                                ::category
                )
                .toList();
    }

    public boolean allCategoriesGenerationReady() {

        return categoriesWithoutClaims()
                .isEmpty();
    }
}