package com.dajeom.resolver;

import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.AnnualLuckResolvedTenGodSource;
import com.dajeom.type.HeavenlyStem;

import java.util.List;

/**
 * 정밀화 14차-3.
 *
 * 기존:
 *
 * - HiddenStemTable
 * - StructureTenGodCalculator
 *
 * 와 세운 분석 사이의 Boundary.
 *
 * =================================================
 *
 * 입력:
 *
 * dayMaster
 * +
 * AnnualLuckPeriod
 *
 * =================================================
 *
 * 출력:
 *
 * 세운 천간 십성
 * +
 * 세운 지지 본기 십성
 * +
 * 세운 지지 중기 십성
 * +
 * 세운 지지 여기 십성
 *
 * =================================================
 *
 * 실제 구현체는 기존 프로젝트의
 * canonical 지장간 자료와 십성 계산기를
 * 반드시 재사용한다.
 *
 * 14차에서 십성 공식을 다시 작성하지 않는다.
 */
public interface AnnualLuckTenGodSourceResolver {

    List<AnnualLuckResolvedTenGodSource> resolve(

            HeavenlyStem dayMaster,

            AnnualLuckPeriod annualLuckPeriod

    );
}