package com.dajeom.type;

/**
 * 합 관계의 종류.
 */
public enum CombinationScope {

    STEM(
            "천간합"
    ),

    SIX_COMBINATION(
            "지지 육합"
    ),

    THREE_HARMONY(
            "지지 삼합"
    );

    private final String koreanName;

    CombinationScope(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}