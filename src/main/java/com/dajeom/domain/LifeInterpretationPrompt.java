package com.dajeom.domain;

import java.util.Objects;

/**
 * 하나의 최종 해석 카테고리를 생성하기 위한
 * AI Prompt.
 *
 * =================================================
 *
 * systemPrompt
 *
 * 전체 생성 규칙.
 *
 * =================================================
 *
 * userPrompt
 *
 * 현재 카테고리와
 * Java가 확정한 해석 소재.
 *
 * =================================================
 *
 * generationAllowed
 *
 * 현재 카테고리에 Java가 확정한 LifeClaim이
 * 최소 하나 존재하는가.
 *
 * false라면 AI를 호출해서는 안 된다.
 *
 * 근거 없이 약 2,000자를 채우기 위해
 * AI가 새로운 사주 해석을 만들어내는 것을 막는다.
 */
public record LifeInterpretationPrompt(

        LifeInterpretationCategory category,

        String systemPrompt,

        String userPrompt,

        boolean generationAllowed

) {

    public LifeInterpretationPrompt {

        Objects.requireNonNull(
                category,
                "해석 카테고리는 필수입니다."
        );

        systemPrompt =
                requireText(
                        systemPrompt,
                        "systemPrompt"
                );

        userPrompt =
                requireText(
                        userPrompt,
                        "userPrompt"
                );
    }

    public String koreanLabel() {

        return category.koreanLabel();
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