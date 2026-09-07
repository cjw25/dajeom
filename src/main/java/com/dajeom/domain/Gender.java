package com.dajeom.domain;

public enum Gender {

    MALE(
            "남성"
    ),

    FEMALE(
            "여성"
    );

    private final String korean;

    Gender(
            String korean
    ) {
        this.korean = korean;
    }

    public String korean() {
        return korean;
    }

    public boolean isMale() {
        return this == MALE;
    }

    public boolean isFemale() {
        return this == FEMALE;
    }
}