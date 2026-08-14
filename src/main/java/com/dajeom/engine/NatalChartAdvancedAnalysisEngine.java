package com.dajeom.engine;

import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 원국 정밀 고차 분석 Engine.
 *
 * =================================================
 *
 * 현재 정밀화 파이프라인을 연결한다.
 *
 * 기본 입력:
 *
 * NatalChartAnalysis
 *
 * =================================================
 *
 * 3차
 * ElementStrengthAnalysis
 *
 * ↓
 *
 * 4차
 * DayMasterStrengthAnalysis
 *
 * ↓
 *
 * 5차
 * StructureAnalysis
 *
 * ↓
 *
 * 6차
 * ClimateHealthAnalysis
 *
 * ↓
 *
 * 7차
 * UsefulGodAnalysis
 *
 * =================================================
 *
 * 동시에 기본 원국에 이미 포함된:
 *
 * 8차
 * RelationAnalysis
 *
 * +
 *
 * 3차
 * ElementStrengthAnalysis
 *
 * ↓
 *
 * 9차
 * CombinationTransformationAnalysis
 *
 * =================================================
 *
 * 마지막으로 모든 결과를
 * NatalChartAdvancedAnalysis로 통합한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * - 이미 계산한 upstream 결과는 재사용한다.
 * - StructureAnalyzer를 직접 호출하지 않는다.
 * - 구형 TemperatureAnalyzer를 사용하지 않는다.
 * - 구형 MediationAnalyzer를 사용하지 않는다.
 * - 구형 UsefulGodAnalyzer를 사용하지 않는다.
 * - 구형 CombinationTransformationAnalyzer를 사용하지 않는다.
 * - 정밀화 공식 Engine들을 통해서만 연결한다.
 */
@Component
public class NatalChartAdvancedAnalysisEngine {

    private final DayMasterStrengthAnalysisEngine
            dayMasterStrengthAnalysisEngine;

    private final StructureAnalysisEngine
            structureAnalysisEngine;

    private final ClimateHealthAnalysisEngine
            climateHealthAnalysisEngine;

    private final UsefulGodAnalysisEngine
            usefulGodAnalysisEngine;

    private final CombinationTransformationAnalysisEngine
            combinationTransformationAnalysisEngine;

    public NatalChartAdvancedAnalysisEngine(

            DayMasterStrengthAnalysisEngine
                    dayMasterStrengthAnalysisEngine,

            StructureAnalysisEngine
                    structureAnalysisEngine,

            ClimateHealthAnalysisEngine
                    climateHealthAnalysisEngine,

            UsefulGodAnalysisEngine
                    usefulGodAnalysisEngine,

            CombinationTransformationAnalysisEngine
                    combinationTransformationAnalysisEngine

    ) {

        this.dayMasterStrengthAnalysisEngine =
                Objects.requireNonNull(
                        dayMasterStrengthAnalysisEngine,
                        "DayMasterStrengthAnalysisEngine이 필요합니다."
                );

        this.structureAnalysisEngine =
                Objects.requireNonNull(
                        structureAnalysisEngine,
                        "StructureAnalysisEngine이 필요합니다."
                );

        this.climateHealthAnalysisEngine =
                Objects.requireNonNull(
                        climateHealthAnalysisEngine,
                        "ClimateHealthAnalysisEngine이 필요합니다."
                );

        this.usefulGodAnalysisEngine =
                Objects.requireNonNull(
                        usefulGodAnalysisEngine,
                        "UsefulGodAnalysisEngine이 필요합니다."
                );

        this.combinationTransformationAnalysisEngine =
                Objects.requireNonNull(
                        combinationTransformationAnalysisEngine,
                        "CombinationTransformationAnalysisEngine이 필요합니다."
                );
    }

    /**
     * 기본 원국 분석 결과를 받아
     * 정밀화 3차~9차의 고차 원국 분석을 연결한다.
     */
    public NatalChartAdvancedAnalysis analyze(
            NatalChartAnalysis natalChart
    ) {

        Objects.requireNonNull(
                natalChart,
                "원국 분석 결과는 필수입니다."
        );

        /*
         * =================================================
         * 3차 + 4차
         *
         * FourPillars부터:
         *
         * ElementStrengthAnalysis
         * ↓
         * DayMasterStrengthAnalysis
         *
         * 를 한 번만 계산한다.
         * =================================================
         */
        DayMasterStrengthAnalysis
                dayMasterStrengthAnalysis =
                dayMasterStrengthAnalysisEngine
                        .analyze(
                                natalChart.fourPillars()
                        );

        /*
         * 4차 결과 안에 들어 있는
         * 3차 결과를 그대로 재사용한다.
         */
        ElementStrengthAnalysis
                elementStrengthAnalysis =
                dayMasterStrengthAnalysis
                        .elementStrengthAnalysis();

        /*
         * =================================================
         * 5차
         *
         * 이미 계산한 4차를 넘겨
         * 격국 분석만 수행한다.
         * =================================================
         */
        StructureAnalysis structureAnalysis =
                structureAnalysisEngine
                        .analyze(
                                dayMasterStrengthAnalysis
                        );

        /*
         * =================================================
         * 6차
         *
         * 이미 계산한 5차를 넘겨
         * 조후·병약·통관 분석만 수행한다.
         * =================================================
         */
        ClimateHealthAnalysis
                climateHealthAnalysis =
                climateHealthAnalysisEngine
                        .analyze(
                                structureAnalysis
                        );

        /*
         * =================================================
         * 7차
         *
         * 6-10 결과를 이용해서
         * 용신·희신·기신을 분석한다.
         * =================================================
         */
        UsefulGodAnalysis usefulGodAnalysis =
                usefulGodAnalysisEngine
                        .analyze(
                                climateHealthAnalysis
                        );

        /*
         * =================================================
         * 8차
         *
         * RelationAnalysis는
         * NatalChartAnalysisEngine에서 이미 계산되어
         * natalChart 내부에 들어 있다.
         *
         * 여기서 다시 계산하지 않는다.
         * =================================================
         */

        /*
         * =================================================
         * 9차
         *
         * 8차 RelationAnalysis
         *
         * +
         *
         * 3차 ElementStrengthAnalysis
         *
         * 를 이용해서 합화 분석을 수행한다.
         * =================================================
         */
        CombinationTransformationAnalysis
                combinationTransformationAnalysis =
                combinationTransformationAnalysisEngine
                        .analyze(
                                natalChart.relationAnalysis(),
                                elementStrengthAnalysis
                        );

        /*
         * =================================================
         * 최종 고차 원국 분석 통합.
         * =================================================
         */
        return new NatalChartAdvancedAnalysis(
                natalChart,
                dayMasterStrengthAnalysis,
                structureAnalysis,
                climateHealthAnalysis,
                usefulGodAnalysis,
                combinationTransformationAnalysis
        );
    }
}