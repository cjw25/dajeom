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
 * WEALTH 원국
 * Cross Validated Pattern
 * deterministic Rule.
 *
 * =================================================
 * 공식 Source Family
 * =================================================
 *
 * WEALTH_CROSS_VALIDATED_PATTERN
 *
 * 하나만 사용한다.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.WEALTH에서 이미 계산된:
 *
 * WEALTH_CROSS_VALIDATED_PATTERN
 *
 * 각각을
 * ConsultationCategory.WEALTH의
 * 독립 deterministic Conclusion으로
 * 1:1 보존한다.
 *
 * =================================================
 * 이전 명칭 제거
 * =================================================
 *
 * 과거:
 *
 * WealthFlowPressurePatternRule
 * WEALTH_FLOW_PRESSURE_PATTERN
 *
 * 라는 이름을 사용했다.
 *
 * 그러나 현재 이 Category Rule은:
 *
 * - Flow를 새로 계산하지 않는다.
 * - Pressure 관계를 새로 계산하지 않는다.
 * - 여러 Pattern을 하나의 흐름으로 연결하지 않는다.
 * - Pattern 사이의 시간순서를 만들지 않는다.
 * - Pattern 사이의 인과를 만들지 않는다.
 *
 * 실제 공식 Source는:
 *
 * WEALTH_CROSS_VALIDATED_PATTERN
 *
 * 하나뿐이다.
 *
 * 따라서 실제 계산 경계와 이름을 맞춘다.
 *
 * =================================================
 * Upstream 관계 계산
 * =================================================
 *
 * WEALTH_CROSS_VALIDATED_PATTERN 자체는
 * upstream Java에서 실제로 계산된:
 *
 * - 식상 -> 재성 Family Chain
 * - 비겁과 재성의 경쟁 / 분산 구조
 * - 재다신약 계열 구조
 * - 재성 -> 관성 구조
 * - 식상 -> 재성 -> 관살 -> 인성 Chain
 *
 * 등을 포함할 수 있다.
 *
 * 또한 일부 Pattern은:
 *
 * - 격국
 * - 신강신약
 * - 용신
 * - 합화
 *
 * 와 이미 교차검증된 결과일 수 있다.
 *
 * =================================================
 * 중요한 경계
 * =================================================
 *
 * upstream Java가 실제 계산한 관계는
 * 그대로 보존할 수 있다.
 *
 * 그러나 이 Category Rule이:
 *
 * - 새로운 관계
 * - 새로운 인과
 * - 새로운 순서
 * - 새로운 강화 / 약화
 *
 * 를 추가해서는 안 된다.
 *
 * =================================================
 * 1 Match -> 1 Conclusion
 * =================================================
 *
 * LifeDomainRuleMatch 1개
 *
 * ->
 *
 * ConsultationCategoryDeterministicMatch 1개
 *
 * 로 변환한다.
 *
 * 여러 WEALTH_CROSS_VALIDATED_PATTERN Match를
 * 하나의 Category Status로 합성하지 않는다.
 *
 * 예:
 *
 * Pattern A = PRESSURE
 * Pattern B = CONTEXTUAL
 * Pattern C = MIXED
 *
 * 이라면:
 *
 * Category Match A = PRESSURE
 * Category Match B = CONTEXTUAL
 * Category Match C = MIXED
 *
 * 로 각각 보존한다.
 *
 * A + B + C를 합쳐
 * 새로운 Category Status를 만들지 않는다.
 *
 * =================================================
 * Pattern subtype 추측 금지
 * =================================================
 *
 * 현재 LifeDomainRuleMatch에는:
 *
 * WEALTH_CROSS_VALIDATED_PATTERN
 *
 * 내부의 세부 Pattern Type을 나타내는
 * 별도의 구조화된 subtype 필드가 없다.
 *
 * 따라서:
 *
 * - summary 문자열
 * - reasons 문자열
 *
 * 을 파싱해서:
 *
 * - 비겁쟁재
 * - 재다신약
 * - 식상생재
 * - 재생관
 * - 기타 Chain
 *
 * 을 다시 분류하지 않는다.
 *
 * 세부 Pattern Type을
 * Category Contract에서 별도로 구분해야 한다면
 * 추후 upstream 모델에 구조화 필드를
 * 명시적으로 추가해야 한다.
 *
 * =================================================
 * Palace 분리
 * =================================================
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT는
 * 이 Rule에서 사용하지 않는다.
 *
 * Palace Distribution은:
 *
 * WealthFunctionDistributionContextRule
 *
 * 이 별도의 독립 deterministic 결과로
 * 담당한다.
 *
 * 따라서:
 *
 * Cross Validated Pattern
 * +
 * Palace Distribution
 *
 * 을 이 Rule에서 합성하지 않는다.
 *
 * =================================================
 * 다른 WEALTH Source와 분리
 * =================================================
 *
 * 다음 Source Family도
 * 이 Rule에 결합하지 않는다.
 *
 * - WEALTH_PRIMARY_WEALTH_STRUCTURE
 * - WEALTH_CAPACITY
 * - WEALTH_DECADE_CONTEXT
 * - WEALTH_ANNUAL_CONTEXT
 * - WEALTH_MONTHLY_CONTEXT
 * - WEALTH_ANNUAL_FUNCTIONAL
 * - WEALTH_MONTHLY_FUNCTIONAL
 *
 * =================================================
 * Status
 * =================================================
 *
 * 각 Category Match의 Status는
 * 해당 LifeDomainRuleMatch의
 * LifeDomainSignalStatus를
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
 * Palace SUPPORTIVE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * Pattern A CONTEXTUAL
 * +
 * Capacity PRESSURE
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
 * - A와 B가 결합되어 C가 된다
 * - 먼저 A 이후 B
 * - A가 B를 강화한다
 * - A가 B를 약화한다
 * - A가 반복될수록 B가 커진다
 *
 * 같은 의미를 새로 생성하지 않는다.
 *
 * 단:
 *
 * 하나의 WEALTH_CROSS_VALIDATED_PATTERN
 * Match 자체가 upstream Java에서
 * 명시적으로 계산한 관계를 포함하는 경우에는
 * 그 Match의 공식 의미 범위 안에서만
 * 해당 관계를 보존할 수 있다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 돈을 많이 번다
 * != 재산이 늘어난다
 * != 투자 성공
 *
 * PRESSURE
 * != 돈을 잃는다
 * != 투자 실패
 * != 부채 발생
 *
 * MIXED
 * != 재물 기복이 크다
 *
 * CONTEXTUAL
 * != 재물운이 없다
 *
 * 또한:
 *
 * - 투자 성공
 * - 투자 실패
 * - 사업 성공
 * - 사업 실패
 * - 재산 증가
 * - 재산 감소
 * - 횡재
 * - 부채
 *
 * 같은 실제 사건으로 변환하지 않는다.
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
 * - Pattern + Palace 합성
 * - Pattern + Capacity 합성
 * - 원국 + Timeline 합성
 * - Pattern Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 재물 사건 생성
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class WealthCrossValidatedPatternDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    /*
     * =================================================
     * Category Deterministic Rule Code
     * =================================================
     *
     * upstream LifeDomain RuleCode:
     *
     * WEALTH_CROSS_VALIDATED_PATTERN
     *
     * 와 Category 결과를 구분하기 위해
     * CONTEXT를 붙인다.
     */

    private static final String RULE_CODE =
            "WEALTH_CROSS_VALIDATED_PATTERN_CONTEXT";


    /*
     * =================================================
     * Source Rule Code
     * =================================================
     */

    private static final String
            CROSS_VALIDATED_PATTERN_RULE_CODE =
            "WEALTH_CROSS_VALIDATED_PATTERN";


    /*
     * =================================================
     * Category
     * =================================================
     */

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.WEALTH;
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
                "WealthCrossValidatedPatternDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );


        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthCrossValidatedPatternDeterministicRule은 WEALTH View만 평가할 수 있습니다."
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
         * Pattern별 독립 결과
         * =================================================
         *
         * 여러 Pattern Match를
         * 하나의 Category Status로 합성하지 않는다.
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
                            "WEALTH Cross Validated Pattern Match는 null일 수 없습니다."
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
                    "WEALTH_CROSS_VALIDATED_PATTERN에서 이미 계산된 하나의 LifeDomain Match를 독립적인 WEALTH Category Match로 1:1 보존했습니다."
            );

            reasons.add(
                    "이 Match의 공식 LifeDomainSignalStatus를 다른 Pattern Match의 Status와 결합하지 않고 그대로 Category Status에 변환했습니다."
            );

            reasons.add(
                    "WEALTH_CROSS_VALIDATED_PATTERN 내부에 upstream Java가 실제 계산한 Family Chain·Core Pattern·Cross Validation 관계가 있다면 해당 Match의 공식 의미 범위 안에서만 보존합니다."
            );

            reasons.add(
                    "이 Category Rule 자체에서는 새로운 Family Chain·관계·인과·순서·강화·약화 의미를 추가하지 않았습니다."
            );

            reasons.add(
                    "WEALTH_CROSS_VALIDATED_PATTERN의 세부 Pattern subtype은 현재 LifeDomainRuleMatch에 구조화된 별도 필드로 노출되지 않으므로 summary 또는 reasons 문자열을 파싱하여 재분류하지 않았습니다."
            );

            reasons.add(
                    "다른 WEALTH_CROSS_VALIDATED_PATTERN Match가 존재하더라도 이 Match와 합쳐 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 만들지 않았습니다."
            );

            reasons.add(
                    "WEALTH_PRIMARY_WEALTH_STRUCTURE와 WEALTH_CAPACITY를 이 Match의 Source 또는 Status 계산에 사용하지 않았습니다."
            );

            reasons.add(
                    "WEALTH_PALACE_DISTRIBUTION_CONTEXT는 별도의 WealthFunctionDistributionContextRule이 담당하므로 이 Match에 결합하지 않았습니다."
            );

            reasons.add(
                    "WEALTH_DECADE_CONTEXT·WEALTH_ANNUAL_CONTEXT·WEALTH_MONTHLY_CONTEXT와 같은 Timeline Source도 이 NATAL Conclusion에 합성하지 않았습니다."
            );

            reasons.add(
                    "Pattern Match 반복 개수를 재물운 강도·수입 규모·자산 규모·성공 가능성·손실 위험의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "이 결과는 upstream Java에서 실제 계산된 하나의 WEALTH Cross Validated Pattern 상태만 보존하며 실제 수입·손실·투자·사업·재산 사건을 생성하지 않습니다."
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

                            ConsultationCategory.WEALTH,

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
     * Source 판별에는:
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
                "WEALTH Evidence View가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "WEALTH Cross Validated Pattern sourceRuleCode가 필요합니다."
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
                            "WEALTH Natal Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.WEALTH
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
     * Source Match 정합성
     * =================================================
     *
     * 다른 WEALTH Source Family가
     * 이 Rule에 유입되는 것을 차단한다.
     */

    private void validateSourceMatch(

            LifeDomainRuleMatch match

    ) {

        Objects.requireNonNull(
                match,
                "WEALTH Cross Validated Pattern Match가 필요합니다."
        );

        if (
                match.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalStateException(
                    "WEALTH_CROSS_VALIDATED_PATTERN_CONTEXT에는 WEALTH Domain Match만 사용할 수 있습니다. "
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
                    "WEALTH_CROSS_VALIDATED_PATTERN_CONTEXT에 다른 Source Family가 유입되었습니다. "
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
     * 다른 Match를 참조하지 않는다.
     *
     * 하나의 LifeDomainRuleMatch 상태를
     * 하나의 Category Match 상태로만 변환한다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(

            LifeDomainSignalStatus sourceStatus

    ) {

        Objects.requireNonNull(
                sourceStatus,
                "WEALTH Cross Validated Pattern에는 LifeDomainSignalStatus가 필요합니다."
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
     * 해당 Match의 공식 방향만 기록한다.
     */

    private String summaryOf(

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                status,
                "WEALTH Cross Validated Pattern Category Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "WEALTH에서 사용하는 하나의 Cross Validated Pattern 결과가 SUPPORTIVE 상태로 독립 보존됩니다.";

            case PRESSURE ->
                    "WEALTH에서 사용하는 하나의 Cross Validated Pattern 결과가 PRESSURE 상태로 독립 보존됩니다.";

            case MIXED ->
                    "WEALTH에서 사용하는 하나의 Cross Validated Pattern 결과가 MIXED 상태로 독립 보존됩니다.";

            case CONTEXTUAL ->
                    "WEALTH에서 사용하는 하나의 Cross Validated Pattern 결과가 CONTEXTUAL 상태로 독립 보존됩니다.";
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