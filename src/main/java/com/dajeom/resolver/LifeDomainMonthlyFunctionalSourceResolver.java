package com.dajeom.resolver;

import com.dajeom.model.LuckIntegratedSegmentAnalysis;
import com.dajeom.model.MonthlyFunctionalActivationAnalysis;

import java.util.Optional;

/**
 * Monthly Functional Activation을
 * 기존 18차 Timeline Segment에 연결하기 위한
 * Boundary Resolver.
 *
 * =================================================
 *
 * LifeDomainEvidenceResolver는
 * Monthly Functional 계산을 직접 수행하지 않는다.
 *
 * 외부 공식 Pipeline에서 이미 계산된:
 *
 * MonthlyFunctionalActivationAnalysis
 *
 * 를 현재 LuckIntegratedSegmentAnalysis에 맞게
 * 제공받는다.
 *
 * =================================================
 *
 * 이유:
 *
 * - 18차에서 명리 재계산 금지
 * - 기존 16차 Timeline 유지
 * - Monthly Functional 계산층과
 *   LifeDomain Evidence 계층 책임 분리
 * - MonthlyLuckAnalysis 객체 Identity 보존
 * - 교운 Segment 임의 선택 방지
 *
 * =================================================
 *
 * 현재 공식 Pipeline에서는
 * 각 Timeline Segment에 대응하는
 * MonthlyFunctionalActivationAnalysis가
 * 반드시 제공되어야 한다.
 *
 * 이 Resolver는 계산 결과를 새로 만들거나
 * 다른 Evidence와 관계형으로 합성하지 않는다.
 */
@FunctionalInterface
public interface LifeDomainMonthlyFunctionalSourceResolver {

    Optional<MonthlyFunctionalActivationAnalysis>
    resolveMonthlyFunctionalActivation(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    );
}