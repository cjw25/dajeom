package com.dajeom.analyzer;

import com.dajeom.model.AnnualLuckNatalCrossResolution;
import com.dajeom.model.AnnualLuckNatalRelationAnalysis;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.resolver.AnnualLuckNatalCrossResolver;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 14차-4.
 *
 * 원국 × 세운 관계 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * 12차 PalaceAnalysis
 * +
 * 14-2 AnnualLuckPeriod
 * +
 * 8/9차 연결 Resolver
 *
 * =================================================
 *
 * 출력:
 *
 * AnnualLuckNatalRelationAnalysis
 *
 * =================================================
 *
 * 이 Analyzer는:
 *
 * - 천간합 공식을 다시 만들지 않는다.
 * - 지지 합충형파해 공식을 다시 만들지 않는다.
 * - 삼합·방합 공식을 다시 만들지 않는다.
 * - 합화 조건을 다시 판정하지 않는다.
 * - 관계를 점수화하지 않는다.
 *
 * 기존 deterministic 결과를
 * 세운 Context에 연결한다.
 */
@Component
public class AnnualLuckNatalRelationAnalyzer {

    /**
     * =================================================
     * 공식 14-4 API
     * =================================================
     */
    public AnnualLuckNatalRelationAnalysis analyze(

            PalaceAnalysis natalAnalysis,

            AnnualLuckPeriod annualLuckPeriod,

            AnnualLuckNatalCrossResolver crossResolver

    ) {

        Objects.requireNonNull(
                natalAnalysis,
                "14-4 분석에는 원국 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckPeriod,
                "14-4 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                crossResolver,
                "AnnualLuckNatalCrossResolver가 필요합니다."
        );

        AnnualLuckNatalCrossResolution resolution =
                Objects.requireNonNull(
                        crossResolver.resolve(
                                natalAnalysis,
                                annualLuckPeriod
                        ),
                        "AnnualLuckNatalCrossResolver가 null을 반환했습니다."
                );

        /*
         * =================================================
         * 다른 세운 결과가 잘못 연결되는 것을 방지.
         * =================================================
         */

        if (
                resolution.period()
                        != annualLuckPeriod
        ) {

            throw new IllegalArgumentException(
                    "Resolver가 현재 AnnualLuckPeriod가 아닌 다른 세운 결과를 반환했습니다."
            );
        }

        return new AnnualLuckNatalRelationAnalysis(
                natalAnalysis.dayMaster(),
                natalAnalysis,
                annualLuckPeriod,
                resolution.relations(),
                resolution.transformations()
        );
    }
}