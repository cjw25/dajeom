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
 * CAREER 현재 명리연도 Functional deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.CAREER에서 이미 계산된:
 *
 * CAREER_ANNUAL_FUNCTIONAL
 *
 * 결과를 ConsultationCategory.CAREER의
 * 별도 CURRENT_TIMELINE 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * CareerAnnualFunctionalRule은
 * RESPONSIBILITY Function을 중심으로:
 *
 * - Annual RESPONSIBILITY Capacity
 * - 현재 Timeline이 선택한 대운 결합 Functional Segment
 * - Resolved RESPONSIBILITY
 * - RESPONSIBILITY 관련 CrossFunctional Pattern
 * - Context Qualification
 *
 * 을 실제 Java에서 계산한다.
 *
 * 이 Category Rule에서는
 * 해당 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * 실제 Java 계산 관계 보존
 * =================================================
 *
 * Upstream에서 실제 계산된:
 *
 * EXPRESSION_CONFLICTS_RESPONSIBILITY
 * RESPONSIBILITY_INTERNAL_MIXTURE
 * EXPRESSION_CONTROLS_PRESSURE
 * RESPONSIBILITY_GENERATES_PROCESSING
 * PRESSURE_GENERATES_PROCESSING
 * RESOURCE_GENERATES_RESPONSIBILITY
 *
 * 등의 typed CrossFunctional Pattern은
 * 유효한 deterministic 계산이다.
 *
 * 이 Rule은 그 결과를 삭제하거나
 * 다시 해석하지 않는다.
 *
 * =================================================
 * 중요한 경계
 * =================================================
 *
 * CAREER_ANNUAL_FUNCTIONAL
 *
 * 과:
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * 는 같은 명리연도에 속하지만
 * 서로 다른 계산 결과다.
 *
 * -------------------------------------------------
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * ->
 *
 * CareerCurrentStructuralFlowRule
 *
 * -------------------------------------------------
 *
 * CAREER_ANNUAL_FUNCTIONAL
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 두 결과를 Category 단계에서
 * 하나의 Status로 합성하지 않는다.
 *
 * =================================================
 * Provenance Family
 * =================================================
 *
 * CAREER_ANNUAL_CONTEXT와
 * CAREER_ANNUAL_FUNCTIONAL은
 * 같은 명리연도의 연결된 provenance다.
 *
 * 따라서 이후 Prominence / Grounding에서:
 *
 * - 독립 검증 2개
 * - 근거 강도 2배
 * - 별도 독립 확인 2회
 *
 * 로 계산해서는 안 된다.
 *
 * 현재 Annual provenance family 정책에 따라
 * 동일 연도 Family로 취급해야 한다.
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
 * 여러 Annual Functional Match가 존재하더라도
 * 서로 합쳐 새로운 Status를 만들지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * CareerAnnualFunctionalRule이 이미 확정한
 * LifeDomainSignalStatus를 그대로 1:1 변환한다.
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
 * 세운 일반 Context와 합성 금지
 * =================================================
 *
 * 다음 계산을 하지 않는다.
 *
 * ANNUAL_CONTEXT SUPPORTIVE
 * +
 * ANNUAL_FUNCTIONAL PRESSURE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * ANNUAL_CONTEXT PRESSURE
 * +
 * ANNUAL_FUNCTIONAL CONTEXTUAL
 * =
 * PRESSURE
 *
 * 금지.
 *
 * =================================================
 * 대운과의 분리
 * =================================================
 *
 * 이 Category Rule에서는:
 *
 * CAREER_DECADE_CONTEXT
 *
 * 를 사용하지 않는다.
 *
 * 단,
 *
 * CAREER_ANNUAL_FUNCTIONAL upstream 내부에서
 * 현재 Timeline이 선택한 정확한 대운 결합
 * Functional Segment를 사용하는 것은
 * 이미 별도의 Java 계산으로 확정된 것이다.
 *
 * 그것을 Category 단계에서 다시
 * 대운 × 세운 Status Matrix로 계산하지 않는다.
 *
 * =================================================
 * 월운과의 분리
 * =================================================
 *
 * 다음 Source를 사용하지 않는다.
 *
 * CAREER_MONTHLY_CONTEXT
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * 월운 계산은 별도 Rule에서 처리한다.
 *
 * =================================================
 * 원국과의 분리
 * =================================================
 *
 * 다음 Source를 사용하지 않는다.
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * CAREER_OFFICER_STRUCTURE
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 원국 CAREER 결과를
 * Annual Functional Status와 합성하지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * Category 단계에서 다음 의미를
 * 새로 생성하지 않는다.
 *
 * - 대운 때문에 책임 기능이 강해진다
 * - 세운 책임 기능이 원국 관살을 강화한다
 * - 책임이 반복되면서 부담이 커진다
 * - 역할을 맡은 뒤 상태가 달라진다
 * - Annual Context 때문에 Functional이 변한다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - 먼저 A이고 이후 B가 된다
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * RESPONSIBILITY active
 * != 취업
 *
 * SUPPORTIVE
 * != 승진
 * != 합격
 * != 직업 안정
 *
 * PRESSURE
 * != 해고
 * != 퇴사
 * != 직장 갈등
 *
 * MIXED
 * != 이직
 * != 직업 변동
 *
 * CONTEXTUAL
 * != 직업운 없음
 *
 * =================================================
 * 특정 직업 생성 금지
 * =================================================
 *
 * RESPONSIBILITY
 * != 공무원
 * != 관리직
 * != 법조인
 *
 * CrossFunctional Pattern도
 * 특정 직업군으로 직접 치환하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각 Segment의 실제:
 *
 * CAREER_ANNUAL_FUNCTIONAL
 *
 * Match를 각각 보존한다.
 *
 * 특정 Segment 하나를
 * 대표값으로 임의 선택하지 않는다.
 *
 * =================================================
 * 좌표
 * =================================================
 *
 * 이 Match는 CURRENT_TIMELINE Layer이므로:
 *
 * - sajuYear
 * - monthIndex
 *
 * 는 현재 Segment 좌표를 그대로 보존한다.
 *
 * monthIndex는 Annual Functional 결과를
 * Monthly Functional 결과로 재해석하는
 * 의미가 아니다.
 *
 * monthIndex == 0도 유효한 좌표다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - RESPONSIBILITY 재계산
 * - Annual Capacity 재계산
 * - Resolved Function 재계산
 * - CrossFunctional Pattern 재계산
 * - Annual Context와 Status 합성
 * - Decade Context와 Status 합성
 * - Monthly Context와 Status 합성
 * - 원국 CAREER Status와 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 특정 직업군 생성
 * - 실제 직업 사건 생성
 */
@Component
public class CareerAnnualFunctionalDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_CURRENT_ANNUAL_FUNCTIONAL";

    private static final String
            ANNUAL_FUNCTIONAL_RULE_CODE =
            "CAREER_ANNUAL_FUNCTIONAL";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.CAREER;
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
                "CareerAnnualFunctionalDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerAnnualFunctionalDeterministicRule은 CAREER View만 평가할 수 있습니다."
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
         * Current Timeline 필요
         * =================================================
         */

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
                            "CAREER Annual Functional Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 실제 Source 조회
             * =================================================
             */

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
                                "CAREER_ANNUAL_FUNCTIONAL Match는 null일 수 없습니다."
                        );

                ConsultationCategoryDeterministicStatus status =
                        toCategoryStatus(
                                normalizedMatch.status()
                        );

                List<String> reasons =
                        buildReasons(
                                normalizedSegment
                        );

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.CAREER,

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
     *
     * Source 판별에는:
     *
     * - domain
     * - ruleCode
     *
     * 만 사용한다.
     *
     * summary / reasons 문자열은
     * Source 판별에 사용하지 않는다.
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
                        "CAREER Annual Functional sourceRuleCode가 필요합니다."
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
                            "CAREER Segment Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.CAREER
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
     * CareerAnnualFunctionalRule이
     * 이미 계산한 공식 Status를
     * 그대로 보존한다.
     *
     * 다른 CAREER Source를 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CAREER Annual Functional에는 LifeDomainSignalStatus가 필요합니다."
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
            ConsultationCategorySegmentEvidence segment
    ) {

        Objects.requireNonNull(
                segment,
                "CAREER Annual Functional Segment가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "CAREER_ANNUAL_FUNCTIONAL에서 이미 계산된 하나의 LifeDomain Match를 별도의 CAREER Category Match로 보존했습니다."
        );

        reasons.add(
                "CareerAnnualFunctionalRule이 RESPONSIBILITY Capacity, 현재 Resolved Function, typed CrossFunctional Pattern 및 Context Qualification을 사용해 확정한 공식 LifeDomainSignalStatus를 그대로 1:1 보존했습니다."
        );

        reasons.add(
                "CAREER_ANNUAL_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "CAREER_ANNUAL_CONTEXT와 CAREER_ANNUAL_FUNCTIONAL은 같은 명리연도의 연결된 provenance이므로 이후 독립 검증 2개 또는 근거 강도 2배로 취급해서는 안 됩니다."
        );

        reasons.add(
                "CAREER_DECADE_CONTEXT를 별도 Category Source로 추가하여 Annual Functional Status와 다시 합성하지 않았습니다."
        );

        reasons.add(
                "CAREER_MONTHLY_CONTEXT와 CAREER_MONTHLY_FUNCTIONAL을 이 Rule에 사용하지 않았습니다."
        );

        reasons.add(
                "CAREER_PRIMARY_OFFICER_STRUCTURE, CAREER_OFFICER_STRUCTURE, CAREER_CROSS_VALIDATED_PATTERN 같은 원국 Source와 Status를 합성하지 않았습니다."
        );

        reasons.add(
                "Upstream에서 실제 계산된 RESPONSIBILITY 관련 CrossFunctional Pattern은 그대로 인정하지만 이 Category Rule에서 새로운 관계·인과·순서를 추가하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과를 취업·승진·합격·이직·퇴사·해고·직장 갈등 같은 실제 직업 사건이나 특정 직업군으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 CAREER Annual Functional 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 좌표의 다른 가능한 Segment가 존재하면 각각의 CAREER_ANNUAL_FUNCTIONAL 결과를 별도로 보존합니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 monthIndex 존재를 이유로 Annual Functional 결과를 Monthly Functional 결과로 재해석하지 않습니다."
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
     * 최종 사용자 Story가 아니다.
     *
     * 다른 CAREER Source와 연결하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "CAREER Annual Functional Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 명리연도의 CAREER RESPONSIBILITY Functional 계산 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "현재 명리연도의 CAREER RESPONSIBILITY Functional 계산 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "현재 명리연도의 CAREER RESPONSIBILITY Functional 계산 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "현재 명리연도의 CAREER RESPONSIBILITY Functional 계산 결과가 CONTEXTUAL 상태로 보존됩니다.";
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