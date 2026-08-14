package com.dajeom.engine;

import com.dajeom.analyzer.ElementStrengthAnalyzer;
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
 * 사주 원국의 기본 통합 분석 Engine.
 *
 * =================================================
 *
 * 처리 흐름:
 *
 * FourPillars
 *
 * ↓
 *
 * NatalChartBaseAnalysisEngine
 *
 * ↓
 *
 * NatalChartBaseAnalysis
 *
 * ↓
 *
 * ElementStrengthAnalyzer
 *
 * ↓
 *
 * ElementStrength
 *
 * ↓
 *
 * StrengthAnalyzer
 *
 * ↓
 *
 * StrengthAnalysis
 *
 * =================================================
 *
 * 관계 분석은 정밀화 8차의 공식 Engine인
 * RelationAnalysisEngine을 사용한다.
 *
 * FourPillars
 *
 * ↓
 *
 * RelationAnalysisEngine
 *
 * ↓
 *
 * RelationAnalysis
 *
 * =================================================
 *
 * 최종:
 *
 * NatalChartAnalysis
 *
 * 이 Engine은 분석 결과를 조립하며
 * 직접적인 명리 Rule을 구현하지 않는다.
 */
@Component
public class NatalChartAnalysisEngine {

    private final NatalChartBaseAnalysisEngine
            natalChartBaseAnalysisEngine;

    private final ElementStrengthAnalyzer
            elementStrengthAnalyzer;

    private final StrengthAnalyzer
            strengthAnalyzer;

    private final RelationAnalysisEngine
            relationAnalysisEngine;

    public NatalChartAnalysisEngine(

            NatalChartBaseAnalysisEngine
                    natalChartBaseAnalysisEngine,

            ElementStrengthAnalyzer
                    elementStrengthAnalyzer,

            StrengthAnalyzer
                    strengthAnalyzer,

            RelationAnalysisEngine
                    relationAnalysisEngine

    ) {

        this.natalChartBaseAnalysisEngine =
                Objects.requireNonNull(
                        natalChartBaseAnalysisEngine,
                        "NatalChartBaseAnalysisEngine이 필요합니다."
                );

        this.elementStrengthAnalyzer =
                Objects.requireNonNull(
                        elementStrengthAnalyzer,
                        "ElementStrengthAnalyzer가 필요합니다."
                );

        this.strengthAnalyzer =
                Objects.requireNonNull(
                        strengthAnalyzer,
                        "StrengthAnalyzer가 필요합니다."
                );

        this.relationAnalysisEngine =
                Objects.requireNonNull(
                        relationAnalysisEngine,
                        "RelationAnalysisEngine이 필요합니다."
                );
    }

    /**
     * 계산된 사주팔자를 받아
     * 원국 기본 통합 분석을 수행한다.
     */
    public NatalChartAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        /*
         * =================================================
         * 1.
         *
         * 십성 / 지장간 / 기본 오행 분석.
         * =================================================
         */
        NatalChartBaseAnalysis baseAnalysis =
                natalChartBaseAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 2.
         *
         * 기존 기본 계층에서 사용하는
         * 가중 오행 세력 분석.
         * =================================================
         */
        ElementStrength elementStrength =
                elementStrengthAnalyzer
                        .analyze(
                                baseAnalysis
                        );

        /*
         * =================================================
         * 3.
         *
         * 기존 기본 계층의 강약 분석.
         *
         * ElementStrength를 다시 계산하지 않는다.
         * =================================================
         */
        StrengthAnalysis strengthAnalysis =
                strengthAnalyzer
                        .analyze(
                                baseAnalysis,
                                elementStrength
                        );

        /*
         * =================================================
         * 4.
         *
         * 정밀화 8차 천간/지지 관계 분석.
         *
         * 구형 RelationAnalyzer를 사용하지 않는다.
         * =================================================
         */
        RelationAnalysis relationAnalysis =
                relationAnalysisEngine
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 5.
         *
         * 기본 원국 분석 통합.
         * =================================================
         */
        return new NatalChartAnalysis(
                baseAnalysis,
                elementStrength,
                strengthAnalysis,
                relationAnalysis
        );
    }
}