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
 * 월운 EXPRESSION / PROCESSING Functional Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.LEARNING_GROWTH에서 이미 계산된:
 *
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 *
 * 결과를 CAREER 질문에서 참조할 수 있는
 * 독립적인 CURRENT_TIMELINE Functional Context로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * LearningGrowthMonthlyFunctionalRule은
 * MONTHLY_FUNCTIONAL_CONTEXT 안에서:
 *
 * - EXPRESSION
 * - PROCESSING
 *
 * 두 Function의 현재 월운 접촉을 계산한다.
 *
 * -------------------------------------------------
 *
 * EXPRESSION
 *
 * - 표현
 * - 산출
 * - 실행
 *
 * -------------------------------------------------
 *
 * PROCESSING
 *
 * - 수용
 * - 정리
 * - 처리
 * - 학습 기반
 *
 * =================================================
 * 중요한 Monthly Functional 경계
 * =================================================
 *
 * 현재 Monthly Functional Layer에는
 * 별도의 Monthly CrossFunctionalAnalysis가 없다.
 *
 * 따라서 upstream Rule은:
 *
 * PROCESSING_REGULATES_EXPRESSION
 * PROCESSING_RESTRAINS_OUTPUT
 *
 * 같은 Function 간 관계를
 * 월운 Functional 단계에서 새로 생성하지 않는다.
 *
 * 이 Category Rule 역시
 * 해당 관계를 생성하지 않는다.
 *
 * =================================================
 * Upstream이 실제 계산하는 것
 * =================================================
 *
 * 각 EXPRESSION / PROCESSING Function에 대해:
 *
 * - monthlyContactExists
 * - annualBaselineActive
 * - monthlyDirectInput
 * - structuredContextExists
 * - activationStatus
 * - mixedMonthlyContact
 * - sameDirectionAccumulation
 * - counterDirectionContact
 * - qualificationRequired
 *
 * 등을 실제 Java에서 계산한다.
 *
 * 해당 계산 결과를 Category에서
 * 다시 계산하지 않는다.
 *
 * =================================================
 * Annual Baseline 경계
 * =================================================
 *
 * upstream에서는
 * Annual Functional Baseline에서
 * 실제 활성된 Function만
 * 강한 방향 판정 후보로 사용한다.
 *
 * 즉:
 *
 * Annual Baseline 비활성
 * +
 * 월운 접촉
 *
 * 만으로 Annual Function을
 * 활성 상태로 승격하지 않는다.
 *
 * 이 계약을 그대로 보존한다.
 *
 * =================================================
 * Upstream Status
 * =================================================
 *
 * completeChart가 아니거나
 * 현재 Activation Segment Timing이 불확실하면:
 *
 * CONTEXTUAL
 *
 * -------------------------------------------------
 *
 * 활성 Annual Baseline Function이 없으면:
 *
 * CONTEXTUAL
 *
 * -------------------------------------------------
 *
 * 같은 Function 내부에
 * 지지 / 제한이 명시적으로 공존하면:
 *
 * MIXED
 *
 * -------------------------------------------------
 *
 * sameDirectionAccumulation
 * +
 * Monthly SUPPORTED
 * +
 * qualification 없음
 *
 * 이 존재하고,
 *
 * 동시에 반대방향 Function의
 * 유효 SUPPORTED 접촉도 존재하면:
 *
 * MIXED
 *
 * -------------------------------------------------
 *
 * sameDirectionAccumulation
 * +
 * Monthly SUPPORTED
 * +
 * qualification 없음
 *
 * 만 존재하면:
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
 * 현재 LearningGrowthMonthlyFunctionalRule은
 * 이 Rule 단독으로 SUPPORTIVE를 생성하지 않는다.
 *
 * 반대 방향 Function이 SUPPORTED라고 해서
 * 학습·성장 호조로 자동 변환하지 않는다.
 *
 * =================================================
 * 중요한 관계 경계
 * =================================================
 *
 * upstream이 계산하는:
 *
 * sameDirectionAccumulation
 * counterDirectionContact
 *
 * 는 각 Function의 Annual Functional Baseline과
 * 현재 월운 접촉을 대상으로
 * Java가 명시적으로 계산한 조건이다.
 *
 * 따라서 해당 계산 결과는 그대로 유효하다.
 *
 * 하지만 Category 단계에서:
 *
 * EXPRESSION Match
 * +
 * PROCESSING Match
 *
 * 를 보고 새로운 Function 관계를
 * 생성하지 않는다.
 *
 * =================================================
 * CrossFunctional 생성 금지
 * =================================================
 *
 * 이 Category Rule에서는 다음을
 * 새로 만들지 않는다.
 *
 * PROCESSING_REGULATES_EXPRESSION
 *
 * PROCESSING_RESTRAINS_OUTPUT
 *
 * RESPONSIBILITY_GENERATES_PROCESSING
 *
 * PRESSURE_GENERATES_PROCESSING
 *
 * Monthly Functional Layer에
 * 실제 별도 CrossFunctional 계산이 없기 때문이다.
 *
 * =================================================
 * 중요한 Cross-Domain 경계
 * =================================================
 *
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL의 Status는
 * 학습·처리·표현·산출 Function의
 * 월운 Functional 상태다.
 *
 * CAREER의:
 *
 * - 직업운
 * - 관살 상태
 * - 직위
 * - 취업
 * - 승진
 * - 조직 역할
 *
 * 방향을 직접 계산한 결과가 아니다.
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
 * != CAREER 직업운 방향 판정
 *
 * =================================================
 * Category Status
 * =================================================
 *
 * 이 Rule이 생성하는
 * ConsultationCategoryDeterministicStatus는
 * 항상:
 *
 * CONTEXTUAL
 *
 * 이다.
 *
 * 원본 LifeDomainSignalStatus는
 * sourceMatches 안에 그대로 보존한다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * upstream LearningGrowthMonthlyFunctionalRule은
 * EXPRESSION / PROCESSING을 하나의
 * 공식 LifeDomainRuleMatch로 계산한다.
 *
 * 따라서:
 *
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * CAREER Category Functional Context 하나
 *
 * 로 그대로 보존한다.
 *
 * 이를 다시:
 *
 * - EXPRESSION Category Match
 * - PROCESSING Category Match
 *
 * 로 분해하지 않는다.
 *
 * =================================================
 * LEARNING_MONTHLY_CONTEXT와 분리
 * =================================================
 *
 * LEARNING_MONTHLY_CONTEXT
 *
 * ->
 *
 * CareerLearningMonthlyContextRule
 *
 * -------------------------------------------------
 *
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 하나는 일반 Monthly Context이고
 * 하나는 Monthly Functional Contract다.
 *
 * 서로 독립적으로 보존한다.
 *
 * =================================================
 * Status 합성 금지
 * =================================================
 *
 * 예:
 *
 * LEARNING_MONTHLY_CONTEXT = SUPPORTIVE
 *
 * +
 *
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL = PRESSURE
 *
 * =
 *
 * MIXED
 *
 * 같은 Category 계산을 하지 않는다.
 *
 * =================================================
 * Monthly provenance 경계
 * =================================================
 *
 * LEARNING_MONTHLY_CONTEXT와
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL은
 * 동일 명리연도·월의 월운 계층에서
 * 파생되는 계산이다.
 *
 * 따라서 이후 Grounding에서:
 *
 * - 독립 검증 2회
 * - 근거 강도 2배
 * - 동일 월의 이중 확증
 *
 * 으로 사용해서는 안 된다.
 *
 * =================================================
 * CAREER_MONTHLY_CONTEXT와 분리
 * =================================================
 *
 * CAREER_MONTHLY_CONTEXT
 *
 * ->
 *
 * CareerCurrentMonthlyActivationRule
 *
 * 에서 별도로 처리한다.
 *
 * 이 Rule에서는
 * CAREER_MONTHLY_CONTEXT를 조회하지 않는다.
 *
 * =================================================
 * CAREER_MONTHLY_FUNCTIONAL과 분리
 * =================================================
 *
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * ->
 *
 * CareerMonthlyFunctionalDeterministicRule
 *
 * 에서 RESPONSIBILITY의 월운 Functional을
 * 별도로 처리한다.
 *
 * 이 Rule에서는
 * CAREER_MONTHLY_FUNCTIONAL을 조회하지 않는다.
 *
 * =================================================
 * Cross-Domain Functional 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * CAREER RESPONSIBILITY Monthly Functional
 * +
 * LEARNING EXPRESSION Monthly Functional
 * =
 * 새로운 CAREER 상태
 *
 * -------------------------------------------------
 *
 * CAREER RESPONSIBILITY Monthly Functional
 * +
 * LEARNING PROCESSING Monthly Functional
 * =
 * 새로운 CAREER 상태
 *
 * =================================================
 * Annual Functional과 분리
 * =================================================
 *
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 *
 * ->
 *
 * CareerLearningAnnualFunctionalContextRule
 *
 * 에서 별도로 처리한다.
 *
 * Monthly Functional upstream 내부에서
 * Annual Functional Baseline을 사용하는 것은
 * 공식 Java 계산 Contract다.
 *
 * 하지만 Category 단계에서:
 *
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 * +
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 *
 * 을 다시 합치지 않는다.
 *
 * =================================================
 * Annual Context와 분리
 * =================================================
 *
 * LEARNING_ANNUAL_CONTEXT도
 * 이 Rule의 Source로 사용하지 않는다.
 *
 * Monthly Functional이 Annual Baseline을
 * 내부적으로 참조한다고 해서:
 *
 * LEARNING_ANNUAL_CONTEXT
 * +
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 *
 * 을 Category에서 다시 합성하지 않는다.
 *
 * =================================================
 * Decade와 분리
 * =================================================
 *
 * LEARNING_DECADE_CONTEXT도
 * 이 Rule에서 조회하지 않는다.
 *
 * 현재 Monthly Functional Effect 내부의
 * segmentResolvedEffect가
 * 정확한 대운 결합 후 Annual 결과를
 * 보존하고 있을 수 있는 것은
 * upstream 공식 계산이다.
 *
 * Category 단계에서 다시:
 *
 * LEARNING_DECADE_CONTEXT
 * +
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 *
 * 을 만들지 않는다.
 *
 * =================================================
 * 원국 Learning과 분리
 * =================================================
 *
 * 다음 Source도 사용하지 않는다.
 *
 * LEARNING_RESOURCE_OUTPUT
 *
 * LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE
 *
 * 원국 기능 구조와
 * Monthly Functional 상태를
 * Category 단계에서 합성하지 않는다.
 *
 * =================================================
 * 관계·인과·순서 생성 금지
 * =================================================
 *
 * Category 단계에서 다음을
 * 새로 생성하지 않는다.
 *
 * - 이번 달 학습 기능 때문에
 *   직업운이 좋아진다
 *
 * - 표현 기능 때문에
 *   업무 성과가 좋아진다
 *
 * - PROCESSING 때문에
 *   EXPRESSION이 안정된다
 *
 * - EXPRESSION 때문에
 *   직업 책임이 흔들린다
 *
 * - 세운 상태 때문에
 *   월운 기능이 변한다
 *
 * - 학습한 뒤 직업 성과가 난다
 *
 * - 먼저 준비하고 이후 실행한다
 *
 * - A 때문에 B
 *
 * - A가 B로 이어진다
 *
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * 실제 사건 생성 금지
 * =================================================
 *
 * PRESSURE
 * != 시험 실패
 * != 업무 실패
 * != 취업 실패
 * != 승진 실패
 * != 퇴사
 * != 해고
 *
 * MIXED
 * != 이직
 * != 진로 변경
 *
 * CONTEXTUAL
 * != 아무 일도 없음
 *
 * =================================================
 * 반대방향 SUPPORTED 경계
 * =================================================
 *
 * counterDirectionContact
 * +
 * Monthly SUPPORTED
 *
 * 가 있다고 해서:
 *
 * - 공부가 잘 된다
 * - 시험에 유리하다
 * - 직업에 유리하다
 * - 문제가 해결된다
 *
 * 로 변환하지 않는다.
 *
 * upstream도 이를 단독 SUPPORTIVE로
 * 변환하지 않는다.
 *
 * =================================================
 * WEAKENED / CONFLICTED 경계
 * =================================================
 *
 * upstream은:
 *
 * WEAKENED
 * CONFLICTED
 *
 * 를 이 계층만으로
 * 부담 완화 또는 추가 마찰이라고
 * 강하게 확정하지 않는다.
 *
 * Category에서도 별도 의미를
 * 추가하지 않는다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * contacted Function 수를:
 *
 * - 직업 역량 수
 * - 능력 강도
 * - 전문성
 * - 학습 능력
 * - 성과 가능성
 *
 * 점수로 사용하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각 Segment에 실제 존재하는:
 *
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 *
 * Match를 각각 독립적으로 보존한다.
 *
 * 특정 Segment 하나를
 * 대표값으로 임의 선택하지 않는다.
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
 * source Match의:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱하여:
 *
 * - EXPRESSION
 * - PROCESSING
 * - activationStatus
 * - annualBaselineActive
 * - sameDirectionAccumulation
 * - counterDirectionContact
 * - qualification
 *
 * 을 다시 판정하지 않는다.
 *
 * 원본 LifeDomainRuleMatch 자체를
 * sourceMatches에 보존한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - MonthlyFunctionalLifeDomainContext 재계산
 * - EXPRESSION Effect 재계산
 * - PROCESSING Effect 재계산
 * - Annual Baseline 재계산
 * - Monthly ActivationStatus 재계산
 * - sameDirectionAccumulation 재계산
 * - counterDirectionContact 재계산
 * - qualification 재계산
 * - Monthly CrossFunctional 관계 생성
 * - LEARNING Status를 CAREER Status로 전이
 * - LEARNING_MONTHLY_CONTEXT와 Status 합성
 * - LEARNING_GROWTH_ANNUAL_FUNCTIONAL과 Status 합성
 * - CAREER_MONTHLY_CONTEXT와 Status 합성
 * - CAREER_MONTHLY_FUNCTIONAL과 Status 합성
 * - CAREER_ANNUAL_CONTEXT와 Status 합성
 * - CAREER_ANNUAL_FUNCTIONAL과 Status 합성
 * - LEARNING_DECADE_CONTEXT와 Status 합성
 * - 원국 Learning Source와 Status 합성
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
public class CareerLearningMonthlyFunctionalContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_LEARNING_MONTHLY_FUNCTIONAL_CONTEXT";

    private static final String
            LEARNING_MONTHLY_FUNCTIONAL_RULE_CODE =
            "LEARNING_GROWTH_MONTHLY_FUNCTIONAL";


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
                "CareerLearningMonthlyFunctionalContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerLearningMonthlyFunctionalContextRule은 CAREER View만 평가할 수 있습니다."
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
                            "CAREER Learning Monthly Functional Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * Selected LEARNING Functional Source
             * =================================================
             */

            List<LifeDomainRuleMatch> functionalMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            LEARNING_MONTHLY_FUNCTIONAL_RULE_CODE
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
                                "LEARNING_GROWTH_MONTHLY_FUNCTIONAL Match는 null일 수 없습니다."
                        );

                /*
                 * =================================================
                 * CAREER Category Status
                 * =================================================
                 *
                 * 원본 LEARNING Monthly Functional Status를
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
     * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
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
                        "CAREER Learning Monthly Functional sourceRuleCode가 필요합니다."
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
                "CAREER Learning Monthly Functional Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "CAREER Learning Monthly Functional Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "LEARNING_GROWTH_MONTHLY_FUNCTIONAL에서 이미 계산된 하나의 EXPRESSION·PROCESSING Monthly Functional Match를 CAREER의 독립 Functional Context로 보존했습니다."
        );

        reasons.add(
                "원본 LEARNING_GROWTH Monthly Functional Match의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 이를 CAREER의 SUPPORTIVE·PRESSURE·MIXED 방향으로 직접 전이하지 않았습니다."
        );

        reasons.add(
                "이 Category Match는 직업운 방향이 아니라 CAREER 질문에서 참조 가능한 EXPRESSION·PROCESSING 월운 Functional Context이므로 CONTEXTUAL로 유지합니다."
        );

        reasons.add(
                "LearningGrowthMonthlyFunctionalRule이 하나의 MONTHLY_FUNCTIONAL_CONTEXT 안에서 EXPRESSION과 PROCESSING의 실제 월운 접촉, Annual Baseline 활성 여부, ActivationStatus 및 qualification을 이용해 확정한 결과를 다시 계산하지 않았습니다."
        );

        reasons.add(
                "upstream에서 실제 계산된 sameDirectionAccumulation 및 counterDirectionContact는 각 Function의 Annual Functional Baseline과 현재 월운 접촉에 대한 공식 Java 조건이므로 그 계산 결과 자체는 그대로 인정합니다."
        );

        reasons.add(
                "현재 Monthly Functional Layer에는 별도 CrossFunctionalAnalysis가 없으므로 PROCESSING_REGULATES_EXPRESSION, PROCESSING_RESTRAINS_OUTPUT 같은 Function 간 관계를 이 Category Rule에서 새로 생성하지 않았습니다."
        );

        reasons.add(
                "Annual Baseline에서 비활성인 Function에 월운이 접촉한 경우 이를 Annual Function 활성으로 승격하지 않는 upstream Contract를 그대로 보존합니다."
        );

        reasons.add(
                "반대 방향 Function의 SUPPORTED 접촉을 학습 호조·시험 합격·업무 성공 또는 CAREER SUPPORTIVE로 변환하지 않았습니다."
        );

        reasons.add(
                "WEAKENED 또는 CONFLICTED 상태를 이 Category 단계에서 부담 완화나 추가 마찰로 새로 판정하지 않았습니다."
        );

        reasons.add(
                "LEARNING_MONTHLY_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "LEARNING_MONTHLY_CONTEXT와 LEARNING_GROWTH_MONTHLY_FUNCTIONAL이 같은 명리연도·월에 존재하더라도 서로의 Status를 합쳐 새로운 CAREER 방향을 만들지 않았습니다."
        );

        reasons.add(
                "같은 월운 계층에서 파생된 Context와 Functional 결과를 독립 검증 2회 또는 근거 강도 2배로 사용하지 않습니다."
        );

        reasons.add(
                "CAREER_MONTHLY_CONTEXT와 CAREER_MONTHLY_FUNCTIONAL을 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "CAREER RESPONSIBILITY Monthly Functional과 LEARNING EXPRESSION·PROCESSING Monthly Functional이 함께 존재한다는 이유만으로 새로운 Cross-Domain 관계를 생성하지 않았습니다."
        );

        reasons.add(
                "LEARNING_GROWTH_ANNUAL_FUNCTIONAL, LEARNING_ANNUAL_CONTEXT, LEARNING_DECADE_CONTEXT를 이 Match와 Category 단계에서 다시 합성하지 않았습니다."
        );

        reasons.add(
                "LEARNING_RESOURCE_OUTPUT과 LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE 같은 원국 Learning Source도 Monthly Functional Status와 합성하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 EXPRESSION, PROCESSING, ActivationStatus, Annual Baseline, 누적 조건 또는 qualification을 재판정하지 않았습니다."
        );

        reasons.add(
                "이 결과를 시험 합격·자격 취득·업무 성공·취업·승진·이직·퇴사·해고 같은 실제 사건이나 특정 직업군으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 CAREER Learning Monthly Functional 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 존재하면 각각의 LEARNING_GROWTH_MONTHLY_FUNCTIONAL 결과를 별도로 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment의 대운이 unavailable 상태라면 다른 Learning Decade 결과를 대신 넣지 않고 upstream에서 실제 생성된 Monthly Functional Match만 보존합니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 실제 좌표를 그대로 보존하며 monthIndex == 0도 유효한 값으로 취급합니다."
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
     * 원본 Functional Status는
     * provenance에 보존하되
     * CAREER 직업운 방향으로 전이하지 않는다.
     */

    private String summaryOf(
            LifeDomainRuleMatch sourceMatch
    ) {

        Objects.requireNonNull(
                sourceMatch,
                "CAREER Learning Monthly Functional Context에는 Source Match가 필요합니다."
        );

        return "현재 월운에서 Java가 계산한 EXPRESSION·PROCESSING Functional 상태를 "
                + "CAREER 질문의 독립 기능 Context로 보존합니다. "
                + "원본 LEARNING_GROWTH Monthly Functional 상태는 "
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