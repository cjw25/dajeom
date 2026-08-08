package com.dajeom.analyzer;

import com.dajeom.model.ElementStrength;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.TemperatureAnalysis;
import com.dajeom.rule.TemperatureRule;
import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.MoistureLevel;
import com.dajeom.type.SeasonType;
import com.dajeom.type.TemperatureLevel;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 원국의 한난조습을 분석한다.
 *
 * V1 입력:
 *
 * - 월지
 * - 월령 계절
 * - 火 세력
 * - 水 세력
 *
 * 처리:
 *
 * 월령 기본 조후
 * +
 * 실제 火水 세력 보정
 *
 * 출력:
 *
 * TemperatureAnalysis
 *
 * 아직 일간별 세부 고전 조후표까지
 * 적용하는 단계는 아니다.
 */
@Component
public class TemperatureAnalyzer {

    public TemperatureAnalysis analyze(
            NatalChartAnalysis natalChart
    ) {

        Objects.requireNonNull(
                natalChart,
                "원국 분석 결과는 필수입니다."
        );

        EarthlyBranch monthBranch =
                natalChart
                        .fourPillars()
                        .monthPillar()
                        .branch();

        SeasonType season =
                TemperatureRule.seasonOf(
                        monthBranch
                );

        ElementStrength elementStrength =
                natalChart.elementStrength();

        double firePercentage =
                elementStrength.percentageOf(
                        FiveElement.FIRE
                );

        double waterPercentage =
                elementStrength.percentageOf(
                        FiveElement.WATER
                );

        double temperatureScore =
                TemperatureRule
                        .baseTemperatureScore(
                                monthBranch
                        )
                        + TemperatureRule
                        .calculateElementTemperatureModifier(
                                firePercentage,
                                waterPercentage
                        );

        double moistureScore =
                TemperatureRule
                        .baseMoistureScore(
                                monthBranch
                        )
                        + TemperatureRule
                        .calculateElementMoistureModifier(
                                firePercentage,
                                waterPercentage
                        );

        TemperatureLevel temperatureLevel =
                TemperatureRule
                        .classifyTemperature(
                                temperatureScore
                        );

        MoistureLevel moistureLevel =
                TemperatureRule
                        .classifyMoisture(
                                moistureScore
                        );

        boolean adjustmentRequired =
                TemperatureRule
                        .requiresAdjustment(
                                temperatureLevel,
                                moistureLevel
                        );

        AnalysisConfidence confidence =
                determineConfidence(
                        natalChart
                );

        return new TemperatureAnalysis(
                season,
                monthBranch,
                temperatureScore,
                temperatureLevel,
                moistureScore,
                moistureLevel,
                TemperatureRule
                        .balancingCandidates(
                                temperatureLevel,
                                moistureLevel
                        ),
                adjustmentRequired,
                confidence,
                TemperatureRule.RULE_VERSION
        );
    }

    private AnalysisConfidence
    determineConfidence(
            NatalChartAnalysis natalChart
    ) {

        /*
         * 출생시간 미상인 경우에는
         * 시주 오행이 누락되므로
         * 전체 조후 세력 판단 신뢰도를 낮춘다.
         */
        if (
                !natalChart.hasHourPillar()
        ) {

            return AnalysisConfidence.LOW;
        }

        /*
         * V1은 아직 일간별 세부 조후표까지
         * 구현하지 않았기 때문에
         * 최대 MEDIUM으로 제한한다.
         */
        return AnalysisConfidence.MEDIUM;
    }
}