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
 * WEALTH 현재 명리연도 재물 Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 현재 Timeline Segment에 실제 존재하는:
 *
 * WEALTH_ANNUAL_CONTEXT
 *
 * 의 공식 상태만
 * ConsultationCategory.WEALTH의
 * 현재 명리연도 deterministic 결과로 보존한다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 이 Rule은 더 이상:
 *
 * WEALTH_DECADE_CONTEXT
 *
 * 를 사용하지 않는다.
 *
 * 대운 재물 Context는 별도의:
 *
 * WealthDecadeContextDeterministicRule
 *
 * 이 독립적으로 담당한다.
 *
 * =================================================
 * 시간층 분리
 * =================================================
 *
 * 대운:
 *
 * WEALTH_DECADE_CONTEXT
 *
 * ->
 *
 * WealthDecadeContextDeterministicRule
 *
 * -------------------------------------------------
 *
 * 세운:
 *
 * WEALTH_ANNUAL_CONTEXT
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 월운:
 *
 * WEALTH_MONTHLY_CONTEXT
 *
 * ->
 *
 * WealthCurrentMonthlyActivationRule
 *
 * -------------------------------------------------
 *
 * Annual Functional:
 *
 * WEALTH_ANNUAL_FUNCTIONAL
 *
 * ->
 *
 * WealthAnnualFunctionalDeterministicRule
 *
 * -------------------------------------------------
 *
 * Monthly Functional:
 *
 * WEALTH_MONTHLY_FUNCTIONAL
 *
 * ->
 *
 * WealthMonthlyFunctionalDeterministicRule
 *
 * =================================================
 * 공식 Status Anchor
 * =================================================
 *
 * 이 Rule의 Status는 오직:
 *
 * WEALTH_ANNUAL_CONTEXT
 *
 * Source Family만 사용한다.
 *
 * =================================================
 * 대운 사용 금지
 * =================================================
 *
 * 다음을 하지 않는다.
 *
 * - WEALTH_DECADE_CONTEXT 조회
 * - 대운 Status와 세운 Status 결합
 * - 대운 Source를 sourceMatches에 포함
 * - 대운을 세운의 보조 근거로 사용
 * - 대운이 세운을 강화한다고 판단
 * - 대운이 세운을 약화한다고 판단
 * - 대운이 세운을 뒤집는다고 판단
 *
 * =================================================
 * Annual Source Family Status
 * =================================================
 *
 * 동일한 WEALTH_ANNUAL_CONTEXT Source Family 내부에서:
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
 * 위 정규화는:
 *
 * 대운과 세운을 합성하는 계산이 아니다.
 *
 * 동일한 WEALTH_ANNUAL_CONTEXT Source Family 안에서
 * upstream이 이미 확정한 공식 상태만
 * Category 수준으로 보존하는 것이다.
 *
 * =================================================
 * Annual Functional과의 분리
 * =================================================
 *
 * WEALTH_ANNUAL_FUNCTIONAL은
 * 이 Rule에서 사용하지 않는다.
 *
 * 일반 Annual Context와
 * Annual Functional은 같은 명리연도의
 * 연결된 provenance이지만
 * 서로 다른 계산 결과다.
 *
 * 따라서 이 Rule에서:
 *
 * ANNUAL_CONTEXT
 * +
 * ANNUAL_FUNCTIONAL
 *
 * 을 합성하지 않는다.
 *
 * =================================================
 * Monthly와의 분리
 * =================================================
 *
 * 다음 Source도 사용하지 않는다.
 *
 * WEALTH_MONTHLY_CONTEXT
 * WEALTH_MONTHLY_FUNCTIONAL
 *
 * 세운과 월운 사이의
 * 강화·약화·상쇄·충돌·전환을
 * 이 Rule에서 생성하지 않는다.
 *
 * =================================================
 * 원국과의 분리
 * =================================================
 *
 * 다음 원국 Source도 사용하지 않는다.
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 * WEALTH_CAPACITY
 * WEALTH_CROSS_VALIDATED_PATTERN
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
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
 * - 대운 때문에 올해 재물이 바뀐다
 * - 원국 재성 구조 때문에 세운이 유리하다
 * - 연간 재물 흐름이 월운으로 이어진다
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
 * != 수입 증가
 * != 돈이 들어온다
 * != 투자 성공
 *
 * PRESSURE
 * != 손실
 * != 돈이 나간다
 * != 투자 실패
 *
 * MIXED
 * != 재물 기복
 *
 * CONTEXTUAL
 * != 재물운이 없다
 *
 * 또한:
 *
 * - 매출 증가
 * - 급여 증가
 * - 투자 성공
 * - 투자 손실
 * - 자산 증가
 * - 부채
 * - 횡재
 *
 * 같은 실제 사건을 생성하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 가능한 Segment가 여러 개라면
 * 각 Segment를 독립적으로 보존한다.
 *
 * 특정 Segment 하나를
 * 대표값으로 임의 선택하지 않는다.
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
 * monthIndex는 Annual Context를
 * 월운 결과로 바꾸는 의미가 아니다.
 *
 * monthIndex == 0도
 * 기존 Timeline 계산의 유효 값으로 취급한다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Annual Match 반복 개수를:
 *
 * - 재물운 강도
 * - 재물 규모
 * - 수입 규모
 * - 자산 규모
 * - 성공 가능성
 *
 * 으로 사용하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 대운 조회
 * - 월운 조회
 * - Annual Functional 조회
 * - 원국 재성 구조 조회
 * - 재성 재계산
 * - 십신 재계산
 * - 신강신약 재계산
 * - 격국 재계산
 * - 용신 재계산
 * - 합충형파해 재계산
 * - 합화 재계산
 * - 대운 × 세운 Status 합성
 * - 원국 × 세운 Status 합성
 * - 세운 × 월운 Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 재물 사건 생성
 */
@Component
public class WealthCurrentStructuralFlowRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_CURRENT_STRUCTURAL_FLOW";

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
                            "WEALTH Annual Context Segment는 null일 수 없습니다."
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
             * 해당 명리연도의 공식 재물 Context가 없으면
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
                    "현재 명리연도 WEALTH Status는 WEALTH_ANNUAL_CONTEXT Source Family만 공식 Status Anchor로 사용했습니다."
            );

            appendAnnualStatusReason(
                    reasons,
                    annualMatches,
                    annualStatus
            );

            /*
             * =================================================
             * 시간층 분리
             * =================================================
             */

            reasons.add(
                    "WEALTH_DECADE_CONTEXT는 이 Rule에서 조회하거나 sourceMatches에 포함하지 않았으며 별도의 WealthDecadeContextDeterministicRule이 독립적으로 담당합니다."
            );

            reasons.add(
                    "WEALTH_ANNUAL_FUNCTIONAL은 이 Rule에 합성하지 않았으며 별도의 WealthAnnualFunctionalDeterministicRule이 독립적으로 담당합니다."
            );

            reasons.add(
                    "WEALTH_MONTHLY_CONTEXT와 WEALTH_MONTHLY_FUNCTIONAL도 이 Rule에 사용하지 않았습니다."
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
                        "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 WEALTH Annual Context 경로로 확정하지 않습니다."
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
                    "서로 다른 시간층 또는 Source Family 사이의 강화·약화·충돌·보완·전환 관계를 새로 생성하지 않았습니다."
            );

            reasons.add(
                    "Annual Source Match의 반복 개수를 재물운 강도·수입 규모·자산 규모·성공 가능성·손실 위험의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
            );

            reasons.add(
                    "monthIndex는 현재 Timeline Segment의 좌표 보존용이며 Annual Context를 Monthly 결과로 재해석하지 않습니다."
            );

            reasons.add(
                    "이 결과는 현재 명리연도의 재물영역 deterministic 상태만 보존하며 실제 수입·지출·투자·손실·자산 증감 사건을 생성하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             *
             * 중요:
             *
             * sourceMatches에는
             * WEALTH_ANNUAL_CONTEXT만 들어간다.
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.WEALTH,

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
                        "WEALTH Annual Context sourceRuleCode가 필요합니다."
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

            if (
                    normalized.domain()
                            != LifeDomain.WEALTH
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
                "WEALTH Annual Match 목록이 필요합니다."
        );

        if (
                annualMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "WEALTH Annual Status 계산에는 최소 하나의 Annual Match가 필요합니다."
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
         * 양방향 상태가 모두 실제 존재
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
                "WEALTH Annual Match 목록이 필요합니다."
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
                "WEALTH reasons가 필요합니다."
        );

        Objects.requireNonNull(
                annualMatches,
                "WEALTH Annual Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                annualStatus,
                "WEALTH Annual Status가 필요합니다."
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
                    "WEALTH_ANNUAL_CONTEXT Source Family에 upstream MIXED 상태가 존재하므로 현재 명리연도 WEALTH Status를 MIXED로 보존합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "WEALTH_ANNUAL_CONTEXT Source Family 내부에 SUPPORTIVE와 PRESSURE가 모두 실제 존재하므로 어느 한 방향을 삭제하지 않고 MIXED로 보존합니다."
            );

            return;
        }

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "WEALTH_ANNUAL_CONTEXT Source Family에 PRESSURE 상태가 존재하고 SUPPORTIVE 또는 MIXED 상태가 없어 현재 명리연도 WEALTH Status를 PRESSURE로 확정합니다."
            );

            return;
        }

        if (
                annualStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "WEALTH_ANNUAL_CONTEXT Source Family에 SUPPORTIVE 상태가 존재하고 PRESSURE 또는 MIXED 상태가 없어 현재 명리연도 WEALTH Status를 SUPPORTIVE로 확정합니다."
            );

            return;
        }

        reasons.add(
                "WEALTH_ANNUAL_CONTEXT Source Family는 존재하지만 SUPPORTIVE·PRESSURE·MIXED 방향이 확정되지 않아 현재 명리연도 WEALTH Status를 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * 다른 시간층과 연결하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus annualStatus
    ) {

        Objects.requireNonNull(
                annualStatus,
                "WEALTH Annual Status가 필요합니다."
        );

        return switch (
                annualStatus
                ) {

            case SUPPORTIVE ->
                    "현재 명리연도의 WEALTH Context 계산 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "현재 명리연도의 WEALTH Context 계산 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "현재 명리연도의 WEALTH Context 계산 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "현재 명리연도의 WEALTH Context 계산 결과가 CONTEXTUAL 상태로 보존됩니다.";
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