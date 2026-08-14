package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.SeasonalPowerState;

import java.util.Objects;

/**
 * 오행 하나의 최종 세력이
 * 어떻게 만들어졌는지를 보존한다.
 *
 * 예:
 *
 * 金
 *
 * 표면              100
 * 지장간            170
 * 원시합계           270
 * 계절 旺            120%
 * 계절적용           324
 * 통근               +16
 * 투간               +6
 * 생조               +14
 * 설기               -9
 * 피극               -9
 * 극 사용            -5
 *
 * 최종               XXX
 *
 * 형태로 계산 근거 전체를
 * 추적할 수 있도록 한다.
 */
public record ElementPowerEvidence(

        FiveElement element,

        int visibleBasePower,

        int hiddenBasePower,

        int rawBasePower,

        SeasonalPowerState seasonalState,

        int seasonalRate,

        int seasonalAdjustedPower,

        int rootActivationIndex,

        int rootActivationRate,

        int rootAdjustment,

        int exposureActivationRate,

        int exposureAdjustment,

        int generationSupportCapacity,

        int generationSupportAdjustment,

        int generationDrainCapacity,

        int generationDrainAdjustment,

        int controlPressureCapacity,

        int controlPressureAdjustment,

        int controlExertionCapacity,

        int controlExertionAdjustment,

        int netInteractionAdjustment,

        int finalPower,

        int shareBasisPoints

) {

    public ElementPowerEvidence {

        Objects.requireNonNull(
                element,
                "오행이 필요합니다."
        );

        Objects.requireNonNull(
                seasonalState,
                "계절 상태가 필요합니다."
        );

        if (
                visibleBasePower < 0
                        || hiddenBasePower < 0
                        || rawBasePower < 0
                        || seasonalAdjustedPower < 0
                        || rootActivationIndex < 0
                        || rootAdjustment < 0
                        || exposureAdjustment < 0
                        || generationSupportCapacity < 0
                        || generationSupportAdjustment < 0
                        || generationDrainCapacity < 0
                        || generationDrainAdjustment < 0
                        || controlPressureCapacity < 0
                        || controlPressureAdjustment < 0
                        || controlExertionCapacity < 0
                        || controlExertionAdjustment < 0
                        || finalPower < 0
        ) {

            throw new IllegalArgumentException(
                    "오행 세력 계산값은 음수가 될 수 없습니다."
            );
        }

        if (
                rawBasePower
                        != visibleBasePower
                        + hiddenBasePower
        ) {

            throw new IllegalArgumentException(
                    "오행 원시 세력이 표면+지장간 합계와 일치하지 않습니다."
            );
        }

        if (
                seasonalRate <= 0
        ) {

            throw new IllegalArgumentException(
                    "계절 보정률은 0보다 커야 합니다."
            );
        }

        if (
                rootActivationRate < 0
                        || rootActivationRate > 100
        ) {

            throw new IllegalArgumentException(
                    "통근 활성률은 0~100 사이여야 합니다."
            );
        }

        if (
                exposureActivationRate < 0
                        || exposureActivationRate > 100
        ) {

            throw new IllegalArgumentException(
                    "투간 활성률은 0~100 사이여야 합니다."
            );
        }

        if (
                shareBasisPoints < 0
                        || shareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 점유율은 0~10000bp 사이여야 합니다."
            );
        }

        int calculatedNetInteraction =
                generationSupportAdjustment
                        - generationDrainAdjustment
                        - controlPressureAdjustment
                        - controlExertionAdjustment;

        if (
                calculatedNetInteraction
                        != netInteractionAdjustment
        ) {

            throw new IllegalArgumentException(
                    "생극 순조정값이 세부 조정값과 일치하지 않습니다."
            );
        }
    }

    /**
     * 원국에 실제로 존재하는 오행인지.
     */
    public boolean exists() {

        return rawBasePower > 0;
    }

    /**
     * 최종 점유율을 % 단위로 반환.
     *
     * 10000bp = 100.00%
     */
    public double sharePercent() {

        return shareBasisPoints
                / 100.0;
    }

    public boolean rooted() {

        return rootActivationIndex > 0;
    }

    public boolean exposed() {

        return exposureActivationRate > 0;
    }

    public boolean seasonallyProsperous() {

        return seasonalState.prosperous();
    }

    public boolean seasonallyRestricted() {

        return seasonalState
                .seasonallyRestricted();
    }

    /**
     * 생극 관계에서 최종적으로
     * 순수하게 힘을 얻었는지.
     */
    public boolean interactionNetPositive() {

        return netInteractionAdjustment > 0;
    }

    /**
     * 생극 관계에서 최종적으로
     * 소모·압력이 더 큰지.
     */
    public boolean interactionNetNegative() {

        return netInteractionAdjustment < 0;
    }
}