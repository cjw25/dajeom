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
 * PARTNER_LUCK 원국
 * Primary Wealth / Officer Structure
 * deterministic Rule.
 *
 * =================================================
 * 공식 Source
 * =================================================
 *
 * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
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
 * 같은 Source Family 안에
 * 여러 독립 Match가 존재하더라도:
 *
 * - 대표 status를 만들지 않는다.
 * - SUPPORTIVE + PRESSURE를
 *   새로운 MIXED로 만들지 않는다.
 * - Match 개수를 점수화하지 않는다.
 * - 여러 summary를 합쳐
 *   새로운 배우자 관계 의미를 만들지 않는다.
 *
 * =================================================
 * 실제 Java 계산 유지
 * =================================================
 *
 * RelationshipPrimaryWealthOfficerStructureRule에서
 * StructureAnalysis를 이용하여 실제 계산한:
 *
 * - Primary Wealth
 * - Primary Officer
 * - 주격 상태
 * - 부담
 * - 손상
 * - 혼잡
 *
 * 등의 deterministic 결과는
 * 각각의 Source Match 범위에서 그대로 유지한다.
 *
 * =================================================
 * 관계 계산과 관계형 풀이의 구분
 * =================================================
 *
 * Java가 실제 계산한 하나의 Source Match 내부 관계는
 * 그대로 사용할 수 있다.
 *
 * 하지만 서로 다른 Source Match끼리:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 결합되어 C가 된다
 * - 먼저 A 이후 B
 * - A가 B를 강화한다
 * - A가 B를 약화한다
 * - A와 B가 상쇄된다
 * - 책임이 반복될수록 변한다
 * - 요구가 커질수록 압박이 커진다
 *
 * 같은 추가 관계형 의미를 생성하지 않는다.
 *
 * =================================================
 * 다른 Source와 분리
 * =================================================
 *
 * 다음 Source와 결합하지 않는다.
 *
 * - RELATIONSHIP_NATAL_RELATION_OVERLAP
 * - RELATIONSHIP_TEN_GOD_CONTEXT
 * - RELATIONSHIP_SPOUSE_PALACE
 * - RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY
 * - RELATIONSHIP_DECADE_CONTEXT
 * - RELATIONSHIP_ANNUAL_CONTEXT
 * - RELATIONSHIP_MONTHLY_CONTEXT
 * - RELATIONSHIP_MONTHLY_FUNCTIONAL
 *
 * 각각 독립적인 deterministic 결과다.
 *
 * =================================================
 * 행동 추론 금지
 * =================================================
 *
 * 이 Match만으로:
 *
 * - 배우자에게 책임을 요구한다
 * - 배우자의 요구가 많다
 * - 상대를 통제한다
 * - 관계 책임을 혼자 진다
 * - 상대에게 부담을 느낀다
 * - 장기 관계를 선호한다
 *
 * 같은 행동 특성을 새로 생성하지 않는다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 좋은 배우자를 만난다
 *
 * PRESSURE
 * != 배우자 때문에 힘들다
 *
 * MIXED
 * != 결혼생활이 불안정하다
 *
 * CONTEXTUAL
 * != 배우자운이 없다
 *
 * 어떤 Status도:
 *
 * - 결혼
 * - 이별
 * - 재회
 * - 배우자 등장
 * - 배우자 갈등
 * - 외도
 * - 이혼
 *
 * 같은 실제 사건으로 변환하지 않는다.
 */
@Component
public class PartnerLuckPrimaryWealthOfficerStructureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_PRIMARY_WEALTH_OFFICER_STRUCTURE";

    private static final String SOURCE_RULE_CODE =
            "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE";

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
                "PartnerLuckPrimaryWealthOfficerStructureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.PARTNER_LUCK
        ) {

            throw new IllegalArgumentException(
                    "PartnerLuckPrimaryWealthOfficerStructureRule은 PARTNER_LUCK View만 평가할 수 있습니다."
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
                            "PARTNER_LUCK Primary Wealth / Officer Source Match는 null일 수 없습니다."
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
                            != LifeDomain.RELATIONSHIP
            ) {

                throw new IllegalStateException(
                        "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Domain이 일치하지 않습니다. "
                                + "expected=RELATIONSHIP, actual="
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
                    "같은 RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Family의 다른 Match와 status·개수·summary를 합산하지 않았습니다."
            );

            reasons.add(
                    "RELATIONSHIP_NATAL_RELATION_OVERLAP·RELATIONSHIP_TEN_GOD_CONTEXT·RELATIONSHIP_SPOUSE_PALACE 또는 Timeline Relationship Source를 현재 Match의 상태 계산에 결합하지 않았습니다."
            );

            reasons.add(
                    "독립 Match 사이에 새로운 관계·인과·순서·강화·약화·상쇄·복합 배우자 의미를 생성하지 않았습니다."
            );

            reasons.add(
                    "upstream Java가 현재 Source Match 안에서 실제 계산한 Primary Wealth / Officer Structure 상태는 그대로 보존합니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 재판정 조건으로 사용하지 않았습니다."
            );

            reasons.add(
                    "Source Match 반복 개수를 배우자운·결혼 가능성·관계 지속성 또는 상태 강도의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "이 Match를 결혼·이별·재회·배우자 등장·갈등·외도·이혼 같은 실제 사건으로 변환하지 않습니다."
            );

            reasons.add(
                    "upstream summary: "
                            + source.summary()
            );

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.PARTNER_LUCK,

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
                "PARTNER_LUCK Primary Wealth / Officer Source Match가 필요합니다."
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