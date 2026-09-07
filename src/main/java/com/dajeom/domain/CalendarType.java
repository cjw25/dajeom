package com.dajeom.domain;

public enum CalendarType {

    SOLAR(
            "양력"
    ),

    LUNAR(
            "음력"
    );

    private final String korean;

    CalendarType(
            String korean
    ) {
        this.korean = korean;
    }

    public String korean() {
        return korean;
    }

    public boolean isSolar() {
        return this == SOLAR;
    }

    public boolean isLunar() {
        return this == LUNAR;
    }
}