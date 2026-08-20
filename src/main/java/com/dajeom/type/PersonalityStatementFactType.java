package com.dajeom.type;

/**
 * Personality Statement Fact의 종류.
 *
 * =================================================
 *
 * CORE_SELF
 *
 * 일간 자기기반 / 신강신약의
 * 운용 기반.
 *
 * 주의:
 *
 * 신강 = 고집
 * 신약 = 소심
 *
 * 같은 심리문장으로 직접 변환하면 안 된다.
 *
 * =================================================
 *
 * FUNCTION
 *
 * 비겁 / 인성 / 식상 / 재성 / 관살
 * 개별 기능축.
 *
 * =================================================
 *
 * CROSS_PATTERN
 *
 * 단독으로 설명할 가치가 있는
 * 전통 십성 교차 Pattern.
 *
 * =================================================
 *
 * SYNTHESIS
 *
 * 둘 이상의 Finding이
 * 하나의 구조로 연결된 복합 Fact.
 *
 * 사용자 출력에서는
 * 개별 Cross Pattern보다 우선할 수 있다.
 *
 * =================================================
 *
 * INTERPERSONAL_CONTEXT
 *
 * 관계/합화 원국 Context.
 *
 * 단독 심리판정으로 확대하지 않는다.
 */
public enum PersonalityStatementFactType {

    CORE_SELF,

    FUNCTION,

    CROSS_PATTERN,

    SYNTHESIS,

    INTERPERSONAL_CONTEXT
}