package com.dajeom.resolver;

import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.MonthlyDecadeLuckCrossResolution;
import com.dajeom.model.MonthlyLuckPeriod;

/**
 * 정밀화 15차-5.
 *
 * 기존:
 *
 * 8차 관계
 * +
 * 9차 합화
 *
 * 와 대운 × 월운을 연결하는 Boundary.
 *
 * =================================================
 *
 * 구현체 책임:
 *
 * 대운 천간 ↔ 월운 천간
 *
 * 대운 지지 ↔ 월운 지지
 *
 * 기존 9차 합화 조건 적용
 *
 * =================================================
 *
 * 여기서는:
 *
 * 원국
 * 세운
 *
 * 을 끼워 넣지 않는다.
 *
 * 복합 다층 Context는 15-7에서 처리한다.
 */
public interface MonthlyDecadeLuckCrossResolver {

    MonthlyDecadeLuckCrossResolution resolve(

            MonthlyLuckPeriod monthlyPeriod,

            DecadeLuckPeriod decadePeriod

    );
}