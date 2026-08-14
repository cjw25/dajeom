package com.dajeom.analyzer;

import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.MonthlyAnnualLuckCrossResolution;
import com.dajeom.model.MonthlyAnnualLuckRelationAnalysis;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.resolver.MonthlyAnnualLuckCrossResolver;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 15차-6.
 *
 * 세운 × 월운 관계 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * AnnualLuckPeriod
 * +
 * MonthlyLuckPeriod
 * +
 * 8/9차 연결 Resolver
 *
 * =================================================
 *
 * 출력:
 *
 * MonthlyAnnualLuckRelationAnalysis
 *
 * =================================================
 *
 * 새로운 관계 공식을 추가하지 않는다.
 */
@Component
public class MonthlyAnnualLuckRelationAnalyzer {

    /**
     * =================================================
     * 공식 15-6 API
     * =================================================
     */
    public MonthlyAnnualLuckRelationAnalysis analyze(

            AnnualLuckPeriod annualPeriod,

            MonthlyLuckPeriod monthlyPeriod,

            MonthlyAnnualLuckCrossResolver crossResolver

    ) {

        Objects.requireNonNull(
                annualPeriod,
                "15-6 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyPeriod,
                "15-6 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                crossResolver,
                "MonthlyAnnualLuckCrossResolver가 필요합니다."
        );

        /*
         * =================================================
         * 월운이 해당 세운에 속하는지
         * Resolver 호출 전부터 검증.
         * =================================================
         */

        if (
                monthlyPeriod.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-6 분석의 MonthlyLuckPeriod가 현재 AnnualLuckPeriod에 속하지 않습니다."
            );
        }

        MonthlyAnnualLuckCrossResolution resolution =
                Objects.requireNonNull(
                        crossResolver.resolve(
                                annualPeriod,
                                monthlyPeriod
                        ),
                        "MonthlyAnnualLuckCrossResolver가 null을 반환했습니다."
                );

        /*
         * =================================================
         * Resolver Identity 검증
         * =================================================
         */

        if (
                resolution.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-6 Resolver가 다른 AnnualLuckPeriod를 반환했습니다."
            );
        }

        if (
                resolution.monthlyPeriod()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-6 Resolver가 다른 MonthlyLuckPeriod를 반환했습니다."
            );
        }

        return new MonthlyAnnualLuckRelationAnalysis(
                annualPeriod,
                monthlyPeriod,
                resolution.relations(),
                resolution.transformations()
        );
    }

    /**
     * MonthlyLuckPeriod 안에 AnnualLuckPeriod가 이미 있으므로
     * 편의 Overload를 제공한다.
     */
    public MonthlyAnnualLuckRelationAnalysis analyze(

            MonthlyLuckPeriod monthlyPeriod,

            MonthlyAnnualLuckCrossResolver crossResolver

    ) {

        Objects.requireNonNull(
                monthlyPeriod,
                "15-6 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        return analyze(
                monthlyPeriod.annualPeriod(),
                monthlyPeriod,
                crossResolver
        );
    }
}