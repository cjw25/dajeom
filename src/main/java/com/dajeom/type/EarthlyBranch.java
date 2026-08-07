package com.dajeom.type;

import java.util.Arrays;

public enum EarthlyBranch {

    JA(
            0,
            "子",
            "자",
            FiveElement.WATER,
            YinYang.YANG
    ),

    CHUK(
            1,
            "丑",
            "축",
            FiveElement.EARTH,
            YinYang.YIN
    ),

    IN(
            2,
            "寅",
            "인",
            FiveElement.WOOD,
            YinYang.YANG
    ),

    MYO(
            3,
            "卯",
            "묘",
            FiveElement.WOOD,
            YinYang.YIN
    ),

    JIN(
            4,
            "辰",
            "진",
            FiveElement.EARTH,
            YinYang.YANG
    ),

    SA(
            5,
            "巳",
            "사",
            FiveElement.FIRE,
            YinYang.YIN
    ),

    O(
            6,
            "午",
            "오",
            FiveElement.FIRE,
            YinYang.YANG
    ),

    MI(
            7,
            "未",
            "미",
            FiveElement.EARTH,
            YinYang.YIN
    ),

    SIN(
            8,
            "申",
            "신",
            FiveElement.METAL,
            YinYang.YANG
    ),

    YU(
            9,
            "酉",
            "유",
            FiveElement.METAL,
            YinYang.YIN
    ),

    SUL(
            10,
            "戌",
            "술",
            FiveElement.EARTH,
            YinYang.YANG
    ),

    HAE(
            11,
            "亥",
            "해",
            FiveElement.WATER,
            YinYang.YIN
    );

    private final int index;
    private final String chineseCharacter;
    private final String koreanName;
    private final FiveElement element;
    private final YinYang yinYang;

    EarthlyBranch(
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

    public static EarthlyBranch fromIndex(int index) {
        int normalizedIndex =
                Math.floorMod(index, values().length);

        return Arrays.stream(values())
                .filter(branch ->
                        branch.index == normalizedIndex
                )
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "유효하지 않은 지지 인덱스입니다: "
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