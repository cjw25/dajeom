package com.dajeom.model;

import java.util.Objects;

/**
 * 24절기의 고정 정의.
 *
 * 절기의 실제 발생 시각은
 * 연도마다 달라지므로 저장하지 않는다.
 *
 * 실제 시각 계산은
 * SolarTermCalculator가 수행한다.
 */
public record SolarTermDefinition(

        /**
         * 한글 절기명.
         *
         * 예:
         * 입춘
         */
        String koreanName,

        /**
         * 한자 절기명.
         *
         * 예:
         * 立春
         */
        String chineseName,

        /**
         * 태양 황경.
         *
         * 입춘 = 315도
         */
        double solarLongitude,

        /**
         * 사주 월 인덱스.
         *
         * 인월 = 0
         * 묘월 = 1
         * 진월 = 2
         * ...
         * 축월 = 11
         *
         * 월 경계가 아닌 중기는 -1.
         */
        int monthIndex

) {

    public SolarTermDefinition {

        Objects.requireNonNull(
                koreanName,
                "한글 절기명은 필수입니다."
        );

        Objects.requireNonNull(
                chineseName,
                "한자 절기명은 필수입니다."
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
                    "월 인덱스는 -1부터 11까지 사용할 수 있습니다."
            );
        }
    }

    public boolean isMonthBoundary() {

        return monthIndex >= 0;
    }
}