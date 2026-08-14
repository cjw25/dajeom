package com.dajeom.engine;

import com.dajeom.calculator.MonthlyLuckPeriodCalculator;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.MonthlyLuckAnalysis;
import com.dajeom.model.MonthlyLuckAnalysisSequence;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.MonthlyLuckSequenceAnalysis;
import com.dajeom.resolver.MonthlyAnnualLuckCrossResolver;
import com.dajeom.resolver.MonthlyDecadeLuckCrossResolver;
import com.dajeom.resolver.MonthlyLuckContextResolver;
import com.dajeom.resolver.MonthlyLuckNatalCrossResolver;
import com.dajeom.resolver.MonthlyLuckTenGodSourceResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-8.
 *
 * 한 세운의 12개 월운 전체를
 * 최종 분석하는 Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * AnnualLuckAnalysis
 *
 * ↓
 *
 * 12개 MonthlyLuckPeriod
 *
 * ↓
 *
 * 각 Period마다:
 *
 * 15-3
 * 15-4
 * 15-5
 * 15-6
 * 15-7
 * 15-8
 *
 * ↓
 *
 * MonthlyLuckAnalysisSequence
 *
 * =================================================
 *
 * 이 Engine에서도 새로운 명리 규칙은
 * 계산하지 않는다.
 */
@Component
public class MonthlyLuckSequenceAnalysisEngine {

    private final MonthlyLuckPeriodCalculator
            monthlyLuckPeriodCalculator;

    private final MonthlyLuckAnalysisEngine
            monthlyLuckAnalysisEngine;

    public MonthlyLuckSequenceAnalysisEngine(

            MonthlyLuckPeriodCalculator
                    monthlyLuckPeriodCalculator,

            MonthlyLuckAnalysisEngine
                    monthlyLuckAnalysisEngine

    ) {

        this.monthlyLuckPeriodCalculator =
                Objects.requireNonNull(
                        monthlyLuckPeriodCalculator,
                        "MonthlyLuckPeriodCalculator가 필요합니다."
                );

        this.monthlyLuckAnalysisEngine =
                Objects.requireNonNull(
                        monthlyLuckAnalysisEngine,
                        "MonthlyLuckAnalysisEngine이 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15차 전체 Sequence API
     * =================================================
     */
    public MonthlyLuckAnalysisSequence analyze(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckTenGodSourceResolver
                    tenGodSourceResolver,

            MonthlyLuckNatalCrossResolver
                    natalCrossResolver,

            MonthlyDecadeLuckCrossResolver
                    decadeCrossResolver,

            MonthlyAnnualLuckCrossResolver
                    annualCrossResolver,

            MonthlyLuckContextResolver
                    contextResolver

    ) {

        Objects.requireNonNull(
                annualLuckAnalysis,
                "월운 Sequence 분석에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodSourceResolver,
                "MonthlyLuckTenGodSourceResolver가 필요합니다."
        );

        Objects.requireNonNull(
                natalCrossResolver,
                "MonthlyLuckNatalCrossResolver가 필요합니다."
        );

        Objects.requireNonNull(
                decadeCrossResolver,
                "MonthlyDecadeLuckCrossResolver가 필요합니다."
        );

        Objects.requireNonNull(
                annualCrossResolver,
                "MonthlyAnnualLuckCrossResolver가 필요합니다."
        );

        Objects.requireNonNull(
                contextResolver,
                "MonthlyLuckContextResolver가 필요합니다."
        );

        /*
         * =================================================
         * 15-1 + 15-2
         *
         * 해당 세운의 정확한 12개 월운 Period.
         * =================================================
         */

        MonthlyLuckSequenceAnalysis periodSequence =
                monthlyLuckPeriodCalculator
                        .calculateAnnualPeriod(
                                annualLuckAnalysis.period()
                        );

        List<MonthlyLuckAnalysis> analyses =
                new ArrayList<>(
                        periodSequence.periodCount()
                );

        /*
         * =================================================
         * 각 월운 최종 분석
         * =================================================
         */

        for (
                MonthlyLuckPeriod monthlyLuckPeriod
                : periodSequence.periods()
        ) {

            MonthlyLuckAnalysis analysis =
                    monthlyLuckAnalysisEngine
                            .analyze(
                                    annualLuckAnalysis,
                                    monthlyLuckPeriod,
                                    tenGodSourceResolver,
                                    natalCrossResolver,
                                    decadeCrossResolver,
                                    annualCrossResolver,
                                    contextResolver
                            );

            analyses.add(
                    analysis
            );
        }

        /*
         * =================================================
         * 최종 12개월 Aggregate
         * =================================================
         */

        return new MonthlyLuckAnalysisSequence(
                annualLuckAnalysis,
                periodSequence,
                analyses
        );
    }
}