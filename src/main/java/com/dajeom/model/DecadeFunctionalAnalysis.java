package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 전체 대운 Sequence의
 * 기능효과 Projection Aggregate.
 *
 * =================================================
 *
 * NatalCrossFunctionalAnalysis
 *
 * +
 *
 * DecadeLuckAnalysis
 *
 * ↓
 *
 * 각 대운 Period별
 * DecadePeriodFunctionalAnalysis
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 원국 신강·신약을 대운마다
 * 새로 계산하지 않는다.
 *
 * 2.
 * 원국 Cross Pattern을
 * 새로 계산하지 않는다.
 *
 * 3.
 * 13차 Context를
 * 하나의 길흉 점수로 압축하지 않는다.
 *
 * 4.
 * 대운 십성이 어느 기능축으로
 * 실제 들어오는지만 구조적으로 투영한다.
 *
 * =================================================
 *
 * 다음 단계에서:
 *
 * - 원국의 자기 수용력
 * - 기존 Cross Pattern
 * - 대운 Transformation
 *
 * 을 이용하여
 * 실제 기능 작동변화를 판단한다.
 */
public record DecadeFunctionalAnalysis(

        NatalCrossFunctionalAnalysis
        natalCrossFunctionalAnalysis,

        DecadeLuckAnalysis
        decadeLuckAnalysis,

        List<DecadePeriodFunctionalAnalysis>
        periodAnalyses,

        boolean completeChart

) {

    public DecadeFunctionalAnalysis {

        Objects.requireNonNull(
                natalCrossFunctionalAnalysis,
                "DecadeFunctionalAnalysis에는 NatalCrossFunctionalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "DecadeFunctionalAnalysis에는 DecadeLuckAnalysis가 필요합니다."
        );

        periodAnalyses =
                periodAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        periodAnalyses
                );

        /*
         * =================================================
         * 원국 완전성
         * =================================================
         */

        if (
                decadeLuckAnalysis
                        .natalAnalysis()
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "DecadeFunctionalAnalysis의 completeChart가 "
                            + "대운 원국과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 분석 불가 상태
         * =================================================
         *
         * record compact constructor에서는
         * return을 사용할 수 없다.
         *
         * 따라서 unavailable 상태에서는
         * Period Analysis가 비어 있는지만 검증하고,
         *
         * available 상태에서만
         * 아래 Period 정합성 검증을 수행한다.
         */

        if (
                !decadeLuckAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !periodAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "13차 대운 분석이 unavailable인데 "
                                + "Functional Period Analysis가 존재합니다."
                );
            }

        } else {

            /*
             * =================================================
             * 모든 대운 Period 보존
             * =================================================
             */

            if (
                    periodAnalyses.size()
                            != decadeLuckAnalysis
                            .periods()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "대운 Period 수와 Functional Analysis 수가 일치하지 않습니다."
                );
            }

            /*
             * =================================================
             * Period 중복 방지
             * =================================================
             */

            Set<Integer> sequenceNumbers =
                    new HashSet<>();

            for (
                    DecadePeriodFunctionalAnalysis periodAnalysis
                    : periodAnalyses
            ) {

                Objects.requireNonNull(
                        periodAnalysis,
                        "Decade Period Functional Analysis는 null일 수 없습니다."
                );

                if (
                        periodAnalysis.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "대운 Functional Period의 completeChart가 일치하지 않습니다."
                    );
                }

                /*
                 * =================================================
                 * 동일 sequence 중복 방지
                 * =================================================
                 */

                if (
                        !sequenceNumbers.add(
                                periodAnalysis
                                        .sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일한 대운 Functional Period가 중복되었습니다: "
                                    + periodAnalysis.sequenceNumber()
                    );
                }

                /*
                 * =================================================
                 * 원본 Period 존재 확인
                 * =================================================
                 */

                DecadeLuckPeriod expectedPeriod =
                        decadeLuckAnalysis
                                .period(
                                        periodAnalysis
                                                .sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "해당 대운 순번이 원본 DecadeLuckAnalysis에 없습니다: "
                                                                + periodAnalysis.sequenceNumber()
                                                )
                                );

                /*
                 * =================================================
                 * Period Identity 보존
                 * =================================================
                 */

                if (
                        expectedPeriod
                                != periodAnalysis.period()
                ) {

                    throw new IllegalArgumentException(
                            "DecadeFunctionalAnalysis가 "
                                    + "원본 대운 Period Identity를 보존하지 않았습니다. sequence="
                                    + periodAnalysis.sequenceNumber()
                    );
                }

                /*
                 * =================================================
                 * 13-4 Identity
                 * =================================================
                 */

                DecadeLuckPeriodTenGodAnalysis expectedTenGodAnalysis =
                        decadeLuckAnalysis
                                .tenGodAnalysisOf(
                                        periodAnalysis
                                                .sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "해당 대운의 13-4 십성 분석이 없습니다: "
                                                                + periodAnalysis.sequenceNumber()
                                                )
                                );

                if (
                        expectedTenGodAnalysis
                                != periodAnalysis
                                .tenGodAnalysis()
                ) {

                    throw new IllegalArgumentException(
                            "DecadeFunctionalAnalysis가 "
                                    + "원본 13-4 대운 십성 분석 Identity를 보존하지 않았습니다. sequence="
                                    + periodAnalysis.sequenceNumber()
                    );
                }

                /*
                 * =================================================
                 * 13-5 Identity
                 * =================================================
                 */

                DecadeLuckPeriodNatalRelationAnalysis
                        expectedNatalRelationAnalysis =
                        decadeLuckAnalysis
                                .natalRelationAnalysisOf(
                                        periodAnalysis
                                                .sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "해당 대운의 13-5 원국 관계 분석이 없습니다: "
                                                                + periodAnalysis.sequenceNumber()
                                                )
                                );

                if (
                        expectedNatalRelationAnalysis
                                != periodAnalysis
                                .natalRelationAnalysis()
                ) {

                    throw new IllegalArgumentException(
                            "DecadeFunctionalAnalysis가 "
                                    + "원본 13-5 대운 관계 분석 Identity를 보존하지 않았습니다. sequence="
                                    + periodAnalysis.sequenceNumber()
                    );
                }

                /*
                 * =================================================
                 * 13-6 Identity
                 * =================================================
                 */

                DecadeLuckPeriodContextAnalysis expectedContextAnalysis =
                        decadeLuckAnalysis
                                .contextOf(
                                        periodAnalysis
                                                .sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "해당 대운의 13-6 Context 분석이 없습니다: "
                                                                + periodAnalysis.sequenceNumber()
                                                )
                                );

                if (
                        expectedContextAnalysis
                                != periodAnalysis
                                .contextAnalysis()
                ) {

                    throw new IllegalArgumentException(
                            "DecadeFunctionalAnalysis가 "
                                    + "원본 13-6 대운 Context Analysis Identity를 보존하지 않았습니다. sequence="
                                    + periodAnalysis.sequenceNumber()
                    );
                }
            }

            /*
             * =================================================
             * 원본 Sequence 전체 Coverage 검증
             * =================================================
             */

            for (
                    DecadeLuckPeriod period
                    : decadeLuckAnalysis.periods()
            ) {

                if (
                        !sequenceNumbers.contains(
                                period.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "원본 대운 Period의 Functional Analysis가 누락되었습니다: "
                                    + period.sequenceNumber()
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 상태
     * =================================================
     */

    public boolean analysisAvailable() {

        return decadeLuckAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return decadeLuckAnalysis
                .unavailableReason();
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    public Optional<DecadePeriodFunctionalAnalysis>
    periodAnalysis(

            int sequenceNumber

    ) {

        if (
                sequenceNumber < 1
        ) {

            return Optional.empty();
        }

        return periodAnalyses
                .stream()
                .filter(
                        analysis ->
                                analysis.sequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    public DecadePeriodFunctionalAnalysis
    requirePeriodAnalysis(

            int sequenceNumber

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "대운 Functional Analysis를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    public List<DecadeFunctionEffect>
    effectsOf(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        List<DecadeFunctionEffect> result =
                new ArrayList<>();

        for (
                DecadePeriodFunctionalAnalysis periodAnalysis
                : periodAnalyses
        ) {

            periodAnalysis
                    .effectOf(
                            functionType
                    )
                    .ifPresent(
                            result::add
                    );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 해당 기능이 실제 대운 십성으로
     * 직접 들어오는 Period만 조회.
     */
    public List<DecadePeriodFunctionalAnalysis>
    periodsWithDirectInput(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return periodAnalyses
                .stream()
                .filter(
                        periodAnalysis ->
                                periodAnalysis
                                        .requireEffect(
                                                functionType
                                        )
                                        .directlyEntered()
                )
                .toList();
    }

    /*
     * =================================================
     * Transformation
     * =================================================
     */

    public List<DecadePeriodFunctionalAnalysis>
    periodsWithActivatedTransformation() {

        return periodAnalyses
                .stream()
                .filter(
                        DecadePeriodFunctionalAnalysis
                                ::activatedTransformationExists
                )
                .toList();
    }

    public List<DecadePeriodFunctionalAnalysis>
    periodsWithOriginalFunctionReduction() {

        return periodAnalyses
                .stream()
                .filter(
                        DecadePeriodFunctionalAnalysis
                                ::originalFunctionReductionExists
                )
                .toList();
    }

    /*
     * =================================================
     * 복합 Context
     * =================================================
     */

    public List<DecadePeriodFunctionalAnalysis>
    periodsWithMixedGlobalContext() {

        return periodAnalyses
                .stream()
                .filter(
                        DecadePeriodFunctionalAnalysis
                                ::mixedGlobalContext
                )
                .toList();
    }
}