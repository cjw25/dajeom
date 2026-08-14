package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;

import java.util.Objects;

/**
 * 정밀화 6차-1.
 *
 * 한난조습 환경이 어떻게 계산됐는지
 * 내부 근거를 보존한다.
 *
 * =================================================
 *
 * temperature
 *
 * 음수 -> 寒
 * 양수 -> 暖/熱
 *
 * =================================================
 *
 * moisture
 *
 * 음수 -> 燥
 * 양수 -> 濕
 *
 * =================================================
 *
 * seasonal...
 *
 * 월령 자체의 계절 기후.
 *
 * elemental...Adjustment
 *
 * 원국 전체 오행 세력에 의한
 * 보정값.
 *
 * final...
 *
 * seasonal + elemental adjustment.
 *
 * 모든 값은 -10000 ~ +10000 범위의
 * 내부 상대지표다.
 *
 * 실제 기온·습도·확률이 아니다.
 */
public record ClimateEnvironmentEvidence(

        EarthlyBranch monthBranch,

        int seasonalTemperatureBasisPoints,

        int elementalTemperatureAdjustmentBasisPoints,

        int finalTemperatureBasisPoints,

        int seasonalMoistureBasisPoints,

        int elementalMoistureAdjustmentBasisPoints,

        int finalMoistureBasisPoints,

        int woodShareBasisPoints,

        int fireShareBasisPoints,

        int earthShareBasisPoints,

        int metalShareBasisPoints,

        int waterShareBasisPoints

) {

    public ClimateEnvironmentEvidence {

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        validateSignedBasisPoints(
                seasonalTemperatureBasisPoints,
                "월령 한난값"
        );

        validateSignedBasisPoints(
                elementalTemperatureAdjustmentBasisPoints,
                "오행 한난 보정값"
        );

        validateSignedBasisPoints(
                finalTemperatureBasisPoints,
                "최종 한난값"
        );

        validateSignedBasisPoints(
                seasonalMoistureBasisPoints,
                "월령 조습값"
        );

        validateSignedBasisPoints(
                elementalMoistureAdjustmentBasisPoints,
                "오행 조습 보정값"
        );

        validateSignedBasisPoints(
                finalMoistureBasisPoints,
                "최종 조습값"
        );

        validateShare(
                woodShareBasisPoints,
                "목"
        );

        validateShare(
                fireShareBasisPoints,
                "화"
        );

        validateShare(
                earthShareBasisPoints,
                "토"
        );

        validateShare(
                metalShareBasisPoints,
                "금"
        );

        validateShare(
                waterShareBasisPoints,
                "수"
        );

        int totalShare =
                woodShareBasisPoints
                        + fireShareBasisPoints
                        + earthShareBasisPoints
                        + metalShareBasisPoints
                        + waterShareBasisPoints;

        if (
                totalShare != 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 비중 합계는 10000bp여야 합니다: "
                            + totalShare
            );
        }

        int expectedTemperature =
                clampSigned(
                        seasonalTemperatureBasisPoints
                                + elementalTemperatureAdjustmentBasisPoints
                );

        if (
                finalTemperatureBasisPoints
                        != expectedTemperature
        ) {

            throw new IllegalArgumentException(
                    "최종 한난값이 월령값과 오행 보정값의 합과 일치하지 않습니다."
            );
        }

        int expectedMoisture =
                clampSigned(
                        seasonalMoistureBasisPoints
                                + elementalMoistureAdjustmentBasisPoints
                );

        if (
                finalMoistureBasisPoints
                        != expectedMoisture
        ) {

            throw new IllegalArgumentException(
                    "최종 조습값이 월령값과 오행 보정값의 합과 일치하지 않습니다."
            );
        }
    }

    private static void validateSignedBasisPoints(

            int value,

            String name

    ) {

        if (
                value < -10_000
                        || value > 10_000
        ) {

            throw new IllegalArgumentException(
                    name
                            + "은 -10000~10000 사이여야 합니다."
            );
        }
    }

    private static void validateShare(

            int value,

            String elementName

    ) {

        if (
                value < 0
                        || value > 10_000
        ) {

            throw new IllegalArgumentException(
                    elementName
                            + " 오행 비중은 0~10000bp 사이여야 합니다."
            );
        }
    }

    private static int clampSigned(
            int value
    ) {

        return Math.max(
                -10_000,
                Math.min(
                        10_000,
                        value
                )
        );
    }

    public boolean seasonalCold() {

        return seasonalTemperatureBasisPoints < 0;
    }

    public boolean seasonalWarm() {

        return seasonalTemperatureBasisPoints > 0;
    }

    public boolean seasonalDry() {

        return seasonalMoistureBasisPoints < 0;
    }

    public boolean seasonalWet() {

        return seasonalMoistureBasisPoints > 0;
    }

    public boolean elementAdjustmentWarms() {

        return elementalTemperatureAdjustmentBasisPoints > 0;
    }

    public boolean elementAdjustmentCools() {

        return elementalTemperatureAdjustmentBasisPoints < 0;
    }

    public boolean elementAdjustmentMoistens() {

        return elementalMoistureAdjustmentBasisPoints > 0;
    }

    public boolean elementAdjustmentDries() {

        return elementalMoistureAdjustmentBasisPoints < 0;
    }

    public double temperaturePercent() {

        return finalTemperatureBasisPoints
                / 100.0;
    }

    public double moisturePercent() {

        return finalMoistureBasisPoints
                / 100.0;
    }
}