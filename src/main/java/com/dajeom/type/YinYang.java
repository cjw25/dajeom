package com.dajeom.type;

/**
 * 음양.
 */
public enum YinYang {

    YANG("양", "陽"),
    YIN("음", "陰");

    private final String koreanName;
    private final String chineseCharacter;

    YinYang(
            String koreanName,
            String chineseCharacter
    ) {
        this.koreanName = koreanName;
        this.chineseCharacter = chineseCharacter;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getChineseCharacter() {
        return chineseCharacter;
    }
}