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
 * SOCIAL_RELATIONSHIP 현재 대운 Context
 * deterministic Rule.
 *
 * 공식 Source:
 *
 * RELATIONSHIP_DECADE_CONTEXT
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * 현재 Segment의
 * LifeDomainRuleMatch 1개
 * ->
 * Category Match 1개
 *
 * 로 1:1 보존한다.
 *
 * 같은 대운 Source Family 안에
 * 여러 Match가 존재하더라도:
 *
 * - 대표 상태를 만들지 않는다.
 * - SUPPORTIVE + PRESSURE를 새 MIXED로 합치지 않는다.
 * - Match 개수를 관계 강도로 환산하지 않는다.
 *
 * 세운·월운·월운 Functional 결과와
 * 결합하지 않는다.
 *
 * 실제 upstream Java 관계 계산은 유지하고
 * 독립 Match끼리 관계형 풀이로 재조합하지 않는다.
 */
@Component
public class SocialRelationshipCurrentDecadeContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_CURRENT_DECADE_CONTEXT";

    private static final String SOURCE_RULE_CODE =
            "RELATIONSHIP_DECADE_CONTEXT";

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
                "SocialRelationshipCurrentDecadeContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.SOCIAL_RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "SocialRelationshipCurrentDecadeContextRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
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

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segment,
                            "SOCIAL_RELATIONSHIP Current Timeline Segment는 null일 수 없습니다."
                    );

            for (
                    LifeDomainRuleMatch source
                    : normalizedSegment.matches()
            ) {

                Objects.requireNonNull(
                        source,
                        "SOCIAL_RELATIONSHIP Segment Source Match는 null일 수 없습니다."
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
                        "같은 Segment의 다른 독립 Decade Source Match와 status·개수·summary를 합산하지 않았습니다."
                );

                reasons.add(
                        "세운·월운·월운 Functional 결과와 결합하여 새로운 인과·순서·강화·약화·상쇄·복합 의미를 만들지 않았습니다."
                );

                reasons.add(
                        "upstream Java가 실제 계산한 관계 결과는 현재 Source Match 범위에서 그대로 보존합니다."
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
                            "현재 Segment가 완전히 확정되지 않았으므로 동일 연·월 좌표의 다른 가능한 Segment가 있으면 각각 별도 결과로 보존합니다."
                    );
                }

                result.add(
                        new ConsultationCategoryDeterministicMatch(
                                ConsultationCategory.SOCIAL_RELATIONSHIP,

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

    private ConsultationCategoryDeterministicStatus
    mapStatus(
            LifeDomainRuleMatch source
    ) {

        Objects.requireNonNull(
                source,
                "SOCIAL_RELATIONSHIP Decade Source Match가 필요합니다."
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