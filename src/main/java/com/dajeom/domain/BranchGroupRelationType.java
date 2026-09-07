package com.dajeom.domain;

public enum BranchGroupRelationType {

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

    BranchGroupRelationType(
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