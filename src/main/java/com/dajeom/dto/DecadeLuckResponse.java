package com.dajeom.dto;

import java.util.List;

/**
 * 대운 API 출력값.
 *
 * 정밀화 13차 구조를 그대로 표현한다.
 *
 * 출생시간 미상 또는 교운 경계에서는
 * 가능한 대운을 하나로 임의 확정하지 않는다.
 */
public record DecadeLuckResponse(

        String direction,

        boolean sequenceAvailable,

        boolean analysisAvailable,

        boolean exactTiming,

        boolean birthTimeUnknown,

        String minimumStartAge,

        String maximumStartAge,

        boolean startAgeRange,

        boolean solarTermBoundaryUncertain,

        List<DecadeLuckPeriodResponse> periods,

        /**
         * 교운시점이 완전히 확정된 원국에서
         * 기준시각에 해당하는 정확한 대운.
         */
        DecadeLuckPeriodResponse exactCurrentPeriod,

        /**
         * 출생시간 미상이어도 모든 가능한 경우에
         * 동일하게 성립하는 확정 대운.
         */
        DecadeLuckPeriodResponse definiteCurrentPeriod,

        /**
         * 기준시각에 활성 상태일 가능성이 있는
         * 모든 대운 후보.
         *
         * 교운 경계에서는 2개가 될 수 있다.
         */
        List<DecadeLuckPeriodResponse> possibleCurrentPeriods,

        /**
         * 어떤 가능한 교운시점을 적용해도
         * 아직 첫 대운 시작 전인지 여부.
         */
        boolean definitelyBeforeFirstDecadeLuck,

        /**
         * 가능한 교운시점 중 일부에서는
         * 아직 첫 대운 시작 전일 수 있는지 여부.
         */
        boolean possiblyBeforeFirstDecadeLuck,

        String unavailableReason

) {

    public DecadeLuckResponse {

        periods =
                periods == null
                        ? List.of()
                        : List.copyOf(
                        periods
                );

        possibleCurrentPeriods =
                possibleCurrentPeriods == null
                        ? List.of()
                        : List.copyOf(
                        possibleCurrentPeriods
                );
    }
}