package com.dajeom.domain;

/**
 * 대운 / 세운 / 월운이 원국에 가하는
 * 구조적 변화 신호.
 *
 * =================================================
 *
 * 이 enum 자체는 실제 사건을 의미하지 않는다.
 *
 * 예:
 *
 * WEALTH_PRESSURE_INCREASED
 *
 * 는
 *
 * "돈을 벌었다"
 * "돈을 잃었다"
 *
 * 가 아니다.
 *
 * 현실적 결과·자원·성과와 관련된 요구가
 * 이전보다 중요하게 작동하는 기간이라는
 * 구조 신호일 뿐이다.
 */
public enum PeriodLifeSignalCode {

    /*
     * 印 / 지원
     */

    SUPPORT_INCREASED,

    SUPPORT_DECREASED,

    /*
     * 食傷 / 외부 출력
     */

    OUTPUT_INCREASED,

    /*
     * 財 / 현실 결과
     */

    WEALTH_PRESSURE_INCREASED,

    /*
     * 官殺 / 책임·평가
     */

    AUTHORITY_PRESSURE_INCREASED,

    /*
     * 比劫 / 경쟁·독립
     */

    PEER_COMPETITION_INCREASED,

    /*
     * 원국 구조 교란
     */

    STRUCTURAL_FRICTION_ACTIVATED,

    /*
     * 교란의 해소 route
     */

    STRUCTURAL_FRICTION_RESOLVED,

    /*
     * 合 등의 강한 결속
     */

    BINDING_ACTIVATED,

    /*
     * 원국 중심 구조가 운에서 다시 강화됨
     */

    NATAL_DIRECTION_REINFORCED,

    /*
     * 원국 중심 구조에 부담이나 충돌이 생김
     */

    NATAL_DIRECTION_CHALLENGED
}