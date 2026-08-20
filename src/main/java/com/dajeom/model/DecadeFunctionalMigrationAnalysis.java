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
 * Function Migration Aggregate.
 *
 * =================================================
 *
 * DayMasterElementRelationAnalysis
 *
 * +
 *
 * DecadeFunctionalCapacityAnalysis
 *
 * ↓
 *
 * Period별 Function Migration
 *
 * =================================================
 *
 * DayMasterElementRelationAnalysis를
 * 직접 보존하는 이유:
 *
 * 합화 targetElement를
 * 어떤 Function으로 해석했는지
 * canonical provenance를 유지하기 위해서다.
 *
 * =================================================
 *
 * FiveElement 상생상극을
 * 이 계층에서 다시 계산하지 않는다.
 */
public record DecadeFunctionalMigrationAnalysis(

        DayMasterElementRelationAnalysis
        elementRelationAnalysis,

        DecadeFunctionalCapacityAnalysis
        capacityAnalysis,

        List<DecadePeriodFunctionMigrationAnalysis>
        periodAnalyses,

        boolean completeChart

) {

    public DecadeFunctionalMigrationAnalysis {

        Objects.requireNonNull(
                elementRelationAnalysis,
                "Function Migration Analysis에는 DayMasterElementRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                capacityAnalysis,
                "Function Migration Analysis에는 DecadeFunctionalCapacityAnalysis가 필요합니다."
        );

        periodAnalyses =
                periodAnalyses == null
                        ? List.of()
                        : List.copyOf(
                        periodAnalyses
                );

        NatalFunctionalAnalysis natal =
                capacityAnalysis
                        .natalFunctionalAnalysis();

        /*
         * =================================================
         * 동일 원국
         * =================================================
         */

        if (
                elementRelationAnalysis.dayMaster()
                        != natal.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "DayMasterElementRelationAnalysis와 NatalFunctionalAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                elementRelationAnalysis.dayMasterElement()
                        != natal
                        .coreSelf()
                        .dayMasterElement()
        ) {

            throw new IllegalArgumentException(
                    "DayMasterElementRelationAnalysis와 Natal CoreSelf의 일간 오행이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * completeChart
         * =================================================
         */

        if (
                natal.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "NatalFunctionalAnalysis와 Function Migration Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                capacityAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Capacity Analysis와 Function Migration Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !capacityAnalysis
                        .analysisAvailable()
        ) {

            if (
                    !periodAnalyses
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Capacity Analysis가 unavailable인데 "
                                + "Function Migration Period가 존재합니다."
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
                            != capacityAnalysis
                            .periodAnalyses()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "Capacity Period 수와 Migration Period 수가 일치하지 않습니다."
                );
            }

            Set<Integer> sequenceNumbers =
                    new HashSet<>();

            for (
                    DecadePeriodFunctionMigrationAnalysis periodAnalysis
                    : periodAnalyses
            ) {

                Objects.requireNonNull(
                        periodAnalysis,
                        "Function Migration Period는 null일 수 없습니다."
                );

                if (
                        periodAnalysis.completeChart()
                                != completeChart
                ) {

                    throw new IllegalArgumentException(
                            "Function Migration Period의 completeChart가 일치하지 않습니다."
                    );
                }

                if (
                        !sequenceNumbers.add(
                                periodAnalysis
                                        .sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일 Function Migration Period가 중복되었습니다: "
                                    + periodAnalysis.sequenceNumber()
                    );
                }

                DecadePeriodFunctionalCapacityAnalysis expected =
                        capacityAnalysis
                                .periodAnalysis(
                                        periodAnalysis
                                                .sequenceNumber()
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "원본 Capacity Period를 찾을 수 없습니다: "
                                                                + periodAnalysis.sequenceNumber()
                                                )
                                );

                if (
                        periodAnalysis.capacityAnalysis()
                                != expected
                ) {

                    throw new IllegalArgumentException(
                            "Function Migration Period가 "
                                    + "원본 Capacity Period Identity를 보존하지 않았습니다: "
                                    + periodAnalysis.sequenceNumber()
                    );
                }
            }

            for (
                    DecadePeriodFunctionalCapacityAnalysis sourcePeriod
                    : capacityAnalysis.periodAnalyses()
            ) {

                if (
                        !sequenceNumbers.contains(
                                sourcePeriod.sequenceNumber()
                        )
                ) {

                    throw new IllegalArgumentException(
                            "Function Migration Period가 누락되었습니다: "
                                    + sourcePeriod.sequenceNumber()
                    );
                }
            }
        }
    }

    public boolean analysisAvailable() {

        return capacityAnalysis
                .analysisAvailable();
    }

    public Optional<String> unavailableReason() {

        return capacityAnalysis
                .unavailableReason();
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    public Optional<DecadePeriodFunctionMigrationAnalysis>
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

    public DecadePeriodFunctionMigrationAnalysis
    requirePeriodAnalysis(

            int sequenceNumber

    ) {

        return periodAnalysis(
                sequenceNumber
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Function Migration Period를 찾을 수 없습니다: "
                                                + sequenceNumber
                                )
                );
    }

    /*
     * =================================================
     * Source Function across decades
     * =================================================
     */

    public List<DecadeFunctionMigration>
    migrationsFrom(

            NatalTenGodFunctionType sourceFunctionType

    ) {

        if (
                sourceFunctionType == null
        ) {

            return List.of();
        }

        List<DecadeFunctionMigration> result =
                new ArrayList<>();

        for (
                DecadePeriodFunctionMigrationAnalysis period
                : periodAnalyses
        ) {

            period
                    .migrationOf(
                            sourceFunctionType
                    )
                    .ifPresent(
                            result::add
                    );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Target Function across decades
     * =================================================
     */

    public List<DecadePeriodFunctionMigrationAnalysis>
    periodsTargeting(

            NatalTenGodFunctionType targetFunctionType

    ) {

        if (
                targetFunctionType == null
        ) {

            return List.of();
        }

        return periodAnalyses
                .stream()
                .filter(
                        period ->
                                !period
                                        .migrationsTargeting(
                                                targetFunctionType
                                        )
                                        .isEmpty()
                )
                .toList();
    }

    public List<DecadePeriodFunctionMigrationAnalysis>
    periodsWithCrossFunctionMigration() {

        return periodAnalyses
                .stream()
                .filter(
                        DecadePeriodFunctionMigrationAnalysis
                                ::hasCrossFunctionMigration
                )
                .toList();
    }
}