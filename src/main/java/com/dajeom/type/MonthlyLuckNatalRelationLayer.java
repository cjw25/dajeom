package com.dajeom.type;

/**
 * 정밀화 15차-4.
 *
 * 월운과 원국 사이 관계가 발생한 계층.
 *
 * HEAVENLY_STEM
 * = 월운 천간 × 원국 천간
 *
 * EARTHLY_BRANCH
 * = 월운 지지 × 원국 지지
 *
 * 관계 자체에는 길흉 점수를 부여하지 않는다.
 */
public enum MonthlyLuckNatalRelationLayer {

    HEAVENLY_STEM(
            "천간 관계"
    ),

    EARTHLY_BRANCH(
            "지지 관계"
    );

    private final String koreanName;

    MonthlyLuckNatalRelationLayer(
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