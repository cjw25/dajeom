package com.dajeom.resolver;

import com.dajeom.model.DecadeLuckNatalCrossResolution;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.PalaceAnalysis;

/**
 * 정밀화 13차-5.
 *
 * 기존 8차 관계 엔진
 * +
 * 기존 9차 합화 엔진
 *
 * 을 대운 분석에 연결하는 Boundary.
 *
 * =================================================
 *
 * 구현체 책임:
 *
 * 1.
 * 대운 천간 ↔ 원국 천간 관계
 *
 * 2.
 * 대운 지지 ↔ 원국 지지 관계
 *
 * 3.
 * 대운 유입으로 완성되는
 *    삼합/방합 등 다중 관계
 *
 * 4.
 * 기존 9차 기준에 따른
 *    합화 활성 여부
 *
 * =================================================
 *
 * 이 인터페이스 자체에는
 * 명리 규칙을 구현하지 않는다.
 *
 * 실제 구현체는 기존 8차/9차
 * canonical engine을 사용한다.
 */
public interface DecadeLuckNatalCrossResolver {

    DecadeLuckNatalCrossResolution resolve(

            PalaceAnalysis natalAnalysis,

            DecadeLuckPeriod period

    );
}