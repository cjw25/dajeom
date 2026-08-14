package com.dajeom.model;

import com.dajeom.type.LuckStartPrecision;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 13차-2.
 *
 * 대운 시작 나이·시점 최종 분석 결과.
 *
 * =================================================
 *
 * 정확한 출생시간:
 *
 * minimumCandidate
 * ==
 * maximumCandidate
 *
 * =================================================
 *
 * 시간 미상:
 *
 * 가능한 출생시각 범위에서
 * 최소/최대 Candidate를 보존한다.
 *
 * =================================================
 *
 * 절입일에 시간 미상:
 *
 * SOLAR_TERM_BOUNDARY_UNCERTAIN
 *
 * 으로 표시한다.
 *
 * 이 경우 13-3 대운 간지 Sequence에서도
 * 월주 경계 불확실성을 확인해야 한다.
 */
public record DecadeLuckStartAnalysis(

        DecadeLuckDirectionAnalysis directionAnalysis,

        LuckStartPrecision precision,

        LocalDate birthDate,

        ZoneId zoneId,

        LuckStartCandidate minimumCandidate,

        LuckStartCandidate maximumCandidate,

        boolean boundaryCrossing

) {

    public DecadeLuckStartAnalysis {

        Objects.requireNonNull(
                directionAnalysis,
                "13-1 대운 방향 분석이 필요합니다."
        );

        Objects.requireNonNull(
                precision,
                "대운 기산 Precision이 필요합니다."
        );

        Objects.requireNonNull(
                birthDate,
                "출생일이 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "대운 기산 ZoneId가 필요합니다."
        );

        Objects.requireNonNull(
                minimumCandidate,
                "최소 대운 시작 Candidate가 필요합니다."
        );

        Objects.requireNonNull(
                maximumCandidate,
                "최대 대운 시작 Candidate가 필요합니다."
        );

        if (
                minimumCandidate
                        .startAge()
                        .compareTo(
                                maximumCandidate
                                        .startAge()
                        ) > 0
        ) {

            throw new IllegalArgumentException(
                    "최소 대운 시작 나이는 최대값보다 클 수 없습니다."
            );
        }

        /*
         * =================================================
         * 13-1 절입 기준과 Candidate 정합성
         * =================================================
         */

        if (
                minimumCandidate.boundaryType()
                        != directionAnalysis
                        .startBoundaryType()

                        || maximumCandidate.boundaryType()
                        != directionAnalysis
                        .startBoundaryType()
        ) {

            throw new IllegalArgumentException(
                    "13-1의 대운 방향과 13-2 절입 기준이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 출생일 정합성
         * =================================================
         */

        if (
                !minimumCandidate
                        .assumedBirthDateTime()
                        .toLocalDate()
                        .equals(
                                birthDate
                        )

                        || !maximumCandidate
                        .assumedBirthDateTime()
                        .toLocalDate()
                        .equals(
                                birthDate
                        )
        ) {

            throw new IllegalArgumentException(
                    "대운 Candidate의 출생일이 분석 출생일과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * EXACT
         * =================================================
         */

        if (
                precision.exact()
        ) {

            if (
                    minimumCandidate
                            != maximumCandidate
            ) {

                throw new IllegalArgumentException(
                        "EXACT 분석은 최소/최대 Candidate가 동일 객체여야 합니다."
                );
            }

            if (
                    boundaryCrossing
            ) {

                throw new IllegalArgumentException(
                        "EXACT 분석에서는 시간 미상 절입 경계 상태를 사용할 수 없습니다."
                );
            }
        }

        /*
         * =================================================
         * 절입 경계 불확실성
         * =================================================
         */

        if (
                boundaryCrossing
                        != precision
                        .solarTermBoundaryUncertain()
        ) {

            throw new IllegalArgumentException(
                    "boundaryCrossing과 LuckStartPrecision 상태가 일치하지 않습니다."
            );
        }
    }

    public boolean exact() {

        return precision.exact();
    }

    public boolean birthTimeUnknown() {

        return precision.range();
    }

    public boolean hasRange() {

        return minimumCandidate
                .startAge()
                .compareTo(
                        maximumCandidate
                                .startAge()
                ) != 0;
    }

    public Optional<LuckStartCandidate> exactCandidate() {

        return exact()
                ? Optional.of(
                minimumCandidate
        )
                : Optional.empty();
    }

    public LuckStartAge minimumStartAge() {

        return minimumCandidate
                .startAge();
    }

    public LuckStartAge maximumStartAge() {

        return maximumCandidate
                .startAge();
    }

    public boolean solarTermBoundaryUncertain() {

        return precision
                .solarTermBoundaryUncertain();
    }

    /**
     * 13-3에서 하나의 월주 기준 대운 Sequence를
     * 확정하기 전에 재검토가 필요한 경우.
     */
    public boolean requiresSequenceBoundaryReview() {

        return boundaryCrossing;
    }
}