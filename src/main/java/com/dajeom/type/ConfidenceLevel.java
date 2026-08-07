package com.dajeom.type;

/**
 * 명리 구조 판정의 신뢰 수준이다.
 *
 * 점수가 높을수록 해당 구조의 조건이
 * 원국에서 명확하게 확인된다는 뜻이다.
 */
public enum ConfidenceLevel {

    LOW("낮음"),
    MEDIUM("보통"),
    HIGH("높음"),
    VERY_HIGH("매우 높음");

    private final String koreanName;

    ConfidenceLevel(String koreanName) {
        this.koreanName = koreanName;
    }

    public static ConfidenceLevel fromScore(
            double score
    ) {
        if (score < 55.0) {
            return LOW;
        }

        if (score < 70.0) {
            return MEDIUM;
        }

        if (score < 85.0) {
            return HIGH;
        }

        return VERY_HIGH;
    }

    public String getKoreanName() {
        return koreanName;
    }
}