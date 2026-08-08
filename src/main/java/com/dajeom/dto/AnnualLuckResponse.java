package com.dajeom.dto;

import java.time.ZonedDateTime;

/**
 * 세운 API 출력값.
 */
public record AnnualLuckResponse(

        int targetYear,

        PillarResponse pillar,

        ZonedDateTime startDateTime,

        ZonedDateTime endDateTime,

        String stemTenGod,

        String branchMainStemKoreanName,

        String branchMainStemChineseName,

        String branchMainTenGod

) {
}