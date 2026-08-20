package com.dajeom.engine;

import com.dajeom.model.DecadeFunctionCapacityContext;
import com.dajeom.model.DecadeFunctionOperation;
import com.dajeom.model.DecadeFunctionalCapacityAnalysis;
import com.dajeom.model.DecadeFunctionalOperationAnalysis;
import com.dajeom.model.DecadePeriodFunctionalCapacityAnalysis;
import com.dajeom.model.DecadePeriodFunctionalOperationAnalysis;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.model.NatalTenGodFamilyEffect;
import com.dajeom.type.DecadeCoreCapacitySide;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 원국의 고정된 CoreSelf Capacity와
 * 대운 Function Operation을 연결한다.
 *
 * =================================================
 *
 * 입력:
 *
 * NatalFunctionalAnalysis
 *
 * +
 *
 * DecadeFunctionalOperationAnalysis
 *
 * =================================================
 *
 * 원칙:
 *
 * NatalCoreSelfEffect는
 * 대운마다 재계산하지 않는다.
 *
 * 원국에서 이미 확정된:
 *
 * - strengthLevel
 * - strengthBasisPoints
 * - supportPower
 * - pressurePower
 * - extreme
 * - followStructureReviewRequired
 *
 * 를 그대로 Anchor로 사용한다.
 *
 * =================================================
 *
 * 여기서 하는 것:
 *
 * Function Family를
 *
 * SUPPORT_SIDE
 * 또는
 * PRESSURE_SIDE
 *
 * 로 연결한다.
 *
 * =================================================
 *
 * 여기서 하지 않는 것:
 *
 * - 용신 판정
 * - 길흉 판정
 * - 신강/신약 재계산
 * - 대운 때문에 신강이 신약으로 변했다고 재분류
 * - 점수 가감
 * - 사건 생성
 */
@Component
public class DecadeFunctionalCapacityAnalysisEngine {

    public DecadeFunctionalCapacityAnalysis analyze(

            NatalFunctionalAnalysis
                    natalFunctionalAnalysis,

            DecadeFunctionalOperationAnalysis
                    operationAnalysis

    ) {

        Objects.requireNonNull(
                natalFunctionalAnalysis,
                "Capacity Analysis에는 NatalFunctionalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                operationAnalysis,
                "Capacity Analysis에는 DecadeFunctionalOperationAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 동일 원국 검증
         * =================================================
         */

        if (
                natalFunctionalAnalysis.dayMaster()
                        != operationAnalysis
                        .functionalAnalysis()
                        .decadeLuckAnalysis()
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "원국 Functional Analysis와 대운 Operation Analysis의 일간이 일치하지 않습니다."
            );
        }

        boolean completeChart =
                natalFunctionalAnalysis
                        .completeChart();

        if (
                operationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 Functional Analysis와 대운 Operation Analysis의 completeChart가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 대운 unavailable
         * =================================================
         */

        if (
                !operationAnalysis
                        .analysisAvailable()
        ) {

            return new DecadeFunctionalCapacityAnalysis(
                    natalFunctionalAnalysis,
                    operationAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<DecadePeriodFunctionalCapacityAnalysis>
                periodAnalyses =
                new ArrayList<>();

        for (
                DecadePeriodFunctionalOperationAnalysis periodOperation
                : operationAnalysis.periodAnalyses()
        ) {

            periodAnalyses.add(
                    analyzePeriod(
                            natalFunctionalAnalysis,
                            periodOperation,
                            completeChart
                    )
            );
        }

        return new DecadeFunctionalCapacityAnalysis(
                natalFunctionalAnalysis,
                operationAnalysis,
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

    private DecadePeriodFunctionalCapacityAnalysis
    analyzePeriod(

            NatalFunctionalAnalysis
                    natalFunctionalAnalysis,

            DecadePeriodFunctionalOperationAnalysis
                    operationAnalysis,

            boolean completeChart

    ) {

        List<DecadeFunctionCapacityContext>
                capacityContexts =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            NatalTenGodFamilyEffect natalFunctionEffect =
                    natalFunctionalAnalysis
                            .requireFunction(
                                    functionType
                            );

            DecadeFunctionOperation operation =
                    operationAnalysis
                            .requireOperation(
                                    functionType
                            );

            DecadeCoreCapacitySide capacitySide =
                    DecadeCoreCapacitySide
                            .from(
                                    functionType
                            );

            capacityContexts.add(
                    new DecadeFunctionCapacityContext(
                            natalFunctionalAnalysis
                                    .coreSelf(),

                            natalFunctionEffect,

                            operation,

                            capacitySide,

                            completeChart
                    )
            );
        }

        return new DecadePeriodFunctionalCapacityAnalysis(
                natalFunctionalAnalysis,
                operationAnalysis,
                List.copyOf(
                        capacityContexts
                ),
                completeChart
        );
    }
}