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
 * PARTNER_LUCK 원국 Relation Overlap
 * deterministic Rule.
 *
 * 공식 Source:
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * =================================================
 * 핵심 경계
 * =================================================
 *
 * LifeDomainRuleMatch 하나를
 * PARTNER_LUCK Category Match 하나로
 * 1:1 보존한다.
 *
 * 같은 Source Family 내부의
 * 여러 독립 Match를 다시 합쳐
 * 대표 배우자운 상태를 만들지 않는다.
 *
 * 실제 Java 관계 계산은 유지하고
 * 독립 관계 결과끼리 새 인과·강화·약화·순서·종합 의미를
 * 생성하지 않는다.
 *
 * 이 결과를:
 *
 * - 결혼 가능성
 * - 배우자 등장
 * - 배우자복
 * - 이별
 *
 * 같은 실제 사건으로 자동 변환하지 않는다.
 */
@Component
public class PartnerLuckNatalRelationOverlapRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_NATAL_RELATION_OVERLAP";

    private static final String SOURCE_RULE_CODE =
            "RELATIONSHIP_NATAL_RELATION_OVERLAP";

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
                "PartnerLuckNatalRelationOverlapRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.PARTNER_LUCK
        ) {

            throw new IllegalArgumentException(
                    "PartnerLuckNatalRelationOverlapRule은 PARTNER_LUCK View만 평가할 수 있습니다."
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
                    "PARTNER_LUCK Natal Source Match는 null일 수 없습니다."
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
                    "독립 관계 Match 사이에 새로운 인과·순서·강화·약화·상쇄·배우자 의미를 생성하지 않았습니다."
            );

            reasons.add(
                    "upstream Java가 실제 계산한 관계 결과는 현재 Source Match 범위에서 그대로 보존합니다."
            );

            reasons.add(
                    "이 Match를 결혼·배우자 등장·배우자복·이별 같은 실제 사건으로 변환하지 않습니다."
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

    private ConsultationCategoryDeterministicStatus
    mapStatus(
            LifeDomainRuleMatch source
    ) {

        Objects.requireNonNull(
                source,
                "PARTNER_LUCK Source Match가 필요합니다."
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