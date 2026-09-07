package com.dajeom.domain;

/**
 * 일간 왕쇠/특수구조의 최종 판정 경로.
 *
 * 이 enum은 점수의 우선순위가 아니라
 * 명리 판단의 실제 진행 경로를 뜻한다.
 */
public enum DayMasterStrengthDecisionPath {

    /**
     * 천간오합의 真化가 실제 성립.
     *
     * 일반 일간 왕쇠를 최종 결론으로
     * 사용하지 않는다.
     */
    TRUE_TRANSFORMATION,

    /**
     * 從財 / 從官殺 / 從兒 / 從勢 /
     * 從旺 / 從強 중 하나가 실제 성립.
     *
     * 일반 왕쇠를 최종 결론으로
     * 사용하지 않는다.
     */
    FOLLOWING_STRUCTURE_ESTABLISHED,

    /**
     * 특수구조 Context는 있었으나
     * 최종 특수격으로 성립하지 않아
     * 일반 왕쇠로 복귀한 경우.
     *
     * 예:
     *
     * - 종격 후보였지만 不從
     * - 假化
     * - 爭合
     * - 不化
     */
    GENERAL_STRENGTH_WITH_SPECIAL_CONTEXT,

    /**
     * 특수구조 Context 없이
     * 일반 왕쇠만 판단.
     */
    GENERAL_STRENGTH
}