package com.dajeom.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 하나의 월운 기간.
 *
 * 사용자 화면에 월운표를 보여주기 위한 객체가 아니다.
 *
 * 목적:
 * - 특정 시점의 월운 판정
 * - 원국 × 월운 분석
 * - 대운 × 월운 분석
 * - 세운 × 월운 분석
 * - 이후 Evidence 생성
 *
 * 기간:
 * [절입시각, 다음 절입시각)
 *
 * monthNumber:
 *
 * 1  = 寅
 * 2  = 卯
 * 3  = 辰
 * 4  = 巳
 * 5  = 午
 * 6  = 未
 * 7  = 申
 * 8  = 酉
 * 9  = 戌
 * 10 = 亥
 * 11 = 子
 * 12 = 丑
 */
public record WolwoonPeriod(

        int sajuYear,

        int monthNumber,

        Ganji ganji,

        LocalDateTime startDateTimeInclusive,

        LocalDateTime endDateTimeExclusive

) {

    public WolwoonPeriod {

        if (
                monthNumber < 1
                        || monthNumber > 12
        ) {

            throw new IllegalArgumentException(
                    "월운 월번호는 1~12여야 합니다."
            );
        }

        Objects.requireNonNull(
                ganji,
                "월운 간지는 필수입니다."
        );

        Objects.requireNonNull(
                startDateTimeInclusive,
                "월운 시작시각은 필수입니다."
        );

        Objects.requireNonNull(
                endDateTimeExclusive,
                "월운 종료시각은 필수입니다."
        );

        if (
                !endDateTimeExclusive.isAfter(
                        startDateTimeInclusive
                )
        ) {

            throw new IllegalArgumentException(
                    "월운 종료시각은 시작시각보다 뒤여야 합니다."
            );
        }
    }

    /**
     * 시작시각 포함,
     * 종료시각 제외.
     */
    public boolean contains(
            LocalDateTime targetDateTime
    ) {

        Objects.requireNonNull(
                targetDateTime,
                "월운 조회 기준시각은 필수입니다."
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