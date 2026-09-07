package com.dajeom.dto;

import com.dajeom.domain.Pillar;

import java.util.Objects;

public record PillarResponse(
        String stem,
        String branch,
        String ganji,
        String stemKorean,
        String branchKorean
) {

    public static PillarResponse from(
            Pillar pillar
    ) {

        Objects.requireNonNull(
                pillar,
                "변환할 기둥이 필요합니다."
        );

        return new PillarResponse(
                pillar.stem().hanja(),
                pillar.branch().hanja(),
                pillar.hanja(),
                pillar.stem().korean(),
                pillar.branch().korean()
        );
    }
}