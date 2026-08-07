package com.dajeom.dto;

import com.dajeom.model.ElementDistribution;

/**
 * 원국의 오행 분포를 구분해서 반환한다.
 */
public record ElementDistributionResponse(

        /*
         * 년간·월간·일간·시간 총 4개 천간의 분포
         */
        ElementCountResponse visibleStems,

        /*
         * 년지·월지·일지·시지의 대표 오행 분포
         */
        ElementCountResponse branchMainElements,

        /*
         * 사주팔자 표면 8글자의 오행 분포
         */
        ElementCountResponse eightCharacters,

        /*
         * 모든 지장간의 오행 분포
         */
        ElementCountResponse hiddenStems
) {

    public static ElementDistributionResponse from(
            ElementDistribution distribution
    ) {
        return new ElementDistributionResponse(
                ElementCountResponse.from(
                        distribution.visibleStemCounts()
                ),

                ElementCountResponse.from(
                        distribution.branchMainCounts()
                ),

                ElementCountResponse.from(
                        distribution.eightCharacterCounts()
                ),

                ElementCountResponse.from(
                        distribution.hiddenStemCounts()
                )
        );
    }
}