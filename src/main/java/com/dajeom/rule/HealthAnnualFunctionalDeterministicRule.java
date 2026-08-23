package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * HEALTH_TENDENCY 현재 세운 Functional deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.HEALTH_LIFESTYLE에서 이미 계산된:
 *
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * 결과를 ConsultationCategory.HEALTH_TENDENCY의
 * 독립적인 CURRENT_TIMELINE 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * HealthLifestyleAnnualFunctionalRule은
 * ANNUAL_FUNCTIONAL_CONTEXT를 사용하여:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * 다섯 Function 전체의:
 *
 * - Annual 활성
 * - 원국 고정 Capacity 방향
 * - 현재 대운 결합 후 Resolved 활성
 * - sameDirectionAccumulationContact
 * - counterDirectionContact
 * - CrossFunctional Pattern
 * - Transformation
 * - Original Function Reduction
 * - qualification
 * - mixed Context
 *
 * 를 실제 Java에서 계산한다.
 *
 * 이 Category Rule에서는
 * 해당 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * Upstream 공식 상태
 * =================================================
 *
 * HealthLifestyleAnnualFunctionalRule은
 * 하나의 Annual Functional Context 안에서
 * 실제 조건을 확인한 뒤:
 *
 * CONTEXTUAL
 * PRESSURE
 * MIXED
 *
 * 등을 계산한다.
 *
 * 현재 upstream Rule 단독으로
 * SUPPORTIVE는 생성하지 않는다.
 *
 * 다만 Category Rule에서는
 * upstream Status를 재판정하지 않으므로
 * 향후 공식 upstream 결과가 SUPPORTIVE라면
 * 그대로 보존할 수 있도록 1:1 변환한다.
 *
 * =================================================
 * 실제 Java 계산 관계 보존
 * =================================================
 *
 * 다음 typed Pattern이
 * upstream Java에서 실제로 계산되고
 * effective contact로 확정된 경우
 * 해당 계산은 그대로 유효하다.
 *
 * 부담 Pattern:
 *
 * RESOURCE_OVERLOADS_CORE_SELF
 * SELF_ASSERTION_COMPETES_FOR_RESOURCE
 * EXPRESSION_CONFLICTS_RESPONSIBILITY
 * PROCESSING_RESTRAINS_OUTPUT
 *
 * 조절 Pattern:
 *
 * PROCESSING_REGULATES_EXPRESSION
 * EXPRESSION_CONTROLS_PRESSURE
 *
 * 내부 혼재:
 *
 * RESPONSIBILITY_INTERNAL_MIXTURE
 *
 * =================================================
 * 중요한 경계
 * =================================================
 *
 * 위 Pattern은:
 *
 * Category가 여러 Evidence를 보고
 * 새로 만든 관계가 아니다.
 *
 * HealthLifestyleAnnualFunctionalRule이
 * AnnualFunctionalLifeDomainContext 안에서
 * 실제 typed CrossFunctional 계산 결과를
 * 사용한 것이다.
 *
 * 따라서 보존한다.
 *
 * =================================================
 * Category에서 관계 생성 금지
 * =================================================
 *
 * 이 Rule에서는 별도로:
 *
 * HEALTH_ANNUAL_CONTEXT
 * HEALTH_DECADE_CONTEXT
 * 원국 HEALTH Match
 *
 * 등을 가져와 새로운 관계를 만들지 않는다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * HEALTH_CURRENT_ANNUAL_FUNCTIONAL
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 보존한다.
 *
 * =================================================
 * Function 재분해 금지
 * =================================================
 *
 * upstream HealthLifestyleAnnualFunctionalRule은
 * 다섯 Function을 하나의 공식
 * HEALTH_LIFESTYLE Match로 계산한다.
 *
 * 따라서 Category에서는 이를 다시:
 *
 * SELF_ASSERTION Match
 * EXPRESSION Match
 * RESOURCE_REALITY Match
 * RESPONSIBILITY Match
 * PROCESSING Match
 *
 * 로 분해하지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 원본 LifeDomainSignalStatus를
 * Category Status로 그대로 1:1 변환한다.
 *
 * SUPPORTIVE
 * -> SUPPORTIVE
 *
 * PRESSURE
 * -> PRESSURE
 *
 * MIXED
 * -> MIXED
 *
 * CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 * HEALTH_ANNUAL_CONTEXT와 분리
 * =================================================
 *
 * HEALTH_ANNUAL_CONTEXT
 *
 * ->
 *
 * HealthAnnualContextDeterministicRule
 *
 * -------------------------------------------------
 *
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 둘은 서로 다른 Annual 계산이다.
 *
 * =================================================
 * Status 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * HEALTH_ANNUAL_CONTEXT SUPPORTIVE
 * +
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL PRESSURE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * HEALTH_ANNUAL_CONTEXT PRESSURE
 * +
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL PRESSURE
 * =
 * 더 강한 PRESSURE
 *
 * 금지.
 *
 * =================================================
 * Annual provenance 경계
 * =================================================
 *
 * HEALTH_ANNUAL_CONTEXT와
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL은
 * 같은 명리연도의 Annual 계층에서
 * 파생될 수 있다.
 *
 * 따라서 이후 Grounding / Prominence에서:
 *
 * - 독립 검증 2회
 * - 근거 강도 2배
 *
 * 로 단순 계산해서는 안 된다.
 *
 * 각 계산 결과는 따로 보존하되
 * temporal provenance family를 고려해야 한다.
 *
 * =================================================
 * HEALTH_DECADE_CONTEXT와 분리
 * =================================================
 *
 * HEALTH_DECADE_CONTEXT
 *
 * ->
 *
 * HealthDecadeContextDeterministicRule
 *
 * 에서 별도로 처리한다.
 *
 * =================================================
 * 대운 결합 Functional 경계
 * =================================================
 *
 * HealthLifestyleAnnualFunctionalRule 내부에서
 * 현재 Timeline의 정확한 대운 결합 후:
 *
 * Resolved Function
 *
 * 을 사용하는 것은
 * upstream 공식 Java 계산이다.
 *
 * 이 계산 자체는 그대로 인정한다.
 *
 * 하지만 Category 단계에서 다시:
 *
 * HEALTH_DECADE_CONTEXT
 * +
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * 을 결합하지 않는다.
 *
 * =================================================
 * Capacity 계산 보존
 * =================================================
 *
 * upstream에서 실제 계산한:
 *
 * sameDirectionAccumulationContact()
 *
 * counterDirectionContact()
 *
 * qualificationRequired()
 *
 * 는 그대로 보존한다.
 *
 * Category에서는 이를 다시 계산하지 않는다.
 *
 * =================================================
 * Capacity 방향의 의미
 * =================================================
 *
 * sameDirectionAccumulationContact
 *
 * 은 원국 Strength와 같은 방향의
 * Functional 누적을 의미한다.
 *
 * 이것은:
 *
 * 질병
 * 발병
 * 신체 이상
 *
 * 을 뜻하지 않는다.
 *
 * =================================================
 * Burden Pattern 경계
 * =================================================
 *
 * upstream에서 실제 effective contact로
 * 계산된 부담 Pattern은
 * PRESSURE 근거가 될 수 있다.
 *
 * 그러나:
 *
 * RESOURCE_OVERLOADS_CORE_SELF
 * != 특정 질환
 *
 * SELF_ASSERTION_COMPETES_FOR_RESOURCE
 * != 체력 저하
 *
 * EXPRESSION_CONFLICTS_RESPONSIBILITY
 * != 스트레스성 질환
 *
 * PROCESSING_RESTRAINS_OUTPUT
 * != 특정 증상
 *
 * 으로 변환하지 않는다.
 *
 * =================================================
 * Regulation Pattern 경계
 * =================================================
 *
 * PROCESSING_REGULATES_EXPRESSION
 * EXPRESSION_CONTROLS_PRESSURE
 *
 * 같은 조절 Pattern은
 * upstream Java가 실제 계산한 경우
 * 그대로 인정한다.
 *
 * 하지만:
 *
 * 조절 Pattern 존재
 * != 건강 회복
 * != SUPPORTIVE
 *
 * 이다.
 *
 * upstream Rule도
 * 이를 단독 SUPPORTIVE로 만들지 않는다.
 *
 * =================================================
 * RESPONSIBILITY_INTERNAL_MIXTURE
 * =================================================
 *
 * upstream Java가
 * RESPONSIBILITY_INTERNAL_MIXTURE를
 * effective contact로 계산한 경우:
 *
 * HEALTH_LIFESTYLE Status = MIXED
 *
 * 로 보존할 수 있다.
 *
 * 이 MIXED는 Category에서
 * 새로 만든 것이 아니다.
 *
 * =================================================
 * mixedContext 경계
 * =================================================
 *
 * currentMixedContext 또는
 * effective CrossFunctional Synthesis 자체의
 * mixed Context 역시
 * upstream이 계산한 공식 상태다.
 *
 * 이 Rule에서는 다시 계산하지 않는다.
 *
 * =================================================
 * 불완전 원국 경계
 * =================================================
 *
 * upstream에서:
 *
 * completeChart == false
 *
 * 이면 강한 HEALTH 방향을
 * 확정하지 않고 CONTEXTUAL로 제한한다.
 *
 * Category에서도 이를 그대로 보존한다.
 *
 * =================================================
 * Timing 불확실성
 * =================================================
 *
 * currentFunctionalSegmentTimingUncertain()
 *
 * 이면 upstream은
 * 강한 방향을 확정하지 않는다.
 *
 * Category에서 이 제한을 제거하지 않는다.
 *
 * =================================================
 * Annual-only 경계
 * =================================================
 *
 * 첫 대운 시작 전 또는
 * Functional Segment가 unavailable인 경우에도
 * HealthLifestyleAnnualFunctionalRule은
 * Annual-only Capacity를 기준으로
 * 공식 Status를 계산할 수 있다.
 *
 * Category에서는 실제 생성된 Match를
 * 그대로 보존한다.
 *
 * 다른 대운 Match를 대신 넣지 않는다.
 *
 * =================================================
 * Monthly Functional과 분리
 * =================================================
 *
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * 은 다음 단계의 별도 Rule이 담당한다.
 *
 * 이 Rule에서는 월운 Functional을
 * 조회하거나 합성하지 않는다.
 *
 * =================================================
 * 원국 HEALTH와 분리
 * =================================================
 *
 * 다음 Category 결과를
 * 이 Rule의 Status 계산에 사용하지 않는다.
 *
 * HEALTH_BASELINE_BALANCE_CONTEXT
 * HEALTH_CLIMATE_DISEASE_FLOW_CONTEXT
 * HEALTH_ADJUSTMENT_DIRECTION_CONTEXT
 *
 * =================================================
 * 조후·병약·통관 재계산 금지
 * =================================================
 *
 * Annual Functional Match 안에
 * 별도의:
 *
 * CLIMATE_DISEASE_FLOW
 *
 * Source가 존재하는 것이 아니다.
 *
 * 따라서 Category에서 Annual Functional을 보고:
 *
 * - 조후 상태
 * - 명리적 병약
 * - 통관 필요
 *
 * 를 새로 생성하지 않는다.
 *
 * =================================================
 * 의료적 의미 금지
 * =================================================
 *
 * PRESSURE
 * != 질병
 * != 발병
 * != 악화
 *
 * MIXED
 * != 건강 불안정
 * != 만성질환
 *
 * CONTEXTUAL
 * != 건강함
 * != 질병 없음
 *
 * SUPPORTIVE
 * != 회복
 * != 치료 효과
 *
 * =================================================
 * Function-장기 대응 금지
 * =================================================
 *
 * SELF_ASSERTION
 * != 특정 장기
 *
 * EXPRESSION
 * != 특정 장기
 *
 * RESOURCE_REALITY
 * != 특정 장기
 *
 * RESPONSIBILITY
 * != 특정 장기
 *
 * PROCESSING
 * != 특정 장기
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * PRESSURE
 * != 병원 방문
 * != 입원
 * != 수술
 * != 사고
 *
 * MIXED
 * != 증상 반복
 *
 * SUPPORTIVE
 * != 회복
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Annual Functional Match 개수를:
 *
 * - 건강 위험도
 * - 생활 부담 점수
 * - 질병 가능성
 * - 체력 점수
 *
 * 로 사용하지 않는다.
 *
 * =================================================
 * 관계·인과·순서 생성 금지
 * =================================================
 *
 * Category 단계에서는 다음을
 * 새로 생성하지 않는다.
 *
 * - 책임 기능 때문에 건강 부담이 생긴다
 * - 표현 기능 때문에 생활 리듬이 무너진다
 * - 처리 기능이 부담을 회복시킨다
 * - 대운 때문에 세운 Functional이 악화된다
 * - 원국 건강 구조 때문에 Functional Pressure가 생긴다
 * - Annual Context 때문에 Functional 상태가 바뀐다
 * - 먼저 부담이 생기고 이후 건강 문제가 나타난다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * 실제 Java 관계와의 구분
 * =================================================
 *
 * upstream AnnualFunctionalLifeDomainContext 및
 * AnnualCrossFunctionalSynthesis에서
 * 실제로 계산된 typed 관계는
 * 그대로 유효하다.
 *
 * 금지하는 것은
 * 독립적인 Category Evidence가
 * 함께 존재한다는 이유만으로
 * 관계를 새로 만드는 것이다.
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * LifeDomainRuleMatch의:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱하여:
 *
 * - Function 종류
 * - Capacity
 * - Pattern
 * - qualification
 * - Transformation
 *
 * 을 재판정하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - AnnualFunctionalLifeDomainContext 재계산
 * - Annual Function Capacity 재계산
 * - Resolved Function 재계산
 * - CrossFunctional Pattern 재계산
 * - Transformation 재계산
 * - Original Function Reduction 재계산
 * - qualification 재계산
 * - currentMixedContext 재계산
 * - 원국 Strength 재계산
 * - HEALTH_ANNUAL_CONTEXT와 Status 합성
 * - HEALTH_DECADE_CONTEXT와 Status 합성
 * - HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL과 Status 합성
 * - 원국 HEALTH Match와 Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - Function-장기 매핑
 * - 특정 질환 생성
 * - 발병 예측
 * - 회복 예측
 * - 입원·수술 예측
 * - 수명 예측
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class HealthAnnualFunctionalDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "HEALTH_CURRENT_ANNUAL_FUNCTIONAL";

    private static final String
            HEALTH_ANNUAL_FUNCTIONAL_RULE_CODE =
            "HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.HEALTH_TENDENCY;
    }


    @Override
    public String code() {

        return RULE_CODE;
    }


    @Override
    public List<ConsultationCategoryDeterministicMatch>
    evaluate(
            ConsultationCategoryEvidenceView view
    ) {

        Objects.requireNonNull(
                view,
                "HealthAnnualFunctionalDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.HEALTH_TENDENCY
        ) {

            throw new IllegalArgumentException(
                    "HealthAnnualFunctionalDeterministicRule은 HEALTH_TENDENCY View만 평가할 수 있습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Current Timeline
         * =================================================
         */

        if (
                !view.hasSegmentEvidences()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        /*
         * =================================================
         * Segment별 독립 처리
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segment,
                            "HEALTH Annual Functional Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 공식 Source
             * =================================================
             */

            List<LifeDomainRuleMatch> functionalMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            HEALTH_ANNUAL_FUNCTIONAL_RULE_CODE
                    );

            if (
                    functionalMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * LifeDomain Match 1개
             * ->
             * Category Match 1개
             * =================================================
             */

            for (
                    LifeDomainRuleMatch functionalMatch
                    : functionalMatches
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                functionalMatch,
                                "HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL Match는 null일 수 없습니다."
                        );

                ConsultationCategoryDeterministicStatus status =
                        toCategoryStatus(
                                normalizedMatch.status()
                        );

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.HEALTH_TENDENCY,

                                ConsultationCategoryDeterministicLayer
                                        .CURRENT_TIMELINE,

                                RULE_CODE,

                                status,

                                normalizedSegment.sajuYear(),

                                normalizedSegment.monthIndex(),

                                List.of(),

                                summaryOf(
                                        status
                                ),

                                List.of(
                                        normalizedMatch
                                ),

                                buildReasons(
                                        normalizedSegment,
                                        normalizedMatch
                                )
                        )
                );
            }
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Segment Source 조회
     * =================================================
     *
     * 정확히:
     *
     * domain =
     * HEALTH_LIFESTYLE
     *
     * ruleCode =
     * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
     *
     * 만 사용한다.
     */

    private List<LifeDomainRuleMatch>
    findSegmentMatches(

            ConsultationCategorySegmentEvidence segment,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                segment,
                "ConsultationCategorySegmentEvidence가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "HEALTH Annual Functional sourceRuleCode가 필요합니다."
                );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : segment.matches()
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "HEALTH Segment Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.HEALTH_LIFESTYLE
            ) {

                continue;
            }

            if (
                    !normalizedRuleCode.equals(
                            normalized.ruleCode()
                    )
            ) {

                continue;
            }

            result.add(
                    normalized
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Status 1 : 1 변환
     * =================================================
     *
     * HealthLifestyleAnnualFunctionalRule에서
     * 이미 확정된 공식 Status만 사용한다.
     *
     * 다른 HEALTH Evidence의 상태는
     * 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "HEALTH Annual Functional에는 LifeDomainSignalStatus가 필요합니다."
        );

        return switch (
                sourceStatus
                ) {

            case SUPPORTIVE ->
                    ConsultationCategoryDeterministicStatus
                            .SUPPORTIVE;

            case PRESSURE ->
                    ConsultationCategoryDeterministicStatus
                            .PRESSURE;

            case MIXED ->
                    ConsultationCategoryDeterministicStatus
                            .MIXED;

            case CONTEXTUAL ->
                    ConsultationCategoryDeterministicStatus
                            .CONTEXTUAL;
        };
    }


    /*
     * =================================================
     * Reasons
     * =================================================
     *
     * 최종 사용자 의료 해석이 아니라
     * deterministic provenance 기록이다.
     */

    private List<String> buildReasons(

            ConsultationCategorySegmentEvidence segment,

            LifeDomainRuleMatch sourceMatch

    ) {

        Objects.requireNonNull(
                segment,
                "HEALTH Annual Functional Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "HEALTH Annual Functional Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL에서 이미 계산된 하나의 HEALTH_LIFESTYLE LifeDomain Match를 HEALTH_TENDENCY의 독립 Annual Functional 결과로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 HEALTH Source와 결합하지 않고 그대로 Category Status로 변환했습니다."
        );

        reasons.add(
                "HealthLifestyleAnnualFunctionalRule이 SELF_ASSERTION, EXPRESSION, RESOURCE_REALITY, RESPONSIBILITY, PROCESSING 다섯 Function의 Annual Capacity와 현재 Resolved Function을 이용해 이미 계산한 결과를 다시 계산하지 않았습니다."
        );

        reasons.add(
                "sameDirectionAccumulationContact, counterDirectionContact, qualificationRequired 및 currentMixedContext는 upstream Annual Functional Contract에서 이미 계산된 값을 그대로 사용합니다."
        );

        reasons.add(
                "RESOURCE_OVERLOADS_CORE_SELF, SELF_ASSERTION_COMPETES_FOR_RESOURCE, EXPRESSION_CONFLICTS_RESPONSIBILITY, PROCESSING_RESTRAINS_OUTPUT 같은 burden Pattern이 upstream Java에서 effective contact로 계산된 경우 해당 typed 관계 자체는 그대로 보존합니다."
        );

        reasons.add(
                "PROCESSING_REGULATES_EXPRESSION, EXPRESSION_CONTROLS_PRESSURE 같은 regulation Pattern 역시 upstream Java에서 실제 계산된 경우에만 인정하며 이 Category Rule에서 새로 생성하지 않습니다."
        );

        reasons.add(
                "RESPONSIBILITY_INTERNAL_MIXTURE로 upstream HealthLifestyleAnnualFunctionalRule 자체가 MIXED를 확정한 경우 해당 MIXED는 공식 Java 계산 결과이므로 그대로 보존합니다."
        );

        reasons.add(
                "HEALTH_ANNUAL_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았으며 Annual Context와 Annual Functional을 Category 단계에서 다시 합성하지 않았습니다."
        );

        reasons.add(
                "HEALTH_DECADE_CONTEXT를 별도 Source로 가져와 Annual Functional Status와 다시 결합하지 않았습니다."
        );

        reasons.add(
                "Annual Functional upstream 내부에서 현재 대운 결합 후 Resolved Function을 사용하는 것은 공식 Java 계산으로 인정하지만 별도의 HEALTH_DECADE_CONTEXT Match를 Category에서 다시 결합하지 않습니다."
        );

        reasons.add(
                "HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL과 원국 HEALTH Match도 이 Rule의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과의 SUPPORTIVE·PRESSURE·MIXED·CONTEXTUAL 상태를 실제 질병·발병·악화·회복·입원·수술 또는 특정 장기의 상태로 변환하지 않습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 Function, CrossFunctional Pattern, Capacity, qualification 또는 의료적 의미를 재구성하지 않았습니다."
        );

        reasons.add(
                "다른 HEALTH Evidence가 같은 Segment에 존재한다는 사실만으로 새로운 강화·약화·보완·인과·순서 또는 종합 Status를 생성하지 않았습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 HEALTH Annual Functional 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 좌표의 다른 가능한 Segment가 존재하면 각각의 HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL 결과를 별도로 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment의 대운이 unavailable 상태이더라도 upstream에서 Annual-only 방식으로 실제 HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL Match가 생성되었다면 해당 결과를 그대로 보존하며 다른 대운 Match를 대신 추가하지 않습니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 monthIndex 존재를 이유로 Annual Functional 결과를 Monthly Functional 결과로 재해석하지 않습니다."
        );

        return List.copyOf(
                reasons
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "HEALTH Annual Functional Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 명리연도의 HEALTH Functional 계산 결과가 SUPPORTIVE 상태로 보존되며 이를 의료적 건강 호조나 회복으로 확대하지 않습니다.";

            case PRESSURE ->
                    "현재 명리연도의 HEALTH Functional 계산 결과가 PRESSURE 상태로 보존되며 이를 질병·발병·악화 상태로 변환하지 않습니다.";

            case MIXED ->
                    "현재 명리연도의 HEALTH Functional 계산 자체에서 MIXED 상태가 확정되어 있으며 해당 upstream 결과를 그대로 보존합니다.";

            case CONTEXTUAL ->
                    "현재 명리연도의 HEALTH Functional 계산 결과가 CONTEXTUAL 상태로 남아 있으며 다른 HEALTH Evidence와 합성하지 않습니다.";
        };
    }


    /*
     * =================================================
     * Text
     * =================================================
     */

    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}