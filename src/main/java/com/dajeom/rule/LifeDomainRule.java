package com.dajeom.rule;

import com.dajeom.model.LifeDomainEvidenceBundle;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;

import java.util.List;
import java.util.Set;

/**
 * 정밀화 18차-1.
 *
 * 생활영역 deterministic Rule 공통 Interface.
 *
 * =================================================
 *
 * 실제 Rule 예:
 *
 * WealthCapacityRule
 * CareerOfficerStructureRule
 * RelationshipSpousePalaceRule
 * LearningResourceOutputRule
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 신살 단독 결과 생성
 * - LLM 호출
 * - 확률 생성
 * - 운세 점수 생성
 * - 기존 용신/격국 변경
 */
public interface LifeDomainRule {

    LifeDomain domain();

    String code();

    /**
     * 이 Rule이 실제로 읽을 수 있는 Source 종류.
     *
     * 선언되지 않은 Source를 Evidence로 사용하면
     * RuleEngine 검증에서 차단한다.
     */
    Set<LifeDomainEvidenceSourceType> supportedSources();

    List<LifeDomainRuleMatch> evaluate(
            LifeDomainEvidenceBundle bundle
    );
}