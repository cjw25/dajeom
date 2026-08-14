package com.dajeom.model;

import com.dajeom.type.LuckCandidateCertainty;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-2.
 *
 * 특정 조회 기준에서
 * 현재 대운일 가능성이 있는 하나의 Candidate.
 *
 * =================================================
 *
 * Candidate가 존재한다는 것은:
 *
 * 현재 LuckReferenceContext와
 * 해당 대운의 possible window가
 * 실제로 교차한다는 의미다.
 *
 * =================================================
 *
 * certainty:
 *
 * DEFINITE
 * -> 현재 정보만으로 이 대운이 확정됨.
 *
 * POSSIBLE
 * -> 이 대운일 가능성은 있지만
 *    확정할 수 없음.
 *
 * =================================================
 *
 * 확률값은 사용하지 않는다.
 */
public record DecadeLuckCandidate(

        DecadeLuckPeriod period,

        LuckCandidateCertainty certainty

) {

    public DecadeLuckCandidate {

        Objects.requireNonNull(
                period,
                "대운 Candidate에는 DecadeLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                certainty,
                "대운 Candidate에는 Certainty가 필요합니다."
        );
    }

    public int sequenceNumber() {

        return period
                .sequenceNumber();
    }

    public SexagenaryPillar pillar() {

        return period
                .pillar();
    }

    public boolean definite() {

        return certainty
                .definite();
    }

    public boolean possibleOnly() {

        return certainty
                .possibleOnly();
    }

    /*
     * =================================================
     * 대운 Possible Window
     * =================================================
     *
     * 대운 시작시점이 불확실할 때:
     *
     * 가장 빠른 시작
     * ~
     * 가장 늦은 종료
     *
     * 가 해당 대운의 possible window다.
     */

    public ZonedDateTime possibleStartInclusive(
            ZoneId zoneId
    ) {

        Objects.requireNonNull(
                zoneId,
                "ZoneId가 필요합니다."
        );

        return period
                .earliestStartDateTime()
                .withZoneSameInstant(
                        zoneId
                );
    }

    public ZonedDateTime possibleEndExclusive(
            ZoneId zoneId
    ) {

        Objects.requireNonNull(
                zoneId,
                "ZoneId가 필요합니다."
        );

        return period
                .latestEndDateTimeExclusive()
                .withZoneSameInstant(
                        zoneId
                );
    }

    /*
     * =================================================
     * 대운 Definite Window
     * =================================================
     *
     * 가장 늦은 시작
     * ~
     * 가장 빠른 종료
     *
     * 구간은 어떤 교운 후보를 적용해도
     * 해당 대운임이 확정되는 구간이다.
     */

    public Optional<ZonedDateTime>
    definiteStartInclusive(
            ZoneId zoneId
    ) {

        Objects.requireNonNull(
                zoneId,
                "ZoneId가 필요합니다."
        );

        ZonedDateTime start =
                period
                        .latestStartDateTime()
                        .withZoneSameInstant(
                                zoneId
                        );

        ZonedDateTime end =
                period
                        .earliestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                zoneId
                        );

        if (
                !end.isAfter(
                        start
                )
        ) {

            return Optional.empty();
        }

        return Optional.of(
                start
        );
    }

    public Optional<ZonedDateTime>
    definiteEndExclusive(
            ZoneId zoneId
    ) {

        Objects.requireNonNull(
                zoneId,
                "ZoneId가 필요합니다."
        );

        ZonedDateTime start =
                period
                        .latestStartDateTime()
                        .withZoneSameInstant(
                                zoneId
                        );

        ZonedDateTime end =
                period
                        .earliestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                zoneId
                        );

        if (
                !end.isAfter(
                        start
                )
        ) {

            return Optional.empty();
        }

        return Optional.of(
                end
        );
    }

    public boolean hasDefiniteWindow(
            ZoneId zoneId
    ) {

        return definiteStartInclusive(
                zoneId
        )
                .isPresent();
    }
}