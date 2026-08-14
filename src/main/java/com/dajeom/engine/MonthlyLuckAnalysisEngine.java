package com.dajeom.engine;

import com.dajeom.analyzer.MonthlyAnnualLuckRelationAnalyzer;
import com.dajeom.analyzer.MonthlyDecadeLuckInteractionAnalyzer;
import com.dajeom.analyzer.MonthlyLuckContextAnalyzer;
import com.dajeom.analyzer.MonthlyLuckNatalRelationAnalyzer;
import com.dajeom.analyzer.MonthlyLuckTenGodAnalyzer;
import com.dajeom.calculator.MonthlyLuckPeriodCalculator;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.MonthlyAnnualLuckRelationAnalysis;
import com.dajeom.model.MonthlyDecadeLuckInteractionAnalysis;
import com.dajeom.model.MonthlyLuckAnalysis;
import com.dajeom.model.MonthlyLuckContextAnalysis;
import com.dajeom.model.MonthlyLuckNatalRelationAnalysis;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.MonthlyLuckTenGodAnalysis;
import com.dajeom.resolver.MonthlyAnnualLuckCrossResolver;
import com.dajeom.resolver.MonthlyDecadeLuckCrossResolver;
import com.dajeom.resolver.MonthlyLuckContextResolver;
import com.dajeom.resolver.MonthlyLuckNatalCrossResolver;
import com.dajeom.resolver.MonthlyLuckTenGodSourceResolver;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 15차-8.
 *
 * 월운 최종 분석 Engine.
 *
 * =================================================
 *
 * 처리 순서:
 *
 * 15-2 MonthlyLuckPeriod
 *
 * ↓
 *
 * 15-3 월운 십성
 *
 * ↓
 *
 * 15-4 원국 × 월운
 *
 * ↓
 *
 * 15-5 대운 × 월운
 *
 * ↓
 *
 * 15-6 세운 × 월운
 *
 * ↓
 *
 * 15-7 월운 Context
 *
 * ↓
 *
 * 15-8 MonthlyLuckAnalysis
 *
 * =================================================
 *
 * 이 Engine에서는 새로운 명리 규칙을
 * 계산하지 않는다.
 *
 * 각 deterministic 단계의 결과를
 * 조립하는 orchestration 역할만 한다.
 */
@Component
public class MonthlyLuckAnalysisEngine {

    private final MonthlyLuckPeriodCalculator
            monthlyLuckPeriodCalculator;

    private final MonthlyLuckTenGodAnalyzer
            monthlyLuckTenGodAnalyzer;

    private final MonthlyLuckNatalRelationAnalyzer
            monthlyLuckNatalRelationAnalyzer;

    private final MonthlyDecadeLuckInteractionAnalyzer
            monthlyDecadeLuckInteractionAnalyzer;

    private final MonthlyAnnualLuckRelationAnalyzer
            monthlyAnnualLuckRelationAnalyzer;

    private final MonthlyLuckContextAnalyzer
            monthlyLuckContextAnalyzer;

    public MonthlyLuckAnalysisEngine(

            MonthlyLuckPeriodCalculator
                    monthlyLuckPeriodCalculator,

            MonthlyLuckTenGodAnalyzer
                    monthlyLuckTenGodAnalyzer,

            MonthlyLuckNatalRelationAnalyzer
                    monthlyLuckNatalRelationAnalyzer,

            MonthlyDecadeLuckInteractionAnalyzer
                    monthlyDecadeLuckInteractionAnalyzer,

            MonthlyAnnualLuckRelationAnalyzer
                    monthlyAnnualLuckRelationAnalyzer,

            MonthlyLuckContextAnalyzer
                    monthlyLuckContextAnalyzer

    ) {

        this.monthlyLuckPeriodCalculator =
                Objects.requireNonNull(
                        monthlyLuckPeriodCalculator,
                        "MonthlyLuckPeriodCalculator가 필요합니다."
                );

        this.monthlyLuckTenGodAnalyzer =
                Objects.requireNonNull(
                        monthlyLuckTenGodAnalyzer,
                        "MonthlyLuckTenGodAnalyzer가 필요합니다."
                );

        this.monthlyLuckNatalRelationAnalyzer =
                Objects.requireNonNull(
                        monthlyLuckNatalRelationAnalyzer,
                        "MonthlyLuckNatalRelationAnalyzer가 필요합니다."
                );

        this.monthlyDecadeLuckInteractionAnalyzer =
                Objects.requireNonNull(
                        monthlyDecadeLuckInteractionAnalyzer,
                        "MonthlyDecadeLuckInteractionAnalyzer가 필요합니다."
                );

        this.monthlyAnnualLuckRelationAnalyzer =
                Objects.requireNonNull(
                        monthlyAnnualLuckRelationAnalyzer,
                        "MonthlyAnnualLuckRelationAnalyzer가 필요합니다."
                );

        this.monthlyLuckContextAnalyzer =
                Objects.requireNonNull(
                        monthlyLuckContextAnalyzer,
                        "MonthlyLuckContextAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15-8 API
     * =================================================
     *
     * 이미 MonthlyLuckPeriod가 계산되어 있을 때.
     */
    public MonthlyLuckAnalysis analyze(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckPeriod monthlyLuckPeriod,

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

        validateInputs(
                annualLuckAnalysis,
                monthlyLuckPeriod,
                tenGodSourceResolver,
                natalCrossResolver,
                decadeCrossResolver,
                annualCrossResolver,
                contextResolver
        );

        /*
         * =================================================
         * 15-3
         *
         * 월운 천간·지지 십성
         * =================================================
         */

        MonthlyLuckTenGodAnalysis tenGodAnalysis =
                monthlyLuckTenGodAnalyzer
                        .analyze(
                                annualLuckAnalysis.dayMaster(),
                                monthlyLuckPeriod,
                                tenGodSourceResolver
                        );

        /*
         * =================================================
         * 15-4
         *
         * 원국 × 월운
         * =================================================
         */

        MonthlyLuckNatalRelationAnalysis natalRelationAnalysis =
                monthlyLuckNatalRelationAnalyzer
                        .analyze(
                                annualLuckAnalysis.natalAnalysis(),
                                monthlyLuckPeriod,
                                natalCrossResolver
                        );

        /*
         * =================================================
         * 15-5
         *
         * 대운 × 월운
         * =================================================
         */

        MonthlyDecadeLuckInteractionAnalysis
                decadeInteractionAnalysis =
                monthlyDecadeLuckInteractionAnalyzer
                        .analyze(
                                monthlyLuckPeriod,
                                annualLuckAnalysis
                                        .decadeLuckAnalysis(),
                                decadeCrossResolver
                        );

        /*
         * =================================================
         * 15-6
         *
         * 세운 × 월운
         * =================================================
         */

        MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis =
                monthlyAnnualLuckRelationAnalyzer
                        .analyze(
                                annualLuckAnalysis.period(),
                                monthlyLuckPeriod,
                                annualCrossResolver
                        );

        /*
         * =================================================
         * 15-7
         *
         * 원국 + 대운 + 세운 + 월운 Context
         * =================================================
         */

        MonthlyLuckContextAnalysis contextAnalysis =
                monthlyLuckContextAnalyzer
                        .analyze(
                                annualLuckAnalysis,
                                tenGodAnalysis,
                                natalRelationAnalysis,
                                decadeInteractionAnalysis,
                                annualRelationAnalysis,
                                contextResolver
                        );

        /*
         * =================================================
         * 15-8
         *
         * 최종 Aggregate
         * =================================================
         */

        return new MonthlyLuckAnalysis(
                annualLuckAnalysis.dayMaster(),
                annualLuckAnalysis,
                monthlyLuckPeriod,
                tenGodAnalysis,
                natalRelationAnalysis,
                decadeInteractionAnalysis,
                annualRelationAnalysis,
                contextAnalysis
        );
    }

    /**
     * =================================================
     * 특정 실제 시각 기준
     * =================================================
     *
     * AnnualLuckAnalysis는 이미 14차에서
     * 정확한 입춘 기준으로 선택되어 있어야 한다.
     */
    public MonthlyLuckAnalysis analyzeAt(

            AnnualLuckAnalysis annualLuckAnalysis,

            ZonedDateTime referenceDateTime,

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
                "월운 분석에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                referenceDateTime,
                "월운 분석 기준시각이 필요합니다."
        );

        if (
                !annualLuckAnalysis.contains(
                        referenceDateTime
                )
        ) {

            throw new IllegalArgumentException(
                    "기준시각이 현재 AnnualLuckAnalysis의 세운 범위에 포함되지 않습니다."
            );
        }

        MonthlyLuckPeriod monthlyLuckPeriod =
                monthlyLuckPeriodCalculator
                        .calculateAt(
                                annualLuckAnalysis.period(),
                                referenceDateTime
                        );

        MonthlyLuckAnalysis analysis =
                analyze(
                        annualLuckAnalysis,
                        monthlyLuckPeriod,
                        tenGodSourceResolver,
                        natalCrossResolver,
                        decadeCrossResolver,
                        annualCrossResolver,
                        contextResolver
                );

        if (
                !analysis.contains(
                        referenceDateTime
                )
        ) {

            throw new IllegalStateException(
                    "기준시각과 최종 MonthlyLuckAnalysis의 절입 범위가 일치하지 않습니다."
            );
        }

        return analysis;
    }

    /**
     * LocalDateTime 버전.
     */
    public MonthlyLuckAnalysis analyzeAt(

            AnnualLuckAnalysis annualLuckAnalysis,

            LocalDateTime referenceDateTime,

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
                "월운 분석에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                referenceDateTime,
                "월운 분석 기준일시가 필요합니다."
        );

        return analyzeAt(
                annualLuckAnalysis,
                referenceDateTime.atZone(
                        annualLuckAnalysis.zoneId()
                ),
                tenGodSourceResolver,
                natalCrossResolver,
                decadeCrossResolver,
                annualCrossResolver,
                contextResolver
        );
    }

    /**
     * =================================================
     * monthIndex 0~11 직접 분석
     * =================================================
     */
    public MonthlyLuckAnalysis analyzeByMonthIndex(

            AnnualLuckAnalysis annualLuckAnalysis,

            int monthIndex,

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
                "월운 분석에는 AnnualLuckAnalysis가 필요합니다."
        );

        MonthlyLuckPeriod monthlyLuckPeriod =
                monthlyLuckPeriodCalculator
                        .calculateByMonthIndex(
                                annualLuckAnalysis.period(),
                                monthIndex
                        );

        return analyze(
                annualLuckAnalysis,
                monthlyLuckPeriod,
                tenGodSourceResolver,
                natalCrossResolver,
                decadeCrossResolver,
                annualCrossResolver,
                contextResolver
        );
    }

    /*
     * =================================================
     * 입력 검증
     * =================================================
     */

    private void validateInputs(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckPeriod monthlyLuckPeriod,

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
                "15-8에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckPeriod,
                "15-8에는 MonthlyLuckPeriod가 필요합니다."
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

        if (
                monthlyLuckPeriod.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "MonthlyLuckPeriod가 현재 AnnualLuckAnalysis에 속하지 않습니다."
            );
        }
    }
}