package com.dajeom.analyzer;

import com.dajeom.calculator.StructureTenGodCalculator;
import com.dajeom.constant.HiddenStemTable;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodDistributionEvidence;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.rule.TenGodDistributionRule;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodPowerLevel;
import com.dajeom.type.TenGodSourceType;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 10차-1.
 *
 * 원국의 십성 분포·세력을 계산한다.
 *
 * =================================================
 *
 * 입력:
 *
 * FourPillars
 * +
 * ElementStrengthAnalysis
 *
 * =================================================
 *
 * 사용하는 upstream:
 *
 * 1차
 * FourPillars
 *
 * 2차
 * HiddenStemTable
 *
 * 3차
 * ElementStrengthAnalysis
 *
 * 5차
 * StructureTenGodCalculator
 *
 * =================================================
 *
 * 핵심 정책:
 *
 * 1.
 * 단순 개수를 세지 않는다.
 *
 * 2.
 * 지장간까지 포함한다.
 *
 * 3.
 * 3차의 final element power를
 * source rawWeight 비율로 배분한다.
 *
 * 4.
 * 일간 자기 자신은 Evidence에서 제외한다.
 *
 * 5.
 * 단, 일간의 rawWeight는
 * 같은 오행의 allocation denominator에 남긴다.
 *
 * 6.
 * 출생시간 미상은 시주를 만들지 않는다.
 */
@Component
public class TenGodDistributionAnalyzer {

    private final StructureTenGodCalculator
            structureTenGodCalculator;

    private final TenGodDistributionRule
            tenGodDistributionRule;

    public TenGodDistributionAnalyzer(

            StructureTenGodCalculator
                    structureTenGodCalculator,

            TenGodDistributionRule
                    tenGodDistributionRule

    ) {

        this.structureTenGodCalculator =
                Objects.requireNonNull(
                        structureTenGodCalculator,
                        "StructureTenGodCalculator가 필요합니다."
                );

        this.tenGodDistributionRule =
                Objects.requireNonNull(
                        tenGodDistributionRule,
                        "TenGodDistributionRule이 필요합니다."
                );
    }

    public TenGodDistributionAnalysis analyze(

            FourPillars fourPillars,

            ElementStrengthAnalysis elementStrengthAnalysis

    ) {

        Objects.requireNonNull(
                fourPillars,
                "FourPillars가 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "ElementStrengthAnalysis가 필요합니다."
        );

        HeavenlyStem dayMaster =
                fourPillars
                        .dayPillar()
                        .heavenlyStem();

        if (
                elementStrengthAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "FourPillars와 ElementStrengthAnalysis의 일간이 일치하지 않습니다."
            );
        }

        boolean completeChart =
                elementStrengthAnalysis
                        .completeChart();

        if (
                completeChart
                        && fourPillars.hourPillar() == null
        ) {

            throw new IllegalArgumentException(
                    "completeChart=true인데 시주가 존재하지 않습니다."
            );
        }

        if (
                !completeChart
                        && fourPillars.hourPillar() != null
        ) {

            throw new IllegalArgumentException(
                    "출생시간 미상 분석에 시주가 존재할 수 없습니다."
            );
        }

        /*
         * =================================================
         * Raw source 수집
         * =================================================
         *
         * 일간 자신도 allocation denominator를 위해
         * RawSource에는 넣는다.
         *
         * 이후 Evidence 생성 시 제외한다.
         */
        List<RawSource> rawSources =
                collectRawSources(
                        fourPillars,
                        completeChart
                );

        /*
         * =================================================
         * 오행별 rawWeight 총합
         * =================================================
         */
        Map<FiveElement, Integer> totalRawWeightByElement =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            totalRawWeightByElement.put(
                    element,
                    0
            );
        }

        for (
                RawSource source
                : rawSources
        ) {

            totalRawWeightByElement.merge(
                    source.element(),
                    source.rawWeight(),
                    Integer::sum
            );
        }

        /*
         * =================================================
         * 외부 십성 Evidence 생성
         * =================================================
         */
        List<TenGodDistributionEvidence> evidences =
                new ArrayList<>();

        for (
                RawSource source
                : rawSources
        ) {

            /*
             * 일간 자기 자신은
             * 십성 occurrence에서 제외한다.
             */
            if (
                    source.dayMasterSelf()
            ) {

                continue;
            }

            int totalSameElementRawWeight =
                    totalRawWeightByElement
                            .getOrDefault(
                                    source.element(),
                                    0
                            );

            int elementFinalPower =
                    elementStrengthAnalysis
                            .powerOf(
                                    source.element()
                            );

            int allocatedPower =
                    tenGodDistributionRule
                            .allocateElementPower(
                                    elementFinalPower,
                                    source.rawWeight(),
                                    totalSameElementRawWeight
                            );

            StructureTenGodType tenGodType =
                    structureTenGodCalculator
                            .calculate(
                                    dayMaster,
                                    source.stem()
                            );

            evidences.add(
                    new TenGodDistributionEvidence(
                            tenGodType,
                            source.stem(),
                            source.element(),
                            source.sourceType(),
                            source.visibleStemPosition(),
                            source.branchPosition(),
                            source.hiddenStemRole(),
                            source.rawWeight(),
                            allocatedPower
                    )
            );
        }

        evidences.sort(
                Comparator
                        .comparing(
                                TenGodDistributionEvidence
                                        ::tenGodType
                        )

                        .thenComparing(
                                TenGodDistributionEvidence
                                        ::sourceType
                        )

                        .thenComparingInt(
                                evidence ->
                                        sourcePositionOrder(
                                                evidence
                                        )
                        )
        );

        int totalExternalTenGodPower =
                evidences
                        .stream()
                        .mapToInt(
                                TenGodDistributionEvidence
                                        ::allocatedElementPower
                        )
                        .sum();

        List<TenGodPowerSummary> summaries =
                buildSummaries(
                        evidences,
                        totalExternalTenGodPower
                );

        FiveElement dayMasterElement =
                tenGodDistributionRule
                        .elementOf(
                                dayMaster
                        );

        return new TenGodDistributionAnalysis(
                dayMaster,
                dayMasterElement,
                evidences,
                summaries,
                totalExternalTenGodPower,
                completeChart ? 4 : 3,
                completeChart
        );
    }

    /*
     * =================================================
     * RawSource 수집
     * =================================================
     */

    private List<RawSource> collectRawSources(

            FourPillars fourPillars,

            boolean completeChart

    ) {

        List<RawSource> sources =
                new ArrayList<>();

        /*
         * YEAR
         */
        addVisibleStem(
                sources,
                VisibleStemPosition.YEAR,
                fourPillars
                        .yearPillar()
                        .heavenlyStem(),
                false
        );

        addHiddenStems(
                sources,
                EarthlyBranchPosition.YEAR,
                fourPillars
                        .yearPillar()
                        .earthlyBranch()
        );

        /*
         * MONTH
         */
        addVisibleStem(
                sources,
                VisibleStemPosition.MONTH,
                fourPillars
                        .monthPillar()
                        .heavenlyStem(),
                false
        );

        addHiddenStems(
                sources,
                EarthlyBranchPosition.MONTH,
                fourPillars
                        .monthPillar()
                        .earthlyBranch()
        );

        /*
         * DAY
         *
         * 일간 천간 자신은 denominator에는 포함,
         * 최종 Evidence에서는 제외.
         */
        addVisibleStem(
                sources,
                VisibleStemPosition.DAY,
                fourPillars
                        .dayPillar()
                        .heavenlyStem(),
                true
        );

        addHiddenStems(
                sources,
                EarthlyBranchPosition.DAY,
                fourPillars
                        .dayPillar()
                        .earthlyBranch()
        );

        /*
         * HOUR
         */
        if (
                completeChart
        ) {

            addVisibleStem(
                    sources,
                    VisibleStemPosition.HOUR,
                    fourPillars
                            .hourPillar()
                            .heavenlyStem(),
                    false
            );

            addHiddenStems(
                    sources,
                    EarthlyBranchPosition.HOUR,
                    fourPillars
                            .hourPillar()
                            .earthlyBranch()
            );
        }

        return List.copyOf(
                sources
        );
    }

    private void addVisibleStem(

            List<RawSource> sources,

            VisibleStemPosition position,

            HeavenlyStem stem,

            boolean dayMasterSelf

    ) {

        Objects.requireNonNull(
                position,
                "천간 위치가 필요합니다."
        );

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        FiveElement element =
                tenGodDistributionRule
                        .elementOf(
                                stem
                        );

        sources.add(
                new RawSource(
                        stem,
                        element,
                        TenGodSourceType.VISIBLE_STEM,
                        position,
                        null,
                        null,
                        TenGodDistributionRule
                                .VISIBLE_STEM_RAW_WEIGHT,
                        dayMasterSelf
                )
        );
    }

    private void addHiddenStems(

            List<RawSource> sources,

            EarthlyBranchPosition position,

            EarthlyBranch branch

    ) {

        Objects.requireNonNull(
                position,
                "지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        List<HiddenStem> hiddenStems =
                HiddenStemTable.get(
                        branch
                );

        for (
                HiddenStem hiddenStem
                : hiddenStems
        ) {

            HeavenlyStem stem =
                    hiddenStem.stem();

            FiveElement element =
                    tenGodDistributionRule
                            .elementOf(
                                    stem
                            );

            sources.add(
                    new RawSource(
                            stem,
                            element,
                            TenGodSourceType.HIDDEN_STEM,
                            null,
                            position,
                            hiddenStem.role(),
                            hiddenStem.weight(),
                            false
                    )
            );
        }
    }

    /*
     * =================================================
     * Summary
     * =================================================
     */

    private List<TenGodPowerSummary> buildSummaries(

            List<TenGodDistributionEvidence> evidences,

            int totalExternalTenGodPower

    ) {

        List<TenGodPowerSummary> summaries =
                new ArrayList<>();

        for (
                StructureTenGodType tenGodType
                : StructureTenGodType.values()
        ) {

            List<TenGodDistributionEvidence> typeEvidences =
                    evidences
                            .stream()
                            .filter(
                                    evidence ->
                                            evidence.tenGodType()
                                                    == tenGodType
                            )
                            .toList();

            int occurrenceCount =
                    typeEvidences.size();

            int visibleOccurrenceCount =
                    (int) typeEvidences
                            .stream()
                            .filter(
                                    TenGodDistributionEvidence
                                            ::visible
                            )
                            .count();

            int hiddenOccurrenceCount =
                    occurrenceCount
                            - visibleOccurrenceCount;

            int totalRawWeight =
                    typeEvidences
                            .stream()
                            .mapToInt(
                                    TenGodDistributionEvidence
                                            ::rawWeight
                            )
                            .sum();

            int allocatedPower =
                    typeEvidences
                            .stream()
                            .mapToInt(
                                    TenGodDistributionEvidence
                                            ::allocatedElementPower
                            )
                            .sum();

            int visibleAllocatedPower =
                    typeEvidences
                            .stream()
                            .filter(
                                    TenGodDistributionEvidence
                                            ::visible
                            )
                            .mapToInt(
                                    TenGodDistributionEvidence
                                            ::allocatedElementPower
                            )
                            .sum();

            int hiddenAllocatedPower =
                    typeEvidences
                            .stream()
                            .filter(
                                    TenGodDistributionEvidence
                                            ::hidden
                            )
                            .mapToInt(
                                    TenGodDistributionEvidence
                                            ::allocatedElementPower
                            )
                            .sum();

            int monthAllocatedPower =
                    typeEvidences
                            .stream()
                            .filter(
                                    TenGodDistributionEvidence
                                            ::monthSource
                            )
                            .mapToInt(
                                    TenGodDistributionEvidence
                                            ::allocatedElementPower
                            )
                            .sum();

            int monthBranchAllocatedPower =
                    typeEvidences
                            .stream()
                            .filter(
                                    TenGodDistributionEvidence
                                            ::monthBranchHiddenStem
                            )
                            .mapToInt(
                                    TenGodDistributionEvidence
                                            ::allocatedElementPower
                            )
                            .sum();

            int shareBasisPoints =
                    tenGodDistributionRule
                            .calculateShareBasisPoints(
                                    allocatedPower,
                                    totalExternalTenGodPower
                            );

            TenGodPowerLevel powerLevel =
                    tenGodDistributionRule
                            .determinePowerLevel(
                                    occurrenceCount,
                                    shareBasisPoints
                            );

            summaries.add(
                    new TenGodPowerSummary(
                            tenGodType,
                            powerLevel,
                            occurrenceCount,
                            visibleOccurrenceCount,
                            hiddenOccurrenceCount,
                            totalRawWeight,
                            allocatedPower,
                            visibleAllocatedPower,
                            hiddenAllocatedPower,
                            monthAllocatedPower,
                            monthBranchAllocatedPower,
                            shareBasisPoints
                    )
            );
        }

        summaries.sort(
                Comparator
                        .comparingInt(
                                TenGodPowerSummary
                                        ::allocatedPower
                        )
                        .reversed()

                        .thenComparing(
                                summary ->
                                        summary
                                                .tenGodType()
                                                .ordinal()
                        )
        );

        return List.copyOf(
                summaries
        );
    }

    /*
     * =================================================
     * 정렬용
     * =================================================
     */

    private int sourcePositionOrder(
            TenGodDistributionEvidence evidence
    ) {

        if (
                evidence.visible()
        ) {

            return evidence
                    .visibleStemPosition()
                    .ordinal()
                    * 10;
        }

        return evidence
                .branchPosition()
                .ordinal()
                * 10
                + hiddenStemRoleOrder(
                evidence.hiddenStemRole()
        );
    }

    private int hiddenStemRoleOrder(
            HiddenStemRole role
    ) {

        if (
                role == null
        ) {

            return 0;
        }

        return switch (
                role
                ) {

            case MAIN ->
                    1;

            case MIDDLE ->
                    2;

            case RESIDUAL ->
                    3;
        };
    }

    /*
     * =================================================
     * 내부 RawSource
     * =================================================
     */

    private record RawSource(

            HeavenlyStem stem,

            FiveElement element,

            TenGodSourceType sourceType,

            VisibleStemPosition visibleStemPosition,

            EarthlyBranchPosition branchPosition,

            HiddenStemRole hiddenStemRole,

            int rawWeight,

            boolean dayMasterSelf

    ) {

        private RawSource {

            Objects.requireNonNull(
                    stem,
                    "RawSource 천간이 필요합니다."
            );

            Objects.requireNonNull(
                    element,
                    "RawSource 오행이 필요합니다."
            );

            Objects.requireNonNull(
                    sourceType,
                    "RawSource 종류가 필요합니다."
            );

            if (
                    rawWeight <= 0
            ) {

                throw new IllegalArgumentException(
                        "RawSource weight는 0보다 커야 합니다."
                );
            }
        }
    }
}