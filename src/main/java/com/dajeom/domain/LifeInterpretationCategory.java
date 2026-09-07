package com.dajeom.domain;

/**
 * 최종 사용자에게 제공하는 7개 장문 해석 카테고리.
 *
 * =================================================
 *
 * 화면에는 이 카테고리와 자연어 풀이만 노출한다.
 *
 * 십성 / 지장간 / 합충형파해 / 격국 등의
 * 내부 계산표는 노출하지 않는다.
 */
public enum LifeInterpretationCategory {

    TEMPERAMENT(
            1,
            "성향·기질"
    ),

    PAST_FLOW(
            2,
            "지나온 흐름"
    ),

    FAMILY_RELATIONSHIPS(
            3,
            "가족·인간관계"
    ),

    LOVE_MARRIAGE(
            4,
            "연애·결혼"
    ),

    WEALTH(
            5,
            "재물"
    ),

    WORK_ACHIEVEMENT(
            6,
            "일·성취"
    ),

    HEALTH_LIFESTYLE(
            7,
            "건강·생활"
    );

    private final int order;

    private final String koreanLabel;

    LifeInterpretationCategory(
            int order,
            String koreanLabel
    ) {

        this.order =
                order;

        this.koreanLabel =
                koreanLabel;
    }

    public int order() {

        return order;
    }

    public String koreanLabel() {

        return koreanLabel;
    }
}