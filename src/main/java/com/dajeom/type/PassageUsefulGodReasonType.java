package com.dajeom.type;

/**
 * 정밀화 7차-6.
 *
 * 통관용신 후보의 내부 선정 근거.
 *
 * 사용자에게 바로 출력하는 해석 문장이 아니라
 * Explainability Evidence다.
 */
public enum PassageUsefulGodReasonType {

    /*
     * =================================================
     * 7-1 통관용신법 적용도
     * =================================================
     */

    PASSAGE_METHOD_CORE(
            "통관용신법 핵심 적용"
    ),

    PASSAGE_METHOD_ACTIVE(
            "통관용신법 적용"
    ),

    PASSAGE_METHOD_CONDITIONAL(
            "통관용신법 조건부 적용"
    ),

    /*
     * =================================================
     * 통관 필요도
     * =================================================
     */

    CRITICAL_PASSAGE_NEED(
            "매우 높은 통관 필요"
    ),

    HIGH_PASSAGE_NEED(
            "높은 통관 필요"
    ),

    MODERATE_PASSAGE_NEED(
            "의미 있는 통관 필요"
    ),

    LOW_PASSAGE_NEED(
            "낮은 통관 필요"
    ),

    /*
     * =================================================
     * 6-7 원본 후보
     * =================================================
     */

    SOURCE_PRIMARY(
            "6-7 통관 1순위 후보"
    ),

    SOURCE_SECONDARY(
            "6-7 통관 보조 후보"
    ),

    SOURCE_CONDITIONAL(
            "6-7 통관 조건부 후보"
    ),

    /*
     * =================================================
     * 실제 통관 상태
     * =================================================
     */

    BRIDGE_ABSENT_OR_WEAK(
            "통관 오행이 실제로 부족"
    ),

    BRIDGE_ALREADY_FUNCTIONING(
            "통관 오행이 이미 일정 수준 작동"
    ),

    DAY_MASTER_DISADVANTAGED(
            "일간이 상극 관계에서 직접 불리"
    ),

    MULTIPLE_MEANINGFUL_PASSAGE_EVIDENCES(
            "복수 통관 병목에 동일 오행이 관여"
    ),

    HIGH_PRIORITY_CONFLICT_ADDRESSED(
            "높은 우선도의 상극 병목을 통관"
    ),

    /*
     * =================================================
     * 병약·조후 수렴
     * =================================================
     */

    REMEDY_ALIGNMENT(
            "병약 후보와 통관 방향 일치"
    ),

    CLIMATE_ALIGNMENT(
            "조후 후보와 통관 방향 일치"
    ),

    REMEDY_AND_CLIMATE_ALIGNMENT(
            "병약·조후 모두 통관 방향과 일치"
    ),

    /*
     * =================================================
     * 충돌 / 과다
     * =================================================
     */

    SOURCE_PASSAGE_CONFLICT(
            "통관 후보에 병약·조후 충돌 존재"
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
            "일간 강약 방향이 통관 후보를 지지"
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
            "격국 구조가 통관 후보를 지지"
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
     * 7-6 최종 역할
     * =================================================
     */

    PRIMARY_BY_HIGH_PASSAGE_NEED(
            "높은 통관 필요로 1순위"
    ),

    PRIMARY_BY_DAY_MASTER_PROTECTION(
            "일간 직접 불리 구조 완화로 1순위"
    ),

    PRIMARY_BY_CROSS_METHOD_ALIGNMENT(
            "다른 용신 방향과 수렴하여 1순위"
    ),

    PRIMARY_PRESERVED(
            "6-7 1순위 유지"
    ),

    PRIMARY_DOWNGRADED(
            "6-7 1순위에서 하향"
    ),

    CANDIDATE_DEFERRED_BY_CONFLICT(
            "구조 충돌로 조건부 처리"
    ),

    NO_MEANINGFUL_PASSAGE_EVIDENCE(
            "의미 있는 통관용신 근거 없음"
    );

    private final String koreanName;

    PassageUsefulGodReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean alignmentReason() {

        return this == REMEDY_ALIGNMENT
                || this == CLIMATE_ALIGNMENT
                || this == REMEDY_AND_CLIMATE_ALIGNMENT
                || this == PRIMARY_BY_CROSS_METHOD_ALIGNMENT;
    }

    public boolean conflictReason() {

        return this == SOURCE_PASSAGE_CONFLICT
                || this == ELEMENT_SATURATED
                || this == STRENGTH_STRAINED
                || this == STRENGTH_CONTRADICTORY
                || this == STRUCTURE_STRAINED
                || this == STRUCTURE_CONTRADICTORY
                || this == CANDIDATE_DEFERRED_BY_CONFLICT;
    }
}