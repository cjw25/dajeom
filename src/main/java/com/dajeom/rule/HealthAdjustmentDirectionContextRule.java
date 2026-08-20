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
 * 원국 최종 조정 방향 Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 기존 18차:
 *
 * HEALTH_LIFESTYLE_BALANCE
 *
 * 안에서 7차 UsefulGodAnalysis를 사용하는
 * Match만 구조적으로 분리한다.
 *
 * 이 Rule이 다루는 것은:
 *
 * - 최종 용신 조정축
 * - 희신 조정축
 * - 기신 Context
 * - 복수 용신
 * - 용신 미확정
 * - 복수 PRIMARY 검토
 * - 추가 검토 필요
 *
 * 등이다.
 *
 * =================================================
 * 중요한 역할 분리
 * =================================================
 *
 * HealthBaselineBalanceContextRule
 *
 * ->
 *
 * 오행 분포와 일간 지원·압력의
 * 기본 생활 균형 Context.
 *
 * -------------------------------------------------
 *
 * HealthClimateAdjustmentPressureRule
 *
 * ->
 *
 * 조후·명리적 병약·통관에서
 * 실제로 확인된 조정 부담 / 충돌.
 *
 * -------------------------------------------------
 *
 * 이 Rule
 *
 * ->
 *
 * 격국·억부·조후·병약·통관 등
 * 서로 다른 용신법과 검토 과정을 거쳐
 * 7차 UsefulGodAnalysis에서 보존된
 * 최종 조정 방향 Context.
 *
 * =================================================
 * Source 선택
 * =================================================
 *
 * LifeDomainRuleMatch 내부 Evidence에
 *
 * USEFUL_GOD
 *
 * Source가 반드시 존재해야 한다.
 *
 * 허용 SourceType은:
 *
 * - USEFUL_GOD
 * - CLIMATE_DISEASE_FLOW
 *
 * 뿐이다.
 *
 * 이유:
 *
 * 기존 HealthLifestyleBalanceRule의
 * 최종 용신 조정축 Match는:
 *
 * CLIMATE_DISEASE_FLOW
 * +
 * USEFUL_GOD
 *
 * 를 함께 보존한다.
 *
 * 복수 용신 / 미확정 검토 Match는
 * USEFUL_GOD만 사용한다.
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * 다음 문자열을 읽어
 * 조정 방향을 다시 만들지 않는다.
 *
 * - summary
 * - reasons
 *
 * 또한:
 *
 * "木"
 * "火"
 * "희신"
 * "기신"
 *
 * 같은 문자열을 추출하여
 * Category Rule에서 새로운 오행 판단을
 * 생성하지 않는다.
 *
 * 실제 오행 후보와 용신법 수렴은
 * UsefulGodAnalysis의 책임이다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * final status는 항상 CONTEXTUAL이다.
 *
 * 이유:
 *
 * 용신이 존재한다
 *
 * !=
 *
 * 현재 건강이 좋다
 *
 * -------------------------------------------------
 *
 * 기신이 존재한다
 *
 * !=
 *
 * 현재 건강이 나쁘다
 *
 * -------------------------------------------------
 *
 * 복수 용신
 *
 * !=
 *
 * 분석 오류
 *
 * -------------------------------------------------
 *
 * 용신 미확정
 *
 * !=
 *
 * 건강 위험
 *
 * 이기 때문이다.
 *
 * 이 Rule은 오직:
 *
 * "원국을 조정할 때 참고할
 * 최종 명리적 방향이 어떤 상태로
 * 보존되어 있는가"
 *
 * 만 기록한다.
 *
 * =================================================
 * 용신법 단순화 금지
 * =================================================
 *
 * UsefulGodAnalysis에는:
 *
 * - 억부
 * - 조후
 * - 격국
 * - 병약
 * - 통관
 * - 종격
 *
 * 등 서로 목적이 다른 방법론이 존재할 수 있다.
 *
 * 따라서 이 Rule에서는:
 *
 * "최종 용신 하나"
 *
 * 를 강제로 대표값으로 만들지 않는다.
 *
 * 복수 용신이 존재하면
 * 그대로 복수 조정축 Context로 보존한다.
 *
 * =================================================
 * 건강·의료 해석 금지
 * =================================================
 *
 * 이 Rule은:
 *
 * 용신 木
 * -> 간에 좋다
 *
 * 용신 火
 * -> 심장에 좋다
 *
 * 기신 水
 * -> 신장이 나쁘다
 *
 * 같은 오행-장기 매핑을 하지 않는다.
 *
 * 또한:
 *
 * - 질병
 * - 발병
 * - 진단
 * - 치료
 * - 수술
 * - 수명
 *
 * 을 예측하지 않는다.
 *
 * =================================================
 * Match 개수
 * =================================================
 *
 * 용신 관련 Match가 많다고:
 *
 * - 건강관리 필요성이 크다
 * - 건강운이 강하다
 * - 건강 위험이 높다
 *
 * 로 판단하지 않는다.
 */
@Component
public class HealthAdjustmentDirectionContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "HEALTH_ADJUSTMENT_DIRECTION_CONTEXT";

    private static final String
            HEALTH_LIFESTYLE_BALANCE_RULE_CODE =
            "HEALTH_LIFESTYLE_BALANCE";

    /*
     * =================================================
     * 이번 Rule에서 허용할 Source
     * =================================================
     */

    private static final Set<LifeDomainEvidenceSourceType>
            ALLOWED_SOURCE_TYPES =
            Set.of(
                    LifeDomainEvidenceSourceType.USEFUL_GOD,
                    LifeDomainEvidenceSourceType.CLIMATE_DISEASE_FLOW
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
                "HealthAdjustmentDirectionContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "HealthAdjustmentDirectionContextRule은 HEALTH_TENDENCY View만 평가할 수 있습니다."
            );
        }

        /*
         * =================================================
         * unavailable View
         * =================================================
         */

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * USEFUL_GOD 관련 Match 분리
         * =================================================
         */

        List<LifeDomainRuleMatch>
                adjustmentMatches =
                findAdjustmentMatches(
                        view
                );

        /*
         * 최종 조정 방향에 관한
         * deterministic Evidence가 없다면
         * 결과를 임의 생성하지 않는다.
         */

        if (
                adjustmentMatches.isEmpty()
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
                "기존 HEALTH_LIFESTYLE_BALANCE 결과 가운데 USEFUL_GOD Source가 실제로 포함된 Match만 구조적으로 분리하여 최종 명리적 조정 방향 Context로 사용했습니다."
        );

        reasons.add(
                "최종 조정축 Match에서는 CLIMATE_DISEASE_FLOW와 USEFUL_GOD가 함께 존재할 수 있으며, 복수 용신 또는 미확정 검토 Match에서는 USEFUL_GOD 단독 Source가 존재할 수 있습니다."
        );

        reasons.add(
                "격국·억부·조후·병약·통관·종격 등 서로 다른 용신 방법론의 후보 생성과 수렴 과정은 기존 UsefulGodAnalysis의 계산 결과를 그대로 사용하며 이번 Rule에서 다시 계산하지 않았습니다."
        );

        reasons.add(
                "LifeDomainRuleMatch의 summary 또는 reasons 문자열을 파싱하여 특정 용신 오행, 희신 오행, 기신 오행 또는 용신법을 재구성하지 않았습니다."
        );

        reasons.add(
                "용신이 존재한다는 사실을 현재 건강 상태가 좋다는 SUPPORTIVE로 변환하지 않았습니다."
        );

        reasons.add(
                "기신이 존재한다는 사실을 현재 건강 상태가 나쁘거나 질병 위험이 있다는 PRESSURE로 변환하지 않았습니다."
        );

        reasons.add(
                "복수 용신은 허용되는 구조적 상태로 보존하며 배열의 첫 번째 후보를 임의의 대표 용신으로 선택하지 않았습니다."
        );

        reasons.add(
                "용신 선택 미확정 또는 추가 검토 상태를 건강 위험이나 의료적 불확실성으로 해석하지 않았습니다."
        );

        reasons.add(
                "이번 결과는 원국의 생활 균형을 해석할 때 참고할 최종 명리적 조정 방향만 보존하므로 final status는 CONTEXTUAL로 유지합니다."
        );

        /*
         * =================================================
         * Result
         * =================================================
         */

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.HEALTH_TENDENCY,

                        ConsultationCategoryDeterministicLayer
                                .NATAL,

                        RULE_CODE,

                        ConsultationCategoryDeterministicStatus
                                .CONTEXTUAL,

                        null,

                        null,

                        List.of(),

                        "원국의 여러 명리적 조정 방법을 거쳐 보존된 최종 용신·희신·기신 방향을 생활 균형의 조정 Context로 참조하되 건강의 길흉이나 질병 판단으로 변환하지 않습니다.",

                        List.copyOf(
                                adjustmentMatches
                        ),

                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * USEFUL_GOD 관련 Match 선택
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
     * Evidence 최소 1개
     *
     * 4.
     * USEFUL_GOD Source가 반드시 존재
     *
     * 5.
     * 모든 Evidence SourceType은:
     *
     * - USEFUL_GOD
     * - CLIMATE_DISEASE_FLOW
     *
     * 안에 있어야 한다.
     */

    private List<LifeDomainRuleMatch>
    findAdjustmentMatches(
            ConsultationCategoryEvidenceView view
    ) {

        Objects.requireNonNull(
                view,
                "ConsultationCategoryEvidenceView가 필요합니다."
        );

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
             * 기존 Health Rule
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
             * 구조화된 SourceType 기준
             * =================================================
             */

            if (
                    !isUsefulGodAdjustmentMatch(
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
     * USEFUL_GOD 조정 Match 여부
     * =================================================
     */

    private boolean isUsefulGodAdjustmentMatch(
            LifeDomainRuleMatch match
    ) {

        Objects.requireNonNull(
                match,
                "LifeDomainRuleMatch가 필요합니다."
        );

        /*
         * Evidence 없는 Match는
         * 사용할 수 없다.
         */

        if (
                match.evidences()
                        .isEmpty()
        ) {

            return false;
        }

        boolean usefulGodExists =
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
             * =================================================
             * 허용 Source 외 Evidence가 섞이면 제외
             * =================================================
             *
             * 이번 Rule에서:
             *
             * ELEMENT_STRENGTH
             * DAY_MASTER_STRENGTH
             *
             * 등을 다시 소비하지 않는다.
             */

            if (
                    !ALLOWED_SOURCE_TYPES.contains(
                            sourceType
                    )
            ) {

                return false;
            }

            /*
             * =================================================
             * USEFUL_GOD 존재 확인
             * =================================================
             */

            if (
                    sourceType
                            == LifeDomainEvidenceSourceType.USEFUL_GOD
            ) {

                usefulGodExists =
                        true;
            }
        }

        /*
         * CLIMATE_DISEASE_FLOW만 있는 Match는
         * HealthClimateAdjustmentPressureRule의 책임이다.
         */

        return usefulGodExists;
    }
}