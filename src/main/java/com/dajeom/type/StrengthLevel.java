package com.dajeom.type;

/**
 * 일간의 기본 강약 단계.
 *
 * 내부 명리 분석용 타입이며,
 * 최종 사용자 화면에서는
 * 일반인이 이해하기 쉬운 문장으로 변환한다.
 */
public enum StrengthLevel {

    VERY_STRONG(
            "매우 신강"
    ),

    STRONG(
            "신강"
    ),

    BALANCED(
            "중화"
    ),

    WEAK(
            "신약"
    ),

    VERY_WEAK(
            "매우 신약"
    );

    private final String koreanName;

    StrengthLevel(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}