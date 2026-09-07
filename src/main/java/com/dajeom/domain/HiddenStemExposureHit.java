package com.dajeom.domain;

import java.util.Objects;

public record HiddenStemExposureHit(

        PillarPosition sourceBranchPosition,

        EarthlyBranch sourceBranch,

        HeavenlyStem hiddenStem,

        HiddenStemPhase hiddenStemPhase,

        PillarPosition exposedStemPosition

) {

    public HiddenStemExposureHit {

        Objects.requireNonNull(
                sourceBranchPosition,
                "지장간 출처 위치는 필수입니다."
        );

        Objects.requireNonNull(
                sourceBranch,
                "지장간 출처 지지는 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "지장간 천간은 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStemPhase,
                "지장간 위치는 필수입니다."
        );

        Objects.requireNonNull(
                exposedStemPosition,
                "투출된 천간 위치는 필수입니다."
        );
    }

    public boolean fromMonthCommand() {

        return sourceBranchPosition
                == PillarPosition.MONTH;
    }
}