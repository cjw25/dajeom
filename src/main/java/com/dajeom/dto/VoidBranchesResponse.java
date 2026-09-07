package com.dajeom.dto;

import com.dajeom.domain.VoidBranches;

import java.util.Objects;

public record VoidBranchesResponse(
        String sourceGanji,

        String xunStartGanji,

        String first,
        String firstKorean,

        String second,
        String secondKorean,

        String ganji
) {

    public static VoidBranchesResponse from(
            VoidBranches value
    ) {

        Objects.requireNonNull(
                value,
                "공망 결과가 필요합니다."
        );

        return new VoidBranchesResponse(
                value.sourceGanji().hanja(),

                value.xunStartGanji().hanja(),

                value.first().hanja(),
                value.first().korean(),

                value.second().hanja(),
                value.second().korean(),

                value.hanja()
        );
    }
}