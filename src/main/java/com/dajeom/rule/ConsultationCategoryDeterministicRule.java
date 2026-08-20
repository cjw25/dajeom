package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.type.ConsultationCategory;

import java.util.List;

/**
 * 상담 카테고리별 deterministic 계산 Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * 기존 18차 LifeDomainRuleMatch를
 * 단순히 상담 카테고리에 복사하지 않고,
 *
 * 실제 상담 카테고리의 의미에 맞게
 * deterministic하게 다시 판단한다.
 *
 * =================================================
 *
 * 예
 * =================================================
 *
 * 같은 RELATIONSHIP Evidence를 사용하더라도:
 *
 * SocialRelationshipRule
 *
 * ->
 * 사람을 받아들이는 방식
 * 거리 조절
 * 기대와 책임에 대한 반응
 *
 * LoveStyleRule
 *
 * ->
 * 감정 표현
 * 호감 확인
 * 친밀감이 높아질 때의 반응
 *
 * PartnerLuckRule
 *
 * ->
 * 장기 관계
 * 역할 분배
 * 책임의 누적
 * 관계 안정 기준
 *
 * 처럼 서로 다른 Rule을 사용해야 한다.
 *
 * =================================================
 *
 * 금지
 * =================================================
 *
 * - OpenAI 호출
 * - 새로운 원국 계산
 * - 입력 View에 없는 LifeDomainRuleMatch 생성
 * - 사건 예측
 * - 문자열 키워드만 보고 사주 사실 생성
 */
public interface ConsultationCategoryDeterministicRule {

    /**
     * 이 Rule이 계산하는 상담 카테고리.
     */
    ConsultationCategory category();


    /**
     * Rule 고유 Code.
     *
     * 동일 Category 안에서는
     * 중복될 수 없다.
     */
    String code();


    /**
     * 현재 Category Evidence View를
     * deterministic하게 평가한다.
     *
     * 결과가 없으면 빈 List를 반환한다.
     *
     * null 반환은 허용하지 않는다.
     */
    List<ConsultationCategoryDeterministicMatch> evaluate(
            ConsultationCategoryEvidenceView view
    );
}