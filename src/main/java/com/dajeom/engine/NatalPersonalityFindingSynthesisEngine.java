package com.dajeom.engine;

import com.dajeom.model.NatalPersonalityFindingAnalysis;
import com.dajeom.model.NatalPersonalityFindingSynthesisAnalysis;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Personality Finding의 2차 임의 Synthesis를 생성하지 않는 호환 Engine.
 *
 * =================================================
 * 명리 기준화 원칙
 * =================================================
 *
 * 이미 각각 독립적으로 성립한 전통 명리 Pattern이 함께 존재한다는 이유만으로
 * 프로젝트가 새로운 상위 의미를 만들지 않는다.
 *
 * 예:
 *
 * - 상관견관 + 상관패인
 *   -> 새로운 복합 성격 의미 생성 금지
 *
 * - 재생관 + 관인상생
 *   -> 새로운 연쇄 성격 의미 생성 금지
 *
 * - 비겁쟁재 + 재다신약
 *   -> 새로운 복합 압박 성격 의미 생성 금지
 *
 * 각 Finding은 각각의 전통적 성립조건과 의미를 그대로 보존한다.
 *
 * 이 Engine 자체는 기존 Pipeline/API 호환을 위해 유지하지만
 * syntheses는 항상 빈 목록을 반환한다.
 */
@Component
public class NatalPersonalityFindingSynthesisEngine {

    public NatalPersonalityFindingSynthesisAnalysis analyze(

            NatalPersonalityFindingAnalysis findingAnalysis

    ) {

        Objects.requireNonNull(
                findingAnalysis,
                "Personality Finding Analysis는 필수입니다."
        );

        return new NatalPersonalityFindingSynthesisAnalysis(
                findingAnalysis,
                List.of(),
                findingAnalysis.completeChart()
        );
    }
}