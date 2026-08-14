package com.dajeom.rule;

import com.dajeom.type.ClimateEnvironmentType;
import com.dajeom.type.ClimateMoistureState;
import com.dajeom.type.ClimateTemperatureState;
import com.dajeom.type.EarthlyBranch;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 6차-1.
 *
 * 월령과 원국 오행 세력을 이용해
 * 한난조습 환경을 계산하는
 * 다점 내부 Rule.
 *
 * =================================================
 *
 * 중요한 정책
 *
 * 1.
 * 월령의 계절 환경을 기본축으로 둔다.
 *
 * 2.
 * 火가 많다고 계절 자체를 완전히 뒤집거나,
 *    水가 많다고 겨울로 취급하지 않는다.
 *
 * 3.
 * 원국 오행 세력은 월령 기본값을
 *    제한적으로 보정한다.
 *
 * 4.
 * 여기서 조후용신을 결정하지 않는다.
 *
 * =================================================
 *
 * 값은 실제 기상 수치가 아니라
 * 명리 엔진의 상대지표다.
 *
 * -10000 ~ +10000
 *
 * temperature:
 *
 * - = 寒
 * + = 暖/熱
 *
 * moisture:
 *
 * - = 燥
 * + = 濕
 */
@Component
public class ClimateEnvironmentRule {

    private static final int
            MAX_TEMPERATURE_ELEMENT_ADJUSTMENT =
            3_000;

    private static final int
            MAX_MOISTURE_ELEMENT_ADJUSTMENT =
            2_500;

    /**
     * 월지 자체의 계절적
     * 한난조습 baseline.
     *
     * 지지 index:
     *
     * 0  子
     * 1  丑
     * 2  寅
     * 3  卯
     * 4  辰
     * 5  巳
     * 6  午
     * 7  未
     * 8  申
     * 9  酉
     * 10 戌
     * 11 亥
     *
     * 이 수치는 자연과학적 온습도값이 아니라
     * 다점 조후 Rule의 상대 baseline이다.
     */
    public SeasonalClimateBaseline
    seasonalBaseline(
            EarthlyBranch monthBranch
    ) {

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        return switch (
                branchIndexOf(
                        monthBranch
                )
                ) {

            /*
             * 子
             * 한기 매우 강함 + 습기 강함
             */
            case 0 ->
                    new SeasonalClimateBaseline(
                            -7_500,
                            6_500
                    );

            /*
             * 丑
             * 겨울의 한습한 토
             */
            case 1 ->
                    new SeasonalClimateBaseline(
                            -6_000,
                            3_500
                    );

            /*
             * 寅
             * 초봄.
             * 한기는 남아 있으나
             * 수분과 생기가 올라오기 시작.
             */
            case 2 ->
                    new SeasonalClimateBaseline(
                            -3_000,
                            2_500
                    );

            /*
             * 卯
             * 봄의 중심.
             */
            case 3 ->
                    new SeasonalClimateBaseline(
                            0,
                            3_000
                    );

            /*
             * 辰
             * 봄 말기의 습토.
             */
            case 4 ->
                    new SeasonalClimateBaseline(
                            1_800,
                            4_500
                    );

            /*
             * 巳
             * 초여름.
             */
            case 5 ->
                    new SeasonalClimateBaseline(
                            5_000,
                            0
                    );

            /*
             * 午
             * 열기 가장 강한 구간.
             */
            case 6 ->
                    new SeasonalClimateBaseline(
                            8_000,
                            -3_000
                    );

            /*
             * 未
             * 여름 말기의 열조한 토.
             */
            case 7 ->
                    new SeasonalClimateBaseline(
                            6_000,
                            -4_500
                    );

            /*
             * 申
             * 초가을.
             *
             * 잔열은 남지만
             * 건조 방향으로 이동.
             */
            case 8 ->
                    new SeasonalClimateBaseline(
                            2_500,
                            -3_000
                    );

            /*
             * 酉
             * 가을 중심.
             */
            case 9 ->
                    new SeasonalClimateBaseline(
                            0,
                            -4_000
                    );

            /*
             * 戌
             * 가을 말기의 조토.
             */
            case 10 ->
                    new SeasonalClimateBaseline(
                            -1_000,
                            -5_000
                    );

            /*
             * 亥
             * 초겨울.
             */
            case 11 ->
                    new SeasonalClimateBaseline(
                            -5_000,
                            5_500
                    );

            default ->
                    throw new IllegalStateException(
                            "월령 한난조습 baseline을 계산할 수 없습니다."
                    );
        };
    }

    /**
     * 원국 전체 오행 비중으로
     * 한난축을 제한적으로 보정한다.
     *
     * 주요 방향:
     *
     * 火 -> 온난
     * 水 -> 한랭
     * 木 -> 약한 온난 방향
     * 金 -> 약한 냉각 방향
     *
     * 土는 온도축에서 직접적인
     * 일괄 보정을 하지 않는다.
     */
    public int calculateTemperatureAdjustment(

            int woodShareBasisPoints,

            int fireShareBasisPoints,

            int metalShareBasisPoints,

            int waterShareBasisPoints

    ) {

        validateShare(
                woodShareBasisPoints
        );

        validateShare(
                fireShareBasisPoints
        );

        validateShare(
                metalShareBasisPoints
        );

        validateShare(
                waterShareBasisPoints
        );

        int fireWaterAdjustment =
                (
                        fireShareBasisPoints
                                - waterShareBasisPoints
                )
                        * 25
                        / 100;

        int woodMetalAdjustment =
                (
                        woodShareBasisPoints
                                - metalShareBasisPoints
                )
                        * 5
                        / 100;

        int adjustment =
                fireWaterAdjustment
                        + woodMetalAdjustment;

        return clamp(
                adjustment,
                -MAX_TEMPERATURE_ELEMENT_ADJUSTMENT,
                MAX_TEMPERATURE_ELEMENT_ADJUSTMENT
        );
    }

    /**
     * 원국 오행 비중으로
     * 조습축을 제한적으로 보정한다.
     *
     * 내부 방향:
     *
     * 水 -> 습
     * 木 -> 약한 습
     *
     * 火 -> 조
     * 金 -> 조
     * 土 -> 약한 조
     *
     * 辰·丑 등의 습토와
     * 未·戌 등의 조토 차이는
     * 월령 baseline에서 이미 구분한다.
     */
    public int calculateMoistureAdjustment(

            int woodShareBasisPoints,

            int fireShareBasisPoints,

            int earthShareBasisPoints,

            int metalShareBasisPoints,

            int waterShareBasisPoints

    ) {

        validateShare(
                woodShareBasisPoints
        );

        validateShare(
                fireShareBasisPoints
        );

        validateShare(
                earthShareBasisPoints
        );

        validateShare(
                metalShareBasisPoints
        );

        validateShare(
                waterShareBasisPoints
        );

        int adjustment =
                waterShareBasisPoints
                        * 20
                        / 100

                        + woodShareBasisPoints
                        * 5
                        / 100

                        - fireShareBasisPoints
                        * 10
                        / 100

                        - metalShareBasisPoints
                        * 8
                        / 100

                        - earthShareBasisPoints
                        * 5
                        / 100;

        return clamp(
                adjustment,
                -MAX_MOISTURE_ELEMENT_ADJUSTMENT,
                MAX_MOISTURE_ELEMENT_ADJUSTMENT
        );
    }

    public int calculateFinalTemperature(

            int seasonalTemperatureBasisPoints,

            int elementalAdjustmentBasisPoints

    ) {

        return clamp(
                seasonalTemperatureBasisPoints
                        + elementalAdjustmentBasisPoints,
                -10_000,
                10_000
        );
    }

    public int calculateFinalMoisture(

            int seasonalMoistureBasisPoints,

            int elementalAdjustmentBasisPoints

    ) {

        return clamp(
                seasonalMoistureBasisPoints
                        + elementalAdjustmentBasisPoints,
                -10_000,
                10_000
        );
    }

    public ClimateTemperatureState
    determineTemperatureState(
            int temperatureBasisPoints
    ) {

        validateSignedBasisPoints(
                temperatureBasisPoints
        );

        if (
                temperatureBasisPoints
                        <= -6_000
        ) {

            return ClimateTemperatureState
                    .SEVERE_COLD;
        }

        if (
                temperatureBasisPoints
                        <= -2_500
        ) {

            return ClimateTemperatureState
                    .COLD;
        }

        if (
                temperatureBasisPoints
                        < 2_500
        ) {

            return ClimateTemperatureState
                    .BALANCED;
        }

        if (
                temperatureBasisPoints
                        < 6_000
        ) {

            return ClimateTemperatureState
                    .WARM;
        }

        return ClimateTemperatureState
                .SEVERE_HEAT;
    }

    public ClimateMoistureState
    determineMoistureState(
            int moistureBasisPoints
    ) {

        validateSignedBasisPoints(
                moistureBasisPoints
        );

        if (
                moistureBasisPoints
                        <= -5_000
        ) {

            return ClimateMoistureState
                    .SEVERE_DRY;
        }

        if (
                moistureBasisPoints
                        <= -2_000
        ) {

            return ClimateMoistureState
                    .DRY;
        }

        if (
                moistureBasisPoints
                        < 2_000
        ) {

            return ClimateMoistureState
                    .BALANCED;
        }

        if (
                moistureBasisPoints
                        < 5_000
        ) {

            return ClimateMoistureState
                    .WET;
        }

        return ClimateMoistureState
                .SEVERE_WET;
    }

    public ClimateEnvironmentType
    determineEnvironmentType(

            ClimateTemperatureState temperature,

            ClimateMoistureState moisture

    ) {

        Objects.requireNonNull(
                temperature,
                "한난 상태가 필요합니다."
        );

        Objects.requireNonNull(
                moisture,
                "조습 상태가 필요합니다."
        );

        boolean cold =
                temperature.cold();

        boolean hot =
                temperature.warm();

        boolean dry =
                moisture.dry();

        boolean wet =
                moisture.wet();

        if (
                cold
                        && dry
        ) {

            return ClimateEnvironmentType
                    .COLD_DRY;
        }

        if (
                cold
                        && wet
        ) {

            return ClimateEnvironmentType
                    .COLD_WET;
        }

        if (
                hot
                        && dry
        ) {

            return ClimateEnvironmentType
                    .HEAT_DRY;
        }

        if (
                hot
                        && wet
        ) {

            return ClimateEnvironmentType
                    .HEAT_WET;
        }

        if (
                cold
        ) {

            return ClimateEnvironmentType
                    .COLD;
        }

        if (
                hot
        ) {

            return ClimateEnvironmentType
                    .HEAT;
        }

        if (
                dry
        ) {

            return ClimateEnvironmentType
                    .DRY;
        }

        if (
                wet
        ) {

            return ClimateEnvironmentType
                    .WET;
        }

        return ClimateEnvironmentType
                .BALANCED;
    }

    private int branchIndexOf(
            EarthlyBranch branch
    ) {

        for (
                int index = 0;
                index < 12;
                index++
        ) {

            if (
                    EarthlyBranch.fromIndex(
                            index
                    ) == branch
            ) {

                return index;
            }
        }

        throw new IllegalArgumentException(
                "알 수 없는 지지입니다: "
                        + branch
        );
    }

    private void validateShare(
            int value
    ) {

        if (
                value < 0
                        || value > 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 비중은 0~10000bp 사이여야 합니다."
            );
        }
    }

    private void validateSignedBasisPoints(
            int value
    ) {

        if (
                value < -10_000
                        || value > 10_000
        ) {

            throw new IllegalArgumentException(
                    "한난조습 지수는 -10000~10000 사이여야 합니다."
            );
        }
    }

    private int clamp(

            int value,

            int min,

            int max

    ) {

        return Math.max(
                min,
                Math.min(
                        max,
                        value
                )
        );
    }

    /**
     * Rule 내부에서만 사용하는
     * 월령 기본 기후값.
     */
    public record SeasonalClimateBaseline(

            int temperatureBasisPoints,

            int moistureBasisPoints

    ) {

        public SeasonalClimateBaseline {

            if (
                    temperatureBasisPoints < -10_000
                            || temperatureBasisPoints > 10_000
                            || moistureBasisPoints < -10_000
                            || moistureBasisPoints > 10_000
            ) {

                throw new IllegalArgumentException(
                        "월령 기후 baseline은 -10000~10000 사이여야 합니다."
                );
            }
        }
    }
}