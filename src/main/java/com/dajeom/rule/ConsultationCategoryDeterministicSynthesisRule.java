package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.type.ConsultationCategory;

import java.util.List;

/**
 * 이미 계산·검증된 Category deterministic 결과를 입력으로 사용하는
 * 명시적 상위 deterministic 계산 Rule.
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
 * 를 계산한다.
 *
 * =================================================
 * Synthesis Rule이 필요한 경우
 * =================================================
 *
 * 일부 상위 판단은
 * 원시 LifeDomain Evidence만으로 직접 계산하지 않고,
 *
 * 이미 계산된:
 *
 * BIG_FLOW
 *
 * PRIMARY_ACTIVE_DOMAIN
 *
 * PRIMARY_PRESSURE_DOMAIN
 *
 * CROSS_DOMAIN_PATTERN
 *
 * 등의 Category deterministic 결과를
 * 명시적인 Java 계산 입력으로 사용해야 한다.
 *
 * 대표적인 예:
 *
 * TURNING_POINT
 *
 * OVERALL_NARRATIVE
 *
 * =================================================
 * 일반 Rule과의 차이
 * =================================================
 *
 * 일반 Rule:
 *
 * ConsultationCategoryDeterministicRule
 *
 * 입력:
 *
 * ConsultationCategoryEvidenceView
 *
 * -------------------------------------------------
 *
 * 상위 계산 Rule:
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
 * 이미 계산되고 Engine 검증까지 완료된
 * ConsultationCategoryDeterministicMatch
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 인터페이스가
 * 서로 다른 독립 판단을 임의로 관계화하거나
 * 하나의 의미로 묶는 것을 허용하는 것은 아니다.
 *
 * 관계,
 * 인과,
 * 순서,
 * 강화·약화,
 * Cross-Domain 의미,
 * 전환,
 * 종합 판단이 필요하다면
 *
 * 반드시 해당 Synthesis Rule 구현체가
 * 그 계산 조건을 Java에서 명시적으로 확정해야 한다.
 *
 * 단순히 여러 priorMatch가 함께 존재한다는 이유만으로
 * 새로운 관계나 종합 의미를 만들 수 없다.
 *
 * 또한 다음 원칙을 따른다.
 *
 * - OpenAI 호출 금지
 * - 새로운 원국 계산 금지
 * - 새로운 운 계산 금지
 * - 입력 View에 없는 LifeDomainRuleMatch 생성 금지
 * - priorMatches에 없는 dependency 주장 금지
 * - 사건 예측 금지
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
 * 등을 필요한 경우
 * 명시적인 dependency로 참조할 수 있다.
 *
 * 이때 원시 LifeDomain Match를 다시 세거나
 * priorMatches에 없는 상위 판단을
 * 임의로 만들어서는 안 된다.
 *
 * =================================================
 * Phase
 * =================================================
 *
 * Synthesis Rule 사이에
 * 명시적인 계산 단계 의존성이 필요한 경우
 * phase를 사용한다.
 *
 * 낮은 Phase가 먼저 실행된다.
 *
 * 같은 Phase의 Rule들은
 * 서로의 결과를 볼 수 없다.
 *
 * 따라서 같은 Phase에서는
 * 실행 순서에 따른 암묵적인 dependency를
 * 만들 수 없다.
 *
 * =================================================
 * 예
 * =================================================
 *
 * phase 100:
 *
 * TURNING_POINT
 *
 * -------------------------------------------------
 *
 * phase 200:
 *
 * OVERALL_NARRATIVE
 *
 * -------------------------------------------------
 *
 * 이 경우 OVERALL_NARRATIVE Rule은
 * Engine이 제공한 이전 Phase snapshot에
 * TURNING_POINT가 실제로 존재하는 경우에만
 * 해당 결과를 dependency로 사용할 수 있다.
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
     * 이미 계산·검증된 deterministic 결과를 입력으로
     * 명시적인 상위 deterministic 판단을 계산한다.
     *
     * priorMatches에는:
     *
     * - 일반 Deterministic Rule 결과
     * - 더 낮은 Phase의 Synthesis 결과
     *
     * 만 존재한다.
     *
     * 같은 Phase에서 새로 생성되는 결과는
     * 포함되지 않는다.
     *
     * priorMatches가 함께 존재한다는 이유만으로
     * 새로운 관계·인과·순서·종합 의미를 만들 수 없으며,
     * 필요한 의미는 구현 Rule이 Java 계산으로
     * 명시적으로 확정해야 한다.
     */
    List<ConsultationCategoryDeterministicMatch> evaluate(

            ConsultationCategoryEvidenceView view,

            List<ConsultationCategoryDeterministicMatch>
                    priorMatches

    );
}