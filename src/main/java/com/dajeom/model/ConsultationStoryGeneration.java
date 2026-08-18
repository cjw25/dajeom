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
 * - 해당 storyText에 실제 존재하는 문자열만 보존한다.
 * - 잘못된 강조 후보 하나 때문에 정상 상담문을 실패시키지 않는다.
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

                /*
                 * Story가 없는 Category의 강조값은
                 * 사용자 출력에 의미가 없으므로 보존하지 않는다.
                 *
                 * 강조 메타데이터 때문에
                 * 정상 Story Generation 전체를 실패시키지 않는다.
                 */
                if (
                        storyText == null
                ) {

                    continue;
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

            /*
             * 강조는 문단 하나 안에서 표현하도록 한다.
             */
            if (
                    phrase.contains(
                            "\n"
                    )
            ) {

                continue;
            }

            /*
             * 실제 Story 안에 없는 강조 후보는
             * 해당 후보만 버린다.
             *
             * 이것 때문에 상담문 전체를 실패시키지 않는다.
             */
            if (
                    !storyText.contains(
                            phrase
                    )
            ) {

                continue;
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