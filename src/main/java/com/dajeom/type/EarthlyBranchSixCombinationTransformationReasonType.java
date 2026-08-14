package com.dajeom.type;

/**
 * 정밀화 9차-4.
 *
 * 지지 육합의 기본 합화 판정 근거.
 *
 * =================================================
 *
 * Reason은 점수 합산용이 아니다.
 *
 * 이후 9-7 / 9-8에서:
 *
 * 왜 현재 육합이
 * 이런 기본 상태를 받았는지
 *
 * 다시 추적하기 위한 Evidence다.
 */
public enum EarthlyBranchSixCombinationTransformationReasonType {

    /*
     * =================================================
     * 원국 위치
     * =================================================
     */

    ADJACENT_PAIR(
            "두 지지가 서로 인접해 있습니다."
    ),

    DISTANT_PAIR(
            "두 지지가 서로 떨어져 있습니다."
    ),

    MONTH_BRANCH_INVOLVED(
            "월지가 육합 관계에 직접 참여합니다."
    ),

    MONTH_BRANCH_NOT_INVOLVED(
            "월지가 육합 관계에 직접 참여하지 않습니다."
    ),

    DAY_BRANCH_INVOLVED(
            "일지가 육합 관계에 직접 참여합니다."
    ),

    /*
     * =================================================
     * 목표 오행 계절
     * =================================================
     */

    TARGET_SEASONALLY_SUPPORTED(
            "합화 목표 오행이 계절적으로 지지됩니다."
    ),

    TARGET_SEASONALLY_NEUTRAL(
            "합화 목표 오행의 계절 환경이 중립적입니다."
    ),

    TARGET_SEASONALLY_WEAK(
            "합화 목표 오행의 계절 지원이 약합니다."
    ),

    /*
     * =================================================
     * 월지 내 목표 오행
     * =================================================
     */

    TARGET_PRESENT_IN_MONTH_BRANCH(
            "월지 내부에 합화 목표 오행의 기반이 존재합니다."
    ),

    TARGET_ABSENT_FROM_MONTH_BRANCH(
            "월지 내부에서 합화 목표 오행의 직접 기반이 약합니다."
    ),

    /*
     * =================================================
     * 원국 전체 목표 오행
     * =================================================
     */

    TARGET_ELEMENT_STRONG_IN_CHART(
            "원국에서 합화 목표 오행의 세력이 충분합니다."
    ),

    TARGET_ELEMENT_PRESENT_IN_CHART(
            "원국에서 합화 목표 오행이 의미 있게 존재합니다."
    ),

    TARGET_ELEMENT_SCARCE_IN_CHART(
            "원국에서 합화 목표 오행의 세력이 부족합니다."
    ),

    TARGET_ELEMENT_VERY_SCARCE_IN_CHART(
            "원국에서 합화 목표 오행의 기반이 매우 약합니다."
    ),

    /*
     * =================================================
     * 9-4 결과
     * =================================================
     */

    PROVISIONAL_TRANSFORMATION_ESTABLISHED(
            "방해 관계 검토 전 기준에서 합화 조건이 충분히 형성되었습니다."
    ),

    PROVISIONAL_PARTIAL_TRANSFORMATION(
            "합화 방향은 의미 있게 형성되지만 완전한 합화 조건에는 부족합니다."
    ),

    TRANSFORMATION_REQUIRES_FURTHER_CONDITIONS(
            "육합은 존재하지만 합화를 위해 추가 조건 검토가 필요합니다."
    ),

    COMBINATION_REMAINS_WITHOUT_TRANSFORMATION(
            "육합 관계는 존재하지만 현재 기본 환경에서는 합화가 성립하지 않습니다."
    );

    private final String koreanDescription;

    EarthlyBranchSixCombinationTransformationReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}