package com.dajeom.type;

/**
 * 대운 진행 방향.
 */
public enum LuckDirection {

    FORWARD(
            1,
            "순행"
    ),

    REVERSE(
            -1,
            "역행"
    );

    private final int step;
    private final String koreanName;

    LuckDirection(
            int step,
            String koreanName
    ) {
        this.step = step;
        this.koreanName = koreanName;
    }

    public int getStep() {
        return step;
    }

    public String getKoreanName() {
        return koreanName;
    }
}