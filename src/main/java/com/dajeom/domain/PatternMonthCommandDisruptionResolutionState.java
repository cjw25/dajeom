package com.dajeom.domain;

/**
 * 월령 전체의 沖 / 刑 / 破 / 害 해소 상태.
 *
 * 숫자 / 점수 / 확률과 무관하다.
 */
public enum PatternMonthCommandDisruptionResolutionState {

    /**
     * 애초에 월령에 沖刑破害가 없음.
     */
    NO_DISRUPTION,

    /**
     * 월령에 걸린 모든 교란이
     * 경쟁 없는 회합 해소 후보를 갖는다.
     *
     * 이 단계에서 格 전체가 완전히 보존됐다는 뜻까지는 아니다.
     */
    RESOLVED_BY_COMBINATION,

    /**
     * 일부 교란은 해소 후보가 있으나
     * 다른 교란이 그대로 남는다.
     */
    PARTIALLY_RESOLVED,

    /**
     * 회합 해소 후보는 있으나
     * 다른 회합과 경쟁하거나 복수 후보가 얽혀
     * 단순 확정할 수 없다.
     */
    REVIEW_REQUIRED,

    /**
     * 월령 교란이 존재하지만
     * 이를 풀 회합 후보가 없다.
     */
    UNRESOLVED
}