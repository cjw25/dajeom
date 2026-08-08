package com.dajeom.dto;

import java.time.ZonedDateTime;

/**
 * 대운 API 출력값.
 *
 * 출생시간 미상 등으로
 * 정확한 대운 시작 계산이 불가능한 경우:
 *
 * calculationAvailable = false
 *
 * 이며 나머지 계산값은 null일 수 있다.
 */
public record DecadeLuckResponse(

        String direction,

        boolean calculationAvailable,

        String startAge,

        ZonedDateTime startDateTime,

        Integer currentOrder,

        PillarResponse currentPillar,

        String currentStartAge,

        String currentEndAge,

        ZonedDateTime currentStartDateTime,

        ZonedDateTime currentEndDateTime,

        boolean beforeFirstDecadeLuck,

        String unavailableReason

) {
}