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
 */
@FunctionalInterface
public interface LifeDomainAnnualFunctionalSourceResolver {

    Optional<AnnualFunctionalBaselineAnalysis>
    resolveAnnualFunctionalBaseline(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    );

    /**
     * 기존 18차 호출부를 깨뜨리지 않기 위한
     * Functional Evidence 미연결 Resolver.
     */
    static LifeDomainAnnualFunctionalSourceResolver none() {

        return segmentAnalysis ->
                Optional.empty();
    }
}