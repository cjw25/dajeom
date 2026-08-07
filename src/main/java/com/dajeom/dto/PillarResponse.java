package com.dajeom.dto;

import com.dajeom.model.Pillar;

/**
 * 사주의 한 기둥을 프론트에 반환하는 객체다.
 */
public record PillarResponse(
        String stem,
        String stemKorean,
        String branch,
        String branchKorean,
        String chineseName,
        String koreanName
) {

    public static PillarResponse from(
            Pillar pillar
    ) {
        return new PillarResponse(
                pillar
                        .stem()
                        .getChineseCharacter(),

                pillar
                        .stem()
                        .getKoreanName(),

                pillar
                        .branch()
                        .getChineseCharacter(),

                pillar
                        .branch()
                        .getKoreanName(),

                pillar.chineseName(),

                pillar.koreanName()
        );
    }
}