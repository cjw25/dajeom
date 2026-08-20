package com.dajeom.engine;

import com.dajeom.model.AnnualFunctionCapacityContext;
import com.dajeom.model.AnnualFunctionEffect;
import com.dajeom.model.AnnualFunctionInputOperation;
import com.dajeom.model.AnnualFunctionMigration;
import com.dajeom.model.AnnualFunctionMigrationTarget;
import com.dajeom.model.AnnualFunctionalCapacityAnalysis;
import com.dajeom.model.AnnualFunctionalProjectionAnalysis;
import com.dajeom.model.AnnualLuckNatalTransformationReference;
import com.dajeom.model.DayMasterElementRelationAnalysis;
import com.dajeom.model.NatalCoreSelfEffect;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.model.NatalTenGodFamilyEffect;
import com.dajeom.type.AnnualCoreCapacitySide;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 세운 Transformation Target을
 * Functional Family로 연결하고,
 *
 * 원국의 고정 DayMaster Capacity와
 * 접촉시킨다.
 *
 * =================================================
 *
 * 입력:
 *
 * DayMasterElementRelationAnalysis
 *
 * +
 *
 * AnnualFunctionalProjectionAnalysis
 *
 * =================================================
 *
 * 처리:
 *
 * 1.
 * 5개 Source Function별
 * 활성 Transformation Target 확인
 *
 * 2.
 * target FiveElement
 * ->
 * DayMasterElementRole
 * ->
 * NatalTenGodFunctionType
 *
 * 3.
 * Source Function과 Target Function을
 * 동시에 보존
 *
 * 4.
 * Target Function 기준으로
 * Incoming Migration 구성
 *
 * 5.
 * 고정 NatalCoreSelfEffect와
 * Function Capacity 연결
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 일간 강약 재계산
 * - 용신 재선정
 * - 대운×세운 결합
 * - 세운 총점
 * - 길흉
 * - 사건 예측
 */
@Component
public class AnnualFunctionalCapacityAnalysisEngine {

    public AnnualFunctionalCapacityAnalysis analyze(

            DayMasterElementRelationAnalysis
                    elementRelationAnalysis,

            AnnualFunctionalProjectionAnalysis
                    projectionAnalysis

    ) {

        Objects.requireNonNull(
                elementRelationAnalysis,
                "Annual Functional Capacity에는 DayMasterElementRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                projectionAnalysis,
                "Annual Functional Capacity에는 AnnualFunctionalProjectionAnalysis가 필요합니다."
        );

        NatalFunctionalAnalysis natal =
                projectionAnalysis
                        .natalFunctionalAnalysis();

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
                projectionAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Projection과 Natal Functional Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 1차:
         * Source Function별 Migration 생성
         * =================================================
         */

        Map<
                NatalTenGodFunctionType,
                AnnualFunctionMigration
                > migrationsByFunction =
                new EnumMap<>(
                        NatalTenGodFunctionType.class
                );

        List<AnnualFunctionMigration> migrations =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            AnnualFunctionEffect sourceEffect =
                    projectionAnalysis
                            .requireEffect(
                                    functionType
                            );

            AnnualFunctionMigration migration =
                    buildMigration(
                            elementRelationAnalysis,
                            sourceEffect,
                            completeChart
                    );

            migrations.add(
                    migration
            );

            migrationsByFunction.put(
                    functionType,
                    migration
            );
        }

        /*
         * =================================================
         * 2차:
         * Target 기준 Incoming 구성
         *
         * 모든 Migration이 먼저 생성돼야
         * 다른 Function에서 현재 Function으로
         * 들어오는 Target을 찾을 수 있다.
         * =================================================
         */

        NatalCoreSelfEffect coreSelf =
                natal.coreSelf();

        List<AnnualFunctionCapacityContext> capacityContexts =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            AnnualFunctionMigration sourceMigration =
                    migrationsByFunction
                            .get(
                                    functionType
                            );

            if (
                    sourceMigration == null
            ) {

                throw new IllegalStateException(
                        "Annual Source Migration이 누락되었습니다: "
                                + functionType
                );
            }

            List<AnnualFunctionMigration> incomingMigrations =
                    migrations
                            .stream()
                            .filter(
                                    migration ->
                                            migration.targets(
                                                    functionType
                                            )
                            )
                            .toList();

            NatalTenGodFamilyEffect natalFunctionEffect =
                    natal.requireFunction(
                            functionType
                    );

            AnnualCoreCapacitySide capacitySide =
                    AnnualCoreCapacitySide.from(
                            functionType
                    );

            capacityContexts.add(
                    new AnnualFunctionCapacityContext(
                            coreSelf,
                            natalFunctionEffect,
                            sourceMigration,
                            incomingMigrations,
                            capacitySide,
                            completeChart
                    )
            );
        }

        return new AnnualFunctionalCapacityAnalysis(
                elementRelationAnalysis,
                projectionAnalysis,
                List.copyOf(
                        migrations
                ),
                List.copyOf(
                        capacityContexts
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Source Function Migration
     * =================================================
     */

    private AnnualFunctionMigration buildMigration(

            DayMasterElementRelationAnalysis
                    elementRelationAnalysis,

            AnnualFunctionEffect sourceEffect,

            boolean completeChart

    ) {

        List<AnnualFunctionMigrationTarget> targets =
                new ArrayList<>();

        /*
         * 동일 Branch Transformation이
         * 여러 지장간 Input에 연결돼도
         * sourceEffect Aggregate에서는
         * 한 번만 처리한다.
         */
        for (
                AnnualLuckNatalTransformationReference transformation
                : sourceEffect
                .distinctTransformationContexts()
        ) {

            if (
                    !transformation
                            .activated()
            ) {

                continue;
            }

            List<AnnualFunctionInputOperation> affectedInputs =
                    sourceEffect
                            .inputOperations()
                            .stream()
                            .filter(
                                    input ->
                                            input
                                                    .natalTransformations()
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
                        "Annual Function Effect에 연결된 Transformation인데 "
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
                    AnnualFunctionMigrationTarget
                            .functionTypeOf(
                                    targetRole
                            );

            targets.add(
                    new AnnualFunctionMigrationTarget(
                            transformation,
                            affectedInputs,
                            targetRole,
                            targetFunctionType
                    )
            );
        }

        return new AnnualFunctionMigration(
                sourceEffect,
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
     * FiveElement 상생상극을
     * 여기서 다시 계산하지 않는다.
     *
     * 기존 4-1
     * DayMasterElementRelationAnalysis의
     * elementByRole을 사용한다.
     */

    private DayMasterElementRole resolveTargetRole(

            DayMasterElementRelationAnalysis
                    elementRelationAnalysis,

            FiveElement targetElement

    ) {

        Objects.requireNonNull(
                targetElement,
                "Annual Transformation Target Element가 필요합니다."
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
                    "Annual Transformation Target Element를 "
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