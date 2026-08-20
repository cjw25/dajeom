package com.dajeom.type;

/**
 * Personality Statement 생성 단계에서
 * 반드시 지켜야 하는 Guardrail.
 *
 * =================================================
 *
 * 이것은 OpenAI에게
 * 사주를 다시 계산시키지 않고
 *
 * Java가 확정한 구조 안에서만
 * 문장을 만들게 하기 위한 계약이다.
 */
public enum PersonalityStatementContractGuardrail {

    /**
     * OpenAI가 원국/십성/격국/용신 등을
     * 다시 계산하거나 재판정하면 안 된다.
     */
    DO_NOT_RECALCULATE_SAJU,

    /**
     * INTERNAL_REFERENCE를
     * 사용자 직접 성향판정으로
     * 승격시키면 안 된다.
     */
    DO_NOT_PROMOTE_INTERNAL_REFERENCE,

    /**
     * Java에 존재하지 않는:
     *
     * - 과거 사건
     * - 실제 경험
     * - 특정 인간관계 사건
     * - 미래 사건
     *
     * 을 만들어내면 안 된다.
     */
    DO_NOT_INVENT_EVENT_OR_HISTORY,

    /**
     * 신강 = 고집
     * 신약 = 소심
     *
     * 같은 단순 심리 변환 금지.
     */
    DO_NOT_CONVERT_CORE_SELF_DIRECTLY_TO_PERSONALITY,

    /**
     * 합충형파해 등이 존재한다는 이유만으로
     *
     * - 사람을 못 믿는다
     * - 사교적이다
     * - 관계에 집착한다
     *
     * 같은 심리 단정을 만들면 안 된다.
     */
    DO_NOT_CONVERT_INTERPERSONAL_CONTEXT_DIRECTLY_TO_PSYCHOLOGY,

    /**
     * qualificationRequired가 있으면
     *
     * 충돌만 말하거나
     * 장점만 말하는 식으로
     * 한쪽 구조를 삭제하면 안 된다.
     */
    PRESERVE_QUALIFICATION_AND_COUNTER_STRUCTURE,

    /**
     * 충돌/통제/경쟁 구조를
     * 무조건 나쁨으로,
     *
     * 상생/조절 구조를
     * 무조건 좋음으로
     *
     * 변환하면 안 된다.
     */
    DO_NOT_FLATTEN_STRUCTURE_TO_GOOD_OR_BAD,

    /**
     * BACKGROUND_CONTEXT는
     * 직접적인 사용자 성향문장으로
     * 사용하면 안 된다.
     */
    USE_BACKGROUND_CONTEXT_AS_CONTEXT_ONLY,

    /**
     * 명리 구조상의:
     *
     * A → B
     *
     * 기능 작용방향을
     *
     * "먼저 A를 하고 나중에 B를 한다"
     *
     * 같은 실제 심리 시간순서로
     * 변환하면 안 된다.
     */
    DO_NOT_INFER_PSYCHOLOGICAL_TIME_ORDER_FROM_FUNCTIONAL_SEQUENCE,

    /**
     * 내부 enum명이나
     * 상관견관/재생관 등의 전문용어를
     * 사용자에게 그대로 나열하는 것으로
     * 설명을 대신하면 안 된다.
     *
     * 전통명리는 Evidence이고,
     * 최종 출력은 일반 한국어로
     * 풀어써야 한다.
     */
    DO_NOT_EXPOSE_INTERNAL_STRUCTURE_AS_FINAL_EXPLANATION
}