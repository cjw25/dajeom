package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 외부 LLM 문장화 결과.
 *
 * =================================================
 *
 * 이 객체는 새로운 사주 계산 결과가 아니다.
 *
 * 정밀화 20차
 * ConsultationInterpretationPromptContext를
 * 사용자용 장문 스토리로 문장화한 결과만 보존한다.
 *
 * =================================================
 *
 * 원칙
 *
 * - Prompt Context Identity 보존
 * - unavailable Category 생성 금지
 * - Prompt Context에 없는 Category 생성 금지
 * - Category별 Story 1개
 * - 빈 Story 금지
 */
public record ConsultationStoryGeneration(

        ConsultationInterpretationPromptContext promptContext,

        String provider,

        String model,

        Map<ConsultationCategory, String> storiesByCategory

) {

    public ConsultationStoryGeneration {

        Objects.requireNonNull(
                promptContext,
                "Story Generation에는 ConsultationInterpretationPromptContext가 필요합니다."
        );

        provider =
                requireText(
                        provider,
                        "LLM Provider는 필수입니다."
                );

        model =
                requireText(
                        model,
                        "LLM Model은 필수입니다."
                );

        EnumMap<ConsultationCategory, String> normalized =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        if (
                storiesByCategory != null
        ) {

            for (
                    Map.Entry<ConsultationCategory, String> entry
                    : storiesByCategory.entrySet()
            ) {

                ConsultationCategory category =
                        Objects.requireNonNull(
                                entry.getKey(),
                                "Story Category는 null일 수 없습니다."
                        );

                String storyText =
                        requireText(
                                entry.getValue(),
                                "Story Text는 비어 있을 수 없습니다."
                        );

                ConsultationInterpretationPromptSection promptSection =
                        promptContext
                                .findSection(
                                        category
                                )
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "Prompt Context에 없는 Category의 Story를 생성할 수 없습니다: "
                                                                + category
                                                )
                                );

                if (
                        promptSection.unavailable()
                ) {

                    throw new IllegalArgumentException(
                            "unavailable Category의 Story를 생성할 수 없습니다: "
                                    + category
                    );
                }

                String previous =
                        normalized.put(
                                category,
                                storyText
                        );

                if (
                        previous != null
                ) {

                    throw new IllegalArgumentException(
                            "동일 Category의 Story가 중복되었습니다: "
                                    + category
                    );
                }
            }
        }

        storiesByCategory =
                Collections.unmodifiableMap(
                        new EnumMap<>(
                                normalized
                        )
                );
    }

    public Optional<String> storyTextOf(
            ConsultationCategory category
    ) {

        if (
                category == null
        ) {

            return Optional.empty();
        }

        return Optional.ofNullable(
                storiesByCategory.get(
                        category
                )
        );
    }

    public boolean hasStory(
            ConsultationCategory category
    ) {

        return storyTextOf(
                category
        )
                .isPresent();
    }

    public int storyCount() {

        return storiesByCategory
                .size();
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