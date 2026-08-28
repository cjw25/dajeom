package com.dajeom.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * 장기 흐름 API 출력값.
 *
 * =================================================
 * 공개 API 역할
 * =================================================
 *
 * 장기 흐름의:
 *
 * - 진행 방향
 * - 계산 가능 여부
 * - 시점 확정 여부
 * - 출생시간 미상 여부
 * - 시작 나이 범위
 * - 절입 경계 불확실성
 * - 전체 Period
 * - 현재 확정/가능 Period
 *
 * 를 구조화해서 제공한다.
 *
 * 출생시간 미상 또는 교운 경계에서는
 * 가능한 장기 흐름을 하나로 임의 확정하지 않는다.
 *
 * =================================================
 * unavailableReason
 * =================================================
 *
 * unavailableReason은 Java 내부 진단용 문자열이다.
 *
 * 공개 API에는 이미:
 *
 * - sequenceAvailable
 * - analysisAvailable
 * - exactTiming
 * - birthTimeUnknown
 * - startAgeRange
 * - solarTermBoundaryUncertain
 *
 * 이라는 구조화 상태가 존재한다.
 *
 * 따라서 프론트가 unavailableReason 문자열을
 * 분석해서 상태를 추론하지 않도록
 * JSON에서는 숨긴다.
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
         * 기준시각에 해당하는 정확한 장기 흐름.
         */
        DecadeLuckPeriodResponse exactCurrentPeriod,

        /**
         * 출생시간 미상이어도 모든 가능한 경우에
         * 동일하게 성립하는 확정 장기 흐름.
         */
        DecadeLuckPeriodResponse definiteCurrentPeriod,

        /**
         * 기준시각에 활성 상태일 가능성이 있는
         * 모든 장기 흐름 후보.
         *
         * 교운 경계에서는 2개가 될 수 있다.
         */
        List<DecadeLuckPeriodResponse> possibleCurrentPeriods,

        /**
         * 어떤 가능한 교운시점을 적용해도
         * 아직 첫 장기 흐름 시작 전인지 여부.
         */
        boolean definitelyBeforeFirstDecadeLuck,

        /**
         * 가능한 교운시점 중 일부에서는
         * 아직 첫 장기 흐름 시작 전일 수 있는지 여부.
         */
        boolean possiblyBeforeFirstDecadeLuck,

        /**
         * Java 내부 진단 문자열.
         *
         * 프론트 공개 상태 계약이 아니다.
         */
        @JsonIgnore
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