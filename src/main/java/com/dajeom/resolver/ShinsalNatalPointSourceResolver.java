package com.dajeom.resolver;

import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.ShinsalPoint;

import java.util.List;

/**
 * 정밀화 17차-4.
 *
 * PalaceAnalysis
 * ->
 * 원국 ShinsalPoint 목록
 *
 * 변환 Boundary.
 *
 * =================================================
 *
 * 구현체는 반드시 원국 Point만 반환한다.
 *
 * completeChart=true:
 *
 * YEAR STEM
 * YEAR BRANCH
 *
 * MONTH STEM
 * MONTH BRANCH
 *
 * DAY STEM
 * DAY BRANCH
 *
 * HOUR STEM
 * HOUR BRANCH
 *
 * 총 8개.
 *
 * =================================================
 *
 * completeChart=false:
 *
 * YEAR STEM
 * YEAR BRANCH
 *
 * MONTH STEM
 * MONTH BRANCH
 *
 * DAY STEM
 * DAY BRANCH
 *
 * 총 6개.
 *
 * HOUR Point를 임의 생성하면 안 된다.
 *
 * =================================================
 *
 * 금지:
 *
 * - 출생시간 미상인데 HOUR 생성
 * - 정오 대체
 * - 자정 대체
 * - 子시 대체
 * - 운 Point 포함
 *
 * =================================================
 *
 * 이 Boundary가 필요한 이유:
 *
 * 12차 PalaceAnalysis의 실제 내부 API와
 * 17차 ShinsalRule을 직접 결합하지 않기 위함이다.
 */
public interface ShinsalNatalPointSourceResolver {

    List<ShinsalPoint> resolveNatalPoints(
            PalaceAnalysis natalAnalysis
    );
}