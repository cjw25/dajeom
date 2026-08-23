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
 * 원국 학습·표현 주격 구조 Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.LEARNING_GROWTH에서 이미 계산된:
 *
 * LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE
 *
 * 결과를 CAREER 질문에서 사용할 수 있는
 * 독립적인 원국 구조 Context로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * LearningPrimaryResourceOutputStructureRule은
 * 5차 StructureAnalysis를 사용하여:
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
 * 중 하나가 실제 원국의
 * 주격 일반격으로 선택되었는지를 계산한다.
 *
 * 또한 해당 주격의:
 *
 * - 성립
 * - 균형 부담
 * - 손상
 * - 강한 손상
 * - 상대적 순도
 * - 높은 혼잡
 * - 보조격 존재
 * - 특수격 후보
 * - 추가 검토 필요 여부
 * - completeChart
 *
 * 를 Java에서 직접 계산한다.
 *
 * 이 Category Rule에서는
 * 위 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * 중요한 의미 경계
 * =================================================
 *
 * LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE의
 * LifeDomainSignalStatus는:
 *
 * 학습·표현 중심축의
 * 구조 성립 상태다.
 *
 * CAREER의:
 *
 * - 취업 방향
 * - 승진 방향
 * - 직위 방향
 * - 조직 역할 방향
 * - 직업 성공 방향
 *
 * 을 직접 계산한 Status가 아니다.
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
 * 원본 LEARNING_GROWTH Status는
 * sourceMatches 내부에 그대로 보존한다.
 *
 * =================================================
 * 이 Rule이 CAREER에서 보존하는 것
 * =================================================
 *
 * 인성 또는 식상 계열이:
 *
 * "실제 원국의 주격 중심축으로
 * 선택되어 있는가"
 *
 * 라는 Structure Context다.
 *
 * -------------------------------------------------
 *
 * 인성 주격:
 *
 * 학습·이해·정리·축적 기능이
 * 원국 중심 구조에 관여한다는 Context.
 *
 * -------------------------------------------------
 *
 * 식상 주격:
 *
 * 표현·실행·산출 기능이
 * 원국 중심 구조에 관여한다는 Context.
 *
 * =================================================
 * CareerLearningResourceOutputContextRule과 차이
 * =================================================
 *
 * CareerLearningResourceOutputContextRule:
 *
 * LEARNING_RESOURCE_OUTPUT
 *
 * ->
 *
 * 실제 인성/식상 Composite,
 * 발달도,
 * 일간 수용력,
 * 인성·식상 관련 구조 Context.
 *
 * -------------------------------------------------
 *
 * 이 Rule:
 *
 * LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE
 *
 * ->
 *
 * 5차 격국에서
 * 인성/식상 계열이 실제 주격으로
 * 선택되어 있는지와
 * 그 주격 구조 상태.
 *
 * -------------------------------------------------
 *
 * 두 결과는 서로 독립이다.
 *
 * =================================================
 * Cross-Source 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * LEARNING_RESOURCE_OUTPUT
 * +
 * LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE
 * =
 * 새로운 CAREER Status
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * +
 * LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE
 * =
 * 새로운 CAREER Status
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * CAREER_OFFICER_STRUCTURE
 * +
 * LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE
 * =
 * 새로운 CAREER Status
 *
 * 금지.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음 의미를 새로 생성하지 않는다.
 *
 * - 인성격이기 때문에 직업에 유리하다
 *
 * - 식상격이기 때문에 직업 성공 가능성이 높다
 *
 * - 학습 중심축 때문에 관살 구조가 안정된다
 *
 * - 표현 중심축이 관살을 강화한다
 *
 * - 인성 주격이 직업 책임으로 이어진다
 *
 * - 식상 주격이 창업으로 이어진다
 *
 * - A 때문에 B
 *
 * - A가 B로 이어진다
 *
 * - A와 B가 함께 작동한다
 *
 * - 먼저 A이고 이후 B가 된다
 *
 * =================================================
 * 실제 Java 관계 계산과의 구분
 * =================================================
 *
 * StructureAnalysis가 실제 Java에서
 * 계산한:
 *
 * - 주격
 * - 성립
 * - 부담
 * - 손상
 * - 혼잡
 * - 보조격
 * - 특수격 후보
 *
 * 자체는 유효한 deterministic 계산이다.
 *
 * 이 Rule은 해당 계산을 삭제하거나
 * 다시 해석하지 않는다.
 *
 * =================================================
 * 특정 직업 생성 금지
 * =================================================
 *
 * DIRECT_RESOURCE
 * != 교사
 * != 공무원
 * != 연구원
 *
 * INDIRECT_RESOURCE
 * != 연구직
 * != 상담직
 * != 창의직
 *
 * EATING_GOD
 * != 예술가
 * != 요리사
 * != 콘텐츠 직업
 *
 * HURTING_OFFICER
 * != 프리랜서
 * != 영업
 * != 창업
 *
 * 같은 직업 대응을 생성하지 않는다.
 *
 * =================================================
 * 능력 확정 금지
 * =================================================
 *
 * 인성격
 * != 공부를 잘한다
 *
 * 정인격
 * != 암기력이 좋다
 *
 * 편인격
 * != 천재다
 *
 * 식신격
 * != 표현력이 뛰어나다
 *
 * 상관격
 * != 창의력이 뛰어나다
 *
 * 구조 중심축과 실제 능력 성과는
 * 동일하지 않다.
 *
 * =================================================
 * 시험 / 자격 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 시험 합격
 * != 자격증 취득
 *
 * PRESSURE
 * != 시험 실패
 * != 학업 실패
 *
 * MIXED
 * != 진로 변경
 *
 * =================================================
 * 직업 사건 생성 금지
 * =================================================
 *
 * 인성 주격
 * != 취업
 *
 * 식상 주격
 * != 창업
 *
 * SUPPORTIVE
 * != 승진
 *
 * PRESSURE
 * != 퇴사
 * != 해고
 *
 * MIXED
 * != 이직
 *
 * =================================================
 * 보조격 경계
 * =================================================
 *
 * upstream LearningPrimaryResourceOutputStructureRule은
 * StructureSelectionCandidate의 타입을 추측하지 않는다.
 *
 * 보조격은:
 *
 * - 존재 여부
 * - 이름
 *
 * 만 provenance로 보존한다.
 *
 * 이 Category Rule에서도
 * 보조격 이름 문자열을 파싱하여:
 *
 * - 직종
 * - 적성
 * - 관계
 * - 사건
 *
 * 을 생성하지 않는다.
 *
 * =================================================
 * 특수격 경계
 * =================================================
 *
 * upstream에서:
 *
 * primaryIsStandard() == false
 *
 * 이면
 * LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE
 * Match 자체가 생성되지 않는다.
 *
 * 이 Category Rule은
 * 특수격을 인성/식상 일반격으로
 * 임의 환원하지 않는다.
 *
 * =================================================
 * completeChart 경계
 * =================================================
 *
 * upstream StructureAnalysis의
 * completeChart=false 상태는
 * 원본 sourceMatches에 그대로 보존된다.
 *
 * 출생시간 미상 등을 이유로
 * 가짜 구조 정보를 추가하지 않는다.
 *
 * =================================================
 * Structured subtype
 * =================================================
 *
 * 이 Rule에서는:
 *
 * - DIRECT_RESOURCE
 * - INDIRECT_RESOURCE
 * - EATING_GOD
 * - HURTING_OFFICER
 *
 * 를 summary 또는 reasons 문자열에서
 * 다시 파싱하지 않는다.
 *
 * 정확한 구조 정보는
 * 원본 LifeDomainRuleMatch와
 * 그 Structure Evidence를 provenance로 보존한다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * 이 Source의 Match 개수를:
 *
 * - 직업 적성 개수
 * - 능력 개수
 * - 전문성
 * - 성공 가능성
 * - 직업운 강도
 *
 * 의 점수로 사용하지 않는다.
 *
 * =================================================
 * CAREER에서의 공식 역할
 * =================================================
 *
 * 이 Match는:
 *
 * "인성 또는 식상 계열이
 * 원국의 실제 주격 중심축으로
 * 관여하는 구조가 존재하는가"
 *
 * 를 CAREER 질문에서 참고할 수 있는
 * NATAL Context로 보존한다.
 *
 * 직업 방향 자체는 별도의:
 *
 * - CAREER_PRIMARY_OFFICER_STRUCTURE
 * - CAREER_OFFICER_STRUCTURE
 * - CAREER_CROSS_VALIDATED_PATTERN
 * - CAREER Timeline
 *
 * 계산이 담당한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - StructureAnalysis 재계산
 * - 주격 재선택
 * - 인성격 재판정
 * - 식상격 재판정
 * - 성립 재계산
 * - 부담 재계산
 * - 손상 재계산
 * - 혼잡 재계산
 * - 보조격 문자열 파싱
 * - 특수격 임의 환원
 * - LEARNING Status를 CAREER Status로 전이
 * - LEARNING_RESOURCE_OUTPUT과 Status 합성
 * - CAREER 관살 구조와 Status 합성
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
public class CareerLearningPrimaryResourceOutputStructureContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE_CONTEXT";

    private static final String
            LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE_RULE_CODE =
            "LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE";


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
                "CareerLearningPrimaryResourceOutputStructureContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerLearningPrimaryResourceOutputStructureContextRule은 CAREER View만 평가할 수 있습니다."
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
         */

        List<LifeDomainRuleMatch> structureMatches =
                findNatalMatches(
                        view,
                        LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE_RULE_CODE
                );

        if (
                structureMatches.isEmpty()
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
                LifeDomainRuleMatch structureMatch
                : structureMatches
        ) {

            LifeDomainRuleMatch normalizedMatch =
                    Objects.requireNonNull(
                            structureMatch,
                            "LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE Match는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * Category Status
             * =================================================
             *
             * Source의:
             *
             * SUPPORTIVE / PRESSURE / MIXED / CONTEXTUAL
             *
             * 을 CAREER 방향으로 전이하지 않는다.
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
                    "LEARNING_PRIMARY_RESOURCE_OUTPUT_STRUCTURE에서 이미 계산된 하나의 인성·식상 주격 Structure Match를 CAREER의 독립 NATAL Context로 보존했습니다."
            );

            reasons.add(
                    "원본 LEARNING_GROWTH Match의 공식 LifeDomainSignalStatus는 sourceMatches에 그대로 보존하지만 이를 CAREER의 SUPPORTIVE·PRESSURE·MIXED 방향으로 직접 전이하지 않았습니다."
            );

            reasons.add(
                    "이 Category Match는 직업 길흉이 아니라 인성 또는 식상 계열이 실제 원국의 주격 중심축으로 관여하는 Structure Context이므로 Category Status를 CONTEXTUAL로 유지합니다."
            );

            reasons.add(
                    "LearningPrimaryResourceOutputStructureRule이 StructureAnalysis를 이용해 이미 계산한 주격 유형, 성립, 부담, 손상, 혼잡, 보조격, 특수격 후보 및 completeChart 상태를 다시 계산하지 않았습니다."
            );

            reasons.add(
                    "LEARNING_RESOURCE_OUTPUT은 이 Rule의 Source 또는 Status 계산에 사용하지 않았으며 CareerLearningResourceOutputContextRule에서 별도의 Composite Context로 처리합니다."
            );

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE, CAREER_OFFICER_STRUCTURE, CAREER_CROSS_VALIDATED_PATTERN 같은 별도 CAREER Source를 이 Match에 결합하지 않았습니다."
            );

            reasons.add(
                    "인성·식상 주격 구조와 관살 구조의 존재를 함께 보고 새로운 직업 방향, 강화, 약화, 보완 또는 인과를 생성하지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 파싱하여 DIRECT_RESOURCE, INDIRECT_RESOURCE, EATING_GOD, HURTING_OFFICER subtype이나 보조격 종류를 재구성하지 않았습니다."
            );

            reasons.add(
                    "이 결과를 실제 학업 성적·시험 합격·자격 취득·취업·승진·이직·퇴사·창업 같은 사건이나 특정 직업군으로 변환하지 않습니다."
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
     * CAREER View 안에서도
     * LifeDomain.LEARNING_GROWTH의
     * 정확한 selected RuleCode만 사용한다.
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
                        "CAREER Learning Primary Resource Output Structure sourceRuleCode가 필요합니다."
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
     * Source의 공식 상태는 남기되
     * CAREER 방향으로 전이하지 않는다.
     */

    private String summaryOf(
            LifeDomainRuleMatch sourceMatch
    ) {

        Objects.requireNonNull(
                sourceMatch,
                "CAREER Learning Primary Structure Context에는 Source Match가 필요합니다."
        );

        return "원국에서 인성 또는 식상 계열이 실제 주격 중심축으로 관여하는 Structure Context를 "
                + "CAREER 질문의 독립 정보로 보존합니다. "
                + "원본 LEARNING_GROWTH 구조 상태는 "
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