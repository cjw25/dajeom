package com.dajeom.engine;

import com.dajeom.model.DecadeFunctionEffect;
import com.dajeom.model.DecadeFunctionInputOperation;
import com.dajeom.model.DecadeFunctionOperation;
import com.dajeom.model.DecadeFunctionalAnalysis;
import com.dajeom.model.DecadeFunctionalOperationAnalysis;
import com.dajeom.model.DecadeLuckNatalRelationReference;
import com.dajeom.model.DecadeLuckNatalTransformationReference;
import com.dajeom.model.DecadeLuckTenGodEvidence;
import com.dajeom.model.DecadePeriodFunctionalAnalysis;
import com.dajeom.model.DecadePeriodFunctionalOperationAnalysis;
import com.dajeom.type.DecadeLuckNatalRelationLayer;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DecadeFunctionalAnalysis에
 * 관계 / Transformation 작동 Context를
 * 연결하는 Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * DecadeFunctionalAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * Period별 5개 Function Operation.
 *
 * =================================================
 *
 * 연결 원칙:
 *
 * 대운 천간 Input
 * ->
 * HEAVENLY_STEM Relation
 * +
 * HEAVENLY_STEM Transformation
 *
 * 대운 지지 지장간 Input
 * ->
 * EARTHLY_BRANCH Relation
 * +
 * EARTHLY_BRANCH Transformation
 *
 * =================================================
 *
 * 중요:
 *
 * 하나의 대운 지지에:
 *
 * 본기
 * 중기
 * 여기
 *
 * 여러 십성이 존재할 수 있으므로
 * 같은 Branch Relation/Transformation Reference가
 * 여러 Function Input에 연결될 수 있다.
 *
 * 이것은 독립 Evidence가 여러 개라는 뜻이 아니다.
 *
 * 그래서 Function Aggregate에서는
 * distinct Relation / Transformation helper를 제공한다.
 *
 * =================================================
 *
 * 이 Engine이 하지 않는 것:
 *
 * - 합 = 강화
 * - 충 = 약화
 * - 형 = 악화
 * - 용신 = 호재
 * - 대운 길흉
 * - 신강신약 재계산
 * - Transformation Target을 새 십성으로 변환
 * - Natal Cross Pattern 활성 확정
 */
@Component
public class DecadeFunctionalOperationEngine {

    public DecadeFunctionalOperationAnalysis analyze(

            DecadeFunctionalAnalysis
                    functionalAnalysis

    ) {

        Objects.requireNonNull(
                functionalAnalysis,
                "DecadeFunctionalOperationEngine에는 DecadeFunctionalAnalysis가 필요합니다."
        );

        boolean completeChart =
                functionalAnalysis
                        .completeChart();

        /*
         * =================================================
         * 대운 자체가 unavailable
         * =================================================
         */

        if (
                !functionalAnalysis
                        .analysisAvailable()
        ) {

            return new DecadeFunctionalOperationAnalysis(
                    functionalAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<DecadePeriodFunctionalOperationAnalysis>
                periodOperations =
                new ArrayList<>();

        for (
                DecadePeriodFunctionalAnalysis periodAnalysis
                : functionalAnalysis.periodAnalyses()
        ) {

            periodOperations.add(
                    analyzePeriod(
                            periodAnalysis,
                            completeChart
                    )
            );
        }

        return new DecadeFunctionalOperationAnalysis(
                functionalAnalysis,
                List.copyOf(
                        periodOperations
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Period
     * =================================================
     */

    private DecadePeriodFunctionalOperationAnalysis
    analyzePeriod(

            DecadePeriodFunctionalAnalysis
                    periodAnalysis,

            boolean completeChart

    ) {

        List<DecadeLuckNatalRelationReference>
                stemRelations =
                periodAnalysis
                        .natalRelationAnalysis()
                        .relations()
                        .stream()
                        .filter(
                                relation ->
                                        relation.layer()
                                                == DecadeLuckNatalRelationLayer
                                                .HEAVENLY_STEM
                        )
                        .toList();

        List<DecadeLuckNatalRelationReference>
                branchRelations =
                periodAnalysis
                        .natalRelationAnalysis()
                        .relations()
                        .stream()
                        .filter(
                                relation ->
                                        relation.layer()
                                                == DecadeLuckNatalRelationLayer
                                                .EARTHLY_BRANCH
                        )
                        .toList();

        List<DecadeLuckNatalTransformationReference>
                stemTransformations =
                periodAnalysis
                        .natalRelationAnalysis()
                        .transformations()
                        .stream()
                        .filter(
                                transformation ->
                                        transformation.layer()
                                                == DecadeLuckNatalRelationLayer
                                                .HEAVENLY_STEM
                        )
                        .toList();

        List<DecadeLuckNatalTransformationReference>
                branchTransformations =
                periodAnalysis
                        .natalRelationAnalysis()
                        .transformations()
                        .stream()
                        .filter(
                                transformation ->
                                        transformation.layer()
                                                == DecadeLuckNatalRelationLayer
                                                .EARTHLY_BRANCH
                        )
                        .toList();

        List<DecadeFunctionOperation>
                functionOperations =
                new ArrayList<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            DecadeFunctionEffect sourceEffect =
                    periodAnalysis
                            .requireEffect(
                                    functionType
                            );

            functionOperations.add(
                    createFunctionOperation(
                            sourceEffect,
                            stemRelations,
                            branchRelations,
                            stemTransformations,
                            branchTransformations,
                            completeChart
                    )
            );
        }

        return new DecadePeriodFunctionalOperationAnalysis(
                periodAnalysis,
                List.copyOf(
                        functionOperations
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Function
     * =================================================
     */

    private DecadeFunctionOperation
    createFunctionOperation(

            DecadeFunctionEffect sourceEffect,

            List<DecadeLuckNatalRelationReference>
                    stemRelations,

            List<DecadeLuckNatalRelationReference>
                    branchRelations,

            List<DecadeLuckNatalTransformationReference>
                    stemTransformations,

            List<DecadeLuckNatalTransformationReference>
                    branchTransformations,

            boolean completeChart

    ) {

        List<DecadeFunctionInputOperation>
                inputOperations =
                new ArrayList<>();

        for (
                DecadeLuckTenGodEvidence input
                : sourceEffect.directTenGodEvidences()
        ) {

            if (
                    input.visible()
            ) {

                inputOperations.add(
                        new DecadeFunctionInputOperation(
                                input,
                                stemRelations,
                                stemTransformations
                        )
                );

                continue;
            }

            inputOperations.add(
                    new DecadeFunctionInputOperation(
                            input,
                            branchRelations,
                            branchTransformations
                    )
            );
        }

        return new DecadeFunctionOperation(
                sourceEffect,
                List.copyOf(
                        inputOperations
                ),
                completeChart
        );
    }
}