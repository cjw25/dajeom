package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차.
 *
 * 실제 ConsultationGroundingAnalysis와
 * 적용할 Grounding Policy를 연결한 결과.
 *
 * =================================================
 *
 * Grounding Section마다
 * 동일 ConsultationCategory의 Policy가
 * 정확히 하나씩 연결된다.
 *
 * =================================================
 *
 * 자유 질문 Category가 아직
 * GROUNDING_REQUIRED 상태라
 * Section이 하나도 없는 경우에는
 * Policy 목록도 비어 있을 수 있다.
 */
public record ConsultationGroundingPolicyAnalysis(

        ConsultationGroundingAnalysis groundingAnalysis,

        List<ConsultationGroundingPolicy> policies

) {

    public ConsultationGroundingPolicyAnalysis {

        Objects.requireNonNull(
                groundingAnalysis,
                "ConsultationGroundingPolicyAnalysis에는 ConsultationGroundingAnalysis가 필요합니다."
        );

        policies =
                policies == null
                        ? List.of()
                        : policies
                        .stream()
                        .map(
                                policy ->
                                        Objects.requireNonNull(
                                                policy,
                                                "Grounding Policy는 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        policy ->
                                                policy
                                                        .category()
                                                        .getOrder()
                                )
                        )
                        .toList();

        validatePolicyCoverage(
                groundingAnalysis,
                policies
        );
    }

    public Optional<ConsultationGroundingPolicy>
    find(
            ConsultationCategory category
    ) {

        if (
                category == null
        ) {

            return Optional.empty();
        }

        return policies
                .stream()
                .filter(
                        policy ->
                                policy.category()
                                        == category
                )
                .findFirst();
    }

    public ConsultationGroundingPolicy get(
            ConsultationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "ConsultationCategory는 필수입니다."
        );

        return find(
                category
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "현재 Grounding에 적용된 Policy를 찾을 수 없습니다: "
                                                + category
                                )
                );
    }

    public int policyCount() {

        return policies
                .size();
    }

    private static void validatePolicyCoverage(

            ConsultationGroundingAnalysis groundingAnalysis,

            List<ConsultationGroundingPolicy> policies

    ) {

        List<ConsultationGroundingSection> sections =
                groundingAnalysis
                        .sections();

        if (
                sections.size()
                        != policies.size()
        ) {

            throw new IllegalArgumentException(
                    "Grounding Section 수와 Policy 수가 일치하지 않습니다. "
                            + "sections="
                            + sections.size()
                            + ", policies="
                            + policies.size()
            );
        }

        Map<
                ConsultationCategory,
                ConsultationGroundingPolicy
                > indexed =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        for (
                ConsultationGroundingPolicy policy
                : policies
        ) {

            ConsultationGroundingPolicy previous =
                    indexed.put(
                            policy.category(),
                            policy
                    );

            if (
                    previous != null
            ) {

                throw new IllegalArgumentException(
                        "Grounding Policy Category가 중복되었습니다: "
                                + policy.category()
                );
            }
        }

        List<ConsultationCategory> missing =
                new ArrayList<>();

        for (
                ConsultationGroundingSection section
                : sections
        ) {

            if (
                    !indexed.containsKey(
                            section.category()
                    )
            ) {

                missing.add(
                        section.category()
                );
            }
        }

        if (
                !missing.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Grounding Section에 필요한 Policy가 누락되었습니다: "
                            + missing
            );
        }
    }
}