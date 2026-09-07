package com.dajeom.domain;

/**
 * 일간의 한 통근 지점이
 * 원국 관계 속에서 놓인 상태.
 *
 * 최종적인 뿌리 유효/무효 판정이 아니다.
 *
 * 특히:
 *
 * CLASH_IN_XIU_QIU_SI
 *
 * 라고 해서 자동으로
 * ROOT_REMOVED가 되는 것은 아니다.
 */
public enum DayMasterRootInfluenceState {

    /**
     * 합충형파해 관계가 직접 걸리지 않은 통근.
     */
    NO_DIRECT_RELATION,

    /**
     * 합 관계 Context에 들어간 통근.
     *
     * 합 = 강화라고 단정하지 않는다.
     */
    COMBINATION_CONTEXT,

    /**
     * 旺 또는 相 상태에서
     * 통근 지지가 충을 받음.
     */
    CLASH_IN_WANG_OR_XIANG,

    /**
     * 休 / 囚 / 死 상태에서
     * 통근 지지가 충을 받음.
     *
     * 뿌리가 실제로 제거됐다는 뜻은 아니다.
     * 최종 쇠왕 판단에서 더 주의해서 볼
     * Context라는 뜻이다.
     */
    CLASH_IN_XIU_QIU_OR_SI,

    /**
     * 충 없이 형/자형/파/해가 존재.
     */
    NON_CLASH_DISTURBANCE_CONTEXT,

    /**
     * 합과 충/형/파/해가 함께 존재.
     *
     * 단순 합 또는 단순 충으로
     * 축약하면 안 되는 상태.
     */
    MIXED_RELATION_CONTEXT,

    /**
     * 충과 다른 형/파/해가 함께 존재.
     */
    CLASH_WITH_ADDITIONAL_DISTURBANCE
}