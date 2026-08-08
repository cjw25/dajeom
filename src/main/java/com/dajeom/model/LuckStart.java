package com.dajeom.model;

import com.dajeom.type.LuckDirection;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 대운 시작 계산 결과.
 *
 * 포함:
 *
 * - 순행 / 역행
 * - 계산 기준이 된 절입
 * - 출생시각과 절입 사이 실제 시간
 * - 대운 시작 나이
 * - 실제 대운 시작 날짜/시간
 */
public record LuckStart(

        LuckDirection direction,

        SolarTermPoint referenceSolarTerm,

        /**
         * 출생시각과 기준 절입 사이의
         * 실제 시간 간격(초).
         */
        long sourceIntervalSeconds,

        LuckStartAge startAge,

        ZonedDateTime startDateTime,

        String ruleVersion

) {

    public LuckStart {

        Objects.requireNonNull(
                direction,
                "대운 방향은 필수입니다."
        );

        Objects.requireNonNull(
                referenceSolarTerm,
                "대운 시작 기준 절입은 필수입니다."
        );

        Objects.requireNonNull(
                startAge,
                "대운 시작 나이는 필수입니다."
        );

        Objects.requireNonNull(
                startDateTime,
                "대운 시작 시각은 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        if (sourceIntervalSeconds < 0L) {

            throw new IllegalArgumentException(
                    "절입까지의 시간 간격은 음수일 수 없습니다."
            );
        }
    }
}