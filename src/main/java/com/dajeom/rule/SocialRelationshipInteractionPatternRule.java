package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
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
 * SOCIAL_RELATIONSHIP 원국 관계망 구조
 * deterministic Rule.
 *
 * =================================================
 * 공식 Source Family
 * =================================================
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * 하나만 사용한다.
 *
 * =================================================
 * 독립성
 * =================================================
 *
 * Palace Context와 Auxiliary Context는
 * 이 Category Conclusion에 결합하지 않는다.
 *
 * 각각 기존 LifeDomain Evidence로
 * 독립 보존된다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 관계 구조 재계산
 * - Palace 결합
 * - Auxiliary 결합
 * - Match 개수 점수화
 * - 관계 단계 생성
 * - 인과 생성
 * - 순서 생성
 * - 사건 생성
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 */
@Component
public class SocialRelationshipInteractionPatternRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_INTERACTION_PATTERN";

    private static final String
            NATAL_RELATION_OVERLAP_RULE_CODE =
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
                "SocialRelationshipInteractionPatternRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.SOCIAL_RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "SocialRelationshipInteractionPatternRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        List<LifeDomainRuleMatch> relationOverlapMatches =
                findNatalMatches(
                        view,
                        NATAL_RELATION_OVERLAP_RULE_CODE
                );

        if (
                relationOverlapMatches.isEmpty()
        ) {

            return List.of();
        }

        ConsultationCategoryDeterministicStatus status =
                resolveStatus(
                        relationOverlapMatches
                );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "SOCIAL_RELATIONSHIP의 원국 관계망 구조는 RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family만 사용했습니다."
        );

        reasons.add(
                statusReason(
                        relationOverlapMatches,
                        status
                )
        );

        reasons.add(
                "Palace Context와 Auxiliary Context는 이 deterministic Match의 Source로 결합하지 않았습니다."
        );

        reasons.add(
                "Source Match의 반복 개수는 관계 강도 또는 Category 강도 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과는 upstream에서 이미 계산된 원국 관계망 구조의 상태만 보존합니다."
        );

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.SOCIAL_RELATIONSHIP,
                        ConsultationCategoryDeterministicLayer.NATAL,
                        RULE_CODE,
                        status,
                        null,
                        null,
                        List.of(),
                        summaryOf(
                                status
                        ),
                        relationOverlapMatches,
                        List.copyOf(
                                reasons
                        )
                )
        );
    }


    private List<LifeDomainRuleMatch>
    findNatalMatches(

            ConsultationCategoryEvidenceView view,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                sourceRuleCode,
                "SOCIAL_RELATIONSHIP sourceRuleCode가 필요합니다."
        );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : view.natalMatches()
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "SOCIAL_RELATIONSHIP Natal Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.RELATIONSHIP
            ) {

                continue;
            }

            if (
                    !sourceRuleCode.equals(
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


    private ConsultationCategoryDeterministicStatus
    resolveStatus(

            List<LifeDomainRuleMatch> matches

    ) {

        Objects.requireNonNull(
                matches,
                "SOCIAL_RELATIONSHIP Core Match 목록이 필요합니다."
        );

        boolean supportiveExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        if (
                pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        if (
                supportiveExists
        ) {

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        return ConsultationCategoryDeterministicStatus.CONTEXTUAL;
    }


    private boolean hasStatus(

            List<LifeDomainRuleMatch> matches,

            LifeDomainSignalStatus status

    ) {

        Objects.requireNonNull(
                matches,
                "SOCIAL_RELATIONSHIP Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "SOCIAL_RELATIONSHIP Status가 필요합니다."
        );

        return matches
                .stream()
                .anyMatch(
                        match ->
                                match.status()
                                        == status
                );
    }


    private String statusReason(

            List<LifeDomainRuleMatch> matches,

            ConsultationCategoryDeterministicStatus status

    ) {

        boolean supportiveExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family의 MIXED 상태를 그대로 보존했습니다.";
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family 안에 SUPPORTIVE와 PRESSURE 상태가 함께 존재하여 MIXED로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family의 공식 방향을 PRESSURE로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family의 공식 방향을 SUPPORTIVE로 보존했습니다.";
        }

        return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family는 존재하지만 방향은 CONTEXTUAL로 유지했습니다.";
    }


    private String summaryOf(

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                status,
                "SOCIAL_RELATIONSHIP deterministic status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "대인관계의 원국 관계망 구조에 SUPPORTIVE 상태가 확인됩니다.";

            case PRESSURE ->
                    "대인관계의 원국 관계망 구조에 PRESSURE 상태가 확인됩니다.";

            case MIXED ->
                    "대인관계의 원국 관계망 구조에 MIXED 상태가 확인됩니다.";

            case CONTEXTUAL ->
                    "대인관계의 원국 관계망 구조가 확인되며 방향은 CONTEXTUAL로 유지됩니다.";
        };
    }
}