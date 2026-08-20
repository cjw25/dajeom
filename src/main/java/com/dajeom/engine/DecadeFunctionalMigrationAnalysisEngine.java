package com.dajeom.engine;

import com.dajeom.model.DayMasterElementRelationAnalysis;
import com.dajeom.model.DecadeFunctionCapacityContext;
import com.dajeom.model.DecadeFunctionInputOperation;
import com.dajeom.model.DecadeFunctionMigration;
import com.dajeom.model.DecadeFunctionMigrationTarget;
import com.dajeom.model.DecadeFunctionOperation;
import com.dajeom.model.DecadeFunctionalCapacityAnalysis;
import com.dajeom.model.DecadeFunctionalMigrationAnalysis;
import com.dajeom.model.DecadeLuckNatalTransformationReference;
import com.dajeom.model.DecadePeriodFunctionMigrationAnalysis;
import com.dajeom.model.DecadePeriodFunctionalCapacityAnalysis;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 활성된 대운 Transformation 목표 오행을
 * 일간 기준 Function Family로 연결한다.
 *
 * =================================================
 *
 * 입력:
 *
 * DayMasterElementRelationAnalysis
 *
 * +
 *
 * DecadeFunctionalCapacityAnalysis
 *
 * =================================================
 *
 * 핵심 흐름:
 *
 * Transformation.targetElement
 *
 * ↓
 *
 * DayMasterElementRelationAnalysis
 *
 * ↓
 *
 * DayMasterElementRole
 *
 * ↓
 *
 * NatalTenGodFunctionType
 *
 * =================================================
 *
 * 여기서 FiveElement의 상생상극을
 * 다시 구현하지 않는다.
 *
 * 이미 4-1에서 확정된:
 *
 * elementByRole
 *
 * 을 공식 Source로 사용한다.
 *
 * =================================================
 *
 * 중요한 정책:
 *
 * target activation이 실제 active인
 * Transformation만 Migration Target을 만든다.
 *
 * 비활성 Transformation은
 * 기존 Operation 계층에 계속 보존되지만
 * 새 Function이 활성됐다고 보지 않는다.
 *
 * =================================================
 *
 * 또한:
 *
 * originalFunctionReduced=true
 *
 * 이라고 해서 Source Function을 삭제하지 않는다.
 *
 * 기존 Source Function 상태와
 * 새 Target Function 활성은
 * 동시에 보존한다.
 */
@Component
public class DecadeFunctionalMigrationAnalysisEngine {

    public DecadeFunctionalMigrationAnalysis analyze(

            DayMasterElementRelationAnalysis
                    elementRelationAnalysis,

            DecadeFunctionalCapacityAnalysis
                    capacityAnalysis

    ) {

        Objects.requireNonNull(
                elementRelationAnalysis,
                "Function Migration에는 DayMasterElementRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                capacityAnalysis,
                "Function Migration에는 DecadeFunctionalCapacityAnalysis가 필요합니다."
        );

        NatalFunctionalAnalysis natal =
                capacityAnalysis
                        .natalFunctionalAnalysis();

        /*
         * =================================================
         * 동일 원국 검증
         * =================================================
         */

        if (
                elementRelationAnalysis.dayMaster()
                        != natal.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "DayMasterElementRelationAnalysis와 NatalFunctionalAnalysis의 일간이 일치하지 않습니다."
            );
        }

        if (
                elementRelationAnalysis.dayMasterElement()
                        != natal
                        .coreSelf()
                        .dayMasterElement()
        ) {

            throw new IllegalArgumentException(
                    "DayMasterElementRelationAnalysis와 Natal CoreSelf의 일간 오행이 일치하지 않습니다."
            );
        }

        boolean completeChart =
                natal.completeChart();

        if (
                capacityAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Capacity Analysis와 NatalFunctionalAnalysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                !capacityAnalysis
                        .analysisAvailable()
        ) {

            return new DecadeFunctionalMigrationAnalysis(
                    elementRelationAnalysis,
                    capacityAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<DecadePeriodFunctionMigrationAnalysis>
                periodAnalyses =
                new ArrayList<>();

        for (
                DecadePeriodFunctionalCapacityAnalysis periodCapacity
                : capacityAnalysis.periodAnalyses()
        ) {

            periodAnalyses.add(
                    analyzePeriod(
                            elementRelationAnalysis,
                            periodCapacity,
                            completeChart
                    )
            );
        }

        return new DecadeFunctionalMigrationAnalysis(
                elementRelationAnalysis,
                capacityAnalysis,
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

    private DecadePeriodFunctionMigrationAnalysis
    analyzePeriod(

            DayMasterElementRelationAnalysis
                    elementRelationAnalysis,

            DecadePeriodFunctionalCapacityAnalysis
                    periodCapacity,

            boolean completeChart

    ) {

        List<DecadeFunctionMigration>
                migrations =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType sourceFunctionType
                : NatalTenGodFunctionType.values()
        ) {

            DecadeFunctionCapacityContext capacityContext =
                    periodCapacity
                            .requireContext(
                                    sourceFunctionType
                            );

            migrations.add(
                    analyzeFunction(
                            elementRelationAnalysis,
                            capacityContext,
                            completeChart
                    )
            );
        }

        return new DecadePeriodFunctionMigrationAnalysis(
                periodCapacity,
                List.copyOf(
                        migrations
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Source Function
     * =================================================
     */

    private DecadeFunctionMigration
    analyzeFunction(

            DayMasterElementRelationAnalysis
                    elementRelationAnalysis,

            DecadeFunctionCapacityContext
                    capacityContext,

            boolean completeChart

    ) {

        DecadeFunctionOperation operation =
                capacityContext
                        .operation();

        List<DecadeFunctionMigrationTarget>
                targets =
                new ArrayList<>();

        /*
         * =================================================
         * distinct 사용.
         *
         * 지장간 여러 개에 동일 Branch Transformation이
         * 연결되어도 Transformation 하나를
         * 여러 Target으로 중복 생성하지 않는다.
         * =================================================
         */

        for (
                DecadeLuckNatalTransformationReference transformation
                : operation
                .distinctTransformationContexts()
        ) {

            /*
             * 목표 오행 비활성.
             *
             * 원기능 감소 Context는 Operation에 남지만
             * 새 Target Function은 만들지 않는다.
             */
            if (
                    !transformation
                            .activated()
            ) {

                continue;
            }

            List<DecadeFunctionInputOperation>
                    affectedInputs =
                    operation
                            .inputOperations()
                            .stream()
                            .filter(
                                    input ->
                                            input
                                                    .transformationContexts()
                                                    .contains(
                                                            transformation
                                                    )
                            )
                            .toList();

            if (
                    affectedInputs
                            .isEmpty()
            ) {

                throw new IllegalStateException(
                        "Function Operation에 연결된 Transformation인데 "
                                + "실제 Source Input을 찾을 수 없습니다. code="
                                + transformation.transformationCode()
                );
            }

            DayMasterElementRole targetRole =
                    resolveTargetRole(
                            elementRelationAnalysis,
                            transformation.targetElement()
                    );

            NatalTenGodFunctionType targetFunctionType =
                    DecadeFunctionMigrationTarget
                            .functionTypeOf(
                                    targetRole
                            );

            targets.add(
                    new DecadeFunctionMigrationTarget(
                            transformation,
                            affectedInputs,
                            targetRole,
                            targetFunctionType
                    )
            );
        }

        return new DecadeFunctionMigration(
                capacityContext,
                List.copyOf(
                        targets
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Canonical Target Element → Role
     * =================================================
     *
     * 상생상극 재계산 금지.
     *
     * 4-1 DayMasterElementRelationAnalysis의
     * elementByRole만 사용한다.
     */

    private DayMasterElementRole resolveTargetRole(

            DayMasterElementRelationAnalysis
                    elementRelationAnalysis,

            FiveElement targetElement

    ) {

        Objects.requireNonNull(
                targetElement,
                "Transformation 목표 오행이 필요합니다."
        );

        DayMasterElementRole resolved =
                null;

        int matchCount =
                0;

        for (
                Map.Entry<
                        DayMasterElementRole,
                        FiveElement
                        > entry
                : elementRelationAnalysis
                .elementByRole()
                .entrySet()
        ) {

            if (
                    entry.getValue()
                            != targetElement
            ) {

                continue;
            }

            resolved =
                    entry.getKey();

            matchCount++;
        }

        if (
                matchCount != 1
                        || resolved == null
        ) {

            throw new IllegalStateException(
                    "Transformation 목표 오행을 "
                            + "일간 기준 역할 하나로 확정할 수 없습니다. "
                            + "targetElement="
                            + targetElement
                            + ", matchCount="
                            + matchCount
            );
        }

        return resolved;
    }
}