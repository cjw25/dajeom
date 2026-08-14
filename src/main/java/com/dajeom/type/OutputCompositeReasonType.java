package com.dajeom.type;

/**
 * 정밀화 10차-3.
 *
 * 식상 복합구조 판정 근거.
 *
 * =================================================
 *
 * 점수 합산용이 아니다.
 *
 * Pattern이 어떤 구조적 이유로
 * 형성되었는지 추적하기 위한 Evidence다.
 */
public enum OutputCompositeReasonType {

    /*
     * =================================================
     * 식신
     * =================================================
     */

    EATING_GOD_PRESENT(
            "식신이 원국에 존재합니다."
    ),

    EATING_GOD_VISIBLE(
            "식신이 천간에 투출되어 있습니다."
    ),

    EATING_GOD_MONTH_PRESENT(
            "식신이 월주 또는 월지에 존재합니다."
    ),

    EATING_GOD_MEANINGFUL_POWER(
            "식신 세력이 유의미한 수준입니다."
    ),

    EATING_GOD_DOMINATES_OUTPUT(
            "식신 세력이 상관 세력보다 우세합니다."
    ),

    /*
     * =================================================
     * 상관
     * =================================================
     */

    HURTING_OFFICER_PRESENT(
            "상관이 원국에 존재합니다."
    ),

    HURTING_OFFICER_VISIBLE(
            "상관이 천간에 투출되어 있습니다."
    ),

    HURTING_OFFICER_MONTH_PRESENT(
            "상관이 월주 또는 월지에 존재합니다."
    ),

    HURTING_OFFICER_MEANINGFUL_POWER(
            "상관 세력이 유의미한 수준입니다."
    ),

    HURTING_OFFICER_DOMINATES_OUTPUT(
            "상관 세력이 식신 세력보다 우세합니다."
    ),

    /*
     * =================================================
     * 식상 전체
     * =================================================
     */

    OUTPUT_FAMILY_MEANINGFUL(
            "식신·상관 전체 세력이 유의미합니다."
    ),

    OUTPUT_FAMILY_STRONG(
            "식신·상관 전체 세력이 강합니다."
    ),

    MULTIPLE_OUTPUT_SOURCES(
            "식상 source가 원국 여러 곳에 분포합니다."
    ),

    EATING_GOD_AND_HURTING_OFFICER_COEXIST(
            "식신과 상관이 함께 존재합니다."
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
     * 인성
     * =================================================
     */

    RESOURCE_PRESENT(
            "인성이 원국에 존재합니다."
    ),

    RESOURCE_APPROACHES_OUTPUT_POWER(
            "인성 세력이 식상 세력에 근접합니다."
    ),

    RESOURCE_EXCEEDS_OUTPUT_POWER(
            "인성 세력이 식상 세력보다 강합니다."
    ),

    /*
     * =================================================
     * 관살
     * =================================================
     *
     * 10-3에서는 관살 존재를 Context로만 보존한다.
     *
     * 상관견관/식신제살 판정은 하지 않는다.
     */

    OFFICER_PRESENT(
            "관살이 원국에 존재합니다."
    ),

    OFFICER_AND_OUTPUT_COEXIST(
            "식상과 관살이 동시에 존재합니다."
    ),

    /*
     * =================================================
     * Pattern
     * =================================================
     */

    OUTPUT_DEVELOPED_PATTERN(
            "식상 전체가 원국에서 발달한 구조입니다."
    ),

    EATING_GOD_CENTERED_PATTERN(
            "식상 구조에서 식신이 중심 역할을 합니다."
    ),

    HURTING_OFFICER_CENTERED_PATTERN(
            "식상 구조에서 상관이 중심 역할을 합니다."
    ),

    MIXED_OUTPUT_PATTERN(
            "식신과 상관이 동시에 작동하는 혼재 구조입니다."
    ),

    WEAK_DAY_MASTER_EXCESSIVE_DRAIN_PATTERN(
            "신약 방향의 일간에서 식상 소모가 크게 형성되어 있습니다."
    ),

    RESOURCE_CONSTRAINED_OUTPUT_PATTERN(
            "인성 세력이 식상 작용을 의미 있게 제약할 수 있는 구조입니다."
    );

    private final String koreanDescription;

    OutputCompositeReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}