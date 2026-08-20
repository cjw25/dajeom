package com.dajeom.type;

/**
 * 카테고리 전용 deterministic Rule이 반환하는
 * 판단 방향.
 *
 * =================================================
 *
 * 정밀화 23차-1.
 *
 * 현재 카테고리 계산층은
 * 기존 18차 LifeDomainSignalStatus의
 * 공식 네 방향을 그대로 사용한다.
 *
 * =================================================
 *
 * SUPPORTIVE
 *
 * 해당 카테고리를 뒷받침하는 구조.
 *
 * =================================================
 *
 * PRESSURE
 *
 * 해당 카테고리에서
 * 부담·소모·충돌 등
 * 주의할 Context.
 *
 * =================================================
 *
 * MIXED
 *
 * 지원 요소와 부담 요소가
 * 동시에 존재하는 구조.
 *
 * =================================================
 *
 * CONTEXTUAL
 *
 * 방향성을 단독 결정하지 않고
 * 다른 카테고리 판단을 보완하는 구조.
 *
 * =================================================
 *
 * 중요한 원칙
 * =================================================
 *
 * 이 상태는:
 *
 * - 길 / 흉
 * - 성공 / 실패
 * - 사건 발생 확률
 *
 * 을 의미하지 않는다.
 *
 * =================================================
 *
 * 현재는 기존 18차 공식 상태에 존재하지 않는
 *
 * - NEUTRAL
 * - CAUTION
 *
 * 같은 새로운 상태를 임의로 추가하지 않는다.
 *
 * 새로운 상태가 정말 필요해질 경우에는
 * 해당 카테고리 deterministic Rule과
 * 실제 계산 근거가 먼저 정의되어야 한다.
 *
 * =================================================
 *
 * 서로 다른 Rule의 결과를
 * 자동으로 하나의 상태로 합치지도 않는다.
 *
 * 예:
 *
 * SUPPORTIVE
 * +
 * PRESSURE
 *
 * 가 존재한다고 해서
 * Engine이 임의로 MIXED 하나로 바꾸지 않는다.
 */
public enum ConsultationCategoryDeterministicStatus {

    SUPPORTIVE,

    PRESSURE,

    MIXED,

    CONTEXTUAL;


    /**
     * 기존 18차 LifeDomainSignalStatus를
     * 새로운 카테고리 계산층의 상태로
     * 의미 변경 없이 옮긴다.
     *
     * 새로운 판단을 만드는 변환이 아니다.
     */
    public static ConsultationCategoryDeterministicStatus from(

            LifeDomainSignalStatus source

    ) {

        if (
                source == null
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainSignalStatus는 null일 수 없습니다."
            );
        }

        return switch (
                source
                ) {

            case SUPPORTIVE ->
                    SUPPORTIVE;

            case PRESSURE ->
                    PRESSURE;

            case MIXED ->
                    MIXED;

            case CONTEXTUAL ->
                    CONTEXTUAL;
        };
    }
}