package com.dajeom.type;

/**
 * 정밀화 7차-8.
 *
 * 최종 용신 통합 후보의 선정 근거.
 *
 * 사용자에게 그대로 노출하는 최종 해설 문장이 아니라
 * 내부 Explainability Evidence다.
 */
public enum UsefulGodIntegrationReasonType {

    /*
     * =================================================
     * CORE 수렴
     * =================================================
     */

    CORE_METHOD_PRIMARY_PRESENT(
            "핵심 용신법의 1순위 후보"
    ),

    MULTIPLE_CORE_METHODS_CONVERGE(
            "복수 핵심 용신법이 동일 오행으로 수렴"
    ),

    CORE_PRIMARY_WITH_ADDITIONAL_SUPPORT(
            "핵심 용신법 1순위에 다른 방법의 지지가 추가됨"
    ),

    /*
     * =================================================
     * 일반 수렴
     * =================================================
     */

    MULTIPLE_METHODS_CONVERGE(
            "복수 용신법이 동일 오행을 지지"
    ),

    SINGLE_METHOD_SUPPORT(
            "하나의 유효 용신법에서 후보로 선택"
    ),

    /*
     * =================================================
     * 종격
     * =================================================
     */

    FOLLOW_STRUCTURE_MODE(
            "실제 종격 주격 모드"
    ),

    FOLLOW_CORE_PRIMARY(
            "종격용신법 핵심 1순위"
    ),

    FOLLOW_CORE_SUPPORT(
            "종격용신법 핵심 보조 방향"
    ),

    FOLLOW_FLOW_BREAKING_ELEMENT(
            "종격 흐름을 깨는 오행"
    ),

    /*
     * =================================================
     * 충돌
     * =================================================
     */

    CORE_METHOD_DIRECT_CONFLICT(
            "CORE 용신법에 직접 충돌 근거 존재"
    ),

    ACTIVE_METHOD_DIRECT_CONFLICT(
            "ACTIVE 용신법에 직접 충돌 근거 존재"
    ),

    CORE_METHOD_CONDITIONAL_WARNING(
            "CORE 용신법에서 조건부 판정 존재"
    ),

    CONDITIONAL_EVIDENCE_PRESENT(
            "조건부 용신 Evidence 존재"
    ),

    /*
     * =================================================
     * 오행 세력
     * =================================================
     */

    ELEMENT_SATURATION_CAUTION(
            "현재 해당 오행 세력이 매우 강함"
    ),

    /*
     * =================================================
     * 최종 역할
     * =================================================
     */

    PRIMARY_BY_CORE_CONVERGENCE(
            "핵심 용신법 수렴으로 주용신 후보"
    ),

    PRIMARY_BY_CORE_METHOD(
            "핵심 용신법의 명확한 지지로 주용신 후보"
    ),

    PRIMARY_BY_FOLLOW_STRUCTURE(
            "종격 핵심 흐름으로 주용신 후보"
    ),

    SUPPORTING_BY_METHOD_CONVERGENCE(
            "복수 방법 수렴으로 보조 용신 후보"
    ),

    SUPPORTING_BY_SINGLE_METHOD(
            "단일 방법의 유효 지지로 보조 용신 후보"
    ),

    SUPPORTING_AFTER_CAUTION(
            "강한 수렴은 있으나 주의 근거로 보조 후보"
    ),

    DEFERRED_BY_CORE_CONFLICT(
            "핵심 용신법 충돌로 판단 보류"
    ),

    DEFERRED_BY_ACTIVE_CONFLICT(
            "유효 용신법 충돌로 판단 보류"
    ),

    DEFERRED_BY_CONDITIONAL_EVIDENCE(
            "조건부 Evidence로 판단 보류"
    ),

    DEFERRED_BY_SATURATION(
            "현재 오행 과다로 판단 보류"
    ),

    NO_ACTIVE_USEFUL_GOD_EVIDENCE(
            "현재 활성 용신 근거 없음"
    );

    private final String koreanName;

    UsefulGodIntegrationReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean conflictReason() {

        return this == CORE_METHOD_DIRECT_CONFLICT
                || this == ACTIVE_METHOD_DIRECT_CONFLICT
                || this == CORE_METHOD_CONDITIONAL_WARNING
                || this == FOLLOW_FLOW_BREAKING_ELEMENT
                || this == DEFERRED_BY_CORE_CONFLICT
                || this == DEFERRED_BY_ACTIVE_CONFLICT;
    }

    public boolean convergenceReason() {

        return this == MULTIPLE_CORE_METHODS_CONVERGE
                || this == CORE_PRIMARY_WITH_ADDITIONAL_SUPPORT
                || this == MULTIPLE_METHODS_CONVERGE
                || this == PRIMARY_BY_CORE_CONVERGENCE
                || this == SUPPORTING_BY_METHOD_CONVERGENCE;
    }
}