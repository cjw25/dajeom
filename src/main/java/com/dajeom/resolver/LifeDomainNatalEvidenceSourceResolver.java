package com.dajeom.resolver;

import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LuckIntegratedAnalysis;

import java.util.List;

/**
 * 정밀화 18차-2.
 *
 * 3~11차의 원국 기반 분석 결과를
 * 생활영역 Evidence Reference로 연결하는 Boundary.
 *
 * =================================================
 *
 * 이 Resolver가 담당하는 Source:
 *
 * ELEMENT_STRENGTH
 * DAY_MASTER_STRENGTH
 * STRUCTURE
 * CLIMATE_DISEASE_FLOW
 * USEFUL_GOD
 * TEN_GOD_COMPOSITE
 * TWELVE_LIFE_STAGE
 * NATAL_RELATION
 * TRANSFORMATION
 *
 * =================================================
 *
 * 담당하지 않는 Source:
 *
 * PALACE
 *
 * -> 18-2 Resolver가 현재 PalaceAnalysis 객체를
 *    직접 연결한다.
 *
 * DECADE_CONTEXT
 * ANNUAL_CONTEXT
 * MONTHLY_CONTEXT
 *
 * -> 운 계층에서 처리한다.
 *
 * SHINSAL
 *
 * -> ShinsalAnalysis에서 직접 연결한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Resolver는 3~11차 분석을 다시 계산하면 안 된다.
 *
 * 이미 생성된 upstream 분석 객체를
 * LifeDomainEvidenceReference의 source에
 * 그대로 넣어야 한다.
 */
public interface LifeDomainNatalEvidenceSourceResolver {

    List<LifeDomainEvidenceReference<?>>
    resolveNatalEvidences(
            LuckIntegratedAnalysis luckIntegratedAnalysis
    );
}