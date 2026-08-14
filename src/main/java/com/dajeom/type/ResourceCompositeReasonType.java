package com.dajeom.type;

/**
 * 정밀화 10차-6.
 *
 * 인성 복합구조 판정 근거.
 *
 * 점수 합산용이 아니다.
 */
public enum ResourceCompositeReasonType {

    /*
     * =================================================
     * 정인
     * =================================================
     */

    DIRECT_RESOURCE_PRESENT(
            "정인이 원국에 존재합니다."
    ),

    DIRECT_RESOURCE_VISIBLE(
            "정인이 천간에 투출되어 있습니다."
    ),

    DIRECT_RESOURCE_MONTH_PRESENT(
            "정인이 월주 또는 월지에 존재합니다."
    ),

    DIRECT_RESOURCE_MEANINGFUL_POWER(
            "정인 세력이 유의미한 수준입니다."
    ),

    DIRECT_RESOURCE_DOMINATES(
            "정인 세력이 편인 세력보다 우세합니다."
    ),

    /*
     * =================================================
     * 편인
     * =================================================
     */

    INDIRECT_RESOURCE_PRESENT(
            "편인이 원국에 존재합니다."
    ),

    INDIRECT_RESOURCE_VISIBLE(
            "편인이 천간에 투출되어 있습니다."
    ),

    INDIRECT_RESOURCE_MONTH_PRESENT(
            "편인이 월주 또는 월지에 존재합니다."
    ),

    INDIRECT_RESOURCE_MEANINGFUL_POWER(
            "편인 세력이 유의미한 수준입니다."
    ),

    INDIRECT_RESOURCE_DOMINATES(
            "편인 세력이 정인 세력보다 우세합니다."
    ),

    /*
     * =================================================
     * 인성 전체
     * =================================================
     */

    RESOURCE_FAMILY_MEANINGFUL(
            "정인·편인 전체 세력이 유의미합니다."
    ),

    RESOURCE_FAMILY_STRONG(
            "정인·편인 전체 세력이 강합니다."
    ),

    MULTIPLE_RESOURCE_SOURCES(
            "인성 source가 원국 여러 곳에 분포합니다."
    ),

    DIRECT_AND_INDIRECT_RESOURCE_COEXIST(
            "정인과 편인이 함께 존재합니다."
    ),

    /*
     * =================================================
     * 신강·신약
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
     * 식상 Context
     * =================================================
     */

    OUTPUT_PRESENT(
            "식상이 원국에 존재합니다."
    ),

    RESOURCE_APPROACHES_OUTPUT_POWER(
            "인성 세력이 식상 세력에 근접합니다."
    ),

    RESOURCE_EXCEEDS_OUTPUT_POWER(
            "인성 세력이 식상 세력보다 강합니다."
    ),

    /*
     * =================================================
     * 관살 Context
     * =================================================
     */

    OFFICER_PRESENT(
            "관살이 원국에 존재합니다."
    ),

    OFFICER_APPROACHES_RESOURCE_POWER(
            "관살 세력이 인성 세력과 비교해 충분한 규모로 존재합니다."
    ),

    OFFICER_EXCEEDS_RESOURCE_POWER(
            "관살 세력이 인성 세력보다 강합니다."
    ),

    /*
     * =================================================
     * 재성 Context
     * =================================================
     */

    WEALTH_PRESENT(
            "재성이 원국에 존재합니다."
    ),

    WEALTH_APPROACHES_RESOURCE_POWER(
            "재성 세력이 인성 세력에 근접합니다."
    ),

    WEALTH_EXCEEDS_RESOURCE_POWER(
            "재성 세력이 인성 세력보다 강합니다."
    ),

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    RESOURCE_DEVELOPED_PATTERN(
            "인성 전체가 원국에서 발달한 구조입니다."
    ),

    DIRECT_RESOURCE_CENTERED_PATTERN(
            "인성 구조에서 정인이 중심 역할을 합니다."
    ),

    INDIRECT_RESOURCE_CENTERED_PATTERN(
            "인성 구조에서 편인이 중심 역할을 합니다."
    ),

    MIXED_RESOURCE_PATTERN(
            "정인과 편인이 동시에 작동하는 혼재 구조입니다."
    ),

    WEAK_DAY_MASTER_RESOURCE_SUPPORT_PATTERN(
            "신약 방향의 일간을 인성이 보조하는 구조입니다."
    ),

    STRONG_DAY_MASTER_EXCESSIVE_RESOURCE_PATTERN(
            "신강 방향의 일간에 인성 세력이 추가로 강하게 형성되어 있습니다."
    ),

    OUTPUT_CONSTRAINT_CONTEXT_PATTERN(
            "인성과 식상이 함께 존재해 식상 제약 관계를 검토할 수 있습니다."
    ),

    OFFICER_GENERATION_CONTEXT_PATTERN(
            "관살과 인성이 함께 존재해 관인·살인 연쇄를 검토할 수 있습니다."
    ),

    WEALTH_PRESSURE_CONTEXT_PATTERN(
            "재성과 인성이 함께 존재해 재극인 관계를 검토할 수 있습니다."
    );

    private final String koreanDescription;

    ResourceCompositeReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}