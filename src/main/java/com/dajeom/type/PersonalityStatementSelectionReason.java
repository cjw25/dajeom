package com.dajeom.type;

/**
 * Statement Fact가 특정 Selection Role을
 * 받은 구조적 이유.
 *
 * =================================================
 *
 * 점수나 순위값이 아니다.
 *
 * 후속 단계에서:
 *
 * "왜 이 Fact가 중심이 되었는가"
 *
 * 를 추적하기 위한 Audit 정보다.
 */
public enum PersonalityStatementSelectionReason {

    /**
     * 직접 표현 가능한 복합 Synthesis.
     */
    DIRECT_SYNTHESIS,

    /**
     * 강하게 근거가 확보된 Synthesis.
     */
    STRONG_SYNTHESIS,

    /**
     * 원국 주격의 중심 기능.
     */
    STRUCTURAL_CENTER_FUNCTION,

    /**
     * STRONGLY_GROUNDED 기능축.
     */
    STRONG_FUNCTION,

    /**
     * 직접 표현 가능한 일반 기능축.
     */
    DIRECT_FUNCTION,

    /**
     * 해당 기능축을 포함하는
     * 직접 Synthesis가 이미 존재.
     *
     * 기능 자체는 삭제하지 않고
     * Supporting으로 사용한다.
     */
    FUNCTION_COVERED_BY_DIRECT_SYNTHESIS,

    /**
     * 다른 Synthesis에 흡수되지 않은
     * 강한 Cross Pattern.
     */
    STANDALONE_STRONG_CROSS_PATTERN,

    /**
     * 다른 Synthesis에 흡수되지 않은
     * 직접 Cross Pattern.
     */
    STANDALONE_CROSS_PATTERN,

    /**
     * 일간 자기기반은
     * 성격문장 자체가 아니라
     * 작동 Context로 사용.
     */
    CORE_SELF_AS_CONTEXT,

    /**
     * 원국 관계 Context는
     * 직접 심리판정으로 확대하지 않음.
     */
    INTERPERSONAL_AS_CONTEXT,

    /**
     * 조건/약화/충돌을
     * 함께 설명해야 함.
     */
    QUALIFICATION_REQUIRED,

    /**
     * 직접 Synthesis가
     * 동일 Cross Finding을 이미 포함.
     */
    ABSORBED_BY_SYNTHESIS,

    /**
     * 현재 Grounding으로는
     * 직접 사용자 단정을 허용하지 않음.
     */
    NOT_DIRECTLY_ASSERTABLE
}