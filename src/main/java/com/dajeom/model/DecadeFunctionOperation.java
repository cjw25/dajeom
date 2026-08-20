package com.dajeom.model;

import com.dajeom.type.DecadeLuckNatalRelationKind;
import com.dajeom.type.FiveElement;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 하나의 대운 Period에서
 * 하나의 Natal Function Family가
 * 실제 어떤 Input Operation Context를
 * 갖는지 묶은 Aggregate.
 *
 * =================================================
 *
 * 예:
 *
 * RESPONSIBILITY
 *
 * 대운 천간:
 * 정관
 *
 * 대운 지지 본기:
 * 편관
 *
 * 이라면:
 *
 * RESPONSIBILITY Function 안에
 * 두 개의 InputOperation이 존재할 수 있다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 천간과 지지를 한 점수로 합치지 않는다.
 *
 * 예:
 *
 * 천간 원기능 강한 감소
 * +
 * 지지 본기 원기능 유지
 *
 * 라면
 *
 * RESPONSIBILITY 전체를
 * "약화"라고 단정하지 않는다.
 *
 * 두 Input 상태를 모두 보존한다.
 */
public record DecadeFunctionOperation(

        DecadeFunctionEffect sourceEffect,

        List<DecadeFunctionInputOperation>
        inputOperations,

        boolean completeChart

) {

    public DecadeFunctionOperation {

        Objects.requireNonNull(
                sourceEffect,
                "DecadeFunctionOperation에는 DecadeFunctionEffect가 필요합니다."
        );

        inputOperations =
                inputOperations == null
                        ? List.of()
                        : List.copyOf(
                        inputOperations
                );

        if (
                sourceEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "DecadeFunctionOperation과 Source Effect의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 입력 Evidence Coverage 검증
         * =================================================
         */

        if (
                inputOperations.size()
                        != sourceEffect
                        .directTenGodEvidences()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "Function Effect의 직접 Input 수와 "
                            + "Function Operation Input 수가 일치하지 않습니다."
            );
        }

        Set<DecadeLuckTenGodEvidence> actual =
                new HashSet<>();

        for (
                DecadeFunctionInputOperation operation
                : inputOperations
        ) {

            Objects.requireNonNull(
                    operation,
                    "DecadeFunctionInputOperation은 null일 수 없습니다."
            );

            DecadeLuckTenGodEvidence input =
                    operation.inputEvidence();

            if (
                    input.period()
                            != sourceEffect.period()
            ) {

                throw new IllegalArgumentException(
                        "다른 대운 Period의 Input Operation이 연결되었습니다."
                );
            }

            if (
                    !sourceEffect
                            .functionType()
                            .contains(
                                    input.tenGodType()
                            )
            ) {

                throw new IllegalArgumentException(
                        "현재 Function Family에 속하지 않는 Input이 연결되었습니다. "
                                + "function="
                                + sourceEffect.functionType()
                                + ", tenGod="
                                + input.tenGodType()
                );
            }

            if (
                    !actual.add(
                            input
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 대운 십성 Input이 Function Operation에 중복되었습니다."
                );
            }
        }

        Set<DecadeLuckTenGodEvidence> expected =
                new HashSet<>(
                        sourceEffect
                                .directTenGodEvidences()
                );

        if (
                !actual.equals(
                        expected
                )
        ) {

            throw new IllegalArgumentException(
                    "Function Effect의 직접 십성 Input이 "
                            + "Operation 계층에서 정확히 보존되지 않았습니다."
            );
        }
    }

    public int sequenceNumber() {

        return sourceEffect
                .sequenceNumber();
    }

    public NatalTenGodFunctionType functionType() {

        return sourceEffect
                .functionType();
    }

    /*
     * =================================================
     * Input
     * =================================================
     */

    public boolean directlyEntered() {

        return !inputOperations
                .isEmpty();
    }

    public Optional<DecadeFunctionInputOperation>
    visibleInputOperation() {

        return inputOperations
                .stream()
                .filter(
                        DecadeFunctionInputOperation
                                ::visible
                )
                .findFirst();
    }

    public List<DecadeFunctionInputOperation>
    hiddenInputOperations() {

        return inputOperations
                .stream()
                .filter(
                        DecadeFunctionInputOperation
                                ::hidden
                )
                .toList();
    }

    public Optional<DecadeFunctionInputOperation>
    mainHiddenInputOperation() {

        return inputOperations
                .stream()
                .filter(
                        DecadeFunctionInputOperation
                                ::mainHiddenStem
                )
                .findFirst();
    }

    /*
     * =================================================
     * Transformation
     * =================================================
     */

    public boolean originalFunctionReductionExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        DecadeFunctionInputOperation
                                ::luckOriginalFunctionReductionExists
                );
    }

    public boolean strongOriginalFunctionReductionExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        DecadeFunctionInputOperation
                                ::luckOriginalFunctionStrongReductionExists
                );
    }

    /**
     * 일부 Input에는 원기능 감소가 있고
     * 일부 Input에는 감소가 없는가.
     *
     * 천간/지지를 한 방향으로
     * 축약하지 않기 위한 Helper.
     */
    public boolean mixedOriginalFunctionCondition() {

        if (
                inputOperations.size()
                        < 2
        ) {

            return inputOperations
                    .stream()
                    .anyMatch(
                            DecadeFunctionInputOperation
                                    ::mixedOriginalFunctionTransformationContext
                    );
        }

        boolean reduced =
                inputOperations
                        .stream()
                        .anyMatch(
                                DecadeFunctionInputOperation
                                        ::luckOriginalFunctionReductionExists
                        );

        boolean retained =
                inputOperations
                        .stream()
                        .anyMatch(
                                DecadeFunctionInputOperation
                                        ::originalInputNotReducedByTransformation
                        );

        return reduced
                && retained;
    }

    public boolean activeTransformationExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        DecadeFunctionInputOperation
                                ::activeTransformationExists
                );
    }

    public boolean strongTransformationExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        DecadeFunctionInputOperation
                                ::strongTransformationExists
                );
    }

    public List<FiveElement>
    activeTargetElements() {

        Set<FiveElement> result =
                new LinkedHashSet<>();

        for (
                DecadeFunctionInputOperation operation
                : inputOperations
        ) {

            result.addAll(
                    operation.activeTargetElements()
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Relation Context
     * =================================================
     */

    public boolean restrictiveRelationContextExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        DecadeFunctionInputOperation
                                ::restrictiveRelationContextExists
                );
    }

    public boolean combinationRelationContextExists() {

        return inputOperations
                .stream()
                .anyMatch(
                        DecadeFunctionInputOperation
                                ::combinationRelationContextExists
                );
    }

    /**
     * 동일 Relation Reference가
     * 여러 hidden Input에 연결될 수 있다.
     *
     * 따라서 Function 차원에서 사용할 때는
     * 반드시 중복 제거된 Reference를 사용한다.
     *
     * 같은 Relation을 여러 독립 Evidence로
     * 세지 않는다.
     */
    public List<DecadeLuckNatalRelationReference>
    distinctRelationContexts() {

        Set<DecadeLuckNatalRelationReference> result =
                new LinkedHashSet<>();

        for (
                DecadeFunctionInputOperation operation
                : inputOperations
        ) {

            result.addAll(
                    operation.relationContexts()
            );
        }

        return List.copyOf(
                result
        );
    }

    public List<DecadeLuckNatalRelationReference>
    distinctRelationContextsOf(

            DecadeLuckNatalRelationKind kind

    ) {

        if (
                kind == null
        ) {

            return List.of();
        }

        return distinctRelationContexts()
                .stream()
                .filter(
                        relation ->
                                relation.relationKind()
                                        == kind
                )
                .toList();
    }

    public List<DecadeLuckNatalTransformationReference>
    distinctTransformationContexts() {

        Set<DecadeLuckNatalTransformationReference> result =
                new LinkedHashSet<>();

        for (
                DecadeFunctionInputOperation operation
                : inputOperations
        ) {

            result.addAll(
                    operation.transformationContexts()
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Natal Cross Pattern
     * =================================================
     */

    public List<NatalCrossFunctionalPattern>
    natalCrossPatterns() {

        return sourceEffect
                .natalCrossPatterns();
    }

    public List<NatalCrossFunctionalPattern>
    effectiveNatalCrossPatterns() {

        return sourceEffect
                .effectiveNatalCrossPatterns();
    }

    /**
     * Function이 대운에서 직접 들어오면서
     * 원국 유효 Cross Pattern도 존재하는 상태.
     *
     * 아직 Cross Pattern 활성 확정은 아니다.
     */
    public boolean natalCrossPatternContactCandidate() {

        return directlyEntered()
                && !effectiveNatalCrossPatterns()
                .isEmpty();
    }

    /*
     * =================================================
     * Context
     * =================================================
     */

    public boolean mixedContext() {

        return sourceEffect
                .mixedContext();
    }

    public List<DecadeLuckContextSignal>
    contextSignals() {

        return sourceEffect
                .contextSignals();
    }

    /**
     * 다음 Capacity/Operation 판정 단계에서
     * 검토할 구조적 제한 후보.
     *
     * 개수 점수로 사용하지 않는다.
     */
    public boolean structuralRestrictionReviewRequired() {

        return originalFunctionReductionExists()
                || restrictiveRelationContextExists()
                || sourceEffect.hasWeakenedContext()
                || sourceEffect.hasConflictedContext();
    }
}