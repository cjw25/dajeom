package com.dajeom.type;

/**
 * 정밀화 7차-7.
 *
 * 종격용신 후보 선정 근거.
 *
 * 사용자용 최종 해석 문장이 아니라
 * Explainability Evidence다.
 */
public enum FollowStructureUsefulGodReasonType {

    /*
     * =================================================
     * 7-1 적용도
     * =================================================
     */

    FOLLOW_METHOD_CORE(
            "종격용신법 핵심 적용"
    ),

    FOLLOW_METHOD_INACTIVE(
            "종격용신법 비활성"
    ),

    /*
     * =================================================
     * 종격 유형
     * =================================================
     */

    FOLLOW_WEAK_STRUCTURE(
            "종약 계열 구조"
    ),

    FOLLOW_STRONG_STRUCTURE(
            "종강 계열 구조"
    ),

    /*
     * =================================================
     * 종약 방향
     * =================================================
     */

    FOLLOW_OUTPUT_DIRECTION(
            "식상 방향으로 종함"
    ),

    FOLLOW_WEALTH_DIRECTION(
            "재성 방향으로 종함"
    ),

    FOLLOW_OFFICER_DIRECTION(
            "관살 방향으로 종함"
    ),

    FOLLOW_MIXED_DIRECTION(
            "식상·재성·관살 복합 방향으로 종함"
    ),

    /*
     * =================================================
     * 역할 관계
     * =================================================
     */

    DIRECT_FOLLOW_DIRECTION(
            "종하는 핵심 방향과 직접 일치"
    ),

    FOLLOW_FLOW_SUPPORT(
            "종하는 흐름을 이어주는 보조 역할"
    ),

    DOMINANT_FOLLOW_ELEMENT(
            "현재 종하는 흐름에서 우세한 오행"
    ),

    BREAKS_FOLLOW_FLOW(
            "종하는 흐름을 되돌리거나 깨뜨릴 가능성"
    ),

    /*
     * =================================================
     * 종강
     * =================================================
     */

    COMPANION_FOLLOW_DIRECTION(
            "비겁 중심 종강 방향"
    ),

    RESOURCE_FOLLOW_DIRECTION(
            "인성 중심 종강 방향"
    ),

    /*
     * =================================================
     * 현재 세력
     * =================================================
     */

    ELEMENT_SCARCE(
            "현재 해당 오행 세력이 부족"
    ),

    ELEMENT_MODERATE(
            "현재 해당 오행 세력이 보통"
    ),

    ELEMENT_HIGH(
            "현재 해당 오행 세력이 강함"
    ),

    ELEMENT_SATURATED(
            "현재 해당 오행 세력이 매우 강함"
    ),

    /*
     * =================================================
     * 신강신약 검증
     * =================================================
     */

    STRENGTH_SUPPORTED(
            "종격 강약 방향이 후보를 지지"
    ),

    STRENGTH_NEUTRAL(
            "강약 구조와 직접 충돌 없음"
    ),

    STRENGTH_STRAINED(
            "강약 구조에 부담"
    ),

    STRENGTH_CONTRADICTORY(
            "종격 강약 방향과 충돌"
    ),

    /*
     * =================================================
     * 격국 검증
     * =================================================
     */

    STRUCTURE_SUPPORTED(
            "격국 구조가 후보를 지지"
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
     * 결과
     * =================================================
     */

    PRIMARY_BY_DIRECT_FOLLOW(
            "종하는 핵심 방향으로 1순위"
    ),

    PRIMARY_BY_DOMINANT_FLOW(
            "현재 우세한 종격 흐름으로 1순위"
    ),

    SECONDARY_BY_FLOW_SUPPORT(
            "종하는 흐름 연결로 보조 후보"
    ),

    CONDITIONAL_BY_CROSS_VALIDATION(
            "교차검증 부담으로 조건부 처리"
    ),

    NO_FOLLOW_STRUCTURE_MODE(
            "실제 종격 주격이 아니므로 후보 생성 안 함"
    );

    private final String koreanName;

    FollowStructureUsefulGodReasonType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean directionReason() {

        return this == FOLLOW_OUTPUT_DIRECTION
                || this == FOLLOW_WEALTH_DIRECTION
                || this == FOLLOW_OFFICER_DIRECTION
                || this == FOLLOW_MIXED_DIRECTION
                || this == COMPANION_FOLLOW_DIRECTION
                || this == RESOURCE_FOLLOW_DIRECTION;
    }

    public boolean conflictReason() {

        return this == BREAKS_FOLLOW_FLOW
                || this == STRENGTH_STRAINED
                || this == STRENGTH_CONTRADICTORY
                || this == STRUCTURE_STRAINED
                || this == STRUCTURE_CONTRADICTORY
                || this == CONDITIONAL_BY_CROSS_VALIDATION;
    }
}