package com.dajeom.domain;

/**
 * 일간과 다른 천간 사이의 오행 생극 관계.
 *
 * 점수나 강약을 뜻하지 않는다.
 */
public enum DayMasterRelationType {

    /**
     * 대상이 일간을 생함.
     *
     * 예:
     * 甲 → 丁
     */
    GENERATES_DAY_MASTER,

    /**
     * 일간과 같은 오행.
     *
     * 예:
     * 丙 ↔ 丁
     */
    SAME_ELEMENT,

    /**
     * 일간이 대상을 생함.
     *
     * 예:
     * 丁 → 戊
     */
    GENERATED_BY_DAY_MASTER,

    /**
     * 대상이 일간을 극함.
     *
     * 예:
     * 癸 → 丁
     */
    CONTROLS_DAY_MASTER,

    /**
     * 일간이 대상을 극함.
     *
     * 예:
     * 丁 → 庚
     */
    CONTROLLED_BY_DAY_MASTER
}