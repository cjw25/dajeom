package com.dajeom.type;

/**
 * 오행 후보가 원국에서 담당하는 최종 역할이다.
 */
public enum UsefulGodType {

    USEFUL_GOD(
            "용신",
            "원국의 핵심 불균형을 조절하는 우선 오행"
    ),

    FAVORABLE_GOD(
            "희신",
            "용신을 돕거나 원국의 균형을 보조하는 오행"
    ),

    NEUTRAL_GOD(
            "한신",
            "원국에 미치는 영향이 비교적 중립적인 오행"
    ),

    UNFAVORABLE_GOD(
            "기신",
            "원국의 불균형을 키우거나 일간에 부담을 주는 오행"
    ),

    ENEMY_GOD(
            "구신",
            "기신을 돕거나 용신의 작용을 방해하는 오행"
    );

    private final String koreanName;
    private final String description;

    UsefulGodType(
            String koreanName,
            String description
    ) {
        this.koreanName = koreanName;
        this.description = description;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getDescription() {
        return description;
    }
}