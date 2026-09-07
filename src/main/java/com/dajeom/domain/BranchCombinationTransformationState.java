package com.dajeom.domain;

/**
 * 지지 합화 판정 상태.
 *
 * 숫자 점수나 확률은 없다.
 */
public enum BranchCombinationTransformationState {

    /**
     * 합 관계/성국은 존재하지만
     * 화신을 실제 변화로 인정할 조건은 부족.
     */
    NOT_ESTABLISHED,

    /**
     * 화신을 뒷받침하는 명리 Context는 있으나
     * 엄격한 합화 조건 전체는 충족하지 못함.
     */
    SUPPORTED_BUT_INCOMPLETE,

    /**
     * 합화 쪽 조건은 존재하지만
     * 충 또는 다른 합 경쟁이 동시에 존재.
     *
     * 따라서 합화를 확정하지 않는다.
     */
    CONTESTED,

    /**
     * 현재 엔진이 채택한 엄격한 합화 조건을
     * 모두 충족.
     *
     * 이 상태에서만 이후 단계가
     * 실제 오행 변화 후보로 사용할 수 있다.
     */
    ACCEPTED
}