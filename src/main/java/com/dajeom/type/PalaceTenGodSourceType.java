package com.dajeom.type;

/**
 * 정밀화 12차-3.
 *
 * 궁위에서 십성이 어디에서 나온 것인지
 * 나타내는 Source Type.
 *
 * =================================================
 *
 * VISIBLE_STEM
 *
 * 년간·월간·시간처럼
 * 천간에 직접 드러난 외부 십성.
 *
 * DAY 천간은 일간 자신이므로
 * 여기에 포함하지 않는다.
 *
 * =================================================
 *
 * BRANCH_MAIN
 *
 * 지지의 본기.
 *
 * =================================================
 *
 * BRANCH_MIDDLE
 *
 * 지지의 중기.
 *
 * =================================================
 *
 * BRANCH_RESIDUAL
 *
 * 지지의 여기.
 *
 * =================================================
 *
 * 이 enum은 세력 가중치를 의미하지 않는다.
 *
 * 본기 > 중기 > 여기라는 실제 세력 배분은
 * 이미 2차/3차에서 처리된 영역이고,
 * 궁위 분석에서 다시 임의 점수를 더하지 않는다.
 */
public enum PalaceTenGodSourceType {

    VISIBLE_STEM(
            "천간 투출"
    ),

    BRANCH_MAIN(
            "지지 본기"
    ),

    BRANCH_MIDDLE(
            "지지 중기"
    ),

    BRANCH_RESIDUAL(
            "지지 여기"
    );

    private final String koreanName;

    PalaceTenGodSourceType(
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