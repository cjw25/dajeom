package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 정밀화 4차-7.
 *
 * 극단적인 신강·신약 구조를
 * 검토할 때 사용한 실제 근거.
 *
 * 여기서는:
 *
 * - 득령
 * - 계절 생조
 * - 통근
 * - 중심근
 * - 득세
 * - 비겁/인성 지원
 * - 식상/재성/관살 압력
 *
 * 을 한 객체에 모으되,
 *
 * 기존 계산값을 다시 계산하거나
 * 다시 가산하지 않는다.
 *
 * 이 객체는 판단 근거를
 * 추적하기 위한 Evidence다.
 */
public record ExtremeStrengthStructureEvidence(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        boolean directCommandGain,

        boolean seasonalSupport,

        boolean rootless,

        boolean centralGrounded,

        boolean strongCentralDirectRoot,

        boolean establishedForce,

        boolean widespreadForce,

        int companionSupportPower,

        int resourceSupportPower,

        int totalSupportPower,

        int outputDrainPower,

        int wealthExertionPower,

        int officerControlPower,

        int totalPressurePower,

        int supportShareBasisPoints,

        int pressureShareBasisPoints,

        DayMasterElementRole dominantPressureRole,

        int dominantPressureShareBasisPoints

) {

    public ExtremeStrengthStructureEvidence {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        if (
                companionSupportPower < 0
                        || resourceSupportPower < 0
                        || totalSupportPower < 0
                        || outputDrainPower < 0
                        || wealthExertionPower < 0
                        || officerControlPower < 0
                        || totalPressurePower < 0
        ) {

            throw new IllegalArgumentException(
                    "지원/압력 세력은 음수가 될 수 없습니다."
            );
        }

        if (
                totalSupportPower
                        != companionSupportPower
                        + resourceSupportPower
        ) {

            throw new IllegalArgumentException(
                    "전체 지원력이 비겁+인성 합계와 일치하지 않습니다."
            );
        }

        if (
                totalPressurePower
                        != outputDrainPower
                        + wealthExertionPower
                        + officerControlPower
        ) {

            throw new IllegalArgumentException(
                    "전체 압력이 식상+재성+관살 합계와 일치하지 않습니다."
            );
        }

        if (
                supportShareBasisPoints < 0
                        || supportShareBasisPoints > 10_000
                        || pressureShareBasisPoints < 0
                        || pressureShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "지원/압력 비중은 0~10000bp 사이여야 합니다."
            );
        }

        int totalPower =
                totalSupportPower
                        + totalPressurePower;

        if (
                totalPower > 0
                        && supportShareBasisPoints
                        + pressureShareBasisPoints
                        != 10_000
        ) {

            throw new IllegalArgumentException(
                    "지원/압력 비중 합계는 10000bp여야 합니다."
            );
        }

        if (
                dominantPressureShareBasisPoints < 0
                        || dominantPressureShareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "주도 압력 비중은 0~10000bp 사이여야 합니다."
            );
        }

        if (
                totalPressurePower <= 0
                        && dominantPressureRole != null
        ) {

            throw new IllegalArgumentException(
                    "압력이 없는데 주도 압력 역할이 존재할 수 없습니다."
            );
        }

        if (
                dominantPressureRole != null
                        && dominantPressureRole
                        != DayMasterElementRole.OUTPUT
                        && dominantPressureRole
                        != DayMasterElementRole.WEALTH
                        && dominantPressureRole
                        != DayMasterElementRole.OFFICER
        ) {

            throw new IllegalArgumentException(
                    "주도 압력은 식상·재성·관살 중 하나여야 합니다."
            );
        }
    }

    public double supportSharePercent() {

        return supportShareBasisPoints
                / 100.0;
    }

    public double pressureSharePercent() {

        return pressureShareBasisPoints
                / 100.0;
    }

    public double dominantPressureSharePercent() {

        return dominantPressureShareBasisPoints
                / 100.0;
    }

    public boolean supportDominant() {

        return totalSupportPower
                > totalPressurePower;
    }

    public boolean pressureDominant() {

        return totalPressurePower
                > totalSupportPower;
    }

    public boolean balancedByPower() {

        return totalSupportPower
                == totalPressurePower;
    }

    public boolean hasCompanionSupport() {

        return companionSupportPower > 0;
    }

    public boolean hasResourceSupport() {

        return resourceSupportPower > 0;
    }

    public boolean hasPressure() {

        return totalPressurePower > 0;
    }

    public boolean outputDominantPressure() {

        return dominantPressureRole
                == DayMasterElementRole.OUTPUT;
    }

    public boolean wealthDominantPressure() {

        return dominantPressureRole
                == DayMasterElementRole.WEALTH;
    }

    public boolean officerDominantPressure() {

        return dominantPressureRole
                == DayMasterElementRole.OFFICER;
    }
}