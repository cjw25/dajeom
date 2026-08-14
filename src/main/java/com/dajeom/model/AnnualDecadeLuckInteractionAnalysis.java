package com.dajeom.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 14차-5.
 *
 * 특정 세운과
 * 대운 전체 Sequence의 Interaction 분석.
 *
 * =================================================
 *
 * 하나의 세운에 Interaction이
 * 2개 이상 존재할 수 있다.
 *
 * 이유:
 *
 * 1.
 * 정확한 교운시점이
 *    세운 기간 중간에 존재하는 경우.
 *
 * 2.
 * 출생시간 미상으로
 *    대운 교체시점이 범위인 경우.
 *
 * =================================================
 *
 * 따라서:
 *
 * "세운 하나 = 대운 하나"
 *
 * 로 강제하지 않는다.
 */
public record AnnualDecadeLuckInteractionAnalysis(

        AnnualLuckPeriod annualPeriod,

        DecadeLuckAnalysis decadeLuckAnalysis,

        List<AnnualDecadeLuckInteraction> interactions,

        boolean analysisAvailable,

        String unavailableReason

) {

    public AnnualDecadeLuckInteractionAnalysis {

        Objects.requireNonNull(
                annualPeriod,
                "대운-세운 분석에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "대운-세운 분석에는 DecadeLuckAnalysis가 필요합니다."
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
                        "대운 Sequence가 불확실한데 대운-세운 분석을 활성화할 수 없습니다."
                );
            }

            if (
                    !unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "분석 가능한 상태에는 unavailableReason이 없어야 합니다."
                );
            }

            for (
                    AnnualDecadeLuckInteraction interaction
                    : interactions
            ) {

                if (
                        interaction.annualPeriod()
                                != annualPeriod
                ) {

                    throw new IllegalArgumentException(
                            "다른 세운의 Interaction이 포함되었습니다."
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
                            "13차 대운 Sequence에 없는 Period가 세운 Interaction에 포함되었습니다."
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
                        "분석 불가 상태에서는 대운-세운 Interaction을 생성할 수 없습니다."
                );
            }

            if (
                    unavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운-세운 분석 불가 사유가 필요합니다."
                );
            }
        }
    }

    public int targetYear() {

        return annualPeriod
                .targetYear();
    }

    public boolean hasInteraction() {

        return !interactions
                .isEmpty();
    }

    /**
     * 한 세운 기간 중 실제 대운 교체 또는
     * 교운시점 불확실성 때문에
     * 여러 대운이 관련되는 경우.
     */
    public boolean multipleDecadesInAnnualPeriod() {

        return interactions.size()
                > 1;
    }

    public boolean hasTimingUncertainty() {

        return interactions
                .stream()
                .anyMatch(
                        AnnualDecadeLuckInteraction
                                ::timingUncertain
                );
    }

    public Optional<AnnualDecadeLuckInteraction>
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
     * 특정 순간에 가능성이 있는 대운-세운 Interaction.
     *
     * 출생시간 미상 교운 경계에서는
     * 2개가 반환될 수 있다.
     */
    public List<AnnualDecadeLuckInteraction>
    possibleInteractionsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !analysisAvailable
                        || !annualPeriod.contains(
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
     * 해당 순간에 확정 가능한 대운.
     *
     * 불확실성 구간이면 empty.
     */
    public Optional<AnnualDecadeLuckInteraction>
    definiteInteractionAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !analysisAvailable
                        || !annualPeriod.contains(
                        dateTime
                )
        ) {

            return Optional.empty();
        }

        List<AnnualDecadeLuckInteraction> matched =
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
                    "동일 시각에 확정 대운이 2개 이상입니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }

    /**
     * 첫 대운 시작 전 세운인지.
     *
     * Sequence가 존재하더라도
     * 첫 교운 이전일 수 있다.
     */
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

        ZonedDateTime annualEnd =
                annualPeriod
                        .endDateTimeExclusive();

        ZonedDateTime firstPossibleStart =
                first
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                annualPeriod.zoneId()
                        );

        return !annualEnd
                .isAfter(
                        firstPossibleStart
                );
    }

    /**
     * 생성된 대운 범위를 이미 벗어난 세운인지.
     *
     * 기본 10개만 생성했다면
     * 그 이후 연도는 여기 해당할 수 있다.
     */
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

        ZonedDateTime annualStart =
                annualPeriod
                        .startDateTimeInclusive();

        ZonedDateTime lastPossibleEnd =
                last
                        .latestEndDateTimeExclusive()
                        .withZoneSameInstant(
                                annualPeriod.zoneId()
                        );

        return !annualStart
                .isBefore(
                        lastPossibleEnd
                );
    }
}