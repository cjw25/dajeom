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
 * 현재 Timeline 장기 관계 기반 × 세운 관계 작동 Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 기존 RELATIONSHIP LifeDomain의:
 *
 * - RELATIONSHIP_DECADE_CONTEXT
 * - RELATIONSHIP_ANNUAL_CONTEXT
 *
 * 를 사용하여:
 *
 * "현재 대운에서 형성된 장기적인
 * 가까운 관계의 구조적 배경 위에서
 * 해당 세운의 관계 작동이
 * 어떤 방향으로 들어오는가"
 *
 * 를 PARTNER_LUCK 관점에서 판단한다.
 *
 * =================================================
 * SOCIAL_RELATIONSHIP과의 차이
 * =================================================
 *
 * SOCIAL_RELATIONSHIP은:
 *
 * 현재 사회적 관계망에서
 * 해당 연도의 상호작용이
 * 어떤 방향으로 작동하는지를 본다.
 *
 * 따라서 세운의 실제 작동 방향을
 * 비교적 직접적으로 보존할 수 있다.
 *
 * -------------------------------------------------
 *
 * PARTNER_LUCK은:
 *
 * 장기간 가까운 관계를
 * 유지·조정할 수 있는 관계 기반을
 * 중심으로 본다.
 *
 * 따라서 세운 하나만으로:
 *
 * - 장기 관계 기반이 좋다
 * - 장기 관계 기반이 나쁘다
 *
 * 라고 승격하지 않는다.
 *
 * =================================================
 * Source 1
 * RELATIONSHIP_DECADE_CONTEXT
 * =================================================
 *
 * 현재 대운에서 이미 계산된
 * 관계 기능의 장기 Context를 사용한다.
 *
 * 이번 Rule에서는:
 *
 * - 십신
 * - 관계
 * - 합
 * - 충
 * - 형
 * - 파
 * - 해
 * - 용신
 * - 합화
 *
 * 등을 다시 계산하지 않는다.
 *
 * =================================================
 * Source 2
 * RELATIONSHIP_ANNUAL_CONTEXT
 * =================================================
 *
 * 해당 세운에서 이미 계산된
 * 관계 기능의 연간 Context를 사용한다.
 *
 * 이 역시 기존 LifeDomain Rule이
 * 확정한 공식 Status를 사용한다.
 *
 * =================================================
 * 두 Source 모두 필수
 * =================================================
 *
 * 같은 현재 Segment 안에서:
 *
 * RELATIONSHIP_DECADE_CONTEXT
 * +
 * RELATIONSHIP_ANNUAL_CONTEXT
 *
 * 가 모두 존재해야 한다.
 *
 * =================================================
 * PARTNER_LUCK 결합 원칙
 * =================================================
 *
 * decadeStatus:
 *
 * 장기간 가까운 관계가 놓이는
 * 구조적 기반.
 *
 * -------------------------------------------------
 *
 * annualStatus:
 *
 * 해당 명리연도에 추가되는
 * 관계 작동 방향.
 *
 * -------------------------------------------------
 *
 * PARTNER_LUCK에서는
 * decadeStatus를 중심축으로 사용한다.
 *
 * annualStatus는:
 *
 * - 장기 기반을 지지하는가
 * - 장기 기반에 부담을 추가하는가
 * - 장기 기반과 반대로 작동하는가
 *
 * 를 판단하는 연간 보정층이다.
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
 * DECADE SUPPORTIVE
 * +
 * ANNUAL PRESSURE
 * -> MIXED
 *
 * 장기 관계 기반은 지지되지만
 * 해당 연도에는 부담 방향이 들어온다.
 *
 * -------------------------------------------------
 *
 * DECADE PRESSURE
 * +
 * ANNUAL SUPPORTIVE
 * -> MIXED
 *
 * 장기 관계 기반에는 부담이 있지만
 * 해당 연도에는 지지 방향이 들어온다.
 *
 * -------------------------------------------------
 *
 * DECADE SUPPORTIVE
 * +
 * ANNUAL SUPPORTIVE / CONTEXTUAL
 * -> SUPPORTIVE
 *
 * 장기 관계 기반의 지지 방향을
 * 해당 연도가 뒤집지 않는다.
 *
 * -------------------------------------------------
 *
 * DECADE PRESSURE
 * +
 * ANNUAL PRESSURE / CONTEXTUAL
 * -> PRESSURE
 *
 * 장기 관계 기반의 부담 방향을
 * 해당 연도가 뒤집지 않는다.
 *
 * -------------------------------------------------
 *
 * DECADE CONTEXTUAL
 * +
 * ANNUAL SUPPORTIVE / PRESSURE / CONTEXTUAL
 * -> CONTEXTUAL
 *
 * 세운 하나만으로
 * 장기 가까운 관계 기반 전체를
 * SUPPORTIVE 또는 PRESSURE로
 * 자동 승격하지 않는다.
 *
 * =================================================
 * 중요한 제한
 * =================================================
 *
 * SUPPORTIVE
 * != 연애가 시작된다
 * != 좋은 배우자가 온다
 * != 결혼한다
 *
 * PRESSURE
 * != 이별한다
 * != 배우자와 싸운다
 * != 결혼이 깨진다
 *
 * MIXED
 * != 헤어졌다 다시 만난다
 *
 * -------------------------------------------------
 *
 * 이 Rule은 사건이 아니라:
 *
 * "장기 관계 기반과 해당 연도의
 * 관계 작동 방향이 어떻게 맞물리는가"
 *
 * 만 판단한다.
 *
 * =================================================
 * 배우자 확정 금지
 * =================================================
 *
 * RELATIONSHIP LifeDomain Evidence를
 * 사용한다고 해서:
 *
 * 특정 인물
 * 미래 배우자
 * 현재 연인
 *
 * 을 확정하지 않는다.
 *
 * PARTNER_LUCK은
 * 가까운 장기 관계가 형성·유지될 때
 * 작동하는 구조를 보는 카테고리다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * 관계 Match 개수를:
 *
 * - 인연 수
 * - 연애 횟수
 * - 결혼 가능성
 * - 배우자운 강도
 *
 * 로 사용하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각각 독립 계산한다.
 *
 * 특정 Segment 하나를
 * 대표 Timeline으로 선택하지 않는다.
 */
@Component
public class PartnerLuckCurrentStructuralFlowRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_CURRENT_STRUCTURAL_FLOW";

    private static final String
            DECADE_CONTEXT_RULE_CODE =
            "RELATIONSHIP_DECADE_CONTEXT";

    private static final String
            ANNUAL_CONTEXT_RULE_CODE =
            "RELATIONSHIP_ANNUAL_CONTEXT";

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
                "PartnerLuckCurrentStructuralFlowRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "PartnerLuckCurrentStructuralFlowRule은 PARTNER_LUCK View만 평가할 수 있습니다."
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
             * 대운 관계 Context
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
             * Source 2
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
             * Family Status
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
             * PARTNER_LUCK 전용 결합
             * =================================================
             */

            ConsultationCategoryDeterministicStatus
                    finalStatus =
                    resolvePartnerFlowStatus(
                            decadeStatus,
                            annualStatus
                    );

            /*
             * =================================================
             * Provenance
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
                    "현재 Timeline Segment에서 대운의 장기 관계 Context와 해당 세운의 관계 Context를 서로 다른 시간층으로 확인했습니다."
            );

            reasons.add(
                    "RELATIONSHIP_DECADE_CONTEXT Source Family의 상태는 "
                            + decadeStatus.name()
                            + "이며, PARTNER_LUCK에서는 가까운 관계가 장기간 놓이는 구조적 기반의 중심 Evidence로 사용했습니다."
            );

            reasons.add(
                    "RELATIONSHIP_ANNUAL_CONTEXT Source Family의 상태는 "
                            + annualStatus.name()
                            + "이며, 해당 명리연도에 장기 관계 기반 위로 추가되는 관계 작동 방향으로 사용했습니다."
            );

            appendCombinationReason(
                    reasons,
                    decadeStatus,
                    annualStatus,
                    finalStatus
            );

            reasons.add(
                    "SOCIAL_RELATIONSHIP처럼 세운의 방향만으로 전체 결론을 승격하지 않고, PARTNER_LUCK에서는 대운의 장기 관계 기반을 중심축으로 유지했습니다."
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
                        "현재 Timeline에는 출생시간·대운 경계 또는 다른 시간 조건에 따른 불확실성이 존재하므로 이 Segment를 유일한 장기 관계 흐름으로 확정하지 않습니다."
                );
            }

            if (
                    !segment.fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment는 완전히 확정된 Timeline이 아니므로 동일 연·월 좌표에 다른 가능한 Segment가 존재할 수 있으며 각 Source 경로를 별도로 보존합니다."
                );
            }

            reasons.add(
                    "대운과 세운의 RELATIONSHIP Match 개수를 비교하여 배우자운, 연애 가능성 또는 관계 지속 가능성의 강도를 계산하지 않았습니다."
            );

            reasons.add(
                    "이 Rule에서는 월운을 사용하지 않으며 월 단위의 가까운 관계 활성은 별도의 PARTNER_LUCK CURRENT_TIMELINE Rule에서 계산합니다."
            );

            reasons.add(
                    "이 결과는 장기적인 가까운 관계의 구조적 기반과 해당 세운의 관계 작동이 어떻게 맞물리는지를 설명하며 연애 시작, 특정 상대의 등장, 결혼, 이별, 재회 또는 배우자 사건을 확정하지 않습니다."
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
             * 공식 ruleCode 기준
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
     * Match 개수를 비교하지 않는다.
     *
     * 해당 Family 안의
     * 공식 Status 존재만 사용한다.
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
     * PARTNER_LUCK 대운 × 세운 Matrix
     * =================================================
     *
     * SOCIAL_RELATIONSHIP과 다르게
     * decadeStatus가 장기 관계 기반의
     * 중심축이다.
     */

    private ConsultationCategoryDeterministicStatus
    resolvePartnerFlowStatus(

            ConsultationCategoryDeterministicStatus decadeStatus,

            ConsultationCategoryDeterministicStatus annualStatus

    ) {

        Objects.requireNonNull(
                decadeStatus,
                "PARTNER_LUCK decadeStatus가 필요합니다."
        );

        Objects.requireNonNull(
                annualStatus,
                "PARTNER_LUCK annualStatus가 필요합니다."
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
         * 장기 기반과 해당 연도의 방향이 반대
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
         * 장기 관계 기반 SUPPORTIVE
         * =================================================
         *
         * 해당 연도가 SUPPORTIVE 또는
         * CONTEXTUAL이라면
         * 장기 기반의 지지 방향을 보존한다.
         */

        if (
                decadeStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        /*
         * =================================================
         * 장기 관계 기반 PRESSURE
         * =================================================
         *
         * 해당 연도가 PRESSURE 또는
         * CONTEXTUAL이라면
         * 장기 기반의 부담 방향을 보존한다.
         */

        if (
                decadeStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        /*
         * =================================================
         * 장기 관계 기반 CONTEXTUAL
         * =================================================
         *
         * 세운이 SUPPORTIVE/PRESSURE라도
         * 그 한 해의 방향만으로
         * 장기 가까운 관계 기반 전체를
         * 승격하지 않는다.
         *
         * annualStatus는 sourceMatches와
         * reasons에 그대로 보존된다.
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
                    "장기적인 가까운 관계의 대운 기반과 해당 세운의 관계 작동 방향이 서로 반대로 나타나거나 어느 한 시간층에 이미 혼재가 존재하여 MIXED로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "현재 대운의 장기 관계 기반에서 부담 방향이 확인되고 해당 세운이 이를 반대로 전환하는 공식 지지 방향을 제공하지 않아 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "현재 대운의 장기 관계 기반에서 지지 방향이 확인되고 해당 세운이 이를 반대로 뒤집는 공식 부담 방향을 제공하지 않아 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        reasons.add(
                "현재 대운의 장기 관계 기반이 CONTEXTUAL이므로 해당 세운에 지지 또는 부담 방향이 있더라도 한 해의 작동만으로 장기적인 가까운 관계 구조 전체를 SUPPORTIVE 또는 PRESSURE로 승격하지 않았습니다."
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
                "PARTNER_LUCK Current Timeline status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운에서 가까운 관계의 장기 기반이 비교적 지지되는 방향으로 놓여 있고 해당 세운이 그 흐름을 크게 뒤집지 않습니다.";

            case PRESSURE ->
                    "현재 대운에서 가까운 관계의 장기 기반에 구조적 부담이 존재하며 해당 세운이 그 부담을 반대로 전환하는 방향으로 확인되지 않습니다.";

            case MIXED ->
                    "가까운 관계의 장기 대운 기반과 해당 세운의 관계 작동이 서로 다른 방향으로 맞물리거나 복합적인 조건이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "해당 세운의 관계 작동은 확인되지만 현재 대운의 장기 관계 기반만으로 가까운 관계의 전체 방향을 한쪽으로 단정하지 않습니다.";
        };
    }
}