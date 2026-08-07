package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.RelationType;

import java.util.Objects;

/**
 * 원국 천간 두 개 사이의 관계를 나타낸다.
 */
public record StemRelation(
        RelationType relationType,
        PillarPosition firstPosition,
        HeavenlyStem firstStem,
        PillarPosition secondPosition,
        HeavenlyStem secondStem,
        FiveElement transformationElement,
        boolean transformationCandidate,
        boolean transformationEstablished,
        String description
) {

    public StemRelation {
        Objects.requireNonNull(
                relationType,
                "천간 관계 유형은 필수입니다."
        );

        Objects.requireNonNull(
                firstPosition,
                "첫 번째 천간 위치는 필수입니다."
        );

        Objects.requireNonNull(
                firstStem,
                "첫 번째 천간은 필수입니다."
        );

        Objects.requireNonNull(
                secondPosition,
                "두 번째 천간 위치는 필수입니다."
        );

        Objects.requireNonNull(
                secondStem,
                "두 번째 천간은 필수입니다."
        );

        Objects.requireNonNull(
                description,
                "천간 관계 설명은 필수입니다."
        );
    }
}