package com.dajeom.type;

/**
 * 하나의 Consultation Meaning Claim이
 * 어떤 계산 계층에서 직접 내려왔는지 나타내는 Origin.
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * Origin은 Meaning의 출처를 구분한다.
 *
 * 서로 다른 Origin이 동시에 존재한다는 이유만으로
 * 새로운 Meaning을 만들지 않는다.
 */
public enum ConsultationMeaningOrigin {

    /**
     * 정확한 10십성 자체에서 직접 내려온 Meaning.
     *
     * 비견 / 겁재 / 식신 / 상관 / 편재 / 정재 /
     * 편관 / 정관 / 편인 / 정인
     */
    EXACT_TEN_GOD,

    /**
     * Java가 전통 명리 성립조건을 확인한
     * 독립 Pattern.
     */
    FORMAL_PATTERN,

    /**
     * Consultation Category의
     * deterministic Rule 결과.
     */
    DETERMINISTIC_RULE,

    /**
     * 연도 / 시기 deterministic Contract.
     */
    TIMELINE_CONTRACT,

    /**
     * CoreSelf 또는 기존 기능축처럼
     * 원국 배경 Context로만 보존되는 Meaning.
     *
     * 정확한 10십성이나 Formal Pattern보다
     * 상위 Meaning을 만들 수 없다.
     */
    NATAL_CONTEXT
}