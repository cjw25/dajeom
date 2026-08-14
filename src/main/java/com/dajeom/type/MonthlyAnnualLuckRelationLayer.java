package com.dajeom.type;

/**
 * 정밀화 15차-6.
 *
 * 세운과 월운 사이의 관계 Layer.
 *
 * =================================================
 *
 * HEAVENLY_STEM
 *
 * 세운 천간
 * ↕
 * 월운 천간
 *
 * =================================================
 *
 * EARTHLY_BRANCH
 *
 * 세운 지지
 * ↕
 * 월운 지지
 *
 * =================================================
 *
 * 이 Enum은 관계의 발생 Layer만 표현한다.
 *
 * 합 = 좋음
 * 충 = 나쁨
 *
 * 같은 길흉 판정은 하지 않는다.
 */
public enum MonthlyAnnualLuckRelationLayer {

    HEAVENLY_STEM(
            "천간 관계"
    ),

    EARTHLY_BRANCH(
            "지지 관계"
    );

    private final String koreanName;

    MonthlyAnnualLuckRelationLayer(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean heavenlyStem() {

        return this
                == HEAVENLY_STEM;
    }

    public boolean earthlyBranch() {

        return this
                == EARTHLY_BRANCH;
    }
}