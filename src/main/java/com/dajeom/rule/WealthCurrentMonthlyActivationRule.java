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
 * 현재 Timeline 세운 × 월운 활성 Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * WealthCurrentStructuralFlowRule과
 * 다른 시간 질문을 담당한다.
 *
 * -------------------------------------------------
 *
 * WealthCurrentStructuralFlowRule
 *
 * ->
 *
 * 대운
 * +
 * 세운
 *
 * 을 결합하여:
 *
 * "현재 명리연도의 재물 기능이
 * 어떤 장기 배경 위에서 작동하는가"
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
 * 현재 월의 재물 기능이
 * 어떤 방향으로 활성되는가"
 *
 * 를 판단한다.
 *
 * =================================================
 * 시간층의 역할
 * =================================================
 *
 * 세운:
 *
 * 해당 명리연도에서
 * 재물 기능이 작동하는 연간 배경.
 *
 * -------------------------------------------------
 *
 * 월운:
 *
 * 그 연간 배경 위에 들어오는
 * 더 짧은 기간의 활성·자극 Context.
 *
 * -------------------------------------------------
 *
 * 따라서 월운 하나만 보고
 * 재물운 전체를 결정하지 않는다.
 *
 * =================================================
 * Source 1
 * WEALTH_ANNUAL_CONTEXT
 * =================================================
 *
 * 기존 18차에서
 * 해당 세운의 재물 관련 Context를
 * 이미 deterministic하게 계산한 결과다.
 *
 * 이 Rule에서:
 *
 * - 세운 십성
 * - 원국 × 세운
 * - 대운과의 관계
 * - 격국
 * - 용신
 * - 합화
 *
 * 등을 다시 계산하지 않는다.
 *
 * =================================================
 * Source 2
 * WEALTH_MONTHLY_CONTEXT
 * =================================================
 *
 * 기존 18차에서
 * 해당 월운의 재물 관련 Context를
 * 이미 deterministic하게 계산한 결과다.
 *
 * 이 Rule에서:
 *
 * - 월운 십성
 * - 원국 × 월운
 * - 대운 / 세운 Context
 * - 합충
 * - 합화
 * - 기능 변화
 *
 * 등을 다시 계산하지 않는다.
 *
 * =================================================
 * 두 Source 모두 필수
 * =================================================
 *
 * WEALTH_ANNUAL_CONTEXT
 * +
 * WEALTH_MONTHLY_CONTEXT
 *
 * 가 같은 Segment에 모두 있어야
 * Match를 생성한다.
 *
 * 세운 Evidence 없이 월운만으로:
 *
 * "이번 달 재물 방향"
 *
 * 을 생성하지 않는다.
 *
 * 반대로 월운 Evidence가 없다면
 * 이번 Rule의 단기 활성 판단도
 * 임의 생성하지 않는다.
 *
 * =================================================
 * 결합 원칙
 * =================================================
 *
 * annualStatus:
 *
 * 해당 연도의 재물 기능 배경.
 *
 * -------------------------------------------------
 *
 * monthlyStatus:
 *
 * 해당 월에 실제 추가되는
 * 단기 활성 방향.
 *
 * -------------------------------------------------
 *
 * 이번 Rule에서는
 * monthlyStatus를
 * 현재 월 활성의 중심 방향으로 본다.
 *
 * annualStatus는
 * 그 월의 작동이 연간 흐름과
 * 같은 방향인지 다른 방향인지
 * 판단하는 배경으로 사용한다.
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
 * 연간 배경은 지지 방향이지만
 * 해당 월에는 부담 방향이 들어온다.
 *
 * -------------------------------------------------
 *
 * annual PRESSURE
 * +
 * monthly SUPPORTIVE
 * -> MIXED
 *
 * 연간 배경은 부담 방향이지만
 * 해당 월에는 지지 방향이 들어온다.
 *
 * -------------------------------------------------
 *
 * monthly PRESSURE
 * +
 * annual PRESSURE / CONTEXTUAL
 * -> PRESSURE
 *
 * 현재 월의 단기 활성에
 * 부담 방향이 실제로 확인된다.
 *
 * -------------------------------------------------
 *
 * monthly SUPPORTIVE
 * +
 * annual SUPPORTIVE / CONTEXTUAL
 * -> SUPPORTIVE
 *
 * 현재 월의 단기 활성에
 * 지지 방향이 확인되고
 * 연간 배경이 이를 반대로 뒤집지 않는다.
 *
 * -------------------------------------------------
 *
 * monthly CONTEXTUAL
 * +
 * annual PRESSURE
 * -> PRESSURE
 *
 * 현재 월이 연간 부담을
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
 * 현재 월이 연간 지지 방향을
 * 뒤집는 공식 부담 방향을
 * 제공하지 않는다.
 *
 * -------------------------------------------------
 *
 * 둘 다 CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 * monthIndex
 * =================================================
 *
 * 기존 Timeline의 monthIndex를
 * 그대로 보존한다.
 *
 * monthIndex == 0 역시
 * 유효한 값으로 취급한다.
 *
 * 0을:
 *
 * - 값 없음
 * - 미확정
 * - 오류
 *
 * 로 해석하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각각 독립적으로 계산한다.
 *
 * 하나를 대표값으로 선택하지 않는다.
 *
 * timelineUncertaintyExists가 true이면
 * 그 사실을 reasons에 보존한다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Annual Match가 5개이고
 * Monthly Match가 2개라고 해서:
 *
 * 연운이 월운보다 강하다
 *
 * 고 판단하지 않는다.
 *
 * 서로 다른 Rule Family의 Match 개수는
 * 영향력의 크기가 아니다.
 *
 * 공식 Status의 존재 방향만 사용한다.
 *
 * =================================================
 * 실제 사건 예측 금지
 * =================================================
 *
 * SUPPORTIVE
 * ->
 * 돈이 들어온다
 *
 * PRESSURE
 * ->
 * 돈이 나간다
 *
 * MIXED
 * ->
 * 투자 변동이 발생한다
 *
 * 같은 실제 금전 사건으로
 * 변환하지 않는다.
 *
 * 또한:
 *
 * - 수입
 * - 지출
 * - 투자
 * - 계약금
 * - 부동산
 * - 사업 매출
 * - 손실
 *
 * 을 확정하지 않는다.
 */
@Component
public class WealthCurrentMonthlyActivationRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_CURRENT_MONTHLY_ACTIVATION";

    private static final String
            ANNUAL_CONTEXT_RULE_CODE =
            "WEALTH_ANNUAL_CONTEXT";

    private static final String
            MONTHLY_CONTEXT_RULE_CODE =
            "WEALTH_MONTHLY_CONTEXT";

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
                "WealthCurrentMonthlyActivationRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category 검증
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthCurrentMonthlyActivationRule은 WEALTH View만 평가할 수 있습니다."
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
         * Current Segment 필요
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
         * 각 가능한 Segment 독립 계산
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            Objects.requireNonNull(
                    segment,
                    "WEALTH Current Timeline Segment는 null일 수 없습니다."
            );

            /*
             * =================================================
             * Source 1
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
             * Source 2
             * 월운
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
                    "현재 Timeline Segment에서 해당 세운의 재물 Context와 현재 월운의 재물 Context를 서로 다른 시간층으로 확인했습니다."
            );

            reasons.add(
                    "WEALTH_ANNUAL_CONTEXT Source Family의 상태는 "
                            + annualStatus.name()
                            + "이며, 현재 명리연도의 재물 기능 배경으로 사용했습니다."
            );

            reasons.add(
                    "WEALTH_MONTHLY_CONTEXT Source Family의 상태는 "
                            + monthlyStatus.name()
                            + "이며, 현재 월에 추가되는 단기 재물 활성 방향의 중심 Evidence로 사용했습니다."
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
                        "현재 Timeline에는 출생시간·대운 경계 또는 다른 시간 조건에 따른 불확실성이 존재하므로 이 월 Segment를 유일한 가능성으로 확정하지 않습니다."
                );
            }

            if (
                    !segment.fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment는 완전 확정된 Timeline이 아니므로 동일한 명리연도·월 좌표에 다른 가능한 Segment가 존재할 수 있으며 각각의 Source 경로를 별도로 보존합니다."
                );
            }

            reasons.add(
                    "Annual Match와 Monthly Match의 개수를 서로 비교하여 연운 또는 월운의 영향력이 더 강하다고 판단하지 않았습니다."
            );

            reasons.add(
                    "monthIndex는 기존 Timeline에서 계산된 값을 그대로 보존하며 0도 유효한 값으로 취급합니다."
            );

            reasons.add(
                    "이번 Rule은 현재 명리연도 안에서 월운이 재물 기능을 어떤 방향으로 단기 활성하는지만 설명하며 실제 수입, 지출, 투자, 손실 또는 재산 사건을 확정하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
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
     * Segment Match 조회
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
             * WEALTH Domain만 허용
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
     * Family Status
     * =================================================
     *
     * Match 수가 아니라
     * 공식 Status 존재 여부만 사용한다.
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
         * 양방향 존재
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
     * 세운 × 월운 Activation Matrix
     * =================================================
     *
     * monthlyStatus를
     * 현재 월 활성 방향의 중심으로 본다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveMonthlyActivationStatus(

            ConsultationCategoryDeterministicStatus annualStatus,

            ConsultationCategoryDeterministicStatus monthlyStatus

    ) {

        Objects.requireNonNull(
                annualStatus,
                "WEALTH annualStatus가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyStatus,
                "WEALTH monthlyStatus가 필요합니다."
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
         * 현재 월이 명확한 방향을 가짐
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
         * 월운 자체가 연간 흐름을
         * 뒤집는 명확한 방향을
         * 제공하지 않는 상태.
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
                    "해당 세운의 재물 배경과 현재 월운의 재물 활성 방향이 서로 다르거나 어느 한 시간층에 이미 혼재가 존재하여 MIXED로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "현재 월의 재물 기능에서 부담 방향이 확인되거나, 월운이 CONTEXTUAL인 상태에서 기존 세운의 부담 방향을 뒤집는 공식 지지 근거가 없어 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "현재 월의 재물 기능에서 지지 방향이 확인되거나, 월운이 CONTEXTUAL인 상태에서 기존 세운의 지지 방향을 뒤집는 공식 부담 근거가 없어 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        reasons.add(
                "해당 세운과 현재 월운 모두 재물 기능의 Context는 존재하지만 공식 근거만으로 현재 월의 단기 활성 방향을 확정할 수 없어 CONTEXTUAL로 유지합니다."
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
                "WEALTH Current Monthly status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "해당 세운의 재물 배경 위에서 현재 월의 재물 기능이 비교적 지지되는 방향으로 활성됩니다.";

            case PRESSURE ->
                    "해당 세운의 재물 배경 위에서 현재 월의 자원 운용·수용 기능에 구조적 부담 방향이 활성됩니다.";

            case MIXED ->
                    "해당 세운의 재물 배경과 현재 월의 재물 활성 방향이 서로 다르게 작동하거나 복합적인 조건이 함께 존재합니다.";

            case CONTEXTUAL ->
                    "해당 세운과 현재 월에서 재물 기능이 관여하지만 현재 월의 단기 활성 방향을 한쪽으로 단정하지 않습니다.";
        };
    }
}