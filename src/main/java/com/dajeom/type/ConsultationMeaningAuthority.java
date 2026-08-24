package com.dajeom.type;

/**
 * Meaning Claim이 사용자 문장화 단계에서
 * 어떤 공식 근거 권한을 갖는지 나타내는 Authority.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * Authority는:
 *
 * - 점수
 * - 강도
 * - 우열
 * - 가중치
 *
 * 가 아니다.
 *
 * 단지:
 *
 * "이 Claim은 어떤 종류의 공식 계산 결과인가"
 *
 * 를 고정한다.
 *
 * 서로 다른 Authority를 조합하여
 * 새로운 Meaning을 만들 수 없다.
 */
public enum ConsultationMeaningAuthority {

    /**
     * 정확한 10십성 원국 Fact.
     */
    EXACT_TEN_GOD_FACT,

    /**
     * 성립조건이 확인된 독립 Formal Pattern Fact.
     */
    FORMAL_PATTERN_FACT,

    /**
     * Category deterministic Rule 결과.
     */
    DETERMINISTIC_RULE_RESULT,

    /**
     * Timeline deterministic Contract 결과.
     */
    TIMELINE_CONTRACT_RESULT,

    /**
     * 사용자 직접 Meaning보다
     * 배경 Context 성격을 갖는 원국 Fact.
     */
    NATAL_CONTEXT_FACT
}