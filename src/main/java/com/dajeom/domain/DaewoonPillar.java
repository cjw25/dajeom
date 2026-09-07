package com.dajeom.domain;

import java.util.Objects;

/**
 * 대운 한 칸의 간지.
 *
 * order = 1
 * → 첫 번째 대운
 *
 * order = 2
 * → 두 번째 대운
 *
 * 아직 시작 나이는 포함하지 않는다.
 * 시작 시점은 절입 시각 계산 단계에서 별도로 결합한다.
 */
public record DaewoonPillar(
        int order,
        Ganji ganji
) {

    public DaewoonPillar {

        if (order < 1) {

            throw new IllegalArgumentException(
                    "대운 차수는 1 이상이어야 합니다."
            );
        }

        Objects.requireNonNull(
                ganji,
                "대운 간지가 필요합니다."
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
}