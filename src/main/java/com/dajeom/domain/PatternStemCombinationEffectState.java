package com.dajeom.domain;

/**
 * 天干五合 occurrence의 구조적 효과 상태.
 *
 * =================================================
 *
 * SELF_COMBINATION_NOT_REMOVAL
 *
 * 日主 자신이 상대 천간과 합.
 *
 * 예:
 *
 * 乙日 + 庚官
 * 丁日 + 壬官
 *
 * 本身之合.
 *
 * 五合 관계는 존재하지만
 * 合去로 처리하지 않는다.
 *
 * =================================================
 *
 * NON_DAY_MASTER_REMOVAL_CANDIDATE
 *
 * 日主가 아닌 두 천간이
 * 인접 위치에서 五合하고,
 * 현재 단계에서 즉시 爭合도 확인되지 않는 상태.
 *
 * 合去 가능성이 있는 구조다.
 *
 * 아직 최종 合去 확정은 아니다.
 *
 * =================================================
 *
 * COMPETING_COMBINATION_REVIEW_REQUIRED
 *
 * 동일 천간을 향해
 * 가까운 위치에서 둘 이상의 합이 몰리는 구조.
 *
 * 爭合 / 妬合 검토 필요.
 *
 * =================================================
 *
 * DISTANT_COMBINATION_REVIEW_REQUIRED
 *
 * 五合 pair 자체는 존재하지만
 * 年時 또는 月時처럼 사이가 떨어져 있어
 * 실제 합 작용을 바로 확정할 수 없는 구조.
 */
public enum PatternStemCombinationEffectState {

    SELF_COMBINATION_NOT_REMOVAL,

    NON_DAY_MASTER_REMOVAL_CANDIDATE,

    COMPETING_COMBINATION_REVIEW_REQUIRED,

    DISTANT_COMBINATION_REVIEW_REQUIRED
}