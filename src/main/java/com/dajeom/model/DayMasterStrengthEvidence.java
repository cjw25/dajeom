package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 정밀화 4차-8.
 *
 * 최종 신강·신약 판정에 사용된
 * 계산 근거를 보존한다.
 *
 * 중요한 설계 원칙:
 *
 * 4-5와 4-6에서 계산된 실제:
 *
 * - 지원력
 * - 압력
 *
 * 을 기본 수치로 사용한다.
 *
 * 4-2 득령
 * 4-3 득지
 * 4-4 득세
 *
 * 는 오행 세력을 다시 더하는 것이 아니라
 * 최종 분류 경계에 사용하는
 * 제한적인 구조 보정값으로만 사용한다.
 *
 * 따라서 지장간·계절·통근 등의
 * 원시 세력이 두 번 더해지는 것을 방지한다.
 */
public record DayMasterStrengthEvidence(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        int totalSupportPower,

        int totalPressurePower,

        int rawSupportShareBasisPoints,

        int rawPressureShareBasisPoints,

        int seasonalAdjustmentBasisPoints,

        int groundAdjustmentBasisPoints,

        int forceAdjustmentBasisPoints,

        int structuralAdjustmentBasisPoints,

        int adjustedStrengthBasisPoints,

        boolean extremeStructureCandidate,

        boolean followStructureCandidate

) {

    public DayMasterStrengthEvidence {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        if (
                totalSupportPower < 0
                        || totalPressurePower < 0
        ) {

            throw new IllegalArgumentException(
                    "지원력과 압력은 음수가 될 수 없습니다."
            );
        }

        if (
                rawSupportShareBasisPoints < 0
                        || rawSupportShareBasisPoints > 10_000
                        || rawPressureShareBasisPoints < 0
                        || rawPressureShareBasisPoints > 10_000
                        || adjustedStrengthBasisPoints < 0
                        || adjustedStrengthBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "신강·신약 비중은 0~10000bp 사이여야 합니다."
            );
        }

        int totalPower =
                totalSupportPower
                        + totalPressurePower;

        if (
                totalPower > 0
                        && rawSupportShareBasisPoints
                        + rawPressureShareBasisPoints
                        != 10_000
        ) {

            throw new IllegalArgumentException(
                    "지원/압력 비중의 합계는 10000bp여야 합니다."
            );
        }

        if (
                seasonalAdjustmentBasisPoints
                        < -10_000
                        || seasonalAdjustmentBasisPoints
                        > 10_000

                        || groundAdjustmentBasisPoints
                        < -10_000
                        || groundAdjustmentBasisPoints
                        > 10_000

                        || forceAdjustmentBasisPoints
                        < -10_000
                        || forceAdjustmentBasisPoints
                        > 10_000

                        || structuralAdjustmentBasisPoints
                        < -10_000
                        || structuralAdjustmentBasisPoints
                        > 10_000
        ) {

            throw new IllegalArgumentException(
                    "구조 보정값의 범위가 올바르지 않습니다."
            );
        }

        if (
                structuralAdjustmentBasisPoints
                        != seasonalAdjustmentBasisPoints
                        + groundAdjustmentBasisPoints
                        + forceAdjustmentBasisPoints
        ) {

            throw new IllegalArgumentException(
                    "전체 구조 보정값이 세부 보정값 합계와 일치하지 않습니다."
            );
        }

        int expectedAdjusted =
                clampBasisPoints(
                        rawSupportShareBasisPoints
                                + structuralAdjustmentBasisPoints
                );

        if (
                expectedAdjusted
                        != adjustedStrengthBasisPoints
        ) {

            throw new IllegalArgumentException(
                    "최종 신강 지수가 구조 보정 결과와 일치하지 않습니다."
            );
        }

        if (
                followStructureCandidate
                        && !extremeStructureCandidate
        ) {

            throw new IllegalArgumentException(
                    "종격 후보는 극단 구조 후보를 먼저 만족해야 합니다."
            );
        }
    }

    private static int clampBasisPoints(
            int value
    ) {

        return Math.max(
                0,
                Math.min(
                        10_000,
                        value
                )
        );
    }

    public double rawSupportSharePercent() {

        return rawSupportShareBasisPoints
                / 100.0;
    }

    public double rawPressureSharePercent() {

        return rawPressureShareBasisPoints
                / 100.0;
    }

    /**
     * 구조 보정까지 반영한
     * 일간의 상대적인 강도 지수.
     *
     * 10000bp = 100.
     *
     * 확률이 아니다.
     */
    public double adjustedStrengthPercent() {

        return adjustedStrengthBasisPoints
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

    public boolean equalPower() {

        return totalSupportPower
                == totalPressurePower;
    }

    public int powerDifference() {

        return totalSupportPower
                - totalPressurePower;
    }
}