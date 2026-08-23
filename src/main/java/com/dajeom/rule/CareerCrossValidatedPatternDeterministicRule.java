package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
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
 * CAREER 원국
 * Cross Validated Pattern
 * deterministic Rule.
 *
 * =================================================
 * 공식 Source Family
 * =================================================
 *
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 하나만 사용한다.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.CAREER에서 이미 계산된:
 *
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 각각을
 * ConsultationCategory.CAREER의
 * 독립 deterministic Conclusion으로
 * 1:1 보존한다.
 *
 * =================================================
 * 이전 명칭 제거
 * =================================================
 *
 * 과거:
 *
 * CareerFunctionIntegrationPatternRule
 * CAREER_FUNCTION_INTEGRATION_PATTERN
 *
 * 라는 명칭을 사용했다.
 *
 * 그러나 현재 이 Category Rule은:
 *
 * - 서로 다른 기능을 통합하지 않는다.
 * - 여러 Pattern을 하나로 합성하지 않는다.
 * - 새로운 Function Integration을 계산하지 않는다.
 * - 서로 다른 Pattern 사이의 관계를 생성하지 않는다.
 * - 서로 다른 Pattern 사이의 인과를 생성하지 않는다.
 * - 서로 다른 Pattern 사이의 순서를 생성하지 않는다.
 *
 * 실제 Source는:
 *
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 하나뿐이다.
 *
 * 따라서 실제 계산 경계와
 * 클래스명 / Category RuleCode를 일치시킨다.
 *
 * =================================================
 * Upstream 계산
 * =================================================
 *
 * CAREER_CROSS_VALIDATED_PATTERN은
 * upstream LifeDomain Java 계산에서 이미:
 *
 * - 식신제살
 * - 상관견관
 * - 정관생인
 * - 칠살생인
 * - 정관·칠살 혼재
 * - 재생정관
 *
 * 및:
 *
 * - 식상 -> 관살
 * - 재성 -> 관살
 * - 관살 -> 인성
 * - 식상 -> 재성 -> 관살 -> 인성
 *
 * 등의 Pattern / Family Chain을
 * 계산한 결과일 수 있다.
 *
 * 또한:
 *
 * - 핵심 십성 Pattern
 * - 격국
 * - 신강신약
 * - 용신
 * - 합화
 *
 * 와 upstream에서 실제로
 * 교차검증된 결과일 수 있다.
 *
 * 이 Category Rule에서는
 * 위 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * 실제 계산된 관계 보존
 * =================================================
 *
 * upstream Java가 하나의
 * CAREER_CROSS_VALIDATED_PATTERN Match 안에서
 * 실제로 계산한:
 *
 * - 생 관계
 * - 극 관계
 * - Family Chain
 * - Core Pattern
 * - Cross Validation
 *
 * 자체는 유효한 deterministic 계산이다.
 *
 * 따라서 해당 Match의 공식 의미 범위 안에서는
 * 그 계산 결과를 보존할 수 있다.
 *
 * =================================================
 * Category Rule의 관계 생성 금지
 * =================================================
 *
 * 이 Rule은 독립적으로 존재하는:
 *
 * Pattern A
 * Pattern B
 * Pattern C
 *
 * 를 보고 새로운:
 *
 * A -> B
 * B -> C
 * A + B = C
 *
 * 의미를 만들지 않는다.
 *
 * =================================================
 * 1 Match -> 1 Conclusion
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
 * Pattern A = SUPPORTIVE
 * Pattern B = PRESSURE
 * Pattern C = CONTEXTUAL
 *
 * 이라면:
 *
 * Category Match A = SUPPORTIVE
 * Category Match B = PRESSURE
 * Category Match C = CONTEXTUAL
 *
 * 로 각각 유지한다.
 *
 * 다음처럼 합성하지 않는다.
 *
 * A + B + C
 * =
 * MIXED
 *
 * =================================================
 * Structured subtype 제한
 * =================================================
 *
 * 현재 LifeDomainRuleMatch에는:
 *
 * - 식신제살
 * - 상관견관
 * - 정관생인
 * - 칠살생인
 * - 재생정관
 *
 * 중 정확히 어느 Pattern인지 나타내는
 * 별도 구조화된 patternType 필드가 없다.
 *
 * 따라서:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱해서
 * Pattern subtype을 다시 추측하지 않는다.
 *
 * 세부 Pattern Type을
 * Category Contract에서 별도로 사용해야 한다면
 * upstream 모델에 구조화된 타입을
 * 명시적으로 추가해야 한다.
 *
 * =================================================
 * 다른 CAREER 원국 Source와 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * - CAREER_PRIMARY_OFFICER_STRUCTURE
 * - CAREER_OFFICER_STRUCTURE
 *
 * 를 사용하지 않는다.
 *
 * -------------------------------------------------
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * ->
 *
 * CareerPrimaryOfficerStructureDeterministicRule
 *
 * -------------------------------------------------
 *
 * CAREER_OFFICER_STRUCTURE
 *
 * ->
 *
 * CareerOfficerStructureDeterministicRule
 *
 * -------------------------------------------------
 *
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 각각 독립적인 deterministic 결과다.
 *
 * =================================================
 * Timeline과 분리
 * =================================================
 *
 * 이 Rule은 NATAL 전용이다.
 *
 * 다음 Timeline Source를 사용하지 않는다.
 *
 * - CAREER_DECADE_CONTEXT
 * - CAREER_ANNUAL_CONTEXT
 * - CAREER_MONTHLY_CONTEXT
 * - CAREER_ANNUAL_FUNCTIONAL
 * - CAREER_MONTHLY_FUNCTIONAL
 *
 * 원국 Pattern과 Timeline 결과를
 * 하나의 Status로 합성하지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 각 CAREER_CROSS_VALIDATED_PATTERN
 * LifeDomainRuleMatch의 공식 Status를
 * 정확히 1:1로 변환한다.
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
 * Status 합성 금지
 * =================================================
 *
 * 다음 계산을 하지 않는다.
 *
 * Pattern A SUPPORTIVE
 * +
 * Pattern B PRESSURE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * Pattern A PRESSURE
 * +
 * Officer Structure SUPPORTIVE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * Pattern A CONTEXTUAL
 * +
 * Annual Context PRESSURE
 * =
 * PRESSURE
 *
 * 금지.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 서로 다른 Pattern Match를 보고:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 작동한다
 * - A와 B가 결합되어 C가 된다
 * - A가 B를 강화한다
 * - A가 B를 약화한다
 * - A를 B가 보완한다
 * - 먼저 A가 나타나고 이후 B가 된다
 * - A가 반복될수록 B가 강해진다
 *
 * 같은 새로운 관계를 생성하지 않는다.
 *
 * 단:
 *
 * 하나의 CAREER_CROSS_VALIDATED_PATTERN
 * Match 자체가 upstream Java에서
 * 명시적으로 계산한 관계를 포함하는 경우에는
 * 그 Match의 공식 의미 범위 안에서만
 * 해당 관계를 보존한다.
 *
 * =================================================
 * 특정 직업 대응 금지
 * =================================================
 *
 * 식신제살
 * != 특정 전문직
 *
 * 정관생인
 * != 공무원
 *
 * 칠살생인
 * != 군인
 * != 경찰
 *
 * 상관견관
 * != 조직생활 불가
 *
 * 재생관
 * != 사업 성공
 *
 * Pattern을 실제 직업으로
 * 직접 치환하지 않는다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 취업 성공
 * != 승진
 *
 * PRESSURE
 * != 퇴사
 * != 해고
 * != 직장 갈등
 *
 * MIXED
 * != 이직
 * != 직업 변동
 *
 * CONTEXTUAL
 * != 직업운 없음
 *
 * 또한:
 *
 * - 취업
 * - 승진
 * - 합격
 * - 이직
 * - 퇴사
 * - 해고
 * - 창업
 * - 사업 성공
 * - 사업 실패
 *
 * 같은 실제 사건을 생성하지 않는다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Pattern Match가 여러 개 존재한다고:
 *
 * - 직업운이 강하다
 * - 직업 능력이 높다
 * - 성공 가능성이 높다
 * - 사회적 지위가 높다
 * - 책임이 크다
 *
 * 로 계산하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 십성 재계산
 * - Family Chain 재계산
 * - Core Pattern 재계산
 * - Cross Validation 재계산
 * - Pattern subtype 문자열 추측
 * - Pattern 간 Status 합성
 * - 다른 CAREER Source와 Status 합성
 * - NATAL + Timeline 합성
 * - Pattern Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 특정 직업군 생성
 * - 실제 직업 사건 생성
 */
@Component
public class CareerCrossValidatedPatternDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    /*
     * =================================================
     * Category Deterministic Rule Code
     * =================================================
     *
     * upstream LifeDomain RuleCode:
     *
     * CAREER_CROSS_VALIDATED_PATTERN
     *
     * 와 Category Deterministic 결과를
     * 구분하기 위해 CONTEXT를 붙인다.
     */

    private static final String RULE_CODE =
            "CAREER_CROSS_VALIDATED_PATTERN_CONTEXT";


    /*
     * =================================================
     * Source Rule Code
     * =================================================
     */

    private static final String
            CROSS_VALIDATED_PATTERN_RULE_CODE =
            "CAREER_CROSS_VALIDATED_PATTERN";


    /*
     * =================================================
     * Category
     * =================================================
     */

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.CAREER;
    }


    /*
     * =================================================
     * Rule Code
     * =================================================
     */

    @Override
    public String code() {

        return RULE_CODE;
    }


    /*
     * =================================================
     * Evaluate
     * =================================================
     */

    @Override
    public List<ConsultationCategoryDeterministicMatch>
    evaluate(

            ConsultationCategoryEvidenceView view

    ) {

        Objects.requireNonNull(
                view,
                "CareerCrossValidatedPatternDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerCrossValidatedPatternDeterministicRule은 CAREER View만 평가할 수 있습니다."
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
         * 공식 Source Family
         * =================================================
         */

        List<LifeDomainRuleMatch> patternMatches =
                findNatalMatches(
                        view,
                        CROSS_VALIDATED_PATTERN_RULE_CODE
                );

        if (
                patternMatches.isEmpty()
        ) {

            return List.of();
        }


        /*
         * =================================================
         * Match 1개 -> Category Match 1개
         * =================================================
         *
         * 여러 Pattern Match를
         * 하나의 final Status로 합성하지 않는다.
         */

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch patternMatch
                : patternMatches
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            patternMatch,
                            "CAREER Cross Validated Pattern Match는 null일 수 없습니다."
                    );


            /*
             * =================================================
             * Source 정합성
             * =================================================
             */

            validateSourceMatch(
                    normalized
            );


            /*
             * =================================================
             * Status 1:1
             * =================================================
             */

            ConsultationCategoryDeterministicStatus status =
                    toCategoryStatus(
                            normalized.status()
                    );


            /*
             * =================================================
             * Reasons
             * =================================================
             *
             * 최종 사용자 Story가 아니다.
             *
             * deterministic provenance와
             * 계산 경계를 기록한다.
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "CAREER_CROSS_VALIDATED_PATTERN에서 이미 계산된 하나의 LifeDomain Match를 독립적인 CAREER Category Match로 1:1 보존했습니다."
            );

            reasons.add(
                    "해당 LifeDomainRuleMatch의 공식 Status를 다른 CAREER_CROSS_VALIDATED_PATTERN Match와 결합하지 않고 그대로 Category Status로 변환했습니다."
            );

            reasons.add(
                    "CAREER_CROSS_VALIDATED_PATTERN 내부에 upstream Java가 실제 계산한 십성 Pattern·Family Chain·Core Pattern·Cross Validation 관계가 있다면 해당 Match의 공식 의미 범위 안에서만 보존합니다."
            );

            reasons.add(
                    "이 Category Rule 자체에서는 새로운 Pattern 관계·인과·순서·강화·약화·보완 의미를 추가하지 않았습니다."
            );

            reasons.add(
                    "CAREER_CROSS_VALIDATED_PATTERN의 세부 Pattern subtype은 현재 LifeDomainRuleMatch에 구조화된 별도 필드로 노출되지 않으므로 summary 또는 reasons 문자열을 파싱하여 재분류하지 않았습니다."
            );

            reasons.add(
                    "다른 CAREER_CROSS_VALIDATED_PATTERN Match가 존재하더라도 서로 연결하거나 Status를 합성하지 않았습니다."
            );

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE와 CAREER_OFFICER_STRUCTURE는 이 Match의 Source 또는 Status 계산에 사용하지 않았습니다."
            );

            reasons.add(
                    "CAREER_DECADE_CONTEXT·CAREER_ANNUAL_CONTEXT·CAREER_MONTHLY_CONTEXT·CAREER_ANNUAL_FUNCTIONAL·CAREER_MONTHLY_FUNCTIONAL을 이 NATAL Match와 합성하지 않았습니다."
            );

            reasons.add(
                    "Pattern Match의 존재 또는 반복 개수를 직업운 강도·직업 능력·사회적 지위·승진 가능성·사업 성공 가능성의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "이 결과는 upstream Java에서 실제 계산된 하나의 CAREER Cross Validated Pattern 상태만 보존하며 특정 직업군 또는 실제 직업 사건을 생성하지 않습니다."
            );


            /*
             * =================================================
             * Result
             * =================================================
             *
             * Source Match 1개
             * ->
             * Category Match 1개
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(

                            ConsultationCategory.CAREER,

                            ConsultationCategoryDeterministicLayer
                                    .NATAL,

                            RULE_CODE,

                            status,

                            null,

                            null,

                            List.of(),

                            summaryOf(
                                    status
                            ),

                            List.of(
                                    normalized
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
     * Natal Source 조회
     * =================================================
     *
     * Source 선택에는:
     *
     * - domain
     * - ruleCode
     *
     * 만 사용한다.
     *
     * summary / reasons 문자열은
     * Source 판별 조건으로 사용하지 않는다.
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
                        "CAREER Cross Validated Pattern sourceRuleCode가 필요합니다."
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

            if (
                    normalized.domain()
                            != LifeDomain.CAREER
            ) {

                continue;
            }

            if (
                    !normalizedRuleCode
                            .equals(
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
     * Source Match 정합성
     * =================================================
     *
     * 다른 CAREER Source Family가
     * 이 Rule에 유입되는 것을 차단한다.
     */

    private void validateSourceMatch(

            LifeDomainRuleMatch match

    ) {

        Objects.requireNonNull(
                match,
                "CAREER Cross Validated Pattern Match가 필요합니다."
        );

        if (
                match.domain()
                        != LifeDomain.CAREER
        ) {

            throw new IllegalStateException(
                    "CAREER_CROSS_VALIDATED_PATTERN_CONTEXT에는 CAREER Domain Match만 사용할 수 있습니다. "
                            + "actualDomain="
                            + match.domain()
                            + ", rule="
                            + match.ruleCode()
            );
        }

        if (
                !CROSS_VALIDATED_PATTERN_RULE_CODE
                        .equals(
                                match.ruleCode()
                        )
        ) {

            throw new IllegalStateException(
                    "CAREER_CROSS_VALIDATED_PATTERN_CONTEXT에 다른 Source Family가 유입되었습니다. "
                            + "expected="
                            + CROSS_VALIDATED_PATTERN_RULE_CODE
                            + ", actual="
                            + match.ruleCode()
            );
        }
    }


    /*
     * =================================================
     * Status 1:1 변환
     * =================================================
     *
     * 다른 Pattern Match 또는
     * 다른 CAREER Source를 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(

            LifeDomainSignalStatus sourceStatus

    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CAREER Cross Validated Pattern에는 LifeDomainSignalStatus가 필요합니다."
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
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * Pattern subtype을 추측하지 않는다.
     * 다른 Pattern과 연결하지 않는다.
     * 해당 Match 하나의 공식 방향만 기록한다.
     */

    private String summaryOf(

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                status,
                "CAREER Cross Validated Pattern Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "CAREER에서 사용하는 하나의 Cross Validated Pattern 결과가 SUPPORTIVE 상태로 독립 보존됩니다.";

            case PRESSURE ->
                    "CAREER에서 사용하는 하나의 Cross Validated Pattern 결과가 PRESSURE 상태로 독립 보존됩니다.";

            case MIXED ->
                    "CAREER에서 사용하는 하나의 Cross Validated Pattern 결과가 MIXED 상태로 독립 보존됩니다.";

            case CONTEXTUAL ->
                    "CAREER에서 사용하는 하나의 Cross Validated Pattern 결과가 CONTEXTUAL 상태로 독립 보존됩니다.";
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