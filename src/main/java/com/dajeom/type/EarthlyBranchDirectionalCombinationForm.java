package com.dajeom.type;

/**
 * 정밀화 8차-5.
 *
 * 방합 구성 완성도.
 *
 * =================================================
 *
 * COMPLETE
 *
 * 같은 계절의 세 지지가 모두 존재.
 *
 * 예:
 *
 * 寅 + 卯 + 辰
 *
 * =================================================
 *
 * PARTIAL_WITH_CARDINAL
 *
 * 두 지지만 존재하지만
 * 해당 方의 왕지를 포함.
 *
 * 예:
 *
 * 寅 + 卯
 * 卯 + 辰
 *
 * =================================================
 *
 * PARTIAL_WITHOUT_CARDINAL
 *
 * 시작지 + 마무리지 두 개만 있고
 * 중심 왕지가 빠진 형태.
 *
 * 예:
 *
 * 寅 + 辰
 *
 * =================================================
 *
 * 8차에서는 관계 존재만 보존한다.
 *
 * 두 지지만 있다고 실제 方局이
 * 성립했다고 확정하지 않는다.
 */
public enum EarthlyBranchDirectionalCombinationForm {

    COMPLETE(
            "완전 방합",
            3
    ),

    PARTIAL_WITH_CARDINAL(
            "왕지 포함 부분 방합",
            2
    ),

    PARTIAL_WITHOUT_CARDINAL(
            "왕지 없는 부분 방합",
            1
    );

    private final String koreanName;

    private final int rank;

    EarthlyBranchDirectionalCombinationForm(
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

    public boolean partialWithCardinal() {

        return this == PARTIAL_WITH_CARDINAL;
    }

    public boolean partialWithoutCardinal() {

        return this == PARTIAL_WITHOUT_CARDINAL;
    }

    public boolean partial() {

        return this != COMPLETE;
    }
}