package com.dajeom.model;

import com.dajeom.type.DecadeLuckNatalRelationKind;
import com.dajeom.type.DecadeLuckNatalRelationLayer;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StructureTenGodType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 특정 대운의 하나의 직접 십성 Input이
 * 원국 관계 / 합화 Context 안에서
 * 어떤 구조적 상태에 놓이는지 보존한다.
 *
 * =================================================
 *
 * Input:
 *
 * DecadeLuckTenGodEvidence
 *
 * 예:
 *
 * - 대운 천간 십성
 * - 대운 지지 본기 십성
 * - 대운 지지 중기 십성
 * - 대운 지지 여기 십성
 *
 * =================================================
 *
 * 연결:
 *
 * visible input
 * ->
 * HEAVENLY_STEM 관계 / Transformation
 *
 * hidden input
 * ->
 * EARTHLY_BRANCH 관계 / Transformation
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 이 객체는:
 *
 * CLASH = 약화
 * COMBINATION = 강화
 * Transformation = 좋은 변화
 *
 * 같은 평가를 하지 않는다.
 *
 * =================================================
 *
 * 특히 지지:
 *
 * EarthlyBranch Transformation에서
 * originalFunctionReduced가 존재하더라도
 *
 * 특정 지장간 하나가 완전히 제거되었다고
 * 해석하지 않는다.
 *
 * 그것은 대운 지지 전체의
 * 원래 기능에 감소 Context가 있다는 뜻으로만
 * 보존한다.
 */
public record DecadeFunctionInputOperation(

        DecadeLuckTenGodEvidence inputEvidence,

        List<DecadeLuckNatalRelationReference>
        relationContexts,

        List<DecadeLuckNatalTransformationReference>
        transformationContexts

) {

    public DecadeFunctionInputOperation {

        Objects.requireNonNull(
                inputEvidence,
                "DecadeFunctionInputOperation에는 대운 십성 Input Evidence가 필요합니다."
        );

        relationContexts =
                relationContexts == null
                        ? List.of()
                        : List.copyOf(
                        relationContexts
                );

        transformationContexts =
                transformationContexts == null
                        ? List.of()
                        : List.copyOf(
                        transformationContexts
                );

        DecadeLuckNatalRelationLayer expectedLayer =
                inputEvidence.visible()
                        ? DecadeLuckNatalRelationLayer.HEAVENLY_STEM
                        : DecadeLuckNatalRelationLayer.EARTHLY_BRANCH;

        /*
         * =================================================
         * Relation Layer 정합성
         * =================================================
         */

        for (
                DecadeLuckNatalRelationReference relation
                : relationContexts
        ) {

            Objects.requireNonNull(
                    relation,
                    "대운 Function Input의 Relation Context는 null일 수 없습니다."
            );

            if (
                    relation.layer()
                            != expectedLayer
            ) {

                throw new IllegalArgumentException(
                        "대운 십성 Input과 Relation Layer가 일치하지 않습니다. "
                                + "source="
                                + inputEvidence.sourceType()
                                + ", relationLayer="
                                + relation.layer()
                );
            }
        }

        /*
         * =================================================
         * Transformation Layer 정합성
         * =================================================
         */

        for (
                DecadeLuckNatalTransformationReference transformation
                : transformationContexts
        ) {

            Objects.requireNonNull(
                    transformation,
                    "대운 Function Input의 Transformation Context는 null일 수 없습니다."
            );

            if (
                    transformation.layer()
                            != expectedLayer
            ) {

                throw new IllegalArgumentException(
                        "대운 십성 Input과 Transformation Layer가 일치하지 않습니다. "
                                + "source="
                                + inputEvidence.sourceType()
                                + ", transformationLayer="
                                + transformation.layer()
                );
            }
        }
    }

    /*
     * =================================================
     * Input
     * =================================================
     */

    public int sequenceNumber() {

        return inputEvidence
                .sequenceNumber();
    }

    public StructureTenGodType tenGodType() {

        return inputEvidence
                .tenGodType();
    }

    public boolean visible() {

        return inputEvidence
                .visible();
    }

    public boolean hidden() {

        return inputEvidence
                .hidden();
    }

    public boolean mainHiddenStem() {

        return inputEvidence
                .mainHiddenStem();
    }

    /*
     * =================================================
     * Relation
     * =================================================
     */

    public boolean hasRelationContext() {

        return !relationContexts
                .isEmpty();
    }

    public List<DecadeLuckNatalRelationReference>
    relationContextsOf(

            DecadeLuckNatalRelationKind kind

    ) {

        if (
                kind == null
        ) {

            return List.of();
        }

        return relationContexts
                .stream()
                .filter(
                        relation ->
                                relation.relationKind()
                                        == kind
                )
                .toList();
    }

    /**
     * 충/극/형/자형/해/파 계열의
     * 구조적 간섭 Context가 있는가.
     *
     * 이것만으로 기능 약화를 확정하지 않는다.
     */
    public boolean restrictiveRelationContextExists() {

        return relationContexts
                .stream()
                .anyMatch(
                        DecadeLuckNatalRelationReference
                                ::restrictiveInteraction
                );
    }

    public boolean combinationRelationContextExists() {

        return relationContexts
                .stream()
                .anyMatch(
                        DecadeLuckNatalRelationReference
                                ::combinationFamily
                );
    }

    /*
     * =================================================
     * Transformation
     * =================================================
     */

    public boolean hasTransformationContext() {

        return !transformationContexts
                .isEmpty();
    }

    /**
     * 대운 Participant 자체의 원기능 감소.
     *
     * 이것이 Function Input의
     * 원형 발현 제한을 검토하는
     * 공식 필드다.
     */
    public boolean luckOriginalFunctionReductionExists() {

        return transformationContexts
                .stream()
                .anyMatch(
                        DecadeLuckNatalTransformationReference
                                ::luckOriginalFunctionReduced
                );
    }

    public boolean luckOriginalFunctionStrongReductionExists() {

        return transformationContexts
                .stream()
                .anyMatch(
                        DecadeLuckNatalTransformationReference
                                ::luckOriginalFunctionStronglyReduced
                );
    }

    /**
     * 하나 이상의 합화 목표가
     * 실제 활성 상태인가.
     */
    public boolean activeTransformationExists() {

        return transformationContexts
                .stream()
                .anyMatch(
                        DecadeLuckNatalTransformationReference
                                ::activated
                );
    }

    public boolean strongTransformationExists() {

        return transformationContexts
                .stream()
                .anyMatch(
                        DecadeLuckNatalTransformationReference
                                ::strongTargetActivation
                );
    }

    /**
     * 실제 활성된 Transformation의
     * 목표 오행.
     *
     * 아직 이 목표 오행을
     * 새로운 십성 Function으로 변환하지 않는다.
     */
    public List<FiveElement>
    activeTargetElements() {

        Set<FiveElement> result =
                new LinkedHashSet<>();

        for (
                DecadeLuckNatalTransformationReference transformation
                : transformationContexts
        ) {

            if (
                    !transformation.activated()
            ) {

                continue;
            }

            result.add(
                    transformation.targetElement()
            );
        }

        return List.copyOf(
                result
        );
    }

    public List<FiveElement>
    strongTargetElements() {

        Set<FiveElement> result =
                new LinkedHashSet<>();

        for (
                DecadeLuckNatalTransformationReference transformation
                : transformationContexts
        ) {

            if (
                    !transformation
                            .strongTargetActivation()
            ) {

                continue;
            }

            result.add(
                    transformation.targetElement()
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 원형 기능 상태 Context
     * =================================================
     */

    /**
     * Transformation에 의해
     * 대운 Participant 원기능 감소가
     * 확인되지 않는 상태.
     *
     * 주의:
     *
     * "강화됨"을 의미하지 않는다.
     */
    public boolean originalInputNotReducedByTransformation() {

        return !luckOriginalFunctionReductionExists();
    }

    /**
     * 같은 Input에 여러 Transformation이 존재하면서
     *
     * 일부에서는 원기능 감소
     * 일부에서는 원기능 유지
     *
     * 상태가 동시에 존재하는가.
     */
    public boolean mixedOriginalFunctionTransformationContext() {

        if (
                transformationContexts.size()
                        < 2
        ) {

            return false;
        }

        boolean reduced =
                transformationContexts
                        .stream()
                        .anyMatch(
                                DecadeLuckNatalTransformationReference
                                        ::luckOriginalFunctionReduced
                        );

        boolean retained =
                transformationContexts
                        .stream()
                        .anyMatch(
                                transformation ->
                                        !transformation
                                                .luckOriginalFunctionReduced()
                        );

        return reduced
                && retained;
    }
}