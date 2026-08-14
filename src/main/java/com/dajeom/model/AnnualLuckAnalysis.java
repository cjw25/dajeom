package com.dajeom.model;

import com.dajeom.type.AnnualLuckContextAxis;
import com.dajeom.type.AnnualLuckContextStatus;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 14차-7.
 *
 * 세운 분석 최종 Aggregate.
 *
 * =================================================
 *
 * 포함:
 *
 * 14-1 세운 입춘 경계
 * 14-2 세운 간지
 * 14-3 세운 십성
 * 14-4 원국 × 세운 관계
 * 14-5 대운 × 세운 관계
 * 14-6 세운 교차 Context
 *
 * =================================================
 *
 * 중요한 구조:
 *
 * AnnualLuckAnalysis
 * =
 * 특정 명리 연도 전체의 deterministic 분석.
 *
 * =================================================
 *
 * 대운이 불확실하더라도:
 *
 * - 세운 간지
 * - 세운 십성
 * - 원국 × 세운
 * - 대운을 제외한 세운 Context
 *
 * 는 그대로 보존한다.
 *
 * =================================================
 *
 * 따라서 전체 AnnualLuckAnalysis 자체와
 * "대운 포함 Context 사용 가능 여부"는
 * 서로 다른 개념이다.
 */
public record AnnualLuckAnalysis(

        HeavenlyStem dayMaster,

        PalaceAnalysis natalAnalysis,

        DecadeLuckAnalysis decadeLuckAnalysis,

        AnnualLuckPeriod period,

        AnnualLuckTenGodAnalysis tenGodAnalysis,

        AnnualLuckNatalRelationAnalysis natalRelationAnalysis,

        AnnualDecadeLuckInteractionAnalysis decadeInteractionAnalysis,

        AnnualLuckContextAnalysis contextAnalysis

) {

    public AnnualLuckAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "세운 최종 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                natalAnalysis,
                "세운 최종 분석에는 PalaceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                decadeLuckAnalysis,
                "세운 최종 분석에는 DecadeLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "14-2 AnnualLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodAnalysis,
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

        Objects.requireNonNull(
                contextAnalysis,
                "14-6 AnnualLuckContextAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 일간 정합성
         * =================================================
         */

        if (
                natalAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "원국 PalaceAnalysis와 세운 최종 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                decadeLuckAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "13차 대운 분석과 14차 세운 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "14-3 세운 십성 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "14-4 세운 관계 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                contextAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "14-6 세운 Context 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 동일 원국 Identity
         * =================================================
         */

        if (
                decadeLuckAnalysis.natalAnalysis()
                        != natalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "13차 대운 분석과 14차 세운 분석이 서로 다른 원국을 참조합니다."
            );
        }

        if (
                natalRelationAnalysis.natalAnalysis()
                        != natalAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14-4 세운 관계 분석이 현재 원국 PalaceAnalysis를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 14-2 ↔ 14-3
         * =================================================
         */

        if (
                tenGodAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "14-3 세운 십성 분석이 현재 AnnualLuckPeriod를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 14-2 ↔ 14-4
         * =================================================
         */

        if (
                natalRelationAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "14-4 세운 관계 분석이 현재 AnnualLuckPeriod를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 14-2 ↔ 14-5
         * =================================================
         */

        if (
                decadeInteractionAnalysis.annualPeriod()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "14-5 대운-세운 Interaction이 현재 AnnualLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                decadeInteractionAnalysis.decadeLuckAnalysis()
                        != decadeLuckAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14-5 대운-세운 Interaction이 현재 DecadeLuckAnalysis를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 14-3 / 14-4 / 14-5 ↔ 14-6
         * =================================================
         */

        if (
                contextAnalysis.annualTenGodAnalysis()
                        != tenGodAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14-6 Context가 현재 14-3 세운 십성 분석을 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.natalRelationAnalysis()
                        != natalRelationAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14-6 Context가 현재 14-4 세운 관계 분석을 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.decadeInteractionAnalysis()
                        != decadeInteractionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "14-6 Context가 현재 14-5 대운-세운 Interaction을 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "14-6 Context가 현재 AnnualLuckPeriod를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 대운 Context 상태 정합성
         * =================================================
         */

        if (
                decadeInteractionAnalysis.analysisAvailable()
                        != contextAnalysis.decadeContextAvailable()
        ) {

            throw new IllegalArgumentException(
                    "14-5와 14-6의 대운 Context 사용 가능 상태가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public int targetYear() {

        return period
                .targetYear();
    }

    public ZoneId zoneId() {

        return period
                .zoneId();
    }

    public SexagenaryPillar annualPillar() {

        return period
                .pillar();
    }

    public HeavenlyStem annualStem() {

        return period
                .stem();
    }

    public EarthlyBranch annualBranch() {

        return period
                .branch();
    }

    public int cycleIndex() {

        return period
                .cycleIndex();
    }

    /*
     * =================================================
     * 14-1 Boundary
     * =================================================
     */

    public AnnualLuckBoundaryAnalysis boundaryAnalysis() {

        return period
                .boundaryAnalysis();
    }

    public ZonedDateTime startDateTimeInclusive() {

        return period
                .startDateTimeInclusive();
    }

    public ZonedDateTime endDateTimeExclusive() {

        return period
                .endDateTimeExclusive();
    }

    public boolean contains(
            ZonedDateTime dateTime
    ) {

        return period
                .contains(
                        dateTime
                );
    }

    /*
     * =================================================
     * 14-3 십성
     * =================================================
     */

    public Optional<AnnualLuckTenGodEvidence>
    visibleTenGodEvidence() {

        return tenGodAnalysis
                .visibleEvidence();
    }

    public Optional<StructureTenGodType>
    visibleTenGodType() {

        return visibleTenGodEvidence()
                .map(
                        AnnualLuckTenGodEvidence
                                ::tenGodType
                );
    }

    public Optional<AnnualLuckTenGodEvidence>
    branchMainTenGodEvidence() {

        return tenGodAnalysis
                .mainHiddenEvidence();
    }

    public Optional<StructureTenGodType>
    branchMainTenGodType() {

        return branchMainTenGodEvidence()
                .map(
                        AnnualLuckTenGodEvidence
                                ::tenGodType
                );
    }

    public Optional<AnnualLuckTenGodEvidence>
    branchMiddleTenGodEvidence() {

        return tenGodAnalysis
                .middleHiddenEvidence();
    }

    public Optional<StructureTenGodType>
    branchMiddleTenGodType() {

        return branchMiddleTenGodEvidence()
                .map(
                        AnnualLuckTenGodEvidence
                                ::tenGodType
                );
    }

    public Optional<AnnualLuckTenGodEvidence>
    branchResidualTenGodEvidence() {

        return tenGodAnalysis
                .residualHiddenEvidence();
    }

    public Optional<StructureTenGodType>
    branchResidualTenGodType() {

        return branchResidualTenGodEvidence()
                .map(
                        AnnualLuckTenGodEvidence
                                ::tenGodType
                );
    }

    public List<AnnualLuckTenGodEvidence>
    tenGodEvidencesOf(
            StructureTenGodType tenGodType
    ) {

        return tenGodAnalysis
                .evidencesOf(
                        tenGodType
                );
    }

    public boolean containsTenGod(
            StructureTenGodType tenGodType
    ) {

        return tenGodAnalysis
                .contains(
                        tenGodType
                );
    }

    /*
     * =================================================
     * 14-4 원국 × 세운
     * =================================================
     */

    public List<AnnualLuckNatalRelationReference>
    natalRelations() {

        return natalRelationAnalysis
                .relations();
    }

    public List<AnnualLuckNatalRelationReference>
    natalStemRelations() {

        return natalRelationAnalysis
                .stemRelations();
    }

    public List<AnnualLuckNatalRelationReference>
    natalBranchRelations() {

        return natalRelationAnalysis
                .branchRelations();
    }

    public List<AnnualLuckNatalTransformationReference>
    natalTransformations() {

        return natalRelationAnalysis
                .transformations();
    }

    public List<AnnualLuckNatalRelationReference>
    natalRelationsOf(
            PalacePosition position
    ) {

        return natalRelationAnalysis
                .relationsOf(
                        position
                );
    }

    public List<AnnualLuckNatalTransformationReference>
    natalTransformationsOf(
            PalacePosition position
    ) {

        return natalRelationAnalysis
                .transformationsOf(
                        position
                );
    }

    public boolean involvesNatalPalace(
            PalacePosition position
    ) {

        return natalRelationAnalysis
                .involves(
                        position
                );
    }

    /*
     * =================================================
     * 14-5 대운 × 세운
     * =================================================
     */

    public boolean decadeInteractionAvailable() {

        return decadeInteractionAnalysis
                .analysisAvailable();
    }

    public Optional<String>
    decadeInteractionUnavailableReason() {

        if (
                decadeInteractionAvailable()
        ) {

            return Optional.empty();
        }

        String reason =
                decadeInteractionAnalysis
                        .unavailableReason();

        return reason == null
                || reason.isBlank()

                ? Optional.empty()

                : Optional.of(
                reason
        );
    }

    public List<AnnualDecadeLuckInteraction>
    decadeInteractions() {

        return decadeInteractionAnalysis
                .interactions();
    }

    public boolean hasDecadeInteraction() {

        return decadeInteractionAnalysis
                .hasInteraction();
    }

    public boolean multipleDecadesInAnnualPeriod() {

        return decadeInteractionAnalysis
                .multipleDecadesInAnnualPeriod();
    }

    public boolean decadeTimingUncertain() {

        return decadeInteractionAnalysis
                .hasTimingUncertainty();
    }

    public boolean beforeFirstDecadeLuck() {

        return decadeInteractionAnalysis
                .beforeFirstDecadeLuck();
    }

    public boolean afterGeneratedDecadeRange() {

        return decadeInteractionAnalysis
                .afterGeneratedDecadeRange();
    }

    /*
     * =================================================
     * 14-6 세운 공통 Context
     * =================================================
     */

    public List<AnnualLuckContextSignal>
    annualContextSignals() {

        return contextAnalysis
                .annualSignals();
    }

    public List<AnnualLuckContextSignal>
    annualContextSignalsOf(
            AnnualLuckContextAxis axis
    ) {

        return contextAnalysis
                .signalsOf(
                        axis
                );
    }

    public List<AnnualLuckContextSignal>
    annualContextSignalsOf(
            AnnualLuckContextStatus status
    ) {

        return contextAnalysis
                .signalsOf(
                        status
                );
    }

    public List<AnnualLuckContextSignal>
    annualContextSignalsOf(
            PalacePosition position
    ) {

        return contextAnalysis
                .signalsOf(
                        position
                );
    }

    public List<AnnualLuckContextSignal>
    annualContextSignalsOf(
            StructureTenGodType tenGodType
    ) {

        return contextAnalysis
                .signalsOf(
                        tenGodType
                );
    }

    /*
     * =================================================
     * 대운 Segment Context
     * =================================================
     */

    public boolean decadeContextAvailable() {

        return contextAnalysis
                .decadeContextAvailable();
    }

    public Optional<String>
    decadeContextUnavailableReason() {

        if (
                decadeContextAvailable()
        ) {

            return Optional.empty();
        }

        String reason =
                contextAnalysis
                        .decadeContextUnavailableReason();

        return reason == null
                || reason.isBlank()

                ? Optional.empty()

                : Optional.of(
                reason
        );
    }

    public List<AnnualLuckDecadeContextAnalysis>
    decadeContexts() {

        return contextAnalysis
                .decadeContexts();
    }

    public Optional<AnnualLuckDecadeContextAnalysis>
    contextOfDecade(
            int sequenceNumber
    ) {

        return contextAnalysis
                .contextOfDecade(
                        sequenceNumber
                );
    }

    /*
     * =================================================
     * 특정 시점
     * =================================================
     */

    public List<AnnualDecadeLuckInteraction>
    possibleDecadeInteractionsAt(
            ZonedDateTime dateTime
    ) {

        return decadeInteractionAnalysis
                .possibleInteractionsAt(
                        dateTime
                );
    }

    public Optional<AnnualDecadeLuckInteraction>
    definiteDecadeInteractionAt(
            ZonedDateTime dateTime
    ) {

        return decadeInteractionAnalysis
                .definiteInteractionAt(
                        dateTime
                );
    }

    /**
     * 세운 공통 Context
     * +
     * 해당 시점에 가능한 대운 Context.
     *
     * 교운 불확실성 구간에서는
     * 여러 Context가 반환될 수 있다.
     */
    public List<AnnualLuckDecadeContextAnalysis>
    possibleContextsAt(
            ZonedDateTime dateTime
    ) {

        return contextAnalysis
                .possibleContextsAt(
                        dateTime
                );
    }

    public Optional<AnnualLuckDecadeContextAnalysis>
    definiteContextAt(
            ZonedDateTime dateTime
    ) {

        return contextAnalysis
                .definiteContextAt(
                        dateTime
                );
    }

    /*
     * =================================================
     * 전체 Context
     * =================================================
     */

    public List<AnnualLuckContextSignal>
    allContextSignals() {

        return contextAnalysis
                .allSignals();
    }

    public List<AnnualLuckContextSignal>
    allContextSignalsOf(
            AnnualLuckContextAxis axis
    ) {

        return contextAnalysis
                .allSignalsOf(
                        axis
                );
    }

    public boolean mixedAnnualContext() {

        return contextAnalysis
                .mixedAnnualContext();
    }
}