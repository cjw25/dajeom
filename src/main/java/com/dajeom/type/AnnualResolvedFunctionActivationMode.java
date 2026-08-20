package com.dajeom.type;

/**
 * 세운 Segment에서 하나의 Function이
 * 어떤 시간 Layer를 통해 활성되는지 보존한다.
 *
 * =================================================
 *
 * 활성 Source:
 *
 * 1.
 * DECADE
 *
 * 기존 대운 Functional 기준선.
 *
 * 2.
 * ANNUAL
 *
 * 원국 × 세운 자체 Functional 결과.
 *
 * 3.
 * CROSS_LUCK_TARGET
 *
 * 대운 × 세운 14-5 Transformation의
 * 활성 Target Function.
 *
 * =================================================
 *
 * 중요:
 *
 * 여러 Source가 동시에 존재한다고 해서
 * 강도 점수를 더하지 않는다.
 *
 * 이것은 Activation Provenance다.
 */
public enum AnnualResolvedFunctionActivationMode {

    INACTIVE(
            false,
            false,
            false
    ),

    DECADE_ONLY(
            true,
            false,
            false
    ),

    ANNUAL_ONLY(
            false,
            true,
            false
    ),

    CROSS_LUCK_TARGET_ONLY(
            false,
            false,
            true
    ),

    DECADE_AND_ANNUAL(
            true,
            true,
            false
    ),

    DECADE_AND_CROSS_LUCK_TARGET(
            true,
            false,
            true
    ),

    ANNUAL_AND_CROSS_LUCK_TARGET(
            false,
            true,
            true
    ),

    DECADE_ANNUAL_AND_CROSS_LUCK_TARGET(
            true,
            true,
            true
    );

    private final boolean decadeActive;

    private final boolean annualActive;

    private final boolean crossLuckTargetActive;

    AnnualResolvedFunctionActivationMode(

            boolean decadeActive,

            boolean annualActive,

            boolean crossLuckTargetActive

    ) {

        this.decadeActive =
                decadeActive;

        this.annualActive =
                annualActive;

        this.crossLuckTargetActive =
                crossLuckTargetActive;
    }

    public boolean decadeActive() {

        return decadeActive;
    }

    public boolean annualActive() {

        return annualActive;
    }

    public boolean crossLuckTargetActive() {

        return crossLuckTargetActive;
    }

    public boolean active() {

        return decadeActive
                || annualActive
                || crossLuckTargetActive;
    }

    public boolean inactive() {

        return this == INACTIVE;
    }

    public boolean multipleActivationSources() {

        int count =
                (decadeActive ? 1 : 0)
                        + (annualActive ? 1 : 0)
                        + (crossLuckTargetActive ? 1 : 0);

        return count >= 2;
    }

    public boolean crossLuckTargetOnly() {

        return this == CROSS_LUCK_TARGET_ONLY;
    }

    public static AnnualResolvedFunctionActivationMode of(

            boolean decadeActive,

            boolean annualActive,

            boolean crossLuckTargetActive

    ) {

        if (
                decadeActive
                        && annualActive
                        && crossLuckTargetActive
        ) {

            return DECADE_ANNUAL_AND_CROSS_LUCK_TARGET;
        }

        if (
                decadeActive
                        && annualActive
        ) {

            return DECADE_AND_ANNUAL;
        }

        if (
                decadeActive
                        && crossLuckTargetActive
        ) {

            return DECADE_AND_CROSS_LUCK_TARGET;
        }

        if (
                annualActive
                        && crossLuckTargetActive
        ) {

            return ANNUAL_AND_CROSS_LUCK_TARGET;
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

        if (
                crossLuckTargetActive
        ) {

            return CROSS_LUCK_TARGET_ONLY;
        }

        return INACTIVE;
    }
}