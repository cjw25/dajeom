package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-2.
 *
 * 하나의 세운 내부에 존재하는
 * 12개 월운 간지 Sequence.
 *
 * =================================================
 *
 * 예:
 *
 * 2026 丙午年
 *
 * 1  庚寅
 * 2  辛卯
 * 3  壬辰
 * 4  癸巳
 * 5  甲午
 * 6  乙未
 * 7  丙申
 * 8  丁酉
 * 9  戊戌
 * 10 己亥
 * 11 庚子
 * 12 辛丑
 *
 * =================================================
 *
 * 절입구간은 15-1에서 계산한 결과를
 * 그대로 보존한다.
 */
public record MonthlyLuckSequenceAnalysis(

        AnnualLuckPeriod annualPeriod,

        MonthlyLuckBoundarySequenceAnalysis boundarySequence,

        List<MonthlyLuckPeriod> periods

) {

    private static final int
            MONTH_COUNT = 12;

    private static final int
            SEXAGENARY_CYCLE_SIZE = 60;

    public MonthlyLuckSequenceAnalysis {

        Objects.requireNonNull(
                annualPeriod,
                "월운 Sequence에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                boundarySequence,
                "월운 Sequence에는 15-1 BoundarySequence가 필요합니다."
        );

        periods =
                periods == null
                        ? List.of()
                        : List.copyOf(
                        periods
                );

        /*
         * =================================================
         * 동일 세운 Identity
         * =================================================
         */

        if (
                boundarySequence.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "월운 BoundarySequence와 월운 간지 Sequence가 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        /*
         * =================================================
         * 12개월
         * =================================================
         */

        if (
                periods.size()
                        != MONTH_COUNT
        ) {

            throw new IllegalArgumentException(
                    "하나의 세운에는 정확히 12개의 월운 Period가 필요합니다: "
                            + periods.size()
            );
        }

        if (
                boundarySequence.monthCount()
                        != MONTH_COUNT
        ) {

            throw new IllegalArgumentException(
                    "15-1 월운 BoundarySequence가 12개월이 아닙니다."
            );
        }

        /*
         * =================================================
         * Period 정합성
         * =================================================
         */

        for (
                int index = 0;
                index < periods.size();
                index++
        ) {

            MonthlyLuckPeriod period =
                    Objects.requireNonNull(
                            periods.get(index),
                            "월운 Period는 null일 수 없습니다."
                    );

            if (
                    period.annualPeriod()
                            != annualPeriod
            ) {

                throw new IllegalArgumentException(
                        "다른 세운의 MonthlyLuckPeriod가 포함되었습니다."
                );
            }

            if (
                    period.monthIndex()
                            != index
            ) {

                throw new IllegalArgumentException(
                        "월운 monthIndex 순서가 일치하지 않습니다. "
                                + "예상="
                                + index
                                + ", 실제="
                                + period.monthIndex()
                );
            }

            MonthlyLuckBoundaryAnalysis expectedBoundary =
                    boundarySequence
                            .monthByIndex(
                                    index
                            )
                            .orElseThrow();

            /*
             * 15-1에서 만든 정확한 Boundary 객체를
             * 그대로 참조해야 한다.
             */

            if (
                    period.boundaryAnalysis()
                            != expectedBoundary
            ) {

                throw new IllegalArgumentException(
                        "월운 Period가 15-1의 해당 Boundary 객체를 참조하지 않습니다: "
                                + index
                );
            }

            /*
             * =================================================
             * 육십갑자 연속성
             * =================================================
             */

            if (
                    index > 0
            ) {

                MonthlyLuckPeriod previous =
                        periods.get(
                                index - 1
                        );

                int expectedCycleIndex =
                        Math.floorMod(
                                previous.cycleIndex() + 1,
                                SEXAGENARY_CYCLE_SIZE
                        );

                if (
                        period.cycleIndex()
                                != expectedCycleIndex
                ) {

                    throw new IllegalArgumentException(
                            "월운 육십갑자 Sequence가 연속되지 않습니다: "
                                    + previous.pillar()
                                    + " -> "
                                    + period.pillar()
                    );
                }
            }

            /*
             * 시간 연속성.
             */

            if (
                    index > 0
            ) {

                MonthlyLuckPeriod previous =
                        periods.get(
                                index - 1
                        );

                if (
                        !previous
                                .endDateTimeExclusive()
                                .toInstant()
                                .equals(
                                        period
                                                .startDateTimeInclusive()
                                                .toInstant()
                                )
                ) {

                    throw new IllegalArgumentException(
                            "월운 Period의 시간구간이 연속되지 않습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * 전체 세운 범위와 일치
         * =================================================
         */

        MonthlyLuckPeriod first =
                periods.get(0);

        MonthlyLuckPeriod last =
                periods.get(
                        periods.size() - 1
                );

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
                    "첫 월운 시작시각이 세운 시작시각과 일치하지 않습니다."
            );
        }

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
                    "마지막 월운 종료시각이 세운 종료시각과 일치하지 않습니다."
            );
        }
    }

    public int sajuYear() {

        return annualPeriod
                .targetYear();
    }

    public int periodCount() {

        return periods.size();
    }

    public Optional<MonthlyLuckPeriod>
    periodByIndex(
            int monthIndex
    ) {

        if (
                monthIndex < 0
                        || monthIndex >= MONTH_COUNT
        ) {

            return Optional.empty();
        }

        return Optional.of(
                periods.get(
                        monthIndex
                )
        );
    }

    public Optional<MonthlyLuckPeriod>
    periodByOrder(
            int monthOrder
    ) {

        if (
                monthOrder < 1
                        || monthOrder > MONTH_COUNT
        ) {

            return Optional.empty();
        }

        return periodByIndex(
                monthOrder - 1
        );
    }

    /**
     * 특정 시점의 월운.
     */
    public Optional<MonthlyLuckPeriod>
    periodAt(
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

        List<MonthlyLuckPeriod> matched =
                periods
                        .stream()
                        .filter(
                                period ->
                                        period.contains(
                                                dateTime
                                        )
                        )
                        .toList();

        if (
                matched.size() > 1
        ) {

            throw new IllegalStateException(
                    "동일 시각에 월운 Period가 2개 이상 존재합니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }
}