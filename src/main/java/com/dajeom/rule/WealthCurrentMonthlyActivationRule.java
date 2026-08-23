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
 * WEALTH 현재 월 재물 상태 deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 현재 Timeline Segment에 실제 존재하는:
 *
 * WEALTH_MONTHLY_CONTEXT
 *
 * 의 공식 상태를
 * WEALTH Category의 현재 월 상태로 보존한다.
 *
 * =================================================
 * 시간층 분리
 * =================================================
 *
 * 세운:
 *
 * WEALTH_ANNUAL_CONTEXT
 *
 * ->
 *
 * WealthCurrentStructuralFlowRule
 *
 * 에서 독립적으로 처리한다.
 *
 * -------------------------------------------------
 *
 * 월운:
 *
 * WEALTH_MONTHLY_CONTEXT
 *
 * ->
 *
 * 이 Rule에서 독립적으로 처리한다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 기존에는:
 *
 * WEALTH_ANNUAL_CONTEXT
 * +
 * WEALTH_MONTHLY_CONTEXT
 *
 * 의 Status를 Matrix로 결합하여
 * 새로운 월운 finalStatus를 만들었다.
 *
 * 이제 그렇게 하지 않는다.
 *
 * =================================================
 * 삭제되는 기존 계산
 * =================================================
 *
 * ANNUAL SUPPORTIVE
 * +
 * MONTHLY PRESSURE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * ANNUAL PRESSURE
 * +
 * MONTHLY SUPPORTIVE
 * =
 * MIXED
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * MONTHLY CONTEXTUAL
 * +
 * ANNUAL PRESSURE
 * =
 * PRESSURE
 *
 * 금지.
 *
 * -------------------------------------------------
 *
 * MONTHLY CONTEXTUAL
 * +
 * ANNUAL SUPPORTIVE
 * =
 * SUPPORTIVE
 *
 * 금지.
 *
 * =================================================
 * 이유
 * =================================================
 *
 * 세운과 월운은 실제 계산된
 * 서로 다른 시간층이다.
 *
 * 하지만:
 *
 * 세운 Status A
 * +
 * 월운 Status B
 *
 * 만으로:
 *
 * - A가 B를 강화한다.
 * - A가 B를 약화한다.
 * - A가 B를 뒤집는다.
 * - A와 B가 충돌한다.
 * - A가 B를 보완한다.
 *
 * 같은 새로운 상호작용을
 * Category Rule에서 생성하지 않는다.
 *
 * 그런 판단이 필요하면
 * 세운 × 월운 상호작용 자체를 직접 계산한
 * 별도의 deterministic Analysis가 필요하다.
 *
 * =================================================
 * 공식 Status Anchor
 * =================================================
 *
 * 이 Rule의 Status는 오직:
 *
 * WEALTH_MONTHLY_CONTEXT
 *
 * Source Family만 사용한다.
 *
 * =================================================
 * Monthly Source Family Status
 * =================================================
 *
 * 동일한 WEALTH_MONTHLY_CONTEXT Source Family 내부에서:
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
 * 중요
 * =================================================
 *
 * 위 MIXED는:
 *
 * 세운과 월운의 합성 결과가 아니다.
 *
 * 동일한 Monthly Source Family 안에서
 * upstream이 이미 확정한
 * 공식 방향들이 실제로 함께 존재할 때만
 * 정규화한 것이다.
 *
 * =================================================
 * Monthly Functional
 * =================================================
 *
 * 프로젝트에는 별도로:
 *
 * WEALTH_MONTHLY_FUNCTIONAL
 *
 * 계산이 존재한다.
 *
 * 이 Rule에서는
 * WEALTH_MONTHLY_CONTEXT와
 * WEALTH_MONTHLY_FUNCTIONAL을
 * 합성하지 않는다.
 *
 * Monthly Functional 결과가
 * WEALTH Category Contract에 필요하면
 * 별도의 deterministic Rule로
 * 독립 보존한다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음을 생성하지 않는다.
 *
 * - 연간 재물 흐름 위에서 월운이 작동한다.
 * - 세운 때문에 이번 달 부담이 커진다.
 * - 월운이 세운의 부담을 완화한다.
 * - 세운과 월운이 같은 방향으로 강화된다.
 * - 먼저 A이고 이후 B가 된다.
 * - A 때문에 B가 된다.
 * - A가 B로 이어진다.
 *
 * =================================================
 * 실제 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 돈이 들어온다
 * != 수입이 증가한다
 * != 투자에 성공한다
 *
 * PRESSURE
 * != 돈이 나간다
 * != 손실이 발생한다
 * != 투자에 실패한다
 *
 * MIXED
 * != 재물 기복이 발생한다
 *
 * CONTEXTUAL
 * != 재물운이 없다
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
 * - 횡재
 *
 * 같은 실제 사건을 확정하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각 Segment를 독립적으로 보존한다.
 *
 * 하나를 대표값으로 임의 선택하지 않는다.
 *
 * =================================================
 * monthIndex
 * =================================================
 *
 * 기존 Timeline에서 계산된
 * monthIndex를 그대로 보존한다.
 *
 * monthIndex == 0도
 * 정상적인 유효 값으로 취급한다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Monthly Match 반복 개수를:
 *
 * - 재물운 강도
 * - 수입 규모
 * - 자산 규모
 * - 재물 사건 횟수
 *
 * 로 사용하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 세운 사용
 * - 대운 사용
 * - 세운 × 월운 Status 합성
 * - 대운 × 월운 Status 합성
 * - 원국 × 월운 Status 합성
 * - 재성 재계산
 * - 십신 재계산
 * - 신강신약 재계산
 * - 격국 재계산
 * - 용신 재계산
 * - 합충형파해 재계산
 * - 합화 재계산
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 재물 사건 생성
 */
@Component
public class WealthCurrentMonthlyActivationRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_CURRENT_MONTHLY_ACTIVATION";

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
         * Category
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
         * 가능한 Segment 각각 독립 처리
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segment,
                            "WEALTH Current Timeline Segment는 null일 수 없습니다."
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
             * 월운 재물 Context가 없으면
             * 이 Rule에서는 Match를 생성하지 않는다.
             *
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
                    "현재 월 WEALTH Status는 WEALTH_MONTHLY_CONTEXT Source Family만 공식 Status Anchor로 사용했습니다."
            );

            appendMonthlyStatusReason(
                    reasons,
                    monthlyMatches,
                    monthlyStatus
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
                        "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 WEALTH 월운 경로로 확정하지 않습니다."
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
                    "WEALTH_ANNUAL_CONTEXT와 WEALTH_DECADE_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
            );

            reasons.add(
                    "세운과 월운의 Status를 Matrix로 결합하여 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 만들지 않았습니다."
            );

            reasons.add(
                    "WEALTH_MONTHLY_FUNCTIONAL 결과도 이 Rule에 합성하지 않았으며 별도 계산 결과로 유지합니다."
            );

            reasons.add(
                    "Monthly Source Match의 반복 개수를 재물운 강도·수입 규모·자산 규모·재물 사건 가능성의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
            );

            reasons.add(
                    "monthIndex는 기존 Timeline 계산값을 그대로 보존하며 0도 유효한 값으로 취급합니다."
            );

            reasons.add(
                    "이 결과는 현재 월의 재물영역 deterministic 상태만 보존하며 실제 수입·지출·투자·손실·자산 증감 사건을 생성하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             *
             * sourceMatches에는 Monthly Source만 넣는다.
             *
             * Annual / Decade Source를 같은 Match에
             * provenance 용도로도 결합하지 않는다.
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.WEALTH,

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
                        "WEALTH Timeline Source ruleCode가 필요합니다."
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
                            "WEALTH Segment Match는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * WEALTH Domain만
             * =================================================
             */

            if (
                    normalized.domain()
                            != LifeDomain.WEALTH
            ) {

                continue;
            }

            /*
             * =================================================
             * 공식 Rule Code만
             * =================================================
             *
             * summary / reasons 문자열은
             * Source 판별에 사용하지 않는다.
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
     * Annual / Decade Status와 결합하지 않는다.
     *
     * 오직:
     *
     * WEALTH_MONTHLY_CONTEXT
     *
     * Source Family 내부의
     * 공식 LifeDomainSignalStatus만 사용한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveMonthlyStatus(
            List<LifeDomainRuleMatch> monthlyMatches
    ) {

        Objects.requireNonNull(
                monthlyMatches,
                "WEALTH Monthly Match 목록이 필요합니다."
        );

        if (
                monthlyMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "WEALTH Monthly Status 계산에는 최소 하나의 Monthly Match가 필요합니다."
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
         * 두 공식 방향이 모두 실제 존재
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
                "WEALTH Monthly Match 목록이 필요합니다."
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
                "WEALTH reasons가 필요합니다."
        );

        Objects.requireNonNull(
                monthlyMatches,
                "WEALTH Monthly Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                monthlyStatus,
                "WEALTH Monthly Status가 필요합니다."
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
                    "WEALTH_MONTHLY_CONTEXT Source Family에 upstream MIXED 상태가 존재하므로 현재 월 WEALTH Status를 MIXED로 보존합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "WEALTH_MONTHLY_CONTEXT Source Family 내부에 SUPPORTIVE와 PRESSURE가 모두 실제 존재하므로 어느 한 방향을 삭제하지 않고 MIXED로 보존합니다."
            );

            return;
        }

        if (
                monthlyStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "WEALTH_MONTHLY_CONTEXT Source Family에 PRESSURE 상태가 존재하고 SUPPORTIVE 또는 MIXED 상태가 없어 현재 월 WEALTH Status를 PRESSURE로 확정합니다."
            );

            return;
        }

        if (
                monthlyStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "WEALTH_MONTHLY_CONTEXT Source Family에 SUPPORTIVE 상태가 존재하고 PRESSURE 또는 MIXED 상태가 없어 현재 월 WEALTH Status를 SUPPORTIVE로 확정합니다."
            );

            return;
        }

        reasons.add(
                "WEALTH_MONTHLY_CONTEXT Source Family는 존재하지만 SUPPORTIVE·PRESSURE·MIXED 방향이 확정되지 않아 현재 월 WEALTH Status를 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * 세운이나 대운과 연결하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus monthlyStatus
    ) {

        Objects.requireNonNull(
                monthlyStatus,
                "WEALTH Monthly Status가 필요합니다."
        );

        return switch (
                monthlyStatus
                ) {

            case SUPPORTIVE ->
                    "현재 월의 WEALTH 영역에서 SUPPORTIVE 상태가 확인됩니다.";

            case PRESSURE ->
                    "현재 월의 WEALTH 영역에서 PRESSURE 상태가 확인됩니다.";

            case MIXED ->
                    "현재 월의 WEALTH 영역에서 MIXED 상태가 확인됩니다.";

            case CONTEXTUAL ->
                    "현재 월의 WEALTH 영역에 Context가 존재하며 방향은 CONTEXTUAL로 유지됩니다.";
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