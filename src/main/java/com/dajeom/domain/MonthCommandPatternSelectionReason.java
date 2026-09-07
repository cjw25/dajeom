package com.dajeom.domain;

/**
 * 월령 격국 선택 경로가 결정된 이유.
 *
 * 점수나 confidence가 아니다.
 */
public enum MonthCommandPatternSelectionReason {

    /**
     * 월령 MAIN이 일반 8격 후보.
     */
    MONTH_MAIN_IS_STANDARD_PATTERN,

    /**
     * 월령 MAIN이 비견.
     */
    MONTH_MAIN_IS_COMPANION,

    /**
     * 월령 MAIN이 겁재.
     */
    MONTH_MAIN_IS_ROB_WEALTH,

    /**
     * 겁재 월령이며 일간이 양간.
     */
    YANG_DAY_MASTER_WITH_ROB_WEALTH_MONTH,

    /**
     * 겁재 월령이며 일간이 음간.
     */
    YIN_DAY_MASTER_WITH_ROB_WEALTH_MONTH,

    /**
     * 월령 MAIN 자체가 천간에 투출.
     */
    PRIMARY_MAIN_EXPOSED,

    /**
     * 월령 MAIN은 천간에 투출하지 않음.
     */
    PRIMARY_MAIN_NOT_EXPOSED,

    /**
     * 비MAIN 지장간의 천간 투출 없음.
     */
    NO_EXPOSED_ALTERNATIVE,

    /**
     * 비MAIN 지장간 하나가 투출.
     */
    SINGLE_EXPOSED_ALTERNATIVE,

    /**
     * 비MAIN 지장간 복수 투출.
     */
    MULTIPLE_EXPOSED_ALTERNATIVES,

    /**
     * 투출된 비MAIN 후보의 격 종류가
     * MAIN 후보와 다름.
     */
    ALTERNATIVE_PATTERN_DIFFERS_FROM_PRIMARY,

    /**
     * 투출된 비MAIN 후보가
     * MAIN과 같은 격 종류에 속함.
     */
    ALTERNATIVE_PATTERN_SAME_AS_PRIMARY
}