package com.dajeom.model;

import com.dajeom.type.AnnualLuckContextAxis;
import com.dajeom.type.AnnualLuckContextStatus;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 14차-6.
 *
 * 세운 교차 Context 최종 분석.
 *
 * =================================================
 *
 * 중요한 설계:
 *
 * 세운 Context 자체는
 * 대운 Sequence가 불확실해도
 * 계산 가능한 부분을 버리지 않는다.
 *
 * =================================================
 *
 * annualSignals
 *
 * 원국 × 세운만으로 확정 가능한 Context.
 *
 * =================================================
 *
 * decadeContexts
 *
 * 대운이 확정/가능한 시간구간별 Context.
 *
 * =================================================
 *
 * decadeContextAvailable=false
 *
 * 대운 Sequence 자체가 불확실한 경우.
 *
 * 이때도 annualSignals는 그대로 보존한다.
 */
public record AnnualLuckContextAnalysis(

        HeavenlyStem dayMaster,

        AnnualLuckTenGodAnalysis annualTenGodAnalysis,

        AnnualLuckNatalRelationAnalysis natalRelationAnalysis,

        AnnualDecadeLuckInteractionAnalysis decadeInteractionAnalysis,

        List<AnnualLuckContextSignal> annualSignals,

        List<AnnualLuckDecadeContextAnalysis> decadeContexts,

        boolean decadeContextAvailable,

        String decadeContextUnavailableReason

) {

    public AnnualLuckContextAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "세운 Context 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                annualTenGodAnalysis,
                "14-3 AnnualLuckTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                natalRelationAnalysis,
                "14-4 AnnualLuckNatalRelationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeInteractionAnalysis,
                "14-5 AnnualDecadeLuckInteractionAnalysis가 필요합니다."
        );

        annualSignals =
                annualSignals == null
                        ? List.of()
                        : List.copyOf(
                        annualSignals
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
         * 일간 정합성
         * =================================================
         */

        if (
                annualTenGodAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "14-3 세운 십성 분석과 14-6의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "14-4 원국 관계 분석과 14-6의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * AnnualLuckPeriod Identity
         * =================================================
         */

        AnnualLuckPeriod annualPeriod =
                annualTenGodAnalysis
                        .period();

        if (
                natalRelationAnalysis.period()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "14-3과 14-4가 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        if (
                decadeInteractionAnalysis.annualPeriod()
                        != annualPeriod
        ) {

            throw new IllegalArgumentException(
                    "14-5가 현재 AnnualLuckPeriod를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 대운 Context 사용 가능
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
                        "14-5 분석이 불가능한 상태에서 대운 Segment Context를 활성화할 수 없습니다."
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
                        "14-5 Interaction 수와 14-6 대운 Segment Context 수가 일치하지 않습니다."
                );
            }

            for (
                    AnnualDecadeLuckInteraction interaction
                    : decadeInteractionAnalysis.interactions()
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
                            "14-5 Interaction에 대응하는 14-6 Segment Context가 없습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * 대운 Context 사용 불가
         * =================================================
         */

        if (
                !decadeContextAvailable
        ) {

            if (
                    !decadeContexts
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "대운 Context 사용 불가 상태에서는 Segment Context를 생성할 수 없습니다."
                );
            }

            if (
                    decadeContextUnavailableReason
                            .isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운 Context 사용 불가 사유가 필요합니다."
                );
            }
        }
    }

    public AnnualLuckPeriod period() {

        return annualTenGodAnalysis
                .period();
    }

    public int targetYear() {

        return period()
                .targetYear();
    }

    public SexagenaryPillar annualPillar() {

        return period()
                .pillar();
    }

    /*
     * =================================================
     * 세운 공통 Context
     * =================================================
     */

    public List<AnnualLuckContextSignal> signalsOf(
            AnnualLuckContextAxis axis
    ) {

        if (
                axis == null
        ) {

            return List.of();
        }

        return annualSignals
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

        return annualSignals
                .stream()
                .filter(
                        signal ->
                                signal.status()
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

        return annualSignals
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

        return annualSignals
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

    public Optional<AnnualLuckDecadeContextAnalysis>
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

    /**
     * 해당 순간에 가능성이 있는
     * 대운 Segment Context.
     *
     * 교운 불확실 구간에서는
     * 2개가 반환될 수 있다.
     */
    public List<AnnualLuckDecadeContextAnalysis>
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
                                context.possiblyActiveAt(
                                        dateTime
                                )
                )
                .toList();
    }

    /**
     * 특정 순간에 확실한 대운 Segment Context.
     */
    public Optional<AnnualLuckDecadeContextAnalysis>
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

        List<AnnualLuckDecadeContextAnalysis> matched =
                decadeContexts
                        .stream()
                        .filter(
                                context ->
                                        context.definitelyActiveAt(
                                                dateTime
                                        )
                        )
                        .toList();

        if (
                matched.size()
                        > 1
        ) {

            throw new IllegalStateException(
                    "동일 시각에 확정 세운-대운 Context가 2개 이상 존재합니다."
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
     *
     * annualSignals는 한 번만 포함하고,
     * 각 Segment의 segmentSignals만 추가한다.
     *
     * 따라서 세운 공통 Signal이
     * 대운 Segment 개수만큼 중복되지 않는다.
     */

    public List<AnnualLuckContextSignal> allSignals() {

        List<AnnualLuckContextSignal> result =
                new ArrayList<>(
                        annualSignals
                );

        for (
                AnnualLuckDecadeContextAnalysis context
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

    public List<AnnualLuckContextSignal> allSignalsOf(
            AnnualLuckContextAxis axis
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

    public boolean mixedAnnualContext() {

        boolean supportive =
                annualSignals
                        .stream()
                        .anyMatch(
                                signal ->
                                        signal.status()
                                                .supported()

                                                || signal.status()
                                                .qualified()
                        );

        boolean restrictive =
                annualSignals
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
                        AnnualLuckDecadeContextAnalysis
                                ::timingUncertain
                );
    }
}