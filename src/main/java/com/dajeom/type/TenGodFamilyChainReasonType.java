package com.dajeom.type;

/**
 * 정밀화 10차-7.
 *
 * Family 상호작용 및 연쇄구조 판정 근거.
 *
 * 점수 합산용이 아니다.
 */
public enum TenGodFamilyChainReasonType {

    /*
     * =================================================
     * Source
     * =================================================
     */

    SOURCE_FAMILY_PRESENT(
            "출발 십성 Family가 원국에 존재합니다."
    ),

    SOURCE_FAMILY_MEANINGFUL(
            "출발 십성 Family의 세력이 유의미합니다."
    ),

    SOURCE_FAMILY_STRONG(
            "출발 십성 Family의 세력이 강합니다."
    ),

    SOURCE_VISIBLE(
            "출발 십성 Family가 천간에 투출되어 있습니다."
    ),

    SOURCE_MONTH_PRESENT(
            "출발 십성 Family가 월주 또는 월지에 존재합니다."
    ),

    /*
     * =================================================
     * Target
     * =================================================
     */

    TARGET_FAMILY_PRESENT(
            "대상 십성 Family가 원국에 존재합니다."
    ),

    TARGET_FAMILY_MEANINGFUL(
            "대상 십성 Family의 세력이 유의미합니다."
    ),

    TARGET_FAMILY_STRONG(
            "대상 십성 Family의 세력이 강합니다."
    ),

    TARGET_VISIBLE(
            "대상 십성 Family가 천간에 투출되어 있습니다."
    ),

    TARGET_MONTH_PRESENT(
            "대상 십성 Family가 월주 또는 월지에 존재합니다."
    ),

    /*
     * =================================================
     * 관계
     * =================================================
     */

    GENERATION_RELATION(
            "두 십성 Family가 생 관계로 연결됩니다."
    ),

    CONTROL_RELATION(
            "두 십성 Family가 극 관계로 연결됩니다."
    ),

    SOURCE_EXCEEDS_TARGET(
            "출발 Family의 세력이 대상 Family보다 강합니다."
    ),

    TARGET_EXCEEDS_SOURCE(
            "대상 Family의 세력이 출발 Family보다 강합니다."
    ),

    SIMILAR_POWER(
            "두 Family의 세력이 서로 비슷한 범위에 있습니다."
    ),

    /*
     * =================================================
     * Interaction 수준
     * =================================================
     */

    REFERENCE_INTERACTION(
            "Family 관계가 참고 수준으로 존재합니다."
    ),

    MEANINGFUL_INTERACTION(
            "Family 관계가 유의미한 수준으로 형성되어 있습니다."
    ),

    STRONG_INTERACTION(
            "Family 관계가 강하게 형성되어 있습니다."
    ),

    /*
     * =================================================
     * 연쇄
     * =================================================
     */

    FULL_CHAIN_PRESENT(
            "연쇄에 필요한 모든 Family 관계가 존재합니다."
    ),

    ALL_CHAIN_INTERACTIONS_MEANINGFUL(
            "연쇄를 구성하는 모든 관계가 유의미한 수준 이상입니다."
    ),

    ALL_CHAIN_INTERACTIONS_STRONG(
            "연쇄를 구성하는 모든 관계가 강한 수준입니다."
    ),

    CHAIN_HAS_VISIBLE_FAMILY(
            "연쇄에 천간 투출된 십성 Family가 포함되어 있습니다."
    ),

    CHAIN_HAS_MONTH_ANCHOR(
            "연쇄에 월주 또는 월지에 자리한 십성 Family가 포함되어 있습니다."
    );

    private final String koreanDescription;

    TenGodFamilyChainReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}