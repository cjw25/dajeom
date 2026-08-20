package com.dajeom.type;

/**
 * Personality Judgment를 지지하는
 * 독립 Evidence Family.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * Evidence "개수"가 아니라
 * 서로 다른 계산계통인지가 중요하다.
 *
 * =================================================
 *
 * 예:
 *
 * FUNCTION_ACTIVATION
 *
 * 안에:
 *
 * - Family Power
 * - Share
 * - Pattern
 *
 * 이 여러 개 있다고 해도
 *
 * 전부 하나의 FUNCTION_ACTIVATION
 * Evidence Family로 취급한다.
 *
 * =================================================
 *
 * 따라서:
 *
 * 같은 계산결과를 여러 번 세서
 * 근거를 부풀리는 것을 방지한다.
 */
public enum PersonalityJudgmentEvidenceType {

    /**
     * 4차 신강·신약.
     */
    CORE_SELF,

    /**
     * 10차 십성 Family 활성.
     */
    FUNCTION_ACTIVATION,

    /**
     * 5차 격국 중심성.
     */
    STRUCTURAL_CENTER,

    /**
     * 10-8 / 10-9 전통 십성 교차 Pattern.
     */
    CROSS_FUNCTIONAL_PATTERN,

    /**
     * 실제 MEANINGFUL/STRONG Pattern에서
     * 변환된 상황별 작동근거.
     */
    CONDITIONAL_RESPONSE,

    /**
     * 8차 관계 + 9차 합화 Context.
     */
    INTERPERSONAL_CONTEXT
}