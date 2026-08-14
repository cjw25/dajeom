package com.dajeom.model;

import com.dajeom.type.DecadeLuckDirection;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 13차-3.
 *
 * 대운 간지 Sequence 분석 결과.
 *
 * =================================================
 *
 * sequenceAvailable = true
 *
 * 월주가 확정되어 하나의 대운 간지 Sequence를
 * 만들 수 있는 상태.
 *
 * =================================================
 *
 * sequenceAvailable = false
 *
 * 출생시간 미상 + 절입일 등으로
 * 월주 자체가 경계 불확실한 상태.
 *
 * 이 경우 하나의 대운 Sequence를
 * 억지로 확정하지 않는다.
 */
public record DecadeLuckSequenceAnalysis(

        DecadeLuckStartAnalysis startAnalysis,

        SexagenaryPillar referenceMonthPillar,

        List<DecadeLuckPeriod> periods,

        boolean sequenceAvailable,

        String unavailableReason

) {

    public DecadeLuckSequenceAnalysis {

        Objects.requireNonNull(
                startAnalysis,
                "13-2 DecadeLuckStartAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                referenceMonthPillar,
                "대운 Sequence의 기준 월주가 필요합니다."
        );

        periods =
                periods == null
                        ? List.of()
                        : List.copyOf(
                        periods
                );

        unavailableReason =
                unavailableReason == null
                        ? ""
                        : unavailableReason.trim();

        /*
         * =================================================
         * 계산 가능 상태
         * =================================================
         */

        if (
                sequenceAvailable
        ) {

            if (
                    periods.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "계산 가능한 대운 Sequence에는 최소 1개 대운이 필요합니다."
                );
            }

            if (
                    !unavailableReason.isBlank()
            ) {

                throw new IllegalArgumentException(
                        "계산 가능한 대운 Sequence에는 unavailableReason이 없어야 합니다."
                );
            }
        }

        /*
         * =================================================
         * 계산 불가 상태
         * =================================================
         */

        if (
                !sequenceAvailable
        ) {

            if (
                    !periods.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "계산 불가 대운 Sequence에는 대운 목록을 생성할 수 없습니다."
                );
            }

            if (
                    unavailableReason.isBlank()
            ) {

                throw new IllegalArgumentException(
                        "계산 불가 사유가 필요합니다."
                );
            }
        }

        /*
         * =================================================
         * 순번 및 간지 진행 검증
         * =================================================
         */

        if (
                sequenceAvailable
        ) {

            DecadeLuckDirection direction =
                    startAnalysis
                            .directionAnalysis()
                            .direction();

            for (
                    int index = 0;
                    index < periods.size();
                    index++
            ) {

                DecadeLuckPeriod period =
                        periods.get(
                                index
                        );

                int expectedSequenceNumber =
                        index + 1;

                if (
                        period.sequenceNumber()
                                != expectedSequenceNumber
                ) {

                    throw new IllegalArgumentException(
                            "대운 Sequence 순번이 연속되지 않습니다."
                    );
                }

                int expectedCycleIndex =
                        Math.floorMod(
                                referenceMonthPillar
                                        .cycleIndex()

                                        + direction
                                        .cycleStep()
                                        * expectedSequenceNumber,
                                60
                        );

                if (
                        period
                                .pillar()
                                .cycleIndex()
                                != expectedCycleIndex
                ) {

                    throw new IllegalArgumentException(
                            "대운 간지가 월주 기준 순·역행 Sequence와 일치하지 않습니다."
                    );
                }
            }
        }
    }

    public DecadeLuckDirection direction() {

        return startAnalysis
                .directionAnalysis()
                .direction();
    }

    public boolean forward() {

        return direction()
                .forward();
    }

    public boolean reverse() {

        return direction()
                .reverse();
    }

    public boolean exactTiming() {

        return startAnalysis
                .exact();
    }

    public boolean birthTimeUnknown() {

        return startAnalysis
                .birthTimeUnknown();
    }

    public int periodCount() {

        return periods.size();
    }

    public Optional<DecadeLuckPeriod> period(
            int sequenceNumber
    ) {

        if (
                sequenceNumber < 1
        ) {

            return Optional.empty();
        }

        return periods
                .stream()
                .filter(
                        period ->
                                period.sequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    /**
     * 정확한 출생시간일 때
     * 특정 시점의 대운.
     *
     * 첫 대운 시작 전이면 empty.
     */
    public Optional<DecadeLuckPeriod> exactPeriodAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !sequenceAvailable
                        || !exactTiming()
        ) {

            return Optional.empty();
        }

        return periods
                .stream()
                .filter(
                        period ->
                                period.containsExact(
                                        dateTime
                                )
                )
                .findFirst();
    }

    /**
     * 출생시간 미상일 경우
     * 해당 시점에 가능성이 있는 대운들을 반환.
     *
     * 교운 경계에서는 2개가 동시에
     * 반환될 수 있다.
     */
    public List<DecadeLuckPeriod> possiblePeriodsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !sequenceAvailable
        ) {

            return List.of();
        }

        return periods
                .stream()
                .filter(
                        period ->
                                period.possiblyActiveAt(
                                        dateTime
                                )
                )
                .toList();
    }

    public Optional<DecadeLuckPeriod> definitePeriodAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !sequenceAvailable
        ) {

            return Optional.empty();
        }

        return periods
                .stream()
                .filter(
                        period ->
                                period.definitelyActiveAt(
                                        dateTime
                                )
                )
                .findFirst();
    }
}