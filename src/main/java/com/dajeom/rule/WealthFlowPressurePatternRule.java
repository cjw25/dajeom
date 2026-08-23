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
 * WEALTH 카테고리의
 * 교차검증 재성 Pattern 보존 Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.WEALTH의:
 *
 * WEALTH_CROSS_VALIDATED_PATTERN
 *
 * 결과를 WEALTH Category에서
 * 각각 독립적인 deterministic 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * WEALTH_CROSS_VALIDATED_PATTERN은
 * 기존 LifeDomain 계산에서 이미:
 *
 * - 식상 -> 재성 Family Chain
 * - 비겁과 재성의 경쟁 / 분산 구조
 * - 재다신약 계열 구조
 * - 재성 -> 관성 구조
 * - 식상 -> 재성 -> 관살 -> 인성 전체 Chain
 *
 * 등을 계산한다.
 *
 * 또한 일부 Core Pattern은:
 *
 * - 격국
 * - 신강신약
 * - 용신
 * - 합화
 *
 * 와 10-9 단계에서 이미
 * 교차검증된 상태를 사용한다.
 *
 * =================================================
 * 매우 중요한 원칙
 * =================================================
 *
 * Java upstream에서 실제 계산된:
 *
 * - 생 관계
 * - 극 관계
 * - Family Chain
 * - Core Pattern
 * - Cross Validation
 *
 * 자체는 유효한 deterministic 계산이다.
 *
 * 이 Rule은 그런 계산을 삭제하지 않는다.
 *
 * =================================================
 * 이번 변경
 * =================================================
 *
 * 기존에는 여러
 * WEALTH_CROSS_VALIDATED_PATTERN Match의
 * Status를 한 번에 모아:
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
 * 같은 방식으로
 * Category 최종 상태를 새로 합성했다.
 *
 * 이제 그렇게 하지 않는다.
 *
 * =================================================
 * 독립 Match 보존
 * =================================================
 *
 * LifeDomainRuleMatch 1개
 *
 * ->
 *
 * ConsultationCategoryDeterministicMatch 1개
 *
 * 로 그대로 보존한다.
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
 * 로 각각 남긴다.
 *
 * A + B + C를 합쳐
 * 새로운 Category Status를 만들지 않는다.
 *
 * =================================================
 * 중요한 제한
 * =================================================
 *
 * 현재 LifeDomainRuleMatch에는
 * WEALTH_CROSS_VALIDATED_PATTERN 내부의
 * 세부 Pattern Type을 나타내는
 * 구조화된 subtype 필드가 없다.
 *
 * 따라서 이 Rule은:
 *
 * - summary 문자열
 * - reasons 문자열
 *
 * 을 파싱하여:
 *
 * - 비겁쟁재
 * - 재다신약
 * - 식상생재
 * - 재생관
 *
 * 을 다시 구분하지 않는다.
 *
 * Pattern Type 자체의 식별이
 * Category Contract에서 필요해지면
 * 추후 구조화된 subtype 필드를
 * 별도로 추가해야 한다.
 *
 * =================================================
 * Palace 분리
 * =================================================
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT는
 * 이 Rule에서 사용하지 않는다.
 *
 * Palace Distribution은 별도의:
 *
 * WealthFunctionDistributionContextRule
 *
 * 이 담당한다.
 *
 * 따라서:
 *
 * Pattern
 * +
 * Palace
 *
 * 를 이 Rule에서 새로 연결하지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 각 Category Match의 Status는
 * 해당 LifeDomainRuleMatch의
 * 공식 LifeDomainSignalStatus를
 * 1:1로 변환한다.
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
 * 관계형 풀이 금지
 * =================================================
 *
 * 서로 다른 Pattern 결과를 보고:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 결합된다
 * - A가 B를 강화한다
 * - A가 B를 약화한다
 * - 먼저 A가 나타나고 이후 B가 된다
 *
 * 같은 관계를 생성하지 않는다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * PRESSURE
 * != 돈을 잃는다
 *
 * SUPPORTIVE
 * != 돈을 많이 번다
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
 * - 재산 증가
 * - 재산 감소
 * - 횡재
 * - 부채 발생
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
 * - Pattern 간 Status 합성
 * - Pattern + Palace 합성
 * - Pattern Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 재물 사건 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 */
@Component
public class WealthFlowPressurePatternRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_FLOW_PRESSURE_PATTERN";

    private static final String
            CROSS_VALIDATED_PATTERN_RULE_CODE =
            "WEALTH_CROSS_VALIDATED_PATTERN";


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
                "WealthFlowPressurePatternRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "WealthFlowPressurePatternRule은 WEALTH View만 평가할 수 있습니다."
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
         * 공식 Source
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
         * 여러 Match를 하나의 Category Status로
         * 합성하지 않는다.
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

            ConsultationCategoryDeterministicStatus status =
                    toCategoryStatus(
                            normalized.status()
                    );

            /*
             * =================================================
             * Reasons
             * =================================================
             *
             * 사용자 Story가 아니라
             * deterministic provenance 기록이다.
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "WEALTH_CROSS_VALIDATED_PATTERN에서 이미 계산된 하나의 LifeDomain Match를 독립적인 WEALTH Category Match로 보존했습니다."
            );

            reasons.add(
                    "이 Match의 공식 LifeDomainSignalStatus를 다른 Pattern Match의 Status와 결합하지 않고 1:1로 Category Status에 보존했습니다."
            );

            reasons.add(
                    "WEALTH_CROSS_VALIDATED_PATTERN 내부의 세부 Pattern subtype은 현재 LifeDomainRuleMatch에 구조화되어 있지 않으므로 summary 또는 reasons 문자열을 파싱하여 재분류하지 않았습니다."
            );

            reasons.add(
                    "다른 WEALTH_CROSS_VALIDATED_PATTERN Match가 존재하더라도 이 Match와 합쳐 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 만들지 않았습니다."
            );

            reasons.add(
                    "WEALTH_PALACE_DISTRIBUTION_CONTEXT는 별도의 WealthFunctionDistributionContextRule이 담당하므로 이 Match에 결합하지 않았습니다."
            );

            reasons.add(
                    "이 결과는 upstream Java에서 실제 계산된 재성 관련 Pattern 또는 Chain의 상태만 보존하며 실제 수입·손실·투자·사업·재산 사건을 생성하지 않습니다."
            );

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
     * Source 구분 조건으로 사용하지 않는다.
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
     * Pattern subtype을 추측하지 않고
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
                    "교차검증된 하나의 재성 관련 Pattern 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "교차검증된 하나의 재성 관련 Pattern 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "교차검증된 하나의 재성 관련 Pattern 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "교차검증된 하나의 재성 관련 Pattern 결과가 CONTEXTUAL 상태로 보존됩니다.";
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