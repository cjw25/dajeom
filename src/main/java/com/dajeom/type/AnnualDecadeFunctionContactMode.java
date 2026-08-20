package com.dajeom.type;

/**
 * 하나의 Function 축이
 * 대운과 세운에서 각각 활성되어 있는지를 나타낸다.
 *
 * =================================================
 *
 * INACTIVE
 *
 * 대운 X
 * 세운 X
 *
 * =================================================
 *
 * DECADE_ONLY
 *
 * 대운 O
 * 세운 X
 *
 * =================================================
 *
 * ANNUAL_ONLY
 *
 * 대운 X
 * 세운 O
 *
 * =================================================
 *
 * DECADE_AND_ANNUAL
 *
 * 대운 O
 * 세운 O
 *
 * =================================================
 *
 * 중요:
 *
 * DECADE_AND_ANNUAL은
 *
 * - 강화 확정
 * - 좋은 운
 * - 강한 사건
 *
 * 을 의미하지 않는다.
 *
 * 동일 Function이 두 시간 Layer에서
 * 동시에 활성되어 있다는 구조만 보존한다.
 */
public enum AnnualDecadeFunctionContactMode {

    INACTIVE(
            false,
            false
    ),

    DECADE_ONLY(
            true,
            false
    ),

    ANNUAL_ONLY(
            false,
            true
    ),

    DECADE_AND_ANNUAL(
            true,
            true
    );

    private final boolean decadeActive;

    private final boolean annualActive;

    AnnualDecadeFunctionContactMode(

            boolean decadeActive,

            boolean annualActive

    ) {

        this.decadeActive =
                decadeActive;

        this.annualActive =
                annualActive;
    }

    public boolean decadeActive() {

        return decadeActive;
    }

    public boolean annualActive() {

        return annualActive;
    }

    public boolean active() {

        return decadeActive
                || annualActive;
    }

    public boolean inactive() {

        return this == INACTIVE;
    }

    public boolean decadeOnly() {

        return this == DECADE_ONLY;
    }

    public boolean annualOnly() {

        return this == ANNUAL_ONLY;
    }

    public boolean bothActive() {

        return this == DECADE_AND_ANNUAL;
    }

    public static AnnualDecadeFunctionContactMode of(

            boolean decadeActive,

            boolean annualActive

    ) {

        if (
                decadeActive
                        && annualActive
        ) {

            return DECADE_AND_ANNUAL;
        }

        if (
                decadeActive
        ) {

            return DECADE_ONLY;
        }

        if (
                annualActive
        ) {

            return ANNUAL_ONLY;
        }

        return INACTIVE;
    }
}