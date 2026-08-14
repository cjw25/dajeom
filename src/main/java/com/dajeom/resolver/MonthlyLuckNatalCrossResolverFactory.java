package com.dajeom.resolver;

import com.dajeom.analyzer.MonthlyLuckNatalCrossAnalyzer;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 15차-4.
 *
 * 요청별 immutable
 * MonthlyLuckNatalCrossResolver를 생성한다.
 *
 * =================================================
 *
 * 월운 × 원국 관계의 실제 정밀 분석에는
 * 현재 요청에서 이미 계산된:
 *
 * - 3차 ElementStrengthAnalysis
 * - 8차 RelationAnalysis
 * - 9차 CombinationTransformationAnalysis
 * - 12차 PalaceAnalysis
 *
 * 가 필요하다.
 *
 * =================================================
 *
 * 따라서:
 *
 * Spring Singleton Resolver 내부에
 * 사용자별 분석 상태를 mutable field로
 * 저장하지 않는다.
 *
 * Singleton Factory가 현재 요청의
 * immutable 분석 객체를 closure로 캡처한
 * Resolver를 생성한다.
 *
 * =================================================
 *
 * 또한 실제 resolve 시 전달되는
 * PalaceAnalysis가 Factory 생성 당시의
 * 객체와 동일한지 Object Identity로 검증한다.
 */
@Component
public class MonthlyLuckNatalCrossResolverFactory {

    private final MonthlyLuckNatalCrossAnalyzer
            monthlyLuckNatalCrossAnalyzer;

    public MonthlyLuckNatalCrossResolverFactory(
            MonthlyLuckNatalCrossAnalyzer
                    monthlyLuckNatalCrossAnalyzer
    ) {

        this.monthlyLuckNatalCrossAnalyzer =
                Objects.requireNonNull(
                        monthlyLuckNatalCrossAnalyzer,
                        "MonthlyLuckNatalCrossAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15차-4 Factory API
     * =================================================
     */
    public MonthlyLuckNatalCrossResolver create(

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

        /*
         * =================================================
         * 요청 Context 정합성
         * =================================================
         */

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
                    "원국 고급 분석과 PalaceAnalysis의 completeChart 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 요청별 immutable Resolver
         * =================================================
         *
         * advancedAnalysis
         * natalAnalysis
         *
         * 는 현재 하나의 analyze 요청에서 이미
         * 생성 완료된 immutable 분석 결과다.
         *
         * Singleton mutable state에 저장하지 않고
         * 아래 lambda가 closure로 캡처한다.
         */

        return (
                resolvedNatalAnalysis,
                monthlyLuckPeriod
        ) -> {

            Objects.requireNonNull(
                    resolvedNatalAnalysis,
                    "15-4 Resolver에는 PalaceAnalysis가 필요합니다."
            );

            Objects.requireNonNull(
                    monthlyLuckPeriod,
                    "15-4 Resolver에는 MonthlyLuckPeriod가 필요합니다."
            );

            /*
             * 다른 사용자/다른 분석 요청의
             * PalaceAnalysis가 섞이는 것을 막는다.
             */
            if (
                    resolvedNatalAnalysis
                            != natalAnalysis
            ) {

                throw new IllegalArgumentException(
                        "15-4 Resolver 생성 당시의 PalaceAnalysis와 실제 전달된 PalaceAnalysis가 다릅니다."
                );
            }

            return monthlyLuckNatalCrossAnalyzer
                    .analyze(
                            advancedAnalysis,
                            natalAnalysis,
                            monthlyLuckPeriod
                    );
        };
    }
}