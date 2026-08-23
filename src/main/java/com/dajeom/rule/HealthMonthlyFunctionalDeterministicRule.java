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
 * HEALTH_TENDENCY 현재 월운 Functional deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.HEALTH_LIFESTYLE에서 이미 계산된:
 *
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * 결과를 ConsultationCategory.HEALTH_TENDENCY의
 * 독립적인 CURRENT_TIMELINE 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * HealthLifestyleMonthlyFunctionalRule은
 * MONTHLY_FUNCTIONAL_CONTEXT를 사용하여:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * 다섯 Function의 현재 월운 접촉을
 * 실제 Java에서 계산한다.
 *
 * =================================================
 * Upstream이 실제 계산하는 것
 * =================================================
 *
 * 각 Function에 대해:
 *
 * - monthlyContactExists
 * - annualBaselineActive
 * - activationStatus
 * - mixedMonthlyContact
 * - sameDirectionAccumulation
 * - counterDirectionContact
 * - qualificationRequired
 *
 * 등을 계산한다.
 *
 * 이 Category Rule에서는
 * 해당 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * Annual Baseline
 * =================================================
 *
 * Monthly Functional은
 * 고정된 Annual Functional Baseline을 기준으로
 * 월운의 실제 접촉을 평가한다.
 *
 * Annual Baseline에서 비활성인 Function에
 * 월운이 접촉했다고 해서
 * 해당 Annual Function을 활성 상태로
 * 승격하지 않는다.
 *
 * 이 upstream Contract를 그대로 보존한다.
 *
 * =================================================
 * Upstream Status 계산
 * =================================================
 *
 * completeChart가 아니면:
 *
 * CONTEXTUAL
 *
 * -------------------------------------------------
 *
 * 현재 Activation Segment Timing이
 * 불확실하면:
 *
 * CONTEXTUAL
 *
 * -------------------------------------------------
 *
 * 실제 활성 Annual Baseline Function이
 * 하나도 없으면:
 *
 * CONTEXTUAL
 *
 * -------------------------------------------------
 *
 * 같은 Function 내부에서
 * mixedMonthlyContact가 존재하면:
 *
 * MIXED
 *
 * -------------------------------------------------
 *
 * 원국 Strength와 같은 방향으로
 * 누적되는 활성 Function을
 * 월운이 SUPPORTED로 지지하고
 * qualification이 없으면:
 *
 * burdenReinforcement
 *
 * 후보가 된다.
 *
 * -------------------------------------------------
 *
 * burdenReinforcement
 * +
 * 반대 방향 Function의 유효 SUPPORTED 접촉
 *
 * 이 동시에 존재하면:
 *
 * MIXED
 *
 * -------------------------------------------------
 *
 * burdenReinforcement만 존재하면:
 *
 * PRESSURE
 *
 * -------------------------------------------------
 *
 * 그 외:
 *
 * CONTEXTUAL
 *
 * =================================================
 * SUPPORTIVE
 * =================================================
 *
 * 현재 upstream
 * HealthLifestyleMonthlyFunctionalRule은
 * 이 Rule 단독으로 SUPPORTIVE를 생성하지 않는다.
 *
 * 반대 방향 Function 접촉을:
 *
 * - 건강 호조
 * - 회복
 * - 개선
 *
 * 으로 자동 변환하지 않는다.
 *
 * Category에서도 같은 경계를 유지한다.
 *
 * =================================================
 * 중요한 Monthly CrossFunctional 경계
 * =================================================
 *
 * 현재 Monthly Functional Layer에는
 * 별도의 Monthly CrossFunctionalAnalysis가 없다.
 *
 * 따라서:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * 사이의:
 *
 * - 생성
 * - 충돌
 * - 제어
 * - 조절
 *
 * 관계를 월운 Functional 단계에서
 * 새로 만들지 않는다.
 *
 * 이 Category Rule 역시
 * 해당 관계를 생성하지 않는다.
 *
 * =================================================
 * sameDirectionAccumulation
 * =================================================
 *
 * sameDirectionAccumulation은
 * 각 Function에 대해:
 *
 * 원국 Strength 방향
 * +
 * Annual Functional Baseline
 * +
 * 현재 월운 접촉
 *
 * 을 upstream Java Contract 안에서
 * 실제 계산한 조건이다.
 *
 * 따라서 해당 결과 자체는 보존한다.
 *
 * 하지만 Category에서 다른 Match를 추가해
 * 새로운 누적 관계를 만들지는 않는다.
 *
 * =================================================
 * counterDirectionContact
 * =================================================
 *
 * counterDirectionContact 역시
 * 해당 Function의 Annual Functional Baseline과
 * 현재 월운 접촉을 대상으로
 * upstream Java가 실제 계산한다.
 *
 * 반대 방향 접촉이 존재한다고 해서:
 *
 * SUPPORTIVE
 * 회복
 * 건강 개선
 *
 * 으로 변환하지 않는다.
 *
 * =================================================
 * mixedMonthlyContact
 * =================================================
 *
 * 같은 Function 내부에서
 * 월운 접촉이 실제 혼재되어
 * upstream이 mixedMonthlyContact=true로
 * 계산한 경우:
 *
 * HEALTH_LIFESTYLE Status = MIXED
 *
 * 가 될 수 있다.
 *
 * 이 MIXED는 Category에서
 * 새로 만든 것이 아니다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * HealthLifestyleMonthlyFunctionalRule은
 * 다섯 Function 전체를 평가한 뒤
 * 하나의 공식:
 *
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * LifeDomainRuleMatch를 만든다.
 *
 * 따라서:
 *
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 보존한다.
 *
 * =================================================
 * Function별 재분해 금지
 * =================================================
 *
 * Category에서 하나의 upstream Match를:
 *
 * SELF_ASSERTION Match
 * EXPRESSION Match
 * RESOURCE_REALITY Match
 * RESPONSIBILITY Match
 * PROCESSING Match
 *
 * 로 다시 분해하지 않는다.
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
 * HEALTH Monthly Context
 * =================================================
 *
 * 현재 실제 소스에는:
 *
 * HEALTH_MONTHLY_CONTEXT
 *
 * Rule이 없다.
 *
 * 따라서 존재하지 않는
 * 일반 HEALTH Monthly Context를
 * 이 Category Rule에서 새로 만들지 않는다.
 *
 * 현재 HEALTH의 월운 계산은:
 *
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * 만 사용한다.
 *
 * =================================================
 * Annual Functional과 분리
 * =================================================
 *
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * ->
 *
 * HealthAnnualFunctionalDeterministicRule
 *
 * -------------------------------------------------
 *
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 둘은 서로 다른 시간층의 계산이다.
 *
 * =================================================
 * Annual + Monthly Status 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * HEALTH Annual Functional = PRESSURE
 * +
 * HEALTH Monthly Functional = CONTEXTUAL
 * =
 * PRESSURE
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * HEALTH Annual Functional = PRESSURE
 * +
 * HEALTH Monthly Functional = MIXED
 * =
 * 더 강한 PRESSURE
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * HEALTH Annual Functional = CONTEXTUAL
 * +
 * HEALTH Monthly Functional = PRESSURE
 * =
 * 새로운 종합 PRESSURE
 *
 * 금지.
 *
 * =================================================
 * Annual Baseline 참조와 Category 합성의 구분
 * =================================================
 *
 * Monthly Functional upstream이
 * Annual Functional Baseline을
 * 내부적으로 참조하는 것은
 * 공식 Java 계산이다.
 *
 * 이를 그대로 인정한다.
 *
 * 하지만 Category 단계에서 별도의:
 *
 * HEALTH_CURRENT_ANNUAL_FUNCTIONAL
 *
 * Match를 다시 가져와
 * Monthly Functional Match와
 * 결합하지 않는다.
 *
 * =================================================
 * HEALTH_ANNUAL_CONTEXT와 분리
 * =================================================
 *
 * HEALTH_ANNUAL_CONTEXT
 *
 * 역시 이 Rule의 Source로 사용하지 않는다.
 *
 * 따라서:
 *
 * HEALTH_ANNUAL_CONTEXT
 * +
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * 을 Category 단계에서
 * 다시 합성하지 않는다.
 *
 * =================================================
 * HEALTH_DECADE_CONTEXT와 분리
 * =================================================
 *
 * HEALTH_DECADE_CONTEXT도
 * 이 Rule에서 조회하지 않는다.
 *
 * Monthly Functional Effect 안에
 * 현재 대운 결합 후 Annual Resolved Effect가
 * 실제 upstream 계산으로 존재할 수 있는 것은
 * 그대로 인정한다.
 *
 * 하지만 Category에서 별도의 대운 Match를
 * 다시 붙이지 않는다.
 *
 * =================================================
 * 원국 HEALTH와 분리
 * =================================================
 *
 * 다음 원국 결과도
 * 이 Rule의 Status 계산에 사용하지 않는다.
 *
 * HEALTH_BASELINE_BALANCE_CONTEXT
 * HEALTH_CLIMATE_DISEASE_FLOW_CONTEXT
 * HEALTH_ADJUSTMENT_DIRECTION_CONTEXT
 *
 * =================================================
 * 조후·병약·통관 생성 금지
 * =================================================
 *
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL은
 * Functional 월운 계산이다.
 *
 * 따라서 이 결과를 보고:
 *
 * - 월운 조후
 * - 월운 병약
 * - 월운 통관
 *
 * 을 새로 생성하지 않는다.
 *
 * =================================================
 * Function과 의료 의미 분리
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
 * Status와 의료 의미 분리
 * =================================================
 *
 * PRESSURE
 * != 질병
 * != 발병
 * != 악화
 *
 * MIXED
 * != 건강 불안정
 * != 만성 질환
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
 * CONTEXTUAL
 * != 아무 일도 없음
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * contacted Function 개수는:
 *
 * - 건강 위험도
 * - 질병 확률
 * - 체력
 * - 생활 부담 점수
 *
 * 로 사용하지 않는다.
 *
 * =================================================
 * 관계·인과·순서 생성 금지
 * =================================================
 *
 * Category 단계에서 다음을
 * 새로 생성하지 않는다.
 *
 * - Annual Functional 때문에
 *   Monthly Functional이 악화된다
 *
 * - 책임 기능 때문에
 *   건강 부담이 생긴다
 *
 * - 표현 기능 때문에
 *   생활 리듬이 무너진다
 *
 * - 처리 기능이
 *   건강 상태를 회복시킨다
 *
 * - 대운 때문에
 *   월운 부담이 커진다
 *
 * - 원국 건강 구조 때문에
 *   월운 Pressure가 생긴다
 *
 * - 먼저 Annual 부담이 생기고
 *   이후 Monthly 문제가 나타난다
 *
 * - A 때문에 B
 *
 * - A가 B로 이어진다
 *
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * 실제 Java 계산과의 구분
 * =================================================
 *
 * MonthlyFunctionalLifeDomainContext와
 * MonthlyFunctionalActivationEffect가
 * 실제 Java에서 계산한:
 *
 * - Annual Baseline
 * - Resolved Annual Effect
 * - Monthly Contact
 * - ActivationStatus
 * - sameDirectionAccumulation
 * - counterDirectionContact
 * - qualification
 *
 * 자체는 그대로 유효하다.
 *
 * 금지하는 것은
 * 서로 독립적인 Category Match 존재만 보고
 * 새로운 관계를 만드는 것이다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각 Segment의 실제:
 *
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * Match를 각각 독립적으로 보존한다.
 *
 * 하나를 대표 월운으로 선택하지 않는다.
 *
 * =================================================
 * 좌표
 * =================================================
 *
 * 이 Match는 CURRENT_TIMELINE Layer이므로:
 *
 * - sajuYear
 * - monthIndex
 *
 * 를 현재 Segment 좌표 그대로 보존한다.
 *
 * monthIndex == 0도
 * 유효한 Timeline 좌표다.
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * Source Match의:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱하여:
 *
 * - Function 종류
 * - monthlyContact
 * - Annual Baseline
 * - ActivationStatus
 * - sameDirectionAccumulation
 * - counterDirectionContact
 * - qualification
 *
 * 을 다시 판정하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - MonthlyFunctionalLifeDomainContext 재계산
 * - Function Effect 재계산
 * - Annual Baseline 재계산
 * - Annual Resolved Effect 재계산
 * - Monthly Contact 재계산
 * - ActivationStatus 재계산
 * - sameDirectionAccumulation 재계산
 * - counterDirectionContact 재계산
 * - qualification 재계산
 * - Monthly CrossFunctional 관계 생성
 * - Function별 Category Match 재분해
 * - HEALTH Annual Functional과 Status 합성
 * - HEALTH Annual Context와 Status 합성
 * - HEALTH Decade Context와 Status 합성
 * - 원국 HEALTH Match와 Status 합성
 * - 존재하지 않는 HEALTH_MONTHLY_CONTEXT 생성
 * - Match 개수 점수화
 * - contacted Function 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - Function-장기 매핑
 * - 특정 질환 생성
 * - 발병 예측
 * - 악화 예측
 * - 회복 예측
 * - 입원·수술 예측
 * - 수명 예측
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class HealthMonthlyFunctionalDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "HEALTH_CURRENT_MONTHLY_FUNCTIONAL";

    private static final String
            HEALTH_MONTHLY_FUNCTIONAL_RULE_CODE =
            "HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL";


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
                "HealthMonthlyFunctionalDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "HealthMonthlyFunctionalDeterministicRule은 HEALTH_TENDENCY View만 평가할 수 있습니다."
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
         * Current Timeline 필요
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
                            "HEALTH Monthly Functional Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 공식 Source
             * =================================================
             */

            List<LifeDomainRuleMatch> functionalMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            HEALTH_MONTHLY_FUNCTIONAL_RULE_CODE
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
                                "HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL Match는 null일 수 없습니다."
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
     * LifeDomain.HEALTH_LIFESTYLE
     *
     * +
     *
     * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
     *
     * 만 사용한다.
     *
     * summary / reasons 문자열은
     * Source 판별에 사용하지 않는다.
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
                        "HEALTH Monthly Functional sourceRuleCode가 필요합니다."
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
     * HealthLifestyleMonthlyFunctionalRule이
     * 이미 확정한 공식 LifeDomainSignalStatus만
     * 그대로 사용한다.
     *
     * 다른 HEALTH Match의 상태는
     * 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "HEALTH Monthly Functional에는 LifeDomainSignalStatus가 필요합니다."
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
                "HEALTH Monthly Functional Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "HEALTH Monthly Functional Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL에서 이미 계산된 하나의 HEALTH_LIFESTYLE LifeDomain Match를 HEALTH_TENDENCY의 독립 Monthly Functional 결과로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 HEALTH Source와 결합하지 않고 그대로 Category Status로 변환했습니다."
        );

        reasons.add(
                "HealthLifestyleMonthlyFunctionalRule이 SELF_ASSERTION, EXPRESSION, RESOURCE_REALITY, RESPONSIBILITY, PROCESSING 다섯 Function의 실제 월운 접촉을 하나의 공식 Monthly Functional Match로 계산한 결과를 다시 분해하지 않았습니다."
        );

        reasons.add(
                "Annual Baseline 활성 여부, Monthly ActivationStatus, mixedMonthlyContact, sameDirectionAccumulation, counterDirectionContact 및 qualification은 upstream Monthly Functional Contract에서 이미 계산된 결과를 그대로 사용합니다."
        );

        reasons.add(
                "Annual Baseline에서 비활성인 Function에 월운이 접촉한 경우 해당 Annual Function을 활성 상태로 승격하지 않는 upstream 경계를 그대로 보존합니다."
        );

        reasons.add(
                "같은 Function 내부의 mixedMonthlyContact로 upstream Rule 자체가 MIXED를 계산한 경우 해당 MIXED는 공식 Java 계산 결과이므로 그대로 보존합니다."
        );

        reasons.add(
                "sameDirectionAccumulation 상태의 활성 Function을 월운이 유효하게 SUPPORTED하여 upstream Rule이 PRESSURE를 계산한 경우 해당 결과를 그대로 보존합니다."
        );

        reasons.add(
                "burdenReinforcement와 counterDirectionSupported가 upstream Rule 내부에서 실제 동시에 확인되어 MIXED가 계산된 경우 해당 결과를 그대로 보존합니다."
        );

        reasons.add(
                "현재 Monthly Functional Layer에는 별도 CrossFunctionalAnalysis가 없으므로 서로 다른 Function 간 생성·충돌·제어·조절 관계를 이 Category Rule에서 새로 생성하지 않았습니다."
        );

        reasons.add(
                "HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL을 이 Rule의 Source 또는 Status 계산에 사용하지 않았으며 Annual Functional과 Monthly Functional을 Category 단계에서 다시 합성하지 않았습니다."
        );

        reasons.add(
                "Monthly Functional upstream 내부에서 Annual Functional Baseline 또는 대운 결합 후 Annual Resolved Effect를 사용하는 것은 공식 Java 계산으로 인정하지만 별도의 Annual 또는 Decade Category Match를 다시 결합하지 않았습니다."
        );

        reasons.add(
                "HEALTH_ANNUAL_CONTEXT와 HEALTH_DECADE_CONTEXT를 이 Rule의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "현재 실제 소스에는 HEALTH_MONTHLY_CONTEXT가 없으므로 존재하지 않는 일반 HEALTH Monthly Context를 새로 생성하거나 Monthly Functional 결과로 대체하지 않았습니다."
        );

        reasons.add(
                "원국 HEALTH_BASELINE_BALANCE_CONTEXT, HEALTH_CLIMATE_DISEASE_FLOW_CONTEXT, HEALTH_ADJUSTMENT_DIRECTION_CONTEXT와도 이 Match를 합성하지 않았습니다."
        );

        reasons.add(
                "이 결과의 SUPPORTIVE·PRESSURE·MIXED·CONTEXTUAL 상태를 실제 질병·발병·악화·회복·입원·수술 또는 특정 장기의 상태로 변환하지 않습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 Function 종류, Annual Baseline, Monthly Contact, ActivationStatus, 누적 조건 또는 qualification을 재판정하지 않았습니다."
        );

        reasons.add(
                "다른 HEALTH Evidence가 같은 Segment에 존재한다는 사실만으로 새로운 강화·약화·보완·인과·순서 또는 종합 Status를 생성하지 않았습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 HEALTH Monthly Functional 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 존재하면 각각의 HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL 결과를 별도로 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment의 대운이 unavailable 상태이더라도 upstream에서 실제 HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL Match가 생성되었다면 해당 결과만 보존하며 별도의 대운 Match를 대신 추가하지 않습니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 실제 좌표이며 monthIndex == 0도 유효한 값으로 취급합니다."
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
                "HEALTH Monthly Functional Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 월운의 HEALTH Functional 계산 결과가 SUPPORTIVE 상태로 보존되며 이를 의료적 건강 호조나 회복으로 확대하지 않습니다.";

            case PRESSURE ->
                    "현재 월운의 HEALTH Functional 계산 결과가 PRESSURE 상태로 보존되며 이를 질병·발병·악화 상태로 변환하지 않습니다.";

            case MIXED ->
                    "현재 월운의 HEALTH Functional 계산 자체에서 MIXED 상태가 확정되어 있으며 해당 upstream 결과를 그대로 보존합니다.";

            case CONTEXTUAL ->
                    "현재 월운의 HEALTH Functional 계산 결과가 CONTEXTUAL 상태로 남아 있으며 다른 HEALTH 시간층이나 원국 Evidence와 합성하지 않습니다.";
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