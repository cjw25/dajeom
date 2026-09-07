package com.dajeom.domain;

/**
 * 십성 기반 판정 근거.
 *
 * 가중치나 confidence가 아니다.
 */
public enum PatternTenGodFoundationReason {

    /**
     * 해당 십성이 천간에 투출됨.
     */
    VISIBLE_STEM_PRESENT,

    /**
     * 해당 십성 계열이 지장간에 존재.
     */
    HIDDEN_FAMILY_PRESENT,

    /**
     * 선택된 월령 MAIN이 해당 십성.
     */
    MONTH_COMMAND_MAIN_FOUNDATION,

    /**
     * 투출 천간과 완전히 같은 천간이
     * 지장간에 존재.
     */
    EXACT_STEM_ROOT_PRESENT,

    /**
     * 같은 십성 계열 오행의 다른 음양 천간이
     * 지장간에 존재.
     *
     * 예:
     *
     * 甲이 卯中乙에 통근하는 식의
     * 동일 오행 기반.
     */
    SAME_FAMILY_ROOT_PRESENT,

    /**
     * 투출 천간이 실제 지장간 기반을 가짐.
     */
    VISIBLE_STEM_ROOTED,

    /**
     * 존재하지만 현재 기준의
     * 구조적 foundation은 확인되지 않음.
     */
    FOUNDATION_NOT_CONFIRMED
}