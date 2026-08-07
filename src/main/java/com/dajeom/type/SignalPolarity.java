package com.dajeom.type;

/**
 * 해석 신호가 가진 방향을 나타낸다.
 *
 * FAVORABLE:
 * 장점, 기회, 발전 가능성을 강조하는 신호
 *
 * CHALLENGING:
 * 부담, 갈등, 위험 관리가 필요한 신호
 *
 * NEUTRAL:
 * 좋고 나쁨보다 성향이나 조건 자체를 설명하는 신호
 */
public enum SignalPolarity {

    FAVORABLE("유리"),
    NEUTRAL("중립"),
    CHALLENGING("주의");

    private final String koreanName;

    SignalPolarity(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}