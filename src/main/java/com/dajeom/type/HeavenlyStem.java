package com.dajeom.type;

import java.util.Arrays;

public enum HeavenlyStem {

    GAP(
            0,
            "甲",
            "갑",
            FiveElement.WOOD,
            YinYang.YANG
    ),

    EUL(
            1,
            "乙",
            "을",
            FiveElement.WOOD,
            YinYang.YIN
    ),

    BYEONG(
            2,
            "丙",
            "병",
            FiveElement.FIRE,
            YinYang.YANG
    ),

    JEONG(
            3,
            "丁",
            "정",
            FiveElement.FIRE,
            YinYang.YIN
    ),

    MU(
            4,
            "戊",
            "무",
            FiveElement.EARTH,
            YinYang.YANG
    ),

    GI(
            5,
            "己",
            "기",
            FiveElement.EARTH,
            YinYang.YIN
    ),

    GYEONG(
            6,
            "庚",
            "경",
            FiveElement.METAL,
            YinYang.YANG
    ),

    SIN(
            7,
            "辛",
            "신",
            FiveElement.METAL,
            YinYang.YIN
    ),

    IM(
            8,
            "壬",
            "임",
            FiveElement.WATER,
            YinYang.YANG
    ),

    GYE(
            9,
            "癸",
            "계",
            FiveElement.WATER,
            YinYang.YIN
    );

    private final int index;
    private final String chineseCharacter;
    private final String koreanName;
    private final FiveElement element;
    private final YinYang yinYang;

    HeavenlyStem(
            int index,
            String chineseCharacter,
            String koreanName,
            FiveElement element,
            YinYang yinYang
    ) {
        this.index = index;
        this.chineseCharacter = chineseCharacter;
        this.koreanName = koreanName;
        this.element = element;
        this.yinYang = yinYang;
    }

    public static HeavenlyStem fromIndex(int index) {
        int normalizedIndex =
                Math.floorMod(index, values().length);

        return Arrays.stream(values())
                .filter(stem ->
                        stem.index == normalizedIndex
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "유효하지 않은 천간 인덱스입니다: "
                                        + index
                        )
                );
    }

    public int getIndex() {
        return index;
    }

    public String getChineseCharacter() {
        return chineseCharacter;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public FiveElement getElement() {
        return element;
    }

    public YinYang getYinYang() {
        return yinYang;
    }
}