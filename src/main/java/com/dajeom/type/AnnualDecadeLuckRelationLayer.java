package com.dajeom.type;

/**
 * 정밀화 14차-5.
 *
 * 세운과 대운 사이의 관계 Layer.
 *
 * =================================================
 *
 * HEAVENLY_STEM
 *
 * 세운 천간
 * ↕
 * 대운 천간
 *
 * =================================================
 *
 * EARTHLY_BRANCH
 *
 * 세운 지지
 * ↕
 * 대운 지지
 *
 * =================================================
 *
 * 관계 자체를 길흉으로 환산하지 않는다.
 */
public enum AnnualDecadeLuckRelationLayer {

    HEAVENLY_STEM(
            "천간 관계"
    ),

    EARTHLY_BRANCH(
            "지지 관계"
    );

    private final String koreanName;

    AnnualDecadeLuckRelationLayer(
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