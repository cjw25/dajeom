package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
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
 * WEALTH 재성 수용력 deterministic Rule.
 *
 * =================================================
 * 공식 Source
 * =================================================
 *
 * WEALTH_CAPACITY
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * LifeDomain의 공식 Source Match 하나를
 * Category deterministic Match 하나로
 * 1:1 보존한다.
 *
 * 같은 WEALTH_CAPACITY Rule에서
 * 여러 독립 Match가 존재하더라도:
 *
 * - 상태를 합산하지 않는다.
 * - 개수를 점수화하지 않는다.
 * - SUPPORTIVE + PRESSURE를
 *   새 MIXED로 만들지 않는다.
 * - summary를 합쳐 새 의미를 만들지 않는다.
 *
 * upstream Java가 실제 계산한
 * 각 Match의 status와 의미 경계를
 * 그대로 보존한다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 독립 Match 사이에:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 있으므로 강화된다
 * - A가 B를 약화한다
 * - A와 B가 상쇄된다
 * - A + B = 새로운 재물 상태
 *
 * 같은 의미를 생성하지 않는다.
 *
 * upstream에서 실제 계산된 관계 Pattern은
 * 해당 Source Match 자체의 계산 결과로 유지한다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 재산 증가
 *
 * PRESSURE
 * != 금전 손실
 *
 * MIXED
 * != 재물 기복
 *
 * CONTEXTUAL
 * != 재물운 부재
 */
@Component
public class WealthCapacityDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_CAPACITY_STRUCTURE";

    private static final String SOURCE_RULE_CODE =
            "WEALTH_CAPACITY";

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
                "WealthCapacityDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthCapacityDeterministicRule은 WEALTH View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        /*
         * =================================================
         * Source Match 1개 -> Category Match 1개
         * =================================================
         */

        for (
                LifeDomainRuleMatch sourceMatch
                : view.natalMatches()
        ) {

            LifeDomainRuleMatch source =
                    Objects.requireNonNull(
                            sourceMatch,
                            "WealthCapacityDeterministicRule의 Natal Source Match는 null일 수 없습니다."
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
                        "WealthCapacityDeterministicRule Source Domain이 일치하지 않습니다. "
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
                    "공식 Source Match ["
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
                    "같은 Source Family의 다른 독립 Match와 status·개수·summary를 합산하지 않았습니다."
            );

            reasons.add(
                    "독립 Evidence 사이에 새로운 인과·순서·강화·약화·상쇄·복합 의미를 생성하지 않았습니다."
            );

            reasons.add(
                    "upstream summary: "
                            + source.summary()
            );

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.WEALTH,

                            ConsultationCategoryDeterministicLayer.NATAL,

                            RULE_CODE,

                            status,

                            null,

                            null,

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
                "WEALTH Capacity Source Match가 필요합니다."
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