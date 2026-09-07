package com.dajeom.domain;

/**
 * 七殺格 성패 판정 이유.
 *
 * 점수 / confidence가 아니다.
 */
public enum SevenKillingsPatternReason {

    /*
     * =================================================
     * 격 자체
     * =================================================
     */

    SEVEN_KILLINGS_PATTERN,

    SEVEN_KILLINGS_EXPOSED,

    SEVEN_KILLINGS_NOT_EXPOSED,

    SEVEN_KILLINGS_FOUNDATION_STABLE,

    SEVEN_KILLINGS_FOUNDATION_REQUIRES_REVIEW,

    SEVEN_KILLINGS_STEM_COMBINED,

    /*
     * =================================================
     * 일간 왕쇠
     * =================================================
     */

    DAY_MASTER_STRONG_SIDE,

    DAY_MASTER_WEAK_SIDE,

    DAY_MASTER_MIXED_STRENGTH,

    /*
     * =================================================
     * 食神制殺
     * =================================================
     */

    EATING_GOD_PRESENT,

    EATING_GOD_EXPOSED,

    EATING_GOD_FOUNDATION_STABLE,

    EATING_GOD_CONTROLS_SEVEN_KILLINGS,

    FOOD_CONTROLS_KILLING_ROUTE,

    /*
     * =================================================
     * 殺印相生
     * =================================================
     */

    RESOURCE_PRESENT,

    RESOURCE_EXPOSED,

    RESOURCE_FOUNDATION_STABLE,

    KILLING_GENERATES_RESOURCE,

    KILLING_RESOURCE_ROUTE,

    /*
     * =================================================
     * 財滋殺
     * =================================================
     */

    WEALTH_PRESENT,

    WEALTH_EXPOSED,

    WEALTH_STRENGTHENS_SEVEN_KILLINGS,

    WEALTH_KILLING_RESOURCE_CHAIN_USABLE,

    WEALTH_STRENGTHENS_KILLING_WITHOUT_CONTROL,

    /*
     * =================================================
     * 食制又逢印
     * =================================================
     */

    RESOURCE_SUPPRESSES_EATING_GOD,

    FOOD_CONTROL_DAMAGED_BY_RESOURCE,

    /*
     * =================================================
     * 財去印存食
     * =================================================
     */

    WEALTH_REMOVES_RESOURCE_PRESERVING_EATING_GOD,

    FOOD_CONTROL_RESCUED_BY_WEALTH,

    /*
     * =================================================
     * 殺印 + 財
     * =================================================
     */

    WEALTH_DAMAGES_RESOURCE_ROUTE,

    /*
     * =================================================
     * 官殺混雜
     * =================================================
     */

    DIRECT_OFFICER_PRESENT,

    DIRECT_OFFICER_EXPOSED,

    OFFICER_AND_SEVEN_KILLINGS_MIXED,

    OFFICER_KILLINGS_MIXTURE_CLEARED,

    OFFICER_KILLINGS_MIXTURE_UNRESOLVED,

    /*
     * =================================================
     * 月令 관계
     * =================================================
     */

    MONTH_COMMAND_DISRUPTED,

    MONTH_COMMAND_DISRUPTION_RESOLVED,

    MONTH_COMMAND_DISRUPTION_UNRESOLVED,

    /*
     * =================================================
     * 최종
     * =================================================
     */

    CLEAN_SEVEN_KILLINGS_FORMATION,

    SEVEN_KILLINGS_FORMATION_WITH_DEFECT,

    SEVEN_KILLINGS_PATTERN_BROKEN,

    SEVEN_KILLINGS_PATTERN_RESCUED,

    SEVEN_KILLINGS_PATTERN_REQUIRES_REVIEW
}