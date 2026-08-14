package com.dajeom.model;

import com.dajeom.type.AnnualLuckContextAxis;
import com.dajeom.type.AnnualLuckContextStatus;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 정밀화 14차-6.
 *
 * 특정 세운 안에서
 * 특정 대운이 작동하는 시간구간의
 * 세운 Context.
 *
 * =================================================
 *
 * 예:
 *
 * 2026 丙午
 *
 * 2026 입춘 ~ 2026-08-20
 * -> 4대운 Context
 *
 * 2026-08-20 ~ 2027 입춘
 * -> 5대운 Context
 *
 * =================================================
 *
 * annualSignals:
 *
 * 세운 전체에 공통인 원국 × 세운 Context.
 *
 * =================================================
 *
 * segmentSignals:
 *
 * 해당 대운이 함께 작동할 때만
 * 발생하는 추가 Context.
 */
public record AnnualLuckDecadeContextAnalysis(

        HeavenlyStem dayMaster,

        AnnualLuckPeriod annualPeriod,

        AnnualDecadeLuckInteraction interaction,

        DecadeLuckPeriodContextAnalysis decadeContext,

        List<AnnualLuckContextSignal> annualSignals,

        List<AnnualLuckContextSignal> segmentSignals

) {

    public AnnualLuckDecadeContextAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "세운 Segment Context에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                annualPeriod,
                "세운 Segment Context에는 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                interaction,
                "세운 Segment Context에는 대운-세운 Interaction이 필요합니다."
        );

        Objects.requireNonNull(
                decadeContext,
                "세운 Segment Context에는 13-6 대운 Context가 필요합니다."
        );

        annualSignals =
                annualSignals == null
                        ? List.of()
                        : List.copyOf(
                        annualSignals
                );

        segmentSignals =
                segmentSignals == null
                        ? List.of()
                        : List.copyOf(
                        segmentSignals
                );

        if (
                interaction.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "세운 Segment와 대운-세운 Interaction의 AnnualLuckPeriod가 일치하지 않습니다."
            );
        }

        if (
                decadeContext.period()
                        != interaction.decadePeriod()
        ) {

            throw new IllegalArgumentException(
                    "13-6 대운 Context와 14-5 Interaction이 서로 다른 대운을 참조합니다."
            );
        }

        if (
                decadeContext.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "대운 Context와 세운 Segment Context의 일간이 일치하지 않습니다."
            );
        }

        for (
                AnnualLuckContextSignal signal
                : annualSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "annualSignal은 null일 수 없습니다."
            );
        }

        for (
                AnnualLuckContextSignal signal
                : segmentSignals
        ) {

            Objects.requireNonNull(
                    signal,
                    "segmentSignal은 null일 수 없습니다."
            );
        }
    }

    public int targetYear() {

        return annualPeriod
                .targetYear();
    }

    public int decadeSequenceNumber() {

        return interaction
                .decadeSequenceNumber();
    }

    public SexagenaryPillar annualPillar() {

        return annualPeriod
                .pillar();
    }

    public SexagenaryPillar decadePillar() {

        return interaction
                .decadePillar();
    }

    public AnnualDecadeLuckOverlap overlap() {

        return interaction
                .overlap();
    }

    /**
     * 세운 공통 Signal
     * +
     * 대운구간 전용 Signal
     */
    public List<AnnualLuckContextSignal> signals() {

        return Stream.concat(
                        annualSignals.stream(),
                        segmentSignals.stream()
                )
                .toList();
    }

    public List<AnnualLuckContextSignal> signalsOf(
            AnnualLuckContextAxis axis
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

    public List<AnnualLuckContextSignal> signalsOf(
            AnnualLuckContextStatus status
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

    public List<AnnualLuckContextSignal> signalsOf(

            AnnualLuckContextAxis axis,

            AnnualLuckContextStatus status

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

    public List<AnnualLuckContextSignal> signalsOf(
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

    public List<AnnualLuckContextSignal> signalsOf(
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
                AnnualLuckContextStatus.SUPPORTED
        )
                .isEmpty();
    }

    public boolean hasQualifiedEvidence() {

        return !signalsOf(
                AnnualLuckContextStatus.QUALIFIED
        )
                .isEmpty();
    }

    public boolean hasWeakenedEvidence() {

        return !signalsOf(
                AnnualLuckContextStatus.WEAKENED
        )
                .isEmpty();
    }

    public boolean hasConflictedEvidence() {

        return !signalsOf(
                AnnualLuckContextStatus.CONFLICTED
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