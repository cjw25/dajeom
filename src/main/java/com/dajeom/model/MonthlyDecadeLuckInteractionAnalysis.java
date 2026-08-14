package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-5.
 *
 * 하나의 월운과
 * 13차 대운 Sequence 사이의
 * 전체 Interaction 분석.
 *
 * =================================================
 *
 * 하나의 월운에도
 * 대운 Interaction이 2개 존재할 수 있다.
 *
 * 예:
 *
 * 월운 시작
 * ↓
 * 기존 대운
 * ↓
 * 월운 중간 교운
 * ↓
 * 다음 대운
 * ↓
 * 월운 종료
 *
 * =================================================
 *
 * 시간 미상이라면
 * possible / definite 범위까지 유지한다.
 */
public record MonthlyDecadeLuckInteractionAnalysis(

        MonthlyLuckPeriod monthlyPeriod,

        DecadeLuckAnalysis decadeLuckAnalysis,

        List<MonthlyDecadeLuckInteraction> interactions,

        boolean analysisAvailable,

        String unavailableReason

) {

    public MonthlyDecadeLuckInteractionAnalysis {

        Objects.requireNonNull(
                monthlyPeriod,
                "대운-월운 분석에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "대운-월운 분석에는 DecadeLuckAnalysis가 필요합니다."
        );

        interactions =
                interactions == null
                        ? List.of()
                        : List.copyOf(
                        interactions
                );

        unavailableReason =
                unavailableReason == null
                        ? ""
                        : unavailableReason.trim();

        if (
                analysisAvailable
        ) {

            if (
                    !decadeLuckAnalysis
                            .sequenceAvailable()
            ) {

                throw new IllegalArgumentException(
                        "대운 Sequence가 사용 불가인데 대운-월운 분석을 활성화할 수 없습니다."
                );
            }

            if (
                    !unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "분석 가능 상태에는 unavailableReason이 없어야 합니다."
                );
            }

            for (
                    MonthlyDecadeLuckInteraction interaction
                    : interactions
            ) {

                Objects.requireNonNull(
                        interaction,
                        "대운-월운 Interaction은 null일 수 없습니다."
                );

                if (
                        interaction.monthlyPeriod()
                                != monthlyPeriod
                ) {

                    throw new IllegalArgumentException(
                            "다른 MonthlyLuckPeriod의 Interaction이 포함되었습니다."
                    );
                }

                boolean knownPeriod =
                        decadeLuckAnalysis
                                .periods()
                                .stream()
                                .anyMatch(
                                        period ->
                                                period
                                                        == interaction
                                                        .decadePeriod()
                                );

                if (
                        !knownPeriod
                ) {

                    throw new IllegalArgumentException(
                            "13차 대운 Sequence에 없는 대운이 월운 Interaction에 포함되었습니다."
                    );
                }
            }
        }

        if (
                !analysisAvailable
        ) {

            if (
                    !interactions
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "분석 불가 상태에서는 대운-월운 Interaction을 생성할 수 없습니다."
                );
            }

            if (
                    unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운-월운 분석 불가 사유가 필요합니다."
                );
            }
        }
    }

    public int sajuYear() {

        return monthlyPeriod
                .sajuYear();
    }

    public int monthIndex() {

        return monthlyPeriod
                .monthIndex();
    }

    public boolean hasInteraction() {

        return !interactions
                .isEmpty();
    }

    public boolean multipleDecadesInMonthlyPeriod() {

        return interactions.size()
                > 1;
    }

    public boolean hasTimingUncertainty() {

        return interactions
                .stream()
                .anyMatch(
                        MonthlyDecadeLuckInteraction
                                ::timingUncertain
                );
    }

    public Optional<MonthlyDecadeLuckInteraction>
    interactionOfDecade(
            int sequenceNumber
    ) {

        return interactions
                .stream()
                .filter(
                        interaction ->
                                interaction
                                        .decadeSequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    /**
     * 해당 시각에 가능한 대운들.
     *
     * 교운 불확실 구간에서는
     * 2개가 반환될 수 있다.
     */
    public List<MonthlyDecadeLuckInteraction>
    possibleInteractionsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !analysisAvailable
                        || !monthlyPeriod.contains(
                        dateTime
                )
        ) {

            return List.of();
        }

        return interactions
                .stream()
                .filter(
                        interaction ->
                                interaction
                                        .overlap()
                                        .possiblyContains(
                                                dateTime
                                        )
                )
                .toList();
    }

    /**
     * 해당 시각에 확정 가능한 대운.
     *
     * 불확실성 구간이면 empty.
     */
    public Optional<MonthlyDecadeLuckInteraction>
    definiteInteractionAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !analysisAvailable
                        || !monthlyPeriod.contains(
                        dateTime
                )
        ) {

            return Optional.empty();
        }

        List<MonthlyDecadeLuckInteraction> matched =
                interactions
                        .stream()
                        .filter(
                                interaction ->
                                        interaction
                                                .overlap()
                                                .definitelyContains(
                                                        dateTime
                                                )
                        )
                        .toList();

        if (
                matched.size()
                        > 1
        ) {

            throw new IllegalStateException(
                    "동일 시각에 확정 대운-월운 Interaction이 2개 이상입니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }

    public boolean beforeFirstDecadeLuck() {

        if (
                !analysisAvailable
                        || hasInteraction()
                        || decadeLuckAnalysis
                        .periods()
                        .isEmpty()
        ) {

            return false;
        }

        DecadeLuckPeriod first =
                decadeLuckAnalysis
                        .periods()
                        .get(0);

        ZonedDateTime monthlyEnd =
                monthlyPeriod
                        .endDateTimeExclusive();

        ZonedDateTime firstPossibleStart =
                first
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                monthlyPeriod.zoneId()
                        );

        return !monthlyEnd
                .isAfter(
                        firstPossibleStart
                );
    }

    public boolean afterGeneratedDecadeRange() {

        if (
                !analysisAvailable
                        || hasInteraction()
                        || decadeLuckAnalysis
                        .periods()
                        .isEmpty()
        ) {

            return false;
        }

        List<DecadeLuckPeriod> periods =
                decadeLuckAnalysis
                        .periods();

        DecadeLuckPeriod last =
                periods.get(
                        periods.size() - 1
                );

        ZonedDateTime monthlyStart =
                monthlyPeriod
                        .startDateTimeInclusive();

        ZonedDateTime lastPossibleEnd =
                last
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                monthlyPeriod.zoneId()
                        );

        return !monthlyStart
                .isBefore(
                        lastPossibleEnd
                );
    }
}