package com.dajeom.rule;

import com.dajeom.model.EarthlyBranchCombinationObstructionRelation;
import com.dajeom.type.EarthlyBranchCombinationObstructionLevel;
import com.dajeom.type.EarthlyBranchCombinationObstructionReasonType;
import com.dajeom.type.EarthlyBranchRelationKind;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class EarthlyBranchCombinationObstructionRule {

    public EarthlyBranchCombinationObstructionLevel determineLevel(
            List<EarthlyBranchCombinationObstructionRelation> relations
    ) {

        relations =
                relations == null
                        ? List.of()
                        : List.copyOf(
                        relations
                );

        if (
                relations.isEmpty()
        ) {

            return EarthlyBranchCombinationObstructionLevel
                    .NONE;
        }

        Set<EarthlyBranchRelationKind> allKinds =
                collectKinds(
                        relations
                );

        int affectedMemberCount =
                relations
                        .stream()
                        .flatMap(
                                relation ->
                                        relation
                                                .affectedCandidatePositions()
                                                .stream()
                        )
                        .distinct()
                        .toList()
                        .size();

        boolean internalObstruction =
                relations
                        .stream()
                        .anyMatch(
                                EarthlyBranchCombinationObstructionRelation
                                        ::internalToCandidate
                        );

        boolean internalClash =
                relations
                        .stream()
                        .anyMatch(
                                relation ->
                                        relation.internalToCandidate()
                                                && relation.hasClash()
                        );

        boolean internalMultipleKinds =
                relations
                        .stream()
                        .anyMatch(
                                relation ->
                                        relation.internalToCandidate()
                                                && relation
                                                .multipleObstructionKinds()
                        );

        boolean cardinalAffected =
                relations
                        .stream()
                        .anyMatch(
                                EarthlyBranchCombinationObstructionRelation
                                        ::cardinalBranchAffected
                        );

        boolean cardinalClashed =
                relations
                        .stream()
                        .anyMatch(
                                relation ->
                                        relation.cardinalBranchAffected()
                                                && relation.hasClash()
                        );

        boolean adjacentObstruction =
                relations
                        .stream()
                        .anyMatch(
                                EarthlyBranchCombinationObstructionRelation
                                        ::adjacent
                        );

        return determineLevel(
                true,
                internalObstruction,
                internalClash,
                internalMultipleKinds,
                cardinalAffected,
                cardinalClashed,
                allKinds.contains(
                        EarthlyBranchRelationKind.CLASH
                ),
                allKinds.contains(
                        EarthlyBranchRelationKind.PUNISHMENT
                ),
                adjacentObstruction,
                affectedMemberCount,
                allKinds.size()
        );
    }

    /**
     * 9-7 공통 방해 상태축.
     */
    public EarthlyBranchCombinationObstructionLevel determineLevel(

            boolean obstructionExists,

            boolean internalObstruction,

            boolean internalClash,

            boolean internalMultipleKinds,

            boolean cardinalAffected,

            boolean cardinalClashed,

            boolean hasClash,

            boolean hasPunishment,

            boolean adjacentObstruction,

            int affectedMemberCount,

            int distinctObstructionKindCount

    ) {

        if (
                !obstructionExists
        ) {

            return EarthlyBranchCombinationObstructionLevel
                    .NONE;
        }

        if (
                internalClash
                        || internalMultipleKinds
                        || cardinalClashed
        ) {

            return EarthlyBranchCombinationObstructionLevel
                    .SEVERE;
        }

        if (
                affectedMemberCount >= 2
                        && (
                        hasClash
                                || hasPunishment
                )
        ) {

            return EarthlyBranchCombinationObstructionLevel
                    .SEVERE;
        }

        if (
                affectedMemberCount >= 2
                        && distinctObstructionKindCount >= 2
        ) {

            return EarthlyBranchCombinationObstructionLevel
                    .SEVERE;
        }

        if (
                internalObstruction
                        || cardinalAffected
                        || hasClash
                        || hasPunishment
                        || adjacentObstruction
                        || affectedMemberCount >= 2
        ) {

            return EarthlyBranchCombinationObstructionLevel
                    .MEANINGFUL;
        }

        return EarthlyBranchCombinationObstructionLevel
                .MINOR;
    }

    public List<EarthlyBranchCombinationObstructionReasonType>
    buildReasons(

            EarthlyBranchCombinationObstructionLevel level,

            List<EarthlyBranchCombinationObstructionRelation> relations

    ) {

        Objects.requireNonNull(
                level
        );

        relations =
                relations == null
                        ? List.of()
                        : List.copyOf(
                        relations
                );

        List<EarthlyBranchCombinationObstructionReasonType>
                reasons =
                new ArrayList<>();

        if (
                relations.isEmpty()
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .NO_OBSTRUCTION
            );

            return List.copyOf(
                    reasons
            );
        }

        Set<EarthlyBranchRelationKind> allKinds =
                collectKinds(
                        relations
                );

        boolean internal =
                relations
                        .stream()
                        .anyMatch(
                                EarthlyBranchCombinationObstructionRelation
                                        ::internalToCandidate
                        );

        boolean external =
                relations
                        .stream()
                        .anyMatch(
                                EarthlyBranchCombinationObstructionRelation
                                        ::externalToCandidate
                        );

        boolean adjacent =
                relations
                        .stream()
                        .anyMatch(
                                EarthlyBranchCombinationObstructionRelation
                                        ::adjacent
                        );

        boolean cardinalAffected =
                relations
                        .stream()
                        .anyMatch(
                                EarthlyBranchCombinationObstructionRelation
                                        ::cardinalBranchAffected
                        );

        boolean cardinalClashed =
                relations
                        .stream()
                        .anyMatch(
                                relation ->
                                        relation.cardinalBranchAffected()
                                                && relation.hasClash()
                        );

        int affectedMemberCount =
                relations
                        .stream()
                        .flatMap(
                                relation ->
                                        relation
                                                .affectedCandidatePositions()
                                                .stream()
                        )
                        .distinct()
                        .toList()
                        .size();

        if (
                internal
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .INTERNAL_MEMBER_OBSTRUCTION
            );
        }

        if (
                external
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .EXTERNAL_MEMBER_OBSTRUCTION
            );
        }

        if (
                allKinds.contains(
                        EarthlyBranchRelationKind.CLASH
                )
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .CLASH_PRESENT
            );
        }

        if (
                allKinds.contains(
                        EarthlyBranchRelationKind.PUNISHMENT
                )
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .PUNISHMENT_PRESENT
            );
        }

        if (
                allKinds.contains(
                        EarthlyBranchRelationKind.BREAK
                )
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .BREAK_PRESENT
            );
        }

        if (
                allKinds.contains(
                        EarthlyBranchRelationKind.HARM
                )
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .HARM_PRESENT
            );
        }

        reasons.add(
                adjacent
                        ? EarthlyBranchCombinationObstructionReasonType
                        .ADJACENT_OBSTRUCTION
                        : EarthlyBranchCombinationObstructionReasonType
                        .DISTANT_ONLY_OBSTRUCTION
        );

        if (
                cardinalAffected
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .CARDINAL_BRANCH_AFFECTED
            );
        }

        if (
                cardinalClashed
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .CARDINAL_BRANCH_CLASHED
            );
        }

        if (
                affectedMemberCount >= 2
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .MULTIPLE_MEMBERS_AFFECTED
            );
        }

        if (
                relations.size() >= 2
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .MULTIPLE_OBSTRUCTION_RELATIONS
            );
        }

        if (
                allKinds.size() >= 2
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .MULTIPLE_OBSTRUCTION_KINDS
            );
        }

        if (
                relations
                        .stream()
                        .anyMatch(
                                relation ->
                                        relation.internalToCandidate()
                                                && relation
                                                .multipleObstructionKinds()
                        )
        ) {

            reasons.add(
                    EarthlyBranchCombinationObstructionReasonType
                            .INTERNAL_MULTIPLE_OBSTRUCTION_KINDS
            );
        }

        switch (
                level
        ) {

            case NONE -> {
            }

            case MINOR ->
                    reasons.add(
                            EarthlyBranchCombinationObstructionReasonType
                                    .MINOR_OBSTRUCTION_STATE
                    );

            case MEANINGFUL ->
                    reasons.add(
                            EarthlyBranchCombinationObstructionReasonType
                                    .MEANINGFUL_OBSTRUCTION_STATE
                    );

            case SEVERE ->
                    reasons.add(
                            EarthlyBranchCombinationObstructionReasonType
                                    .SEVERE_OBSTRUCTION_STATE
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    private Set<EarthlyBranchRelationKind> collectKinds(
            List<EarthlyBranchCombinationObstructionRelation> relations
    ) {

        EnumSet<EarthlyBranchRelationKind> kinds =
                EnumSet.noneOf(
                        EarthlyBranchRelationKind.class
                );

        for (
                EarthlyBranchCombinationObstructionRelation relation
                : relations
        ) {

            kinds.addAll(
                    relation.obstructionKinds()
            );
        }

        return Set.copyOf(
                kinds
        );
    }
}