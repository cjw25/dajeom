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
 * CAREER 현재 월 Functional deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.CAREER에서 이미 계산된:
 *
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * 결과를 ConsultationCategory.CAREER의
 * 독립적인 CURRENT_TIMELINE 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * CareerMonthlyFunctionalRule은
 * RESPONSIBILITY Function에 대해:
 *
 * - 현재 월운 실제 접촉
 * - 고정된 Annual Functional Baseline
 * - 현재 Segment의 Resolved Annual Effect
 * - Monthly Functional ActivationStatus
 * - 동일방향 누적 조건
 * - 반대방향 접촉
 * - qualification 조건
 *
 * 을 실제 Java에서 계산한다.
 *
 * 이 Category Rule에서는
 * 해당 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * Monthly Functional 경계
 * =================================================
 *
 * 현재 Monthly Functional Layer에는
 * 별도의 Monthly CrossFunctionalAnalysis가 없다.
 *
 * 따라서 upstream CareerMonthlyFunctionalRule도:
 *
 * EXPRESSION_CONFLICTS_RESPONSIBILITY
 *
 * 같은 Function 간 관계를
 * 월운 단계에서 새로 만들지 않는다.
 *
 * 이 Category Rule 역시
 * 그런 관계를 생성하지 않는다.
 *
 * =================================================
 * 실제 Java 계산 보존
 * =================================================
 *
 * CareerMonthlyFunctionalRule이 실제 계산한:
 *
 * - monthlyContactExists
 * - annualBaselineActive
 * - mixedMonthlyContact
 * - sameDirectionAccumulation
 * - baselineQualificationRequired
 * - activationStatus
 *
 * 등의 결과와 그 최종
 * LifeDomainSignalStatus는
 * 유효한 deterministic 계산이다.
 *
 * Category에서는 그 결과를 그대로 보존한다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * CAREER_MONTHLY_FUNCTIONAL
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 그대로 보존한다.
 *
 * 여러 Match가 존재하더라도
 * 서로 합쳐 새로운 Status를 만들지 않는다.
 *
 * =================================================
 * CAREER_MONTHLY_CONTEXT와의 분리
 * =================================================
 *
 * 일반 월운 CAREER Context:
 *
 * CAREER_MONTHLY_CONTEXT
 *
 * ->
 *
 * CareerCurrentMonthlyActivationRule
 *
 * -------------------------------------------------
 *
 * 월운 RESPONSIBILITY Functional:
 *
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 두 결과를 이 Rule에서 합성하지 않는다.
 *
 * =================================================
 * Provenance Family
 * =================================================
 *
 * CAREER_MONTHLY_CONTEXT와
 * CAREER_MONTHLY_FUNCTIONAL은
 * 같은 명리연도·월에서 파생된
 * 연결된 Monthly provenance다.
 *
 * 따라서 이후 Grounding / Prominence에서:
 *
 * - 독립 검증 2개
 * - 근거 강도 2배
 * - 독립 확인 2회
 *
 * 로 계산해서는 안 된다.
 *
 * 동일한:
 *
 * MONTHLY_CONTEXT_FAMILY:<year>:<month>
 *
 * 계열로 취급해야 한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * CareerMonthlyFunctionalRule이 이미 확정한
 * LifeDomainSignalStatus를
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
 * SUPPORTIVE 제한
 * =================================================
 *
 * 현재 upstream
 * CareerMonthlyFunctionalRule은
 * RESPONSIBILITY 월운 접촉만으로
 * SUPPORTIVE를 생성하지 않는다.
 *
 * 따라서 일반적으로 이 Rule에서
 * SUPPORTIVE가 새로 만들어지는 일은 없다.
 *
 * 하지만 Category 단계에서는
 * upstream Status를 재판정하지 않으므로
 * 만약 upstream Match의 공식 Status가
 * SUPPORTIVE라면 그대로 보존한다.
 *
 * =================================================
 * 세운과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * CAREER_ANNUAL_CONTEXT
 * CAREER_ANNUAL_FUNCTIONAL
 *
 * 을 조회하지 않는다.
 *
 * 월운 Functional 내부에서
 * Annual Functional Baseline을 참조하는 것은
 * upstream Java가 명시적으로 계산한
 * Monthly Functional Contract다.
 *
 * 이를 허용한다.
 *
 * 그러나 Category 단계에서 다시:
 *
 * ANNUAL_FUNCTIONAL Status
 * +
 * MONTHLY_FUNCTIONAL Status
 *
 * 를 결합하지 않는다.
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
 * upstream Annual Functional Baseline에
 * 정확한 대운 결합 Functional Segment가
 * 이미 반영되어 있을 수 있지만,
 *
 * 그것을 다시 대운 × 월운 Category Matrix로
 * 계산하지 않는다.
 *
 * =================================================
 * 원국과의 분리
 * =================================================
 *
 * 다음 원국 Source를 사용하지 않는다.
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * CAREER_OFFICER_STRUCTURE
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 원국 CAREER 결과와
 * Monthly Functional Status를
 * Category 단계에서 합성하지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * Category 단계에서 다음 의미를
 * 새로 생성하지 않는다.
 *
 * - 세운 때문에 이번 달 책임이 강해진다
 * - 대운 때문에 이번 달 역할 부담이 커진다
 * - 월운이 연간 책임 기능을 강화한다
 * - 책임이 반복될수록 부담이 커진다
 * - 역할을 맡은 뒤 상태가 달라진다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 작동한다
 * - 먼저 A이고 이후 B가 된다
 *
 * =================================================
 * 실제 계산된 조건과의 구분
 * =================================================
 *
 * upstream CareerMonthlyFunctionalRule에서
 * 명시적으로 계산한:
 *
 * Annual Baseline 활성
 * +
 * 동일방향 누적 조건
 * +
 * Monthly SUPPORTED
 *
 * ->
 *
 * PRESSURE
 *
 * 는 Java에 존재하는 실제 계산 Contract다.
 *
 * 이 계산 결과 자체는 보존한다.
 *
 * 하지만 Category 단계에서
 * 별개의 Evidence를 더 붙여
 * 새로운 관계를 만들지는 않는다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * PRESSURE
 * != 업무 증가
 * != 승진 부담
 * != 직장 갈등
 * != 해고
 * != 퇴사
 *
 * MIXED
 * != 이직
 * != 직업 변동
 *
 * CONTEXTUAL
 * != 아무 일도 없음
 * != 직업운 없음
 *
 * SUPPORTIVE
 * != 취업
 * != 승진
 * != 합격
 *
 * =================================================
 * 특정 직업 생성 금지
 * =================================================
 *
 * RESPONSIBILITY
 * != 관리직
 * != 공무원
 * != 법조인
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각 Segment에 실제 존재하는:
 *
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * Match를 각각 독립적으로 보존한다.
 *
 * 특정 Segment 하나를 대표값으로
 * 임의 선택하지 않는다.
 *
 * =================================================
 * monthIndex
 * =================================================
 *
 * 현재 Timeline이 계산한:
 *
 * - sajuYear
 * - monthIndex
 *
 * 를 그대로 보존한다.
 *
 * monthIndex == 0도
 * 유효한 Timeline 좌표로 취급한다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Monthly Functional Match 개수를:
 *
 * - 책임 크기
 * - 직업운 강도
 * - 업무량
 * - 사회적 지위
 * - 승진 가능성
 *
 * 의 점수로 사용하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - RESPONSIBILITY 재계산
 * - Monthly Activation 재계산
 * - Annual Functional Baseline 재계산
 * - sameDirectionAccumulation 재계산
 * - qualification 재계산
 * - Monthly CrossFunctional 관계 생성
 * - CAREER_MONTHLY_CONTEXT와 Status 합성
 * - CAREER_ANNUAL_CONTEXT와 Status 합성
 * - CAREER_ANNUAL_FUNCTIONAL과 Status 합성
 * - CAREER_DECADE_CONTEXT와 Status 합성
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
public class CareerMonthlyFunctionalDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_CURRENT_MONTHLY_FUNCTIONAL";

    private static final String
            MONTHLY_FUNCTIONAL_RULE_CODE =
            "CAREER_MONTHLY_FUNCTIONAL";


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
                "CareerMonthlyFunctionalDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerMonthlyFunctionalDeterministicRule은 CAREER View만 평가할 수 있습니다."
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
                            "CAREER Monthly Functional Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 실제 Source 조회
             * =================================================
             */

            List<LifeDomainRuleMatch> functionalMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            MONTHLY_FUNCTIONAL_RULE_CODE
                    );

            /*
             * 실제 Monthly Functional Match가 없으면
             * 다른 CAREER Source를 대신 사용하지 않는다.
             */

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
                                "CAREER_MONTHLY_FUNCTIONAL Match는 null일 수 없습니다."
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
     * Source 판별 조건으로 사용하지 않는다.
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
                        "CAREER Monthly Functional sourceRuleCode가 필요합니다."
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
     * CareerMonthlyFunctionalRule이
     * 이미 계산한 LifeDomainSignalStatus를
     * 그대로 Category Status로 보존한다.
     *
     * 다른 CAREER Source는 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CAREER Monthly Functional에는 LifeDomainSignalStatus가 필요합니다."
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
                "CAREER Monthly Functional Segment가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "CAREER_MONTHLY_FUNCTIONAL에서 이미 계산된 하나의 LifeDomain Match를 독립적인 CAREER Category Match로 보존했습니다."
        );

        reasons.add(
                "CareerMonthlyFunctionalRule이 RESPONSIBILITY의 실제 월운 접촉, Annual Functional Baseline, Monthly ActivationStatus 및 qualification 조건을 이용해 확정한 공식 LifeDomainSignalStatus를 그대로 1:1 보존했습니다."
        );

        reasons.add(
                "CAREER_MONTHLY_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "CAREER_MONTHLY_CONTEXT와 CAREER_MONTHLY_FUNCTIONAL은 같은 명리연도·월의 연결된 Monthly provenance이므로 이후 독립 검증 2개 또는 근거 강도 2배로 취급해서는 안 됩니다."
        );

        reasons.add(
                "CAREER_ANNUAL_CONTEXT와 CAREER_ANNUAL_FUNCTIONAL을 Category 단계에서 이 Monthly Functional Status와 다시 합성하지 않았습니다."
        );

        reasons.add(
                "CAREER_DECADE_CONTEXT와 원국 CAREER Source도 이 Rule의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "현재 Monthly Functional Layer에는 Function 간 CrossFunctional 판정이 없으므로 표현 기능과 책임 기능 사이의 새로운 관계를 이 Category Rule에서도 생성하지 않았습니다."
        );

        reasons.add(
                "Upstream CareerMonthlyFunctionalRule에서 명시적으로 계산된 Annual Baseline 활성, 동일방향 누적 조건, Monthly ActivationStatus 및 qualification 결과 자체는 그대로 인정합니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과를 취업·승진·합격·이직·퇴사·해고·직장 갈등·업무 증가 같은 실제 직업 사건이나 특정 직업군으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 CAREER Monthly Functional 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 존재하면 각각의 CAREER_MONTHLY_FUNCTIONAL 결과를 별도로 보존합니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 실제 좌표를 그대로 보존하며 monthIndex == 0도 유효한 값으로 취급합니다."
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
     * Annual / Decade / Natal 결과와
     * 새로 연결하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "CAREER Monthly Functional Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 월의 CAREER RESPONSIBILITY Functional 계산 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "현재 월의 CAREER RESPONSIBILITY Functional 계산 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "현재 월의 CAREER RESPONSIBILITY Functional 계산 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "현재 월의 CAREER RESPONSIBILITY Functional 계산 결과가 CONTEXTUAL 상태로 보존됩니다.";
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