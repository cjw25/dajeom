package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodCorePatternType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-10.
 *
 * 십성 복합구조 최종 Aggregate.
 *
 * =================================================
 *
 * 포함 단계:
 *
 * 10-1 십성 원국 분포·세력
 * 10-2 비겁 복합구조
 * 10-3 식상 복합구조
 * 10-4 재성 복합구조
 * 10-5 관살 복합구조
 * 10-6 인성 복합구조
 * 10-7 십성 Family 생·극 연쇄
 * 10-8 핵심 십성 Pattern
 * 10-9 격국·신강신약·용신·합화 교차검증
 *
 * =================================================
 *
 * 이 객체가 정밀화 10차의 공식 최종 결과다.
 *
 * 이후:
 *
 * 11차 12운성
 * 12차 궁위
 * 18차 생활영역
 * 19차 고민분석
 * 20차 AI Grounding
 *
 * 에서는 필요한 십성 정보를 이 Aggregate를
 * 통해 조회할 수 있다.
 *
 * =================================================
 *
 * 중요:
 *
 * 10-8의 Pattern과
 * 10-9의 교차검증 결과를 모두 보존한다.
 *
 * 예:
 *
 * 상관견관 STRONG
 * +
 * 10-9 WEAKENED
 *
 * 이라면
 *
 * "상관견관이 없다"
 *
 * 로 바꾸지 않는다.
 *
 * source Pattern과 최종 상태를
 * 동시에 보존한다.
 */
public record TenGodCompositeAnalysis(

        HeavenlyStem dayMaster,

        TenGodDistributionAnalysis distributionAnalysis,

        CompanionCompositeAnalysis companionAnalysis,

        OutputCompositeAnalysis outputAnalysis,

        WealthCompositeAnalysis wealthAnalysis,

        OfficerCompositeAnalysis officerAnalysis,

        ResourceCompositeAnalysis resourceAnalysis,

        TenGodFamilyChainAnalysis familyChainAnalysis,

        TenGodCorePatternAnalysis corePatternAnalysis,

        TenGodCrossValidationAnalysis crossValidationAnalysis,

        boolean completeChart

) {

    public TenGodCompositeAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                distributionAnalysis,
                "10-1 십성 분포 분석이 필요합니다."
        );

        Objects.requireNonNull(
                companionAnalysis,
                "10-2 비겁 복합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                outputAnalysis,
                "10-3 식상 복합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                wealthAnalysis,
                "10-4 재성 복합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                officerAnalysis,
                "10-5 관살 복합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                resourceAnalysis,
                "10-6 인성 복합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                familyChainAnalysis,
                "10-7 십성 Family Chain 분석이 필요합니다."
        );

        Objects.requireNonNull(
                corePatternAnalysis,
                "10-8 핵심 십성 Pattern 분석이 필요합니다."
        );

        Objects.requireNonNull(
                crossValidationAnalysis,
                "10-9 십성 Pattern 교차검증 분석이 필요합니다."
        );

        /*
         * =================================================
         * 일간 동일성
         * =================================================
         */

        if (
                distributionAnalysis.dayMaster()
                        != dayMaster

                        || companionAnalysis.dayMaster()
                        != dayMaster

                        || outputAnalysis.dayMaster()
                        != dayMaster

                        || wealthAnalysis.dayMaster()
                        != dayMaster

                        || officerAnalysis.dayMaster()
                        != dayMaster

                        || resourceAnalysis.dayMaster()
                        != dayMaster

                        || familyChainAnalysis.dayMaster()
                        != dayMaster

                        || corePatternAnalysis.dayMaster()
                        != dayMaster

                        || crossValidationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 분석들의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 10-2 ~ 10-6은 동일 10-1을 사용해야 한다.
         * =================================================
         */

        if (
                companionAnalysis
                        .tenGodDistributionAnalysis()
                        != distributionAnalysis

                        || outputAnalysis
                        .tenGodDistributionAnalysis()
                        != distributionAnalysis

                        || wealthAnalysis
                        .tenGodDistributionAnalysis()
                        != distributionAnalysis

                        || officerAnalysis
                        .tenGodDistributionAnalysis()
                        != distributionAnalysis

                        || resourceAnalysis
                        .tenGodDistributionAnalysis()
                        != distributionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10-2~10-6 분석이 동일한 10-1 십성 분포 분석을 참조해야 합니다."
            );
        }

        /*
         * =================================================
         * 10-7 입력 정합성
         * =================================================
         */

        if (
                familyChainAnalysis
                        .tenGodDistributionAnalysis()
                        != distributionAnalysis

                        || familyChainAnalysis
                        .companionCompositeAnalysis()
                        != companionAnalysis

                        || familyChainAnalysis
                        .outputCompositeAnalysis()
                        != outputAnalysis

                        || familyChainAnalysis
                        .wealthCompositeAnalysis()
                        != wealthAnalysis

                        || familyChainAnalysis
                        .officerCompositeAnalysis()
                        != officerAnalysis

                        || familyChainAnalysis
                        .resourceCompositeAnalysis()
                        != resourceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10-7 Family Chain 분석의 입력 참조가 10-1~10-6 결과와 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 10-8 입력 정합성
         * =================================================
         */

        if (
                corePatternAnalysis
                        .familyChainAnalysis()
                        != familyChainAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10-8 핵심 Pattern 분석이 현재 10-7 Family Chain 결과를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 10-9 입력 정합성
         * =================================================
         */

        if (
                crossValidationAnalysis
                        .corePatternAnalysis()
                        != corePatternAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10-9 교차검증 분석이 현재 10-8 핵심 Pattern 결과를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원국 완전성
         * =================================================
         */

        if (
                distributionAnalysis.completeChart()
                        != completeChart

                        || companionAnalysis.completeChart()
                        != completeChart

                        || outputAnalysis.completeChart()
                        != completeChart

                        || wealthAnalysis.completeChart()
                        != completeChart

                        || officerAnalysis.completeChart()
                        != completeChart

                        || resourceAnalysis.completeChart()
                        != completeChart

                        || familyChainAnalysis.completeChart()
                        != completeChart

                        || corePatternAnalysis.completeChart()
                        != completeChart

                        || crossValidationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "10차 십성 분석들의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 외부 분석 접근
     * =================================================
     */

    public DayMasterStrengthAnalysis dayMasterStrengthAnalysis() {

        return crossValidationAnalysis
                .dayMasterStrengthAnalysis();
    }

    public StructureAnalysis structureAnalysis() {

        return crossValidationAnalysis
                .structureAnalysis();
    }

    public UsefulGodAnalysis usefulGodAnalysis() {

        return crossValidationAnalysis
                .usefulGodAnalysis();
    }

    public CombinationTransformationAnalysis
    combinationTransformationAnalysis() {

        return crossValidationAnalysis
                .combinationTransformationAnalysis();
    }

    public TenGodCrossValidationContext
    crossValidationContext() {

        return crossValidationAnalysis
                .context();
    }

    /*
     * =================================================
     * 십성 개별 조회
     * =================================================
     */

    public Optional<TenGodPowerSummary> tenGod(
            StructureTenGodType tenGodType
    ) {

        if (
                tenGodType == null
        ) {

            return Optional.empty();
        }

        return distributionAnalysis
                .summaryOf(
                        tenGodType
                );
    }

    public int powerOf(
            StructureTenGodType tenGodType
    ) {

        return tenGod(
                tenGodType
        )
                .map(
                        TenGodPowerSummary
                                ::allocatedPower
                )
                .orElse(0);
    }

    public int shareBasisPointsOf(
            StructureTenGodType tenGodType
    ) {

        return tenGod(
                tenGodType
        )
                .map(
                        TenGodPowerSummary
                                ::shareBasisPoints
                )
                .orElse(0);
    }

    public boolean present(
            StructureTenGodType tenGodType
    ) {

        return tenGod(
                tenGodType
        )
                .map(
                        TenGodPowerSummary
                                ::present
                )
                .orElse(false);
    }

    public boolean visible(
            StructureTenGodType tenGodType
    ) {

        return tenGod(
                tenGodType
        )
                .map(
                        TenGodPowerSummary
                                ::visible
                )
                .orElse(false);
    }

    /*
     * =================================================
     * 5대 Family Power
     * =================================================
     */

    public int companionFamilyPower() {

        return distributionAnalysis
                .companionFamilyPower();
    }

    public int outputFamilyPower() {

        return distributionAnalysis
                .outputFamilyPower();
    }

    public int wealthFamilyPower() {

        return distributionAnalysis
                .wealthFamilyPower();
    }

    public int officerFamilyPower() {

        return distributionAnalysis
                .officerFamilyPower();
    }

    public int resourceFamilyPower() {

        return distributionAnalysis
                .resourceFamilyPower();
    }

    public int totalExternalTenGodPower() {

        return distributionAnalysis
                .totalExternalTenGodPower();
    }

    /*
     * =================================================
     * 10-8 원래 Pattern
     * =================================================
     */

    public Optional<TenGodCorePatternSignal> corePattern(
            TenGodCorePatternType patternType
    ) {

        return corePatternAnalysis
                .signalOf(
                        patternType
                );
    }

    public boolean hasCorePattern(
            TenGodCorePatternType patternType
    ) {

        return corePatternAnalysis
                .hasPattern(
                        patternType
                );
    }

    public boolean hasMeaningfulCorePattern(
            TenGodCorePatternType patternType
    ) {

        return corePatternAnalysis
                .hasMeaningfulPattern(
                        patternType
                );
    }

    public List<TenGodCorePatternSignal>
    meaningfulCorePatterns() {

        return corePatternAnalysis
                .meaningfulSignals();
    }

    public List<TenGodCorePatternSignal>
    strongCorePatterns() {

        return corePatternAnalysis
                .strongSignals();
    }

    /*
     * =================================================
     * 10-9 교차검증 Pattern
     * =================================================
     */

    public Optional<TenGodCrossValidatedPattern>
    validatedPattern(
            TenGodCorePatternType patternType
    ) {

        return crossValidationAnalysis
                .patternOf(
                        patternType
                );
    }

    public boolean isPatternSupported(
            TenGodCorePatternType patternType
    ) {

        return crossValidationAnalysis
                .isSupported(
                        patternType
                );
    }

    public boolean isPatternQualified(
            TenGodCorePatternType patternType
    ) {

        return crossValidationAnalysis
                .isQualified(
                        patternType
                );
    }

    public boolean isPatternWeakened(
            TenGodCorePatternType patternType
    ) {

        return crossValidationAnalysis
                .isWeakened(
                        patternType
                );
    }

    public boolean isPatternConflicted(
            TenGodCorePatternType patternType
    ) {

        return crossValidationAnalysis
                .isConflicted(
                        patternType
                );
    }

    public List<TenGodCrossValidatedPattern>
    supportedPatterns() {

        return crossValidationAnalysis
                .supportedPatterns();
    }

    public List<TenGodCrossValidatedPattern>
    qualifiedPatterns() {

        return crossValidationAnalysis
                .qualifiedPatterns();
    }

    public List<TenGodCrossValidatedPattern>
    weakenedPatterns() {

        return crossValidationAnalysis
                .weakenedPatterns();
    }

    public List<TenGodCrossValidatedPattern>
    conflictedPatterns() {

        return crossValidationAnalysis
                .conflictedPatterns();
    }

    public List<TenGodCrossValidatedPattern>
    orderedPatterns() {

        return crossValidationAnalysis
                .orderedPatterns();
    }

    /*
     * =================================================
     * 주요 Pattern Shortcut
     * =================================================
     */

    public boolean eatingGodControlsSevenKillings() {

        return isPatternSupported(
                TenGodCorePatternType
                        .EATING_GOD_CONTROLS_SEVEN_KILLINGS
        );
    }

    public boolean hurtingOfficerMeetsDirectOfficer() {

        return isPatternSupported(
                TenGodCorePatternType
                        .HURTING_OFFICER_MEETS_DIRECT_OFFICER
        );
    }

    public boolean hurtingOfficerWithResource() {

        return isPatternSupported(
                TenGodCorePatternType
                        .HURTING_OFFICER_WITH_RESOURCE
        );
    }

    public boolean indirectResourceSeizesEatingGod() {

        return isPatternSupported(
                TenGodCorePatternType
                        .INDIRECT_RESOURCE_SEIZES_EATING_GOD
        );
    }

    public boolean wealthGeneratesDirectOfficer() {

        return isPatternSupported(
                TenGodCorePatternType
                        .WEALTH_GENERATES_DIRECT_OFFICER
        );
    }

    public boolean directOfficerGeneratesResource() {

        return isPatternSupported(
                TenGodCorePatternType
                        .DIRECT_OFFICER_GENERATES_RESOURCE
        );
    }

    public boolean sevenKillingsGeneratesResource() {

        return isPatternSupported(
                TenGodCorePatternType
                        .SEVEN_KILLINGS_GENERATES_RESOURCE
        );
    }

    public boolean wealthControlsResource() {

        return isPatternSupported(
                TenGodCorePatternType
                        .WEALTH_CONTROLS_RESOURCE
        );
    }

    public boolean companionContestsWealth() {

        return isPatternSupported(
                TenGodCorePatternType
                        .COMPANION_CONTESTS_WEALTH
        );
    }

    public boolean wealthHeavyWeakDayMaster() {

        return isPatternSupported(
                TenGodCorePatternType
                        .WEALTH_HEAVY_WEAK_DAY_MASTER
        );
    }

    public boolean mixedOfficerKillings() {

        return isPatternSupported(
                TenGodCorePatternType
                        .MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS
        );
    }

    /*
     * =================================================
     * Family Chain
     * =================================================
     */

    public boolean outputGeneratesWealth() {

        return familyChainAnalysis
                .outputGeneratesWealth();
    }

    public boolean wealthGeneratesOfficer() {

        return familyChainAnalysis
                .wealthGeneratesOfficer();
    }

    public boolean officerGeneratesResource() {

        return familyChainAnalysis
                .officerGeneratesResource();
    }

    public boolean companionControlsWealth() {

        return familyChainAnalysis
                .companionControlsWealth();
    }

    public boolean wealthControlsResourceFamily() {

        return familyChainAnalysis
                .wealthControlsResource();
    }

    public boolean resourceControlsOutput() {

        return familyChainAnalysis
                .resourceControlsOutput();
    }

    public boolean outputControlsOfficer() {

        return familyChainAnalysis
                .outputControlsOfficer();
    }

    public boolean fullOutputWealthOfficerResourceChain() {

        return familyChainAnalysis
                .fullOutputWealthOfficerResourceChain();
    }

    /*
     * =================================================
     * 상태 Summary
     * =================================================
     */

    public int detectedCorePatternCount() {

        return corePatternAnalysis
                .signals()
                .size();
    }

    public int supportedPatternCount() {

        return crossValidationAnalysis
                .supportedPatterns()
                .size();
    }

    public int qualifiedPatternCount() {

        return crossValidationAnalysis
                .qualifiedPatterns()
                .size();
    }

    public int weakenedPatternCount() {

        return crossValidationAnalysis
                .weakenedPatterns()
                .size();
    }

    public int conflictedPatternCount() {

        return crossValidationAnalysis
                .conflictedPatterns()
                .size();
    }

    /*
     * =================================================
     * 시간 미상
     * =================================================
     */

    public boolean hourUnknown() {

        return !completeChart;
    }
}