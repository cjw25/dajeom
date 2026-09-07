package com.dajeom.domain;

/**
 * 특정 십성의 구조적 기반 상태.
 *
 * 숫자 / 점수 / 강도 계산 없음.
 */
public enum PatternTenGodFoundationState {

    /**
     * 원국에서 해당 십성이 확인되지 않음.
     */
    ABSENT,

    /**
     * 십성은 존재하지만
     * 현재 규칙으로는 기반이 확인되지 않음.
     *
     * 예:
     *
     * - 천간에 투출됐지만 통근 없음
     * - 지장간에만 존재하지만 월령 MAIN은 아님
     */
    PRESENT_WITHOUT_CONFIRMED_FOUNDATION,

    /**
     * 다음 중 하나가 확인됨:
     *
     * - 월령 MAIN 자체
     * - 투출 천간이 동일 십성 계열 지장간에 통근
     */
    FOUNDATION_CONFIRMED
}