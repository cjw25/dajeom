package com.dajeom.dto;

import com.dajeom.model.AnnualLuck;

import java.time.ZonedDateTime;

public record AnnualLuckResponse(
        int targetYear,
        PillarResponse pillar,
        ZonedDateTime startDateTime,
        ZonedDateTime endDateTime,
        String stemTenGod,
        String stemTenGodKorean,
        String branchMainTenGod,
        String branchMainTenGodKorean
) {

    public static AnnualLuckResponse from(
            AnnualLuck luck
    ) {
        return new AnnualLuckResponse(
                luck.targetYear(),

                PillarResponse.from(
                        luck.pillar()
                ),

                luck.startDateTime(),

                luck.endDateTime(),

                luck.stemTenGod().name(),

                luck
                        .stemTenGod()
                        .getKoreanName(),

                luck.branchMainTenGod().name(),

                luck
                        .branchMainTenGod()
                        .getKoreanName()
        );
    }
}