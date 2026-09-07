package com.dajeom.domain;

/**
 * 官殺混雜의 制化取清 상태.
 *
 * 숫자 / 점수 / 확률 없음.
 */
public enum PatternOfficerKillingsPurificationState {

    /**
     * 투출 正官과 七殺이 동시에 존재하지 않음.
     */
    NOT_MIXED,

    /**
     * 食神이 七殺을 제하고
     * 正官은 보존됨.
     *
     * 去殺留官.
     */
    KILLINGS_CONTROLLED_OFFICER_RETAINED,

    /**
     * 傷官이 正官을 제하고
     * 七殺은 보존됨.
     *
     * 去官留殺.
     */
    OFFICERS_CONTROLLED_KILLINGS_RETAINED,

    /**
     * 제어 관계는 있으나
     * 보존 조건까지 완성되지 않음.
     */
    PARTIAL_CLEARING,

    /**
     * 두 방향이 동시에 성립하거나
     * 양일간 去官留殺에서 羊刃合 등
     * 추가 판정이 필요한 상태.
     */
    REVIEW_REQUIRED,

    /**
     * 官殺混雜인데 정리 구조가 없음.
     */
    UNCLEARED
}