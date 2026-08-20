package com.dajeom.type;

/**
 * Personality Statement Fact의
 * 최종 선택 역할.
 *
 * =================================================
 *
 * PRIMARY
 *
 * 기본성향 설명의 중심이 되는 Fact.
 *
 * =================================================
 *
 * SUPPORTING
 *
 * PRIMARY 설명을 보강하거나
 * 독립적으로 의미는 있지만
 * 최상위 중심축까지는 아닌 Fact.
 *
 * =================================================
 *
 * BACKGROUND_CONTEXT
 *
 * 사용자 성향을 직접 단정하지 않고
 * 다른 Statement를 해석할 때
 * 배경 Context로 사용하는 Fact.
 *
 * 예:
 *
 * - 일간 신강·신약 기반
 * - 원국 관계 Context
 *
 * =================================================
 *
 * INTERNAL_REFERENCE
 *
 * 내부 Evidence로 보존하지만
 * 현재 사용자 설명에는 직접 사용하지 않는 Fact.
 *
 * 예:
 *
 * - REFERENCE
 * - CONTEXTUAL
 * - 상위 Synthesis에 흡수된 Cross Pattern
 */
public enum PersonalityStatementSelectionRole {

    PRIMARY(
            true
    ),

    SUPPORTING(
            true
    ),

    BACKGROUND_CONTEXT(
            false
    ),

    INTERNAL_REFERENCE(
            false
    );

    private final boolean directOutput;

    PersonalityStatementSelectionRole(
            boolean directOutput
    ) {

        this.directOutput =
                directOutput;
    }

    public boolean directOutput() {

        return directOutput;
    }
}