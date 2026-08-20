package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 대운 Period에 대한
 * 최종 구조적 Functional Effect Aggregate.
 *
 * =================================================
 *
 * DecadePeriodFunctionMigrationAnalysis
 *
 * ↓
 *
 * 5개 DecadeResolvedFunctionEffect
 *
 * =================================================
 *
 * 이 단계가 대운 Functional Layer의
 * 첫 번째 최종 수렴점이다.
 *
 * =================================================
 *
 * 각 Function에 대해:
 *
 * - 원래 Input 존재
 * - 원기능 보존/감소
 * - Transformation Target 유입
 * - Natal Fixed Capacity 접촉
 * - Natal Cross Pattern 접촉
 * - Function Context
 *
 * 를 함께 조회할 수 있다.
 *
 * =================================================
 *
 * 그러나 생활영역 길흉은
 * 아직 만들지 않는다.
 */
public record DecadePeriodResolvedFunctionalAnalysis(

        DecadePeriodFunctionMigrationAnalysis
        migrationAnalysis,

        List<DecadeResolvedFunctionEffect>
        functionEffects,

        boolean completeChart

) {

    public DecadePeriodResolvedFunctionalAnalysis {

        Objects.requireNonNull(
                migrationAnalysis,
                "Resolved Period Analysis에는 Function Migration Analysis가 필요합니다."
        );

        functionEffects =
                functionEffects == null
                        ? List.of()
                        : List.copyOf(
                        functionEffects
                );

        if (
                migrationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Resolved Period와 Migration Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 5 Function
         * =================================================
         */

        if (
                functionEffects.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "각 대운 Resolved Period에는 정확히 "
                            + NatalTenGodFunctionType.values().length
                            + "개의 Function Effect가 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> seen =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                DecadeResolvedFunctionEffect effect
                : functionEffects
        ) {

            Objects.requireNonNull(
                    effect,
                    "DecadeResolvedFunctionEffect는 null일 수 없습니다."
            );

            if (
                    effect.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Resolved Function Effect의 completeChart가 일치하지 않습니다."
                );
            }

            DecadeFunctionMigration expected =
                    migrationAnalysis
                            .requireMigration(
                                    effect.functionType()
                            );

            if (
                    effect.sourceMigration()
                            != expected
            ) {

                throw new IllegalArgumentException(
                        "Resolved Function Effect가 현재 Period의 "
                                + "Source Migration Identity를 보존하지 않았습니다. function="
                                + effect.functionType()
                );
            }

            if (
                    !seen.add(
                            effect.functionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Resolved Function Effect가 중복되었습니다: "
                                + effect.functionType()
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
                    "대운 Resolved Function 5개가 모두 보존되지 않았습니다."
            );
        }
    }

    public int sequenceNumber() {

        return migrationAnalysis
                .sequenceNumber();
    }

    public DecadeLuckPeriod period() {

        return migrationAnalysis
                .period();
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    public Optional<DecadeResolvedFunctionEffect>
    effectOf(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return functionEffects
                .stream()
                .filter(
                        effect ->
                                effect.functionType()
                                        == functionType
                )
                .findFirst();
    }

    public DecadeResolvedFunctionEffect
    requireEffect(

            NatalTenGodFunctionType functionType

    ) {

        Objects.requireNonNull(
                functionType,
                "Resolved Function 조회에는 Function Type이 필요합니다."
        );

        return effectOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Resolved Function Effect를 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }

    /*
     * =================================================
     * 활성 Function
     * =================================================
     */

    public List<DecadeResolvedFunctionEffect>
    activeEffects() {

        return functionEffects
                .stream()
                .filter(
                        DecadeResolvedFunctionEffect
                                ::active
                )
                .toList();
    }

    public List<DecadeResolvedFunctionEffect>
    originalInputEffects() {

        return functionEffects
                .stream()
                .filter(
                        DecadeResolvedFunctionEffect
                                ::originalInputActive
                )
                .toList();
    }

    public List<DecadeResolvedFunctionEffect>
    transformedTargetEffects() {

        return functionEffects
                .stream()
                .filter(
                        DecadeResolvedFunctionEffect
                                ::transformedTargetActive
                )
                .toList();
    }

    /*
     * =================================================
     * 원기능 감소
     * =================================================
     */

    public List<DecadeResolvedFunctionEffect>
    effectsWithOriginalInputReduction() {

        return functionEffects
                .stream()
                .filter(
                        DecadeResolvedFunctionEffect
                                ::originalInputReduced
                )
                .toList();
    }

    /*
     * =================================================
     * Capacity
     * =================================================
     */

    public List<DecadeResolvedFunctionEffect>
    effectsRequiringCapacityReview() {

        return functionEffects
                .stream()
                .filter(
                        DecadeResolvedFunctionEffect
                                ::capacityReviewRequired
                )
                .toList();
    }

    /*
     * =================================================
     * Natal Cross Pattern
     * =================================================
     */

    public List<DecadeResolvedFunctionEffect>
    effectsWithNatalCrossPatternContact() {

        return functionEffects
                .stream()
                .filter(
                        DecadeResolvedFunctionEffect
                                ::natalCrossPatternContact
                )
                .toList();
    }

    /*
     * =================================================
     * Qualification
     * =================================================
     */

    public List<DecadeResolvedFunctionEffect>
    qualifiedEffects() {

        return functionEffects
                .stream()
                .filter(
                        DecadeResolvedFunctionEffect
                                ::qualificationRequired
                )
                .toList();
    }
}