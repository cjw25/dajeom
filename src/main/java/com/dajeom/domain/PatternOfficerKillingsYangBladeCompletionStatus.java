package com.dajeom.domain;

/**
 * retained 七殺 노드 전체에 대해
 * 羊刃合殺 처리가 어느 정도 완료되었는지를 나타낸다.
 *
 * =================================================
 *
 * 중요:
 *
 * 七殺 여러 개 중 하나만 처리된 경우
 * COMPLETE가 아니라 PARTIAL이다.
 */
public enum PatternOfficerKillingsYangBladeCompletionStatus {

    /**
     * 이 규칙에서 처리할 retained 七殺 자체가 없음.
     */
    NO_RETAINED_KILLINGS,

    /**
     * retained 七殺 각각에 대해
     * 모두 유효한 羊刃合殺 처리가 확인됨.
     */
    COMPLETE,

    /**
     * retained 七殺 중 일부만 처리됨.
     *
     * 바로 이 상태가 기존 오류를 막는다.
     */
    PARTIAL,

    /**
     * 일부 七殺 노드는
     * 羊刃合殺 성립 여부를 확정할 수 없음.
     */
    REVIEW_REQUIRED,

    /**
     * retained 七殺은 존재하지만
     * 처리된 七殺 노드가 없음.
     */
    UNRESOLVED
}