package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.LuckCandidateCertainty;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 16차-4.
 *
 * 특정 조회 기준에서
 * 현재 월운일 가능성이 있는 하나의 Candidate.
 *
 * =================================================
 *
 * 월운 Candidate는 반드시:
 *
 * AnnualLuckCandidate
 * +
 * MonthlyLuckPeriod
 *
 * 를 함께 가진다.
 *
 * =================================================
 *
 * 이유:
 *
 * 월운 간지는 어느 세운의 五虎遁으로
 * 계산되었는지가 중요하다.
 *
 * 특히 입춘 당일 DATE_ONLY에서는:
 *
 * 이전 세운의 마지막 丑월
 * +
 * 새 세운의 첫 寅월
 *
 * 이 동시에 가능할 수 있다.
 *
 * =================================================
 *
 * certainty:
 *
 * DEFINITE
 *
 * 조회 기준 전체를 고려해도
 * 해당 세운 + 해당 월운이 확정됨.
 *
 * POSSIBLE
 *
 * 해당 월운일 가능성이 있지만
 * 조회시간 또는 경계 때문에 확정할 수 없음.
 */
public record MonthlyLuckCandidate(

        AnnualLuckCandidate annualCandidate,

        MonthlyLuckPeriod period,

        LuckCandidateCertainty certainty

) {

    public MonthlyLuckCandidate {

        Objects.requireNonNull(
                annualCandidate,
                "월운 Candidate에는 AnnualLuckCandidate가 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "월운 Candidate에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                certainty,
                "월운 Candidate에는 LuckCandidateCertainty가 필요합니다."
        );

        /*
         * =================================================
         * 세운 Identity
         * =================================================
         *
         * 단순 targetYear 일치가 아니라
         * 실제 동일 AnnualLuckPeriod 객체를 요구한다.
         */

        if (
                period.annualPeriod()
                        != annualCandidate.period()
        ) {

            throw new IllegalArgumentException(
                    "MonthlyLuckPeriod가 현재 AnnualLuckCandidate의 세운에 속하지 않습니다."
            );
        }

        /*
         * =================================================
         * 확정 월운인데 상위 세운이 POSSIBLE일 수는 없다.
         * =================================================
         */

        if (
                certainty.definite()
                        && !annualCandidate.definite()
        ) {

            throw new IllegalArgumentException(
                    "세운 Candidate가 POSSIBLE인데 월운 Candidate를 DEFINITE로 만들 수 없습니다."
            );
        }

        /*
         * Zone도 동일해야 한다.
         */

        if (
                !period.zoneId()
                        .equals(
                                annualCandidate.zoneId()
                        )
        ) {

            throw new IllegalArgumentException(
                    "세운 Candidate와 월운 Candidate의 ZoneId가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 세운
     * =================================================
     */

    public int sajuYear() {

        return annualCandidate
                .targetYear();
    }

    public SexagenaryPillar annualPillar() {

        return annualCandidate
                .pillar();
    }

    public HeavenlyStem annualStem() {

        return annualCandidate
                .stem();
    }

    public EarthlyBranch annualBranch() {

        return annualCandidate
                .branch();
    }

    /*
     * =================================================
     * 월운
     * =================================================
     */

    public int monthIndex() {

        return period
                .monthIndex();
    }

    public int monthOrder() {

        return period
                .monthOrder();
    }

    public SexagenaryPillar monthlyPillar() {

        return period
                .pillar();
    }

    public HeavenlyStem monthlyStem() {

        return period
                .stem();
    }

    public EarthlyBranch monthlyBranch() {

        return period
                .branch();
    }

    public int cycleIndex() {

        return period
                .cycleIndex();
    }

    /*
     * =================================================
     * 시간
     * =================================================
     */

    public ZoneId zoneId() {

        return period
                .zoneId();
    }

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