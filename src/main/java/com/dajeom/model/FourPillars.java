package com.dajeom.model;

import java.util.Objects;

/**
 * 년주, 월주, 일주, 시주로 구성된 사주 원국이다.
 */
public record FourPillars(
        Pillar yearPillar,
        Pillar monthPillar,
        Pillar dayPillar,
        Pillar hourPillar
) {

    public FourPillars {
        Objects.requireNonNull(
                yearPillar,
                "년주는 필수입니다."
        );

        Objects.requireNonNull(
                monthPillar,
                "월주는 필수입니다."
        );

        Objects.requireNonNull(
                dayPillar,
                "일주는 필수입니다."
        );

        Objects.requireNonNull(
                hourPillar,
                "시주는 필수입니다."
        );
    }
}