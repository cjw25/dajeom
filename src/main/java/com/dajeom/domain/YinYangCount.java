package com.dajeom.domain;

/**
 * 원국에 드러난 천간 + 지지의 음양 개수.
 *
 * 지장간은 포함하지 않는다.
 */
public record YinYangCount(
        int yang,
        int yin
) {

    public YinYangCount {

        if (yang < 0 || yin < 0) {

            throw new IllegalArgumentException(
                    "음양 개수는 음수가 될 수 없습니다."
            );
        }
    }

    public int get(
            YinYang yinYang
    ) {

        if (yinYang == null) {

            throw new IllegalArgumentException(
                    "음양 값이 필요합니다."
            );
        }

        return switch (yinYang) {

            case YANG -> yang;
            case YIN -> yin;
        };
    }

    public int total() {

        return yang + yin;
    }

    public boolean isYangDominant() {

        return yang > yin;
    }

    public boolean isYinDominant() {

        return yin > yang;
    }

    public boolean isBalanced() {

        return yang == yin;
    }
}