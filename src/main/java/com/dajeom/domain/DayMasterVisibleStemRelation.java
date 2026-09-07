package com.dajeom.domain;

import java.util.Objects;

/**
 * 일간과 겉으로 드러난 천간의 관계.
 *
 * 일간 자신(DAY 위치)은 제외한다.
 */
public record DayMasterVisibleStemRelation(

        HeavenlyStem dayMaster,

        PillarPosition targetPosition,

        HeavenlyStem targetStem,

        DayMasterRelationType relationType

) {

    public DayMasterVisibleStemRelation {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                targetPosition,
                "대상 천간 위치는 필수입니다."
        );

        Objects.requireNonNull(
                targetStem,
                "대상 천간은 필수입니다."
        );

        Objects.requireNonNull(
                relationType,
                "생극 관계는 필수입니다."
        );

        if (
                targetPosition
                        == PillarPosition.DAY
        ) {

            throw new IllegalArgumentException(
                    "일간 자신은 VisibleStemRelation의 대상이 아닙니다."
            );
        }
    }
}