package com.dajeom.resolver;

import com.dajeom.engine.AnnualLuckAnalysisEngine;
import com.dajeom.engine.MonthlyLuckAnalysisEngine;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckCandidate;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.MonthlyLuckAnalysis;
import com.dajeom.model.MonthlyLuckCandidate;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 16차.
 *
 * 16차 Candidate Selection과
 * 기존 14/15차 deterministic Engine 사이를 연결하는
 * 요청별 immutable Resolver Factory.
 *
 * =================================================
 *
 * 역할:
 *
 * 16-3 AnnualLuckCandidate
 *        ↓
 * exact AnnualLuckPeriod
 *        ↓
 * 14차 AnnualLuckAnalysisEngine
 *
 * 16-4 MonthlyLuckCandidate
 *        ↓
 * exact MonthlyLuckPeriod
 *        ↓
 * 15차 MonthlyLuckAnalysisEngine
 *
 * =================================================
 *
 * 핵심:
 *
 * 16차에서 선택된 Period를 다시 계산하지 않는다.
 *
 * 따라서:
 *
 * AnnualLuckAnalysis.period()
 * ==
 * AnnualLuckCandidate.period()
 *
 * MonthlyLuckAnalysis.period()
 * ==
 * MonthlyLuckCandidate.period()
 *
 * Identity가 유지된다.
 *
 * =================================================
 *
 * 요청별 원국 분석 결과는
 * Singleton mutable state에 저장하지 않는다.
 *
 * Factory의 create(...)에서 생성되는
 * Resolver closure 안에 immutable하게 캡처한다.
 */
@Component
public class LuckIntegratedLayerAnalysisResolverFactory {

    private final AnnualLuckAnalysisEngine
            annualLuckAnalysisEngine;

    private final MonthlyLuckAnalysisEngine
            monthlyLuckAnalysisEngine;

    private final AnnualLuckTenGodSourceResolver
            annualLuckTenGodSourceResolver;

    private final MonthlyLuckTenGodSourceResolver
            monthlyLuckTenGodSourceResolver;

    private final AnnualLuckNatalCrossResolverFactory
            annualLuckNatalCrossResolverFactory;

    private final AnnualDecadeLuckCrossResolverFactory
            annualDecadeLuckCrossResolverFactory;

    private final AnnualLuckContextResolverFactory
            annualLuckContextResolverFactory;

    private final MonthlyLuckNatalCrossResolverFactory
            monthlyLuckNatalCrossResolverFactory;

    private final MonthlyDecadeLuckCrossResolverFactory
            monthlyDecadeLuckCrossResolverFactory;

    private final MonthlyAnnualLuckCrossResolverFactory
            monthlyAnnualLuckCrossResolverFactory;

    private final MonthlyLuckContextResolverFactory
            monthlyLuckContextResolverFactory;

    public LuckIntegratedLayerAnalysisResolverFactory(

            AnnualLuckAnalysisEngine
                    annualLuckAnalysisEngine,

            MonthlyLuckAnalysisEngine
                    monthlyLuckAnalysisEngine,

            AnnualLuckTenGodSourceResolver
                    annualLuckTenGodSourceResolver,

            MonthlyLuckTenGodSourceResolver
                    monthlyLuckTenGodSourceResolver,

            AnnualLuckNatalCrossResolverFactory
                    annualLuckNatalCrossResolverFactory,

            AnnualDecadeLuckCrossResolverFactory
                    annualDecadeLuckCrossResolverFactory,

            AnnualLuckContextResolverFactory
                    annualLuckContextResolverFactory,

            MonthlyLuckNatalCrossResolverFactory
                    monthlyLuckNatalCrossResolverFactory,

            MonthlyDecadeLuckCrossResolverFactory
                    monthlyDecadeLuckCrossResolverFactory,

            MonthlyAnnualLuckCrossResolverFactory
                    monthlyAnnualLuckCrossResolverFactory,

            MonthlyLuckContextResolverFactory
                    monthlyLuckContextResolverFactory

    ) {

        this.annualLuckAnalysisEngine =
                Objects.requireNonNull(
                        annualLuckAnalysisEngine,
                        "AnnualLuckAnalysisEngine이 필요합니다."
                );

        this.monthlyLuckAnalysisEngine =
                Objects.requireNonNull(
                        monthlyLuckAnalysisEngine,
                        "MonthlyLuckAnalysisEngine이 필요합니다."
                );

        this.annualLuckTenGodSourceResolver =
                Objects.requireNonNull(
                        annualLuckTenGodSourceResolver,
                        "AnnualLuckTenGodSourceResolver가 필요합니다."
                );

        this.monthlyLuckTenGodSourceResolver =
                Objects.requireNonNull(
                        monthlyLuckTenGodSourceResolver,
                        "MonthlyLuckTenGodSourceResolver가 필요합니다."
                );

        this.annualLuckNatalCrossResolverFactory =
                Objects.requireNonNull(
                        annualLuckNatalCrossResolverFactory,
                        "AnnualLuckNatalCrossResolverFactory가 필요합니다."
                );

        this.annualDecadeLuckCrossResolverFactory =
                Objects.requireNonNull(
                        annualDecadeLuckCrossResolverFactory,
                        "AnnualDecadeLuckCrossResolverFactory가 필요합니다."
                );

        this.annualLuckContextResolverFactory =
                Objects.requireNonNull(
                        annualLuckContextResolverFactory,
                        "AnnualLuckContextResolverFactory가 필요합니다."
                );

        this.monthlyLuckNatalCrossResolverFactory =
                Objects.requireNonNull(
                        monthlyLuckNatalCrossResolverFactory,
                        "MonthlyLuckNatalCrossResolverFactory가 필요합니다."
                );

        this.monthlyDecadeLuckCrossResolverFactory =
                Objects.requireNonNull(
                        monthlyDecadeLuckCrossResolverFactory,
                        "MonthlyDecadeLuckCrossResolverFactory가 필요합니다."
                );

        this.monthlyAnnualLuckCrossResolverFactory =
                Objects.requireNonNull(
                        monthlyAnnualLuckCrossResolverFactory,
                        "MonthlyAnnualLuckCrossResolverFactory가 필요합니다."
                );

        this.monthlyLuckContextResolverFactory =
                Objects.requireNonNull(
                        monthlyLuckContextResolverFactory,
                        "MonthlyLuckContextResolverFactory가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 Factory API
     * =================================================
     */
    public LuckIntegratedLayerAnalysisResolver create(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "16차 Layer Resolver 생성에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "16차 Layer Resolver 생성에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "16차 Layer Resolver 생성에는 PalaceAnalysis가 필요합니다."
        );

        validateCapturedContext(
                advancedAnalysis,
                tenGodCompositeAnalysis,
                palaceAnalysis
        );

        /*
         * =================================================
         * 요청별 14차 Resolver
         * =================================================
         */

        AnnualLuckNatalCrossResolver
                annualNatalCrossResolver =
                annualLuckNatalCrossResolverFactory
                        .create(
                                advancedAnalysis,
                                palaceAnalysis
                        );

        AnnualDecadeLuckCrossResolver
                annualDecadeCrossResolver =
                annualDecadeLuckCrossResolverFactory
                        .create(
                                advancedAnalysis
                        );

        AnnualLuckContextResolver
                annualContextResolver =
                annualLuckContextResolverFactory
                        .create(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                palaceAnalysis
                        );

        /*
         * =================================================
         * 요청별 15차 Resolver
         * =================================================
         */

        MonthlyLuckNatalCrossResolver
                monthlyNatalCrossResolver =
                monthlyLuckNatalCrossResolverFactory
                        .create(
                                advancedAnalysis,
                                palaceAnalysis
                        );

        MonthlyDecadeLuckCrossResolver
                monthlyDecadeCrossResolver =
                monthlyDecadeLuckCrossResolverFactory
                        .create(
                                advancedAnalysis
                        );

        MonthlyAnnualLuckCrossResolver
                monthlyAnnualCrossResolver =
                monthlyAnnualLuckCrossResolverFactory
                        .create(
                                advancedAnalysis
                        );

        MonthlyLuckContextResolver
                monthlyContextResolver =
                monthlyLuckContextResolverFactory
                        .create(
                                advancedAnalysis,
                                tenGodCompositeAnalysis,
                                palaceAnalysis
                        );

        /*
         * =================================================
         * 요청별 immutable Layer Resolver
         * =================================================
         */

        return new LuckIntegratedLayerAnalysisResolver() {

            /**
             * =============================================
             * 16-3 Candidate -> 14차
             * =============================================
             */
            @Override
            public AnnualLuckAnalysis resolveAnnual(

                    PalaceAnalysis resolvedNatalAnalysis,

                    DecadeLuckAnalysis decadeLuckAnalysis,

                    AnnualLuckCandidate annualCandidate

            ) {

                Objects.requireNonNull(
                        resolvedNatalAnalysis,
                        "resolveAnnual()에는 PalaceAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        decadeLuckAnalysis,
                        "resolveAnnual()에는 DecadeLuckAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        annualCandidate,
                        "resolveAnnual()에는 AnnualLuckCandidate가 필요합니다."
                );

                /*
                 * Factory가 생성된 요청의 원국만 허용한다.
                 */
                if (
                        resolvedNatalAnalysis
                                != palaceAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "16차 Layer Resolver 생성 당시와 다른 PalaceAnalysis가 전달되었습니다."
                    );
                }

                if (
                        decadeLuckAnalysis.natalAnalysis()
                                != palaceAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "16차 Layer Resolver의 DecadeLuckAnalysis가 현재 PalaceAnalysis를 참조하지 않습니다."
                    );
                }

                if (
                        decadeLuckAnalysis.dayMaster()
                                != palaceAnalysis.dayMaster()
                ) {

                    throw new IllegalArgumentException(
                            "16차 Layer Resolver의 대운과 원국 일간이 일치하지 않습니다."
                    );
                }

                /*
                 * 중요:
                 *
                 * annualCandidate.period()를
                 * targetYear로 바꾸어 다시 계산하지 않는다.
                 */
                AnnualLuckAnalysis analysis =
                        annualLuckAnalysisEngine
                                .analyze(
                                        annualCandidate.period(),
                                        palaceAnalysis,
                                        decadeLuckAnalysis,
                                        annualLuckTenGodSourceResolver,
                                        annualNatalCrossResolver,
                                        annualDecadeCrossResolver,
                                        annualContextResolver
                                );

                if (
                        analysis.period()
                                != annualCandidate.period()
                ) {

                    throw new IllegalStateException(
                            "14차 분석이 16차 AnnualLuckCandidate의 Period Identity를 보존하지 않았습니다."
                    );
                }

                if (
                        analysis.natalAnalysis()
                                != palaceAnalysis
                ) {

                    throw new IllegalStateException(
                            "14차 분석이 현재 PalaceAnalysis를 보존하지 않았습니다."
                    );
                }

                if (
                        analysis.decadeLuckAnalysis()
                                != decadeLuckAnalysis
                ) {

                    throw new IllegalStateException(
                            "14차 분석이 현재 DecadeLuckAnalysis를 보존하지 않았습니다."
                    );
                }

                return analysis;
            }

            /**
             * =============================================
             * 16-4 Candidate -> 15차
             * =============================================
             */
            @Override
            public MonthlyLuckAnalysis resolveMonthly(

                    AnnualLuckAnalysis annualLuckAnalysis,

                    MonthlyLuckCandidate monthlyCandidate

            ) {

                Objects.requireNonNull(
                        annualLuckAnalysis,
                        "resolveMonthly()에는 AnnualLuckAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        monthlyCandidate,
                        "resolveMonthly()에는 MonthlyLuckCandidate가 필요합니다."
                );

                if (
                        annualLuckAnalysis.natalAnalysis()
                                != palaceAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "15차 연결 AnnualLuckAnalysis가 현재 PalaceAnalysis를 참조하지 않습니다."
                    );
                }

                /*
                 * MonthlyCandidate의 상위 AnnualCandidate와
                 * 현재 AnnualLuckAnalysis의 Period가
                 * 정확히 같은 객체여야 한다.
                 */
                if (
                        monthlyCandidate
                                .annualCandidate()
                                .period()
                                != annualLuckAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "16차 MonthlyLuckCandidate의 상위 AnnualLuckCandidate와 현재 AnnualLuckAnalysis가 다른 AnnualLuckPeriod를 참조합니다."
                    );
                }

                /*
                 * MonthlyLuckPeriod 내부 AnnualPeriod도
                 * 같은 객체여야 한다.
                 */
                if (
                        monthlyCandidate
                                .period()
                                .annualPeriod()
                                != annualLuckAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "16차 MonthlyLuckPeriod와 14차 AnnualLuckAnalysis의 Period Identity가 일치하지 않습니다."
                    );
                }

                /*
                 * 중요:
                 *
                 * monthIndex나 기준날짜로 월운을 다시 계산하지 않는다.
                 *
                 * 16-4가 선택한 MonthlyLuckPeriod를
                 * 그대로 15차 Engine에 전달한다.
                 */
                MonthlyLuckAnalysis analysis =
                        monthlyLuckAnalysisEngine
                                .analyze(
                                        annualLuckAnalysis,
                                        monthlyCandidate.period(),
                                        monthlyLuckTenGodSourceResolver,
                                        monthlyNatalCrossResolver,
                                        monthlyDecadeCrossResolver,
                                        monthlyAnnualCrossResolver,
                                        monthlyContextResolver
                                );

                if (
                        analysis.period()
                                != monthlyCandidate.period()
                ) {

                    throw new IllegalStateException(
                            "15차 분석이 16차 MonthlyLuckCandidate의 Period Identity를 보존하지 않았습니다."
                    );
                }

                if (
                        analysis.annualLuckAnalysis()
                                != annualLuckAnalysis
                ) {

                    throw new IllegalStateException(
                            "15차 분석이 현재 AnnualLuckAnalysis Identity를 보존하지 않았습니다."
                    );
                }

                if (
                        analysis
                                .period()
                                .annualPeriod()
                                != annualLuckAnalysis.period()
                ) {

                    throw new IllegalStateException(
                            "15차 월운과 14차 세운의 Period Identity가 일치하지 않습니다."
                    );
                }

                return analysis;
            }
        };
    }

    /*
     * =================================================
     * Factory Capture 정합성
     * =================================================
     */

    private void validateCapturedContext(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis

    ) {

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
                tenGodCompositeAnalysis.dayMaster()
                        != palaceAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10차와 12차의 일간이 일치하지 않습니다."
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

        if (
                advancedAnalysis.hasHourPillar()
                        != palaceAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "원국 고급 분석과 PalaceAnalysis의 출생시간 상태가 일치하지 않습니다."
            );
        }
    }
}