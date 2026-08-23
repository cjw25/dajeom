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
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * HEALTH_TENDENCY 카테고리의
 * 원국 조후·명리적 병약·통관 조정 Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 기존:
 *
 * HEALTH_LIFESTYLE_BALANCE
 *
 * 안에서 이미 계산된:
 *
 * CLIMATE_DISEASE_FLOW
 *
 * Source Match를 HEALTH_TENDENCY의
 * 독립적인 NATAL 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * HealthLifestyleBalanceRule은
 * ClimateHealthAnalysis를 사용하여 이미:
 *
 * - 조후 조정 필요
 * - 의미 있는 명리적 병약 구조
 * - 미해결 명리적 병약 구조
 * - 의미 있는 통관 필요
 * - 조후·병약·통관 통합 충돌
 * - 후보 교차검증 모순
 * - 추가 재검토 필요
 *
 * 등을 각각 deterministic하게 계산한다.
 *
 * 이 Category Rule에서는
 * 해당 판단을 다시 수행하지 않는다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 기존 Category Rule은
 * 여러 CLIMATE_DISEASE_FLOW Match를 모은 뒤:
 *
 * MIXED 존재
 * -> MIXED
 *
 * SUPPORTIVE + PRESSURE
 * -> MIXED
 *
 * PRESSURE 존재
 * -> PRESSURE
 *
 * SUPPORTIVE 존재
 * -> SUPPORTIVE
 *
 * 와 같이 Category 단계에서
 * Family Status를 다시 만들었다.
 *
 * 이제 이 계산을 제거한다.
 *
 * =================================================
 * 이유
 * =================================================
 *
 * 각각의 LifeDomainRuleMatch는
 * 이미 서로 다른 upstream deterministic 판단이다.
 *
 * 예:
 *
 * 조후 조정 필요
 *
 * 와
 *
 * 통관 필요
 *
 * 가 동시에 존재한다고 해서
 * Category가 두 결과를 다시 합쳐
 * 새로운 상태를 만들 필요가 없다.
 *
 * =================================================
 * 실제 upstream MIXED 보존
 * =================================================
 *
 * 다음과 같이 upstream Java가
 * 실제 조건을 확인해서 생성한 MIXED는
 * 그대로 보존한다.
 *
 * - ClimateHealthIntegrationAnalysis conflict
 * - ClimateHealthCrossValidationAnalysis contradiction
 *
 * 이는 Category 단계에서
 * 두 독립 Evidence를 보고 새로 만든 MIXED가 아니다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * CLIMATE_DISEASE_FLOW
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * HEALTH_TENDENCY
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 보존한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 원본 LifeDomainSignalStatus를
 * Category Status로 그대로 1:1 변환한다.
 *
 * SUPPORTIVE
 * -> SUPPORTIVE
 *
 * PRESSURE
 * -> PRESSURE
 *
 * MIXED
 * -> MIXED
 *
 * CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 * Status 합성 금지
 * =================================================
 *
 * 다음 계산을 하지 않는다.
 *
 * CLIMATE Match A = SUPPORTIVE
 *
 * +
 *
 * CLIMATE Match B = PRESSURE
 *
 * =
 *
 * Category MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * CLIMATE Match A = PRESSURE
 *
 * +
 *
 * CLIMATE Match B = PRESSURE
 *
 * =
 *
 * 더 강한 PRESSURE
 *
 * 금지.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * PRESSURE Match가 여러 개 존재해도:
 *
 * - 건강 위험도 증가
 * - 질병 가능성 증가
 * - 체력 저하 정도 증가
 * - 수명 감소
 *
 * 같은 점수로 변환하지 않는다.
 *
 * =================================================
 * Source 분리
 * =================================================
 *
 * 이번 Rule에서는
 * 모든 Evidence가:
 *
 * CLIMATE_DISEASE_FLOW
 *
 * 인 HEALTH_LIFESTYLE_BALANCE Match만 사용한다.
 *
 * =================================================
 * 제외
 * =================================================
 *
 * ELEMENT_STRENGTH
 * DAY_MASTER_STRENGTH
 *
 * ->
 *
 * HealthBaselineBalanceContextRule
 *
 * -------------------------------------------------
 *
 * USEFUL_GOD가 포함된 Match
 *
 * ->
 *
 * HealthAdjustmentDirectionContextRule
 *
 * 의 책임이다.
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
 * 문자열을 파싱하여:
 *
 * - 조후
 * - 병약
 * - 통관
 * - 통합 충돌
 * - 교차검증 모순
 *
 * subtype을 다시 판정하지 않는다.
 *
 * Source 분리는 오직:
 *
 * LifeDomainEvidenceReference.sourceType()
 *
 * 를 사용한다.
 *
 * =================================================
 * 의료적 의미 금지
 * =================================================
 *
 * PRESSURE
 * != 질병
 *
 * PRESSURE
 * != 발병 가능성
 *
 * PRESSURE
 * != 장기 이상
 *
 * MIXED
 * != 건강 상태 불안정
 *
 * CONTEXTUAL
 * != 건강함
 *
 * SUPPORTIVE
 * != 의학적으로 건강함
 *
 * =================================================
 * 명리적 병약 경계
 * =================================================
 *
 * 명리적 병약
 * != 의료적 pathology
 *
 * 명리적 병약 구조는
 * 명리 내부에서 계산된
 * 균형 저해 Context다.
 *
 * 실제 질환을 의미하지 않는다.
 *
 * =================================================
 * 관계·인과 생성 금지
 * =================================================
 *
 * 다음을 새로 생성하지 않는다.
 *
 * - 조후 부담 때문에 병약이 생긴다
 * - 병약 때문에 통관이 필요하다
 * - 통관 필요가 건강 악화로 이어진다
 * - 조후와 병약이 같이 있으므로 더 위험하다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * 실제 Java 관계와의 구분
 * =================================================
 *
 * ClimateHealthAnalysis 내부에서
 * 실제 Java가 계산한:
 *
 * - integrationConflict
 * - crossValidationContradiction
 * - pathology remedy 상태
 * - 통관 필요
 *
 * 등의 결과는 그대로 인정한다.
 *
 * Category가 새 관계를 추가하지 않는다는 뜻이지
 * upstream 계산을 삭제한다는 뜻이 아니다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * PRESSURE
 * != 병원 방문
 * != 치료
 * != 수술
 * != 발병
 *
 * MIXED
 * != 건강 악화
 *
 * SUPPORTIVE
 * != 건강 회복
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - ClimateHealthAnalysis 재계산
 * - 조후 재계산
 * - 병약 재계산
 * - Remedy 재계산
 * - 통관 재계산
 * - 통합 충돌 재계산
 * - 교차검증 모순 재계산
 * - 여러 Match Status 합성
 * - SUPPORTIVE + PRESSURE -> MIXED 생성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 특정 질환 생성
 * - 특정 장기 생성
 * - 발병 예측
 * - 수명 예측
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class HealthClimateAdjustmentPressureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "HEALTH_CLIMATE_ADJUSTMENT_PRESSURE";

    private static final String
            HEALTH_LIFESTYLE_BALANCE_RULE_CODE =
            "HEALTH_LIFESTYLE_BALANCE";

    private static final LifeDomainEvidenceSourceType
            TARGET_SOURCE_TYPE =
            LifeDomainEvidenceSourceType
                    .CLIMATE_DISEASE_FLOW;


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
                "HealthClimateAdjustmentPressureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.HEALTH_TENDENCY
        ) {

            throw new IllegalArgumentException(
                    "HealthClimateAdjustmentPressureRule은 HEALTH_TENDENCY View만 평가할 수 있습니다."
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
         * CLIMATE_DISEASE_FLOW Match 조회
         * =================================================
         */

        List<LifeDomainRuleMatch> climateMatches =
                findClimateMatches(
                        view
                );

        if (
                climateMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Match 1개 -> Category Match 1개
         * =================================================
         */

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch climateMatch
                : climateMatches
        ) {

            LifeDomainRuleMatch normalizedMatch =
                    Objects.requireNonNull(
                            climateMatch,
                            "HEALTH CLIMATE_DISEASE_FLOW Match는 null일 수 없습니다."
                    );

            ConsultationCategoryDeterministicStatus categoryStatus =
                    toCategoryStatus(
                            normalizedMatch.status()
                    );

            List<String> reasons =
                    buildReasons(
                            normalizedMatch
                    );

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.HEALTH_TENDENCY,

                            ConsultationCategoryDeterministicLayer
                                    .NATAL,

                            RULE_CODE,

                            categoryStatus,

                            null,

                            null,

                            List.of(),

                            summaryOf(
                                    categoryStatus
                            ),

                            List.of(
                                    normalizedMatch
                            ),

                            reasons
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Climate Match 조회
     * =================================================
     *
     * 조건:
     *
     * 1.
     * HEALTH_LIFESTYLE
     *
     * 2.
     * HEALTH_LIFESTYLE_BALANCE
     *
     * 3.
     * Evidence 존재
     *
     * 4.
     * 모든 Evidence SourceType이
     * CLIMATE_DISEASE_FLOW
     */

    private List<LifeDomainRuleMatch>
    findClimateMatches(
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

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "HEALTH_TENDENCY Natal Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.HEALTH_LIFESTYLE
            ) {

                continue;
            }

            if (
                    !HEALTH_LIFESTYLE_BALANCE_RULE_CODE.equals(
                            normalized.ruleCode()
                    )
            ) {

                continue;
            }

            if (
                    !usesOnlyClimateSource(
                            normalized
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
     * CLIMATE_DISEASE_FLOW Source만 사용하는가
     * =================================================
     */

    private boolean usesOnlyClimateSource(
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

        for (
                LifeDomainEvidenceReference<?> evidence
                : match.evidences()
        ) {

            LifeDomainEvidenceReference<?> normalizedEvidence =
                    Objects.requireNonNull(
                            evidence,
                            "HEALTH Evidence는 null일 수 없습니다."
                    );

            if (
                    normalizedEvidence.sourceType()
                            != TARGET_SOURCE_TYPE
            ) {

                return false;
            }
        }

        return true;
    }


    /*
     * =================================================
     * Status 1 : 1 변환
     * =================================================
     *
     * 다른 Climate Match를 참조하지 않는다.
     *
     * upstream LifeDomainRuleMatch 하나가 가진
     * 공식 상태만 그대로 Category 상태로 보존한다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "HEALTH Climate Match에는 LifeDomainSignalStatus가 필요합니다."
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
     * Reasons
     * =================================================
     *
     * 최종 사용자 Story가 아니라
     * deterministic provenance 기록이다.
     */

    private List<String> buildReasons(
            LifeDomainRuleMatch sourceMatch
    ) {

        Objects.requireNonNull(
                sourceMatch,
                "HEALTH Climate Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "HEALTH_LIFESTYLE_BALANCE에서 이미 계산된 하나의 CLIMATE_DISEASE_FLOW Match를 HEALTH_TENDENCY Category Match로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 CLIMATE_DISEASE_FLOW Match와 결합하지 않고 그대로 Category Status로 변환했습니다."
        );

        reasons.add(
                "조후 필요, 명리적 병약 구조, Remedy 이후 미해결 상태, 통관 필요, 통합 충돌, 교차검증 모순 및 재검토 여부는 기존 ClimateHealthAnalysis와 HealthLifestyleBalanceRule이 이미 계산한 결과를 사용하며 이번 Rule에서 다시 계산하지 않았습니다."
        );

        reasons.add(
                "upstream Java가 integrationConflict 또는 crossValidationContradiction 같은 조건으로 실제 MIXED를 생성한 경우 해당 MIXED 자체는 유효한 deterministic 결과로 그대로 보존합니다."
        );

        reasons.add(
                "별도의 CLIMATE_DISEASE_FLOW Match가 SUPPORTIVE와 PRESSURE로 동시에 존재하더라도 Category 단계에서 이를 새 MIXED로 합성하지 않습니다."
        );

        reasons.add(
                "PRESSURE Match가 여러 개 존재하더라도 더 강한 PRESSURE 또는 건강 위험 점수로 변환하지 않습니다."
        );

        reasons.add(
                "LifeDomainRuleMatch의 summary 또는 reasons 문자열을 파싱하여 조후·병약·통관 등의 subtype을 재구성하지 않았습니다."
        );

        reasons.add(
                "PRESSURE와 MIXED는 명리 내부의 균형 조정 구조를 나타내며 실제 질환·장기 이상·발병·치료 필요성·수명과 같은 의료적 의미로 변환하지 않습니다."
        );

        reasons.add(
                "서로 다른 Climate Match가 함께 존재한다는 사실만으로 새로운 관계·인과·순서 또는 종합 상태를 생성하지 않았습니다."
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
     * 최종 의료 해석이나 사용자 Story가 아니다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "HEALTH Climate Category Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국의 하나의 조후·명리적 병약·통관 계산에서 SUPPORTIVE 상태가 확인되며 다른 Climate Match와 합성하지 않고 그대로 보존합니다.";

            case PRESSURE ->
                    "원국의 하나의 조후·명리적 병약·통관 계산에서 명리적 조정 부담을 나타내는 PRESSURE 상태가 확인되며 그대로 보존합니다.";

            case MIXED ->
                    "원국의 하나의 조후·명리적 병약·통관 계산 자체에서 MIXED 상태가 확인되며 해당 upstream 계산을 그대로 보존합니다.";

            case CONTEXTUAL ->
                    "원국의 하나의 조후·명리적 병약·통관 계산이 CONTEXTUAL 상태로 남아 있으며 다른 Match와 합성하지 않습니다.";
        };
    }
}