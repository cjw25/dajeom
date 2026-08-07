package com.dajeom.type;

/**
 * 일간 강약 점수에 영향을 준 판정 요인을 나타낸다.
 */
public enum StrengthFactorType {

    BASE_SCORE("기준점수"),

    SEASONAL_COMMAND("득령·월령"),

    ROOT_SUPPORT("통근"),

    VISIBLE_PEER_SUPPORT("비겁 투출"),

    VISIBLE_RESOURCE_SUPPORT("인성 투출"),

    OUTPUT_DRAIN("식상 설기"),

    WEALTH_CONSUMPTION("재성 소모"),

    OFFICER_CONTROL("관성 극제"),

    ELEMENT_BALANCE("오행 세력 균형"),

    RELATION_IMPACT("합충형파해 영향");

    private final String koreanName;

    StrengthFactorType(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}