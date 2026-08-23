package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CAREER 카테고리의
 * 세운 EXPRESSION / PROCESSING Functional Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.LEARNING_GROWTH에서 이미 계산된:
 *
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 *
 * 결과를 CAREER 질문에서 참조할 수 있는
 * 독립적인 CURRENT_TIMELINE Functional Context로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * LearningGrowthAnnualFunctionalRule은
 * 하나의:
 *
 * ANNUAL_FUNCTIONAL_CONTEXT
 *
 * 안에서:
 *
 * 1.
 * Annual EXPRESSION Capacity
 *
 * 2.
 * Annual PROCESSING Capacity
 *
 * 3.
 * 현재 Timeline이 선택한 대운 결합 후
 * Resolved EXPRESSION
 *
 * 4.
 * 현재 Timeline이 선택한 대운 결합 후
 * Resolved PROCESSING
 *
 * 5.
 * EXPRESSION / PROCESSING과 관련된
 * Annual CrossFunctional Pattern
 *
 * 6.
 * Annual Context Qualification
 *
 * 을 실제 Java에서 계산한다.
 *
 * 이 Category Rule에서는
 * 위 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * EXPRESSION
 * =================================================
 *
 * EXPRESSION은:
 *
 * - 표현
 * - 산출
 * - 실행
 * - 외부 발현
 *
 * 기능축이다.
 *
 * =================================================
 * PROCESSING
 * =================================================
 *
 * PROCESSING은:
 *
 * - 수용
 * - 정리
 * - 처리
 * - 내재화
 * - 학습 기반
 *
 * 기능축이다.
 *
 * =================================================
 * 실제 Java 관계 계산 보존
 * =================================================
 *
 * upstream Annual Functional Layer에서
 * 실제 조건을 확인하여 계산된:
 *
 * PROCESSING_REGULATES_EXPRESSION
 * PROCESSING_RESTRAINS_OUTPUT
 * RESPONSIBILITY_GENERATES_PROCESSING
 * PRESSURE_GENERATES_PROCESSING
 *
 * 등의 typed CrossFunctional Pattern은
 * 유효한 deterministic 계산이다.
 *
 * 이 Rule은 해당 관계를 삭제하거나
 * 무효화하지 않는다.
 *
 * =================================================
 * 관계 생성과의 구분
 * =================================================
 *
 * 허용:
 *
 * upstream Java가 실제 조건을 확인해서
 * 생성한 CrossFunctional Pattern.
 *
 * -------------------------------------------------
 *
 * 금지:
 *
 * 이 Category Rule에서 서로 다른
 * LifeDomainRuleMatch가 함께 있다는 이유만으로
 * 새로운 관계를 만드는 것.
 *
 * =================================================
 * PROCESSING_REGULATES_EXPRESSION
 * =================================================
 *
 * upstream에서는:
 *
 * 처리·수용 기능이
 * 표현·산출 기능을 조절하는
 * 실제 Functional Pattern으로 계산된다.
 *
 * 이 Pattern의 존재만으로
 * SUPPORTIVE를 만들지는 않는다.
 *
 * Category에서도:
 *
 * - 공부와 표현이 조화를 이룬다
 * - 업무 능력이 좋아진다
 * - 직업에 유리하다
 *
 * 같은 의미를 추가하지 않는다.
 *
 * =================================================
 * PROCESSING_RESTRAINS_OUTPUT
 * =================================================
 *
 * upstream에서 structurally effective
 * Full Contact로 계산된 경우
 * LEARNING_GROWTH Pressure의
 * 실제 계산 근거가 될 수 있다.
 *
 * 해당 계산은 그대로 보존한다.
 *
 * 하지만:
 *
 * PROCESSING_RESTRAINS_OUTPUT
 * != 업무 실패
 * != 표현 실패
 * != 직업 실패
 * != 승진 실패
 *
 * 이다.
 *
 * =================================================
 * RESPONSIBILITY / PRESSURE -> PROCESSING
 * =================================================
 *
 * RESPONSIBILITY_GENERATES_PROCESSING
 * PRESSURE_GENERATES_PROCESSING
 *
 * 역시 upstream에서 실제 계산된 관계라면
 * 그대로 보존한다.
 *
 * 그러나 이를:
 *
 * - 책임이 있어서 공부하게 된다
 * - 직업 압박 때문에 전문성이 생긴다
 * - 업무 부담이 학습으로 이어진다
 *
 * 같은 사용자 사건·과정 문장으로
 * Category 단계에서 확대하지 않는다.
 *
 * =================================================
 * Capacity 방향
 * =================================================
 *
 * upstream Functional Contract에서는:
 *
 * EXPRESSION
 * =
 * pressure-side
 *
 * PROCESSING
 * =
 * support-side
 *
 * 로 취급한다.
 *
 * 그리고 원국 고정 DayMaster Strength를 기준으로
 * sameDirectionAccumulationContact 등의
 * Capacity 조건을 계산한다.
 *
 * 이 Rule에서는 해당 Capacity 계산을
 * 다시 하지 않는다.
 *
 * =================================================
 * DayMaster Strength
 * =================================================
 *
 * 세운에서 일간 Strength를
 * 다시 계산하지 않는다.
 *
 * upstream Annual Functional 계산이 사용하는
 * 원국 고정 DayMaster Strength Contract를
 * 그대로 따른다.
 *
 * =================================================
 * Qualification
 * =================================================
 *
 * upstream에서는:
 *
 * - balanced
 * - extreme
 * - follow structure review
 * - transformation target
 * - original function reduction
 * - restrictive relation
 * - current segment qualification
 *
 * 등의 조건 때문에
 * 단독 방향 판정을 제한할 수 있다.
 *
 * 해당 qualification 결과를
 * Category에서 제거하거나 재판정하지 않는다.
 *
 * =================================================
 * Upstream Status
 * =================================================
 *
 * 현재 LearningGrowthAnnualFunctionalRule은
 * 실제 계산 결과에 따라 주로:
 *
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * 을 생성한다.
 *
 * 이 Rule 단독으로 SUPPORTIVE를
 * 생성하지 않는 Contract도
 * upstream 그대로 유지한다.
 *
 * =================================================
 * 중요한 Cross-Domain 경계
 * =================================================
 *
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL의 Status는
 * EXPRESSION / PROCESSING Functional 상태다.
 *
 * CAREER의:
 *
 * - 관살 상태
 * - 직업운 방향
 * - 직위 상태
 * - 취업 방향
 * - 승진 방향
 *
 * 을 직접 계산한 Status가 아니다.
 *
 * 따라서:
 *
 * LEARNING FUNCTIONAL PRESSURE
 * != CAREER PRESSURE
 *
 * LEARNING FUNCTIONAL MIXED
 * != CAREER MIXED
 *
 * LEARNING FUNCTIONAL CONTEXTUAL
 * != CAREER CONTEXTUAL이라는
 * 직업 방향 판정
 *
 * 으로 바꾸지 않는다.
 *
 * =================================================
 * Category Status
 * =================================================
 *
 * 이 Rule이 생성하는:
 *
 * ConsultationCategoryDeterministicStatus
 *
 * 는 항상:
 *
 * CONTEXTUAL
 *
 * 이다.
 *
 * 원본 LifeDomainSignalStatus는
 * sourceMatches에 그대로 보존한다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * CAREER Category Functional Context 하나
 *
 * 로 그대로 보존한다.
 *
 * upstream Rule 자체가 EXPRESSION과 PROCESSING을
 * 하나의 공식 Functional Match로 계산했으므로,
 * Category에서는 이것을 다시:
 *
 * - EXPRESSION Match
 * - PROCESSING Match
 *
 * 로 분해하지 않는다.
 *
 * =================================================
 * LEARNING_ANNUAL_CONTEXT와 분리
 * =================================================
 *
 * LEARNING_ANNUAL_CONTEXT
 *
 * ->
 *
 * CareerLearningAnnualContextRule
 *
 * -------------------------------------------------
 *
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 둘은 서로 다른 계산 Source다.
 *
 * 하나는 일반 Annual Context,
 * 하나는 Annual Functional Contract다.
 *
 * =================================================
 * Status 합성 금지
 * =================================================
 *
 * 예:
 *
 * LEARNING_ANNUAL_CONTEXT = SUPPORTIVE
 *
 * +
 *
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL = PRESSURE
 *
 * =
 *
 * MIXED
 *
 * 같은 Category 합성을 하지 않는다.
 *
 * =================================================
 * Annual provenance 경계
 * =================================================
 *
 * 두 Source가 서로 다른 계산이더라도
 * 동일 명리연도의 Annual 계층에서
 * 파생된 결과다.
 *
 * 따라서 이후 Grounding / Prominence에서
 * 단순히:
 *
 * - Annual Context 1개
 * - Annual Functional 1개
 *
 * 를 독립 검증 2회로 계산하여
 * 근거 강도를 부풀려서는 안 된다.
 *
 * 동일 Annual temporal provenance를
 * 고려해야 한다.
 *
 * =================================================
 * CAREER_ANNUAL_FUNCTIONAL과 분리
 * =================================================
 *
 * CAREER_ANNUAL_FUNCTIONAL
 *
 * ->
 *
 * CareerAnnualFunctionalDeterministicRule
 *
 * 에서 RESPONSIBILITY Function을
 * 별도로 처리한다.
 *
 * 이 Rule에서는:
 *
 * CAREER_ANNUAL_FUNCTIONAL
 *
 * 을 조회하지 않는다.
 *
 * =================================================
 * Cross-Domain Functional 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * CAREER RESPONSIBILITY Functional
 * +
 * LEARNING EXPRESSION Functional
 * =
 * 새로운 직업 상태
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * CAREER RESPONSIBILITY Functional
 * +
 * LEARNING PROCESSING Functional
 * =
 * 새로운 직업 상태
 *
 * 금지.
 *
 * =================================================
 * 중요한 예외
 * =================================================
 *
 * upstream Annual Functional Context 안에서
 * RESPONSIBILITY와 PROCESSING 사이의
 * typed CrossFunctional Pattern이
 * 실제 Java 계산으로 존재하는 경우는 다르다.
 *
 * 예:
 *
 * RESPONSIBILITY_GENERATES_PROCESSING
 *
 * 이 관계는 이미 upstream이 계산한
 * deterministic 결과이므로 보존한다.
 *
 * Category에서 두 독립 Match를 보고
 * 다시 만든 관계가 아니다.
 *
 * =================================================
 * CAREER_ANNUAL_CONTEXT와 분리
 * =================================================
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * 역시 이 Rule에 사용하지 않는다.
 *
 * 다음 계산을 하지 않는다.
 *
 * CAREER_ANNUAL_CONTEXT
 * +
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 * =
 * 새로운 CAREER Status
 *
 * =================================================
 * 대운과의 경계
 * =================================================
 *
 * LearningGrowthAnnualFunctionalRule 내부에서
 * 현재 Timeline이 선택한 정확한
 * Annual-Decade Functional Segment를
 * 사용하는 것은 upstream 공식 계산이다.
 *
 * 이를 그대로 인정한다.
 *
 * 하지만 Category 단계에서 다시:
 *
 * LEARNING_DECADE_CONTEXT
 * +
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 *
 * 을 결합하지 않는다.
 *
 * =================================================
 * 월운과 분리
 * =================================================
 *
 * 다음 Source를 사용하지 않는다.
 *
 * LEARNING_MONTHLY_CONTEXT
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 * CAREER_MONTHLY_CONTEXT
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * 월운은 별도 Rule이 담당한다.
 *
 * =================================================
 * 원국 Learning과 분리
 * =================================================
 *
 * 다음 Source도 이 Rule에서 사용하지 않는다.
 *
 * LEARNING_RESOURCE_OUTPUT
 * LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE
 *
 * 원국 기능 구조와
 * Annual Functional Status를
 * Category 단계에서 합성하지 않는다.
 *
 * =================================================
 * 실제 사건 생성 금지
 * =================================================
 *
 * EXPRESSION 활성
 * != 업무 성과
 *
 * PROCESSING 활성
 * != 공부 성과
 *
 * PRESSURE
 * != 시험 실패
 * != 업무 실패
 * != 직장 갈등
 * != 퇴사
 *
 * MIXED
 * != 이직
 * != 진로 변경
 *
 * CONTEXTUAL
 * != 아무 일도 없음
 *
 * =================================================
 * 자격 / 시험 사건 생성 금지
 * =================================================
 *
 * PROCESSING 활성
 * != 자격증 취득
 *
 * PROCESSING 관련 Pattern
 * != 시험 합격
 *
 * EXPRESSION 관련 Pattern
 * != 면접 합격
 *
 * =================================================
 * 특정 직업 생성 금지
 * =================================================
 *
 * PROCESSING
 * != 연구직
 * != 교사
 * != 전문직
 *
 * EXPRESSION
 * != 영업
 * != 창업
 * != 콘텐츠 직업
 *
 * =================================================
 * 능력 확정 금지
 * =================================================
 *
 * PROCESSING 활성
 * != 학습 능력이 좋다
 *
 * EXPRESSION 활성
 * != 표현 능력이 뛰어나다
 *
 * PROCESSING_REGULATES_EXPRESSION
 * != 준비와 실행이 완벽하다
 *
 * PROCESSING_RESTRAINS_OUTPUT
 * != 표현력이 떨어진다
 *
 * 구조적 Functional 계산과
 * 실제 능력 성과는 동일하지 않다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각 Segment에 실제 존재하는:
 *
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 *
 * Match를 각각 독립적으로 보존한다.
 *
 * 특정 Segment 하나를
 * 대표값으로 선택하지 않는다.
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
 * monthIndex가 존재한다고 해서
 * Annual Functional 결과를
 * Monthly Functional로 재해석하지 않는다.
 *
 * monthIndex == 0도 유효하다.
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * source Match의:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱하여:
 *
 * - EXPRESSION
 * - PROCESSING
 * - CrossFunctional Pattern
 * - Capacity
 * - Qualification
 *
 * 을 다시 판정하지 않는다.
 *
 * 원본 LifeDomainRuleMatch를
 * provenance로 그대로 보존한다.
 *
 * =================================================
 * 관계·인과·순서 생성 금지
 * =================================================
 *
 * Category 단계에서는 다음을
 * 새로 생성하지 않는다.
 *
 * - 학습 기능이 직업운을 강화한다
 * - 표현 기능이 승진으로 이어진다
 * - 처리 기능 때문에 직업 책임이 안정된다
 * - 책임 때문에 학습이 필요해진다
 * - 학습한 뒤 성과가 난다
 * - 먼저 준비하고 이후 실행한다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Annual Functional Context 재계산
 * - EXPRESSION Capacity 재계산
 * - PROCESSING Capacity 재계산
 * - Resolved Function 재계산
 * - CrossFunctional Pattern 재계산
 * - qualification 재계산
 * - 원국 Strength 재계산
 * - LEARNING Status를 CAREER Status로 전이
 * - LEARNING_ANNUAL_CONTEXT와 Status 합성
 * - CAREER_ANNUAL_CONTEXT와 Status 합성
 * - CAREER_ANNUAL_FUNCTIONAL과 Status 합성
 * - LEARNING_DECADE_CONTEXT와 Status 합성
 * - Monthly Context와 Status 합성
 * - Monthly Functional과 Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 특정 직업군 생성
 * - 시험·자격 사건 생성
 * - 실제 직업 사건 생성
 */
@Component
public class CareerLearningAnnualFunctionalContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_LEARNING_ANNUAL_FUNCTIONAL_CONTEXT";

    private static final String
            LEARNING_ANNUAL_FUNCTIONAL_RULE_CODE =
            "LEARNING_GROWTH_ANNUAL_FUNCTIONAL";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.CAREER;
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
                "CareerLearningAnnualFunctionalContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerLearningAnnualFunctionalContextRule은 CAREER View만 평가할 수 있습니다."
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
                            "CAREER Learning Annual Functional Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * Selected LEARNING Functional Source
             * =================================================
             */

            List<LifeDomainRuleMatch> functionalMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            LEARNING_ANNUAL_FUNCTIONAL_RULE_CODE
                    );

            if (
                    functionalMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * Match 1개 -> Category Context 1개
             * =================================================
             */

            for (
                    LifeDomainRuleMatch functionalMatch
                    : functionalMatches
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                functionalMatch,
                                "LEARNING_GROWTH_ANNUAL_FUNCTIONAL Match는 null일 수 없습니다."
                        );

                /*
                 * =================================================
                 * CAREER Category Status
                 * =================================================
                 *
                 * 원본 Functional Status는
                 * CAREER 직업운 방향으로 전이하지 않는다.
                 */

                ConsultationCategoryDeterministicStatus categoryStatus =
                        ConsultationCategoryDeterministicStatus
                                .CONTEXTUAL;

                List<String> reasons =
                        buildReasons(
                                normalizedSegment,
                                normalizedMatch
                        );

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.CAREER,

                                ConsultationCategoryDeterministicLayer
                                        .CURRENT_TIMELINE,

                                RULE_CODE,

                                categoryStatus,

                                normalizedSegment
                                        .sajuYear(),

                                normalizedSegment
                                        .monthIndex(),

                                List.of(),

                                summaryOf(
                                        normalizedMatch
                                ),

                                List.of(
                                        normalizedMatch
                                ),

                                reasons
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
     * Segment Selected LEARNING Functional Source 조회
     * =================================================
     *
     * CAREER View 안에서:
     *
     * LifeDomain.LEARNING_GROWTH
     *
     * +
     *
     * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
     *
     * 정확 일치만 사용한다.
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
                        "CAREER Learning Annual Functional sourceRuleCode가 필요합니다."
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
                            "CAREER Segment Match는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * LEARNING_GROWTH Domain만
             * =================================================
             */

            if (
                    normalized.domain()
                            != LifeDomain.LEARNING_GROWTH
            ) {

                continue;
            }

            /*
             * =================================================
             * 정확한 Functional RuleCode만
             * =================================================
             */

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
                "CAREER Learning Annual Functional Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "CAREER Learning Annual Functional Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "LEARNING_GROWTH_ANNUAL_FUNCTIONAL에서 이미 계산된 하나의 EXPRESSION·PROCESSING Annual Functional Match를 CAREER의 독립 Functional Context로 보존했습니다."
        );

        reasons.add(
                "원본 LEARNING_GROWTH Functional Match의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 이를 CAREER의 SUPPORTIVE·PRESSURE·MIXED 방향으로 직접 전이하지 않았습니다."
        );

        reasons.add(
                "이 Category Match는 직업운 방향이 아니라 CAREER 질문에서 참조 가능한 EXPRESSION·PROCESSING 세운 Functional Context이므로 CONTEXTUAL로 유지합니다."
        );

        reasons.add(
                "LearningGrowthAnnualFunctionalRule이 하나의 ANNUAL_FUNCTIONAL_CONTEXT 안에서 EXPRESSION Capacity, PROCESSING Capacity, 현재 Resolved Function, CrossFunctional Pattern 및 qualification을 이용해 확정한 결과를 다시 계산하지 않았습니다."
        );

        reasons.add(
                "PROCESSING_REGULATES_EXPRESSION, PROCESSING_RESTRAINS_OUTPUT, RESPONSIBILITY_GENERATES_PROCESSING, PRESSURE_GENERATES_PROCESSING 등 upstream Java가 실제 계산한 typed CrossFunctional Pattern이 존재하면 해당 관계 자체는 그대로 보존합니다."
        );

        reasons.add(
                "upstream에서 실제 계산된 CrossFunctional Pattern을 인정하는 것과 별개의 Category Match들을 보고 새로운 관계를 만드는 것을 구분하며 이 Rule에서는 후자를 수행하지 않았습니다."
        );

        reasons.add(
                "LEARNING_ANNUAL_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "LEARNING_ANNUAL_CONTEXT와 LEARNING_GROWTH_ANNUAL_FUNCTIONAL이 같은 명리연도에 존재하더라도 서로의 Status를 합쳐 새로운 CAREER 방향을 만들지 않았습니다."
        );

        reasons.add(
                "같은 명리연도의 Annual 계층에서 파생된 Context와 Functional 결과를 단순 독립 검증 2회 또는 근거 강도 2배로 사용하지 않습니다."
        );

        reasons.add(
                "CAREER_ANNUAL_CONTEXT와 CAREER_ANNUAL_FUNCTIONAL을 이 Rule의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "CAREER RESPONSIBILITY Functional과 LEARNING EXPRESSION·PROCESSING Functional이 함께 존재한다는 이유만으로 새로운 Cross-Domain 관계를 생성하지 않았습니다."
        );

        reasons.add(
                "단, upstream Annual Functional Context가 실제로 RESPONSIBILITY_GENERATES_PROCESSING 같은 typed 관계를 계산했다면 그 기존 Java 결과 자체는 그대로 인정합니다."
        );

        reasons.add(
                "LEARNING_DECADE_CONTEXT, LEARNING_MONTHLY_CONTEXT, LEARNING_GROWTH_MONTHLY_FUNCTIONAL을 이 Match와 Category 단계에서 다시 합성하지 않았습니다."
        );

        reasons.add(
                "LEARNING_RESOURCE_OUTPUT과 LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE 같은 원국 Learning Source도 Annual Functional Status와 합성하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 EXPRESSION, PROCESSING, CrossFunctional Pattern, Capacity 또는 qualification을 재판정하지 않았습니다."
        );

        reasons.add(
                "이 결과를 시험 합격·자격 취득·업무 성공·취업·승진·이직·퇴사·해고 같은 실제 사건이나 특정 직업군으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 CAREER Learning Annual Functional 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 존재하면 각각의 LEARNING_GROWTH_ANNUAL_FUNCTIONAL 결과를 별도로 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment에서 대운을 사용할 수 없는 경우 다른 Learning Decade Match를 대신 넣지 않으며 upstream에서 실제 생성된 Annual Functional Match만 보존합니다."
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
     *
     * 최종 사용자 Story가 아니다.
     *
     * Source Functional Status는
     * provenance에 보존하되
     * CAREER 직업운 방향으로 전이하지 않는다.
     */

    private String summaryOf(
            LifeDomainRuleMatch sourceMatch
    ) {

        Objects.requireNonNull(
                sourceMatch,
                "CAREER Learning Annual Functional Context에는 Source Match가 필요합니다."
        );

        return "현재 명리연도에서 Java가 계산한 EXPRESSION·PROCESSING Functional 상태를 "
                + "CAREER 질문의 독립 기능 Context로 보존합니다. "
                + "원본 LEARNING_GROWTH Functional 상태는 "
                + sourceMatch.status()
                + "이며 이를 CAREER 직업운의 방향으로 직접 변환하지 않습니다.";
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