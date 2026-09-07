package com.dajeom.domain;

/**
 * 傷官格 성패 판정 이유.
 *
 * 점수 / confidence가 아니다.
 */
public enum HurtingOfficerPatternReason {

    /*
     * =================================================
     * 격 자체
     * =================================================
     */

    HURTING_OFFICER_PATTERN,

    HURTING_OFFICER_EXPOSED,

    HURTING_OFFICER_NOT_EXPOSED,

    HURTING_OFFICER_FOUNDATION_STABLE,

    HURTING_OFFICER_FOUNDATION_REQUIRES_REVIEW,

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
     * 傷官生財
     * =================================================
     */

    WEALTH_PRESENT,

    WEALTH_EXPOSED,

    WEALTH_FOUNDATION_STABLE,

    WEALTH_FOUNDATION_REQUIRES_REVIEW,

    HURTING_OFFICER_GENERATES_WEALTH_ROUTE,

    /*
     * =================================================
     * 傷官佩印
     * =================================================
     */

    RESOURCE_PRESENT,

    RESOURCE_EXPOSED,

    RESOURCE_FOUNDATION_STABLE,

    RESOURCE_CONTROLS_HURTING_OFFICER,

    HURTING_OFFICER_WITH_RESOURCE_ROUTE,

    HURTING_OFFICER_TOO_LIGHT_FOR_RESOURCE_ROUTE,

    /*
     * =================================================
     * 七殺 / 煞印
     * =================================================
     */

    SEVEN_KILLINGS_PRESENT,

    SEVEN_KILLINGS_EXPOSED,

    KILLING_GENERATES_RESOURCE_TO_SUPPORT_DAY_MASTER,

    HURTING_OFFICER_WITH_KILLING_NO_WEALTH,

    KILLING_RESOURCE_ROUTE,

    /*
     * =================================================
     * 傷官見官
     * =================================================
     */

    DIRECT_OFFICER_PRESENT,

    DIRECT_OFFICER_EXPOSED,

    DIRECT_OFFICER_CONFLICTS_WITH_HURTING_OFFICER,

    NON_METAL_WATER_HURTING_OFFICER_SEES_OFFICER,

    METAL_WATER_HURTING_OFFICER,

    METAL_WATER_OFFICER_ROUTE_USABLE,

    OFFICER_CONFLICT_RESOLVED_BY_WEALTH_BRIDGE,

    OFFICER_CONFLICT_UNRESOLVED,

    /*
     * =================================================
     * 官殺混雜
     * =================================================
     */

    OFFICER_AND_SEVEN_KILLINGS_MIXED,

    OFFICER_KILLINGS_MIXTURE_CLEARED,

    OFFICER_KILLINGS_MIXTURE_UNRESOLVED,

    /*
     * =================================================
     * 生財帶煞
     * =================================================
     */

    WEALTH_ROUTE_WITH_SEVEN_KILLINGS,

    SEVEN_KILLINGS_DAMAGES_WEALTH_ROUTE,

    SEVEN_KILLINGS_COMBINED_AWAY,

    SEVEN_KILLINGS_DAMAGE_UNRESOLVED,

    /*
     * =================================================
     * 成中有敗
     * =================================================
     */

    WEALTH_COMBINED,

    RESOURCE_DAMAGED,

    KILLING_RESOURCE_ROUTE_DAMAGED_BY_WEALTH,

    /*
     * =================================================
     * 최종
     * =================================================
     */

    CLEAN_HURTING_OFFICER_FORMATION,

    HURTING_OFFICER_FORMATION_WITH_DEFECT,

    HURTING_OFFICER_PATTERN_BROKEN,

    HURTING_OFFICER_PATTERN_RESCUED,

    HURTING_OFFICER_PATTERN_REQUIRES_REVIEW
}