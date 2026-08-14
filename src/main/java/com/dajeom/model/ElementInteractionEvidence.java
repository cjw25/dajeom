package com.dajeom.model;

import com.dajeom.type.ElementInteractionType;
import com.dajeom.type.FiveElement;

import java.util.Objects;

/**
 * 두 오행 사이의 하나의 생극 작용 근거.
 *
 * 예:
 *
 * 金 324
 * 水 182
 *
 * 金生水
 *
 * source = 金
 * target = 水
 *
 * sourcePower = 324
 * targetPower = 182
 *
 * relationCapacity = min(324, 182)
 *                  = 182
 *
 * relationCapacity는
 * 최종 세력에 그대로 더하거나 빼는 점수가 아니다.
 *
 * 두 오행이 실제로 관계를 형성할 수 있는
 * 상대적인 작용 가능량을 뜻한다.
 *
 * 최종 반영 비율은 3-7에서
 * 다른 근거와 함께 결정한다.
 */
public record ElementInteractionEvidence(

        ElementInteractionType interactionType,

        FiveElement sourceElement,

        FiveElement targetElement,

        int sourcePower,

        int targetPower,

        int relationCapacity,

        int sourceShareRate,

        int targetShareRate

) {

    public ElementInteractionEvidence {

        Objects.requireNonNull(
                interactionType,
                "오행 작용 종류가 필요합니다."
        );

        Objects.requireNonNull(
                sourceElement,
                "작용을 보내는 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "작용을 받는 오행이 필요합니다."
        );

        if (
                sourceElement == targetElement
        ) {

            throw new IllegalArgumentException(
                    "같은 오행끼리는 생극 Evidence를 만들 수 없습니다."
            );
        }

        if (
                sourcePower < 0
                        || targetPower < 0
        ) {

            throw new IllegalArgumentException(
                    "오행 세력은 음수가 될 수 없습니다."
            );
        }

        if (
                relationCapacity < 0
        ) {

            throw new IllegalArgumentException(
                    "관계 작용량은 음수가 될 수 없습니다."
            );
        }

        if (
                relationCapacity
                        > Math.min(
                        sourcePower,
                        targetPower
                )
        ) {

            throw new IllegalArgumentException(
                    "관계 작용량은 두 오행 중 더 약한 세력을 초과할 수 없습니다."
            );
        }

        if (
                sourceShareRate < 0
                        || sourceShareRate > 100
                        || targetShareRate < 0
                        || targetShareRate > 100
        ) {

            throw new IllegalArgumentException(
                    "오행 비율은 0~100 사이여야 합니다."
            );
        }

        if (
                sourcePower + targetPower > 0
                        && sourceShareRate
                        + targetShareRate
                        != 100
        ) {

            throw new IllegalArgumentException(
                    "source/target 비율 합계는 100이어야 합니다."
            );
        }
    }

    public boolean generating() {

        return interactionType.generating();
    }

    public boolean controlling() {

        return interactionType.controlling();
    }

    /**
     * 두 오행 중 source가 더 강한 상태.
     */
    public boolean sourceStronger() {

        return sourcePower > targetPower;
    }

    /**
     * target이 더 강한 상태.
     */
    public boolean targetStronger() {

        return targetPower > sourcePower;
    }

    /**
     * 두 오행의 현재 세력이 같은 상태.
     */
    public boolean equalPower() {

        return sourcePower == targetPower;
    }

    /**
     * source 또는 target이 아예 없어
     * 실제 작용량이 0인지.
     */
    public boolean inactive() {

        return relationCapacity == 0;
    }

    /**
     * 실제 작용 관계가 존재하는지.
     */
    public boolean active() {

        return relationCapacity > 0;
    }
}