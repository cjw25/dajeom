package com.dajeom.type;

/**
 * 원국의 한난 상태.
 *
 * 내부 분석용 값이다.
 *
 * 최종 사용자 화면에서는
 * "매우 차가운 사주" 같은 단정적인 표현보다
 * 일반인이 이해하기 쉬운 상담 문장으로 변환한다.
 */
public enum TemperatureLevel {

    VERY_COLD(
            "매우 한랭"
    ),

    COLD(
            "한랭"
    ),

    BALANCED(
            "한난 균형"
    ),

    HOT(
            "온열"
    ),

    VERY_HOT(
            "매우 온열"
    );

    private final String koreanName;

    TemperatureLevel(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}