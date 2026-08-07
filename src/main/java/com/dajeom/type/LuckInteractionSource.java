package com.dajeom.type;

/**
 * 운의 상호작용이 발생한 분석 구간을 나타낸다.
 */
public enum LuckInteractionSource {

    NATAL_DECADE(
            "원국-대운"
    ),

    NATAL_ANNUAL(
            "원국-세운"
    ),

    DECADE_ANNUAL(
            "대운-세운"
    );

    private final String koreanName;

    LuckInteractionSource(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}