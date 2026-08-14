package com.dajeom.analyzer;

import com.dajeom.model.AnnualDecadeLuckInteraction;
import com.dajeom.model.AnnualDecadeLuckInteractionAnalysis;
import com.dajeom.model.AnnualLuckContextAnalysis;
import com.dajeom.model.AnnualLuckContextResolution;
import com.dajeom.model.AnnualLuckDecadeContextAnalysis;
import com.dajeom.model.AnnualLuckNatalRelationAnalysis;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.AnnualLuckTenGodAnalysis;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.resolver.AnnualLuckContextResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 14차-6.
 *
 * 세운 교차 Context Analyzer.
 *
 * =================================================
 *
 * 1.
 * 세운 공통 Context 생성.
 *
 * 14-3
 * +
 * 14-4
 * +
 * 5/7/10/12차
 *
 * =================================================
 *
 * 2.
 * 대운별 Segment Context 생성.
 *
 * 14-5
 * +
 * 13-6
 * +
 * 원국/대운/세운 3층 관계
 *
 * =================================================
 *
 * 중요한 정책:
 *
 * 대운 Sequence가 불확실해도
 * 세운 자체에서 확정 가능한 annualSignals는
 * 버리지 않는다.
 */
@Component
public class AnnualLuckContextAnalyzer {

    /**
     * =================================================
     * 공식 14-6 API
     * =================================================
     */
    public AnnualLuckContextAnalysis analyze(

            AnnualLuckTenGodAnalysis
                    annualTenGodAnalysis,

            AnnualLuckNatalRelationAnalysis
                    natalRelationAnalysis,

            AnnualDecadeLuckInteractionAnalysis
                    decadeInteractionAnalysis,

            AnnualLuckContextResolver
                    contextResolver

    ) {

        Objects.requireNonNull(
                annualTenGodAnalysis,
                "14-3 AnnualLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "14-4 AnnualLuckNatalRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeInteractionAnalysis,
                "14-5 AnnualDecadeLuckInteractionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                contextResolver,
                "AnnualLuckContextResolver가 필요합니다."
        );

        validateInputs(
                annualTenGodAnalysis,
                natalRelationAnalysis,
                decadeInteractionAnalysis
        );

        AnnualLuckPeriod annualPeriod =
                annualTenGodAnalysis
                        .period();

        /*
         * =================================================
         * 세운 공통 Context
         * =================================================
         */

        AnnualLuckContextResolution annualResolution =
                Objects.requireNonNull(
                        contextResolver.resolveAnnual(
                                annualTenGodAnalysis,
                                natalRelationAnalysis
                        ),
                        "AnnualLuckContextResolver.resolveAnnual()이 null을 반환했습니다."
                );

        validateAnnualResolution(
                annualPeriod,
                annualResolution
        );

        /*
         * =================================================
         * 대운 Sequence 불확실
         * =================================================
         *
         * 세운 공통 Context는 그대로 반환한다.
         */

        if (
                !decadeInteractionAnalysis
                        .analysisAvailable()
        ) {

            String reason =
                    decadeInteractionAnalysis
                            .unavailableReason();

            if (
                    reason == null
                            || reason.isBlank()
            ) {

                reason =
                        "대운 Sequence가 확정되지 않아 대운을 포함한 세운 Context를 생성할 수 없습니다.";
            }

            return new AnnualLuckContextAnalysis(
                    annualTenGodAnalysis.dayMaster(),
                    annualTenGodAnalysis,
                    natalRelationAnalysis,
                    decadeInteractionAnalysis,
                    annualResolution.signals(),
                    List.of(),
                    false,
                    reason
            );
        }

        DecadeLuckAnalysis decadeLuckAnalysis =
                decadeInteractionAnalysis
                        .decadeLuckAnalysis();

        /*
         * 13차 설계상 Sequence가 사용 가능하면
         * 13-6 Context도 사용 가능해야 한다.
         */

        if (
                !decadeLuckAnalysis
                        .analysisAvailable()
        ) {

            throw new IllegalStateException(
                    "14-5 대운 Sequence는 사용 가능하지만 13-6 대운 Context를 사용할 수 없습니다."
            );
        }

        List<AnnualLuckDecadeContextAnalysis>
                decadeContexts =
                new ArrayList<>();

        for (
                AnnualDecadeLuckInteraction interaction
                : decadeInteractionAnalysis
                .interactions()
        ) {

            int sequenceNumber =
                    interaction
                            .decadeSequenceNumber();

            DecadeLuckPeriodContextAnalysis decadeContext =
                    decadeLuckAnalysis
                            .contextOf(
                                    sequenceNumber
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "13-6 대운 Context를 찾을 수 없습니다: "
                                                            + sequenceNumber
                                            )
                            );

            /*
             * =================================================
             * 특정 대운 × 세운 Segment Context
             * =================================================
             */

            AnnualLuckContextResolution segmentResolution =
                    Objects.requireNonNull(
                            contextResolver
                                    .resolveWithDecade(
                                            annualTenGodAnalysis,
                                            natalRelationAnalysis,
                                            interaction,
                                            decadeContext
                                    ),
                            "AnnualLuckContextResolver.resolveWithDecade()가 null을 반환했습니다."
                    );

            validateSegmentResolution(
                    annualPeriod,
                    interaction,
                    segmentResolution
            );

            decadeContexts.add(
                    new AnnualLuckDecadeContextAnalysis(
                            annualTenGodAnalysis.dayMaster(),
                            annualPeriod,
                            interaction,
                            decadeContext,
                            annualResolution.signals(),
                            segmentResolution.signals()
                    )
            );
        }

        /*
         * 가능한 시작 시점 기준 정렬.
         */

        decadeContexts.sort(
                Comparator
                        .comparing(
                                (
                                        AnnualLuckDecadeContextAnalysis context
                                ) ->
                                        context
                                                .overlap()
                                                .possibleStartInclusive()
                        )
                        .thenComparingInt(
                                AnnualLuckDecadeContextAnalysis
                                        ::decadeSequenceNumber
                        )
        );

        return new AnnualLuckContextAnalysis(
                annualTenGodAnalysis.dayMaster(),
                annualTenGodAnalysis,
                natalRelationAnalysis,
                decadeInteractionAnalysis,
                annualResolution.signals(),
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

            AnnualLuckTenGodAnalysis
                    annualTenGodAnalysis,

            AnnualLuckNatalRelationAnalysis
                    natalRelationAnalysis,

            AnnualDecadeLuckInteractionAnalysis
                    decadeInteractionAnalysis

    ) {

        if (
                annualTenGodAnalysis.dayMaster()
                        != natalRelationAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "14-3과 14-4의 일간이 일치하지 않습니다."
            );
        }

        AnnualLuckPeriod annualPeriod =
                annualTenGodAnalysis
                        .period();

        if (
                natalRelationAnalysis.period()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "14-3과 14-4가 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        if (
                decadeInteractionAnalysis
                        .annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "14-5가 현재 14-2 AnnualLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                decadeInteractionAnalysis
                        .decadeLuckAnalysis()
                        .dayMaster()
                        != annualTenGodAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "13차 대운 분석과 14차 세운 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                decadeInteractionAnalysis
                        .decadeLuckAnalysis()
                        .natalAnalysis()
                        != natalRelationAnalysis
                        .natalAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "13차 대운 분석과 14차 세운 분석이 서로 다른 원국 PalaceAnalysis를 참조합니다."
            );
        }
    }

    /*
     * =================================================
     * Annual Resolution 검증
     * =================================================
     */

    private void validateAnnualResolution(

            AnnualLuckPeriod annualPeriod,

            AnnualLuckContextResolution resolution

    ) {

        if (
                resolution.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "Annual Context Resolver가 다른 AnnualLuckPeriod를 반환했습니다."
            );
        }

        if (
                !resolution.annualOnly()
        ) {

            throw new IllegalArgumentException(
                    "resolveAnnual() 결과에는 DecadeLuckPeriod가 포함되면 안 됩니다."
            );
        }
    }

    /*
     * =================================================
     * Segment Resolution 검증
     * =================================================
     */

    private void validateSegmentResolution(

            AnnualLuckPeriod annualPeriod,

            AnnualDecadeLuckInteraction interaction,

            AnnualLuckContextResolution resolution

    ) {

        if (
                resolution.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "대운 Segment Resolver가 다른 AnnualLuckPeriod를 반환했습니다."
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
                    "대운 Segment Resolver가 현재 Interaction과 다른 대운을 반환했습니다."
            );
        }
    }
}