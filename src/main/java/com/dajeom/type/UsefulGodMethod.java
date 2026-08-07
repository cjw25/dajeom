package com.dajeom.type;

/**
 * 용신 후보를 산출한 명리 분석 방식이다.
 */
public enum UsefulGodMethod {

    CLIMATE(
            "조후",
            "계절의 한난조습을 조절하는 방식"
    ),

    BALANCING(
            "억부",
            "일간의 신강·신약을 조절하는 방식"
    ),

    MEDIATION(
            "통관",
            "서로 충돌하는 오행 사이를 연결하는 방식"
    ),

    DISEASE_REMEDY(
            "병약",
            "원국의 가장 큰 문제를 찾아 해소하는 방식"
    ),

    INTEGRATED(
            "종합",
            "여러 분석 방식을 통합한 최종 판정"
    );

    private final String koreanName;
    private final String description;

    UsefulGodMethod(
            String koreanName,
            String description
    ) {
        this.koreanName = koreanName;
        this.description = description;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getDescription() {
        return description;
    }
}