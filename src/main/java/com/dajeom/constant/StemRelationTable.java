package com.dajeom.constant;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.List;
import java.util.Optional;

/**
 * 천간 오합 고정표다.
 */
public final class StemRelationTable {

    private static final List<StemCombination>
            COMBINATIONS = List.of(

            new StemCombination(
                    HeavenlyStem.GAP,
                    HeavenlyStem.GI,
                    FiveElement.EARTH
            ),

            new StemCombination(
                    HeavenlyStem.EUL,
                    HeavenlyStem.GYEONG,
                    FiveElement.METAL
            ),

            new StemCombination(
                    HeavenlyStem.BYEONG,
                    HeavenlyStem.SIN,
                    FiveElement.WATER
            ),

            new StemCombination(
                    HeavenlyStem.JEONG,
                    HeavenlyStem.IM,
                    FiveElement.WOOD
            ),

            new StemCombination(
                    HeavenlyStem.MU,
                    HeavenlyStem.GYE,
                    FiveElement.FIRE
            )
    );

    private StemRelationTable() {
    }

    public static Optional<StemCombination>
    findCombination(
            HeavenlyStem first,
            HeavenlyStem second
    ) {
        return COMBINATIONS.stream()
                .filter(combination ->
                        combination.matches(
                                first,
                                second
                        )
                )
                .findFirst();
    }

    public record StemCombination(
            HeavenlyStem first,
            HeavenlyStem second,
            FiveElement transformationElement
    ) {

        public boolean matches(
                HeavenlyStem targetFirst,
                HeavenlyStem targetSecond
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
}