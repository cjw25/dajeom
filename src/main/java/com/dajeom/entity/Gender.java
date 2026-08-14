package com.dajeom.entity;

/**
 * 사용자 성별.
 *
 * 대운 순행/역행 계산 등에 사용한다.
 *
 * DB / API 기본 값:
 *
 * MALE
 * FEMALE
 */
public enum Gender {

    MALE(
            "남성"
    ),

    FEMALE(
            "여성"
    );

    private final String koreanName;

    Gender(
            String koreanName
    ) {
        this.koreanName =
                koreanName;
    }

    public String koreanName() {
        return koreanName;
    }

    public boolean male() {
        return this == MALE;
    }

    public boolean female() {
        return this == FEMALE;
    }
}