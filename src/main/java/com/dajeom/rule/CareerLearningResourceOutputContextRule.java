package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
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
 * 원국 학습·처리·표현 기능 구조 Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.LEARNING_GROWTH에서 이미 계산된:
 *
 * LEARNING_RESOURCE_OUTPUT
 *
 * 결과를 CAREER 질문에서 사용할 수 있는
 * 독립적인 기능 구조 Context로 보존한다.
 *
 * =================================================
 * 매우 중요한 의미 경계
 * =================================================
 *
 * 이 Rule은:
 *
 * LEARNING_GROWTH의 길흉 방향을
 * CAREER의 길흉 방향으로 변환하는 Rule이 아니다.
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
 * 이유:
 *
 * LEARNING_RESOURCE_OUTPUT이 계산하는 것은
 * 직접적인:
 *
 * - 취업운
 * - 승진운
 * - 직위
 * - 조직 역할 결과
 *
 * 가 아니라:
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
 * 기능의 원국 구조이기 때문이다.
 *
 * =================================================
 * Source Status 보존
 * =================================================
 *
 * 원본 LifeDomainRuleMatch에는:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * 중 upstream Java가 계산한
 * 실제 LifeDomainSignalStatus가 그대로 존재한다.
 *
 * 해당 Status를 삭제하거나 변경하지 않는다.
 *
 * sourceMatches에 원본 Match를 그대로 보존한다.
 *
 * 단:
 *
 * 그 Status를 CAREER Category Status로
 * 직접 전이하지 않는다.
 *
 * =================================================
 * Upstream 계산
 * =================================================
 *
 * LearningResourceOutputRule은
 * TenGodCompositeAnalysis와
 * DayMasterStrengthAnalysis를 사용하여
 * 이미 다음 구조를 계산한다.
 *
 * 인성:
 *
 * - RESOURCE_DEVELOPED
 * - DIRECT_RESOURCE_CENTERED
 * - INDIRECT_RESOURCE_CENTERED
 * - MIXED_RESOURCE
 * - WEAK_DAY_MASTER_RESOURCE_SUPPORT
 * - STRONG_DAY_MASTER_EXCESSIVE_RESOURCE
 * - OUTPUT_CONSTRAINT_CONTEXT
 * - OFFICER_GENERATION_CONTEXT
 * - WEALTH_PRESSURE_CONTEXT
 *
 * 식상:
 *
 * - OUTPUT_DEVELOPED
 * - EATING_GOD_CENTERED
 * - HURTING_OFFICER_CENTERED
 * - MIXED_OUTPUT
 * - WEAK_DAY_MASTER_EXCESSIVE_DRAIN
 * - RESOURCE_CONSTRAINED_OUTPUT
 *
 * 등을 deterministic하게 계산한다.
 *
 * 이 Category Rule에서는
 * 해당 계산을 다시 하지 않는다.
 *
 * =================================================
 * 실제 Java 관계 계산 보존
 * =================================================
 *
 * upstream에서 이미 실제 계산된:
 *
 * - 인성이 식상을 제약하는 Context
 * - 식상이 인성을 제약하는 Context
 * - 관살이 인성과 연결되는 Context
 * - 재성이 인성을 압박하는 Context
 *
 * 자체는 유효한 Java 계산이다.
 *
 * 관계형 풀이 금지는
 * 이러한 upstream deterministic 관계를
 * 삭제한다는 뜻이 아니다.
 *
 * 이 Rule은 그 결과를 그대로 보존한다.
 *
 * =================================================
 * Category 단계에서 하지 않는 것
 * =================================================
 *
 * 서로 다른 LEARNING_RESOURCE_OUTPUT Match를 보고:
 *
 * - 학습 능력이 좋으므로 직업에 유리하다
 * - 표현 기능이 강해서 승진한다
 * - 인성 Pressure 때문에 직장생활이 어렵다
 * - 식상 Pressure 때문에 직업운이 나쁘다
 * - 학습과 표현이 함께 좋아서 직업 성공 가능성이 높다
 *
 * 같은 CAREER 의미를 생성하지 않는다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 보존한다.
 *
 * 예:
 *
 * RESOURCE_DEVELOPED 계열 Match
 *
 * ->
 *
 * CAREER Learning Context Match 하나
 *
 * -------------------------------------------------
 *
 * OUTPUT_DEVELOPED 계열 Match
 *
 * ->
 *
 * CAREER Learning Context Match 하나
 *
 * -------------------------------------------------
 *
 * RESOURCE_CONSTRAINED_OUTPUT 계열 Match
 *
 * ->
 *
 * CAREER Learning Context Match 하나
 *
 * -------------------------------------------------
 *
 * 각각 독립적으로 유지한다.
 *
 * 여러 Match를 하나로 합쳐:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 *
 * 같은 새로운 CAREER Status를 만들지 않는다.
 *
 * =================================================
 * Structured subtype 제한
 * =================================================
 *
 * 현재 LifeDomainRuleMatch에는:
 *
 * RESOURCE_DEVELOPED
 * OUTPUT_DEVELOPED
 * MIXED_RESOURCE
 * MIXED_OUTPUT
 * RESOURCE_CONSTRAINED_OUTPUT
 * ...
 *
 * 같은 세부 Pattern Type을 나타내는
 * 별도의 구조화된 subtype 필드가 없다.
 *
 * 따라서 이 Category Rule에서는:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱하여
 * Pattern Type을 다시 판정하지 않는다.
 *
 * 원본 LifeDomainRuleMatch를
 * sourceMatches에 그대로 보존한다.
 *
 * =================================================
 * CAREER 원국 계산과의 분리
 * =================================================
 *
 * 다음 CAREER Source와
 * 이 Rule의 Match를 합성하지 않는다.
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * CAREER_OFFICER_STRUCTURE
 * CAREER_CROSS_VALIDATED_PATTERN
 * CAREER_ENVIRONMENTAL_PALACE_CONTEXT
 *
 * 따라서:
 *
 * 관살 구조
 * +
 * 학습 구조
 *
 * 를 보고 새로운 직업 상태를 만들지 않는다.
 *
 * =================================================
 * 특정 직업 생성 금지
 * =================================================
 *
 * 인성 발달
 * != 교사
 * != 연구원
 * != 전문직
 *
 * 정인 중심
 * != 공무원
 *
 * 편인 중심
 * != 창의직
 *
 * 식상 발달
 * != 영업
 * != 콘텐츠 직업
 * != 창업
 *
 * 식신 중심
 * != 예술가
 *
 * 상관 중심
 * != 프리랜서
 *
 * 같은 직업군 대응을 생성하지 않는다.
 *
 * =================================================
 * 직업 사건 생성 금지
 * =================================================
 *
 * 학습 Supportive
 * != 취업 성공
 *
 * 표현 Supportive
 * != 승진
 *
 * 학습 Pressure
 * != 시험 실패
 * != 직업 실패
 *
 * 표현 Pressure
 * != 업무 실패
 * != 해고
 *
 * MIXED
 * != 이직
 *
 * =================================================
 * 능력 확정 금지
 * =================================================
 *
 * RESOURCE_DEVELOPED
 * != 공부를 잘한다
 *
 * OUTPUT_DEVELOPED
 * != 말을 잘한다
 *
 * DIRECT_RESOURCE_CENTERED
 * != 암기력이 좋다
 *
 * INDIRECT_RESOURCE_CENTERED
 * != 천재적이다
 *
 * EATING_GOD_CENTERED
 * != 실무능력이 뛰어나다
 *
 * HURTING_OFFICER_CENTERED
 * != 창의력이 뛰어나다
 *
 * upstream이 계산한 것은
 * 기능 구조이지 실제 능력 성과가 아니다.
 *
 * =================================================
 * CAREER에서의 공식 역할
 * =================================================
 *
 * 이 Match는 CAREER 질문에서:
 *
 * "원국의 학습·처리·표현·산출 기능 구조가
 * 어떤 Context를 가지고 있는가"
 *
 * 를 보존하기 위한 것이다.
 *
 * 직업 방향성 자체는:
 *
 * - CAREER 주격 구조
 * - CAREER 관살 구조
 * - CAREER 실제 Pattern
 * - CAREER Timeline
 *
 * 등 별도의 CAREER 계산이 담당한다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음을 생성하지 않는다.
 *
 * - 배우면 직업으로 이어진다
 * - 표현력이 직업 성공으로 이어진다
 * - 학습 압박 때문에 직업 부담이 생긴다
 * - 직업 책임 때문에 학습이 필요해진다
 * - 먼저 학습하고 이후 성과가 난다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 작동한다
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * LEARNING_RESOURCE_OUTPUT Match가 많다고:
 *
 * - 직업 역량이 높다
 * - 직업 적성이 많다
 * - 성공 가능성이 높다
 * - 전문성이 높다
 *
 * 로 계산하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 인성 재계산
 * - 식상 재계산
 * - 신강신약 재계산
 * - 인성/식상 관계 재계산
 * - 관살 생인 재계산
 * - 재극인 재계산
 * - 다른 CAREER Source 조회
 * - 다른 CAREER Source와 Status 합성
 * - LEARNING Status를 CAREER Status로 전이
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
public class CareerLearningResourceOutputContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_LEARNING_RESOURCE_OUTPUT_CONTEXT";

    private static final String
            LEARNING_RESOURCE_OUTPUT_RULE_CODE =
            "LEARNING_RESOURCE_OUTPUT";


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
                "CareerLearningResourceOutputContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerLearningResourceOutputContextRule은 CAREER View만 평가할 수 있습니다."
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
         * Selected LEARNING Source
         * =================================================
         *
         * ConsultationCategoryEvidencePolicyRegistry에서
         * CAREER Natal selected Rule로
         * LEARNING_RESOURCE_OUTPUT만 명시적으로
         * Routing된 상태를 사용한다.
         */

        List<LifeDomainRuleMatch> learningMatches =
                findNatalMatches(
                        view,
                        LEARNING_RESOURCE_OUTPUT_RULE_CODE
                );

        if (
                learningMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Match 1개 -> Category Context 1개
         * =================================================
         */

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch learningMatch
                : learningMatches
        ) {

            LifeDomainRuleMatch normalizedMatch =
                    Objects.requireNonNull(
                            learningMatch,
                            "LEARNING_RESOURCE_OUTPUT Match는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * CAREER Status
             * =================================================
             *
             * 매우 중요:
             *
             * source Match의 LifeDomainSignalStatus를
             * CAREER Category Status로 전이하지 않는다.
             *
             * 이 계산은 직업운 방향이 아니라
             * CAREER에서 사용할 수 있는
             * 학습·처리·표현 기능 Context다.
             */

            ConsultationCategoryDeterministicStatus categoryStatus =
                    ConsultationCategoryDeterministicStatus
                            .CONTEXTUAL;

            /*
             * =================================================
             * Reasons
             * =================================================
             *
             * 최종 사용자 Story가 아니라
             * deterministic provenance 기록이다.
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "LEARNING_RESOURCE_OUTPUT에서 이미 계산된 하나의 LifeDomain Match를 CAREER의 학습·처리·표현 기능 Context로 독립 보존했습니다."
            );

            reasons.add(
                    "원본 LEARNING_GROWTH Match의 공식 LifeDomainSignalStatus는 sourceMatches 안에 그대로 보존하지만 이를 CAREER의 SUPPORTIVE·PRESSURE·MIXED 방향으로 직접 전이하지 않았습니다."
            );

            reasons.add(
                    "이 Category Match의 Status는 직업운 방향이 아니라 CAREER 질문에서 참조 가능한 기능 구조임을 명확히 하기 위해 CONTEXTUAL로 유지합니다."
            );

            reasons.add(
                    "LearningResourceOutputRule이 TenGodCompositeAnalysis와 DayMasterStrengthAnalysis를 이용해 실제 계산한 인성·식상 Composite 구조와 그 deterministic 상태를 다시 계산하지 않았습니다."
            );

            reasons.add(
                    "Upstream Java에서 실제 계산된 인성-식상 제약, 관살-인성 Context, 재성-인성 Pressure 같은 구조가 원본 Match에 존재하면 해당 계산 자체는 그대로 보존합니다."
            );

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE, CAREER_OFFICER_STRUCTURE, CAREER_CROSS_VALIDATED_PATTERN 같은 별도 CAREER Source를 이 Match에 결합하지 않았습니다."
            );

            reasons.add(
                    "LEARNING_RESOURCE_OUTPUT Match가 여러 개 존재하더라도 서로 합쳐 새로운 CAREER Status나 직업 적성 점수를 만들지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 파싱하여 인성·식상의 세부 Pattern subtype을 재구성하지 않았습니다."
            );

            reasons.add(
                    "이 결과를 취업·승진·합격·이직·퇴사·직업 성공 같은 실제 사건이나 교사·연구원·전문직·창업 등의 특정 직업군으로 변환하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.CAREER,

                            ConsultationCategoryDeterministicLayer
                                    .NATAL,

                            RULE_CODE,

                            categoryStatus,

                            null,

                            null,

                            List.of(),

                            summaryOf(
                                    normalizedMatch
                            ),

                            List.of(
                                    normalizedMatch
                            ),

                            List.copyOf(
                                    reasons
                            )
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Natal Selected LEARNING Source 조회
     * =================================================
     *
     * 여기서는 CAREER Domain이 아니라:
     *
     * LifeDomain.LEARNING_GROWTH
     *
     * 를 조회한다.
     *
     * ConsultationCategoryEvidencePolicyRegistry가
     * 명시적으로 CAREER에 Routing한
     * selected RuleCode만 사용한다.
     */

    private List<LifeDomainRuleMatch>
    findNatalMatches(

            ConsultationCategoryEvidenceView view,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                view,
                "CAREER Evidence View가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "CAREER Learning Resource Output sourceRuleCode가 필요합니다."
                );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : view.natalMatches()
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "CAREER Natal Match는 null일 수 없습니다."
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
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * source status를 CAREER status로 전환하지 않는다.
     */

    private String summaryOf(
            LifeDomainRuleMatch sourceMatch
    ) {

        Objects.requireNonNull(
                sourceMatch,
                "CAREER Learning Context에는 Source Match가 필요합니다."
        );

        return "원국에서 계산된 하나의 학습·처리·표현·산출 기능 구조를 "
                + "CAREER 질문의 독립 Context로 보존합니다. "
                + "원본 LEARNING_GROWTH 상태는 "
                + sourceMatch.status()
                + "이며 이를 CAREER의 직업운 방향으로 직접 변환하지 않습니다.";
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