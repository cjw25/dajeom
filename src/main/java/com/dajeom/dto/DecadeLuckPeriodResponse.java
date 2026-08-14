package com.dajeom.dto;

import java.time.ZonedDateTime;

/**
 * 하나의 대운 구간 API 출력값.
 *
 * 출생시간 미상 등으로 교운시점이 범위인 경우
 * earliest/latest 값을 그대로 보존한다.
 */
public record DecadeLuckPeriodResponse(

        int sequenceNumber,

        PillarResponse pillar,

        String minimumStartAge,

        String maximumStartAge,

        ZonedDateTime earliestStartDateTime,

        ZonedDateTime latestStartDateTime,

        ZonedDateTime earliestEndDateTimeExclusive,

        ZonedDateTime latestEndDateTimeExclusive,

        boolean exactTiming

) {
}