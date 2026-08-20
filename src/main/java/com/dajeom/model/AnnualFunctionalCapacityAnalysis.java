package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 세운에 대한
 * Transformation Migration
 * +
 * Natal Fixed Capacity
 * 최종 Aggregate.
 *
 * =================================================
 *
 * 입력:
 *
 * DayMasterElementRelationAnalysis
 *
 * +
 *
 * AnnualFunctionalProjectionAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * 5개 AnnualFunctionMigration
 *
 * +
 *
 * 5개 AnnualFunctionCapacityContext
 *
 * =================================================
 *
 * 이 단계까지는 오직:
 *
 * Natal × Annual
 *
 * 을 다룬다.
 *
 * =================================================
 *
 * 아직:
 *
 * - 14-5 대운 × 세운
 * - 교운 Segment
 * - DecadeFunctionalSynthesis
 *
 * 는 사용하지 않는다.
 */
public record AnnualFunctionalCapacityAnalysis(

        DayMasterElementRelationAnalysis
        elementRelationAnalysis,

        AnnualFunctionalProjectionAnalysis
        projectionAnalysis,

        List<AnnualFunctionMigration>
        functionMigrations,

        List<AnnualFunctionCapacityContext>
        capacityContexts,

        boolean completeChart

) {

    public AnnualFunctionalCapacityAnalysis {

        Objects.requireNonNull(
                elementRelationAnalysis,
                "Annual Functional Capacity에는 DayMasterElementRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                projectionAnalysis,
                "Annual Functional Capacity에는 AnnualFunctionalProjectionAnalysis가 필요합니다."
        );

        functionMigrations =
                functionMigrations == null
                        ? List.of()
                        : List.copyOf(
                        functionMigrations
                );

        capacityContexts =
                capacityContexts == null
                        ? List.of()
                        : List.copyOf(
                        capacityContexts
                );

        NatalFunctionalAnalysis natal =
                projectionAnalysis
                        .natalFunctionalAnalysis();

        /*
         * =================================================
         * 동일 일간
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
                    "Natal Functional Analysis와 Annual Functional Capacity의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                projectionAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Projection과 Annual Functional Capacity의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 정확히 5 Migration
         * =================================================
         */

        if (
                functionMigrations.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Capacity에는 정확히 5개의 Function Migration이 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> migrationFunctions =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                AnnualFunctionMigration migration
                : functionMigrations
        ) {

            Objects.requireNonNull(
                    migration,
                    "AnnualFunctionMigration은 null일 수 없습니다."
            );

            if (
                    migration.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual Function Migration의 completeChart가 일치하지 않습니다."
                );
            }

            AnnualFunctionEffect expected =
                    projectionAnalysis
                            .requireEffect(
                                    migration.sourceFunctionType()
                            );

            if (
                    migration.sourceEffect()
                            != expected
            ) {

                throw new IllegalArgumentException(
                        "Annual Function Migration이 "
                                + "Projection Source Effect Identity를 보존하지 않았습니다. function="
                                + migration.sourceFunctionType()
                );
            }

            if (
                    !migrationFunctions.add(
                            migration.sourceFunctionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Annual Function Migration이 중복되었습니다: "
                                + migration.sourceFunctionType()
                );
            }
        }

        /*
         * =================================================
         * 정확히 5 Capacity Context
         * =================================================
         */

        if (
                capacityContexts.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "Annual Functional Capacity에는 정확히 5개의 Capacity Context가 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> capacityFunctions =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                AnnualFunctionCapacityContext context
                : capacityContexts
        ) {

            Objects.requireNonNull(
                    context,
                    "AnnualFunctionCapacityContext는 null일 수 없습니다."
            );

            if (
                    context.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual Capacity Context의 completeChart가 일치하지 않습니다."
                );
            }

            AnnualFunctionMigration expectedMigration =
                    functionMigrations
                            .stream()
                            .filter(
                                    migration ->
                                            migration.sourceFunctionType()
                                                    == context.functionType()
                            )
                            .findFirst()
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "Capacity Context의 Source Migration을 찾을 수 없습니다: "
                                                            + context.functionType()
                                            )
                            );

            if (
                    context.sourceMigration()
                            != expectedMigration
            ) {

                throw new IllegalArgumentException(
                        "Annual Capacity Context가 "
                                + "현재 Function Migration Identity를 보존하지 않았습니다. function="
                                + context.functionType()
                );
            }

            if (
                    !capacityFunctions.add(
                            context.functionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Annual Capacity Context가 중복되었습니다: "
                                + context.functionType()
                );
            }
        }

        Set<NatalTenGodFunctionType> expectedFunctions =
                EnumSet.allOf(
                        NatalTenGodFunctionType.class
                );

        if (
                !migrationFunctions.equals(
                        expectedFunctions
                )
        ) {

            throw new IllegalArgumentException(
                    "Annual Function Migration 5개 Coverage가 완전하지 않습니다."
            );
        }

        if (
                !capacityFunctions.equals(
                        expectedFunctions
                )
        ) {

            throw new IllegalArgumentException(
                    "Annual Capacity Context 5개 Coverage가 완전하지 않습니다."
            );
        }
    }

    public int targetYear() {

        return projectionAnalysis
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return projectionAnalysis
                .period();
    }

    public NatalCoreSelfEffect coreSelf() {

        return projectionAnalysis
                .natalFunctionalAnalysis()
                .coreSelf();
    }

    /*
     * =================================================
     * Migration
     * =================================================
     */

    public Optional<AnnualFunctionMigration>
    migrationOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return functionMigrations
                .stream()
                .filter(
                        migration ->
                                migration.sourceFunctionType()
                                        == functionType
                )
                .findFirst();
    }

    public AnnualFunctionMigration requireMigration(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "Annual Function Migration 조회에는 Function Type이 필요합니다."
        );

        return migrationOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual Function Migration을 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }

    /*
     * =================================================
     * Capacity
     * =================================================
     */

    public Optional<AnnualFunctionCapacityContext>
    contextOf(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return capacityContexts
                .stream()
                .filter(
                        context ->
                                context.functionType()
                                        == functionType
                )
                .findFirst();
    }

    public AnnualFunctionCapacityContext requireContext(
            NatalTenGodFunctionType functionType
    ) {

        Objects.requireNonNull(
                functionType,
                "Annual Capacity Context 조회에는 Function Type이 필요합니다."
        );

        return contextOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Annual Function Capacity Context를 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }

    public List<AnnualFunctionCapacityContext>
    activeContexts() {

        return capacityContexts
                .stream()
                .filter(
                        AnnualFunctionCapacityContext
                                ::active
                )
                .toList();
    }

    public List<AnnualFunctionCapacityContext>
    transformedTargetContexts() {

        return capacityContexts
                .stream()
                .filter(
                        AnnualFunctionCapacityContext
                                ::transformedTargetActive
                )
                .toList();
    }

    public List<AnnualFunctionCapacityContext>
    contextsRequiringCapacityReview() {

        return capacityContexts
                .stream()
                .filter(
                        AnnualFunctionCapacityContext
                                ::capacityReviewRequired
                )
                .toList();
    }

    /*
     * =================================================
     * Transformation 중복 제거
     * =================================================
     */

    public List<AnnualLuckNatalTransformationReference>
    distinctActiveTransformations() {

        Set<AnnualLuckNatalTransformationReference> result =
                new LinkedHashSet<>();

        for (
                AnnualFunctionMigration migration
                : functionMigrations
        ) {

            for (
                    AnnualFunctionMigrationTarget target
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
}