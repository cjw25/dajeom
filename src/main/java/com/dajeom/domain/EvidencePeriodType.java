package com.dajeom.domain;

/**
 * Evidence가 어느 시간 범위에 적용되는지 구분한다.
 */
public enum EvidencePeriodType {

    /**
     * 출생 원국 자체에서 발생한
     * 기본 구조 Evidence.
     */
    NATAL,

    /**
     * 대운 기간.
     */
    DAEWOON,

    /**
     * 세운 기간.
     */
    SEWOON,

    /**
     * 월운 기간.
     */
    WOLWOON
}