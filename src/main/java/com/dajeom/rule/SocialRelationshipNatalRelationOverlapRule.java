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
 * SOCIAL_RELATIONSHIP 원국 Relation Overlap
 * deterministic Rule.
 *
 * 공식 Source:
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * Source Match 1개
 * ->
 * Category Match 1개
 *
 * 로 독립 보존한다.
 *
 * 같은 Source Family 안에 여러 Match가 존재해도:
 *
 * - 대표 status를 계산하지 않는다.
 * - SUPPORTIVE + PRESSURE를 새 MIXED로 만들지 않는다.
 * - Match 개수를 관계 강도로 환산하지 않는다.
 * - 여러 summary를 합쳐 새 사회관계 의미를 만들지 않는다.
 *
 * 실제 Java가 계산한 관계 결과는 유지하지만
 * 서로 다른 독립 Match끼리 관계형으로 다시 풀이하지 않는다.
 */
@Component
public class SocialRelationshipNatalRelationOverlapRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_NATAL_RELATION_OVERLAP";

    private static final String SOURCE_RULE_CODE =
            "RELATIONSHIP_NATAL_RELATION_OVERLAP";

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
                "SocialRelationshipNatalRelationOverlapRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.SOCIAL_RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "SocialRelationshipNatalRelationOverlapRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch source
                : view.natalMatches()
        ) {

            Objects.requireNonNull(
                    source,
                    "SOCIAL_RELATIONSHIP Natal Source Match는 null일 수 없습니다."
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
                    "독립 Match 사이에 새로운 관계·인과·순서·강화·약화·상쇄 의미를 생성하지 않았습니다."
            );

            reasons.add(
                    "upstream Java가 실제 계산한 합·충·형·파·해 등 관계 결과는 현재 Source Match 범위에서 그대로 보존합니다."
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

    private ConsultationCategoryDeterministicStatus
    mapStatus(
            LifeDomainRuleMatch source
    ) {

        Objects.requireNonNull(
                source,
                "SOCIAL_RELATIONSHIP Source Match가 필요합니다."
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