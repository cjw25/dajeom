package com.dajeom.type;

/**
 * 일간을 기준으로 계산되는 십성을 나타낸다.
 */
public enum TenGod {

    FRIEND("비견", "比肩"),
    ROB_WEALTH("겁재", "劫財"),

    EATING_GOD("식신", "食神"),
    HURTING_OFFICER("상관", "傷官"),

    INDIRECT_WEALTH("편재", "偏財"),
    DIRECT_WEALTH("정재", "正財"),

    SEVEN_KILLINGS("편관", "偏官"),
    DIRECT_OFFICER("정관", "正官"),

    INDIRECT_RESOURCE("편인", "偏印"),
    DIRECT_RESOURCE("정인", "正印");

    private final String koreanName;
    private final String chineseCharacter;

    TenGod(
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