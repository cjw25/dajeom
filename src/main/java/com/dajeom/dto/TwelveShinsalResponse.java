package com.dajeom.dto;

import com.dajeom.domain.TwelveShinsalAnalysis;

import java.util.Objects;

public record TwelveShinsalResponse(
        String position,
        String positionKorean,

        String branch,
        String branchKorean,

        String yearBranchReference,
        String yearBranchGroup,
        String yearBranchShinsal,
        String yearBranchShinsalHanja,

        String dayBranchReference,
        String dayBranchGroup,
        String dayBranchShinsal,
        String dayBranchShinsalHanja,

        String dayStemReference,
        String dayStemGroup,
        String dayStemShinsal,
        String dayStemShinsalHanja
) {

    public static TwelveShinsalResponse from(
            TwelveShinsalAnalysis value
    ) {

        Objects.requireNonNull(
                value,
                "12신살 분석 결과가 필요합니다."
        );

        return new TwelveShinsalResponse(
                value.position().name(),
                value.position().korean(),

                value.targetBranch().hanja(),
                value.targetBranch().korean(),

                value.yearBranchReference().hanja(),
                value.yearBranchGroup().hanja(),
                value.yearBranchShinsal().korean(),
                value.yearBranchShinsal().hanja(),

                value.dayBranchReference().hanja(),
                value.dayBranchGroup().hanja(),
                value.dayBranchShinsal().korean(),
                value.dayBranchShinsal().hanja(),

                value.dayStemReference().hanja(),
                value.dayStemGroup().hanja(),
                value.dayStemShinsal().korean(),
                value.dayStemShinsal().hanja()
        );
    }
}