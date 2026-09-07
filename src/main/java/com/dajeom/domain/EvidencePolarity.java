package com.dajeom.domain;

/**
 * Evidence가 삶의 특정 방향에
 * 어떤 작용을 하는지 나타낸다.
 *
 * 좋다/나쁘다의 단순 이분법이 아니다.
 */
public enum EvidencePolarity {

    /**
     * 해당 LifeClaim 방향을
     * 비교적 자연스럽게 뒷받침한다.
     */
    SUPPORT,

    /**
     * 부담, 제약, 압력 방향.
     */
    PRESSURE,

    /**
     * 하나의 Evidence 내부에서도
     * 서로 다른 방향이 함께 존재한다.
     */
    MIXED,

    /**
     * 직접적으로 상승/압력을 정하지 않고
     * 다른 Evidence를 해석하기 위한 문맥.
     */
    CONTEXT
}