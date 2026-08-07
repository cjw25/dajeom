package com.dajeom.constant;

import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 갑자부터 계해까지 이어지는 육십갑자 순환표다.
 *
 * 인덱스:
 *
 * 0  = 갑자
 * 1  = 을축
 * 2  = 병인
 * ...
 * 59 = 계해
 */
public final class GanjiCycleTable {

    private static final int GANJI_CYCLE_SIZE = 60;

    private static final List<Pillar> GANJI_CYCLE =
            createGanjiCycle();

    private GanjiCycleTable() {
    }

    /**
     * 육십갑자 순환 인덱스에 해당하는 간지를 반환한다.
     *
     * 음수 또는 60 이상의 값도 정상적으로 순환시킨다.
     */
    public static Pillar get(int index) {
        int normalizedIndex =
                Math.floorMod(
                        index,
                        GANJI_CYCLE_SIZE
                );

        return GANJI_CYCLE.get(normalizedIndex);
    }

    /**
     * 갑자부터 계해까지 전체 육십갑자를 반환한다.
     *
     * 반환 목록은 수정할 수 없다.
     */
    public static List<Pillar> getAll() {
        return GANJI_CYCLE;
    }

    /**
     * 천간과 지지 조합의 육십갑자 인덱스를 반환한다.
     *
     * 육십갑자에 존재하지 않는 조합이면 -1을 반환한다.
     */
    public static int findIndex(
            HeavenlyStem stem,
            EarthlyBranch branch
    ) {
        for (int index = 0;
             index < GANJI_CYCLE.size();
             index++) {

            Pillar pillar =
                    GANJI_CYCLE.get(index);

            if (pillar.stem() == stem
                    && pillar.branch() == branch) {

                return index;
            }
        }

        return -1;
    }

    private static List<Pillar> createGanjiCycle() {
        List<Pillar> cycle =
                new ArrayList<>(GANJI_CYCLE_SIZE);

        for (int index = 0;
             index < GANJI_CYCLE_SIZE;
             index++) {

            HeavenlyStem stem =
                    HeavenlyStem.fromIndex(index);

            EarthlyBranch branch =
                    EarthlyBranch.fromIndex(index);

            cycle.add(
                    new Pillar(stem, branch)
            );
        }

        return Collections.unmodifiableList(cycle);
    }
}