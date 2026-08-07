package com.dajeom.dto;

import com.dajeom.model.DecadeLuck;

import java.time.LocalDate;

public record DecadeLuckResponse(
        int sequence,
        PillarResponse pillar,
        LocalDate startDate,
        LocalDate endDate,
        int startAge,
        int endAge,
        String stemTenGod,
        String stemTenGodKorean,
        String branchMainTenGod,
        String branchMainTenGodKorean,
        boolean current
) {

    public static DecadeLuckResponse from(
            DecadeLuck luck
    ) {
        return new DecadeLuckResponse(
                luck.sequence(),

                PillarResponse.from(
                        luck.pillar()
                ),

                luck.startDate(),

                luck.endDate(),

                luck.startAge(),

                luck.endAge(),

                luck.stemTenGod().name(),

                luck
                        .stemTenGod()
                        .getKoreanName(),

                luck.branchMainTenGod().name(),

                luck
                        .branchMainTenGod()
                        .getKoreanName(),

                luck.current()
        );
    }
}