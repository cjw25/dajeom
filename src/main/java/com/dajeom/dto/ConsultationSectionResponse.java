package com.dajeom.dto;

import com.dajeom.type.ConsultationCategory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * 하나의 상담 분야 API 출력값.
 *
 * =================================================
 * 공식 Meaning Source
 * =================================================
 *
 * 현재 사용자 상담 의미는:
 *
 * Java deterministic Grounding
 * +
 * ConsultationInterpretationPromptContext
 * +
 * 외부 LLM 문장화
 *
 * 결과인:
 *
 * storyText
 *
 * 하나를 공식 상담문으로 사용한다.
 *
 * =================================================
 * 제거한 Legacy 필드
 * =================================================
 *
 * 다음 V1 numeric 상담 필드는 제거한다.
 *
 * - polarity
 * - intensity
 * - confidence
 * - openingMessage
 * - summaryMessage
 * - guidanceMessage
 * - narrationText
 * - basisLabels
 * - evidenceCount
 *
 * 해당 값들은:
 *
 * InterpretationEvidence
 * ->
 * supportWeight / cautionWeight
 * ->
 * InterpretationSignal
 * ->
 * ConsultationRule
 *
 * 경로에서 생성되던
 * 구형 Category Meaning이므로
 * 신규 deterministic 상담 API에 포함하지 않는다.
 *
 * =================================================
 * storyText
 * =================================================
 *
 * ConsultationInterpretationPromptContext를
 * 외부 LLM이 사용자용 한국어 상담문으로
 * 문장화한 순수 텍스트.
 *
 * Markdown / HTML 강조 정보를 섞지 않는다.
 *
 * =================================================
 * storyEmphasis
 * =================================================
 *
 * storyText 안에 실제로 존재하는
 * 문장 또는 구절 중
 * 화면에서 진한 글씨로 표시할 부분.
 *
 * 새로운 사주 계산값이 아니다.
 *
 * 잘못된 강조값 하나 때문에
 * API 전체를 실패시키지 않는다.
 */
public record ConsultationSectionResponse(

        /** 공식 ConsultationCategory 고정 순서 */
        int order,

        /** 사용자 질문/Context를 반영한 화면 표시 순서 */
        int displayOrder,

        /** 현재 사전 질문/Context에서 우선적으로 보여줄 Section인지 */
        boolean focused,

        ConsultationCategory category,

        String title,

        String storyText,

        List<String> storyEmphasis

) {

    public ConsultationSectionResponse {

        if (
                order < 1
                        ||
                        displayOrder < 1
        ) {

            throw new IllegalArgumentException(
                    "ConsultationSectionResponse order/displayOrder는 1 이상이어야 합니다."
            );
        }

        Objects.requireNonNull(
                category,
                "ConsultationSectionResponse category는 필수입니다."
        );

        title =
                requireText(
                        title,
                        "ConsultationSectionResponse title은 필수입니다."
                );

        storyText =
                storyText == null
                        ? ""
                        : storyText.trim();

        storyEmphasis =
                normalizeStoryEmphasis(
                        storyText,
                        storyEmphasis
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

            if (
                    phrase.contains(
                            "\n"
                    )
            ) {

                continue;
            }

            /*
             * 실제 storyText에 존재하지 않는 강조값은
             * 조용히 제거한다.
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