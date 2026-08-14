package com.dajeom.type;

/**
 * 정밀화 7차-9.
 *
 * 희신·기신 판정의 내부 근거.
 *
 * 사용자 최종 해설 문장이 아니라
 * Explainability Evidence다.
 */
public enum UsefulGodRelationReasonType {

    /*
     * =================================================
     * 7-8 원본 역할
     * =================================================
     */

    SOURCE_PRIMARY_USEFUL_GOD(
            "7-8 주용신 후보"
    ),

    SOURCE_SUPPORTING_USEFUL_GOD(
            "7-8 보조 용신 후보"
    ),

    SOURCE_DEFERRED(
            "7-8 용신 판단 보류 후보"
    ),

    SOURCE_NOT_SELECTED(
            "7-8 미선택 오행"
    ),

    /*
     * =================================================
     * 주용신 생극 관계
     * =================================================
     */

    SAME_AS_PRIMARY_USEFUL_GOD(
            "해당 오행 자체가 주용신"
    ),

    GENERATES_PRIMARY_USEFUL_GOD(
            "주용신을 직접 생조"
    ),

    CONTROLS_PRIMARY_THREAT(
            "주용신을 극하는 오행을 제어"
    ),

    CONTROLS_PRIMARY_USEFUL_GOD(
            "주용신을 직접 극함"
    ),

    DRAINS_PRIMARY_USEFUL_GOD(
            "주용신의 기운을 설기"
    ),

    PRIMARY_CONTROLS_THIS_ELEMENT(
            "주용신이 해당 오행을 극제해야 함"
    ),

    /*
     * =================================================
     * 복수 주용신
     * =================================================
     */

    SUPPORTS_MULTIPLE_PRIMARY_GODS(
            "복수 주용신을 동시에 지원"
    ),

    HARMS_PRIMARY_GOD(
            "주용신 손상 관계 존재"
    ),

    MIXED_PRIMARY_RELATIONS(
            "주용신 사이에서 유리·불리 관계가 혼재"
    ),

    /*
     * =================================================
     * 종격
     * =================================================
     */

    FOLLOW_STRUCTURE_ALIGNMENT(
            "종격 핵심 흐름에 부합"
    ),

    FOLLOW_FLOW_BREAKING(
            "종격 흐름을 깨는 오행"
    ),

    /*
     * =================================================
     * 기존 7-8 충돌
     * =================================================
     */

    CORE_METHOD_CONFLICT(
            "CORE 용신법과 직접 충돌"
    ),

    ACTIVE_METHOD_CONFLICT(
            "ACTIVE 용신법과 직접 충돌"
    ),

    /*
     * =================================================
     * 오행량
     * =================================================
     */

    ELEMENT_SATURATION_CAUTION(
            "현재 해당 오행 세력이 이미 매우 강함"
    ),

    /*
     * =================================================
     * 주용신 미확정
     * =================================================
     */

    NO_PRIMARY_USEFUL_GOD_ANCHOR(
            "주용신 기준축이 아직 확정되지 않음"
    ),

    /*
     * =================================================
     * 희신
     * =================================================
     */

    FAVORABLE_BY_PRIMARY_GENERATION(
            "주용신 생조로 희신"
    ),

    FAVORABLE_BY_PRIMARY_PROTECTION(
            "주용신 보호 작용으로 희신"
    ),

    FAVORABLE_BY_EXISTING_SUPPORT_ROLE(
            "7-8 보조 용신 기능으로 희신"
    ),

    /*
     * =================================================
     * 기신
     * =================================================
     */

    UNFAVORABLE_BY_PRIMARY_DAMAGE(
            "주용신 직접 손상으로 기신"
    ),

    UNFAVORABLE_BY_CORE_CONFLICT(
            "핵심 용신 구조와 충돌하여 기신"
    ),

    UNFAVORABLE_BY_FOLLOW_BREAK(
            "종격 핵심 흐름을 깨뜨려 기신"
    ),

    /*
     * =================================================
     * 조건부
     * =================================================
     */

    CONDITIONAL_BY_MIXED_RELATION(
            "긍정·부정 관계가 동시에 존재"
    ),

    CONDITIONAL_BY_PRIMARY_BURDEN(
            "주용신을 돕는 기능과 설기·극제 부담이 공존"
    ),

    CONDITIONAL_BY_SOURCE_DEFERRED(
            "7-8에서 이미 판단 보류"
    ),

    CONDITIONAL_BY_SATURATION(
            "필요 작용은 있으나 현재 오행 과다"
    ),

    CONDITIONAL_WITHOUT_PRIMARY_ANCHOR(
            "주용신 미확정 상태라 희신·기신 확정 보류"
    ),

    /*
     * =================================================
     * 중립
     * =================================================
     */

    NEUTRAL_WITHOUT_DECISIVE_RELATION(
            "희신·기신으로 볼 결정적 근거 부족"
    );

    private final String koreanName;

    UsefulGodRelationReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean favorableReason() {

        return this == GENERATES_PRIMARY_USEFUL_GOD
                || this == CONTROLS_PRIMARY_THREAT
                || this == SUPPORTS_MULTIPLE_PRIMARY_GODS
                || this == FAVORABLE_BY_PRIMARY_GENERATION
                || this == FAVORABLE_BY_PRIMARY_PROTECTION
                || this == FAVORABLE_BY_EXISTING_SUPPORT_ROLE;
    }

    public boolean unfavorableReason() {

        return this == CONTROLS_PRIMARY_USEFUL_GOD
                || this == HARMS_PRIMARY_GOD
                || this == FOLLOW_FLOW_BREAKING
                || this == CORE_METHOD_CONFLICT
                || this == UNFAVORABLE_BY_PRIMARY_DAMAGE
                || this == UNFAVORABLE_BY_CORE_CONFLICT
                || this == UNFAVORABLE_BY_FOLLOW_BREAK;
    }

    public boolean cautionReason() {

        return this == DRAINS_PRIMARY_USEFUL_GOD
                || this == PRIMARY_CONTROLS_THIS_ELEMENT
                || this == MIXED_PRIMARY_RELATIONS
                || this == ACTIVE_METHOD_CONFLICT
                || this == ELEMENT_SATURATION_CAUTION
                || this == SOURCE_DEFERRED;
    }
}