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
 * SOCIAL_RELATIONSHIP 원국
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
 * 같은 Source Family에
 * 여러 독립 Match가 존재하더라도:
 *
 * - 대표 status를 만들지 않는다.
 * - SUPPORTIVE + PRESSURE를
 *   새로운 MIXED로 만들지 않는다.
 * - Match 개수를 관계 강도로 사용하지 않는다.
 * - 여러 summary를 합쳐
 *   새로운 대인관계 의미를 만들지 않는다.
 *
 * =================================================
 * 실제 Java 계산 유지
 * =================================================
 *
 * RelationshipPrimaryWealthOfficerStructureRule에서
 * StructureAnalysis를 이용하여 실제 계산된:
 *
 * - Primary Wealth
 * - Primary Officer
 * - 주격 상태
 * - 부담
 * - 손상
 * - 혼잡
 *
 * 등의 결과는
 * 각 Source Match 범위에서 그대로 유지한다.
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 독립 Match끼리:
 *
 * - 기대가 커진다
 * - 책임이 반복된다
 * - 상대에게 부담을 준다
 * - A 때문에 B
 * - A 이후 B
 * - A가 B를 강화한다
 * - A가 B를 약화한다
 * - 두 결과가 합쳐 새로운 관계 성향이 된다
 *
 * 같은 의미를 생성하지 않는다.
 *
 * =================================================
 * 다른 Source와 분리
 * =================================================
 *
 * 다음 Source와 결합하지 않는다.
 *
 * - RELATIONSHIP_NATAL_RELATION_OVERLAP
 * - RELATIONSHIP_DECADE_CONTEXT
 * - RELATIONSHIP_ANNUAL_CONTEXT
 * - RELATIONSHIP_MONTHLY_CONTEXT
 * - RELATIONSHIP_MONTHLY_FUNCTIONAL
 *
 * 각각 독립적인 deterministic 결과다.
 *
 * =================================================
 * 사건·행동 생성 금지
 * =================================================
 *
 * 이 Match만으로:
 *
 * - 사람을 통제한다
 * - 책임을 요구한다
 * - 기대가 많다
 * - 관계가 좋아진다
 * - 갈등이 생긴다
 * - 관계가 단절된다
 *
 * 같은 행동이나 사건을 생성하지 않는다.
 */
@Component
public class SocialRelationshipPrimaryWealthOfficerStructureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE";

    private static final String SOURCE_RULE_CODE =
            "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE";

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.SOCIAL_RELATIONSHIP;
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
                "SocialRelationshipPrimaryWealthOfficerStructureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.SOCIAL_RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "SocialRelationshipPrimaryWealthOfficerStructureRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
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
                            "SOCIAL_RELATIONSHIP Primary Wealth / Officer Source Match는 null일 수 없습니다."
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
                    "RELATIONSHIP_NATAL_RELATION_OVERLAP 또는 Timeline Relationship Source를 현재 Match의 상태 계산에 결합하지 않았습니다."
            );

            reasons.add(
                    "독립 Match 사이에 새로운 관계·인과·순서·강화·약화·상쇄·복합 의미를 생성하지 않았습니다."
            );

            reasons.add(
                    "upstream Java가 현재 Source Match 안에서 실제 계산한 Primary Wealth / Officer Structure 상태는 그대로 보존합니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 재판정 조건으로 사용하지 않았습니다."
            );

            reasons.add(
                    "이 Match를 인간관계 성공·실패·갈등·단절 같은 실제 사건으로 변환하지 않습니다."
            );

            reasons.add(
                    "upstream summary: "
                            + source.summary()
            );

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.SOCIAL_RELATIONSHIP,

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
                "SOCIAL_RELATIONSHIP Primary Wealth / Officer Source Match가 필요합니다."
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