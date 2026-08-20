package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 전체 대운 Sequence의
 * CrossFunctional Contact Aggregate.
 *
 * =================================================
 *
 * NatalCrossFunctionalAnalysis
 *
 * +
 *
 * DecadeResolvedFunctionalAnalysis
 *
 * ↓
 *
 * Period별 CrossFunctional Analysis
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Aggregate는
 * 원국 Pattern을 대운마다 다시 계산하지 않는다.
 *
 * 원국 Pattern은 고정.
 *
 * 대운은:
 *
 * "어느 Participant를
 * 실제로 건드렸는가"
 *
 * 만 추가한다.
 */
public record DecadeCrossFunctionalAnalysis(

        NatalCrossFunctionalAnalysis
        natalCrossFunctionalAnalysis,

        DecadeResolvedFunctionalAnalysis
        resolvedFunctionalAnalysis,

        List<DecadePeriodCrossFunctionalAnalysis>
        periodAnalyses,

        boolean completeChart

) {

    public DecadeCrossFunctionalAnalysis {

        Objects.requireNonNull(
                natalCrossFunctionalAnalysis,
                "DecadeCrossFunctionalAnalysis에는 NatalCrossFunctionalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                resolvedFunctionalAnalysis,
                "DecadeCrossFunctionalAnalysis에는 DecadeResolvedFunctionalAnalysis가 필요합니다."
        );

        periodAnalyses =
                periodAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        periodAnalyses
                );

        /*
         * =================================================
         * Canonical Natal Identity
         * =================================================
         */

        NatalCrossFunctionalAnalysis expectedNatal =
                resolvedFunctionalAnalysis
                        .migrationAnalysis()
                        .capacityAnalysis()
                        .operationAnalysis()
                        .functionalAnalysis()
                        .natalCrossFunctionalAnalysis();

        if (
                expectedNatal
                        != natalCrossFunctionalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "Resolved Decade Functional Analysis와 "
                            + "NatalCrossFunctionalAnalysis의 원국 Identity가 일치하지 않습니다."
            );
        }

        if (
                natalCrossFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Natal CrossFunctional Analysis와 "
                            + "Decade Cross Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                resolvedFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Resolved Functional Analysis와 "
                            + "Decade Cross Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !resolvedFunctionalAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !periodAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Resolved Decade Analysis가 unavailable인데 "
                                + "CrossFunctional Period가 존재합니다."
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
                            != resolvedFunctionalAnalysis
                            .periodAnalyses()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Resolved Period 수와 CrossFunctional Period 수가 일치하지 않습니다."
                );
            }

            Set<Integer> sequences =
                    new HashSet<>();

            for (
                    DecadePeriodCrossFunctionalAnalysis period
                    : periodAnalyses
            ) {

                Objects.requireNonNull(
                        period,
                        "DecadePeriodCrossFunctionalAnalysis는 null일 수 없습니다."
                );

                if (
                        period.natalCrossFunctionalAnalysis()
                                != natalCrossFunctionalAnalysis
                ) {

                    throw new IllegalArgumentException(
                            "CrossFunctional Period가 현재 원국 Cross Analysis를 참조하지 않습니다."
                    );
                }

                if (
                        period.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "CrossFunctional Period의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !sequences.add(
                                period.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일 CrossFunctional Period가 중복되었습니다: "
                                    + period.sequenceNumber()
                    );
                }

                DecadePeriodResolvedFunctionalAnalysis expectedPeriod =
                        resolvedFunctionalAnalysis
                                .periodAnalysis(
                                        period.sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "원본 Resolved Period를 찾을 수 없습니다: "
                                                                + period.sequenceNumber()
                                                )
                                );

                if (
                        period.resolvedFunctionalAnalysis()
                                != expectedPeriod
                ) {

                    throw new IllegalArgumentException(
                            "CrossFunctional Period가 "
                                    + "Resolved Period Identity를 보존하지 않았습니다: "
                                    + period.sequenceNumber()
                    );
                }
            }

            for (
                    DecadePeriodResolvedFunctionalAnalysis sourcePeriod
                    : resolvedFunctionalAnalysis.periodAnalyses()
            ) {

                if (
                        !sequences.contains(
                                sourcePeriod.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "CrossFunctional Period가 누락되었습니다: "
                                    + sourcePeriod.sequenceNumber()
                    );
                }
            }
        }
    }

    public boolean analysisAvailable() {

        return resolvedFunctionalAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return resolvedFunctionalAnalysis
                .unavailableReason();
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    public Optional<DecadePeriodCrossFunctionalAnalysis>
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

    public DecadePeriodCrossFunctionalAnalysis
    requirePeriodAnalysis(

            int sequenceNumber

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Decade CrossFunctional Period를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * Pattern across decades
     * =================================================
     */

    public List<DecadeCrossFunctionalEffect>
    effectsOf(

            NatalCrossFunctionalPatternType patternType

    ) {

        if (
                patternType == null
        ) {

            return List.of();
        }

        List<DecadeCrossFunctionalEffect> result =
                new ArrayList<>();

        for (
                DecadePeriodCrossFunctionalAnalysis period
                : periodAnalyses
        ) {

            period
                    .effectOf(
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

    public List<DecadePeriodCrossFunctionalAnalysis>
    periodsWithFullContact(

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
                                        .effectOf(
                                                patternType
                                        )
                                        .map(
                                                DecadeCrossFunctionalEffect
                                                        ::fullPatternContact
                                        )
                                        .orElse(
                                                false
                                        )
                )
                .toList();
    }

    public List<DecadePeriodCrossFunctionalAnalysis>
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
                                        .effectOf(
                                                patternType
                                        )
                                        .map(
                                                DecadeCrossFunctionalEffect
                                                        ::effectiveFullPatternContact
                                        )
                                        .orElse(
                                                false
                                        )
                )
                .toList();
    }
}