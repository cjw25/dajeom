package com.dajeom.rule;

import com.dajeom.model.HeavenlyStemCombinationCandidate;
import com.dajeom.model.HeavenlyStemControlEvidence;
import com.dajeom.type.HeavenlyStemCombinationCompetitionReasonType;
import com.dajeom.type.HeavenlyStemCombinationCompetitionState;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class HeavenlyStemCombinationCompetitionRule {

    public boolean sharesMember(

            HeavenlyStemCombinationCandidate first,

            HeavenlyStemCombinationCandidate second

    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 천간합 후보가 필요합니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 천간합 후보가 필요합니다."
        );

        if (
                first == second
        ) {

            return false;
        }

        return first.involves(
                second.firstPosition()
        )
                || first.involves(
                second.secondPosition()
        );
    }

    public List<VisibleStemPosition> sharedPositions(

            HeavenlyStemCombinationCandidate first,

            HeavenlyStemCombinationCandidate second

    ) {

        Objects.requireNonNull(
                first
        );

        Objects.requireNonNull(
                second
        );

        List<VisibleStemPosition> positions =
                new ArrayList<>();

        if (
                second.involves(
                        first.firstPosition()
                )
        ) {

            positions.add(
                    first.firstPosition()
            );
        }

        if (
                second.involves(
                        first.secondPosition()
                )
        ) {

            positions.add(
                    first.secondPosition()
            );
        }

        return List.copyOf(
                positions
        );
    }

    public boolean equalOrStronger(

            HeavenlyStemCombinationCandidate candidate,

            HeavenlyStemCombinationCandidate competitor

    ) {

        Objects.requireNonNull(
                candidate
        );

        Objects.requireNonNull(
                competitor
        );

        return competitor
                .applicability()
                .rank()

                >= candidate
                .applicability()
                .rank();
    }

    public boolean internalPairRelation(

            HeavenlyStemCombinationCandidate candidate,

            HeavenlyStemControlEvidence controlEvidence

    ) {

        Objects.requireNonNull(
                candidate
        );

        Objects.requireNonNull(
                controlEvidence
        );

        return controlEvidence
                .involves(
                        candidate.firstPosition()
                )
                && controlEvidence
                .involves(
                        candidate.secondPosition()
                );
    }

    public boolean externalClashObstruction(

            HeavenlyStemCombinationCandidate candidate,

            HeavenlyStemControlEvidence controlEvidence

    ) {

        Objects.requireNonNull(
                candidate
        );

        Objects.requireNonNull(
                controlEvidence
        );

        if (
                internalPairRelation(
                        candidate,
                        controlEvidence
                )
        ) {

            return false;
        }

        if (
                !controlEvidence.clash()
        ) {

            return false;
        }

        return controlEvidence
                .involves(
                        candidate.firstPosition()
                )
                || controlEvidence
                .involves(
                        candidate.secondPosition()
                );
    }

    public boolean externalControlObstruction(

            HeavenlyStemCombinationCandidate candidate,

            HeavenlyStemControlEvidence controlEvidence

    ) {

        Objects.requireNonNull(
                candidate
        );

        Objects.requireNonNull(
                controlEvidence
        );

        if (
                internalPairRelation(
                        candidate,
                        controlEvidence
                )
        ) {

            return false;
        }

        if (
                !controlEvidence.controlOnly()
        ) {

            return false;
        }

        return candidate.involves(
                controlEvidence
                        .controlledPosition()
        );
    }

    public boolean externalObstruction(

            HeavenlyStemCombinationCandidate candidate,

            HeavenlyStemControlEvidence controlEvidence

    ) {

        return externalClashObstruction(
                candidate,
                controlEvidence
        )
                || externalControlObstruction(
                candidate,
                controlEvidence
        );
    }

    public HeavenlyStemCombinationCompetitionState determineState(

            List<HeavenlyStemCombinationCandidate>
                    competingCandidates,

            List<HeavenlyStemCombinationCandidate>
                    equalOrStrongerCompetingCandidates,

            List<HeavenlyStemControlEvidence>
                    externalObstructions

    ) {

        return determineState(
                competingCandidates != null
                        && !competingCandidates.isEmpty(),
                equalOrStrongerCompetingCandidates != null
                        && !equalOrStrongerCompetingCandidates.isEmpty(),
                externalObstructions != null
                        && !externalObstructions.isEmpty()
        );
    }

    /**
     * 9-2 공통 상태축.
     *
     * 외부 방해
     * >
     * 동급/상위 경쟁
     * >
     * 약한 경쟁
     * >
     * 비경쟁
     */
    public HeavenlyStemCombinationCompetitionState determineState(

            boolean competitionExists,

            boolean equalOrStrongerCompetitionExists,

            boolean externalObstructionExists

    ) {

        if (
                externalObstructionExists
        ) {

            return HeavenlyStemCombinationCompetitionState
                    .OBSTRUCTED;
        }

        if (
                equalOrStrongerCompetitionExists
        ) {

            return HeavenlyStemCombinationCompetitionState
                    .CONTESTED;
        }

        if (
                competitionExists
        ) {

            return HeavenlyStemCombinationCompetitionState
                    .COMPETING;
        }

        return HeavenlyStemCombinationCompetitionState
                .UNCONTESTED;
    }

    public List<HeavenlyStemCombinationCompetitionReasonType>
    buildReasons(

            HeavenlyStemCombinationCandidate candidate,

            HeavenlyStemCombinationCompetitionState state,

            List<HeavenlyStemCombinationCandidate>
                    competingCandidates,

            List<HeavenlyStemCombinationCandidate>
                    equalOrStrongerCompetingCandidates,

            List<HeavenlyStemControlEvidence>
                    externalObstructions,

            boolean dayMasterMultipleCombination

    ) {

        Objects.requireNonNull(
                candidate
        );

        Objects.requireNonNull(
                state
        );

        competingCandidates =
                competingCandidates == null
                        ? List.of()
                        : competingCandidates;

        equalOrStrongerCompetingCandidates =
                equalOrStrongerCompetingCandidates == null
                        ? List.of()
                        : equalOrStrongerCompetingCandidates;

        externalObstructions =
                externalObstructions == null
                        ? List.of()
                        : externalObstructions;

        List<HeavenlyStemCombinationCompetitionReasonType>
                reasons =
                new ArrayList<>();

        if (
                competingCandidates.isEmpty()
        ) {

            reasons.add(
                    HeavenlyStemCombinationCompetitionReasonType
                            .NO_COMPETING_CANDIDATE
            );

        } else {

            reasons.add(
                    HeavenlyStemCombinationCompetitionReasonType
                            .SHARED_MEMBER_COMPETITION
            );

            if (
                    competingCandidates.size()
                            >= 2
            ) {

                reasons.add(
                        HeavenlyStemCombinationCompetitionReasonType
                                .MULTIPLE_COMPETING_CANDIDATES
                );
            }

            if (
                    competingCandidates
                            .stream()
                            .anyMatch(
                                    HeavenlyStemCombinationCandidate
                                            ::adjacent
                            )
            ) {

                reasons.add(
                        HeavenlyStemCombinationCompetitionReasonType
                                .ADJACENT_COMPETITOR
                );
            }

            if (
                    !equalOrStrongerCompetingCandidates
                            .isEmpty()
            ) {

                reasons.add(
                        HeavenlyStemCombinationCompetitionReasonType
                                .EQUAL_OR_STRONGER_COMPETITOR
                );

            } else {

                reasons.add(
                        HeavenlyStemCombinationCompetitionReasonType
                                .ONLY_WEAKER_COMPETITORS
                );
            }
        }

        if (
                dayMasterMultipleCombination
        ) {

            reasons.add(
                    HeavenlyStemCombinationCompetitionReasonType
                            .DAY_MASTER_IN_MULTIPLE_COMBINATIONS
            );
        }

        boolean clashExists =
                externalObstructions
                        .stream()
                        .anyMatch(
                                HeavenlyStemControlEvidence
                                        ::clash
                        );

        boolean controlExists =
                externalObstructions
                        .stream()
                        .anyMatch(
                                HeavenlyStemControlEvidence
                                        ::controlOnly
                        );

        if (
                clashExists
        ) {

            reasons.add(
                    HeavenlyStemCombinationCompetitionReasonType
                            .EXTERNAL_CLASH_ON_MEMBER
            );
        }

        if (
                controlExists
        ) {

            reasons.add(
                    HeavenlyStemCombinationCompetitionReasonType
                            .EXTERNAL_CONTROL_ON_MEMBER
            );
        }

        if (
                !externalObstructions.isEmpty()
        ) {

            if (
                    externalObstructions
                            .stream()
                            .anyMatch(
                                    HeavenlyStemControlEvidence
                                            ::adjacent
                            )
            ) {

                reasons.add(
                        HeavenlyStemCombinationCompetitionReasonType
                                .ADJACENT_EXTERNAL_OBSTRUCTION
                );
            }

            if (
                    externalObstructions
                            .stream()
                            .anyMatch(
                                    evidence ->
                                            !evidence.adjacent()
                            )
            ) {

                reasons.add(
                        HeavenlyStemCombinationCompetitionReasonType
                                .DISTANT_EXTERNAL_OBSTRUCTION
                );
            }

            if (
                    externalObstructions.size()
                            >= 2
            ) {

                reasons.add(
                        HeavenlyStemCombinationCompetitionReasonType
                                .MULTIPLE_EXTERNAL_OBSTRUCTIONS
                );
            }
        }

        reasons.add(
                HeavenlyStemCombinationCompetitionReasonType
                        .INTERNAL_PAIR_CONTROL_IGNORED
        );

        switch (
                state
        ) {

            case UNCONTESTED ->
                    reasons.add(
                            HeavenlyStemCombinationCompetitionReasonType
                                    .UNCONTESTED_STATE
                    );

            case COMPETING ->
                    reasons.add(
                            HeavenlyStemCombinationCompetitionReasonType
                                    .COMPETING_STATE
                    );

            case CONTESTED ->
                    reasons.add(
                            HeavenlyStemCombinationCompetitionReasonType
                                    .CONTESTED_STATE
                    );

            case OBSTRUCTED ->
                    reasons.add(
                            HeavenlyStemCombinationCompetitionReasonType
                                    .OBSTRUCTED_STATE
                    );
        }

        return List.copyOf(
                reasons
        );
    }
}