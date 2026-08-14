package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.MonthlyLuckContextAxis;
import com.dajeom.type.MonthlyLuckContextStatus;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 15차-8.
 *
 * 월운 분석 최종 Aggregate.
 *
 * =================================================
 *
 * 포함:
 *
 * 15-1 월운 절입 경계
 * 15-2 월운 간지
 * 15-3 월운 십성
 * 15-4 원국 × 월운 관계
 * 15-5 대운 × 월운 관계
 * 15-6 세운 × 월운 관계
 * 15-7 월운 교차 Context
 *
 * =================================================
 *
 * MonthlyLuckAnalysis 자체는
 * 하나의 절입 월운을 의미한다.
 *
 * 예:
 *
 * 2026 丙午 세운 내부
 *
 * 庚寅 월운
 * [입춘, 경칩)
 *
 * =================================================
 *
 * 대운 Sequence가 불확실하더라도:
 *
 * - 월운 간지
 * - 월운 십성
 * - 원국 × 월운
 * - 세운 × 월운
 * - 대운을 제외한 월운 Context
 *
 * 는 그대로 보존한다.
 *
 * 따라서:
 *
 * 월운 분석 가능 여부
 * !=
 * 대운 포함 월운 Context 가능 여부
 */
public record MonthlyLuckAnalysis(

        HeavenlyStem dayMaster,

        AnnualLuckAnalysis annualLuckAnalysis,

        MonthlyLuckPeriod period,

        MonthlyLuckTenGodAnalysis tenGodAnalysis,

        MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

        MonthlyDecadeLuckInteractionAnalysis decadeInteractionAnalysis,

        MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis,

        MonthlyLuckContextAnalysis contextAnalysis

) {

    public MonthlyLuckAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "월운 최종 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "월운 최종 분석에는 AnnualLuckAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                period,
                "15-2 MonthlyLuckPeriod가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodAnalysis,
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

        Objects.requireNonNull(
                contextAnalysis,
                "15-7 MonthlyLuckContextAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 일간 정합성
         * =================================================
         */

        if (
                annualLuckAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "14차 세운 분석과 15차 월운 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                tenGodAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "15-3 월운 십성 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "15-4 원국-월운 관계 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                contextAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "15-7 월운 Context 분석의 일간이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * AnnualLuckPeriod Identity
         * =================================================
         */

        if (
                period.annualPeriod()
                        != annualLuckAnalysis.period()
        ) {

            throw new IllegalArgumentException(
                    "월운이 현재 AnnualLuckAnalysis의 세운에 속하지 않습니다."
            );
        }

        /*
         * =================================================
         * MonthlyLuckPeriod Identity
         * =================================================
         */

        if (
                tenGodAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-3 월운 십성 분석이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                natalRelationAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-4 원국-월운 관계 분석이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                decadeInteractionAnalysis.monthlyPeriod()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-5 대운-월운 분석이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                annualRelationAnalysis.monthlyPeriod()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-6 세운-월운 관계 분석이 현재 MonthlyLuckPeriod를 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.period()
                        != period
        ) {

            throw new IllegalArgumentException(
                    "15-7 월운 Context가 현재 MonthlyLuckPeriod를 참조하지 않습니다."
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
         * 대운 Identity
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
         * =================================================
         * 세운 × 월운 Identity
         * =================================================
         */

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
         * 15-3 ~ 15-6 ↔ 15-7 Identity
         * =================================================
         */

        if (
                contextAnalysis.annualLuckAnalysis()
                        != annualLuckAnalysis
        ) {

            throw new IllegalArgumentException(
                    "15-7 Context가 현재 AnnualLuckAnalysis를 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.monthlyTenGodAnalysis()
                        != tenGodAnalysis
        ) {

            throw new IllegalArgumentException(
                    "15-7 Context가 현재 15-3 월운 십성 분석을 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.natalRelationAnalysis()
                        != natalRelationAnalysis
        ) {

            throw new IllegalArgumentException(
                    "15-7 Context가 현재 15-4 원국-월운 분석을 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.decadeInteractionAnalysis()
                        != decadeInteractionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "15-7 Context가 현재 15-5 대운-월운 분석을 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.annualRelationAnalysis()
                        != annualRelationAnalysis
        ) {

            throw new IllegalArgumentException(
                    "15-7 Context가 현재 15-6 세운-월운 분석을 참조하지 않습니다."
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
                    "15-5와 15-7의 대운 Context 사용 가능 상태가 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 기본 월운 정보
     * =================================================
     */

    public int sajuYear() {

        return period
                .sajuYear();
    }

    public int monthIndex() {

        return period
                .monthIndex();
    }

    public int monthOrder() {

        return period
                .monthOrder();
    }

    public SexagenaryPillar monthlyPillar() {

        return period
                .pillar();
    }

    public HeavenlyStem monthlyStem() {

        return period
                .stem();
    }

    public EarthlyBranch monthlyBranch() {

        return period
                .branch();
    }

    public int cycleIndex() {

        return period
                .cycleIndex();
    }

    /*
     * =================================================
     * 세운 정보
     * =================================================
     */

    public SexagenaryPillar annualPillar() {

        return annualLuckAnalysis
                .annualPillar();
    }

    public HeavenlyStem annualStem() {

        return annualLuckAnalysis
                .annualStem();
    }

    public EarthlyBranch annualBranch() {

        return annualLuckAnalysis
                .annualBranch();
    }

    /*
     * =================================================
     * 시간
     * =================================================
     */

    public ZoneId zoneId() {

        return period
                .zoneId();
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
     * 15-3 월운 십성
     * =================================================
     */

    public Optional<MonthlyLuckTenGodEvidence>
    visibleTenGodEvidence() {

        return tenGodAnalysis
                .visibleEvidence();
    }

    public Optional<StructureTenGodType>
    visibleTenGodType() {

        return visibleTenGodEvidence()
                .map(
                        MonthlyLuckTenGodEvidence
                                ::tenGodType
                );
    }

    public Optional<MonthlyLuckTenGodEvidence>
    branchMainTenGodEvidence() {

        return tenGodAnalysis
                .mainHiddenEvidence();
    }

    public Optional<StructureTenGodType>
    branchMainTenGodType() {

        return branchMainTenGodEvidence()
                .map(
                        MonthlyLuckTenGodEvidence
                                ::tenGodType
                );
    }

    public Optional<MonthlyLuckTenGodEvidence>
    branchMiddleTenGodEvidence() {

        return tenGodAnalysis
                .middleHiddenEvidence();
    }

    public Optional<StructureTenGodType>
    branchMiddleTenGodType() {

        return branchMiddleTenGodEvidence()
                .map(
                        MonthlyLuckTenGodEvidence
                                ::tenGodType
                );
    }

    public Optional<MonthlyLuckTenGodEvidence>
    branchResidualTenGodEvidence() {

        return tenGodAnalysis
                .residualHiddenEvidence();
    }

    public Optional<StructureTenGodType>
    branchResidualTenGodType() {

        return branchResidualTenGodEvidence()
                .map(
                        MonthlyLuckTenGodEvidence
                                ::tenGodType
                );
    }

    public List<MonthlyLuckTenGodEvidence>
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
     * 15-4 원국 × 월운
     * =================================================
     */

    public List<MonthlyLuckNatalRelationReference>
    natalRelations() {

        return natalRelationAnalysis
                .relations();
    }

    public List<MonthlyLuckNatalRelationReference>
    natalStemRelations() {

        return natalRelationAnalysis
                .stemRelations();
    }

    public List<MonthlyLuckNatalRelationReference>
    natalBranchRelations() {

        return natalRelationAnalysis
                .branchRelations();
    }

    public List<MonthlyLuckNatalTransformationReference>
    natalTransformations() {

        return natalRelationAnalysis
                .transformations();
    }

    public List<MonthlyLuckNatalRelationReference>
    natalRelationsOf(
            PalacePosition position
    ) {

        return natalRelationAnalysis
                .relationsOf(
                        position
                );
    }

    public List<MonthlyLuckNatalTransformationReference>
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
     * 15-5 대운 × 월운
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

        if (
                reason == null
                        || reason.isBlank()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                reason
        );
    }

    public List<MonthlyDecadeLuckInteraction>
    decadeInteractions() {

        return decadeInteractionAnalysis
                .interactions();
    }

    public boolean hasDecadeInteraction() {

        return decadeInteractionAnalysis
                .hasInteraction();
    }

    public boolean multipleDecadesInMonthlyPeriod() {

        return decadeInteractionAnalysis
                .multipleDecadesInMonthlyPeriod();
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
     * 15-6 세운 × 월운
     * =================================================
     */

    public List<MonthlyAnnualLuckRelationReference>
    annualRelations() {

        return annualRelationAnalysis
                .relations();
    }

    public List<MonthlyAnnualLuckRelationReference>
    annualStemRelations() {

        return annualRelationAnalysis
                .stemRelations();
    }

    public List<MonthlyAnnualLuckRelationReference>
    annualBranchRelations() {

        return annualRelationAnalysis
                .branchRelations();
    }

    public List<MonthlyAnnualLuckTransformationReference>
    annualTransformations() {

        return annualRelationAnalysis
                .transformations();
    }

    /*
     * =================================================
     * 15-7 월운 공통 Context
     * =================================================
     */

    public List<MonthlyLuckContextSignal>
    monthlyContextSignals() {

        return contextAnalysis
                .monthlySignals();
    }

    public List<MonthlyLuckContextSignal>
    monthlyContextSignalsOf(
            MonthlyLuckContextAxis axis
    ) {

        return contextAnalysis
                .signalsOf(
                        axis
                );
    }

    public List<MonthlyLuckContextSignal>
    monthlyContextSignalsOf(
            MonthlyLuckContextStatus status
    ) {

        return contextAnalysis
                .signalsOf(
                        status
                );
    }

    public List<MonthlyLuckContextSignal>
    monthlyContextSignalsOf(
            PalacePosition position
    ) {

        return contextAnalysis
                .signalsOf(
                        position
                );
    }

    public List<MonthlyLuckContextSignal>
    monthlyContextSignalsOf(
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

        if (
                reason == null
                        || reason.isBlank()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                reason
        );
    }

    public List<MonthlyLuckDecadeContextAnalysis>
    decadeContexts() {

        return contextAnalysis
                .decadeContexts();
    }

    public Optional<MonthlyLuckDecadeContextAnalysis>
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

    public List<MonthlyDecadeLuckInteraction>
    possibleDecadeInteractionsAt(
            ZonedDateTime dateTime
    ) {

        return decadeInteractionAnalysis
                .possibleInteractionsAt(
                        dateTime
                );
    }

    public Optional<MonthlyDecadeLuckInteraction>
    definiteDecadeInteractionAt(
            ZonedDateTime dateTime
    ) {

        return decadeInteractionAnalysis
                .definiteInteractionAt(
                        dateTime
                );
    }

    public List<MonthlyLuckDecadeContextAnalysis>
    possibleContextsAt(
            ZonedDateTime dateTime
    ) {

        return contextAnalysis
                .possibleContextsAt(
                        dateTime
                );
    }

    public Optional<MonthlyLuckDecadeContextAnalysis>
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

    public List<MonthlyLuckContextSignal>
    allContextSignals() {

        return contextAnalysis
                .allSignals();
    }

    public List<MonthlyLuckContextSignal>
    allContextSignalsOf(
            MonthlyLuckContextAxis axis
    ) {

        return contextAnalysis
                .allSignalsOf(
                        axis
                );
    }

    public boolean mixedMonthlyContext() {

        return contextAnalysis
                .mixedMonthlyContext();
    }
}