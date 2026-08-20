package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 세운 Source Function에 대한
 * Transformation Migration 결과.
 *
 * =================================================
 *
 * Source:
 *
 * AnnualFunctionEffect
 *
 * =================================================
 *
 * Targets:
 *
 * 0개 이상의
 * AnnualFunctionMigrationTarget
 *
 * =================================================
 *
 * Target이 없을 수 있는 경우:
 *
 * - Transformation 없음
 * - Transformation Context는 있으나 비활성
 *
 * =================================================
 *
 * 중요:
 *
 * Source Function은 절대로 삭제하지 않는다.
 *
 * annualOriginalFunctionState가 감소 상태여도
 * Source Evidence 자체는 계속 보존한다.
 */
public record AnnualFunctionMigration(

        AnnualFunctionEffect sourceEffect,

        List<AnnualFunctionMigrationTarget>
        migrationTargets,

        boolean completeChart

) {

    public AnnualFunctionMigration {

        Objects.requireNonNull(
                sourceEffect,
                "Annual Function Migration에는 Source Effect가 필요합니다."
        );

        /*
         * =================================================
         * Compact Constructor Local
         * =================================================
         *
         * record compact constructor에서는
         * canonical field assignment가 constructor body
         * 실행 이후에 이루어진다.
         *
         * 따라서 constructor 내부 검증에서는
         *
         * sourceFunctionType()
         *
         * 처럼 this.sourceEffect를 참조하는
         * instance helper를 호출하지 않는다.
         *
         * constructor parameter인 sourceEffect에서
         * 필요한 값을 먼저 고정해서 사용한다.
         */

        NatalTenGodFunctionType sourceFunctionType =
                Objects.requireNonNull(
                        sourceEffect.functionType(),
                        "Annual Function Migration의 Source Function Type이 필요합니다."
                );

        migrationTargets =
                migrationTargets == null
                        ? List.of()
                        : List.copyOf(
                        migrationTargets
                );

        if (
                sourceEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Function Migration과 Source Effect의 completeChart가 일치하지 않습니다."
            );
        }

        Set<AnnualLuckNatalTransformationReference>
                seenTransformations =
                new HashSet<>();

        for (
                AnnualFunctionMigrationTarget target
                : migrationTargets
        ) {

            Objects.requireNonNull(
                    target,
                    "AnnualFunctionMigrationTarget은 null일 수 없습니다."
            );

            if (
                    !seenTransformations.add(
                            target.sourceTransformation()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Transformation이 같은 Annual Source Function에서 중복되었습니다. code="
                                + target
                                .sourceTransformation()
                                .transformationCode()
                );
            }

            if (
                    !sourceEffect
                            .distinctTransformationContexts()
                            .contains(
                                    target.sourceTransformation()
                            )
            ) {

                throw new IllegalArgumentException(
                        "현재 Annual Source Function에 존재하지 않는 Transformation이 "
                                + "Migration Target으로 연결되었습니다."
                );
            }

            for (
                    AnnualFunctionInputOperation input
                    : target.sourceInputOperations()
            ) {

                if (
                        !sourceEffect
                                .inputOperations()
                                .contains(
                                        input
                                )
                ) {

                    throw new IllegalArgumentException(
                            "Migration Target의 Source Input이 "
                                    + "현재 Annual Function Effect에 없습니다."
                    );
                }

                if (
                        !sourceFunctionType
                                .contains(
                                        input.tenGodType()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "Migration Source Input이 현재 Source Function Family에 속하지 않습니다."
                    );
                }
            }
        }
    }

    public int targetYear() {

        return sourceEffect
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return sourceEffect
                .period();
    }

    public NatalTenGodFunctionType sourceFunctionType() {

        return sourceEffect
                .functionType();
    }

    /*
     * =================================================
     * Direct Source
     * =================================================
     */

    public boolean directlyEntered() {

        return sourceEffect
                .directlyEntered();
    }

    /*
     * =================================================
     * Migration
     * =================================================
     */

    public boolean hasActiveTransformationTarget() {

        return !migrationTargets
                .isEmpty();
    }

    public List<AnnualFunctionMigrationTarget>
    sameFunctionTargets() {

        return migrationTargets
                .stream()
                .filter(
                        target ->
                                target.targetFunctionType()
                                        == sourceFunctionType()
                )
                .toList();
    }

    public List<AnnualFunctionMigrationTarget>
    crossFunctionTargets() {

        return migrationTargets
                .stream()
                .filter(
                        target ->
                                target.targetFunctionType()
                                        != sourceFunctionType()
                )
                .toList();
    }

    public boolean sameFunctionTargetExists() {

        return !sameFunctionTargets()
                .isEmpty();
    }

    public boolean crossFunctionMigrationExists() {

        return !crossFunctionTargets()
                .isEmpty();
    }

    public List<NatalTenGodFunctionType>
    targetFunctionTypes() {

        Set<NatalTenGodFunctionType> result =
                new LinkedHashSet<>();

        for (
                AnnualFunctionMigrationTarget target
                : migrationTargets
        ) {

            result.add(
                    target.targetFunctionType()
            );
        }

        return List.copyOf(
                result
        );
    }

    public List<AnnualFunctionMigrationTarget>
    targetsTo(
            NatalTenGodFunctionType functionType
    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return migrationTargets
                .stream()
                .filter(
                        target ->
                                target.targetFunctionType()
                                        == functionType
                )
                .toList();
    }

    public boolean targets(
            NatalTenGodFunctionType functionType
    ) {

        return !targetsTo(
                functionType
        )
                .isEmpty();
    }

    public boolean strongCrossFunctionMigrationExists() {

        return crossFunctionTargets()
                .stream()
                .anyMatch(
                        AnnualFunctionMigrationTarget
                                ::strongTargetActivation
                );
    }

    /*
     * =================================================
     * Annual Original Function
     * =================================================
     */

    public boolean annualOriginalFunctionReductionExists() {

        return sourceEffect
                .annualOriginalFunctionReductionExists();
    }

    public boolean annualOriginalFunctionStrongReductionExists() {

        return sourceEffect
                .annualOriginalFunctionStrongReductionExists();
    }

    public boolean mixedAnnualOriginalFunctionCondition() {

        return sourceEffect
                .mixedAnnualOriginalFunctionCondition();
    }

    public boolean qualificationRequired() {

        return annualOriginalFunctionReductionExists()

                || mixedAnnualOriginalFunctionCondition()

                || migrationTargets.size() >= 2;
    }
}