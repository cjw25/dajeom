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
 * SOCIAL_RELATIONSHIP 원국
 * 현실·역할·책임 구조 deterministic Rule.
 *
 * =================================================
 * 공식 Source Family
 * =================================================
 *
 * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
 *
 * 하나만 사용한다.
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * 이 Rule의 Category Conclusion은
 * 하나의 Source Family에서만 생성한다.
 *
 * RELATIONSHIP_TEN_GOD_CONTEXT 등
 * 다른 독립 원국 Evidence를
 * 이 Match 안에 결합하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 다른 Source Family 결합
 * - 관계 과정 생성
 * - 관계 단계 생성
 * - 사건 생성
 * - 인과 생성
 * - 순서 생성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 */
@Component
public class SocialRelationshipExpectationPressureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_EXPECTATION_PRESSURE";

    private static final String
            PRIMARY_WEALTH_OFFICER_STRUCTURE_RULE_CODE =
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
                "SocialRelationshipExpectationPressureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.SOCIAL_RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "SocialRelationshipExpectationPressureRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
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
                "SOCIAL_RELATIONSHIP의 현실적 주고받음·역할·책임 원국 판단은 RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Family만 사용했습니다."
        );

        reasons.add(
                statusReason(
                        structureMatches,
                        status
                )
        );

        reasons.add(
                "다른 원국 Source Family의 상태를 이 Category Status에 결합하지 않았습니다."
        );

        reasons.add(
                "Source Match의 반복 개수는 상태 강도나 관계 강도 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과는 현실적 주고받음·역할·책임과 관련된 원국 구조 상태만 보존합니다."
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
                        structureMatches,
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
                "SOCIAL_RELATIONSHIP Source Match 목록이 필요합니다."
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

            return "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE Source Family에 MIXED 상태가 존재하여 MIXED로 보존했습니다.";
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
                "SOCIAL_RELATIONSHIP deterministic status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "대인관계의 현실적 주고받음·역할·책임과 관련된 원국 구조에 SUPPORTIVE 방향이 확인됩니다.";

            case PRESSURE ->
                    "대인관계의 현실적 주고받음·역할·책임과 관련된 원국 구조에 PRESSURE 방향이 확인됩니다.";

            case MIXED ->
                    "대인관계의 현실적 주고받음·역할·책임과 관련된 원국 구조는 MIXED 상태입니다.";

            case CONTEXTUAL ->
                    "대인관계의 현실적 주고받음·역할·책임과 관련된 원국 구조가 존재하며 방향은 CONTEXTUAL로 유지됩니다.";
        };
    }
}