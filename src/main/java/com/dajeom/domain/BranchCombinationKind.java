package com.dajeom.domain;

/**
 * 지지의 합 구조 종류.
 *
 * 여기서는 완성된 합 구조만 대상으로 한다.
 *
 * - 육합: 2지
 * - 삼합: 3지
 * - 방합: 3지
 *
 * 반합/부분삼합/부분방합은
 * 별도 관계 Context로 보존하고
 * 이 단계에서 완전한 합화 후보로 승격하지 않는다.
 */
public enum BranchCombinationKind {

    SIX_HARMONY,

    THREE_HARMONY,

    DIRECTIONAL_HARMONY
}