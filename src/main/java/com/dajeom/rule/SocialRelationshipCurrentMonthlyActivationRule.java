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
 * 현재 Timeline 세운 × 월운 상호작용 활성 Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * SocialRelationshipCurrentStructuralFlowRule과
 * 서로 다른 시간 질문을 담당한다.
 *
 * -------------------------------------------------
 *
 * SocialRelationshipCurrentStructuralFlowRule
 *
 * ->
 *
 * 대운
 * +
 * 세운
 *
 * 을 결합하여:
 *
 * "현재 명리연도의 인간관계 흐름이
 * 어떤 장기 관계 배경 위에서
 * 작동하는가"
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
 * "해당 명리연도 안에서
 * 현재 월의 사회적 상호작용이
 * 어떤 방향으로 단기 활성되는가"
 *
 * 를 판단한다.
 *
 * =================================================
 * SOCIAL_RELATIONSHIP 범위
 * =================================================
 *
 * 이 Rule에서 관계는:
 *
 * - 주변 사람
 * - 사회적 관계망
 * - 가까운 사람과의 상호작용
 * - 기대와 역할이 오가는 관계
 * - 관계 경계의 단기 변화 Context
 *
 * 를 의미한다.
 *
 * 다음 사건을 직접 판단하지 않는다.
 *
 * - 연애 시작
 * - 배우자 등장
 * - 결혼
 * - 이별
 * - 재회
 * - 배신
 * - 절교
 *
 * =================================================
 * 시간층 의미
 * =================================================
 *
 * ANNUAL
 *
 * ->
 *
 * 해당 명리연도에서 유지되는
 * 인간관계 상호작용의 연간 배경.
 *
 * -------------------------------------------------
 *
 * MONTHLY
 *
 * ->
 *
 * 그 연간 배경 위에서
 * 현재 월에 추가되는:
 *
 * - 관계 지지
 * - 관계 부담
 * - 상호작용 복합성
 * - 단기 관계 Context
 *
 * 의 활성 방향.
 *
 * =================================================
 * Source 1
 * RELATIONSHIP_ANNUAL_CONTEXT
 * =================================================
 *
 * 기존 RelationshipAnnualContextRule에서
 * 이미 deterministic하게 계산된
 * 해당 세운의 관계 Context를 사용한다.
 *
 * 이 Rule에서:
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
 * 기존 RelationshipMonthlyContextRule에서
 * 이미 deterministic하게 계산된
 * 해당 월운의 관계 Context를 사용한다.
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
 * 를 다시 계산하지 않는다.
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
 * 가 모두 존재해야
 * Match를 생성한다.
 *
 * 세운 없이 월운 하나만으로
 * 현재 월 인간관계 방향을
 * 생성하지 않는다.
 *
 * =================================================
 * 결합 원칙
 * =================================================
 *
 * annualStatus:
 *
 * 해당 명리연도의
 * 사회적 관계 상호작용 배경.
 *
 * -------------------------------------------------
 *
 * monthlyStatus:
 *
 * 현재 월에 실제 추가되는
 * 단기 상호작용 활성 방향.
 *
 * -------------------------------------------------
 *
 * 이번 Rule에서는
 * monthlyStatus를
 * 현재 월 활성 방향의 중심으로 본다.
 *
 * annualStatus는
 * 그 월의 작동이
 * 연간 관계 흐름과 같은지,
 * 반대인지 확인하는 배경이다.
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
 * annual SUPPORTIVE
 * +
 * monthly PRESSURE
 * -> MIXED
 *
 * 연간 관계 흐름은 지지되지만
 * 현재 월에는 부담 방향이 들어온다.
 *
 * -------------------------------------------------
 *
 * annual PRESSURE
 * +
 * monthly SUPPORTIVE
 * -> MIXED
 *
 * 연간 관계 흐름에는 부담이 있지만
 * 현재 월에는 지지 방향이 들어온다.
 *
 * -------------------------------------------------
 *
 * monthly PRESSURE
 * +
 * annual PRESSURE / CONTEXTUAL
 * -> PRESSURE
 *
 * -------------------------------------------------
 *
 * monthly SUPPORTIVE
 * +
 * annual SUPPORTIVE / CONTEXTUAL
 * -> SUPPORTIVE
 *
 * -------------------------------------------------
 *
 * monthly CONTEXTUAL
 * +
 * annual PRESSURE
 * -> PRESSURE
 *
 * 현재 월이 연간 관계 부담을
 * 뒤집는 공식 지지 방향을
 * 제공하지 않는다.
 *
 * -------------------------------------------------
 *
 * monthly CONTEXTUAL
 * +
 * annual SUPPORTIVE
 * -> SUPPORTIVE
 *
 * 현재 월이 연간 관계 지지 흐름을
 * 뒤집는 공식 부담 방향을
 * 제공하지 않는다.
 *
 * -------------------------------------------------
 *
 * 둘 다 CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 * 중요한 제한
 * =================================================
 *
 * SUPPORTIVE
 * != 인기가 많아진다
 * != 좋은 사람이 들어온다
 *
 * PRESSURE
 * != 싸움이 생긴다
 * != 관계가 끊어진다
 *
 * MIXED
 * != 인간관계가 불안정해진다
 *
 * -------------------------------------------------
 *
 * 이 Rule은 관계 사건이 아니라
 * 해당 월의 상호작용 방향만 보존한다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Annual Match와 Monthly Match의
 * 개수를 비교해서:
 *
 * - 관계 영향력이 더 강하다
 * - 사람 문제가 많다
 * - 사회성이 높다
 *
 * 같은 판단을 하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각각 독립 계산한다.
 *
 * 특정 Segment 하나를
 * 대표값으로 임의 선택하지 않는다.
 *
 * monthIndex == 0 역시
 * 유효한 Timeline 값으로 보존한다.
 */
@Component
public class SocialRelationshipCurrentMonthlyActivationRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_CURRENT_MONTHLY_ACTIVATION";

    private static final String
            ANNUAL_CONTEXT_RULE_CODE =
            "RELATIONSHIP_ANNUAL_CONTEXT";

    private static final String
            MONTHLY_CONTEXT_RULE_CODE =
            "RELATIONSHIP_MONTHLY_CONTEXT";

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
                "SocialRelationshipCurrentMonthlyActivationRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "SocialRelationshipCurrentMonthlyActivationRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
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
             * 세운 + 월운 모두 필요
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
             * 시간층별 Status
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
             * 세운 × 월운
             * =================================================
             */

            ConsultationCategoryDeterministicStatus
                    finalStatus =
                    resolveMonthlyActivationStatus(
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
                    "현재 Timeline Segment에서 해당 세운의 인간관계 Context와 현재 월운의 인간관계 Context를 서로 다른 시간층으로 확인했습니다."
            );

            reasons.add(
                    "RELATIONSHIP_ANNUAL_CONTEXT Source Family의 상태는 "
                            + annualStatus.name()
                            + "이며, 해당 명리연도의 사회적 관계 상호작용 배경으로 사용했습니다."
            );

            reasons.add(
                    "RELATIONSHIP_MONTHLY_CONTEXT Source Family의 상태는 "
                            + monthlyStatus.name()
                            + "이며, 현재 월에 추가되는 사회적 상호작용의 단기 활성 방향으로 사용했습니다."
            );

            appendCombinationReason(
                    reasons,
                    annualStatus,
                    monthlyStatus,
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
                        "현재 Timeline에는 출생시간·대운 경계 또는 다른 시간 조건에 따른 불확실성이 존재하므로 이 월 Segment를 유일한 관계 흐름으로 확정하지 않습니다."
                );
            }

            if (
                    !segment.fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment는 완전히 확정된 Timeline이 아니므로 동일 명리연도·월 좌표에 다른 가능한 Segment가 존재할 수 있으며 각 Source 경로를 별도 결과로 보존합니다."
                );
            }

            reasons.add(
                    "Annual Match와 Monthly Match의 개수를 비교하여 어느 시간층의 관계 영향력이 더 강한지 판단하지 않았습니다."
            );

            reasons.add(
                    "monthIndex는 기존 Timeline에서 계산된 값을 그대로 보존하며 0도 정상적인 값으로 취급합니다."
            );

            reasons.add(
                    "이번 Rule은 해당 명리연도의 인간관계 흐름 안에서 현재 월의 사회적 상호작용이 어떤 방향으로 단기 활성되는지만 설명하며 새로운 인연, 갈등, 단절, 배신, 인기 또는 특정 관계 사건을 확정하지 않습니다."
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
             * 공식 ruleCode로 Source Family 구분
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
     *
     * Match 개수를 사용하지 않는다.
     *
     * 공식 LifeDomainSignalStatus의
     * 존재 조합만 사용한다.
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
         * 같은 시간층에 양방향 존재
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
     * SOCIAL_RELATIONSHIP 세운 × 월운 Matrix
     * =================================================
     *
     * monthlyStatus를
     * 현재 월의 직접 활성 방향으로 사용한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveMonthlyActivationStatus(

            ConsultationCategoryDeterministicStatus annualStatus,

            ConsultationCategoryDeterministicStatus monthlyStatus

    ) {

        Objects.requireNonNull(
                annualStatus,
                "SOCIAL_RELATIONSHIP annualStatus가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyStatus,
                "SOCIAL_RELATIONSHIP monthlyStatus가 필요합니다."
        );

        /*
         * =================================================
         * 어느 한 시간층이 MIXED
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
         * 연간 방향과 월 활성 방향이 반대
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
         * 현재 월이 명확한 방향
         * =================================================
         */

        if (
                monthlyStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        if (
                monthlyStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        /*
         * =================================================
         * 월운 CONTEXTUAL
         * =================================================
         *
         * 현재 월이 연간 관계 방향을
         * 공식적으로 뒤집는 근거를
         * 제공하지 않는 상태다.
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
                    "해당 세운의 인간관계 상호작용 배경과 현재 월운의 관계 활성 방향이 서로 다르거나 어느 한 시간층에 이미 혼재가 존재하여 MIXED로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "현재 월의 사회적 상호작용에서 부담 방향이 확인되거나, 월운이 CONTEXTUAL인 상태에서 기존 세운의 관계 부담을 뒤집는 공식 지지 근거가 없어 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "현재 월의 사회적 상호작용에서 지지 방향이 확인되거나, 월운이 CONTEXTUAL인 상태에서 기존 세운의 관계 지지 방향을 뒤집는 공식 부담 근거가 없어 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        reasons.add(
                "해당 세운과 현재 월 모두 인간관계 Context는 존재하지만 공식 근거만으로 현재 월의 사회적 상호작용 방향을 확정할 수 없어 CONTEXTUAL로 유지합니다."
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
                "SOCIAL_RELATIONSHIP Current Monthly status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "해당 세운의 인간관계 배경 위에서 현재 월의 사회적 상호작용이 비교적 지지되는 방향으로 활성됩니다.";

            case PRESSURE ->
                    "해당 세운의 인간관계 배경 위에서 현재 월의 사회적 상호작용에 구조적인 관계 부담 방향이 활성됩니다.";

            case MIXED ->
                    "해당 세운의 인간관계 배경과 현재 월의 사회적 상호작용이 서로 다른 방향으로 작동하거나 복합적인 관계 조건이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "해당 세운과 현재 월에서 인간관계 기능은 관여하지만 현재 월의 사회적 상호작용 방향을 한쪽으로 단정하지 않습니다.";
        };
    }
}