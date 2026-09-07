package com.dajeom.domain;

/**
 * 천간 화기 판정의 명리적 이유.
 *
 * confidence나 점수가 아니다.
 */
public enum DayMasterStemTransformationReason {

    /**
     * 엄격한 진화 Context 충족.
     */
    STRICT_TRUE_TRANSFORMATION_CONTEXT,

    /**
     * 동일 일간에 합 상대가 복수 존재.
     */
    COMPETING_COUNTERPART,

    /**
     * 화신이 출생 월령에서 旺하지 않음.
     */
    TRANSFORMATION_NOT_COMMANDING_SEASON,

    /**
     * 일간 본래의 지지 근이 남아 있음.
     */
    ORIGINAL_ROOT_REMAINS,

    /**
     * 일간을 생하는 인성 방향이 남아 있음.
     */
    ORIGINAL_GENERATING_SUPPORT_REMAINS,

    /**
     * 일간과 같은 오행의 비겁 방향이 남아 있음.
     */
    ORIGINAL_SAME_ELEMENT_SUPPORT_REMAINS,

    /**
     * 일간의 합 상대가 년간에만 위치.
     *
     * 현재 엔진의 엄격한 진화 기준에서는
     * 월간/시간 합과 구분한다.
     */
    COUNTERPART_ONLY_YEAR_POSITION
}