package com.dajeom.type;

/**
 * 정밀화 13차-5.
 *
 * 대운과 원국 사이의 관계 Layer.
 *
 * =================================================
 *
 * HEAVENLY_STEM
 *
 * 대운 천간과
 * 원국 천간 사이의 관계.
 *
 * =================================================
 *
 * EARTHLY_BRANCH
 *
 * 대운 지지와
 * 원국 지지 사이의 관계.
 *
 * =================================================
 *
 * 이 Layer 자체는 길흉을 뜻하지 않는다.
 */
public enum DecadeLuckNatalRelationLayer {

    HEAVENLY_STEM(
            "천간 관계"
    ),

    EARTHLY_BRANCH(
            "지지 관계"
    );

    private final String koreanName;

    DecadeLuckNatalRelationLayer(
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