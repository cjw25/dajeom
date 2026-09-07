package com.dajeom.domain;

import java.util.Objects;

public record Ganji(
        int cycleIndex,
        HeavenlyStem stem,
        EarthlyBranch branch
) {

    public Ganji {

        if (cycleIndex < 0 || cycleIndex >= 60) {
            throw new IllegalArgumentException(
                    "60갑자 index는 0~59 사이여야 합니다. cycleIndex="
                            + cycleIndex
            );
        }

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        int expectedStemIndex = cycleIndex % 10;
        int expectedBranchIndex = cycleIndex % 12;

        if (stem.index() != expectedStemIndex) {
            throw new IllegalArgumentException(
                    "60갑자 index와 천간이 일치하지 않습니다. "
                            + "cycleIndex=" + cycleIndex
                            + ", stem=" + stem
            );
        }

        if (branch.index() != expectedBranchIndex) {
            throw new IllegalArgumentException(
                    "60갑자 index와 지지가 일치하지 않습니다. "
                            + "cycleIndex=" + cycleIndex
                            + ", branch=" + branch
            );
        }
    }

    public String hanja() {
        return stem.hanja() + branch.hanja();
    }

    public String korean() {
        return stem.korean() + branch.korean();
    }

    @Override
    public String toString() {
        return hanja();
    }
}