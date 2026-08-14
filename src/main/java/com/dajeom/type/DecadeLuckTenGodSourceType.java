package com.dajeom.type;

/**
 * 정밀화 13차-4.
 *
 * 대운 십성이 어느 위치에서 발생했는지
 * 구분하기 위한 Source Type.
 *
 * =================================================
 *
 * VISIBLE_STEM
 *
 * 대운 천간 자체.
 *
 * =================================================
 *
 * BRANCH_MAIN
 *
 * 대운 지지의 본기.
 *
 * =================================================
 *
 * BRANCH_MIDDLE
 *
 * 대운 지지의 중기.
 *
 * =================================================
 *
 * BRANCH_RESIDUAL
 *
 * 대운 지지의 여기.
 *
 * =================================================
 *
 * 중요:
 *
 * Source Type은 세력 점수가 아니다.
 *
 * 예:
 *
 * VISIBLE_STEM = 100
 * BRANCH_MAIN = 80
 *
 * 같은 가중치를 이번 단계에서
 * 만들지 않는다.
 */
public enum DecadeLuckTenGodSourceType {

    VISIBLE_STEM(
            "대운 천간"
    ),

    BRANCH_MAIN(
            "대운 지지 본기"
    ),

    BRANCH_MIDDLE(
            "대운 지지 중기"
    ),

    BRANCH_RESIDUAL(
            "대운 지지 여기"
    );

    private final String koreanName;

    DecadeLuckTenGodSourceType(
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