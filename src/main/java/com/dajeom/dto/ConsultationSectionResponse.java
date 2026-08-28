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
 * API 상태 계약
 * =================================================
 *
 * 프론트에서는 storyText 문자열을 분석해서
 * 현재 Section 상태를 추측하지 않는다.
 *
 * status가 공식 상태값이다.
 *
 * ANALYSIS_UNAVAILABLE
 *
 * =
 *
 * 상담용 deterministic 분석 실행선 자체가
 * 현재 요청에서 준비되지 않은 상태.
 *
 * NOT_REQUESTED
 *
 * =
 *
 * 상담 전체 API 구조에는 존재하지만
 * 현재 요청의 Prompt Section에는 포함되지 않은 Category.
 *
 * UNAVAILABLE
 *
 * =
 *
 * Category는 요청되었지만
 * 현재 Java deterministic 근거만으로
 * 독립적인 사용자 Meaning을 확정하지 않은 상태.
 *
 * GENERATION_UNAVAILABLE
 *
 * =
 *
 * Java Meaning / Prompt Section은 준비되었지만
 * 외부 Story Generation 결과가 없는 상태.
 *
 * READY
 *
 * =
 *
 * Java 경계를 통과한 최종 storyText가
 * 실제로 존재하는 상태.
 *
 * =================================================
 * focused와 status는 다른 개념
 * =================================================
 *
 * focused
 *
 * =
 *
 * 사용자 질문 / 관계 Context / 직업 Context 등에 의해
 * 화면상 우선 강조되는 Section인지 여부.
 *
 * status
 *
 * =
 *
 * 해당 Section의 데이터 준비 상태.
 *
 * 따라서:
 *
 * focused=false
 * +
 * status=READY
 *
 * 는 정상적인 조합이다.
 */
public record ConsultationSectionResponse(

        /** 공식 ConsultationCategory 고정 순서 */
        int order,

        /** 사용자 질문/Context를 반영한 화면 표시 순서 */
        int displayOrder,

        /** 사용자 질문/Context 기준 화면 우선 강조 여부 */
        boolean focused,

        ConsultationCategory category,

        String title,

        /** 현재 Section의 공식 API 준비 상태 */
        ConsultationSectionStatus status,

        String storyText,

        List<String> storyEmphasis

) {

    public enum ConsultationSectionStatus {

        /**
         * 상담용 deterministic 분석 실행선 자체가
         * 현재 요청에서 준비되지 않았다.
         */
        ANALYSIS_UNAVAILABLE,

        /**
         * 전체 Category 구조에는 존재하지만
         * 현재 요청 Prompt Section에는 포함되지 않았다.
         */
        NOT_REQUESTED,

        /**
         * 요청은 되었지만 Java 근거상
         * 독립 Meaning을 확정하지 않는다.
         */
        UNAVAILABLE,

        /**
         * Java Meaning은 준비됐지만
         * 최종 Story Generation 결과가 없다.
         */
        GENERATION_UNAVAILABLE,

        /**
         * 검증된 최종 Story가 존재한다.
         */
        READY
    }

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

        Objects.requireNonNull(
                status,
                "ConsultationSectionResponse status는 필수입니다."
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

        /*
         * READY는 반드시 실제 Story를 가진다.
         */
        if (
                status
                        == ConsultationSectionStatus.READY
                        &&
                        storyText.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "READY 상태의 ConsultationSectionResponse에는 storyText가 필요합니다."
            );
        }

        /*
         * 분석 자체가 없거나 요청되지 않은 Category에는
         * 사용자 상담문을 넣지 않는다.
         */
        if (
                (
                        status
                                == ConsultationSectionStatus.ANALYSIS_UNAVAILABLE

                                ||

                                status
                                        == ConsultationSectionStatus.NOT_REQUESTED
                )
                        &&
                        !storyText.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "ANALYSIS_UNAVAILABLE/NOT_REQUESTED 상태에는 storyText를 넣을 수 없습니다."
            );
        }

        /*
         * READY가 아닌 상태에서는
         * Story 강조값을 노출하지 않는다.
         */
        if (
                (
                        status
                                == ConsultationSectionStatus.ANALYSIS_UNAVAILABLE

                                ||

                                status
                                        == ConsultationSectionStatus.NOT_REQUESTED

                                ||

                                status
                                        == ConsultationSectionStatus.UNAVAILABLE

                                ||

                                status
                                        == ConsultationSectionStatus.GENERATION_UNAVAILABLE
                )
                        &&
                        !storyEmphasis.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "READY가 아닌 ConsultationSectionResponse에는 storyEmphasis를 넣을 수 없습니다."
            );
        }

        /*
         * 요청됐지만 결과를 제공할 수 없는 경우에는
         * 사용자에게 이유를 설명하는 안전 안내문이 필요하다.
         */
        if (
                (
                        status
                                == ConsultationSectionStatus.UNAVAILABLE

                                ||

                                status
                                        == ConsultationSectionStatus.GENERATION_UNAVAILABLE
                )
                        &&
                        storyText.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "UNAVAILABLE/GENERATION_UNAVAILABLE 상태에는 사용자 안내 storyText가 필요합니다."
            );
        }
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