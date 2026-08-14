package com.dajeom.rule;

import com.dajeom.model.HeavenlyStemCombinationCandidate;
import com.dajeom.model.HeavenlyStemCombinationCompetitionEvidence;
import com.dajeom.type.HeavenlyStemCombinationApplicability;
import com.dajeom.type.HeavenlyStemCombinationCompetitionState;
import com.dajeom.type.HeavenlyStemCombinationTransformationReasonType;
import com.dajeom.type.HeavenlyStemCombinationTransformationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class HeavenlyStemCombinationTransformationRule {

    public HeavenlyStemCombinationTransformationState determineState(

            HeavenlyStemCombinationCandidate candidate,

            HeavenlyStemCombinationCompetitionEvidence
                    competitionEvidence

    ) {

        Objects.requireNonNull(
                candidate
        );

        Objects.requireNonNull(
                competitionEvidence
        );

        if (
                !competitionEvidence
                        .candidate()
                        .equals(
                                candidate
                        )
        ) {

            throw new IllegalArgumentException(
                    "9-1 후보와 9-2 경쟁 Evidence의 후보가 일치하지 않습니다."
            );
        }

        return determineState(
                candidate.applicability(),
                competitionEvidence.state()
        );
    }

    /**
     * 정밀화 9-3 공통 Rule Matrix.
     */
    public HeavenlyStemCombinationTransformationState determineState(

            HeavenlyStemCombinationApplicability applicability,

            HeavenlyStemCombinationCompetitionState
                    competitionState

    ) {

        Objects.requireNonNull(
                applicability,
                "9-1 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                competitionState,
                "9-2 경쟁 상태가 필요합니다."
        );

        if (
                applicability
                        == HeavenlyStemCombinationApplicability.STRONG
        ) {

            return switch (
                    competitionState
                    ) {

                case UNCONTESTED ->
                        HeavenlyStemCombinationTransformationState
                                .TRANSFORMED;

                case COMPETING ->
                        HeavenlyStemCombinationTransformationState
                                .PARTIALLY_TRANSFORMED;

                case CONTESTED,
                     OBSTRUCTED ->
                        HeavenlyStemCombinationTransformationState
                                .CONDITIONAL;
            };
        }

        if (
                applicability
                        == HeavenlyStemCombinationApplicability.ACTIVE
        ) {

            return switch (
                    competitionState
                    ) {

                case UNCONTESTED ->
                        HeavenlyStemCombinationTransformationState
                                .PARTIALLY_TRANSFORMED;

                case COMPETING ->
                        HeavenlyStemCombinationTransformationState
                                .CONDITIONAL;

                case CONTESTED,
                     OBSTRUCTED ->
                        HeavenlyStemCombinationTransformationState
                                .COMBINED_NOT_TRANSFORMED;
            };
        }

        if (
                applicability
                        == HeavenlyStemCombinationApplicability.CONDITIONAL
        ) {

            return switch (
                    competitionState
                    ) {

                case UNCONTESTED ->
                        HeavenlyStemCombinationTransformationState
                                .CONDITIONAL;

                case COMPETING,
                     CONTESTED ->
                        HeavenlyStemCombinationTransformationState
                                .COMBINED_NOT_TRANSFORMED;

                case OBSTRUCTED ->
                        HeavenlyStemCombinationTransformationState
                                .SUPPRESSED;
            };
        }

        return switch (
                competitionState
                ) {

            case UNCONTESTED ->
                    HeavenlyStemCombinationTransformationState
                            .COMBINED_NOT_TRANSFORMED;

            case COMPETING,
                 CONTESTED,
                 OBSTRUCTED ->
                    HeavenlyStemCombinationTransformationState
                            .SUPPRESSED;
        };
    }

    public List<HeavenlyStemCombinationTransformationReasonType>
    buildReasons(

            HeavenlyStemCombinationCandidate candidate,

            HeavenlyStemCombinationCompetitionEvidence
                    competitionEvidence,

            HeavenlyStemCombinationTransformationState
                    transformationState

    ) {

        Objects.requireNonNull(
                candidate
        );

        Objects.requireNonNull(
                competitionEvidence
        );

        Objects.requireNonNull(
                transformationState
        );

        List<HeavenlyStemCombinationTransformationReasonType>
                reasons =
                new ArrayList<>();

        switch (
                candidate.applicability()
        ) {

            case STRONG ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .STRONG_BASE_CONDITION
                    );

            case ACTIVE ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .ACTIVE_BASE_CONDITION
                    );

            case CONDITIONAL ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .CONDITIONAL_BASE_CONDITION
                    );

            case WEAK ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .WEAK_BASE_CONDITION
                    );
        }

        switch (
                competitionEvidence.state()
        ) {

            case UNCONTESTED ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .NO_COMPETITION
                    );

            case COMPETING ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .WEAKER_COMPETITION
                    );

            case CONTESTED ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .EQUAL_OR_STRONGER_COMPETITION
                    );

            case OBSTRUCTED -> {

                if (
                        competitionEvidence
                                .hasEqualOrStrongerCompetitor()
                ) {

                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .EQUAL_OR_STRONGER_COMPETITION
                    );

                } else if (
                        competitionEvidence
                                .hasCompetition()
                ) {

                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .WEAKER_COMPETITION
                    );
                }
            }
        }

        if (
                competitionEvidence
                        .dayMasterMultipleCombination()
        ) {

            reasons.add(
                    HeavenlyStemCombinationTransformationReasonType
                            .DAY_MASTER_MULTIPLE_COMBINATION
            );
        }

        if (
                competitionEvidence
                        .hasExternalObstruction()
        ) {

            reasons.add(
                    HeavenlyStemCombinationTransformationReasonType
                            .EXTERNAL_OBSTRUCTION_PRESENT
            );

            if (
                    competitionEvidence
                            .adjacentExternalObstructionExists()
            ) {

                reasons.add(
                        HeavenlyStemCombinationTransformationReasonType
                                .ADJACENT_EXTERNAL_OBSTRUCTION
                );
            }

            if (
                    competitionEvidence
                            .multipleExternalObstructions()
            ) {

                reasons.add(
                        HeavenlyStemCombinationTransformationReasonType
                                .MULTIPLE_EXTERNAL_OBSTRUCTIONS
                );
            }
        }

        switch (
                transformationState
        ) {

            case TRANSFORMED ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .FULL_TRANSFORMATION_CONDITIONS_MET
                    );

            case PARTIALLY_TRANSFORMED ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .PARTIAL_TRANSFORMATION_CONDITIONS_MET
                    );

            case CONDITIONAL ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .TRANSFORMATION_REQUIRES_CONDITIONS
                    );

            case COMBINED_NOT_TRANSFORMED ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .COMBINATION_REMAINS_WITHOUT_TRANSFORMATION
                    );

            case SUPPRESSED ->
                    reasons.add(
                            HeavenlyStemCombinationTransformationReasonType
                                    .TRANSFORMATION_SUPPRESSED
                    );
        }

        return List.copyOf(
                reasons
        );
    }
}