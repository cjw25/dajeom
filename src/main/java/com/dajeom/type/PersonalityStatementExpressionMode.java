package com.dajeom.type;

/**
 * Statement Fact를
 * 최종 사용자에게 어느 강도로
 * 표현할 수 있는지 나타낸다.
 *
 * =================================================
 *
 * 이것은:
 *
 * - 정확도 %
 * - 확률
 * - 점수
 *
 * 가 아니다.
 *
 * 명리 Evidence에 따른
 * 표현 허용 수준이다.
 *
 * =================================================
 *
 * INTERNAL_REFERENCE
 *
 * 내부 Evidence로만 보존.
 * 단독 사용자 문장 금지.
 *
 * =================================================
 *
 * CONTEXT_ONLY
 *
 * 의미 있는 Context는 있으나
 * 직접적인 성향 단정은 하지 않는다.
 *
 * =================================================
 *
 * DIRECT
 *
 * 직접 설명 가능.
 *
 * =================================================
 *
 * DIRECT_WITH_QUALIFICATION
 *
 * 직접 설명 가능하지만
 * 조건/약화/충돌도 같이 말해야 한다.
 *
 * =================================================
 *
 * STRONG_DIRECT
 *
 * 강한 근거로 직접 설명 가능.
 *
 * =================================================
 *
 * STRONG_DIRECT_WITH_QUALIFICATION
 *
 * 강한 근거가 있지만
 * 반대축/조절축/충돌조건도
 * 반드시 함께 설명해야 한다.
 */
public enum PersonalityStatementExpressionMode {

    INTERNAL_REFERENCE(
            false,
            false,
            false
    ),

    CONTEXT_ONLY(
            false,
            false,
            true
    ),

    DIRECT(
            true,
            false,
            false
    ),

    DIRECT_WITH_QUALIFICATION(
            true,
            false,
            true
    ),

    STRONG_DIRECT(
            true,
            true,
            false
    ),

    STRONG_DIRECT_WITH_QUALIFICATION(
            true,
            true,
            true
    );

    private final boolean userFacing;

    private final boolean strong;

    private final boolean qualificationRequired;

    PersonalityStatementExpressionMode(

            boolean userFacing,

            boolean strong,

            boolean qualificationRequired

    ) {

        this.userFacing =
                userFacing;

        this.strong =
                strong;

        this.qualificationRequired =
                qualificationRequired;
    }

    public boolean userFacing() {

        return userFacing;
    }

    public boolean strong() {

        return strong;
    }

    public boolean qualificationRequired() {

        return qualificationRequired;
    }
}