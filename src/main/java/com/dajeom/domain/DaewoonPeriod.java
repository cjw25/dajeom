package com.dajeom.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 하나의 실제 대운 기간.
 *
 * 사용자 UI에 대운표를 보여주기 위한 객체가 아니다.
 *
 * 목적:
 *
 * - 각 대운의 실제 시작/종료 시각 보존
 * - 특정 시점이 어느 대운에 속하는지 판정
 * - 이후 세운 / 월운 / Evidence 계산의 내부 근거
 *
 * 기간 규칙:
 *
 * [startDateTimeInclusive, endDateTimeExclusive)
 *
 * 시작 시각은 포함하고
 * 종료 시각은 포함하지 않는다.
 */
public record DaewoonPeriod(

        int sequenceNumber,

        Ganji ganji,

        LocalDateTime startDateTimeInclusive,

        LocalDateTime endDateTimeExclusive

) {

    public DaewoonPeriod {

        if (sequenceNumber < 1) {

            throw new IllegalArgumentException(
                    "대운 순번은 1 이상이어야 합니다."
            );
        }

        Objects.requireNonNull(
                ganji,
                "대운 간지는 필수입니다."
        );

        Objects.requireNonNull(
                startDateTimeInclusive,
                "대운 시작시각은 필수입니다."
        );

        Objects.requireNonNull(
                endDateTimeExclusive,
                "대운 종료시각은 필수입니다."
        );

        if (
                !endDateTimeExclusive
                        .isAfter(
                                startDateTimeInclusive
                        )
        ) {

            throw new IllegalArgumentException(
                    "대운 종료시각은 시작시각보다 뒤여야 합니다."
            );
        }
    }

    /**
     * 특정 시각이 현재 대운 기간에 속하는지 판정한다.
     *
     * 시작시각:
     * 포함
     *
     * 종료시각:
     * 제외
     */
    public boolean contains(
            LocalDateTime targetDateTime
    ) {

        Objects.requireNonNull(
                targetDateTime,
                "대운 조회 기준시각은 필수입니다."
        );

        return !targetDateTime
                .isBefore(
                        startDateTimeInclusive
                )
                &&
                targetDateTime
                        .isBefore(
                                endDateTimeExclusive
                        );
    }
}