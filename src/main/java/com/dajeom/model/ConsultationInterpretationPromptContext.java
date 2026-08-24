package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationQuestionRoutingStatus;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 20차.
 *
 * OpenAI 등 LLM 호출 직전에 사용하는
 * 최종 Interpretation Prompt Context.
 *
 * =================================================
 *
 * 이 객체까지가 Java deterministic 계층의 책임이다.
 *
 * 이후 LLM은:
 *
 * - 설명
 * - 상담 문장화
 * - 기술 용어의 일상어 변환
 * - 사용자 질문에 맞는 표현
 *
 * 만 담당한다.
 *
 * =================================================
 *
 * LLM에게 허용하지 않는 것:
 *
 * - 새로운 사주 계산
 * - 누락된 계산값 추정
 * - Evidence 수정
 * - 단일 길흉 점수 생성
 * - 불확실 Scenario 임의 선택
 * - 직접 사건 예언
 *
 * =================================================
 *
 * globalInstructions
 *
 * 모든 Section에 공통으로 적용되는
 * AI Interpretation 계약.
 *
 * =================================================
 *
 * sections
 *
 * 사용자가 실제로 요청한
 * ConsultationCategory만 포함한다.
 *
 * 종합운이면 11개.
 *
 * 명시 선택이면 선택한 Category만.
 *
 * 자유 질문 Category가 아직
 * GROUNDING_REQUIRED 상태이면 비어 있을 수 있다.
 */
public record ConsultationInterpretationPromptContext(

        String groundingVersion,

        ConsultationQuestionRoutingStatus
        questionRoutingStatus,

        Optional<String> consultationQuestion,

        boolean completeChart,

        boolean uncertaintyExists,

        List<String> globalInstructions,

        List<ConsultationInterpretationPromptSection> sections

) {

    public ConsultationInterpretationPromptContext {

        groundingVersion =
                requireText(
                        groundingVersion,
                        "Grounding Version은 필수입니다."
                );

        Objects.requireNonNull(
                questionRoutingStatus,
                "Question Routing Status는 필수입니다."
        );

        consultationQuestion =
                normalizeOptionalText(
                        consultationQuestion
                );

        globalInstructions =
                normalizeInstructions(
                        globalInstructions
                );

        if (
                globalInstructions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Prompt Context에는 최소 하나 이상의 globalInstruction이 필요합니다."
            );
        }

        sections =
                sections == null
                        ? List.of()
                        : sections
                        .stream()
                        .map(
                                section ->
                                        Objects.requireNonNull(
                                                section,
                                                "Prompt Section은 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        ConsultationInterpretationPromptSection
                                                ::order
                                )
                        )
                        .toList();

        validateQuestionState(
                questionRoutingStatus,
                consultationQuestion,
                sections
        );

        validateSections(
                sections
        );
    }

    public boolean hasQuestion() {

        return consultationQuestion
                .isPresent();
    }

    public boolean categoryResolutionRequired() {

        return questionRoutingStatus
                .groundingRequired();
    }

    public boolean hasSections() {

        return !sections
                .isEmpty();
    }

    public Optional<ConsultationInterpretationPromptSection>
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

    public int sectionCount() {

        return sections
                .size();
    }

    public int evidenceCount() {

        return sections
                .stream()
                .mapToInt(
                        ConsultationInterpretationPromptSection
                                ::evidenceCount
                )
                .sum();
    }

    public boolean hasUnavailableSections() {

        return sections
                .stream()
                .anyMatch(
                        ConsultationInterpretationPromptSection
                                ::unavailable
                );
    }

    private static void validateQuestionState(

            ConsultationQuestionRoutingStatus status,

            Optional<String> question,

            List<ConsultationInterpretationPromptSection> sections

    ) {

        switch (status) {

            case NOT_APPLICABLE -> {

                if (
                        question.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "NOT_APPLICABLE Prompt Context에는 자유 질문이 존재할 수 없습니다."
                    );
                }
            }

            case EXPLICITLY_SCOPED -> {

                if (
                        question.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "EXPLICITLY_SCOPED Prompt Context에는 자유 질문이 필요합니다."
                    );
                }
            }

            case BLIND_ANALYSIS_READY -> {

                if (
                        question.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "BLIND_ANALYSIS_READY 상태에는 자유 질문이 필요합니다."
                    );
                }

                if (
                        sections.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "BLIND_ANALYSIS_READY 상태에는 블라인드 분석 Section이 필요합니다."
                    );
                }
            }

            case GROUNDING_REQUIRED -> {

                if (
                        question.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "GROUNDING_REQUIRED Prompt Context에는 자유 질문이 필요합니다."
                    );
                }

                if (
                        !sections.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "Category가 아직 미확정된 자유 질문에는 Prompt Section을 미리 넣을 수 없습니다."
                    );
                }
            }
        }
    }

    private static void validateSections(
            List<ConsultationInterpretationPromptSection> sections
    ) {

        Map<
                ConsultationCategory,
                ConsultationInterpretationPromptSection
                > indexed =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        for (
                ConsultationInterpretationPromptSection section
                : sections
        ) {

            ConsultationInterpretationPromptSection previous =
                    indexed.put(
                            section.category(),
                            section
                    );

            if (
                    previous != null
            ) {

                throw new IllegalArgumentException(
                        "Prompt Section의 ConsultationCategory가 중복되었습니다: "
                                + section.category()
                );
            }

            if (
                    section.order()
                            != section
                            .category()
                            .getOrder()
            ) {

                throw new IllegalArgumentException(
                        "Prompt Section order와 ConsultationCategory order가 일치하지 않습니다: "
                                + section.category()
                );
            }
        }
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

    private static Optional<String>
    normalizeOptionalText(
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

        if (
                value.isBlank()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                value
        );
    }

    private static List<String>
    normalizeInstructions(
            List<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return List.of();
        }

        return source
                .stream()
                .map(
                        instruction ->
                                requireText(
                                        instruction,
                                        "Global Instruction은 비어 있을 수 없습니다."
                                )
                )
                .distinct()
                .toList();
    }
}