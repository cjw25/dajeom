package com.dajeom.domain;

/**
 * 대운 / 세운 / 월운의 구조적 변화가
 * 실제 삶의 흐름에서 어떤 의미를 가질 수 있는지를
 * 나타내는 기간 Claim.
 *
 * =================================================
 *
 * 실제 사건 코드가 아니다.
 *
 * 다음과 같은 코드는 만들지 않는다.
 *
 * - CHANGED_JOB
 * - GOT_MARRIED
 * - MADE_MONEY
 * - BROKE_UP
 * - MOVED_HOUSE
 *
 * 그런 실제 사건은 사주 계산만으로
 * 확정할 수 없기 때문이다.
 */
public enum PeriodLifeClaimCode {

    /*
     * 지원 / 안정
     */

    SUPPORT_AND_RECOVERY_BECAME_MORE_AVAILABLE,

    SUPPORT_AND_PREPARATION_BECAME_MORE_DOMINANT,

    SELF_PACING_AND_SUPPORT_ALLOCATION_BECAME_MORE_IMPORTANT,

    /*
     * 출력 / 활동
     */

    EXPRESSION_AND_RESULT_PRODUCTION_BECAME_MORE_ACTIVE,

    OUTPUT_DEMAND_REQUIRED_MORE_RECOVERY,

    /*
     * 財 / 현실성
     */

    PRACTICAL_RESULTS_AND_RESOURCE_DECISIONS_BECAME_MORE_CENTRAL,

    PRACTICAL_PRESSURE_CHALLENGED_STABILITY,

    PRACTICAL_DEMANDS_PUSHED_ACTION_FORWARD,

    /*
     * 官殺
     */

    RESPONSIBILITY_AND_EVALUATION_PRESSURE_INCREASED,

    /*
     * 比劫
     */

    AUTONOMY_AND_PEER_BOUNDARIES_BECAME_MORE_IMPORTANT,

    /*
     * 구조 충돌
     */

    EXISTING_STRUCTURE_REQUIRED_REORGANIZATION,

    /*
     * 구조 해소
     */

    REORGANIZATION_ROUTE_BECAME_AVAILABLE,

    /*
     * 合
     */

    STRONG_BINDING_REDIRECTED_PRIORITIES,

    /*
     * 원국 방향 강화
     */

    EXISTING_DIRECTION_WAS_REINFORCED,

    /*
     * 원국 방향 도전
     */

    EXISTING_DIRECTION_REQUIRED_ADJUSTMENT
}