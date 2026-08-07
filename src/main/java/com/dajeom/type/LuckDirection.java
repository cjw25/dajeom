package com.dajeom.type;

/**
 * 대운이 월주에서 어느 방향으로 진행되는지를 나타낸다.
 */
public enum LuckDirection {

    FORWARD("순행", 1),
    REVERSE("역행", -1);

    private final String koreanName;
    private final int step;

    LuckDirection(
            String koreanName,
            int step
    ) {
        this.koreanName = koreanName;
        this.step = step;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public int getStep() {
        return step;
    }
}