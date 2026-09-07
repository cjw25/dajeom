package com.dajeom.domain;

public enum FiveElement {

    WOOD("木", "목"),
    FIRE("火", "화"),
    EARTH("土", "토"),
    METAL("金", "금"),
    WATER("水", "수");

    private final String hanja;
    private final String korean;

    FiveElement(
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