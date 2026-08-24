package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * OpenAI 직전의 최종 서술 설계도.
 *
 * promptContext는 블라인드 명리 계산으로 확정된 공식 Grounding이며,
 * userContext는 그 이후에 붙는 표현/선택 Context다.
 * 둘의 역할을 섞지 않는다.
 */
public record ConsultationNarrativePlan(

        String planVersion,
        ConsultationInterpretationPromptContext promptContext,
        ConsultationUserContext userContext,
        List<ConsultationNarrativeSectionPlan> sections

) {

    public ConsultationNarrativePlan {
        planVersion = requireText(
                planVersion,
                "Narrative Plan Version은 필수입니다."
        );

        Objects.requireNonNull(
                promptContext,
                "Narrative Plan에는 Prompt Context가 필요합니다."
        );

        Objects.requireNonNull(
                userContext,
                "Narrative Plan에는 User Context가 필요합니다."
        );

        sections = sections == null
                ? List.of()
                : sections.stream()
                .map(
                        section ->
                                Objects.requireNonNull(
                                        section,
                                        "Narrative Section은 null일 수 없습니다."
                                )
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        ConsultationNarrativeSectionPlan::focusRank
                                )
                                .thenComparingInt(
                                        ConsultationNarrativeSectionPlan::categoryOrder
                                )
                )
                .toList();

        Map<
                ConsultationCategory,
                ConsultationNarrativeSectionPlan
                > indexed =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        for (
                ConsultationNarrativeSectionPlan section
                : sections
        ) {

            if (
                    promptContext
                            .findSection(
                                    section.category()
                            )
                            .isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "Prompt Context에 없는 Category가 Narrative Plan에 포함되었습니다: "
                                + section.category()
                );
            }

            if (
                    indexed.put(
                            section.category(),
                            section
                    ) != null
            ) {

                throw new IllegalArgumentException(
                        "Narrative Section Category가 중복되었습니다: "
                                + section.category()
                );
            }
        }
    }

    public Optional<ConsultationNarrativeSectionPlan>
    findSection(
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

    public List<ConsultationCategory>
    focusedCategories() {

        return sections
                .stream()
                .filter(
                        ConsultationNarrativeSectionPlan::questionFocused
                )
                .map(
                        ConsultationNarrativeSectionPlan::category
                )
                .toList();
    }

    public boolean hasSections() {

        return !sections
                .isEmpty();
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