package com.dajeom.engine;

import com.dajeom.constant.ConsultationCategoryUserMeaningPolicyRegistry;
import com.dajeom.model.ConsultationCategoryUserMeaning;
import com.dajeom.model.ConsultationCategoryUserMeaningPolicy;
import com.dajeom.model.ConsultationGroundingDeterministicConclusion;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 감사된 Category NATAL Deterministic Rule만
 * 사용자 Meaning으로 변환한다.
 *
 * =================================================
 * 기존 방식
 * =================================================
 *
 * conclusion.summary()
 * ->
 * Meaning Claim
 *
 * =================================================
 * 변경
 * =================================================
 *
 * category
 * +
 * ruleCode
 * +
 * deterministic status
 *
 * ->
 *
 * User Meaning Policy Registry
 *
 * ->
 *
 * directOutput 확인
 *
 * ->
 *
 * 사용자 Meaning
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 다른 Rule과 조합
 * - Evidence 개수 점수화
 * - 사건 생성
 * - 인과 생성
 */
@Component
public class ConsultationCategoryUserMeaningEngine {

    public boolean audits(
            ConsultationCategory category
    ) {

        return ConsultationCategoryUserMeaningPolicyRegistry
                .audits(
                        category
                );
    }

    public Optional<ConsultationCategoryUserMeaning> analyze(

            ConsultationGroundingDeterministicConclusion conclusion

    ) {

        Objects.requireNonNull(
                conclusion,
                "Category User Meaning 분석에는 Deterministic Conclusion이 필요합니다."
        );

        ConsultationCategory category =
                conclusion
                        .structuralReference()
                        .category();

        if (
                !audits(
                        category
                )
        ) {

            throw new IllegalArgumentException(
                    "현재 Category User Meaning 감사 대상이 아닙니다. category="
                            + category
                            + ", rule="
                            + conclusion.ruleCode()
            );
        }

        if (
                conclusion
                        .structuralReference()
                        .layer()
                        != ConsultationCategoryDeterministicLayer.NATAL
        ) {

            throw new IllegalArgumentException(
                    "Category User Meaning Engine은 NATAL Conclusion만 처리합니다. category="
                            + category
                            + ", rule="
                            + conclusion.ruleCode()
                            + ", layer="
                            + conclusion
                            .structuralReference()
                            .layer()
            );
        }

        ConsultationCategoryUserMeaningPolicy policy =
                ConsultationCategoryUserMeaningPolicyRegistry
                        .requireAudited(
                                category,
                                conclusion.ruleCode()
                        );

        /*
         * =================================================
         * 계산은 보존하지만 사용자 Meaning 권한 없음
         * =================================================
         */

        if (
                !policy.directOutput()
        ) {

            return Optional.empty();
        }

        String userMeaning =
                userMeaningOf(
                        policy.userSubject(),
                        conclusion.status()
                );

        return Optional.of(
                new ConsultationCategoryUserMeaning(
                        conclusion,
                        policy,
                        userMeaning
                )
        );
    }

    /**
     * ConsultationCategoryDeterministicStatus의
     * 공식 네 방향만 사용자 언어로 옮긴다.
     *
     * 성공/실패/길흉/사건확률로 변환하지 않는다.
     */
    private String userMeaningOf(

            String subject,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                status,
                "Category User Meaning 생성에는 deterministic status가 필요합니다."
        );

        String normalizedSubject =
                requireText(
                        subject,
                        "Category User Meaning subject는 필수입니다."
                );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    normalizedSubject
                            + " 측면에서 비교적 원활하게 작동하는 방향이 확인됩니다.";

            case PRESSURE ->
                    normalizedSubject
                            + " 측면에서 부담이나 제약이 걸리는 방향이 확인됩니다.";

            case MIXED ->
                    normalizedSubject
                            + " 측면에서 원활한 작동과 부담 방향이 함께 확인됩니다.";

            case CONTEXTUAL ->
                    normalizedSubject
                            + " 측면은 방향을 단독으로 확정하지 않는 배경 구조로 확인됩니다.";
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