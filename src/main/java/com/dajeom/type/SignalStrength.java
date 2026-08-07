package com.dajeom.type;

/**
 * 해석 신호가 원국과 운에서 얼마나 강하게 확인되는지를 나타낸다.
 */
public enum SignalStrength {

    LOW("낮음"),
    MEDIUM("보통"),
    HIGH("높음"),
    VERY_HIGH("매우 높음");

    private final String koreanName;

    SignalStrength(String koreanName) {
        this.koreanName = koreanName;
    }

    public static SignalStrength fromScore(
            double score
    ) {
        double absoluteScore =
                Math.abs(score);

        if (absoluteScore < 25.0) {
            return LOW;
        }

        if (absoluteScore < 50.0) {
            return MEDIUM;
        }

        if (absoluteScore < 75.0) {
            return HIGH;
        }

        return VERY_HIGH;
    }

    public String getKoreanName() {
        return koreanName;
    }
}