package com.dajeom.domain;

public enum StemRelationType {

    COMBINATION(
            "合",
            "합"
    ),

    CLASH(
            "沖",
            "충"
    );

    private final String hanja;
    private final String korean;

    StemRelationType(
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