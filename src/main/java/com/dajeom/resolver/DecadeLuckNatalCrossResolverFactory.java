package com.dajeom.resolver;

import com.dajeom.analyzer.DecadeLuckNatalCrossAnalyzer;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 13차-5.
 *
 * 요청별 immutable DecadeLuckNatalCrossResolver를 생성한다.
 *
 * =================================================
 *
 * 이유
 *
 * DecadeLuckNatalCrossResolver.resolve()의 공식 Boundary는:
 *
 * PalaceAnalysis
 * +
 * DecadeLuckPeriod
 *
 * 이지만 실제 9차 수준의 합화 정밀화에는
 * 해당 요청의:
 *
 * - ElementStrengthAnalysis
 * - RelationAnalysis
 * - CombinationTransformationAnalysis
 *
 * 가 필요하다.
 *
 * 따라서 request state를 singleton Resolver 안에
 * mutable field로 저장하지 않는다.
 *
 * Spring singleton Factory가
 * 현재 요청의 immutable 분석 결과를 캡처한
 * Resolver를 생성한다.
 */
@Component
public class DecadeLuckNatalCrossResolverFactory {

    private final DecadeLuckNatalCrossAnalyzer
            decadeLuckNatalCrossAnalyzer;

    public DecadeLuckNatalCrossResolverFactory(
            DecadeLuckNatalCrossAnalyzer
                    decadeLuckNatalCrossAnalyzer
    ) {

        this.decadeLuckNatalCrossAnalyzer =
                Objects.requireNonNull(
                        decadeLuckNatalCrossAnalyzer,
                        "DecadeLuckNatalCrossAnalyzer가 필요합니다."
                );
    }

    public DecadeLuckNatalCrossResolver create(

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

        /*
         * Resolver 자체는 mutable request state를 갖지 않는다.
         *
         * 아래 두 객체 reference는 해당 analyze() 요청에서
         * 이미 완성된 immutable Evidence aggregate다.
         */
        return (
                resolvedNatalAnalysis,
                period
        ) -> {

            Objects.requireNonNull(
                    resolvedNatalAnalysis,
                    "13-5 Resolver에는 PalaceAnalysis가 필요합니다."
            );

            Objects.requireNonNull(
                    period,
                    "13-5 Resolver에는 DecadeLuckPeriod가 필요합니다."
            );

            /*
             * 13차 Engine이 다른 원국을 잘못 섞는 것을 방지한다.
             *
             * DecadeLuckAnalysis 자체도 동일 원국 identity를
             * 엄격히 검증하므로 여기서도 동일 정책을 유지한다.
             */
            if (
                    resolvedNatalAnalysis
                            != natalAnalysis
            ) {

                throw new IllegalArgumentException(
                        "13-5 Resolver가 생성될 때의 PalaceAnalysis와 실제 전달된 PalaceAnalysis가 다릅니다."
                );
            }

            return decadeLuckNatalCrossAnalyzer
                    .analyze(
                            advancedAnalysis,
                            natalAnalysis,
                            period
                    );
        };
    }
}