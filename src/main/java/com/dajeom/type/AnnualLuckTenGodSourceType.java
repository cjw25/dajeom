package com.dajeom.type;

/**
 * 정밀화 14차-3.
 *
 * 세운 십성이 어느 Source에서
 * 발생했는지를 구분한다.
 *
 * =================================================
 *
 * VISIBLE_STEM
 *
 * 세운 천간.
 *
 * =================================================
 *
 * BRANCH_MAIN
 *
 * 세운 지지 본기.
 *
 * =================================================
 *
 * BRANCH_MIDDLE
 *
 * 세운 지지 중기.
 *
 * =================================================
 *
 * BRANCH_RESIDUAL
 *
 * 세운 지지 여기.
 *
 * =================================================
 *
 * 중요:
 *
 * Source Type은 강약 점수가 아니다.
 *
 * 천간 100
 * 본기 80
 * 중기 50
 *
 * 같은 별도의 가중치는
 * 이번 단계에서 만들지 않는다.
 */
public enum AnnualLuckTenGodSourceType {

    VISIBLE_STEM(
            "세운 천간"
    ),

    BRANCH_MAIN(
            "세운 지지 본기"
    ),

    BRANCH_MIDDLE(
            "세운 지지 중기"
    ),

    BRANCH_RESIDUAL(
            "세운 지지 여기"
    );

    private final String koreanName;

    AnnualLuckTenGodSourceType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean visibleStem() {

        return this
                == VISIBLE_STEM;
    }

    public boolean hiddenStem() {

        return this
                != VISIBLE_STEM;
    }

    public boolean mainHiddenStem() {

        return this
                == BRANCH_MAIN;
    }

    public boolean middleHiddenStem() {

        return this
                == BRANCH_MIDDLE;
    }

    public boolean residualHiddenStem() {

        return this
                == BRANCH_RESIDUAL;
    }
}