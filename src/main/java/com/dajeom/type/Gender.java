package com.dajeom.type;

public enum Gender {

    MALE("남성"),
    FEMALE("여성");

    private final String koreanName;

    Gender(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}