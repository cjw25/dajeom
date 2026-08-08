package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * 사용자에게 제공할 전체 사주 상담 구조.
 *
 * 현재 구성:
 *
 * - 상담 대상 연도
 * - 전체 시작 문장
 * - 11개 상담 분야
 * - 전체 마무리 문장
 *
 * 이 모델은:
 *
 * 웹 화면
 * 앱 화면
 * OpenAI 입력
 * TTS
 * Live2D
 *
 * 계층에서 공통으로 사용할 수 있다.
 */
public record ConsultationAnalysis(

        int targetYear,

        String openingMessage,

        List<ConsultationSection> sections,

        String closingMessage,

        String ruleVersion

) {

    public ConsultationAnalysis {

        Objects.requireNonNull(
                openingMessage,
                "전체 상담 시작 문장은 필수입니다."
        );

        Objects.requireNonNull(
                sections,
                "상담 분야 목록은 필수입니다."
        );

        Objects.requireNonNull(
                closingMessage,
                "전체 상담 마무리 문장은 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        if (
                openingMessage.isBlank()
                        || closingMessage.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "상담 시작/마무리 문장은 비어 있을 수 없습니다."
            );
        }

        sections =
                List.copyOf(
                        sections
                );

        /*
         * 현재 다점에서 확정한
         * 모든 상담 분야가 반드시 존재해야 한다.
         */
        if (
                sections.size()
                        != ConsultationCategory.values().length
        ) {

            throw new IllegalArgumentException(
                    "모든 상담 분야가 포함되어야 합니다."
            );
        }

        Set<ConsultationCategory> categories =
                EnumSet.noneOf(
                        ConsultationCategory.class
                );

        for (
                ConsultationSection section
                : sections
        ) {

            if (
                    !categories.add(
                            section.category()
                    )
            ) {

                throw new IllegalArgumentException(
                        "중복된 상담 분야가 있습니다: "
                                + section.category()
                );
            }
        }

        if (
                categories.size()
                        != ConsultationCategory.values().length
        ) {

            throw new IllegalArgumentException(
                    "일부 상담 분야가 누락되었습니다."
            );
        }
    }

    public Optional<ConsultationSection> find(
            ConsultationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "상담 분야는 필수입니다."
        );

        return sections
                .stream()
                .filter(
                        section ->
                                section.category()
                                        == category
                )
                .findFirst();
    }

    public ConsultationSection get(
            ConsultationCategory category
    ) {

        return find(
                category
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "상담 분야를 찾을 수 없습니다: "
                                                + category
                                )
                );
    }

    /**
     * 전체 기본 TTS 문자열.
     *
     * 모든 분야를 한 번에 읽어야 하는 경우
     * 사용할 수 있다.
     */
    public String fullNarrationText() {

        StringJoiner joiner =
                new StringJoiner(
                        " "
                );

        joiner.add(
                openingMessage
        );

        for (
                ConsultationSection section
                : sections
        ) {

            joiner.add(
                    section.narrationText()
            );
        }

        joiner.add(
                closingMessage
        );

        return joiner.toString();
    }
}