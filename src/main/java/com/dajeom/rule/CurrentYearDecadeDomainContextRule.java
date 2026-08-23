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
 * LifeDomain별 Decade Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 현재 Timeline Segment에 이미 존재하는
 * 다섯 LifeDomain의 공식 대운 Context를:
 *
 * - 서로 합치지 않고
 * - 세운과 합치지 않고
 * - 월운과 합치지 않고
 * - 원국과 합치지 않고
 * - 전체 CURRENT_YEAR Status를 만들지 않고
 *
 * 독립적인 CURRENT_TIMELINE Match로 보존한다.
 *
 * =================================================
 * 대상 Source
 * =================================================
 *
 * WEALTH
 * ->
 * WEALTH_DECADE_CONTEXT
 *
 * CAREER
 * ->
 * CAREER_DECADE_CONTEXT
 *
 * RELATIONSHIP
 * ->
 * RELATIONSHIP_DECADE_CONTEXT
 *
 * LEARNING_GROWTH
 * ->
 * LEARNING_DECADE_CONTEXT
 *
 * HEALTH_LIFESTYLE
 * ->
 * HEALTH_DECADE_CONTEXT
 *
 * =================================================
 * Upstream 공통 원칙
 * =================================================
 *
 * 각 LifeDomain Decade Rule은
 * 13-6 DecadeLuckPeriodContextAnalysis에서
 * 이미 계산된:
 *
 * - 대운 십성
 * - 원국 × 대운 관계
 * - 용신·희신
 * - 격국
 * - 궁위
 * - 관계
 * - 합화·기능 변화
 *
 * 등의 deterministic Context를
 * 다시 계산하지 않고
 * LifeDomain별 상태로 변환한다.
 *
 * 이 Category Rule 역시
 * 해당 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * WEALTH
 * =================================================
 *
 * WealthDecadeContextRule은:
 *
 * DIRECT_WEALTH
 * INDIRECT_WEALTH
 *
 * 관련 DecadeLuckContextSignal을 사용한다.
 *
 * Signal.tenGodTypes에 재성이 포함된
 * 여러 Axis Signal을 실제 Java에서 모은 뒤
 * 공식 LifeDomainSignalStatus를 계산한다.
 *
 * =================================================
 * CAREER
 * =================================================
 *
 * CareerDecadeContextRule은:
 *
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * 관련 DecadeLuckContextSignal을 사용한다.
 *
 * 정관·칠살에 실제 연결된
 * 공식 대운 Context Signal을 기반으로
 * 상태를 계산한다.
 *
 * =================================================
 * RELATIONSHIP
 * =================================================
 *
 * RelationshipDecadeContextRule은
 * upstream Java에서 필요에 따라:
 *
 * - Day Palace Context
 * - 관계 TenGod Context
 * - Combined Context
 *
 * 등을 각각 LifeDomainRuleMatch로
 * 생성할 수 있다.
 *
 * =================================================
 * RELATIONSHIP Combined 경계
 * =================================================
 *
 * RelationshipDecadeContextRule이
 * 실제 조건을 확인한 뒤 생성한
 * Combined Context는
 * 공식 upstream deterministic Match다.
 *
 * 따라서 그대로 보존한다.
 *
 * 하지만 이 Category Rule에서:
 *
 * Day Palace Match
 * +
 * TenGod Match
 *
 * 를 다시 보고
 * Combined Match를 새로 만들지는 않는다.
 *
 * =================================================
 * LEARNING_GROWTH
 * =================================================
 *
 * LearningDecadeContextRule은
 * upstream Java에서:
 *
 * - Resource Context
 * - Output Context
 * - Resource + Output Combined Context
 *
 * 를 각각 생성할 수 있다.
 *
 * =================================================
 * LEARNING Combined 경계
 * =================================================
 *
 * Resource + Output Combined Match가
 * 실제 LearningDecadeContextRule에서
 * 계산되어 존재한다면
 * 그것은 공식 Java 계산이다.
 *
 * 그대로 보존한다.
 *
 * Category에서 Resource Match와
 * Output Match가 함께 존재한다는 이유로
 * 새로운 Combined Match를 만들지 않는다.
 *
 * =================================================
 * HEALTH_LIFESTYLE
 * =================================================
 *
 * HealthDecadeContextRule은
 * DecadeLuckContextAxis.USEFUL_GOD를
 * 생활균형의 temporal 조정 Context로 사용한다.
 *
 * 다음을 대운에서 새로 생성하지 않는다.
 *
 * - CLIMATE
 * - PATHOLOGY
 * - REMEDY
 * - PASSAGE
 *
 * 따라서 Category에서도
 * 대운 조후·병약·통관을 새로 만들지 않는다.
 *
 * =================================================
 * Upstream Status 계산
 * =================================================
 *
 * 각 Decade Rule은 실제 대상 Signal을
 * 평가한 뒤 공식 Status를 계산한다.
 *
 * 일반적인 경계는:
 *
 * SUPPORTED
 * ->
 * SUPPORTIVE
 *
 * QUALIFIED only
 * ->
 * CONTEXTUAL
 *
 * WEAKENED only
 * ->
 * PRESSURE
 *
 * CONFLICTED 존재
 * ->
 * MIXED
 *
 * 지원 계열 + 약화 계열
 * ->
 * MIXED
 *
 * 형태다.
 *
 * 각 Domain Rule 내부에서 실제 계산된
 * 공식 LifeDomainSignalStatus를
 * Category에서는 그대로 사용한다.
 *
 * =================================================
 * 실제 upstream MIXED 보존
 * =================================================
 *
 * 하나의 공식 Decade Context 안에서
 * upstream Java가 실제 Signal 상태를 확인해
 * MIXED를 계산한 경우:
 *
 * 그 MIXED는 그대로 인정한다.
 *
 * 이는 Category가 서로 다른 Domain을 보고
 * 새로 만든 MIXED가 아니다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * 공식 Decade LifeDomainRuleMatch 하나
 *
 * ->
 *
 * CURRENT_YEAR_LUCK Category Match 하나
 *
 * 로 보존한다.
 *
 * Relationship 또는 Learning에서
 * upstream Match가 여러 개 존재하면
 * 각각 따로 보존한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 원본 LifeDomainSignalStatus를
 * Category Status로 1:1 변환한다.
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
 * "해당 LifeDomain의 현재 대운 Context 상태"
 *
 * 로만 읽는다.
 *
 * 예:
 *
 * WEALTH_DECADE_CONTEXT = SUPPORTIVE
 *
 * !=
 *
 * 올해 전체 SUPPORTIVE
 *
 * -------------------------------------------------
 *
 * CAREER_DECADE_CONTEXT = PRESSURE
 *
 * !=
 *
 * 올해 전체 PRESSURE
 *
 * =================================================
 * Cross-Domain 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * WEALTH Decade SUPPORTIVE
 * +
 * CAREER Decade PRESSURE
 * =
 * CURRENT_YEAR MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * RELATIONSHIP Decade SUPPORTIVE
 * +
 * LEARNING Decade SUPPORTIVE
 * =
 * 더 강한 SUPPORTIVE
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * HEALTH Decade PRESSURE
 * +
 * CAREER Decade PRESSURE
 * =
 * 더 강한 PRESSURE
 *
 * 금지.
 *
 * =================================================
 * Domain 간 관계 생성 금지
 * =================================================
 *
 * 다음을 새로 만들지 않는다.
 *
 * - 재물 대운 때문에 직업 상태가 변한다
 * - 직업 대운 때문에 관계 부담이 생긴다
 * - 학습 대운이 재물로 이어진다
 * - 건강 대운이 직업을 약화한다
 * - 관계 대운이 재물을 강화한다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 C다
 *
 * =================================================
 * Annual Context와 분리
 * =================================================
 *
 * 앞에서 연결한:
 *
 * WEALTH_ANNUAL_CONTEXT
 * CAREER_ANNUAL_CONTEXT
 * RELATIONSHIP_ANNUAL_CONTEXT
 * LEARNING_ANNUAL_CONTEXT
 * HEALTH_ANNUAL_CONTEXT
 *
 * 를 이 Rule에서 사용하지 않는다.
 *
 * =================================================
 * 대운 × 세운 Status 합성 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * WEALTH_DECADE_CONTEXT = PRESSURE
 * +
 * WEALTH_ANNUAL_CONTEXT = SUPPORTIVE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * CAREER_DECADE_CONTEXT = SUPPORTIVE
 * +
 * CAREER_ANNUAL_CONTEXT = SUPPORTIVE
 * =
 * 더 강한 SUPPORTIVE
 *
 * 금지.
 *
 * =================================================
 * Upstream 세운의 대운 참조와 구분
 * =================================================
 *
 * AnnualLuckContextAnalysis 또는
 * Annual Functional 계산 자체가
 * 정확한 현재 대운 Segment를
 * 실제 Java 계산에 반영하는 것은
 * 공식 upstream 계산이다.
 *
 * 이를 그대로 인정한다.
 *
 * 그러나 Category 단계에서
 * 별도의 Decade Match와 Annual Match를
 * 다시 결합하지 않는다.
 *
 * =================================================
 * Annual Functional과 분리
 * =================================================
 *
 * 다음 Source도 사용하지 않는다.
 *
 * WEALTH_ANNUAL_FUNCTIONAL
 * CAREER_ANNUAL_FUNCTIONAL
 * LEARNING_GROWTH_ANNUAL_FUNCTIONAL
 * HEALTH_LIFESTYLE_ANNUAL_FUNCTIONAL
 *
 * =================================================
 * Monthly와 분리
 * =================================================
 *
 * 다음 Monthly Context 또는
 * Monthly Functional Source도
 * 이 Rule에서 사용하지 않는다.
 *
 * 월운은 별도 CURRENT_YEAR Rule이 담당한다.
 *
 * =================================================
 * Natal과 분리
 * =================================================
 *
 * CURRENT_YEAR_LUCK View에는
 * Natal Match도 존재할 수 있다.
 *
 * 하지만 이 Rule에서는
 * natalMatches를 사용하지 않는다.
 *
 * 원국 구조와 대운 상태를
 * Category 단계에서 다시 합성하지 않는다.
 *
 * =================================================
 * 동일 Decade provenance
 * =================================================
 *
 * 같은 Segment 안에서 여러 Domain의
 * DECADE_CONTEXT Match가 존재하더라도
 * 모두 동일한:
 *
 * DecadeLuckPeriodContextAnalysis
 *
 * 에서 파생되었을 수 있다.
 *
 * 따라서 이후 Grounding / Prominence에서
 * 단순히:
 *
 * Domain Match 5개
 * =
 * 독립 검증 5회
 *
 * 로 계산해서는 안 된다.
 *
 * 이번 Rule은 계산 결과를 분리 보존할 뿐
 * 새로운 독립 provenance를 부여하지 않는다.
 *
 * =================================================
 * 같은 Domain의 복수 Match 경계
 * =================================================
 *
 * 특히:
 *
 * RELATIONSHIP_DECADE_CONTEXT
 * LEARNING_DECADE_CONTEXT
 *
 * 는 같은 Domain / 같은 RuleCode로
 * 복수 Match가 생성될 수 있다.
 *
 * 이들도 같은 대운 Source에서 파생된
 * 서로 다른 공식 계산 결과다.
 *
 * Match 개수를 곧바로
 * 독립 검증 횟수로 사용하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Timeline Segment가 여러 개면
 * 각 Segment에 실제 존재하는
 * Decade Context Match를 각각 보존한다.
 *
 * 특정 Segment 하나를
 * 현재 연도의 대표 대운으로
 * 임의 선택하지 않는다.
 *
 * =================================================
 * 대운 unavailable
 * =================================================
 *
 * 현재 Segment에 공식 Decade Context가
 * 존재하지 않으면 아무 Match도 만들지 않는다.
 *
 * 다음으로 대체하지 않는다.
 *
 * - Annual Context
 * - Annual Functional
 * - Natal Match
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
 * 대운 Match라고 해서
 * sajuYear 또는 monthIndex를 제거하거나
 * 별도 대표 좌표로 변경하지 않는다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * WEALTH Decade SUPPORTIVE
 * != 돈을 번다
 * != 자산 증가
 *
 * CAREER Decade SUPPORTIVE
 * != 취업
 * != 승진
 *
 * RELATIONSHIP Decade SUPPORTIVE
 * != 연애
 * != 결혼
 *
 * LEARNING Decade SUPPORTIVE
 * != 시험 합격
 * != 자격 취득
 *
 * HEALTH Decade SUPPORTIVE
 * != 건강 회복
 *
 * -------------------------------------------------
 *
 * PRESSURE 역시:
 *
 * - 손실
 * - 퇴사
 * - 이별
 * - 시험 실패
 * - 질병
 *
 * 등의 사건을 확정하지 않는다.
 *
 * =================================================
 * 대표 Domain 선택 금지
 * =================================================
 *
 * 다음 기준으로
 * 올해의 대표 대운 Domain을 고르지 않는다.
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
 * 대운 Match 개수를:
 *
 * - 올해 운 강도
 * - 변화 강도
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
 * - 대운 여부
 * - subtype
 * - Status
 * - 관계
 * - 사건
 *
 * 을 재판정하지 않는다.
 *
 * Domain + RuleCode의
 * 정확한 구조화된 Pair만 사용한다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - DecadeLuckPeriodContextAnalysis 재계산
 * - 대운 Signal 재계산
 * - upstream LifeDomain Status 재계산
 * - Relationship Combined Match 재생성
 * - Learning Combined Match 재생성
 * - Cross-Domain Status 합성
 * - 대운 × 세운 Status 합성
 * - Annual Functional과 합성
 * - Monthly Context와 합성
 * - Monthly Functional과 합성
 * - Natal Match와 합성
 * - 전체 CURRENT_YEAR Status 생성
 * - 대표 Domain 선택
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 실제 현재 연도 사건 생성
 */
@Component
public class CurrentYearDecadeDomainContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CURRENT_YEAR_DECADE_DOMAIN_CONTEXT";

    private static final String
            WEALTH_DECADE_CONTEXT_RULE_CODE =
            "WEALTH_DECADE_CONTEXT";

    private static final String
            CAREER_DECADE_CONTEXT_RULE_CODE =
            "CAREER_DECADE_CONTEXT";

    private static final String
            RELATIONSHIP_DECADE_CONTEXT_RULE_CODE =
            "RELATIONSHIP_DECADE_CONTEXT";

    private static final String
            LEARNING_DECADE_CONTEXT_RULE_CODE =
            "LEARNING_DECADE_CONTEXT";

    private static final String
            HEALTH_DECADE_CONTEXT_RULE_CODE =
            "HEALTH_DECADE_CONTEXT";


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
                "CurrentYearDecadeDomainContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CurrentYearDecadeDomainContextRule은 CURRENT_YEAR_LUCK View만 평가할 수 있습니다."
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
                            "CURRENT_YEAR_LUCK Decade Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * 공식 Decade Domain Source만 선택
             * =================================================
             *
             * Relationship / Learning은
             * 동일 RuleCode로 복수 upstream Match가
             * 존재할 수 있으므로
             * 각각 1:1로 보존한다.
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
                        !isOfficialDecadeDomainContext(
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
     * 공식 Decade Domain Context 판별
     * =================================================
     *
     * Domain + RuleCode의
     * 정확한 Pair만 허용한다.
     */

    private boolean isOfficialDecadeDomainContext(
            LifeDomainRuleMatch match
    ) {

        Objects.requireNonNull(
                match,
                "Decade Domain Context 판별에는 LifeDomainRuleMatch가 필요합니다."
        );

        String ruleCode =
                requireText(
                        match.ruleCode(),
                        "Decade Domain Context Match에는 ruleCode가 필요합니다."
                );

        return switch (
                match.domain()
                ) {

            case WEALTH ->
                    WEALTH_DECADE_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case CAREER ->
                    CAREER_DECADE_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case RELATIONSHIP ->
                    RELATIONSHIP_DECADE_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case LEARNING_GROWTH ->
                    LEARNING_DECADE_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );

            case HEALTH_LIFESTYLE ->
                    HEALTH_DECADE_CONTEXT_RULE_CODE.equals(
                            ruleCode
                    );
        };
    }


    /*
     * =================================================
     * Status 1 : 1 변환
     * =================================================
     *
     * 각 upstream Decade Rule이
     * 이미 확정한 공식 Status만 사용한다.
     *
     * 다른 Match의 상태를 함께 보지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CURRENT_YEAR Decade Domain Match에는 LifeDomainSignalStatus가 필요합니다."
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
                "CURRENT_YEAR Decade Segment가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMatch,
                "CURRENT_YEAR Decade Source Match가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "현재 Segment에 이미 존재하는 "
                        + sourceMatch.domain()
                        + " Domain의 공식 Decade Context Match를 CURRENT_YEAR_LUCK의 독립 대운 결과로 1:1 보존했습니다."
        );

        reasons.add(
                "원본 Decade Context Match의 공식 LifeDomainSignalStatus는 "
                        + sourceMatch.status()
                        + "이며 다른 LifeDomain이나 다른 시간층의 상태와 결합하지 않고 그대로 Category Status로 변환했습니다."
        );

        reasons.add(
                "이 Status는 "
                        + sourceMatch.domain()
                        + " 영역의 현재 대운 Context 상태이며 CURRENT_YEAR_LUCK 전체의 종합 Status를 의미하지 않습니다."
        );

        reasons.add(
                "각 upstream Decade Rule이 DecadeLuckPeriodContextAnalysis에서 이미 계산된 대운 십성·원국×대운 관계·용신·격국·궁위·관계·합화·기능 변화 등의 deterministic Signal을 사용해 확정한 결과를 다시 계산하지 않았습니다."
        );

        if (
                sourceMatch.domain()
                        == LifeDomain.RELATIONSHIP
        ) {

            reasons.add(
                    "RELATIONSHIP_DECADE_CONTEXT에서 Day Palace Context, 관계 TenGod Context 또는 upstream Java가 실제 계산한 Combined Context가 각각 존재할 수 있으며 현재 Source Match 하나만 그대로 보존하고 다른 Relationship Match를 Category에서 다시 결합하지 않았습니다."
            );
        }

        if (
                sourceMatch.domain()
                        == LifeDomain.LEARNING_GROWTH
        ) {

            reasons.add(
                    "LEARNING_DECADE_CONTEXT에서 Resource Context, Output Context 또는 upstream Java가 실제 계산한 Resource+Output Combined Context가 각각 존재할 수 있으며 현재 Source Match 하나만 그대로 보존하고 다른 Learning Match를 Category에서 다시 결합하지 않았습니다."
            );
        }

        if (
                sourceMatch.domain()
                        == LifeDomain.HEALTH_LIFESTYLE
        ) {

            reasons.add(
                    "HEALTH_DECADE_CONTEXT는 USEFUL_GOD Axis의 생활균형 조정 Context만 사용하며 대운에서 조후·명리적 병약·통관을 새로 생성하지 않습니다."
            );
        }

        reasons.add(
                "upstream Decade Rule 자체가 동일 공식 Context의 SUPPORTED·QUALIFIED·WEAKENED·CONFLICTED Signal을 실제 계산하여 MIXED를 확정한 경우 해당 MIXED는 그대로 인정합니다."
        );

        reasons.add(
                "다른 LifeDomain의 Decade Context가 같은 Segment에 존재하더라도 Cross-Domain SUPPORTIVE·PRESSURE·MIXED 합성을 수행하지 않았습니다."
        );

        reasons.add(
                "Annual Context 또는 Annual Functional Match를 이 Rule의 Source 또는 Status 계산에 사용하지 않았으며 대운×세운 Category Status Matrix를 생성하지 않았습니다."
        );

        reasons.add(
                "Annual 계층 upstream 계산 내부에서 현재 대운 Segment가 실제 반영되는 것은 공식 Java 계산으로 인정하지만 별도의 Decade Category Match를 Annual Match와 다시 결합하지 않습니다."
        );

        reasons.add(
                "Monthly Context, Monthly Functional 및 Natal Match도 이 Rule의 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "같은 DecadeLuckPeriodContextAnalysis에서 여러 Domain Match가 파생될 수 있으므로 Match 개수를 독립 검증 횟수나 근거 강도로 그대로 사용해서는 안 됩니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 파싱하여 Domain subtype, 관계, Status 또는 실제 사건을 재판정하지 않았습니다."
        );

        reasons.add(
                "이 결과를 수입 증가·승진·퇴사·연애·결혼·시험 합격·질병·회복 등의 실제 현재 연도 사건으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 CURRENT_YEAR_LUCK의 유일한 대운 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 다른 가능한 Segment가 존재하면 각 공식 Decade Domain Match를 별도로 보존합니다."
            );
        }

        if (
                segment.beforeFirstDecadeLuck()
        ) {

            reasons.add(
                    "현재 Segment가 첫 대운 시작 전이라면 실제 Decade Context Match가 존재하는 경우에만 Category Match를 생성하며 Annual 또는 Natal 결과를 대운 대신 사용하지 않습니다."
            );
        }

        if (
                segment.afterGeneratedDecadeRange()
        ) {

            reasons.add(
                    "현재 Segment가 생성된 대운 범위 이후라면 실제 Decade Context Match가 존재하는 경우에만 결과를 보존하며 다른 시간층으로 보충하지 않습니다."
            );
        }

        if (
                segment.decadeUnavailable()
        ) {

            reasons.add(
                    "현재 Segment의 대운이 unavailable 상태라면 Annual Context, Annual Functional, Monthly 또는 Natal Match를 Decade Context 대신 사용하지 않습니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 대운 결과를 세운 또는 월운 결과로 재해석하지 않습니다."
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
     * 현재 Domain 하나의 Decade 계산이다.
     */

    private String summaryOf(

            LifeDomain domain,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                domain,
                "CURRENT_YEAR Decade Summary에는 LifeDomain이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "CURRENT_YEAR Decade Summary에는 Status가 필요합니다."
        );

        return switch (
                domain
                ) {

            case WEALTH ->
                    "현재 대운의 재물·자원 영역 Context가 "
                            + status
                            + " 상태로 계산되었으며 세운·월운 또는 다른 생활영역과 합성하지 않습니다.";

            case CAREER ->
                    "현재 대운의 일·책임 영역 Context가 "
                            + status
                            + " 상태로 계산되었으며 세운·월운 또는 다른 생활영역과 합성하지 않습니다.";

            case RELATIONSHIP ->
                    "현재 대운의 관계 영역 Context가 "
                            + status
                            + " 상태로 계산되었으며 upstream의 개별 또는 Combined Match를 그대로 보존하고 Category에서 새 관계를 만들지 않습니다.";

            case LEARNING_GROWTH ->
                    "현재 대운의 학습·처리·표현 영역 Context가 "
                            + status
                            + " 상태로 계산되었으며 upstream의 개별 또는 Combined Match를 그대로 보존하고 Category에서 새 관계를 만들지 않습니다.";

            case HEALTH_LIFESTYLE ->
                    "현재 대운의 생활균형 조정 Context가 "
                            + status
                            + " 상태로 계산되었으며 의료적 상태나 다른 시간층·생활영역과 합성하지 않습니다.";
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