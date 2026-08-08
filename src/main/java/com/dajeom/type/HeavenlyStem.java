package com.dajeom.type;

/**
 * 십천간.
 *
 * index는 갑(甲)을 0으로 시작한다.
 */
public enum HeavenlyStem {

    JIA(
            0,
            "갑",
            "甲",
            FiveElement.WOOD,
            YinYang.YANG
    ),

    YI(
            1,
            "을",
            "乙",
            FiveElement.WOOD,
            YinYang.YIN
    ),

    BING(
            2,
            "병",
            "丙",
            FiveElement.FIRE,
            YinYang.YANG
    ),

    DING(
            3,
            "정",
            "丁",
            FiveElement.FIRE,
            YinYang.YIN
    ),

    WU(
            4,
            "무",
            "戊",
            FiveElement.EARTH,
            YinYang.YANG
    ),

    JI(
            5,
            "기",
            "己",
            FiveElement.EARTH,
            YinYang.YIN
    ),

    GENG(
            6,
            "경",
            "庚",
            FiveElement.METAL,
            YinYang.YANG
    ),

    XIN(
            7,
            "신",
            "辛",
            FiveElement.METAL,
            YinYang.YIN
    ),

    REN(
            8,
            "임",
            "壬",
            FiveElement.WATER,
            YinYang.YANG
    ),

    GUI(
            9,
            "계",
            "癸",
            FiveElement.WATER,
            YinYang.YIN
    );

    private final int index;
    private final String koreanName;
    private final String chineseCharacter;
    private final FiveElement element;
    private final YinYang yinYang;

    HeavenlyStem(
            int index,
            String koreanName,
            String chineseCharacter,
            FiveElement element,
            YinYang yinYang
    ) {
        this.index = index;
        this.koreanName = koreanName;
        this.chineseCharacter =
                chineseCharacter;
        this.element = element;
        this.yinYang = yinYang;
    }

    public int getIndex() {
        return index;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getChineseCharacter() {
        return chineseCharacter;
    }

    public FiveElement getElement() {
        return element;
    }

    public YinYang getYinYang() {
        return yinYang;
    }

    public static HeavenlyStem fromIndex(
            int index
    ) {

        int normalizedIndex =
                Math.floorMod(
                        index,
                        values().length
                );

        return values()[normalizedIndex];
    }
}