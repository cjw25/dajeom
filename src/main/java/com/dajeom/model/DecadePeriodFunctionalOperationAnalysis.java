package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 대운 Period에 대한
 * Function Operation Aggregate.
 *
 * =================================================
 *
 * DecadePeriodFunctionalAnalysis
 *
 * ↓
 *
 * 5개 DecadeFunctionOperation
 *
 * =================================================
 *
 * 목적:
 *
 * Projection 단계에서 확인한
 * "어떤 기능이 들어왔는가"
 *
 * 를
 *
 * "그 Input이 관계와 Transformation 안에서
 * 어떤 Context를 갖는가"
 *
 * 까지 확장한다.
 *
 * =================================================
 *
 * 아직 하지 않는 것:
 *
 * - 기능 강약 최종판정
 * - 원국 수용력 적용
 * - 길흉
 * - 생활영역 판정
 * - 실제 사건 판정
 */
public record DecadePeriodFunctionalOperationAnalysis(

        DecadePeriodFunctionalAnalysis
        functionalAnalysis,

        List<DecadeFunctionOperation>
        functionOperations,

        boolean completeChart

) {

    public DecadePeriodFunctionalOperationAnalysis {

        Objects.requireNonNull(
                functionalAnalysis,
                "DecadePeriodFunctionalOperationAnalysis에는 Functional Analysis가 필요합니다."
        );

        functionOperations =
                functionOperations == null
                        ? List.of()
                        : List.copyOf(
                        functionOperations
                );

        if (
                functionalAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Period Functional Operation과 Functional Analysis의 "
                            + "completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Function 5개 보존
         * =================================================
         */

        if (
                functionOperations.size()
                        != NatalTenGodFunctionType
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "각 대운 Period에는 정확히 "
                            + NatalTenGodFunctionType.values().length
                            + "개의 Function Operation이 필요합니다."
            );
        }

        Set<NatalTenGodFunctionType> actualFunctions =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                DecadeFunctionOperation operation
                : functionOperations
        ) {

            Objects.requireNonNull(
                    operation,
                    "DecadeFunctionOperation은 null일 수 없습니다."
            );

            if (
                    operation.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Function Operation의 completeChart가 일치하지 않습니다."
                );
            }

            if (
                    operation.sourceEffect()
                            != functionalAnalysis
                            .requireEffect(
                                    operation.functionType()
                            )
            ) {

                throw new IllegalArgumentException(
                        "Function Operation이 현재 Period의 "
                                + "원본 DecadeFunctionEffect를 참조하지 않습니다. function="
                                + operation.functionType()
                );
            }

            if (
                    !actualFunctions.add(
                            operation.functionType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Function Operation이 중복되었습니다: "
                                + operation.functionType()
                );
            }
        }

        Set<NatalTenGodFunctionType> expectedFunctions =
                EnumSet.allOf(
                        NatalTenGodFunctionType.class
                );

        if (
                !actualFunctions.equals(
                        expectedFunctions
                )
        ) {

            throw new IllegalArgumentException(
                    "대운 Function Operation 5개가 모두 보존되지 않았습니다."
            );
        }
    }

    public int sequenceNumber() {

        return functionalAnalysis
                .sequenceNumber();
    }

    public DecadeLuckPeriod period() {

        return functionalAnalysis
                .period();
    }

    public Optional<DecadeFunctionOperation>
    operationOf(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return Optional.empty();
        }

        return functionOperations
                .stream()
                .filter(
                        operation ->
                                operation.functionType()
                                        == functionType
                )
                .findFirst();
    }

    public DecadeFunctionOperation
    requireOperation(

            NatalTenGodFunctionType functionType

    ) {

        Objects.requireNonNull(
                functionType,
                "NatalTenGodFunctionType은 필수입니다."
        );

        return operationOf(
                functionType
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "대운 Function Operation을 찾을 수 없습니다: "
                                                + functionType
                                )
                );
    }

    /*
     * =================================================
     * 직접 유입
     * =================================================
     */

    public List<DecadeFunctionOperation>
    directlyEnteredOperations() {

        return functionOperations
                .stream()
                .filter(
                        DecadeFunctionOperation
                                ::directlyEntered
                )
                .toList();
    }

    /*
     * =================================================
     * Transformation
     * =================================================
     */

    public List<DecadeFunctionOperation>
    operationsWithOriginalFunctionReduction() {

        return functionOperations
                .stream()
                .filter(
                        DecadeFunctionOperation
                                ::originalFunctionReductionExists
                )
                .toList();
    }

    public List<DecadeFunctionOperation>
    operationsWithStrongOriginalFunctionReduction() {

        return functionOperations
                .stream()
                .filter(
                        DecadeFunctionOperation
                                ::strongOriginalFunctionReductionExists
                )
                .toList();
    }

    public List<DecadeFunctionOperation>
    operationsWithActiveTransformation() {

        return functionOperations
                .stream()
                .filter(
                        DecadeFunctionOperation
                                ::activeTransformationExists
                )
                .toList();
    }

    /*
     * =================================================
     * Restriction Review
     * =================================================
     */

    public List<DecadeFunctionOperation>
    operationsRequiringStructuralRestrictionReview() {

        return functionOperations
                .stream()
                .filter(
                        DecadeFunctionOperation
                                ::structuralRestrictionReviewRequired
                )
                .toList();
    }

    /*
     * =================================================
     * Period Global Context
     * =================================================
     */

    public List<DecadeLuckContextSignal>
    globalContextSignals() {

        return functionalAnalysis
                .globalContextSignals();
    }

    public boolean mixedGlobalContext() {

        return functionalAnalysis
                .mixedGlobalContext();
    }
}