package com.dajeom.domain;

/**
 * 종격 계열 실제 성립 상태.
 *
 * 후보가 아니라 Rule을 통과한 결과다.
 *
 * 단:
 * FOLLOW_CONTROL은 오행 관계 수준이다.
 * 이후 기존 TenGodCalculator와 연결해서
 * 정관/칠살을 세부 구분한다.
 */
public enum DayMasterFollowingStructureState {

    /**
     * 엄격한 종격 조건이 성립하지 않음.
     *
     * 일반 왕쇠 판단으로 돌아간다.
     */
    NOT_ESTABLISHED,

    /**
     * 從財.
     */
    FOLLOW_WEALTH,

    /**
     * 從官 / 從殺 계열.
     *
     * 정관/칠살 구별은 십성 결과와
     * 이후 연결한다.
     */
    FOLLOW_CONTROL,

    /**
     * 從兒.
     */
    FOLLOW_OUTPUT,

    /**
     * 從勢.
     *
     * 무근·무생부이나
     * 財官食傷의 여러 방향이 함께 형성되어
     * 하나의 단일 從神으로 축약하기 어려운 경우.
     */
    FOLLOW_MOMENTUM,

    /**
     * 從旺.
     *
     * 비겁/동류 중심의 극왕 구조.
     */
    FOLLOW_PROSPEROUS,

    /**
     * 從強.
     *
     * 인성 생조와 비겁이 함께 형성된
     * 강한 쪽의 종격 구조.
     */
    FOLLOW_STRONG
}