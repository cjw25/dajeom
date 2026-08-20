package com.dajeom.engine;

import com.dajeom.model.AnnualFunctionEffect;
import com.dajeom.model.AnnualFunctionInputOperation;
import com.dajeom.model.AnnualFunctionalProjectionAnalysis;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckNatalRelationReference;
import com.dajeom.model.AnnualLuckNatalTransformationReference;
import com.dajeom.model.AnnualLuckTenGodEvidence;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.model.NatalTenGodFamilyEffect;
import com.dajeom.type.AnnualLuckNatalRelationLayer;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 14-3 Annual TenGod를
 * Functional 5축으로 투영하고,
 *
 * 각 직접 Input에
 * 같은 Layer의 14-4 Natal Relation /
 * Transformation Context를 연결한다.
 *
 * =================================================
 *
 * 입력:
 *
 * NatalFunctionalAnalysis
 *
 * +
 *
 * AnnualLuckAnalysis
 *
 * =================================================
 *
 * 사용하는 14차:
 *
 * 14-3
 * AnnualLuckTenGodAnalysis
 *
 * 14-4
 * AnnualLuckNatalRelationAnalysis
 *
 * =================================================
 *
 * 아직 사용하지 않는 것:
 *
 * 14-5
 * Decade × Annual Interaction
 *
 * 14-6
 * Annual Context 최종 결론
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 점수 없음
 * - 길흉 없음
 * - 신강신약 재계산 없음
 * - 용신 재선정 없음
 * - 원국 Pattern 재계산 없음
 * - 대운 효과 합산 없음
 *
 * =================================================
 *
 * Function mapping:
 *
 * StructureTenGodType
 *
 * ↓
 *
 * 기존 NatalTenGodFunctionType.contains(...)
 *
 * 를 사용한다.
 *
 * 새로운 십성 Family 공식을
 * 여기서 만들지 않는다.
 */
@Component
public class AnnualFunctionalProjectionEngine {

    public AnnualFunctionalProjectionAnalysis analyze(

            NatalFunctionalAnalysis natalFunctionalAnalysis,

            AnnualLuckAnalysis annualLuckAnalysis

    ) {

        Objects.requireNonNull(
                natalFunctionalAnalysis,
                "Annual Functional Projection에는 NatalFunctionalAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "Annual Functional Projection에는 AnnualLuckAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 동일 일간
         * =================================================
         */

        if (
                natalFunctionalAnalysis.dayMaster()
                        != annualLuckAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "Natal Functional Analysis와 AnnualLuckAnalysis의 일간이 일치하지 않습니다."
            );
        }

        boolean completeChart =
                natalFunctionalAnalysis
                        .completeChart();

        if (
                annualLuckAnalysis
                        .natalAnalysis()
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Natal Functional Analysis와 AnnualLuckAnalysis의 completeChart가 일치하지 않습니다."
            );
        }

        List<AnnualFunctionEffect> functionEffects =
                new ArrayList<>();

        /*
         * =================================================
         * 5 Function
         * =================================================
         */

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            NatalTenGodFamilyEffect natalFunctionEffect =
                    natalFunctionalAnalysis
                            .requireFunction(
                                    functionType
                            );

            List<AnnualFunctionInputOperation>
                    inputOperations =
                    new ArrayList<>();

            for (
                    AnnualLuckTenGodEvidence evidence
                    : annualLuckAnalysis
                    .tenGodAnalysis()
                    .evidences()
            ) {

                if (
                        !functionType
                                .contains(
                                        evidence.tenGodType()
                                )
                ) {

                    continue;
                }

                inputOperations.add(
                        buildInputOperation(
                                annualLuckAnalysis,
                                evidence,
                                completeChart
                        )
                );
            }

            functionEffects.add(
                    new AnnualFunctionEffect(
                            annualLuckAnalysis,
                            natalFunctionEffect,
                            functionType,
                            List.copyOf(
                                    inputOperations
                            ),
                            completeChart
                    )
            );
        }

        return new AnnualFunctionalProjectionAnalysis(
                natalFunctionalAnalysis,
                annualLuckAnalysis,
                List.copyOf(
                        functionEffects
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Input Operation
     * =================================================
     */

    private AnnualFunctionInputOperation
    buildInputOperation(

            AnnualLuckAnalysis annualLuckAnalysis,

            AnnualLuckTenGodEvidence evidence,

            boolean completeChart

    ) {

        AnnualLuckNatalRelationLayer layer =
                evidence.visible()
                        ? AnnualLuckNatalRelationLayer
                        .HEAVENLY_STEM
                        : AnnualLuckNatalRelationLayer
                        .EARTHLY_BRANCH;

        /*
         * =================================================
         * 14-4 같은 Layer 관계
         * =================================================
         *
         * 세운 천간 Input에는
         * 세운 천간 × 원국 천간 관계.
         *
         * 세운 지지의 모든 지장간 Input에는
         * 세운 지지 × 원국 지지 관계.
         */

        List<AnnualLuckNatalRelationReference> relations =
                annualLuckAnalysis
                        .natalRelationAnalysis()
                        .relations()
                        .stream()
                        .filter(
                                relation ->
                                        relation.layer()
                                                == layer
                        )
                        .toList();

        /*
         * =================================================
         * 14-4 같은 Layer Transformation
         * =================================================
         */

        List<AnnualLuckNatalTransformationReference> transformations =
                annualLuckAnalysis
                        .natalRelationAnalysis()
                        .transformations()
                        .stream()
                        .filter(
                                transformation ->
                                        transformation.layer()
                                                == layer
                        )
                        .toList();

        return new AnnualFunctionInputOperation(
                evidence,
                relations,
                transformations,
                completeChart
        );
    }
}