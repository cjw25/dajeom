package com.dajeom.resolver;

import com.dajeom.model.AnnualDecadeLuckCrossResolution;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.DecadeLuckPeriod;

/**
 * 정밀화 14차-5.
 *
 * 기존:
 *
 * 8차 관계
 * +
 * 9차 합화
 *
 * 와:
 *
 * 대운 × 세운
 *
 * 을 연결하는 Boundary.
 *
 * =================================================
 *
 * 구현체 책임:
 *
 * 1.
 * 세운 천간 ↔ 대운 천간
 *
 * 2.
 * 세운 지지 ↔ 대운 지지
 *
 * 3.
 * 기존 9차 기준에 따른
 *    합화 활성 여부
 *
 * =================================================
 *
 * 여기서는:
 *
 * 원국 + 대운 + 세운
 *
 * 3중 구조를 계산하지 않는다.
 *
 * 그 교차 Context는 14-6에서 처리한다.
 */
public interface AnnualDecadeLuckCrossResolver {

    AnnualDecadeLuckCrossResolution resolve(

            AnnualLuckPeriod annualPeriod,

            DecadeLuckPeriod decadePeriod

    );
}