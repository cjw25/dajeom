package com.dajeom.type;

/**
 * 정밀화 8차-7.
 *
 * 지지 형(刑)의 실제 구성 형태.
 *
 * =================================================
 *
 * COMPLETE_THREE
 *
 * 삼형 계열의 세 지지가 모두 존재.
 *
 * 예:
 *
 * 寅 + 巳 + 申
 * 丑 + 戌 + 未
 *
 * =================================================
 *
 * PARTIAL_THREE
 *
 * 삼형 계열의 세 구성원 중
 * 두 지지만 존재.
 *
 * 예:
 *
 * 寅 + 巳
 * 巳 + 申
 * 申 + 寅
 *
 * 丑 + 戌
 * 戌 + 未
 * 未 + 丑
 *
 * =================================================
 *
 * MUTUAL_TWO
 *
 * 두 지지 자체가 완전한 상형 관계를 형성.
 *
 * 子 + 卯
 *
 * 子卯는 삼형의 일부가 아니라
 * 두 지지의 상호 형 관계이므로
 * PARTIAL_THREE로 처리하지 않는다.
 *
 * =================================================
 *
 * SELF
 *
 * 동일 자형 지지가 서로 다른 궁위에
 * 두 번 이상 존재.
 *
 * 辰辰
 * 午午
 * 酉酉
 * 亥亥
 */
public enum EarthlyBranchPunishmentForm {

    COMPLETE_THREE(
            "완전 삼형",
            4
    ),

    PARTIAL_THREE(
            "부분 삼형",
            3
    ),

    MUTUAL_TWO(
            "상호 형",
            2
    ),

    SELF(
            "자형",
            1
    );

    private final String koreanName;

    private final int rank;

    EarthlyBranchPunishmentForm(
            String koreanName,
            int rank
    ) {

        this.koreanName =
                koreanName;

        this.rank =
                rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean completeThree() {

        return this == COMPLETE_THREE;
    }

    public boolean partialThree() {

        return this == PARTIAL_THREE;
    }

    public boolean mutualTwo() {

        return this == MUTUAL_TWO;
    }

    public boolean selfPunishment() {

        return this == SELF;
    }

    public boolean pairForm() {

        return this != COMPLETE_THREE;
    }
}