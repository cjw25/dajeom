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
 * PARTNER_LUCK 원국 관계망 구조 deterministic Rule.
 *
 * 공식 Source Family:
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * 하나만 사용한다.
 *
 * 이름에 LONG_TERM이 남아 있지만
 * 현재 Source만으로 관계 지속 기간,
 * 결혼 지속성, 장기 유지 능력을 새로 계산하지 않는다.
 */
@Component
public class PartnerLuckLongTermStructureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_LONG_TERM_STRUCTURE";

    private static final String
            NATAL_RELATION_OVERLAP_RULE_CODE =
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
                "PartnerLuckLongTermStructureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.PARTNER_LUCK
        ) {

            throw new IllegalArgumentException(
                    "PartnerLuckLongTermStructureRule은 PARTNER_LUCK View만 평가할 수 있습니다."
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
                "PARTNER_LUCK의 이 원국 판단은 RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family만 사용했습니다."
        );

        reasons.add(
                statusReason(
                        relationOverlapMatches,
                        status
                )
        );

        reasons.add(
                "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE와 RELATIONSHIP_SPOUSE_PALACE를 이 Conclusion의 Source로 결합하지 않았습니다."
        );

        reasons.add(
                "역할·책임 구조는 별도의 PARTNER_LUCK deterministic Conclusion에서 독립적으로 처리합니다."
        );

        reasons.add(
                "Source Match 반복 개수를 배우자운·관계 지속성·결혼 가능성의 강도 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과는 원국 관계망 구조 상태만 보존하며 장기 지속성·관계 단계·인과·순서·사건을 생성하지 않습니다."
        );

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.PARTNER_LUCK,
                        ConsultationCategoryDeterministicLayer.NATAL,
                        RULE_CODE,
                        status,
                        null,
                        null,
                        List.of(),
                        summaryOf(
                                status
                        ),
                        List.copyOf(
                                relationOverlapMatches
                        ),
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
                view,
                "PARTNER_LUCK Evidence View가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "PARTNER_LUCK Source ruleCode가 필요합니다."
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
                            "PARTNER_LUCK Natal Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.RELATIONSHIP
            ) {

                continue;
            }

            if (
                    !normalizedRuleCode.equals(
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
                "PARTNER_LUCK 관계망 Match 목록이 필요합니다."
        );

        if (
                matches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "PARTNER_LUCK 관계망 Status 계산에는 최소 하나의 Match가 필요합니다."
            );
        }

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
                "PARTNER_LUCK Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "LifeDomainSignalStatus가 필요합니다."
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

        Objects.requireNonNull(
                matches,
                "PARTNER_LUCK 관계망 Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "PARTNER_LUCK 관계망 Status가 필요합니다."
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

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family의 upstream MIXED 상태를 그대로 보존했습니다.";
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return "RELATIONSHIP_NATAL_RELATION_OVERLAP Source Family 안에 SUPPORTIVE와 PRESSURE 상태가 모두 존재하여 MIXED로 보존했습니다.";
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
                "PARTNER_LUCK 관계망 Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "PARTNER_LUCK에서 참조하는 원국 관계망 구조에 SUPPORTIVE 상태가 확인됩니다.";

            case PRESSURE ->
                    "PARTNER_LUCK에서 참조하는 원국 관계망 구조에 PRESSURE 상태가 확인됩니다.";

            case MIXED ->
                    "PARTNER_LUCK에서 참조하는 원국 관계망 구조에 MIXED 상태가 확인됩니다.";

            case CONTEXTUAL ->
                    "PARTNER_LUCK에서 참조하는 원국 관계망 구조가 존재하며 방향은 CONTEXTUAL로 유지됩니다.";
        };
    }


    private static String requireText(

            String value,

            String message

    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}