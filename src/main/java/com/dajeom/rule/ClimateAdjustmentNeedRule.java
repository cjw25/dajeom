package com.dajeom.rule;

import com.dajeom.model.ClimateEnvironmentAnalysis;
import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.ClimateCorrectionInteraction;
import com.dajeom.type.ClimateMitigationEffect;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 6차-2.
 *
 * 6-1 한난조습 결과를
 * 실제 조후 필요성 단계로 변환한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 아직 필요 오행을 결정하지 않는다.
 *
 * 2.
 * 한난과 조습을 따로 평가한다.
 *
 * 3.
 * overallNeed는 두 축 중 더 높은
 *    필요도를 그대로 사용한다.
 *
 * 4.
 * 복합 불균형이라고 임의로 점수를
 *    추가하지 않는다.
 *
 * 5.
 * 월령 baseline보다 최종 편차가 줄었다면
 *    원국 자체에 완화 작용이 있다고 본다.
 *
 * 6.
 * 반대 방향을 넘어가면 단순 완화가 아니라
 *    과보정으로 별도 기록한다.
 */
@Component
public class ClimateAdjustmentNeedRule {

    /*
     * =================================================
     * 한난 필요도 threshold
     * =================================================
     *
     * 6-1:
     *
     * |index| < 2500
     * -> 한난 중화
     *
     * 6000 이상
     * -> 극단 한/열
     */
    private static final int
            TEMPERATURE_LOW_THRESHOLD =
            2_500;

    private static final int
            TEMPERATURE_MODERATE_THRESHOLD =
            3_500;

    private static final int
            TEMPERATURE_HIGH_THRESHOLD =
            5_000;

    private static final int
            TEMPERATURE_CRITICAL_THRESHOLD =
            6_000;

    /*
     * =================================================
     * 조습 필요도 threshold
     * =================================================
     *
     * 6-1:
     *
     * |index| < 2000
     * -> 조습 중화
     *
     * 5000 이상
     * -> 극단 조/습
     */
    private static final int
            MOISTURE_LOW_THRESHOLD =
            2_000;

    private static final int
            MOISTURE_MODERATE_THRESHOLD =
            3_000;

    private static final int
            MOISTURE_HIGH_THRESHOLD =
            4_000;

    private static final int
            MOISTURE_CRITICAL_THRESHOLD =
            5_000;

    public ClimateAdjustmentNeedLevel
    determineTemperatureNeed(
            int finalTemperatureBasisPoints
    ) {

        validateSignedBasisPoints(
                finalTemperatureBasisPoints
        );

        int deviation =
                Math.abs(
                        finalTemperatureBasisPoints
                );

        if (
                deviation
                        < TEMPERATURE_LOW_THRESHOLD
        ) {

            return ClimateAdjustmentNeedLevel
                    .NONE;
        }

        if (
                deviation
                        < TEMPERATURE_MODERATE_THRESHOLD
        ) {

            return ClimateAdjustmentNeedLevel
                    .LOW;
        }

        if (
                deviation
                        < TEMPERATURE_HIGH_THRESHOLD
        ) {

            return ClimateAdjustmentNeedLevel
                    .MODERATE;
        }

        if (
                deviation
                        < TEMPERATURE_CRITICAL_THRESHOLD
        ) {

            return ClimateAdjustmentNeedLevel
                    .HIGH;
        }

        return ClimateAdjustmentNeedLevel
                .CRITICAL;
    }

    public ClimateAdjustmentNeedLevel
    determineMoistureNeed(
            int finalMoistureBasisPoints
    ) {

        validateSignedBasisPoints(
                finalMoistureBasisPoints
        );

        int deviation =
                Math.abs(
                        finalMoistureBasisPoints
                );

        if (
                deviation
                        < MOISTURE_LOW_THRESHOLD
        ) {

            return ClimateAdjustmentNeedLevel
                    .NONE;
        }

        if (
                deviation
                        < MOISTURE_MODERATE_THRESHOLD
        ) {

            return ClimateAdjustmentNeedLevel
                    .LOW;
        }

        if (
                deviation
                        < MOISTURE_HIGH_THRESHOLD
        ) {

            return ClimateAdjustmentNeedLevel
                    .MODERATE;
        }

        if (
                deviation
                        < MOISTURE_CRITICAL_THRESHOLD
        ) {

            return ClimateAdjustmentNeedLevel
                    .HIGH;
        }

        return ClimateAdjustmentNeedLevel
                .CRITICAL;
    }

    public ClimateAdjustmentNeedLevel
    determineOverallNeed(

            ClimateAdjustmentNeedLevel temperatureNeed,

            ClimateAdjustmentNeedLevel moistureNeed

    ) {

        Objects.requireNonNull(
                temperatureNeed,
                "한난 조정 필요도가 필요합니다."
        );

        Objects.requireNonNull(
                moistureNeed,
                "조습 조정 필요도가 필요합니다."
        );

        /*
         * 복합 불균형이라고
         * 임의로 한 단계를 올리지 않는다.
         *
         * 두 축은 evidence로 별도 보존하고
         * overall은 가장 높은 필요도를 사용한다.
         */
        return ClimateAdjustmentNeedLevel.max(
                temperatureNeed,
                moistureNeed
        );
    }

    /**
     * 월령 baseline과
     * 원국 오행 보정 이후 결과를 비교한다.
     */
    public ClimateMitigationEffect
    determineMitigationEffect(

            int seasonalBasisPoints,

            int elementalAdjustmentBasisPoints,

            int finalBasisPoints

    ) {

        validateSignedBasisPoints(
                seasonalBasisPoints
        );

        validateSignedBasisPoints(
                elementalAdjustmentBasisPoints
        );

        validateSignedBasisPoints(
                finalBasisPoints
        );

        if (
                elementalAdjustmentBasisPoints == 0
        ) {

            return ClimateMitigationEffect
                    .NEUTRAL;
        }

        /*
         * 계절 baseline 자체가 정확히 중화인데
         * 원국 오행 때문에 한쪽으로 이동했다면
         * 완화가 아니라 불균형 생성이다.
         */
        if (
                seasonalBasisPoints == 0
        ) {

            if (
                    finalBasisPoints == 0
            ) {

                return ClimateMitigationEffect
                        .NEUTRAL;
            }

            return ClimateMitigationEffect
                    .AGGRAVATING;
        }

        /*
         * 원래 방향을 넘어
         * 반대편으로 이동했다면 과보정.
         */
        if (
                finalBasisPoints != 0
                        && Integer.signum(
                        seasonalBasisPoints
                )
                        != Integer.signum(
                        finalBasisPoints
                )
        ) {

            return ClimateMitigationEffect
                    .OVER_CORRECTING;
        }

        int seasonalDeviation =
                Math.abs(
                        seasonalBasisPoints
                );

        int finalDeviation =
                Math.abs(
                        finalBasisPoints
                );

        if (
                finalDeviation
                        < seasonalDeviation
        ) {

            return ClimateMitigationEffect
                    .MITIGATING;
        }

        if (
                finalDeviation
                        > seasonalDeviation
        ) {

            return ClimateMitigationEffect
                    .AGGRAVATING;
        }

        return ClimateMitigationEffect
                .NEUTRAL;
    }

    /**
     * 한난과 조습의 조정 관계.
     *
     * =================================================
     *
     * 寒濕
     *
     * 온난 + 건조
     * -> 비교적 같은 방향
     *
     * =================================================
     *
     * 熱燥
     *
     * 냉각 + 습윤
     * -> 비교적 같은 방향
     *
     * =================================================
     *
     * 寒燥
     *
     * 온난 필요
     * 습윤 필요
     *
     * 직접적인 火/水 조정이
     * 서로 반대 부작용을 만들 수 있음.
     *
     * =================================================
     *
     * 熱濕
     *
     * 냉각 필요
     * 건조 필요
     *
     * 역시 직접적인 단일 조정으로
     * 해결하기 어려운 경쟁 구조.
     *
     * =================================================
     *
     * 아직 실제 오행은 선택하지 않는다.
     */
    public ClimateCorrectionInteraction
    determineCorrectionInteraction(
            ClimateEnvironmentAnalysis climate
    ) {

        Objects.requireNonNull(
                climate,
                "한난조습 환경 분석이 필요합니다."
        );

        boolean temperatureImbalance =
                !climate
                        .temperatureBalanced();

        boolean moistureImbalance =
                !climate
                        .moistureBalanced();

        if (
                !temperatureImbalance
                        && !moistureImbalance
        ) {

            return ClimateCorrectionInteraction
                    .NONE;
        }

        if (
                temperatureImbalance
                        ^ moistureImbalance
        ) {

            return ClimateCorrectionInteraction
                    .SINGLE_AXIS;
        }

        /*
         * 寒濕
         *
         * 온난 + 건조가 같은 방향으로
         * 작동할 수 있다.
         */
        if (
                climate.coldWet()
        ) {

            return ClimateCorrectionInteraction
                    .ALIGNED;
        }

        /*
         * 熱燥
         *
         * 냉각 + 습윤이 같은 방향으로
         * 작동할 수 있다.
         */
        if (
                climate.heatDry()
        ) {

            return ClimateCorrectionInteraction
                    .ALIGNED;
        }

        /*
         * 寒燥
         * 熱濕
         *
         * 조후 요구가 서로 경쟁할 가능성이 있다.
         */
        if (
                climate.coldDry()
                        || climate.heatWet()
        ) {

            return ClimateCorrectionInteraction
                    .COMPETING;
        }

        /*
         * 방어적 fallback.
         *
         * 두 축 모두 불균형인데
         * 6-1 EnvironmentType과 위 조건들이
         * 일치하지 않는다면 데이터 불일치다.
         */
        throw new IllegalStateException(
                "복합 한난조습 환경의 보정 관계를 결정할 수 없습니다: "
                        + climate.environmentType()
        );
    }

    private void validateSignedBasisPoints(
            int value
    ) {

        if (
                value < -10_000
                        || value > 10_000
        ) {

            throw new IllegalArgumentException(
                    "조후 내부 지수는 -10000~10000 사이여야 합니다."
            );
        }
    }
}