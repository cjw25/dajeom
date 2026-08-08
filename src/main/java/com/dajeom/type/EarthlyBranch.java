package com.dajeom.type;

/**
 * 십이지지.
 *
 * index는 자(子)를 0으로 시작한다.
 */
public enum EarthlyBranch {

    ZI(
            0,
            "자",
            "子",
            FiveElement.WATER,
            YinYang.YANG
    ),

    CHOU(
            1,
            "축",
            "丑",
            FiveElement.EARTH,
            YinYang.YIN
    ),

    YIN(
            2,
            "인",
            "寅",
            FiveElement.WOOD,
            YinYang.YANG
    ),

    MAO(
            3,
            "묘",
            "卯",
            FiveElement.WOOD,
            YinYang.YIN
    ),

    CHEN(
            4,
            "진",
            "辰",
            FiveElement.EARTH,
            YinYang.YANG
    ),

    SI(
            5,
            "사",
            "巳",
            FiveElement.FIRE,
            YinYang.YIN
    ),

    WU(
            6,
            "오",
            "午",
            FiveElement.FIRE,
            YinYang.YANG
    ),

    WEI(
            7,
            "미",
            "未",
            FiveElement.EARTH,
            YinYang.YIN
    ),

    SHEN(
            8,
            "신",
            "申",
            FiveElement.METAL,
            YinYang.YANG
    ),

    YOU(
            9,
            "유",
            "酉",
            FiveElement.METAL,
            YinYang.YIN
    ),

    XU(
            10,
            "술",
            "戌",
            FiveElement.EARTH,
            YinYang.YANG
    ),

    HAI(
            11,
            "해",
            "亥",
            FiveElement.WATER,
            YinYang.YIN
    );

    private final int index;
    private final String koreanName;
    private final String chineseCharacter;
    private final FiveElement element;
    private final YinYang yinYang;

    EarthlyBranch(
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

    public static EarthlyBranch fromIndex(
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