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
 * 월운 학습·처리·표현 Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.LEARNING_GROWTH에서 이미 계산된:
 *
 * LEARNING_MONTHLY_CONTEXT
 *
 * 결과를 CAREER 질문에서 참조할 수 있는
 * 독립적인 CURRENT_TIMELINE Context로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * LearningMonthlyContextRule은
 * 15-7 MonthlyLuckContextAnalysis에서:
 *
 * 인성 계열:
 *
 * - DIRECT_RESOURCE
 * - INDIRECT_RESOURCE
 *
 * 식상 계열:
 *
 * - EATING_GOD
 * - HURTING_OFFICER
 *
 * 관련 Monthly Context Signal을 사용한다.
 *
 * =================================================
 * 시간층 경계
 * =================================================
 *
 * upstream LearningMonthlyContextRule은:
 *
 * MonthlyLuckContextAnalysis.signalsOf(...)
 *
 * 만 사용한다.
 *
 * 이 API는 monthlySignals만 조회한다.
 *
 * 다음은 사용하지 않는다.
 *
 * MonthlyLuckContextAnalysis.allSignals()
 *
 * allSignals()에는:
 *
 * monthlySignals
 * +
 * decadeContexts.segmentSignals
 *
 * 이 포함되므로 LearningMonthlyContextRule은
 * 명시적으로 사용하지 않는다.
 *
 * 따라서:
 *
 * LEARNING_DECADE_CONTEXT
 *
 * 는 별도 Rule이 담당한다.
 *
 * =================================================
 * 이미 계산된 월운 구조
 * =================================================
 *
 * MonthlyLuckContextAnalysis에는
 * upstream Java에서 실제 계산된:
 *
 * - 월운 십성
 * - 원국 × 월운 관계
 * - 세운 × 월운 관계
 * - 대운 × 월운 관계
 * - 용신 Context
 * - 격국 Context
 * - 궁위 Context
 * - 합화·기능변화 Context
 * - 다층 관계
 *
 * 등이 monthlySignal 안에 포함될 수 있다.
 *
 * 이러한 관계가 실제 Monthly Java 계산에서
 * 명시적으로 생성된 경우
 * 그 계산 결과 자체는 유효하다.
 *
 * 이 Category Rule에서는
 * 해당 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * Upstream Match 구조
 * =================================================
 *
 * LearningMonthlyContextRule은
 * 실제 Signal 존재에 따라:
 *
 * 1.
 * 인성 Monthly Context
 *
 * 2.
 * 식상 Monthly Context
 *
 * 3.
 * 인성 + 식상 동시 관여 Context
 *
 * 를 각각 LifeDomainRuleMatch로
 * 생성할 수 있다.
 *
 * =================================================
 * 인성 + 식상 동시 관여
 * =================================================
 *
 * 세 번째 Match는
 * Category에서 두 Evidence를 보고
 * 임의로 만든 결과가 아니다.
 *
 * upstream LearningMonthlyContextRule이:
 *
 * resourceSignals가 비어 있지 않음
 * &&
 * outputSignals가 비어 있지 않음
 *
 * 을 실제 Java에서 확인하고,
 *
 * 두 Signal Group을 합쳐
 * 공식 LifeDomainSignalStatus까지
 * 계산한 결과다.
 *
 * 따라서 해당 Match 자체는 보존한다.
 *
 * 다만 이 Category Rule에서
 * 그 Match에 새로운 CAREER 관계나
 * 직업 의미를 추가하지 않는다.
 *
 * =================================================
 * Upstream Status 계산
 * =================================================
 *
 * LearningMonthlyContextRule은
 * MonthlyLuckContextSignal 상태를 이용해:
 *
 * CONFLICTED 존재
 * ->
 * MIXED
 *
 * -------------------------------------------------
 *
 * SUPPORT 계열
 * +
 * WEAKENED
 * ->
 * MIXED
 *
 * -------------------------------------------------
 *
 * WEAKENED
 * ->
 * PRESSURE
 *
 * -------------------------------------------------
 *
 * SUPPORTED
 * ->
 * SUPPORTIVE
 *
 * -------------------------------------------------
 *
 * QUALIFIED만 존재
 * ->
 * CONTEXTUAL
 *
 * 로 실제 Java에서 결정한다.
 *
 * 이 Category Rule에서는
 * 해당 상태를 다시 계산하지 않는다.
 *
 * =================================================
 * 중요한 의미 경계
 * =================================================
 *
 * LEARNING_MONTHLY_CONTEXT의 Status는:
 *
 * - 배우기
 * - 이해하기
 * - 정리하기
 * - 흡수하기
 * - 내재화하기
 * - 표현하기
 * - 실행하기
 * - 산출하기
 * - 응용하기
 *
 * 기능에 대한 월운 Context다.
 *
 * CAREER의:
 *
 * - 취업
 * - 승진
 * - 직위
 * - 직업 성공
 * - 조직 역할
 *
 * 방향을 직접 계산한 Status가 아니다.
 *
 * 따라서:
 *
 * LEARNING SUPPORTIVE
 * != CAREER SUPPORTIVE
 *
 * LEARNING PRESSURE
 * != CAREER PRESSURE
 *
 * LEARNING MIXED
 * != CAREER MIXED
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
 * 원본 LifeDomainRuleMatch의
 * LifeDomainSignalStatus는
 * sourceMatches 안에 그대로 보존한다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * LEARNING_MONTHLY_CONTEXT
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * CAREER Category Context Match 하나
 *
 * 로 보존한다.
 *
 * 여러 Source Match가 존재해도
 * 서로 합쳐 새로운 CAREER Status를
 * 만들지 않는다.
 *
 * =================================================
 * Match 간 합성 금지
 * =================================================
 *
 * 예:
 *
 * 인성 Monthly Context = SUPPORTIVE
 *
 * 식상 Monthly Context = PRESSURE
 *
 * 라고 해도:
 *
 * CAREER Learning Monthly = MIXED
 *
 * 로 다시 계산하지 않는다.
 *
 * 각 Match는:
 *
 * Category Status = CONTEXTUAL
 *
 * 로 독립 보존되고,
 * 원본 상태만 sourceMatches에 남는다.
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
 * -------------------------------------------------
 *
 * LEARNING_MONTHLY_CONTEXT
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 같은 명리연도·월에 존재할 수 있지만
 * 서로 다른 deterministic 계산이다.
 *
 * =================================================
 * Cross-Domain Status 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * CAREER_MONTHLY_CONTEXT SUPPORTIVE
 * +
 * LEARNING_MONTHLY_CONTEXT PRESSURE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * CAREER_MONTHLY_CONTEXT CONTEXTUAL
 * +
 * LEARNING_MONTHLY_CONTEXT SUPPORTIVE
 * =
 * CAREER SUPPORTIVE
 *
 * 금지.
 *
 * =================================================
 * Monthly provenance 경계
 * =================================================
 *
 * CAREER_MONTHLY_CONTEXT와
 * LEARNING_MONTHLY_CONTEXT가
 * 같은 MonthlyLuckContextAnalysis 계열에서
 * 파생될 수 있다고 해서:
 *
 * - 독립 검증 2회
 * - 근거 강도 2배
 * - 동일 월운의 별도 확증
 *
 * 으로 계산하지 않는다.
 *
 * 이 Rule은 Learning 기능 Context를
 * 별도로 보존할 뿐이다.
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
 * 에서 별도로 처리한다.
 *
 * 이 Rule에서는:
 *
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * 을 조회하거나 합성하지 않는다.
 *
 * =================================================
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL과 분리
 * =================================================
 *
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL은
 * 별도의 Functional 계산이다.
 *
 * 따라서:
 *
 * LEARNING_MONTHLY_CONTEXT
 * +
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 *
 * 을 이 Rule에서 합치지 않는다.
 *
 * Monthly Functional 결과는
 * 별도 Category Rule에서 보존한다.
 *
 * =================================================
 * Annual과 분리
 * =================================================
 *
 * LEARNING_ANNUAL_CONTEXT
 *
 * ->
 *
 * CareerLearningAnnualContextRule
 *
 * 에서 별도로 처리한다.
 *
 * MonthlyLuckContextAnalysis 내부의
 * 세운 × 월운 관계가
 * 실제 Java에서 계산되어
 * monthlySignal에 들어가 있는 경우는
 * 그 upstream 계산을 그대로 인정한다.
 *
 * 하지만 Category 단계에서:
 *
 * LEARNING_ANNUAL_CONTEXT
 * +
 * LEARNING_MONTHLY_CONTEXT
 *
 * 를 다시 결합하지 않는다.
 *
 * =================================================
 * Decade와 분리
 * =================================================
 *
 * LEARNING_DECADE_CONTEXT
 *
 * ->
 *
 * CareerLearningDecadeContextRule
 *
 * 에서 독립적으로 처리한다.
 *
 * 이 Rule은
 * LearningMonthlyContextRule과 동일하게
 * allSignals()를 사용해서
 * 대운 Signal을 다시 섞지 않는다.
 *
 * =================================================
 * 실제 Java 관계와의 구분
 * =================================================
 *
 * MonthlyLuckContextAnalysis에서
 * 실제 계산된:
 *
 * - 원국 × 월운 관계
 * - 세운 × 월운 관계
 * - 대운 × 월운 관계
 * - 용신 Context
 * - 격국 Context
 * - 합화·기능변화 Context
 * - 다층 관계
 *
 * 는 유효한 deterministic 계산이다.
 *
 * 이 Rule은 그것을 삭제하지 않는다.
 *
 * 금지하는 것은:
 *
 * 서로 다른 LifeDomainRuleMatch 또는
 * Category Match가 같이 있다는 사실만으로
 * 새로운 관계를 만드는 것이다.
 *
 * =================================================
 * 관계·인과·순서 생성 금지
 * =================================================
 *
 * 다음을 새로 만들지 않는다.
 *
 * - 이번 달 학습 상태 때문에
 *   직업운이 좋아진다
 *
 * - 표현 기능 때문에 승진한다
 *
 * - 학습 압박이 업무 부담으로 이어진다
 *
 * - 세운 Learning 상태 때문에
 *   월운 CAREER 상태가 변한다
 *
 * - CAREER 월운과 Learning 월운이
 *   같은 방향이므로 더 강하다
 *
 * - 한쪽이 다른 쪽을 보완한다
 *
 * - 한쪽이 다른 쪽을 약화한다
 *
 * - A 때문에 B
 *
 * - A가 B로 이어진다
 *
 * - 먼저 A이고 이후 B가 된다
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * LEARNING SUPPORTIVE
 * != 시험 합격
 * != 자격 취득
 * != 업무 성공
 * != 취업
 * != 승진
 *
 * LEARNING PRESSURE
 * != 시험 실패
 * != 업무 실패
 * != 퇴사
 * != 해고
 *
 * LEARNING MIXED
 * != 진로 변경
 * != 이직
 *
 * =================================================
 * 능력 확정 금지
 * =================================================
 *
 * 인성 Monthly SUPPORTIVE
 * != 공부를 잘한다
 *
 * 식상 Monthly SUPPORTIVE
 * != 표현력이 좋다
 *
 * 인성 + 식상 동시 관여
 * != 준비와 실행이 모두 뛰어나다
 * != 성과가 높다
 *
 * =================================================
 * 특정 직업 생성 금지
 * =================================================
 *
 * 인성 Monthly Context
 * != 연구직
 * != 교사
 * != 전문직
 *
 * 식상 Monthly Context
 * != 영업
 * != 창업
 * != 콘텐츠 직업
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각 Segment의 실제:
 *
 * LEARNING_MONTHLY_CONTEXT
 *
 * Match를 각각 보존한다.
 *
 * 특정 Segment 하나를
 * 대표값으로 임의 선택하지 않는다.
 *
 * =================================================
 * sajuYear / monthIndex
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
 * LifeDomainRuleMatch의:
 *
 * - summary
 * - reasons
 *
 * 을 파싱하여:
 *
 * - 인성 Match
 * - 식상 Match
 * - Combined Match
 *
 * 를 다시 판정하지 않는다.
 *
 * 세부 구조는
 * 원본 LifeDomainRuleMatch와
 * Evidence provenance에 그대로 남긴다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * LEARNING_MONTHLY_CONTEXT Match 개수를:
 *
 * - 직업 역량
 * - 전문성
 * - 업무 능력
 * - 성장 가능성
 * - 직업운 강도
 *
 * 의 점수로 사용하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - MonthlyLuckContextAnalysis 재계산
 * - Monthly Signal 재계산
 * - 인성 Signal 재계산
 * - 식상 Signal 재계산
 * - Combined Match 재계산
 * - 원국 × 월운 관계 재계산
 * - 세운 × 월운 관계 재계산
 * - 대운 × 월운 관계 재계산
 * - 용신 재계산
 * - 격국 재계산
 * - 합화·기능변화 재계산
 * - allSignals() 기반 대운 Signal 재혼합
 * - LEARNING Status를 CAREER Status로 전이
 * - CAREER_MONTHLY_CONTEXT와 Status 합성
 * - CAREER_MONTHLY_FUNCTIONAL과 Status 합성
 * - LEARNING_ANNUAL_CONTEXT와 Status 합성
 * - LEARNING_DECADE_CONTEXT와 Status 합성
 * - LEARNING_GROWTH_MONTHLY_FUNCTIONAL과 Status 합성
 * - Match 간 Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 특정 직업군 생성
 * - 실제 직업 사건 생성
 */
@Component
public class CareerLearningMonthlyContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_LEARNING_MONTHLY_CONTEXT";

    private static final String
            LEARNING_MONTHLY_CONTEXT_RULE_CODE =
            "LEARNING_MONTHLY_CONTEXT";


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
                "CareerLearningMonthlyContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerLearningMonthlyContextRule은 CAREER View만 평가할 수 있습니다."
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
                            "CAREER Learning Monthly Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * Selected LEARNING Source
             * =================================================
             */

            List<LifeDomainRuleMatch> learningMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            LEARNING_MONTHLY_CONTEXT_RULE_CODE
                    );

            if (
                    learningMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * Match 1개 -> Category Context 1개
             * =================================================
             */

            for (
                    LifeDomainRuleMatch learningMatch
                    : learningMatches
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                learningMatch,
                                "LEARNING_MONTHLY_CONTEXT Match는 null일 수 없습니다."
                        );

                /*
                 * =================================================
                 * CAREER Category Status
                 * =================================================
                 *
                 * 원본 LEARNING Status를
                 * CAREER 방향으로 전이하지 않는다.
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
     * Segment Selected LEARNING Source 조회
     * =================================================
     *
     * CAREER View 안에서:
     *
     * LifeDomain.LEARNING_GROWTH
     *
     * +
     *
     * LEARNING_MONTHLY_CONTEXT
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
                        "CAREER Learning Monthly Context sourceRuleCode가 필요합니다."
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
             * 정확한 selected RuleCode만
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
                "CAREER Learning Monthly Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "CAREER Learning Monthly Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "LEARNING_MONTHLY_CONTEXT에서 이미 계산된 하나의 LEARNING_GROWTH LifeDomain Match를 CAREER의 월운 학습·처리·표현 Context로 독립 보존했습니다."
        );

        reasons.add(
                "원본 LEARNING_GROWTH Match의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 이를 CAREER의 SUPPORTIVE·PRESSURE·MIXED 방향으로 직접 전이하지 않았습니다."
        );

        reasons.add(
                "이 Category Match는 직업운 방향이 아니라 CAREER 질문에서 참조 가능한 월운 학습·내재화·표현·산출 기능 Context이므로 CONTEXTUAL로 유지합니다."
        );

        reasons.add(
                "LearningMonthlyContextRule이 MonthlyLuckContextAnalysis.signalsOf(...)를 이용해 monthlySignals만 평가한 공식 결과를 그대로 사용하며 allSignals()를 이용한 대운 Signal 재혼합을 하지 않았습니다."
        );

        reasons.add(
                "LearningMonthlyContextRule이 동일 월운에서 인성 Signal과 식상 Signal의 실제 동시 존재를 확인하여 Combined Match를 생성한 경우 해당 upstream Java 계산 자체는 그대로 보존합니다."
        );

        reasons.add(
                "MonthlyLuckContextAnalysis에서 실제 계산된 원국×월운, 세운×월운, 가능한 대운×월운, 용신, 격국, 궁위, 합화·기능변화 및 다층 관계가 source Match에 반영되어 있다면 해당 upstream 계산 자체는 그대로 인정합니다."
        );

        reasons.add(
                "CAREER_MONTHLY_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "CAREER_MONTHLY_CONTEXT와 LEARNING_MONTHLY_CONTEXT가 같은 명리연도·월에 존재하더라도 서로의 Status를 결합하여 새로운 CAREER 방향을 생성하지 않았습니다."
        );

        reasons.add(
                "같은 Monthly Context 계열에서 파생된 결과의 존재를 CAREER 방향에 대한 독립 검증 2회 또는 근거 강도 2배로 사용하지 않습니다."
        );

        reasons.add(
                "CAREER_MONTHLY_FUNCTIONAL과 LEARNING_GROWTH_MONTHLY_FUNCTIONAL을 이 Match에 합성하지 않았습니다."
        );

        reasons.add(
                "LEARNING_ANNUAL_CONTEXT와 LEARNING_DECADE_CONTEXT를 Category 단계에서 이 Monthly Context와 다시 합성하지 않았습니다."
        );

        reasons.add(
                "CAREER의 원국 관살 구조, Decade Context, Annual Context, Annual Functional과도 이 Match를 합성하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 인성·식상·Combined subtype을 재판정하지 않았습니다."
        );

        reasons.add(
                "이 결과를 시험 합격·자격 취득·업무 성공·취업·승진·이직·퇴사 같은 실제 사건이나 특정 직업군으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 CAREER Learning Monthly Context 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 존재하면 각 LEARNING_MONTHLY_CONTEXT 결과를 별도로 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment에서 대운 Context를 사용할 수 없더라도 실제 MonthlyLuckContextAnalysis.monthlySignals로 확정된 LEARNING_MONTHLY_CONTEXT Match만 보존하며 별도의 대운 결과를 대신 넣지 않습니다."
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
     * 원본 Status는 provenance로 보존하지만
     * CAREER 방향으로 전이하지 않는다.
     */

    private String summaryOf(
            LifeDomainRuleMatch sourceMatch
    ) {

        Objects.requireNonNull(
                sourceMatch,
                "CAREER Learning Monthly Context에는 Source Match가 필요합니다."
        );

        return "현재 월운에서 계산된 하나의 학습·내재화·표현·산출 기능 Context를 "
                + "CAREER 질문의 독립 정보로 보존합니다. "
                + "원본 LEARNING_GROWTH 상태는 "
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