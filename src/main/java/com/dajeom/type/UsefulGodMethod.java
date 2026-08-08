package com.dajeom.type;

/**
 * 용신 판단 방법.
 *
 * 현재 V1에서는 억부 관점의
 * 기본 후보 분석부터 구현한다.
 *
 * 이후:
 *
 * - 조후
 * - 통관
 * - 병약
 *
 * 등의 방법을 확장할 수 있다.
 */
public enum UsefulGodMethod {

    SUPPRESSION_SUPPORT(
            "억부"
    );

    private final String koreanName;

    UsefulGodMethod(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}