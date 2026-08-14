package com.dajeom.resolver;

import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.AnnualLuckDecadeContextAnalysis;
import com.dajeom.model.DecadeLuckPeriodContextAnalysis;
import com.dajeom.model.MonthlyAnnualLuckRelationAnalysis;
import com.dajeom.model.MonthlyDecadeLuckInteraction;
import com.dajeom.model.MonthlyLuckContextResolution;
import com.dajeom.model.MonthlyLuckNatalRelationAnalysis;
import com.dajeom.model.MonthlyLuckTenGodAnalysis;

/**
 * 정밀화 15차-7.
 *
 * 월운과 기존 원국·대운·세운 분석을
 * 교차시키는 Boundary.
 *
 * =================================================
 *
 * 실제 구현체는 요청별로:
 *
 * 5차 격국
 * 7차 용신
 * 10차 십성 복합구조
 * 12차 궁위
 *
 * 결과를 참조할 수 있어야 한다.
 *
 * =================================================
 *
 * Resolver 구현체에 사용자별 mutable 상태를
 * Spring Singleton field로 저장하면 안 된다.
 *
 * 요청별 immutable Adapter를 만들거나
 * 필요한 분석 객체를 명시적으로 전달한다.
 */
public interface MonthlyLuckContextResolver {

    /**
     * =================================================
     * 월운 공통 Context
     * =================================================
     *
     * 대운이 무엇인지와 상관없이
     * 월운 자체에서 확정 가능한 Evidence.
     *
     * 여기에서는 예를 들어:
     *
     * - 월운 십성 유입
     * - 용신/희신 계열 유입
     * - 격국과의 관계
     * - 원국 × 월운
     * - 세운 × 월운
     * - Palace 활성
     * - 원국 + 세운 + 월운 3층 관계
     *
     * 등을 생성할 수 있다.
     */
    MonthlyLuckContextResolution resolveMonthly(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis

    );

    /**
     * =================================================
     * 특정 대운 Segment에서만 성립하는 Context
     * =================================================
     *
     * 여기에서는:
     *
     * - 대운 × 월운
     * - 대운 Context × 월운
     * - 세운 Segment Context × 월운
     * - 원국 + 대운 + 월운
     * - 대운 + 세운 + 월운
     * - 원국 + 대운 + 세운 + 월운
     *
     * 등을 생성할 수 있다.
     */
    MonthlyLuckContextResolution resolveWithDecade(

            AnnualLuckAnalysis annualLuckAnalysis,

            MonthlyLuckTenGodAnalysis monthlyTenGodAnalysis,

            MonthlyLuckNatalRelationAnalysis natalRelationAnalysis,

            MonthlyAnnualLuckRelationAnalysis annualRelationAnalysis,

            MonthlyDecadeLuckInteraction decadeInteraction,

            DecadeLuckPeriodContextAnalysis decadeContext,

            AnnualLuckDecadeContextAnalysis annualDecadeContext

    );
}