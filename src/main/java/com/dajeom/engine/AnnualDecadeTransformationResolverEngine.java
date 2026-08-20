package com.dajeom.engine;

import com.dajeom.model.AnnualDecadeFunctionalAnalysis;
import com.dajeom.model.AnnualDecadeFunctionalSegment;
import com.dajeom.model.AnnualDecadeLuckTransformationReference;
import com.dajeom.model.AnnualDecadeTransformationAnalysis;
import com.dajeom.model.AnnualDecadeTransformationParticipantResolution;
import com.dajeom.model.AnnualDecadeTransformationResolution;
import com.dajeom.model.AnnualFunctionEffect;
import com.dajeom.model.AnnualFunctionInputOperation;
import com.dajeom.model.AnnualFunctionMigrationTarget;
import com.dajeom.model.DecadeFunctionInputOperation;
import com.dajeom.model.DecadeResolvedFunctionEffect;
import com.dajeom.type.AnnualDecadeLuckRelationLayer;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.NatalTenGodFunctionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 14-5 Transformation에
 * 실제 대운 / 세운 Functional Source Participant를 연결한다.
 *
 * 기존 AnnualDecadeTransformationAnalysisEngine과
 * 계산 의미는 동일하다.
 *
 * =================================================
 *
 * HEAVENLY_STEM
 *
 * 대운 Visible Input
 * +
 * 세운 Visible Input
 *
 * =================================================
 *
 * EARTHLY_BRANCH
 *
 * 대운 Hidden Inputs
 * +
 * 세운 Hidden Inputs
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 합화 재계산 안 함
 * - Target Activation 재계산 안 함
 * - Original Function State 재계산 안 함
 * - Source Function 삭제 안 함
 * - 지지 Transformation 중복 생성 안 함
 *
 * 기존 14-5 typed provenance를
 * Functional Source와 연결만 한다.
 */
@Component
public class AnnualDecadeTransformationResolverEngine {

    public AnnualDecadeTransformationAnalysis analyze(

            AnnualDecadeFunctionalAnalysis functionalAnalysis

    ) {

        Objects.requireNonNull(
                functionalAnalysis,
                "Annual-Decade Transformation Analysis에는 AnnualDecadeFunctionalAnalysis가 필요합니다."
        );

        boolean completeChart =
                functionalAnalysis
                        .completeChart();

        if (
                !functionalAnalysis
                        .analysisAvailable()
        ) {

            return new AnnualDecadeTransformationAnalysis(
                    functionalAnalysis,
                    List.of(),
                    completeChart
            );
        }

        List<AnnualDecadeTransformationResolution>
                segmentResolutions =
                new ArrayList<>();

        for (
                AnnualDecadeFunctionalSegment segment
                : functionalAnalysis.segments()
        ) {

            segmentResolutions.add(
                    resolveSegment(
                            segment,
                            completeChart
                    )
            );
        }

        return new AnnualDecadeTransformationAnalysis(
                functionalAnalysis,
                List.copyOf(
                        segmentResolutions
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Segment
     * =================================================
     */

    private AnnualDecadeTransformationResolution
    resolveSegment(

            AnnualDecadeFunctionalSegment segment,

            boolean completeChart

    ) {

        List<AnnualDecadeTransformationParticipantResolution>
                resolutions =
                new ArrayList<>();

        for (
                AnnualDecadeLuckTransformationReference transformation
                : segment.crossLuckTransformations()
        ) {

            resolutions.add(
                    resolveTransformation(
                            segment,
                            transformation,
                            completeChart
                    )
            );
        }

        return new AnnualDecadeTransformationResolution(
                segment,
                List.copyOf(
                        resolutions
                ),
                completeChart
        );
    }

    /*
     * =================================================
     * Transformation
     * =================================================
     */

    private AnnualDecadeTransformationParticipantResolution
    resolveTransformation(

            AnnualDecadeFunctionalSegment segment,

            AnnualDecadeLuckTransformationReference transformation,

            boolean completeChart

    ) {

        Objects.requireNonNull(
                transformation,
                "14-5 Transformation이 필요합니다."
        );

        AnnualDecadeLuckRelationLayer layer =
                transformation
                        .layer();

        /*
         * =================================================
         * 대운 Source Inputs
         * =================================================
         */

        List<DecadeFunctionInputOperation>
                decadeSourceInputs =
                new ArrayList<>();

        Set<NatalTenGodFunctionType>
                decadeSourceFunctions =
                new LinkedHashSet<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            DecadeResolvedFunctionEffect decadeEffect =
                    segment
                            .decadeBaseline()
                            .requireFunctionEffect(
                                    functionType
                            );

            for (
                    DecadeFunctionInputOperation input
                    : decadeEffect
                    .sourceOperation()
                    .inputOperations()
            ) {

                if (
                        !matchesLayer(
                                layer,
                                input.visible()
                        )
                ) {

                    continue;
                }

                decadeSourceInputs.add(
                        input
                );

                decadeSourceFunctions.add(
                        functionType
                );
            }
        }

        /*
         * =================================================
         * 세운 Source Inputs
         * =================================================
         */

        List<AnnualFunctionInputOperation>
                annualSourceInputs =
                new ArrayList<>();

        Set<NatalTenGodFunctionType>
                annualSourceFunctions =
                new LinkedHashSet<>();

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            AnnualFunctionEffect annualEffect =
                    segment
                            .annualCapacityAnalysis()
                            .projectionAnalysis()
                            .requireEffect(
                                    functionType
                            );

            for (
                    AnnualFunctionInputOperation input
                    : annualEffect
                    .inputOperations()
            ) {

                if (
                        !matchesLayer(
                                layer,
                                input.visible()
                        )
                ) {

                    continue;
                }

                annualSourceInputs.add(
                        input
                );

                annualSourceFunctions.add(
                        functionType
                );
            }
        }

        /*
         * =================================================
         * Source Coverage
         * =================================================
         */

        if (
                decadeSourceInputs
                        .isEmpty()
        ) {

            throw new IllegalStateException(
                    "14-5 Transformation에 대응하는 대운 Source Input을 찾을 수 없습니다. "
                            + "sequence="
                            + segment.decadeSequenceNumber()
                            + ", code="
                            + transformation.transformationCode()
                            + ", layer="
                            + layer
            );
        }

        if (
                annualSourceInputs
                        .isEmpty()
        ) {

            throw new IllegalStateException(
                    "14-5 Transformation에 대응하는 세운 Source Input을 찾을 수 없습니다. "
                            + "year="
                            + segment.targetYear()
                            + ", code="
                            + transformation.transformationCode()
                            + ", layer="
                            + layer
            );
        }

        /*
         * =================================================
         * Target Function
         * =================================================
         */

        DayMasterElementRole targetRole =
                resolveTargetRole(
                        segment,
                        transformation
                                .targetElement()
                );

        NatalTenGodFunctionType targetFunction =
                AnnualFunctionMigrationTarget
                        .functionTypeOf(
                                targetRole
                        );

        return new AnnualDecadeTransformationParticipantResolution(
                transformation,
                List.copyOf(
                        decadeSourceInputs
                ),
                List.copyOf(
                        annualSourceInputs
                ),
                List.copyOf(
                        decadeSourceFunctions
                ),
                List.copyOf(
                        annualSourceFunctions
                ),
                targetRole,
                targetFunction,
                completeChart
        );
    }

    /*
     * =================================================
     * Layer
     * =================================================
     */

    private boolean matchesLayer(

            AnnualDecadeLuckRelationLayer layer,

            boolean visible

    ) {

        if (
                layer
                        == AnnualDecadeLuckRelationLayer
                        .HEAVENLY_STEM
        ) {

            return visible;
        }

        if (
                layer
                        == AnnualDecadeLuckRelationLayer
                        .EARTHLY_BRANCH
        ) {

            return !visible;
        }

        throw new IllegalStateException(
                "지원하지 않는 Annual-Decade Relation Layer입니다: "
                        + layer
        );
    }

    /*
     * =================================================
     * Target Element → DayMaster Role
     * =================================================
     */

    private DayMasterElementRole resolveTargetRole(

            AnnualDecadeFunctionalSegment segment,

            FiveElement targetElement

    ) {

        Objects.requireNonNull(
                targetElement,
                "14-5 Transformation Target Element가 필요합니다."
        );

        Map<
                DayMasterElementRole,
                FiveElement
                > elementByRole =
                segment
                        .annualCapacityAnalysis()
                        .elementRelationAnalysis()
                        .elementByRole();

        DayMasterElementRole result =
                null;

        int matches =
                0;

        for (
                Map.Entry<
                        DayMasterElementRole,
                        FiveElement
                        > entry
                : elementByRole.entrySet()
        ) {

            if (
                    entry.getValue()
                            != targetElement
            ) {

                continue;
            }

            result =
                    entry.getKey();

            matches++;
        }

        if (
                matches != 1
                        || result == null
        ) {

            throw new IllegalStateException(
                    "14-5 Transformation Target Element를 DayMaster Role 하나로 확정할 수 없습니다. "
                            + "targetElement="
                            + targetElement
                            + ", matches="
                            + matches
            );
        }

        return result;
    }
}