package com.dajeom.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 하나의 세운 기간.
 *
 * 사용자에게 세운표를 보여주기 위한 객체가 아니다.
 *
 * 목적:
 * - 특정 시점의 세운 판정
 * - 원국 × 세운 분석
 * - 대운 × 세운 분석
 * - 이후 Evidence 생성
 *
 * 기간:
 * [입춘, 다음 입춘)
 */
public record SewoonPeriod(

        int sajuYear,

        Ganji ganji,

        LocalDateTime startDateTimeInclusive,

        LocalDateTime endDateTimeExclusive

) {

    public SewoonPeriod {

        Objects.requireNonNull(
                ganji,
                "세운 간지는 필수입니다."
        );

        Objects.requireNonNull(
                startDateTimeInclusive,
                "세운 시작시각은 필수입니다."
        );

        Objects.requireNonNull(
                endDateTimeExclusive,
                "세운 종료시각은 필수입니다."
        );

        if (
                !endDateTimeExclusive.isAfter(
                        startDateTimeInclusive
                )
        ) {

            throw new IllegalArgumentException(
                    "세운 종료시각은 시작시각보다 뒤여야 합니다."
            );
        }
    }

    public boolean contains(
            LocalDateTime targetDateTime
    ) {

        Objects.requireNonNull(
                targetDateTime,
                "세운 조회 기준시각은 필수입니다."
        );

        return !targetDateTime.isBefore(
                startDateTimeInclusive
        )
                &&
                targetDateTime.isBefore(
                        endDateTimeExclusive
                );
    }
}