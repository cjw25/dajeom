package com.dajeom.analyzer;

import com.dajeom.model.MonthlyLuckNatalCrossResolution;
import com.dajeom.model.MonthlyLuckNatalRelationAnalysis;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.resolver.MonthlyLuckNatalCrossResolver;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 15차-4.
 *
 * 원국 × 월운 관계 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * 12차 PalaceAnalysis
 * +
 * 15-2 MonthlyLuckPeriod
 * +
 * 8/9차 연결 Resolver
 *
 * =================================================
 *
 * 출력:
 *
 * MonthlyLuckNatalRelationAnalysis
 *
 * =================================================
 *
 * 이 Analyzer에서는:
 *
 * - 천간합 공식을 만들지 않는다.
 * - 합충형파해 표를 만들지 않는다.
 * - 삼합·방합 표를 만들지 않는다.
 * - 합화 조건을 다시 계산하지 않는다.
 * - 길흉 점수를 만들지 않는다.
 *
 * 기존 deterministic 결과를
 * 월운 시간축에 연결하는 역할만 한다.
 */
@Component
public class MonthlyLuckNatalRelationAnalyzer {

    /**
     * =================================================
     * 공식 15-4 API
     * =================================================
     */
    public MonthlyLuckNatalRelationAnalysis analyze(

            PalaceAnalysis natalAnalysis,

            MonthlyLuckPeriod monthlyLuckPeriod,

            MonthlyLuckNatalCrossResolver crossResolver

    ) {

        Objects.requireNonNull(
                natalAnalysis,
                "15-4 분석에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckPeriod,
                "15-4 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                crossResolver,
                "MonthlyLuckNatalCrossResolver가 필요합니다."
        );

        MonthlyLuckNatalCrossResolution resolution =
                Objects.requireNonNull(
                        crossResolver.resolve(
                                natalAnalysis,
                                monthlyLuckPeriod
                        ),
                        "MonthlyLuckNatalCrossResolver가 null을 반환했습니다."
                );

        /*
         * Resolver가 다른 월운을 반환하면 안 된다.
         */
        if (
                resolution.period()
                        != monthlyLuckPeriod
        ) {

            throw new IllegalArgumentException(
                    "Resolver가 현재 MonthlyLuckPeriod가 아닌 다른 월운 결과를 반환했습니다."
            );
        }

        return new MonthlyLuckNatalRelationAnalysis(
                natalAnalysis.dayMaster(),
                natalAnalysis,
                monthlyLuckPeriod,
                resolution.relations(),
                resolution.transformations()
        );
    }
}