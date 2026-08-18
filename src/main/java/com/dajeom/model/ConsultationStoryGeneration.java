package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
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
 * ConsultationInterpretationPromptContext를
 * 사용자용 장문 스토리로 문장화한 결과만 보존한다.
 *
 * =================================================
 *
 * storiesByCategory
 *
 * - 사용자 상담문 순수 텍스트
 * - Markdown / HTML 강조 마커를 넣지 않는다.
 *
 * =================================================
 *
 * emphasisByCategory
 *
 * - 사용자 화면에서 진한 글씨로 보여줄 실제 상담문 일부
 * - 반드시 해당 storyText에 실제로 존재하는 문자열만 허용한다.
 * - 사주 계산값이나 새로운 판단이 아니다.
 */
public record ConsultationStoryGeneration(

        ConsultationInterpretationPromptContext promptContext,

        String provider,

        String model,

        Map<ConsultationCategory, String> storiesByCategory,

        Map<ConsultationCategory, List<String>> emphasisByCategory

) {

    /**
     * 기존 4개 인자 호출부 호환.
     */
    public ConsultationStoryGeneration(

            ConsultationInterpretationPromptContext promptContext,

            String provider,

            String model,

            Map<ConsultationCategory, String> storiesByCategory

    ) {

        this(
                promptContext,
                provider,
                model,
                storiesByCategory,
                Map.of()
        );
    }

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

        EnumMap<ConsultationCategory, String> normalizedStories =
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
                        normalizedStories.put(
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

        EnumMap<ConsultationCategory, List<String>> normalizedEmphasis =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        if (
                emphasisByCategory != null
        ) {

            for (
                    Map.Entry<ConsultationCategory, List<String>> entry
                    : emphasisByCategory.entrySet()
            ) {

                ConsultationCategory category =
                        Objects.requireNonNull(
                                entry.getKey(),
                                "Story Emphasis Category는 null일 수 없습니다."
                        );

                String storyText =
                        normalizedStories.get(
                                category
                        );

                if (
                        storyText == null
                ) {

                    throw new IllegalArgumentException(
                            "Story가 없는 Category에는 Emphasis를 넣을 수 없습니다: "
                                    + category
                    );
                }

                List<String> emphasis =
                        normalizeEmphasis(
                                storyText,
                                entry.getValue()
                        );

                normalizedEmphasis.put(
                        category,
                        emphasis
                );
            }
        }

        for (
                ConsultationCategory category
                : normalizedStories.keySet()
        ) {

            normalizedEmphasis.putIfAbsent(
                    category,
                    List.of()
            );
        }

        storiesByCategory =
                Collections.unmodifiableMap(
                        new EnumMap<>(
                                normalizedStories
                        )
                );

        emphasisByCategory =
                Collections.unmodifiableMap(
                        new EnumMap<>(
                                normalizedEmphasis
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

    public List<String> storyEmphasisOf(
            ConsultationCategory category
    ) {

        if (
                category == null
        ) {

            return List.of();
        }

        List<String> emphasis =
                emphasisByCategory.get(
                        category
                );

        return emphasis == null
                ? List.of()
                : emphasis;
    }

    public boolean hasStory(
            ConsultationCategory category
    ) {

        return storyTextOf(
                category
        )
                .isPresent();
    }

    public boolean hasEmphasis(
            ConsultationCategory category
    ) {

        return !storyEmphasisOf(
                category
        )
                .isEmpty();
    }

    public int storyCount() {

        return storiesByCategory
                .size();
    }

    private static List<String> normalizeEmphasis(

            String storyText,

            List<String> source

    ) {

        if (
                source == null
                        ||
                        source.isEmpty()
        ) {

            return List.of();
        }

        LinkedHashSet<String> normalized =
                new LinkedHashSet<>();

        for (
                String raw
                : source
        ) {

            if (
                    raw == null
            ) {

                continue;
            }

            String phrase =
                    raw.trim();

            if (
                    phrase.isBlank()
            ) {

                continue;
            }

            if (
                    !storyText.contains(
                            phrase
                    )
            ) {

                throw new IllegalArgumentException(
                        "Story Emphasis는 실제 storyText에 존재하는 문자열이어야 합니다: "
                                + phrase
                );
            }

            normalized.add(
                    phrase
            );
        }

        return List.copyOf(
                normalized
        );
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