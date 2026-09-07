package com.dajeom.domain;

/**
 * 天干五合 효과 상태 판정 이유.
 *
 * 점수 / confidence가 아니다.
 */
public enum PatternStemCombinationEffectReason {

    /**
     * 합의 한쪽이 日主.
     */
    DAY_MASTER_INVOLVED,

    /**
     * 日主가 아닌 두 천간의 합.
     */
    NON_DAY_MASTER_COMBINATION,

    /**
     * 두 천간 위치가 바로 인접.
     */
    ADJACENT_STEMS,

    /**
     * 두 천간 사이에 다른 기둥 위치가 존재.
     */
    DISTANT_STEMS,

    /**
     * 동일 천간이 가까운 두 합 후보에 동시에 참여.
     */
    IMMEDIATE_COMPETING_COMBINATION,

    /**
     * 本身之合이므로 合去로 처리하지 않음.
     */
    SELF_COMBINATION_NOT_COMBINED_AWAY,

    /**
     * 비일간 천간끼리의 合去 후보.
     */
    REMOVAL_CANDIDATE,

    /**
     * 爭合 / 妬合 추가 검토 필요.
     */
    COMPETING_REVIEW_REQUIRED,

    /**
     * 隔位 또는 거리 때문에
     * 실제 합 작용 추가 검토 필요.
     */
    DISTANT_REVIEW_REQUIRED
}