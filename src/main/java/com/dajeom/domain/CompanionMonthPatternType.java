package com.dajeom.domain;

/**
 * 比劫 계열 월령의 별도 격국 경로.
 */
public enum CompanionMonthPatternType {

    /**
     * 月令 比肩.
     *
     * 建祿.
     */
    JIANLU,

    /**
     * 陰日主의 月令 劫財.
     *
     * 月劫.
     */
    YUEJIE,

    /**
     * 陽日主의 月令 劫財이면서
     * 실제 月支가 해당 일간의 陽刃.
     */
    YANG_BLADE
}