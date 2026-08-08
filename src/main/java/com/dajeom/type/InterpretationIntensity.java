package com.dajeom.type;

/**
 * 해석 신호의 내부 중요도.
 *
 * 명리 근거가 얼마나 많이 겹치는지를
 * 표현하기 위한 내부 값이다.
 *
 * 확률을 의미하지 않는다.
 */
public enum InterpretationIntensity {

    NONE(
            "근거 부족"
    ),

    LOW(
            "약함"
    ),

    MEDIUM(
            "보통"
    ),

    HIGH(
            "강함"
    );

    private final String koreanName;

    InterpretationIntensity(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}