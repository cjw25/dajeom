package com.dajeom.domain;

import java.util.Objects;

public record StemRelation(
        PillarPosition leftPosition,
        HeavenlyStem leftStem,
        PillarPosition rightPosition,
        HeavenlyStem rightStem,
        StemRelationType type
) {

    public StemRelation {

        Objects.requireNonNull(
                leftPosition,
                "왼쪽 기둥 위치가 필요합니다."
        );

        Objects.requireNonNull(
                leftStem,
                "왼쪽 천간이 필요합니다."
        );

        Objects.requireNonNull(
                rightPosition,
                "오른쪽 기둥 위치가 필요합니다."
        );

        Objects.requireNonNull(
                rightStem,
                "오른쪽 천간이 필요합니다."
        );

        Objects.requireNonNull(
                type,
                "천간 관계 종류가 필요합니다."
        );

        if (leftPosition == rightPosition) {

            throw new IllegalArgumentException(
                    "같은 기둥끼리는 천간 관계를 만들 수 없습니다."
            );
        }
    }

    public String display() {

        return leftPosition.korean()
                + " "
                + leftStem.hanja()
                + " ↔ "
                + rightPosition.korean()
                + " "
                + rightStem.hanja()
                + " : "
                + type.korean();
    }
}