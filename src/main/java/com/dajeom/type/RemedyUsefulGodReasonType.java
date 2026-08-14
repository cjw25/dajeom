package com.dajeom.type;

/**
 * 정밀화 7차-5.
 *
 * 병약용신 후보 선정의 내부 근거.
 *
 * 최종 사용자 설명문 자체가 아니라
 * Explainability Evidence다.
 */
public enum RemedyUsefulGodReasonType {

    /*
     * =================================================
     * 7-1 병약용신법 적용도
     * =================================================
     */

    REMEDY_METHOD_CORE(
            "병약용신법 핵심 적용"
    ),

    REMEDY_METHOD_ACTIVE(
            "병약용신법 적용"
    ),

    REMEDY_METHOD_CONDITIONAL(
            "병약용신법 조건부 적용"
    ),

    /*
     * =================================================
     * 전체 병 구조
     * =================================================
     */

    CRITICAL_PATHOLOGY_PRESENT(
            "매우 강한 병 구조 존재"
    ),

    HIGH_PATHOLOGY_PRESENT(
            "강한 병 구조 존재"
    ),

    MODERATE_PATHOLOGY_PRESENT(
            "의미 있는 병 구조 존재"
    ),

    LOW_PATHOLOGY_PRESENT(
            "약한 병 구조 존재"
    ),

    /*
     * =================================================
     * 6-5 원본 후보
     * =================================================
     */

    SOURCE_PRIMARY(
            "6-5 약 1순위 후보"
    ),

    SOURCE_SECONDARY(
            "6-5 약 보조 후보"
    ),

    SOURCE_CONDITIONAL(
            "6-5 약 조건부 후보"
    ),

    /*
     * =================================================
     * 병 구조 해결 범위
     * =================================================
     */

    ADDRESSES_SEVERE_PATHOLOGY(
            "강한 병 구조를 직접 완화"
    ),

    ADDRESSES_MULTIPLE_PATHOLOGIES(
            "복수 병 구조를 동시에 완화"
    ),

    ADDRESSES_SINGLE_MEANINGFUL_PATHOLOGY(
            "의미 있는 병 구조 하나를 완화"
    ),

    NO_MEANINGFUL_REMEDY_EVIDENCE(
            "의미 있는 병 구조 완화 근거 부족"
    ),

    UNRESOLVED_MEANINGFUL_PATHOLOGY_REMAINS(
            "아직 해결되지 않은 의미 있는 병 구조 존재"
    ),

    /*
     * =================================================
     * 충돌 / 과다
     * =================================================
     */

    SOURCE_REMEDY_CONFLICT(
            "다른 병 구조 악화 가능"
    ),

    ELEMENT_SATURATED(
            "현재 해당 오행 세력이 과다"
    ),

    /*
     * =================================================
     * 신강신약
     * =================================================
     */

    STRENGTH_SUPPORTED(
            "일간 강약 방향이 병약 후보를 지지"
    ),

    STRENGTH_NEUTRAL(
            "일간 강약과 직접 충돌 없음"
    ),

    STRENGTH_STRAINED(
            "일간 강약에 부담"
    ),

    STRENGTH_CONTRADICTORY(
            "일간 강약 핵심 방향과 충돌"
    ),

    /*
     * =================================================
     * 격국
     * =================================================
     */

    STRUCTURE_SUPPORTED(
            "격국 구조가 병약 후보를 지지"
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
     * 최종 7-5 조정
     * =================================================
     */

    PRIMARY_BY_SEVERE_REMEDY(
            "강한 병 구조 직접 완화로 1순위"
    ),

    PRIMARY_BY_MULTI_REMEDY(
            "복수 병 구조 동시 완화로 1순위"
    ),

    PRIMARY_PRESERVED(
            "6-5 1순위 유지"
    ),

    PRIMARY_DOWNGRADED(
            "6-5 1순위에서 하향"
    ),

    CANDIDATE_DEFERRED_BY_CONFLICT(
            "충돌 근거로 조건부 처리"
    );

    private final String koreanName;

    RemedyUsefulGodReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean pathologyReason() {

        return this == ADDRESSES_SEVERE_PATHOLOGY
                || this == ADDRESSES_MULTIPLE_PATHOLOGIES
                || this == ADDRESSES_SINGLE_MEANINGFUL_PATHOLOGY
                || this == NO_MEANINGFUL_REMEDY_EVIDENCE
                || this == UNRESOLVED_MEANINGFUL_PATHOLOGY_REMAINS;
    }

    public boolean conflictReason() {

        return this == SOURCE_REMEDY_CONFLICT
                || this == ELEMENT_SATURATED
                || this == STRENGTH_STRAINED
                || this == STRENGTH_CONTRADICTORY
                || this == STRUCTURE_STRAINED
                || this == STRUCTURE_CONTRADICTORY
                || this == CANDIDATE_DEFERRED_BY_CONFLICT;
    }
}