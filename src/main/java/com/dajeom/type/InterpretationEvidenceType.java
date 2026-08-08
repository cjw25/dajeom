package com.dajeom.type;

/**
 * 상담 해석 신호의 근거 종류.
 *
 * 실제 상담문을 생성할 때
 * 어떤 명리 근거에서 나온 판단인지
 * 추적하기 위해 사용한다.
 */
public enum InterpretationEvidenceType {

    STRENGTH(
            "일간 강약"
    ),

    STRUCTURE(
            "원국 구조"
    ),

    NATAL_RELATION(
            "원국 관계"
    ),

    TEMPERATURE(
            "조후"
    ),

    COMBINATION_TRANSFORMATION(
            "합화"
    ),

    MEDIATION(
            "통관"
    ),

    LUCK_TEN_GOD(
            "운의 십성"
    ),

    LUCK_RELATION(
            "원국·대운·세운 관계"
    ),

    USEFUL_ELEMENT_MATCH(
            "용신 후보 오행 유입"
    ),

    DECADE_TRANSITION(
            "대운 교체"
    );

    private final String koreanName;

    InterpretationEvidenceType(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}