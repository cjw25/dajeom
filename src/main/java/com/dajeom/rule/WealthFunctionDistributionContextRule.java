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
 * 재성 기능 Palace 배치 Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.WEALTH에서 이미 계산된:
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
 *
 * 를 WEALTH Category의
 * 독립적인 Context 결과로 보존한다.
 *
 * =================================================
 * Upstream 계산
 * =================================================
 *
 * WealthPalaceDistributionContextRule은
 * 실제 원국의 알려진 Palace 중:
 *
 * - DIRECT_WEALTH
 * - INDIRECT_WEALTH
 *
 * 가 존재하는 위치마다
 * LifeDomainRuleMatch를 하나씩 생성한다.
 *
 * 따라서 이 Rule에서는
 * Palace 위치를 다시 계산하지 않는다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 기존에는 Palace Context를 채택하기 위해:
 *
 * - WEALTH_PRIMARY_WEALTH_STRUCTURE
 * - WEALTH_CAPACITY
 * - WEALTH_CROSS_VALIDATED_PATTERN
 *
 * 중 하나 이상의 존재를 추가 Anchor로 요구했다.
 *
 * 그리고 해당 Anchor Match까지
 * 같은 Category Match의 sourceMatches에 넣었다.
 *
 * 이제 그렇게 하지 않는다.
 *
 * =================================================
 * 독립 계산 결과 보존
 * =================================================
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT는
 * 그 자체로:
 *
 * "재성 기능이 원국의 어느 Palace Context에
 * 실제 존재하는가"
 *
 * 를 이미 계산한 독립 Context다.
 *
 * 따라서 다른 재물 구조의 존재 여부를
 * 이 Context의 생성 조건으로 사용하지 않는다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * LifeDomain Palace Match 하나를
 * Category Context Match 하나로 보존한다.
 *
 * 예:
 *
 * MONTH Palace Match
 * HOUR Palace Match
 *
 * 가 각각 존재하면:
 *
 * WEALTH Category에서도
 * 두 개의 독립 Context Match로 보존한다.
 *
 * 두 Palace를 하나의 Match로 합치지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 항상 CONTEXTUAL.
 *
 * Palace 배치는:
 *
 * - 재성 기능의 위치
 * - 생활축 Context
 *
 * 를 나타내는 것이지
 * 그 자체로 재물의 길흉 방향을
 * 결정하는 Evidence가 아니기 때문이다.
 *
 * =================================================
 * 다른 재물 계산과의 분리
 * =================================================
 *
 * 이 Rule에서는 다음을 사용하지 않는다.
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 * WEALTH_CAPACITY
 * WEALTH_CROSS_VALIDATED_PATTERN
 *
 * 각각은 별도의 deterministic Rule이
 * 독립적으로 담당한다.
 *
 * 따라서:
 *
 * Palace + Primary Structure
 * Palace + Capacity
 * Palace + Cross Validated Pattern
 *
 * 을 연결한 새로운 의미를 만들지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음 의미를 생성하지 않는다.
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
 * - A와 B가 함께 작동한다
 *
 * =================================================
 * Palace 사건화 금지
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
 * 또한:
 *
 * - 재산 증가
 * - 수입 증가
 * - 사업 성공
 * - 투자 성공
 * - 상속
 * - 횡재
 *
 * 같은 사건으로 변환하지 않는다.
 *
 * =================================================
 * Match 개수
 * =================================================
 *
 * Palace Match가 여러 개 존재해도:
 *
 * - 재물복 증가
 * - 재물운 강화
 * - 수입 증가
 * - 자산 증가
 *
 * 로 계산하지 않는다.
 *
 * 각각의 Palace Context만 독립적으로 보존한다.
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
 * Palace 위치를 재구성하지 않는다.
 *
 * 원본 LifeDomainRuleMatch 자체를
 * sourceMatches에 그대로 보존한다.
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
 * 이 Rule도 누락된 HOUR를 추정하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 재성 위치 재계산
 * - 십성 재계산
 * - Palace 재계산
 * - 12운성 길흉 변환
 * - 합충형파해 재계산
 * - 다른 재물 Source와 결합
 * - Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 재물 사건 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class WealthFunctionDistributionContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_FUNCTION_DISTRIBUTION_CONTEXT";

    private static final String
            PALACE_DISTRIBUTION_RULE_CODE =
            "WEALTH_PALACE_DISTRIBUTION_CONTEXT";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.WEALTH;
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
                "WealthFunctionDistributionContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "WealthFunctionDistributionContextRule은 WEALTH View만 평가할 수 있습니다."
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
         * Palace Context Source
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
         * 하나의 Category Match로 평탄화하지 않는다.
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
             * Reasons
             * =================================================
             *
             * 최종 사용자 Story가 아니라
             * deterministic provenance 기록이다.
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "WEALTH_PALACE_DISTRIBUTION_CONTEXT에서 이미 계산된 하나의 Palace Context Match를 독립적인 WEALTH Category Context로 보존했습니다."
            );

            reasons.add(
                    "이 Palace Context를 생성하기 위해 WEALTH_PRIMARY_WEALTH_STRUCTURE, WEALTH_CAPACITY, WEALTH_CROSS_VALIDATED_PATTERN 같은 다른 재물 Source의 존재를 추가 조건으로 요구하지 않았습니다."
            );

            reasons.add(
                    "다른 재물 구조 Match를 이 Category Match의 sourceMatches에 결합하지 않았습니다."
            );

            reasons.add(
                    "이 결과의 Status는 Palace 위치 자체에 재물 길흉 방향을 부여하지 않기 때문에 CONTEXTUAL로 유지합니다."
            );

            reasons.add(
                    "다른 Palace Match가 존재하더라도 서로 합쳐 재물운의 강도·규모·방향을 만들지 않습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 파싱하여 Palace 위치나 사건 의미를 새로 생성하지 않았습니다."
            );

            reasons.add(
                    "이 결과는 upstream에서 실제 계산된 하나의 재성 Palace Context만 보존하며 수입·자산·투자·사업·상속·횡재 같은 실제 재물 사건을 생성하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
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

                            "원국에서 확인된 하나의 재성 Palace 배치 Context를 독립적으로 보존하며 해당 위치 자체에는 재물의 길흉 방향을 부여하지 않습니다.",

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
     * 판단 조건으로 사용하지 않는다.
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
                        "WEALTH Palace Source ruleCode가 필요합니다."
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