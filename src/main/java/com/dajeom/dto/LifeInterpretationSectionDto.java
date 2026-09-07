package com.dajeom.dto;

import java.util.Objects;

/**
 * 사용자에게 공개되는 장문풀이 한 영역.
 *
 * =================================================
 *
 * 공개 필드:
 *
 * order
 * title
 * content
 *
 * =================================================
 *
 * 내부적으로 사용한:
 *
 * - LifeClaimCode
 * - LifeClaimBasisCode
 * - NarrativeDirective
 * - 명리 계산 근거
 *
 * 는 사용자 응답에 포함하지 않는다.
 */
public record LifeInterpretationSectionDto(

        int order,

        String title,

        String content

) {

    public LifeInterpretationSectionDto {

        if (
                order < 1
                        ||
                        order > 7
        ) {

            throw new IllegalArgumentException(
                    "풀이 순서는 1~7 사이여야 합니다."
            );
        }

        title =
                requireText(
                        title,
                        "풀이 제목"
                );

        content =
                requireText(
                        content,
                        "풀이 내용"
                );

        String expectedTitle =
                expectedTitle(
                        order
                );

        if (
                !expectedTitle.equals(
                        title
                )
        ) {

            throw new IllegalArgumentException(
                    "풀이 순서 "
                            + order
                            + "의 제목은 '"
                            + expectedTitle
                            + "'이어야 합니다. 현재 제목='"
                            + title
                            + "'"
            );
        }
    }

    private static String expectedTitle(
            int order
    ) {

        return switch (order) {

            case 1 ->
                    "성향·기질";

            case 2 ->
                    "지나온 흐름";

            case 3 ->
                    "가족·인간관계";

            case 4 ->
                    "연애·결혼";

            case 5 ->
                    "재물";

            case 6 ->
                    "일·성취";

            case 7 ->
                    "건강·생활";

            default ->
                    throw new IllegalArgumentException(
                            "지원하지 않는 풀이 순서입니다: "
                                    + order
                    );
        };
    }

    private static String requireText(

            String value,

            String name

    ) {

        Objects.requireNonNull(
                value,
                name + "은 필수입니다."
        );

        String normalized =
                value.trim();

        if (
                normalized.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    name + "은 비어 있을 수 없습니다."
            );
        }

        return normalized;
    }
}