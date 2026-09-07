package com.dajeom.domain;

import java.util.Objects;

public record TwelveShinsalAnalysis(
        PillarPosition position,
        EarthlyBranch targetBranch,

        EarthlyBranch yearBranchReference,
        TwelveShinsalGroup yearBranchGroup,
        TwelveShinsal yearBranchShinsal,

        EarthlyBranch dayBranchReference,
        TwelveShinsalGroup dayBranchGroup,
        TwelveShinsal dayBranchShinsal,

        HeavenlyStem dayStemReference,
        TwelveShinsalGroup dayStemGroup,
        TwelveShinsal dayStemShinsal
) {

    public TwelveShinsalAnalysis {

        Objects.requireNonNull(
                position,
                "기둥 위치가 필요합니다."
        );

        Objects.requireNonNull(
                targetBranch,
                "12신살 대상 지지가 필요합니다."
        );

        Objects.requireNonNull(
                yearBranchReference,
                "년지 기준값이 필요합니다."
        );

        Objects.requireNonNull(
                yearBranchGroup,
                "년지 기준 삼합 그룹이 필요합니다."
        );

        Objects.requireNonNull(
                yearBranchShinsal,
                "년지 기준 12신살 결과가 필요합니다."
        );

        Objects.requireNonNull(
                dayBranchReference,
                "일지 기준값이 필요합니다."
        );

        Objects.requireNonNull(
                dayBranchGroup,
                "일지 기준 삼합 그룹이 필요합니다."
        );

        Objects.requireNonNull(
                dayBranchShinsal,
                "일지 기준 12신살 결과가 필요합니다."
        );

        Objects.requireNonNull(
                dayStemReference,
                "일간 기준값이 필요합니다."
        );

        Objects.requireNonNull(
                dayStemGroup,
                "일간 기준 삼합 그룹이 필요합니다."
        );

        Objects.requireNonNull(
                dayStemShinsal,
                "일간 기준 12신살 결과가 필요합니다."
        );
    }
}