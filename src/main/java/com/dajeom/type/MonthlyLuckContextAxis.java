package com.dajeom.type;

/**
 * 정밀화 15차-7.
 *
 * 월운 Context의 독립 분석축.
 *
 * =================================================
 *
 * 하나의 월운에 여러 Axis가 동시에 존재할 수 있다.
 *
 * 예:
 *
 * USEFUL_GOD
 * -> SUPPORTED
 *
 * TEN_GOD
 * -> SUPPORTED
 *
 * NATAL_RELATION
 * -> CONFLICTED
 *
 * DECADE_INTERACTION
 * -> QUALIFIED
 *
 * =================================================
 *
 * 하나의 점수 또는 하나의 길흉 상태로
 * 강제 축약하지 않는다.
 */
public enum MonthlyLuckContextAxis {

    USEFUL_GOD(
            "용신·희신"
    ),

    STRUCTURE(
            "격국"
    ),

    TEN_GOD(
            "십성 구조"
    ),

    PALACE(
            "궁위"
    ),

    NATAL_RELATION(
            "원국·월운 관계"
    ),

    ANNUAL_INTERACTION(
            "세운·월운 관계"
    ),

    DECADE_INTERACTION(
            "대운·월운 관계"
    ),

    TRANSFORMATION(
            "합화·기능변화"
    ),

    THREE_LAYER_RELATION(
            "3층 관계"
    ),

    FOUR_LAYER_RELATION(
            "원국·대운·세운·월운 4층 관계"
    );

    private final String koreanName;

    MonthlyLuckContextAxis(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean usefulGod() {

        return this == USEFUL_GOD;
    }

    public boolean structure() {

        return this == STRUCTURE;
    }

    public boolean tenGod() {

        return this == TEN_GOD;
    }

    public boolean palace() {

        return this == PALACE;
    }

    public boolean natalRelation() {

        return this == NATAL_RELATION;
    }

    public boolean annualInteraction() {

        return this == ANNUAL_INTERACTION;
    }

    public boolean decadeInteraction() {

        return this == DECADE_INTERACTION;
    }

    public boolean transformation() {

        return this == TRANSFORMATION;
    }

    public boolean threeLayerRelation() {

        return this == THREE_LAYER_RELATION;
    }

    public boolean fourLayerRelation() {

        return this == FOUR_LAYER_RELATION;
    }
}