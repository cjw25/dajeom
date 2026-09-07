package com.dajeom.domain;

public enum EarthlyBranch {

    ZI(
            0,
            "子",
            "자",
            YinYang.YANG,
            FiveElement.WATER
    ),

    CHOU(
            1,
            "丑",
            "축",
            YinYang.YIN,
            FiveElement.EARTH
    ),

    YIN(
            2,
            "寅",
            "인",
            YinYang.YANG,
            FiveElement.WOOD
    ),

    MAO(
            3,
            "卯",
            "묘",
            YinYang.YIN,
            FiveElement.WOOD
    ),

    CHEN(
            4,
            "辰",
            "진",
            YinYang.YANG,
            FiveElement.EARTH
    ),

    SI(
            5,
            "巳",
            "사",
            YinYang.YIN,
            FiveElement.FIRE
    ),

    WU(
            6,
            "午",
            "오",
            YinYang.YANG,
            FiveElement.FIRE
    ),

    WEI(
            7,
            "未",
            "미",
            YinYang.YIN,
            FiveElement.EARTH
    ),

    SHEN(
            8,
            "申",
            "신",
            YinYang.YANG,
            FiveElement.METAL
    ),

    YOU(
            9,
            "酉",
            "유",
            YinYang.YIN,
            FiveElement.METAL
    ),

    XU(
            10,
            "戌",
            "술",
            YinYang.YANG,
            FiveElement.EARTH
    ),

    HAI(
            11,
            "亥",
            "해",
            YinYang.YIN,
            FiveElement.WATER
    );

    private static final EarthlyBranch[] VALUES = values();

    private final int index;
    private final String hanja;
    private final String korean;
    private final YinYang yinYang;
    private final FiveElement element;

    EarthlyBranch(
            int index,
            String hanja,
            String korean,
            YinYang yinYang,
            FiveElement element
    ) {
        this.index = index;
        this.hanja = hanja;
        this.korean = korean;
        this.yinYang = yinYang;
        this.element = element;
    }

    public int index() {
        return index;
    }

    public String hanja() {
        return hanja;
    }

    public String korean() {
        return korean;
    }

    public YinYang yinYang() {
        return yinYang;
    }

    public FiveElement element() {
        return element;
    }

    public static EarthlyBranch ofIndex(int index) {
        return VALUES[Math.floorMod(index, VALUES.length)];
    }
}