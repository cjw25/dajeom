package com.dajeom.type;

/**
 * 사주 원국에서 천간 또는 지지가 위치하는 자리를 나타낸다.
 */
public enum PillarPosition {

    YEAR_STEM("년간"),
    YEAR_BRANCH("년지"),

    MONTH_STEM("월간"),
    MONTH_BRANCH("월지"),

    DAY_STEM("일간"),
    DAY_BRANCH("일지"),

    HOUR_STEM("시간"),
    HOUR_BRANCH("시지");

    private final String koreanName;

    PillarPosition(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}