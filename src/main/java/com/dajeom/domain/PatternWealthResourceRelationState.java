package com.dajeom.domain;

/**
 * 하나의 財 노드와 印 노드 사이의
 * 위치 기반 관계 상태.
 *
 * 숫자 / 점수 / 가중치 없음.
 */
public enum PatternWealthResourceRelationState {

    /**
     * 財와 印이 가까이 위치하며
     * 財 → 印의 직접 克 관계가 그대로 작동하는 후보.
     */
    DIRECT_CONFLICT,

    /**
     * 財 → 官/殺 → 印
     *
     * 동일 노드 체인의 통관이 존재.
     */
    AUTHORITY_BRIDGED_NON_OBSTRUCTING,

    /**
     * 財와 印 사이에 직접 인접 충돌이 없고
     * 위치상 서로 떨어져 있음.
     *
     * 財印不相礙 후보.
     */
    POSITIONALLY_SEPARATED_NON_OBSTRUCTING
}