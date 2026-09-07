package com.dajeom.domain;

/**
 * 종격 판정의 실제 명리 조건.
 *
 * 점수나 confidence가 아니다.
 */
public enum DayMasterFollowingStructureReason {

    /*
     * =================================================
     * 공통
     * =================================================
     */

    NO_DAY_MASTER_ROOT,

    DAY_MASTER_ROOT_PRESENT,

    NO_ORIGINAL_SUPPORT,

    ORIGINAL_SUPPORT_REMAINS,

    /*
     * =================================================
     * 월령 MAIN 방향
     * =================================================
     */

    MONTH_COMMAND_SUPPORTS_DAY_MASTER,

    MONTH_COMMAND_SAME_ELEMENT,

    MONTH_COMMAND_OUTPUT_DIRECTION,

    MONTH_COMMAND_WEALTH_DIRECTION,

    MONTH_COMMAND_CONTROL_DIRECTION,

    /*
     * =================================================
     * 원국 구조
     * =================================================
     */

    OUTPUT_STRUCTURE_PRESENT,

    WEALTH_STRUCTURE_PRESENT,

    CONTROL_STRUCTURE_PRESENT,

    GENERATING_SUPPORT_PRESENT,

    SAME_ELEMENT_SUPPORT_PRESENT,

    /*
     * =================================================
     * 종격을 깨는 핵심 조건
     * =================================================
     */

    OUTPUT_OPPOSES_CONTROL_FOLLOWING,

    CONTROL_OPPOSES_OUTPUT_FOLLOWING,

    WEALTH_OR_CONTROL_BREAKS_STRONG_FOLLOWING,

    /*
     * =================================================
     * 從勢
     * =================================================
     */

    MULTIPLE_OPPOSING_DIRECTIONS,

    NO_SINGLE_PURE_FOLLOWING_DIRECTION,

    /*
     * =================================================
     * 일반경로
     * =================================================
     */

    STRICT_FOLLOWING_CONDITION_NOT_MET
}