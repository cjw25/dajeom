package com.dajeom.domain;

/**
 * 일반 격국 통합 판정기의 실행 결과 경로.
 *
 * 숫자 / 점수 / 확률과 무관하다.
 */
public enum StandardPatternAssessmentPath {

    /**
     * 일반 8격 후보 하나가 선택되어
     * 실제 전용 PatternRule까지 실행됨.
     */
    ASSESSED,

    /**
     * 월령 MAIN 외 하나의 지장간이 투출되어
     * 격국 변화 판단이 먼저 필요한 상태.
     */
    PATTERN_CHANGE_REVIEW_REQUIRED,

    /**
     * 월령의 비MAIN 지장간이 복수 투출되어
     * 하나의 격을 선택하기 전에
     * 혼재 구조 판단이 필요한 상태.
     */
    MULTIPLE_PATTERN_CHANGE_REVIEW_REQUIRED,

    /**
     * 建祿 별도 경로.
     */
    JIANLU_REVIEW,

    /**
     * 陽刃 별도 경로.
     */
    YANG_BLADE_REVIEW,

    /**
     * 月劫 별도 경로.
     */
    YUEJIE_REVIEW
}