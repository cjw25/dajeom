package com.dajeom.domain;

import java.util.Objects;

public record BranchRelation(
        PillarPosition leftPosition,
        EarthlyBranch leftBranch,
        PillarPosition rightPosition,
        EarthlyBranch rightBranch,
        BranchRelationType type
) {

    public BranchRelation {

        Objects.requireNonNull(
                leftPosition,
                "왼쪽 기둥 위치가 필요합니다."
        );

        Objects.requireNonNull(
                leftBranch,
                "왼쪽 지지가 필요합니다."
        );

        Objects.requireNonNull(
                rightPosition,
                "오른쪽 기둥 위치가 필요합니다."
        );

        Objects.requireNonNull(
                rightBranch,
                "오른쪽 지지가 필요합니다."
        );

        Objects.requireNonNull(
                type,
                "지지 관계 종류가 필요합니다."
        );

        if (leftPosition == rightPosition) {

            throw new IllegalArgumentException(
                    "같은 기둥끼리는 지지 관계를 만들 수 없습니다."
            );
        }
    }

    public String display() {

        return leftPosition.korean()
                + " "
                + leftBranch.hanja()
                + " ↔ "
                + rightPosition.korean()
                + " "
                + rightBranch.hanja()
                + " : "
                + type.korean();
    }
}