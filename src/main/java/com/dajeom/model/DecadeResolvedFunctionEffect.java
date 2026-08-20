package com.dajeom.model;

import com.dajeom.type.DecadeCoreCapacitySide;
import com.dajeom.type.DecadeFunctionActivationMode;
import com.dajeom.type.DecadeOriginalInputState;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 대운 Period에서
 * 하나의 Function에 대해 확정된
 * 최종 구조적 Functional Effect.
 *
 * =================================================
 *
 * 입력 계층:
 *
 * DecadeFunctionMigration
 *
 * 즉 내부적으로 이미:
 *
 * - 원래 대운 십성 Input
 * - 원국 관계
 * - 합화
 * - 원기능 감소
 * - 원국 CoreSelf Capacity
 * - Transformation Target Function
 *
 * 을 모두 거친 상태다.
 *
 * =================================================
 *
 * 이 객체에서 처음으로:
 *
 * "현재 Function이 이번 대운에서
 * 실제 활성 접점을 가지는가"
 *
 * 를 원래 Input과 Transformation Target을
 * 함께 보아 확정한다.
 *
 * =================================================
 *
 * 그러나 여전히:
 *
 * - 좋다
 * - 나쁘다
 * - 재물운이 좋다
 * - 직업운이 나쁘다
 *
 * 같은 생활영역 결론은 만들지 않는다.
 *
 * =================================================
 *
 * 매우 중요한 원칙:
 *
 * 같은 Branch Transformation이
 * 여러 지장간 Source Function에 연결될 수 있다.
 *
 * incomingMigrations의 개수를
 * Transformation 독립 근거 개수로 사용하지 않는다.
 *
 * distinctIncomingTransformations()
 *
 * 을 사용해야 한다.
 */
public record DecadeResolvedFunctionEffect(

        DecadeFunctionMigration sourceMigration,

        List<DecadeFunctionMigration> incomingMigrations,

        DecadeFunctionActivationMode activationMode,

        DecadeOriginalInputState originalInputState,

        boolean completeChart

) {

    public DecadeResolvedFunctionEffect {

        /*
         * =================================================
         * Compact Constructor 주의
         * =================================================
         *
         * record compact constructor 본문이 실행되는 동안에는
         * record field에 대한 최종 대입이 아직 끝나지 않았다.
         *
         * 따라서 이 생성자 내부에서는:
         *
         * sequenceNumber()
         * functionType()
         * capacityContext()
         * sourceOperation()
         *
         * 처럼 this.sourceMigration을 읽는
         * instance helper를 호출하면 안 된다.
         *
         * 반드시 생성자 매개변수 sourceMigration에서
         * 직접 필요한 값을 꺼내서 사용한다.
         */

        Objects.requireNonNull(
                sourceMigration,
                "Resolved Function Effect에는 Source Migration이 필요합니다."
        );

        incomingMigrations =
                incomingMigrations == null
                        ? List.of()
                        : List.copyOf(
                        incomingMigrations
                );

        Objects.requireNonNull(
                activationMode,
                "Resolved Function Effect에는 Activation Mode가 필요합니다."
        );

        Objects.requireNonNull(
                originalInputState,
                "Resolved Function Effect에는 Original Input State가 필요합니다."
        );

        /*
         * =================================================
         * Constructor-local Source 값
         * =================================================
         *
         * 이 값들은 this.sourceMigration이 아니라
         * 생성자 매개변수 sourceMigration에서 가져온다.
         */

        int sourceSequenceNumber =
                sourceMigration
                        .sequenceNumber();

        NatalTenGodFunctionType sourceFunctionType =
                sourceMigration
                        .sourceFunctionType();

        DecadeFunctionOperation sourceOperation =
                sourceMigration
                        .sourceOperation();

        if (
                sourceMigration.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Resolved Function Effect와 Source Migration의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Incoming Migration 검증
         * =================================================
         */

        for (
                DecadeFunctionMigration incomingMigration
                : incomingMigrations
        ) {

            Objects.requireNonNull(
                    incomingMigration,
                    "Incoming Function Migration은 null일 수 없습니다."
            );

            if (
                    incomingMigration.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Incoming Migration의 completeChart가 일치하지 않습니다."
                );
            }

            /*
             * =================================================
             * 중요
             * =================================================
             *
             * 기존:
             *
             * incomingMigration.sequenceNumber()
             * != sequenceNumber()
             *
             * sequenceNumber()는 this.sourceMigration을 읽는다.
             *
             * compact constructor 실행 중에는
             * this.sourceMigration이 아직 null일 수 있으므로
             * 생성자 지역값을 사용한다.
             */

            if (
                    incomingMigration.sequenceNumber()
                            != sourceSequenceNumber
            ) {

                throw new IllegalArgumentException(
                        "다른 대운 Period의 Migration이 Incoming Target으로 연결되었습니다."
                );
            }

            /*
             * =================================================
             * 중요
             * =================================================
             *
             * 기존 functionType() 호출 역시
             * this.sourceMigration을 읽으므로 사용하지 않는다.
             */

            if (
                    !incomingMigration.targets(
                            sourceFunctionType
                    )
            ) {

                throw new IllegalArgumentException(
                        "현재 Function을 실제 Target으로 하지 않는 Migration이 연결되었습니다. "
                                + "function="
                                + sourceFunctionType
                                + ", source="
                                + incomingMigration.sourceFunctionType()
                );
            }
        }

        /*
         * =================================================
         * Activation Mode 검증
         * =================================================
         */

        boolean originalInput =
                sourceOperation
                        .directlyEntered();

        boolean transformedTarget =
                !incomingMigrations
                        .isEmpty();

        DecadeFunctionActivationMode expectedActivationMode =
                DecadeFunctionActivationMode.of(
                        originalInput,
                        transformedTarget
                );

        if (
                activationMode
                        != expectedActivationMode
        ) {

            throw new IllegalArgumentException(
                    "Function Activation Mode가 실제 Source와 일치하지 않습니다. "
                            + "function="
                            + sourceFunctionType
                            + ", expected="
                            + expectedActivationMode
                            + ", actual="
                            + activationMode
            );
        }

        /*
         * =================================================
         * Original Input State 검증
         * =================================================
         */

        DecadeOriginalInputState expectedOriginalState =
                resolveOriginalInputState(
                        sourceOperation
                );

        if (
                originalInputState
                        != expectedOriginalState
        ) {

            throw new IllegalArgumentException(
                    "Original Input State가 실제 Input Operation과 일치하지 않습니다. "
                            + "function="
                            + sourceFunctionType
                            + ", expected="
                            + expectedOriginalState
                            + ", actual="
                            + originalInputState
            );
        }
    }

    public int sequenceNumber() {

        return sourceMigration
                .sequenceNumber();
    }

    public NatalTenGodFunctionType functionType() {

        return sourceMigration
                .sourceFunctionType();
    }

    public DecadeFunctionCapacityContext capacityContext() {

        return sourceMigration
                .capacityContext();
    }

    public DecadeFunctionOperation sourceOperation() {

        return sourceMigration
                .sourceOperation();
    }

    /*
     * =================================================
     * 활성
     * =================================================
     */

    public boolean active() {

        return activationMode
                .active();
    }

    public boolean originalInputActive() {

        return activationMode
                .originalInput();
    }

    public boolean transformedTargetActive() {

        return activationMode
                .transformedTarget();
    }

    public boolean originalAndTransformedTargetActive() {

        return activationMode
                .mixedActivationSource();
    }

    /*
     * =================================================
     * Original Input
     * =================================================
     */

    public boolean originalInputPresent() {

        return originalInputState
                .present();
    }

    public boolean originalInputRetained() {

        return originalInputState
                .retained();
    }

    public boolean originalInputReduced() {

        return originalInputState
                .reduced();
    }

    public boolean originalInputStronglyReduced() {

        return originalInputState
                .stronglyReduced();
    }

    public boolean mixedOriginalInputState() {

        return originalInputState
                .mixed();
    }

    /*
     * =================================================
     * Transformation Target
     * =================================================
     */

    public List<DecadeFunctionMigrationTarget>
    incomingTargets() {

        return incomingMigrations
                .stream()
                .flatMap(
                        migration ->
                                migration
                                        .targetsTo(
                                                functionType()
                                        )
                                        .stream()
                )
                .toList();
    }

    /**
     * 같은 Branch Transformation이
     * 여러 Source Function에 걸쳐
     * 중복 연결될 수 있으므로
     * Transformation 자체는 반드시
     * 중복 제거해서 사용한다.
     */
    public List<DecadeLuckNatalTransformationReference>
    distinctIncomingTransformations() {

        Set<DecadeLuckNatalTransformationReference> result =
                new LinkedHashSet<>();

        for (
                DecadeFunctionMigrationTarget target
                : incomingTargets()
        ) {

            result.add(
                    target.sourceTransformation()
            );
        }

        return List.copyOf(
                result
        );
    }

    public boolean strongTransformedTargetActive() {

        return distinctIncomingTransformations()
                .stream()
                .anyMatch(
                        DecadeLuckNatalTransformationReference
                                ::strongTargetActivation
                );
    }

    public List<NatalTenGodFunctionType>
    incomingSourceFunctions() {

        Set<NatalTenGodFunctionType> result =
                new LinkedHashSet<>();

        for (
                DecadeFunctionMigration migration
                : incomingMigrations
        ) {

            result.add(
                    migration.sourceFunctionType()
            );
        }

        return List.copyOf(
                result
        );
    }

    public boolean crossFunctionTargetActive() {

        return incomingMigrations
                .stream()
                .anyMatch(
                        migration ->
                                migration.sourceFunctionType()
                                        != functionType()
                );
    }

    public boolean sameFunctionTransformationTargetActive() {

        return incomingMigrations
                .stream()
                .anyMatch(
                        migration ->
                                migration.sourceFunctionType()
                                        == functionType()
                );
    }

    /*
     * =================================================
     * Natal Fixed Capacity
     * =================================================
     *
     * 이전 Capacity Context에서는
     * 직접 Input만 기준으로 접촉을 봤다.
     *
     * 여기서는 Transformation Target으로
     * Function이 활성된 경우도
     * 실제 CoreSelf Capacity 접촉으로 본다.
     */

    public DecadeCoreCapacitySide capacitySide() {

        return capacityContext()
                .capacitySide();
    }

    public DayMasterStrengthLevel natalStrengthLevel() {

        return capacityContext()
                .natalStrengthLevel();
    }

    public boolean natalStrongSide() {

        return capacityContext()
                .natalStrongSide();
    }

    public boolean natalWeakSide() {

        return capacityContext()
                .natalWeakSide();
    }

    public boolean natalBalancedZone() {

        return capacityContext()
                .natalBalancedZone();
    }

    public boolean supportSide() {

        return capacitySide()
                .supportSide();
    }

    public boolean pressureSide() {

        return capacitySide()
                .pressureSide();
    }

    /**
     * strong-side 원국에
     * support-side Function이
     * 실제 활성된 경우.
     *
     * 길흉이 아니라
     * 동일 방향 누적 검토 상태다.
     */
    public boolean strongSideSupportContact() {

        return active()
                && natalStrongSide()
                && supportSide();
    }

    /**
     * weak-side 원국에
     * pressure-side Function이
     * 실제 활성된 경우.
     */
    public boolean weakSidePressureContact() {

        return active()
                && natalWeakSide()
                && pressureSide();
    }

    public boolean strongSidePressureContact() {

        return active()
                && natalStrongSide()
                && pressureSide();
    }

    public boolean weakSideSupportContact() {

        return active()
                && natalWeakSide()
                && supportSide();
    }

    public boolean sameDirectionAccumulationContact() {

        return strongSideSupportContact()
                || weakSidePressureContact();
    }

    public boolean counterDirectionContact() {

        return strongSidePressureContact()
                || weakSideSupportContact();
    }

    /*
     * =================================================
     * Natal Cross Pattern 접촉
     * =================================================
     */

    public List<NatalCrossFunctionalPattern>
    effectiveNatalCrossPatterns() {

        return sourceOperation()
                .effectiveNatalCrossPatterns();
    }

    /**
     * 현재 Function이 실제로 활성되어 있고
     * 원국에도 해당 Function이 참여하는
     * 유효 Cross Pattern이 존재한다.
     *
     * =================================================
     *
     * 이것은:
     *
     * "원국 Cross Pattern 전체가
     * 완전히 발동했다"
     *
     * 는 뜻은 아니다.
     *
     * 대운이 그 Pattern의 한 기능축에
     * 실제 접촉했다는 뜻이다.
     */
    public boolean natalCrossPatternContact() {

        return active()
                && !effectiveNatalCrossPatterns()
                .isEmpty();
    }

    /*
     * =================================================
     * Function Context
     * =================================================
     */

    public List<DecadeLuckContextSignal>
    contextSignals() {

        return sourceOperation()
                .contextSignals();
    }

    public boolean supportedContextExists() {

        return sourceOperation()
                .sourceEffect()
                .hasSupportedContext();
    }

    public boolean qualifiedContextExists() {

        return sourceOperation()
                .sourceEffect()
                .hasQualifiedContext();
    }

    public boolean weakenedContextExists() {

        return sourceOperation()
                .sourceEffect()
                .hasWeakenedContext();
    }

    public boolean conflictedContextExists() {

        return sourceOperation()
                .sourceEffect()
                .hasConflictedContext();
    }

    public boolean mixedFunctionContext() {

        return sourceOperation()
                .mixedContext();
    }

    /*
     * =================================================
     * Relation
     * =================================================
     */

    public boolean restrictiveRelationContextExists() {

        return sourceOperation()
                .restrictiveRelationContextExists();
    }

    /*
     * =================================================
     * 최종 Qualification
     * =================================================
     */

    /**
     * 이 Function을 한 방향의 단순 문장으로
     * 설명하면 안 되는 상태.
     */
    public boolean qualificationRequired() {

        if (
                !active()
        ) {

            return false;
        }

        return capacityContext()
                .followStructureReviewRequired()

                || capacityContext()
                .natalExtreme()

                || natalBalancedZone()

                || originalInputReduced()

                || transformedTargetActive()

                || restrictiveRelationContextExists()

                || mixedFunctionContext()

                || qualifiedContextExists()

                || weakenedContextExists()

                || conflictedContextExists();
    }

    /**
     * 후속 LifeDomain 또는 Annual 교차에서
     * 원국 Capacity를 반드시 확인해야 하는가.
     */
    public boolean capacityReviewRequired() {

        if (
                !active()
        ) {

            return false;
        }

        return sameDirectionAccumulationContact()
                || capacityContext()
                .followStructureReviewRequired()

                || capacityContext()
                .natalExtreme()

                || originalInputReduced()

                || transformedTargetActive();
    }

    /*
     * =================================================
     * Original State Resolution
     * =================================================
     */

    private static DecadeOriginalInputState
    resolveOriginalInputState(

            DecadeFunctionOperation operation

    ) {

        Objects.requireNonNull(
                operation,
                "Original Input State 판정에는 Function Operation이 필요합니다."
        );

        if (
                !operation.directlyEntered()
        ) {

            return DecadeOriginalInputState
                    .ABSENT;
        }

        Set<DecadeOriginalInputState> inputStates =
                new LinkedHashSet<>();

        for (
                DecadeFunctionInputOperation input
                : operation.inputOperations()
        ) {

            if (
                    input.mixedOriginalFunctionTransformationContext()
            ) {

                inputStates.add(
                        DecadeOriginalInputState
                                .MIXED
                );

                continue;
            }

            if (
                    input.luckOriginalFunctionStrongReductionExists()
            ) {

                inputStates.add(
                        DecadeOriginalInputState
                                .STRONGLY_REDUCED
                );

                continue;
            }

            if (
                    input.luckOriginalFunctionReductionExists()
            ) {

                inputStates.add(
                        DecadeOriginalInputState
                                .REDUCED
                );

                continue;
            }

            inputStates.add(
                    DecadeOriginalInputState
                            .RETAINED
            );
        }

        if (
                inputStates.size()
                        != 1
        ) {

            return DecadeOriginalInputState
                    .MIXED;
        }

        DecadeOriginalInputState only =
                inputStates
                        .iterator()
                        .next();

        if (
                only == DecadeOriginalInputState.MIXED
        ) {

            return DecadeOriginalInputState
                    .MIXED;
        }

        return only;
    }
}