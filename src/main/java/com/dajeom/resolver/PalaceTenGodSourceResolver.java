package com.dajeom.resolver;

import com.dajeom.model.PalacePositionAnalysis;
import com.dajeom.model.PalaceResolvedTenGodSource;

import java.util.List;

/**
 * 정밀화 12차-3.
 *
 * 궁위와 기존 십성/지장간 엔진 사이의 Boundary.
 *
 * =================================================
 *
 * 구현 책임:
 *
 * 1.
 * YEAR/MONTH/HOUR 천간의 십성 계산
 *
 * 2.
 * 각 지지의 지장간 조회
 *
 * 3.
 * 본기/중기/여기 구분
 *
 * 4.
 * 각 지장간을 일간 기준 십성으로 변환
 *
 * =================================================
 *
 * DAY 천간은 일간 자신이므로
 * 외부 십성 Source로 반환하지 않는다.
 *
 * =================================================
 *
 * 구현체에서는 기존:
 *
 * HiddenStemTable
 * StructureTenGodCalculator
 *
 * 를 연결한다.
 *
 * 해당 기존 API를 12차 때문에 수정하지 않는다.
 */
public interface PalaceTenGodSourceResolver {

    List<PalaceResolvedTenGodSource> resolve(
            PalacePositionAnalysis palacePositionAnalysis
    );
}