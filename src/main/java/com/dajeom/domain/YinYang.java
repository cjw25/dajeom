package com.dajeom.domain;

public enum YinYang {

    YANG("陽", "양"),
    YIN("陰", "음");

    private final String hanja;
    private final String korean;

    YinYang(
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