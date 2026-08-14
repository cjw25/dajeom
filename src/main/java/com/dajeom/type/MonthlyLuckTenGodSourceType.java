package com.dajeom.type;

/**
 * 정밀화 15차-3.
 *
 * 월운 십성 Evidence가
 * 어느 Source에서 발생했는지를 구분한다.
 *
 * =================================================
 *
 * VISIBLE_STEM
 *
 * 월운 천간.
 *
 * =================================================
 *
 * BRANCH_MAIN
 *
 * 월운 지지 본기.
 *
 * =================================================
 *
 * BRANCH_MIDDLE
 *
 * 월운 지지 중기.
 *
 * =================================================
 *
 * BRANCH_RESIDUAL
 *
 * 월운 지지 여기.
 *
 * =================================================
 *
 * Source Type 자체에는
 * 강약 점수나 길흉 점수를 부여하지 않는다.
 */
public enum MonthlyLuckTenGodSourceType {

    VISIBLE_STEM(
            "월운 천간"
    ),

    BRANCH_MAIN(
            "월운 지지 본기"
    ),

    BRANCH_MIDDLE(
            "월운 지지 중기"
    ),

    BRANCH_RESIDUAL(
            "월운 지지 여기"
    );

    private final String koreanName;

    MonthlyLuckTenGodSourceType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean visibleStem() {

        return this == VISIBLE_STEM;
    }

    public boolean hiddenStem() {

        return this != VISIBLE_STEM;
    }

    public boolean mainHiddenStem() {

        return this == BRANCH_MAIN;
    }

    public boolean middleHiddenStem() {

        return this == BRANCH_MIDDLE;
    }

    public boolean residualHiddenStem() {

        return this == BRANCH_RESIDUAL;
    }
}