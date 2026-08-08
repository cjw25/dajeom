package com.dajeom.engine;

import com.dajeom.analyzer.ElementStrengthAnalyzer;
import com.dajeom.analyzer.RelationAnalyzer;
import com.dajeom.analyzer.StrengthAnalyzer;
import com.dajeom.model.ElementStrength;
import com.dajeom.model.FourPillars;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.NatalChartBaseAnalysis;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.model.StrengthAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 사주 원국의 통합 분석 Engine.
 *
 * FourPillars를 받아 지금까지 만든
 * 원국 분석 계층을 순서대로 실행한다.
 *
 * 처리 흐름:
 *
 * FourPillars
 *      ↓
 * NatalChartBaseAnalysisEngine
 *      ↓
 * NatalChartBaseAnalysis
 *      ↓
 * ElementStrengthAnalyzer
 *      ↓
 * ElementStrength
 *      ↓
 * StrengthAnalyzer
 *      ↓
 * StrengthAnalysis
 *
 * 동시에:
 *
 * FourPillars
 *      ↓
 * RelationAnalyzer
 *      ↓
 * RelationAnalysis
 *
 * 최종:
 *
 * NatalChartAnalysis
 *
 * 이 Engine은 분석 결과를 조립하지만
 * 직접적인 명리 공식이나 Rule을 구현하지 않는다.
 */
@Component
public class NatalChartAnalysisEngine {

    private final NatalChartBaseAnalysisEngine
            natalChartBaseAnalysisEngine;

    private final ElementStrengthAnalyzer
            elementStrengthAnalyzer;

    private final StrengthAnalyzer
            strengthAnalyzer;

    private final RelationAnalyzer
            relationAnalyzer;

    public NatalChartAnalysisEngine(
            NatalChartBaseAnalysisEngine
                    natalChartBaseAnalysisEngine,
            ElementStrengthAnalyzer
                    elementStrengthAnalyzer,
            StrengthAnalyzer
                    strengthAnalyzer,
            RelationAnalyzer
                    relationAnalyzer
    ) {

        this.natalChartBaseAnalysisEngine =
                natalChartBaseAnalysisEngine;

        this.elementStrengthAnalyzer =
                elementStrengthAnalyzer;

        this.strengthAnalyzer =
                strengthAnalyzer;

        this.relationAnalyzer =
                relationAnalyzer;
    }

    /**
     * 계산된 사주팔자를 받아
     * 원국 전체 분석을 수행한다.
     */
    public NatalChartAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        /*
         * 1.
         * 십성 / 지장간 / 기본 오행 분석.
         */
        NatalChartBaseAnalysis baseAnalysis =
                natalChartBaseAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        /*
         * 2.
         * 실제 분석용 가중 오행 세력 계산.
         */
        ElementStrength elementStrength =
                elementStrengthAnalyzer
                        .analyze(
                                baseAnalysis
                        );

        /*
         * 3.
         * 계산된 오행 세력을 그대로 사용해서
         * 일간 강약을 분석한다.
         *
         * ElementStrength를 다시 계산하지 않는다.
         */
        StrengthAnalysis strengthAnalysis =
                strengthAnalyzer
                        .analyze(
                                baseAnalysis,
                                elementStrength
                        );

        /*
         * 4.
         * 천간/지지 관계 분석.
         */
        RelationAnalysis relationAnalysis =
                relationAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * 5.
         * 원국 전체 결과를 하나로 통합.
         */
        return new NatalChartAnalysis(
                baseAnalysis,
                elementStrength,
                strengthAnalysis,
                relationAnalysis
        );
    }
}