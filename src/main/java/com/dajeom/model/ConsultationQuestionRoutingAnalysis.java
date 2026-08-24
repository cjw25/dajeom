package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationQuestionRoutingStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 19차.
 *
 * 사용자의 자유 상담 질문과
 * ConsultationCategory 사이의 Routing 상태.
 *
 * =================================================
 *
 * 이 모델은 명리 계산 결과가 아니다.
 *
 * 또한 자유 질문의 자연어 의미를
 * Java 규칙으로 해석하지 않는다.
 *
 * =================================================
 *
 * status
 *
 * NOT_APPLICABLE
 * -> 질문 없음
 *
 * EXPLICITLY_SCOPED
 * -> 사용자가 Category를 직접 선택함
 *
 * GROUNDING_REQUIRED
 * -> 질문만 존재하고 Category는 아직 미확정
 *
 * =================================================
 *
 * routedCategories
 *
 * EXPLICITLY_SCOPED일 때만 존재한다.
 *
 * GROUNDING_REQUIRED일 때는
 * Category를 추측하지 않으므로 빈 목록이다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * "돈"
 * -> WEALTH
 *
 * "결혼"
 * -> PARTNER_LUCK
 *
 * "이직"
 * -> CAREER
 *
 * 같은 문자열 키워드 변환을
 * 이 모델과 19차 Analyzer에서 수행하지 않는다.
 */
public record ConsultationQuestionRoutingAnalysis(

        ConsultationQuestionRoutingStatus status,

        Optional<String> question,

        List<ConsultationCategory> routedCategories

) {

    public ConsultationQuestionRoutingAnalysis {

        Objects.requireNonNull(
                status,
                "ConsultationQuestionRoutingStatus는 필수입니다."
        );

        question =
                normalizeQuestion(
                        question
                );

        routedCategories =
                routedCategories == null
                        ? List.of()
                        : routedCategories
                        .stream()
                        .map(
                                category ->
                                        Objects.requireNonNull(
                                                category,
                                                "질문 Routing Category에는 null을 넣을 수 없습니다."
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

        switch (status) {

            case NOT_APPLICABLE -> {

                if (
                        question.isPresent()
                                || !routedCategories.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "NOT_APPLICABLE 질문 Routing에는 질문이나 Category를 넣을 수 없습니다."
                    );
                }
            }

            case EXPLICITLY_SCOPED -> {

                if (
                        question.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "EXPLICITLY_SCOPED 질문 Routing에는 자유 질문이 필요합니다."
                    );
                }

                if (
                        routedCategories.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "EXPLICITLY_SCOPED 질문 Routing에는 사용자가 선택한 ConsultationCategory가 필요합니다."
                    );
                }
            }

            case BLIND_ANALYSIS_READY -> {

                if (
                        question.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "BLIND_ANALYSIS_READY 질문 Routing에는 자유 질문이 필요합니다."
                    );
                }

                if (
                        routedCategories.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "BLIND_ANALYSIS_READY 상태에는 블라인드 분석을 보존할 ConsultationCategory가 필요합니다."
                    );
                }
            }

            case GROUNDING_REQUIRED -> {

                if (
                        question.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "GROUNDING_REQUIRED 질문 Routing에는 자유 질문이 필요합니다."
                    );
                }

                if (
                        !routedCategories.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "GROUNDING_REQUIRED 상태에서는 ConsultationCategory를 미리 결정할 수 없습니다."
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 상태
     * =================================================
     */

    public boolean hasQuestion() {

        return question
                .isPresent();
    }

    public boolean notApplicable() {

        return status
                .notApplicable();
    }

    public boolean explicitlyScoped() {

        return status
                .explicitlyScoped();
    }

    public boolean blindAnalysisReady() {

        return status
                .blindAnalysisReady();
    }

    public boolean groundingRequired() {

        return status
                .groundingRequired();
    }

    /**
     * 현재 19차에서 Category가 확정되어 있는지.
     *
     * 질문이 없는 경우에는
     * Category Routing 자체가 필요 없으므로
     * resolved로 보지 않는다.
     */
    public boolean categoryResolved() {

        return explicitlyScoped()
                || blindAnalysisReady();
    }

    public boolean routesTo(
            ConsultationCategory category
    ) {

        return category != null
                && routedCategories.contains(
                category
        );
    }

    public int routedCategoryCount() {

        return routedCategories
                .size();
    }

    /*
     * =================================================
     * Normalize
     * =================================================
     */

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