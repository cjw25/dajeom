package com.dajeom.type;

/**
 * 정밀화 12차-4.
 *
 * 궁위 사이의 관계가
 * 천간층에서 발생했는지,
 * 지지층에서 발생했는지를 구분한다.
 *
 * 관계의 길흉을 의미하지 않는다.
 */
public enum PalaceRelationLayer {

    HEAVENLY_STEM(
            "천간 관계"
    ),

    EARTHLY_BRANCH(
            "지지 관계"
    );

    private final String koreanName;

    PalaceRelationLayer(
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