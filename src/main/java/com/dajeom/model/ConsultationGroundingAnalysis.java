package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationQuestionRoutingStatus;

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
 * LLM Interpretation 직전의
 * 최종 deterministic Grounding Aggregate.
 *
 * =================================================
 *
 * 여기까지가 Java의 책임이다.
 *
 * Java:
 *
 * - 사주 계산
 * - Evidence 판정
 * - 생활영역 Rule
 * - 상담 Category Routing
 * - 불확실성 보존
 * - Grounding 구조화
 *
 * 를 담당한다.
 *
 * =================================================
 *
 * LLM:
 *
 * 이 Grounding 안에서만
 * 일반 사용자용 설명을 생성한다.
 *
 * LLM이 새로운 명리 계산이나
 * 누락된 운을 추정해서는 안 된다.
 */
public record ConsultationGroundingAnalysis(

        ConsultationQuestionRoutingStatus
        questionRoutingStatus,

        Optional<String> consultationQuestion,

        List<ConsultationGroundingSection> sections,

        boolean completeChart,

        boolean uncertaintyExists

) {

    public ConsultationGroundingAnalysis {

        Objects.requireNonNull(
                questionRoutingStatus,
                "Grounding Question Routing Status는 필수입니다."
        );

        consultationQuestion =
                normalizeQuestion(
                        consultationQuestion
                );

        sections =
                sections == null
                        ? List.of()
                        : sections
                        .stream()
                        .map(
                                section ->
                                        Objects.requireNonNull(
                                                section,
                                                "Grounding Section은 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        section ->
                                                section
                                                        .category()
                                                        .getOrder()
                                )
                        )
                        .toList();

        validateQuestionState(
                questionRoutingStatus,
                consultationQuestion,
                sections
        );

        validateDuplicateCategories(
                sections
        );
    }

    public boolean hasQuestion() {

        return consultationQuestion
                .isPresent();
    }

    public boolean groundingQuestionRoutingRequired() {

        return questionRoutingStatus
                .groundingRequired();
    }

    public boolean hasSections() {

        return !sections
                .isEmpty();
    }

    public Optional<ConsultationGroundingSection>
    find(
            ConsultationCategory category
    ) {

        if (
                category == null
        ) {

            return Optional.empty();
        }

        return sections
                .stream()
                .filter(
                        section ->
                                section.category()
                                        == category
                )
                .findFirst();
    }

    public List<ConsultationGroundingSection>
    availableSections() {

        return sections
                .stream()
                .filter(
                        ConsultationGroundingSection
                                ::available
                )
                .toList();
    }

    public List<ConsultationGroundingSection>
    unavailableSections() {

        return sections
                .stream()
                .filter(
                        ConsultationGroundingSection
                                ::unavailable
                )
                .toList();
    }

    public boolean hasUnavailableSections() {

        return !unavailableSections()
                .isEmpty();
    }

    public int evidenceCount() {

        return sections
                .stream()
                .mapToInt(
                        ConsultationGroundingSection
                                ::evidenceCount
                )
                .sum();
    }

    private static void validateQuestionState(

            ConsultationQuestionRoutingStatus status,

            Optional<String> question,

            List<ConsultationGroundingSection> sections

    ) {

        switch (status) {

            case NOT_APPLICABLE -> {

                if (
                        question.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "NOT_APPLICABLE Grounding에는 자유 질문이 존재할 수 없습니다."
                    );
                }
            }

            case EXPLICITLY_SCOPED -> {

                if (
                        question.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "EXPLICITLY_SCOPED Grounding에는 자유 질문이 필요합니다."
                    );
                }
            }

            case GROUNDING_REQUIRED -> {

                if (
                        question.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "GROUNDING_REQUIRED Grounding에는 자유 질문이 필요합니다."
                    );
                }

                if (
                        !sections.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "Category가 아직 미확정된 자유 질문에는 Grounding Section을 미리 지정할 수 없습니다."
                    );
                }
            }
        }
    }

    private static void validateDuplicateCategories(
            List<ConsultationGroundingSection> sections
    ) {

        Map<
                ConsultationCategory,
                ConsultationGroundingSection
                > indexed =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        for (
                ConsultationGroundingSection section
                : sections
        ) {

            ConsultationGroundingSection previous =
                    indexed.put(
                            section.category(),
                            section
                    );

            if (
                    previous != null
            ) {

                throw new IllegalArgumentException(
                        "Grounding Section의 ConsultationCategory가 중복되었습니다: "
                                + section.category()
                );
            }
        }
    }

    private static Optional<String>
    normalizeQuestion(
            Optional<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Optional.empty();
        }

        String value =
                source
                        .orElseThrow()
                        .trim();

        return value.isBlank()
                ? Optional.empty()
                : Optional.of(
                value
        );
    }
}