package com.dajeom.engine;

import com.dajeom.analyzer.ElementInteractionAnalyzer;
import com.dajeom.analyzer.ElementPowerAnalyzer;
import com.dajeom.analyzer.HiddenElementPowerAnalyzer;
import com.dajeom.analyzer.RootElementPowerAnalyzer;
import com.dajeom.analyzer.SeasonalElementPowerAnalyzer;
import com.dajeom.analyzer.StemExposureElementPowerAnalyzer;
import com.dajeom.analyzer.VisibleStemPowerAnalyzer;
import com.dajeom.model.BasePowerAnalysis;
import com.dajeom.model.ElementInteractionAnalysis;
import com.dajeom.model.ElementPowerAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenElementPowerAnalysis;
import com.dajeom.model.RootElementPowerAnalysis;
import com.dajeom.model.SeasonalElementPowerAnalysis;
import com.dajeom.model.StemExposureElementPowerAnalysis;
import com.dajeom.model.VisibleStemPowerAnalysis;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 3차 전체 오행 세력 분석 Engine.
 *
 * 정밀화 2차에서 생성된:
 *
 * BasePowerAnalysis
 *
 * 을 받아서:
 *
 * 3-1 천간 표면
 * 3-2 지장간
 * 3-3 월령
 * 3-4 통근
 * 3-5 투간
 * 3-6 생극
 * 3-7 최종 오행 세력
 *
 * 을 순서대로 계산한다.
 *
 * 이 Engine 이후 상위 계층은
 * 세부 Analyzer를 각각 호출하지 않고
 * ElementStrengthAnalysis 하나를
 * 사용하면 된다.
 */
@Component
public class ElementStrengthAnalysisEngine {

    private final BasePowerAnalysisEngine
            basePowerAnalysisEngine;

    private final VisibleStemPowerAnalyzer
            visibleStemPowerAnalyzer;

    private final HiddenElementPowerAnalyzer
            hiddenElementPowerAnalyzer;

    private final SeasonalElementPowerAnalyzer
            seasonalElementPowerAnalyzer;

    private final RootElementPowerAnalyzer
            rootElementPowerAnalyzer;

    private final StemExposureElementPowerAnalyzer
            stemExposureElementPowerAnalyzer;

    private final ElementInteractionAnalyzer
            elementInteractionAnalyzer;

    private final ElementPowerAnalyzer
            elementPowerAnalyzer;

    public ElementStrengthAnalysisEngine(

            BasePowerAnalysisEngine basePowerAnalysisEngine,

            VisibleStemPowerAnalyzer visibleStemPowerAnalyzer,

            HiddenElementPowerAnalyzer hiddenElementPowerAnalyzer,

            SeasonalElementPowerAnalyzer seasonalElementPowerAnalyzer,

            RootElementPowerAnalyzer rootElementPowerAnalyzer,

            StemExposureElementPowerAnalyzer
                    stemExposureElementPowerAnalyzer,

            ElementInteractionAnalyzer
                    elementInteractionAnalyzer,

            ElementPowerAnalyzer elementPowerAnalyzer

    ) {

        this.basePowerAnalysisEngine =
                Objects.requireNonNull(
                        basePowerAnalysisEngine,
                        "BasePowerAnalysisEngine이 필요합니다."
                );

        this.visibleStemPowerAnalyzer =
                Objects.requireNonNull(
                        visibleStemPowerAnalyzer,
                        "VisibleStemPowerAnalyzer가 필요합니다."
                );

        this.hiddenElementPowerAnalyzer =
                Objects.requireNonNull(
                        hiddenElementPowerAnalyzer,
                        "HiddenElementPowerAnalyzer가 필요합니다."
                );

        this.seasonalElementPowerAnalyzer =
                Objects.requireNonNull(
                        seasonalElementPowerAnalyzer,
                        "SeasonalElementPowerAnalyzer가 필요합니다."
                );

        this.rootElementPowerAnalyzer =
                Objects.requireNonNull(
                        rootElementPowerAnalyzer,
                        "RootElementPowerAnalyzer가 필요합니다."
                );

        this.stemExposureElementPowerAnalyzer =
                Objects.requireNonNull(
                        stemExposureElementPowerAnalyzer,
                        "StemExposureElementPowerAnalyzer가 필요합니다."
                );

        this.elementInteractionAnalyzer =
                Objects.requireNonNull(
                        elementInteractionAnalyzer,
                        "ElementInteractionAnalyzer가 필요합니다."
                );

        this.elementPowerAnalyzer =
                Objects.requireNonNull(
                        elementPowerAnalyzer,
                        "ElementPowerAnalyzer가 필요합니다."
                );
    }

    /**
     * FourPillars에서 바로
     * 정밀화 2차 + 3차 전체를 실행한다.
     *
     * 이미 FourPillars가 있는 상위 Engine에서는
     * 이 메서드 하나만 호출하면 된다.
     */
    public ElementStrengthAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        BasePowerAnalysis basePowerAnalysis =
                basePowerAnalysisEngine.analyze(
                        fourPillars
                );

        return analyze(
                basePowerAnalysis
        );
    }

    /**
     * 정밀화 2차가 이미 계산되어 있다면
     * BasePowerAnalysis를 직접 받아
     * 3차만 실행한다.
     */
    public ElementStrengthAnalysis analyze(
            BasePowerAnalysis basePowerAnalysis
    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        /*
         * 3-1
         *
         * 천간 표면 세력.
         */
        VisibleStemPowerAnalysis visiblePower =
                visibleStemPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        /*
         * 3-2
         *
         * 지장간 내부 세력.
         */
        HiddenElementPowerAnalysis hiddenPower =
                hiddenElementPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        /*
         * 3-3
         *
         * 월령 계절 세력.
         *
         * 이미 계산된 hiddenPower를 넘겨
         * 지장간 분석을 다시 하지 않는다.
         */
        SeasonalElementPowerAnalysis seasonalPower =
                seasonalElementPowerAnalyzer.analyze(
                        basePowerAnalysis,
                        hiddenPower
                );

        /*
         * 3-4
         *
         * 통근 활성.
         */
        RootElementPowerAnalysis rootPower =
                rootElementPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        /*
         * 3-5
         *
         * 투간 활성.
         *
         * 역시 기존 hiddenPower를 재사용한다.
         */
        StemExposureElementPowerAnalysis exposurePower =
                stemExposureElementPowerAnalyzer.analyze(
                        basePowerAnalysis,
                        hiddenPower
                );

        /*
         * 3-6
         *
         * 생조 / 설기 / 극제.
         *
         * 3-1 ~ 3-3 결과를 그대로 재사용.
         */
        ElementInteractionAnalysis interaction =
                elementInteractionAnalyzer.analyze(
                        visiblePower,
                        hiddenPower,
                        seasonalPower
                );

        /*
         * 3-7
         *
         * 최종 오행 세력.
         *
         * 이미 계산된 모든 분석 객체를 전달하므로
         * ElementPowerAnalyzer 내부에서
         * 3-1 ~ 3-6을 다시 계산하지 않는다.
         */
        ElementPowerAnalysis finalPower =
                elementPowerAnalyzer.analyze(
                        visiblePower,
                        hiddenPower,
                        seasonalPower,
                        rootPower,
                        exposurePower,
                        interaction
                );

        /*
         * 3-8
         *
         * 모든 결과를 하나의
         * 정밀 오행 세력 분석으로 통합.
         */
        return new ElementStrengthAnalysis(
                basePowerAnalysis,
                visiblePower,
                hiddenPower,
                seasonalPower,
                rootPower,
                exposurePower,
                interaction,
                finalPower
        );
    }
}