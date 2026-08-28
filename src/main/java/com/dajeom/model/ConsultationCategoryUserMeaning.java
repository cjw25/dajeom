package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;

import java.util.List;
import java.util.Objects;

/**
 * 하나의 NATAL Category Deterministic Conclusion을
 * 사용자 Meaning 경계로 변환한 결과.
 *
 * =================================================
 * 중요한 원칙
 * =================================================
 *
 * sourceConclusion.summary()를 복사하지 않는다.
 *
 * sourceConclusion은:
 *
 * - ruleCode
 * - status
 * - provenance
 *
 * 확인용 공식 Source로만 유지한다.
 *
 * 실제 userMeaning은
 * 감사된 ConsultationCategoryUserMeaningPolicy와
 * deterministic status에서 생성된다.
 */
public record ConsultationCategoryUserMeaning(

        ConsultationGroundingDeterministicConclusion sourceConclusion,

        ConsultationCategoryUserMeaningPolicy policy,

        String userMeaning

) {

    public ConsultationCategoryUserMeaning {

        Objects.requireNonNull(
                sourceConclusion,
                "Category User Meaning sourceConclusion은 필수입니다."
        );

        Objects.requireNonNull(
                policy,
                "Category User Meaning policy는 필수입니다."
        );

        userMeaning =
                requireText(
                        userMeaning,
                        "Category User Meaning userMeaning은 필수입니다."
                );

        if (
                !policy.directOutput()
        ) {

            throw new IllegalArgumentException(
                    "directOutput=false Policy로 Category User Meaning을 생성할 수 없습니다. rule="
                            + policy.ruleCode()
            );
        }

        ConsultationCategory category =
                sourceConclusion
                        .structuralReference()
                        .category();

        if (
                category
                        != policy.category()
        ) {

            throw new IllegalArgumentException(
                    "Category User Meaning의 Source Category와 Policy Category가 일치하지 않습니다. "
                            + "source="
                            + category
                            + ", policy="
                            + policy.category()
            );
        }

        if (
                sourceConclusion
                        .structuralReference()
                        .layer()
                        != ConsultationCategoryDeterministicLayer.NATAL
        ) {

            throw new IllegalArgumentException(
                    "Category User Meaning은 NATAL Conclusion에서만 생성할 수 있습니다. rule="
                            + sourceConclusion.ruleCode()
            );
        }

        if (
                !sourceConclusion
                        .ruleCode()
                        .equals(
                                policy.ruleCode()
                        )
        ) {

            throw new IllegalArgumentException(
                    "Category User Meaning의 Source Rule과 Policy Rule이 일치하지 않습니다. "
                            + "source="
                            + sourceConclusion.ruleCode()
                            + ", policy="
                            + policy.ruleCode()
            );
        }
    }

    public ConsultationCategory category() {

        return policy
                .category();
    }

    public String ruleCode() {

        return policy
                .ruleCode();
    }

    public List<String> forbiddenExtensions() {

        return policy
                .forbiddenExtensions();
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