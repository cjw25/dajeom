package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 하나의 10년 대운 구간.
 *
 * startDateTime은 포함,
 * endDateTime은 미포함으로 처리한다.
 *
 * 예:
 *
 * [2030-01-01, 2040-01-01)
 */
public record DecadeLuckPeriod(

        int order,

        Pillar pillar,

        LuckStartAge startAge,

        LuckStartAge endAge,

        ZonedDateTime startDateTime,

        ZonedDateTime endDateTime

) {

    public DecadeLuckPeriod {

        if (order < 1) {

            throw new IllegalArgumentException(
                    "대운 순서는 1 이상이어야 합니다."
            );
        }

        Objects.requireNonNull(
                pillar,
                "대운 간지는 필수입니다."
        );

        Objects.requireNonNull(
                startAge,
                "대운 시작 나이는 필수입니다."
        );

        Objects.requireNonNull(
                endAge,
                "대운 종료 나이는 필수입니다."
        );

        Objects.requireNonNull(
                startDateTime,
                "대운 시작 시각은 필수입니다."
        );

        Objects.requireNonNull(
                endDateTime,
                "대운 종료 시각은 필수입니다."
        );

        if (
                !endDateTime.isAfter(
                        startDateTime
                )
        ) {

            throw new IllegalArgumentException(
                    "대운 종료 시각은 시작 시각보다 뒤여야 합니다."
            );
        }
    }

    /**
     * 특정 시각이 이 대운 구간에
     * 포함되는지 확인한다.
     */
    public boolean contains(
            ZonedDateTime dateTime
    ) {

        Objects.requireNonNull(
                dateTime,
                "조회 시각은 필수입니다."
        );

        return !dateTime.isBefore(
                startDateTime
        )
                && dateTime.isBefore(
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