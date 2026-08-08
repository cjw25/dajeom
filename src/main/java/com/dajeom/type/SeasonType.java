package com.dajeom.type;

/**
 * 월령을 기준으로 하는 계절 구분.
 *
 * 봄:
 * 寅 卯 辰
 *
 * 여름:
 * 巳 午 未
 *
 * 가을:
 * 申 酉 戌
 *
 * 겨울:
 * 亥 子 丑
 */
public enum SeasonType {

    SPRING(
            "봄"
    ),

    SUMMER(
            "여름"
    ),

    AUTUMN(
            "가을"
    ),

    WINTER(
            "겨울"
    );

    private final String koreanName;

    SeasonType(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}