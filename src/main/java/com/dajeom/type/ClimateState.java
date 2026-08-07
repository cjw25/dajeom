package com.dajeom.type;

/**
 * 원국의 계절적 한난조습 상태를 나타낸다.
 */
public enum ClimateState {

    VERY_COLD("매우 차가움"),
    COLD("차가움"),
    BALANCED_TEMPERATURE("온도 중화"),
    HOT("더움"),
    VERY_HOT("매우 더움"),

    VERY_DRY("매우 건조함"),
    DRY("건조함"),
    BALANCED_MOISTURE("습도 중화"),
    HUMID("습함"),
    VERY_HUMID("매우 습함");

    private final String koreanName;

    ClimateState(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}