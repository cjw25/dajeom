package com.dajeom.dto;

import java.util.Objects;

/**
 * 사용자에게 공개되는 사주 기둥 하나.
 *
 * 예:
 *
 * 癸酉
 *
 * stem = "癸"
 * branch = "酉"
 * stemElement = "수"
 * branchElement = "금"
 *
 * =================================================
 *
 * 공개하지 않는 정보:
 *
 * - 십성
 * - 지장간
 * - 통근
 * - 투간
 * - 납음
 * - 신살
 * - 12운성
 * - 합충형파해
 */
public record PublicPillarDto(

        String stem,

        String branch,

        String stemElement,

        String branchElement

) {

    public PublicPillarDto {

        stem =
                requireText(
                        stem,
                        "천간"
                );

        branch =
                requireText(
                        branch,
                        "지지"
                );

        stemElement =
                requireElement(
                        stemElement,
                        "천간 오행"
                );

        branchElement =
                requireElement(
                        branchElement,
                        "지지 오행"
                );
    }

    /**
     * 천간 + 지지.
     *
     * 예:
     *
     * 癸 + 酉
     * →
     * 癸酉
     */
    public String pillarText() {

        return stem + branch;
    }

    private static String requireElement(

            String value,

            String name

    ) {

        String element =
                requireText(
                        value,
                        name
                );

        if (
                !element.equals("목")
                        &&
                        !element.equals("화")
                        &&
                        !element.equals("토")
                        &&
                        !element.equals("금")
                        &&
                        !element.equals("수")
        ) {

            throw new IllegalArgumentException(
                    name
                            + "은 목/화/토/금/수 중 하나여야 합니다: "
                            + element
            );
        }

        return element;
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