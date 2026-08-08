package com.dajeom.engine;

import com.dajeom.analyzer.CombinationTransformationAnalyzer;
import com.dajeom.analyzer.MediationAnalyzer;
import com.dajeom.analyzer.StructureAnalyzer;
import com.dajeom.analyzer.TemperatureAnalyzer;
import com.dajeom.analyzer.UsefulGodAnalyzer;
import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.MediationAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.TemperatureAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 원국의 고차 명리 분석 Engine.
 *
 * 처리 순서:
 *
 * NatalChartAnalysis
 *      ↓
 * StructureAnalysis
 *      ↓
 * TemperatureAnalysis
 *      ↓
 * CombinationTransformationAnalysis
 *      ↓
 * MediationAnalysis
 *      ↓
 * UsefulGodAnalysis
 *      ↓
 * NatalChartAdvancedAnalysis
 */
@Component
public class NatalChartAdvancedAnalysisEngine {

    private final StructureAnalyzer
            structureAnalyzer;

    private final TemperatureAnalyzer
            temperatureAnalyzer;

    private final CombinationTransformationAnalyzer
            combinationTransformationAnalyzer;

    private final MediationAnalyzer
            mediationAnalyzer;

    private final UsefulGodAnalyzer
            usefulGodAnalyzer;

    public NatalChartAdvancedAnalysisEngine(
            StructureAnalyzer structureAnalyzer,
            TemperatureAnalyzer temperatureAnalyzer,
            CombinationTransformationAnalyzer
                    combinationTransformationAnalyzer,
            MediationAnalyzer mediationAnalyzer,
            UsefulGodAnalyzer usefulGodAnalyzer
    ) {

        this.structureAnalyzer =
                structureAnalyzer;

        this.temperatureAnalyzer =
                temperatureAnalyzer;

        this.combinationTransformationAnalyzer =
                combinationTransformationAnalyzer;

        this.mediationAnalyzer =
                mediationAnalyzer;

        this.usefulGodAnalyzer =
                usefulGodAnalyzer;
    }

    public NatalChartAdvancedAnalysis analyze(
            NatalChartAnalysis natalChart
    ) {

        Objects.requireNonNull(
                natalChart,
                "원국 분석 결과는 필수입니다."
        );

        /*
         * 1.
         * 격국/구조 분석.
         */
        StructureAnalysis structureAnalysis =
                structureAnalyzer.analyze(
                        natalChart
                );

        /*
         * 2.
         * 한난조습 분석.
         */
        TemperatureAnalysis temperatureAnalysis =
                temperatureAnalyzer.analyze(
                        natalChart
                );

        /*
         * 3.
         * 단순 합 관계와
         * 실제 합화 가능성을 분리해서 분석.
         */
        CombinationTransformationAnalysis
                combinationAnalysis =
                combinationTransformationAnalyzer
                        .analyze(
                                natalChart
                        );

        /*
         * 4.
         * 오행 충돌 및 통관 분석.
         */
        MediationAnalysis mediationAnalysis =
                mediationAnalyzer.analyze(
                        natalChart,
                        combinationAnalysis
                );

        /*
         * 5.
         * 억부 + 조후 + 격국
         * + 합화 + 통관을 이용해
         * 용신 후보를 다시 분석한다.
         */
        UsefulGodAnalysis usefulGodAnalysis =
                usefulGodAnalyzer.analyze(
                        natalChart,
                        structureAnalysis,
                        temperatureAnalysis,
                        combinationAnalysis,
                        mediationAnalysis
                );

        return new NatalChartAdvancedAnalysis(
                natalChart,
                structureAnalysis,
                temperatureAnalysis,
                combinationAnalysis,
                mediationAnalysis,
                usefulGodAnalysis
        );
    }
}