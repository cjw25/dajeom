package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 대운 Period에서
 * 5개 Function의 Natal Capacity Context를
 * 모두 보존하는 Aggregate.
 *
 * =================================================
 *
 * DecadePeriodFunctionalOperationAnalysis
 *
 * +
 *
 * NatalFunctionalAnalysis
 *
 * ↓
 *
 * 5개 DecadeFunctionCapacityContext
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 원국 CoreSelf는 모든 대운에서 동일하다.
 *
 * 대운마다:
 *
 * - 신강신약 재계산
 * - 지원력 재계산
 * - 압력 재계산
 *
 * 을 하지 않는다.
 */
public record DecadePeriodFunctionalCapacityAnalysis(

        NatalFunctionalAnalysis natalFunctionalAnalysis,

        DecadePeriodFunctionalOperationAnalysis
        operationAnalysis,

        List<DecadeFunctionCapacityContext>
        capacityContexts,

        boolean completeChart

) {

    public DecadePeriodFunctionalCapacityAnalysis {

        Objects.requireNonNull(
                natalFunctionalAnalysis,
                "Period Capacity Analysis에는 NatalFunctionalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                operationAnalysis,
                "Period Capacity Analysis에는 DecadePeriodFunctionalOperationAnalysis가 필요합니다."
        );

        capacityContexts =
                capacityContexts == null
                        ? List.of()
                        : List.copyOf(
                        capacityContexts
                );

        /*
         * =================================================
         * completeChart
         * =================================================
         */

        if (
                natalFunctionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "NatalFunctionalAnalysis와 Period Capacity의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                operationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Operation Analysis와 Period Capacity의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Function 5개
         * =================================================
         */

        if (
                capacityContexts.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "각 대운 Period에는 정확히 "
                            + NatalTenGodFunctionType.values().length
                            + "개의 Capacity Context가 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> seen =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                DecadeFunctionCapacityContext context
                : capacityContexts
        ) {

            Objects.requireNonNull(
                    context,
                    "DecadeFunctionCapacityContext는 null일 수 없습니다."
            );

            if (
                    context.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Capacity Context의 completeChart가 일치하지 않습니다."
                );
            }

            if (
                    context.coreSelf()
                            != natalFunctionalAnalysis.coreSelf()
            ) {

                throw new IllegalArgumentException(
                        "Capacity Context가 현재 원국의 CoreSelf를 참조하지 않습니다."
                );
            }

            NatalTenGodFamilyEffect expectedNatalFunction =
                    natalFunctionalAnalysis
                            .requireFunction(
                                    context.functionType()
                            );

            if (
                    context.natalFunctionEffect()
                            != expectedNatalFunction
            ) {

                throw new IllegalArgumentException(
                        "Capacity Context가 현재 원국의 Function Effect를 참조하지 않습니다. function="
                                + context.functionType()
                );
            }

            DecadeFunctionOperation expectedOperation =
                    operationAnalysis
                            .requireOperation(
                                    context.functionType()
                            );

            if (
                    context.operation()
                            != expectedOperation
            ) {

                throw new IllegalArgumentException(
                        "Capacity Context가 현재 대운 Period의 Function Operation을 참조하지 않습니다. function="
                                + context.functionType()
                );
            }

            if (
                    !seen.add(
                            context.functionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Capacity Function이 중복되었습니다: "
                                + context.functionType()
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
                    "대운 Capacity Context의 5개 기능축이 모두 보존되지 않았습니다."
            );
        }
    }

    public int sequenceNumber() {

        return operationAnalysis
                .sequenceNumber();
    }

    public DecadeLuckPeriod period() {

        return operationAnalysis
                .period();
    }

    public NatalCoreSelfEffect coreSelf() {

        return natalFunctionalAnalysis
                .coreSelf();
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    public Optional<DecadeFunctionCapacityContext>
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

    public DecadeFunctionCapacityContext
    requireContext(

            NatalTenGodFunctionType functionType

    ) {

        Objects.requireNonNull(
                functionType,
                "Capacity Context 조회에는 기능축이 필요합니다."
        );

        return contextOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "대운 Capacity Context를 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }

    /*
     * =================================================
     * 직접 입력
     * =================================================
     */

    public List<DecadeFunctionCapacityContext>
    directlyEnteredContexts() {

        return capacityContexts
                .stream()
                .filter(
                        DecadeFunctionCapacityContext
                                ::directlyEntered
                )
                .toList();
    }

    /*
     * =================================================
     * Capacity Review
     * =================================================
     */

    public List<DecadeFunctionCapacityContext>
    contextsRequiringCapacityReview() {

        return capacityContexts
                .stream()
                .filter(
                        DecadeFunctionCapacityContext
                                ::capacityReviewRequired
                )
                .toList();
    }

    public List<DecadeFunctionCapacityContext>
    sameDirectionAccumulationContexts() {

        return capacityContexts
                .stream()
                .filter(
                        DecadeFunctionCapacityContext
                                ::sameDirectionAccumulationReviewRequired
                )
                .toList();
    }

    public List<DecadeFunctionCapacityContext>
    counterDirectionContexts() {

        return capacityContexts
                .stream()
                .filter(
                        DecadeFunctionCapacityContext
                                ::counterDirectionContact
                )
                .toList();
    }

    public List<DecadeFunctionCapacityContext>
    qualifiedContexts() {

        return capacityContexts
                .stream()
                .filter(
                        DecadeFunctionCapacityContext
                                ::qualificationRequired
                )
                .toList();
    }
}