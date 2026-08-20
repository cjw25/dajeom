package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 대운 Period에 대한
 * Function Migration 전체 분석.
 *
 * =================================================
 *
 * 5개 Source Function 각각에 대해
 * DecadeFunctionMigration을
 * 정확히 하나씩 보존한다.
 *
 * =================================================
 *
 * 중요한 점:
 *
 * 하나의 EARTHLY_BRANCH Transformation은
 * 대운 지지의 여러 지장간 Function에
 * 동시에 연결될 수 있다.
 *
 * 따라서 같은 Transformation Reference가
 * 서로 다른 Source Function Migration에
 * 나타날 수 있다.
 *
 * 이것을:
 *
 * "독립적인 Transformation 여러 개"
 *
 * 로 세면 안 된다.
 *
 * 그래서 Period Aggregate에서
 * distinctActiveTransformations()
 *
 * 를 별도로 제공한다.
 */
public record DecadePeriodFunctionMigrationAnalysis(

        DecadePeriodFunctionalCapacityAnalysis
        capacityAnalysis,

        List<DecadeFunctionMigration>
        functionMigrations,

        boolean completeChart

) {

    public DecadePeriodFunctionMigrationAnalysis {

        Objects.requireNonNull(
                capacityAnalysis,
                "Period Function Migration에는 Capacity Analysis가 필요합니다."
        );

        functionMigrations =
                functionMigrations == null
                        ? List.of()
                        : List.copyOf(
                        functionMigrations
                );

        if (
                capacityAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Period Function Migration과 Capacity Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Function 5개
         * =================================================
         */

        if (
                functionMigrations.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "각 대운 Period에는 정확히 "
                            + NatalTenGodFunctionType.values().length
                            + "개의 Function Migration이 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> seen =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                DecadeFunctionMigration migration
                : functionMigrations
        ) {

            Objects.requireNonNull(
                    migration,
                    "DecadeFunctionMigration은 null일 수 없습니다."
            );

            if (
                    migration.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Function Migration의 completeChart가 일치하지 않습니다."
                );
            }

            DecadeFunctionCapacityContext expected =
                    capacityAnalysis
                            .requireContext(
                                    migration
                                            .sourceFunctionType()
                            );

            if (
                    migration.capacityContext()
                            != expected
            ) {

                throw new IllegalArgumentException(
                        "Function Migration이 현재 Period의 "
                                + "Capacity Context Identity를 보존하지 않았습니다. function="
                                + migration.sourceFunctionType()
                );
            }

            if (
                    !seen.add(
                            migration.sourceFunctionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Source Function Migration이 중복되었습니다: "
                                + migration.sourceFunctionType()
                );
            }
        }

        Set<NatalTenGodFunctionType> expected =
                EnumSet.allOf(
                        NatalTenGodFunctionType.class
                );

        if (
                !seen.equals(
                        expected
                )
        ) {

            throw new IllegalArgumentException(
                    "대운 Function Migration 5개가 모두 보존되지 않았습니다."
            );
        }
    }

    public int sequenceNumber() {

        return capacityAnalysis
                .sequenceNumber();
    }

    public DecadeLuckPeriod period() {

        return capacityAnalysis
                .period();
    }

    /*
     * =================================================
     * Source Function
     * =================================================
     */

    public Optional<DecadeFunctionMigration>
    migrationOf(

            NatalTenGodFunctionType sourceFunctionType

    ) {

        if (
                sourceFunctionType == null
        ) {

            return Optional.empty();
        }

        return functionMigrations
                .stream()
                .filter(
                        migration ->
                                migration
                                        .sourceFunctionType()
                                        == sourceFunctionType
                )
                .findFirst();
    }

    public DecadeFunctionMigration
    requireMigration(

            NatalTenGodFunctionType sourceFunctionType

    ) {

        Objects.requireNonNull(
                sourceFunctionType,
                "Source Function Type은 필수입니다."
        );

        return migrationOf(
                sourceFunctionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "대운 Function Migration을 찾을 수 없습니다: "
                                                + sourceFunctionType
                                )
                );
    }

    /*
     * =================================================
     * Cross Function
     * =================================================
     */

    public List<DecadeFunctionMigration>
    crossFunctionMigrations() {

        return functionMigrations
                .stream()
                .filter(
                        DecadeFunctionMigration
                                ::crossFunctionMigrationExists
                )
                .toList();
    }

    public boolean hasCrossFunctionMigration() {

        return !crossFunctionMigrations()
                .isEmpty();
    }

    /*
     * =================================================
     * Target Function 기준
     * =================================================
     */

    public List<DecadeFunctionMigration>
    migrationsTargeting(

            NatalTenGodFunctionType targetFunctionType

    ) {

        if (
                targetFunctionType == null
        ) {

            return List.of();
        }

        return functionMigrations
                .stream()
                .filter(
                        migration ->
                                migration.targets(
                                        targetFunctionType
                                )
                )
                .toList();
    }

    public List<NatalTenGodFunctionType>
    activatedTargetFunctions() {

        Set<NatalTenGodFunctionType> result =
                new LinkedHashSet<>();

        for (
                DecadeFunctionMigration migration
                : functionMigrations
        ) {

            result.addAll(
                    migration.targetFunctionTypes()
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Transformation 중복 제거
     * =================================================
     *
     * 같은 Branch Transformation이
     * 여러 지장간 Function에 연결되어도
     * Transformation 자체는 하나다.
     */

    public List<DecadeLuckNatalTransformationReference>
    distinctActiveTransformations() {

        Set<DecadeLuckNatalTransformationReference> result =
                new LinkedHashSet<>();

        for (
                DecadeFunctionMigration migration
                : functionMigrations
        ) {

            for (
                    DecadeFunctionMigrationTarget target
                    : migration.migrationTargets()
            ) {

                result.add(
                        target.sourceTransformation()
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    public List<DecadeLuckNatalTransformationReference>
    distinctTransformationsTargeting(

            NatalTenGodFunctionType targetFunctionType

    ) {

        if (
                targetFunctionType == null
        ) {

            return List.of();
        }

        Set<DecadeLuckNatalTransformationReference> result =
                new LinkedHashSet<>();

        for (
                DecadeFunctionMigration migration
                : functionMigrations
        ) {

            for (
                    DecadeFunctionMigrationTarget target
                    : migration.targetsTo(
                    targetFunctionType
            )
            ) {

                result.add(
                        target.sourceTransformation()
                );
            }
        }

        return List.copyOf(
                result
        );
    }
}