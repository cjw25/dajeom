package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * WEALTH 현재 월운 Monthly Context
 * deterministic Rule.
 *
 * =================================================
 * 공식 Source
 * =================================================
 *
 * WEALTH_MONTHLY_CONTEXT
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * 현재 Timeline Segment 안의
 * 공식 LifeDomain Source Match 하나를
 * Category deterministic Match 하나로
 * 1:1 보존한다.
 *
 * 같은 Segment 안에
 * WEALTH_MONTHLY_CONTEXT Match가
 * 여러 개 존재하더라도:
 *
 * - 대표 status를 만들지 않는다.
 * - SUPPORTIVE + PRESSURE를
 *   새 MIXED로 만들지 않는다.
 * - Match 개수를 점수화하지 않는다.
 * - 서로 다른 Monthly Signal의 의미를
 *   다시 하나로 합치지 않는다.
 *
 * =================================================
 * 시간층 독립성
 * =================================================
 *
 * 다음 Source와 결합하지 않는다.
 *
 * - WEALTH_DECADE_CONTEXT
 * - WEALTH_ANNUAL_CONTEXT
 * - WEALTH_ANNUAL_FUNCTIONAL
 * - WEALTH_MONTHLY_FUNCTIONAL
 * - 원국 WEALTH Source
 *
 * 월운 Context는 월운 Context 자체로만
 * 독립 보존한다.
 *
 * =================================================
 * 관계 계산과 관계형 풀이의 구분
 * =================================================
 *
 * upstream MonthlyLuckContextSignal 안에
 * 실제 Java 계산으로 존재하는:
 *
 * - 관계
 * - 합화
 * - 다층 관계
 * - 격국 Context
 * - 용신 Context
 *
 * 등은 그대로 유지한다.
 *
 * 다만 서로 다른 Source Match끼리
 * 공존한다는 이유만으로:
 *
 * - 인과
 * - 강화
 * - 약화
 * - 상쇄
 * - 순서
 * - 새로운 복합 상태
 *
 * 를 생성하지 않는다.
 */
@Component
public class WealthCurrentMonthlyContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_CURRENT_MONTHLY_CONTEXT";

    private static final String SOURCE_RULE_CODE =
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
                "WealthCurrentMonthlyContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthCurrentMonthlyContextRule은 WEALTH View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
                        || !view.hasSegmentEvidences()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        /*
         * =================================================
         * Segment별 독립 계산
         * =================================================
         */

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segment,
                            "WealthCurrentMonthlyContextRule의 Segment Evidence는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * Source Match 1개 -> Category Match 1개
             * =================================================
             */

            for (
                    LifeDomainRuleMatch sourceMatch
                    : normalizedSegment.matches()
            ) {

                LifeDomainRuleMatch source =
                        Objects.requireNonNull(
                                sourceMatch,
                                "WealthCurrentMonthlyContextRule의 Segment Source Match는 null일 수 없습니다."
                        );

                if (
                        !SOURCE_RULE_CODE.equals(
                                source.ruleCode()
                        )
                ) {

                    continue;
                }

                if (
                        source.domain()
                                != LifeDomain.WEALTH
                ) {

                    throw new IllegalStateException(
                            "WealthCurrentMonthlyContextRule Source Domain이 일치하지 않습니다. "
                                    + "expected=WEALTH, actual="
                                    + source.domain()
                    );
                }

                ConsultationCategoryDeterministicStatus status =
                        mapStatus(
                                source
                        );

                List<String> reasons =
                        new ArrayList<>();

                reasons.add(
                        "현재 Segment의 공식 Source Match ["
                                + SOURCE_RULE_CODE
                                + "] 하나를 1:1로 보존했습니다."
                );

                reasons.add(
                        "upstream LifeDomain status="
                                + source.status().name()
                                + "을 Category status="
                                + status.name()
                                + "으로 직접 변환했습니다."
                );

                reasons.add(
                        "같은 Segment의 다른 독립 Source Match와 status·개수·summary를 합산하지 않았습니다."
                );

                reasons.add(
                        "다른 시간층 또는 Source Family와 새로운 인과·순서·강화·약화·상쇄·복합 의미를 생성하지 않았습니다."
                );

                reasons.add(
                        "upstream summary: "
                                + source.summary()
                );

                if (
                        normalizedSegment
                                .timelineUncertaintyExists()
                ) {

                    reasons.add(
                            "현재 Timeline에 불확실성이 있으므로 가능한 Segment를 임의로 하나만 선택하지 않습니다."
                    );
                }

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

                                "["
                                        + SOURCE_RULE_CODE
                                        + "] 독립 Match를 보존합니다: "
                                        + source.summary(),

                                List.of(
                                        source
                                ),

                                List.copyOf(
                                        reasons
                                )
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
     * Status 1:1 변환
     * =================================================
     */

    private ConsultationCategoryDeterministicStatus
    mapStatus(
            LifeDomainRuleMatch source
    ) {

        Objects.requireNonNull(
                source,
                "WEALTH Monthly Context Source Match가 필요합니다."
        );

        return switch (
                source.status()
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
}