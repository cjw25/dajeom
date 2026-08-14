package com.dajeom.resolver;

import com.dajeom.model.AnnualDecadeLuckInteraction;
import com.dajeom.model.AnnualLuckContextResolution;
import com.dajeom.model.AnnualLuckNatalRelationAnalysis;
import com.dajeom.model.AnnualLuckTenGodAnalysis;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;

/**
 * 정밀화 14차-6.
 *
 * 기존 원국 분석과
 * 세운 분석을 연결하는 Boundary.
 *
 * =================================================
 *
 * 실제 구현체는 원국의:
 *
 * 5차 격국
 * 7차 용신
 * 10차 십성 복합구조
 * 12차 궁위
 *
 * 결과를 알고 있는 상태에서 생성한다.
 *
 * =================================================
 *
 * 중요한 구현 정책:
 *
 * 원국 분석 결과를 Spring Singleton Resolver의
 * mutable field에 저장하면 안 된다.
 *
 * 요청별 immutable Resolver 또는
 * Context Adapter를 생성해서 사용한다.
 *
 * =================================================
 *
 * resolveAnnual()
 *
 * 대운과 상관없이 세운 자체에서
 * 성립하는 Context를 생성한다.
 *
 * 예:
 *
 * - 용신 계열 유입
 * - 격국 기능 보조
 * - 원국 십성 구조 강화
 * - Palace 접촉
 * - 원국 × 세운 관계
 * - 원국 × 세운 합화
 *
 * =================================================
 *
 * resolveWithDecade()
 *
 * 특정 대운과 세운이 실제로
 * 겹치는 구간에서만 생성되는 Context.
 *
 * 예:
 *
 * - 대운 × 세운 관계
 * - 대운 Context와 세운 기능의 교차
 * - 원국 + 대운 + 세운 삼합
 * - 원국 + 대운 + 세운 방합
 * - 3층 합화 Context
 *
 * =================================================
 *
 * 하나의 종합 점수는 반환하지 않는다.
 */
public interface AnnualLuckContextResolver {

    /**
     * 세운 전체에 공통으로 적용되는
     * 원국 × 세운 Context.
     */
    AnnualLuckContextResolution resolveAnnual(

            AnnualLuckTenGodAnalysis
                    annualTenGodAnalysis,

            AnnualLuckNatalRelationAnalysis
                    natalRelationAnalysis

    );

    /**
     * 특정 대운과 세운이 겹치는 구간에서만
     * 추가되는 Context.
     */
    AnnualLuckContextResolution resolveWithDecade(

            AnnualLuckTenGodAnalysis
                    annualTenGodAnalysis,

            AnnualLuckNatalRelationAnalysis
                    natalRelationAnalysis,

            AnnualDecadeLuckInteraction
                    decadeInteraction,

            DecadeLuckPeriodContextAnalysis
                    decadeContext

    );
}