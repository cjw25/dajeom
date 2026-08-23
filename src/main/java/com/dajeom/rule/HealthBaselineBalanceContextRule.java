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
 * 기존 LifeDomain:
 *
 * HEALTH_LIFESTYLE_BALANCE
 *
 * 안에서 이미 독립적으로 계산된:
 *
 * - 오행 세력 분포 Context
 * - 일간 지원력·압력 / 수용·소모 Context
 * - 극단 신강·신약 구조 검토 Context
 *
 * 를 HEALTH_TENDENCY의
 * 독립적인 NATAL Context로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * HealthLifestyleBalanceRule은
 * ElementStrengthAnalysis와
 * DayMasterStrengthAnalysis를 사용하여
 * 다음 Match들을 각각 따로 생성한다.
 *
 * -------------------------------------------------
 *
 * 1.
 * ELEMENT_STRENGTH
 *
 * ->
 *
 * 원국의 상대적인 오행 세력 분포 Context
 *
 * -------------------------------------------------
 *
 * 2.
 * ELEMENT_STRENGTH
 * +
 * DAY_MASTER_STRENGTH
 *
 * ->
 *
 * 일간의 지원력·압력 관계와
 * 수용·소모 Context
 *
 * -------------------------------------------------
 *
 * 3.
 * DAY_MASTER_STRENGTH
 *
 * ->
 *
 * extreme=true인 경우
 * 극단 강약 구조 검토 Context
 *
 * -------------------------------------------------
 *
 * 위 세 계산은 upstream Java에서
 * 이미 각각 독립적인
 * LifeDomainRuleMatch로 생성된다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 기존 Category Rule은
 * 위 Match들을 모두 모아:
 *
 * sourceMatches = [
 *     Match A,
 *     Match B,
 *     Match C
 * ]
 *
 * 형태의 하나의
 * ConsultationCategoryDeterministicMatch로
 * 만들었다.
 *
 * 이제 그렇게 하지 않는다.
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
 * 따라서:
 *
 * ELEMENT_STRENGTH Match
 *
 * ->
 *
 * Category Match A
 *
 * -------------------------------------------------
 *
 * ELEMENT_STRENGTH
 * +
 * DAY_MASTER_STRENGTH Match
 *
 * ->
 *
 * Category Match B
 *
 * -------------------------------------------------
 *
 * DAY_MASTER_STRENGTH extreme Match
 *
 * ->
 *
 * Category Match C
 *
 * 로 각각 독립적으로 유지된다.
 *
 * =================================================
 * 왜 분리하는가
 * =================================================
 *
 * 다음은 서로 다른 upstream 계산이다.
 *
 * - 오행 상대 분포
 * - 일간 지원력·압력
 * - extreme 구조 검토
 *
 * 이들이 동시에 존재한다고 해서:
 *
 * - 서로 강화한다
 * - 서로 보완한다
 * - 하나의 건강 구조다
 * - 함께 있으므로 더 중요하다
 *
 * 같은 새로운 의미를
 * Category 단계에서 만들지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 이 Category Rule이 생성하는 Status는
 * 항상:
 *
 * CONTEXTUAL
 *
 * 이다.
 *
 * 이유:
 *
 * - 오행의 강약 분포
 * - 신강 / 신약
 * - 지원력 / 압력
 * - extreme 여부
 *
 * 자체는 의료적 건강 상태의
 * 좋음 / 나쁨이 아니기 때문이다.
 *
 * =================================================
 * Source Status
 * =================================================
 *
 * 현재 upstream
 * HealthLifestyleBalanceRule의
 * baseline 관련 Match들은
 * 모두 CONTEXTUAL이다.
 *
 * 원본 Match 자체는
 * sourceMatches에 그대로 보존한다.
 *
 * Category에서는
 * baseline 구조를 SUPPORTIVE 또는 PRESSURE로
 * 새로 변환하지 않는다.
 *
 * =================================================
 * 구조화된 SourceType 사용
 * =================================================
 *
 * LifeDomainRuleMatch의 summary 또는 reasons를
 * 문자열 파싱하지 않는다.
 *
 * 다음 구조화된 SourceType만 사용한다.
 *
 * ELEMENT_STRENGTH
 * DAY_MASTER_STRENGTH
 *
 * =================================================
 * 허용되는 Source 조합
 * =================================================
 *
 * 다음은 허용된다.
 *
 * ELEMENT_STRENGTH
 *
 * ELEMENT_STRENGTH
 * +
 * DAY_MASTER_STRENGTH
 *
 * DAY_MASTER_STRENGTH
 *
 * =================================================
 * 제외 Source
 * =================================================
 *
 * 다음 Source가 하나라도 포함된 Match는
 * 이번 Rule 대상에서 제외한다.
 *
 * CLIMATE_DISEASE_FLOW
 * USEFUL_GOD
 *
 * -------------------------------------------------
 *
 * CLIMATE_DISEASE_FLOW
 *
 * ->
 *
 * HealthClimateDiseaseFlowDeterministicRule
 *
 * -------------------------------------------------
 *
 * USEFUL_GOD 계열
 *
 * ->
 *
 * HealthAdjustmentDirectionContextRule
 *
 * 에서 별도로 처리한다.
 *
 * =================================================
 * 관계형 계산 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * 오행 불균형
 * +
 * 신약
 * =
 * 건강 Pressure
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * 신약
 * +
 * extreme
 * =
 * 더 큰 건강 부담
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * 강한 오행
 * +
 * 약한 오행
 * =
 * 특정 건강 패턴
 *
 * 금지.
 *
 * =================================================
 * 실제 upstream 계산 보존
 * =================================================
 *
 * DayMasterStrengthAnalysis가 실제 Java에서
 * 계산한:
 *
 * - supportPower
 * - pressurePower
 * - strengthBasisPoints
 * - level
 * - extreme
 * - requiresFollowStructureReview
 *
 * 등은 그대로 유효하다.
 *
 * ElementStrengthAnalysis가 계산한:
 *
 * - 각 오행 finalPower
 * - sharePercent
 * - strongestElement
 * - weakestExistingElement
 *
 * 등도 그대로 유효하다.
 *
 * Category 단계에서
 * 그 계산을 다시 하지 않을 뿐이다.
 *
 * =================================================
 * 신강·신약 의료 변환 금지
 * =================================================
 *
 * 신강
 * != 건강함
 *
 * 신약
 * != 허약함
 *
 * 신약
 * != 질병
 *
 * 극신약
 * != 중증 질환
 *
 * 극신강
 * != 체력이 강함
 *
 * =================================================
 * 오행-장기 직접 대응 금지
 * =================================================
 *
 * 木 약함
 * != 간 질환
 *
 * 火 약함
 * != 심장 질환
 *
 * 土 약함
 * != 위장 질환
 *
 * 金 약함
 * != 폐 질환
 *
 * 水 약함
 * != 신장 질환
 *
 * =================================================
 * strongest / weakest 경계
 * =================================================
 *
 * strongestElement
 *
 * 는 원국 안의 상대적인
 * 세력 우위 정보다.
 *
 * -------------------------------------------------
 *
 * weakestExistingElement
 *
 * 는 원국에 실제 존재하는 오행 가운데
 * 상대적으로 가장 약한 오행이다.
 *
 * -------------------------------------------------
 *
 * 이 정보는:
 *
 * - 과다 질환
 * - 부족 질환
 * - 장기 취약성
 *
 * 판정이 아니다.
 *
 * =================================================
 * extreme 경계
 * =================================================
 *
 * DayMasterStrengthAnalysis.extreme()
 *
 * 은:
 *
 * - 극단 강약 구조
 * - 종격 가능성
 * - 특별 구조 검토
 *
 * 등의 명리적 검토 Context다.
 *
 * HEALTH_TENDENCY의
 * 위험도 점수가 아니다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * baseline Match가 1개보다 많다고:
 *
 * - 건강 위험이 높다
 * - 균형이 더 나쁘다
 * - 체력이 더 약하다
 * - 문제가 더 많다
 *
 * 로 계산하지 않는다.
 *
 * =================================================
 * 관계·인과·순서 생성 금지
 * =================================================
 *
 * Category 단계에서:
 *
 * - 오행 불균형 때문에 신약하다
 * - 신약 때문에 소모가 커진다
 * - extreme이므로 건강 문제가 이어진다
 * - 먼저 균형이 무너지고 이후 건강 부담이 생긴다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 같이 있으므로 C다
 *
 * 같은 의미를 새로 생성하지 않는다.
 *
 * =================================================
 * 의료 사건 생성 금지
 * =================================================
 *
 * CONTEXTUAL
 * != 건강함
 *
 * CONTEXTUAL
 * != 질병 없음
 *
 * extreme
 * != 발병
 *
 * 신약
 * != 병원 방문
 *
 * 오행 편중
 * != 치료 필요
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
 * 를 파싱하여:
 *
 * - 어떤 오행이 강한지
 * - 어떤 오행이 약한지
 * - 신강인지 신약인지
 * - extreme인지
 *
 * 를 Category에서 재판정하지 않는다.
 *
 * 정확한 구조 정보는
 * 원본 LifeDomainRuleMatch와
 * Evidence source에 그대로 남긴다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - ElementStrengthAnalysis 재계산
 * - DayMasterStrengthAnalysis 재계산
 * - strongestElement 재계산
 * - weakestExistingElement 재계산
 * - 신강신약 재계산
 * - extreme 재계산
 * - 여러 baseline Match 결합
 * - 여러 baseline Match Status 합성
 * - baseline Match 개수 점수화
 * - CLIMATE Source 혼합
 * - USEFUL_GOD Source 혼합
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 오행-장기 매핑
 * - 특정 질환 생성
 * - 발병 예측
 * - 수명 예측
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
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
         * Category
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
         * Baseline Source Match 선택
         * =================================================
         */

        List<LifeDomainRuleMatch> baselineMatches =
                findBaselineMatches(
                        view
                );

        if (
                baselineMatches.isEmpty()
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
                LifeDomainRuleMatch baselineMatch
                : baselineMatches
        ) {

            LifeDomainRuleMatch normalizedMatch =
                    Objects.requireNonNull(
                            baselineMatch,
                            "HEALTH Baseline Match는 null일 수 없습니다."
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

                            ConsultationCategoryDeterministicStatus
                                    .CONTEXTUAL,

                            null,

                            null,

                            List.of(),

                            summaryOf(),

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
     * 기본 균형 Match 선택
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
     * Evidence가 비어 있지 않음
     *
     * 4.
     * 모든 SourceType이:
     *
     * ELEMENT_STRENGTH
     * DAY_MASTER_STRENGTH
     *
     * 중 하나.
     */

    private List<LifeDomainRuleMatch>
    findBaselineMatches(
            ConsultationCategoryEvidenceView view
    ) {

        Objects.requireNonNull(
                view,
                "HEALTH_TENDENCY Evidence View가 필요합니다."
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

            /*
             * =================================================
             * Domain
             * =================================================
             */

            if (
                    normalized.domain()
                            != LifeDomain.HEALTH_LIFESTYLE
            ) {

                continue;
            }

            /*
             * =================================================
             * Upstream Rule
             * =================================================
             */

            if (
                    !HEALTH_LIFESTYLE_BALANCE_RULE_CODE.equals(
                            normalized.ruleCode()
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
                    !usesOnlyBaselineSources(
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
     * Baseline Source만 사용하는가
     * =================================================
     *
     * 허용:
     *
     * ELEMENT_STRENGTH
     *
     * ELEMENT_STRENGTH
     * +
     * DAY_MASTER_STRENGTH
     *
     * DAY_MASTER_STRENGTH
     *
     * -------------------------------------------------
     *
     * 금지:
     *
     * CLIMATE_DISEASE_FLOW
     * USEFUL_GOD
     *
     * 등이 하나라도 포함된 Match.
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

        for (
                LifeDomainEvidenceReference<?> evidence
                : match.evidences()
        ) {

            LifeDomainEvidenceReference<?> normalizedEvidence =
                    Objects.requireNonNull(
                            evidence,
                            "HEALTH Baseline Evidence는 null일 수 없습니다."
                    );

            if (
                    !BASELINE_SOURCE_TYPES.contains(
                            normalizedEvidence
                                    .sourceType()
                    )
            ) {

                return false;
            }
        }

        return true;
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
                "HEALTH Baseline Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "HEALTH_LIFESTYLE_BALANCE에서 이미 독립적으로 계산된 하나의 기본 균형 LifeDomain Match를 HEALTH_TENDENCY Category Context로 1:1 보존했습니다."
        );

        reasons.add(
                "이 Match는 ELEMENT_STRENGTH와 DAY_MASTER_STRENGTH Source만 사용하며 CLIMATE_DISEASE_FLOW 또는 USEFUL_GOD Source와 결합하지 않았습니다."
        );

        reasons.add(
                "원본 LifeDomain Match의 상태는 "
                        + sourceMatch.status()
                        + "이며 오행 분포·일간 수용력·극단 강약 구조를 의료적 길흉으로 변환하지 않기 위해 Category Status는 CONTEXTUAL로 유지합니다."
        );

        reasons.add(
                "다른 HEALTH baseline Match를 같은 sourceMatches에 묶지 않았으며 하나의 upstream Match만 이 Category Match의 provenance로 사용했습니다."
        );

        reasons.add(
                "오행 세력 분포, 일간 지원력·압력 관계, extreme 구조 검토는 HealthLifestyleBalanceRule과 그 upstream 분석이 이미 계산한 결과를 그대로 사용하며 이 Rule에서 다시 계산하지 않았습니다."
        );

        reasons.add(
                "여러 baseline Match가 동시에 존재하더라도 서로 강화·약화·보완한다고 판정하거나 하나의 종합 건강 상태로 합성하지 않았습니다."
        );

        reasons.add(
                "LifeDomainRuleMatch의 summary 또는 reasons 문자열을 파싱하여 오행·신강신약·extreme subtype을 재판정하지 않았습니다."
        );

        reasons.add(
                "신강·신약·오행 편중·extreme 정보를 특정 장기·질환·발병 가능성·치료 필요성·수명과 연결하지 않았습니다."
        );

        reasons.add(
                "baseline Match 개수를 건강 위험도·체력·허약도·수명 등의 점수로 사용하지 않았습니다."
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
     * 최종 사용자 의료 해석이 아니다.
     *
     * Source의 세부 종류를 문자열로
     * 재구성하지 않기 때문에
     * 모든 baseline Match에 동일한
     * 중립 Context Summary를 사용한다.
     */

    private String summaryOf() {

        return "원국에서 이미 계산된 하나의 오행 분포 또는 일간 수용·소모 관련 기본 구조를 HEALTH_TENDENCY의 독립 Context로 보존하며 의료적 건강 상태로 변환하지 않습니다.";
    }
}