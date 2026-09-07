package com.dajeom.domain;

/**
 * 천간합 activation 판정 근거.
 *
 * 숫자 / 점수 / confidence가 아니다.
 */
public enum PatternStemCombinationActivationReason {

    /**
     * 日主가 직접 합에 참여.
     */
    DAY_MASTER_SELF_COMBINATION,

    /**
     * 本身之合이므로 合去 아님.
     */
    SELF_COMBINATION_NOT_REMOVAL,

    /**
     * 日主가 아닌 두 천간의 합.
     */
    NON_DAY_MASTER_COMBINATION,

    /**
     * 두 합 당사자가 인접.
     */
    ADJACENT_COMBINATION,

    /**
     * 현재 조건에서
     * active 비일간 천간합으로 사용 가능.
     */
    ACTIVE_COMBINATION,

    /**
     * 합 당사자 사이에
     * 다른 천간이 존재.
     */
    INTERVENING_STEM_EXISTS,

    /**
     * 중간 천간이
     * 왼쪽 합 당사자를 剋함.
     */
    INTERVENING_STEM_CONTROLS_LEFT_PARTICIPANT,

    /**
     * 중간 천간이
     * 오른쪽 합 당사자를 剋함.
     */
    INTERVENING_STEM_CONTROLS_RIGHT_PARTICIPANT,

    /**
     * 중간 천간의 剋 때문에
     * 合 작용을 active로 사용할 수 없음.
     */
    COMBINATION_BLOCKED_BY_CONTROL,

    /**
     * 합 pair가 서로 멀리 떨어져 있음.
     */
    DISTANT_COMBINATION,

    /**
     * 멀리 떨어졌지만
     * 중간 천간의 직접 剋은 확인되지 않음.
     */
    DISTANT_WITHOUT_DIRECT_CONTROL_BLOCK,

    /**
     * 현재 엔진에서는
     * 멀리 떨어진 합을 合去에 사용하지 않음.
     */
    DISTANT_COMBINATION_NOT_USABLE_AS_REMOVAL,

    /**
     * 가까운 복수 합 후보가 존재.
     */
    IMMEDIATE_COMPETING_COMBINATION,

    /**
     * 爭合 / 妬合 추가 검토 필요.
     */
    COMPETING_REVIEW_REQUIRED
}