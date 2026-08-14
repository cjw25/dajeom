package com.dajeom.engine;

import com.dajeom.analyzer.TwelveLifeStageContextAnalyzer;
import com.dajeom.analyzer.TwelveLifeStageEvidenceAnalyzer;
import com.dajeom.model.FourPillars;
import com.dajeom.model.TwelveLifeStageAnalysis;
import com.dajeom.model.TwelveLifeStageContextAnalysis;
import com.dajeom.model.TwelveLifeStageEvidenceAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 11차-5.
 *
 * 12운성 최종 Analysis Engine.
 *
 * =================================================
 *
 * 공식 실행 흐름:
 *
 * FourPillars
 *
 * ↓
 *
 * 11-3
 * TwelveLifeStageEvidenceAnalyzer
 *
 * ↓
 *
 * 11-4
 * TwelveLifeStageContextAnalyzer
 *
 * ↓
 *
 * 11-5
 * TwelveLifeStageAnalysis
 *
 * =================================================
 *
 * 11-1의 TwelveLifeStageTable과
 * 11-2의 TwelveLifeStageCalculator는
 * 11-3 내부에서 사용된다.
 *
 * =================================================
 *
 * 이 Engine은 12운성을:
 *
 * - 오행 세력
 * - 신강신약
 * - 격국
 * - 용신
 *
 * 에 재합산하지 않는다.
 *
 * 12운성은 독립적인 상태 Evidence로
 * 결과에 포함한다.
 */
@Component
public class TwelveLifeStageAnalysisEngine {

    private final TwelveLifeStageEvidenceAnalyzer
            twelveLifeStageEvidenceAnalyzer;

    private final TwelveLifeStageContextAnalyzer
            twelveLifeStageContextAnalyzer;

    public TwelveLifeStageAnalysisEngine(

            TwelveLifeStageEvidenceAnalyzer
                    twelveLifeStageEvidenceAnalyzer,

            TwelveLifeStageContextAnalyzer
                    twelveLifeStageContextAnalyzer

    ) {

        this.twelveLifeStageEvidenceAnalyzer =
                Objects.requireNonNull(
                        twelveLifeStageEvidenceAnalyzer,
                        "TwelveLifeStageEvidenceAnalyzer가 필요합니다."
                );

        this.twelveLifeStageContextAnalyzer =
                Objects.requireNonNull(
                        twelveLifeStageContextAnalyzer,
                        "TwelveLifeStageContextAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 11차 분석 진입점
     * =================================================
     */
    public TwelveLifeStageAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "12운성 분석에는 FourPillars가 필요합니다."
        );

        /*
         * =================================================
         * 11-3
         *
         * 년·월·일·시 12운성 계산.
         * =================================================
         */

        TwelveLifeStageEvidenceAnalysis evidenceAnalysis =
                twelveLifeStageEvidenceAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 11-4
         *
         * 위치 Context 및 시간 미상 Scope.
         * =================================================
         */

        TwelveLifeStageContextAnalysis contextAnalysis =
                twelveLifeStageContextAnalyzer
                        .analyze(
                                evidenceAnalysis
                        );

        /*
         * =================================================
         * 11-5
         *
         * 최종 Aggregate.
         * =================================================
         */

        return new TwelveLifeStageAnalysis(
                evidenceAnalysis.dayMaster(),
                evidenceAnalysis,
                contextAnalysis,
                evidenceAnalysis.completeChart()
        );
    }
}