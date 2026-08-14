package com.dajeom.type;

/**
 * 정밀화 9차-6.
 *
 * 지지 방합 국 성립 판정 근거.
 *
 * =================================================
 *
 * 방합은 삼합과 달리
 * 계절 방향성 자체가 핵심이므로
 *
 * TARGET_SEASON_DIRECTION_ALIGNED
 *
 * 를 별도 Evidence로 둔다.
 *
 * =================================================
 *
 * Reason은 점수 합산용이 아니다.
 *
 * 각 방합 후보가 왜 현재 상태가 되었는지
 * 추적하기 위한 구조적 Evidence다.
 */
public enum EarthlyBranchDirectionalCombinationFormationReasonType {

    /*
     * =================================================
     * 구성 형태
     * =================================================
     */

    COMPLETE_DIRECTIONAL_COMBINATION(
            "방합의 세 지지가 모두 갖추어졌습니다."
    ),

    PARTIAL_DIRECTIONAL_WITH_CARDINAL(
            "왕지를 포함한 부분 방합 구조입니다."
    ),

    PARTIAL_DIRECTIONAL_WITHOUT_CARDINAL(
            "왕지가 없는 시작지·마무리지 부분 방합 구조입니다."
    ),

    CARDINAL_BRANCH_INCLUDED(
            "방합의 계절 중심 왕지가 관계에 포함되어 있습니다."
    ),

    CARDINAL_BRANCH_ABSENT(
            "방합의 계절 중심 왕지가 관계에 포함되어 있지 않습니다."
    ),

    /*
     * =================================================
     * 배치
     * =================================================
     */

    COMPACT_PLACEMENT(
            "방합 구성 지지가 원국에서 밀집되어 있습니다."
    ),

    DISPERSED_PLACEMENT(
            "방합 구성 지지가 원국에서 떨어져 있습니다."
    ),

    MONTH_BRANCH_INVOLVED(
            "월지가 방합 관계에 직접 참여합니다."
    ),

    MONTH_BRANCH_NOT_INVOLVED(
            "월지가 방합 관계에 직접 참여하지 않습니다."
    ),

    DAY_BRANCH_INVOLVED(
            "일지가 방합 관계에 직접 참여합니다."
    ),

    /*
     * =================================================
     * 계절 방향
     * =================================================
     */

    TARGET_SEASON_DIRECTION_ALIGNED(
            "방합 목표 오행과 현재 계절 방향이 강하게 일치합니다."
    ),

    TARGET_SEASONALLY_SUPPORTED(
            "방합 목표 오행이 계절적으로 지지됩니다."
    ),

    TARGET_SEASONALLY_NEUTRAL(
            "방합 목표 오행의 계절 환경이 중립적입니다."
    ),

    TARGET_SEASONALLY_WEAK(
            "방합 목표 오행의 계절 지원이 약합니다."
    ),

    /*
     * =================================================
     * 월지 내부 기반
     * =================================================
     */

    TARGET_PRESENT_IN_MONTH_BRANCH(
            "월지 내부에 방합 목표 오행의 기반이 존재합니다."
    ),

    TARGET_ABSENT_FROM_MONTH_BRANCH(
            "월지 내부에서 방합 목표 오행의 직접 기반이 약합니다."
    ),

    /*
     * =================================================
     * 원국 전체 목표 오행
     * =================================================
     */

    TARGET_ELEMENT_STRONG_IN_CHART(
            "원국에서 방합 목표 오행의 세력이 충분합니다."
    ),

    TARGET_ELEMENT_PRESENT_IN_CHART(
            "원국에서 방합 목표 오행이 의미 있게 존재합니다."
    ),

    TARGET_ELEMENT_SCARCE_IN_CHART(
            "원국에서 방합 목표 오행의 세력이 부족합니다."
    ),

    TARGET_ELEMENT_VERY_SCARCE_IN_CHART(
            "원국에서 방합 목표 오행의 기반이 매우 약합니다."
    ),

    /*
     * =================================================
     * 9-6 최종 상태
     * =================================================
     */

    PROVISIONAL_FORMATION_ESTABLISHED(
            "방해 관계 검토 전 기준에서 방합 국의 성립 조건이 충분합니다."
    ),

    PROVISIONAL_PARTIAL_FORMATION(
            "방합 목표 오행의 국 작용이 부분적으로 형성됩니다."
    ),

    FORMATION_REQUIRES_FURTHER_CONDITIONS(
            "방합계 관계는 존재하지만 국 성립에는 추가 조건이 필요합니다."
    ),

    RELATION_EXISTS_WITHOUT_FORMATION(
            "방합계 관계는 존재하지만 현재 환경에서 방국 작용은 성립하지 않습니다."
    );

    private final String koreanDescription;

    EarthlyBranchDirectionalCombinationFormationReasonType(
            String koreanDescription
    ) {

        this.koreanDescription = koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}