package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 15차-2.
 *
 * 하나의 월운 Period.
 *
 * =================================================
 *
 * 포함:
 *
 * - 해당 세운
 * - 월운 절입 경계
 * - 월운 간지
 *
 * =================================================
 *
 * 예:
 *
 * 2026 丙午 세운
 *
 * 입춘 ~ 경칩
 *
 * 庚寅 월운
 *
 * =================================================
 *
 * 아직 포함하지 않는 것:
 *
 * - 월운 십성
 * - 원국 × 월운
 * - 대운 × 월운
 * - 세운 × 월운
 * - 종합 Context
 *
 * 위 내용은 15-3 이후 단계의 책임이다.
 */
public record MonthlyLuckPeriod(

        AnnualLuckPeriod annualPeriod,

        MonthlyLuckBoundaryAnalysis boundaryAnalysis,

        SexagenaryPillar pillar

) {

    public MonthlyLuckPeriod {

        Objects.requireNonNull(
                annualPeriod,
                "월운 Period에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                boundaryAnalysis,
                "월운 Period에는 MonthlyLuckBoundaryAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                pillar,
                "월운 Period에는 월운 간지가 필요합니다."
        );

        /*
         * =================================================
         * 연도 정합성
         * =================================================
         */

        if (
                annualPeriod.targetYear()
                        != boundaryAnalysis.sajuYear()
        ) {

            throw new IllegalArgumentException(
                    "월운과 세운의 명리연도가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Zone 정합성
         * =================================================
         */

        if (
                !annualPeriod.zoneId()
                        .equals(
                                boundaryAnalysis.zoneId()
                        )
        ) {

            throw new IllegalArgumentException(
                    "월운과 세운의 ZoneId가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 월운은 반드시 세운 범위 안에 있어야 한다.
         * =================================================
         */

        if (
                boundaryAnalysis
                        .startDateTimeInclusive()
                        .isBefore(
                                annualPeriod
                                        .startDateTimeInclusive()
                        )
        ) {

            throw new IllegalArgumentException(
                    "월운 시작시각이 세운 시작보다 빠릅니다."
            );
        }

        if (
                boundaryAnalysis
                        .endDateTimeExclusive()
                        .isAfter(
                                annualPeriod
                                        .endDateTimeExclusive()
                        )
        ) {

            throw new IllegalArgumentException(
                    "월운 종료시각이 세운 종료보다 늦습니다."
            );
        }
    }

    /*
     * =================================================
     * 연도
     * =================================================
     */

    public int sajuYear() {

        return annualPeriod
                .targetYear();
    }

    public SexagenaryPillar annualPillar() {

        return annualPeriod
                .pillar();
    }

    /*
     * =================================================
     * 월운 순서
     * =================================================
     */

    public int monthIndex() {

        return boundaryAnalysis
                .monthIndex();
    }

    public int monthOrder() {

        return boundaryAnalysis
                .monthOrder();
    }

    /*
     * =================================================
     * 월운 간지
     * =================================================
     */

    public HeavenlyStem stem() {

        return pillar
                .stem();
    }

    public EarthlyBranch branch() {

        return pillar
                .branch();
    }

    public int cycleIndex() {

        return pillar
                .cycleIndex();
    }

    /*
     * =================================================
     * 시간
     * =================================================
     */

    public ZoneId zoneId() {

        return boundaryAnalysis
                .zoneId();
    }

    public ZonedDateTime startDateTimeInclusive() {

        return boundaryAnalysis
                .startDateTimeInclusive();
    }

    public ZonedDateTime endDateTimeExclusive() {

        return boundaryAnalysis
                .endDateTimeExclusive();
    }

    public boolean contains(
            ZonedDateTime dateTime
    ) {

        return boundaryAnalysis
                .contains(
                        dateTime
                );
    }

    public boolean startsAt(
            ZonedDateTime dateTime
    ) {

        return boundaryAnalysis
                .startsAt(
                        dateTime
                );
    }
}