package com.dajeom.model;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 정밀화 14차-2.
 *
 * 하나의 세운 Period.
 *
 * =================================================
 *
 * 포함:
 *
 * - 명리 연도
 * - 세운 간지
 * - 시작 입춘
 * - 다음 입춘
 *
 * =================================================
 *
 * 아직 포함하지 않는 것:
 *
 * - 십성
 * - 원국 관계
 * - 대운 관계
 * - 용신 Context
 * - 격국 Context
 *
 * 위 내용은 14-3 이후에서 결합한다.
 */
public record AnnualLuckPeriod(

        AnnualLuckBoundaryAnalysis boundaryAnalysis,

        SexagenaryPillar pillar

) {

    public AnnualLuckPeriod {

        Objects.requireNonNull(
                boundaryAnalysis,
                "세운 Period에는 14-1 BoundaryAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                pillar,
                "세운 Period에는 세운 간지가 필요합니다."
        );
    }

    /**
     * =================================================
     * 기본 연도
     * =================================================
     */

    public int targetYear() {

        return boundaryAnalysis
                .targetYear();
    }

    public ZoneId zoneId() {

        return boundaryAnalysis
                .zoneId();
    }

    /**
     * =================================================
     * 육십갑자
     * =================================================
     */

    public int cycleIndex() {

        return pillar
                .cycleIndex();
    }

    public com.dajeom.type.HeavenlyStem stem() {

        return pillar
                .stem();
    }

    public com.dajeom.type.EarthlyBranch branch() {

        return pillar
                .branch();
    }

    /**
     * =================================================
     * 세운 시간 범위
     * =================================================
     */

    public ZonedDateTime startDateTimeInclusive() {

        return boundaryAnalysis
                .startDateTimeInclusive();
    }

    public ZonedDateTime endDateTimeExclusive() {

        return boundaryAnalysis
                .endDateTimeExclusive();
    }

    /**
     * 특정 시각이 현재 세운 Period에 속하는가.
     */
    public boolean contains(
            ZonedDateTime dateTime
    ) {

        return boundaryAnalysis
                .contains(
                        dateTime
                );
    }

    /**
     * 정확한 입춘 시작 순간인가.
     */
    public boolean startsAt(
            ZonedDateTime dateTime
    ) {

        return boundaryAnalysis
                .startsAt(
                        dateTime
                );
    }
}