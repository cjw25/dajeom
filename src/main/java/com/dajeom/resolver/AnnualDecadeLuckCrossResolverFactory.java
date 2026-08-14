package com.dajeom.resolver;

import com.dajeom.analyzer.AnnualDecadeLuckCrossAnalyzer;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 14차-5.
 *
 * 요청별 immutable AnnualDecadeLuckCrossResolver를 생성한다.
 *
 * =================================================
 *
 * 대운 × 세운 자체는 두 외부 시간축의 관계다.
 *
 * 다만 기존 9차 합화 기준에서 사용하는
 * 목표 오행의 원국 환경을 참조하기 위해
 * 현재 요청의 ElementStrengthAnalysis를 사용한다.
 *
 * =================================================
 *
 * 여기서는:
 *
 * 원국 글자 × 대운 × 세운
 *
 * 3중 관계를 계산하지 않는다.
 *
 * 3중 Context는 14-6의 책임이다.
 *
 * =================================================
 *
 * Spring singleton Resolver에
 * 사용자별 원국 상태를 저장하지 않는다.
 */
@Component
public class AnnualDecadeLuckCrossResolverFactory {

    private final AnnualDecadeLuckCrossAnalyzer
            annualDecadeLuckCrossAnalyzer;

    public AnnualDecadeLuckCrossResolverFactory(
            AnnualDecadeLuckCrossAnalyzer
                    annualDecadeLuckCrossAnalyzer
    ) {

        this.annualDecadeLuckCrossAnalyzer =
                Objects.requireNonNull(
                        annualDecadeLuckCrossAnalyzer,
                        "AnnualDecadeLuckCrossAnalyzer가 필요합니다."
                );
    }

    public AnnualDecadeLuckCrossResolver create(
            NatalChartAdvancedAnalysis advancedAnalysis
    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "14-5 Resolver 생성에는 원국 고급 분석이 필요합니다."
        );

        return (
                annualPeriod,
                decadePeriod
        ) ->
                annualDecadeLuckCrossAnalyzer
                        .analyze(
                                advancedAnalysis
                                        .elementStrengthAnalysis(),
                                annualPeriod,
                                decadePeriod
                        );
    }
}