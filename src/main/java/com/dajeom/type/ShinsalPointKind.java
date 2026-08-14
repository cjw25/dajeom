package com.dajeom.type;

/**
 * 정밀화 17차-1.
 *
 * 신살 Rule에서 사용하는
 * 간지 Point의 종류.
 */
public enum ShinsalPointKind {

    HEAVENLY_STEM(
            "천간"
    ),

    EARTHLY_BRANCH(
            "지지"
    );

    private final String koreanName;

    ShinsalPointKind(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean heavenlyStem() {

        return this == HEAVENLY_STEM;
    }

    public boolean earthlyBranch() {

        return this == EARTHLY_BRANCH;
    }
}