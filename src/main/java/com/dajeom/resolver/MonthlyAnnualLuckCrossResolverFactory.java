package com.dajeom.resolver;

import com.dajeom.analyzer.MonthlyAnnualLuckCrossAnalyzer;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 15차-6.
 *
 * 요청별 immutable
 * MonthlyAnnualLuckCrossResolver를 생성한다.
 *
 * =================================================
 *
 * 세운 × 월운은 두 외부 시간축의 관계다.
 *
 * 실제 9차 합화 판정에 필요한
 * 목표 오행의 환경만 현재 요청의
 * ElementStrengthAnalysis에서 가져온다.
 *
 * =================================================
 *
 * 여기서는:
 *
 * - 원국 글자를 끼워 넣지 않는다.
 * - 대운 글자를 끼워 넣지 않는다.
 * - 외부 운축에 PalacePosition을 부여하지 않는다.
 * - 사용자별 상태를 Singleton field에 저장하지 않는다.
 *
 * 다층 Context는 정밀화 15차-7 책임이다.
 */
@Component
public class MonthlyAnnualLuckCrossResolverFactory {

    private final MonthlyAnnualLuckCrossAnalyzer
            monthlyAnnualLuckCrossAnalyzer;

    public MonthlyAnnualLuckCrossResolverFactory(
            MonthlyAnnualLuckCrossAnalyzer
                    monthlyAnnualLuckCrossAnalyzer
    ) {

        this.monthlyAnnualLuckCrossAnalyzer =
                Objects.requireNonNull(
                        monthlyAnnualLuckCrossAnalyzer,
                        "MonthlyAnnualLuckCrossAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15차-6 Factory API
     * =================================================
     */
    public MonthlyAnnualLuckCrossResolver create(
            NatalChartAdvancedAnalysis advancedAnalysis
    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "15-6 Resolver 생성에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        ElementStrengthAnalysis elementStrengthAnalysis =
                Objects.requireNonNull(
                        advancedAnalysis
                                .elementStrengthAnalysis(),
                        "15-6 Resolver 생성에는 ElementStrengthAnalysis가 필요합니다."
                );

        /*
         * 현재 요청의 immutable 원국 환경만
         * Resolver closure에 캡처한다.
         */
        return (
                annualPeriod,
                monthlyPeriod
        ) -> {

            Objects.requireNonNull(
                    annualPeriod,
                    "15-6 Resolver에는 AnnualLuckPeriod가 필요합니다."
            );

            Objects.requireNonNull(
                    monthlyPeriod,
                    "15-6 Resolver에는 MonthlyLuckPeriod가 필요합니다."
            );

            /*
             * 값이 같은 세운을 재생성해서 허용하지 않는다.
             *
             * 16차에서 선택한 정확한 Period 객체가
             * 15차까지 유지되어야 한다.
             */
            if (
                    monthlyPeriod.annualPeriod()
                            != annualPeriod
            ) {

                throw new IllegalArgumentException(
                        "15-6 Resolver의 MonthlyLuckPeriod가 현재 AnnualLuckPeriod에 속하지 않습니다."
                );
            }

            return monthlyAnnualLuckCrossAnalyzer
                    .analyze(
                            elementStrengthAnalysis,
                            annualPeriod,
                            monthlyPeriod
                    );
        };
    }
}