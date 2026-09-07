package com.dajeom.dto;

import com.dajeom.domain.DaewoonPillar;

import java.util.Objects;

public record DaewoonPillarResponse(
        int order,
        int cycleIndex,

        String stem,
        String stemKorean,

        String branch,
        String branchKorean,

        String ganji,
        String ganjiKorean
) {

    public static DaewoonPillarResponse from(
            DaewoonPillar daewoonPillar
    ) {

        Objects.requireNonNull(
                daewoonPillar,
                "대운 간지가 필요합니다."
        );

        return new DaewoonPillarResponse(
                daewoonPillar.order(),
                daewoonPillar.cycleIndex(),

                daewoonPillar.stem().hanja(),
                daewoonPillar.stem().korean(),

                daewoonPillar.branch().hanja(),
                daewoonPillar.branch().korean(),

                daewoonPillar.hanja(),
                daewoonPillar.korean()
        );
    }
}