package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 절기의 정확한 시점을 표현한다.
 *
 * 예:
 * 입춘
 * 경칩
 * 청명
 * ...
 *
 * 실제 천문 계산은
 * SolarTermCalculator가 담당한다.
 */
public record SolarTermPoint(

        /**
         * 절기명.
         *
         * 예:
         * 입춘
         */
        String name,

        /**
         * 태양 황경.
         *
         * 예:
         * 입춘 = 315도
         */
        double solarLongitude,

        /**
         * 해당 절기의 실제 시각.
         */
        ZonedDateTime dateTime,

        /**
         * 사주 월 계산용 월 인덱스.
         *
         * 인월 = 0
         * 묘월 = 1
         * ...
         *
         * 월 경계로 사용하지 않는 절기는
         * -1 사용 가능.
         */
        int monthIndex

) {

    public SolarTermPoint {

        Objects.requireNonNull(
                name,
                "절기명은 필수입니다."
        );

        Objects.requireNonNull(
                dateTime,
                "절기 시각은 필수입니다."
        );

        if (
                solarLongitude < 0.0
                        || solarLongitude >= 360.0
        ) {

            throw new IllegalArgumentException(
                    "태양 황경은 0도 이상 360도 미만이어야 합니다."
            );
        }

        if (
                monthIndex < -1
                        || monthIndex > 11
        ) {

            throw new IllegalArgumentException(
                    "절기 월 인덱스는 -1부터 11까지 사용할 수 있습니다."
            );
        }
    }

    /**
     * 해당 절기가 사주 월 경계인지 확인한다.
     */
    public boolean isMonthBoundary() {

        return monthIndex >= 0;
    }
}