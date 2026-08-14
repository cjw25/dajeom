package com.dajeom.model;

import com.dajeom.type.ClimateEnvironmentType;
import com.dajeom.type.ClimateMoistureState;
import com.dajeom.type.ClimateTemperatureState;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.Objects;

/**
 * 정밀화 6차-1.
 *
 * 원국의 최종 한난조습 환경 분석.
 *
 * 여기서는:
 *
 * - 환경 상태
 * - 한난 상태
 * - 조습 상태
 *
 * 까지만 결정한다.
 *
 * 조후상 어떤 오행이 필요하다는 판정은
 * 6-2 / 6-3의 책임이다.
 */
public record ClimateEnvironmentAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        ClimateTemperatureState temperatureState,

        ClimateMoistureState moistureState,

        ClimateEnvironmentType environmentType,

        ClimateEnvironmentEvidence evidence,

        boolean completeChart

) {

    public ClimateEnvironmentAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                temperatureState,
                "한난 상태가 필요합니다."
        );

        Objects.requireNonNull(
                moistureState,
                "조습 상태가 필요합니다."
        );

        Objects.requireNonNull(
                environmentType,
                "한난조습 환경 유형이 필요합니다."
        );

        Objects.requireNonNull(
                evidence,
                "한난조습 Evidence가 필요합니다."
        );

        if (
                evidence.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "한난조습 Evidence의 월지가 일치하지 않습니다."
            );
        }
    }

    public boolean cold() {

        return temperatureState
                .cold();
    }

    public boolean hot() {

        return temperatureState
                .warm();
    }

    public boolean temperatureBalanced() {

        return temperatureState
                .balanced();
    }

    public boolean dry() {

        return moistureState
                .dry();
    }

    public boolean wet() {

        return moistureState
                .wet();
    }

    public boolean moistureBalanced() {

        return moistureState
                .balanced();
    }

    public boolean climateBalanced() {

        return environmentType
                .balanced();
    }

    public boolean coldDry() {

        return environmentType
                == ClimateEnvironmentType.COLD_DRY;
    }

    public boolean coldWet() {

        return environmentType
                == ClimateEnvironmentType.COLD_WET;
    }

    public boolean heatDry() {

        return environmentType
                == ClimateEnvironmentType.HEAT_DRY;
    }

    public boolean heatWet() {

        return environmentType
                == ClimateEnvironmentType.HEAT_WET;
    }

    public boolean combinedImbalance() {

        return environmentType
                .combinedImbalance();
    }

    public boolean strongTemperatureImbalance() {

        return temperatureState
                .extreme();
    }

    public boolean strongMoistureImbalance() {

        return moistureState
                .extreme();
    }

    /**
     * 6-2에서 조후 필요성을
     * 적극적으로 검토할 가치가 있는 환경인가.
     *
     * 여기서는 필요 오행을 결정하지 않는다.
     */
    public boolean requiresClimateReview() {

        return !climateBalanced();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}