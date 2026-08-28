package com.dajeom.engine;

import com.dajeom.constant.ConsultationTimelineUserMeaningPolicyRegistry;
import com.dajeom.model.ConsultationGroundingDeterministicConclusion;
import com.dajeom.model.ConsultationTimelineUserMeaning;
import com.dajeom.model.ConsultationTimelineUserMeaningPolicy;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 107~111 Timeline User Meaning Engine.
 *
 * 내부 deterministic summary를 파싱하거나 복사하지 않는다.
 *
 * category + layer + ruleCode
 * -> fail-closed Policy
 * -> deterministic status
 * -> 안전한 Timeline 사용자 Meaning
 */
@Component
public class ConsultationTimelineUserMeaningEngine {

    public Optional<ConsultationTimelineUserMeaning> analyze(

            ConsultationGroundingDeterministicConclusion conclusion

    ) {

        Objects.requireNonNull(
                conclusion,
                "Timeline User Meaning 분석에는 Deterministic Conclusion이 필요합니다."
        );

        ConsultationCategory category =
                conclusion
                        .structuralReference()
                        .category();

        ConsultationCategoryDeterministicLayer layer =
                conclusion
                        .structuralReference()
                        .layer();

        if (
                layer
                        == ConsultationCategoryDeterministicLayer.NATAL
        ) {

            throw new IllegalArgumentException(
                    "Timeline User Meaning Engine은 NATAL Conclusion을 처리하지 않습니다. category="
                            + category
                            + ", rule="
                            + conclusion.ruleCode()
            );
        }

        ConsultationTimelineUserMeaningPolicy policy =
                ConsultationTimelineUserMeaningPolicyRegistry
                        .requireAudited(
                                category,
                                layer,
                                conclusion.ruleCode()
                        );

        if (
                !policy.directOutput()
        ) {

            return Optional.empty();
        }

        String subject =
                subjectOf(
                        policy,
                        conclusion
                );

        String userMeaning =
                userMeaningOf(
                        subject,
                        conclusion.status()
                );

        return Optional.of(
                new ConsultationTimelineUserMeaning(
                        conclusion,
                        policy,
                        userMeaning
                )
        );
    }

    private String subjectOf(

            ConsultationTimelineUserMeaningPolicy policy,

            ConsultationGroundingDeterministicConclusion conclusion

    ) {

        String subject =
                requireText(
                        policy.userSubject(),
                        "Timeline User Meaning subject는 필수입니다."
                );

        if (
                !policy.sourceDomainAware()
        ) {

            return subject;
        }

        List<LifeDomain> domains =
                new ArrayList<>(
                        conclusion
                                .structuralReference()
                                .sourceDomainStatuses()
                                .keySet()
                );

        if (
                domains.size()
                        != 1
        ) {

            throw new IllegalStateException(
                    "sourceDomainAware Timeline Meaning에는 정확히 하나의 Source LifeDomain이 필요합니다. category="
                            + policy.category()
                            + ", rule="
                            + policy.ruleCode()
                            + ", domains="
                            + domains
            );
        }

        String domainText =
                domains
                        .get(0)
                        .koreanName();

        return subject.replace(
                ConsultationTimelineUserMeaningPolicy.SOURCE_DOMAIN_TOKEN,
                domainText
        );
    }

    private String userMeaningOf(

            String subject,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                status,
                "Timeline User Meaning 생성에는 deterministic status가 필요합니다."
        );

        String normalizedSubject =
                requireText(
                        subject,
                        "Timeline User Meaning subject는 필수입니다."
                );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    normalizedSubject
                            + "에서 지지 방향이 확인됩니다.";

            case PRESSURE ->
                    normalizedSubject
                            + "에서 부담이나 제약 방향이 확인됩니다.";

            case MIXED ->
                    normalizedSubject
                            + "에서 지지와 부담의 서로 다른 방향이 함께 확인됩니다.";

            case CONTEXTUAL ->
                    normalizedSubject
                            + "은 방향을 단독으로 확정하지 않는 배경 구조로 확인됩니다.";
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