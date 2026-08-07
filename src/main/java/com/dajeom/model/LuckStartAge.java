package com.dajeom.model;

/**
 * 대운이 시작되는 나이를 연·월·일로 나타낸다.
 */
public record LuckStartAge(
        int years,
        int months,
        int days,
        double decimalYears
) {

    public LuckStartAge {
        if (years < 0 || months < 0 || days < 0) {
            throw new IllegalArgumentException(
                    "대운 시작 나이는 음수일 수 없습니다."
            );
        }
    }
}