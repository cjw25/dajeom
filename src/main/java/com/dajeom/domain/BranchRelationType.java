package com.dajeom.domain;

public enum BranchRelationType {

    SIX_COMBINATION(
            "六合",
            "육합"
    ),

    CLASH(
            "沖",
            "충"
    ),

    PUNISHMENT(
            "刑",
            "형"
    ),

    SELF_PUNISHMENT(
            "自刑",
            "자형"
    ),

    BREAK(
            "破",
            "파"
    ),

    HARM(
            "害",
            "해"
    ),

    THREE_HARMONY(
            "三合",
            "삼합"
    ),

    DIRECTIONAL_HARMONY(
            "方合",
            "방합"
    );

    private final String hanja;
    private final String korean;

    BranchRelationType(
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