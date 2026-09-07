package com.dajeom.domain;

/**
 * 官殺混雜의 최종 정리 상태.
 *
 * =================================================
 *
 * 핵심 원칙
 * =================================================
 *
 * 1. 官殺混雜 자체가 아니면 NOT_MIXED.
 *
 * 2. 기존 官殺取清 규칙에서 완전히 정리되면 CLEARED.
 *
 * 3. 기존 규칙에서 남은 七殺을
 *    羊刃合殺로 처리하는 경우에는
 *    retained 七殺 전체가 개별적으로 처리되어야 CLEARED.
 *
 * 4. 七殺 여러 개 중 일부만 羊刃合殺되면
 *    PARTIALLY_CLEARED.
 *
 * 5. 하나라도 확정 불가 노드가 있으면 REVIEW_REQUIRED.
 *
 * 6. 아무 정리도 성립하지 않으면 UNRESOLVED.
 */
public enum PatternOfficerKillingsFinalPurificationStatus {

    /**
     * 官殺混雜 구조 자체가 아님.
     */
    NOT_MIXED,

    /**
     * 官殺混雜이 최종적으로 정리됨.
     */
    CLEARED,

    /**
     * 일부 官/殺 또는 七殺만 처리됨.
     *
     * 최종 取清 완료로 보면 안 된다.
     */
    PARTIALLY_CLEARED,

    /**
     * 경쟁·차단·중복 등의 이유로
     * 최종 판정을 확정할 수 없음.
     */
    REVIEW_REQUIRED,

    /**
     * 官殺混雜이 존재하며
     * 유효한 정리 경로가 확정되지 않음.
     */
    UNRESOLVED
}