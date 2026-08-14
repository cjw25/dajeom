package com.dajeom.resolver;

import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckCandidate;
import com.dajeom.model.DecadeLuckAnalysis;
import com.dajeom.model.MonthlyLuckAnalysis;
import com.dajeom.model.MonthlyLuckCandidate;
import com.dajeom.model.PalaceAnalysis;

/**
 * 정밀화 16차-7.
 *
 * 16차에서 선택된 실제 Period와
 * 14차/15차 분석 계층을 연결하는 Boundary.
 *
 * =================================================
 *
 * 매우 중요한 Identity 정책:
 *
 * resolveAnnual() 결과:
 *
 * result.period()
 * ==
 * annualCandidate.period()
 *
 * 이어야 한다.
 *
 * =================================================
 *
 * resolveMonthly() 결과:
 *
 * result.period()
 * ==
 * monthlyCandidate.period()
 *
 * 그리고:
 *
 * result.annualLuckAnalysis()
 * ==
 * annualLuckAnalysis
 *
 * 이어야 한다.
 *
 * =================================================
 *
 * 즉:
 *
 * 같은 연도/같은 월운이라는 이유로
 * 새로운 Period 객체를 다시 생성해서는 안 된다.
 *
 * 16차 Selection에서 선택된 정확한 객체를
 * 그대로 분석 계층에 전달해야 한다.
 */
public interface LuckIntegratedLayerAnalysisResolver {

    /**
     * 선택된 세운 Period를 그대로 사용해서
     * 14차 AnnualLuckAnalysis를 생성/조회한다.
     */
    AnnualLuckAnalysis resolveAnnual(

            PalaceAnalysis natalAnalysis,

            DecadeLuckAnalysis decadeLuckAnalysis,

            AnnualLuckCandidate annualCandidate

    );

    /**
     * 선택된 월운 Period를 그대로 사용해서
     * 15차 MonthlyLuckAnalysis를 생성/조회한다.
     */
    MonthlyLuckAnalysis resolveMonthly(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckCandidate monthlyCandidate

    );
}