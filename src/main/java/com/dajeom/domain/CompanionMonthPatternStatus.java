package com.dajeom.domain;

/**
 * 建祿 / 月劫 / 陽刃 경로 판정 상태.
 */
public enum CompanionMonthPatternStatus {

    /**
     * 현재 월령 선택 경로가
     * 建祿 / 月劫 / 陽刃이 아님.
     */
    NOT_APPLICABLE,

    /**
     * 고전적 성격 경로가 확인됨.
     */
    FORMED,

    /**
     * 핵심 경로는 있으나
     * 傷官 등 명시적인 결함도 함께 존재.
     */
    FORMED_WITH_DEFECT,

    /**
     * 핵심 글자는 있으나
     * 통근/보조조건이 부족하여
     * 성격으로 바로 확정하지 않음.
     */
    REVIEW_REQUIRED,

    /**
     * 사용할 財官殺食 경로 자체가 확인되지 않음.
     */
    UNFORMED
}