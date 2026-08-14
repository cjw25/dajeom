package com.dajeom.type;

/**
 * 정밀화 10차-2.
 *
 * 비겁 복합구조 판정 근거.
 *
 * 점수 합산용이 아니다.
 */
public enum CompanionCompositeReasonType {

    /*
     * =================================================
     * 비견
     * =================================================
     */

    COMPANION_PRESENT(
            "비견이 원국에 존재합니다."
    ),

    COMPANION_VISIBLE(
            "비견이 천간에 투출되어 있습니다."
    ),

    COMPANION_MONTH_PRESENT(
            "비견이 월주 또는 월지에 존재합니다."
    ),

    COMPANION_MEANINGFUL_POWER(
            "비견 세력이 유의미한 수준입니다."
    ),

    /*
     * =================================================
     * 겁재
     * =================================================
     */

    ROB_WEALTH_PRESENT(
            "겁재가 원국에 존재합니다."
    ),

    ROB_WEALTH_VISIBLE(
            "겁재가 천간에 투출되어 있습니다."
    ),

    ROB_WEALTH_MONTH_PRESENT(
            "겁재가 월주 또는 월지에 존재합니다."
    ),

    ROB_WEALTH_MEANINGFUL_POWER(
            "겁재 세력이 유의미한 수준입니다."
    ),

    /*
     * =================================================
     * 비겁 전체
     * =================================================
     */

    COMPANION_FAMILY_MEANINGFUL(
            "비견·겁재 전체 세력이 유의미합니다."
    ),

    COMPANION_FAMILY_STRONG(
            "비견·겁재 전체 세력이 강합니다."
    ),

    MULTIPLE_COMPANION_SOURCES(
            "비견·겁재 source가 여러 곳에 분포합니다."
    ),

    COMPANION_AND_ROB_WEALTH_COEXIST(
            "비견과 겁재가 함께 존재합니다."
    ),

    /*
     * =================================================
     * 일간 강약
     * =================================================
     */

    DAY_MASTER_WEAK_SIDE(
            "일간이 신약 방향에 있습니다."
    ),

    DAY_MASTER_BALANCED(
            "일간이 균형 구간에 있습니다."
    ),

    DAY_MASTER_STRONG_SIDE(
            "일간이 신강 방향에 있습니다."
    ),

    /*
     * =================================================
     * 재성 관계
     * =================================================
     */

    WEALTH_PRESENT(
            "재성이 원국에 존재합니다."
    ),

    WEALTH_MEANINGFUL(
            "재성 세력이 유의미합니다."
    ),

    COMPANION_APPROACHES_WEALTH_POWER(
            "비겁 세력이 재성 세력에 근접합니다."
    ),

    COMPANION_EXCEEDS_WEALTH_POWER(
            "비겁 세력이 재성 세력보다 강합니다."
    ),

    ROB_WEALTH_WITH_WEALTH(
            "겁재와 재성이 동시에 유의미하게 존재합니다."
    ),

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    SUPPORTIVE_PATTERN(
            "신약한 일간을 비겁이 보조하는 구조입니다."
    ),

    CLUSTER_PATTERN(
            "비겁이 여러 source와 세력으로 군집한 구조입니다."
    ),

    COMPETITION_PATTERN(
            "겁재의 경쟁성이 구조적으로 의미 있는 수준입니다."
    ),

    WEALTH_CONTEST_PATTERN(
            "비겁과 재성의 상대 세력이 재성 쟁탈 구조를 형성합니다."
    ),

    EXCESSIVE_PATTERN(
            "신강한 일간에 비겁 세력이 추가로 강하게 형성되어 있습니다."
    ),

    MIXED_PATTERN(
            "비견과 겁재가 동시에 유의미하게 존재합니다."
    );

    private final String koreanDescription;

    CompanionCompositeReasonType(
            String koreanDescription
    ) {

        this.koreanDescription = koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}