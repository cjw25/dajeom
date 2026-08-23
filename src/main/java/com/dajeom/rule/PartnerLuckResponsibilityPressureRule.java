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
 * PARTNER_LUCK 원국 역할·책임 구조 deterministic Rule.
 *
 * 공식 Source Family:
 *
 * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
 *
 * 하나만 사용한다.
 *
 * 다른 관계 Source Family를
 * 이 Conclusion에 결합하지 않는다.
 *
 * 이 Rule은:
 *
 * - 배우자 존재 여부
 * - 결혼 성사
 * - 관계 지속
 * - 이별
 * - 재회
 *
 * 같은 사건을 생성하지 않는다.
 *
 * 또한:
 *
 * - 책임이 반복될수록
 * - 요구가 커질수록
 * - 관계가 깊어진 뒤
 * - A 때문에 B
 * - A가 B로 이어진다
 *
 * 같은 관계·인과·순서를 생성하지 않는다.
 */
@Component
public class PartnerLuckResponsibilityPressureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_RESPONSIBILITY_PRESSURE";

    private static final String
            PRIMARY_WEALTH_OFFICER_STRUCTURE_RULE_CODE =
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
                "PartnerLuckResponsibilityPressureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.PARTNER_LUCK
        ) {

            throw new IllegalArgumentException(
                    "PartnerLuckResponsibilityPressureRule은 PARTNER_LUCK View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        List<LifeDomainRuleMatch> structureMatches =
                findNatalMatches(
                        view,
                        PRIMARY_WEALTH_OFFICER_STRUCTURE_RULE_CODE
                );

        if (
                structureMatches.isEmpty()
        ) {

            return List.of();
        }

        ConsultationCategoryDeterministicStatus status =
                resolveStatus(
                        structureMatches
                );

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "PARTNER_LUCK의 역할·책임 구조 판단은 RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Family만 사용했습니다."
        );

        reasons.add(
                statusReason(
                        structureMatches,
                        status
                )
        );

        reasons.add(
                "RELATIONSHIP_TEN_GOD_CONTEXT를 포함한 다른 관계 Source Family를 이 Conclusion에 결합하지 않았습니다."
        );

        reasons.add(
                "Source Match 반복 개수를 배우자운·결혼 가능성·관계 지속성의 강도 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과는 원국의 역할·책임 관련 관계 구조 상태만 보존하며 관계 단계·사건·인과·순서를 생성하지 않습니다."
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
                                structureMatches
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
                "PARTNER_LUCK 역할·책임 Match 목록이 필요합니다."
        );

        if (
                matches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "PARTNER_LUCK 역할·책임 Status 계산에는 최소 하나의 Match가 필요합니다."
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
                "PARTNER_LUCK 역할·책임 Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "PARTNER_LUCK 역할·책임 Status가 필요합니다."
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

            return "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Family의 MIXED 상태를 그대로 보존했습니다.";
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Family 안에 SUPPORTIVE와 PRESSURE 상태가 모두 존재하여 MIXED로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Family의 공식 방향을 PRESSURE로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Family의 공식 방향을 SUPPORTIVE로 보존했습니다.";
        }

        return "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Family는 존재하지만 방향은 CONTEXTUAL로 유지했습니다.";
    }


    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "PARTNER_LUCK 역할·책임 Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "PARTNER_LUCK에서 참조하는 원국 역할·책임 구조에 SUPPORTIVE 상태가 확인됩니다.";

            case PRESSURE ->
                    "PARTNER_LUCK에서 참조하는 원국 역할·책임 구조에 PRESSURE 상태가 확인됩니다.";

            case MIXED ->
                    "PARTNER_LUCK에서 참조하는 원국 역할·책임 구조에 MIXED 상태가 확인됩니다.";

            case CONTEXTUAL ->
                    "PARTNER_LUCK에서 참조하는 원국 역할·책임 구조가 존재하며 방향은 CONTEXTUAL로 유지됩니다.";
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