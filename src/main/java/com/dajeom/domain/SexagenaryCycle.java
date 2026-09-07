package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public final class SexagenaryCycle {

    public static final int CYCLE_SIZE = 60;

    private static final List<Ganji> CYCLE =
            IntStream
                    .range(0, CYCLE_SIZE)
                    .mapToObj(index ->
                            new Ganji(
                                    index,
                                    HeavenlyStem.ofIndex(index),
                                    EarthlyBranch.ofIndex(index)
                            )
                    )
                    .toList();

    private SexagenaryCycle() {
    }

    public static Ganji ofIndex(int index) {

        int normalizedIndex =
                Math.floorMod(
                        index,
                        CYCLE_SIZE
                );

        return CYCLE.get(normalizedIndex);
    }

    public static Ganji of(
            HeavenlyStem stem,
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        for (Ganji ganji : CYCLE) {

            if (ganji.stem() == stem
                    && ganji.branch() == branch) {

                return ganji;
            }
        }

        throw new IllegalArgumentException(
                "60갑자에 존재하지 않는 조합입니다. "
                        + stem.hanja()
                        + branch.hanja()
        );
    }

    public static Ganji next(
            Ganji ganji
    ) {

        Objects.requireNonNull(
                ganji,
                "기준 간지가 필요합니다."
        );

        return move(
                ganji,
                1
        );
    }

    public static Ganji previous(
            Ganji ganji
    ) {

        Objects.requireNonNull(
                ganji,
                "기준 간지가 필요합니다."
        );

        return move(
                ganji,
                -1
        );
    }

    public static Ganji move(
            Ganji ganji,
            int distance
    ) {

        Objects.requireNonNull(
                ganji,
                "기준 간지가 필요합니다."
        );

        return ofIndex(
                ganji.cycleIndex() + distance
        );
    }

    public static int distanceForward(
            Ganji from,
            Ganji to
    ) {

        Objects.requireNonNull(
                from,
                "시작 간지가 필요합니다."
        );

        Objects.requireNonNull(
                to,
                "도착 간지가 필요합니다."
        );

        return Math.floorMod(
                to.cycleIndex()
                        - from.cycleIndex(),
                CYCLE_SIZE
        );
    }

    public static List<Ganji> all() {
        return CYCLE;
    }
}