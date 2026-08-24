package com.dajeom.type;

/**
 * AI Narrative 출력 검증 위반 유형.
 *
 * 구조 검증과 의미 경계 검증을
 * 고정 타입으로 보존한다.
 */
public enum ConsultationNarrativeViolationType {

    EMPTY_TEXT,

    EMPTY_USED_CLAIMS,

    UNKNOWN_CLAIM,

    EMPHASIS_NOT_IN_TEXT,

    MARKDOWN_EXPOSED,

    HTML_EXPOSED,

    INTERNAL_TOKEN_EXPOSED,

    SAJU_TERM_EXPOSED,

    YEAR_SCOPE_EXCEEDED,

    /**
     * 현재 사용 Claim에 없는
     * 현실 사건을 AI가 새로 만든 경우.
     */
    SENSITIVE_EVENT_INVENTED,

    /**
     * 현재 사용 Claim에 없는
     * 원인/결과/선후 관계를 만든 경우.
     */
    CAUSAL_RELATION_INVENTED,

    /**
     * strongExpressionAllowed 범위를
     * 초과해서 확정적으로 단정한 경우.
     */
    STRONG_ASSERTION_EXCEEDED,

    /**
     * qualificationRequired인데
     * 제한 표현을 하지 않은 경우.
     */
    REQUIRED_QUALIFICATION_MISSING,

    LEGAL_RESULT_ASSERTED,

    MEDICAL_RESULT_ASSERTED
}