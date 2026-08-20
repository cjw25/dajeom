package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 전체 대운 Sequence의
 * 최종 Functional Synthesis Aggregate.
 *
 * =================================================
 *
 * 이 객체가 Annual Functional Layer의
 * 공식 대운 기준선이 된다.
 *
 * =================================================
 *
 * 포함:
 *
 * DecadeResolvedFunctionalAnalysis
 *
 * +
 *
 * DecadeCrossFunctionalAnalysis
 *
 * +
 *
 * Period별
 * DecadePeriodFunctionalSynthesisAnalysis
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 대운 전체를 하나의 점수나
 * 길흉 상태로 축약하지 않는다.
 *
 * 각 Period / Function / Cross Pattern의
 * 구조적 상태를 그대로 보존한다.
 */
public record DecadeFunctionalSynthesisAnalysis(

        DecadeResolvedFunctionalAnalysis
        resolvedFunctionalAnalysis,

        DecadeCrossFunctionalAnalysis
        crossFunctionalAnalysis,

        List<DecadePeriodFunctionalSynthesisAnalysis>
        periodAnalyses,

        boolean completeChart

) {

    public DecadeFunctionalSynthesisAnalysis {

        Objects.requireNonNull(
                resolvedFunctionalAnalysis,
                "DecadeFunctionalSynthesisAnalysis에는 Resolved Functional Analysis가 필요합니다."
        );

        Objects.requireNonNull(
                crossFunctionalAnalysis,
                "DecadeFunctionalSynthesisAnalysis에는 CrossFunctional Analysis가 필요합니다."
        );

        periodAnalyses =
                periodAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        periodAnalyses
                );

        if (
                crossFunctionalAnalysis
                        .resolvedFunctionalAnalysis()
                        != resolvedFunctionalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctional Analysis가 현재 Resolved Functional Analysis를 참조하지 않습니다."
            );
        }

        if (
                resolvedFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Resolved Functional Analysis와 Synthesis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                crossFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "CrossFunctional Analysis와 Synthesis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !crossFunctionalAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !periodAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "대운 CrossFunctional Analysis가 unavailable인데 "
                                + "Functional Synthesis Period가 존재합니다."
                );
            }

        } else {

            /*
             * =================================================
             * Period Coverage
             * =================================================
             */

            if (
                    periodAnalyses.size()
                            != crossFunctionalAnalysis
                            .periodAnalyses()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "CrossFunctional Period 수와 "
                                + "Functional Synthesis Period 수가 일치하지 않습니다."
                );
            }

            Set<Integer> sequences =
                    new HashSet<>();

            for (
                    DecadePeriodFunctionalSynthesisAnalysis period
                    : periodAnalyses
            ) {

                Objects.requireNonNull(
                        period,
                        "DecadePeriodFunctionalSynthesisAnalysis는 null일 수 없습니다."
                );

                if (
                        period.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Functional Synthesis Period의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !sequences.add(
                                period.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일 Functional Synthesis Period가 중복되었습니다: "
                                    + period.sequenceNumber()
                    );
                }

                DecadePeriodCrossFunctionalAnalysis expectedCross =
                        crossFunctionalAnalysis
                                .periodAnalysis(
                                        period.sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "원본 CrossFunctional Period를 찾을 수 없습니다: "
                                                                + period.sequenceNumber()
                                                )
                                );

                if (
                        period.crossFunctionalAnalysis()
                                != expectedCross
                ) {

                    throw new IllegalArgumentException(
                            "Functional Synthesis Period가 "
                                    + "CrossFunctional Period Identity를 보존하지 않았습니다: "
                                    + period.sequenceNumber()
                    );
                }

                DecadePeriodResolvedFunctionalAnalysis expectedResolved =
                        resolvedFunctionalAnalysis
                                .periodAnalysis(
                                        period.sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "원본 Resolved Functional Period를 찾을 수 없습니다: "
                                                                + period.sequenceNumber()
                                                )
                                );

                if (
                        period.resolvedFunctionalAnalysis()
                                != expectedResolved
                ) {

                    throw new IllegalArgumentException(
                            "Functional Synthesis Period가 "
                                    + "Resolved Functional Period Identity를 보존하지 않았습니다: "
                                    + period.sequenceNumber()
                    );
                }
            }

            for (
                    DecadePeriodCrossFunctionalAnalysis sourcePeriod
                    : crossFunctionalAnalysis.periodAnalyses()
            ) {

                if (
                        !sequences.contains(
                                sourcePeriod.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "Functional Synthesis Period가 누락되었습니다: "
                                    + sourcePeriod.sequenceNumber()
                    );
                }
            }
        }
    }

    public boolean analysisAvailable() {

        return crossFunctionalAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return crossFunctionalAnalysis
                .unavailableReason();
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    public Optional<DecadePeriodFunctionalSynthesisAnalysis>
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
                        period ->
                                period.sequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    public DecadePeriodFunctionalSynthesisAnalysis
    requirePeriodAnalysis(

            int sequenceNumber

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "대운 Functional Synthesis Period를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * Function across decades
     * =================================================
     */

    public List<DecadeResolvedFunctionEffect>
    functionEffectsOf(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        List<DecadeResolvedFunctionEffect> result =
                new ArrayList<>();

        for (
                DecadePeriodFunctionalSynthesisAnalysis period
                : periodAnalyses
        ) {

            period
                    .functionEffectOf(
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

    public List<DecadePeriodFunctionalSynthesisAnalysis>
    periodsWithActiveFunction(

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
                        period ->
                                period
                                        .requireFunctionEffect(
                                                functionType
                                        )
                                        .active()
                )
                .toList();
    }

    /*
     * =================================================
     * Cross Pattern across decades
     * =================================================
     */

    public List<DecadeCrossFunctionalSynthesis>
    synthesesOf(

            NatalCrossFunctionalPatternType patternType

    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        List<DecadeCrossFunctionalSynthesis> result =
                new ArrayList<>();

        for (
                DecadePeriodFunctionalSynthesisAnalysis period
                : periodAnalyses
        ) {

            period
                    .synthesisOf(
                            patternType
                    )
                    .ifPresent(
                            result::add
                    );
        }

        return List.copyOf(
                result
        );
    }

    public List<DecadePeriodFunctionalSynthesisAnalysis>
    periodsWithEffectiveFullContact(

            NatalCrossFunctionalPatternType patternType

    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return periodAnalyses
                .stream()
                .filter(
                        period ->
                                period
                                        .synthesisOf(
                                                patternType
                                        )
                                        .map(
                                                DecadeCrossFunctionalSynthesis
                                                        ::effectiveFullPatternContact
                                        )
                                        .orElse(
                                                false
                                        )
                )
                .toList();
    }

    public List<DecadePeriodFunctionalSynthesisAnalysis>
    periodsWithEffectiveFullContactAndExactContext(

            NatalCrossFunctionalPatternType patternType

    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        return periodAnalyses
                .stream()
                .filter(
                        period ->
                                period
                                        .synthesisOf(
                                                patternType
                                        )
                                        .map(
                                                DecadeCrossFunctionalSynthesis
                                                        ::effectiveFullContactWithExactContext
                                        )
                                        .orElse(
                                                false
                                        )
                )
                .toList();
    }
}