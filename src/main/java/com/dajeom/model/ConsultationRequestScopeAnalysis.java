package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 19차.
 *
 * 하나의 사주 분석 요청에서
 * 사용자가 실제로 원하는 상담 범위를
 * 정규화한 결과.
 *
 * =================================================
 *
 * 이 객체는 명리 계산 결과가 아니다.
 *
 * 사용자 요청:
 *
 * - 종합운
 * - 명시적 분야 선택
 * - 자유 질문
 *
 * 을 11개 ConsultationCategory 경계와
 * 연결하기 위한 Request Scope다.
 *
 * =================================================
 *
 * selectedCategories
 *
 * 이미 확정된 ConsultationCategory.
 *
 * 종합운:
 * -> 11개 전체.
 *
 * 명시적 선택:
 * -> 선택한 Category.
 *
 * 자유 질문만 존재:
 * -> 빈 목록.
 * -> 아직 Category Resolution 필요.
 *
 * =================================================
 *
 * consultationQuestion
 *
 * 사용자의 자유 질문 원문.
 *
 * 이 단계에서는 내용을 명리 규칙으로
 * 해석하거나 특정 Category로 추측하지 않는다.
 *
 * =================================================
 *
 * comprehensiveRequest
 *
 * 사용자가 별도 질문/분야를 지정하지 않은
 * 전체 종합운 요청 여부.
 */
public record ConsultationRequestScopeAnalysis(

        List<ConsultationCategory> selectedCategories,

        Optional<String> consultationQuestion,

        boolean comprehensiveRequest

) {

    public ConsultationRequestScopeAnalysis {

        selectedCategories =
                selectedCategories == null
                        ? List.of()
                        : selectedCategories
                        .stream()
                        .map(
                                category ->
                                        Objects.requireNonNull(
                                                category,
                                                "선택 상담 분야에는 null을 넣을 수 없습니다."
                                        )
                        )
                        .distinct()
                        .sorted(
                                Comparator.comparingInt(
                                        ConsultationCategory
                                                ::getOrder
                                )
                        )
                        .toList();

        consultationQuestion =
                normalizeQuestion(
                        consultationQuestion
                );

        /*
         * =================================================
         * 종합운 요청
         * =================================================
         *
         * 종합운이면:
         *
         * 질문 없음
         * +
         * 11개 Category 전체
         *
         * 를 유지한다.
         */

        if (
                comprehensiveRequest
        ) {

            if (
                    consultationQuestion.isPresent()
            ) {

                throw new IllegalArgumentException(
                        "종합운 요청에는 특정 자유 질문을 함께 지정하지 않습니다."
                );
            }

            if (
                    selectedCategories.size()
                            != ConsultationCategory
                            .values()
                            .length
            ) {

                throw new IllegalArgumentException(
                        "종합운 요청에는 11개 ConsultationCategory가 모두 필요합니다."
                );
            }

            for (
                    ConsultationCategory category
                    : ConsultationCategory.values()
            ) {

                if (
                        !selectedCategories.contains(
                                category
                        )
                ) {

                    throw new IllegalArgumentException(
                            "종합운 요청의 ConsultationCategory가 누락되었습니다: "
                                    + category
                    );
                }
            }
        }

        /*
         * =================================================
         * 비종합 요청
         * =================================================
         *
         * Category도 없고 Question도 없다면
         * 의미 없는 Request Scope가 된다.
         *
         * 이런 경우는 Analyzer에서
         * 종합운으로 변환되어야 한다.
         */

        if (
                !comprehensiveRequest
                        && selectedCategories.isEmpty()
                        && consultationQuestion.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "비종합 상담 요청에는 선택 분야 또는 자유 질문이 필요합니다."
            );
        }
    }

    /*
     * =================================================
     * 요청 종류
     * =================================================
     */

    public boolean hasSelectedCategories() {

        return !selectedCategories
                .isEmpty();
    }

    public boolean hasQuestion() {

        return consultationQuestion
                .isPresent();
    }

    /**
     * 자유 질문만 있고
     * ConsultationCategory가 아직 확정되지 않은 상태.
     *
     * 이 단계에서 임의 키워드 분류를 하지 않는다.
     */
    public boolean requiresCategoryResolution() {

        return !comprehensiveRequest
                && selectedCategories.isEmpty()
                && consultationQuestion.isPresent();
    }

    /**
     * 현재 요청에서 사용할 Category가
     * 이미 deterministic하게 확정된 상태.
     */
    public boolean categorySelectionResolved() {

        return comprehensiveRequest
                || hasSelectedCategories();
    }

    public boolean accepts(
            ConsultationCategory category
    ) {

        return category != null
                && selectedCategories.contains(
                category
        );
    }

    public int selectedCategoryCount() {

        return selectedCategories
                .size();
    }

    private static Optional<String>
    normalizeQuestion(
            Optional<String> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Optional.empty();
        }

        String normalized =
                source
                        .orElseThrow()
                        .trim();

        if (
                normalized.isBlank()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                normalized
        );
    }
}