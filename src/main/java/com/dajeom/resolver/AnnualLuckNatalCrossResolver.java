package com.dajeom.resolver;

import com.dajeom.model.AnnualLuckNatalCrossResolution;
import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.PalaceAnalysis;

/**
 * 정밀화 14차-4.
 *
 * 기존:
 *
 * 8차 천간·지지 관계
 * +
 * 9차 합화 분석
 *
 * 과 세운을 연결하는 Boundary.
 *
 * =================================================
 *
 * 구현체 책임:
 *
 * 1.
 * 세운 천간
 * ↕
 * 원국 천간
 *
 * 2.
 * 세운 지지
 * ↕
 * 원국 지지
 *
 * 3.
 * 세운 글자가 들어오면서 완성되는
 * 다중 관계
 *
 * - 삼합
 * - 방합
 * 등
 *
 * 4.
 * 기존 9차 기준에 따른
 * 실제 합화 여부
 *
 * =================================================
 *
 * 이 Resolver 자체에서는
 * 관계 공식이나 합화 공식을
 * 새로 정의하지 않는다.
 */
public interface AnnualLuckNatalCrossResolver {

    AnnualLuckNatalCrossResolution resolve(

            PalaceAnalysis natalAnalysis,

            AnnualLuckPeriod annualLuckPeriod

    );
}