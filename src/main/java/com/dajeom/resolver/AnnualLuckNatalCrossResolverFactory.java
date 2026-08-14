package com.dajeom.resolver;

import com.dajeom.analyzer.AnnualLuckNatalCrossAnalyzer;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 14차-4.
 *
 * 요청별 immutable AnnualLuckNatalCrossResolver를 생성한다.
 *
 * 세운 × 원국 관계의 실제 합화 판정을 위해서는
 * 현재 요청의:
 *
 * - 3차 ElementStrengthAnalysis
 * - 8차 RelationAnalysis
 * - 9차 CombinationTransformationAnalysis
 *
 * 가 필요하다.
 *
 * 따라서 singleton Resolver 내부에
 * 사용자별 분석 상태를 mutable field로 저장하지 않는다.
 */
@Component
public class AnnualLuckNatalCrossResolverFactory {

    private final AnnualLuckNatalCrossAnalyzer
            annualLuckNatalCrossAnalyzer;

    public AnnualLuckNatalCrossResolverFactory(
            AnnualLuckNatalCrossAnalyzer
                    annualLuckNatalCrossAnalyzer
    ) {

        this.annualLuckNatalCrossAnalyzer =
                Objects.requireNonNull(
                        annualLuckNatalCrossAnalyzer,
                        "AnnualLuckNatalCrossAnalyzer가 필요합니다."
                );
    }

    public AnnualLuckNatalCrossResolver create(

            NatalChartAdvancedAnalysis advancedAnalysis,

            PalaceAnalysis natalAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "원국 고급 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "12차 PalaceAnalysis가 필요합니다."
        );

        if (
                advancedAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
                        != natalAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "원국 고급 분석과 PalaceAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                advancedAnalysis.hasHourPillar()
                        != natalAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "원국 고급 분석과 PalaceAnalysis의 출생시간 상태가 일치하지 않습니다."
            );
        }

        return (
                resolvedNatalAnalysis,
                annualLuckPeriod
        ) -> {

            Objects.requireNonNull(
                    resolvedNatalAnalysis,
                    "14-4 Resolver에는 PalaceAnalysis가 필요합니다."
            );

            Objects.requireNonNull(
                    annualLuckPeriod,
                    "14-4 Resolver에는 AnnualLuckPeriod가 필요합니다."
            );

            if (
                    resolvedNatalAnalysis
                            != natalAnalysis
            ) {

                throw new IllegalArgumentException(
                        "14-4 Resolver 생성 당시의 PalaceAnalysis와 실제 전달된 PalaceAnalysis가 다릅니다."
                );
            }

            return annualLuckNatalCrossAnalyzer
                    .analyze(
                            advancedAnalysis,
                            natalAnalysis,
                            annualLuckPeriod
                    );
        };
    }
}