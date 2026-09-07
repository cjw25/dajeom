package com.dajeom.domain;

import java.util.Objects;

public record Pillar(
        Ganji ganji
) {

    public Pillar {

        Objects.requireNonNull(
                ganji,
                "기둥의 간지가 필요합니다."
        );
    }

    public HeavenlyStem stem() {
        return ganji.stem();
    }

    public EarthlyBranch branch() {
        return ganji.branch();
    }

    public int cycleIndex() {
        return ganji.cycleIndex();
    }

    public String hanja() {
        return ganji.hanja();
    }

    public String korean() {
        return ganji.korean();
    }

    @Override
    public String toString() {
        return hanja();
    }
}