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
 * CAREER 현재 명리연도 Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 현재 Timeline Segment에 실제 존재하는:
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * 의 공식 상태만
 * ConsultationCategory.CAREER의
 * 현재 명리연도 deterministic 결과로 보존한다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 기존에는:
 *
 * CAREER_DECADE_CONTEXT
 * +
 * CAREER_ANNUAL_CONTEXT
 *
 * 를 같은 Match 안에서 결합하고
 * 두 시간층의 Status Matrix를 계산했다.
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
 * 별도의 CAREER Decade deterministic Rule
 *
 * -------------------------------------------------
 *
 * 세운:
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 월운:
 *
 * CAREER_MONTHLY_CONTEXT
 *
 * ->
 *
 * 별도의 CAREER Monthly deterministic Rule
 *
 * =================================================
 * 공식 Status Anchor
 * =================================================
 *
 * 이 Rule의 공식 Status Anchor는:
 *
 * CAREER_ANNUAL_CONTEXT
 *
 * 하나뿐이다.
 *
 * =================================================
 * 삭제되는 기존 Matrix
 * =================================================
 *
 * 다음 계산을 하지 않는다.
 *
 * DECADE SUPPORTIVE
 * +
 * ANNUAL PRESSURE
 * =
 * MIXED
 *
 * -------------------------------------------------
 *
 * DECADE PRESSURE
 * +
 * ANNUAL SUPPORTIVE
 * =
 * MIXED
 *
 * -------------------------------------------------
 *
 * ANNUAL CONTEXTUAL
 * +
 * DECADE PRESSURE
 * =
 * PRESSURE
 *
 * -------------------------------------------------
 *
 * ANNUAL CONTEXTUAL
 * +
 * DECADE SUPPORTIVE
 * =
 * SUPPORTIVE
 *
 * =================================================
 * 이유
 * =================================================
 *
 * 대운과 세운은 실제 계산된
 * 서로 다른 시간층이다.
 *
 * 두 시간층에 각각:
 *
 * - SUPPORTIVE
 * - PRESSURE
 * - MIXED
 * - CONTEXTUAL
 *
 * 상태가 존재한다는 사실만으로
 * 서로의 방향을:
 *
 * - 강화
 * - 약화
 * - 상쇄
 * - 전환
 * - 보완
 *
 * 한다고 계산할 수 없다.
 *
 * 대운 × 세운 상호작용이 필요하다면
 * 두 시간층 사이의 실제 관계 자체를
 * 별도의 Java deterministic Analysis로
 * 계산해야 한다.
 *
 * =================================================
 * Annual Source Family Status
 * =================================================
 *
 * 동일한:
 *
 * CAREER_ANNUAL_CONTEXT
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
 * 대운과 세운을 합성한 결과가 아니다.
 *
 * 같은 CAREER_ANNUAL_CONTEXT Source Family 안에서
 * upstream Java가 이미 확정한 공식 방향들이
 * 실제로 함께 존재하는 경우만 정규화한 것이다.
 *
 * =================================================
 * 대운 사용 금지
 * =================================================
 *
 * 이 Rule에서는:
 *
 * CAREER_DECADE_CONTEXT
 *
 * 를 조회하지 않는다.
 *
 * sourceMatches에도 넣지 않는다.
 *
 * 세운의 보조 근거로도 사용하지 않는다.
 *
 * =================================================
 * 월운 사용 금지
 * =================================================
 *
 * 이 Rule에서는:
 *
 * CAREER_MONTHLY_CONTEXT
 *
 * 를 사용하지 않는다.
 *
 * 월 단위 CAREER Context는
 * 별도의 deterministic Rule이 담당한다.
 *
 * =================================================
 * Functional 결과와의 분리
 * =================================================
 *
 * CAREER_ANNUAL_FUNCTIONAL
 * CAREER_MONTHLY_FUNCTIONAL
 *
 * 같은 Functional 계산이 존재하더라도
 * 이 Rule에서는 일반 Annual Context와
 * 합성하지 않는다.
 *
 * 해당 계산은 존재 여부와 upstream 구조를
 * 별도로 확인하여 독립 Rule로 처리한다.
 *
 * =================================================
 * 원국 계산과의 분리
 * =================================================
 *
 * 다음 원국 Source를 사용하지 않는다.
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * CAREER_OFFICER_STRUCTURE
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 원국 구조와 세운 Context를
 * 하나의 Category Status로 합성하지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음 의미를 생성하지 않는다.
 *
 * - 대운의 역할 배경 위에서 세운이 작동한다
 * - 대운이 세운의 책임을 강화한다
 * - 세운이 대운의 부담을 완화한다
 * - 두 시간층이 맞물린다
 * - 역할이 반복될수록 부담이 커진다
 * - 책임을 맡은 뒤 상태가 달라진다
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 작동한다
 * - 먼저 A이고 이후 B가 된다
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 취업
 * != 승진
 * != 합격
 * != 직업 성공
 *
 * PRESSURE
 * != 퇴사
 * != 해고
 * != 직장 갈등
 * != 사업 실패
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
 * 정관 Context
 * != 공무원
 *
 * 칠살 Context
 * != 군인
 * != 경찰
 * != 법조인
 *
 * Annual CAREER Context를
 * 실제 직종으로 직접 변환하지 않는다.
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
 * 이 Match는 CURRENT_TIMELINE Layer이므로
 * 현재 Segment의:
 *
 * - sajuYear
 * - monthIndex
 *
 * 좌표를 그대로 보존한다.
 *
 * monthIndex 존재가
 * Annual Context를 Monthly 계산으로
 * 바꾸는 것은 아니다.
 *
 * monthIndex == 0도
 * 기존 Timeline 계산의 유효 값으로 취급한다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * CAREER_ANNUAL_CONTEXT Match 개수를:
 *
 * - 직업운 강도
 * - 사회적 성공 가능성
 * - 책임의 크기
 * - 승진 가능성
 * - 직업 능력
 *
 * 의 점수로 사용하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - CAREER_DECADE_CONTEXT 조회
 * - CAREER_MONTHLY_CONTEXT 조회
 * - 원국 CAREER 구조 조회
 * - 대운 × 세운 Status 합성
 * - 세운 × 월운 Status 합성
 * - 원국 × 세운 Status 합성
 * - 관살 재계산
 * - 십성 재계산
 * - 격국 재계산
 * - 신강신약 재계산
 * - 용신 재계산
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
public class CareerCurrentStructuralFlowRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_CURRENT_STRUCTURAL_FLOW";

    private static final String
            ANNUAL_CONTEXT_RULE_CODE =
            "CAREER_ANNUAL_CONTEXT";


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
                "CareerCurrentStructuralFlowRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerCurrentStructuralFlowRule은 CAREER View만 평가할 수 있습니다."
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
                            "CAREER Annual Context Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * Annual 공식 Status Anchor
             * =================================================
             */

            List<LifeDomainRuleMatch> annualMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            ANNUAL_CONTEXT_RULE_CODE
                    );

            /*
             * 해당 명리연도의 CAREER Context가 없으면
             * 다른 시간층을 대신 사용해서
             * Annual Category Match를 만들지 않는다.
             */

            if (
                    annualMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * Annual Status
             * =================================================
             */

            ConsultationCategoryDeterministicStatus annualStatus =
                    resolveAnnualStatus(
                            annualMatches
                    );

            /*
             * =================================================
             * Reasons
             * =================================================
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "현재 명리연도 CAREER Status는 CAREER_ANNUAL_CONTEXT Source Family만 공식 Status Anchor로 사용했습니다."
            );

            appendAnnualStatusReason(
                    reasons,
                    annualMatches,
                    annualStatus
            );

            reasons.add(
                    "CAREER_DECADE_CONTEXT는 이 Rule에서 조회하거나 sourceMatches에 포함하지 않았으며 대운 CAREER Context는 별도의 deterministic 결과로 분리합니다."
            );

            reasons.add(
                    "CAREER_MONTHLY_CONTEXT는 이 Rule에 사용하지 않았으며 월운 CAREER Context는 별도의 CURRENT_TIMELINE Rule이 담당합니다."
            );

            reasons.add(
                    "원국의 CAREER_PRIMARY_OFFICER_STRUCTURE, CAREER_OFFICER_STRUCTURE, CAREER_CROSS_VALIDATED_PATTERN을 현재 Annual Status와 합성하지 않았습니다."
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
                        "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 CAREER Annual Context 경로로 확정하지 않습니다."
                );
            }

            if (
                    !normalizedSegment
                            .fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 있으면 각각 별도 결과로 보존합니다."
                );
            }

            /*
             * =================================================
             * 계산 경계
             * =================================================
             */

            reasons.add(
                    "대운·월운·원국 Status와 현재 Annual Status를 결합하여 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 만들지 않았습니다."
            );

            reasons.add(
                    "서로 다른 시간층 또는 Source Family 사이의 강화·약화·상쇄·충돌·보완·전환 관계를 새로 생성하지 않았습니다."
            );

            reasons.add(
                    "CAREER_ANNUAL_CONTEXT Match의 반복 개수를 직업운 강도·사회적 지위·책임 크기·승진 가능성의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
            );

            reasons.add(
                    "monthIndex는 현재 Timeline Segment의 좌표 보존용이며 Annual Context를 Monthly 결과로 재해석하지 않습니다."
            );

            reasons.add(
                    "이 결과는 현재 명리연도의 CAREER deterministic Context만 보존하며 취업·승진·합격·이직·퇴사·해고·사업 결과 같은 실제 직업 사건을 생성하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             *
             * sourceMatches에는
             * CAREER_ANNUAL_CONTEXT만 들어간다.
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.CAREER,

                            ConsultationCategoryDeterministicLayer
                                    .CURRENT_TIMELINE,

                            RULE_CODE,

                            annualStatus,

                            normalizedSegment
                                    .sajuYear(),

                            normalizedSegment
                                    .monthIndex(),

                            List.of(),

                            summaryOf(
                                    annualStatus
                            ),

                            List.copyOf(
                                    annualMatches
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
                        "CAREER Annual Context sourceRuleCode가 필요합니다."
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
     * Annual Source Family Status
     * =================================================
     *
     * 다른 Source Family 또는
     * 다른 시간층을 사용하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveAnnualStatus(
            List<LifeDomainRuleMatch> annualMatches
    ) {

        Objects.requireNonNull(
                annualMatches,
                "CAREER Annual Match 목록이 필요합니다."
        );

        if (
                annualMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "CAREER Annual Status 계산에는 최소 하나의 Annual Match가 필요합니다."
            );
        }

        boolean supportiveExists =
                hasStatus(
                        annualMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        annualMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        annualMatches,
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
         * 동일 Annual Source Family 내부에
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
                "CAREER Annual Match 목록이 필요합니다."
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
     * Annual Status Provenance
     * =================================================
     */

    private void appendAnnualStatusReason(

            List<String> reasons,

            List<LifeDomainRuleMatch> annualMatches,

            ConsultationCategoryDeterministicStatus annualStatus

    ) {

        Objects.requireNonNull(
                reasons,
                "CAREER reasons가 필요합니다."
        );

        Objects.requireNonNull(
                annualMatches,
                "CAREER Annual Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                annualStatus,
                "CAREER Annual Status가 필요합니다."
        );

        boolean supportiveExists =
                hasStatus(
                        annualMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        annualMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        annualMatches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            reasons.add(
                    "CAREER_ANNUAL_CONTEXT Source Family에 upstream MIXED 상태가 존재하므로 현재 명리연도 CAREER Status를 MIXED로 보존합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "CAREER_ANNUAL_CONTEXT Source Family 내부에 SUPPORTIVE와 PRESSURE가 모두 실제 존재하므로 어느 한 방향을 삭제하지 않고 MIXED로 보존합니다."
            );

            return;
        }

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "CAREER_ANNUAL_CONTEXT Source Family에 PRESSURE 상태가 존재하고 SUPPORTIVE 또는 MIXED 상태가 없어 현재 명리연도 CAREER Status를 PRESSURE로 확정합니다."
            );

            return;
        }

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "CAREER_ANNUAL_CONTEXT Source Family에 SUPPORTIVE 상태가 존재하고 PRESSURE 또는 MIXED 상태가 없어 현재 명리연도 CAREER Status를 SUPPORTIVE로 확정합니다."
            );

            return;
        }

        reasons.add(
                "CAREER_ANNUAL_CONTEXT Source Family는 존재하지만 SUPPORTIVE·PRESSURE·MIXED 방향이 확정되지 않아 현재 명리연도 CAREER Status를 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     * 다른 시간층과 연결하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus annualStatus
    ) {

        Objects.requireNonNull(
                annualStatus,
                "CAREER Annual Status가 필요합니다."
        );

        return switch (
                annualStatus
                ) {

            case SUPPORTIVE ->
                    "현재 명리연도의 CAREER Context 계산 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "현재 명리연도의 CAREER Context 계산 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "현재 명리연도의 CAREER Context 계산 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "현재 명리연도의 CAREER Context 계산 결과가 CONTEXTUAL 상태로 보존됩니다.";
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