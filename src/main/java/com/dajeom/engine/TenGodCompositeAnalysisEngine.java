package com.dajeom.engine;

import com.dajeom.analyzer.CompanionCompositeAnalyzer;
import com.dajeom.analyzer.OfficerCompositeAnalyzer;
import com.dajeom.analyzer.OutputCompositeAnalyzer;
import com.dajeom.analyzer.ResourceCompositeAnalyzer;
import com.dajeom.analyzer.TenGodCorePatternAnalyzer;
import com.dajeom.analyzer.TenGodCrossValidationAnalyzer;
import com.dajeom.analyzer.TenGodDistributionAnalyzer;
import com.dajeom.analyzer.TenGodFamilyChainAnalyzer;
import com.dajeom.analyzer.WealthCompositeAnalyzer;
import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.CompanionCompositeAnalysis;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.OfficerCompositeAnalysis;
import com.dajeom.model.OutputCompositeAnalysis;
import com.dajeom.model.ResourceCompositeAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.model.TenGodCorePatternAnalysis;
import com.dajeom.model.TenGodCrossValidationAnalysis;
import com.dajeom.model.TenGodCrossValidationContext;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodFamilyChainAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.model.WealthCompositeAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 10차-10.
 *
 * 십성 복합구조 최종 Engine.
 *
 * =================================================
 *
 * 공식 실행순서:
 *
 * 10-1
 * TenGodDistributionAnalyzer
 *
 * ↓
 *
 * 10-2
 * CompanionCompositeAnalyzer
 *
 * 10-3
 * OutputCompositeAnalyzer
 *
 * 10-4
 * WealthCompositeAnalyzer
 *
 * 10-5
 * OfficerCompositeAnalyzer
 *
 * 10-6
 * ResourceCompositeAnalyzer
 *
 * ↓
 *
 * 10-7
 * TenGodFamilyChainAnalyzer
 *
 * ↓
 *
 * 10-8
 * TenGodCorePatternAnalyzer
 *
 * ↓
 *
 * 10-9
 * TenGodCrossValidationAnalyzer
 *
 * ↓
 *
 * TenGodCompositeAnalysis
 *
 * =================================================
 *
 * 주의:
 *
 * 이 Engine은 1~9차 전체를 다시 계산하는
 * Saju 전체 Engine이 아니다.
 *
 * 다음 선행 분석 결과는 입력으로 받는다.
 *
 * - ElementStrengthAnalysis      : 3차
 * - DayMasterStrengthAnalysis   : 4차
 * - StructureAnalysis           : 5차
 * - UsefulGodAnalysis           : 7차
 * - CombinationTransformationAnalysis : 9차
 *
 * 즉 이 클래스는 "정밀화 10차 내부"의
 * 공식 오케스트레이터다.
 */
@Component
public class TenGodCompositeAnalysisEngine {

    private final TenGodDistributionAnalyzer
            tenGodDistributionAnalyzer;

    private final CompanionCompositeAnalyzer
            companionCompositeAnalyzer;

    private final OutputCompositeAnalyzer
            outputCompositeAnalyzer;

    private final WealthCompositeAnalyzer
            wealthCompositeAnalyzer;

    private final OfficerCompositeAnalyzer
            officerCompositeAnalyzer;

    private final ResourceCompositeAnalyzer
            resourceCompositeAnalyzer;

    private final TenGodFamilyChainAnalyzer
            tenGodFamilyChainAnalyzer;

    private final TenGodCorePatternAnalyzer
            tenGodCorePatternAnalyzer;

    private final TenGodCrossValidationAnalyzer
            tenGodCrossValidationAnalyzer;

    public TenGodCompositeAnalysisEngine(

            TenGodDistributionAnalyzer
                    tenGodDistributionAnalyzer,

            CompanionCompositeAnalyzer
                    companionCompositeAnalyzer,

            OutputCompositeAnalyzer
                    outputCompositeAnalyzer,

            WealthCompositeAnalyzer
                    wealthCompositeAnalyzer,

            OfficerCompositeAnalyzer
                    officerCompositeAnalyzer,

            ResourceCompositeAnalyzer
                    resourceCompositeAnalyzer,

            TenGodFamilyChainAnalyzer
                    tenGodFamilyChainAnalyzer,

            TenGodCorePatternAnalyzer
                    tenGodCorePatternAnalyzer,

            TenGodCrossValidationAnalyzer
                    tenGodCrossValidationAnalyzer

    ) {

        this.tenGodDistributionAnalyzer =
                Objects.requireNonNull(
                        tenGodDistributionAnalyzer,
                        "TenGodDistributionAnalyzer가 필요합니다."
                );

        this.companionCompositeAnalyzer =
                Objects.requireNonNull(
                        companionCompositeAnalyzer,
                        "CompanionCompositeAnalyzer가 필요합니다."
                );

        this.outputCompositeAnalyzer =
                Objects.requireNonNull(
                        outputCompositeAnalyzer,
                        "OutputCompositeAnalyzer가 필요합니다."
                );

        this.wealthCompositeAnalyzer =
                Objects.requireNonNull(
                        wealthCompositeAnalyzer,
                        "WealthCompositeAnalyzer가 필요합니다."
                );

        this.officerCompositeAnalyzer =
                Objects.requireNonNull(
                        officerCompositeAnalyzer,
                        "OfficerCompositeAnalyzer가 필요합니다."
                );

        this.resourceCompositeAnalyzer =
                Objects.requireNonNull(
                        resourceCompositeAnalyzer,
                        "ResourceCompositeAnalyzer가 필요합니다."
                );

        this.tenGodFamilyChainAnalyzer =
                Objects.requireNonNull(
                        tenGodFamilyChainAnalyzer,
                        "TenGodFamilyChainAnalyzer가 필요합니다."
                );

        this.tenGodCorePatternAnalyzer =
                Objects.requireNonNull(
                        tenGodCorePatternAnalyzer,
                        "TenGodCorePatternAnalyzer가 필요합니다."
                );

        this.tenGodCrossValidationAnalyzer =
                Objects.requireNonNull(
                        tenGodCrossValidationAnalyzer,
                        "TenGodCrossValidationAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 10차 분석 진입점
     * =================================================
     *
     * 용신·합화 Pattern Alignment까지
     * 이미 결정된 Context를 입력받는다.
     *
     * 이것이 가장 완전한 10차 실행 방식이다.
     */
    public TenGodCompositeAnalysis analyze(

            FourPillars fourPillars,

            ElementStrengthAnalysis
                    elementStrengthAnalysis,

            DayMasterStrengthAnalysis
                    dayMasterStrengthAnalysis,

            StructureAnalysis
                    structureAnalysis,

            UsefulGodAnalysis
                    usefulGodAnalysis,

            CombinationTransformationAnalysis
                    combinationTransformationAnalysis,

            TenGodCrossValidationContext
                    crossValidationContext

    ) {

        validateInputs(
                fourPillars,
                elementStrengthAnalysis,
                dayMasterStrengthAnalysis,
                structureAnalysis,
                usefulGodAnalysis,
                combinationTransformationAnalysis,
                crossValidationContext
        );

        /*
         * =================================================
         * 10-1
         *
         * 십성 원국 분포·세력
         * =================================================
         */

        TenGodDistributionAnalysis distributionAnalysis =
                tenGodDistributionAnalyzer
                        .analyze(
                                fourPillars,
                                elementStrengthAnalysis
                        );

        /*
         * =================================================
         * 10-2
         *
         * 비겁 복합구조
         * =================================================
         */

        CompanionCompositeAnalysis companionAnalysis =
                companionCompositeAnalyzer
                        .analyze(
                                distributionAnalysis,
                                dayMasterStrengthAnalysis
                        );

        /*
         * =================================================
         * 10-3
         *
         * 식상 복합구조
         * =================================================
         */

        OutputCompositeAnalysis outputAnalysis =
                outputCompositeAnalyzer
                        .analyze(
                                distributionAnalysis,
                                dayMasterStrengthAnalysis
                        );

        /*
         * =================================================
         * 10-4
         *
         * 재성 복합구조
         * =================================================
         */

        WealthCompositeAnalysis wealthAnalysis =
                wealthCompositeAnalyzer
                        .analyze(
                                distributionAnalysis,
                                dayMasterStrengthAnalysis
                        );

        /*
         * =================================================
         * 10-5
         *
         * 관살 복합구조
         * =================================================
         */

        OfficerCompositeAnalysis officerAnalysis =
                officerCompositeAnalyzer
                        .analyze(
                                distributionAnalysis,
                                dayMasterStrengthAnalysis
                        );

        /*
         * =================================================
         * 10-6
         *
         * 인성 복합구조
         * =================================================
         */

        ResourceCompositeAnalysis resourceAnalysis =
                resourceCompositeAnalyzer
                        .analyze(
                                distributionAnalysis,
                                dayMasterStrengthAnalysis
                        );

        /*
         * =================================================
         * 10-7
         *
         * Family 생·극 및 연쇄구조
         * =================================================
         */

        TenGodFamilyChainAnalysis familyChainAnalysis =
                tenGodFamilyChainAnalyzer
                        .analyze(
                                distributionAnalysis,
                                companionAnalysis,
                                outputAnalysis,
                                wealthAnalysis,
                                officerAnalysis,
                                resourceAnalysis
                        );

        /*
         * =================================================
         * 10-8
         *
         * 핵심 십성 복합 Pattern
         * =================================================
         */

        TenGodCorePatternAnalysis corePatternAnalysis =
                tenGodCorePatternAnalyzer
                        .analyze(
                                familyChainAnalysis
                        );

        /*
         * =================================================
         * 10-9
         *
         * 격국·강약·용신·합화 교차검증
         * =================================================
         */

        TenGodCrossValidationAnalysis crossValidationAnalysis =
                tenGodCrossValidationAnalyzer
                        .analyze(
                                corePatternAnalysis,
                                structureAnalysis,
                                dayMasterStrengthAnalysis,
                                usefulGodAnalysis,
                                combinationTransformationAnalysis,
                                crossValidationContext
                        );

        /*
         * =================================================
         * 10-10
         *
         * 최종 Aggregate
         * =================================================
         */

        return new TenGodCompositeAnalysis(
                distributionAnalysis
                        .dayMaster(),
                distributionAnalysis,
                companionAnalysis,
                outputAnalysis,
                wealthAnalysis,
                officerAnalysis,
                resourceAnalysis,
                familyChainAnalysis,
                corePatternAnalysis,
                crossValidationAnalysis,
                distributionAnalysis
                        .completeChart()
        );
    }

    /**
     * =================================================
     * 보수적 분석
     * =================================================
     *
     * 용신/합화와 핵심 Pattern 사이의
     * Alignment Mapping을 아직 구성하지 않은 경우 사용한다.
     *
     * IMPORTANT:
     *
     * usefulGodAnalysis와
     * combinationTransformationAnalysis 자체는
     * 실제 분석 결과를 그대로 보존한다.
     *
     * 단지 "어떤 10-8 Pattern을 지지/충돌/약화시키는가"라는
     * Pattern Alignment만 중립으로 처리한다.
     *
     * 임의로 지지한다고 가정하지 않는다.
     */
    public TenGodCompositeAnalysis analyzeConservatively(

            FourPillars fourPillars,

            ElementStrengthAnalysis
                    elementStrengthAnalysis,

            DayMasterStrengthAnalysis
                    dayMasterStrengthAnalysis,

            StructureAnalysis
                    structureAnalysis,

            UsefulGodAnalysis
                    usefulGodAnalysis,

            CombinationTransformationAnalysis
                    combinationTransformationAnalysis

    ) {

        return analyze(
                fourPillars,
                elementStrengthAnalysis,
                dayMasterStrengthAnalysis,
                structureAnalysis,
                usefulGodAnalysis,
                combinationTransformationAnalysis,
                TenGodCrossValidationContext
                        .neutral()
        );
    }

    /*
     * =================================================
     * 선행 분석 정합성 검증
     * =================================================
     */

    private void validateInputs(

            FourPillars fourPillars,

            ElementStrengthAnalysis
                    elementStrengthAnalysis,

            DayMasterStrengthAnalysis
                    dayMasterStrengthAnalysis,

            StructureAnalysis
                    structureAnalysis,

            UsefulGodAnalysis
                    usefulGodAnalysis,

            CombinationTransformationAnalysis
                    combinationTransformationAnalysis,

            TenGodCrossValidationContext
                    crossValidationContext

    ) {

        Objects.requireNonNull(
                fourPillars,
                "FourPillars가 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 ElementStrengthAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterStrengthAnalysis,
                "4차 DayMasterStrengthAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "5차 StructureAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                usefulGodAnalysis,
                "7차 UsefulGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                combinationTransformationAnalysis,
                "9차 CombinationTransformationAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                crossValidationContext,
                "10-9 TenGodCrossValidationContext가 필요합니다."
        );

        /*
         * =================================================
         * 3차 ↔ 4차
         * =================================================
         */

        if (
                elementStrengthAnalysis
                        .dayMaster()

                        != dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "3차 오행 세력 분석과 4차 신강·신약 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis
                        .completeChart()

                        != dayMasterStrengthAnalysis
                        .elementStrengthAnalysis()
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "3차와 4차 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 3차 ↔ 7차
         * =================================================
         */

        if (
                elementStrengthAnalysis
                        .dayMaster()

                        != usefulGodAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "3차 오행 세력 분석과 7차 용신 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis
                        .completeChart()

                        != usefulGodAnalysis
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "3차와 7차 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 3차 ↔ 9차
         * =================================================
         */

        if (
                elementStrengthAnalysis
                        .dayMaster()

                        != combinationTransformationAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "3차 오행 세력 분석과 9차 합화 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                elementStrengthAnalysis
                        .completeChart()

                        != combinationTransformationAnalysis
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "3차와 9차 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }
}