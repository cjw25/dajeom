package com.dajeom.constant;

import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * 육십갑자 순환표.
 *
 * 0 = 갑자(甲子)
 * 1 = 을축(乙丑)
 * ...
 * 59 = 계해(癸亥)
 *
 * 계산기는 이 테이블을 이용해
 * 간지를 순환시킨다.
 */
public final class GanjiCycleTable {

    private static final List<Pillar> CYCLE =
            IntStream
                    .range(
                            0,
                            SajuConstants.GANJI_CYCLE_SIZE
                    )
                    .mapToObj(
                            index ->
                                    new Pillar(
                                            HeavenlyStem.fromIndex(
                                                    index
                                            ),
                                            EarthlyBranch.fromIndex(
                                                    index
                                            )
                                    )
                    )
                    .toList();

    private GanjiCycleTable() {

        throw new IllegalStateException(
                "GanjiCycleTable은 생성할 수 없습니다."
        );
    }

    /**
     * 육십갑자의 특정 위치를 반환한다.
     *
     * index가 60을 넘어가거나 음수여도
     * 자동으로 순환시킨다.
     */
    public static Pillar get(
            int index
    ) {

        int normalizedIndex =
                Math.floorMod(
                        index,
                        SajuConstants.GANJI_CYCLE_SIZE
                );

        return CYCLE.get(
                normalizedIndex
        );
    }

    /**
     * 육십갑자 전체를 반환한다.
     */
    public static List<Pillar> getAll() {

        return CYCLE;
    }

    /**
     * 특정 천간 + 지지 조합이
     * 육십갑자에서 몇 번째인지 찾는다.
     *
     * 갑자 = 0
     * 을축 = 1
     * ...
     */
    public static int findIndex(
            HeavenlyStem stem,
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                stem,
                "천간은 필수입니다."
        );

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        for (
                int index = 0;
                index < CYCLE.size();
                index++
        ) {

            Pillar pillar =
                    CYCLE.get(index);

            if (
                    pillar.stem() == stem
                            && pillar.branch() == branch
            ) {

                return index;
            }
        }

        throw new IllegalArgumentException(
                "유효한 육십갑자 조합이 아닙니다: "
                        + stem.getKoreanName()
                        + branch.getKoreanName()
        );
    }
}