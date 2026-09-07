package com.dajeom.domain;

/**
 * 印格 성패 판정 이유.
 *
 * 正印 / 偏印 공통.
 *
 * 점수나 confidence가 아니다.
 */
public enum ResourcePatternReason {

    /*
     * =================================================
     * 격 자체
     * =================================================
     */

    RESOURCE_PATTERN,

    DIRECT_RESOURCE_PATTERN,

    INDIRECT_RESOURCE_PATTERN,

    RESOURCE_EXPOSED,

    RESOURCE_NOT_EXPOSED,

    RESOURCE_FOUNDATION_STABLE,

    RESOURCE_FOUNDATION_REQUIRES_REVIEW,

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
     * 官印
     * =================================================
     */

    DIRECT_OFFICER_PRESENT,

    DIRECT_OFFICER_EXPOSED,

    OFFICER_RESOURCE_CHAIN_INTACT,

    OFFICER_RESOURCE_ROUTE,

    /*
     * =================================================
     * 七殺生印
     * =================================================
     */

    SEVEN_KILLINGS_PRESENT,

    SEVEN_KILLINGS_EXPOSED,

    SEVEN_KILLINGS_SUPPORTS_RESOURCE,

    STRONG_RESOURCE_WITH_EXCESS_KILLING,

    SEVEN_KILLINGS_CONTROLLED_BY_OUTPUT,

    /*
     * =================================================
     * 官殺混雜
     * =================================================
     */

    OFFICER_AND_SEVEN_KILLINGS_MIXED,

    SEVEN_KILLINGS_CLEARED_BY_COMBINATION,

    OFFICER_KILLINGS_MIXTURE_CONTROLLED,

    OFFICER_KILLINGS_MIXTURE_UNRESOLVED,

    /*
     * =================================================
     * 食傷洩秀
     * =================================================
     */

    OUTPUT_EXPOSED,

    EATING_GOD_EXPOSED,

    HURTING_OFFICER_EXPOSED,

    STRONG_BODY_RESOURCE_USES_OUTPUT,

    OUTPUT_EXCESSIVELY_DRAINS_WEAK_STRUCTURE,

    /*
     * =================================================
     * 梟奪食
     * =================================================
     */

    INDIRECT_RESOURCE_EXPOSED,

    INDIRECT_RESOURCE_SUPPRESSES_EATING_GOD,

    /*
     * =================================================
     * 印多用財 / 財破印
     * =================================================
     */

    WEALTH_EXPOSED,

    WEALTH_MODERATES_EXCESS_RESOURCE,

    WEALTH_ACTUALLY_DAMAGES_RESOURCE,

    WEALTH_DAMAGE_RESOLVED_BY_ROB_WEALTH,

    WEALTH_COMBINED_AWAY_PRESERVING_RESOURCE,

    WEALTH_DAMAGE_UNRESOLVED,

    /*
     * =================================================
     * 成中有敗
     * =================================================
     */

    OUTPUT_ROUTE_WITH_WEALTH_DEFECT,

    KILLING_RESOURCE_ROUTE_WITH_WEALTH_DEFECT,

    OUTPUT_ROUTE_WITH_INDIRECT_RESOURCE_DEFECT,

    /*
     * =================================================
     * 최종
     * =================================================
     */

    CLEAN_RESOURCE_FORMATION,

    RESOURCE_FORMATION_WITH_DEFECT,

    RESOURCE_PATTERN_BROKEN,

    RESOURCE_PATTERN_RESCUED,

    RESOURCE_PATTERN_REQUIRES_REVIEW
}