package com.dajeom.type;

/**
 * 정밀화 7차-4.
 *
 * 격국용신 후보의 내부 선정 근거.
 *
 * 사용자에게 직접 출력하는 해석 문장이 아니라
 * Explainability Evidence다.
 */
public enum StructureUsefulGodReasonType {

    /*
     * =================================================
     * 7-1 격국용신법 적용도
     * =================================================
     */

    STRUCTURE_METHOD_CORE(
            "격국용신법 핵심 적용"
    ),

    STRUCTURE_METHOD_ACTIVE(
            "격국용신법 적용"
    ),

    STRUCTURE_METHOD_CONDITIONAL(
            "격국용신법 조건부 적용"
    ),

    /*
     * =================================================
     * 주격
     * =================================================
     */

    STANDARD_PRIMARY_STRUCTURE(
            "일반 내격이 주격"
    ),

    SPECIAL_PRIMARY_STRUCTURE(
            "특수격이 주격"
    ),

    FOLLOW_STRUCTURE_SUPPRESSION(
            "종격 주격이므로 일반 격국용신법 제한"
    ),

    /*
     * =================================================
     * 주격 상태
     * =================================================
     */

    STRUCTURE_ESTABLISHED(
            "주격이 안정적으로 성립"
    ),

    STRUCTURE_ESTABLISHED_WITH_STRAIN(
            "주격은 성립하지만 부담 존재"
    ),

    STRUCTURE_COMPROMISED(
            "주격에 의미 있는 손상 존재"
    ),

    STRUCTURE_UNSTABLE(
            "주격 성립이 불안정"
    ),

    STRUCTURE_SPECIAL_REVIEW(
            "특수 구조 추가 검토"
    ),

    /*
     * =================================================
     * 성격 / 파격 / 복구
     * =================================================
     */

    FORMATION_SUPPORT_ELEMENT(
            "주격 성격 요소와 일치"
    ),

    DAMAGE_ELEMENT(
            "주격 파격 요소와 일치"
    ),

    DAMAGE_REPAIR_ELEMENT(
            "주격 손상을 복구하는 오행"
    ),

    SUPPORT_AND_DAMAGE_MIXED(
            "동일 오행에 성격·파격 근거가 동시에 존재"
    ),

    REPAIR_AND_DAMAGE_MIXED(
            "동일 오행에 복구·파격 근거가 동시에 존재"
    ),

    /*
     * =================================================
     * 신강신약 교차검증
     * =================================================
     */

    STRENGTH_SUPPORTED(
            "일간 강약 방향이 후보를 지지"
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
     * 특수격
     * =================================================
     */

    SPECIAL_STRUCTURE_CONSERVATIVE_REVIEW(
            "특수격 세부 규칙 확인 전 조건부 검토"
    ),

    /*
     * =================================================
     * 최종 역할
     * =================================================
     */

    PRIMARY_BY_STRUCTURE_SUPPORT(
            "주격 성립을 직접 보조"
    ),

    PRIMARY_BY_DAMAGE_REPAIR(
            "주격 손상을 직접 보완"
    ),

    SECONDARY_BY_STRENGTH_STRAIN(
            "일간 부담으로 보조 후보로 하향"
    ),

    CONDITIONAL_BY_CONFLICT(
            "격국 또는 일간 충돌로 조건부 처리"
    ),

    NO_POSITIVE_STRUCTURE_EVIDENCE(
            "격국용신으로 사용할 적극적 근거 없음"
    );

    private final String koreanName;

    StructureUsefulGodReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean positiveStructureReason() {

        return this == FORMATION_SUPPORT_ELEMENT
                || this == DAMAGE_REPAIR_ELEMENT
                || this == PRIMARY_BY_STRUCTURE_SUPPORT
                || this == PRIMARY_BY_DAMAGE_REPAIR;
    }

    public boolean negativeStructureReason() {

        return this == DAMAGE_ELEMENT
                || this == SUPPORT_AND_DAMAGE_MIXED
                || this == REPAIR_AND_DAMAGE_MIXED
                || this == STRENGTH_STRAINED
                || this == STRENGTH_CONTRADICTORY
                || this == CONDITIONAL_BY_CONFLICT;
    }
}