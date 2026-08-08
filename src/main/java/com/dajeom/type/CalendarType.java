package com.dajeom.type;

/**
 * 사용자가 입력한 생년월일의 달력 기준.
 */
public enum CalendarType {

    SOLAR("양력"),
    LUNAR("음력");

    private final String koreanName;

    CalendarType(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}