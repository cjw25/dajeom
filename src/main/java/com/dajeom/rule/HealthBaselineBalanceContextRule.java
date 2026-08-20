package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.LifeDomainEvidenceReference;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainEvidenceSourceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * HEALTH_TENDENCY 카테고리의
 * 원국 기본 균형 / 수용력 Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 기존 18차:
 *
 * HEALTH_LIFESTYLE_BALANCE
 *
 * 안에는 여러 종류의 판단이 함께 존재한다.
 *
 * 예:
 *
 * - 오행 세력 분포
 * - 일간 수용력
 * - 극단 신강·신약 Context
 * - 조후 조정 필요
 * - 명리적 병약 구조
 * - 통관 필요
 * - 조후·병약·통관 충돌
 * - 용신 조정 방향
 *
 * 하지만 모든 Match의 ruleCode가:
 *
 * HEALTH_LIFESTYLE_BALANCE
 *
 * 로 동일하다.
 *
 * =================================================
 * 구조화된 SourceType 사용
 * =================================================
 *
 * 다행히 LifeDomainRuleMatch 내부 Evidence에는:
 *
 * LifeDomainEvidenceSourceType
 *
 * 이 구조적으로 보존되어 있다.
 *
 * 따라서 이 Rule에서는 문자열을 파싱하지 않고:
 *
 * - ELEMENT_STRENGTH
 * - DAY_MASTER_STRENGTH
 *
 * Evidence만 사용하는 Match를 골라낸다.
 *
 * 이를 통해:
 *
 * - 오행의 상대 분포
 * - 일간의 지원력 / 압력 관계
 * - 극단 강약 검토 Context
 *
 * 만 HEALTH의 기본 균형 축으로 분리한다.
 *
 * =================================================
 * 제외되는 Source
 * =================================================
 *
 * 다음 Source가 들어간 Match는
 * 이번 Rule에서 사용하지 않는다.
 *
 * - CLIMATE_DISEASE_FLOW
 * - USEFUL_GOD
 *
 * 이 둘은 이후 별도의 HEALTH Category Rule에서
 * 각각 다룬다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 이 Rule은 항상 CONTEXTUAL이다.
 *
 * 이유:
 *
 * - 오행의 강약 분포
 * - 신강 / 신약
 * - 지원력 / 압력
 * - extreme 여부
 *
 * 자체는 의료적 건강 상태의
 * 좋음 / 나쁨을 뜻하지 않는다.
 *
 * 예:
 *
 * 신강
 * -> 건강하다
 *
 * 신약
 * -> 병약하다
 *
 * 로 변환하지 않는다.
 *
 * =================================================
 * 의료 해석 금지
 * =================================================
 *
 * 이 Rule은 절대로:
 *
 * 木 약함 -> 간 질환
 * 火 약함 -> 심장 질환
 * 土 약함 -> 위장 질환
 * 金 약함 -> 폐 질환
 * 水 약함 -> 신장 질환
 *
 * 같은 오행-장기 직접 대응을 하지 않는다.
 *
 * 또한:
 *
 * - 질병
 * - 발병
 * - 진단
 * - 치료 필요
 * - 수명
 *
 * 을 예측하지 않는다.
 *
 * =================================================
 * Match 개수
 * =================================================
 *
 * Match 수가 많거나 적다고:
 *
 * - 건강하다
 * - 허약하다
 * - 체력이 좋다
 * - 수명이 길다
 *
 * 로 판단하지 않는다.
 */
@Component
public class HealthBaselineBalanceContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "HEALTH_BASELINE_BALANCE_CONTEXT";

    private static final String
            HEALTH_LIFESTYLE_BALANCE_RULE_CODE =
            "HEALTH_LIFESTYLE_BALANCE";

    /**
     * 이번 Rule이 허용하는
     * 구조화된 upstream Source.
     */
    private static final Set<LifeDomainEvidenceSourceType>
            BASELINE_SOURCE_TYPES =
            Set.of(
                    LifeDomainEvidenceSourceType.ELEMENT_STRENGTH,
                    LifeDomainEvidenceSourceType.DAY_MASTER_STRENGTH
            );

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.HEALTH_TENDENCY;
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
                "HealthBaselineBalanceContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category 검증
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.HEALTH_TENDENCY
        ) {

            throw new IllegalArgumentException(
                    "HealthBaselineBalanceContextRule은 HEALTH_TENDENCY View만 평가할 수 있습니다."
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
         * HEALTH_LIFESTYLE_BALANCE 중
         * 기본 균형 Source만 선택
         * =================================================
         */

        List<LifeDomainRuleMatch> baselineMatches =
                findBaselineMatches(
                        view
                );

        /*
         * 기본 균형 Evidence가 없다면
         * 임의로 결과를 만들지 않는다.
         */

        if (
                baselineMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Reasons
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "원국의 오행 세력 분포와 일간의 지원·압력 관계를 HEALTH_TENDENCY의 기본 생활 균형 Context로 사용했습니다."
        );

        reasons.add(
                "기존 HEALTH_LIFESTYLE_BALANCE Match 가운데 ELEMENT_STRENGTH와 DAY_MASTER_STRENGTH Source만 사용하는 Match를 구조적으로 선별했습니다."
        );

        reasons.add(
                "CLIMATE_DISEASE_FLOW와 USEFUL_GOD Source가 포함된 Match는 이번 기본 균형 판단에서 제외했으며 별도의 건강 세부축에서 다룹니다."
        );

        reasons.add(
                "LifeDomainRuleMatch의 summary 또는 reasons 문자열을 파싱하여 오행 상태, 신강신약 상태 또는 세부 건강 의미를 재구성하지 않았습니다."
        );

        reasons.add(
                "신강을 건강함으로, 신약을 질병이나 허약함으로 변환하지 않았습니다."
        );

        reasons.add(
                "오행의 상대적인 강약과 분포를 특정 장기, 질환, 발병 가능성 또는 의학적 위험으로 연결하지 않았습니다."
        );

        reasons.add(
                "이번 결과는 원국에서 생활 균형과 소모·수용 구조를 해석하기 위한 기본 Context만 보존하므로 final status는 CONTEXTUAL로 유지합니다."
        );

        /*
         * =================================================
         * Result
         * =================================================
         */

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.HEALTH_TENDENCY,

                        ConsultationCategoryDeterministicLayer.NATAL,

                        RULE_CODE,

                        ConsultationCategoryDeterministicStatus.CONTEXTUAL,

                        null,

                        null,

                        List.of(),

                        "원국의 오행 분포와 일간의 지원·압력 관계를 생활 균형과 수용·소모 경향을 살피는 기본 Context로 보존합니다.",

                        List.copyOf(
                                baselineMatches
                        ),

                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * 기본 균형 Match 선택
     * =================================================
     *
     * 조건:
     *
     * 1.
     * HEALTH_LIFESTYLE Domain
     *
     * 2.
     * HEALTH_LIFESTYLE_BALANCE Rule
     *
     * 3.
     * 모든 Evidence SourceType이:
     *
     * - ELEMENT_STRENGTH
     * - DAY_MASTER_STRENGTH
     *
     * 안에 있어야 한다.
     *
     * 따라서:
     *
     * CLIMATE_DISEASE_FLOW
     * USEFUL_GOD
     *
     * 가 하나라도 포함되면
     * 이번 Rule에서는 제외한다.
     */

    private List<LifeDomainRuleMatch>
    findBaselineMatches(
            ConsultationCategoryEvidenceView view
    ) {

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : view.natalMatches()
        ) {

            Objects.requireNonNull(
                    match,
                    "HEALTH_TENDENCY Natal Match는 null일 수 없습니다."
            );

            /*
             * =================================================
             * Domain
             * =================================================
             */

            if (
                    match.domain()
                            != LifeDomain.HEALTH_LIFESTYLE
            ) {

                continue;
            }

            /*
             * =================================================
             * 기존 Health Balance Rule
             * =================================================
             */

            if (
                    !HEALTH_LIFESTYLE_BALANCE_RULE_CODE.equals(
                            match.ruleCode()
                    )
            ) {

                continue;
            }

            /*
             * =================================================
             * 구조화된 SourceType 기준 분리
             * =================================================
             */

            if (
                    !usesOnlyBaselineSources(
                            match
                    )
            ) {

                continue;
            }

            result.add(
                    match
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 기본 Source만 사용하는가
     * =================================================
     */

    private boolean usesOnlyBaselineSources(
            LifeDomainRuleMatch match
    ) {

        Objects.requireNonNull(
                match,
                "LifeDomainRuleMatch가 필요합니다."
        );

        if (
                match.evidences()
                        .isEmpty()
        ) {

            return false;
        }

        boolean baselineSourceExists =
                false;

        for (
                LifeDomainEvidenceReference<?> evidence
                : match.evidences()
        ) {

            Objects.requireNonNull(
                    evidence,
                    "HEALTH Evidence는 null일 수 없습니다."
            );

            LifeDomainEvidenceSourceType sourceType =
                    evidence.sourceType();

            /*
             * ELEMENT_STRENGTH 또는
             * DAY_MASTER_STRENGTH 외의
             * Source가 하나라도 존재하면
             * 이번 Rule 대상이 아니다.
             */

            if (
                    !BASELINE_SOURCE_TYPES.contains(
                            sourceType
                    )
            ) {

                return false;
            }

            baselineSourceExists =
                    true;
        }

        return baselineSourceExists;
    }
}