package com.dajeom.domain;

import java.util.Objects;

public record BranchGroupMember(
        PillarPosition position,
        EarthlyBranch branch
) {

    public BranchGroupMember {

        Objects.requireNonNull(
                position,
                "기둥 위치가 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );
    }
}