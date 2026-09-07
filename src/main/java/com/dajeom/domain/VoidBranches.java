package com.dajeom.domain;

import java.util.Objects;

/**
 * 공망 결과.
 *
 * 하나의 60갑자가 어느 旬에 속하는지 계산한 뒤
 * 그 旬에서 빠지는 두 지지를 보관한다.
 *
 * 예:
 *
 * 癸酉
 * → 甲子旬
 * → 戌亥 공망
 *
 * 丁丑
 * → 甲戌旬
 * → 申酉 공망
 */
public record VoidBranches(
        Ganji sourceGanji,
        Ganji xunStartGanji,
        EarthlyBranch first,
        EarthlyBranch second
) {

    public VoidBranches {

        Objects.requireNonNull(
                sourceGanji,
                "공망 계산 기준 간지가 필요합니다."
        );

        Objects.requireNonNull(
                xunStartGanji,
                "旬 시작 간지가 필요합니다."
        );

        Objects.requireNonNull(
                first,
                "첫 번째 공망 지지가 필요합니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 공망 지지가 필요합니다."
        );

        /*
         * 모든 旬은 반드시 甲으로 시작한다.
         */
        if (xunStartGanji.stem()
                != HeavenlyStem.JIA) {

            throw new IllegalArgumentException(
                    "旬 시작 간지는 반드시 甲으로 시작해야 합니다. xunStart="
                            + xunStartGanji.hanja()
            );
        }

        if (first == second) {

            throw new IllegalArgumentException(
                    "두 공망 지지는 서로 달라야 합니다."
            );
        }
    }

    public String hanja() {

        return first.hanja()
                + second.hanja();
    }

    public String korean() {

        return first.korean()
                + second.korean();
    }

    @Override
    public String toString() {
        return hanja();
    }
}