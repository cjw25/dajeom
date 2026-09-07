package com.dajeom.domain;

/**
 * 월령 기반 격국 후보의 다음 판정 경로.
 *
 * 아직 최종 격국 성립이 아니다.
 */
public enum MonthCommandPatternSelectionPath {

    /**
     * 월령 MAIN이 일반 8격 후보이고
     * 비MAIN 투출에 의한 변화 후보가 없음.
     *
     * 본래 월령 후보를 기준으로
     * 成格/破格 판정으로 진행.
     */
    STANDARD_PRIMARY_REVIEW,

    /**
     * 월령 MAIN은 일반 8격 후보이나
     * EARLY/MIDDLE 중 하나가 실제 투출.
     *
     * 본래 격과 변화 후보를 함께 검토.
     */
    STANDARD_CHANGE_REVIEW,

    /**
     * 월령의 비MAIN 지장간 중
     * 둘 이상이 투출되어
     * 하나의 변화 후보로 바로 정리하기 어려움.
     */
    STANDARD_MULTIPLE_CHANGE_REVIEW,

    /**
     * 월령 MAIN이 일간과 동일 천간/비견.
     *
     * 建祿 계열 검토.
     */
    JIANLU_REVIEW,

    /**
     * 월령 MAIN이 겁재이고
     * 일간이 양간.
     *
     * 陽刃 계열 검토.
     */
    YANG_BLADE_REVIEW,

    /**
     * 월령 MAIN이 겁재이고
     * 일간이 음간.
     *
     * 月劫 계열 검토.
     */
    YUEJIE_REVIEW
}