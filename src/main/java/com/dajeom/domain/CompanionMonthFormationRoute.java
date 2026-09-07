package com.dajeom.domain;

/**
 * 建祿 / 月劫 / 陽刃의 실제 성격 경로.
 */
public enum CompanionMonthFormationRoute {

    /*
     * =================================================
     * 建祿 / 月劫
     * =================================================
     */

    /**
     * 透官而逢財印.
     */
    DIRECT_OFFICER_WITH_WEALTH_OR_RESOURCE,

    /**
     * 透財而逢食傷.
     */
    WEALTH_WITH_OUTPUT,

    /**
     * 透煞而遇制伏.
     */
    SEVEN_KILLINGS_WITH_OUTPUT_CONTROL,

    /*
     * =================================================
     * 陽刃
     * =================================================
     */

    /**
     * 陽刃 + 正官 제복 경로.
     */
    YANG_BLADE_WITH_DIRECT_OFFICER,

    /**
     * 陽刃 + 七殺 제복 경로.
     */
    YANG_BLADE_WITH_SEVEN_KILLINGS
}