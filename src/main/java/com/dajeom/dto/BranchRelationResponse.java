package com.dajeom.dto;

import com.dajeom.domain.BranchRelation;

import java.util.Objects;

public record BranchRelationResponse(
        String leftPosition,
        String leftPositionKorean,
        String leftBranch,
        String leftBranchKorean,

        String rightPosition,
        String rightPositionKorean,
        String rightBranch,
        String rightBranchKorean,

        String type,
        String typeKorean,
        String typeHanja
) {

    public static BranchRelationResponse from(
            BranchRelation relation
    ) {

        Objects.requireNonNull(
                relation,
                "지지 관계가 필요합니다."
        );

        return new BranchRelationResponse(
                relation.leftPosition().name(),
                relation.leftPosition().korean(),
                relation.leftBranch().hanja(),
                relation.leftBranch().korean(),

                relation.rightPosition().name(),
                relation.rightPosition().korean(),
                relation.rightBranch().hanja(),
                relation.rightBranch().korean(),

                relation.type().name(),
                relation.type().korean(),
                relation.type().hanja()
        );
    }
}