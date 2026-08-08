package com.dajeom.type;

/**
 * 명리 계산에 사용하는 성별.
 */
public enum Gender {

    MALE("남성"),
    FEMALE("여성");

    private final String koreanName;

    Gender(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}