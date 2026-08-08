package com.dajeom.rule;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.MoistureLevel;
import com.dajeom.type.SeasonType;
import com.dajeom.type.TemperatureLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 조후 기초 분석 Rule.
 *
 * V1은 월령을 중심으로 한
 * 계절성 + 원국 火水 세력 보정을 사용한다.
 *
 * 이 Rule은 최종 조후용신을 확정하는
 * 완성형 고전 명리 Rule이 아니다.
 *
 * 향후 실제 사례 검증 및
 * 일간/월령 세부 조후표를 추가할 경우
 * V2 Rule로 확장한다.
 */
public final class TemperatureRule {

    public static final String RULE_VERSION =
            "DAJEOM_TEMPERATURE_V1";

    /**
     * 火 / 水 세력 차이가
     * 한난 점수에 미치는 최대 보정값.
     */
    private static final double
            ELEMENT_TEMPERATURE_LIMIT =
            20.0;

    private TemperatureRule() {

        throw new IllegalStateException(
                "TemperatureRule은 생성할 수 없습니다."
        );
    }

    /**
     * 월지 기준 계절.
     */
    public static SeasonType seasonOf(
            EarthlyBranch monthBranch
    ) {

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        return switch (monthBranch) {

            case YIN,
                 MAO,
                 CHEN ->
                    SeasonType.SPRING;

            case SI,
                 WU,
                 WEI ->
                    SeasonType.SUMMER;

            case SHEN,
                 YOU,
                 XU ->
                    SeasonType.AUTUMN;

            case HAI,
                 ZI,
                 CHOU ->
                    SeasonType.WINTER;
        };
    }

    /**
     * 월령 자체가 가지는
     * 기본 한난 점수.
     *
     * 음수:
     * 한
     *
     * 양수:
     * 난
     */
    public static double
    baseTemperatureScore(
            EarthlyBranch monthBranch
    ) {

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        return switch (monthBranch) {

            case YIN ->
                    -10.0;

            case MAO ->
                    0.0;

            case CHEN ->
                    5.0;

            case SI ->
                    20.0;

            case WU ->
                    30.0;

            case WEI ->
                    20.0;

            case SHEN ->
                    5.0;

            case YOU ->
                    -5.0;

            case XU ->
                    -10.0;

            case HAI ->
                    -20.0;

            case ZI ->
                    -30.0;

            case CHOU ->
                    -20.0;
        };
    }

    /**
     * 월령 자체가 가지는
     * 기본 조습 점수.
     *
     * 음수:
     * 건조
     *
     * 양수:
     * 습윤
     */
    public static double
    baseMoistureScore(
            EarthlyBranch monthBranch
    ) {

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        return switch (monthBranch) {

            case YIN ->
                    0.0;

            case MAO ->
                    5.0;

            case CHEN ->
                    20.0;

            case SI ->
                    -10.0;

            case WU ->
                    -20.0;

            case WEI ->
                    -20.0;

            case SHEN ->
                    -10.0;

            case YOU ->
                    -15.0;

            case XU ->
                    -25.0;

            case HAI ->
                    20.0;

            case ZI ->
                    30.0;

            case CHOU ->
                    20.0;
        };
    }

    /**
     * 火 / 水의 실제 세력 차이를
     * 한난 점수에 추가한다.
     */
    public static double
    calculateElementTemperatureModifier(
            double firePercentage,
            double waterPercentage
    ) {

        double difference =
                firePercentage
                        - waterPercentage;

        double modifier =
                difference
                        * 0.5;

        return clamp(
                modifier,
                -ELEMENT_TEMPERATURE_LIMIT,
                ELEMENT_TEMPERATURE_LIMIT
        );
    }

    /**
     * 水 / 火의 차이를 이용해
     * 조습 상태를 보조적으로 조정한다.
     *
     * 水가 강하면 습 쪽,
     * 火가 강하면 조 쪽으로 이동한다.
     */
    public static double
    calculateElementMoistureModifier(
            double firePercentage,
            double waterPercentage
    ) {

        double difference =
                waterPercentage
                        - firePercentage;

        return clamp(
                difference * 0.35,
                -15.0,
                15.0
        );
    }

    public static TemperatureLevel
    classifyTemperature(
            double score
    ) {

        if (score <= -30.0) {

            return TemperatureLevel.VERY_COLD;
        }

        if (score <= -10.0) {

            return TemperatureLevel.COLD;
        }

        if (score < 10.0) {

            return TemperatureLevel.BALANCED;
        }

        if (score < 30.0) {

            return TemperatureLevel.HOT;
        }

        return TemperatureLevel.VERY_HOT;
    }

    public static MoistureLevel
    classifyMoisture(
            double score
    ) {

        if (score <= -30.0) {

            return MoistureLevel.VERY_DRY;
        }

        if (score <= -10.0) {

            return MoistureLevel.DRY;
        }

        if (score < 10.0) {

            return MoistureLevel.BALANCED;
        }

        if (score < 30.0) {

            return MoistureLevel.WET;
        }

        return MoistureLevel.VERY_WET;
    }

    /**
     * 현재 한난조습 상태를 기준으로
     * 보완 후보 오행을 만든다.
     *
     * 한:
     * 火 우선
     *
     * 열:
     * 水 우선
     *
     * 습:
     * 土 보완 후보
     *
     * 조:
     * 水 보완 후보
     *
     * 이것은 최종 용신 확정이 아니다.
     */
    public static List<FiveElement>
    balancingCandidates(
            TemperatureLevel temperatureLevel,
            MoistureLevel moistureLevel
    ) {

        Objects.requireNonNull(
                temperatureLevel,
                "한난 상태는 필수입니다."
        );

        Objects.requireNonNull(
                moistureLevel,
                "조습 상태는 필수입니다."
        );

        List<FiveElement> result =
                new ArrayList<>();

        if (
                temperatureLevel
                        == TemperatureLevel.VERY_COLD
                        || temperatureLevel
                        == TemperatureLevel.COLD
        ) {

            addIfAbsent(
                    result,
                    FiveElement.FIRE
            );

            /*
             * 목은 화를 돕는 보조 후보로 둔다.
             */
            addIfAbsent(
                    result,
                    FiveElement.WOOD
            );
        }

        if (
                temperatureLevel
                        == TemperatureLevel.VERY_HOT
                        || temperatureLevel
                        == TemperatureLevel.HOT
        ) {

            addIfAbsent(
                    result,
                    FiveElement.WATER
            );

            /*
             * 금은 수를 돕는 보조 후보.
             */
            addIfAbsent(
                    result,
                    FiveElement.METAL
            );
        }

        if (
                moistureLevel
                        == MoistureLevel.VERY_DRY
                        || moistureLevel
                        == MoistureLevel.DRY
        ) {

            addIfAbsent(
                    result,
                    FiveElement.WATER
            );
        }

        if (
                moistureLevel
                        == MoistureLevel.VERY_WET
                        || moistureLevel
                        == MoistureLevel.WET
        ) {

            addIfAbsent(
                    result,
                    FiveElement.EARTH
            );
        }

        return List.copyOf(
                result
        );
    }

    public static boolean
    requiresAdjustment(
            TemperatureLevel temperatureLevel,
            MoistureLevel moistureLevel
    ) {

        return temperatureLevel
                != TemperatureLevel.BALANCED
                || moistureLevel
                != MoistureLevel.BALANCED;
    }

    private static void addIfAbsent(
            List<FiveElement> target,
            FiveElement element
    ) {

        if (
                !target.contains(
                        element
                )
        ) {

            target.add(
                    element
            );
        }
    }

    private static double clamp(
            double value,
            double minimum,
            double maximum
    ) {

        return Math.max(
                minimum,
                Math.min(
                        maximum,
                        value
                )
        );
    }
}