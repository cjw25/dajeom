package com.dajeom.engine;

import com.dajeom.model.DecadeFunctionInputOperation;
import com.dajeom.model.DecadeFunctionMigration;
import com.dajeom.model.DecadeFunctionalMigrationAnalysis;
import com.dajeom.model.DecadePeriodFunctionMigrationAnalysis;
import com.dajeom.model.DecadePeriodResolvedFunctionalAnalysis;
import com.dajeom.model.DecadeResolvedFunctionEffect;
import com.dajeom.model.DecadeResolvedFunctionalAnalysis;
import com.dajeom.type.DecadeFunctionActivationMode;
import com.dajeom.type.DecadeOriginalInputState;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 대운 Functional Layer의
 * 첫 번째 최종 수렴 Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * DecadeFunctionalMigrationAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * Period별 5개
 * DecadeResolvedFunctionEffect
 *
 * =================================================
 *
 * 각 Function에 대해:
 *
 * 1.
 * 원래 대운 Input 존재 여부
 *
 * 2.
 * 원기능 유지/감소 상태
 *
 * 3.
 * 다른 Function Transformation으로부터
 * 들어온 Target 활성
 *
 * 4.
 * 원국 고정 Capacity 접촉
 *
 * 5.
 * Natal Cross Pattern 접촉
 *
 * 6.
 * Function Context
 *
 * 를 하나의 구조에서 조회 가능하게 한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 좋음/나쁨
 * - 대운 총점
 * - 생활영역 점수
 * - 사건 예측
 * - 신강신약 재계산
 * - 용신 재선정
 * - 격국 재판정
 *
 * =================================================
 *
 * 특히:
 *
 * Transformation Target으로 새로 활성된
 * Function도 실제 활성 Function으로 본다.
 *
 * 하지만 Source Function을 삭제하거나
 * Target Function으로 교체하지 않는다.
 */
@Component
public class DecadeResolvedFunctionalAnalysisEngine {

    public DecadeResolvedFunctionalAnalysis analyze(

            DecadeFunctionalMigrationAnalysis
                    migrationAnalysis

    ) {

        Objects.requireNonNull(
                migrationAnalysis,
                "Resolved Functional Analysis에는 DecadeFunctionalMigrationAnalysis가 필요합니다."
        );

        boolean completeChart =
                migrationAnalysis
                        .completeChart();

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !migrationAnalysis
                        .analysisAvailable()
        ) {

            return new DecadeResolvedFunctionalAnalysis(
                    migrationAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<DecadePeriodResolvedFunctionalAnalysis>
                periodAnalyses =
                new ArrayList<>();

        for (
                DecadePeriodFunctionMigrationAnalysis periodMigration
                : migrationAnalysis.periodAnalyses()
        ) {

            periodAnalyses.add(
                    analyzePeriod(
                            periodMigration,
                            completeChart
                    )
            );
        }

        return new DecadeResolvedFunctionalAnalysis(
                migrationAnalysis,
                List.copyOf(
                        periodAnalyses
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    private DecadePeriodResolvedFunctionalAnalysis
    analyzePeriod(

            DecadePeriodFunctionMigrationAnalysis
                    periodMigration,

            boolean completeChart

    ) {

        List<DecadeResolvedFunctionEffect>
                resolvedEffects =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            DecadeFunctionMigration sourceMigration =
                    periodMigration
                            .requireMigration(
                                    functionType
                            );

            List<DecadeFunctionMigration> incomingMigrations =
                    periodMigration
                            .functionMigrations()
                            .stream()
                            .filter(
                                    migration ->
                                            migration.targets(
                                                    functionType
                                            )
                            )
                            .toList();

            boolean originalInput =
                    sourceMigration
                            .sourceOperation()
                            .directlyEntered();

            boolean transformedTarget =
                    !incomingMigrations
                            .isEmpty();

            DecadeFunctionActivationMode activationMode =
                    DecadeFunctionActivationMode
                            .of(
                                    originalInput,
                                    transformedTarget
                            );

            DecadeOriginalInputState originalInputState =
                    resolveOriginalInputState(
                            sourceMigration
                    );

            resolvedEffects.add(
                    new DecadeResolvedFunctionEffect(
                            sourceMigration,
                            incomingMigrations,
                            activationMode,
                            originalInputState,
                            completeChart
                    )
            );
        }

        return new DecadePeriodResolvedFunctionalAnalysis(
                periodMigration,
                List.copyOf(
                        resolvedEffects
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Original Input State
     * =================================================
     *
     * Function 전체를:
     *
     * "강한 감소 하나가 있으니
     * STRONGLY_REDUCED"
     *
     * 로 단순화하지 않는다.
     *
     * 각 Input의 상태가 다르면 MIXED다.
     *
     * 예:
     *
     * 천간 = STRONGLY_REDUCED
     * 지지 본기 = RETAINED
     *
     * ↓
     *
     * Function = MIXED
     */

    private DecadeOriginalInputState
    resolveOriginalInputState(

            DecadeFunctionMigration migration

    ) {

        if (
                !migration
                        .sourceOperation()
                        .directlyEntered()
        ) {

            return DecadeOriginalInputState
                    .ABSENT;
        }

        Set<DecadeOriginalInputState> states =
                new LinkedHashSet<>();

        for (
                DecadeFunctionInputOperation input
                : migration
                .sourceOperation()
                .inputOperations()
        ) {

            states.add(
                    resolveInputState(
                            input
                    )
            );
        }

        if (
                states.size()
                        != 1
        ) {

            return DecadeOriginalInputState
                    .MIXED;
        }

        DecadeOriginalInputState only =
                states
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

    private DecadeOriginalInputState
    resolveInputState(

            DecadeFunctionInputOperation input

    ) {

        /*
         * 같은 Input 자체가
         * 여러 Transformation에서
         * 감소/유지 상태를 동시에 갖는 경우.
         */
        if (
                input
                        .mixedOriginalFunctionTransformationContext()
        ) {

            return DecadeOriginalInputState
                    .MIXED;
        }

        if (
                input
                        .luckOriginalFunctionStrongReductionExists()
        ) {

            return DecadeOriginalInputState
                    .STRONGLY_REDUCED;
        }

        if (
                input
                        .luckOriginalFunctionReductionExists()
        ) {

            return DecadeOriginalInputState
                    .REDUCED;
        }

        return DecadeOriginalInputState
                .RETAINED;
    }
}