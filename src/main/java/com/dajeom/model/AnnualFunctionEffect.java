package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.StructureTenGodType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 세운에서
 * 하나의 Functional Family로 들어오는
 * 직접 Input과 14-4 작동 Context.
 *
 * =================================================
 *
 * 예:
 *
 * EXPRESSION
 *
 * 안에:
 *
 * - 세운 천간 식신
 * - 세운 지지 본기 상관
 *
 * 등이 동시에 들어올 수 있다.
 *
 * =================================================
 *
 * 이 단계에서는:
 *
 * - Function이 직접 들어왔는지
 * - 천간/지장간 어디에서 들어왔는지
 * - 원국 관계가 있는지
 * - 합화가 있는지
 * - 세운 원기능 감소 Context가 있는지
 *
 * 만 보존한다.
 *
 * =================================================
 *
 * 아직 하지 않는 것:
 *
 * - 합화 목표 Function Migration
 * - Natal Fixed Capacity 최종 접촉
 * - 대운 Functional 기준선 결합
 * - 14-5 대운 × 세운 Interaction
 * - CrossFunctional Pattern 최종 접촉
 * - 길흉
 */
public record AnnualFunctionEffect(

        AnnualLuckAnalysis annualLuckAnalysis,

        NatalTenGodFamilyEffect natalFunctionEffect,

        NatalTenGodFunctionType functionType,

        List<AnnualFunctionInputOperation> inputOperations,

        boolean completeChart

) {

    public AnnualFunctionEffect {

        Objects.requireNonNull(
                annualLuckAnalysis,
                "AnnualFunctionEffect에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalFunctionEffect,
                "AnnualFunctionEffect에는 Natal Function Effect가 필요합니다."
        );

        Objects.requireNonNull(
                functionType,
                "AnnualFunctionEffect에는 Function Type이 필요합니다."
        );

        inputOperations =
                inputOperations == null
                        ? List.of()
                        : List.copyOf(
                        inputOperations
                );

        if (
                natalFunctionEffect.functionType()
                        != functionType
        ) {

            throw new IllegalArgumentException(
                    "Natal Function Effect와 Annual Function Type이 일치하지 않습니다."
            );
        }

        if (
                natalFunctionEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Natal Function Effect와 Annual Function Effect의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                annualLuckAnalysis
                        .natalAnalysis()
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "AnnualLuckAnalysis 원국과 Annual Function Effect의 completeChart가 일치하지 않습니다."
            );
        }

        Set<AnnualLuckTenGodEvidence> seenEvidences =
                new LinkedHashSet<>();

        for (
                AnnualFunctionInputOperation operation
                : inputOperations
        ) {

            Objects.requireNonNull(
                    operation,
                    "AnnualFunctionInputOperation은 null일 수 없습니다."
            );

            if (
                    operation.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Annual Function Input Operation의 completeChart가 일치하지 않습니다."
                );
            }

            AnnualLuckTenGodEvidence evidence =
                    operation
                            .sourceEvidence();

            if (
                    evidence.period()
                            != annualLuckAnalysis.period()
            ) {

                throw new IllegalArgumentException(
                        "다른 AnnualLuckPeriod의 Input Operation이 Function에 연결되었습니다."
                );
            }

            if (
                    !annualLuckAnalysis
                            .tenGodAnalysis()
                            .evidences()
                            .contains(
                                    evidence
                            )
            ) {

                throw new IllegalArgumentException(
                        "AnnualLuckAnalysis에 존재하지 않는 TenGod Evidence가 Function에 연결되었습니다."
                );
            }

            if (
                    !functionType
                            .contains(
                                    evidence.tenGodType()
                            )
            ) {

                throw new IllegalArgumentException(
                        "Annual TenGod Evidence가 현재 Function Family에 속하지 않습니다. "
                                + "function="
                                + functionType
                                + ", tenGod="
                                + evidence.tenGodType()
                );
            }

            if (
                    !seenEvidences.add(
                            evidence
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Annual TenGod Evidence가 Function에 중복 연결되었습니다: "
                                + evidence.sourceType()
                );
            }
        }
    }

    public int targetYear() {

        return annualLuckAnalysis
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return annualLuckAnalysis
                .period();
    }

    /*
     * =================================================
     * Direct Input
     * =================================================
     */

    public boolean directlyEntered() {

        return !inputOperations
                .isEmpty();
    }

    public List<AnnualLuckTenGodEvidence>
    directTenGodEvidences() {

        return inputOperations
                .stream()
                .map(
                        AnnualFunctionInputOperation
                                ::sourceEvidence
                )
                .toList();
    }

    public List<StructureTenGodType>
    directTenGodTypes() {

        Set<StructureTenGodType> result =
                new LinkedHashSet<>();

        for (
                AnnualLuckTenGodEvidence evidence
                : directTenGodEvidences()
        ) {

            result.add(
                    evidence.tenGodType()
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Visible / Hidden
     * =================================================
     */

    public boolean visibleInputExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        AnnualFunctionInputOperation
                                ::visible
                );
    }

    public boolean hiddenInputExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        AnnualFunctionInputOperation
                                ::hidden
                );
    }

    public List<AnnualFunctionInputOperation>
    visibleInputOperations() {

        return inputOperations
                .stream()
                .filter(
                        AnnualFunctionInputOperation
                                ::visible
                )
                .toList();
    }

    public List<AnnualFunctionInputOperation>
    hiddenInputOperations() {

        return inputOperations
                .stream()
                .filter(
                        AnnualFunctionInputOperation
                                ::hidden
                )
                .toList();
    }

    /*
     * =================================================
     * Natal Relation
     * =================================================
     */

    public boolean relationContextExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        AnnualFunctionInputOperation
                                ::relationContextExists
                );
    }

    public boolean restrictiveRelationContextExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        AnnualFunctionInputOperation
                                ::restrictiveRelationContextExists
                );
    }

    /*
     * =================================================
     * Transformation
     * =================================================
     */

    public boolean transformationContextExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        AnnualFunctionInputOperation
                                ::transformationContextExists
                );
    }

    public boolean activeTransformationExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        AnnualFunctionInputOperation
                                ::activeTransformationExists
                );
    }

    public boolean annualOriginalFunctionReductionExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        AnnualFunctionInputOperation
                                ::annualOriginalFunctionReductionExists
                );
    }

    public boolean annualOriginalFunctionStrongReductionExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        AnnualFunctionInputOperation
                                ::annualOriginalFunctionStrongReductionExists
                );
    }

    public boolean mixedAnnualOriginalFunctionCondition() {

        if (
                inputOperations
                        .stream()
                        .anyMatch(
                                AnnualFunctionInputOperation
                                        ::mixedAnnualOriginalFunctionTransformationContext
                        )
        ) {

            return true;
        }

        if (
                inputOperations.size()
                        <= 1
        ) {

            return false;
        }

        boolean reducedExists =
                inputOperations
                        .stream()
                        .anyMatch(
                                AnnualFunctionInputOperation
                                        ::annualOriginalFunctionReductionExists
                        );

        boolean retainedExists =
                inputOperations
                        .stream()
                        .anyMatch(
                                operation ->
                                        !operation
                                                .annualOriginalFunctionReductionExists()
                        );

        return reducedExists
                && retainedExists;
    }

    /*
     * =================================================
     * Transformation 중복 제거
     * =================================================
     *
     * 하나의 세운 지지 Transformation이
     * 본기/중기/여기 Input에 반복 연결될 수 있다.
     */

    public List<AnnualLuckNatalTransformationReference>
    distinctTransformationContexts() {

        Set<AnnualLuckNatalTransformationReference> result =
                new LinkedHashSet<>();

        for (
                AnnualFunctionInputOperation operation
                : inputOperations
        ) {

            result.addAll(
                    operation
                            .natalTransformations()
            );
        }

        return List.copyOf(
                result
        );
    }

    public List<AnnualLuckNatalTransformationReference>
    distinctActiveTransformations() {

        return distinctTransformationContexts()
                .stream()
                .filter(
                        AnnualLuckNatalTransformationReference
                                ::activated
                )
                .toList();
    }
}