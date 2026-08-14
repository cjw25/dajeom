package com.dajeom.type;

/**
 * 정밀화 10차-4.
 *
 * 재성 복합구조 판정 근거.
 *
 * =================================================
 *
 * 점수 합산용 enum이 아니다.
 *
 * 각 Pattern이 어떤 구조로부터
 * 만들어졌는지를 보존하기 위한 Evidence다.
 */
public enum WealthCompositeReasonType {

    /*
     * =================================================
     * 정재
     * =================================================
     */

    DIRECT_WEALTH_PRESENT(
            "정재가 원국에 존재합니다."
    ),

    DIRECT_WEALTH_VISIBLE(
            "정재가 천간에 투출되어 있습니다."
    ),

    DIRECT_WEALTH_MONTH_PRESENT(
            "정재가 월주 또는 월지에 존재합니다."
    ),

    DIRECT_WEALTH_MEANINGFUL_POWER(
            "정재 세력이 유의미한 수준입니다."
    ),

    DIRECT_WEALTH_DOMINATES(
            "정재 세력이 편재 세력보다 우세합니다."
    ),

    /*
     * =================================================
     * 편재
     * =================================================
     */

    INDIRECT_WEALTH_PRESENT(
            "편재가 원국에 존재합니다."
    ),

    INDIRECT_WEALTH_VISIBLE(
            "편재가 천간에 투출되어 있습니다."
    ),

    INDIRECT_WEALTH_MONTH_PRESENT(
            "편재가 월주 또는 월지에 존재합니다."
    ),

    INDIRECT_WEALTH_MEANINGFUL_POWER(
            "편재 세력이 유의미한 수준입니다."
    ),

    INDIRECT_WEALTH_DOMINATES(
            "편재 세력이 정재 세력보다 우세합니다."
    ),

    /*
     * =================================================
     * 재성 전체
     * =================================================
     */

    WEALTH_FAMILY_MEANINGFUL(
            "정재·편재 전체 세력이 유의미합니다."
    ),

    WEALTH_FAMILY_STRONG(
            "정재·편재 전체 세력이 강합니다."
    ),

    MULTIPLE_WEALTH_SOURCES(
            "재성 source가 원국 여러 곳에 분포합니다."
    ),

    DIRECT_AND_INDIRECT_WEALTH_COEXIST(
            "정재와 편재가 함께 존재합니다."
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
     * 비겁
     * =================================================
     */

    COMPANION_PRESENT(
            "비겁이 원국에 존재합니다."
    ),

    COMPANION_APPROACHES_WEALTH_POWER(
            "비겁 세력이 재성 세력에 근접합니다."
    ),

    COMPANION_EXCEEDS_WEALTH_POWER(
            "비겁 세력이 재성 세력보다 강합니다."
    ),

    /*
     * =================================================
     * 관살
     * =================================================
     *
     * 재생관 여부는 아직 확정하지 않는다.
     */

    OFFICER_PRESENT(
            "관살이 원국에 존재합니다."
    ),

    WEALTH_AND_OFFICER_COEXIST(
            "재성과 관살이 동시에 존재합니다."
    ),

    /*
     * =================================================
     * 인성
     * =================================================
     *
     * 재극인 여부는 아직 확정하지 않는다.
     */

    RESOURCE_PRESENT(
            "인성이 원국에 존재합니다."
    ),

    WEALTH_AND_RESOURCE_COEXIST(
            "재성과 인성이 동시에 존재합니다."
    ),

    /*
     * =================================================
     * 최종 Pattern
     * =================================================
     */

    WEALTH_DEVELOPED_PATTERN(
            "재성 전체가 원국에서 발달한 구조입니다."
    ),

    DIRECT_WEALTH_CENTERED_PATTERN(
            "재성 구조에서 정재가 중심 역할을 합니다."
    ),

    INDIRECT_WEALTH_CENTERED_PATTERN(
            "재성 구조에서 편재가 중심 역할을 합니다."
    ),

    MIXED_WEALTH_PATTERN(
            "정재와 편재가 동시에 작동하는 혼재 구조입니다."
    ),

    WEAK_DAY_MASTER_WEALTH_BURDEN_PATTERN(
            "신약 방향의 일간에 재성 부담이 크게 형성되어 있습니다."
    ),

    COMPANION_PRESSURED_WEALTH_PATTERN(
            "비겁 세력이 재성 구조에 의미 있는 압력을 주고 있습니다."
    );

    private final String koreanDescription;

    WealthCompositeReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}