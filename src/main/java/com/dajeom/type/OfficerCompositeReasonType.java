package com.dajeom.type;

/**
 * 정밀화 10차-5.
 *
 * 관살 복합구조 판정 Evidence.
 *
 * 점수 합산용이 아니다.
 */
public enum OfficerCompositeReasonType {

    /*
     * =================================================
     * 정관
     * =================================================
     */

    DIRECT_OFFICER_PRESENT(
            "정관이 원국에 존재합니다."
    ),

    DIRECT_OFFICER_VISIBLE(
            "정관이 천간에 투출되어 있습니다."
    ),

    DIRECT_OFFICER_MONTH_PRESENT(
            "정관이 월주 또는 월지에 존재합니다."
    ),

    DIRECT_OFFICER_MEANINGFUL_POWER(
            "정관 세력이 유의미한 수준입니다."
    ),

    DIRECT_OFFICER_DOMINATES(
            "정관 세력이 칠살 세력보다 우세합니다."
    ),

    /*
     * =================================================
     * 칠살
     * =================================================
     */

    SEVEN_KILLINGS_PRESENT(
            "칠살이 원국에 존재합니다."
    ),

    SEVEN_KILLINGS_VISIBLE(
            "칠살이 천간에 투출되어 있습니다."
    ),

    SEVEN_KILLINGS_MONTH_PRESENT(
            "칠살이 월주 또는 월지에 존재합니다."
    ),

    SEVEN_KILLINGS_MEANINGFUL_POWER(
            "칠살 세력이 유의미한 수준입니다."
    ),

    SEVEN_KILLINGS_DOMINATES(
            "칠살 세력이 정관 세력보다 우세합니다."
    ),

    /*
     * =================================================
     * 관살 전체
     * =================================================
     */

    OFFICER_FAMILY_MEANINGFUL(
            "정관·칠살 전체 세력이 유의미합니다."
    ),

    OFFICER_FAMILY_STRONG(
            "정관·칠살 전체 세력이 강합니다."
    ),

    MULTIPLE_OFFICER_SOURCES(
            "관살 source가 원국 여러 곳에 분포합니다."
    ),

    DIRECT_OFFICER_AND_SEVEN_KILLINGS_COEXIST(
            "정관과 칠살이 함께 존재합니다."
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

    OUTPUT_APPROACHES_OFFICER_POWER(
            "식상 세력이 관살 세력에 근접합니다."
    ),

    OUTPUT_EXCEEDS_OFFICER_POWER(
            "식상 세력이 관살 세력보다 강합니다."
    ),

    /*
     * =================================================
     * 인성 Context
     * =================================================
     */

    RESOURCE_PRESENT(
            "인성이 원국에 존재합니다."
    ),

    RESOURCE_APPROACHES_OFFICER_POWER(
            "인성 세력이 관살 세력과 비교해 충분한 규모로 존재합니다."
    ),

    RESOURCE_EXCEEDS_OFFICER_POWER(
            "인성 세력이 관살 세력보다 강합니다."
    ),

    /*
     * =================================================
     * 재성 Context
     * =================================================
     */

    WEALTH_PRESENT(
            "재성이 원국에 존재합니다."
    ),

    WEALTH_APPROACHES_OFFICER_POWER(
            "재성 세력이 관살 세력과 비교해 충분한 규모로 존재합니다."
    ),

    WEALTH_EXCEEDS_OFFICER_POWER(
            "재성 세력이 관살 세력보다 강합니다."
    ),

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    OFFICER_DEVELOPED_PATTERN(
            "관살 전체가 원국에서 발달한 구조입니다."
    ),

    DIRECT_OFFICER_CENTERED_PATTERN(
            "관살 구조에서 정관이 중심 역할을 합니다."
    ),

    SEVEN_KILLINGS_CENTERED_PATTERN(
            "관살 구조에서 칠살이 중심 역할을 합니다."
    ),

    MIXED_OFFICER_KILLINGS_PATTERN(
            "정관과 칠살이 동시에 작동하는 혼재 구조입니다."
    ),

    WEAK_DAY_MASTER_OFFICER_PRESSURE_PATTERN(
            "신약 방향의 일간에서 관살 압력이 크게 형성되어 있습니다."
    ),

    OUTPUT_CONTROL_CONTEXT_PATTERN(
            "식상과 관살이 함께 존재해 제어 관계를 검토할 수 있습니다."
    ),

    RESOURCE_MEDIATION_CONTEXT_PATTERN(
            "관살과 인성이 함께 존재해 관인·살인 연결 구조를 검토할 수 있습니다."
    ),

    WEALTH_SUPPORT_CONTEXT_PATTERN(
            "재성과 관살이 함께 존재해 재생관 구조를 검토할 수 있습니다."
    );

    private final String koreanDescription;

    OfficerCompositeReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}