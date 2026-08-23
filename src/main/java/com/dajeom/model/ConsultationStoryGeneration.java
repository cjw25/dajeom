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
 * 역할
 * =================================================
 *
 * 이 객체는 새로운 사주 계산 결과가 아니다.
 *
 * ConsultationInterpretationPromptContext를
 * 사용자용 장문 Story로 문장화한
 * 최종 LLM 출력 결과만 보존한다.
 *
 * =================================================
 * 공식 생성 계약
 * =================================================
 *
 * Story Generation은 반드시:
 *
 * - promptContext
 * - provider
 * - model
 * - storiesByCategory
 * - emphasisByCategory
 *
 * 전체를 명시해서 생성한다.
 *
 * emphasisByCategory를 생략하는
 * 과거 호환 생성 경로는 제공하지 않는다.
 *
 * =================================================
 * storiesByCategory
 * =================================================
 *
 * ConsultationCategory별
 * 사용자 상담문 순수 텍스트.
 *
 * - Markdown 강조 마커를 넣지 않는다.
 * - HTML 강조 마커를 넣지 않는다.
 * - Prompt Context에 존재하는 Category만 허용한다.
 * - unavailable Category에는 Story를 만들 수 없다.
 *
 * =================================================
 * emphasisByCategory
 * =================================================
 *
 * 사용자 화면에서 강조해서 보여줄
 * 실제 Story 일부 문자열.
 *
 * 반드시 해당 Category storyText 안에
 * 실제 존재하는 문자열만 보존한다.
 *
 * 강조 후보가 잘못되었다고 해서
 * 정상 Story Generation 전체를 실패시키지 않는다.
 *
 * 강조 정보는:
 *
 * - 사주 계산값이 아니다.
 * - 새로운 Meaning이 아니다.
 * - Story 내용을 변경하지 않는다.
 * - Category 간 의미를 합성하지 않는다.
 *
 * =================================================
 * Category 독립성
 * =================================================
 *
 * 각 Story는 ConsultationCategory별로
 * 독립 저장한다.
 *
 * 이 모델에서는:
 *
 * - 서로 다른 Category Story를 합치지 않는다.
 * - Category 간 관계를 만들지 않는다.
 * - Category 간 인과를 만들지 않는다.
 * - Category 간 시간순서를 만들지 않는다.
 * - 여러 Story를 하나의 종합 Status로 계산하지 않는다.
 *
 * =================================================
 * Prompt Context 경계
 * =================================================
 *
 * Story가 존재하는 Category는 반드시
 * promptContext 안에 동일 Category의
 * Prompt Section이 존재해야 한다.
 *
 * Prompt Context에 없는 Category의
 * Story 생성은 허용하지 않는다.
 *
 * unavailable Prompt Section의
 * Story 생성도 허용하지 않는다.
 *
 * =================================================
 * 부분 생성
 * =================================================
 *
 * Prompt Context에 여러 available Category가
 * 존재하더라도 모든 Category Story가 반드시
 * 이 객체에 존재해야 하는 것은 아니다.
 *
 * 개별 Category 생성 실패 등으로
 * 일부 Story만 존재할 수 있다.
 *
 * 존재하지 않는 Story를
 * 이 모델에서 임의로 만들어 채우지 않는다.
 */
public record ConsultationStoryGeneration(

        ConsultationInterpretationPromptContext promptContext,

        String provider,

        String model,

        Map<ConsultationCategory, String> storiesByCategory,

        Map<ConsultationCategory, List<String>> emphasisByCategory

) {

    /*
     * =================================================
     * Canonical Constructor
     * =================================================
     */

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

        /*
         * =================================================
         * Story Normalize
         * =================================================
         */

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

                /*
                 * =================================================
                 * Prompt Context Category 경계
                 * =================================================
                 */

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

                /*
                 * =================================================
                 * unavailable Category 차단
                 * =================================================
                 */

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

        /*
         * =================================================
         * Emphasis Normalize
         * =================================================
         */

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

        /*
         * Story가 존재하는 모든 Category에는
         * 강조 목록을 반드시 가진다.
         *
         * 강조할 문구가 없으면 빈 List.
         */
        for (
                ConsultationCategory category
                : normalizedStories.keySet()
        ) {

            normalizedEmphasis.putIfAbsent(
                    category,
                    List.of()
            );
        }

        /*
         * =================================================
         * Immutable
         * =================================================
         */

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


    /*
     * =================================================
     * Story
     * =================================================
     */

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


    /*
     * =================================================
     * Emphasis
     * =================================================
     */

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


    public boolean hasEmphasis(
            ConsultationCategory category
    ) {

        return !storyEmphasisOf(
                category
        )
                .isEmpty();
    }


    /*
     * =================================================
     * Emphasis Normalize
     * =================================================
     */

    private static List<String> normalizeEmphasis(

            String storyText,

            List<String> source

    ) {

        if (
                source == null
                        || source.isEmpty()
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
             * 강조는 하나의 문단 안에서
             * 실제 Story 일부를 지정한다.
             */
            if (
                    phrase.contains(
                            "\n"
                    )
            ) {

                continue;
            }

            /*
             * 실제 Story에 없는 강조 후보는
             * 해당 후보만 버린다.
             *
             * 이것 때문에 정상 상담문 전체를
             * 실패시키지 않는다.
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


    /*
     * =================================================
     * Text
     * =================================================
     */

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