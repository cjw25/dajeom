package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 절입 시점 하나를 나타낸다.
 *
 * =================================================
 *
 * 예:
 *
 * 입춘
 * 경칩
 * 청명
 * 입하
 * 망종
 * 소서
 * 입추
 * 백로
 * 한로
 * 입동
 * 대설
 * 소한
 *
 * =================================================
 *
 * 명리 월 경계에서 사용하는 Point.
 *
 * monthIndex:
 *
 * 0  입춘월
 * 1  경칩월
 * 2  청명월
 * 3  입하월
 * 4  망종월
 * 5  소서월
 * 6  입추월
 * 7  백로월
 * 8  한로월
 * 9  입동월
 * 10 대설월
 * 11 소한월
 *
 * =================================================
 *
 * dateTime은 반드시 실제 ZoneId가 포함된
 * ZonedDateTime으로 유지한다.
 *
 * LocalDate로 낮추거나 시각을 임의로 버리지 않는다.
 */
public record SolarTermPoint(

        String name,

        double longitude,

        ZonedDateTime dateTime,

        int monthIndex

) {

    public SolarTermPoint {

        if (
                name == null
                        || name.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "절기 이름이 필요합니다."
            );
        }

        name =
                name.trim();

        if (
                !Double.isFinite(
                        longitude
                )
        ) {

            throw new IllegalArgumentException(
                    "절기 황경은 유한한 숫자여야 합니다."
            );
        }

        /*
         * 태양 황경.
         *
         * 일반적인 표현 범위:
         * 0 <= longitude < 360
         */
        if (
                longitude < 0.0
                        || longitude >= 360.0
        ) {

            throw new IllegalArgumentException(
                    "절기 황경은 0 이상 360 미만이어야 합니다: "
                            + longitude
            );
        }

        Objects.requireNonNull(
                dateTime,
                "절입 시각이 필요합니다."
        );

        if (
                monthIndex < 0
                        || monthIndex > 11
        ) {

            throw new IllegalArgumentException(
                    "명리 월 경계 index는 0~11이어야 합니다: "
                            + monthIndex
            );
        }
    }

    /*
     * =================================================
     * 편의 메서드
     * =================================================
     */

    public boolean isIpchun() {

        return monthIndex == 0
                && "입춘".equals(
                name
        );
    }

    public boolean isSohan() {

        return monthIndex == 11
                && "소한".equals(
                name
        );
    }

    public boolean isBefore(
            ZonedDateTime target
    ) {

        Objects.requireNonNull(
                target,
                "비교 시각이 필요합니다."
        );

        return dateTime.isBefore(
                target
        );
    }

    public boolean isAfter(
            ZonedDateTime target
    ) {

        Objects.requireNonNull(
                target,
                "비교 시각이 필요합니다."
        );

        return dateTime.isAfter(
                target
        );
    }

    public boolean isEqual(
            ZonedDateTime target
    ) {

        Objects.requireNonNull(
                target,
                "비교 시각이 필요합니다."
        );

        return dateTime.isEqual(
                target
        );
    }
}