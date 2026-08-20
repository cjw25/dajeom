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
 * WEALTH 카테고리의
 * 현재 Timeline 대운 배경 × 세운 방향 Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 원국 재물구조를 다시 계산하지 않는다.
 *
 * 현재 Timeline Segment 안에서 이미
 * 18차 LifeDomain Rule이 계산한:
 *
 * - WEALTH_DECADE_CONTEXT
 * - WEALTH_ANNUAL_CONTEXT
 *
 * 를 서로 다른 시간층으로 분리하여:
 *
 * "현재 대운의 재물 배경 위에서
 * 해당 세운의 재물 기능이
 * 어떤 방향으로 작동하는가"
 *
 * 를 WEALTH Category 관점에서
 * 다시 판단한다.
 *
 * =================================================
 * 전통사주 시간층
 * =================================================
 *
 * 이 Rule에서:
 *
 * 대운
 * ->
 * 비교적 긴 기간의 구조적 배경
 *
 * 세운
 * ->
 * 해당 명리연도에서 실제로
 * 추가되는 연간 작동 방향
 *
 * 으로 구분한다.
 *
 * 따라서:
 *
 * 대운 하나만 보고
 * "올해 재물운"
 *
 * 을 만들지 않는다.
 *
 * 반대로:
 *
 * 세운 하나만 보고
 * 현재 장기 배경을 무시하지 않는다.
 *
 * =================================================
 * 이번 Rule에서 월운은 사용하지 않는다
 * =================================================
 *
 * MONTHLY_CONTEXT는
 * 별도의:
 *
 * WealthCurrentMonthlyActivationRule
 *
 * 에서 세운과 결합한다.
 *
 * 대운 + 세운 + 월운을
 * 한 Rule에 전부 집어넣어
 * 서로 다른 시간층의 의미를
 * 없애지 않는다.
 *
 * =================================================
 * Source 1
 * WEALTH_DECADE_CONTEXT
 * =================================================
 *
 * 기존 18차에서
 * 대운의 재성 관련 Context를
 * 이미 계산한 결과다.
 *
 * 여기서:
 *
 * - 대운 십성
 * - 원국 × 대운
 * - 격국
 * - 용신
 * - 합화
 * - 기능 변화
 *
 * 등을 다시 계산하지 않는다.
 *
 * =================================================
 * Source 2
 * WEALTH_ANNUAL_CONTEXT
 * =================================================
 *
 * 기존 18차에서
 * 해당 세운의 재성 관련 Context를
 * 이미 계산한 결과다.
 *
 * 여기서도:
 *
 * - 세운 십성
 * - 원국 × 세운
 * - 가능한 대운 Context
 * - 격국
 * - 용신
 * - 합화
 *
 * 등을 다시 계산하지 않는다.
 *
 * =================================================
 * 두 Source 모두 필수
 * =================================================
 *
 * 이 Rule은:
 *
 * WEALTH_DECADE_CONTEXT
 * +
 * WEALTH_ANNUAL_CONTEXT
 *
 * 가 현재 Segment에 모두 존재할 때만
 * Match를 만든다.
 *
 * 따라서:
 *
 * - 첫 대운 시작 전
 * - 대운 계산 불가
 * - 생성 대운 범위 밖
 *
 * 등으로 대운 Evidence가 없는 경우
 * 억지로 대운 배경을 추정하지 않는다.
 *
 * 세운만 존재하는 경우는
 * 이후 별도 시간축 Rule에서
 * 다룰 수 있다.
 *
 * =================================================
 * Status 결합 원칙
 * =================================================
 *
 * 대운과 세운은 각각 먼저
 * Family Status로 정리한다.
 *
 * Match 개수는 사용하지 않는다.
 *
 * -------------------------------------------------
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
 * 장기 배경은 지지되지만
 * 해당 연도에는 부담 방향이 들어옴.
 *
 * -------------------------------------------------
 *
 * DECADE PRESSURE
 * +
 * ANNUAL SUPPORTIVE
 * -> MIXED
 *
 * 장기 배경에는 부담이 있지만
 * 해당 연도에는 지지 방향이 들어옴.
 *
 * -------------------------------------------------
 *
 * ANNUAL PRESSURE
 * +
 * DECADE PRESSURE / CONTEXTUAL
 * -> PRESSURE
 *
 * -------------------------------------------------
 *
 * ANNUAL SUPPORTIVE
 * +
 * DECADE SUPPORTIVE / CONTEXTUAL
 * -> SUPPORTIVE
 *
 * -------------------------------------------------
 *
 * ANNUAL CONTEXTUAL
 * +
 * DECADE PRESSURE
 * -> PRESSURE
 *
 * 해당 연도가 기존 대운 부담을
 * 뒤집는 공식 근거를 제공하지 않음.
 *
 * -------------------------------------------------
 *
 * ANNUAL CONTEXTUAL
 * +
 * DECADE SUPPORTIVE
 * -> SUPPORTIVE
 *
 * 해당 연도가 기존 대운의 지지 방향을
 * 뒤집는 공식 부담 근거를 제공하지 않음.
 *
 * -------------------------------------------------
 *
 * 둘 다 CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * ConsultationCategoryEvidenceView에
 * 여러 가능한 Segment가 존재하면
 * 각 Segment를 독립적으로 계산한다.
 *
 * 서로 다른 가능한 Segment를
 * 임의로 하나 선택하지 않는다.
 *
 * 현재 ConsultationCategoryDeterministicMatch에는
 * segmentAnalysis 자체를 직접 보존하는 필드가 없으므로
 * 각 결과는:
 *
 * - sajuYear
 * - monthIndex
 * - 실제 sourceMatches
 * - reasons
 *
 * 를 통해 가능한 계산 경로를 보존한다.
 *
 * timelineUncertaintyExists가 true이면
 * 해당 사실도 reasons에 명시한다.
 *
 * =================================================
 * 사건 예측 금지
 * =================================================
 *
 * SUPPORTIVE
 * ->
 * 돈이 들어온다
 *
 * PRESSURE
 * ->
 * 돈을 잃는다
 *
 * MIXED
 * ->
 * 투자 변동이 생긴다
 *
 * 로 변환하지 않는다.
 *
 * 이 결과는:
 *
 * 현재 대운 배경과 해당 세운에서
 * 재물 기능의 구조적 방향이
 * 어떻게 맞물리는지만 나타낸다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * LifeDomain Match 수를:
 *
 * - 재물 규모
 * - 수입 규모
 * - 성공 확률
 * - 손실 위험도
 *
 * 로 사용하지 않는다.
 */
@Component
public class WealthCurrentStructuralFlowRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_CURRENT_STRUCTURAL_FLOW";

    private static final String
            DECADE_CONTEXT_RULE_CODE =
            "WEALTH_DECADE_CONTEXT";

    private static final String
            ANNUAL_CONTEXT_RULE_CODE =
            "WEALTH_ANNUAL_CONTEXT";

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.WEALTH;
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
                "WealthCurrentStructuralFlowRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthCurrentStructuralFlowRule은 WEALTH View만 평가할 수 있습니다."
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
         * 현재 Timeline 자체가 없다면 종료
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
         * 가능한 Segment를 각각 독립 계산
         * =================================================
         *
         * 출생시간 / 교운시점 등의 불확실성으로
         * Segment가 복수라면 하나를 임의 선택하지 않는다.
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            Objects.requireNonNull(
                    segment,
                    "WEALTH Current Timeline Segment는 null일 수 없습니다."
            );

            List<LifeDomainRuleMatch> decadeMatches =
                    findSegmentMatches(
                            segment,
                            DECADE_CONTEXT_RULE_CODE
                    );

            List<LifeDomainRuleMatch> annualMatches =
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
             * 시간층별 Family Status
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
             * 대운 × 세운 결합
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
                    "현재 Timeline Segment에서 대운의 재물 Context와 해당 세운의 재물 Context를 서로 다른 시간층으로 확인했습니다."
            );

            reasons.add(
                    "WEALTH_DECADE_CONTEXT Source Family의 상태는 "
                            + decadeStatus.name()
                            + "이며, 현재 재물 기능의 장기 구조적 배경으로 사용했습니다."
            );

            reasons.add(
                    "WEALTH_ANNUAL_CONTEXT Source Family의 상태는 "
                            + annualStatus.name()
                            + "이며, 현재 명리연도에서 추가되는 재물 기능의 연간 작동 방향으로 사용했습니다."
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
                        "현재 Segment는 완전 확정 Timeline이 아니므로 동일 연·월의 다른 가능한 Segment가 존재할 수 있으며, 각 가능성을 별도 결과로 보존합니다."
                );
            }

            reasons.add(
                    "대운과 세운의 LifeDomain Match 개수를 재물 규모, 수입 규모, 성공 가능성 또는 손실 위험 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "이번 Rule에서는 월운을 사용하지 않았으며, 월운의 단기 활성은 별도의 WEALTH CURRENT_TIMELINE Rule에서 계산합니다."
            );

            reasons.add(
                    "이 결과는 현재 대운의 장기 재물 배경과 해당 세운의 재물 작동 방향이 어떻게 맞물리는지를 설명하며 실제 수입, 지출, 투자, 손실 또는 재산 사건을 확정하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             *
             * monthIndex는 0을 포함해
             * 기존 Timeline 값을 그대로 사용한다.
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.WEALTH,

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
     *
     * 반드시 현재 ConsultationCategorySegmentEvidence의
     * matches() 안에서만 찾는다.
     *
     * Natal Match를 CURRENT_TIMELINE Source로
     * 끌어오지 않는다.
     *
     * 이는 ConsultationCategoryDeterministicRuleEngine의
     * 시간층 Source 검증 정책과도 일치한다.
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
                    "WEALTH Timeline Source ruleCode가 필요합니다."
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
                    "WEALTH Segment Match는 null일 수 없습니다."
            );

            /*
             * =================================================
             * WEALTH Domain만 사용
             * =================================================
             */

            if (
                    match.domain()
                            != LifeDomain.WEALTH
            ) {

                continue;
            }

            /*
             * =================================================
             * 공식 Rule Code
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
     * Match 개수는 비교하지 않는다.
     *
     * 해당 시간층 안에 존재하는
     * 공식 LifeDomainSignalStatus만 사용한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveFamilyStatus(
            List<LifeDomainRuleMatch> matches
    ) {

        Objects.requireNonNull(
                matches,
                "WEALTH Timeline Match 목록이 필요합니다."
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
         * 양방향 동시 존재
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
     * 대운 × 세운 Matrix
     * =================================================
     */

    private ConsultationCategoryDeterministicStatus
    resolveStructuralFlowStatus(

            ConsultationCategoryDeterministicStatus decadeStatus,

            ConsultationCategoryDeterministicStatus annualStatus

    ) {

        Objects.requireNonNull(
                decadeStatus,
                "WEALTH decadeStatus가 필요합니다."
        );

        Objects.requireNonNull(
                annualStatus,
                "WEALTH annualStatus가 필요합니다."
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
         * 장기 배경과 연간 방향이 서로 반대
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
         * 세운이 명확한 방향을 가진 경우
         * =================================================
         *
         * 현재 명리연도의 작동 방향을 보존한다.
         *
         * 반대 방향의 대운은 위에서
         * 이미 MIXED로 처리되었다.
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
         * 해당 연도가 장기 배경을
         * 공식적으로 뒤집는 방향을
         * 제공하지 않은 상태다.
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
                "WEALTH Timeline Match 목록이 필요합니다."
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
     * 결합 Reason
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
                    "현재 대운의 재물 배경과 해당 세운의 재물 작동 방향이 서로 다르거나 어느 한 시간층에 이미 혼재가 존재하여 MIXED로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "현재 대운 또는 해당 세운에서 확인된 재물 기능의 부담 방향이 다른 시간층에 의해 반대로 전환된 것으로 볼 공식 근거가 없어 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "현재 대운 또는 해당 세운의 재물 기능에서 지지 방향이 확인되고 다른 시간층에서 이를 반대로 뒤집는 공식 부담 방향이 확인되지 않아 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        reasons.add(
                "현재 대운과 해당 세운 모두 재물 기능의 Context는 존재하지만 공식 근거만으로 한 방향의 흐름을 확정할 수 없어 CONTEXTUAL로 유지합니다."
        );
    }

    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 사용자용 최종 storyText가 아니다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "WEALTH Current Timeline status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운의 재물 배경과 해당 세운의 재물 작동이 비교적 지지되는 방향으로 연결됩니다.";

            case PRESSURE ->
                    "현재 대운의 재물 배경과 해당 세운의 작동에서 자원 운용·수용과 관련한 구조적 부담 방향이 확인됩니다.";

            case MIXED ->
                    "현재 대운의 재물 배경과 해당 세운의 작동이 서로 다른 방향으로 겹치거나 한 시간층에 복합성이 존재합니다.";

            case CONTEXTUAL ->
                    "현재 대운과 해당 세운에서 재물 기능은 관여하지만 두 시간층의 결합을 한 방향으로 단정하지 않습니다.";
        };
    }
}