package com.dajeom.domain;

import java.util.Objects;

public record StemGroundHit(

        HeavenlyStem targetStem,

        PillarPosition branchPosition,

        EarthlyBranch branch,

        HeavenlyStem hiddenStem,

        HiddenStemPhase hiddenStemPhase,

        StemGroundRelationType relationType

) {

    public StemGroundHit {

        Objects.requireNonNull(
                targetStem,
                "분석 대상 천간은 필수입니다."
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
                "천간-지장간 관계 종류는 필수입니다."
        );
    }

    public boolean exactStem() {

        return relationType
                == StemGroundRelationType.EXACT_STEM;
    }

    public boolean sameElementOtherStem() {

        return relationType
                == StemGroundRelationType.SAME_ELEMENT_OTHER_STEM;
    }
}