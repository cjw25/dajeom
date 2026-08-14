package com.dajeom.resolver;

import com.dajeom.model.MonthlyLuckPeriod;
import com.dajeom.model.MonthlyLuckResolvedTenGodSource;
import com.dajeom.type.HeavenlyStem;

import java.util.List;

/**
 * 정밀화 15차-3.
 *
 * 기존:
 *
 * - HiddenStemTable
 * - StructureTenGodCalculator
 *
 * 와 월운 사이를 연결하는 Boundary.
 *
 * =================================================
 *
 * 입력:
 *
 * 원국 일간
 * +
 * MonthlyLuckPeriod
 *
 * =================================================
 *
 * 출력:
 *
 * - 월운 천간 십성
 * - 지지 본기 십성
 * - 지지 중기 십성
 * - 지지 여기 십성
 *
 * =================================================
 *
 * 월운 단계에서 지장간표나
 * 십성 공식을 새로 작성하지 않는다.
 */
public interface MonthlyLuckTenGodSourceResolver {

    List<MonthlyLuckResolvedTenGodSource> resolve(

            HeavenlyStem dayMaster,

            MonthlyLuckPeriod monthlyLuckPeriod

    );
}