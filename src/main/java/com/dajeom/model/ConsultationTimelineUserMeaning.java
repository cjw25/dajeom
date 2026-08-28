package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;

import java.util.List;
import java.util.Objects;

/**
 * Timeline Deterministic Conclusion을
 * 감사된 사용자 Meaning으로 변환한 결과.
 *
 * sourceConclusion.summary()를 복사하지 않는다.
 * 실제 userMeaning은 Policy와 deterministic status,
 * 그리고 허용된 경우 source LifeDomain 구조만 사용해 생성한다.
 */
public record ConsultationTimelineUserMeaning(

        ConsultationGroundingDeterministicConclusion sourceConclusion,

        ConsultationTimelineUserMeaningPolicy policy,

        String userMeaning

) {

    public ConsultationTimelineUserMeaning {

        Objects.requireNonNull(
                sourceConclusion,
                "Timeline User Meaning sourceConclusion은 필수입니다."
        );

        Objects.requireNonNull(
                policy,
                "Timeline User Meaning policy는 필수입니다."
        );

        userMeaning =
                requireText(
                        userMeaning,
                        "Timeline User Meaning userMeaning은 필수입니다."
                );

        if (
                !policy.directOutput()
        ) {

            throw new IllegalArgumentException(
                    "directOutput=false Policy로 Timeline User Meaning을 생성할 수 없습니다. rule="
                            + policy.ruleCode()
            );
        }

        ConsultationCategory sourceCategory =
                sourceConclusion
                        .structuralReference()
                        .category();

        ConsultationCategoryDeterministicLayer sourceLayer =
                sourceConclusion
                        .structuralReference()
                        .layer();

        if (
                sourceCategory
                        != policy.category()
        ) {

            throw new IllegalArgumentException(
                    "Timeline User Meaning의 Source Category와 Policy Category가 일치하지 않습니다. source="
                            + sourceCategory
                            + ", policy="
                            + policy.category()
            );
        }

        if (
                sourceLayer
                        != policy.layer()
        ) {

            throw new IllegalArgumentException(
                    "Timeline User Meaning의 Source Layer와 Policy Layer가 일치하지 않습니다. source="
                            + sourceLayer
                            + ", policy="
                            + policy.layer()
            );
        }

        if (
                sourceLayer
                        == ConsultationCategoryDeterministicLayer.NATAL
        ) {

            throw new IllegalArgumentException(
                    "Timeline User Meaning은 NATAL Conclusion에서 생성할 수 없습니다. rule="
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
                    "Timeline User Meaning의 Source Rule과 Policy Rule이 일치하지 않습니다. source="
                            + sourceConclusion.ruleCode()
                            + ", policy="
                            + policy.ruleCode()
            );
        }
    }

    public ConsultationCategory category() {
        return policy.category();
    }

    public ConsultationCategoryDeterministicLayer layer() {
        return policy.layer();
    }

    public String ruleCode() {
        return policy.ruleCode();
    }

    public ConsultationTimelineUserMeaningPolicy.TimelineStage stage() {
        return policy.stage();
    }

    public List<String> forbiddenExtensions() {
        return policy.forbiddenExtensions();
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