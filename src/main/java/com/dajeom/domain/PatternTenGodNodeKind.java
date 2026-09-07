package com.dajeom.domain;

/**
 * 격국 생극 그래프의 노드 종류.
 */
public enum PatternTenGodNodeKind {

    /**
     * 일간 자신.
     */
    DAY_MASTER,

    /**
     * 년간 / 월간 / 시간에 드러난 천간.
     *
     * 일간 자신은 제외.
     */
    VISIBLE_STEM,

    /**
     * 각 지지 내부의 지장간.
     */
    HIDDEN_STEM
}