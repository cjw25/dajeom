package com.dajeom.rule;

import org.springframework.stereotype.Component;

/**
 * 오행 최종 세력 계산 규칙.
 *
 * 이 클래스의 수치는
 * 자연과학적 확률이나 절대적인 명리 수치가 아니다.
 *
 * 다점 명리엔진에서:
 *
 * - 월령
 * - 지장간
 * - 통근
 * - 투간
 * - 생조
 * - 설기
 * - 극제
 *
 * 를 중복 계산하지 않으면서
 * 상대적인 오행 세력으로 합성하기 위한
 * 내부 계산 정책이다.
 *
 * Analyzer에 숫자를 직접 하드코딩하지 않고
 * Rule로 분리하여 이후 정밀화가 가능하도록 한다.
 */
@Component
public class ElementPowerRule {

    /**
     * 통근으로 인해 표면 천간이
     * 받을 수 있는 최대 활성 보정률.
     *
     * 20 = 최대 20%
     */
    private static final int
            ROOT_MAX_BONUS_RATE =
            20;

    /**
     * 지장간이 투간했을 때
     * hidden power가 받을 수 있는
     * 최대 활성 보정률.
     *
     * 10 = 최대 10%
     */
    private static final int
            EXPOSURE_MAX_BONUS_RATE =
            10;

    /**
     * 생을 받는 오행의 보정률.
     */
    private static final int
            GENERATION_SUPPORT_RATE =
            10;

    /**
     * 다른 오행을 생하면서 발생하는
     * 설기 보정률.
     *
     * 생했다고 source 세력이
     * 동일량만큼 사라지는 것은 아니므로
     * support보다 작게 반영한다.
     */
    private static final int
            GENERATION_DRAIN_RATE =
            5;

    /**
     * 극을 받는 target의 압력 반영률.
     */
    private static final int
            CONTROL_PRESSURE_RATE =
            10;

    /**
     * 다른 오행을 극하기 위해
     * source가 힘을 사용하는 정도.
     */
    private static final int
            CONTROL_EXERTION_RATE =
            5;

    /**
     * 통근 활성률.
     *
     * rootIndex
     * ---------
     * hiddenBasePower
     *
     * 를 0~100으로 정규화한다.
     *
     * 통근 index는 위치 가중을 포함하므로
     * 100을 초과할 수 있지만
     * 최종 활성률은 100으로 제한한다.
     */
    public int calculateRootActivationRate(

            int rootIndex,

            int hiddenBasePower

    ) {

        if (
                rootIndex <= 0
                        || hiddenBasePower <= 0
        ) {

            return 0;
        }

        int rate =
                (int) Math.round(
                        rootIndex
                                * 100.0
                                / hiddenBasePower
                );

        return clampPercent(
                rate
        );
    }

    /**
     * 통근 보정.
     *
     * 지장간 자체를 다시 더하는 것이 아니라
     * 표면 천간의 세력만 활성화한다.
     */
    public int calculateRootAdjustment(

            int visibleBasePower,

            int rootActivationRate

    ) {

        if (
                visibleBasePower <= 0
                        || rootActivationRate <= 0
        ) {

            return 0;
        }

        return applyTwoRates(
                visibleBasePower,
                rootActivationRate,
                ROOT_MAX_BONUS_RATE
        );
    }

    /**
     * 투간 활성 보정.
     *
     * hiddenBasePower 자체는 이미 3-2에서
     * 원시 세력에 포함되어 있다.
     *
     * 따라서 실제 투간된 비율에 대해
     * 제한된 활성 보정만 추가한다.
     */
    public int calculateExposureAdjustment(

            int hiddenBasePower,

            int activationRate

    ) {

        if (
                hiddenBasePower <= 0
                        || activationRate <= 0
        ) {

            return 0;
        }

        return applyTwoRates(
                hiddenBasePower,
                activationRate,
                EXPOSURE_MAX_BONUS_RATE
        );
    }

    /**
     * 다른 오행으로부터 생을 받은
     * 실제 보정량.
     */
    public int calculateGenerationSupportAdjustment(
            int supportCapacity
    ) {

        return applyRate(
                supportCapacity,
                GENERATION_SUPPORT_RATE
        );
    }

    /**
     * 다른 오행을 생하면서
     * 발생하는 설기량.
     */
    public int calculateGenerationDrainAdjustment(
            int drainCapacity
    ) {

        return applyRate(
                drainCapacity,
                GENERATION_DRAIN_RATE
        );
    }

    /**
     * 다른 오행에게 극을 받는 압력.
     */
    public int calculateControlPressureAdjustment(
            int pressureCapacity
    ) {

        return applyRate(
                pressureCapacity,
                CONTROL_PRESSURE_RATE
        );
    }

    /**
     * 다른 오행을 극하기 위해
     * 사용하는 힘.
     */
    public int calculateControlExertionAdjustment(
            int exertionCapacity
    ) {

        return applyRate(
                exertionCapacity,
                CONTROL_EXERTION_RATE
        );
    }

    /**
     * 최종 오행 세력.
     *
     * 계절 적용 세력
     * +
     * 통근 활성
     * +
     * 투간 활성
     * +
     * 생조
     * -
     * 설기
     * -
     * 피극
     * -
     * 극제에 사용한 힘
     */
    public int calculateFinalPower(

            int seasonalAdjustedPower,

            int rootAdjustment,

            int exposureAdjustment,

            int generationSupportAdjustment,

            int generationDrainAdjustment,

            int controlPressureAdjustment,

            int controlExertionAdjustment,

            boolean elementExists

    ) {

        long calculated =
                (long) seasonalAdjustedPower

                        + rootAdjustment

                        + exposureAdjustment

                        + generationSupportAdjustment

                        - generationDrainAdjustment

                        - controlPressureAdjustment

                        - controlExertionAdjustment;

        /*
         * 실제 원국에 존재하는 오행이라면
         * 관계 계산 때문에 완전히 0 이하로
         * 사라지지는 않도록 최소 1을 유지한다.
         *
         * 원국 자체에 존재하지 않는 오행은
         * 0을 유지한다.
         */
        int minimum =
                elementExists
                        ? 1
                        : 0;

        if (
                calculated <= minimum
        ) {

            return minimum;
        }

        if (
                calculated > Integer.MAX_VALUE
        ) {

            return Integer.MAX_VALUE;
        }

        return (int) calculated;
    }

    private int applyRate(

            int value,

            int rate

    ) {

        if (
                value <= 0
                        || rate <= 0
        ) {

            return 0;
        }

        return Math.max(
                0,
                (int) Math.round(
                        value
                                * (
                                rate / 100.0
                        )
                )
        );
    }

    /**
     * value
     * × firstRate%
     * × secondRate%
     */
    private int applyTwoRates(

            int value,

            int firstRate,

            int secondRate

    ) {

        if (
                value <= 0
                        || firstRate <= 0
                        || secondRate <= 0
        ) {

            return 0;
        }

        double calculated =
                value
                        * (
                        firstRate / 100.0
                )
                        * (
                        secondRate / 100.0
                );

        return Math.max(
                0,
                (int) Math.round(
                        calculated
                )
        );
    }

    private int clampPercent(
            int rate
    ) {

        return Math.max(
                0,
                Math.min(
                        100,
                        rate
                )
        );
    }
}