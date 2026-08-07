package com.dajeom.constant;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.RelationType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 지지 합충형파해 고정표다.
 */
public final class BranchRelationTable {

    private static final List<BranchPairRelation>
            SIX_HARMONIES = List.of(

            pair(
                    RelationType.SIX_HARMONY,
                    EarthlyBranch.JA,
                    EarthlyBranch.CHUK,
                    FiveElement.EARTH
            ),

            pair(
                    RelationType.SIX_HARMONY,
                    EarthlyBranch.IN,
                    EarthlyBranch.HAE,
                    FiveElement.WOOD
            ),

            pair(
                    RelationType.SIX_HARMONY,
                    EarthlyBranch.MYO,
                    EarthlyBranch.SUL,
                    FiveElement.FIRE
            ),

            pair(
                    RelationType.SIX_HARMONY,
                    EarthlyBranch.JIN,
                    EarthlyBranch.YU,
                    FiveElement.METAL
            ),

            pair(
                    RelationType.SIX_HARMONY,
                    EarthlyBranch.SA,
                    EarthlyBranch.SIN,
                    FiveElement.WATER
            ),

            pair(
                    RelationType.SIX_HARMONY,
                    EarthlyBranch.O,
                    EarthlyBranch.MI,
                    FiveElement.EARTH
            )
    );

    private static final List<BranchPairRelation>
            CLASHES = List.of(

            pair(
                    RelationType.CLASH,
                    EarthlyBranch.JA,
                    EarthlyBranch.O,
                    null
            ),

            pair(
                    RelationType.CLASH,
                    EarthlyBranch.CHUK,
                    EarthlyBranch.MI,
                    null
            ),

            pair(
                    RelationType.CLASH,
                    EarthlyBranch.IN,
                    EarthlyBranch.SIN,
                    null
            ),

            pair(
                    RelationType.CLASH,
                    EarthlyBranch.MYO,
                    EarthlyBranch.YU,
                    null
            ),

            pair(
                    RelationType.CLASH,
                    EarthlyBranch.JIN,
                    EarthlyBranch.SUL,
                    null
            ),

            pair(
                    RelationType.CLASH,
                    EarthlyBranch.SA,
                    EarthlyBranch.HAE,
                    null
            )
    );

    private static final List<BranchPairRelation>
            HARMS = List.of(

            pair(
                    RelationType.HARM,
                    EarthlyBranch.JA,
                    EarthlyBranch.MI,
                    null
            ),

            pair(
                    RelationType.HARM,
                    EarthlyBranch.CHUK,
                    EarthlyBranch.O,
                    null
            ),

            pair(
                    RelationType.HARM,
                    EarthlyBranch.IN,
                    EarthlyBranch.SA,
                    null
            ),

            pair(
                    RelationType.HARM,
                    EarthlyBranch.MYO,
                    EarthlyBranch.JIN,
                    null
            ),

            pair(
                    RelationType.HARM,
                    EarthlyBranch.SIN,
                    EarthlyBranch.HAE,
                    null
            ),

            pair(
                    RelationType.HARM,
                    EarthlyBranch.YU,
                    EarthlyBranch.SUL,
                    null
            )
    );

    private static final List<BranchPairRelation>
            BREAKS = List.of(

            pair(
                    RelationType.BREAK,
                    EarthlyBranch.JA,
                    EarthlyBranch.YU,
                    null
            ),

            pair(
                    RelationType.BREAK,
                    EarthlyBranch.CHUK,
                    EarthlyBranch.JIN,
                    null
            ),

            pair(
                    RelationType.BREAK,
                    EarthlyBranch.IN,
                    EarthlyBranch.HAE,
                    null
            ),

            pair(
                    RelationType.BREAK,
                    EarthlyBranch.MYO,
                    EarthlyBranch.O,
                    null
            ),

            pair(
                    RelationType.BREAK,
                    EarthlyBranch.SA,
                    EarthlyBranch.SIN,
                    null
            ),

            pair(
                    RelationType.BREAK,
                    EarthlyBranch.MI,
                    EarthlyBranch.SUL,
                    null
            )
    );

    private static final List<BranchPairRelation>
            PAIR_PUNISHMENTS = List.of(

            pair(
                    RelationType.PUNISHMENT,
                    EarthlyBranch.JA,
                    EarthlyBranch.MYO,
                    null
            )
    );

    private static final List<BranchTripleRelation>
            THREE_HARMONIES = List.of(

            triple(
                    EarthlyBranch.SIN,
                    EarthlyBranch.JA,
                    EarthlyBranch.JIN,
                    FiveElement.WATER
            ),

            triple(
                    EarthlyBranch.HAE,
                    EarthlyBranch.MYO,
                    EarthlyBranch.MI,
                    FiveElement.WOOD
            ),

            triple(
                    EarthlyBranch.IN,
                    EarthlyBranch.O,
                    EarthlyBranch.SUL,
                    FiveElement.FIRE
            ),

            triple(
                    EarthlyBranch.SA,
                    EarthlyBranch.YU,
                    EarthlyBranch.CHUK,
                    FiveElement.METAL
            )
    );

    private static final List<BranchTripleRelation>
            TRIPLE_PUNISHMENTS = List.of(

            triple(
                    EarthlyBranch.IN,
                    EarthlyBranch.SA,
                    EarthlyBranch.SIN,
                    null
            ),

            triple(
                    EarthlyBranch.CHUK,
                    EarthlyBranch.SUL,
                    EarthlyBranch.MI,
                    null
            )
    );

    private static final Set<EarthlyBranch>
            SELF_PUNISHMENTS = Set.of(
            EarthlyBranch.JIN,
            EarthlyBranch.O,
            EarthlyBranch.YU,
            EarthlyBranch.HAE
    );

    private BranchRelationTable() {
    }

    public static Optional<BranchPairRelation>
    findPairRelation(
            RelationType type,
            EarthlyBranch first,
            EarthlyBranch second
    ) {
        return pairRelations(type)
                .stream()
                .filter(relation ->
                        relation.matches(
                                first,
                                second
                        )
                )
                .findFirst();
    }

    public static List<BranchTripleRelation>
    getThreeHarmonies() {
        return THREE_HARMONIES;
    }

    public static List<BranchTripleRelation>
    getTriplePunishments() {
        return TRIPLE_PUNISHMENTS;
    }

    public static boolean isSelfPunishment(
            EarthlyBranch branch
    ) {
        return SELF_PUNISHMENTS.contains(branch);
    }

    public static Optional<BranchTripleRelation>
    findHarmonyGroupContaining(
            EarthlyBranch first,
            EarthlyBranch second
    ) {
        return THREE_HARMONIES.stream()
                .filter(group ->
                        group.contains(first)
                                && group.contains(second)
                )
                .findFirst();
    }

    private static List<BranchPairRelation>
    pairRelations(
            RelationType type
    ) {
        return switch (type) {
            case SIX_HARMONY -> SIX_HARMONIES;
            case CLASH -> CLASHES;
            case HARM -> HARMS;
            case BREAK -> BREAKS;
            case PUNISHMENT -> PAIR_PUNISHMENTS;

            default -> List.of();
        };
    }

    private static BranchPairRelation pair(
            RelationType type,
            EarthlyBranch first,
            EarthlyBranch second,
            FiveElement transformationElement
    ) {
        return new BranchPairRelation(
                type,
                first,
                second,
                transformationElement
        );
    }

    private static BranchTripleRelation triple(
            EarthlyBranch first,
            EarthlyBranch second,
            EarthlyBranch third,
            FiveElement transformationElement
    ) {
        return new BranchTripleRelation(
                Set.of(first, second, third),
                transformationElement
        );
    }

    public record BranchPairRelation(
            RelationType relationType,
            EarthlyBranch first,
            EarthlyBranch second,
            FiveElement transformationElement
    ) {

        public boolean matches(
                EarthlyBranch targetFirst,
                EarthlyBranch targetSecond
        ) {
            return (
                    first == targetFirst
                            && second == targetSecond
            ) || (
                    first == targetSecond
                            && second == targetFirst
            );
        }
    }

    public record BranchTripleRelation(
            Set<EarthlyBranch> branches,
            FiveElement transformationElement
    ) {

        public boolean contains(
                EarthlyBranch branch
        ) {
            return branches.contains(branch);
        }

        public boolean matches(
                Set<EarthlyBranch> targets
        ) {
            return branches.equals(targets);
        }
    }
}