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
 * WEALTH 카테고리의
 * Palace Distribution Context
 * deterministic Rule.
 *
 * =================================================
 * 공식 Source Family
 * =================================================
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
 *
 * 하나만 사용한다.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.WEALTH에서 이미 계산된:
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
 *
 * 각각을
 * ConsultationCategory.WEALTH의
 * 독립적인 NATAL Context로
 * 1:1 보존한다.
 *
 * =================================================
 * 이전 명칭 제거
 * =================================================
 *
 * 과거:
 *
 * WealthFunctionDistributionContextRule
 * WEALTH_FUNCTION_DISTRIBUTION_CONTEXT
 *
 * 라는 명칭을 사용했다.
 *
 * 그러나 이 Category Rule은:
 *
 * - 재성 기능을 새로 분배하지 않는다.
 * - 여러 Palace 사이의 기능 배분을 계산하지 않는다.
 * - Palace 간 우선순위를 계산하지 않는다.
 * - Palace 간 기능 이동을 계산하지 않는다.
 *
 * 실제 공식 Source는:
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
 *
 * 하나뿐이다.
 *
 * 따라서 실제 계산 경계를
 * 클래스명과 RuleCode에 직접 반영한다.
 *
 * =================================================
 * Upstream 계산
 * =================================================
 *
 * WealthPalaceDistributionContextRule은
 * 실제 원국의 알려진 Palace 가운데:
 *
 * - DIRECT_WEALTH
 * - INDIRECT_WEALTH
 *
 * 가 존재하는 위치마다
 * LifeDomainRuleMatch를 생성한다.
 *
 * 따라서 이 Category Rule에서는:
 *
 * - 재성 위치
 * - Palace 위치
 * - TenGod
 *
 * 를 다시 계산하지 않는다.
 *
 * =================================================
 * 1 Match -> 1 Conclusion
 * =================================================
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * WEALTH Category
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 보존한다.
 *
 * 예를 들어:
 *
 * MONTH Palace Match
 * HOUR Palace Match
 *
 * 가 각각 존재하면
 * Category 결과도 각각 독립적으로 보존한다.
 *
 * 두 Palace를 하나의 결과로 합치지 않는다.
 *
 * =================================================
 * 공식 Status
 * =================================================
 *
 * Category Status는 항상:
 *
 * CONTEXTUAL
 *
 * 이다.
 *
 * Palace Distribution은:
 *
 * - 재성 기능이 존재하는 위치
 * - 원국 생활축의 위치 Context
 *
 * 를 제공하는 계산이다.
 *
 * 그 자체가:
 *
 * - SUPPORTIVE
 * - PRESSURE
 * - MIXED
 *
 * 방향을 새로 결정하는 Category 계산은 아니다.
 *
 * =================================================
 * Source Status 전이 금지
 * =================================================
 *
 * 다른 WEALTH Source의:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 *
 * 상태를 Palace Context에 전이하지 않는다.
 *
 * =================================================
 * 다른 WEALTH 계산과 분리
 * =================================================
 *
 * 다음 Source Family를
 * 이 Rule의 sourceMatches에 결합하지 않는다.
 *
 * - WEALTH_PRIMARY_WEALTH_STRUCTURE
 * - WEALTH_CAPACITY
 * - WEALTH_CROSS_VALIDATED_PATTERN
 *
 * 각각은 별도의 deterministic 결과다.
 *
 * =================================================
 * 현재 공식 분리
 * =================================================
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 *
 * ->
 *
 * WealthPrimaryWealthStructureDeterministicRule
 *
 * -------------------------------------------------
 *
 * WEALTH_CAPACITY
 *
 * ->
 *
 * WealthCapacityDeterministicRule
 *
 * -------------------------------------------------
 *
 * WEALTH_CROSS_VALIDATED_PATTERN
 *
 * ->
 *
 * WealthCrossValidatedPatternDeterministicRule
 *
 * -------------------------------------------------
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
 *
 * ->
 *
 * 이 Rule
 *
 * =================================================
 * Timeline과 분리
 * =================================================
 *
 * 이 Rule은 NATAL 전용이다.
 *
 * 다음 Timeline Source를 사용하지 않는다.
 *
 * - WEALTH_DECADE_CONTEXT
 * - WEALTH_ANNUAL_CONTEXT
 * - WEALTH_MONTHLY_CONTEXT
 * - WEALTH_ANNUAL_FUNCTIONAL
 * - WEALTH_MONTHLY_FUNCTIONAL
 *
 * Palace Context와 Timeline Context를
 * 하나의 상태로 합성하지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음 의미를 새로 생성하지 않는다.
 *
 * - 이 Palace에 재성이 있기 때문에
 *   다른 재물 구조가 강화된다
 *
 * - 재성 중심 구조가 있으므로
 *   이 Palace가 중요하다
 *
 * - Capacity가 PRESSURE이므로
 *   이 Palace에서 재물 부담이 발생한다
 *
 * - 여러 Palace에 재성이 있으므로
 *   재물운이 강하다
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 결합되어 C가 된다
 * - 먼저 A 이후 B
 * - A가 B를 강화한다
 * - A가 B를 약화한다
 *
 * =================================================
 * 실제 upstream Palace 관계 보존
 * =================================================
 *
 * upstream PalaceAnalysis에서
 * 실제 Java가 계산한 관계나
 * 구조화된 Palace 정보가 있다면
 * 해당 Source Match의 공식 범위 안에서는
 * 그대로 보존할 수 있다.
 *
 * 이것은 Category Rule이
 * 새로운 관계를 생성한다는 의미가 아니다.
 *
 * =================================================
 * Palace 의미 확대 금지
 * =================================================
 *
 * YEAR
 * != 집안 재산
 *
 * MONTH
 * != 직장 수입
 *
 * DAY
 * != 배우자 재산
 *
 * HOUR
 * != 말년 재산
 *
 * Palace 위치를
 * 실제 생활 사건으로 직접 치환하지 않는다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * Palace Context 존재
 * != 재산 증가
 *
 * Palace Context 존재
 * != 수입 증가
 *
 * Palace Context 존재
 * != 투자 성공
 *
 * Palace Context 존재
 * != 상속
 *
 * Palace Context 존재
 * != 횡재
 *
 * 또한:
 *
 * - 계약
 * - 투자
 * - 매매
 * - 상속
 * - 수입 증가
 * - 자산 증가
 * - 금전 손실
 *
 * 같은 실제 재물 사건을 생성하지 않는다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Palace Match가 여러 개 존재해도:
 *
 * - 재물복이 많다
 * - 재물운이 강하다
 * - 수입원이 많다
 * - 자산이 많다
 * - 돈이 자주 들어온다
 *
 * 로 계산하지 않는다.
 *
 * 각각의 Palace Context만
 * 독립적으로 보존한다.
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
 * 문자열을 파싱하여
 * Palace 위치나 의미를 재구성하지 않는다.
 *
 * Source Match 자체를
 * provenance로 그대로 보존한다.
 *
 * =================================================
 * 시간 미상
 * =================================================
 *
 * upstream PalaceAnalysis에서
 * HOUR가 알려지지 않았다면
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT 자체가
 * HOUR Match를 생성하지 않는다.
 *
 * 이 Rule도 누락된 HOUR를
 * 임의로 추정하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 재성 위치 재계산
 * - 십성 재계산
 * - Palace 재계산
 * - Palace 간 기능 분배 계산
 * - Palace 간 우선순위 계산
 * - 12운성 길흉 변환
 * - 합충형파해 재계산
 * - 다른 WEALTH Source와 결합
 * - Timeline Source와 결합
 * - Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 재물 사건 생성
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class WealthPalaceDistributionContextDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    /*
     * =================================================
     * Category Deterministic Rule Code
     * =================================================
     *
     * upstream RuleCode:
     *
     * WEALTH_PALACE_DISTRIBUTION_CONTEXT
     *
     * 와 Category Deterministic 결과를
     * 구분하기 위해 VIEW를 붙인다.
     */

    private static final String RULE_CODE =
            "WEALTH_PALACE_DISTRIBUTION_CONTEXT_VIEW";


    /*
     * =================================================
     * Source Rule Code
     * =================================================
     */

    private static final String
            PALACE_DISTRIBUTION_RULE_CODE =
            "WEALTH_PALACE_DISTRIBUTION_CONTEXT";


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
                "WealthPalaceDistributionContextDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "WealthPalaceDistributionContextDeterministicRule은 WEALTH View만 평가할 수 있습니다."
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
         * 공식 Palace Context Source
         * =================================================
         */

        List<LifeDomainRuleMatch> palaceMatches =
                findNatalMatches(
                        view,
                        PALACE_DISTRIBUTION_RULE_CODE
                );

        if (
                palaceMatches.isEmpty()
        ) {

            return List.of();
        }


        /*
         * =================================================
         * Palace Match별 독립 결과
         * =================================================
         *
         * 여러 Palace Match를
         * 하나의 Category Match로 합치지 않는다.
         */

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch palaceMatch
                : palaceMatches
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            palaceMatch,
                            "WEALTH Palace Distribution Match는 null일 수 없습니다."
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
                    "WEALTH_PALACE_DISTRIBUTION_CONTEXT에서 이미 계산된 하나의 Palace Context Match를 독립적인 WEALTH Category Context로 1:1 보존했습니다."
            );

            reasons.add(
                    "이 Category Rule에서는 재성 위치·Palace 위치·TenGod를 다시 계산하지 않았습니다."
            );

            reasons.add(
                    "WEALTH_PRIMARY_WEALTH_STRUCTURE·WEALTH_CAPACITY·WEALTH_CROSS_VALIDATED_PATTERN의 존재를 이 Palace Context의 추가 생성 조건으로 요구하지 않았습니다."
            );

            reasons.add(
                    "다른 WEALTH 원국 Source Match를 이 Category Match의 sourceMatches에 결합하지 않았습니다."
            );

            reasons.add(
                    "WEALTH_DECADE_CONTEXT·WEALTH_ANNUAL_CONTEXT·WEALTH_MONTHLY_CONTEXT·WEALTH_ANNUAL_FUNCTIONAL·WEALTH_MONTHLY_FUNCTIONAL을 이 NATAL Palace Context와 합성하지 않았습니다."
            );

            reasons.add(
                    "이 결과의 Category Status는 Palace 위치 자체에 재물 길흉 방향을 새로 부여하지 않기 때문에 CONTEXTUAL로 유지합니다."
            );

            reasons.add(
                    "다른 Palace Match가 존재하더라도 서로 합쳐 재물운의 강도·규모·방향·수입원 개수 또는 사건 가능성을 만들지 않았습니다."
            );

            reasons.add(
                    "upstream Java가 해당 Palace Match 안에서 실제 계산한 구조 정보가 있다면 그 Source Match의 공식 범위 안에서만 보존하며 이 Category Rule에서 새로운 관계를 추가하지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 파싱하여 Palace 위치·생활 사건·재물 사건을 새로 생성하지 않았습니다."
            );

            reasons.add(
                    "이 결과는 upstream에서 실제 계산된 하나의 재성 Palace Context만 보존하며 수입·자산·투자·사업·상속·횡재 같은 실제 재물 사건을 생성하지 않습니다."
            );


            /*
             * =================================================
             * Result
             * =================================================
             *
             * Source Match 1개
             *
             * ->
             *
             * Category Match 1개
             *
             * Status는 CONTEXTUAL 고정.
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(

                            ConsultationCategory.WEALTH,

                            ConsultationCategoryDeterministicLayer
                                    .NATAL,

                            RULE_CODE,

                            ConsultationCategoryDeterministicStatus
                                    .CONTEXTUAL,

                            null,

                            null,

                            List.of(),

                            "원국에서 확인된 하나의 WEALTH Palace Distribution Context를 독립적으로 보존하며 해당 위치 자체에는 재물의 길흉 방향을 부여하지 않습니다.",

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
     * summary / reasons 문자열을
     * Source 선택 조건으로 사용하지 않는다.
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
                        "WEALTH Palace Distribution sourceRuleCode가 필요합니다."
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
     */

    private void validateSourceMatch(

            LifeDomainRuleMatch match

    ) {

        Objects.requireNonNull(
                match,
                "WEALTH Palace Distribution Source Match가 필요합니다."
        );

        if (
                match.domain()
                        != LifeDomain.WEALTH
        ) {

            throw new IllegalStateException(
                    "WEALTH_PALACE_DISTRIBUTION_CONTEXT_VIEW에는 WEALTH Domain Match만 사용할 수 있습니다. "
                            + "actualDomain="
                            + match.domain()
                            + ", rule="
                            + match.ruleCode()
            );
        }

        if (
                !PALACE_DISTRIBUTION_RULE_CODE
                        .equals(
                                match.ruleCode()
                        )
        ) {

            throw new IllegalStateException(
                    "WEALTH_PALACE_DISTRIBUTION_CONTEXT_VIEW에 다른 Source Family가 유입되었습니다. "
                            + "expected="
                            + PALACE_DISTRIBUTION_RULE_CODE
                            + ", actual="
                            + match.ruleCode()
            );
        }
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