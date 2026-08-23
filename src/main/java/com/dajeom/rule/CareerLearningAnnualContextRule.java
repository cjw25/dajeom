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
 * 세운 학습·처리·표현 Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.LEARNING_GROWTH에서 이미 계산된:
 *
 * LEARNING_ANNUAL_CONTEXT
 *
 * 결과를 CAREER 질문에서 참조할 수 있는
 * 독립적인 CURRENT_TIMELINE Context로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * LearningAnnualContextRule은
 * 14-6 AnnualLuckContextAnalysis에서:
 *
 * 인성:
 *
 * - DIRECT_RESOURCE
 * - INDIRECT_RESOURCE
 *
 * 식상:
 *
 * - EATING_GOD
 * - HURTING_OFFICER
 *
 * 관련 Annual Context Signal을 사용한다.
 *
 * =================================================
 * Upstream에서 이미 계산된 것
 * =================================================
 *
 * AnnualLuckContextAnalysis에는
 * upstream Java에서 이미 계산된:
 *
 * - 세운 십성
 * - 원국 × 세운 관계
 * - 격국 Context
 * - 용신 Context
 * - 궁위 Context
 * - 관계 Context
 * - 합화 Context
 * - 가능한 경우 대운 Segment Context
 *
 * 등이 반영될 수 있다.
 *
 * LearningAnnualContextRule은
 * 해당 결과를 LEARNING_GROWTH Domain의
 * 세운 Context로 변환한다.
 *
 * 이 Category Rule에서는
 * 위 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * Upstream Match 구조
 * =================================================
 *
 * LearningAnnualContextRule은
 * 실제 Signal 존재에 따라:
 *
 * 1.
 * 인성 Annual Context
 *
 * 2.
 * 식상 Annual Context
 *
 * 3.
 * 인성 + 식상 동시 작동 Context
 *
 * 를 각각 LifeDomainRuleMatch로
 * 생성할 수 있다.
 *
 * =================================================
 * 인성 + 식상 동시 작동
 * =================================================
 *
 * 세 번째 Match는
 * Category 단계에서 임의로 만든 관계가 아니다.
 *
 * LearningAnnualContextRule이
 * 실제 같은 세운에서:
 *
 * resourceSignals 존재
 * &&
 * outputSignals 존재
 *
 * 를 확인한 뒤
 * 두 Signal Group을 Java에서 직접 계산한 결과다.
 *
 * 따라서 해당 Match 자체는
 * 유효한 deterministic 결과로 보존한다.
 *
 * 다만 이 Category Rule에서
 * 그 결과에 새로운 관계·인과·직업 의미를
 * 추가하지 않는다.
 *
 * =================================================
 * 중요한 의미 경계
 * =================================================
 *
 * LEARNING_ANNUAL_CONTEXT의 Status는:
 *
 * - 학습
 * - 흡수
 * - 정리
 * - 내재화
 * - 표현
 * - 산출
 * - 응용
 * - 외부 발현
 *
 * 기능의 세운 Context 방향이다.
 *
 * CAREER의:
 *
 * - 직업운
 * - 취업운
 * - 승진운
 * - 직위
 * - 조직 역할
 *
 * 방향을 직접 계산한 결과가 아니다.
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
 * LEARNING_ANNUAL_CONTEXT
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * CAREER Category Context Match 하나
 *
 * 로 보존한다.
 *
 * 예:
 *
 * 인성 Annual Match
 * ->
 * CAREER Learning Annual Context A
 *
 * 식상 Annual Match
 * ->
 * CAREER Learning Annual Context B
 *
 * 인성 + 식상 Combined Match
 * ->
 * CAREER Learning Annual Context C
 *
 * 각각 독립적으로 유지한다.
 *
 * =================================================
 * Match 간 합성 금지
 * =================================================
 *
 * 예:
 *
 * 인성 Annual Context = SUPPORTIVE
 *
 * 식상 Annual Context = PRESSURE
 *
 * 라고 해도:
 *
 * CAREER Learning Annual = MIXED
 *
 * 같은 새로운 Category Status를 만들지 않는다.
 *
 * 각 Match의 원본 상태는
 * sourceMatches에서 그대로 보존하고,
 * CAREER Category Status는 각각
 * CONTEXTUAL로 유지한다.
 *
 * =================================================
 * CAREER_ANNUAL_CONTEXT와 분리
 * =================================================
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * ->
 *
 * CareerCurrentStructuralFlowRule
 *
 * -------------------------------------------------
 *
 * LEARNING_ANNUAL_CONTEXT
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 두 결과는 같은 명리연도에 존재할 수 있지만
 * 서로 다른 deterministic 계산이다.
 *
 * =================================================
 * Cross-Domain Status 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * CAREER_ANNUAL_CONTEXT SUPPORTIVE
 * +
 * LEARNING_ANNUAL_CONTEXT PRESSURE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * CAREER_ANNUAL_CONTEXT CONTEXTUAL
 * +
 * LEARNING_ANNUAL_CONTEXT SUPPORTIVE
 * =
 * CAREER SUPPORTIVE
 *
 * 금지.
 *
 * =================================================
 * 같은 Annual provenance 경계
 * =================================================
 *
 * CAREER_ANNUAL_CONTEXT와
 * LEARNING_ANNUAL_CONTEXT가
 * 같은 명리연도의 Annual Context에서
 * 파생되었다고 해서:
 *
 * - 독립 검증 2개
 * - 근거 강도 2배
 * - 동일 사실의 두 번 확인
 *
 * 으로 계산하지 않는다.
 *
 * 이 Rule은 Learning Context를
 * 별도 정보로 보존할 뿐
 * CAREER 방향의 추가 독립 검증으로
 * 승격하지 않는다.
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
 * 이 별도로 담당한다.
 *
 * 이 Rule에서는:
 *
 * CAREER_ANNUAL_FUNCTIONAL
 *
 * 을 조회하거나
 * LEARNING_ANNUAL_CONTEXT와 합성하지 않는다.
 *
 * =================================================
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL과 분리
 * =================================================
 *
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL은
 * 별도의 Functional 계산이다.
 *
 * 이 Rule에서는:
 *
 * LEARNING_ANNUAL_CONTEXT
 * +
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 *
 * 을 결합하지 않는다.
 *
 * Functional 결과는
 * 별도의 CAREER Context Rule에서
 * 독립적으로 보존한다.
 *
 * =================================================
 * 대운과의 분리
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
 * LearningAnnualContextRule upstream 내부에서
 * 가능한 경우 정확한 대운 Segment Context가
 * 이미 반영될 수 있는 것은
 * 기존 Annual Java 계산 Contract다.
 *
 * 하지만 Category 단계에서 다시:
 *
 * LEARNING_DECADE_CONTEXT
 * +
 * LEARNING_ANNUAL_CONTEXT
 *
 * 를 결합하지 않는다.
 *
 * =================================================
 * 월운과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * LEARNING_MONTHLY_CONTEXT
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 *
 * 을 사용하지 않는다.
 *
 * 월운 결과는 별도의
 * deterministic Rule로 처리한다.
 *
 * =================================================
 * 실제 Java 관계와의 구분
 * =================================================
 *
 * AnnualLuckContextAnalysis에서 실제 계산된:
 *
 * - 원국 × 세운 관계
 * - 격국 Context
 * - 용신 Context
 * - 합화 Context
 * - 가능한 대운 Segment Context
 *
 * 는 유효한 deterministic 계산이다.
 *
 * 이 Category Rule은
 * 해당 upstream 계산을 삭제하지 않는다.
 *
 * 금지하는 것은:
 *
 * 서로 다른 Category Evidence가
 * 동시에 존재한다는 사실만으로
 * 새로운 관계를 만드는 것이다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음 의미를 새로 생성하지 않는다.
 *
 * - 학습 세운 때문에 직업운이 좋아진다
 *
 * - 표현 세운 때문에 승진한다
 *
 * - 인성 세운이 직업 책임을 보완한다
 *
 * - 식상 세운이 CAREER 관살을 약화한다
 *
 * - CAREER 세운과 LEARNING 세운이
 *   같은 방향이라 더 강하다
 *
 * - 학습 상태가 직업 결과로 이어진다
 *
 * - 먼저 학습하고 이후 성과가 난다
 *
 * - A 때문에 B
 *
 * - A가 B로 이어진다
 *
 * - A와 B가 함께 작동한다
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * LEARNING SUPPORTIVE
 * != 시험 합격
 * != 자격 취득
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
 * != 이직
 * != 진로 변경
 *
 * =================================================
 * 성과 확정 금지
 * =================================================
 *
 * 인성 Annual SUPPORTIVE
 * != 공부를 잘한다
 *
 * 식상 Annual SUPPORTIVE
 * != 업무 성과가 높다
 *
 * 인성 + 식상 동시 작동
 * != 학습과 실행이 모두 뛰어나다
 * != 전문성이 높아진다
 *
 * =================================================
 * 특정 직업 생성 금지
 * =================================================
 *
 * 인성 Annual Context
 * != 연구직
 * != 교사
 * != 전문직
 *
 * 식상 Annual Context
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
 * LEARNING_ANNUAL_CONTEXT
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
 * monthIndex가 존재한다고 해서
 * Annual Context를 Monthly Context로
 * 재해석하지 않는다.
 *
 * monthIndex == 0도
 * 기존 Timeline의 유효한 값이다.
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
 * 정확한 세부 계산은
 * 원본 LifeDomainRuleMatch와
 * Evidence provenance에 그대로 남긴다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * LEARNING_ANNUAL_CONTEXT Match 개수를:
 *
 * - 직업 역량
 * - 전문성
 * - 성장 가능성
 * - 직업운 강도
 * - 성공 가능성
 *
 * 의 점수로 사용하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - AnnualLuckContextAnalysis 재계산
 * - 인성 Signal 재계산
 * - 식상 Signal 재계산
 * - Combined Match 재계산
 * - 원국 × 세운 관계 재계산
 * - 격국 재계산
 * - 용신 재계산
 * - 합화 재계산
 * - LEARNING Status를 CAREER Status로 전이
 * - CAREER_ANNUAL_CONTEXT와 Status 합성
 * - CAREER_ANNUAL_FUNCTIONAL과 Status 합성
 * - LEARNING_DECADE_CONTEXT와 Status 합성
 * - LEARNING_MONTHLY_CONTEXT와 Status 합성
 * - LEARNING_GROWTH_ANNUAL_FUNCTIONAL과 Status 합성
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
public class CareerLearningAnnualContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_LEARNING_ANNUAL_CONTEXT";

    private static final String
            LEARNING_ANNUAL_CONTEXT_RULE_CODE =
            "LEARNING_ANNUAL_CONTEXT";


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
                "CareerLearningAnnualContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerLearningAnnualContextRule은 CAREER View만 평가할 수 있습니다."
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
                            "CAREER Learning Annual Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * Selected LEARNING Source
             * =================================================
             */

            List<LifeDomainRuleMatch> learningMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            LEARNING_ANNUAL_CONTEXT_RULE_CODE
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
                                "LEARNING_ANNUAL_CONTEXT Match는 null일 수 없습니다."
                        );

                /*
                 * =================================================
                 * CAREER Category Status
                 * =================================================
                 *
                 * 원본 LEARNING Status는
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
     * LEARNING_ANNUAL_CONTEXT
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
                        "CAREER Learning Annual Context sourceRuleCode가 필요합니다."
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
                "CAREER Learning Annual Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "CAREER Learning Annual Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "LEARNING_ANNUAL_CONTEXT에서 이미 계산된 하나의 LEARNING_GROWTH LifeDomain Match를 CAREER의 세운 학습·처리·표현 Context로 독립 보존했습니다."
        );

        reasons.add(
                "원본 LEARNING_GROWTH Match의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 이를 CAREER의 SUPPORTIVE·PRESSURE·MIXED 방향으로 직접 전이하지 않았습니다."
        );

        reasons.add(
                "이 Category Match는 직업운 방향이 아니라 CAREER 질문에서 참조 가능한 세운 학습·내재화·표현·산출 기능 Context이므로 CONTEXTUAL로 유지합니다."
        );

        reasons.add(
                "LearningAnnualContextRule이 AnnualLuckContextAnalysis의 인성·식상 관련 Signal을 이용해 이미 계산한 deterministic 상태를 다시 계산하지 않았습니다."
        );

        reasons.add(
                "LearningAnnualContextRule이 동일 세운에서 인성 Signal과 식상 Signal의 실제 동시 존재를 확인하여 Combined Match를 생성한 경우 해당 upstream Java 계산 자체는 그대로 보존합니다."
        );

        reasons.add(
                "CAREER_ANNUAL_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "CAREER_ANNUAL_CONTEXT와 LEARNING_ANNUAL_CONTEXT가 같은 명리연도에 존재하더라도 서로의 Status를 결합하여 새로운 CAREER 방향을 생성하지 않았습니다."
        );

        reasons.add(
                "CAREER_ANNUAL_CONTEXT와 LEARNING_ANNUAL_CONTEXT를 CAREER 방향의 독립 검증 2개 또는 근거 강도 2배로 취급하지 않습니다."
        );

        reasons.add(
                "CAREER_ANNUAL_FUNCTIONAL과 LEARNING_GROWTH_ANNUAL_FUNCTIONAL을 이 Match에 합성하지 않았습니다."
        );

        reasons.add(
                "LEARNING_DECADE_CONTEXT, LEARNING_MONTHLY_CONTEXT, LEARNING_GROWTH_MONTHLY_FUNCTIONAL을 이 Match에 합성하지 않았습니다."
        );

        reasons.add(
                "CAREER의 원국 관살 구조, Decade Context, Monthly Context, Monthly Functional과도 이 Match를 합성하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 인성·식상·Combined subtype을 재판정하지 않았습니다."
        );

        reasons.add(
                "이 결과를 시험 합격·자격 취득·취업·승진·이직·퇴사 같은 실제 사건이나 특정 직업군으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 CAREER Learning Annual Context 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 존재하면 각 LEARNING_ANNUAL_CONTEXT 결과를 별도로 보존합니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 monthIndex 존재를 이유로 Annual Learning Context를 Monthly Learning Context로 재해석하지 않습니다."
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
                "CAREER Learning Annual Context에는 Source Match가 필요합니다."
        );

        return "현재 명리연도에서 계산된 하나의 학습·내재화·표현·산출 기능 Context를 "
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