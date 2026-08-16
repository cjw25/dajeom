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
 *
 * -------------------------------------------------
 *
 * 구조 보정 계약
 *
 * seasonalAdjustment
 * + groundAdjustment
 * + forceAdjustment
 *
 * 의 합계는 구조 보정의 원시 합계이다.
 *
 * 실제 적용되는 structuralAdjustment는
 * 이 원시 합계에 정밀화 4차-8 정책의
 * 최대 +/-800bp 제한을 적용한 값이다.
 *
 * 즉:
 *
 * rawStructuralAdjustment
 * =
 * seasonalAdjustment
 * + groundAdjustment
 * + forceAdjustment
 *
 * structuralAdjustment
 * =
 * clamp(
 *     rawStructuralAdjustment,
 *     -800,
 *     +800
 * )
 *
 * adjustedStrength
 * =
 * clamp(
 *     rawSupportShare
 *     + structuralAdjustment,
 *     0,
 *     10000
 * )
 *
 * 이 구조를 통해
 * 3차 세력 계산에서 이미 반영된
 * 계절·통근·투간 등의 영향이
 * 최종 신강·신약 판정 단계에서
 * 과도하게 중복 가산되는 것을 방지한다.
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

    /**
     * 정밀화 4차-8 구조 보정의 최대 절대값.
     *
     * 800bp = 8%.
     *
     * DayMasterStrengthRule의
     * 구조 보정 정책과 동일한 계약이다.
     */
    private static final int
            MAX_STRUCTURAL_ADJUSTMENT_BASIS_POINTS =
            800;

    public DayMasterStrengthEvidence {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        /*
         * ------------------------------------------------
         * 실제 지원력 / 압력 검증
         * ------------------------------------------------
         */

        if (
                totalSupportPower < 0
                        || totalPressurePower < 0
        ) {

            throw new IllegalArgumentException(
                    "지원력과 압력은 음수가 될 수 없습니다."
            );
        }

        /*
         * ------------------------------------------------
         * 지원 / 압력 / 최종 지수 범위 검증
         * ------------------------------------------------
         *
         * 10000bp = 100%.
         */

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

        /*
         * ------------------------------------------------
         * 지원 / 압력 비중 합계 검증
         * ------------------------------------------------
         */

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

        /*
         * ------------------------------------------------
         * 세부 구조 보정값 기본 범위 검증
         * ------------------------------------------------
         *
         * seasonal / ground / force는
         * 각각의 Rule에서 계산된 원시 구조 보정 근거이다.
         *
         * 현재 실제 정책값은 이보다 훨씬 작지만,
         * Evidence의 기본적인 방어 범위로
         * +/-10000bp를 유지한다.
         */

        if (
                seasonalAdjustmentBasisPoints < -10_000
                        || seasonalAdjustmentBasisPoints > 10_000

                        || groundAdjustmentBasisPoints < -10_000
                        || groundAdjustmentBasisPoints > 10_000

                        || forceAdjustmentBasisPoints < -10_000
                        || forceAdjustmentBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "세부 구조 보정값의 범위가 올바르지 않습니다."
            );
        }

        /*
         * ------------------------------------------------
         * 최종 구조 보정 범위 검증
         * ------------------------------------------------
         *
         * 세부 구조 보정의 원시 합계와 달리
         * 실제 적용되는 structuralAdjustment는
         * 반드시 +/-800bp 범위 안에 있어야 한다.
         */

        if (
                structuralAdjustmentBasisPoints
                        < -MAX_STRUCTURAL_ADJUSTMENT_BASIS_POINTS
                        || structuralAdjustmentBasisPoints
                        > MAX_STRUCTURAL_ADJUSTMENT_BASIS_POINTS
        ) {

            throw new IllegalArgumentException(
                    "전체 구조 보정값은 -800~800bp 사이여야 합니다."
            );
        }

        /*
         * ------------------------------------------------
         * 구조 보정 계약 검증
         * ------------------------------------------------
         *
         * 기존 계약:
         *
         * structuralAdjustment
         * =
         * seasonal
         * + ground
         * + force
         *
         * 는 정밀화 4차-8의 상한/하한 정책을
         * 반영하지 못한다.
         *
         * 실제 계약:
         *
         * rawStructuralAdjustment
         * =
         * seasonal
         * + ground
         * + force
         *
         * structuralAdjustment
         * =
         * clamp(
         *     rawStructuralAdjustment,
         *     -800,
         *     +800
         * )
         */

        int rawStructuralAdjustmentBasisPoints =
                seasonalAdjustmentBasisPoints
                        + groundAdjustmentBasisPoints
                        + forceAdjustmentBasisPoints;

        int expectedStructuralAdjustmentBasisPoints =
                clampStructuralAdjustmentBasisPoints(
                        rawStructuralAdjustmentBasisPoints
                );

        if (
                structuralAdjustmentBasisPoints
                        != expectedStructuralAdjustmentBasisPoints
        ) {

            throw new IllegalArgumentException(
                    "전체 구조 보정값이 세부 보정값 합계에 "
                            + "구조 보정 상한/하한을 적용한 결과와 일치하지 않습니다."
            );
        }

        /*
         * ------------------------------------------------
         * 최종 신강 지수 검증
         * ------------------------------------------------
         *
         * adjustedStrength
         * =
         * clamp(
         *     rawSupportShare
         *     + structuralAdjustment
         * )
         */

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

        /*
         * ------------------------------------------------
         * 극단 구조 후보 관계 검증
         * ------------------------------------------------
         *
         * 종격 후보는 일반 극단 구조 후보보다
         * 더 엄격한 조건을 통과한 상태이므로
         * 반드시 extremeStructureCandidate를
         * 먼저 만족해야 한다.
         */

        if (
                followStructureCandidate
                        && !extremeStructureCandidate
        ) {

            throw new IllegalArgumentException(
                    "종격 후보는 극단 구조 후보를 먼저 만족해야 합니다."
            );
        }
    }

    /**
     * 구조 보정 전용 clamp.
     *
     * 정밀화 4차-8 정책:
     *
     * -800bp ~ +800bp
     */
    private static int clampStructuralAdjustmentBasisPoints(
            int value
    ) {

        return Math.max(
                -MAX_STRUCTURAL_ADJUSTMENT_BASIS_POINTS,
                Math.min(
                        MAX_STRUCTURAL_ADJUSTMENT_BASIS_POINTS,
                        value
                )
        );
    }

    /**
     * 일반 비중 값 clamp.
     *
     * 0bp ~ 10000bp.
     */
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