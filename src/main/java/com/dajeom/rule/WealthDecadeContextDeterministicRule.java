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
 * WEALTH 현재 대운 Context deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.WEALTH에서 이미 계산된:
 *
 * WEALTH_DECADE_CONTEXT
 *
 * 결과를 ConsultationCategory.WEALTH의
 * 독립적인 CURRENT_TIMELINE 결과로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * WealthDecadeContextRule은 이미:
 *
 * - DIRECT_WEALTH
 * - INDIRECT_WEALTH
 * - 대운 TenGod Context
 * - 대운 UsefulGod Context
 * - 대운 Structure Context
 * - 대운 Relation Context
 * - 대운 Transformation Context
 *
 * 중 재성과 직접 연결된 deterministic Signal을
 * 사용하여 LifeDomainSignalStatus를 계산한다.
 *
 * 이 Category Rule에서는
 * 위 계산을 다시 수행하지 않는다.
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
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 세운:
 *
 * WEALTH_ANNUAL_CONTEXT
 *
 * ->
 *
 * WealthCurrentStructuralFlowRule
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
 * 세 시간층을 하나의 Status로 합성하지 않는다.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * LifeDomainRuleMatch 하나를
 *
 * ->
 *
 * ConsultationCategoryDeterministicMatch 하나로
 *
 * 그대로 보존한다.
 *
 * 여러 Decade Context Match가 존재하면
 * 각각 별도 결과로 유지한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 해당 WEALTH_DECADE_CONTEXT
 * LifeDomainRuleMatch의 공식 Status를
 * 그대로 1:1 변환한다.
 *
 * SUPPORTIVE
 * -> SUPPORTIVE
 *
 * PRESSURE
 * -> PRESSURE
 *
 * MIXED
 * -> MIXED
 *
 * CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 * 세운과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * WEALTH_ANNUAL_CONTEXT
 *
 * 를 사용하지 않는다.
 *
 * 따라서:
 *
 * DECADE SUPPORTIVE
 * +
 * ANNUAL PRESSURE
 *
 * 를 보고
 *
 * MIXED
 *
 * 를 생성하지 않는다.
 *
 * =================================================
 * 월운과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * WEALTH_MONTHLY_CONTEXT
 * WEALTH_MONTHLY_FUNCTIONAL
 *
 * 을 사용하지 않는다.
 *
 * 대운 Status와 월운 Status 사이의
 * 강화·약화·충돌·보완을 생성하지 않는다.
 *
 * =================================================
 * 원국과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 * WEALTH_CAPACITY
 * WEALTH_CROSS_VALIDATED_PATTERN
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
 *
 * 를 사용하지 않는다.
 *
 * 원국 구조와 대운 Context를 결합하여
 * 새로운 Category Status를 만들지 않는다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 다음을 새로 생성하지 않는다.
 *
 * - 원국 재성 구조 때문에 대운이 유리하다
 * - 대운이 원국 재성을 강화한다
 * - 대운의 재물 기반 위에서 세운이 작동한다
 * - 대운 때문에 앞으로 돈의 흐름이 바뀐다
 * - 대운에서 먼저 A가 되고 이후 세운에서 B가 된다
 * - A 때문에 B
 * - A가 B로 이어진다
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 장기간 돈을 많이 번다
 *
 * PRESSURE
 * != 장기간 돈을 잃는다
 *
 * MIXED
 * != 재물 기복이 심하다
 *
 * CONTEXTUAL
 * != 재물운이 없다
 *
 * 또한:
 *
 * - 사업 성공
 * - 사업 실패
 * - 자산 증가
 * - 부동산 취득
 * - 투자 성공
 * - 투자 손실
 * - 부채
 * - 횡재
 *
 * 같은 실제 사건을 생성하지 않는다.
 *
 * =================================================
 * Timeline 불확실성
 * =================================================
 *
 * 현재 Timeline에서 가능한 Segment가
 * 둘 이상 존재한다면
 * 하나를 임의 선택하지 않는다.
 *
 * 각 Segment에서 실제 존재하는
 * WEALTH_DECADE_CONTEXT 결과를
 * 각각 보존한다.
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
 * 좌표를 보존한다.
 *
 * monthIndex는 대운의 월 단위 의미가 아니라
 * 현재 분석 Segment의 좌표다.
 *
 * monthIndex == 0도
 * 기존 Timeline 계산의 유효 값으로 보존한다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Decade Context Match 개수를:
 *
 * - 대운 재물운 강도
 * - 자산 규모
 * - 돈의 양
 * - 성공 가능성
 *
 * 으로 사용하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 대운 재계산
 * - 재성 재계산
 * - 십신 재계산
 * - 격국 재계산
 * - 신강신약 재계산
 * - 용신 재계산
 * - 합충형파해 재계산
 * - 합화 재계산
 * - 원국 × 대운 Status 합성
 * - 대운 × 세운 Status 합성
 * - 대운 × 월운 Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 재물 사건 생성
 */
@Component
public class WealthDecadeContextDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_CURRENT_DECADE_CONTEXT";

    private static final String
            DECADE_CONTEXT_RULE_CODE =
            "WEALTH_DECADE_CONTEXT";


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
                "WealthDecadeContextDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "WealthDecadeContextDeterministicRule은 WEALTH View만 평가할 수 있습니다."
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
         * Segment별 독립 처리
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segment,
                            "WEALTH Decade Context Segment는 null일 수 없습니다."
                    );

            List<LifeDomainRuleMatch> decadeMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            DECADE_CONTEXT_RULE_CODE
                    );

            /*
             * 실제 대운 재물 Context가 없는 Segment는
             * 아무 Match도 생성하지 않는다.
             */

            if (
                    decadeMatches.isEmpty()
            ) {

                continue;
            }

            /*
             * =================================================
             * Match 1개 -> Category Match 1개
             * =================================================
             */

            for (
                    LifeDomainRuleMatch decadeMatch
                    : decadeMatches
            ) {

                LifeDomainRuleMatch normalizedMatch =
                        Objects.requireNonNull(
                                decadeMatch,
                                "WEALTH Decade Context Match는 null일 수 없습니다."
                        );

                ConsultationCategoryDeterministicStatus status =
                        toCategoryStatus(
                                normalizedMatch.status()
                        );

                List<String> reasons =
                        buildReasons(
                                normalizedSegment
                        );

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.WEALTH,

                                ConsultationCategoryDeterministicLayer
                                        .CURRENT_TIMELINE,

                                RULE_CODE,

                                status,

                                normalizedSegment
                                        .sajuYear(),

                                normalizedSegment
                                        .monthIndex(),

                                List.of(),

                                summaryOf(
                                        status
                                ),

                                List.of(
                                        normalizedMatch
                                ),

                                reasons
                        )
                );
            }
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
                        "WEALTH Decade Context sourceRuleCode가 필요합니다."
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
     * Status 1:1 변환
     * =================================================
     *
     * 다른 Source 또는 다른 시간층을
     * 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "WEALTH Decade Context에는 LifeDomainSignalStatus가 필요합니다."
        );

        return switch (
                sourceStatus
                ) {

            case SUPPORTIVE ->
                    ConsultationCategoryDeterministicStatus
                            .SUPPORTIVE;

            case PRESSURE ->
                    ConsultationCategoryDeterministicStatus
                            .PRESSURE;

            case MIXED ->
                    ConsultationCategoryDeterministicStatus
                            .MIXED;

            case CONTEXTUAL ->
                    ConsultationCategoryDeterministicStatus
                            .CONTEXTUAL;
        };
    }


    /*
     * =================================================
     * Reasons
     * =================================================
     */

    private List<String> buildReasons(
            ConsultationCategorySegmentEvidence segment
    ) {

        Objects.requireNonNull(
                segment,
                "WEALTH Decade Context Segment가 필요합니다."
        );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "WEALTH_DECADE_CONTEXT에서 이미 계산된 하나의 LifeDomain Match를 독립적인 WEALTH Category Match로 보존했습니다."
        );

        reasons.add(
                "해당 LifeDomainRuleMatch의 공식 Status를 다른 WEALTH Source와 결합하지 않고 1:1로 Category Status에 보존했습니다."
        );

        reasons.add(
                "WEALTH_ANNUAL_CONTEXT와 WEALTH_MONTHLY_CONTEXT를 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
        );

        reasons.add(
                "원국 WEALTH_PRIMARY_WEALTH_STRUCTURE, WEALTH_CAPACITY, WEALTH_CROSS_VALIDATED_PATTERN과도 Status를 합성하지 않았습니다."
        );

        reasons.add(
                "대운 Context와 세운·월운·원국 결과 사이의 새로운 강화·약화·충돌·보완 관계를 생성하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과를 장기 수입 증가·자산 증가·투자 성공·손실 같은 실제 재물 사건으로 변환하지 않습니다."
        );

        if (
                segment.timelineUncertaintyExists()
        ) {

            reasons.add(
                    "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 WEALTH Decade Context 경로로 확정하지 않습니다."
            );
        }

        if (
                !segment.fullyDefinite()
        ) {

            reasons.add(
                    "현재 Segment가 완전히 확정되지 않았으므로 다른 가능한 Segment가 있으면 각각 별도 결과로 보존합니다."
            );
        }

        reasons.add(
                "sajuYear와 monthIndex는 현재 Timeline Segment의 좌표이며 이를 대운 내부의 연·월 진행 순서로 재해석하지 않습니다."
        );

        return List.copyOf(
                reasons
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "WEALTH Decade Context Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "현재 대운의 WEALTH Context 계산 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "현재 대운의 WEALTH Context 계산 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "현재 대운의 WEALTH Context 계산 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "현재 대운의 WEALTH Context 계산 결과가 CONTEXTUAL 상태로 보존됩니다.";
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