package com.dajeom.type;

/**
 * 정밀화 10차-9.
 *
 * 핵심 십성 Pattern을 다시 검증하는
 * 네 개의 독립 분석축.
 */
public enum TenGodCrossValidationFactorType {

    STRUCTURE(
            "격국"
    ),

    DAY_MASTER_STRENGTH(
            "신강·신약"
    ),

    USEFUL_GOD(
            "용신"
    ),

    TRANSFORMATION(
            "합화"
    );

    private final String koreanName;

    TenGodCrossValidationFactorType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean structure() {

        return this == STRUCTURE;
    }

    public boolean dayMasterStrength() {

        return this == DAY_MASTER_STRENGTH;
    }

    public boolean usefulGod() {

        return this == USEFUL_GOD;
    }

    public boolean transformation() {

        return this == TRANSFORMATION;
    }
}