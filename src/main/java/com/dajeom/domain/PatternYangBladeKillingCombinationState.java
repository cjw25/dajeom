package com.dajeom.domain;

/**
 * 陽刃 속 劫財藏干과
 * 투출 七殺의 합 상태.
 */
public enum PatternYangBladeKillingCombinationState {

    /**
     * 刃中劫財와 七殺의 五合이 확인되고,
     * 刃支에 별도 지지 교란이 확인되지 않음.
     */
    ACTIVE,

    /**
     * 五合 자체는 확인되지만
     * 해당 陽刃 지지가 沖刑破害를 받고 있음.
     *
     * 바로 留殺 확정에 사용하지 않고
     * 추가 검토.
     */
    BLADE_BRANCH_DISRUPTED_REVIEW_REQUIRED
}