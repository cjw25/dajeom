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
 * WEALTH 원국
 * Primary Wealth Structure
 * deterministic Rule.
 *
 * =================================================
 * 공식 Source
 * =================================================
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * LifeDomainRuleMatch 1개
 * ->
 * ConsultationCategoryDeterministicMatch 1개
 *
 * 로 1:1 보존한다.
 *
 * 같은 Source Family 내부에
 * 여러 Match가 존재하더라도:
 *
 * - status를 합산하지 않는다.
 * - Match 개수를 점수화하지 않는다.
 * - SUPPORTIVE + PRESSURE를
 *   새 MIXED로 만들지 않는다.
 * - 여러 summary를 합쳐
 *   새로운 재물 상태를 만들지 않는다.
 *
 * =================================================
 * upstream 계산 유지
 * =================================================
 *
 * WealthPrimaryWealthStructureRule에서
 * StructureAnalysis를 이용해 이미 계산한:
 *
 * - 실제 주격
 * - DIRECT_WEALTH / INDIRECT_WEALTH
 * - 성립
 * - 부담
 * - 손상
 * - 강한 손상
 * - 혼잡
 *
 * 등의 결과는
 * 현재 Source Match 범위에서 그대로 유지한다.
 *
 * =================================================
 * 독립 Source 분리
 * =================================================
 *
 * 다음 Source와 결합하지 않는다.
 *
 * - WEALTH_CAPACITY
 * - WEALTH_CROSS_VALIDATED_PATTERN
 * - WEALTH_PALACE_DISTRIBUTION_CONTEXT
 * - Timeline WEALTH Context
 *
 * 각 계산은 독립 결과다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 독립 Match 사이에:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A가 B를 강화한다
 * - A가 B를 약화한다
 * - A와 B가 상쇄된다
 * - A + B = 새로운 재물 상태
 *
 * 를 생성하지 않는다.
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
 * != 재물운 없음
 */
@Component
public class WealthPrimaryWealthStructureDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_PRIMARY_WEALTH_STRUCTURE_CONTEXT";

    private static final String SOURCE_RULE_CODE =
            "WEALTH_PRIMARY_WEALTH_STRUCTURE";

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
                "WealthPrimaryWealthStructureDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthPrimaryWealthStructureDeterministicRule은 WEALTH View만 평가할 수 있습니다."
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
                            "WEALTH Primary Wealth Structure Source Match는 null일 수 없습니다."
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
                        "WEALTH_PRIMARY_WEALTH_STRUCTURE Source Domain이 일치하지 않습니다. "
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
                    "같은 WEALTH_PRIMARY_WEALTH_STRUCTURE Source Family의 다른 Match와 status·개수·summary를 합산하지 않았습니다."
            );

            reasons.add(
                    "WEALTH_CAPACITY·WEALTH_CROSS_VALIDATED_PATTERN·WEALTH_PALACE_DISTRIBUTION_CONTEXT를 현재 Match의 상태 계산에 결합하지 않았습니다."
            );

            reasons.add(
                    "독립 Match 사이에 새로운 인과·순서·강화·약화·상쇄·복합 재물 의미를 생성하지 않았습니다."
            );

            reasons.add(
                    "upstream Java가 현재 Source Match 안에서 실제 계산한 재성 주격 구조와 상태는 그대로 보존합니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 재판정 조건으로 사용하지 않았습니다."
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
                "WEALTH Primary Wealth Structure Source Match가 필요합니다."
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