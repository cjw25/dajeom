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
 * CURRENT_YEAR_LUCK의
 * 생활영역별 Annual Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 현재 Timeline Segment에 이미 존재하는
 * 다섯 LifeDomain의 공식 Annual Context를:
 *
 * - 서로 합치지 않고
 * - 대표 Domain을 선택하지 않고
 * - 전체 연도 Status를 새로 만들지 않고
 *
 * CURRENT_YEAR_LUCK의 독립적인
 * CURRENT_TIMELINE Match로 보존한다.
 *
 * =================================================
 * 대상 Source
 * =================================================
 *
 * WEALTH
 * ->
 * WEALTH_ANNUAL_CONTEXT
 *
 * CAREER
 * ->
 * CAREER_ANNUAL_CONTEXT
 *
 * RELATIONSHIP
 * ->
 * RELATIONSHIP_ANNUAL_CONTEXT
 *
 * LEARNING_GROWTH
 * ->
 * LEARNING_ANNUAL_CONTEXT
 *
 * HEALTH_LIFESTYLE
 * ->
 * HEALTH_ANNUAL_CONTEXT
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 Rule은:
 *
 * "올해 전체 운"
 *
 * 을 하나의 SUPPORTIVE / PRESSURE / MIXED로
 * 합산하는 Rule이 아니다.
 *
 * 각각의 LifeDomain에서 이미 계산된
 * 세운 상태를 CURRENT_YEAR_LUCK 안에
 * 독립적으로 보존하는 Rule이다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * Annual LifeDomainRuleMatch 하나
 *
 * ->
 *
 * Current Year Category Match 하나
 *
 * 로 보존한다.
 *
 * 예:
 *
 * WEALTH_ANNUAL_CONTEXT = SUPPORTIVE
 *
 * ->
 *
 * CURRENT_YEAR_ANNUAL_DOMAIN_CONTEXT
 * sourceDomain = WEALTH
 * status = SUPPORTIVE
 *
 * -------------------------------------------------
 *
 * CAREER_ANNUAL_CONTEXT = PRESSURE
 *
 * ->
 *
 * CURRENT_YEAR_ANNUAL_DOMAIN_CONTEXT
 * sourceDomain = CAREER
 * status = PRESSURE
 *
 * 두 Match를 다시 하나로 합치지 않는다.
 *
 * =================================================
 * Cross-Domain 합성 금지
 * =================================================
 *
 * 다음 계산을 하지 않는다.
 *
 * WEALTH SUPPORTIVE
 * +
 * CAREER PRESSURE
 * =
 * CURRENT_YEAR MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * RELATIONSHIP SUPPORTIVE
 * +
 * CAREER SUPPORTIVE
 * =
 * 더 강한 SUPPORTIVE
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * HEALTH PRESSURE
 * +
 * WEALTH PRESSURE
 * =
 * 더 강한 PRESSURE
 *
 * 금지.
 *
 * =================================================
 * 관계 생성 금지
 * =================================================
 *
 * 서로 다른 Domain이 같은 Segment에
 * 존재한다는 이유만으로:
 *
 * - 재물 때문에 직업이 변한다
 * - 직업 때문에 관계에 부담이 생긴다
 * - 학습이 재물로 이어진다
 * - 건강 부담이 직업을 약화한다
 * - 관계가 재물을 강화한다
 *
 * 같은 관계를 만들지 않는다.
 *
 * =================================================
 * 실제 upstream 관계 보존
 * =================================================
 *
 * 각 Annual Context 내부에서
 * upstream Java가 실제로 계산한:
 *
 * - 원국 × 세운 관계
 * - 가능한 대운 Segment Context
 * - 용신 Context
 * - 격국 Context
 * - Palace Context
 * - 합화·기능 변화
 * - 해당 Domain의 공식 Signal
 *
 * 은 그대로 유효하다.
 *
 * 이 Rule은 해당 계산을 다시 하지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 각각의 LifeDomainRuleMatch가 가진
 * 공식 LifeDomainSignalStatus를
 * 그대로 Category Status로 1:1 변환한다.
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
 * Status 의미
 * =================================================
 *
 * 이 Status는 반드시:
 *
 * "해당 LifeDomain의 Annual Context 상태"
 *
 * 로만 읽어야 한다.
 *
 * 예:
 *
 * WEALTH SUPPORTIVE
 *
 * 는:
 *
 * 올해 전체 SUPPORTIVE
 *
 * 가 아니다.
 *
 * CAREER PRESSURE
 *
 * 역시:
 *
 * 올해 전체 PRESSURE
 *
 * 가 아니다.
 *
 * =================================================
 * Annual Functional과 분리
 * =================================================
 *
 * 다음 Functional Source는
 * 이 Rule에서 사용하지 않는다.
 *
 * WEALTH_ANNUAL_FUNCTIONAL
 * CAREER_ANNUAL_FUNCTIONAL
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * Annual Functional은
 * 별도 Current Year Rule에서 처리한다.
 *
 * =================================================
 * Relationship Annual Functional
 * =================================================
 *
 * 현재 실제 소스에는:
 *
 * RELATIONSHIP_ANNUAL_FUNCTIONAL
 *
 * 이 존재하지 않는다.
 *
 * 따라서 존재하지 않는 계산을
 * 새로 만들어 채우지 않는다.
 *
 * =================================================
 * Decade와 분리
 * =================================================
 *
 * 다음 Source도 사용하지 않는다.
 *
 * WEALTH_DECADE_CONTEXT
 * CAREER_DECADE_CONTEXT
 * RELATIONSHIP_DECADE_CONTEXT
 * LEARNING_DECADE_CONTEXT
 * HEALTH_DECADE_CONTEXT
 *
 * 대운 Context는 별도 Rule에서 처리한다.
 *
 * =================================================
 * Monthly와 분리
 * =================================================
 *
 * 다음 Source도 사용하지 않는다.
 *
 * WEALTH_MONTHLY_CONTEXT
 * CAREER_MONTHLY_CONTEXT
 * RELATIONSHIP_MONTHLY_CONTEXT
 * LEARNING_MONTHLY_CONTEXT
 *
 * 및 각 Monthly Functional.
 *
 * 월운은 별도 Current Year Rule에서 처리한다.
 *
 * =================================================
 * HEALTH Monthly Context
 * =================================================
 *
 * 현재 실제 소스에는:
 *
 * HEALTH_MONTHLY_CONTEXT
 *
 * 가 없다.
 *
 * 따라서 이 Rule 또는 이후 Rule에서
 * 없는 Source를 가정해서 만들지 않는다.
 *
 * =================================================
 * Natal과 분리
 * =================================================
 *
 * CURRENT_YEAR_LUCK Evidence View에는
 * 모든 Natal LifeDomain Match도 존재한다.
 *
 * 하지만 이 Rule에서는
 * natalMatches를 사용하지 않는다.
 *
 * 원국 구조와 Annual Status를
 * Category 단계에서 다시 합성하지 않는다.
 *
 * =================================================
 * 전체 연도 대표 Domain 선택 금지
 * =================================================
 *
 * 이 Rule은:
 *
 * - 가장 많은 Match가 있는 Domain
 * - 가장 많은 PRESSURE가 있는 Domain
 * - 가장 많은 SUPPORTIVE가 있는 Domain
 *
 * 을 올해 대표 분야로 선택하지 않는다.
 *
 * =================================================
 * Match 개수 점수화 금지
 * =================================================
 *
 * Annual Match 개수를:
 *
 * - 올해 운의 강도
 * - 올해 좋고 나쁨
 * - 올해 사건 개수
 * - 중요도
 *
 * 로 사용하지 않는다.
 *
 * =================================================
 * 현재 연도 사건 생성 금지
 * =================================================
 *
 * WEALTH SUPPORTIVE
 * != 수입 증가
 * != 투자 성공
 *
 * CAREER SUPPORTIVE
 * != 취업
 * != 승진
 *
 * RELATIONSHIP SUPPORTIVE
 * != 연애 시작
 * != 결혼
 *
 * LEARNING SUPPORTIVE
 * != 시험 합격
 * != 자격 취득
 *
 * HEALTH SUPPORTIVE
 * != 건강 회복
 *
 * -------------------------------------------------
 *
 * PRESSURE 역시:
 *
 * 손실
 * 해고
 * 이별
 * 시험 실패
 * 질병
 *
 * 같은 실제 사건을 의미하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각 Segment의 실제 Annual Context Match를
 * 각각 독립적으로 보존한다.
 *
 * 특정 Segment 하나를
 * 올해의 대표값으로 선택하지 않는다.
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
 * 를 현재 Segment 좌표 그대로 보존한다.
 *
 * monthIndex 존재를 이유로
 * Annual Context를 Monthly Context로
 * 재해석하지 않는다.
 *
 * monthIndex == 0도 유효하다.
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * source Match의:
 *
 * - summary
 * - reasons
 *
 * 를 파싱하여:
 *
 * - Domain
 * - Annual 여부
 * - Status
 * - 관계
 * - 사건
 *
 * 을 재판정하지 않는다.
 *
 * Domain과 RuleCode의 정확한
 * 구조화된 조합만 사용한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Annual Context 재계산
 * - LifeDomain Status 재계산
 * - Cross-Domain Status 합성
 * - 전체 연도 Status 생성
 * - 대표 Domain 선택
 * - 대운과 합성
 * - Annual Functional과 합성
 * - Monthly Context와 합성
 * - Monthly Functional과 합성
 * - Natal Match와 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 현재 연도 실제 사건 생성
 */
@Component
public class CurrentYearAnnualDomainContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CURRENT_YEAR_ANNUAL_DOMAIN_CONTEXT";

    private static final String
            WEALTH_ANNUAL_CONTEXT_RULE_CODE =
            "WEALTH_ANNUAL_CONTEXT";

    private static final String
            CAREER_ANNUAL_CONTEXT_RULE_CODE =
            "CAREER_ANNUAL_CONTEXT";

    private static final String
            RELATIONSHIP_ANNUAL_CONTEXT_RULE_CODE =
            "RELATIONSHIP_ANNUAL_CONTEXT";

    private static final String
            LEARNING_ANNUAL_CONTEXT_RULE_CODE =
            "LEARNING_ANNUAL_CONTEXT";

    private static final String
            HEALTH_ANNUAL_CONTEXT_RULE_CODE =
            "HEALTH_ANNUAL_CONTEXT";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.CURRENT_YEAR_LUCK;
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
                "CurrentYearAnnualDomainContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.CURRENT_YEAR_LUCK
        ) {

            throw new IllegalArgumentException(
                    "CurrentYearAnnualDomainContextRule은 CURRENT_YEAR_LUCK View만 평가할 수 있습니다."
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
                            "CURRENT_YEAR_LUCK Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 모든 Match를 보되
             * 공식 Annual Domain Source만 선택
             * =================================================
             */

            for (
                    LifeDomainRuleMatch match
                    : normalizedSegment.matches()
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                match,
                                "CURRENT_YEAR_LUCK Segment Match는 null일 수 없습니다."
                        );

                if (
                        !isOfficialAnnualDomainContext(
                                normalizedMatch
                        )
                ) {

                    continue;
                }

                ConsultationCategoryDeterministicStatus status =
                        toCategoryStatus(
                                normalizedMatch.status()
                        );

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.CURRENT_YEAR_LUCK,

                                ConsultationCategoryDeterministicLayer
                                        .CURRENT_TIMELINE,

                                RULE_CODE,

                                status,

                                normalizedSegment.sajuYear(),

                                normalizedSegment.monthIndex(),

                                List.of(),

                                summaryOf(
                                        normalizedMatch.domain(),
                                        status
                                ),

                                List.of(
                                        normalizedMatch
                                ),

                                buildReasons(
                                        normalizedSegment,
                                        normalizedMatch
                                )
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
     * 공식 Annual Domain Context 판별
     * =================================================
     *
     * Domain과 RuleCode의 정확한 Pair만 허용한다.
     *
     * 예:
     *
     * WEALTH
     * +
     * WEALTH_ANNUAL_CONTEXT
     *
     * 는 허용.
     *
     * -------------------------------------------------
     *
     * WEALTH
     * +
     * CAREER_ANNUAL_CONTEXT
     *
     * 는 허용하지 않는다.
     */

    private boolean isOfficialAnnualDomainContext(
            LifeDomainRuleMatch match
    ) {

        Objects.requireNonNull(
                match,
                "Annual Domain Context 판별에는 LifeDomainRuleMatch가 필요합니다."
        );

        String ruleCode =
                requireText(
                        match.ruleCode(),
                        "Annual Domain Context Match에는 ruleCode가 필요합니다."
                );

        return switch (
                match.domain()
                ) {

            case WEALTH ->
                    WEALTH_ANNUAL_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case CAREER ->
                    CAREER_ANNUAL_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case RELATIONSHIP ->
                    RELATIONSHIP_ANNUAL_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case LEARNING_GROWTH ->
                    LEARNING_ANNUAL_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case HEALTH_LIFESTYLE ->
                    HEALTH_ANNUAL_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );
        };
    }


    /*
     * =================================================
     * Status 1 : 1 변환
     * =================================================
     *
     * 각 LifeDomain Annual Rule이
     * 이미 확정한 공식 Status만 사용한다.
     *
     * 다른 Domain의 상태를
     * 함께 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CURRENT_YEAR Annual Domain Match에는 LifeDomainSignalStatus가 필요합니다."
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

            ConsultationCategorySegmentEvidence segment,

            LifeDomainRuleMatch sourceMatch

    ) {

        Objects.requireNonNull(
                segment,
                "CURRENT_YEAR Annual Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "CURRENT_YEAR Annual Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "현재 Segment에 이미 존재하는 "
                        + sourceMatch.domain()
                        + " Domain의 공식 Annual Context Match를 CURRENT_YEAR_LUCK의 독립 영역 결과로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 Annual Context의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 LifeDomain의 상태와 결합하지 않고 그대로 보존했습니다."
        );

        reasons.add(
                "이 Match의 Status는 "
                        + sourceMatch.domain()
                        + " 영역의 현재 명리연도 상태이며 CURRENT_YEAR_LUCK 전체의 종합 Status를 의미하지 않습니다."
        );

        reasons.add(
                "다른 LifeDomain Annual Match가 같은 Segment에 존재하더라도 SUPPORTIVE·PRESSURE·MIXED를 교차 합성하지 않았습니다."
        );

        reasons.add(
                "각 LifeDomain Annual Rule 내부에서 실제 Java가 계산한 원국×세운 관계, 가능한 대운 Context, 용신·격국·궁위·합화·기능 변화 등의 결과는 그대로 인정하되 이 Category Rule에서 다시 계산하지 않았습니다."
        );

        reasons.add(
                "Annual Functional, Decade Context, Monthly Context, Monthly Functional 또는 Natal Match를 이 Match의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "Annual Match 개수나 동일 Status Domain 개수를 이용해 올해의 전체 방향·강도·대표 분야를 계산하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 Domain, Status, 사건 또는 새로운 관계를 생성하지 않았습니다."
        );

        reasons.add(
                "이 결과를 수입 증가·승진·연애 시작·시험 합격·질병 회복 등의 실제 현재 연도 사건으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 CURRENT_YEAR_LUCK의 유일한 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 좌표의 다른 가능한 Segment가 존재하면 각 Annual Domain 결과를 별도로 보존합니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 monthIndex 존재를 이유로 Annual Domain Context를 Monthly Context로 재해석하지 않습니다."
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
     * 전체 CURRENT_YEAR_LUCK 결론이 아니다.
     * 현재 Domain 하나의 Annual 계산 Summary다.
     */

    private String summaryOf(

            LifeDomain domain,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                domain,
                "CURRENT_YEAR Annual Summary에는 LifeDomain이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "CURRENT_YEAR Annual Summary에는 Status가 필요합니다."
        );

        return switch (
                domain
                ) {

            case WEALTH ->
                    "현재 명리연도의 재물·자원 영역 Annual Context가 "
                            + status
                            + " 상태로 계산되었으며 다른 생활영역과 합성하지 않습니다.";

            case CAREER ->
                    "현재 명리연도의 일·책임 영역 Annual Context가 "
                            + status
                            + " 상태로 계산되었으며 다른 생활영역과 합성하지 않습니다.";

            case RELATIONSHIP ->
                    "현재 명리연도의 관계 영역 Annual Context가 "
                            + status
                            + " 상태로 계산되었으며 다른 생활영역과 합성하지 않습니다.";

            case LEARNING_GROWTH ->
                    "현재 명리연도의 학습·처리·표현 영역 Annual Context가 "
                            + status
                            + " 상태로 계산되었으며 다른 생활영역과 합성하지 않습니다.";

            case HEALTH_LIFESTYLE ->
                    "현재 명리연도의 생활균형 영역 Annual Context가 "
                            + status
                            + " 상태로 계산되었으며 의료적 상태나 다른 생활영역과 합성하지 않습니다.";
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