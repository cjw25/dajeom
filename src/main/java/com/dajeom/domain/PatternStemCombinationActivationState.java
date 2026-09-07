package com.dajeom.domain;

/**
 * 天干五合의 실제 작용 가능 상태.
 *
 * =================================================
 *
 * SELF_COMBINATION_NOT_REMOVAL
 *
 * 日主 본신이 직접 합하는 경우.
 *
 * 五合 관계는 존재하지만
 * 다른 십성을 合去한 것으로 사용하지 않는다.
 *
 * =================================================
 *
 * ACTIVE_NON_DAY_MASTER_COMBINATION
 *
 * 日主가 아닌 두 투출 천간이
 * 인접하여 합하고,
 *
 * - 즉시 爭合 없음
 * - 사이에 다른 천간 없음
 *
 * 이 확인된 상태.
 *
 * 이후 合去/合一留一 같은
 * 격국 Rule에서 사용할 수 있는 active 합이다.
 *
 * =================================================
 *
 * BLOCKED_BY_INTERVENING_CONTROL
 *
 * 합하려는 두 천간 사이에
 * 다른 천간이 존재하고,
 *
 * 그 중간 천간이 합 당사자 중 하나를
 * 직접 剋하는 구조.
 *
 * 合而不敢合.
 *
 * =================================================
 *
 * DISTANT_WEAK_COMBINATION
 *
 * 천간 오합 pair는 존재하지만
 * 서로 멀리 떨어져 있음.
 *
 * 현재 엔진에서는
 * 合去 확정에 사용하지 않는다.
 *
 * =================================================
 *
 * COMPETING_REVIEW_REQUIRED
 *
 * 가까운 위치에서
 * 동일 천간을 둘 이상의 천간이
 * 동시에 합하려는 구조.
 *
 * 爭合 / 妬合 검토 필요.
 */
public enum PatternStemCombinationActivationState {

    SELF_COMBINATION_NOT_REMOVAL,

    ACTIVE_NON_DAY_MASTER_COMBINATION,

    BLOCKED_BY_INTERVENING_CONTROL,

    DISTANT_WEAK_COMBINATION,

    COMPETING_REVIEW_REQUIRED
}