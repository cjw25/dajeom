package com.dajeom.domain;

/**
 * 財格 성패 판정 이유.
 *
 * 正財 / 偏財 공통.
 *
 * 점수나 가중치가 아니다.
 */
public enum WealthPatternReason {

    /*
     * =================================================
     * 격 자체
     * =================================================
     */

    WEALTH_PATTERN,

    DIRECT_WEALTH_PATTERN,

    INDIRECT_WEALTH_PATTERN,

    WEALTH_EXPOSED,

    WEALTH_NOT_EXPOSED,

    /*
     * =================================================
     * 일간 왕쇠 Context
     * =================================================
     */

    DAY_MASTER_STRONG_SIDE,

    DAY_MASTER_WEAK_SIDE,

    DAY_MASTER_MIXED_STRENGTH,

    /*
     * =================================================
     * 財生官
     * =================================================
     */

    DIRECT_OFFICER_PRESENT,

    DIRECT_OFFICER_EXPOSED,

    WEALTH_GENERATES_OFFICER_ROUTE,

    DAY_MASTER_WEAK_WITH_EXPOSED_OFFICER,

    GENERATED_OFFICER_DAMAGED_BY_HURTING_OFFICER,

    GENERATED_OFFICER_COMBINED,

    /*
     * =================================================
     * 食神生財
     * =================================================
     */

    EATING_GOD_EXPOSED,

    FOOD_GENERATES_WEALTH_ROUTE,

    FOOD_GENERATES_WEALTH_BUT_DAY_MASTER_NOT_STRONG,

    DIRECT_OFFICER_BLOCKS_PURE_FOOD_WEALTH_ROUTE,

    /*
     * =================================================
     * 財格佩印
     * =================================================
     */

    RESOURCE_EXPOSED,

    WEALTH_WITH_RESOURCE_ROUTE,

    WEALTH_AND_RESOURCE_NON_OBSTRUCTING,

    WEALTH_RESOURCE_PLACEMENT_REQUIRES_REVIEW,

    /*
     * =================================================
     * 比劫奪財
     * =================================================
     */

    COMPANION_OR_ROB_WEALTH_EXPOSED,

    WEALTH_CONTESTED_BY_COMPANION_OR_ROB_WEALTH,

    ROBBERY_RESOLVED_BY_EATING_GOD,

    ROBBERY_CONTROLLED_BY_OFFICER,

    ROBBERY_UNRESOLVED,

    /*
     * =================================================
     * 財透七殺
     * =================================================
     */

    SEVEN_KILLINGS_PRESENT,

    SEVEN_KILLINGS_EXPOSED,

    SEVEN_KILLINGS_CONTROLLED_BY_EATING_GOD,

    SEVEN_KILLINGS_COMBINED_AWAY_PRESERVING_WEALTH,

    SEVEN_KILLINGS_UNRESOLVED,

    HIDDEN_SEVEN_KILLINGS_REQUIRES_REVIEW,

    /*
     * =================================================
     * 傷官
     * =================================================
     */

    HURTING_OFFICER_EXPOSED,

    /*
     * =================================================
     * 최종 판정
     * =================================================
     */

    CLEAN_WEALTH_FORMATION,

    WEALTH_FORMATION_WITH_DEFECT,

    WEALTH_PATTERN_BROKEN,

    WEALTH_PATTERN_RESCUED,

    ISOLATED_WEALTH_REQUIRES_REVIEW
}