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
 * PARTNER_LUCK 카테고리의
 * 현재 Timeline 세운 관계 기반 × 월운 친밀관계 활성 Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * PartnerLuckCurrentStructuralFlowRule과
 * 서로 다른 시간 질문을 담당한다.
 *
 * -------------------------------------------------
 *
 * PartnerLuckCurrentStructuralFlowRule
 *
 * ->
 *
 * 대운
 * +
 * 세운
 *
 * 을 결합하여:
 *
 * "장기간 가까운 관계가 놓이는
 * 구조적 배경과 해당 연도의
 * 관계 작동이 어떻게 맞물리는가"
 *
 * 를 판단한다.
 *
 * -------------------------------------------------
 *
 * 이 Rule
 *
 * ->
 *
 * 세운
 * +
 * 월운
 *
 * 을 결합하여:
 *
 * "해당 명리연도의 가까운 관계 배경 안에서
 * 현재 월의 관계 조정 자극이
 * 어떤 방향으로 들어오는가"
 *
 * 를 판단한다.
 *
 * =================================================
 * SOCIAL_RELATIONSHIP 월운과의 차이
 * =================================================
 *
 * SOCIAL_RELATIONSHIP에서는
 * 현재 월의 사회적 상호작용 자체가
 * 중요한 질문이므로
 * monthlyStatus를 직접 활성 방향으로
 * 비교적 적극적으로 보존할 수 있다.
 *
 * -------------------------------------------------
 *
 * PARTNER_LUCK에서는
 * 한 달의 자극 하나만으로:
 *
 * - 장기 관계 기반이 좋다
 * - 장기 관계 기반이 나쁘다
 *
 * 라고 판단하지 않는다.
 *
 * 따라서 이번 Rule에서는:
 *
 * annualStatus
 *
 * 를 해당 연도의 가까운 관계 기반으로 두고,
 *
 * monthlyStatus
 *
 * 를 그 위에 추가되는
 * 단기 조정 / 자극 Context로 사용한다.
 *
 * =================================================
 * Source 1
 * RELATIONSHIP_ANNUAL_CONTEXT
 * =================================================
 *
 * 해당 세운에서 이미 계산된
 * 관계 기능의 연간 Context를 사용한다.
 *
 * 여기서:
 *
 * - 십신
 * - 원국 × 세운
 * - 대운 × 세운
 * - 합
 * - 충
 * - 형
 * - 파
 * - 해
 * - 합화
 * - 용신
 *
 * 을 다시 계산하지 않는다.
 *
 * =================================================
 * Source 2
 * RELATIONSHIP_MONTHLY_CONTEXT
 * =================================================
 *
 * 해당 월운에서 이미 계산된
 * 관계 기능의 단기 Context를 사용한다.
 *
 * 이 Rule에서도:
 *
 * - 월운 십신
 * - 원국 × 월운
 * - 세운 × 월운
 * - 대운 Context
 * - 관계
 * - 합화
 *
 * 등을 다시 계산하지 않는다.
 *
 * =================================================
 * 두 Source 모두 필수
 * =================================================
 *
 * 같은 현재 Segment 안에:
 *
 * RELATIONSHIP_ANNUAL_CONTEXT
 * +
 * RELATIONSHIP_MONTHLY_CONTEXT
 *
 * 가 모두 존재해야 Match를 만든다.
 *
 * 월운 하나만으로
 * PARTNER_LUCK 결과를 생성하지 않는다.
 *
 * =================================================
 * PARTNER_LUCK 결합 원칙
 * =================================================
 *
 * annualStatus:
 *
 * 해당 명리연도에서
 * 가까운 관계가 놓이는
 * 연간 구조적 방향.
 *
 * -------------------------------------------------
 *
 * monthlyStatus:
 *
 * 그 연간 관계 배경 위에서
 * 현재 월에 추가되는
 * 단기 관계 자극 방향.
 *
 * -------------------------------------------------
 *
 * 이번 Rule에서는
 * annualStatus를 중심축으로 사용한다.
 *
 * monthlyStatus는:
 *
 * - 연간 관계 방향을 지지하는가
 * - 연간 관계 방향에 부담을 추가하는가
 * - 반대 방향으로 작동하는가
 * - 단순 Context인가
 *
 * 를 판단하는 조정층이다.
 *
 * =================================================
 * Matrix
 * =================================================
 *
 * 어느 한 Family가 MIXED
 * -> MIXED
 *
 * -------------------------------------------------
 *
 * ANNUAL SUPPORTIVE
 * +
 * MONTHLY PRESSURE
 * -> MIXED
 *
 * 연간 가까운 관계 흐름은 지지되지만
 * 현재 월에는 부담 자극이 들어온다.
 *
 * -------------------------------------------------
 *
 * ANNUAL PRESSURE
 * +
 * MONTHLY SUPPORTIVE
 * -> MIXED
 *
 * 연간 관계 흐름에는 부담이 있지만
 * 현재 월에는 지지 자극이 들어온다.
 *
 * -------------------------------------------------
 *
 * ANNUAL SUPPORTIVE
 * +
 * MONTHLY SUPPORTIVE / CONTEXTUAL
 * -> SUPPORTIVE
 *
 * 현재 월이 연간 지지 방향을
 * 뒤집지 않는다.
 *
 * -------------------------------------------------
 *
 * ANNUAL PRESSURE
 * +
 * MONTHLY PRESSURE / CONTEXTUAL
 * -> PRESSURE
 *
 * 현재 월이 연간 부담 방향을
 * 뒤집지 않는다.
 *
 * -------------------------------------------------
 *
 * ANNUAL CONTEXTUAL
 * +
 * MONTHLY SUPPORTIVE / PRESSURE / CONTEXTUAL
 * -> CONTEXTUAL
 *
 * 한 달의 단기 자극만으로
 * 해당 연도의 가까운 관계 기반 전체를
 * SUPPORTIVE 또는 PRESSURE로 승격하지 않는다.
 *
 * =================================================
 * 중요한 제한
 * =================================================
 *
 * SUPPORTIVE
 *
 * != 연애가 시작된다
 * != 결혼한다
 * != 좋은 사람이 나타난다
 *
 * -------------------------------------------------
 *
 * PRESSURE
 *
 * != 싸운다
 * != 헤어진다
 * != 관계가 끊어진다
 *
 * -------------------------------------------------
 *
 * MIXED
 *
 * != 헤어졌다 재회한다
 * != 관계 변화가 발생한다
 *
 * =================================================
 * 특정 상대 추론 금지
 * =================================================
 *
 * 이 Rule은:
 *
 * - 현재 연인
 * - 미래 배우자
 * - 특정 이성
 *
 * 의 행동이나 감정을 추론하지 않는다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Annual Match 또는 Monthly Match의 개수를:
 *
 * - 인연 수
 * - 배우자운 강도
 * - 연애 가능성
 * - 관계 지속 가능성
 *
 * 점수로 사용하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각각 독립적으로 계산한다.
 *
 * 특정 Segment 하나를
 * 임의의 대표 Timeline으로 선택하지 않는다.
 *
 * monthIndex == 0 역시
 * 정상적인 Timeline 값으로 보존한다.
 */
@Component
public class PartnerLuckCurrentMonthlyActivationRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_CURRENT_MONTHLY_ACTIVATION";

    private static final String
            ANNUAL_CONTEXT_RULE_CODE =
            "RELATIONSHIP_ANNUAL_CONTEXT";

    private static final String
            MONTHLY_CONTEXT_RULE_CODE =
            "RELATIONSHIP_MONTHLY_CONTEXT";

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.PARTNER_LUCK;
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
                "PartnerLuckCurrentMonthlyActivationRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.PARTNER_LUCK
        ) {

            throw new IllegalArgumentException(
                    "PartnerLuckCurrentMonthlyActivationRule은 PARTNER_LUCK View만 평가할 수 있습니다."
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
         * 가능한 Segment별 독립 계산
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            Objects.requireNonNull(
                    segment,
                    "PARTNER_LUCK Current Timeline Segment는 null일 수 없습니다."
            );

            /*
             * =================================================
             * Source 1
             * 세운 관계 Context
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
             * Source 2
             * 월운 관계 Context
             * =================================================
             */

            List<LifeDomainRuleMatch>
                    monthlyMatches =
                    findSegmentMatches(
                            segment,
                            MONTHLY_CONTEXT_RULE_CODE
                    );

            /*
             * =================================================
             * 두 시간층 모두 필수
             * =================================================
             */

            if (
                    annualMatches.isEmpty()
                            || monthlyMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * Family Status
             * =================================================
             */

            ConsultationCategoryDeterministicStatus
                    annualStatus =
                    resolveFamilyStatus(
                            annualMatches
                    );

            ConsultationCategoryDeterministicStatus
                    monthlyStatus =
                    resolveFamilyStatus(
                            monthlyMatches
                    );

            /*
             * =================================================
             * PARTNER_LUCK 전용 세운 × 월운 결합
             * =================================================
             */

            ConsultationCategoryDeterministicStatus
                    finalStatus =
                    resolvePartnerMonthlyStatus(
                            annualStatus,
                            monthlyStatus
                    );

            /*
             * =================================================
             * Source Provenance
             * =================================================
             */

            List<LifeDomainRuleMatch> sourceMatches =
                    new ArrayList<>();

            sourceMatches.addAll(
                    annualMatches
            );

            sourceMatches.addAll(
                    monthlyMatches
            );

            /*
             * =================================================
             * Reasons
             * =================================================
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "현재 Timeline Segment에서 해당 세운의 관계 Context와 현재 월운의 관계 Context를 서로 다른 시간층으로 확인했습니다."
            );

            reasons.add(
                    "RELATIONSHIP_ANNUAL_CONTEXT Source Family의 상태는 "
                            + annualStatus.name()
                            + "이며, PARTNER_LUCK에서는 해당 명리연도에 가까운 관계가 놓이는 구조적 배경의 중심 Evidence로 사용했습니다."
            );

            reasons.add(
                    "RELATIONSHIP_MONTHLY_CONTEXT Source Family의 상태는 "
                            + monthlyStatus.name()
                            + "이며, 해당 연간 관계 배경 위에서 현재 월에 추가되는 단기 관계 조정 자극으로 사용했습니다."
            );

            appendCombinationReason(
                    reasons,
                    annualStatus,
                    monthlyStatus,
                    finalStatus
            );

            reasons.add(
                    "SOCIAL_RELATIONSHIP 월운처럼 monthlyStatus 하나만으로 결론을 승격하지 않고 PARTNER_LUCK에서는 annualStatus를 가까운 관계의 연간 기반으로 유지했습니다."
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
                        "현재 Timeline에는 출생시간·대운 경계 또는 다른 시간 조건에 따른 불확실성이 존재하므로 이 월 Segment를 유일한 가까운 관계 흐름으로 확정하지 않습니다."
                );
            }

            if (
                    !segment.fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment는 완전히 확정된 Timeline이 아니므로 동일 명리연도·월 좌표에 다른 가능한 Segment가 존재할 수 있으며 각각의 Source 경로를 별도로 보존합니다."
                );
            }

            reasons.add(
                    "Annual Match와 Monthly Match의 개수를 비교하여 인연의 수, 배우자운 강도, 연애 가능성 또는 관계 지속 가능성을 계산하지 않았습니다."
            );

            reasons.add(
                    "monthIndex는 기존 Timeline의 값을 그대로 보존하며 0도 유효한 값으로 취급합니다."
            );

            reasons.add(
                    "이 결과는 해당 명리연도의 가까운 관계 배경 안에서 현재 월의 관계 조정 자극이 어떤 방향으로 들어오는지를 설명하며 연애 시작, 결혼, 이별, 재회, 특정 상대의 등장 또는 실제 관계 사건을 확정하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.PARTNER_LUCK,

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
                    "PARTNER_LUCK Timeline Source ruleCode가 필요합니다."
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
                    "PARTNER_LUCK Segment Match는 null일 수 없습니다."
            );

            /*
             * =================================================
             * RELATIONSHIP Domain만 사용
             * =================================================
             */

            if (
                    match.domain()
                            != LifeDomain.RELATIONSHIP
            ) {

                continue;
            }

            /*
             * =================================================
             * 공식 ruleCode로만 Source Family 구분
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
     * Source Family Status
     * =================================================
     *
     * Match 개수는 사용하지 않는다.
     *
     * 공식 Status의 존재 조합만 사용한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveFamilyStatus(
            List<LifeDomainRuleMatch> matches
    ) {

        Objects.requireNonNull(
                matches,
                "PARTNER_LUCK Timeline Match 목록이 필요합니다."
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
         * 같은 시간층에서 양방향 존재
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
     * PARTNER_LUCK 세운 × 월운 Matrix
     * =================================================
     *
     * annualStatus:
     *
     * 해당 연도의 가까운 관계 기반.
     *
     * monthlyStatus:
     *
     * 현재 월의 단기 조정 자극.
     *
     * -------------------------------------------------
     *
     * annualStatus가 중심축이다.
     */

    private ConsultationCategoryDeterministicStatus
    resolvePartnerMonthlyStatus(

            ConsultationCategoryDeterministicStatus annualStatus,

            ConsultationCategoryDeterministicStatus monthlyStatus

    ) {

        Objects.requireNonNull(
                annualStatus,
                "PARTNER_LUCK annualStatus가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyStatus,
                "PARTNER_LUCK monthlyStatus가 필요합니다."
        );

        /*
         * =================================================
         * 어느 한 시간층이 이미 MIXED
         * =================================================
         */

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
                        || monthlyStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 연간 관계 기반과 월 자극이 반대
         * =================================================
         */

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
                        && monthlyStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
                        && monthlyStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 연간 가까운 관계 기반 SUPPORTIVE
         * =================================================
         *
         * 월운이 SUPPORTIVE 또는
         * CONTEXTUAL이면
         * 연간 기반의 지지 방향을 보존한다.
         */

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        /*
         * =================================================
         * 연간 가까운 관계 기반 PRESSURE
         * =================================================
         *
         * 월운이 PRESSURE 또는
         * CONTEXTUAL이면
         * 연간 기반의 부담 방향을 보존한다.
         */

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        /*
         * =================================================
         * 연간 관계 기반 CONTEXTUAL
         * =================================================
         *
         * monthlyStatus가 SUPPORTIVE 또는
         * PRESSURE라고 해도
         *
         * 한 달의 자극만으로
         * 해당 연도의 가까운 관계 기반 전체를
         * 승격하지 않는다.
         */

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
                "PARTNER_LUCK Match 목록이 필요합니다."
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

            ConsultationCategoryDeterministicStatus annualStatus,

            ConsultationCategoryDeterministicStatus monthlyStatus,

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
                    "해당 명리연도의 가까운 관계 기반과 현재 월의 관계 조정 자극이 서로 반대 방향으로 작동하거나 어느 한 시간층에 이미 혼재가 존재하여 MIXED로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "해당 명리연도의 가까운 관계 기반에서 부담 방향이 확인되고 현재 월이 이를 반대로 전환하는 공식 지지 자극을 제공하지 않아 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "해당 명리연도의 가까운 관계 기반에서 지지 방향이 확인되고 현재 월이 이를 반대로 뒤집는 공식 부담 자극을 제공하지 않아 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        reasons.add(
                "해당 명리연도의 가까운 관계 기반이 CONTEXTUAL이므로 현재 월에 지지 또는 부담 자극이 있더라도 한 달의 작동만으로 연간 관계 기반 전체를 SUPPORTIVE 또는 PRESSURE로 승격하지 않았습니다."
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
                "PARTNER_LUCK Current Monthly status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "해당 명리연도의 가까운 관계 기반이 비교적 지지되는 방향에 있으며 현재 월의 관계 자극이 그 흐름을 크게 뒤집지 않습니다.";

            case PRESSURE ->
                    "해당 명리연도의 가까운 관계 기반에 구조적 부담이 있으며 현재 월의 관계 자극이 이를 반대로 전환하는 방향으로 확인되지 않습니다.";

            case MIXED ->
                    "해당 명리연도의 가까운 관계 기반과 현재 월의 관계 조정 자극이 서로 다른 방향으로 맞물리거나 복합적인 조건이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 월의 관계 자극은 확인되지만 해당 명리연도의 가까운 관계 기반을 한 달의 작동만으로 한쪽 방향으로 단정하지 않습니다.";
        };
    }
}