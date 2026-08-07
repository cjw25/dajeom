package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.RelationType;

import java.util.List;
import java.util.Objects;

/**
 * 원국 지지 사이의 관계를 나타낸다.
 *
 * 육합·충·파·해는 일반적으로 지지 2개가 들어가고,
 * 삼합·삼형은 지지 3개가 들어갈 수 있다.
 */
public record BranchRelation(
        RelationType relationType,
        List<PillarPosition> positions,
        List<EarthlyBranch> branches,
        FiveElement transformationElement,
        boolean transformationCandidate,
        boolean transformationEstablished,
        String description
) {

    public BranchRelation {
        Objects.requireNonNull(
                relationType,
                "지지 관계 유형은 필수입니다."
        );

        Objects.requireNonNull(
                positions,
                "지지 위치 목록은 필수입니다."
        );

        Objects.requireNonNull(
                branches,
                "지지 목록은 필수입니다."
        );

        Objects.requireNonNull(
                description,
                "지지 관계 설명은 필수입니다."
        );

        if (positions.size() != branches.size()) {
            throw new IllegalArgumentException(
                    "지지 위치와 지지의 개수가 일치해야 합니다."
            );
        }

        positions = List.copyOf(positions);
        branches = List.copyOf(branches);
    }

    public boolean containsPosition(
            PillarPosition position
    ) {
        return positions.contains(position);
    }
}