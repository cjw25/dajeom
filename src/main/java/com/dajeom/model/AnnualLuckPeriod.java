package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 하나의 세운 기간.
 *
 * 세운은 양력 1월 1일이 아니라
 * 입춘을 기준으로 변경한다.
 *
 * 예:
 *
 * 2026년 세운
 *
 * 시작:
 * 2026년 입춘
 *
 * 종료:
 * 2027년 입춘
 *
 * 기간은:
 *
 * [startDateTime, endDateTime)
 *
 * 형태로 관리한다.
 */
public record AnnualLuckPeriod(

        /**
         * 사주 기준 연도.
         *
         * 예:
         * 2026
         */
        int sajuYear,

        /**
         * 해당 연도의 세운 간지.
         *
         * 예:
         * 2026 = 丙午
         */
        Pillar pillar,

        /**
         * 해당 세운이 시작되는
         * 실제 입춘 시각.
         */
        ZonedDateTime startDateTime,

        /**
         * 다음 해 입춘 시각.
         */
        ZonedDateTime endDateTime

) {

    public AnnualLuckPeriod {

        Objects.requireNonNull(
                pillar,
                "세운 간지는 필수입니다."
        );

        Objects.requireNonNull(
                startDateTime,
                "세운 시작 시각은 필수입니다."
        );

        Objects.requireNonNull(
                endDateTime,
                "세운 종료 시각은 필수입니다."
        );

        if (
                !endDateTime.isAfter(
                        startDateTime
                )
        ) {

            throw new IllegalArgumentException(
                    "세운 종료 시각은 시작 시각보다 뒤여야 합니다."
            );
        }
    }

    /**
     * 특정 시각이 이 세운 기간에
     * 포함되는지 확인한다.
     */
    public boolean contains(
            ZonedDateTime dateTime
    ) {

        Objects.requireNonNull(
                dateTime,
                "조회 시각은 필수입니다."
        );

        ZonedDateTime normalized =
                dateTime.withZoneSameInstant(
                        startDateTime.getZone()
                );

        return !normalized.isBefore(
                startDateTime
        )
                && normalized.isBefore(
                endDateTime
        );
    }

    public String chineseName() {

        return pillar.chineseName();
    }

    public String koreanName() {

        return pillar.koreanName();
    }
}