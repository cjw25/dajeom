package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 대운 시작 시점 계산에 사용되는 절입 정보다.
 */
public record SolarTermPoint(
        String name,
        double longitude,
        ZonedDateTime dateTime,
        int monthIndex
) {

    public SolarTermPoint {
        Objects.requireNonNull(
                name,
                "절기 이름은 필수입니다."
        );

        Objects.requireNonNull(
                dateTime,
                "절기 시각은 필수입니다."
        );
    }
}