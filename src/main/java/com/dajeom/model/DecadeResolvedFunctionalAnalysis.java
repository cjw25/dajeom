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
 * 최종 구조적 Functional Effect Aggregate.
 *
 * =================================================
 *
 * DecadeFunctionalMigrationAnalysis
 *
 * ↓
 *
 * DecadePeriodResolvedFunctionalAnalysis
 *
 * =================================================
 *
 * 이후:
 *
 * AnnualFunctionalEffect
 * MonthlyFunctionalEffect
 * LifeDomain Luck
 *
 * 의 대운 기준선으로 사용할 수 있다.
 *
 * =================================================
 *
 * 중요:
 *
 * 대운을 하나의:
 *
 * GOOD
 * BAD
 * +80
 * -40
 *
 * 같은 값으로 만들지 않는다.
 *
 * 각 Function의 작동 상태를
 * 독립적으로 보존한다.
 */
public record DecadeResolvedFunctionalAnalysis(

        DecadeFunctionalMigrationAnalysis
        migrationAnalysis,

        List<DecadePeriodResolvedFunctionalAnalysis>
        periodAnalyses,

        boolean completeChart

) {

    public DecadeResolvedFunctionalAnalysis {

        Objects.requireNonNull(
                migrationAnalysis,
                "DecadeResolvedFunctionalAnalysis에는 Migration Analysis가 필요합니다."
        );

        periodAnalyses =
                periodAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        periodAnalyses
                );

        if (
                migrationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Resolved Functional Analysis와 Migration Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !migrationAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !periodAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Migration Analysis가 unavailable인데 "
                                + "Resolved Period가 존재합니다."
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
                            != migrationAnalysis
                            .periodAnalyses()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Migration Period 수와 Resolved Period 수가 일치하지 않습니다."
                );
            }

            Set<Integer> sequenceNumbers =
                    new HashSet<>();

            for (
                    DecadePeriodResolvedFunctionalAnalysis periodAnalysis
                    : periodAnalyses
            ) {

                Objects.requireNonNull(
                        periodAnalysis,
                        "Resolved Functional Period는 null일 수 없습니다."
                );

                if (
                        periodAnalysis.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Resolved Functional Period의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !sequenceNumbers.add(
                                periodAnalysis.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일 Resolved Functional Period가 중복되었습니다: "
                                    + periodAnalysis.sequenceNumber()
                    );
                }

                DecadePeriodFunctionMigrationAnalysis expected =
                        migrationAnalysis
                                .periodAnalysis(
                                        periodAnalysis
                                                .sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "원본 Migration Period를 찾을 수 없습니다: "
                                                                + periodAnalysis.sequenceNumber()
                                                )
                                );

                if (
                        periodAnalysis.migrationAnalysis()
                                != expected
                ) {

                    throw new IllegalArgumentException(
                            "Resolved Period가 원본 Migration Period Identity를 보존하지 않았습니다: "
                                    + periodAnalysis.sequenceNumber()
                    );
                }
            }

            for (
                    DecadePeriodFunctionMigrationAnalysis sourcePeriod
                    : migrationAnalysis.periodAnalyses()
            ) {

                if (
                        !sequenceNumbers.contains(
                                sourcePeriod.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "Resolved Functional Period가 누락되었습니다: "
                                    + sourcePeriod.sequenceNumber()
                    );
                }
            }
        }
    }

    public boolean analysisAvailable() {

        return migrationAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return migrationAnalysis
                .unavailableReason();
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    public Optional<DecadePeriodResolvedFunctionalAnalysis>
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

    public DecadePeriodResolvedFunctionalAnalysis
    requirePeriodAnalysis(

            int sequenceNumber

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Resolved Functional Period를 찾을 수 없습니다: "
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
    effectsOf(

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
                DecadePeriodResolvedFunctionalAnalysis period
                : periodAnalyses
        ) {

            period
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

    public List<DecadePeriodResolvedFunctionalAnalysis>
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
                                        .requireEffect(
                                                functionType
                                        )
                                        .active()
                )
                .toList();
    }

    public List<DecadePeriodResolvedFunctionalAnalysis>
    periodsWithTransformedTarget(

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
                                        .requireEffect(
                                                functionType
                                        )
                                        .transformedTargetActive()
                )
                .toList();
    }

    public List<DecadePeriodResolvedFunctionalAnalysis>
    periodsWithNatalCrossPatternContact(

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
                                        .requireEffect(
                                                functionType
                                        )
                                        .natalCrossPatternContact()
                )
                .toList();
    }
}