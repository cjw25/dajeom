package com.dajeom.type;

/**
 * 대운·세운 종합 평가 단계를 나타낸다.
 *
 * 길흉을 단정하는 표현보다는
 * 운의 활용도와 부담 정도를 나타낸다.
 */
public enum LuckEvaluationLevel {

    VERY_CHALLENGING(
            "부담이 매우 큰 운"
    ),

    CHALLENGING(
            "부담이 있는 운"
    ),

    SLIGHTLY_CHALLENGING(
            "주의가 필요한 운"
    ),

    NEUTRAL(
            "중립적인 운"
    ),

    SLIGHTLY_FAVORABLE(
            "다소 유리한 운"
    ),

    FAVORABLE(
            "유리한 운"
    ),

    VERY_FAVORABLE(
            "매우 유리한 운"
    );

    private final String koreanName;

    LuckEvaluationLevel(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public static LuckEvaluationLevel fromScore(
            double score
    ) {
        if (score < 20.0) {
            return VERY_CHALLENGING;
        }

        if (score < 35.0) {
            return CHALLENGING;
        }

        if (score < 45.0) {
            return SLIGHTLY_CHALLENGING;
        }

        if (score <= 55.0) {
            return NEUTRAL;
        }

        if (score <= 65.0) {
            return SLIGHTLY_FAVORABLE;
        }

        if (score <= 80.0) {
            return FAVORABLE;
        }

        return VERY_FAVORABLE;
    }

    public String getKoreanName() {
        return koreanName;
    }
}