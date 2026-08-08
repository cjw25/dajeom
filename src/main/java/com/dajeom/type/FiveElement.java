package com.dajeom.type;

/**
 * 오행.
 */
public enum FiveElement {

    WOOD("목", "木"),
    FIRE("화", "火"),
    EARTH("토", "土"),
    METAL("금", "金"),
    WATER("수", "水");

    private final String koreanName;
    private final String chineseCharacter;

    FiveElement(
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