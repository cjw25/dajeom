package com.dajeom.type;

/**
 * 지장간에서 해당 천간이 차지하는 역할을 나타낸다.
 *
 * MAIN:
 * 해당 지지의 중심 기운인 본기
 *
 * MIDDLE:
 * 본기 다음으로 작용하는 중기
 *
 * RESIDUAL:
 * 남아 있는 이전 계절의 기운인 여기
 */
public enum HiddenStemRole {

    MAIN("본기"),
    MIDDLE("중기"),
    RESIDUAL("여기");

    private final String koreanName;

    HiddenStemRole(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}