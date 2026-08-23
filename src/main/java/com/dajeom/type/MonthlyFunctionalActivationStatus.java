package com.dajeom.type;

/**
 * 월운이 공식 Annual Functional Baseline에
 * 어떤 방식으로 접촉하는지를 나타내는 상태.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 enum은 길흉이 아니다.
 *
 * 또한:
 *
 * - 신강신약을 재계산하지 않는다.
 * - 용신을 재선정하지 않는다.
 * - Annual Functional Baseline을 변경하지 않는다.
 *
 * =================================================
 *
 * NO_CONTACT
 *
 * 현재 Function Family에
 * 월운 직접 입력도 없고
 * 구조화된 월운 Context도 없음.
 *
 * =================================================
 *
 * CONTACT
 *
 * 월운 직접 입력 또는 구조화된 Context가 있으나
 * 방향성까지 확정하지 않는 접촉.
 *
 * =================================================
 *
 * SUPPORTED
 *
 * 월운 Context가 현재 Function의 작동을
 * 지지하는 방향으로 접촉.
 *
 * =================================================
 *
 * QUALIFIED
 *
 * 월운 Context가 조건부 또는 제한을 동반한
 * 지지 방향으로 접촉.
 *
 * =================================================
 *
 * WEAKENED
 *
 * 월운 Context가 현재 Function의
 * 직접 작동을 약화하는 방향으로 접촉.
 *
 * =================================================
 *
 * CONFLICTED
 *
 * 월운 Context가 현재 Function에
 * 충돌 또는 마찰 방향으로 접촉.
 *
 * =================================================
 *
 * MIXED
 *
 * 지지/조건부 방향과
 * 약화/충돌 방향이 동시에 존재.
 */
public enum MonthlyFunctionalActivationStatus {

    NO_CONTACT,

    CONTACT,

    SUPPORTED,

    QUALIFIED,

    WEAKENED,

    CONFLICTED,

    MIXED;


    public boolean contacted() {

        return this
                != NO_CONTACT;
    }


    public boolean supportiveSide() {

        return this
                == SUPPORTED

                || this
                == QUALIFIED

                || this
                == MIXED;
    }


    public boolean restrictiveSide() {

        return this
                == WEAKENED

                || this
                == CONFLICTED

                || this
                == MIXED;
    }


    public boolean supported() {

        return this
                == SUPPORTED;
    }


    public boolean qualified() {

        return this
                == QUALIFIED;
    }


    public boolean weakened() {

        return this
                == WEAKENED;
    }


    public boolean conflicted() {

        return this
                == CONFLICTED;
    }


    public boolean mixed() {

        return this
                == MIXED;
    }


    /**
     * 월운 직접 입력과 Context Status를 이용해
     * 최종 월간 접촉 상태를 결정한다.
     *
     * 개수는 강도로 사용하지 않는다.
     *
     * 단순 존재 여부만 사용한다.
     */
    public static MonthlyFunctionalActivationStatus of(

            boolean monthlyDirectInput,

            boolean structuredContextExists,

            boolean supported,

            boolean qualified,

            boolean weakened,

            boolean conflicted

    ) {

        boolean supportiveSide =
                supported
                        || qualified;

        boolean restrictiveSide =
                weakened
                        || conflicted;


        if (
                supportiveSide
                        && restrictiveSide
        ) {

            return MIXED;
        }


        if (
                conflicted
        ) {

            return CONFLICTED;
        }


        if (
                weakened
        ) {

            return WEAKENED;
        }


        if (
                qualified
        ) {

            return QUALIFIED;
        }


        if (
                supported
        ) {

            return SUPPORTED;
        }


        if (
                monthlyDirectInput
                        || structuredContextExists
        ) {

            return CONTACT;
        }


        return NO_CONTACT;
    }
}