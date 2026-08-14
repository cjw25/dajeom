package com.dajeom.resolver;

import com.dajeom.analyzer.MonthlyDecadeLuckCrossAnalyzer;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 15차-5.
 *
 * 요청별 immutable
 * MonthlyDecadeLuckCrossResolver를 생성한다.
 *
 * =================================================
 *
 * 대운 × 월운 자체는
 * 두 외부 시간축 사이의 관계다.
 *
 * 다만 기존 9차 합화 판단에서 필요한
 * 목표 오행의 원국 환경은
 * 현재 요청의 3차 ElementStrengthAnalysis를 사용한다.
 *
 * =================================================
 *
 * 이 Factory에서는:
 *
 * - 원국 글자 × 대운 × 월운을 계산하지 않는다.
 * - 세운 × 대운 × 월운을 계산하지 않는다.
 * - PalacePosition을 외부 운축에 부여하지 않는다.
 *
 * 위 다층 Context는 15차-7의 책임이다.
 *
 * =================================================
 *
 * Spring Singleton Bean에
 * 사용자별 ElementStrengthAnalysis를
 * mutable field로 저장하지 않는다.
 *
 * 현재 요청에서 이미 완성된 immutable 분석 객체를
 * 요청별 Resolver closure에 캡처한다.
 */
@Component
public class MonthlyDecadeLuckCrossResolverFactory {

    private final MonthlyDecadeLuckCrossAnalyzer
            monthlyDecadeLuckCrossAnalyzer;

    public MonthlyDecadeLuckCrossResolverFactory(
            MonthlyDecadeLuckCrossAnalyzer
                    monthlyDecadeLuckCrossAnalyzer
    ) {

        this.monthlyDecadeLuckCrossAnalyzer =
                Objects.requireNonNull(
                        monthlyDecadeLuckCrossAnalyzer,
                        "MonthlyDecadeLuckCrossAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15차-5 Factory API
     * =================================================
     */
    public MonthlyDecadeLuckCrossResolver create(
            NatalChartAdvancedAnalysis advancedAnalysis
    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "15-5 Resolver 생성에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        ElementStrengthAnalysis elementStrengthAnalysis =
                Objects.requireNonNull(
                        advancedAnalysis
                                .elementStrengthAnalysis(),
                        "15-5 Resolver 생성에는 3차 ElementStrengthAnalysis가 필요합니다."
                );

        /*
         * 요청별 immutable Resolver.
         *
         * ElementStrengthAnalysis는 현재 요청에서
         * 이미 완성된 deterministic 원국 분석 객체다.
         */
        return (
                monthlyPeriod,
                decadePeriod
        ) -> {

            Objects.requireNonNull(
                    monthlyPeriod,
                    "15-5 Resolver에는 MonthlyLuckPeriod가 필요합니다."
            );

            Objects.requireNonNull(
                    decadePeriod,
                    "15-5 Resolver에는 DecadeLuckPeriod가 필요합니다."
            );

            return monthlyDecadeLuckCrossAnalyzer
                    .analyze(
                            elementStrengthAnalysis,
                            monthlyPeriod,
                            decadePeriod
                    );
        };
    }
}