package com.dajeom.service;

import com.dajeom.config.SajuCalculationPolicy;
import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.dto.TraditionalSajuAnalysisResponse;
import com.dajeom.engine.BirthNormalizationEngine;
import com.dajeom.engine.CategoryReportEngine;
import com.dajeom.engine.FourPillarsCalculationEngine;
import com.dajeom.engine.InterpretationSignalEngine;
import com.dajeom.engine.LuckAnalysisEngine;
import com.dajeom.engine.LuckInteractionEngine;
import com.dajeom.engine.NatalChartAnalysisEngine;
import com.dajeom.model.FourPillars;
import com.dajeom.model.InterpretationSignalAnalysis;
import com.dajeom.model.LuckAnalysis;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.model.SajuReport;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * 새로운 명리 계산 엔진을 사용해
 * 사주 계산부터 최종 보고서까지
 * 전체 흐름을 관리한다.
 *
 * 기존 웹툰형 사주에서 사용하는
 * SajuAnalysisResponse와는 현재 분리하여 동작한다.
 *
 * 추후 구조가 안정되면
 * 하나의 통합 Response 구조로 변경할 수 있다.
 */
@Service
public class SajuAnalysisService {

    private final BirthNormalizationEngine
            birthNormalizationEngine;

    private final FourPillarsCalculationEngine
            fourPillarsCalculationEngine;

    private final NatalChartAnalysisEngine
            natalChartAnalysisEngine;

    private final LuckAnalysisEngine
            luckAnalysisEngine;

    private final LuckInteractionEngine
            luckInteractionEngine;

    private final InterpretationSignalEngine
            interpretationSignalEngine;

    private final CategoryReportEngine
            categoryReportEngine;

    public SajuAnalysisService(

            BirthNormalizationEngine
                    birthNormalizationEngine,

            FourPillarsCalculationEngine
                    fourPillarsCalculationEngine,

            NatalChartAnalysisEngine
                    natalChartAnalysisEngine,

            LuckAnalysisEngine
                    luckAnalysisEngine,

            LuckInteractionEngine
                    luckInteractionEngine,

            InterpretationSignalEngine
                    interpretationSignalEngine,

            CategoryReportEngine
                    categoryReportEngine
    ) {

        this.birthNormalizationEngine =
                birthNormalizationEngine;

        this.fourPillarsCalculationEngine =
                fourPillarsCalculationEngine;

        this.natalChartAnalysisEngine =
                natalChartAnalysisEngine;

        this.luckAnalysisEngine =
                luckAnalysisEngine;

        this.luckInteractionEngine =
                luckInteractionEngine;

        this.interpretationSignalEngine =
                interpretationSignalEngine;

        this.categoryReportEngine =
                categoryReportEngine;
    }

    /**
     * 출생정보를 받아
     * 최종 정통 종합사주 결과를 생성한다.
     */
    public TraditionalSajuAnalysisResponse analyze(
            SajuAnalysisRequest request
    ) {

        NormalizedBirthData birthData =
                birthNormalizationEngine.normalize(
                        request
                );

        /*
         * 1. 사주팔자 계산
         */
        FourPillars fourPillars =
                fourPillarsCalculationEngine.calculate(
                        birthData
                );

        /*
         * 2. 원국 전체 분석
         *
         * 지장간
         * 십성
         * 오행
         * 세력
         * 관계
         * 신강·신약
         * 구조
         * 용신
         */
        NatalChartAnalysis natalChartAnalysis =
                natalChartAnalysisEngine.analyze(
                        fourPillars
                );

        /*
         * 분석 연도가 없으면
         * Asia/Seoul 기준 현재 연도를 사용한다.
         */
        int targetYear =
                request.targetYear() == null

                        ? ZonedDateTime.now(
                        SajuCalculationPolicy
                                .DEFAULT_ZONE_ID
                ).getYear()

                        : request.targetYear();

        /*
         * 3. 대운·세운 분석
         */
        LuckAnalysis luckAnalysis =
                luckAnalysisEngine.analyze(

                        birthData,

                        fourPillars,

                        targetYear
                );

        /*
         * 4. 원국 + 대운 + 세운
         * 상호작용 분석
         */
        LuckInteractionAnalysis
                luckInteractionAnalysis =
                luckInteractionEngine.analyze(

                        fourPillars,

                        natalChartAnalysis,

                        luckAnalysis
                );

        /*
         * 5. 분야별 해석 신호 생성
         */
        InterpretationSignalAnalysis
                interpretationSignalAnalysis =
                interpretationSignalEngine.analyze(

                        fourPillars,

                        natalChartAnalysis,

                        luckAnalysis,

                        luckInteractionAnalysis
                );

        /*
         * 6. 13개 분야 최종 보고서 생성
         */
        SajuReport sajuReport =
                categoryReportEngine.generate(

                        fourPillars,

                        natalChartAnalysis,

                        luckAnalysis,

                        interpretationSignalAnalysis
                );

        /*
         * 7. 정통 종합사주 Response 변환
         */
        return TraditionalSajuAnalysisResponse.of(

                birthData,

                fourPillars,

                natalChartAnalysis,

                luckAnalysis,

                luckInteractionAnalysis,

                interpretationSignalAnalysis,

                sajuReport
        );
    }
}