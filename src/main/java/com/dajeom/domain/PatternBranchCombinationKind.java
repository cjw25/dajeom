package com.dajeom.domain;

/**
 * 격국 구조 분석용 지지 회합 종류.
 *
 * =================================================
 *
 * SIX_HARMONY
 * = 六合
 *
 * COMPLETE_THREE_HARMONY
 * = 완전한 三合
 *
 * COMPLETE_THREE_MEETING
 * = 완전한 三會 / 方合
 *
 * =================================================
 *
 * 현재 포함하지 않음:
 *
 * - 半合
 * - 拱合
 * - 暗合
 * - 合化 여부
 */
public enum PatternBranchCombinationKind {

    /**
     * 六合
     */
    SIX_HARMONY,

    /**
     * 三合 완성.
     */
    COMPLETE_THREE_HARMONY,

    /**
     * 三會 / 方合 완성.
     */
    COMPLETE_THREE_MEETING
}