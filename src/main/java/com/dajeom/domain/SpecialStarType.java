package com.dajeom.domain;

public enum SpecialStarType {

    MUNCHANG_NOBLEMAN(
            "文昌貴人",
            "문창귀인"
    ),

    MUNGOK_NOBLEMAN(
            "文曲貴人",
            "문곡귀인"
    ),

    HAKDANG_NOBLEMAN(
            "學堂貴人",
            "학당귀인"
    ),

    CHEONJU_NOBLEMAN(
            "天廚貴人",
            "천주귀인"
    ),

    AMROK(
            "暗祿",
            "암록"
    ),

    TAEGEUK_NOBLEMAN(
            "太極貴人",
            "태극귀인"
    ),

    BOKSEONG_NOBLEMAN(
            "福星貴人",
            "복성귀인"
    ),

    GWANGWI_HAKGWAN(
            "官貴學館",
            "관귀학관"
    ),

    GEUMYEO(
            "金輿",
            "금여"
    ),

    HYEONCHIM(
            "懸針殺",
            "현침살"
    ),

    BAEKHO(
            "白虎殺",
            "백호살"
    ),

    BIIN(
            "飛刃殺",
            "비인살"
    ),

    CHEONDEOK_NOBLEMAN(
            "天德貴人",
            "천덕귀인"
    ),

    WOLDEOK_NOBLEMAN(
            "月德貴人",
            "월덕귀인"
    );

    private final String hanja;
    private final String korean;

    SpecialStarType(
            String hanja,
            String korean
    ) {
        this.hanja = hanja;
        this.korean = korean;
    }

    public String hanja() {
        return hanja;
    }

    public String korean() {
        return korean;
    }
}