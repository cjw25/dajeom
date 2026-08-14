package com.dajeom.model;

import com.dajeom.type.LuckTimelineDecadeState;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 16차-7.
 *
 * 16-5의 하나의 실제 Timeline Segment에
 *
 * 14차 AnnualLuckAnalysis
 * +
 * 15차 MonthlyLuckAnalysis
 *
 * 를 연결한 분석 객체.
 *
 * =================================================
 *
 * TimelineSegment
 *
 * = 어느 시간에 어떤 운 조합이 가능한가
 *
 * =================================================
 *
 * AnnualLuckAnalysis
 *
 * = 그 세운의 deterministic 분석
 *
 * =================================================
 *
 * MonthlyLuckAnalysis
 *
 * = 그 월운의 deterministic 분석
 *
 * =================================================
 *
 * 즉 이 객체부터:
 *
 * "시간 선택"
 * +
 * "명리 분석 결과"
 *
 * 가 연결된다.
 *
 * 하지만 기존 Evidence를 하나의 점수로
 * 합치지는 않는다.
 */
public record LuckIntegratedSegmentAnalysis(

        LuckTimelineSegment timelineSegment,

        AnnualLuckAnalysis annualLuckAnalysis,

        MonthlyLuckAnalysis monthlyLuckAnalysis

) {

    public LuckIntegratedSegmentAnalysis {

        Objects.requireNonNull(
                timelineSegment,
                "통합 Segment 분석에는 LuckTimelineSegment가 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "통합 Segment 분석에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyLuckAnalysis,
                "통합 Segment 분석에는 MonthlyLuckAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * AnnualLuckPeriod Identity
         * =================================================
         */

        if (
                timelineSegment.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "Timeline Segment와 AnnualLuckAnalysis가 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        /*
         * =================================================
         * MonthlyLuckPeriod Identity
         * =================================================
         */

        if (
                timelineSegment.monthlyPeriod()
                        != monthlyLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "Timeline Segment와 MonthlyLuckAnalysis가 서로 다른 MonthlyLuckPeriod를 참조합니다."
            );
        }

        /*
         * =================================================
         * 14차 ↔ 15차 Identity
         * =================================================
         */

        if (
                monthlyLuckAnalysis.annualLuckAnalysis()
                        != annualLuckAnalysis
        ) {

            throw new IllegalArgumentException(
                    "MonthlyLuckAnalysis가 현재 AnnualLuckAnalysis를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 일간
         * =================================================
         */

        if (
                annualLuckAnalysis.dayMaster()
                        != monthlyLuckAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "세운 분석과 월운 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 세운 Candidate
         * =================================================
         */

        if (
                timelineSegment
                        .annualCandidate()
                        .period()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "16-3 세운 Candidate와 14차 세운 분석의 Period가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 월운 Candidate
         * =================================================
         */

        if (
                timelineSegment
                        .monthlyCandidate()
                        .period()
                        != monthlyLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "16-4 월운 Candidate와 15차 월운 분석의 Period가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * Candidate
     * =================================================
     */

    public AnnualLuckCandidate annualCandidate() {

        return timelineSegment
                .annualCandidate();
    }

    public MonthlyLuckCandidate monthlyCandidate() {

        return timelineSegment
                .monthlyCandidate();
    }

    public Optional<DecadeLuckCandidate>
    decadeCandidate() {

        return timelineSegment
                .decadeCandidate();
    }

    /*
     * =================================================
     * 대운 State
     * =================================================
     */

    public LuckTimelineDecadeState decadeState() {

        return timelineSegment
                .decadeState();
    }

    public boolean hasActiveDecade() {

        return decadeState()
                .active();
    }

    public boolean beforeFirstDecadeLuck() {

        return decadeState()
                .beforeFirst();
    }

    public boolean afterGeneratedDecadeRange() {

        return decadeState()
                .afterGeneratedRange();
    }

    public boolean decadeUnavailable() {

        return decadeState()
                .unavailable();
    }

    /*
     * =================================================
     * 운 간지
     * =================================================
     */

    public Optional<SexagenaryPillar>
    decadePillar() {

        return timelineSegment
                .decadePillar();
    }

    public SexagenaryPillar annualPillar() {

        return annualLuckAnalysis
                .annualPillar();
    }

    public SexagenaryPillar monthlyPillar() {

        return monthlyLuckAnalysis
                .monthlyPillar();
    }

    /*
     * =================================================
     * 시간
     * =================================================
     */

    public LuckTimelineExtent extent() {

        return timelineSegment
                .extent();
    }

    public ZoneId zoneId() {

        return timelineSegment
                .zoneId();
    }

    public boolean exact() {

        return extent()
                .exact();
    }

    public boolean fullyDefinite() {

        return extent()
                .fullyDefinite();
    }

    public boolean timelineUncertaintyExists() {

        return timelineSegment
                .uncertaintyExists();
    }

    public boolean possiblyActiveAt(
            ZonedDateTime dateTime
    ) {

        return extent()
                .possiblyContains(
                        dateTime
                );
    }

    public boolean definitelyActiveAt(
            ZonedDateTime dateTime
    ) {

        return extent()
                .definitelyContains(
                        dateTime
                );
    }

    /*
     * =================================================
     * 분석 Convenience
     * =================================================
     */

    public PalaceAnalysis natalAnalysis() {

        return annualLuckAnalysis
                .natalAnalysis();
    }

    public DecadeLuckAnalysis decadeLuckAnalysis() {

        return annualLuckAnalysis
                .decadeLuckAnalysis();
    }

    public MonthlyLuckContextAnalysis monthlyContextAnalysis() {

        return monthlyLuckAnalysis
                .contextAnalysis();
    }

    public AnnualLuckContextAnalysis annualContextAnalysis() {

        return annualLuckAnalysis
                .contextAnalysis();
    }
}