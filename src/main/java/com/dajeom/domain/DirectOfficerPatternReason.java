package com.dajeom.domain;

/**
 * 正官格 성패 판정 이유.
 *
 * 점수나 가중치가 아니다.
 */
public enum DirectOfficerPatternReason {

    /*
     * =================================================
     * 正官 자체
     * =================================================
     */

    DIRECT_OFFICER_PATTERN,

    DIRECT_OFFICER_EXPOSED,

    DIRECT_OFFICER_NOT_EXPOSED,

    /*
     * =================================================
     * 成格 보조
     * =================================================
     */

    WEALTH_SUPPORT_PRESENT,

    RESOURCE_SUPPORT_PRESENT,

    WEALTH_AND_RESOURCE_NON_OBSTRUCTING,

    WEALTH_AND_RESOURCE_PLACEMENT_REQUIRES_REVIEW,

    /*
     * =================================================
     * 傷官
     * =================================================
     */

    HURTING_OFFICER_PRESENT,

    HURTING_OFFICER_EXPOSED,

    HURTING_OFFICER_RESOLVED_BY_RESOURCE,

    HURTING_OFFICER_UNRESOLVED,

    /*
     * =================================================
     * 官殺混雜
     * =================================================
     */

    SEVEN_KILLINGS_PRESENT,

    SEVEN_KILLINGS_EXPOSED,

    SEVEN_KILLINGS_CLEARED_BY_COMBINATION,

    SEVEN_KILLINGS_MIXTURE_UNRESOLVED,

    /*
     * =================================================
     * 官星逢合
     * =================================================
     */

    DIRECT_OFFICER_STEM_COMBINED,

    /*
     * =================================================
     * 月令 형충파해
     * =================================================
     */

    MONTH_COMMAND_CLASHED,

    MONTH_COMMAND_PUNISHED,

    MONTH_COMMAND_BROKEN,

    MONTH_COMMAND_HARMED,

    MONTH_COMMAND_DISRUPTION_RESOLVED,

    MONTH_COMMAND_DISRUPTION_UNRESOLVED,

    /*
     * =================================================
     * 최종 Context
     * =================================================
     */

    CLEAN_DIRECT_OFFICER_FORMATION,

    DIRECT_OFFICER_WITH_DEFECT,

    DIRECT_OFFICER_BROKEN,

    DIRECT_OFFICER_RESCUED,

    LONE_DIRECT_OFFICER_REQUIRES_REVIEW
}