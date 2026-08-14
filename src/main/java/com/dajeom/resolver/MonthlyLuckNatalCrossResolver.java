package com.dajeom.resolver;

import com.dajeom.model.MonthlyLuckNatalCrossResolution;
import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.PalaceAnalysis;

/**
 * 정밀화 15차-4.
 *
 * 기존:
 *
 * 8차 천간·지지 관계
 * +
 * 9차 합화
 *
 * 와 월운을 연결하는 Boundary.
 *
 * =================================================
 *
 * 구현체 책임:
 *
 * 1.
 * 월운 천간 × 원국 천간
 *
 * 2.
 * 월운 지지 × 원국 지지
 *
 * 3.
 * 월운이 들어와 완성되는
 * 원국 다중관계
 *
 * 4.
 * 기존 9차 기준에 따른
 * 실제 합화 여부
 *
 * =================================================
 *
 * 여기서는 관계표를 다시 구현하지 않는다.
 *
 * 월운 × 대운은 15-5.
 * 월운 × 세운은 15-6.
 */
public interface MonthlyLuckNatalCrossResolver {

    MonthlyLuckNatalCrossResolution resolve(

            PalaceAnalysis natalAnalysis,

            MonthlyLuckPeriod monthlyLuckPeriod

    );
}