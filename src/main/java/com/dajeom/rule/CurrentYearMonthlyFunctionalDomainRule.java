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
 * CURRENT_YEAR_LUCK의
 * LifeDomain별 Monthly Functional deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 현재 Timeline Segment에 이미 존재하는
 * 공식 Monthly Functional LifeDomain Match를
 * CURRENT_YEAR_LUCK에서 독립적으로 참조할 수 있도록
 * 1 : 1 Category Match로 보존한다.
 *
 * =================================================
 * 중요한 설계 원칙
 * =================================================
 *
 * CURRENT_YEAR_LUCK은 여기서:
 *
 * - 월운 종합기를 만들지 않는다.
 * - LifeDomain 간 관계를 만들지 않는다.
 * - LifeDomain 간 Status를 합성하지 않는다.
 * - 전체 연도 Status를 만들지 않는다.
 *
 * 각 영역이 이미 Java에서 계산한
 * 월운 Functional 결과를
 * 독립적으로 전달받을 뿐이다.
 *
 * =================================================
 * 실제 Source
 * =================================================
 *
 * WEALTH
 * ->
 * WEALTH_MONTHLY_FUNCTIONAL
 *
 * CAREER
 * ->
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * RELATIONSHIP
 * ->
 * RELATIONSHIP_MONTHLY_FUNCTIONAL
 *
 * LEARNING_GROWTH
 * ->
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 *
 * HEALTH_LIFESTYLE
 * ->
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * =================================================
 * WEALTH
 * =================================================
 *
 * WealthMonthlyFunctionalRule은:
 *
 * RESOURCE_REALITY
 *
 * Function의 공식 Monthly Functional 결과를
 * 계산한다.
 *
 * Annual Functional Baseline을 변경하지 않고,
 * 실제 월운 접촉이 존재하는 경우에만
 * 현재 월의 Functional 상태를 계산한다.
 *
 * Category에서는 다시 계산하지 않는다.
 *
 * =================================================
 * CAREER
 * =================================================
 *
 * CareerMonthlyFunctionalRule은:
 *
 * RESPONSIBILITY
 *
 * Function의 공식 Monthly Functional 결과를
 * 계산한다.
 *
 * 현재 Monthly Functional Layer에는
 * 별도 Monthly CrossFunctionalAnalysis가 없으므로
 * 다른 Function과의 관계를 새로 만들지 않는다.
 *
 * Category에서도 관계를 추가하지 않는다.
 *
 * =================================================
 * RELATIONSHIP
 * =================================================
 *
 * RelationshipMonthlyFunctionalRule은:
 *
 * RESOURCE_REALITY
 *
 * 와
 *
 * RESPONSIBILITY
 *
 * 를 각각 독립적으로 계산한다.
 *
 * 현재 Relationship Evidence에는
 * Gender를 이용한 배우자성 선택 Contract가 없으므로
 * 두 Function 중 하나를 임의로 대표 배우자성으로
 * 선택하지 않는다.
 *
 * =================================================
 * RELATIONSHIP 1 : 1 보존
 * =================================================
 *
 * RelationshipMonthlyFunctionalRule이:
 *
 * RESOURCE_REALITY Match
 *
 * 와
 *
 * RESPONSIBILITY Match
 *
 * 를 각각 생성했다면
 * Category에서도 각각 따로 보존한다.
 *
 * 즉:
 *
 * Relationship Functional Match A
 * ->
 * Category Match A
 *
 * Relationship Functional Match B
 * ->
 * Category Match B
 *
 * 이다.
 *
 * =================================================
 * RELATIONSHIP 합성 금지
 * =================================================
 *
 * RESOURCE_REALITY Match
 * +
 * RESPONSIBILITY Match
 *
 * 가 모두 존재한다고 해서:
 *
 * - 관계 부담이 강하다
 * - 관계가 복잡하다
 * - 장기 관계가 된다
 * - 배우자운이 강하다
 * - 두 Function이 충돌한다
 *
 * 같은 새로운 상태나 관계를 만들지 않는다.
 *
 * =================================================
 * LEARNING_GROWTH
 * =================================================
 *
 * LearningGrowthMonthlyFunctionalRule은:
 *
 * EXPRESSION
 *
 * PROCESSING
 *
 * Function을 공식 Monthly Functional Contract 안에서
 * 계산한다.
 *
 * upstream Rule이 하나의 공식
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL Match로
 * 확정한 결과를 그대로 보존한다.
 *
 * Category에서 다시 Function별 Match로
 * 재분해하지 않는다.
 *
 * =================================================
 * HEALTH_LIFESTYLE
 * =================================================
 *
 * HealthLifestyleMonthlyFunctionalRule은:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * 다섯 Function 전체의 월운 접촉을
 * 하나의 공식 HEALTH_LIFESTYLE Functional 계산으로
 * 평가한다.
 *
 * upstream Rule이 최종 생성한 Match를
 * 그대로 보존한다.
 *
 * =================================================
 * Monthly CrossFunctional 경계
 * =================================================
 *
 * 현재 Monthly Functional Layer에는
 * 별도의 Monthly CrossFunctionalAnalysis가 없다.
 *
 * 따라서 Category에서도 다음과 같은
 * Function 간 관계를 새로 생성하지 않는다.
 *
 * EXPRESSION_CONFLICTS_RESPONSIBILITY
 *
 * PROCESSING_REGULATES_EXPRESSION
 *
 * RESOURCE_GENERATES_RESPONSIBILITY
 *
 * RESPONSIBILITY_GENERATES_PROCESSING
 *
 * 등.
 *
 * =================================================
 * Annual Baseline 참조
 * =================================================
 *
 * Monthly Functional upstream 계산이
 * Annual Functional Baseline을 사용하는 것은
 * 공식 Java 계산이다.
 *
 * 이를 그대로 인정한다.
 *
 * 하지만 Category에서 별도의:
 *
 * ANNUAL_FUNCTIONAL Match
 *
 * 를 다시 가져와
 * Monthly Functional Match와 합치지 않는다.
 *
 * =================================================
 * 대운 Segment 참조
 * =================================================
 *
 * Monthly Functional Effect가
 * 현재 대운 Segment가 반영된
 * AnnualResolvedFunctionEffect를
 * 실제 upstream Java 계산으로 사용하는 것은
 * 공식 계산이다.
 *
 * 이를 인정한다.
 *
 * 하지만 Category에서 별도의:
 *
 * DECADE_CONTEXT
 *
 * Match를 다시 추가하지 않는다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * 공식 Monthly Functional
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * CURRENT_YEAR_LUCK
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 보존한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 원본 LifeDomainRuleMatch의
 * 공식 LifeDomainSignalStatus를
 * 그대로 Category Status로 변환한다.
 *
 * SUPPORTIVE
 * ->
 * SUPPORTIVE
 *
 * PRESSURE
 * ->
 * PRESSURE
 *
 * MIXED
 * ->
 * MIXED
 *
 * CONTEXTUAL
 * ->
 * CONTEXTUAL
 *
 * =================================================
 * Status 의미
 * =================================================
 *
 * 이 Status는 반드시:
 *
 * 해당 LifeDomain의 Monthly Functional 상태
 *
 * 로만 읽는다.
 *
 * 예:
 *
 * WEALTH_MONTHLY_FUNCTIONAL = PRESSURE
 *
 * 는:
 *
 * CURRENT_YEAR_LUCK 전체 PRESSURE
 *
 * 가 아니다.
 *
 * -------------------------------------------------
 *
 * CAREER_MONTHLY_FUNCTIONAL = PRESSURE
 *
 * 역시:
 *
 * 올해 전체 직업운이 나쁘다
 *
 * 를 의미하지 않는다.
 *
 * =================================================
 * Cross-Domain Status 합성 금지
 * =================================================
 *
 * 다음 계산을 하지 않는다.
 *
 * WEALTH Monthly Functional = SUPPORTIVE
 *
 * +
 *
 * CAREER Monthly Functional = PRESSURE
 *
 * =
 *
 * CURRENT_YEAR MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * RELATIONSHIP Monthly Functional = PRESSURE
 *
 * +
 *
 * LEARNING Monthly Functional = PRESSURE
 *
 * =
 *
 * 더 강한 PRESSURE
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * HEALTH Monthly Functional = MIXED
 *
 * +
 *
 * CAREER Monthly Functional = MIXED
 *
 * =
 *
 * 올해가 복잡한 달
 *
 * 금지.
 *
 * =================================================
 * Cross-Domain 관계 생성 금지
 * =================================================
 *
 * 다음을 만들지 않는다.
 *
 * - 재물 기능 때문에 일이 바뀐다
 * - 직업 부담 때문에 관계가 흔들린다
 * - 관계 부담 때문에 학습이 어려워진다
 * - 학습 기능이 직업 성과로 이어진다
 * - 건강 Functional 때문에 직업이 약해진다
 * - 재물과 직업이 같이 움직인다
 * - 여러 영역이 동시에 작동한다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * Monthly Context와 분리
 * =================================================
 *
 * 앞에서 연결한 일반 Monthly Context:
 *
 * WEALTH_MONTHLY_CONTEXT
 * CAREER_MONTHLY_CONTEXT
 * RELATIONSHIP_MONTHLY_CONTEXT
 * LEARNING_MONTHLY_CONTEXT
 *
 * 와 이번 Monthly Functional은
 * 서로 다른 공식 계산이다.
 *
 * Category에서는 두 결과를 합치지 않는다.
 *
 * =================================================
 * Monthly Context + Functional 합성 금지
 * =================================================
 *
 * 예:
 *
 * WEALTH_MONTHLY_CONTEXT = SUPPORTIVE
 *
 * +
 *
 * WEALTH_MONTHLY_FUNCTIONAL = PRESSURE
 *
 * =
 *
 * MIXED
 *
 * 같은 Category 계산을 하지 않는다.
 *
 * -------------------------------------------------
 *
 * RELATIONSHIP_MONTHLY_CONTEXT = SUPPORTIVE
 *
 * +
 *
 * RELATIONSHIP_MONTHLY_FUNCTIONAL = PRESSURE
 *
 * =
 *
 * 관계운 MIXED
 *
 * 같은 계산도 하지 않는다.
 *
 * =================================================
 * Monthly provenance 경계
 * =================================================
 *
 * 같은 LifeDomain의:
 *
 * MONTHLY_CONTEXT
 *
 * 와
 *
 * MONTHLY_FUNCTIONAL
 *
 * 은 서로 다른 계산 결과지만
 * 같은 명리연도·월의 temporal provenance에서
 * 파생될 수 있다.
 *
 * 따라서 이후 Grounding / Prominence에서:
 *
 * Monthly Context 1개
 * +
 * Monthly Functional 1개
 *
 * =
 *
 * 독립 검증 2회
 *
 * 로 단순 계산해서는 안 된다.
 *
 * =================================================
 * Cross-Domain provenance 경계
 * =================================================
 *
 * 같은 월의 여러 LifeDomain Functional 결과도
 * 동일 MonthlyFunctionalLifeDomainContext 계층에서
 * 파생된 결과일 수 있다.
 *
 * 따라서:
 *
 * 5개 Domain Match
 *
 * =
 *
 * 독립 검증 5회
 *
 * 로 단순 계산해서는 안 된다.
 *
 * =================================================
 * Annual Context와 분리
 * =================================================
 *
 * 다음 Source를 사용하지 않는다.
 *
 * WEALTH_ANNUAL_CONTEXT
 * CAREER_ANNUAL_CONTEXT
 * RELATIONSHIP_ANNUAL_CONTEXT
 * LEARNING_ANNUAL_CONTEXT
 * HEALTH_ANNUAL_CONTEXT
 *
 * =================================================
 * Annual Functional과 분리
 * =================================================
 *
 * 다음 Source 역시
 * Category Match로 다시 결합하지 않는다.
 *
 * WEALTH_ANNUAL_FUNCTIONAL
 * CAREER_ANNUAL_FUNCTIONAL
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * Relationship에는 현재
 * RELATIONSHIP_ANNUAL_FUNCTIONAL이 없다.
 *
 * =================================================
 * Decade Context와 분리
 * =================================================
 *
 * 다음 Source도 이 Rule에서
 * 직접 사용하지 않는다.
 *
 * WEALTH_DECADE_CONTEXT
 * CAREER_DECADE_CONTEXT
 * RELATIONSHIP_DECADE_CONTEXT
 * LEARNING_DECADE_CONTEXT
 * HEALTH_DECADE_CONTEXT
 *
 * =================================================
 * Natal과 분리
 * =================================================
 *
 * CURRENT_YEAR_LUCK View에는
 * Natal Match도 존재할 수 있다.
 *
 * 하지만 이 Rule에서는
 * natalMatches를 사용하지 않는다.
 *
 * 원국 상태와 Monthly Functional 상태를
 * Category 단계에서 합성하지 않는다.
 *
 * =================================================
 * 전체 CURRENT_YEAR 종합 금지
 * =================================================
 *
 * 이 Rule은 다음을 계산하지 않는다.
 *
 * - 올해 전체가 SUPPORTIVE
 * - 올해 전체가 PRESSURE
 * - 올해 전체가 MIXED
 * - 이번 달 전체가 SUPPORTIVE
 * - 이번 달 전체가 PRESSURE
 * - 이번 달 전체가 MIXED
 *
 * =================================================
 * 대표 Domain 선택 금지
 * =================================================
 *
 * 다음 기준으로
 * 현재 월의 대표 영역을 고르지 않는다.
 *
 * - Match 개수
 * - SUPPORTIVE 수
 * - PRESSURE 수
 * - MIXED 수
 * - Function 개수
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * WEALTH PRESSURE
 * != 손실
 *
 * CAREER PRESSURE
 * != 퇴사
 * != 해고
 *
 * RELATIONSHIP PRESSURE
 * != 이별
 * != 갈등 사건
 *
 * LEARNING PRESSURE
 * != 시험 실패
 *
 * HEALTH PRESSURE
 * != 질병
 *
 * =================================================
 * Relationship 사건 경계
 * =================================================
 *
 * RESOURCE_REALITY 접촉
 * != 연애 시작
 * != 결혼
 *
 * RESPONSIBILITY 접촉
 * != 결혼
 * != 배우자 등장
 *
 * 두 Function 동시 접촉
 * != 관계 진전
 * != 장기 관계
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각 Segment에 실제 존재하는
 * Monthly Functional Match를
 * 각각 그대로 보존한다.
 *
 * 특정 Segment를 대표값으로
 * 임의 선택하지 않는다.
 *
 * =================================================
 * 좌표
 * =================================================
 *
 * 결과는 CURRENT_TIMELINE Layer이며:
 *
 * - sajuYear
 * - monthIndex
 *
 * 를 현재 Segment 좌표 그대로 보존한다.
 *
 * monthIndex == 0도
 * 유효한 값이다.
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
 * - ActivationStatus
 * - Capacity
 * - 관계
 * - 사건
 *
 * 을 재판정하지 않는다.
 *
 * 정확한:
 *
 * LifeDomain
 * +
 * RuleCode
 *
 * 조합만 사용한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - MonthlyFunctionalLifeDomainContext 재계산
 * - Monthly Functional Effect 재계산
 * - Annual Baseline 재계산
 * - Resolved Function 재계산
 * - ActivationStatus 재계산
 * - Capacity 재계산
 * - qualification 재계산
 * - Monthly CrossFunctional 관계 생성
 * - Cross-Domain Status 합성
 * - Monthly Context와 Status 합성
 * - Annual Context와 Status 합성
 * - Annual Functional과 Status 합성
 * - Decade Context와 Status 합성
 * - Natal Match와 Status 합성
 * - Relationship 두 Function 합성
 * - 전체 CURRENT_YEAR Status 생성
 * - 현재 월 전체 Status 생성
 * - 대표 Domain 선택
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 실제 사건 생성
 */
@Component
public class CurrentYearMonthlyFunctionalDomainRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CURRENT_YEAR_MONTHLY_FUNCTIONAL_DOMAIN";

    private static final String
            WEALTH_MONTHLY_FUNCTIONAL_RULE_CODE =
            "WEALTH_MONTHLY_FUNCTIONAL";

    private static final String
            CAREER_MONTHLY_FUNCTIONAL_RULE_CODE =
            "CAREER_MONTHLY_FUNCTIONAL";

    private static final String
            RELATIONSHIP_MONTHLY_FUNCTIONAL_RULE_CODE =
            "RELATIONSHIP_MONTHLY_FUNCTIONAL";

    private static final String
            LEARNING_MONTHLY_FUNCTIONAL_RULE_CODE =
            "LEARNING_GROWTH_MONTHLY_FUNCTIONAL";

    private static final String
            HEALTH_MONTHLY_FUNCTIONAL_RULE_CODE =
            "HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.CURRENT_YEAR_LUCK;
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
                "CurrentYearMonthlyFunctionalDomainRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.CURRENT_YEAR_LUCK
        ) {

            throw new IllegalArgumentException(
                    "CurrentYearMonthlyFunctionalDomainRule은 CURRENT_YEAR_LUCK View만 평가할 수 있습니다."
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
                            "CURRENT_YEAR_LUCK Monthly Functional Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 공식 Monthly Functional Source만 선택
             * =================================================
             *
             * RelationshipMonthlyFunctionalRule은
             * 같은 RuleCode로:
             *
             * RESOURCE_REALITY
             * RESPONSIBILITY
             *
             * 각각의 Match를 만들 수 있다.
             *
             * 둘 모두 1 : 1로 그대로 보존한다.
             */

            for (
                    LifeDomainRuleMatch match
                    : normalizedSegment.matches()
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                match,
                                "CURRENT_YEAR_LUCK Monthly Functional Match는 null일 수 없습니다."
                        );

                if (
                        !isOfficialMonthlyFunctional(
                                normalizedMatch
                        )
                ) {

                    continue;
                }

                ConsultationCategoryDeterministicStatus status =
                        toCategoryStatus(
                                normalizedMatch.status()
                        );

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.CURRENT_YEAR_LUCK,

                                ConsultationCategoryDeterministicLayer
                                        .CURRENT_TIMELINE,

                                RULE_CODE,

                                status,

                                normalizedSegment.sajuYear(),

                                normalizedSegment.monthIndex(),

                                List.of(),

                                summaryOf(
                                        normalizedMatch.domain(),
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
     * 공식 Monthly Functional 판별
     * =================================================
     *
     * 정확한:
     *
     * LifeDomain
     * +
     * RuleCode
     *
     * Pair만 허용한다.
     */

    private boolean isOfficialMonthlyFunctional(
            LifeDomainRuleMatch match
    ) {

        Objects.requireNonNull(
                match,
                "Monthly Functional 판별에는 LifeDomainRuleMatch가 필요합니다."
        );

        String ruleCode =
                requireText(
                        match.ruleCode(),
                        "Monthly Functional Match에는 ruleCode가 필요합니다."
                );

        return switch (
                match.domain()
                ) {

            case WEALTH ->
                    WEALTH_MONTHLY_FUNCTIONAL_RULE_CODE.equals(
                            ruleCode
                    );

            case CAREER ->
                    CAREER_MONTHLY_FUNCTIONAL_RULE_CODE.equals(
                            ruleCode
                    );

            case RELATIONSHIP ->
                    RELATIONSHIP_MONTHLY_FUNCTIONAL_RULE_CODE.equals(
                            ruleCode
                    );

            case LEARNING_GROWTH ->
                    LEARNING_MONTHLY_FUNCTIONAL_RULE_CODE.equals(
                            ruleCode
                    );

            case HEALTH_LIFESTYLE ->
                    HEALTH_MONTHLY_FUNCTIONAL_RULE_CODE.equals(
                            ruleCode
                    );
        };
    }


    /*
     * =================================================
     * Status 1 : 1 변환
     * =================================================
     *
     * 각 upstream Monthly Functional Rule이
     * 이미 확정한 공식 LifeDomainSignalStatus만
     * 그대로 사용한다.
     *
     * 다른 Domain 또는 다른 시간층의
     * 상태를 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CURRENT_YEAR Monthly Functional Match에는 LifeDomainSignalStatus가 필요합니다."
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
     * 최종 사용자 Story가 아니라
     * deterministic provenance 기록이다.
     */

    private List<String> buildReasons(

            ConsultationCategorySegmentEvidence segment,

            LifeDomainRuleMatch sourceMatch

    ) {

        Objects.requireNonNull(
                segment,
                "CURRENT_YEAR Monthly Functional Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "CURRENT_YEAR Monthly Functional Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "현재 Segment에 이미 존재하는 "
                        + sourceMatch.domain()
                        + " Domain의 공식 Monthly Functional Match를 CURRENT_YEAR_LUCK의 독립 월운 기능 결과로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 Monthly Functional Match의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 LifeDomain이나 다른 시간층의 상태와 결합하지 않고 그대로 Category Status로 변환했습니다."
        );

        reasons.add(
                "이 Status는 "
                        + sourceMatch.domain()
                        + " 영역의 Monthly Functional 상태이며 CURRENT_YEAR_LUCK 전체 또는 현재 월 전체의 종합 Status를 의미하지 않습니다."
        );

        reasons.add(
                "Monthly Functional upstream 계산에서 이미 확정된 Annual Baseline 활성 여부, 실제 월운 접촉, ActivationStatus, Capacity 관련 조건 및 qualification을 다시 계산하지 않았습니다."
        );

        reasons.add(
                "현재 Monthly Functional Layer에는 별도 Monthly CrossFunctionalAnalysis가 없으므로 서로 다른 Function 사이의 생성·충돌·제어·조절 관계를 이 Category Rule에서 새로 생성하지 않았습니다."
        );

        if (
                sourceMatch.domain()
                        == LifeDomain.RELATIONSHIP
        ) {

            reasons.add(
                    "RelationshipMonthlyFunctionalRule은 RESOURCE_REALITY와 RESPONSIBILITY를 각각 독립적인 LifeDomainRuleMatch로 생성할 수 있으며 현재 Source Match 하나만 보존하고 두 Function Match를 Category에서 합성하지 않았습니다."
            );

            reasons.add(
                    "RESOURCE_REALITY와 RESPONSIBILITY가 같은 월에 모두 접촉하더라도 이를 배우자성 확정, 관계 진전, 관계 충돌 또는 장기 관계 같은 새로운 의미로 변환하지 않았습니다."
            );
        }

        if (
                sourceMatch.domain()
                        == LifeDomain.LEARNING_GROWTH
        ) {

            reasons.add(
                    "LearningGrowthMonthlyFunctionalRule이 EXPRESSION과 PROCESSING을 공식 Monthly Functional Contract 안에서 계산해 생성한 LifeDomain Match를 그대로 보존하며 Category에서 다시 Function별 결과로 재분해하지 않았습니다."
            );
        }

        if (
                sourceMatch.domain()
                        == LifeDomain.HEALTH_LIFESTYLE
        ) {

            reasons.add(
                    "HealthLifestyleMonthlyFunctionalRule이 다섯 Function 전체의 공식 월운 접촉을 계산해 생성한 LifeDomain Match를 그대로 보존하며 Function을 특정 장기·질환 또는 의료적 상태로 변환하지 않았습니다."
            );
        }

        reasons.add(
                "Monthly Functional upstream 내부에서 Annual Functional Baseline 또는 현재 대운 Segment가 반영된 Annual Resolved Effect를 사용하는 것은 공식 Java 계산으로 인정하지만 별도의 Annual 또는 Decade Category Match를 다시 결합하지 않았습니다."
        );

        reasons.add(
                "같은 LifeDomain의 일반 Monthly Context를 이 Rule의 Source 또는 Status 계산에 사용하지 않았으며 Monthly Context와 Monthly Functional을 Category 단계에서 합성하지 않았습니다."
        );

        reasons.add(
                "Annual Context, 별도의 Annual Functional Category Match, Decade Context 및 Natal Match를 이 Match의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "다른 LifeDomain Monthly Functional Match가 같은 Segment에 존재하더라도 Cross-Domain SUPPORTIVE·PRESSURE·MIXED 합성을 수행하지 않았습니다."
        );

        reasons.add(
                "같은 명리연도·월에서 Monthly Context와 Monthly Functional 또는 여러 LifeDomain Functional 결과가 파생될 수 있으므로 Match 개수를 독립 검증 횟수나 근거 강도로 그대로 사용해서는 안 됩니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 Function 종류, 관계, Capacity, ActivationStatus 또는 실제 사건을 재판정하지 않았습니다."
        );

        reasons.add(
                "이 결과를 손실·승진·퇴사·해고·연애 시작·결혼·이별·시험 합격·시험 실패·질병·회복 등의 실제 사건으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 CURRENT_YEAR_LUCK의 유일한 Monthly Functional 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 존재하면 각 공식 Monthly Functional Match를 별도로 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment의 대운이 unavailable 상태이더라도 upstream Monthly Functional Rule에서 실제 생성된 Match가 있다면 해당 결과만 보존하며 다른 Decade Match를 대신 추가하지 않습니다."
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
     *
     * CURRENT_YEAR_LUCK 전체 결론이 아니다.
     *
     * 현재 LifeDomain 하나의
     * Monthly Functional 결과만 표현한다.
     */

    private String summaryOf(

            LifeDomain domain,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                domain,
                "CURRENT_YEAR Monthly Functional Summary에는 LifeDomain이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "CURRENT_YEAR Monthly Functional Summary에는 Status가 필요합니다."
        );

        return switch (
                domain
                ) {

            case WEALTH ->
                    "현재 월운의 재물·자원 Functional 계산이 "
                            + status
                            + " 상태로 확정되어 있으며 다른 생활영역이나 다른 시간층과 합성하지 않습니다.";

            case CAREER ->
                    "현재 월운의 일·책임 Functional 계산이 "
                            + status
                            + " 상태로 확정되어 있으며 다른 생활영역이나 다른 시간층과 합성하지 않습니다.";

            case RELATIONSHIP ->
                    "현재 월운의 관계 Functional 계산 하나가 "
                            + status
                            + " 상태로 확정되어 있으며 다른 관계 Function Match 또는 다른 생활영역과 합성하지 않습니다.";

            case LEARNING_GROWTH ->
                    "현재 월운의 학습·처리·표현 Functional 계산이 "
                            + status
                            + " 상태로 확정되어 있으며 다른 생활영역이나 다른 시간층과 합성하지 않습니다.";

            case HEALTH_LIFESTYLE ->
                    "현재 월운의 생활균형 Functional 계산이 "
                            + status
                            + " 상태로 확정되어 있으며 의료적 상태나 다른 생활영역·시간층과 합성하지 않습니다.";
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