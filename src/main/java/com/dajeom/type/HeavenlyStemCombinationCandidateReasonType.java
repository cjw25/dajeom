package com.dajeom.type;

/**
 * 정밀화 9차-1.
 *
 * 천간합 후보의 기본 적용도를 만든 근거.
 *
 * =================================================
 *
 * 주의:
 *
 * Reason 하나만으로
 * 합화 여부를 확정하지 않는다.
 *
 * 이후 9-2, 9-3에서 다시 소비할
 * Evidence를 보존하기 위한 타입이다.
 */
public enum HeavenlyStemCombinationCandidateReasonType {

    /**
     * 두 천간이 바로 인접한 궁위에 위치.
     */
    ADJACENT_PAIR(
            "두 천간이 인접해 있습니다."
    ),

    /**
     * 두 천간 사이에 다른 천간 위치가 존재.
     */
    DISTANT_PAIR(
            "두 천간이 서로 떨어져 있습니다."
    ),

    /**
     * 일간이 합 관계에 직접 참여.
     *
     * 이것 자체를 강한 합화 조건으로
     * 가산하지는 않는다.
     */
    DAY_MASTER_INVOLVED(
            "일간이 천간합에 직접 참여합니다."
    ),

    /**
     * 일간이 포함되지 않은 합.
     */
    NON_DAY_MASTER_COMBINATION(
            "일간 외 천간 사이의 합입니다."
    ),

    /**
     * 목표 오행이 계절적으로 강하게 지지됨.
     */
    TARGET_SEASONALLY_SUPPORTED(
            "합화 목표 오행이 계절적으로 지지됩니다."
    ),

    /**
     * 목표 오행의 계절 상태가 중립권.
     */
    TARGET_SEASONALLY_NEUTRAL(
            "합화 목표 오행의 계절 환경이 중립적입니다."
    ),

    /**
     * 목표 오행이 계절적으로 약함.
     */
    TARGET_SEASONALLY_WEAK(
            "합화 목표 오행의 계절 지원이 약합니다."
    ),

    /**
     * 월지 내부에 목표 오행이 존재.
     */
    TARGET_PRESENT_IN_MONTH_BRANCH(
            "월지에 합화 목표 오행의 기반이 존재합니다."
    ),

    /**
     * 월지에 목표 오행 기반이 없음.
     */
    TARGET_ABSENT_FROM_MONTH_BRANCH(
            "월지에서 합화 목표 오행의 직접 기반이 약합니다."
    ),

    /**
     * 원국 전체에서 목표 오행 세력이 충분함.
     */
    TARGET_ELEMENT_STRONG_IN_CHART(
            "원국에서 합화 목표 오행의 세력이 충분합니다."
    ),

    /**
     * 목표 오행이 원국에 의미 있게 존재.
     */
    TARGET_ELEMENT_PRESENT_IN_CHART(
            "원국에 합화 목표 오행이 존재합니다."
    ),

    /**
     * 목표 오행이 원국에서 부족.
     */
    TARGET_ELEMENT_SCARCE_IN_CHART(
            "원국에서 합화 목표 오행의 세력이 부족합니다."
    ),

    /**
     * 목표 오행이 사실상 매우 미약.
     */
    TARGET_ELEMENT_VERY_SCARCE_IN_CHART(
            "원국에서 합화 목표 오행의 기반이 매우 약합니다."
    ),

    STRONG_BASE_APPLICABILITY(
            "기본 합화 검토 조건이 강하게 형성되었습니다."
    ),

    ACTIVE_BASE_APPLICABILITY(
            "합화 검토가 가능한 기본 조건이 형성되었습니다."
    ),

    CONDITIONAL_BASE_APPLICABILITY(
            "합화 검토에는 추가 조건 확인이 필요합니다."
    ),

    WEAK_BASE_APPLICABILITY(
            "현재 기본 조건만으로는 합화 적용도가 낮습니다."
    );

    private final String koreanDescription;

    HeavenlyStemCombinationCandidateReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}