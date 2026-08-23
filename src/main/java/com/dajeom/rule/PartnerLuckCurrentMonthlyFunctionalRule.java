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
 * PARTNER_LUCK 현재 월 Functional deterministic Rule.
 *
 * =================================================
 * 공식 Source
 * =================================================
 *
 * RELATIONSHIP_MONTHLY_FUNCTIONAL
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * RELATIONSHIP_MONTHLY_FUNCTIONAL은
 * 하나의 Monthly Functional Evidence에서:
 *
 * - RESOURCE_REALITY
 * - RESPONSIBILITY
 *
 * 같은 독립 Function Match를
 * 각각 생성할 수 있다.
 *
 * 따라서:
 *
 * LifeDomainRuleMatch 1개
 * ->
 * Category Deterministic Match 1개
 *
 * 로 1:1 보존한다.
 *
 * =================================================
 * 금지
 * =================================================
 *
 * 같은 Source Family 안에
 * 여러 Functional Match가 존재하더라도:
 *
 * - 상태를 합산하지 않는다.
 * - SUPPORTIVE + PRESSURE를
 *   새 MIXED로 만들지 않는다.
 * - Match 개수를 점수화하지 않는다.
 * - RESOURCE_REALITY + RESPONSIBILITY를
 *   새 관계 의미로 합성하지 않는다.
 *
 * =================================================
 * 관계 계산과 관계형 풀이의 구분
 * =================================================
 *
 * upstream Java가 각 Function 내부에서
 * 실제 계산한:
 *
 * - annual baseline
 * - monthly contact
 * - activation status
 * - mixed monthly contact
 * - same-direction accumulation
 *
 * 등의 계산 결과는 그대로 유지한다.
 *
 * 단, 서로 다른 Function Match끼리:
 *
 * - A 때문에 B
 * - A가 B를 강화한다
 * - A가 B를 약화한다
 * - A와 B가 상쇄된다
 * - A + B = 새로운 배우자운 상태
 *
 * 같은 추가 관계형 풀이를 생성하지 않는다.
 *
 * =================================================
 * 시간층 독립성
 * =================================================
 *
 * 다음 Source와 결합하지 않는다.
 *
 * - RELATIONSHIP_DECADE_CONTEXT
 * - RELATIONSHIP_ANNUAL_CONTEXT
 * - RELATIONSHIP_MONTHLY_CONTEXT
 *
 * Monthly Functional은
 * Monthly Functional 자체로만 보존한다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * 이 결과를:
 *
 * - 배우자 등장
 * - 연애 시작
 * - 결혼
 * - 이별
 * - 재회
 *
 * 같은 실제 사건으로 변환하지 않는다.
 */
@Component
public class PartnerLuckCurrentMonthlyFunctionalRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_CURRENT_MONTHLY_FUNCTIONAL";

    private static final String SOURCE_RULE_CODE =
            "RELATIONSHIP_MONTHLY_FUNCTIONAL";

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
                "PartnerLuckCurrentMonthlyFunctionalRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.PARTNER_LUCK
        ) {

            throw new IllegalArgumentException(
                    "PartnerLuckCurrentMonthlyFunctionalRule은 PARTNER_LUCK View만 평가할 수 있습니다."
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
                            "PARTNER_LUCK Monthly Functional Segment는 null일 수 없습니다."
                    );

            /*
             * =================================================
             * Source Match 1개 -> Category Match 1개
             * =================================================
             */

            for (
                    LifeDomainRuleMatch source
                    : normalizedSegment.matches()
            ) {

                Objects.requireNonNull(
                        source,
                        "PARTNER_LUCK Monthly Functional Source Match는 null일 수 없습니다."
                );

                if (
                        source.domain()
                                != LifeDomain.RELATIONSHIP
                ) {

                    continue;
                }

                if (
                        !SOURCE_RULE_CODE.equals(
                                source.ruleCode()
                        )
                ) {

                    continue;
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
                        "같은 Segment의 다른 RELATIONSHIP_MONTHLY_FUNCTIONAL Match와 status·개수·summary를 합산하지 않았습니다."
                );

                reasons.add(
                        "RESOURCE_REALITY와 RESPONSIBILITY 같은 독립 Function Match를 결합하여 새로운 PARTNER_LUCK 상태를 만들지 않았습니다."
                );

                reasons.add(
                        "Monthly Context·Annual Context·Decade Context Source를 이 Monthly Functional Match에 결합하지 않았습니다."
                );

                reasons.add(
                        "독립 Match 사이에 새로운 인과·순서·강화·약화·상쇄·전환·복합 의미를 생성하지 않았습니다."
                );

                reasons.add(
                        "upstream Java가 현재 Function Match 안에서 실제 계산한 Monthly Functional 결과는 그대로 보존합니다."
                );

                reasons.add(
                        "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
                );

                reasons.add(
                        "monthIndex는 기존 Timeline 계산값을 그대로 보존하며 0도 유효한 값으로 취급합니다."
                );

                reasons.add(
                        "이 Match를 배우자 등장·연애 시작·결혼·이별·재회 같은 실제 사건으로 변환하지 않습니다."
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

                if (
                        !normalizedSegment
                                .fullyDefinite()
                ) {

                    reasons.add(
                            "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 있으면 각각 별도 결과로 보존합니다."
                    );
                }

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.PARTNER_LUCK,

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
                                        + "] 독립 Functional Match를 보존합니다: "
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
                "PARTNER_LUCK Monthly Functional Source Match가 필요합니다."
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