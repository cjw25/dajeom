package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.LuckCandidateCertainty;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 16차-3.
 *
 * 특정 조회 기준에서
 * 현재 세운일 가능성이 있는 하나의 Candidate.
 *
 * =================================================
 *
 * 세운 경계는:
 *
 * [해당 연도 입춘,
 *  다음 연도 입춘)
 *
 * =================================================
 *
 * certainty:
 *
 * DEFINITE
 *
 * 현재 조회정보 전체를 고려해도
 * 해당 세운이라고 확정 가능.
 *
 * =================================================
 *
 * POSSIBLE
 *
 * 해당 세운일 가능성은 있지만
 * 조회시간이 없어 확정할 수 없음.
 *
 * 대표적인 경우:
 *
 * 입춘 당일 DATE_ONLY 조회.
 *
 * =================================================
 *
 * 세운 Candidate에는 확률을 부여하지 않는다.
 */
public record AnnualLuckCandidate(

        AnnualLuckPeriod period,

        LuckCandidateCertainty certainty

) {

    public AnnualLuckCandidate {

        Objects.requireNonNull(
                period,
                "세운 Candidate에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                certainty,
                "세운 Candidate에는 LuckCandidateCertainty가 필요합니다."
        );
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public int targetYear() {

        return period
                .targetYear();
    }

    public SexagenaryPillar pillar() {

        return period
                .pillar();
    }

    public HeavenlyStem stem() {

        return period
                .stem();
    }

    public EarthlyBranch branch() {

        return period
                .branch();
    }

    public int cycleIndex() {

        return period
                .cycleIndex();
    }

    public ZoneId zoneId() {

        return period
                .zoneId();
    }

    /*
     * =================================================
     * 시간
     * =================================================
     */

    public ZonedDateTime startDateTimeInclusive() {

        return period
                .startDateTimeInclusive();
    }

    public ZonedDateTime endDateTimeExclusive() {

        return period
                .endDateTimeExclusive();
    }

    /*
     * =================================================
     * 확정 수준
     * =================================================
     */

    public boolean definite() {

        return certainty
                .definite();
    }

    public boolean possibleOnly() {

        return certainty
                .possibleOnly();
    }
}