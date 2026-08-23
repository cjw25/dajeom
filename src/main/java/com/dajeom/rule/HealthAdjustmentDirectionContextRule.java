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
 * LifeDomain.HEALTH_LIFESTYLE의:
 *
 * HEALTH_LIFESTYLE_BALANCE
 *
 * 결과 가운데
 * USEFUL_GOD Source가 실제 포함된 Match를
 * HEALTH_TENDENCY의 독립 NATAL Context로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * HealthLifestyleBalanceRule은
 * UsefulGodAnalysis 결과를 사용하여
 * 다음을 각각 별도 LifeDomainRuleMatch로 만든다.
 *
 * -------------------------------------------------
 *
 * 1.
 * 최종 용신 조정축
 *
 * Evidence:
 *
 * CLIMATE_DISEASE_FLOW
 * +
 * USEFUL_GOD
 *
 * -------------------------------------------------
 *
 * 2.
 * 복수 용신 Context
 *
 * Evidence:
 *
 * USEFUL_GOD
 *
 * -------------------------------------------------
 *
 * 3.
 * 최종 용신 미확정 /
 * 복수 PRIMARY /
 * 추가 검토 Context
 *
 * Evidence:
 *
 * USEFUL_GOD
 *
 * -------------------------------------------------
 *
 * 위 Match들은 upstream Java에서
 * 이미 서로 다른 deterministic 결과로
 * 생성되어 있다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 기존 Category Rule은
 * USEFUL_GOD 관련 Match를 모두 모아:
 *
 * sourceMatches = [
 *     Match A,
 *     Match B,
 *     Match C
 * ]
 *
 * 형태의 하나의 Category Match로 만들었다.
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
 * 최종 용신 조정축 Match
 * ->
 * Category Match A
 *
 * 복수 용신 Match
 * ->
 * Category Match B
 *
 * 용신 미확정 / 검토 Match
 * ->
 * Category Match C
 *
 * 로 각각 독립적으로 유지한다.
 *
 * =================================================
 * 왜 분리하는가
 * =================================================
 *
 * 다음은 서로 다른 upstream 계산이다.
 *
 * - 최종 조정축 존재
 * - 복수 용신 존재
 * - 선택 불확실성 / 재검토 존재
 *
 * 이 결과들이 동시에 존재한다고 해서:
 *
 * - 더 강한 조정 필요
 * - 더 복잡한 건강 상태
 * - 더 높은 건강 위험
 * - 하나의 종합 상태
 *
 * 로 Category 단계에서 합치지 않는다.
 *
 * =================================================
 * Source 선택
 * =================================================
 *
 * 대상 Match에는:
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
 * =================================================
 * CLIMATE_DISEASE_FLOW 포함 경계
 * =================================================
 *
 * 최종 용신 조정축 Match에는:
 *
 * CLIMATE_DISEASE_FLOW
 * +
 * USEFUL_GOD
 *
 * 가 함께 존재할 수 있다.
 *
 * 이것은 Category에서
 * 두 Evidence를 임의로 연결한 것이 아니다.
 *
 * HealthLifestyleBalanceRule이
 * 최종 UsefulGodAnalysis 결과를
 * 보존하기 위해 upstream에서
 * 이미 만든 하나의 공식 Match다.
 *
 * 따라서 그 Match 자체를 그대로 보존한다.
 *
 * =================================================
 * CLIMATE 단독 Match 제외
 * =================================================
 *
 * CLIMATE_DISEASE_FLOW만 사용하는 Match는
 * 이 Rule에서 사용하지 않는다.
 *
 * 해당 Match는:
 *
 * HealthClimateDiseaseFlowDeterministicRule
 *
 * 의 책임이다.
 *
 * =================================================
 * Baseline Source 제외
 * =================================================
 *
 * 다음 Source는 이 Rule에서 사용하지 않는다.
 *
 * ELEMENT_STRENGTH
 * DAY_MASTER_STRENGTH
 *
 * 해당 기본 균형 계산은:
 *
 * HealthBaselineBalanceContextRule
 *
 * 이 담당한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 이 Rule이 생성하는 Category Status는
 * 항상:
 *
 * CONTEXTUAL
 *
 * 이다.
 *
 * 이유:
 *
 * 용신 존재
 * != 건강 좋음
 *
 * 희신 존재
 * != 건강 회복
 *
 * 기신 존재
 * != 건강 나쁨
 *
 * 복수 용신
 * != 건강 불안정
 *
 * 용신 미확정
 * != 건강 위험
 *
 * 이기 때문이다.
 *
 * =================================================
 * Source Status
 * =================================================
 *
 * 현재 upstream
 * HealthLifestyleBalanceRule에서
 * USEFUL_GOD 관련 Match는
 * CONTEXTUAL로 생성된다.
 *
 * 원본 Match는 sourceMatches에
 * 그대로 보존한다.
 *
 * Category에서 별도 길흉을 만들지 않는다.
 *
 * =================================================
 * UsefulGod 방법론 보존
 * =================================================
 *
 * UsefulGodAnalysis에는:
 *
 * - BALANCING
 * - CLIMATE_CONTROL
 * - STRUCTURE
 * - REMEDY
 * - PASSAGE
 * - FOLLOW_STRUCTURE
 *
 * 등 목적이 다른 용신 방법론이
 * 존재할 수 있다.
 *
 * Category에서는 이 방법론들을
 * 다시 계산하거나 단일 대표값으로
 * 강제 통합하지 않는다.
 *
 * =================================================
 * 복수 용신 경계
 * =================================================
 *
 * 복수 용신은 허용되는
 * deterministic 상태다.
 *
 * 따라서:
 *
 * usefulGodElements().get(0)
 *
 * 같은 방식으로 첫 후보를
 * 대표 용신으로 임의 선택하지 않는다.
 *
 * =================================================
 * 용신 미확정 경계
 * =================================================
 *
 * 다음 상태 역시 그대로 보존한다.
 *
 * - unresolvedUsefulGodSelection
 * - multiplePrimaryReview
 * - requiresReview
 *
 * 이를:
 *
 * - 분석 실패
 * - 건강 위험
 * - 질병 가능성
 *
 * 으로 변환하지 않는다.
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
 * - 용신 오행
 * - 희신 오행
 * - 기신 오행
 * - 용신법
 * - 복수 여부
 * - 미확정 여부
 *
 * 를 다시 판정하지 않는다.
 *
 * 실제 계산 정보는
 * 원본 LifeDomainRuleMatch와
 * UsefulGodAnalysis Evidence에
 * 그대로 남긴다.
 *
 * =================================================
 * Match 간 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * 최종 용신 존재
 * +
 * 복수 용신
 * =
 * 더 강한 Context
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * 복수 용신
 * +
 * 추가 검토
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * 최종 용신
 * +
 * 재검토
 * =
 * 건강 불안정
 *
 * 금지.
 *
 * =================================================
 * Provenance 경계
 * =================================================
 *
 * 여러 USEFUL_GOD Match가 존재하더라도
 * 동일한 UsefulGodAnalysis에서
 * 파생된 결과일 수 있다.
 *
 * 따라서 이후 Grounding / Prominence에서:
 *
 * - 독립 검증 횟수
 * - 근거 강도
 *
 * 를 단순 Match 개수만으로
 * 증가시켜서는 안 된다.
 *
 * 이번 Rule은 Match를 분리 보존할 뿐
 * 독립성까지 새로 부여하지 않는다.
 *
 * =================================================
 * 관계·인과 생성 금지
 * =================================================
 *
 * 다음 의미를 새로 생성하지 않는다.
 *
 * - 조후 문제가 있어서 용신이 필요하다
 * - 용신이 있으므로 균형이 회복된다
 * - 기신 때문에 건강 부담이 생긴다
 * - 복수 용신 때문에 상태가 복잡하다
 * - 미확정이므로 건강 위험이 있다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 같이 있으므로 C다
 *
 * =================================================
 * 실제 Java 계산과의 구분
 * =================================================
 *
 * UsefulGodAnalysis가 실제 Java에서:
 *
 * - 후보 생성
 * - 방법론별 평가
 * - PRIMARY 판정
 * - 복수 후보 유지
 * - 충돌 검토
 * - 최종 선택
 * - 미확정
 * - review 필요
 *
 * 를 계산한 결과는 그대로 인정한다.
 *
 * 금지하는 것은 Category 단계에서
 * 별개의 Match 존재만 보고
 * 새로운 관계나 상태를 만드는 것이다.
 *
 * =================================================
 * 오행-장기 대응 금지
 * =================================================
 *
 * 용신 木
 * != 간에 좋음
 *
 * 용신 火
 * != 심장에 좋음
 *
 * 용신 土
 * != 위장에 좋음
 *
 * 용신 金
 * != 폐에 좋음
 *
 * 용신 水
 * != 신장에 좋음
 *
 * 기신 역시 특정 장기 문제로
 * 변환하지 않는다.
 *
 * =================================================
 * 의료 사건 생성 금지
 * =================================================
 *
 * 용신 존재
 * != 건강 회복
 *
 * 기신 존재
 * != 발병
 *
 * 복수 용신
 * != 만성 문제
 *
 * 미확정
 * != 진단 불가
 *
 * review
 * != 병원 검사 필요
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * USEFUL_GOD 관련 Match가 여러 개라고:
 *
 * - 건강 위험이 높다
 * - 건강 관리가 더 필요하다
 * - 건강운이 강하다
 * - 신체 문제가 많다
 *
 * 로 계산하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - UsefulGodAnalysis 재계산
 * - 용신 후보 재생성
 * - 용신법 재평가
 * - 대표 용신 임의 선택
 * - 희신 재계산
 * - 기신 재계산
 * - 복수 용신 재판정
 * - 용신 미확정 재판정
 * - review 상태 재판정
 * - CLIMATE 단독 Match 소비
 * - Baseline Match 소비
 * - 여러 USEFUL_GOD Match 결합
 * - 여러 USEFUL_GOD Match Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 특정 장기 생성
 * - 특정 질환 생성
 * - 발병 예측
 * - 치료 예측
 * - 수명 예측
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
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
     * 이번 Rule에서 허용하는 Source
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
         * Category
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
         * USEFUL_GOD 관련 Match
         * =================================================
         */

        List<LifeDomainRuleMatch> adjustmentMatches =
                findAdjustmentMatches(
                        view
                );

        if (
                adjustmentMatches.isEmpty()
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
                LifeDomainRuleMatch adjustmentMatch
                : adjustmentMatches
        ) {

            LifeDomainRuleMatch normalizedMatch =
                    Objects.requireNonNull(
                            adjustmentMatch,
                            "HEALTH USEFUL_GOD Adjustment Match는 null일 수 없습니다."
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
     * USEFUL_GOD 관련 Match 선택
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
     * USEFUL_GOD Source 반드시 존재
     *
     * 5.
     * 모든 Evidence SourceType이:
     *
     * USEFUL_GOD
     * CLIMATE_DISEASE_FLOW
     *
     * 중 하나.
     */

    private List<LifeDomainRuleMatch>
    findAdjustmentMatches(
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
             * 구조화된 SourceType 판정
             * =================================================
             */

            if (
                    !isUsefulGodAdjustmentMatch(
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
     * USEFUL_GOD Adjustment Match 여부
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
         * =================================================
         * Evidence 필수
         * =================================================
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

            LifeDomainEvidenceReference<?> normalizedEvidence =
                    Objects.requireNonNull(
                            evidence,
                            "HEALTH Adjustment Evidence는 null일 수 없습니다."
                    );

            LifeDomainEvidenceSourceType sourceType =
                    normalizedEvidence
                            .sourceType();

            /*
             * =================================================
             * 허용 Source 외 Evidence 제외
             * =================================================
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
             * USEFUL_GOD 반드시 존재
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
         * =================================================
         * CLIMATE 단독 Match 제외
         * =================================================
         */

        return usefulGodExists;
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
                "HEALTH Adjustment Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "HEALTH_LIFESTYLE_BALANCE에서 이미 계산된 하나의 USEFUL_GOD 관련 LifeDomain Match를 HEALTH_TENDENCY의 독립 조정 방향 Context로 1:1 보존했습니다."
        );

        reasons.add(
                "이 Category Match의 sourceMatches에는 다른 USEFUL_GOD 관련 Match를 추가하지 않고 현재 하나의 upstream Match만 보존했습니다."
        );

        reasons.add(
                "원본 LifeDomain Match의 공식 상태는 "
                        + sourceMatch.status()
                        + "이며 용신·희신·기신 및 선택 상태를 건강 길흉으로 변환하지 않기 위해 Category Status는 CONTEXTUAL로 유지합니다."
        );

        reasons.add(
                "최종 용신 조정축 Match에서는 CLIMATE_DISEASE_FLOW와 USEFUL_GOD가 함께 존재할 수 있는데 이는 HealthLifestyleBalanceRule에서 이미 생성한 하나의 공식 upstream Match이며 Category에서 새로 결합한 것이 아닙니다."
        );

        reasons.add(
                "복수 용신 또는 용신 미확정·복수 PRIMARY·추가 검토 Match가 USEFUL_GOD 단독 Source로 존재하는 경우도 각각의 upstream Match 그대로 보존합니다."
        );

        reasons.add(
                "격국·억부·조후·병약·통관·종격 등 서로 다른 UsefulGod 방법론의 후보 생성·검토·수렴은 UsefulGodAnalysis가 이미 계산한 결과를 사용하며 이 Rule에서 다시 계산하지 않았습니다."
        );

        reasons.add(
                "여러 USEFUL_GOD 관련 Match가 동시에 존재하더라도 서로 합쳐 새로운 종합 Status, 조정 강도 또는 건강 위험도를 생성하지 않았습니다."
        );

        reasons.add(
                "여러 USEFUL_GOD 관련 Match가 동일 UsefulGodAnalysis에서 파생될 수 있으므로 Match 개수를 독립 검증 횟수나 근거 강도로 그대로 사용해서는 안 됩니다."
        );

        reasons.add(
                "LifeDomainRuleMatch의 summary 또는 reasons 문자열을 파싱하여 용신 오행·희신 오행·기신 오행·용신법·복수 여부·미확정 여부를 재판정하지 않았습니다."
        );

        reasons.add(
                "용신·희신·기신을 특정 장기·질환·발병·치료·수술·수명과 연결하지 않았습니다."
        );

        reasons.add(
                "서로 다른 조정 Match가 함께 존재한다는 사실만으로 새로운 관계·인과·순서 또는 종합 의미를 생성하지 않았습니다."
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
     * Source 세부 subtype은
     * 문자열로 재구성하지 않는다.
     */

    private String summaryOf() {

        return "원국의 UsefulGodAnalysis를 통해 이미 계산된 하나의 명리적 조정 방향 Context를 HEALTH_TENDENCY에 독립적으로 보존하며 건강의 길흉이나 의료적 상태로 변환하지 않습니다.";
    }
}