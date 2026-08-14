package com.dajeom.resolver;

import com.dajeom.model.DecadeLuckContextResolution;
import com.dajeom.model.DecadeLuckPeriodNatalRelationAnalysis;
import com.dajeom.model.DecadeLuckPeriodTenGodAnalysis;

/**
 * 정밀화 13차-6.
 *
 * 기존 원국 분석:
 *
 * 5차 격국
 * 7차 용신
 * 10차 십성 복합구조
 * 12차 궁위
 *
 * 와:
 *
 * 13-4 대운 십성
 * 13-5 대운-원국 관계
 *
 * 를 교차하기 위한 Boundary.
 *
 * =================================================
 *
 * 왜 Resolver인가?
 *
 * 현재 5차/7차의 실제 public API를
 * 13차가 추측해서 결합하면 안 된다.
 *
 * 실제 구현체는 해당 원국의:
 *
 * - Structure 분석
 * - UsefulGod 분석
 * - TenGodCompositeAnalysis
 * - PalaceAnalysis
 *
 * 을 참조한 immutable adapter 형태로 만든다.
 *
 * =================================================
 *
 * 구현체가 생성할 수 있는 Signal 예:
 *
 * USEFUL_GOD
 * - PRIMARY_USEFUL_ELEMENT_ENTERED
 * - FAVORABLE_ELEMENT_ENTERED
 * - AVOID_ELEMENT_ENTERED
 *
 * STRUCTURE
 * - STRUCTURE_SUPPORT
 * - STRUCTURE_PRESSURE
 *
 * TEN_GOD
 * - NATAL_PATTERN_REINFORCED
 * - NATAL_PATTERN_CONSTRAINED
 *
 * PALACE
 * - CORE_PALACE_CONTACT
 *
 * RELATION
 * - NATAL_RELATION_ACTIVATED
 *
 * TRANSFORMATION
 * - TRANSFORMATION_ACTIVATED
 * - ORIGINAL_FUNCTION_REDUCED
 *
 * =================================================
 *
 * Resolver는 하나의 종합 점수를 반환하지 않는다.
 */
public interface DecadeLuckContextResolver {

    DecadeLuckContextResolution resolve(

            DecadeLuckPeriodTenGodAnalysis
                    luckTenGodAnalysis,

            DecadeLuckPeriodNatalRelationAnalysis
                    natalRelationAnalysis

    );
}