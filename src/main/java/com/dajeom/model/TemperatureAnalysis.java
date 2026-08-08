package com.dajeom.model;

import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.MoistureLevel;
import com.dajeom.type.SeasonType;
import com.dajeom.type.TemperatureLevel;

import java.util.List;
import java.util.Objects;

/**
 * 원국의 조후 기초 분석 결과.
 *
 * 조후는 크게:
 *
 * - 한난
 * - 조습
 *
 * 상태를 본다.
 *
 * 현재 V1에서는:
 *
 * 1. 월령의 계절적 성질
 * 2. 월지의 한난 기준
 * 3. 월지의 조습 기준
 * 4. 원국의 火 / 水 세력
 *
 * 을 사용해 기본 조후 상태를 계산한다.
 *
 * 특정 일간/월령 조합에 대한
 * 고전별 세부 조후용신은
 * 향후 Rule 버전에서 확장한다.
 */
public record TemperatureAnalysis(

        SeasonType season,

        EarthlyBranch monthBranch,

        /**
         * 한난 점수.
         *
         * 음수:
         * 차가운 방향
         *
         * 양수:
         * 뜨거운 방향
         */
        double temperatureScore,

        TemperatureLevel temperatureLevel,

        /**
         * 조습 점수.
         *
         * 음수:
         * 건조한 방향
         *
         * 양수:
         * 습한 방향
         */
        double moistureScore,

        MoistureLevel moistureLevel,

        /**
         * 현재 조후 상태를 보완할 가능성이 있는
         * 오행 후보.
         *
         * 이것이 곧 최종 용신이라는 의미는 아니다.
         */
        List<FiveElement> balancingCandidates,

        /**
         * 조후 보정이 필요한 상태인지.
         */
        boolean adjustmentRequired,

        AnalysisConfidence confidence,

        String ruleVersion

) {

    public TemperatureAnalysis {

        Objects.requireNonNull(
                season,
                "계절은 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                temperatureLevel,
                "한난 상태는 필수입니다."
        );

        Objects.requireNonNull(
                moistureLevel,
                "조습 상태는 필수입니다."
        );

        Objects.requireNonNull(
                balancingCandidates,
                "조후 보완 후보는 필수입니다."
        );

        Objects.requireNonNull(
                confidence,
                "분석 신뢰도는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        if (
                Double.isNaN(
                        temperatureScore
                )
                        || Double.isInfinite(
                        temperatureScore
                )
        ) {

            throw new IllegalArgumentException(
                    "한난 점수는 유한한 값이어야 합니다."
            );
        }

        if (
                Double.isNaN(
                        moistureScore
                )
                        || Double.isInfinite(
                        moistureScore
                )
        ) {

            throw new IllegalArgumentException(
                    "조습 점수는 유한한 값이어야 합니다."
            );
        }

        balancingCandidates =
                List.copyOf(
                        balancingCandidates
                );
    }

    public boolean isCold() {

        return temperatureLevel
                == TemperatureLevel.COLD
                || temperatureLevel
                == TemperatureLevel.VERY_COLD;
    }

    public boolean isHot() {

        return temperatureLevel
                == TemperatureLevel.HOT
                || temperatureLevel
                == TemperatureLevel.VERY_HOT;
    }

    public boolean isDry() {

        return moistureLevel
                == MoistureLevel.DRY
                || moistureLevel
                == MoistureLevel.VERY_DRY;
    }

    public boolean isWet() {

        return moistureLevel
                == MoistureLevel.WET
                || moistureLevel
                == MoistureLevel.VERY_WET;
    }
}