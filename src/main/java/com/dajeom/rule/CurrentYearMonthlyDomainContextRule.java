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
 * LifeDomain별 일반 Monthly Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 현재 Timeline Segment에 이미 존재하는
 * 공식 Monthly Context LifeDomain Match를
 * CURRENT_YEAR_LUCK의 독립적인 월운 결과로
 * 1:1 보존한다.
 *
 * =================================================
 * 실제 존재하는 Source
 * =================================================
 *
 * WEALTH
 * ->
 * WEALTH_MONTHLY_CONTEXT
 *
 * CAREER
 * ->
 * CAREER_MONTHLY_CONTEXT
 *
 * RELATIONSHIP
 * ->
 * RELATIONSHIP_MONTHLY_CONTEXT
 *
 * LEARNING_GROWTH
 * ->
 * LEARNING_MONTHLY_CONTEXT
 *
 * =================================================
 * 존재하지 않는 Source
 * =================================================
 *
 * 현재 실제 Java 소스에는:
 *
 * HEALTH_MONTHLY_CONTEXT
 *
 * 가 존재하지 않는다.
 *
 * 따라서 이 Rule에서는
 * HEALTH_LIFESTYLE의 일반 Monthly Context를
 * 임의로 생성하지 않는다.
 *
 * HEALTH 월운은 별도의:
 *
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * 계산만 사용한다.
 *
 * =================================================
 * 중요한 시간층 경계
 * =================================================
 *
 * 실제 upstream Monthly Context Rule들은
 * MonthlyLuckContextAnalysis의:
 *
 * signalsOf(...)
 *
 * 계열 API를 사용한다.
 *
 * allSignals()
 *
 * 를 사용하지 않는다.
 *
 * =================================================
 * allSignals()를 사용하지 않는 이유
 * =================================================
 *
 * allSignals()
 *
 * =
 *
 * monthlySignals
 * +
 * decadeContexts.segmentSignals
 *
 * 이다.
 *
 * 대운 Context는 이미:
 *
 * WEALTH_DECADE_CONTEXT
 * CAREER_DECADE_CONTEXT
 * RELATIONSHIP_DECADE_CONTEXT
 * LEARNING_DECADE_CONTEXT
 *
 * 등으로 별도 계산된다.
 *
 * 따라서 Monthly Context 단계에서
 * 대운 Segment Signal을 다시 소비하지 않는다.
 *
 * =================================================
 * 15-7 upstream 계산 보존
 * =================================================
 *
 * MonthlyLuckContextAnalysis 안에서 이미 계산된:
 *
 * - 월운 십성
 * - 원국 × 월운 관계
 * - 세운 × 월운 관계
 * - 가능한 경우 대운 × 월운 관계
 * - 용신
 * - 격국
 * - 궁위
 * - 합화·기능 변화
 * - 다층 관계
 *
 * 자체는 그대로 유효하다.
 *
 * Category에서는 해당 계산을
 * 다시 수행하지 않는다.
 *
 * =================================================
 * WEALTH Monthly Context
 * =================================================
 *
 * WealthMonthlyContextRule은:
 *
 * DIRECT_WEALTH
 * INDIRECT_WEALTH
 *
 * 관련 monthlySignals를 실제 Java에서
 * 집계하여 하나의 공식
 * WEALTH_MONTHLY_CONTEXT Match를 만든다.
 *
 * =================================================
 * CAREER Monthly Context
 * =================================================
 *
 * CareerMonthlyContextRule은:
 *
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * 관련 monthlySignals를 실제 Java에서
 * 집계하여 하나의 공식
 * CAREER_MONTHLY_CONTEXT Match를 만든다.
 *
 * =================================================
 * RELATIONSHIP Monthly Context
 * =================================================
 *
 * RelationshipMonthlyContextRule은
 * 실제 Java에서 필요에 따라 다음 Match를
 * 각각 생성한다.
 *
 * 1.
 * DAY 궁위 관련 Monthly Context
 *
 * 2.
 * 재성·관살 관련 Monthly Context
 *
 * 3.
 * DAY 궁위 + 관계 십성
 * Combined Monthly Context
 *
 * =================================================
 * RELATIONSHIP Combined 경계
 * =================================================
 *
 * DAY 궁위 관련 Signal과
 * 관계 십성 관련 Signal이 모두 존재할 때
 * RelationshipMonthlyContextRule 자체가
 * 두 Signal 집합을 실제로 결합하여
 * Combined Match를 생성한다.
 *
 * 이 Combined Match는
 * 공식 upstream Java 계산이다.
 *
 * 따라서 그대로 보존한다.
 *
 * 하지만 Category에서:
 *
 * DAY Match
 * +
 * TenGod Match
 *
 * 를 다시 보고
 * Combined Match를 새로 생성하지 않는다.
 *
 * =================================================
 * LEARNING Monthly Context
 * =================================================
 *
 * LearningMonthlyContextRule은
 * 실제 Java에서:
 *
 * 1.
 * 인성 계열 Context
 *
 * 2.
 * 식상 계열 Context
 *
 * 3.
 * 인성 + 식상 Combined Context
 *
 * 를 각각 생성할 수 있다.
 *
 * =================================================
 * LEARNING Combined 경계
 * =================================================
 *
 * 인성 Signal과 식상 Signal이
 * 모두 존재하는 경우
 * LearningMonthlyContextRule 자체가
 * 실제 Signal들을 결합하여
 * Combined Match를 생성한다.
 *
 * 이 결과 역시 공식 upstream 계산이다.
 *
 * Category에서는:
 *
 * Resource Match
 * +
 * Output Match
 *
 * 를 보고 새로운 Combined Match를
 * 생성하지 않는다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * 공식 Monthly LifeDomainRuleMatch 하나
 *
 * ->
 *
 * CURRENT_YEAR_LUCK Category Match 하나
 *
 * 로 보존한다.
 *
 * Relationship 또는 Learning처럼
 * 같은 RuleCode로 여러 upstream Match가
 * 생성될 수 있는 경우에도
 * 각각 그대로 1:1 보존한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 각각의 upstream LifeDomainRuleMatch가 가진
 * 공식 LifeDomainSignalStatus를
 * Category Status로 그대로 변환한다.
 *
 * SUPPORTIVE
 * ->
 * SUPPORTIVE
 *
 * PRESSURE
 * ->
 * PRESSURE
 *
 * MIXED
 * ->
 * MIXED
 *
 * CONTEXTUAL
 * ->
 * CONTEXTUAL
 *
 * =================================================
 * Upstream Status 계산 보존
 * =================================================
 *
 * 각 Monthly Rule은 실제 Signal을 집계해서:
 *
 * CONFLICTED 존재
 * ->
 * MIXED
 *
 * 지원 계열 + WEAKENED
 * ->
 * MIXED
 *
 * WEAKENED
 * ->
 * PRESSURE
 *
 * SUPPORTED
 * ->
 * SUPPORTIVE
 *
 * QUALIFIED only
 * ->
 * CONTEXTUAL
 *
 * 등의 공식 상태를 계산한다.
 *
 * 해당 계산은 그대로 보존한다.
 *
 * =================================================
 * 실제 upstream MIXED와 Category 합성의 구분
 * =================================================
 *
 * 허용:
 *
 * 하나의 Monthly Context Rule 내부에서
 * 실제 monthlySignals의 상태를 계산하여
 * upstream Java가 MIXED를 확정한 경우.
 *
 * -------------------------------------------------
 *
 * 금지:
 *
 * 서로 다른 LifeDomain Monthly Match가
 * 같은 월에 존재한다는 이유로
 * Category에서 새 MIXED를 만드는 것.
 *
 * =================================================
 * Cross-Domain Status 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * WEALTH Monthly SUPPORTIVE
 * +
 * CAREER Monthly PRESSURE
 * =
 * CURRENT_YEAR MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * RELATIONSHIP Monthly SUPPORTIVE
 * +
 * LEARNING Monthly SUPPORTIVE
 * =
 * 더 강한 SUPPORTIVE
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * WEALTH Monthly PRESSURE
 * +
 * CAREER Monthly PRESSURE
 * =
 * 더 강한 PRESSURE
 *
 * 금지.
 *
 * =================================================
 * Cross-Domain 관계 생성 금지
 * =================================================
 *
 * 다음을 새로 만들지 않는다.
 *
 * - 재물 월운 때문에 직업 상태가 변한다
 * - 직업 월운 때문에 관계 부담이 생긴다
 * - 학습 월운이 재물로 이어진다
 * - 관계 월운 때문에 학습이 흔들린다
 * - 여러 영역이 같이 움직여 큰 변화가 생긴다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * Decade Context와 분리
 * =================================================
 *
 * 앞에서 연결한:
 *
 * WEALTH_DECADE_CONTEXT
 * CAREER_DECADE_CONTEXT
 * RELATIONSHIP_DECADE_CONTEXT
 * LEARNING_DECADE_CONTEXT
 * HEALTH_DECADE_CONTEXT
 *
 * 를 이 Rule에서 사용하지 않는다.
 *
 * =================================================
 * 대운 × 월운 재합성 금지
 * =================================================
 *
 * MonthlyLuckContextAnalysis 내부에서
 * 가능한 경우 실제 대운 × 월운 관계가
 * Java 계산으로 포함되는 것은 인정한다.
 *
 * 하지만 Category에서 별도의:
 *
 * DECADE_CONTEXT Match
 *
 * 를 다시 가져와
 * Monthly Context Match와 결합하지 않는다.
 *
 * =================================================
 * Annual Context와 분리
 * =================================================
 *
 * 다음 Source도 이 Rule에서 사용하지 않는다.
 *
 * WEALTH_ANNUAL_CONTEXT
 * CAREER_ANNUAL_CONTEXT
 * RELATIONSHIP_ANNUAL_CONTEXT
 * LEARNING_ANNUAL_CONTEXT
 * HEALTH_ANNUAL_CONTEXT
 *
 * =================================================
 * 세운 × 월운 재합성 금지
 * =================================================
 *
 * MonthlyLuckContextAnalysis 내부에서
 * 실제 세운 × 월운 관계가 계산되어
 * monthlySignal에 포함된 경우
 * 그 upstream 결과는 그대로 인정한다.
 *
 * 하지만 Category에서 별도의:
 *
 * ANNUAL_CONTEXT Match
 *
 * 를 다시 가져와
 * Monthly Context와 결합하지 않는다.
 *
 * =================================================
 * Annual Functional과 분리
 * =================================================
 *
 * 다음 Annual Functional Source도
 * 사용하지 않는다.
 *
 * WEALTH_ANNUAL_FUNCTIONAL
 * CAREER_ANNUAL_FUNCTIONAL
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * =================================================
 * Monthly Functional과 분리
 * =================================================
 *
 * 다음 단계에서 연결할:
 *
 * WEALTH_MONTHLY_FUNCTIONAL
 * CAREER_MONTHLY_FUNCTIONAL
 * LEARNING_GROWTH_MONTHLY_FUNCTIONAL
 * HEALTH_LIFESTYLE_MONTHLY_FUNCTIONAL
 *
 * 역시 이 Rule에서 사용하지 않는다.
 *
 * =================================================
 * RELATIONSHIP Monthly Functional
 * =================================================
 *
 * 현재 실제 소스에는:
 *
 * RELATIONSHIP_MONTHLY_FUNCTIONAL
 *
 * 이 존재하지 않는다.
 *
 * 따라서 관계 영역 Monthly Functional을
 * 임의로 생성하지 않는다.
 *
 * =================================================
 * Natal과 분리
 * =================================================
 *
 * CURRENT_YEAR_LUCK View에는
 * Natal LifeDomain Match도 존재할 수 있다.
 *
 * 하지만 이 Rule에서는
 * natalMatches를 사용하지 않는다.
 *
 * 원국 구조와 월운 Context를
 * Category 단계에서 다시 합성하지 않는다.
 *
 * =================================================
 * Monthly provenance 경계
 * =================================================
 *
 * 같은 명리연도·월에서 여러 Domain의
 * Monthly Context Match가 존재하더라도
 * 모두 동일한:
 *
 * MonthlyLuckContextAnalysis
 *
 * 에서 파생되었을 수 있다.
 *
 * 따라서 이후 Grounding / Prominence에서:
 *
 * Domain Match 4개
 * =
 * 독립 검증 4회
 *
 * 로 단순 계산해서는 안 된다.
 *
 * =================================================
 * 같은 Domain의 복수 Match
 * =================================================
 *
 * 특히:
 *
 * RELATIONSHIP_MONTHLY_CONTEXT
 * LEARNING_MONTHLY_CONTEXT
 *
 * 는 동일 Domain / 동일 RuleCode에서
 * 복수 공식 Match가 생성될 수 있다.
 *
 * 이 역시 같은 월운 Source에서 파생된
 * 서로 다른 공식 계산 결과다.
 *
 * Match 개수를 곧바로
 * 독립 검증 횟수로 사용하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개라면
 * 각 Segment에 실제 존재하는
 * Monthly Context Match를 각각 보존한다.
 *
 * 특정 Segment 하나를
 * 현재 월운의 대표값으로
 * 임의 선택하지 않는다.
 *
 * =================================================
 * monthIndex
 * =================================================
 *
 * 결과는 CURRENT_TIMELINE Layer이며:
 *
 * - sajuYear
 * - monthIndex
 *
 * 를 현재 Segment 좌표 그대로 보존한다.
 *
 * monthIndex == 0도
 * 유효한 Timeline 좌표다.
 *
 * =================================================
 * 전체 CURRENT_YEAR Status 생성 금지
 * =================================================
 *
 * 이 Rule은:
 *
 * - 월운 전체 Status
 * - 현재 연도 전체 Status
 * - 월별 종합운 Status
 *
 * 를 생성하지 않는다.
 *
 * 각각의 Domain Monthly Context를
 * 독립적으로 보존하기만 한다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * WEALTH Monthly SUPPORTIVE
 * != 수입 증가
 * != 투자 성공
 *
 * CAREER Monthly SUPPORTIVE
 * != 취업
 * != 승진
 *
 * RELATIONSHIP Monthly SUPPORTIVE
 * != 연애 시작
 * != 결혼
 *
 * LEARNING Monthly SUPPORTIVE
 * != 시험 합격
 * != 자격 취득
 *
 * -------------------------------------------------
 *
 * PRESSURE 역시:
 *
 * - 손실
 * - 퇴사
 * - 이별
 * - 시험 실패
 *
 * 등을 직접 의미하지 않는다.
 *
 * =================================================
 * 대표 Domain 선택 금지
 * =================================================
 *
 * 다음 기준으로 현재 월의
 * 대표 영역을 선택하지 않는다.
 *
 * - Match 수
 * - SUPPORTIVE 수
 * - PRESSURE 수
 * - MIXED 수
 * - Signal 수
 *
 * =================================================
 * Match 개수 점수화 금지
 * =================================================
 *
 * Monthly Match 개수를:
 *
 * - 올해 운 강도
 * - 월운 강도
 * - 변화량
 * - 사건 개수
 * - 중요도
 *
 * 로 사용하지 않는다.
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * Source Match의:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱하여:
 *
 * - Domain
 * - Monthly 여부
 * - subtype
 * - 관계
 * - 사건
 *
 * 을 재판정하지 않는다.
 *
 * LifeDomain + RuleCode의
 * 정확한 구조화된 Pair만 사용한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - MonthlyLuckContextAnalysis 재계산
 * - monthlySignal 재계산
 * - upstream LifeDomain Status 재계산
 * - Relationship Combined Match 재생성
 * - Learning Combined Match 재생성
 * - Cross-Domain Status 합성
 * - Decade Context와 Status 합성
 * - Annual Context와 Status 합성
 * - Annual Functional과 Status 합성
 * - Monthly Functional과 Status 합성
 * - Natal Match와 Status 합성
 * - 전체 CURRENT_YEAR Status 생성
 * - 현재 월 전체 Status 생성
 * - 대표 Domain 선택
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 존재하지 않는 HEALTH_MONTHLY_CONTEXT 생성
 * - 존재하지 않는 RELATIONSHIP_MONTHLY_FUNCTIONAL 생성
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 실제 현재 연도 사건 생성
 */
@Component
public class CurrentYearMonthlyDomainContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CURRENT_YEAR_MONTHLY_DOMAIN_CONTEXT";

    private static final String
            WEALTH_MONTHLY_CONTEXT_RULE_CODE =
            "WEALTH_MONTHLY_CONTEXT";

    private static final String
            CAREER_MONTHLY_CONTEXT_RULE_CODE =
            "CAREER_MONTHLY_CONTEXT";

    private static final String
            RELATIONSHIP_MONTHLY_CONTEXT_RULE_CODE =
            "RELATIONSHIP_MONTHLY_CONTEXT";

    private static final String
            LEARNING_MONTHLY_CONTEXT_RULE_CODE =
            "LEARNING_MONTHLY_CONTEXT";


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
                "CurrentYearMonthlyDomainContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CurrentYearMonthlyDomainContextRule은 CURRENT_YEAR_LUCK View만 평가할 수 있습니다."
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
                            "CURRENT_YEAR_LUCK Monthly Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 공식 Monthly Domain Source만 선택
             * =================================================
             *
             * Relationship / Learning은
             * 동일 RuleCode로 복수 upstream Match가
             * 존재할 수 있으므로
             * 각각 1:1로 그대로 보존한다.
             */

            for (
                    LifeDomainRuleMatch match
                    : normalizedSegment.matches()
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                match,
                                "CURRENT_YEAR_LUCK Monthly Segment Match는 null일 수 없습니다."
                        );

                if (
                        !isOfficialMonthlyDomainContext(
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
     * 공식 Monthly Domain Context 판별
     * =================================================
     *
     * 정확한:
     *
     * LifeDomain
     * +
     * RuleCode
     *
     * Pair만 허용한다.
     *
     * HEALTH_LIFESTYLE은
     * 공식 HEALTH_MONTHLY_CONTEXT가 없으므로
     * 항상 false다.
     */

    private boolean isOfficialMonthlyDomainContext(
            LifeDomainRuleMatch match
    ) {

        Objects.requireNonNull(
                match,
                "Monthly Domain Context 판별에는 LifeDomainRuleMatch가 필요합니다."
        );

        String ruleCode =
                requireText(
                        match.ruleCode(),
                        "Monthly Domain Context Match에는 ruleCode가 필요합니다."
                );

        return switch (
                match.domain()
                ) {

            case WEALTH ->
                    WEALTH_MONTHLY_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case CAREER ->
                    CAREER_MONTHLY_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case RELATIONSHIP ->
                    RELATIONSHIP_MONTHLY_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case LEARNING_GROWTH ->
                    LEARNING_MONTHLY_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case HEALTH_LIFESTYLE ->
                    false;
        };
    }


    /*
     * =================================================
     * Status 1 : 1 변환
     * =================================================
     *
     * 각 upstream Monthly Rule이
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
                "CURRENT_YEAR Monthly Domain Match에는 LifeDomainSignalStatus가 필요합니다."
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
                "CURRENT_YEAR Monthly Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "CURRENT_YEAR Monthly Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "현재 Segment에 이미 존재하는 "
                        + sourceMatch.domain()
                        + " Domain의 공식 Monthly Context Match를 CURRENT_YEAR_LUCK의 독립 월운 결과로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 Monthly Context Match의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 LifeDomain이나 다른 시간층의 상태와 결합하지 않고 그대로 Category Status로 변환했습니다."
        );

        reasons.add(
                "이 Status는 "
                        + sourceMatch.domain()
                        + " 영역의 현재 월운 Context 상태이며 CURRENT_YEAR_LUCK 전체나 현재 월 전체의 종합 Status를 의미하지 않습니다."
        );

        reasons.add(
                "upstream Monthly Context Rule은 MonthlyLuckContextAnalysis의 monthlySignals를 대상으로 signalsOf(...) 계열 API를 사용하며 decadeContexts.segmentSignals까지 포함하는 allSignals()를 사용하지 않습니다."
        );

        reasons.add(
                "MonthlyLuckContextAnalysis에서 이미 계산된 원국×월운, 세운×월운 및 가능한 경우 대운×월운 관계와 용신·격국·궁위·합화·기능 변화·다층 관계는 공식 upstream 결과로 그대로 인정하며 Category에서 다시 계산하지 않았습니다."
        );

        if (
                sourceMatch.domain()
                        == LifeDomain.RELATIONSHIP
        ) {

            reasons.add(
                    "RELATIONSHIP_MONTHLY_CONTEXT에서는 DAY 궁위 Context, 재성·관살 Context 또는 upstream Java가 실제 계산한 DAY 궁위+관계 십성 Combined Context가 각각 존재할 수 있으며 현재 Source Match 하나만 그대로 보존하고 다른 Relationship Match를 Category에서 다시 결합하지 않았습니다."
            );
        }

        if (
                sourceMatch.domain()
                        == LifeDomain.LEARNING_GROWTH
        ) {

            reasons.add(
                    "LEARNING_MONTHLY_CONTEXT에서는 인성 Context, 식상 Context 또는 upstream Java가 실제 계산한 인성+식상 Combined Context가 각각 존재할 수 있으며 현재 Source Match 하나만 그대로 보존하고 다른 Learning Match를 Category에서 다시 결합하지 않았습니다."
            );
        }

        reasons.add(
                "upstream Monthly Rule 자체가 동일 공식 Monthly Context의 SUPPORTED·QUALIFIED·WEAKENED·CONFLICTED Signal을 실제 계산하여 MIXED를 확정한 경우 해당 MIXED는 그대로 인정합니다."
        );

        reasons.add(
                "다른 LifeDomain의 Monthly Context가 같은 Segment에 존재하더라도 Cross-Domain SUPPORTIVE·PRESSURE·MIXED 합성을 수행하지 않았습니다."
        );

        reasons.add(
                "Decade Context를 이 Rule의 Source 또는 Status 계산에 사용하지 않았으며 MonthlyLuckContextAnalysis 내부에서 이미 반영된 실제 대운×월운 계산에 별도 Decade Match를 다시 결합하지 않았습니다."
        );

        reasons.add(
                "Annual Context를 별도 Source로 사용하지 않았으며 MonthlyLuckContextAnalysis 내부에서 이미 계산된 세운×월운 관계에 별도 Annual Context Match를 다시 결합하지 않았습니다."
        );

        reasons.add(
                "Annual Functional, Monthly Functional 및 Natal Match를 이 Rule의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "같은 MonthlyLuckContextAnalysis에서 여러 Domain Match 또는 같은 Domain의 복수 Match가 파생될 수 있으므로 Match 개수를 독립 검증 횟수나 근거 강도로 그대로 사용해서는 안 됩니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 Domain subtype, 관계, Status 또는 실제 사건을 재판정하지 않았습니다."
        );

        reasons.add(
                "이 결과를 수입 증가·승진·퇴사·연애 시작·결혼·이별·시험 합격·시험 실패 등의 실제 현재 연도 사건으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 CURRENT_YEAR_LUCK의 유일한 Monthly Context 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 존재하면 각 공식 Monthly Domain Match를 별도로 보존합니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment의 대운이 unavailable 상태이더라도 upstream MonthlyLuckContextAnalysis에서 실제 확정 가능한 monthlySignals로 생성된 Monthly Context Match는 그대로 보존하며 다른 Decade Match를 대신 추가하지 않습니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 실제 좌표이며 monthIndex == 0도 유효한 값으로 취급합니다."
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
     * CURRENT_YEAR_LUCK 전체 결론이 아니다.
     * 현재 Domain 하나의 Monthly Context 결과다.
     */

    private String summaryOf(

            LifeDomain domain,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                domain,
                "CURRENT_YEAR Monthly Summary에는 LifeDomain이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "CURRENT_YEAR Monthly Summary에는 Status가 필요합니다."
        );

        return switch (
                domain
                ) {

            case WEALTH ->
                    "현재 월운의 재물·자원 영역 Context가 "
                            + status
                            + " 상태로 계산되었으며 대운·세운 또는 다른 생활영역과 Category 단계에서 다시 합성하지 않습니다.";

            case CAREER ->
                    "현재 월운의 일·책임 영역 Context가 "
                            + status
                            + " 상태로 계산되었으며 대운·세운 또는 다른 생활영역과 Category 단계에서 다시 합성하지 않습니다.";

            case RELATIONSHIP ->
                    "현재 월운의 관계 영역 Context가 "
                            + status
                            + " 상태로 계산되었으며 upstream의 개별 또는 Combined Match를 그대로 보존하고 Category에서 새 관계를 만들지 않습니다.";

            case LEARNING_GROWTH ->
                    "현재 월운의 학습·처리·표현 영역 Context가 "
                            + status
                            + " 상태로 계산되었으며 upstream의 개별 또는 Combined Match를 그대로 보존하고 Category에서 새 관계를 만들지 않습니다.";

            case HEALTH_LIFESTYLE ->
                    throw new IllegalArgumentException(
                            "HEALTH_LIFESTYLE에는 공식 HEALTH_MONTHLY_CONTEXT Source가 없습니다."
                    );
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