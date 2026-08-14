package com.dajeom.type;

/**
 * 정밀화 8차-5.
 *
 * 방합(方合)을 구성하는 세 지지의 계절 내 위치.
 *
 * 예:
 *
 * 寅卯辰 木方
 *
 * 寅 = 계절 시작
 * 卯 = 계절 중심 / 왕지
 * 辰 = 계절 마무리
 *
 * =================================================
 *
 * OPENING
 *
 * 계절의 시작 지지.
 *
 * 木方 寅
 * 火方 巳
 * 金方 申
 * 水方 亥
 *
 * =================================================
 *
 * CARDINAL
 *
 * 해당 계절 오행의 중심이 되는 왕지.
 *
 * 木方 卯
 * 火方 午
 * 金方 酉
 * 水方 子
 *
 * =================================================
 *
 * CLOSING
 *
 * 계절의 마무리 지지.
 *
 * 木方 辰
 * 火方 未
 * 金方 戌
 * 水方 丑
 */
public enum EarthlyBranchDirectionalCombinationMemberRole {

    OPENING(
            "계절 시작"
    ),

    CARDINAL(
            "계절 중심"
    ),

    CLOSING(
            "계절 마무리"
    );

    private final String koreanName;

    EarthlyBranchDirectionalCombinationMemberRole(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean opening() {

        return this == OPENING;
    }

    public boolean cardinal() {

        return this == CARDINAL;
    }

    public boolean closing() {

        return this == CLOSING;
    }
}