package com.dajeom.domain;

public enum HeavenlyStem {

    JIA(
            0,
            "甲",
            "갑",
            YinYang.YANG,
            FiveElement.WOOD
    ),

    YI(
            1,
            "乙",
            "을",
            YinYang.YIN,
            FiveElement.WOOD
    ),

    BING(
            2,
            "丙",
            "병",
            YinYang.YANG,
            FiveElement.FIRE
    ),

    DING(
            3,
            "丁",
            "정",
            YinYang.YIN,
            FiveElement.FIRE
    ),

    WU(
            4,
            "戊",
            "무",
            YinYang.YANG,
            FiveElement.EARTH
    ),

    JI(
            5,
            "己",
            "기",
            YinYang.YIN,
            FiveElement.EARTH
    ),

    GENG(
            6,
            "庚",
            "경",
            YinYang.YANG,
            FiveElement.METAL
    ),

    XIN(
            7,
            "辛",
            "신",
            YinYang.YIN,
            FiveElement.METAL
    ),

    REN(
            8,
            "壬",
            "임",
            YinYang.YANG,
            FiveElement.WATER
    ),

    GUI(
            9,
            "癸",
            "계",
            YinYang.YIN,
            FiveElement.WATER
    );

    private static final HeavenlyStem[] VALUES = values();

    private final int index;
    private final String hanja;
    private final String korean;
    private final YinYang yinYang;
    private final FiveElement element;

    HeavenlyStem(
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

    public static HeavenlyStem ofIndex(int index) {
        return VALUES[Math.floorMod(index, VALUES.length)];
    }
}