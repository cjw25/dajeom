package com.dajeom.model;

import com.dajeom.type.AnnualDecadeLuckRelationLayer;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 14-5 대운 × 세운 Transformation 하나에 대해
 * 실제 Functional Source Participant를 연결한 결과.
 *
 * =================================================
 *
 * HEAVENLY_STEM
 *
 * 대운 천간 Input
 * +
 * 세운 천간 Input
 *
 * =================================================
 *
 * EARTHLY_BRANCH
 *
 * 대운 지지의 모든 지장간 Input
 * +
 * 세운 지지의 모든 지장간 Input
 *
 * =================================================
 *
 * 중요:
 *
 * 지지 Transformation에 여러 지장간 Function이
 * Source Participant로 연결되더라도
 *
 * Transformation 자체는 하나다.
 *
 * =================================================
 *
 * 또한 지지 Transformation에 연결된
 * 모든 지장간 Function이 실제로 동일한 정도로
 * 변형되었다는 뜻이 아니다.
 *
 * 현재 14-5 Rule이 제공하는
 * candidate-level OriginalFunctionState를
 * 해당 지지 전체의 구조적 Context로 보존한다.
 *
 * =================================================
 *
 * 이 객체는:
 *
 * - Source Function
 * - Target Function
 * - Original Function State
 *
 * 를 연결할 뿐,
 *
 * 길흉이나 최종 Function 강도를 결정하지 않는다.
 */
public record AnnualDecadeTransformationParticipantResolution(

        AnnualDecadeLuckTransformationReference
        sourceTransformation,

        List<DecadeFunctionInputOperation>
        decadeSourceInputs,

        List<AnnualFunctionInputOperation>
        annualSourceInputs,

        List<NatalTenGodFunctionType>
        decadeSourceFunctions,

        List<NatalTenGodFunctionType>
        annualSourceFunctions,

        DayMasterElementRole targetRole,

        NatalTenGodFunctionType targetFunctionType,

        boolean completeChart

) {

    public AnnualDecadeTransformationParticipantResolution {

        Objects.requireNonNull(
                sourceTransformation,
                "Annual-Decade Transformation Participant Resolution에는 Transformation이 필요합니다."
        );

        decadeSourceInputs =
                decadeSourceInputs == null
                        ? List.of()
                        : List.copyOf(
                        decadeSourceInputs
                );

        annualSourceInputs =
                annualSourceInputs == null
                        ? List.of()
                        : List.copyOf(
                        annualSourceInputs
                );

        decadeSourceFunctions =
                decadeSourceFunctions == null
                        ? List.of()
                        : List.copyOf(
                        decadeSourceFunctions
                );

        annualSourceFunctions =
                annualSourceFunctions == null
                        ? List.of()
                        : List.copyOf(
                        annualSourceFunctions
                );

        Objects.requireNonNull(
                targetRole,
                "Annual-Decade Transformation에는 Target Role이 필요합니다."
        );

        Objects.requireNonNull(
                targetFunctionType,
                "Annual-Decade Transformation에는 Target Function이 필요합니다."
        );

        if (
                decadeSourceInputs.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Transformation에는 최소 하나의 대운 Source Input이 필요합니다."
            );
        }

        if (
                annualSourceInputs.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Transformation에는 최소 하나의 세운 Source Input이 필요합니다."
            );
        }

        if (
                decadeSourceFunctions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Transformation에는 최소 하나의 대운 Source Function이 필요합니다."
            );
        }

        if (
                annualSourceFunctions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Transformation에는 최소 하나의 세운 Source Function이 필요합니다."
            );
        }

        /*
         * =================================================
         * Target Role ↔ Function
         * =================================================
         */

        NatalTenGodFunctionType expectedTarget =
                AnnualFunctionMigrationTarget
                        .functionTypeOf(
                                targetRole
                        );

        if (
                targetFunctionType
                        != expectedTarget
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Transformation Target Role과 Function이 일치하지 않습니다. "
                            + "role="
                            + targetRole
                            + ", expected="
                            + expectedTarget
                            + ", actual="
                            + targetFunctionType
            );
        }

        /*
         * =================================================
         * Layer
         * =================================================
         */

        boolean heavenly =
                sourceTransformation.layer()
                        == AnnualDecadeLuckRelationLayer
                        .HEAVENLY_STEM;

        if (
                heavenly
        ) {

            if (
                    decadeSourceInputs.size()
                            != 1
            ) {

                throw new IllegalArgumentException(
                        "천간 대운-세운 Transformation에는 정확히 하나의 대운 Visible Input이 필요합니다."
                );
            }

            if (
                    annualSourceInputs.size()
                            != 1
            ) {

                throw new IllegalArgumentException(
                        "천간 대운-세운 Transformation에는 정확히 하나의 세운 Visible Input이 필요합니다."
                );
            }

            if (
                    !decadeSourceInputs
                            .get(0)
                            .visible()
            ) {

                throw new IllegalArgumentException(
                        "천간 Transformation의 대운 Source가 Visible Input이 아닙니다."
                );
            }

            if (
                    !annualSourceInputs
                            .get(0)
                            .visible()
            ) {

                throw new IllegalArgumentException(
                        "천간 Transformation의 세운 Source가 Visible Input이 아닙니다."
                );
            }

        } else {

            if (
                    decadeSourceInputs
                            .stream()
                            .anyMatch(
                                    input ->
                                            !input.hidden()
                            )
            ) {

                throw new IllegalArgumentException(
                        "지지 Transformation의 대운 Source에는 Hidden Input만 연결할 수 있습니다."
                );
            }

            if (
                    annualSourceInputs
                            .stream()
                            .anyMatch(
                                    input ->
                                            !input.hidden()
                            )
            ) {

                throw new IllegalArgumentException(
                        "지지 Transformation의 세운 Source에는 Hidden Input만 연결할 수 있습니다."
                );
            }
        }

        /*
         * =================================================
         * Function Coverage
         * =================================================
         */

        Set<NatalTenGodFunctionType> actualDecadeFunctions =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                DecadeFunctionInputOperation input
                : decadeSourceInputs
        ) {

            Objects.requireNonNull(
                    input,
                    "대운 Transformation Source Input은 null일 수 없습니다."
            );

            actualDecadeFunctions.add(
                    functionOf(
                            input.tenGodType()
                    )
            );
        }

        Set<NatalTenGodFunctionType> declaredDecadeFunctions =
                decadeSourceFunctions.isEmpty()
                        ? EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                )
                        : EnumSet.copyOf(
                        decadeSourceFunctions
                );

        if (
                !actualDecadeFunctions.equals(
                        declaredDecadeFunctions
                )
        ) {

            throw new IllegalArgumentException(
                    "대운 Transformation Source Function과 실제 Input Coverage가 일치하지 않습니다. "
                            + "actual="
                            + actualDecadeFunctions
                            + ", declared="
                            + declaredDecadeFunctions
            );
        }

        Set<NatalTenGodFunctionType> actualAnnualFunctions =
                EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                );

        for (
                AnnualFunctionInputOperation input
                : annualSourceInputs
        ) {

            Objects.requireNonNull(
                    input,
                    "세운 Transformation Source Input은 null일 수 없습니다."
            );

            actualAnnualFunctions.add(
                    functionOf(
                            input.tenGodType()
                    )
            );
        }

        Set<NatalTenGodFunctionType> declaredAnnualFunctions =
                annualSourceFunctions.isEmpty()
                        ? EnumSet.noneOf(
                        NatalTenGodFunctionType.class
                )
                        : EnumSet.copyOf(
                        annualSourceFunctions
                );

        if (
                !actualAnnualFunctions.equals(
                        declaredAnnualFunctions
                )
        ) {

            throw new IllegalArgumentException(
                    "세운 Transformation Source Function과 실제 Input Coverage가 일치하지 않습니다. "
                            + "actual="
                            + actualAnnualFunctions
                            + ", declared="
                            + declaredAnnualFunctions
            );
        }
    }

    /*
     * =================================================
     * Transformation
     * =================================================
     */

    public AnnualDecadeLuckRelationLayer layer() {

        return sourceTransformation
                .layer();
    }

    public boolean heavenlyStemTransformation() {

        return sourceTransformation
                .heavenlyStemTransformation();
    }

    public boolean earthlyBranchTransformation() {

        return sourceTransformation
                .earthlyBranchTransformation();
    }

    public boolean activated() {

        return sourceTransformation
                .activated();
    }

    public boolean strongTargetActivation() {

        return sourceTransformation
                .strongTargetActivation();
    }

    public TransformationTargetActivationState
    targetActivationState() {

        return sourceTransformation
                .targetActivationState();
    }

    /*
     * =================================================
     * Original Function State
     * =================================================
     */

    public TransformationOriginalFunctionState
    decadeOriginalFunctionState() {

        return sourceTransformation
                .decadeOriginalFunctionState();
    }

    public TransformationOriginalFunctionState
    annualOriginalFunctionState() {

        return sourceTransformation
                .annualOriginalFunctionState();
    }

    public boolean decadeOriginalFunctionReduced() {

        return sourceTransformation
                .decadeOriginalFunctionReduced();
    }

    public boolean decadeOriginalFunctionStronglyReduced() {

        return sourceTransformation
                .decadeOriginalFunctionStronglyReduced();
    }

    public boolean annualOriginalFunctionReduced() {

        return sourceTransformation
                .annualOriginalFunctionReduced();
    }

    public boolean annualOriginalFunctionStronglyReduced() {

        return sourceTransformation
                .annualOriginalFunctionStronglyReduced();
    }

    public boolean mixedParticipantOriginalFunctionState() {

        return sourceTransformation
                .mixedParticipantOriginalFunctionState();
    }

    /*
     * =================================================
     * Source Function
     * =================================================
     */

    public boolean decadeSourceIncludes(
            NatalTenGodFunctionType functionType
    ) {

        return functionType != null
                && decadeSourceFunctions
                .contains(
                        functionType
                );
    }

    public boolean annualSourceIncludes(
            NatalTenGodFunctionType functionType
    ) {

        return functionType != null
                && annualSourceFunctions
                .contains(
                        functionType
                );
    }

    public boolean sameFunctionOnBothSourceLayers(
            NatalTenGodFunctionType functionType
    ) {

        return decadeSourceIncludes(
                functionType
        )
                && annualSourceIncludes(
                functionType
        );
    }

    public boolean crossFunctionSourceExists() {

        return decadeSourceFunctions
                .stream()
                .anyMatch(
                        decadeFunction ->
                                annualSourceFunctions
                                        .stream()
                                        .anyMatch(
                                                annualFunction ->
                                                        annualFunction
                                                                != decadeFunction
                                        )
                );
    }

    public boolean targets(
            NatalTenGodFunctionType functionType
    ) {

        return functionType != null
                && targetFunctionType
                == functionType;
    }

    /*
     * =================================================
     * Function Resolver
     * =================================================
     */

    private static NatalTenGodFunctionType functionOf(
            com.dajeom.type.StructureTenGodType tenGodType
    ) {

        Objects.requireNonNull(
                tenGodType,
                "Source TenGodType이 필요합니다."
        );

        NatalTenGodFunctionType resolved =
                null;

        int matches =
                0;

        for (
                NatalTenGodFunctionType functionType
                : NatalTenGodFunctionType.values()
        ) {

            if (
                    !functionType
                            .contains(
                                    tenGodType
                            )
            ) {

                continue;
            }

            resolved =
                    functionType;

            matches++;
        }

        if (
                matches != 1
                        || resolved == null
        ) {

            throw new IllegalStateException(
                    "TenGodType을 Functional Family 하나로 확정할 수 없습니다. tenGod="
                            + tenGodType
                            + ", matches="
                            + matches
            );
        }

        return resolved;
    }
}