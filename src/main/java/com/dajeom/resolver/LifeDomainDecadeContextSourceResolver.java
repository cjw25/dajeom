package com.dajeom.resolver;

import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LuckIntegratedSegmentAnalysis;

import java.util.Optional;

/**
 * 정밀화 18차-2.
 *
 * 현재 Timeline Segment의
 * 13차 대운 Context 분석 객체를
 * 생활영역 Evidence로 연결하는 Boundary.
 *
 * =================================================
 *
 * 반환 SourceType:
 *
 * DECADE_CONTEXT
 *
 * =================================================
 *
 * ACTIVE가 아닌 경우:
 *
 * BEFORE_FIRST
 * AFTER_GENERATED_RANGE
 * UNAVAILABLE
 *
 * -> Optional.empty()
 *
 * =================================================
 *
 * ACTIVE이지만 기존 대운 Context를
 * 사용할 수 없는 경우에도
 * Optional.empty()를 허용한다.
 *
 * 없는 대운 Context를 임의 생성하지 않는다.
 */
public interface LifeDomainDecadeContextSourceResolver {

    Optional<LifeDomainEvidenceReference<?>>
    resolveDecadeContext(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    );
}