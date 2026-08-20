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
 * SOCIAL_RELATIONSHIP 카테고리의
 * 현재 Timeline 대운 관계 배경 × 세운 상호작용 Rule.
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
 * 를 서로 다른 시간층으로 분리하여:
 *
 * "현재 대운의 인간관계 배경 위에서
 * 해당 세운의 사회적 상호작용이
 * 어떤 방향으로 작동하는가"
 *
 * 를 SOCIAL_RELATIONSHIP 관점에서 판단한다.
 *
 * =================================================
 * SOCIAL_RELATIONSHIP의 범위
 * =================================================
 *
 * 여기서 관계는:
 *
 * - 주변 사람
 * - 사회적 관계망
 * - 가까운 사람과의 상호작용
 * - 역할과 기대가 오가는 관계
 *
 * 의 구조를 의미한다.
 *
 * 다음을 직접 판단하지 않는다.
 *
 * - 연애 성사
 * - 배우자 등장
 * - 결혼
 * - 이별
 * - 재회
 *
 * 해당 질문은 LOVE_STYLE 또는
 * PARTNER_LUCK과 별도로 다뤄야 한다.
 *
 * =================================================
 * 시간층 의미
 * =================================================
 *
 * DECADE
 *
 * ->
 *
 * 비교적 긴 기간 동안
 * 인간관계 기능이 놓이는
 * 구조적 배경.
 *
 * -------------------------------------------------
 *
 * ANNUAL
 *
 * ->
 *
 * 해당 명리연도에서
 * 관계 상호작용에 추가되는
 * 지지 / 부담 / 혼재 / Context.
 *
 * =================================================
 * Source 재계산 금지
 * =================================================
 *
 * 대운과 세운에서:
 *
 * - 합
 * - 충
 * - 형
 * - 파
 * - 해
 * - 십신
 * - 궁위
 * - 합화
 * - 용신
 *
 * 등을 이 Rule에서 다시 계산하지 않는다.
 *
 * 이미 기존 LifeDomain Rule이 확정한
 * 공식 Status를 사용한다.
 *
 * =================================================
 * 두 Source 모두 필수
 * =================================================
 *
 * 같은 현재 Segment 안에:
 *
 * RELATIONSHIP_DECADE_CONTEXT
 * +
 * RELATIONSHIP_ANNUAL_CONTEXT
 *
 * 가 모두 존재할 때만 Match를 생성한다.
 *
 * =================================================
 * Status 결합
 * =================================================
 *
 * decadeStatus:
 *
 * 현재 장기 인간관계 배경.
 *
 * annualStatus:
 *
 * 해당 연도의 실제 관계 작동 방향.
 *
 * -------------------------------------------------
 *
 * 어느 한 Family가 MIXED
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
 * != 인기가 많다
 * != 좋은 사람이 생긴다
 *
 * PRESSURE
 * != 싸움이 생긴다
 * != 관계가 끊긴다
 *
 * MIXED
 * != 인간관계가 불안정하다
 *
 * 로 변환하지 않는다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * 관계 Match의 개수를:
 *
 * - 인간관계 수
 * - 관계 문제 크기
 * - 사회성
 * - 인기
 *
 * 점수로 사용하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 하나를 임의 선택하지 않는다.
 *
 * 각 Segment를 독립 계산한다.
 */
@Component
public class SocialRelationshipCurrentStructuralFlowRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_CURRENT_STRUCTURAL_FLOW";

    private static final String
            DECADE_CONTEXT_RULE_CODE =
            "RELATIONSHIP_DECADE_CONTEXT";

    private static final String
            ANNUAL_CONTEXT_RULE_CODE =
            "RELATIONSHIP_ANNUAL_CONTEXT";

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.SOCIAL_RELATIONSHIP;
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
                "SocialRelationshipCurrentStructuralFlowRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.SOCIAL_RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "SocialRelationshipCurrentStructuralFlowRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
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
                    "SOCIAL_RELATIONSHIP Current Timeline Segment는 null일 수 없습니다."
            );

            /*
             * =================================================
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
             * 두 시간층 모두 있어야
             * 장기 배경 × 연간 작동을 판단한다.
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
                    "현재 Timeline Segment에서 대운의 인간관계 Context와 해당 세운의 인간관계 Context를 서로 다른 시간층으로 확인했습니다."
            );

            reasons.add(
                    "RELATIONSHIP_DECADE_CONTEXT Source Family의 상태는 "
                            + decadeStatus.name()
                            + "이며, 현재 사회적 관계망과 상호작용의 장기 구조적 배경으로 사용했습니다."
            );

            reasons.add(
                    "RELATIONSHIP_ANNUAL_CONTEXT Source Family의 상태는 "
                            + annualStatus.name()
                            + "이며, 해당 명리연도에서 추가되는 사회적 관계 상호작용의 작동 방향으로 사용했습니다."
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
                        "현재 Timeline에는 출생시간·대운 경계 또는 다른 시간 조건에 따른 불확실성이 존재하므로 이 Segment를 유일한 관계 흐름으로 확정하지 않습니다."
                );
            }

            if (
                    !segment.fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment는 완전히 확정된 Timeline이 아니므로 동일 연·월 좌표에 다른 가능한 Segment가 존재할 수 있으며 각각의 Source 경로를 별도로 보존합니다."
                );
            }

            reasons.add(
                    "대운과 세운의 RELATIONSHIP Match 개수를 비교하여 인간관계 영향력이나 사회성의 크기를 계산하지 않았습니다."
            );

            reasons.add(
                    "이 Rule에서는 월운을 사용하지 않으며 월 단위 관계 상호작용의 단기 활성은 별도의 SOCIAL_RELATIONSHIP CURRENT_TIMELINE Rule에서 계산합니다."
            );

            reasons.add(
                    "이 결과는 현재 대운의 장기 인간관계 배경과 해당 세운의 사회적 상호작용 방향을 설명하며 새로운 인연, 단절, 갈등 사건, 인기 또는 특정 인간관계 결과를 확정하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.SOCIAL_RELATIONSHIP,

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
                    "SOCIAL_RELATIONSHIP Timeline Source ruleCode가 필요합니다."
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
                    "SOCIAL_RELATIONSHIP Segment Match는 null일 수 없습니다."
            );

            /*
             * RELATIONSHIP Domain만 사용한다.
             */

            if (
                    match.domain()
                            != LifeDomain.RELATIONSHIP
            ) {

                continue;
            }

            /*
             * summary / reasons 문자열이 아니라
             * 공식 ruleCode로만 Source를 구분한다.
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
     * 개수 비교 없이
     * 공식 Status 존재 조합만 사용한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveFamilyStatus(
            List<LifeDomainRuleMatch> matches
    ) {

        Objects.requireNonNull(
                matches,
                "SOCIAL_RELATIONSHIP Timeline Match 목록이 필요합니다."
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
         * upstream MIXED 보존.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * 같은 시간층에
         * 지지와 부담이 모두 존재.
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
     * 대운 × 세운 관계 Matrix
     * =================================================
     */

    private ConsultationCategoryDeterministicStatus
    resolveStructuralFlowStatus(

            ConsultationCategoryDeterministicStatus decadeStatus,

            ConsultationCategoryDeterministicStatus annualStatus

    ) {

        Objects.requireNonNull(
                decadeStatus,
                "SOCIAL_RELATIONSHIP decadeStatus가 필요합니다."
        );

        Objects.requireNonNull(
                annualStatus,
                "SOCIAL_RELATIONSHIP annualStatus가 필요합니다."
        );

        /*
         * 어느 한 시간층이 이미 MIXED.
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
         * 장기 관계 배경과
         * 해당 연도의 작동 방향이 반대.
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
         * 세운이 명확한 방향을 가지면
         * 해당 연도의 작동 방향을 우선 보존한다.
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
         * 세운이 CONTEXTUAL이면
         * 장기 관계 배경을 뒤집는
         * 공식 방향이 없는 상태다.
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
                "SOCIAL_RELATIONSHIP Match 목록이 필요합니다."
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
                    "현재 대운의 인간관계 배경과 해당 세운의 사회적 상호작용 방향이 서로 다르거나 어느 한 시간층에 이미 혼재가 존재하여 MIXED로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "현재 대운 또는 해당 세운의 관계 상호작용에서 부담 방향이 확인되고 다른 시간층에서 이를 반대로 전환할 공식 지지 근거가 없어 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "현재 대운 또는 해당 세운의 관계 상호작용에서 지지 방향이 확인되고 다른 시간층에서 이를 반대로 뒤집는 공식 부담 방향이 없어 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        reasons.add(
                "현재 대운과 해당 세운 모두 인간관계 Context는 존재하지만 공식 근거만으로 사회적 상호작용의 방향을 한쪽으로 확정할 수 없어 CONTEXTUAL로 유지합니다."
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
                "SOCIAL_RELATIONSHIP Current Timeline status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운의 인간관계 배경과 해당 세운의 사회적 상호작용이 비교적 지지되는 방향으로 연결됩니다.";

            case PRESSURE ->
                    "현재 대운의 인간관계 배경과 해당 세운의 사회적 상호작용에서 구조적인 관계 부담 방향이 확인됩니다.";

            case MIXED ->
                    "현재 대운의 인간관계 배경과 해당 세운의 사회적 상호작용이 서로 다른 방향으로 겹치거나 복합적인 관계 조건이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운과 해당 세운에서 인간관계 기능은 관여하지만 사회적 상호작용의 흐름을 한 방향으로 단정하지 않습니다.";
        };
    }
}