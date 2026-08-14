package com.dajeom.analyzer;

import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckDecadeContextAnalysis;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.MonthlyAnnualLuckRelationAnalysis;
import com.dajeom.model.MonthlyDecadeLuckInteraction;
import com.dajeom.model.MonthlyDecadeLuckInteractionAnalysis;
import com.dajeom.model.MonthlyLuckContextAnalysis;
import com.dajeom.model.MonthlyLuckContextResolution;
import com.dajeom.model.MonthlyLuckDecadeContextAnalysis;
import com.dajeom.model.MonthlyLuckNatalRelationAnalysis;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.MonthlyLuckTenGodAnalysis;
import com.dajeom.resolver.MonthlyLuckContextResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 15차-7.
 *
 * 월운 교차 Context Analyzer.
 *
 * =================================================
 *
 * 월운 공통 Context:
 *
 * 15-3 월운 십성
 * +
 * 15-4 원국 × 월운
 * +
 * 15-6 세운 × 월운
 * +
 * 14차 세운 분석
 * +
 * 원국 분석
 *
 * =================================================
 *
 * 대운 Segment Context:
 *
 * 15-5 대운 × 월운
 * +
 * 13-6 대운 Context
 * +
 * 14-6 세운 Segment Context
 * +
 * 다층 관계
 *
 * =================================================
 *
 * 대운이 불확실해도 월운 자체에서
 * 확정 가능한 monthlySignals는 보존한다.
 */
@Component
public class MonthlyLuckContextAnalyzer {

    /**
     * 공식 15-7 API.
     */
    public MonthlyLuckContextAnalysis analyze(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            MonthlyDecadeLuckInteractionAnalysis
                    decadeInteractionAnalysis,

            MonthlyAnnualLuckRelationAnalysis
                    annualRelationAnalysis,

            MonthlyLuckContextResolver contextResolver

    ) {

        Objects.requireNonNull(
                annualLuckAnalysis,
                "15-7에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyTenGodAnalysis,
                "15-7에는 MonthlyLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "15-7에는 MonthlyLuckNatalRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeInteractionAnalysis,
                "15-7에는 MonthlyDecadeLuckInteractionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualRelationAnalysis,
                "15-7에는 MonthlyAnnualLuckRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                contextResolver,
                "MonthlyLuckContextResolver가 필요합니다."
        );

        validateInputs(
                annualLuckAnalysis,
                monthlyTenGodAnalysis,
                natalRelationAnalysis,
                decadeInteractionAnalysis,
                annualRelationAnalysis
        );

        MonthlyLuckPeriod monthlyPeriod =
                monthlyTenGodAnalysis
                        .period();

        /*
         * =================================================
         * 월운 공통 Context
         * =================================================
         */

        MonthlyLuckContextResolution monthlyResolution =
                Objects.requireNonNull(
                        contextResolver.resolveMonthly(
                                annualLuckAnalysis,
                                monthlyTenGodAnalysis,
                                natalRelationAnalysis,
                                annualRelationAnalysis
                        ),
                        "MonthlyLuckContextResolver.resolveMonthly()이 null을 반환했습니다."
                );

        validateMonthlyResolution(
                monthlyPeriod,
                monthlyResolution
        );

        /*
         * =================================================
         * 대운 Context 사용 불가
         * =================================================
         */

        if (
                !decadeInteractionAnalysis
                        .analysisAvailable()
        ) {

            return new MonthlyLuckContextAnalysis(
                    annualLuckAnalysis.dayMaster(),
                    annualLuckAnalysis,
                    monthlyTenGodAnalysis,
                    natalRelationAnalysis,
                    decadeInteractionAnalysis,
                    annualRelationAnalysis,
                    monthlyResolution.signals(),
                    List.of(),
                    false,
                    resolveUnavailableReason(
                            annualLuckAnalysis,
                            decadeInteractionAnalysis
                    )
            );
        }

        /*
         * 같은 DecadeLuckAnalysis를 기반으로 하므로
         * 14차도 대운 Context를 사용할 수 있어야 한다.
         */

        if (
                !annualLuckAnalysis
                        .decadeContextAvailable()
        ) {

            throw new IllegalStateException(
                    "15-5 대운 분석은 사용 가능하지만 14-6 세운 대운 Context는 사용할 수 없습니다."
            );
        }

        List<MonthlyLuckDecadeContextAnalysis>
                decadeContexts =
                new ArrayList<>();

        for (
                MonthlyDecadeLuckInteraction interaction
                : decadeInteractionAnalysis
                .interactions()
        ) {

            int sequenceNumber =
                    interaction
                            .decadeSequenceNumber();

            /*
             * =================================================
             * 14-6에서 동일 대운 Segment를 찾는다.
             * =================================================
             *
             * 월운은 세운 내부에 있으므로
             * 월운에 해당 대운 Interaction이 있다면
             * 세운에도 같은 대운 Segment가 존재해야 한다.
             */

            AnnualLuckDecadeContextAnalysis annualDecadeContext =
                    annualLuckAnalysis
                            .contextOfDecade(
                                    sequenceNumber
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "14-6 세운 Context에서 대응하는 대운 Segment를 찾을 수 없습니다: "
                                                            + sequenceNumber
                                            )
                            );

            DecadeLuckPeriodContextAnalysis decadeContext =
                    annualDecadeContext
                            .decadeContext();

            if (
                    decadeContext.period()
                            != interaction.decadePeriod()
            ) {

                throw new IllegalStateException(
                        "13-6 대운 Context와 15-5 월운 Interaction의 대운 Period가 일치하지 않습니다."
                );
            }

            /*
             * =================================================
             * 대운 Segment 전용 월운 Context
             * =================================================
             */

            MonthlyLuckContextResolution segmentResolution =
                    Objects.requireNonNull(
                            contextResolver
                                    .resolveWithDecade(
                                            annualLuckAnalysis,
                                            monthlyTenGodAnalysis,
                                            natalRelationAnalysis,
                                            annualRelationAnalysis,
                                            interaction,
                                            decadeContext,
                                            annualDecadeContext
                                    ),
                            "MonthlyLuckContextResolver.resolveWithDecade()가 null을 반환했습니다."
                    );

            validateSegmentResolution(
                    monthlyPeriod,
                    interaction,
                    segmentResolution
            );

            decadeContexts.add(
                    new MonthlyLuckDecadeContextAnalysis(
                            annualLuckAnalysis.dayMaster(),
                            monthlyPeriod,
                            interaction,
                            decadeContext,
                            annualDecadeContext,
                            monthlyResolution.signals(),
                            segmentResolution.signals()
                    )
            );
        }

        decadeContexts.sort(
                Comparator
                        .comparing(
                                (
                                        MonthlyLuckDecadeContextAnalysis context
                                ) ->
                                        context
                                                .overlap()
                                                .possibleStartInclusive()
                        )
                        .thenComparingInt(
                                MonthlyLuckDecadeContextAnalysis
                                        ::decadeSequenceNumber
                        )
        );

        return new MonthlyLuckContextAnalysis(
                annualLuckAnalysis.dayMaster(),
                annualLuckAnalysis,
                monthlyTenGodAnalysis,
                natalRelationAnalysis,
                decadeInteractionAnalysis,
                annualRelationAnalysis,
                monthlyResolution.signals(),
                decadeContexts,
                true,
                ""
        );
    }

    /*
     * =================================================
     * 입력 검증
     * =================================================
     */

    private void validateInputs(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            MonthlyDecadeLuckInteractionAnalysis
                    decadeInteractionAnalysis,

            MonthlyAnnualLuckRelationAnalysis
                    annualRelationAnalysis

    ) {

        MonthlyLuckPeriod monthlyPeriod =
                monthlyTenGodAnalysis
                        .period();

        /*
         * 월운 Identity
         */

        if (
                natalRelationAnalysis.period()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-3과 15-4가 서로 다른 MonthlyLuckPeriod를 참조합니다."
            );
        }

        if (
                decadeInteractionAnalysis.monthlyPeriod()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-5가 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                annualRelationAnalysis.monthlyPeriod()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-6이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        /*
         * 세운 Identity
         */

        if (
                monthlyPeriod.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "15차 월운이 현재 14차 AnnualLuckAnalysis에 속하지 않습니다."
            );
        }

        if (
                annualRelationAnalysis.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "15-6 세운-월운 관계가 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        /*
         * 일간
         */

        if (
                monthlyTenGodAnalysis.dayMaster()
                        != annualLuckAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "14차와 15-3의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.dayMaster()
                        != annualLuckAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "14차와 15-4의 일간이 일치하지 않습니다."
            );
        }

        /*
         * 원국
         */

        if (
                natalRelationAnalysis.natalAnalysis()
                        != annualLuckAnalysis.natalAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "14차와 15차가 서로 다른 PalaceAnalysis를 참조합니다."
            );
        }

        /*
         * 대운
         */

        if (
                decadeInteractionAnalysis.decadeLuckAnalysis()
                        != annualLuckAnalysis.decadeLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "14차와 15차가 서로 다른 DecadeLuckAnalysis를 참조합니다."
            );
        }

        /*
         * 동일 대운 분석을 기준으로 하므로
         * 사용 가능 상태도 동일해야 한다.
         */

        if (
                decadeInteractionAnalysis.analysisAvailable()
                        != annualLuckAnalysis.decadeContextAvailable()
        ) {

            throw new IllegalArgumentException(
                    "14차와 15차의 대운 Context 사용 가능 상태가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 월운 공통 Resolution
     * =================================================
     */

    private void validateMonthlyResolution(

            MonthlyLuckPeriod monthlyPeriod,

            MonthlyLuckContextResolution resolution

    ) {

        if (
                resolution.monthlyPeriod()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "월운 Context Resolver가 다른 MonthlyLuckPeriod를 반환했습니다."
            );
        }

        if (
                !resolution.monthlyOnly()
        ) {

            throw new IllegalArgumentException(
                    "resolveMonthly() 결과에는 DecadeLuckPeriod가 포함되면 안 됩니다."
            );
        }
    }

    /*
     * =================================================
     * 대운 Segment Resolution
     * =================================================
     */

    private void validateSegmentResolution(

            MonthlyLuckPeriod monthlyPeriod,

            MonthlyDecadeLuckInteraction interaction,

            MonthlyLuckContextResolution resolution

    ) {

        if (
                resolution.monthlyPeriod()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "월운 Segment Resolver가 다른 MonthlyLuckPeriod를 반환했습니다."
            );
        }

        if (
                resolution.decadePeriod()
                        .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "resolveWithDecade() 결과에는 DecadeLuckPeriod가 필요합니다."
            );
        }

        if (
                resolution
                        .decadePeriod()
                        .orElseThrow()
                        != interaction
                        .decadePeriod()
        ) {

            throw new IllegalArgumentException(
                    "월운 Segment Resolver가 현재 Interaction과 다른 대운을 반환했습니다."
            );
        }
    }

    private String resolveUnavailableReason(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyDecadeLuckInteractionAnalysis
                    decadeInteractionAnalysis

    ) {

        String reason =
                decadeInteractionAnalysis
                        .unavailableReason();

        if (
                reason != null
                        && !reason.isBlank()
        ) {

            return reason;
        }

        return annualLuckAnalysis
                .decadeContextUnavailableReason()
                .orElse(
                        "대운 Sequence가 확정되지 않아 대운을 포함한 월운 Context를 생성할 수 없습니다."
                );
    }
}