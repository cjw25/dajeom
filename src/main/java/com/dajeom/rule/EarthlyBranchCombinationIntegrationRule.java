package com.dajeom.rule;

import com.dajeom.model.EarthlyBranchCombinationObstructionEvidence;
import com.dajeom.type.EarthlyBranchCombinationCandidateKind;
import com.dajeom.type.EarthlyBranchCombinationIntegrationReasonType;
import com.dajeom.type.EarthlyBranchCombinationIntegrationRole;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EarthlyBranchCombinationIntegrationRule {

    public boolean sharesMember(

            EarthlyBranchCombinationObstructionEvidence first,

            EarthlyBranchCombinationObstructionEvidence second

    ) {

        Objects.requireNonNull(
                first
        );

        Objects.requireNonNull(
                second
        );

        if (
                first.equals(
                        second
                )
        ) {

            return false;
        }

        return first
                .candidatePositions()
                .stream()
                .anyMatch(
                        second
                                .candidatePositions()
                                ::contains
                );
    }

    public boolean sameTarget(

            EarthlyBranchCombinationObstructionEvidence first,

            EarthlyBranchCombinationObstructionEvidence second

    ) {

        Objects.requireNonNull(
                first
        );

        Objects.requireNonNull(
                second
        );

        return first.targetElement()
                == second.targetElement();
    }

    public int comparePriority(

            EarthlyBranchCombinationObstructionEvidence first,

            EarthlyBranchCombinationObstructionEvidence second

    ) {

        Objects.requireNonNull(
                first
        );

        Objects.requireNonNull(
                second
        );

        return comparePriority(
                first.baseCandidateRemains(),
                first.baseMeaningful(),
                first.level(),
                first.baseStateRank(),
                structuralRank(
                        first
                ),
                first.monthBranchInvolved(),
                cardinalIncluded(
                        first
                ),

                second.baseCandidateRemains(),
                second.baseMeaningful(),
                second.level(),
                second.baseStateRank(),
                structuralRank(
                        second
                ),
                second.monthBranchInvolved(),
                cardinalIncluded(
                        second
                )
        );
    }

    /**
     * 9-8 공통 우선도 축.
     *
     * 점수합산이 아니라
     * 기존 9-8과 동일한 lexicographical 비교다.
     */
    public int comparePriority(

            boolean firstCandidateRemains,

            boolean firstMeaningful,

            EarthlyBranchCombinationObstructionLevel firstLevel,

            int firstBaseStateRank,

            int firstStructuralRank,

            boolean firstMonthBranchInvolved,

            boolean firstCardinalIncluded,

            boolean secondCandidateRemains,

            boolean secondMeaningful,

            EarthlyBranchCombinationObstructionLevel secondLevel,

            int secondBaseStateRank,

            int secondStructuralRank,

            boolean secondMonthBranchInvolved,

            boolean secondCardinalIncluded

    ) {

        Objects.requireNonNull(
                firstLevel
        );

        Objects.requireNonNull(
                secondLevel
        );

        int comparison =
                Boolean.compare(
                        firstCandidateRemains,
                        secondCandidateRemains
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        comparison =
                Boolean.compare(
                        firstMeaningful,
                        secondMeaningful
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        /*
         * 방해 rank는 낮을수록 우세.
         */
        comparison =
                Integer.compare(
                        secondLevel.rank(),
                        firstLevel.rank()
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        comparison =
                Integer.compare(
                        firstBaseStateRank,
                        secondBaseStateRank
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        comparison =
                Integer.compare(
                        firstStructuralRank,
                        secondStructuralRank
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        comparison =
                Boolean.compare(
                        firstMonthBranchInvolved,
                        secondMonthBranchInvolved
                );

        if (
                comparison != 0
        ) {

            return comparison;
        }

        return Boolean.compare(
                firstCardinalIncluded,
                secondCardinalIncluded
        );
    }

    public int structuralRank(
            EarthlyBranchCombinationObstructionEvidence candidate
    ) {

        Objects.requireNonNull(
                candidate
        );

        if (
                candidate
                        .candidateKind()
                        .sixCombination()
        ) {

            return 2;
        }

        if (
                candidate
                        .candidateKind()
                        .threeCombination()
        ) {

            if (
                    candidate
                            .threeCombinationEvidence()
                            .completeCombination()
            ) {

                return 5;
            }

            if (
                    candidate
                            .threeCombinationEvidence()
                            .halfCombination()
            ) {

                return 3;
            }

            return 1;
        }

        if (
                candidate
                        .directionalCombinationEvidence()
                        .completeCombination()
        ) {

            return 5;
        }

        if (
                candidate
                        .directionalCombinationEvidence()
                        .partialWithCardinal()
        ) {

            return 3;
        }

        return 1;
    }

    public boolean cardinalIncluded(
            EarthlyBranchCombinationObstructionEvidence candidate
    ) {

        Objects.requireNonNull(
                candidate
        );

        if (
                candidate
                        .candidateKind()
                        .sixCombination()
        ) {

            return false;
        }

        if (
                candidate
                        .candidateKind()
                        .threeCombination()
        ) {

            return candidate
                    .threeCombinationEvidence()
                    .cardinalBranchIncluded();
        }

        return candidate
                .directionalCombinationEvidence()
                .cardinalBranchIncluded();
    }

    public boolean higherPriority(

            EarthlyBranchCombinationObstructionEvidence current,

            EarthlyBranchCombinationObstructionEvidence
                    higherCandidate

    ) {

        return comparePriority(
                higherCandidate,
                current
        ) > 0;
    }

    public boolean equalPriority(

            EarthlyBranchCombinationObstructionEvidence first,

            EarthlyBranchCombinationObstructionEvidence second

    ) {

        return comparePriority(
                first,
                second
        ) == 0;
    }

    public EarthlyBranchCombinationIntegrationRole determineRole(

            EarthlyBranchCombinationObstructionEvidence candidate,

            List<EarthlyBranchCombinationObstructionEvidence>
                    convergentCandidates,

            List<EarthlyBranchCombinationObstructionEvidence>
                    higherPriorityConvergentCandidates,

            List<EarthlyBranchCombinationObstructionEvidence>
                    competingCandidates,

            List<EarthlyBranchCombinationObstructionEvidence>
                    higherPriorityCompetingCandidates,

            List<EarthlyBranchCombinationObstructionEvidence>
                    equalPriorityCompetingCandidates

    ) {

        Objects.requireNonNull(
                candidate
        );

        convergentCandidates =
                safeList(
                        convergentCandidates
                );

        higherPriorityConvergentCandidates =
                safeList(
                        higherPriorityConvergentCandidates
                );

        higherPriorityCompetingCandidates =
                safeList(
                        higherPriorityCompetingCandidates
                );

        equalPriorityCompetingCandidates =
                safeList(
                        equalPriorityCompetingCandidates
                );

        boolean meaningfulConvergentSupport =
                convergentCandidates
                        .stream()
                        .anyMatch(
                                convergent ->
                                        convergent
                                                .baseMeaningful()

                                                && !convergent
                                                .severeObstruction()
                        );

        return determineRole(
                candidate.severeObstruction(),
                candidate.baseCandidateRemains(),
                candidate.baseMeaningful(),
                !higherPriorityCompetingCandidates
                        .isEmpty(),
                !equalPriorityCompetingCandidates
                        .isEmpty(),
                meaningfulConvergentSupport,
                !higherPriorityConvergentCandidates
                        .isEmpty()
        );
    }

    public EarthlyBranchCombinationIntegrationRole determineRole(

            boolean severeObstruction,

            boolean baseCandidateRemains,

            boolean baseMeaningful,

            boolean higherPriorityCompetingExists,

            boolean equalPriorityCompetingExists,

            boolean meaningfulConvergentSupport,

            boolean higherPriorityConvergentExists

    ) {

        if (
                severeObstruction
        ) {

            return EarthlyBranchCombinationIntegrationRole
                    .SUPPRESSED;
        }

        if (
                !baseCandidateRemains
        ) {

            return EarthlyBranchCombinationIntegrationRole
                    .DEFERRED;
        }

        if (
                higherPriorityCompetingExists
        ) {

            return EarthlyBranchCombinationIntegrationRole
                    .DEFERRED;
        }

        if (
                equalPriorityCompetingExists
        ) {

            return EarthlyBranchCombinationIntegrationRole
                    .COMPETING;
        }

        if (
                !baseMeaningful
        ) {

            if (
                    meaningfulConvergentSupport
            ) {

                return EarthlyBranchCombinationIntegrationRole
                        .SUPPORTING;
            }

            return EarthlyBranchCombinationIntegrationRole
                    .DEFERRED;
        }

        if (
                higherPriorityConvergentExists
        ) {

            return EarthlyBranchCombinationIntegrationRole
                    .SUPPORTING;
        }

        return EarthlyBranchCombinationIntegrationRole
                .PRIMARY;
    }

    public List<EarthlyBranchCombinationIntegrationReasonType>
    buildReasons(

            EarthlyBranchCombinationObstructionEvidence candidate,

            EarthlyBranchCombinationIntegrationRole role,

            List<EarthlyBranchCombinationObstructionEvidence>
                    convergentCandidates,

            List<EarthlyBranchCombinationObstructionEvidence>
                    higherPriorityConvergentCandidates,

            List<EarthlyBranchCombinationObstructionEvidence>
                    competingCandidates,

            List<EarthlyBranchCombinationObstructionEvidence>
                    higherPriorityCompetingCandidates,

            List<EarthlyBranchCombinationObstructionEvidence>
                    equalPriorityCompetingCandidates

    ) {

        Objects.requireNonNull(
                candidate
        );

        Objects.requireNonNull(
                role
        );

        convergentCandidates =
                safeList(
                        convergentCandidates
                );

        higherPriorityConvergentCandidates =
                safeList(
                        higherPriorityConvergentCandidates
                );

        competingCandidates =
                safeList(
                        competingCandidates
                );

        higherPriorityCompetingCandidates =
                safeList(
                        higherPriorityCompetingCandidates
                );

        equalPriorityCompetingCandidates =
                safeList(
                        equalPriorityCompetingCandidates
                );

        List<EarthlyBranchCombinationIntegrationReasonType>
                reasons =
                new ArrayList<>();

        if (
                candidate.baseMeaningful()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .BASE_MEANINGFUL
            );

        } else if (
                candidate.baseCandidateRemains()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .BASE_CONDITIONAL
            );

        } else {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .BASE_INACTIVE
            );
        }

        switch (
                candidate.level()
        ) {

            case NONE ->
                    reasons.add(
                            EarthlyBranchCombinationIntegrationReasonType
                                    .NO_OBSTRUCTION
                    );

            case MINOR ->
                    reasons.add(
                            EarthlyBranchCombinationIntegrationReasonType
                                    .MINOR_OBSTRUCTION
                    );

            case MEANINGFUL ->
                    reasons.add(
                            EarthlyBranchCombinationIntegrationReasonType
                                    .MEANINGFUL_OBSTRUCTION
                    );

            case SEVERE ->
                    reasons.add(
                            EarthlyBranchCombinationIntegrationReasonType
                                    .SEVERE_OBSTRUCTION
                    );
        }

        if (
                structuralRank(
                        candidate
                ) >= 5
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .COMPLETE_FORMATION
            );
        }

        if (
                cardinalIncluded(
                        candidate
                )
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .CARDINAL_BRANCH_INCLUDED
            );
        }

        if (
                candidate.monthBranchInvolved()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .MONTH_BRANCH_INVOLVED
            );
        }

        if (
                convergentCandidates.isEmpty()
                        && competingCandidates.isEmpty()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .NO_OVERLAPPING_CANDIDATE
            );
        }

        if (
                !convergentCandidates.isEmpty()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .SAME_TARGET_CONVERGENCE
            );

            if (
                    convergentCandidates.size()
                            >= 2
            ) {

                reasons.add(
                        EarthlyBranchCombinationIntegrationReasonType
                                .MULTIPLE_SAME_TARGET_CONVERGENCE
                );
            }

            if (
                    convergentCandidates
                            .stream()
                            .anyMatch(
                                    convergent ->
                                            convergent
                                                    .baseMeaningful()

                                                    && !convergent
                                                    .severeObstruction()
                            )
            ) {

                reasons.add(
                        EarthlyBranchCombinationIntegrationReasonType
                                .MEANINGFUL_CONVERGENT_SUPPORT
                );
            }
        }

        if (
                !higherPriorityConvergentCandidates
                        .isEmpty()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .HIGHER_PRIORITY_CONVERGENT_CANDIDATE
            );
        }

        if (
                !competingCandidates.isEmpty()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .DIFFERENT_TARGET_COMPETITION
            );
        }

        if (
                !higherPriorityCompetingCandidates
                        .isEmpty()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .HIGHER_PRIORITY_COMPETITOR
            );
        }

        if (
                !equalPriorityCompetingCandidates
                        .isEmpty()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .EQUAL_PRIORITY_COMPETITOR
            );
        }

        if (
                !competingCandidates.isEmpty()
                        && higherPriorityCompetingCandidates.isEmpty()
                        && equalPriorityCompetingCandidates.isEmpty()
        ) {

            reasons.add(
                    EarthlyBranchCombinationIntegrationReasonType
                            .OUTRANKS_CONFLICTING_CANDIDATES
            );
        }

        switch (
                role
        ) {

            case PRIMARY ->
                    reasons.add(
                            EarthlyBranchCombinationIntegrationReasonType
                                    .PRIMARY_ROLE_SELECTED
                    );

            case SUPPORTING ->
                    reasons.add(
                            EarthlyBranchCombinationIntegrationReasonType
                                    .SUPPORTING_ROLE_SELECTED
                    );

            case COMPETING ->
                    reasons.add(
                            EarthlyBranchCombinationIntegrationReasonType
                                    .COMPETING_ROLE_SELECTED
                    );

            case DEFERRED ->
                    reasons.add(
                            EarthlyBranchCombinationIntegrationReasonType
                                    .DEFERRED_ROLE_SELECTED
                    );

            case SUPPRESSED ->
                    reasons.add(
                            EarthlyBranchCombinationIntegrationReasonType
                                    .SUPPRESSED_ROLE_SELECTED
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    private List<EarthlyBranchCombinationObstructionEvidence> safeList(
            List<EarthlyBranchCombinationObstructionEvidence> candidates
    ) {

        return candidates == null
                ? List.of()
                : List.copyOf(
                candidates
        );
    }
}