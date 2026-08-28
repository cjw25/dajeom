package com.dajeom.type;

/**
 * 하나의 Meaning Claim이
 * 사용자 문장으로 표현될 때의 공식 표현 경계.
 *
 * =================================================
 * DIRECT
 * =================================================
 *
 * 일반 직접표현 가능.
 *
 * 강한 확정표현은 허용하지 않으며
 * 별도의 qualification도 필수는 아니다.
 *
 * =================================================
 * QUALIFIED
 * =================================================
 *
 * 직접 설명은 가능하지만:
 *
 * - 경향
 * - 가능성
 * - 조건에 따라
 * - 나타날 수 있음
 *
 * 같은 제한 표현이 반드시 필요하다.
 *
 * =================================================
 * STRONG_DIRECT
 * =================================================
 *
 * Java deterministic 근거가 충분하여
 * 일반 Claim보다 강한 표현을 허용한다.
 *
 * qualification은 필수가 아니다.
 *
 * =================================================
 * STRONG_QUALIFIED
 * =================================================
 *
 * 강한 근거는 존재하지만
 * 동시에 제한조건도 존재한다.
 *
 * 예:
 *
 * 구조적으로 강하게 확인된 Fact
 * +
 * 출생시간 미상 등 완전성 제한
 *
 * 이 경우:
 *
 * 강한 근거 자체를 없애지는 않되
 * 제한조건을 반드시 함께 표현한다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 enum은 문체 취향이 아니다.
 *
 * Java가 확정한 Meaning Boundary다.
 *
 * AI는 이 값을:
 *
 * - 승격
 * - 강등
 * - 변경
 *
 * 할 수 없다.
 */
public enum ConsultationMeaningExpressionMode {

    DIRECT(
            false,
            false
    ),

    QUALIFIED(
            false,
            true
    ),

    STRONG_DIRECT(
            true,
            false
    ),

    STRONG_QUALIFIED(
            true,
            true
    );

    private final boolean strongExpressionAllowed;

    private final boolean qualificationRequired;

    ConsultationMeaningExpressionMode(

            boolean strongExpressionAllowed,

            boolean qualificationRequired

    ) {

        this.strongExpressionAllowed =
                strongExpressionAllowed;

        this.qualificationRequired =
                qualificationRequired;
    }

    /**
     * 기존 두 boolean 경계를
     * 하나의 공식 Expression Mode로 변환한다.
     */
    public static ConsultationMeaningExpressionMode of(

            boolean strongExpressionAllowed,

            boolean qualificationRequired

    ) {

        if (
                strongExpressionAllowed
        ) {

            return qualificationRequired

                    ? STRONG_QUALIFIED

                    : STRONG_DIRECT;
        }

        return qualificationRequired

                ? QUALIFIED

                : DIRECT;
    }

    public boolean strongExpressionAllowed() {

        return strongExpressionAllowed;
    }

    public boolean qualificationRequired() {

        return qualificationRequired;
    }

    public boolean strong() {

        return strongExpressionAllowed;
    }

    public boolean qualified() {

        return qualificationRequired;
    }

    public boolean directWithoutQualification() {

        return this
                == DIRECT

                ||

                this
                        == STRONG_DIRECT;
    }
}