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
 * 원국 조후·병약·통관 조정 부담 Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 기존:
 *
 * HEALTH_LIFESTYLE_BALANCE
 *
 * 안에는:
 *
 * - 오행 세력
 * - 일간 강약
 * - 조후
 * - 명리적 병약
 * - 통관
 * - 용신
 *
 * 관련 Match가 하나의 ruleCode 아래
 * 함께 존재한다.
 *
 * 이 Rule은 그중:
 *
 * CLIMATE_DISEASE_FLOW
 *
 * Source만 사용하는 Match를 구조적으로 분리하여:
 *
 * "원국에서 한난조습·명리적 병약·통관 등의
 * 조정 필요가 어느 방향으로 존재하는가"
 *
 * 를 HEALTH_TENDENCY의 별도 축으로 판단한다.
 *
 * =================================================
 * 사용하는 upstream 판단
 * =================================================
 *
 * 기존 HealthLifestyleBalanceRule에서
 * CLIMATE_DISEASE_FLOW Source는 다음과 같은
 * deterministic 결과에 사용된다.
 *
 * - 조후 조정 필요
 * - 의미 있는 명리적 병약 구조
 * - 미해결 명리적 병약 구조
 * - 의미 있는 통관 필요
 * - 조후·병약·통관 통합 충돌
 * - 후보 교차검증 모순
 * - 추가 재검토 필요
 *
 * 이 Rule은 위 판단을 다시 계산하지 않는다.
 *
 * =================================================
 * Source 분리 방식
 * =================================================
 *
 * LifeDomainRuleMatch의:
 *
 * - summary
 * - reasons
 *
 * 문자열을 읽어서:
 *
 * "조후"
 * "병약"
 * "통관"
 *
 * 을 재구성하지 않는다.
 *
 * 오직 구조화된:
 *
 * LifeDomainEvidenceReference.sourceType()
 *
 * 만 사용한다.
 *
 * 이번 Rule의 대상 Match는
 * 모든 Evidence Source가:
 *
 * CLIMATE_DISEASE_FLOW
 *
 * 인 Match만 허용한다.
 *
 * =================================================
 * 제외
 * =================================================
 *
 * ELEMENT_STRENGTH
 * DAY_MASTER_STRENGTH
 *
 * Source는:
 *
 * HealthBaselineBalanceContextRule
 *
 * 의 책임이다.
 *
 * -------------------------------------------------
 *
 * USEFUL_GOD
 *
 * Source가 포함된 Match는:
 *
 * 이후 HealthAdjustmentDirectionContextRule
 *
 * 에서 별도로 다룬다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 이번 Rule은 CONTEXTUAL 고정이 아니다.
 *
 * 기존 6차 ClimateHealthAnalysis가 이미
 * 조정 부담 또는 충돌 방향을 deterministic하게
 * 확정했기 때문에 이를 보존한다.
 *
 * -------------------------------------------------
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
 * 그 외
 * -> CONTEXTUAL
 *
 * -------------------------------------------------
 *
 * 현재 upstream에서는 일반적으로:
 *
 * PRESSURE
 * MIXED
 * CONTEXTUAL
 *
 * 이 사용되지만,
 * 공식 Status 전체를 안전하게 처리한다.
 *
 * =================================================
 * 중요한 의미 제한
 * =================================================
 *
 * PRESSURE는:
 *
 * "질병이 있다"
 *
 * 를 뜻하지 않는다.
 *
 * 명리 내부에서:
 *
 * - 한난조습 조정 필요
 * - 병약 구조의 균형 저해
 * - 통관 필요
 *
 * 같은 구조적 조정 부담이
 * 존재한다는 뜻이다.
 *
 * MIXED 역시:
 *
 * "건강 상태가 불안정하다"
 *
 * 는 의료판정이 아니다.
 *
 * 서로 다른 명리적 조정 방향이
 * 동시에 존재하거나 충돌한다는 뜻이다.
 *
 * =================================================
 * 의료적 해석 금지
 * =================================================
 *
 * 이 Rule은:
 *
 * - 특정 질환
 * - 특정 장기
 * - 발병
 * - 치료
 * - 수술
 * - 수명
 * - 건강검진 결과
 *
 * 를 예측하지 않는다.
 *
 * 또한:
 *
 * 명리적 병약
 * ->
 * 실제 질병
 *
 * 으로 변환하지 않는다.
 *
 * =================================================
 * Match 개수
 * =================================================
 *
 * PRESSURE Match가 3개 있다고 해서
 * 1개보다 건강 위험이 3배라고 보지 않는다.
 *
 * Match 개수는:
 *
 * - 질병 가능성
 * - 건강 위험도
 * - 수명
 * - 체력
 *
 * 점수가 아니다.
 *
 * 공식 Status의 존재 조합만 사용한다.
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
         * Category 검증
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
         * CLIMATE_DISEASE_FLOW Match 분리
         * =================================================
         */

        List<LifeDomainRuleMatch>
                climateMatches =
                findClimateMatches(
                        view
                );

        /*
         * 조후·병약·통관 관련
         * deterministic Match가 없다면
         * 이번 질문에 답할 근거가 없다.
         */

        if (
                climateMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Status 결합
         * =================================================
         */

        ConsultationCategoryDeterministicStatus
                finalStatus =
                resolveClimateStatus(
                        climateMatches
                );

        /*
         * =================================================
         * Reasons
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "기존 HEALTH_LIFESTYLE_BALANCE 결과 가운데 CLIMATE_DISEASE_FLOW Source만 사용하는 Match를 구조적으로 분리하여 조후·명리적 병약·통관 조정축의 상태를 확인했습니다."
        );

        reasons.add(
                "조후 필요, 명리적 병약 구조, 통관 필요, 조정 방향 충돌 등의 세부 판단은 기존 ClimateHealthAnalysis가 이미 계산한 결과를 사용하며 이번 Rule에서 다시 계산하지 않았습니다."
        );

        reasons.add(
                "LifeDomainRuleMatch의 summary 또는 reasons 문자열을 파싱해 조후·병약·통관 subtype을 재구성하지 않았습니다."
        );

        appendStatusReason(
                reasons,
                finalStatus
        );

        reasons.add(
                "PRESSURE는 명리적 균형 조정 필요를 뜻하며 실제 질환, 장기 이상, 발병 또는 치료 필요성을 의미하지 않습니다."
        );

        reasons.add(
                "MIXED는 서로 다른 명리적 조정 방향 또는 교차검증 근거가 함께 존재함을 뜻하며 의료적 건강 상태의 불안정성을 뜻하지 않습니다."
        );

        reasons.add(
                "CLIMATE_DISEASE_FLOW Match 개수의 많고 적음을 건강 위험도, 체력, 질병 확률 또는 수명 점수로 사용하지 않았습니다."
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

                        finalStatus,

                        null,

                        null,

                        List.of(),

                        summaryOf(
                                finalStatus
                        ),

                        List.copyOf(
                                climateMatches
                        ),

                        List.copyOf(
                                reasons
                        )
                )
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
     * HEALTH_LIFESTYLE Domain
     *
     * 2.
     * HEALTH_LIFESTYLE_BALANCE Rule
     *
     * 3.
     * Evidence가 최소 하나 존재
     *
     * 4.
     * 모든 Evidence SourceType이
     * CLIMATE_DISEASE_FLOW
     *
     * 이어야 한다.
     *
     * -------------------------------------------------
     *
     * 따라서:
     *
     * CLIMATE_DISEASE_FLOW
     * +
     * USEFUL_GOD
     *
     * 를 함께 사용하는 Match는
     * 이번 Rule에 포함되지 않는다.
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
             * 구조화된 Evidence Source
             * =================================================
             */

            if (
                    !usesOnlyClimateSource(
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
     * CLIMATE_DISEASE_FLOW만 사용하는가
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

        boolean climateSourceExists =
                false;

        for (
                LifeDomainEvidenceReference<?> evidence
                : match.evidences()
        ) {

            Objects.requireNonNull(
                    evidence,
                    "HEALTH Evidence는 null일 수 없습니다."
            );

            if (
                    evidence.sourceType()
                            != TARGET_SOURCE_TYPE
            ) {

                return false;
            }

            climateSourceExists =
                    true;
        }

        return climateSourceExists;
    }

    /*
     * =================================================
     * Climate Family Status
     * =================================================
     *
     * Match 개수를 비교하지 않는다.
     *
     * 공식 Status의 존재 여부만 사용한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveClimateStatus(
            List<LifeDomainRuleMatch> matches
    ) {

        Objects.requireNonNull(
                matches,
                "HEALTH Climate Match 목록이 필요합니다."
        );

        boolean supportiveExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.MIXED
                );

        /*
         * =================================================
         * 이미 upstream에 MIXED가 존재
         * =================================================
         *
         * 조후·병약·통관 방향 사이의
         * 실제 충돌 / 모순을 보존한다.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 지원 + 부담 동시 존재
         * =================================================
         */

        if (
                supportiveExists
                        && pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 조정 부담 존재
         * =================================================
         */

        if (
                pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        /*
         * =================================================
         * 지원 방향 존재
         * =================================================
         *
         * 현재 upstream에서는
         * 일반적으로 사용되지 않더라도
         * 공식 Status 전체를 처리한다.
         */

        if (
                supportiveExists
        ) {

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        /*
         * =================================================
         * 재검토 Context 등
         * =================================================
         */

        return ConsultationCategoryDeterministicStatus.CONTEXTUAL;
    }

    /*
     * =================================================
     * Status 존재 여부
     * =================================================
     */

    private boolean hasStatus(

            List<LifeDomainRuleMatch> matches,

            LifeDomainSignalStatus status

    ) {

        Objects.requireNonNull(
                matches,
                "HEALTH Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "LifeDomainSignalStatus가 필요합니다."
        );

        return matches
                .stream()
                .anyMatch(
                        match ->
                                match.status()
                                        == status
                );
    }

    /*
     * =================================================
     * Status Reason
     * =================================================
     */

    private void appendStatusReason(

            List<String> reasons,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                reasons,
                "reasons가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "HEALTH deterministic status가 필요합니다."
        );

        switch (
                status
        ) {

            case SUPPORTIVE ->

                    reasons.add(
                            "조후·병약·통관 관련 공식 Source Family에서 SUPPORTIVE 방향이 확인되고 이를 뒤집는 PRESSURE 또는 MIXED 상태가 없어 SUPPORTIVE로 판단합니다."
                    );

            case PRESSURE ->

                    reasons.add(
                            "조후·명리적 병약·통관 관련 공식 Source Family에서 균형 조정 필요를 나타내는 PRESSURE가 확인되어 이를 보존합니다."
                    );

            case MIXED ->

                    reasons.add(
                            "조후·병약·통관 조정축 사이에 이미 MIXED가 확인되었거나 서로 다른 방향의 공식 상태가 함께 존재하여 한 방향으로 단순화하지 않고 MIXED로 판단합니다."
                    );

            case CONTEXTUAL ->

                    reasons.add(
                            "조후·병약·통관 관련 검토 Context는 존재하지만 현재 공식 상태만으로 명리적 조정 부담 방향을 확정할 수 없어 CONTEXTUAL로 유지합니다."
                    );
        }
    }

    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 최종 사용자 storyText가 아니다.
     *
     * 의료 진단 표현을 사용하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "HEALTH deterministic status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국의 조후·병약·통관 조정축에서 비교적 지지되는 방향이 확인됩니다.";

            case PRESSURE ->
                    "원국의 조후·명리적 병약·통관 구조에서 생활 균형을 조정해야 하는 명리적 부담 방향이 확인됩니다.";

            case MIXED ->
                    "원국의 조후·명리적 병약·통관 조정축에서 복수의 방향이나 상충하는 근거가 함께 존재합니다.";

            case CONTEXTUAL ->
                    "원국의 조후·명리적 병약·통관 구조에서 추가 검토가 필요한 조정 Context가 존재하지만 한 방향의 부담으로 단정하지 않습니다.";
        };
    }
}