package com.dajeom.type;

/**
 * 정밀화 15차-5.
 *
 * 대운과 월운 사이의 관계 Layer.
 *
 * HEAVENLY_STEM
 * = 대운 천간 × 월운 천간
 *
 * EARTHLY_BRANCH
 * = 대운 지지 × 월운 지지
 *
 * 관계 자체는 길흉 점수가 아니다.
 */
public enum MonthlyDecadeLuckRelationLayer {

    HEAVENLY_STEM(
            "천간 관계"
    ),

    EARTHLY_BRANCH(
            "지지 관계"
    );

    private final String koreanName;

    MonthlyDecadeLuckRelationLayer(
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