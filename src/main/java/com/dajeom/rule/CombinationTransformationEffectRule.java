package com.dajeom.rule;

import com.dajeom.model.EarthlyBranchCombinationIntegratedCandidate;
import com.dajeom.model.HeavenlyStemCombinationTransformationEvidence;
import com.dajeom.type.CombinationTransformationEffectReasonType;
import com.dajeom.type.EarthlyBranchCombinationIntegrationRole;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import com.dajeom.type.HeavenlyStemCombinationTransformationState;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CombinationTransformationEffectRule {

    public TransformationOriginalFunctionState
    determineHeavenlyOriginalFunctionState(
            HeavenlyStemCombinationTransformationEvidence evidence
    ) {

        Objects.requireNonNull(
                evidence
        );

        return determineHeavenlyOriginalFunctionState(
                evidence.state()
        );
    }

    public TransformationOriginalFunctionState
    determineHeavenlyOriginalFunctionState(
            HeavenlyStemCombinationTransformationState state
    ) {

        Objects.requireNonNull(
                state
        );

        return switch (
                state
                ) {

            case TRANSFORMED ->
                    TransformationOriginalFunctionState
                            .STRONGLY_REDUCED;

            case PARTIALLY_TRANSFORMED ->
                    TransformationOriginalFunctionState
                            .PARTIALLY_REDUCED;

            case CONDITIONAL ->
                    TransformationOriginalFunctionState
                            .MOSTLY_PRESERVED;

            case COMBINED_NOT_TRANSFORMED,
                 SUPPRESSED ->
                    TransformationOriginalFunctionState
                            .PRESERVED;
        };
    }

    public TransformationTargetActivationState
    determineHeavenlyTargetActivationState(
            HeavenlyStemCombinationTransformationEvidence evidence
    ) {

        Objects.requireNonNull(
                evidence
        );

        return determineHeavenlyTargetActivationState(
                evidence.state()
        );
    }

    public TransformationTargetActivationState
    determineHeavenlyTargetActivationState(
            HeavenlyStemCombinationTransformationState state
    ) {

        Objects.requireNonNull(
                state
        );

        return switch (
                state
                ) {

            case TRANSFORMED ->
                    TransformationTargetActivationState
                            .STRONG;

            case PARTIALLY_TRANSFORMED ->
                    TransformationTargetActivationState
                            .PARTIAL;

            case CONDITIONAL ->
                    TransformationTargetActivationState
                            .CONDITIONAL;

            case COMBINED_NOT_TRANSFORMED,
                 SUPPRESSED ->
                    TransformationTargetActivationState
                            .INACTIVE;
        };
    }

    public TransformationOriginalFunctionState
    determineEarthlyOriginalFunctionState(
            EarthlyBranchCombinationIntegratedCandidate candidate
    ) {

        Objects.requireNonNull(
                candidate
        );

        return determineEarthlyOriginalFunctionState(
                candidate.role(),
                candidate.obstructionLevel()
        );
    }

    public TransformationOriginalFunctionState
    determineEarthlyOriginalFunctionState(

            EarthlyBranchCombinationIntegrationRole role,

            EarthlyBranchCombinationObstructionLevel level

    ) {

        Objects.requireNonNull(
                role
        );

        Objects.requireNonNull(
                level
        );

        if (
                role.primary()
                        && (
                        level
                                == EarthlyBranchCombinationObstructionLevel.NONE

                                || level
                                == EarthlyBranchCombinationObstructionLevel.MINOR
                )
        ) {

            return TransformationOriginalFunctionState
                    .PARTIALLY_REDUCED;
        }

        if (
                role.primary()
                        || role.supporting()
        ) {

            return TransformationOriginalFunctionState
                    .MOSTLY_PRESERVED;
        }

        return TransformationOriginalFunctionState
                .PRESERVED;
    }

    public TransformationTargetActivationState
    determineEarthlyTargetActivationState(
            EarthlyBranchCombinationIntegratedCandidate candidate
    ) {

        Objects.requireNonNull(
                candidate
        );

        return determineEarthlyTargetActivationState(
                candidate.role(),
                candidate.baseMeaningful(),
                candidate.obstructionLevel(),
                candidate.baseCandidateRemains()
        );
    }

    public TransformationTargetActivationState
    determineEarthlyTargetActivationState(

            EarthlyBranchCombinationIntegrationRole role,

            boolean baseMeaningful,

            EarthlyBranchCombinationObstructionLevel level,

            boolean baseCandidateRemains

    ) {

        Objects.requireNonNull(
                role
        );

        Objects.requireNonNull(
                level
        );

        if (
                role.primary()
        ) {

            if (
                    baseMeaningful
                            && (
                            level
                                    == EarthlyBranchCombinationObstructionLevel.NONE

                                    || level
                                    == EarthlyBranchCombinationObstructionLevel.MINOR
                    )
            ) {

                return TransformationTargetActivationState
                        .STRONG;
            }

            return TransformationTargetActivationState
                    .PARTIAL;
        }

        if (
                role.supporting()
        ) {

            return baseMeaningful
                    ? TransformationTargetActivationState
                    .PARTIAL
                    : TransformationTargetActivationState
                    .CONDITIONAL;
        }

        if (
                role.competing()
        ) {

            return TransformationTargetActivationState
                    .CONDITIONAL;
        }

        if (
                role.deferred()
        ) {

            return baseCandidateRemains
                    ? TransformationTargetActivationState
                    .CONDITIONAL
                    : TransformationTargetActivationState
                    .INACTIVE;
        }

        return TransformationTargetActivationState
                .INACTIVE;
    }

    public List<CombinationTransformationEffectReasonType>
    buildHeavenlyReasons(

            HeavenlyStemCombinationTransformationEvidence evidence,

            TransformationOriginalFunctionState originalFunctionState,

            TransformationTargetActivationState targetActivationState

    ) {

        Objects.requireNonNull(
                evidence
        );

        Objects.requireNonNull(
                originalFunctionState
        );

        Objects.requireNonNull(
                targetActivationState
        );

        List<CombinationTransformationEffectReasonType>
                reasons =
                new ArrayList<>();

        switch (
                evidence.state()
        ) {

            case TRANSFORMED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .HEAVENLY_FULL_TRANSFORMATION
                    );

            case PARTIALLY_TRANSFORMED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .HEAVENLY_PARTIAL_TRANSFORMATION
                    );

            case CONDITIONAL ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .HEAVENLY_CONDITIONAL_TRANSFORMATION
                    );

            case COMBINED_NOT_TRANSFORMED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .HEAVENLY_COMBINED_WITHOUT_TRANSFORMATION
                    );

            case SUPPRESSED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .HEAVENLY_TRANSFORMATION_SUPPRESSED
                    );
        }

        addOriginalFunctionReason(
                reasons,
                originalFunctionState
        );

        addTargetActivationReason(
                reasons,
                targetActivationState
        );

        return List.copyOf(
                reasons
        );
    }

    public List<CombinationTransformationEffectReasonType>
    buildEarthlyReasons(

            EarthlyBranchCombinationIntegratedCandidate candidate,

            boolean cardinalMember,

            TransformationOriginalFunctionState originalFunctionState,

            TransformationTargetActivationState targetActivationState

    ) {

        Objects.requireNonNull(
                candidate
        );

        Objects.requireNonNull(
                originalFunctionState
        );

        Objects.requireNonNull(
                targetActivationState
        );

        List<CombinationTransformationEffectReasonType>
                reasons =
                new ArrayList<>();

        switch (
                candidate.role()
        ) {

            case PRIMARY ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .EARTHLY_PRIMARY_COMBINATION
                    );

            case SUPPORTING ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .EARTHLY_SUPPORTING_COMBINATION
                    );

            case COMPETING ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .EARTHLY_COMPETING_COMBINATION
                    );

            case DEFERRED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .EARTHLY_DEFERRED_COMBINATION
                    );

            case SUPPRESSED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .EARTHLY_SUPPRESSED_COMBINATION
                    );
        }

        if (
                candidate.baseMeaningful()
        ) {

            reasons.add(
                    CombinationTransformationEffectReasonType
                            .EARTHLY_BASE_MEANINGFUL
            );

        } else if (
                candidate.baseCandidateRemains()
        ) {

            reasons.add(
                    CombinationTransformationEffectReasonType
                            .EARTHLY_BASE_CONDITIONAL
            );
        }

        switch (
                candidate.obstructionLevel()
        ) {

            case NONE ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .NO_EARTHLY_OBSTRUCTION
                    );

            case MINOR ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .MINOR_EARTHLY_OBSTRUCTION
                    );

            case MEANINGFUL ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .MEANINGFUL_EARTHLY_OBSTRUCTION
                    );

            case SEVERE ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .SEVERE_EARTHLY_OBSTRUCTION
                    );
        }

        if (
                cardinalMember
        ) {

            reasons.add(
                    CombinationTransformationEffectReasonType
                            .CARDINAL_MEMBER
            );
        }

        reasons.add(
                CombinationTransformationEffectReasonType
                        .EARTHLY_HIDDEN_STEMS_PRESERVED
        );

        addOriginalFunctionReason(
                reasons,
                originalFunctionState
        );

        addTargetActivationReason(
                reasons,
                targetActivationState
        );

        return List.copyOf(
                reasons
        );
    }

    private void addOriginalFunctionReason(

            List<CombinationTransformationEffectReasonType> reasons,

            TransformationOriginalFunctionState state

    ) {

        switch (
                state
        ) {

            case PRESERVED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .ORIGINAL_FUNCTION_PRESERVED
                    );

            case MOSTLY_PRESERVED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .ORIGINAL_FUNCTION_MOSTLY_PRESERVED
                    );

            case PARTIALLY_REDUCED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .ORIGINAL_FUNCTION_PARTIALLY_REDUCED
                    );

            case STRONGLY_REDUCED ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .ORIGINAL_FUNCTION_STRONGLY_REDUCED
                    );
        }
    }

    private void addTargetActivationReason(

            List<CombinationTransformationEffectReasonType> reasons,

            TransformationTargetActivationState state

    ) {

        switch (
                state
        ) {

            case INACTIVE ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .TARGET_ELEMENT_INACTIVE
                    );

            case CONDITIONAL ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .TARGET_ELEMENT_CONDITIONAL
                    );

            case PARTIAL ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .TARGET_ELEMENT_PARTIAL
                    );

            case STRONG ->
                    reasons.add(
                            CombinationTransformationEffectReasonType
                                    .TARGET_ELEMENT_STRONG
                    );
        }
    }
}