package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
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
 * WEALTH 현재 명리연도 Functional deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.WEALTH에서 이미 계산된:
 *
 * WEALTH_ANNUAL_FUNCTIONAL
 *
 * 결과를 ConsultationCategory.WEALTH의
 * 독립적인 CURRENT_TIMELINE 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * WealthAnnualFunctionalRule은 이미:
 *
 * - Annual RESOURCE_REALITY Capacity
 * - 현재 Timeline이 실제 선택한 대운 Functional Segment
 * - Resolved RESOURCE_REALITY
 * - RESOURCE_REALITY 관련 CrossFunctional Pattern
 * - Context Qualification
 *
 * 을 하나의 ANNUAL_FUNCTIONAL_CONTEXT 안에서 계산한다.
 *
 * 이 Category Rule에서는 위 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * 중요한 원칙
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
 * 여러 Annual Functional Match가 존재하더라도
 * 서로 합쳐 하나의 Status를 만들지 않는다.
 *
 * =================================================
 * WEALTH_ANNUAL_CONTEXT와의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * WEALTH_ANNUAL_CONTEXT
 *
 * 를 사용하지 않는다.
 *
 * 일반 Annual Context는:
 *
 * WealthCurrentStructuralFlowRule
 *
 * 에서 독립적으로 처리한다.
 *
 * 따라서:
 *
 * ANNUAL_CONTEXT
 * +
 * ANNUAL_FUNCTIONAL
 *
 * 을 결합하여 새로운 Status를 만들지 않는다.
 *
 * =================================================
 * Provenance Family
 * =================================================
 *
 * WEALTH_ANNUAL_CONTEXT와
 * WEALTH_ANNUAL_FUNCTIONAL은
 * 같은 명리연도에서 파생된
 * 연결된 Annual Context다.
 *
 * 따라서 이후 Category Contract / Prominence에서:
 *
 * - 독립 검증 2개
 * - 강도 2배
 * - 근거 2개 누적
 *
 * 로 취급하면 안 된다.
 *
 * 현재 Prominence 계층의
 * ANNUAL_CONTEXT_FAMILY 규칙을 그대로 따른다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 해당 WEALTH_ANNUAL_FUNCTIONAL
 * LifeDomainRuleMatch의 공식 Status를
 * 그대로 1:1 변환한다.
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
 * 다음 의미를 새로 생성하지 않는다.
 *
 * - 대운 때문에 세운 재성 기능이 변한다
 * - Annual Functional이 일반 세운 흐름을 강화한다
 * - Annual Functional이 기존 부담을 완화한다
 * - RESOURCE_REALITY가 활성화되어 돈이 들어온다
 * - 재성 기능이 작동해서 수입이 생긴다
 * - A 때문에 B
 * - A가 B로 이어진다
 *
 * Upstream Java가 실제 계산한
 * Annual Functional Status만 보존한다.
 *
 * =================================================
 * Upstream 관계 계산
 * =================================================
 *
 * WealthAnnualFunctionalRule 내부에서 사용하는
 * CrossFunctional Pattern은 upstream Java가 실제로
 * 계산한 typed deterministic 결과다.
 *
 * 이 Rule은 그 계산을 재구성하지 않으며,
 * 새로운 Pattern 관계도 추가하지 않는다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 수입 증가
 *
 * PRESSURE
 * != 금전 손실
 *
 * MIXED
 * != 재물 기복
 *
 * CONTEXTUAL
 * != 특별한 재물 변화 없음
 *
 * 또한 다음 사건을 생성하지 않는다.
 *
 * - 매출 증가
 * - 급여 증가
 * - 투자 성공
 * - 투자 실패
 * - 손실
 * - 횡재
 * - 자산 증가
 * - 부채 발생
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각 Segment의 실제 Annual Functional Match를
 * 각각 독립적으로 보존한다.
 *
 * 특정 Segment 하나를 대표값으로
 * 임의 선택하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Annual Functional 재계산
 * - RESOURCE_REALITY 재계산
 * - CrossFunctional Pattern 재계산
 * - 일반 Annual Context와 Status 합성
 * - Decade Context와 Status 합성
 * - Monthly Context와 Status 합성
 * - Natal Status와 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 재물 사건 생성
 */
@Component
public class WealthAnnualFunctionalDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_CURRENT_ANNUAL_FUNCTIONAL";

    private static final String
            ANNUAL_FUNCTIONAL_RULE_CODE =
            "WEALTH_ANNUAL_FUNCTIONAL";


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
                "WealthAnnualFunctionalDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthAnnualFunctionalDeterministicRule은 WEALTH View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        if (
                !view.hasSegmentEvidences()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        /*
         * =================================================
         * Segment별 독립 처리
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segment,
                            "WEALTH Annual Functional Segment는 null일 수 없습니다."
                    );

            List<LifeDomainRuleMatch> functionalMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            ANNUAL_FUNCTIONAL_RULE_CODE
                    );

            if (
                    functionalMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * Match 1개 -> Category Match 1개
             * =================================================
             */

            for (
                    LifeDomainRuleMatch functionalMatch
                    : functionalMatches
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                functionalMatch,
                                "WEALTH Annual Functional Match는 null일 수 없습니다."
                        );

                ConsultationCategoryDeterministicStatus status =
                        toCategoryStatus(
                                normalizedMatch.status()
                        );

                List<String> reasons =
                        buildReasons(
                                normalizedSegment,
                                status
                        );

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.WEALTH,

                                ConsultationCategoryDeterministicLayer
                                        .CURRENT_TIMELINE,

                                RULE_CODE,

                                status,

                                normalizedSegment
                                        .sajuYear(),

                                normalizedSegment
                                        .monthIndex(),

                                List.of(),

                                summaryOf(
                                        status
                                ),

                                List.of(
                                        normalizedMatch
                                ),

                                reasons
                        )
                );
            }
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Segment Source 조회
     * =================================================
     */

    private List<LifeDomainRuleMatch>
    findSegmentMatches(

            ConsultationCategorySegmentEvidence segment,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                segment,
                "ConsultationCategorySegmentEvidence가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "WEALTH Annual Functional sourceRuleCode가 필요합니다."
                );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : segment.matches()
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "WEALTH Segment Match는 null일 수 없습니다."
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
     * 다른 Match 또는 다른 시간층을 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "WEALTH Annual Functional에는 LifeDomainSignalStatus가 필요합니다."
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
     */

    private List<String> buildReasons(

            ConsultationCategorySegmentEvidence segment,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                segment,
                "WEALTH Annual Functional Segment가 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "WEALTH Annual Functional Status가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "WEALTH_ANNUAL_FUNCTIONAL에서 이미 계산된 하나의 LifeDomain Match를 독립적인 WEALTH Category Match로 보존했습니다."
        );

        reasons.add(
                "LifeDomainRuleMatch의 공식 Status를 다른 WEALTH Source와 결합하지 않고 1:1로 Category Status에 보존했습니다."
        );

        reasons.add(
                "WEALTH_ANNUAL_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "WEALTH_ANNUAL_CONTEXT와 WEALTH_ANNUAL_FUNCTIONAL은 같은 명리연도의 연결된 Annual provenance이므로 이후 독립 검증 2개 또는 강도 2배로 취급해서는 안 됩니다."
        );

        reasons.add(
                "Decade·Monthly·Natal Status를 이 Annual Functional Status와 합성하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과를 수입 증가·손실·투자 성공·투자 실패·자산 증감 같은 실제 재물 사건으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 WEALTH Annual Functional 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 있으면 각각 별도 결과로 보존합니다."
            );
        }

        /*
         * Annual Functional Match도 CURRENT_TIMELINE 객체이므로
         * 현재 Segment 좌표의 monthIndex를 보존한다.
         *
         * 단:
         * monthIndex 존재를 Monthly 계산으로 해석하지 않는다.
         */

        reasons.add(
                "monthIndex는 현재 Segment의 좌표 보존용이며 Annual Functional 결과를 Monthly 결과로 재해석하지 않습니다."
        );

        return List.copyOf(
                reasons
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "WEALTH Annual Functional Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 명리연도의 WEALTH Functional 계산 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "현재 명리연도의 WEALTH Functional 계산 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "현재 명리연도의 WEALTH Functional 계산 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "현재 명리연도의 WEALTH Functional 계산 결과가 CONTEXTUAL 상태로 보존됩니다.";
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