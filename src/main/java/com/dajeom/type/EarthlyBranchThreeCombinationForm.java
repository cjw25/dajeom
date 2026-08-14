package com.dajeom.type;

/**
 * 정밀화 8차-4.
 *
 * 삼합 관계의 구성 완성도.
 *
 * =================================================
 *
 * COMPLETE
 *
 * 생지 + 왕지 + 고지가 모두 존재.
 *
 * 예:
 *
 * 申 + 子 + 辰
 *
 * =================================================
 *
 * HALF_WITH_CARDINAL
 *
 * 왕지를 포함한 두 지지의 조합.
 *
 * 예:
 *
 * 申 + 子
 * 子 + 辰
 *
 * 다점 엔진에서는 이 형태를
 * 정식 "반합" Evidence로 취급한다.
 *
 * =================================================
 *
 * PARTIAL_WITHOUT_CARDINAL
 *
 * 생지 + 고지만 있고 왕지가 빠진 조합.
 *
 * 예:
 *
 * 申 + 辰
 *
 * 유파에 따라 공합·부분 삼합 등
 * 표현이 달라질 수 있으므로,
 *
 * 다점 엔진에서는 보수적으로
 * "왕지 없는 부분조합"으로 저장한다.
 *
 * HALF_WITH_CARDINAL과 동일하게 보지 않는다.
 */
public enum EarthlyBranchThreeCombinationForm {

    COMPLETE(
            "완전 삼합",
            3
    ),

    HALF_WITH_CARDINAL(
            "왕지 포함 반합",
            2
    ),

    PARTIAL_WITHOUT_CARDINAL(
            "왕지 없는 부분조합",
            1
    );

    private final String koreanName;

    private final int rank;

    EarthlyBranchThreeCombinationForm(
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

    public boolean complete() {

        return this == COMPLETE;
    }

    public boolean halfWithCardinal() {

        return this == HALF_WITH_CARDINAL;
    }

    public boolean partialWithoutCardinal() {

        return this == PARTIAL_WITHOUT_CARDINAL;
    }

    public boolean pairForm() {

        return this != COMPLETE;
    }
}