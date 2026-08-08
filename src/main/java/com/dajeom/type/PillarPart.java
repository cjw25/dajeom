package com.dajeom.type;

/**
 * 하나의 간지에서
 * 천간/지지를 구분한다.
 */
public enum PillarPart {

    STEM(
            "천간"
    ),

    BRANCH(
            "지지"
    );

    private final String koreanName;

    PillarPart(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}