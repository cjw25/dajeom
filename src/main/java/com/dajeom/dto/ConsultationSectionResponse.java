package com.dajeom.dto;

import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.InterpretationIntensity;
import com.dajeom.type.InterpretationPolarity;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 하나의 상담 분야 API 출력값.
 *
 * =================================================
 *
 * storyText
 *
 * deterministic Grounding을 외부 LLM이
 * 사용자용 한국어 상담문으로 문장화한 순수 텍스트.
 *
 * =================================================
 *
 * storyEmphasis
 *
 * storyText 안에서 실제로 존재하는 문장 또는 구절 중
 * 화면에서 진한 글씨로 표현할 부분.
 *
 * Markdown이나 HTML을 API에 섞지 않는다.
 */
public record ConsultationSectionResponse(

        int order,

        ConsultationCategory category,

        String title,

        InterpretationPolarity polarity,

        InterpretationIntensity intensity,

        AnalysisConfidence confidence,

        String openingMessage,

        String summaryMessage,

        String guidanceMessage,

        String narrationText,

        String storyText,

        List<String> storyEmphasis,

        List<String> basisLabels,

        int evidenceCount

) {

    /**
     * 기존 storyEmphasis 없는 호출부 호환.
     */
    public ConsultationSectionResponse(

            int order,

            ConsultationCategory category,

            String title,

            InterpretationPolarity polarity,

            InterpretationIntensity intensity,

            AnalysisConfidence confidence,

            String openingMessage,

            String summaryMessage,

            String guidanceMessage,

            String narrationText,

            String storyText,

            List<String> basisLabels,

            int evidenceCount

    ) {

        this(
                order,
                category,
                title,
                polarity,
                intensity,
                confidence,
                openingMessage,
                summaryMessage,
                guidanceMessage,
                narrationText,
                storyText,
                List.of(),
                basisLabels,
                evidenceCount
        );
    }

    public ConsultationSectionResponse {

        storyText =
                storyText == null
                        ? ""
                        : storyText.trim();

        storyEmphasis =
                normalizeStoryEmphasis(
                        storyText,
                        storyEmphasis
                );

        basisLabels =
                basisLabels == null
                        ? List.of()
                        : List.copyOf(
                        basisLabels
                );
    }

    private static List<String> normalizeStoryEmphasis(

            String storyText,

            List<String> source

    ) {

        if (
                storyText == null
                        ||
                        storyText.isBlank()
                        ||
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
             * 잘못된 강조값 하나 때문에
             * API 응답 전체를 실패시키지 않는다.
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
}