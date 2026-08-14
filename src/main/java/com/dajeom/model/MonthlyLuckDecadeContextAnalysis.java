package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthlyLuckContextAxis;
import com.dajeom.type.MonthlyLuckContextStatus;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 정밀화 15차-7.
 *
 * 하나의 월운 안에서
 * 특정 대운이 작동하는 시간 Segment의
 * 최종 월운 Context.
 *
 * =================================================
 *
 * monthlySignals
 *
 * 해당 월운 전체에 공통인 Signal.
 *
 * =================================================
 *
 * segmentSignals
 *
 * 현재 대운 Segment에만 추가되는 Signal.
 *
 * =================================================
 *
 * 예:
 *
 * 입추
 * ↓
 * 월운 丙申
 * │
 * │ 대운 A
 * │
 * 대운교체
 * │
 * │ 대운 B
 * │
 * 백로
 *
 * 이 경우 같은 丙申 월운에
 * MonthlyLuckDecadeContextAnalysis가
 * 2개 존재할 수 있다.
 */
public record MonthlyLuckDecadeContextAnalysis(

        HeavenlyStem dayMaster,

        MonthlyLuckPeriod monthlyPeriod,

        MonthlyDecadeLuckInteraction interaction,

        DecadeLuckPeriodContextAnalysis decadeContext,

        AnnualLuckDecadeContextAnalysis annualDecadeContext,

        List<MonthlyLuckContextSignal> monthlySignals,

        List<MonthlyLuckContextSignal> segmentSignals

) {

    public MonthlyLuckDecadeContextAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "월운 Segment Context에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthlyPeriod,
                "월운 Segment Context에는 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                interaction,
                "월운 Segment Context에는 대운-월운 Interaction이 필요합니다."
        );

        Objects.requireNonNull(
                decadeContext,
                "월운 Segment Context에는 13-6 대운 Context가 필요합니다."
        );

        Objects.requireNonNull(
                annualDecadeContext,
                "월운 Segment Context에는 14-6 세운 대운 Context가 필요합니다."
        );

        monthlySignals =
                monthlySignals == null
                        ? List.of()
                        : List.copyOf(
                        monthlySignals
                );

        segmentSignals =
                segmentSignals == null
                        ? List.of()
                        : List.copyOf(
                        segmentSignals
                );

        /*
         * =================================================
         * 월운 Identity
         * =================================================
         */

        if (
                interaction.monthlyPeriod()
                        != monthlyPeriod
        ) {

            throw new IllegalArgumentException(
                    "15-5 Interaction과 15-7 Context가 서로 다른 MonthlyLuckPeriod를 참조합니다."
            );
        }

        /*
         * =================================================
         * 대운 Identity
         * =================================================
         */

        if (
                decadeContext.period()
                        != interaction.decadePeriod()
        ) {

            throw new IllegalArgumentException(
                    "13-6 대운 Context와 15-5 대운-월운 Interaction의 대운이 일치하지 않습니다."
            );
        }

        if (
                annualDecadeContext.interaction()
                        .decadePeriod()
                        != interaction.decadePeriod()
        ) {

            throw new IllegalArgumentException(
                    "14-6 세운 Segment와 15-5 월운 Segment의 대운이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 세운 Identity
         * =================================================
         */

        if (
                annualDecadeContext.annualPeriod()
                        != monthlyPeriod.annualPeriod()
        ) {

            throw new IllegalArgumentException(
                    "14-6 세운 Context와 현재 월운이 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        /*
         * =================================================
         * 일간
         * =================================================
         */

        if (
                decadeContext.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "13-6 대운 Context와 월운 Context의 일간이 일치하지 않습니다."
            );
        }

        if (
                annualDecadeContext.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "14-6 세운 Context와 월운 Context의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 시간범위 검증
         *
         * 월운에서 가능한 대운 범위는
         * 같은 대운의 세운 possible 범위를
         * 벗어날 수 없다.
         * =================================================
         */

        AnnualDecadeLuckOverlap annualOverlap =
                annualDecadeContext
                        .overlap();

        MonthlyDecadeLuckOverlap monthlyOverlap =
                interaction
                        .overlap();

        if (
                monthlyOverlap
                        .possibleStartInclusive()
                        .isBefore(
                                annualOverlap
                                        .possibleStartInclusive()
                        )

                        || monthlyOverlap
                        .possibleEndExclusive()
                        .isAfter(
                                annualOverlap
                                        .possibleEndExclusive()
                        )
        ) {

            throw new IllegalArgumentException(
                    "월운 대운 Overlap이 해당 세운 대운 Overlap 범위를 벗어났습니다."
            );
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

        for (
                MonthlyLuckContextSignal signal
                : segmentSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "segmentSignal은 null일 수 없습니다."
            );
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

    public int monthOrder() {

        return monthlyPeriod
                .monthOrder();
    }

    public int decadeSequenceNumber() {

        return interaction
                .decadeSequenceNumber();
    }

    public SexagenaryPillar monthlyPillar() {

        return monthlyPeriod
                .pillar();
    }

    public SexagenaryPillar annualPillar() {

        return monthlyPeriod
                .annualPillar();
    }

    public SexagenaryPillar decadePillar() {

        return interaction
                .decadePillar();
    }

    public MonthlyDecadeLuckOverlap overlap() {

        return interaction
                .overlap();
    }

    /**
     * 월운 공통 Signal
     * +
     * 현재 대운 Segment Signal.
     */
    public List<MonthlyLuckContextSignal> signals() {

        return Stream.concat(
                        monthlySignals.stream(),
                        segmentSignals.stream()
                )
                .toList();
    }

    public List<MonthlyLuckContextSignal> signalsOf(
            MonthlyLuckContextAxis axis
    ) {

        if (
                axis == null
        ) {

            return List.of();
        }

        return signals()
                .stream()
                .filter(
                        signal ->
                                signal.axis()
                                        == axis
                )
                .toList();
    }

    public List<MonthlyLuckContextSignal> signalsOf(
            MonthlyLuckContextStatus status
    ) {

        if (
                status == null
        ) {

            return List.of();
        }

        return signals()
                .stream()
                .filter(
                        signal ->
                                signal.status()
                                        == status
                )
                .toList();
    }

    public List<MonthlyLuckContextSignal> signalsOf(

            MonthlyLuckContextAxis axis,

            MonthlyLuckContextStatus status

    ) {

        if (
                axis == null
                        || status == null
        ) {

            return List.of();
        }

        return signals()
                .stream()
                .filter(
                        signal ->
                                signal.axis()
                                        == axis

                                        && signal.status()
                                        == status
                )
                .toList();
    }

    public List<MonthlyLuckContextSignal> signalsOf(
            PalacePosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return signals()
                .stream()
                .filter(
                        signal ->
                                signal.involves(
                                        position
                                )
                )
                .toList();
    }

    public List<MonthlyLuckContextSignal> signalsOf(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return List.of();
        }

        return signals()
                .stream()
                .filter(
                        signal ->
                                signal.involves(
                                        tenGodType
                                )
                )
                .toList();
    }

    public boolean hasSupportedEvidence() {

        return !signalsOf(
                MonthlyLuckContextStatus.SUPPORTED
        )
                .isEmpty();
    }

    public boolean hasQualifiedEvidence() {

        return !signalsOf(
                MonthlyLuckContextStatus.QUALIFIED
        )
                .isEmpty();
    }

    public boolean hasWeakenedEvidence() {

        return !signalsOf(
                MonthlyLuckContextStatus.WEAKENED
        )
                .isEmpty();
    }

    public boolean hasConflictedEvidence() {

        return !signalsOf(
                MonthlyLuckContextStatus.CONFLICTED
        )
                .isEmpty();
    }

    public boolean mixedContext() {

        boolean supportive =
                hasSupportedEvidence()
                        || hasQualifiedEvidence();

        boolean restrictive =
                hasWeakenedEvidence()
                        || hasConflictedEvidence();

        return supportive
                && restrictive;
    }

    public boolean timingUncertain() {

        return interaction
                .timingUncertain();
    }

    public boolean possiblyActiveAt(
            ZonedDateTime dateTime
    ) {

        return overlap()
                .possiblyContains(
                        dateTime
                );
    }

    public boolean definitelyActiveAt(
            ZonedDateTime dateTime
    ) {

        return overlap()
                .definitelyContains(
                        dateTime
                );
    }
}