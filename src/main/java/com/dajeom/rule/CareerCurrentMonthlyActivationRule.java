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
 * CAREER 현재 월 Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 현재 Timeline Segment에 실제 존재하는:
 *
 * CAREER_MONTHLY_CONTEXT
 *
 * 의 공식 상태만
 * ConsultationCategory.CAREER의
 * 현재 월 deterministic 결과로 보존한다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 기존에는:
 *
 * CAREER_ANNUAL_CONTEXT
 * +
 * CAREER_MONTHLY_CONTEXT
 *
 * 를 결합하여
 * 세운 × 월운 Status Matrix를 계산했다.
 *
 * 이제 그렇게 하지 않는다.
 *
 * =================================================
 * 시간층 분리
 * =================================================
 *
 * 대운:
 *
 * CAREER_DECADE_CONTEXT
 *
 * ->
 *
 * CareerDecadeContextDeterministicRule
 *
 * -------------------------------------------------
 *
 * 세운:
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * ->
 *
 * CareerCurrentStructuralFlowRule
 *
 * -------------------------------------------------
 *
 * 월운:
 *
 * CAREER_MONTHLY_CONTEXT
 *
 * ->
 *
 * 이 Rule
 *
 * =================================================
 * 공식 Status Anchor
 * =================================================
 *
 * 이 Rule의 공식 Status Anchor는:
 *
 * CAREER_MONTHLY_CONTEXT
 *
 * 하나뿐이다.
 *
 * =================================================
 * Upstream 계산 보존
 * =================================================
 *
 * CareerMonthlyContextRule은
 * MonthlyLuckContextAnalysis.signalsOf(
 *     DIRECT_OFFICER
 * )
 *
 * 및:
 *
 * MonthlyLuckContextAnalysis.signalsOf(
 *     SEVEN_KILLINGS
 * )
 *
 * 를 이용하여
 * 현재 월의 관살 관련 Context를
 * 이미 계산한다.
 *
 * =================================================
 * 중요한 시간층 경계
 * =================================================
 *
 * CareerMonthlyContextRule은:
 *
 * MonthlyLuckContextAnalysis.allSignals()
 *
 * 를 사용하지 않는다.
 *
 * 월운 자체에서 확정 가능한:
 *
 * monthlySignals
 *
 * 만 사용한다.
 *
 * 따라서 Decade Context Signal을
 * 월운 LifeDomain Rule에서
 * 중복 집계하지 않는다.
 *
 * =================================================
 * 실제 Java 계산된 월운 관계
 * =================================================
 *
 * 15-7 MonthlyLuckContextAnalysis 안에는
 * upstream Java가 실제 계산한:
 *
 * - 월운 십성
 * - 원국 × 월운 관계
 * - 세운 × 월운 관계
 * - 가능한 경우 대운 × 월운 관계
 * - 용신 Context
 * - 격국 Context
 * - 궁위 Context
 * - 합화 / 기능 변화
 * - 다층 관계
 *
 * 등이 Monthly Signal에 반영될 수 있다.
 *
 * 이러한 실제 upstream 계산은
 * 유효한 deterministic 계산이다.
 *
 * 이 Category Rule에서는
 * 그것을 다시 계산하지 않는다.
 *
 * =================================================
 * 세운과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * 를 조회하지 않는다.
 *
 * 따라서 다음 Matrix를 사용하지 않는다.
 *
 * ANNUAL SUPPORTIVE
 * +
 * MONTHLY PRESSURE
 * =
 * MIXED
 *
 * -------------------------------------------------
 *
 * ANNUAL PRESSURE
 * +
 * MONTHLY SUPPORTIVE
 * =
 * MIXED
 *
 * -------------------------------------------------
 *
 * MONTHLY CONTEXTUAL
 * +
 * ANNUAL PRESSURE
 * =
 * PRESSURE
 *
 * -------------------------------------------------
 *
 * MONTHLY CONTEXTUAL
 * +
 * ANNUAL SUPPORTIVE
 * =
 * SUPPORTIVE
 *
 * =================================================
 * 이유
 * =================================================
 *
 * 세운과 월운에 각각 Status가 존재한다는
 * 사실만으로 Category 단계에서:
 *
 * - 같은 방향
 * - 반대 방향
 * - 강화
 * - 약화
 * - 상쇄
 * - 보완
 * - 전환
 *
 * 을 새로 계산하지 않는다.
 *
 * 세운 × 월운 관계가 실제로 계산되어 있다면
 * 그것은 upstream MonthlyLuckContextAnalysis의
 * 실제 Signal 계산 안에서만 인정한다.
 *
 * =================================================
 * Monthly Source Family Status
 * =================================================
 *
 * 동일한:
 *
 * CAREER_MONTHLY_CONTEXT
 *
 * Source Family 내부에서:
 *
 * MIXED 존재
 * -> MIXED
 *
 * SUPPORTIVE + PRESSURE 존재
 * -> MIXED
 *
 * PRESSURE만 존재
 * -> PRESSURE
 *
 * SUPPORTIVE만 존재
 * -> SUPPORTIVE
 *
 * 그 외
 * -> CONTEXTUAL
 *
 * =================================================
 * 중요한 경계
 * =================================================
 *
 * 위 MIXED는:
 *
 * 세운 × 월운 합성 결과가 아니다.
 *
 * 동일한 CAREER_MONTHLY_CONTEXT
 * Source Family 안에서
 * upstream Java가 이미 확정한 공식 방향들이
 * 실제로 함께 존재하는 경우만
 * Category 수준에서 정규화한 것이다.
 *
 * =================================================
 * 대운과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * CAREER_DECADE_CONTEXT
 *
 * 를 조회하지 않는다.
 *
 * 대운 Status를 월운 Status에
 * 추가하지 않는다.
 *
 * =================================================
 * 원국과의 분리
 * =================================================
 *
 * 다음 Source도 사용하지 않는다.
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * CAREER_OFFICER_STRUCTURE
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 원국 CAREER 구조와
 * 월운 CAREER Context를
 * Category 단계에서 합성하지 않는다.
 *
 * =================================================
 * Functional과의 분리
 * =================================================
 *
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * 계산이 존재한다면
 * CAREER_MONTHLY_CONTEXT와
 * 이 Rule에서 합치지 않는다.
 *
 * Functional 계산은 별도 확인 후
 * 독립 deterministic 결과로 처리한다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음 의미를 새로 생성하지 않는다.
 *
 * - 연간 직업 배경 위에서 월운이 작동한다
 * - 세운 때문에 이번 달 책임이 커진다
 * - 월운이 세운의 부담을 완화한다
 * - 세운과 월운이 같은 방향이라 강해진다
 * - 책임이 반복될수록 부담이 커진다
 * - 책임을 맡은 뒤 상태가 달라진다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 작동한다
 * - 먼저 A이고 이후 B가 된다
 *
 * =================================================
 * 실제 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 취업
 * != 승진
 * != 합격
 *
 * PRESSURE
 * != 퇴사
 * != 해고
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
 * 특정 직업 추론 금지
 * =================================================
 *
 * 정관 월운
 * != 공무원
 *
 * 칠살 월운
 * != 군인
 * != 경찰
 * != 법조인
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각각 독립적으로 보존한다.
 *
 * 특정 Segment 하나를
 * 대표 Timeline으로 임의 선택하지 않는다.
 *
 * =================================================
 * monthIndex
 * =================================================
 *
 * 현재 Timeline이 계산한:
 *
 * monthIndex
 *
 * 를 그대로 보존한다.
 *
 * monthIndex == 0도
 * 유효한 Timeline 값이다.
 *
 * 0을:
 *
 * - 값 없음
 * - 오류
 * - 미확정
 *
 * 으로 해석하지 않는다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * CAREER_MONTHLY_CONTEXT Match 개수를:
 *
 * - 직업운 강도
 * - 책임 크기
 * - 사회적 영향력
 * - 승진 가능성
 * - 직업 변화 가능성
 *
 * 의 점수로 사용하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - CAREER_ANNUAL_CONTEXT 조회
 * - CAREER_DECADE_CONTEXT 조회
 * - 원국 CAREER Source 조회
 * - 세운 × 월운 Status 합성
 * - 대운 × 월운 Status 합성
 * - 원국 × 월운 Status 합성
 * - 월운 십성 재계산
 * - 원국 × 월운 관계 재계산
 * - 세운 × 월운 관계 재계산
 * - 대운 × 월운 관계 재계산
 * - 용신 재계산
 * - 격국 재계산
 * - 합충형파해 재계산
 * - 합화 재계산
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
public class CareerCurrentMonthlyActivationRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_CURRENT_MONTHLY_ACTIVATION";

    private static final String
            MONTHLY_CONTEXT_RULE_CODE =
            "CAREER_MONTHLY_CONTEXT";


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
                "CareerCurrentMonthlyActivationRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerCurrentMonthlyActivationRule은 CAREER View만 평가할 수 있습니다."
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
         * 가능한 Segment별 독립 처리
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segment,
                            "CAREER Monthly Context Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * Monthly 공식 Status Anchor
             * =================================================
             */

            List<LifeDomainRuleMatch> monthlyMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            MONTHLY_CONTEXT_RULE_CODE
                    );

            /*
             * 현재 Segment에 월운 CAREER Context가 없으면
             * 세운이나 대운을 대신 사용하지 않는다.
             */

            if (
                    monthlyMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * Monthly Status
             * =================================================
             */

            ConsultationCategoryDeterministicStatus monthlyStatus =
                    resolveMonthlyStatus(
                            monthlyMatches
                    );

            /*
             * =================================================
             * Reasons
             * =================================================
             *
             * 최종 사용자 Story가 아니라
             * deterministic provenance 기록이다.
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "현재 월 CAREER Status는 CAREER_MONTHLY_CONTEXT Source Family만 공식 Status Anchor로 사용했습니다."
            );

            appendMonthlyStatusReason(
                    reasons,
                    monthlyMatches,
                    monthlyStatus
            );

            reasons.add(
                    "CAREER_ANNUAL_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았으며 세운 CAREER Context는 CareerCurrentStructuralFlowRule이 독립적으로 담당합니다."
            );

            reasons.add(
                    "CAREER_DECADE_CONTEXT를 이 Rule에 사용하지 않았으며 대운 CAREER Context는 CareerDecadeContextDeterministicRule이 독립적으로 담당합니다."
            );

            reasons.add(
                    "원국의 CAREER_PRIMARY_OFFICER_STRUCTURE, CAREER_OFFICER_STRUCTURE, CAREER_CROSS_VALIDATED_PATTERN을 현재 Monthly Status와 합성하지 않았습니다."
            );

            reasons.add(
                    "CareerMonthlyContextRule이 MonthlyLuckContextAnalysis의 monthlySignals를 사용해 이미 계산한 관살 관련 월운 Context를 그대로 Category 수준에 보존하며 upstream 관계를 다시 계산하지 않았습니다."
            );

            /*
             * =================================================
             * Timeline uncertainty
             * =================================================
             */

            if (
                    normalizedSegment
                            .timelineUncertaintyExists()
            ) {

                reasons.add(
                        "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 CAREER Monthly Context 경로로 확정하지 않습니다."
                );
            }

            if (
                    !normalizedSegment
                            .fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 존재하면 각각 별도 결과로 보존합니다."
                );
            }

            /*
             * =================================================
             * 계산 경계
             * =================================================
             */

            reasons.add(
                    "세운·대운·원국 Status와 현재 Monthly Status를 결합하여 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 만들지 않았습니다."
            );

            reasons.add(
                    "Category 단계에서 세운과 월운 사이의 새로운 강화·약화·상쇄·충돌·보완·전환 관계를 생성하지 않았습니다."
            );

            reasons.add(
                    "CAREER_MONTHLY_CONTEXT Match의 반복 개수를 직업운 강도·책임 크기·사회적 영향력·승진 가능성의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
            );

            reasons.add(
                    "monthIndex는 기존 Timeline 계산값을 그대로 보존하며 0도 유효한 값으로 취급합니다."
            );

            reasons.add(
                    "이 결과는 현재 월의 CAREER deterministic Context만 보존하며 취업·승진·합격·이직·퇴사·해고·직장 갈등 같은 실제 직업 사건을 생성하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             *
             * sourceMatches에는
             * CAREER_MONTHLY_CONTEXT만 들어간다.
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.CAREER,

                            ConsultationCategoryDeterministicLayer
                                    .CURRENT_TIMELINE,

                            RULE_CODE,

                            monthlyStatus,

                            normalizedSegment
                                    .sajuYear(),

                            normalizedSegment
                                    .monthIndex(),

                            List.of(),

                            summaryOf(
                                    monthlyStatus
                            ),

                            List.copyOf(
                                    monthlyMatches
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
                        "CAREER Monthly Context sourceRuleCode가 필요합니다."
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

            /*
             * =================================================
             * CAREER Domain만
             * =================================================
             */

            if (
                    normalized.domain()
                            != LifeDomain.CAREER
            ) {

                continue;
            }

            /*
             * =================================================
             * 공식 Rule Code만
             * =================================================
             *
             * summary / reasons 문자열은
             * Source 판별 조건으로 사용하지 않는다.
             */

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
     * Monthly Source Family Status
     * =================================================
     *
     * 다른 시간층 또는
     * 다른 Source Family를 사용하지 않는다.
     *
     * 오직:
     *
     * CAREER_MONTHLY_CONTEXT
     *
     * 내부의 공식 LifeDomainSignalStatus만
     * Category Status로 정규화한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveMonthlyStatus(
            List<LifeDomainRuleMatch> monthlyMatches
    ) {

        Objects.requireNonNull(
                monthlyMatches,
                "CAREER Monthly Match 목록이 필요합니다."
        );

        if (
                monthlyMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "CAREER Monthly Status 계산에는 최소 하나의 Monthly Match가 필요합니다."
            );
        }

        boolean supportiveExists =
                hasStatus(
                        monthlyMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        monthlyMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        monthlyMatches,
                        LifeDomainSignalStatus.MIXED
                );

        /*
         * =================================================
         * upstream MIXED 보존
         * =================================================
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 동일 Monthly Source Family 내부에
         * 양방향 상태가 실제 함께 존재
         * =================================================
         */

        if (
                supportiveExists
                        && pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        if (
                pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        if (
                supportiveExists
        ) {

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

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
                "CAREER Monthly Match 목록이 필요합니다."
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
     * Monthly Status Provenance
     * =================================================
     */

    private void appendMonthlyStatusReason(

            List<String> reasons,

            List<LifeDomainRuleMatch> monthlyMatches,

            ConsultationCategoryDeterministicStatus monthlyStatus

    ) {

        Objects.requireNonNull(
                reasons,
                "CAREER reasons가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyMatches,
                "CAREER Monthly Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                monthlyStatus,
                "CAREER Monthly Status가 필요합니다."
        );

        boolean supportiveExists =
                hasStatus(
                        monthlyMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        monthlyMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        monthlyMatches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            reasons.add(
                    "CAREER_MONTHLY_CONTEXT Source Family에 upstream MIXED 상태가 존재하므로 현재 월 CAREER Status를 MIXED로 보존합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "CAREER_MONTHLY_CONTEXT Source Family 내부에 SUPPORTIVE와 PRESSURE가 모두 실제 존재하므로 어느 한 방향을 삭제하지 않고 MIXED로 보존합니다."
            );

            return;
        }

        if (
                monthlyStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "CAREER_MONTHLY_CONTEXT Source Family에 PRESSURE 상태가 존재하고 SUPPORTIVE 또는 MIXED 상태가 없어 현재 월 CAREER Status를 PRESSURE로 확정합니다."
            );

            return;
        }

        if (
                monthlyStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "CAREER_MONTHLY_CONTEXT Source Family에 SUPPORTIVE 상태가 존재하고 PRESSURE 또는 MIXED 상태가 없어 현재 월 CAREER Status를 SUPPORTIVE로 확정합니다."
            );

            return;
        }

        reasons.add(
                "CAREER_MONTHLY_CONTEXT Source Family는 존재하지만 SUPPORTIVE·PRESSURE·MIXED 방향이 확정되지 않아 현재 월 CAREER Status를 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * 세운·대운·원국과 연결하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus monthlyStatus
    ) {

        Objects.requireNonNull(
                monthlyStatus,
                "CAREER Monthly Status가 필요합니다."
        );

        return switch (
                monthlyStatus
                ) {

            case SUPPORTIVE ->
                    "현재 월의 CAREER Context 계산 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "현재 월의 CAREER Context 계산 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "현재 월의 CAREER Context 계산 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "현재 월의 CAREER Context 계산 결과가 CONTEXTUAL 상태로 보존됩니다.";
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