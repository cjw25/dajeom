package com.dajeom.engine;

import com.dajeom.model.AnnualCrossFunctionalSynthesisAnalysis;
import com.dajeom.model.AnnualFunctionalBaselineAnalysis;
import com.dajeom.type.AnnualFunctionalBaselineStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 세운 Functional 체인의
 * 공식 최종 Baseline Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * AnnualCrossFunctionalSynthesisAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * AnnualFunctionalBaselineAnalysis
 *
 * =================================================
 *
 * 이 Engine은 새로운 명리 계산을 하지 않는다.
 *
 * 지금까지 계산된:
 *
 * - 세운 자체 Function
 * - Natal Fixed Capacity
 * - 대운 Segment
 * - 14-5 Transformation
 * - CrossFunctional Pattern
 * - 14-6 Context
 *
 * 를 공식 Annual Functional 기준선으로
 * 봉합한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 신강신약 재계산
 * - 점수 추가
 * - 용신 재선정
 * - Context 독립 Evidence 승격
 * - Segment 평균
 * - 길흉 판정
 * - LifeDomain 판정
 * - 사건 생성
 */
@Component
public class AnnualFunctionalBaselineAnalysisEngine {

    public AnnualFunctionalBaselineAnalysis analyze(

            AnnualCrossFunctionalSynthesisAnalysis
                    crossFunctionalSynthesisAnalysis

    ) {

        Objects.requireNonNull(
                crossFunctionalSynthesisAnalysis,
                "Annual Functional Baseline 생성에는 AnnualCrossFunctionalSynthesisAnalysis가 필요합니다."
        );

        boolean completeChart =
                crossFunctionalSynthesisAnalysis
                        .completeChart();

        AnnualFunctionalBaselineStatus baselineStatus =
                AnnualFunctionalBaselineStatus.of(
                        crossFunctionalSynthesisAnalysis
                                .analysisAvailable(),
                        crossFunctionalSynthesisAnalysis
                                .segmentAnalyses()
                                .size()
                );

        return new AnnualFunctionalBaselineAnalysis(
                crossFunctionalSynthesisAnalysis,
                baselineStatus,
                completeChart
        );
    }
}