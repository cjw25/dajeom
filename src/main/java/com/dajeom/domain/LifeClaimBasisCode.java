package com.dajeom.domain;

/**
 * LifeClaim 생성 근거.
 *
 * 사용자에게 직접 노출하지 않는 내부 근거 코드다.
 *
 * 숫자 점수, 확률, confidence 용도로 사용하지 않는다.
 */
public enum LifeClaimBasisCode {

    /*
     * =================================================
     * 日主 강약
     * =================================================
     */

    GENERAL_STRENGTH_WEAK_SIDE,

    GENERAL_STRENGTH_STRONG_SIDE,

    GENERAL_STRENGTH_SPECIAL_CONTEXT,

    FOLLOWING_STRUCTURE_ESTABLISHED,

    TRUE_TRANSFORMATION_STRUCTURE,

    /*
     * =================================================
     * 십성 family 기본 구조
     * =================================================
     */

    WEALTH_FAMILY_PRESENT,

    WEALTH_FOUNDATION_CONFIRMED,

    RESOURCE_FAMILY_PRESENT,

    RESOURCE_FOUNDATION_CONFIRMED,

    OUTPUT_FAMILY_PRESENT,

    OUTPUT_FOUNDATION_CONFIRMED,

    AUTHORITY_FAMILY_PRESENT,

    AUTHORITY_FOUNDATION_CONFIRMED,

    COMPANION_FAMILY_PRESENT,

    COMPANION_FOUNDATION_CONFIRMED,

    /*
     * =================================================
     * 食傷 / 印 강약 민감 구조
     * =================================================
     */

    OUTPUT_DRAINS_WEAK_STRUCTURE,

    EXCESSIVE_RESOURCE_CONTEXT,

    WEALTH_MODERATES_EXCESS_RESOURCE,

    HURTING_OFFICER_TOO_LIGHT_FOR_RESOURCE_ROUTE,

    /*
     * =================================================
     * 財印
     * =================================================
     */

    WEALTH_RESOURCE_DIRECT_CONFLICT,

    WEALTH_RESOURCE_MUTUALLY_NON_OBSTRUCTING,

    /*
     * =================================================
     * 格局
     * =================================================
     */

    PATTERN_FORMED,

    PATTERN_FORMED_WITH_DEFECT,

    PATTERN_BROKEN,

    PATTERN_BROKEN_WITH_RESCUE,

    PATTERN_REVIEW_REQUIRED,

    PATTERN_UNFORMED,

    PATTERN_NOT_YET_SELECTED,

    /*
     * =================================================
     * 月令
     * =================================================
     */

    MONTH_COMMAND_DISRUPTED,

    MONTH_COMMAND_DISRUPTION_RESOLVED,

    MONTH_COMMAND_DISRUPTION_REVIEW_REQUIRED,

    /*
     * =================================================
     * 天干合
     * =================================================
     */

    ACTIVE_NON_DAY_MASTER_STEM_COMBINATION,

    OFFICER_KILLINGS_CLEARED_BY_STEM_COMBINATION,

    SEVEN_KILLINGS_CLEARED_PRESERVING_WEALTH,

    WEALTH_CLEARED_PRESERVING_RESOURCE,

    /*
     * =================================================
     * 官殺
     * =================================================
     */

    OFFICER_KILLINGS_MIXED,

    OFFICER_KILLINGS_MIXTURE_CLEARED,

    OFFICER_KILLINGS_REVIEW_REQUIRED,

    /*
     * =================================================
     * 陽刃
     * =================================================
     */

    YANG_BLADE_PRESENT,

    MONTH_YANG_BLADE_PRESENT,

    ACTIVE_YANG_BLADE_KILLING_COMBINATION
}