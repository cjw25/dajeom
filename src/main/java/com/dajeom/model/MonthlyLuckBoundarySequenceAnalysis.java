package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-1.
 *
 * 하나의 세운 안에 존재하는
 * 12개 월운 절입구간 전체.
 *
 * =================================================
 *
 * 예:
 *
 * 2026 세운
 *
 * 2026 입춘
 * ↓
 * monthIndex 0
 *
 * 경칩
 * ↓
 * monthIndex 1
 *
 * ...
 *
 * 2027 소한
 * ↓
 * monthIndex 11
 *
 * 2027 입춘
 * ↓
 * 2026 세운 종료
 *
 * =================================================
 *
 * 중요한 점:
 *
 * monthIndex 11인 丑월은
 * 달력상 다음 해 1월에 시작하지만
 *
 * 다음 입춘 전이므로 여전히
 * 이전 명리연도에 속한다.
 */
public record MonthlyLuckBoundarySequenceAnalysis(

        AnnualLuckPeriod annualPeriod,

        List<MonthlyLuckBoundaryAnalysis> months

) {

    private static final int MONTH_COUNT = 12;

    public MonthlyLuckBoundarySequenceAnalysis {

        Objects.requireNonNull(
                annualPeriod,
                "월운 경계 Sequence에는 AnnualLuckPeriod가 필요합니다."
        );

        months =
                months == null
                        ? List.of()
                        : List.copyOf(
                        months
                );

        /*
         * =================================================
         * 정확히 12개
         * =================================================
         */

        if (
                months.size()
                        != MONTH_COUNT
        ) {

            throw new IllegalArgumentException(
                    "하나의 세운에는 정확히 12개의 월운 절입구간이 필요합니다: "
                            + months.size()
            );
        }

        /*
         * =================================================
         * 각 월 검증
         * =================================================
         */

        for (
                int index = 0;
                index < months.size();
                index++
        ) {

            MonthlyLuckBoundaryAnalysis month =
                    Objects.requireNonNull(
                            months.get(index),
                            "월운 경계는 null일 수 없습니다."
                    );

            /*
             * 세운 연도와 동일해야 한다.
             */

            if (
                    month.sajuYear()
                            != annualPeriod.targetYear()
            ) {

                throw new IllegalArgumentException(
                        "월운 명리연도와 세운 targetYear가 일치하지 않습니다."
                );
            }

            /*
             * 0~11 순서.
             */

            if (
                    month.monthIndex()
                            != index
            ) {

                throw new IllegalArgumentException(
                        "월운 monthIndex 순서가 잘못되었습니다. "
                                + "예상="
                                + index
                                + ", 실제="
                                + month.monthIndex()
                );
            }

            if (
                    !month.zoneId()
                            .equals(
                                    annualPeriod.zoneId()
                            )
            ) {

                throw new IllegalArgumentException(
                        "월운 ZoneId와 세운 ZoneId가 일치하지 않습니다."
                );
            }

            /*
             * 앞 월 종료 == 다음 월 시작
             */

            if (
                    index > 0
            ) {

                MonthlyLuckBoundaryAnalysis previous =
                        months.get(
                                index - 1
                        );

                if (
                        !previous
                                .endDateTimeExclusive()
                                .toInstant()
                                .equals(
                                        month
                                                .startDateTimeInclusive()
                                                .toInstant()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "월운 절입 Sequence가 연속되지 않습니다: "
                                    + (index - 1)
                                    + " -> "
                                    + index
                    );
                }
            }
        }

        /*
         * =================================================
         * 첫 월 시작 == 세운 입춘
         * =================================================
         */

        MonthlyLuckBoundaryAnalysis first =
                months.get(0);

        if (
                !first
                        .startDateTimeInclusive()
                        .toInstant()
                        .equals(
                                annualPeriod
                                        .startDateTimeInclusive()
                                        .toInstant()
                        )
        ) {

            throw new IllegalArgumentException(
                    "첫 월운 시작시각이 세운 시작 입춘과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 마지막 월 종료 == 다음 세운 입춘
         * =================================================
         */

        MonthlyLuckBoundaryAnalysis last =
                months.get(
                        months.size() - 1
                );

        if (
                !last
                        .endDateTimeExclusive()
                        .toInstant()
                        .equals(
                                annualPeriod
                                        .endDateTimeExclusive()
                                        .toInstant()
                        )
        ) {

            throw new IllegalArgumentException(
                    "마지막 월운 종료시각이 다음 세운 입춘과 일치하지 않습니다."
            );
        }
    }

    public int sajuYear() {

        return annualPeriod
                .targetYear();
    }

    public int monthCount() {

        return months.size();
    }

    /**
     * monthIndex 0~11.
     */
    public Optional<MonthlyLuckBoundaryAnalysis>
    monthByIndex(
            int monthIndex
    ) {

        if (
                monthIndex < 0
                        || monthIndex >= MONTH_COUNT
        ) {

            return Optional.empty();
        }

        return Optional.of(
                months.get(
                        monthIndex
                )
        );
    }

    /**
     * 월운 순번 1~12.
     */
    public Optional<MonthlyLuckBoundaryAnalysis>
    monthByOrder(
            int monthOrder
    ) {

        if (
                monthOrder < 1
                        || monthOrder > MONTH_COUNT
        ) {

            return Optional.empty();
        }

        return monthByIndex(
                monthOrder - 1
        );
    }

    /**
     * 특정 실제 시점의 월운.
     */
    public Optional<MonthlyLuckBoundaryAnalysis>
    monthAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !annualPeriod.contains(
                        dateTime
                )
        ) {

            return Optional.empty();
        }

        List<MonthlyLuckBoundaryAnalysis> matched =
                months
                        .stream()
                        .filter(
                                month ->
                                        month.contains(
                                                dateTime
                                        )
                        )
                        .toList();

        if (
                matched.size()
                        > 1
        ) {

            throw new IllegalStateException(
                    "동일 시각에 월운 절입구간이 2개 이상 존재합니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }
}