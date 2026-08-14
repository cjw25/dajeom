package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthlyLuckContextAxis;
import com.dajeom.type.MonthlyLuckContextStatus;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-7.
 *
 * 월운 교차 Context 최종 분석.
 *
 * =================================================
 *
 * monthlySignals
 *
 * 현재 월운 전체에서 확정 가능한 Context.
 *
 * 대운 Sequence가 불확실해도 보존한다.
 *
 * =================================================
 *
 * decadeContexts
 *
 * 현재 월운 안에서 실제로 겹치는
 * 각 대운 Segment별 Context.
 *
 * =================================================
 *
 * 따라서:
 *
 * 월운 분석 가능
 * !=
 * 대운 포함 월운 Context 가능
 *
 * 이 두 상태를 분리한다.
 */
public record MonthlyLuckContextAnalysis(

        HeavenlyStem dayMaster,

        AnnualLuckAnalysis annualLuckAnalysis,

        MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

        MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

        MonthlyDecadeLuckInteractionAnalysis decadeInteractionAnalysis,

        MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis,

        List<MonthlyLuckContextSignal> monthlySignals,

        List<MonthlyLuckDecadeContextAnalysis> decadeContexts,

        boolean decadeContextAvailable,

        String decadeContextUnavailableReason

) {

    public MonthlyLuckContextAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "월운 Context 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "14차 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyTenGodAnalysis,
                "15-3 MonthlyLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "15-4 MonthlyLuckNatalRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeInteractionAnalysis,
                "15-5 MonthlyDecadeLuckInteractionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                annualRelationAnalysis,
                "15-6 MonthlyAnnualLuckRelationAnalysis가 필요합니다."
        );

        monthlySignals =
                monthlySignals == null
                        ? List.of()
                        : List.copyOf(
                        monthlySignals
                );

        decadeContexts =
                decadeContexts == null
                        ? List.of()
                        : List.copyOf(
                        decadeContexts
                );

        decadeContextUnavailableReason =
                decadeContextUnavailableReason == null
                        ? ""
                        : decadeContextUnavailableReason.trim();

        /*
         * =================================================
         * 월운 Period Identity
         * =================================================
         */

        MonthlyLuckPeriod monthlyPeriod =
                monthlyTenGodAnalysis
                        .period();

        if (
                natalRelationAnalysis.period()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-3과 15-4가 서로 다른 MonthlyLuckPeriod를 참조합니다."
            );
        }

        if (
                decadeInteractionAnalysis.monthlyPeriod()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-5가 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                annualRelationAnalysis.monthlyPeriod()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-6이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * AnnualLuckPeriod Identity
         * =================================================
         */

        if (
                monthlyPeriod.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "월운과 14차 AnnualLuckAnalysis가 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        if (
                annualRelationAnalysis.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "15-6 세운-월운 관계가 현재 AnnualLuckAnalysis를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 일간
         * =================================================
         */

        if (
                annualLuckAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "14차 세운 분석과 월운 Context의 일간이 일치하지 않습니다."
            );
        }

        if (
                monthlyTenGodAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "15-3 월운 십성 분석과 월운 Context의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "15-4 원국-월운 관계와 월운 Context의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 Identity
         * =================================================
         */

        if (
                natalRelationAnalysis.natalAnalysis()
                        != annualLuckAnalysis.natalAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "14차와 15차가 서로 다른 원국 PalaceAnalysis를 참조합니다."
            );
        }

        /*
         * =================================================
         * 대운 분석 Identity
         * =================================================
         */

        if (
                decadeInteractionAnalysis.decadeLuckAnalysis()
                        != annualLuckAnalysis.decadeLuckAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "14차와 15차가 서로 다른 DecadeLuckAnalysis를 참조합니다."
            );
        }

        /*
         * 같은 대운 분석을 사용하므로
         * 사용 가능 상태도 일치해야 한다.
         */
        if (
                decadeInteractionAnalysis.analysisAvailable()
                        != annualLuckAnalysis.decadeContextAvailable()
        ) {

            throw new IllegalArgumentException(
                    "세운과 월운의 대운 Context 사용 가능 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Segment Context 상태
         * =================================================
         */

        if (
                decadeContextAvailable
        ) {

            if (
                    !decadeInteractionAnalysis
                            .analysisAvailable()
            ) {

                throw new IllegalArgumentException(
                        "15-5 분석이 불가능한데 월운 대운 Segment Context를 활성화할 수 없습니다."
                );
            }

            if (
                    !decadeContextUnavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운 Context 사용 가능 상태에는 unavailableReason이 없어야 합니다."
                );
            }

            if (
                    decadeContexts.size()
                            != decadeInteractionAnalysis
                            .interactions()
                            .size()
            ) {

                throw new IllegalArgumentException(
                        "15-5 Interaction 수와 15-7 Segment Context 수가 일치하지 않습니다."
                );
            }

            for (
                    MonthlyDecadeLuckInteraction interaction
                    : decadeInteractionAnalysis
                    .interactions()
            ) {

                boolean found =
                        decadeContexts
                                .stream()
                                .anyMatch(
                                        context ->
                                                context.interaction()
                                                        == interaction
                                );

                if (
                        !found
                ) {

                    throw new IllegalArgumentException(
                            "15-5 Interaction에 대응하는 15-7 Context가 없습니다."
                    );
                }
            }
        }

        if (
                !decadeContextAvailable
        ) {

            if (
                    !decadeContexts
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "대운 Context 사용 불가 상태에서는 월운 Segment Context를 생성할 수 없습니다."
                );
            }

            if (
                    decadeContextUnavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "월운 대운 Context 사용 불가 사유가 필요합니다."
                );
            }
        }

        for (
                MonthlyLuckContextSignal signal
                : monthlySignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "monthlySignal은 null일 수 없습니다."
            );
        }
    }

    public MonthlyLuckPeriod period() {

        return monthlyTenGodAnalysis
                .period();
    }

    public int sajuYear() {

        return period()
                .sajuYear();
    }

    public int monthIndex() {

        return period()
                .monthIndex();
    }

    public int monthOrder() {

        return period()
                .monthOrder();
    }

    public SexagenaryPillar monthlyPillar() {

        return period()
                .pillar();
    }

    public SexagenaryPillar annualPillar() {

        return annualLuckAnalysis
                .annualPillar();
    }

    /*
     * =================================================
     * 월운 공통 Context
     * =================================================
     */

    public List<MonthlyLuckContextSignal>
    signalsOf(
            MonthlyLuckContextAxis axis
    ) {

        if (
                axis == null
        ) {

            return List.of();
        }

        return monthlySignals
                .stream()
                .filter(
                        signal ->
                                signal.axis()
                                        == axis
                )
                .toList();
    }

    public List<MonthlyLuckContextSignal>
    signalsOf(
            MonthlyLuckContextStatus status
    ) {

        if (
                status == null
        ) {

            return List.of();
        }

        return monthlySignals
                .stream()
                .filter(
                        signal ->
                                signal.status()
                                        == status
                )
                .toList();
    }

    public List<MonthlyLuckContextSignal>
    signalsOf(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return monthlySignals
                .stream()
                .filter(
                        signal ->
                                signal.involves(
                                        position
                                )
                )
                .toList();
    }

    public List<MonthlyLuckContextSignal>
    signalsOf(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return List.of();
        }

        return monthlySignals
                .stream()
                .filter(
                        signal ->
                                signal.involves(
                                        tenGodType
                                )
                )
                .toList();
    }

    /*
     * =================================================
     * 대운 Segment
     * =================================================
     */

    public Optional<MonthlyLuckDecadeContextAnalysis>
    contextOfDecade(
            int sequenceNumber
    ) {

        if (
                sequenceNumber < 1
                        || !decadeContextAvailable
        ) {

            return Optional.empty();
        }

        return decadeContexts
                .stream()
                .filter(
                        context ->
                                context
                                        .decadeSequenceNumber()
                                        == sequenceNumber
                )
                .findFirst();
    }

    public List<MonthlyLuckDecadeContextAnalysis>
    possibleContextsAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !decadeContextAvailable
                        || !period()
                        .contains(
                                dateTime
                        )
        ) {

            return List.of();
        }

        return decadeContexts
                .stream()
                .filter(
                        context ->
                                context
                                        .possiblyActiveAt(
                                                dateTime
                                        )
                )
                .toList();
    }

    public Optional<MonthlyLuckDecadeContextAnalysis>
    definiteContextAt(
            ZonedDateTime dateTime
    ) {

        if (
                dateTime == null
                        || !decadeContextAvailable
                        || !period()
                        .contains(
                                dateTime
                        )
        ) {

            return Optional.empty();
        }

        List<MonthlyLuckDecadeContextAnalysis> matched =
                decadeContexts
                        .stream()
                        .filter(
                                context ->
                                        context
                                                .definitelyActiveAt(
                                                        dateTime
                                                )
                        )
                        .toList();

        if (
                matched.size()
                        > 1
        ) {

            throw new IllegalStateException(
                    "동일 시각에 확정 월운-대운 Context가 2개 이상 존재합니다."
            );
        }

        return matched
                .stream()
                .findFirst();
    }

    /*
     * =================================================
     * 전체 Signal
     * =================================================
     */

    public List<MonthlyLuckContextSignal>
    allSignals() {

        List<MonthlyLuckContextSignal> result =
                new ArrayList<>(
                        monthlySignals
                );

        for (
                MonthlyLuckDecadeContextAnalysis context
                : decadeContexts
        ) {

            result.addAll(
                    context.segmentSignals()
            );
        }

        return List.copyOf(
                result
        );
    }

    public List<MonthlyLuckContextSignal>
    allSignalsOf(
            MonthlyLuckContextAxis axis
    ) {

        if (
                axis == null
        ) {

            return List.of();
        }

        return allSignals()
                .stream()
                .filter(
                        signal ->
                                signal.axis()
                                        == axis
                )
                .toList();
    }

    public boolean mixedMonthlyContext() {

        boolean supportive =
                monthlySignals
                        .stream()
                        .anyMatch(
                                signal ->
                                        signal.status()
                                                .supported()

                                                || signal.status()
                                                .qualified()
                        );

        boolean restrictive =
                monthlySignals
                        .stream()
                        .anyMatch(
                                signal ->
                                        signal.status()
                                                .weakened()

                                                || signal.status()
                                                .conflicted()
                        );

        return supportive
                && restrictive;
    }

    public boolean hasDecadeSegments() {

        return !decadeContexts
                .isEmpty();
    }

    public boolean decadeTimingUncertain() {

        return decadeContexts
                .stream()
                .anyMatch(
                        MonthlyLuckDecadeContextAnalysis
                                ::timingUncertain
                );
    }
}