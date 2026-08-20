package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.type.ConsultationCategory;

import java.util.List;

/**
 * 카테고리 deterministic 결과를
 * 다시 조합하는 2차 Synthesis Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * ConsultationCategoryDeterministicRule은:
 *
 * ConsultationCategoryEvidenceView
 * ->
 * ConsultationCategoryDeterministicMatch
 *
 * 를 만든다.
 *
 * =================================================
 *
 * 하지만 다음과 같은 상위 판단은:
 *
 * BIG_FLOW
 *
 * PRIMARY_ACTIVE_DOMAIN
 *
 * PRIMARY_PRESSURE_DOMAIN
 *
 * CROSS_DOMAIN_PATTERN
 *
 * 처럼 이미 계산된
 * Category Deterministic 결과끼리
 * 다시 비교해야 한다.
 *
 * 대표적인 예가:
 *
 * TURNING_POINT
 *
 * 이다.
 *
 * =================================================
 * 기존 Rule과의 차이
 * =================================================
 *
 * 기존:
 *
 * ConsultationCategoryDeterministicRule
 *
 * 입력:
 * ConsultationCategoryEvidenceView
 *
 * =================================================
 *
 * Synthesis:
 *
 * ConsultationCategoryDeterministicSynthesisRule
 *
 * 입력:
 *
 * 1.
 * ConsultationCategoryEvidenceView
 *
 * 2.
 * 현재 Phase보다 앞에서
 *    이미 검증 완료된
 *    ConsultationCategoryDeterministicMatch
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 Rule 역시:
 *
 * - OpenAI 호출 금지
 * - 새로운 원국 계산 금지
 * - 새로운 운 계산 금지
 * - 입력 View에 없는 LifeDomainRuleMatch 생성 금지
 * - 사건 예측 금지
 *
 * 를 그대로 따른다.
 *
 * =================================================
 * priorMatches
 * =================================================
 *
 * priorMatches는 같은
 * ConsultationCategoryEvidenceView에 대해
 * 이전 단계에서 이미 계산되고
 * Engine 검증까지 통과한 결과다.
 *
 * 따라서 TURNING_POINT 같은 Rule은:
 *
 * BIG_FLOW 결과
 * PRIMARY_ACTIVE_DOMAIN 결과
 * PRIMARY_PRESSURE_DOMAIN 결과
 * CROSS_DOMAIN_PATTERN 결과
 *
 * 를 다시 원시 LifeDomain Match에서
 * 재계산하지 않고 직접 사용할 수 있다.
 *
 * =================================================
 * Phase
 * =================================================
 *
 * Synthesis Rule끼리도 의존성이 생길 수 있으므로
 * phase를 명시한다.
 *
 * 낮은 Phase가 먼저 실행된다.
 *
 * 같은 Phase의 Rule들은
 * 서로의 결과를 볼 수 없다.
 *
 * 즉 같은 Phase에서는
 * 실행 순서에 따른 암묵적 의존을 만들 수 없다.
 *
 * =================================================
 * 예
 * =================================================
 *
 * phase 100:
 *
 * TURNING_POINT
 *
 * =================================================
 *
 * phase 200:
 *
 * OVERALL_NARRATIVE
 *
 * =================================================
 *
 * 이렇게 하면 OVERALL_NARRATIVE는
 * TURNING_POINT까지 포함한
 * 이전 단계 결과를 사용할 수 있다.
 */
public interface ConsultationCategoryDeterministicSynthesisRule {

    /**
     * 이 Synthesis Rule이 계산하는
     * 상담 카테고리.
     */
    ConsultationCategory category();


    /**
     * Rule 고유 Code.
     *
     * 같은 ConsultationCategory 안에서는
     * 일반 Deterministic Rule과
     * Synthesis Rule 전체를 통틀어
     * 중복될 수 없다.
     */
    String code();


    /**
     * Synthesis 실행 단계.
     *
     * 낮은 Phase가 먼저 실행된다.
     *
     * 같은 Phase의 Rule끼리는
     * 서로의 결과를 볼 수 없다.
     */
    default int phase() {

        return 100;
    }


    /**
     * 이미 계산된 deterministic 결과를
     * 추가로 합성한다.
     *
     * priorMatches에는:
     *
     * - 기존 일반 Deterministic Rule 결과
     * - 더 낮은 Phase의 Synthesis 결과
     *
     * 만 존재한다.
     *
     * 같은 Phase에서 새로 생성되는 결과는
     * 포함되지 않는다.
     */
    List<ConsultationCategoryDeterministicMatch> evaluate(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch>
                    priorMatches

    );
}