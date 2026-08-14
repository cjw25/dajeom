package com.dajeom.type;

/**
 * 정밀화 17차-1.
 *
 * 신살 Rule이 어떤 원국 기준점을
 * 출발점으로 사용하는지 표현한다.
 *
 * =================================================
 *
 * 중요 정책:
 *
 * 기본 신살 Rule의 기준점은
 * 원국(NATAL)에서 가져온다.
 *
 * 운의 천간/지지를 다시 기준점으로 사용해서
 * 또 다른 신살을 무한 재생산하지 않는다.
 *
 * =================================================
 *
 * 예:
 *
 * 천을귀인
 *
 * 원국 DAY STEM
 * ↓
 * 원국/대운/세운/월운 Branch 검사
 *
 * =================================================
 *
 * 역마
 *
 * 원국 YEAR BRANCH 또는 DAY BRANCH
 * ↓
 * 원국/대운/세운/월운 Branch 검사
 */
public enum ShinsalRuleBasis {

    NATAL_DAY_STEM(
            "원국 일간 기준"
    ),

    NATAL_YEAR_STEM(
            "원국 년간 기준"
    ),

    NATAL_DAY_BRANCH(
            "원국 일지 기준"
    ),

    NATAL_YEAR_BRANCH(
            "원국 년지 기준"
    ),

    NATAL_YEAR_OR_DAY_BRANCH(
            "원국 년지 또는 일지 기준"
    ),

    NATAL_PILLAR(
            "원국 특정 기둥 기준"
    ),

    MULTI_POINT(
            "복수 기준점"
    );

    private final String koreanName;

    ShinsalRuleBasis(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }
}