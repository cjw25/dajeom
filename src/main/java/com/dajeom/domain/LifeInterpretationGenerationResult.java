package com.dajeom.domain;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 7개 사주 장문풀이 AI 생성 실행 결과.
 *
 * =================================================
 *
 * generatedTexts
 *
 * 실제 AI 호출에 성공하여 생성된 카테고리별 본문.
 *
 * =================================================
 *
 * blockedCategories
 *
 * generationAllowed=false라서
 * AI를 호출하지 않은 카테고리.
 *
 * =================================================
 *
 * 중요:
 *
 * 일부 카테고리가 생성되지 않았다고 해서
 * 빈 문자열이나 임의 기본 문장을 넣지 않는다.
 *
 * 7개가 모두 존재할 때만
 * LifeInterpretationTextBundle을 만들 수 있다.
 */
public record LifeInterpretationGenerationResult(

        Map<
                LifeInterpretationCategory,
                String
                > generatedTexts,

        List<LifeInterpretationCategory> blockedCategories

) {

    public LifeInterpretationGenerationResult {

        Objects.requireNonNull(
                generatedTexts,
                "생성 본문 Map은 필수입니다."
        );

        Objects.requireNonNull(
                blockedCategories,
                "생성 차단 카테고리 목록은 필수입니다."
        );

        EnumMap<
                LifeInterpretationCategory,
                String
                > normalizedTexts =
                new EnumMap<>(
                        LifeInterpretationCategory.class
                );

        for (
                Map.Entry<
                        LifeInterpretationCategory,
                        String
                        > entry
                : generatedTexts.entrySet()
        ) {

            LifeInterpretationCategory category =
                    Objects.requireNonNull(
                            entry.getKey(),
                            "생성 본문의 카테고리는 null일 수 없습니다."
                    );

            String text =
                    normalizeText(
                            entry.getValue(),
                            category
                    );

            normalizedTexts.put(
                    category,
                    text
            );
        }

        List<LifeInterpretationCategory> normalizedBlocked =
                new ArrayList<>();

        for (
                LifeInterpretationCategory category
                : blockedCategories
        ) {

            LifeInterpretationCategory checked =
                    Objects.requireNonNull(
                            category,
                            "생성 차단 카테고리에 null이 포함될 수 없습니다."
                    );

            if (
                    !normalizedBlocked.contains(
                            checked
                    )
            ) {

                normalizedBlocked.add(
                        checked
                );
            }
        }

        /*
         * 동일 카테고리가
         *
         * generated
         * +
         * blocked
         *
         * 양쪽에 동시에 존재하면 안 된다.
         */
        for (
                LifeInterpretationCategory blocked
                : normalizedBlocked
        ) {

            if (
                    normalizedTexts.containsKey(
                            blocked
                    )
            ) {

                throw new IllegalArgumentException(
                        "같은 카테고리가 생성 완료와 생성 차단에 동시에 포함될 수 없습니다: "
                                + blocked
                );
            }
        }

        generatedTexts =
                Map.copyOf(
                        normalizedTexts
                );

        blockedCategories =
                List.copyOf(
                        normalizedBlocked
                );
    }

    /**
     * 특정 카테고리가 실제 생성됐는가.
     */
    public boolean generated(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "LifeInterpretationCategory는 필수입니다."
        );

        return generatedTexts.containsKey(
                category
        );
    }

    /**
     * 특정 카테고리가 generationAllowed=false라서
     * 호출되지 않았는가.
     */
    public boolean blocked(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "LifeInterpretationCategory는 필수입니다."
        );

        return blockedCategories.contains(
                category
        );
    }

    /**
     * 실제 생성된 본문을 조회한다.
     *
     * 생성되지 않은 카테고리라면 예외.
     */
    public String generatedTextFor(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "LifeInterpretationCategory는 필수입니다."
        );

        String text =
                generatedTexts.get(
                        category
                );

        if (
                text == null
        ) {

            throw new IllegalStateException(
                    "해당 카테고리의 생성 본문이 존재하지 않습니다: "
                            + category
            );
        }

        return text;
    }

    /**
     * 7개 카테고리 모두 생성됐는가.
     */
    public boolean complete() {

        if (
                !blockedCategories.isEmpty()
        ) {

            return false;
        }

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            String text =
                    generatedTexts.get(
                            category
                    );

            if (
                    text == null
                            ||
                            text.isBlank()
            ) {

                return false;
            }
        }

        return generatedTexts.size()
                == LifeInterpretationCategory.values().length;
    }

    /**
     * 7개가 모두 생성된 경우에만
     * 최종 공개 응답용 TextBundle을 반환한다.
     *
     * 일부 카테고리가 차단 또는 미생성 상태라면
     * Optional.empty().
     */
    public Optional<LifeInterpretationTextBundle>
    completeBundle() {

        if (
                !complete()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                new LifeInterpretationTextBundle(
                        generatedTexts
                )
        );
    }

    /**
     * 반드시 7개가 완성돼 있어야 하는 호출부용.
     *
     * 하나라도 없으면 예외를 발생시킨다.
     */
    public LifeInterpretationTextBundle
    requireCompleteBundle() {

        return completeBundle()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "최종 사주 응답을 만들려면 7개 해석 본문이 모두 생성되어야 합니다. "
                                                + "생성 완료="
                                                + generatedTexts.keySet()
                                                + ", 생성 차단="
                                                + blockedCategories
                                )
                );
    }

    /**
     * 현재 아직 생성되지 않은 카테고리.
     *
     * blocked 여부와 관계없이
     * generatedTexts에 없는 카테고리를 반환한다.
     */
    public List<LifeInterpretationCategory>
    missingCategories() {

        List<LifeInterpretationCategory> result =
                new ArrayList<>();

        for (
                LifeInterpretationCategory category
                : LifeInterpretationCategory.values()
        ) {

            if (
                    !generatedTexts.containsKey(
                            category
                    )
            ) {

                result.add(
                        category
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    private static String normalizeText(

            String value,

            LifeInterpretationCategory category

    ) {

        Objects.requireNonNull(
                value,
                category
                        + " AI 생성 결과는 null일 수 없습니다."
        );

        String normalized =
                value.trim();

        if (
                normalized.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    category
                            + " AI 생성 결과는 비어 있을 수 없습니다."
            );
        }

        return normalized;
    }
}