package com.dajeom.type;

/**
 * 정밀화 10차-1.
 *
 * 십성 Evidence의 원국 출처.
 */
public enum TenGodSourceType {

    VISIBLE_STEM(
            "천간"
    ),

    HIDDEN_STEM(
            "지장간"
    );

    private final String koreanName;

    TenGodSourceType(
            String koreanName
    ) {

        this.koreanName = koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean visibleStem() {

        return this == VISIBLE_STEM;
    }

    public boolean hiddenStem() {

        return this == HIDDEN_STEM;
    }
}