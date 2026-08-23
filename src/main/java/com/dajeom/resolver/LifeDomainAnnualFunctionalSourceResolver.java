package com.dajeom.resolver;

import com.dajeom.model.AnnualFunctionalBaselineAnalysis;
import com.dajeom.model.LuckIntegratedSegmentAnalysis;

import java.util.Optional;

/**
 * Annual Functional 최종 기준선을
 * 기존 18차 Timeline Segment에 연결하기 위한
 * Boundary Resolver.
 *
 * =================================================
 *
 * 18차 LifeDomainEvidenceResolver는
 * Annual Functional 계산을 직접 수행하지 않는다.
 *
 * 대신 외부에서 이미 계산된
 * AnnualFunctionalBaselineAnalysis를
 * 현재 LuckIntegratedSegmentAnalysis에 맞게
 * 제공받는다.
 *
 * =================================================
 *
 * 이유:
 *
 * - 18차에서 명리 재계산 금지
 * - 기존 16차 Timeline 유지
 * - Annual Functional 계산층과
 *   LifeDomain Evidence 계층의 책임 분리
 *
 * =================================================
 *
 * 현재 공식 Pipeline에서는
 * 각 Timeline Segment에 대응하는
 * AnnualFunctionalBaselineAnalysis가
 * 반드시 제공되어야 한다.
 *
 * 이 Resolver는 계산 결과를 새로 만들거나
 * 다른 Evidence와 관계형으로 합성하지 않는다.
 */
@FunctionalInterface
public interface LifeDomainAnnualFunctionalSourceResolver {

    Optional<AnnualFunctionalBaselineAnalysis>
    resolveAnnualFunctionalBaseline(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    );
}