package com.dajeom.dto;

import com.dajeom.domain.YinYangCount;

import java.util.Objects;

public record YinYangCountResponse(
        int yang,
        int yin,
        int total,
        String balance
) {

    public static YinYangCountResponse from(
            YinYangCount count
    ) {

        Objects.requireNonNull(
                count,
                "음양 개수 결과가 필요합니다."
        );

        String balance;

        if (count.isBalanced()) {

            balance = "BALANCED";

        } else if (count.isYangDominant()) {

            balance = "YANG_DOMINANT";

        } else {

            balance = "YIN_DOMINANT";
        }

        return new YinYangCountResponse(
                count.yang(),
                count.yin(),
                count.total(),
                balance
        );
    }
}