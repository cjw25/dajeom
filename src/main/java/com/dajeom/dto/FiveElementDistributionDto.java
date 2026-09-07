package com.dajeom.dto;

/**
 * 사용자 공개용 오행분포.
 *
 * =================================================
 *
 * 사주팔자 8글자의 오행 개수만 표시한다.
 *
 * 예:
 *
 * 목 1
 * 화 1
 * 토 2
 * 금 3
 * 수 1
 *
 * =================================================
 *
 * 지장간 등을 가중해서 계산한 별도 오행 점수는
 * 이 공개 DTO에 넣지 않는다.
 *
 * 사용자에게 보여주는 것은
 * 사주팔자 8글자의 단순 오행분포다.
 */
public record FiveElementDistributionDto(

        int wood,

        int fire,

        int earth,

        int metal,

        int water

) {

    public FiveElementDistributionDto {

        validateNonNegative(
                wood,
                "목"
        );

        validateNonNegative(
                fire,
                "화"
        );

        validateNonNegative(
                earth,
                "토"
        );

        validateNonNegative(
                metal,
                "금"
        );

        validateNonNegative(
                water,
                "수"
        );

        int total =
                wood
                        + fire
                        + earth
                        + metal
                        + water;

        /*
         * 사용자 공개 오행분포는
         * 사주팔자 8글자 기준이므로
         * 반드시 합계가 8이어야 한다.
         */
        if (
                total != 8
        ) {

            throw new IllegalArgumentException(
                    "공개 오행분포의 합계는 사주팔자 8글자 기준으로 반드시 8이어야 합니다. 현재 합계="
                            + total
            );
        }
    }

    public int total() {

        return wood
                + fire
                + earth
                + metal
                + water;
    }

    private static void validateNonNegative(

            int value,

            String element

    ) {

        if (
                value < 0
        ) {

            throw new IllegalArgumentException(
                    element
                            + " 오행 개수는 음수일 수 없습니다."
            );
        }
    }
}