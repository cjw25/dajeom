package com.dajeom.resolver;

import com.dajeom.analyzer.MonthlyLuckContextSignalAnalyzer;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckDecadeContextAnalysis;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.MonthlyAnnualLuckRelationAnalysis;
import com.dajeom.model.MonthlyDecadeLuckInteraction;
import com.dajeom.model.MonthlyLuckContextResolution;
import com.dajeom.model.MonthlyLuckNatalRelationAnalysis;
import com.dajeom.model.MonthlyLuckTenGodAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 15차-7.
 *
 * 요청별 immutable
 * MonthlyLuckContextResolver Factory.
 *
 * =================================================
 *
 * 현재 요청에서 이미 계산된:
 *
 * - 5차 StructureAnalysis
 * - 7차 UsefulGodAnalysis
 * - 10차 TenGodCompositeAnalysis
 * - 12차 PalaceAnalysis
 *
 * 를 immutable closure로 캡처한다.
 *
 * =================================================
 *
 * resolveMonthly()
 *
 * 대운에 관계없이 현재 월운에서
 * 확정 가능한 Context를 생성한다.
 *
 * =================================================
 *
 * resolveWithDecade()
 *
 * 현재 월운과 실제로 겹치는
 * 특정 DecadeLuckPeriod Segment에서만
 * 추가 가능한 Context를 생성한다.
 *
 * =================================================
 *
 * 중요:
 *
 * Spring Singleton field에
 * 사용자별 원국 분석 상태를 저장하지 않는다.
 *
 * Period는 새로 만들지 않는다.
 *
 * 14/15/16차 Identity 정책에 따라
 * 현재 분석 계층에서 전달된 객체를 그대로 사용한다.
 */
@Component
public class MonthlyLuckContextResolverFactory {

    private final MonthlyLuckContextSignalAnalyzer
            monthlyLuckContextSignalAnalyzer;

    public MonthlyLuckContextResolverFactory(
            MonthlyLuckContextSignalAnalyzer
                    monthlyLuckContextSignalAnalyzer
    ) {

        this.monthlyLuckContextSignalAnalyzer =
                Objects.requireNonNull(
                        monthlyLuckContextSignalAnalyzer,
                        "MonthlyLuckContextSignalAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 15차-7 Factory API
     * =================================================
     */
    public MonthlyLuckContextResolver create(

            NatalChartAdvancedAnalysis advancedAnalysis,

            TenGodCompositeAnalysis tenGodCompositeAnalysis,

            PalaceAnalysis palaceAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "15-7 Resolver 생성에는 NatalChartAdvancedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "15-7 Resolver 생성에는 TenGodCompositeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                palaceAnalysis,
                "15-7 Resolver 생성에는 PalaceAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 동일 원국 정합성
         * =================================================
         */

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

        /*
         * =================================================
         * 요청별 immutable Resolver
         * =================================================
         */

        return new MonthlyLuckContextResolver() {

            /**
             * =============================================
             * 월운 전체 공통 Context
             * =============================================
             */
            @Override
            public MonthlyLuckContextResolution resolveMonthly(

                    AnnualLuckAnalysis annualLuckAnalysis,

                    MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

                    MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

                    MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis

            ) {

                Objects.requireNonNull(
                        annualLuckAnalysis,
                        "resolveMonthly()에는 AnnualLuckAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        monthlyTenGodAnalysis,
                        "resolveMonthly()에는 MonthlyLuckTenGodAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        natalRelationAnalysis,
                        "resolveMonthly()에는 MonthlyLuckNatalRelationAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        annualRelationAnalysis,
                        "resolveMonthly()에는 MonthlyAnnualLuckRelationAnalysis가 필요합니다."
                );

                /*
                 * =============================================
                 * 원국 Identity
                 * =============================================
                 */

                if (
                        annualLuckAnalysis.natalAnalysis()
                                != palaceAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "15-7 Resolver 생성 당시의 PalaceAnalysis와 14차 원국이 다릅니다."
                    );
                }

                if (
                        natalRelationAnalysis.natalAnalysis()
                                != palaceAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "15-4 관계 분석이 현재 요청의 PalaceAnalysis를 참조하지 않습니다."
                    );
                }

                /*
                 * =============================================
                 * 일간
                 * =============================================
                 */

                if (
                        annualLuckAnalysis.dayMaster()
                                != palaceAnalysis.dayMaster()

                                || monthlyTenGodAnalysis.dayMaster()
                                != palaceAnalysis.dayMaster()

                                || natalRelationAnalysis.dayMaster()
                                != palaceAnalysis.dayMaster()
                ) {

                    throw new IllegalArgumentException(
                            "14/15차 분석의 일간이 현재 원국과 일치하지 않습니다."
                    );
                }

                /*
                 * =============================================
                 * MonthlyLuckPeriod Identity
                 * =============================================
                 */

                if (
                        natalRelationAnalysis.period()
                                != monthlyTenGodAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "15-3과 15-4가 서로 다른 MonthlyLuckPeriod를 참조합니다."
                    );
                }

                if (
                        annualRelationAnalysis.monthlyPeriod()
                                != monthlyTenGodAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "15-6이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
                    );
                }

                /*
                 * =============================================
                 * AnnualLuckPeriod Identity
                 * =============================================
                 */

                if (
                        monthlyTenGodAnalysis
                                .period()
                                .annualPeriod()
                                != annualLuckAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "현재 MonthlyLuckPeriod가 현재 AnnualLuckAnalysis에 속하지 않습니다."
                    );
                }

                if (
                        annualRelationAnalysis.annualPeriod()
                                != annualLuckAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "15-6이 현재 AnnualLuckPeriod를 참조하지 않습니다."
                    );
                }

                return MonthlyLuckContextResolution
                        .monthlyOnly(
                                monthlyTenGodAnalysis
                                        .period(),

                                monthlyLuckContextSignalAnalyzer
                                        .analyzeMonthly(
                                                advancedAnalysis
                                                        .structureAnalysis(),

                                                advancedAnalysis
                                                        .usefulGodAnalysis(),

                                                tenGodCompositeAnalysis,

                                                palaceAnalysis,

                                                annualLuckAnalysis,

                                                monthlyTenGodAnalysis,

                                                natalRelationAnalysis,

                                                annualRelationAnalysis
                                        )
                        );
            }

            /**
             * =============================================
             * 특정 대운 Segment Context
             * =============================================
             */
            @Override
            public MonthlyLuckContextResolution resolveWithDecade(

                    AnnualLuckAnalysis annualLuckAnalysis,

                    MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

                    MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

                    MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis,

                    MonthlyDecadeLuckInteraction decadeInteraction,

                    DecadeLuckPeriodContextAnalysis decadeContext,

                    AnnualLuckDecadeContextAnalysis annualDecadeContext

            ) {

                Objects.requireNonNull(
                        annualLuckAnalysis,
                        "resolveWithDecade()에는 AnnualLuckAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        monthlyTenGodAnalysis,
                        "resolveWithDecade()에는 MonthlyLuckTenGodAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        natalRelationAnalysis,
                        "resolveWithDecade()에는 MonthlyLuckNatalRelationAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        annualRelationAnalysis,
                        "resolveWithDecade()에는 MonthlyAnnualLuckRelationAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        decadeInteraction,
                        "resolveWithDecade()에는 MonthlyDecadeLuckInteraction이 필요합니다."
                );

                Objects.requireNonNull(
                        decadeContext,
                        "resolveWithDecade()에는 DecadeLuckPeriodContextAnalysis가 필요합니다."
                );

                Objects.requireNonNull(
                        annualDecadeContext,
                        "resolveWithDecade()에는 AnnualLuckDecadeContextAnalysis가 필요합니다."
                );

                /*
                 * =============================================
                 * 원국 Identity
                 * =============================================
                 */

                if (
                        annualLuckAnalysis.natalAnalysis()
                                != palaceAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "15-7 Segment Resolver가 현재 요청과 다른 PalaceAnalysis를 참조합니다."
                    );
                }

                if (
                        natalRelationAnalysis.natalAnalysis()
                                != palaceAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "15-4 관계 분석이 현재 요청과 다른 PalaceAnalysis를 참조합니다."
                    );
                }

                /*
                 * =============================================
                 * MonthlyLuckPeriod Identity
                 * =============================================
                 */

                if (
                        natalRelationAnalysis.period()
                                != monthlyTenGodAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "15-3과 15-4가 서로 다른 MonthlyLuckPeriod를 참조합니다."
                    );
                }

                if (
                        annualRelationAnalysis.monthlyPeriod()
                                != monthlyTenGodAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "15-6이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
                    );
                }

                if (
                        decadeInteraction.monthlyPeriod()
                                != monthlyTenGodAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "15-5가 현재 MonthlyLuckPeriod를 참조하지 않습니다."
                    );
                }

                /*
                 * =============================================
                 * AnnualLuckPeriod Identity
                 * =============================================
                 */

                if (
                        monthlyTenGodAnalysis
                                .period()
                                .annualPeriod()
                                != annualLuckAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "현재 월운이 현재 AnnualLuckAnalysis에 속하지 않습니다."
                    );
                }

                if (
                        annualRelationAnalysis.annualPeriod()
                                != annualLuckAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "15-6이 다른 AnnualLuckPeriod를 참조합니다."
                    );
                }

                if (
                        annualDecadeContext.annualPeriod()
                                != annualLuckAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "14-6 세운 Segment가 현재 AnnualLuckPeriod를 참조하지 않습니다."
                    );
                }

                /*
                 * =============================================
                 * DecadeLuckPeriod Identity
                 * =============================================
                 */

                if (
                        decadeContext.period()
                                != decadeInteraction.decadePeriod()
                ) {

                    throw new IllegalArgumentException(
                            "13-6 대운 Context와 15-5 대운-월운 Interaction의 대운이 다릅니다."
                    );
                }

                if (
                        annualDecadeContext
                                .interaction()
                                .decadePeriod()
                                != decadeInteraction.decadePeriod()
                ) {

                    throw new IllegalArgumentException(
                            "14-6 세운 Segment와 15-5 월운 Segment의 대운이 다릅니다."
                    );
                }

                if (
                        annualDecadeContext.decadeContext()
                                != decadeContext
                ) {

                    throw new IllegalArgumentException(
                            "14-6 세운 Segment가 현재 13-6 대운 Context를 참조하지 않습니다."
                    );
                }

                /*
                 * =============================================
                 * 일간
                 * =============================================
                 */

                if (
                        annualLuckAnalysis.dayMaster()
                                != palaceAnalysis.dayMaster()

                                || monthlyTenGodAnalysis.dayMaster()
                                != palaceAnalysis.dayMaster()

                                || natalRelationAnalysis.dayMaster()
                                != palaceAnalysis.dayMaster()

                                || decadeContext.dayMaster()
                                != palaceAnalysis.dayMaster()

                                || annualDecadeContext.dayMaster()
                                != palaceAnalysis.dayMaster()
                ) {

                    throw new IllegalArgumentException(
                            "13/14/15차 Segment의 일간이 현재 원국과 일치하지 않습니다."
                    );
                }

                return MonthlyLuckContextResolution
                        .withDecade(
                                monthlyTenGodAnalysis
                                        .period(),

                                decadeInteraction
                                        .decadePeriod(),

                                monthlyLuckContextSignalAnalyzer
                                        .analyzeWithDecade(
                                                palaceAnalysis,

                                                annualLuckAnalysis,

                                                monthlyTenGodAnalysis,

                                                natalRelationAnalysis,

                                                annualRelationAnalysis,

                                                decadeInteraction,

                                                decadeContext,

                                                annualDecadeContext
                                        )
                        );
            }
        };
    }
}