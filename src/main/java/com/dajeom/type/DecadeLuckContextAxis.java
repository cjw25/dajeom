package com.dajeom.type;

/**
 * 정밀화 13차-6.
 *
 * 대운 Context를 구성하는 독립적인 분석 축.
 *
 * 중요:
 *
 * 여러 Axis가 동시에 서로 다른 상태를
 * 가질 수 있다.
 *
 * 예:
 *
 * USEFUL_GOD = SUPPORTED
 * STRUCTURE  = QUALIFIED
 * RELATION   = CONFLICTED
 *
 * 이 경우 하나를 삭제하거나
 * 평균 점수로 합치지 않는다.
 */
public enum DecadeLuckContextAxis {

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

    RELATION(
            "원국 관계"
    ),

    TRANSFORMATION(
            "합화·기능변화"
    );

    private final String koreanName;

    DecadeLuckContextAxis(
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

    public boolean relation() {

        return this == RELATION;
    }

    public boolean transformation() {

        return this == TRANSFORMATION;
    }
}