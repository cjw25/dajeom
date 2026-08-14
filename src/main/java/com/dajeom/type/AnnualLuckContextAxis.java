package com.dajeom.type;

/**
 * 정밀화 14차-6.
 *
 * 세운 Context를 구성하는 독립 분석축.
 *
 * =================================================
 *
 * 하나의 세운에는 여러 Axis의 Signal이
 * 동시에 존재할 수 있다.
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
 * 하나의 종합 점수나 하나의 최종 상태로
 * 강제 축약하지 않는다.
 */
public enum AnnualLuckContextAxis {

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
            "원국·세운 관계"
    ),

    DECADE_INTERACTION(
            "대운·세운 관계"
    ),

    TRANSFORMATION(
            "합화·기능변화"
    ),

    THREE_LAYER_RELATION(
            "원국·대운·세운 3층 관계"
    );

    private final String koreanName;

    AnnualLuckContextAxis(
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

    public boolean decadeInteraction() {

        return this == DECADE_INTERACTION;
    }

    public boolean transformation() {

        return this == TRANSFORMATION;
    }

    public boolean threeLayerRelation() {

        return this == THREE_LAYER_RELATION;
    }
}