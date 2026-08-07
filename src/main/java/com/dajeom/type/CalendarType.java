package com.dajeom.type;

public enum CalendarType {

    SOLAR("양력"),
    LUNAR("음력");

    private final String koreanName;

    CalendarType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}