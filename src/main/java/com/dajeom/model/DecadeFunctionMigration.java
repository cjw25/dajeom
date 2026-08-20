package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 대운 Period에서
 * 하나의 원래 Function이
 * Transformation을 통해
 * 어떤 Function Target을 활성화하는지 보존한다.
 *
 * =================================================
 *
 * Source:
 *
 * DecadeFunctionCapacityContext
 *
 * =================================================
 *
 * Target:
 *
 * 0개 이상
 * DecadeFunctionMigrationTarget
 *
 * =================================================
 *
 * Target이 0개일 수 있는 이유:
 *
 * - Transformation 자체가 없음
 * - 합 관계만 존재
 * - Transformation은 존재하지만
 *   target activation이 비활성
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 원래 Function과 Target Function을
 * 서로 대체하지 않는다.
 *
 * 예:
 *
 * RESPONSIBILITY
 *
 * 원기능 PARTIALLY_REDUCED
 *
 * +
 *
 * PROCESSING target 활성
 *
 * 은:
 *
 * RESPONSIBILITY 삭제
 * PROCESSING으로 교체
 *
 * 가 아니다.
 *
 * 두 구조를 동시에 보존한다.
 */
public record DecadeFunctionMigration(

        DecadeFunctionCapacityContext
        capacityContext,

        List<DecadeFunctionMigrationTarget>
        migrationTargets,

        boolean completeChart

) {

    public DecadeFunctionMigration {

        /*
         * =================================================
         * Compact Constructor 주의
         * =================================================
         *
         * record의 compact constructor 본문이 실행되는 동안에는
         * canonical field의 최종 대입이 아직 완료되지 않았다.
         *
         * 따라서 이 생성자 안에서는:
         *
         * sourceFunctionType()
         * sourceOperation()
         * sequenceNumber()
         *
         * 처럼 this.capacityContext를 읽는
         * instance helper를 사용하면 안 된다.
         *
         * 반드시 생성자 매개변수 capacityContext를
         * 직접 사용해야 한다.
         */

        Objects.requireNonNull(
                capacityContext,
                "DecadeFunctionMigration에는 Capacity Context가 필요합니다."
        );

        migrationTargets =
                migrationTargets == null
                        ? List.of()
                        : List.copyOf(
                        migrationTargets
                );

        if (
                capacityContext.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Function Migration과 Capacity Context의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Transformation 하나당
         * Source Function 내부 Target 하나.
         * =================================================
         */

        Set<DecadeLuckNatalTransformationReference>
                seenTransformations =
                new HashSet<>();

        DecadeFunctionOperation sourceOperation =
                capacityContext
                        .operation();

        NatalTenGodFunctionType sourceFunctionType =
                capacityContext
                        .functionType();

        for (
                DecadeFunctionMigrationTarget target
                : migrationTargets
        ) {

            Objects.requireNonNull(
                    target,
                    "DecadeFunctionMigrationTarget은 null일 수 없습니다."
            );

            if (
                    !seenTransformations.add(
                            target.sourceTransformation()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Transformation이 같은 Source Function에서 "
                                + "중복 Migration Target으로 생성되었습니다. code="
                                + target
                                .sourceTransformation()
                                .transformationCode()
                );
            }

            if (
                    !sourceOperation
                            .distinctTransformationContexts()
                            .contains(
                                    target.sourceTransformation()
                            )
            ) {

                throw new IllegalArgumentException(
                        "현재 Source Function Operation에 없는 Transformation이 "
                                + "Migration Target으로 연결되었습니다."
                );
            }

            for (
                    DecadeFunctionInputOperation input
                    : target.sourceInputOperations()
            ) {

                if (
                        !sourceOperation
                                .inputOperations()
                                .contains(
                                        input
                                )
                ) {

                    throw new IllegalArgumentException(
                            "Migration Target의 Source Input이 "
                                    + "현재 Function Operation에 존재하지 않습니다."
                    );
                }

                /*
                 * =================================================
                 * 중요
                 * =================================================
                 *
                 * 기존:
                 *
                 * sourceFunctionType()
                 *
                 * 위 instance helper는 compact constructor 실행 중
                 * 아직 대입되지 않은 this.capacityContext를 읽어서
                 * NullPointerException을 발생시켰다.
                 *
                 * 수정:
                 *
                 * 생성자 매개변수에서 미리 확정한
                 * sourceFunctionType 지역변수를 사용한다.
                 */

                if (
                        !sourceFunctionType
                                .contains(
                                        input.tenGodType()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "Migration Target Source Input이 "
                                    + "현재 Source Function에 속하지 않습니다."
                    );
                }
            }
        }
    }


    public int sequenceNumber() {

        return capacityContext
                .sequenceNumber();
    }


    public NatalTenGodFunctionType sourceFunctionType() {

        return capacityContext
                .functionType();
    }


    public DecadeFunctionOperation sourceOperation() {

        return capacityContext
                .operation();
    }


    /*
     * =================================================
     * Target 존재
     * =================================================
     */

    public boolean hasActiveTransformationTarget() {

        return !migrationTargets
                .isEmpty();
    }


    /*
     * =================================================
     * Same Function / Cross Function
     * =================================================
     */

    /**
     * Transformation 목표가
     * 원래 Function과 동일한 경우.
     *
     * 이것은 다른 Function으로 이동한 것이 아니라
     * 동일 Functional Family 방향의
     * 목표 활성이다.
     */
    public List<DecadeFunctionMigrationTarget>
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


    /**
     * 원래 Function과 다른
     * Functional Family가 활성된 경우.
     */
    public List<DecadeFunctionMigrationTarget>
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


    /*
     * =================================================
     * Original Function 상태
     * =================================================
     */

    public boolean sourceOriginalFunctionReductionExists() {

        return capacityContext
                .originalFunctionReductionExists();
    }


    public boolean sourceOriginalFunctionStrongReductionExists() {

        return capacityContext
                .strongOriginalFunctionReductionExists();
    }


    public boolean sourceOriginalFunctionMixedCondition() {

        return capacityContext
                .mixedOriginalFunctionCondition();
    }


    /*
     * =================================================
     * Target Function
     * =================================================
     */

    public List<NatalTenGodFunctionType>
    targetFunctionTypes() {

        Set<NatalTenGodFunctionType> result =
                new LinkedHashSet<>();

        for (
                DecadeFunctionMigrationTarget target
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


    public List<DecadeFunctionMigrationTarget>
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


    /*
     * =================================================
     * Strong Target
     * =================================================
     */

    public List<DecadeFunctionMigrationTarget>
    strongTargets() {

        return migrationTargets
                .stream()
                .filter(
                        DecadeFunctionMigrationTarget
                                ::strongTargetActivation
                )
                .toList();
    }


    public boolean strongCrossFunctionMigrationExists() {

        return crossFunctionTargets()
                .stream()
                .anyMatch(
                        DecadeFunctionMigrationTarget
                                ::strongTargetActivation
                );
    }


    /*
     * =================================================
     * Qualification
     * =================================================
     */

    /**
     * 원래 Function 감소와
     * 새로운 Target Function 활성 등이
     * 동시에 존재해서 단순한
     * "A가 B로 바뀌었다" 식 표현을
     * 하면 안 되는 상태.
     */
    public boolean qualificationRequired() {

        return capacityContext
                .qualificationRequired()

                || sourceOriginalFunctionReductionExists()

                || migrationTargets.size() >= 2;
    }
}