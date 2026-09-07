package com.dajeom.dto;

import com.dajeom.domain.StemRelation;

import java.util.Objects;

public record StemRelationResponse(
        String leftPosition,
        String leftPositionKorean,
        String leftStem,
        String leftStemKorean,

        String rightPosition,
        String rightPositionKorean,
        String rightStem,
        String rightStemKorean,

        String type,
        String typeKorean,
        String typeHanja
) {

    public static StemRelationResponse from(
            StemRelation relation
    ) {

        Objects.requireNonNull(
                relation,
                "천간 관계가 필요합니다."
        );

        return new StemRelationResponse(
                relation.leftPosition().name(),
                relation.leftPosition().korean(),
                relation.leftStem().hanja(),
                relation.leftStem().korean(),

                relation.rightPosition().name(),
                relation.rightPosition().korean(),
                relation.rightStem().hanja(),
                relation.rightStem().korean(),

                relation.type().name(),
                relation.type().korean(),
                relation.type().hanja()
        );
    }
}