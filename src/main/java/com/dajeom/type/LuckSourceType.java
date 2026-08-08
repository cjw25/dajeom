package com.dajeom.type;

/**
 * 운 상호작용에서 사용되는
 * 기둥의 출처.
 */
public enum LuckSourceType {

    NATAL(
            "원국"
    ),

    DECADE_LUCK(
            "대운"
    ),

    ANNUAL_LUCK(
            "세운"
    );

    private final String koreanName;

    LuckSourceType(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}