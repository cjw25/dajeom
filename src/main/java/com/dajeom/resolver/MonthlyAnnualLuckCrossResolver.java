package com.dajeom.resolver;

import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.MonthlyAnnualLuckCrossResolution;
import com.dajeom.model.MonthlyLuckPeriod;

/**
 * 정밀화 15차-6.
 *
 * 기존:
 *
 * 8차 관계
 * +
 * 9차 합화
 *
 * 와:
 *
 * 세운 × 월운
 *
 * 을 연결하는 Boundary.
 *
 * =================================================
 *
 * 구현체 책임:
 *
 * 1.
 * 세운 천간 ↔ 월운 천간
 *
 * 2.
 * 세운 지지 ↔ 월운 지지
 *
 * 3.
 * 기존 9차 조건에 따른
 * 실제 합화 / 기능 변화
 *
 * =================================================
 *
 * 이 Resolver에서는:
 *
 * - 원국을 넣지 않는다.
 * - 대운을 넣지 않는다.
 * - 길흉 점수를 만들지 않는다.
 *
 * 원국·대운까지 포함한 다층 관계는
 * 15-7에서 처리한다.
 */
public interface MonthlyAnnualLuckCrossResolver {

    MonthlyAnnualLuckCrossResolution resolve(

            AnnualLuckPeriod annualPeriod,

            MonthlyLuckPeriod monthlyPeriod

    );
}