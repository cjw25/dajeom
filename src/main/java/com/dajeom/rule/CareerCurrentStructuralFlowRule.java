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
 * CAREER 카테고리의
 * 현재 Timeline 대운 배경 × 세운 작동 Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 원국 CAREER 구조를 다시 계산하지 않는다.
 *
 * 현재 Timeline Segment 안에서
 * 기존 18차 LifeDomain Rule이 계산한:
 *
 * - CAREER_DECADE_CONTEXT
 * - CAREER_ANNUAL_CONTEXT
 *
 * 를 서로 다른 시간층으로 분리하여:
 *
 * "현재 대운의 역할·책임·규범 배경 위에서
 * 해당 세운의 직업·사회 기능이
 * 어떤 방향으로 작동하는가"
 *
 * 를 CAREER Category 관점에서 판단한다.
 *
 * =================================================
 * 시간층 의미
 * =================================================
 *
 * DECADE
 *
 * ->
 *
 * 현재 장기간 유지되는:
 *
 * - 역할
 * - 책임
 * - 규범
 * - 통제
 * - 사회적 요구
 * - 관살 작동
 *
 * 의 구조적 배경.
 *
 * -------------------------------------------------
 *
 * ANNUAL
 *
 * ->
 *
 * 해당 명리연도에서 추가되는:
 *
 * - 역할 요구
 * - 책임 압력
 * - 규범 Context
 * - 직업·사회 기능의 지지 / 약화 / 충돌
 *
 * 의 연간 작동 방향.
 *
 * =================================================
 * Source 1
 * CAREER_DECADE_CONTEXT
 * =================================================
 *
 * 기존 CareerDecadeContextRule은
 * DecadeLuckPeriodContextAnalysis에서
 *
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * 관련 Signal을 사용한다.
 *
 * 또한 upstream 13차에서 이미:
 *
 * - 대운 십성
 * - 원국 × 대운 관계
 * - 용신 / 희신
 * - 격국
 * - 궁위
 * - 관계
 * - 합화 / 기능변화
 *
 * 를 계산한 결과를 사용한다.
 *
 * 이번 Rule에서는
 * 이를 다시 계산하지 않는다.
 *
 * =================================================
 * Source 2
 * CAREER_ANNUAL_CONTEXT
 * =================================================
 *
 * 기존 CareerAnnualContextRule 역시
 * AnnualLuckContextAnalysis에서
 *
 * DIRECT_OFFICER
 * SEVEN_KILLINGS
 *
 * 관련 Signal을 사용한다.
 *
 * upstream 14차에서 이미:
 *
 * - 세운 십성
 * - 원국 × 세운
 * - 격국
 * - 용신
 * - 궁위
 * - 관계
 * - 합화
 * - 가능한 대운 Segment Context
 *
 * 를 계산한 결과를 사용한다.
 *
 * =================================================
 * 두 Source 모두 필수
 * =================================================
 *
 * 이번 Rule은:
 *
 * CAREER_DECADE_CONTEXT
 * +
 * CAREER_ANNUAL_CONTEXT
 *
 * 가 같은 현재 Segment에
 * 모두 존재할 때만 Match를 만든다.
 *
 * 대운이 없는 Segment에서
 * 장기 직업 배경을 임의 생성하지 않는다.
 *
 * =================================================
 * Status 결합
 * =================================================
 *
 * decadeStatus:
 *
 * 현재 대운에서
 * 역할·책임·규범 기능이 놓인
 * 장기 구조적 방향.
 *
 * -------------------------------------------------
 *
 * annualStatus:
 *
 * 해당 명리연도에 추가되는
 * 직업·사회 기능의 작동 방향.
 *
 * -------------------------------------------------
 *
 * 어느 한 시간층이 MIXED
 * -> MIXED
 *
 * DECADE SUPPORTIVE
 * +
 * ANNUAL PRESSURE
 * -> MIXED
 *
 * DECADE PRESSURE
 * +
 * ANNUAL SUPPORTIVE
 * -> MIXED
 *
 * ANNUAL PRESSURE
 * +
 * DECADE PRESSURE / CONTEXTUAL
 * -> PRESSURE
 *
 * ANNUAL SUPPORTIVE
 * +
 * DECADE SUPPORTIVE / CONTEXTUAL
 * -> SUPPORTIVE
 *
 * ANNUAL CONTEXTUAL
 * +
 * DECADE PRESSURE
 * -> PRESSURE
 *
 * ANNUAL CONTEXTUAL
 * +
 * DECADE SUPPORTIVE
 * -> SUPPORTIVE
 *
 * 둘 다 CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 * 중요한 제한
 * =================================================
 *
 * SUPPORTIVE
 *
 * != 취업
 * != 승진
 * != 합격
 * != 직업 성공
 *
 * -------------------------------------------------
 *
 * PRESSURE
 *
 * != 퇴사
 * != 해고
 * != 직장 갈등 발생
 * != 사업 실패
 *
 * -------------------------------------------------
 *
 * MIXED
 *
 * != 이직
 * != 직업 변동 발생
 *
 * =================================================
 * 직종 추론 금지
 * =================================================
 *
 * 정관
 * -> 공무원
 *
 * 칠살
 * -> 군인 / 경찰
 *
 * 같은 특정 직업 대응을 하지 않는다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * CAREER Match 개수가 많다고:
 *
 * - 직업운이 강하다
 * - 사회적 성공 가능성이 높다
 * - 책임이 크다
 *
 * 로 판단하지 않는다.
 *
 * 공식 Status의 존재 방향만 사용한다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각각 독립적으로 계산한다.
 *
 * 특정 Segment 하나를
 * 대표 Timeline으로 임의 선택하지 않는다.
 */
@Component
public class CareerCurrentStructuralFlowRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_CURRENT_STRUCTURAL_FLOW";

    private static final String
            DECADE_CONTEXT_RULE_CODE =
            "CAREER_DECADE_CONTEXT";

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
         * 현재 Timeline 필요
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
         * 가능한 Segment별 독립 계산
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            Objects.requireNonNull(
                    segment,
                    "CAREER Current Timeline Segment는 null일 수 없습니다."
            );

            /*
             * =================================================
             * 대운
             * =================================================
             */

            List<LifeDomainRuleMatch>
                    decadeMatches =
                    findSegmentMatches(
                            segment,
                            DECADE_CONTEXT_RULE_CODE
                    );

            /*
             * =================================================
             * 세운
             * =================================================
             */

            List<LifeDomainRuleMatch>
                    annualMatches =
                    findSegmentMatches(
                            segment,
                            ANNUAL_CONTEXT_RULE_CODE
                    );

            /*
             * =================================================
             * 두 시간층 모두 필수
             * =================================================
             */

            if (
                    decadeMatches.isEmpty()
                            || annualMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * 시간층별 Status
             * =================================================
             */

            ConsultationCategoryDeterministicStatus
                    decadeStatus =
                    resolveFamilyStatus(
                            decadeMatches
                    );

            ConsultationCategoryDeterministicStatus
                    annualStatus =
                    resolveFamilyStatus(
                            annualMatches
                    );

            /*
             * =================================================
             * 대운 × 세운
             * =================================================
             */

            ConsultationCategoryDeterministicStatus
                    finalStatus =
                    resolveStructuralFlowStatus(
                            decadeStatus,
                            annualStatus
                    );

            /*
             * =================================================
             * Source Provenance
             * =================================================
             */

            List<LifeDomainRuleMatch> sourceMatches =
                    new ArrayList<>();

            sourceMatches.addAll(
                    decadeMatches
            );

            sourceMatches.addAll(
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
                    "현재 Timeline Segment에서 대운의 직업·사회 Context와 해당 세운의 직업·사회 Context를 서로 다른 시간층으로 확인했습니다."
            );

            reasons.add(
                    "CAREER_DECADE_CONTEXT Source Family의 상태는 "
                            + decadeStatus.name()
                            + "이며, 현재 역할·책임·규범 기능의 장기 구조적 배경으로 사용했습니다."
            );

            reasons.add(
                    "CAREER_ANNUAL_CONTEXT Source Family의 상태는 "
                            + annualStatus.name()
                            + "이며, 해당 명리연도에서 추가되는 역할·책임·사회적 요구의 작동 방향으로 사용했습니다."
            );

            appendCombinationReason(
                    reasons,
                    decadeStatus,
                    annualStatus,
                    finalStatus
            );

            /*
             * =================================================
             * Timeline uncertainty
             * =================================================
             */

            if (
                    segment.timelineUncertaintyExists()
            ) {

                reasons.add(
                        "현재 조회 시점에는 출생시간·대운 경계 또는 Timeline 조건에 따른 불확실성이 존재하므로 이 Segment를 유일한 가능성으로 확정하지 않습니다."
                );
            }

            if (
                    !segment.fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment는 완전 확정된 Timeline이 아니므로 동일 연·월 좌표에 다른 가능한 Segment가 존재할 수 있으며 각 Source 경로를 별도로 보존합니다."
                );
            }

            reasons.add(
                    "대운과 세운의 CAREER Match 개수를 서로 비교하여 어느 시간층의 직업 영향력이 더 강하다고 판단하지 않았습니다."
            );

            reasons.add(
                    "이번 Rule에서는 월운을 사용하지 않으며 월 단위 직업·사회 기능의 활성은 별도의 CURRENT_TIMELINE Rule에서 계산합니다."
            );

            reasons.add(
                    "이 결과는 현재 대운의 장기 역할·책임 배경과 해당 세운의 직업·사회 기능이 어떻게 맞물리는지를 설명하며 취업, 승진, 이직, 퇴사, 조직 갈등 또는 사업 결과를 확정하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.CAREER,

                            ConsultationCategoryDeterministicLayer
                                    .CURRENT_TIMELINE,

                            RULE_CODE,

                            finalStatus,

                            segment.sajuYear(),

                            segment.monthIndex(),

                            List.of(),

                            summaryOf(
                                    finalStatus
                            ),

                            List.copyOf(
                                    sourceMatches
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

        if (
                sourceRuleCode == null
                        || sourceRuleCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "CAREER Timeline Source ruleCode가 필요합니다."
            );
        }

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : segment.matches()
        ) {

            Objects.requireNonNull(
                    match,
                    "CAREER Segment Match는 null일 수 없습니다."
            );

            /*
             * =================================================
             * CAREER Domain
             * =================================================
             */

            if (
                    match.domain()
                            != LifeDomain.CAREER
            ) {

                continue;
            }

            /*
             * =================================================
             * 공식 Source Family
             * =================================================
             */

            if (
                    !sourceRuleCode.equals(
                            match.ruleCode()
                    )
            ) {

                continue;
            }

            result.add(
                    match
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 시간층 Family Status
     * =================================================
     */

    private ConsultationCategoryDeterministicStatus
    resolveFamilyStatus(
            List<LifeDomainRuleMatch> matches
    ) {

        Objects.requireNonNull(
                matches,
                "CAREER Timeline Match 목록이 필요합니다."
        );

        boolean supportiveExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.MIXED
                );

        /*
         * upstream MIXED는 그대로 보존한다.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * 같은 시간층에
         * SUPPORTIVE와 PRESSURE가 함께 있으면
         * 한쪽으로 단순화하지 않는다.
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
     * CAREER 대운 × 세운 Matrix
     * =================================================
     */

    private ConsultationCategoryDeterministicStatus
    resolveStructuralFlowStatus(

            ConsultationCategoryDeterministicStatus decadeStatus,

            ConsultationCategoryDeterministicStatus annualStatus

    ) {

        Objects.requireNonNull(
                decadeStatus,
                "CAREER decadeStatus가 필요합니다."
        );

        Objects.requireNonNull(
                annualStatus,
                "CAREER annualStatus가 필요합니다."
        );

        /*
         * =================================================
         * 어느 한 시간층이 이미 MIXED
         * =================================================
         */

        if (
                decadeStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
                        || annualStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 대운과 세운 방향이 반대
         * =================================================
         */

        if (
                decadeStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
                        && annualStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        if (
                decadeStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
                        && annualStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 세운이 명확한 방향
         * =================================================
         */

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        /*
         * =================================================
         * 세운 CONTEXTUAL
         * =================================================
         *
         * 세운이 대운의 방향을
         * 뒤집는 공식 근거를 제공하지 않은 상태다.
         */

        if (
                decadeStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        if (
                decadeStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
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
                "CAREER Timeline Match 목록이 필요합니다."
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
     * Combination Reason
     * =================================================
     */

    private void appendCombinationReason(

            List<String> reasons,

            ConsultationCategoryDeterministicStatus decadeStatus,

            ConsultationCategoryDeterministicStatus annualStatus,

            ConsultationCategoryDeterministicStatus finalStatus

    ) {

        Objects.requireNonNull(
                reasons,
                "reasons가 필요합니다."
        );

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
        ) {

            reasons.add(
                    "현재 대운의 역할·책임 배경과 해당 세운의 직업·사회 기능이 서로 다른 방향으로 작동하거나 어느 한 시간층에 이미 혼재가 존재하여 MIXED로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "현재 대운 또는 해당 세운에서 역할·책임·규범 기능의 부담 방향이 확인되고 다른 시간층에서 이를 반대로 전환할 공식 지지 근거가 없어 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "현재 대운 또는 해당 세운에서 역할·책임·직업 기능의 지지 방향이 확인되고 다른 시간층에서 이를 반대로 뒤집는 공식 부담 방향이 없어 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        reasons.add(
                "현재 대운과 해당 세운 모두 역할·책임·직업 기능의 Context는 존재하지만 공식 근거만으로 한 방향의 직업·사회 흐름을 확정할 수 없어 CONTEXTUAL로 유지합니다."
        );
    }

    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 최종 사용자용 storyText가 아니다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "CAREER Current Timeline status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운의 역할·책임 배경과 해당 세운의 직업·사회 기능이 비교적 지지되는 방향으로 연결됩니다.";

            case PRESSURE ->
                    "현재 대운의 역할·책임 배경과 해당 세운의 직업·사회 기능에서 구조적인 요구·압박 방향이 확인됩니다.";

            case MIXED ->
                    "현재 대운의 역할·책임 배경과 해당 세운의 직업·사회 기능이 서로 다른 방향으로 겹치거나 복합적인 조건이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운과 해당 세운에서 역할·책임·직업 기능은 관여하지만 두 시간층의 결합을 한 방향으로 단정하지 않습니다.";
        };
    }
}