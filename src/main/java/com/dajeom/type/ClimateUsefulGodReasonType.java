package com.dajeom.type;

/**
 * 정밀화 7차-3.
 *
 * 조후용신 후보의 내부 선정 근거.
 *
 * 최종 사용자 해석 문장이 아니라
 * Explainability Evidence다.
 */
public enum ClimateUsefulGodReasonType {

    /*
     * =================================================
     * 7-1 조후용신법 적용도
     * =================================================
     */

    CLIMATE_METHOD_CORE(
            "조후용신법 핵심 적용"
    ),

    CLIMATE_METHOD_ACTIVE(
            "조후용신법 적용"
    ),

    CLIMATE_METHOD_CONDITIONAL(
            "조후용신법 조건부 적용"
    ),

    /*
     * =================================================
     * 조후 필요도
     * =================================================
     */

    CRITICAL_CLIMATE_NEED(
            "매우 높은 조후 필요"
    ),

    HIGH_CLIMATE_NEED(
            "높은 조후 필요"
    ),

    MODERATE_CLIMATE_NEED(
            "중간 조후 필요"
    ),

    LOW_CLIMATE_NEED(
            "낮은 조후 필요"
    ),

    /*
     * =================================================
     * 6-3 후보
     * =================================================
     */

    SOURCE_PRIMARY(
            "6-3 조후 1순위 후보"
    ),

    SOURCE_SECONDARY(
            "6-3 조후 보조 후보"
    ),

    SOURCE_CONDITIONAL(
            "6-3 조후 조건부 후보"
    ),

    /*
     * =================================================
     * 한난·조습 관계
     * =================================================
     */

    DUAL_AXIS_ALIGNED(
            "한난·조습 보정 방향 정렬"
    ),

    DUAL_AXIS_COMPETING(
            "한난·조습 보정 방향 경쟁"
    ),

    CLIMATE_BRIDGE_CANDIDATE(
            "한난·조습 충돌 중재 후보"
    ),

    SOURCE_CLIMATE_CONFLICT(
            "다른 조후축 악화 가능"
    ),

    /*
     * =================================================
     * 현재 오행량
     * =================================================
     */

    ELEMENT_SCARCE(
            "현재 해당 오행 세력이 부족"
    ),

    ELEMENT_MODERATE(
            "현재 해당 오행 세력이 보통"
    ),

    ELEMENT_HIGH(
            "현재 해당 오행 세력이 많음"
    ),

    ELEMENT_SATURATED(
            "현재 해당 오행 세력이 과다"
    ),

    /*
     * =================================================
     * 6-9 신강신약
     * =================================================
     */

    STRENGTH_SUPPORTED(
            "신강신약 방향이 조후 후보를 지지"
    ),

    STRENGTH_NEUTRAL(
            "신강신약과 직접 충돌 없음"
    ),

    STRENGTH_STRAINED(
            "일간 강약에 부담"
    ),

    STRENGTH_CONTRADICTORY(
            "일간 강약 핵심 방향과 충돌"
    ),

    /*
     * =================================================
     * 6-9 격국
     * =================================================
     */

    STRUCTURE_SUPPORTED(
            "격국 구조가 조후 후보를 지지"
    ),

    STRUCTURE_NEUTRAL(
            "격국과 직접 충돌 없음"
    ),

    STRUCTURE_STRAINED(
            "격국 구조에 부담"
    ),

    STRUCTURE_CONTRADICTORY(
            "격국 핵심 구조와 충돌"
    ),

    /*
     * =================================================
     * 최종 7-3 조정
     * =================================================
     */

    PRIMARY_PRESERVED(
            "조후 1순위 유지"
    ),

    PRIMARY_DOWNGRADED(
            "조후 1순위에서 하향"
    ),

    CANDIDATE_DEFERRED_BY_CONFLICT(
            "구조 충돌로 조건부 처리"
    );

    private final String koreanName;

    ClimateUsefulGodReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean conflictReason() {

        return this == SOURCE_CLIMATE_CONFLICT
                || this == ELEMENT_SATURATED
                || this == STRENGTH_STRAINED
                || this == STRENGTH_CONTRADICTORY
                || this == STRUCTURE_STRAINED
                || this == STRUCTURE_CONTRADICTORY;
    }

    public boolean crossValidationReason() {

        return this == STRENGTH_SUPPORTED
                || this == STRENGTH_NEUTRAL
                || this == STRENGTH_STRAINED
                || this == STRENGTH_CONTRADICTORY
                || this == STRUCTURE_SUPPORTED
                || this == STRUCTURE_NEUTRAL
                || this == STRUCTURE_STRAINED
                || this == STRUCTURE_CONTRADICTORY;
    }
}