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
 * LifeDomain별 Annual Functional deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 현재 Timeline Segment에 이미 존재하는
 * 공식 Annual Functional LifeDomain Match를
 * CURRENT_YEAR_LUCK의 독립 결과로 보존한다.
 *
 * =================================================
 * 실제 존재하는 Source
 * =================================================
 *
 * WEALTH
 * ->
 * WEALTH_ANNUAL_FUNCTIONAL
 *
 * CAREER
 * ->
 * CAREER_ANNUAL_FUNCTIONAL
 *
 * LEARNING_GROWTH
 * ->
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 *
 * HEALTH_LIFESTYLE
 * ->
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * =================================================
 * 존재하지 않는 Source
 * =================================================
 *
 * 현재 실제 Java 소스에는:
 *
 * RELATIONSHIP_ANNUAL_FUNCTIONAL
 *
 * 이 존재하지 않는다.
 *
 * 따라서 이 Rule에서
 * 관계 영역 Annual Functional을
 * 임의로 생성하지 않는다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * 이 Rule은:
 *
 * - 올해 전체 Functional Status
 * - 올해 종합운 Status
 * - LifeDomain 간 통합 Status
 *
 * 를 계산하지 않는다.
 *
 * 각각의 upstream Annual Functional 결과를
 * 독립적으로 보존하기만 한다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * Annual Functional LifeDomainRuleMatch 하나
 *
 * ->
 *
 * CURRENT_YEAR_LUCK Category Match 하나
 *
 * 로 보존한다.
 *
 * =================================================
 * WEALTH Annual Functional
 * =================================================
 *
 * WealthAnnualFunctionalRule은
 * RESOURCE_REALITY Function을 중심으로:
 *
 * - Annual Function 활성
 * - 현재 대운 결합 후 Resolved Function
 * - Capacity
 * - weakSidePressureContact
 * - CrossFunctional Pattern
 * - qualification
 * - mixedContext
 *
 * 등을 실제 Java에서 계산한다.
 *
 * Category에서는 재계산하지 않는다.
 *
 * =================================================
 * CAREER Annual Functional
 * =================================================
 *
 * CareerAnnualFunctionalRule은
 * RESPONSIBILITY Function을 중심으로:
 *
 * - Annual Function 활성
 * - 현재 Resolved Function
 * - Capacity
 * - conflict Pattern
 * - regulation Pattern
 * - RESPONSIBILITY_INTERNAL_MIXTURE
 * - qualification
 * - mixedContext
 *
 * 등을 실제 Java에서 계산한다.
 *
 * 해당 typed 관계는
 * upstream Java가 실제 계산한 경우에만
 * 그대로 보존한다.
 *
 * =================================================
 * LEARNING Annual Functional
 * =================================================
 *
 * LearningGrowthAnnualFunctionalRule은:
 *
 * - EXPRESSION
 * - PROCESSING
 *
 * Function을 대상으로:
 *
 * - Annual 활성
 * - Resolved 활성
 * - sameDirectionAccumulationContact
 * - counterDirectionContact
 * - 실제 CrossFunctional Pattern
 * - qualification
 * - mixedContext
 *
 * 등을 계산한다.
 *
 * PROCESSING_REGULATES_EXPRESSION,
 * PROCESSING_RESTRAINS_OUTPUT 등
 * upstream에서 실제 계산된 관계는
 * 그대로 유효하다.
 *
 * =================================================
 * HEALTH Annual Functional
 * =================================================
 *
 * HealthLifestyleAnnualFunctionalRule은:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * 다섯 Function 전체에 대해:
 *
 * - Annual Capacity
 * - Resolved Function
 * - sameDirectionAccumulationContact
 * - counterDirectionContact
 * - effective CrossFunctional Pattern
 * - qualification
 * - mixedContext
 *
 * 등을 실제 Java에서 계산한다.
 *
 * =================================================
 * 실제 upstream 관계와 Category 관계의 구분
 * =================================================
 *
 * 허용:
 *
 * upstream Annual Functional Rule이
 * 실제 조건을 확인하여 생성한
 * typed CrossFunctional Pattern.
 *
 * -------------------------------------------------
 *
 * 금지:
 *
 * 서로 다른 LifeDomain Functional Match가
 * 같은 연도에 존재한다는 이유만으로
 * Category에서 새로운 관계를 만드는 것.
 *
 * =================================================
 * Cross-Domain 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * WEALTH Functional
 * +
 * CAREER Functional
 * =
 * 새로운 CURRENT_YEAR Status
 *
 * -------------------------------------------------
 *
 * CAREER Functional
 * +
 * LEARNING Functional
 * =
 * 새로운 CURRENT_YEAR Status
 *
 * -------------------------------------------------
 *
 * LEARNING Functional
 * +
 * HEALTH Functional
 * =
 * 새로운 CURRENT_YEAR Status
 *
 * =================================================
 * Cross-Domain 관계 생성 금지
 * =================================================
 *
 * 다음을 새로 만들지 않는다.
 *
 * - 재물 기능 때문에 직업 책임이 커진다
 * - 직업 책임 때문에 학습 기능이 활성된다
 * - 학습 기능이 건강 부담으로 이어진다
 * - 건강 Functional Pressure 때문에 일이 흔들린다
 * - 재물과 직업 Function이 같이 있으므로 큰 변화가 있다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * Annual Context와 분리
 * =================================================
 *
 * 앞 단계에서 처리한:
 *
 * WEALTH_ANNUAL_CONTEXT
 * CAREER_ANNUAL_CONTEXT
 * RELATIONSHIP_ANNUAL_CONTEXT
 * LEARNING_ANNUAL_CONTEXT
 * HEALTH_ANNUAL_CONTEXT
 *
 * 와 이번 Annual Functional Source는
 * 서로 다른 공식 계산이다.
 *
 * Category에서는 서로 합성하지 않는다.
 *
 * =================================================
 * Annual Context + Functional 합성 금지
 * =================================================
 *
 * 예:
 *
 * WEALTH_ANNUAL_CONTEXT = SUPPORTIVE
 *
 * +
 *
 * WEALTH_ANNUAL_FUNCTIONAL = PRESSURE
 *
 * =
 *
 * MIXED
 *
 * 같은 계산을 하지 않는다.
 *
 * -------------------------------------------------
 *
 * CAREER_ANNUAL_CONTEXT = PRESSURE
 *
 * +
 *
 * CAREER_ANNUAL_FUNCTIONAL = PRESSURE
 *
 * =
 *
 * 더 강한 PRESSURE
 *
 * 같은 계산도 하지 않는다.
 *
 * =================================================
 * Annual provenance 경계
 * =================================================
 *
 * 같은 LifeDomain의:
 *
 * ANNUAL_CONTEXT
 *
 * 와
 *
 * ANNUAL_FUNCTIONAL
 *
 * 은 서로 다른 계산이지만
 * 동일 명리연도의 Annual 계층에 속한다.
 *
 * 따라서 이후 Grounding / Prominence에서는
 * 단순히 독립 검증 2회로 계산하여
 * 근거 강도를 부풀려서는 안 된다.
 *
 * 동일 Annual temporal provenance를
 * 고려해야 한다.
 *
 * =================================================
 * Decade와 분리
 * =================================================
 *
 * 다음 Source를 직접 사용하지 않는다.
 *
 * WEALTH_DECADE_CONTEXT
 * CAREER_DECADE_CONTEXT
 * RELATIONSHIP_DECADE_CONTEXT
 * LEARNING_DECADE_CONTEXT
 * HEALTH_DECADE_CONTEXT
 *
 * Annual Functional upstream 내부에서
 * 현재 대운 결합 후 Resolved Function을
 * 실제 Java 계산에 사용하는 것은 인정한다.
 *
 * Category에서는 별도 Decade Match를
 * 다시 결합하지 않는다.
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
 * 및 각 Monthly Functional Source.
 *
 * 월운은 별도 Rule에서 처리한다.
 *
 * =================================================
 * Natal과 분리
 * =================================================
 *
 * 원국 LifeDomain Match도
 * 이 Rule에서 사용하지 않는다.
 *
 * 원국 구조와 Annual Functional 결과를
 * Category 단계에서 다시 합성하지 않는다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 각각의 upstream LifeDomainRuleMatch가 가진
 * 공식 LifeDomainSignalStatus를
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
 * Status 의미
 * =================================================
 *
 * 이 Status는 반드시:
 *
 * 해당 LifeDomain Annual Functional 상태
 *
 * 로만 읽는다.
 *
 * 예:
 *
 * CAREER_ANNUAL_FUNCTIONAL = PRESSURE
 *
 * 는:
 *
 * 올해 전체가 PRESSURE
 *
 * 라는 뜻이 아니다.
 *
 * =================================================
 * 대표 Domain 선택 금지
 * =================================================
 *
 * 다음 기준으로 올해 대표 분야를
 * 선택하지 않는다.
 *
 * - 가장 많은 PRESSURE
 * - 가장 많은 SUPPORTIVE
 * - Match 수
 * - Functional 수
 * - Pattern 수
 *
 * =================================================
 * Match 개수 점수화 금지
 * =================================================
 *
 * Functional Match 개수를:
 *
 * - 올해 운 강도
 * - 사건 개수
 * - 변화 크기
 * - 중요도
 *
 * 로 변환하지 않는다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * WEALTH Functional PRESSURE
 * != 손실
 * != 지출 증가
 *
 * CAREER Functional PRESSURE
 * != 해고
 * != 퇴사
 * != 승진 실패
 *
 * LEARNING Functional PRESSURE
 * != 시험 실패
 *
 * HEALTH Functional PRESSURE
 * != 질병
 * != 발병
 *
 * -------------------------------------------------
 *
 * MIXED 역시:
 *
 * 이직
 * 진로 변경
 * 투자 변화
 * 시험 결과 변화
 * 건강 이상 반복
 *
 * 같은 사건을 직접 의미하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각 Segment에 실제 존재하는
 * Annual Functional Match를
 * 각각 독립적으로 보존한다.
 *
 * 특정 Segment를 대표값으로
 * 임의 선택하지 않는다.
 *
 * =================================================
 * 좌표
 * =================================================
 *
 * 결과는 CURRENT_TIMELINE Layer이며:
 *
 * - sajuYear
 * - monthIndex
 *
 * 를 현재 Segment 좌표 그대로 보존한다.
 *
 * monthIndex가 존재한다고 해서
 * Annual Functional을 Monthly Functional로
 * 재해석하지 않는다.
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
 * - Function
 * - Pattern
 * - Capacity
 * - qualification
 * - 관계
 * - 사건
 *
 * 을 재판정하지 않는다.
 *
 * LifeDomain과 RuleCode의
 * 정확한 구조화된 조합만 사용한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Annual Functional 재계산
 * - Capacity 재계산
 * - Resolved Function 재계산
 * - CrossFunctional Pattern 재계산
 * - qualification 재계산
 * - LifeDomain Status 재계산
 * - Cross-Domain Status 합성
 * - Annual Context와 Status 합성
 * - Decade Context와 Status 합성
 * - Monthly Context와 Status 합성
 * - Monthly Functional과 Status 합성
 * - Natal Match와 Status 합성
 * - 전체 CURRENT_YEAR Status 생성
 * - 대표 Domain 선택
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 존재하지 않는 RELATIONSHIP_ANNUAL_FUNCTIONAL 생성
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 실제 현재 연도 사건 생성
 */
@Component
public class CurrentYearAnnualFunctionalDomainRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CURRENT_YEAR_ANNUAL_FUNCTIONAL_DOMAIN";

    private static final String
            WEALTH_ANNUAL_FUNCTIONAL_RULE_CODE =
            "WEALTH_ANNUAL_FUNCTIONAL";

    private static final String
            CAREER_ANNUAL_FUNCTIONAL_RULE_CODE =
            "CAREER_ANNUAL_FUNCTIONAL";

    private static final String
            LEARNING_ANNUAL_FUNCTIONAL_RULE_CODE =
            "LEARNING_GROWTH_ANNUAL_FUNCTIONAL";

    private static final String
            HEALTH_ANNUAL_FUNCTIONAL_RULE_CODE =
            "HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL";


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
                "CurrentYearAnnualFunctionalDomainRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CurrentYearAnnualFunctionalDomainRule은 CURRENT_YEAR_LUCK View만 평가할 수 있습니다."
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
                            "CURRENT_YEAR_LUCK Annual Functional Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 공식 Annual Functional Source만 선택
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
                        !isOfficialAnnualFunctional(
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
     * 공식 Annual Functional 판별
     * =================================================
     *
     * 정확한 Domain + RuleCode Pair만 사용한다.
     *
     * RELATIONSHIP은 공식 Annual Functional
     * Source가 없으므로 항상 false다.
     */

    private boolean isOfficialAnnualFunctional(
            LifeDomainRuleMatch match
    ) {

        Objects.requireNonNull(
                match,
                "Annual Functional 판별에는 LifeDomainRuleMatch가 필요합니다."
        );

        String ruleCode =
                requireText(
                        match.ruleCode(),
                        "Annual Functional Match에는 ruleCode가 필요합니다."
                );

        return switch (
                match.domain()
                ) {

            case WEALTH ->
                    WEALTH_ANNUAL_FUNCTIONAL_RULE_CODE.equals(
                            ruleCode
                    );

            case CAREER ->
                    CAREER_ANNUAL_FUNCTIONAL_RULE_CODE.equals(
                            ruleCode
                    );

            case RELATIONSHIP ->
                    false;

            case LEARNING_GROWTH ->
                    LEARNING_ANNUAL_FUNCTIONAL_RULE_CODE.equals(
                            ruleCode
                    );

            case HEALTH_LIFESTYLE ->
                    HEALTH_ANNUAL_FUNCTIONAL_RULE_CODE.equals(
                            ruleCode
                    );
        };
    }


    /*
     * =================================================
     * Status 1 : 1 변환
     * =================================================
     *
     * 각 upstream Annual Functional Rule이
     * 이미 확정한 공식 LifeDomainSignalStatus만
     * 그대로 사용한다.
     *
     * 다른 Domain 또는 다른 시간층의
     * 상태는 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CURRENT_YEAR Annual Functional Match에는 LifeDomainSignalStatus가 필요합니다."
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
                "CURRENT_YEAR Annual Functional Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "CURRENT_YEAR Annual Functional Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "현재 Segment에 이미 존재하는 "
                        + sourceMatch.domain()
                        + " Domain의 공식 Annual Functional Match를 CURRENT_YEAR_LUCK의 독립 Functional 결과로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 Annual Functional Match의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 LifeDomain이나 다른 시간층의 상태와 결합하지 않고 그대로 Category Status로 변환했습니다."
        );

        reasons.add(
                "이 Status는 "
                        + sourceMatch.domain()
                        + " 영역의 Annual Functional 상태이며 CURRENT_YEAR_LUCK 전체의 종합 Status를 의미하지 않습니다."
        );

        reasons.add(
                "각 upstream Annual Functional Rule에서 실제 Java로 계산된 Capacity, 현재 Resolved Function, qualification 및 유효한 typed CrossFunctional Pattern은 그대로 인정하며 이 Category Rule에서 재계산하지 않았습니다."
        );

        reasons.add(
                "upstream Java가 실제 계산한 CrossFunctional Pattern은 보존하지만 서로 다른 LifeDomain Annual Functional Match가 같은 Segment에 존재한다는 이유만으로 새로운 Cross-Domain 관계를 만들지 않았습니다."
        );

        reasons.add(
                "같은 LifeDomain의 Annual Context Match를 가져와 Annual Functional Status와 합성하지 않았습니다."
        );

        reasons.add(
                "Decade Context, Monthly Context, Monthly Functional 또는 Natal Match를 이 Match의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "Annual Functional upstream 내부에서 현재 대운 결합 후 Resolved Function을 사용하는 것은 공식 Java 계산으로 인정하지만 별도의 Decade Category Match를 다시 결합하지 않았습니다."
        );

        reasons.add(
                "Annual Context와 Annual Functional은 같은 명리연도 provenance를 공유할 수 있으므로 이후 Grounding에서 단순 독립 검증 2회로 취급하여 근거 강도를 부풀려서는 안 됩니다."
        );

        reasons.add(
                "다른 LifeDomain Annual Functional Match가 SUPPORTIVE·PRESSURE·MIXED 상태로 함께 존재하더라도 전체 CURRENT_YEAR Status나 대표 Domain을 생성하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 Function 종류, Pattern, Capacity, qualification, 관계 또는 사건을 재판정하지 않았습니다."
        );

        reasons.add(
                "이 결과를 손실·승진·퇴사·시험 합격·시험 실패·질병·회복 등 실제 현재 연도 사건으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 CURRENT_YEAR_LUCK의 유일한 Annual Functional 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 좌표의 다른 가능한 Segment가 존재하면 각 Annual Functional 결과를 별도로 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment에서 대운을 사용할 수 없더라도 upstream Annual Functional Rule이 실제 Annual-only 결과를 생성했다면 해당 Match를 그대로 보존하며 다른 대운 Match를 대신 추가하지 않습니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 monthIndex 존재를 이유로 Annual Functional을 Monthly Functional로 재해석하지 않습니다."
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
     * CURRENT_YEAR_LUCK 전체 결론이 아니라
     * 현재 LifeDomain 하나의
     * Annual Functional 결과다.
     */

    private String summaryOf(

            LifeDomain domain,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                domain,
                "CURRENT_YEAR Annual Functional Summary에는 LifeDomain이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "CURRENT_YEAR Annual Functional Summary에는 Status가 필요합니다."
        );

        return switch (
                domain
                ) {

            case WEALTH ->
                    "현재 명리연도의 재물·자원 Functional 계산이 "
                            + status
                            + " 상태로 확정되어 있으며 다른 생활영역이나 Annual Context와 합성하지 않습니다.";

            case CAREER ->
                    "현재 명리연도의 일·책임 Functional 계산이 "
                            + status
                            + " 상태로 확정되어 있으며 다른 생활영역이나 Annual Context와 합성하지 않습니다.";

            case RELATIONSHIP ->
                    throw new IllegalArgumentException(
                            "RELATIONSHIP에는 공식 CURRENT_YEAR Annual Functional Source가 없습니다."
                    );

            case LEARNING_GROWTH ->
                    "현재 명리연도의 학습·처리·표현 Functional 계산이 "
                            + status
                            + " 상태로 확정되어 있으며 다른 생활영역이나 Annual Context와 합성하지 않습니다.";

            case HEALTH_LIFESTYLE ->
                    "현재 명리연도의 생활균형 Functional 계산이 "
                            + status
                            + " 상태로 확정되어 있으며 의료적 상태나 다른 생활영역과 합성하지 않습니다.";
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