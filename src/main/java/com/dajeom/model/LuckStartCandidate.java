package com.dajeom.model;

import com.dajeom.type.LuckStartBoundaryType;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 13차-2.
 *
 * 하나의 출생시각을 가정했을 때의
 * 대운 기산 계산 Evidence.
 *
 * =================================================
 *
 * 정확한 출생시간:
 *
 * candidate 1개
 *
 * =================================================
 *
 * 출생시간 미상:
 *
 * 여러 Candidate를 계산한 뒤
 * 최소/최대 범위를 구한다.
 *
 * =================================================
 *
 * 보존 정보:
 *
 * - 계산에 사용한 출생시각
 * - 사용한 절입
 * - 출생 ↔ 절입 Duration
 * - 환산된 시작 나이
 * - 실제 첫 대운 시작 DateTime
 */
public record LuckStartCandidate(

        ZonedDateTime assumedBirthDateTime,

        SolarTermPoint boundaryPoint,

        LuckStartBoundaryType boundaryType,

        Duration sourceDuration,

        LuckStartAge startAge,

        ZonedDateTime firstLuckStartDateTime

) {

    public LuckStartCandidate {

        Objects.requireNonNull(
                assumedBirthDateTime,
                "대운 기산 Candidate에는 출생시각이 필요합니다."
        );

        Objects.requireNonNull(
                boundaryPoint,
                "대운 기산 Candidate에는 절입이 필요합니다."
        );

        Objects.requireNonNull(
                boundaryType,
                "대운 기산 Candidate에는 절입 기준이 필요합니다."
        );

        Objects.requireNonNull(
                sourceDuration,
                "대운 기산 Candidate에는 출생-절입 Duration이 필요합니다."
        );

        Objects.requireNonNull(
                startAge,
                "대운 시작 나이가 필요합니다."
        );

        Objects.requireNonNull(
                firstLuckStartDateTime,
                "첫 대운 시작시각이 필요합니다."
        );

        if (
                sourceDuration.isNegative()
        ) {

            throw new IllegalArgumentException(
                    "대운 기산 Duration은 음수일 수 없습니다."
            );
        }

        ZonedDateTime boundaryDateTime =
                boundaryPoint.dateTime();

        Objects.requireNonNull(
                boundaryDateTime,
                "SolarTermPoint의 절입시각이 필요합니다."
        );

        /*
         * =================================================
         * 순행용 다음 절입
         * =================================================
         */

        if (
                boundaryType.nextBoundary()
                        && !boundaryDateTime
                        .isAfter(
                                assumedBirthDateTime
                        )
        ) {

            throw new IllegalArgumentException(
                    "NEXT_MONTH_BOUNDARY는 출생시각 이후여야 합니다."
            );
        }

        /*
         * =================================================
         * 역행용 이전 절입
         *
         * exact boundary는 허용한다.
         * =================================================
         */

        if (
                boundaryType.previousBoundary()
                        && boundaryDateTime
                        .isAfter(
                                assumedBirthDateTime
                        )
        ) {

            throw new IllegalArgumentException(
                    "PREVIOUS_MONTH_BOUNDARY는 출생시각 이후일 수 없습니다."
            );
        }

        if (
                firstLuckStartDateTime
                        .isBefore(
                                assumedBirthDateTime
                        )
        ) {

            throw new IllegalArgumentException(
                    "첫 대운 시작시각은 출생시각 이전일 수 없습니다."
            );
        }
    }

    public String solarTermName() {

        return boundaryPoint
                .name();
    }

    public ZonedDateTime solarTermDateTime() {

        return boundaryPoint
                .dateTime();
    }

    public long sourceSeconds() {

        return sourceDuration
                .getSeconds();
    }

    public long sourceMinutes() {

        return sourceDuration
                .toMinutes();
    }

    public boolean zeroStart() {

        return startAge.zero();
    }
}