package com.dajeom.resolver;

import com.dajeom.analyzer.AnnualLuckContextSignalAnalyzer;
import com.dajeom.model.AnnualDecadeLuckInteraction;
import com.dajeom.model.AnnualLuckContextResolution;
import com.dajeom.model.AnnualLuckNatalRelationAnalysis;
import com.dajeom.model.AnnualLuckTenGodAnalysis;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 14차-6.
 *
 * 요청별 immutable AnnualLuckContextResolver Factory.
 *
 * =================================================
 *
 * 현재 요청에서 계산된:
 *
 * 5차 StructureAnalysis
 * 7차 UsefulGodAnalysis
 * 10차 TenGodCompositeAnalysis
 * 12차 PalaceAnalysis
 *
 * 를 immutable closure로 캡처한다.
 *
 * =================================================
 *
 * Spring Singleton Resolver에
 * 사용자별 원국 분석을 mutable field로
 * 저장하지 않는다.
 */
@Component
public class AnnualLuckContextResolverFactory {

    private final AnnualLuckContextSignalAnalyzer
            annualLuckContextSignalAnalyzer;

    public AnnualLuckContextResolverFactory(
            AnnualLuckContextSignalAnalyzer
                    annualLuckContextSignalAnalyzer
    ) {

        this.annualLuckContextSignalAnalyzer =
                Objects.requireNonNull(
                        annualLuckContextSignalAnalyzer,
                        "AnnualLuckContextSignalAnalyzer가 필요합니다."
                );
    }

    public AnnualLuckContextResolver create(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "14-6 Resolver 생성에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "14-6 Resolver 생성에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "14-6 Resolver 생성에는 PalaceAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 동일 원국 Identity 검증
         * =================================================
         */

        if (
                tenGodCompositeAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10차와 12차의 일간이 일치하지 않습니다."
            );
        }

        if (
                advancedAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "3차와 12차의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .structureAnalysis()
                        != advancedAnalysis
                        .structureAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차가 현재 5차 StructureAnalysis를 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .usefulGodAnalysis()
                        != advancedAnalysis
                        .usefulGodAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차가 현재 7차 UsefulGodAnalysis를 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis
                        .combinationTransformationAnalysis()
                        != advancedAnalysis
                        .combinationTransformationAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "10차가 현재 9차 CombinationTransformationAnalysis를 참조하지 않습니다."
            );
        }

        if (
                tenGodCompositeAnalysis.completeChart()
                        != palaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "10차와 12차의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 요청별 immutable Resolver
         * =================================================
         */

        return new AnnualLuckContextResolver() {

            @Override
            public AnnualLuckContextResolution
            resolveAnnual(

                    AnnualLuckTenGodAnalysis
                            annualTenGodAnalysis,

                    AnnualLuckNatalRelationAnalysis
                            natalRelationAnalysis

            ) {

                Objects.requireNonNull(
                        annualTenGodAnalysis,
                        "resolveAnnual()에는 AnnualLuckTenGodAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        natalRelationAnalysis,
                        "resolveAnnual()에는 AnnualLuckNatalRelationAnalysis가 필요합니다."
                );

                if (
                        natalRelationAnalysis
                                .natalAnalysis()
                                != palaceAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "14-6 Resolver 생성 당시의 PalaceAnalysis와 14-4 관계 분석의 원국이 다릅니다."
                    );
                }

                return AnnualLuckContextResolution
                        .annualOnly(
                                annualTenGodAnalysis
                                        .period(),

                                annualLuckContextSignalAnalyzer
                                        .analyzeAnnual(
                                                advancedAnalysis
                                                        .structureAnalysis(),

                                                advancedAnalysis
                                                        .usefulGodAnalysis(),

                                                tenGodCompositeAnalysis,

                                                palaceAnalysis,

                                                annualTenGodAnalysis,

                                                natalRelationAnalysis
                                        )
                        );
            }

            @Override
            public AnnualLuckContextResolution
            resolveWithDecade(

                    AnnualLuckTenGodAnalysis
                            annualTenGodAnalysis,

                    AnnualLuckNatalRelationAnalysis
                            natalRelationAnalysis,

                    AnnualDecadeLuckInteraction
                            decadeInteraction,

                    DecadeLuckPeriodContextAnalysis
                            decadeContext

            ) {

                Objects.requireNonNull(
                        annualTenGodAnalysis,
                        "resolveWithDecade()에는 AnnualLuckTenGodAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        natalRelationAnalysis,
                        "resolveWithDecade()에는 AnnualLuckNatalRelationAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        decadeInteraction,
                        "resolveWithDecade()에는 AnnualDecadeLuckInteraction이 필요합니다."
                );

                Objects.requireNonNull(
                        decadeContext,
                        "resolveWithDecade()에는 DecadeLuckPeriodContextAnalysis가 필요합니다."
                );

                if (
                        natalRelationAnalysis
                                .natalAnalysis()
                                != palaceAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "14-6 Segment Resolver가 현재 요청과 다른 PalaceAnalysis를 참조합니다."
                    );
                }

                if (
                        decadeContext.period()
                                != decadeInteraction
                                .decadePeriod()
                ) {

                    throw new IllegalArgumentException(
                            "13-6 대운 Context와 14-5 Interaction의 대운이 다릅니다."
                    );
                }

                return AnnualLuckContextResolution
                        .withDecade(
                                annualTenGodAnalysis
                                        .period(),

                                decadeInteraction
                                        .decadePeriod(),

                                annualLuckContextSignalAnalyzer
                                        .analyzeWithDecade(
                                                palaceAnalysis,
                                                annualTenGodAnalysis,
                                                natalRelationAnalysis,
                                                decadeInteraction,
                                                decadeContext
                                        )
                        );
            }
        };
    }
}