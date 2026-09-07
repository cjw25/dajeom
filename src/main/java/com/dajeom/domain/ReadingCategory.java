package com.dajeom.domain;

/**
 * 최종 사용자 풀이 7개 카테고리와
 * Evidence / LifeClaim을 연결하는 내부 카테고리.
 */
public enum ReadingCategory {

    TEMPERAMENT(
            "성향·기질"
    ),

    LIFE_FLOW(
            "지나온 흐름"
    ),

    FAMILY_RELATIONSHIP(
            "가족·인간관계"
    ),

    LOVE_MARRIAGE(
            "연애·결혼"
    ),

    WEALTH(
            "재물"
    ),

    CAREER_ACHIEVEMENT(
            "일·성취"
    ),

    HEALTH_LIFESTYLE(
            "건강·생활"
    );

    private final String koreanName;

    ReadingCategory(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }
}