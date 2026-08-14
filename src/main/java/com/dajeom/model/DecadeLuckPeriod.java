package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 13차-3.
 *
 * 하나의 10년 대운 구간.
 *
 * =================================================
 *
 * sequenceNumber:
 *
 * 1 = 첫 번째 대운
 * 2 = 두 번째 대운
 * ...
 *
 * =================================================
 *
 * 시간 미상인 경우:
 *
 * start/end를 한 시점으로 확정하지 않고
 *
 * earliest
 * latest
 *
 * 범위로 보존한다.
 *
 * =================================================
 *
 * 종료시점은 Exclusive다.
 *
 * 예:
 *
 * [2022-05-01 10:00,
 *  2032-05-01 10:00)
 *
 * 형태다.
 *
 * 따라서 마지막 날 23:59:59 같은
 * 인위적인 종료값을 만들지 않는다.
 */
public record DecadeLuckPeriod(

        int sequenceNumber,

        SexagenaryPillar pillar,

        LuckStartAge minimumStartAge,

        LuckStartAge maximumStartAge,

        ZonedDateTime earliestStartDateTime,

        ZonedDateTime latestStartDateTime,

        ZonedDateTime earliestEndDateTimeExclusive,

        ZonedDateTime latestEndDateTimeExclusive,

        boolean exactTiming

) {

    public DecadeLuckPeriod {

        if (
                sequenceNumber < 1
        ) {

            throw new IllegalArgumentException(
                    "대운 순번은 1 이상이어야 합니다."
            );
        }

        Objects.requireNonNull(
                pillar,
                "대운 간지가 필요합니다."
        );

        Objects.requireNonNull(
                minimumStartAge,
                "대운 최소 시작 나이가 필요합니다."
        );

        Objects.requireNonNull(
                maximumStartAge,
                "대운 최대 시작 나이가 필요합니다."
        );

        Objects.requireNonNull(
                earliestStartDateTime,
                "대운 최소 시작시점이 필요합니다."
        );

        Objects.requireNonNull(
                latestStartDateTime,
                "대운 최대 시작시점이 필요합니다."
        );

        Objects.requireNonNull(
                earliestEndDateTimeExclusive,
                "대운 최소 종료시점이 필요합니다."
        );

        Objects.requireNonNull(
                latestEndDateTimeExclusive,
                "대운 최대 종료시점이 필요합니다."
        );

        if (
                minimumStartAge
                        .compareTo(
                                maximumStartAge
                        ) > 0
        ) {

            throw new IllegalArgumentException(
                    "대운 최소 시작 나이가 최대 시작 나이보다 클 수 없습니다."
            );
        }

        if (
                earliestStartDateTime
                        .isAfter(
                                latestStartDateTime
                        )
        ) {

            throw new IllegalArgumentException(
                    "대운 earliestStart가 latestStart보다 늦을 수 없습니다."
            );
        }

        if (
                !earliestEndDateTimeExclusive
                        .equals(
                                earliestStartDateTime
                                        .plusYears(
                                                10
                                        )
                        )
        ) {

            throw new IllegalArgumentException(
                    "대운 earliest 종료시점은 시작시점 + 10년이어야 합니다."
            );
        }

        if (
                !latestEndDateTimeExclusive
                        .equals(
                                latestStartDateTime
                                        .plusYears(
                                                10
                                        )
                        )
        ) {

            throw new IllegalArgumentException(
                    "대운 latest 종료시점은 시작시점 + 10년이어야 합니다."
            );
        }

        boolean actuallyExact =
                minimumStartAge
                        .equals(
                                maximumStartAge
                        )

                        && earliestStartDateTime
                        .equals(
                                latestStartDateTime
                        )

                        && earliestEndDateTimeExclusive
                        .equals(
                                latestEndDateTimeExclusive
                        );

        if (
                exactTiming
                        != actuallyExact
        ) {

            throw new IllegalArgumentException(
                    "exactTiming과 실제 대운 시점 범위가 일치하지 않습니다."
            );
        }
    }

    /**
     * 정확한 교운시점을 알고 있을 때
     * 해당 시점이 이 대운에 속하는지 확인.
     */
    public boolean containsExact(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !exactTiming
        ) {

            return false;
        }

        return !dateTime
                .isBefore(
                        earliestStartDateTime
                )

                && dateTime
                .isBefore(
                        earliestEndDateTimeExclusive
                );
    }

    /**
     * 시간 미상 범위가 있을 때
     * 이 대운일 가능성이 있는 영역.
     */
    public boolean possiblyActiveAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
        ) {

            return false;
        }

        return !dateTime
                .isBefore(
                        earliestStartDateTime
                )

                && dateTime
                .isBefore(
                        latestEndDateTimeExclusive
                );
    }

    /**
     * 시간 미상이어도
     * 어떤 가능한 출생시간을 적용하더라도
     * 이 대운에 들어가는 구간.
     */
    public boolean definitelyActiveAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
        ) {

            return false;
        }

        return !dateTime
                .isBefore(
                        latestStartDateTime
                )

                && dateTime
                .isBefore(
                        earliestEndDateTimeExclusive
                );
    }
}