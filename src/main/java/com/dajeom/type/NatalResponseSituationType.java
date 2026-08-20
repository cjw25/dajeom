package com.dajeom.type;

/**
 * 원국의 전통 십성 복합 Pattern이
 * 실제로 작동하는 상황 범위.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 이 enum은 심리 진단이나
 * 사건 예측이 아니다.
 *
 * 기존 10차에서 이미 성립한
 * 십성 복합 Pattern이
 *
 * "어떤 종류의 상황에서
 * 어떤 기능 관계를 설명하는가"
 *
 * 를 구조화한다.
 *
 * =================================================
 *
 * 예:
 *
 * 상관견관
 *
 * → RULES_AND_EXPRESSION
 *
 * 재생관
 *
 * → REALITY_AND_RESPONSIBILITY
 *
 * 비겁쟁재
 *
 * → COMPETITION_AND_RESOURCE
 *
 * =================================================
 *
 * 평상시 기본성향은
 * 이 enum에서 만들지 않는다.
 *
 * 평상시 기본성향은:
 *
 * CORE_SELF
 * +
 * STRUCTURAL_CENTER
 * +
 * 십성 5대 Functional Effect
 *
 * 를 이용해 다음 Personality 단계에서
 * 별도로 결정한다.
 */
public enum NatalResponseSituationType {

    /**
     * 외부 책임·압력 기능이
     * 작동하는 상황.
     *
     * 관련 예:
     *
     * 식신제살
     * 관인상생
     * 살인상생
     */
    PRESSURE_AND_RESPONSIBILITY(
            "압력·책임 상황"
    ),

    /**
     * 표현 기능과
     * 기준·규범 기능이
     * 직접 마주하는 상황.
     *
     * 관련 예:
     *
     * 상관견관
     */
    RULES_AND_EXPRESSION(
            "기준·규범과 표현의 충돌 상황"
    ),

    /**
     * 인성의 수용·처리 기능과
     * 식상의 표현·산출 기능이
     * 서로 작동하는 상황.
     *
     * 관련 예:
     *
     * 상관패인
     * 편인도식
     */
    PROCESSING_AND_EXPRESSION(
            "수용·처리와 표현의 조정 상황"
    ),

    /**
     * 현실·자원·성과 기능이
     * 책임·사회기능으로
     * 연결되는 상황.
     *
     * 관련 예:
     *
     * 재생관
     */
    REALITY_AND_RESPONSIBILITY(
            "현실·성과와 책임의 연결 상황"
    ),

    /**
     * 현실·자원 기능과
     * 인성의 수용·처리 기능이
     * 서로 제어관계에 놓이는 상황.
     *
     * 관련 예:
     *
     * 재극인
     */
    REALITY_AND_PROCESSING(
            "현실 요구와 수용·처리의 충돌 상황"
    ),

    /**
     * 자기 기반·경쟁 기능과
     * 현실·자원 기능이
     * 경합하는 상황.
     *
     * 관련 예:
     *
     * 비겁쟁재
     */
    COMPETITION_AND_RESOURCE(
            "경쟁과 자원 경합 상황"
    ),

    /**
     * 현실·자원 기능의 요구가
     * 일간 자기기반의 수용상태와
     * 직접 연결되는 상황.
     *
     * 관련 예:
     *
     * 재다신약
     */
    RESOURCE_CAPACITY(
            "현실·자원 요구의 수용 부담 상황"
    ),

    /**
     * 관성과 칠살이 동시에 작동하여
     * 책임·규범·압력 기능 내부에
     * 복수 작동이 존재하는 상황.
     *
     * 관련 예:
     *
     * 관살혼재
     */
    MIXED_RESPONSIBILITY(
            "복수 책임·압력 기능의 혼재 상황"
    );

    private final String koreanName;

    NatalResponseSituationType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }
}