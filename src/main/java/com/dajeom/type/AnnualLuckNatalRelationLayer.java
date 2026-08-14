package com.dajeom.type;

/**
 * 정밀화 14차-4.
 *
 * 세운과 원국 사이의 관계 Layer.
 *
 * =================================================
 *
 * HEAVENLY_STEM
 *
 * 세운 천간
 * ↕
 * 원국 천간
 *
 * =================================================
 *
 * EARTHLY_BRANCH
 *
 * 세운 지지
 * ↕
 * 원국 지지
 *
 * =================================================
 *
 * 관계 Layer 자체는 길흉이 아니다.
 */
public enum AnnualLuckNatalRelationLayer {

    HEAVENLY_STEM(
            "천간 관계"
    ),

    EARTHLY_BRANCH(
            "지지 관계"
    );

    private final String koreanName;

    AnnualLuckNatalRelationLayer(
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