package com.dajeom.domain;

import java.util.Objects;

/**
 * 일간과 지장간의 생극 관계.
 *
 * 어느 지지에서 나왔는지,
 * EARLY / MIDDLE / MAIN 중 어디인지까지 보존한다.
 */
public record DayMasterHiddenStemRelation(

        HeavenlyStem dayMaster,

        PillarPosition branchPosition,

        EarthlyBranch branch,

        HeavenlyStem hiddenStem,

        HiddenStemPhase hiddenStemPhase,

        DayMasterRelationType relationType

) {

    public DayMasterHiddenStemRelation {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                branchPosition,
                "지지 위치는 필수입니다."
        );

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "지장간은 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStemPhase,
                "지장간 위치는 필수입니다."
        );

        Objects.requireNonNull(
                relationType,
                "생극 관계는 필수입니다."
        );
    }
}