package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 19차.
 *
 * 사용자 상담 요청 범위와
 * 11개 ConsultationCategory Evidence View를
 * 실제로 결합한 결과.
 *
 * =================================================
 *
 * 구성:
 *
 * 1.
 * ConsultationRequestScopeAnalysis
 *
 * 사용자 요청의 범위.
 *
 * =================================================
 *
 * 2.
 * ConsultationQuestionRoutingAnalysis
 *
 * 자유 질문의 Category Routing 상태.
 *
 * 질문 의미를 Java에서 임의 분류하지 않는다.
 *
 * =================================================
 *
 * 3.
 * ConsultationCategoryEvidenceAnalysis
 *
 * 18차 LifeDomainAnalysis에서 Routing된
 * 11개 ConsultationCategory Evidence View.
 *
 * =================================================
 *
 * 4.
 * selectedViews
 *
 * 현재 요청에서 실제 소비할 View.
 *
 * =================================================
 *
 * 종합운:
 *
 * -> 11개 View 전체
 *
 * =================================================
 *
 * 명시적 분야 선택:
 *
 * -> 선택된 View만
 *
 * =================================================
 *
 * 자유 질문만 존재:
 *
 * -> selectedViews empty
 * -> QuestionRouting = GROUNDING_REQUIRED
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * Category 선택 상태와
 * Evidence availability는 서로 다르다.
 *
 * 예:
 *
 * FUTURE_LUCK이 사용자 요청에 포함되어도
 * 미래 Timeline Range가 아직 없으면
 * 해당 View는 selected 상태를 유지하면서
 * unavailable일 수 있다.
 */
public record ConsultationRequestEvidenceAnalysis(

        ConsultationRequestScopeAnalysis requestScopeAnalysis,

        ConsultationQuestionRoutingAnalysis
        questionRoutingAnalysis,

        ConsultationCategoryEvidenceAnalysis
        categoryEvidenceAnalysis,

        List<ConsultationCategoryEvidenceView> selectedViews

) {

    public ConsultationRequestEvidenceAnalysis {

        Objects.requireNonNull(
                requestScopeAnalysis,
                "ConsultationRequestEvidenceAnalysis에는 ConsultationRequestScopeAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                questionRoutingAnalysis,
                "ConsultationRequestEvidenceAnalysis에는 ConsultationQuestionRoutingAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                categoryEvidenceAnalysis,
                "ConsultationRequestEvidenceAnalysis에는 ConsultationCategoryEvidenceAnalysis가 필요합니다."
        );

        selectedViews =
                selectedViews == null
                        ? List.of()
                        : selectedViews
                        .stream()
                        .map(
                                view ->
                                        Objects.requireNonNull(
                                                view,
                                                "선택된 ConsultationCategoryEvidenceView는 null일 수 없습니다."
                                        )
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        view ->
                                                view
                                                        .category()
                                                        .getOrder()
                                )
                        )
                        .toList();

        validateQuestionRouting(
                requestScopeAnalysis,
                questionRoutingAnalysis
        );

        validateSelectedViews(
                requestScopeAnalysis,
                questionRoutingAnalysis,
                categoryEvidenceAnalysis,
                selectedViews
        );
    }

    /*
     * =================================================
     * 요청 상태
     * =================================================
     */

    public boolean comprehensiveRequest() {

        return requestScopeAnalysis
                .comprehensiveRequest();
    }

    public boolean hasQuestion() {

        return questionRoutingAnalysis
                .hasQuestion();
    }

    public Optional<String> consultationQuestion() {

        return questionRoutingAnalysis
                .question();
    }

    public boolean requiresCategoryResolution() {

        return questionRoutingAnalysis
                .groundingRequired();
    }

    public boolean questionExplicitlyScoped() {

        return questionRoutingAnalysis
                .explicitlyScoped();
    }

    /*
     * =================================================
     * 선택 Category
     * =================================================
     */

    public List<ConsultationCategory> selectedCategories() {

        return requestScopeAnalysis
                .selectedCategories();
    }

    public int selectedCategoryCount() {

        return selectedViews
                .size();
    }

    public boolean hasSelectedViews() {

        return !selectedViews
                .isEmpty();
    }

    /*
     * =================================================
     * View 조회
     * =================================================
     */

    public Optional<ConsultationCategoryEvidenceView>
    findSelectedView(
            ConsultationCategory category
    ) {

        if (
                category == null
        ) {

            return Optional.empty();
        }

        return selectedViews
                .stream()
                .filter(
                        view ->
                                view.category()
                                        == category
                )
                .findFirst();
    }

    public ConsultationCategoryEvidenceView
    getSelectedView(
            ConsultationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "ConsultationCategory가 필요합니다."
        );

        return findSelectedView(
                category
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "현재 상담 요청에 선택되지 않은 ConsultationCategory입니다: "
                                                + category
                                )
                );
    }

    public boolean selected(
            ConsultationCategory category
    ) {

        return findSelectedView(
                category
        )
                .isPresent();
    }

    /*
     * =================================================
     * Availability
     * =================================================
     */

    public List<ConsultationCategoryEvidenceView>
    availableSelectedViews() {

        return selectedViews
                .stream()
                .filter(
                        ConsultationCategoryEvidenceView
                                ::available
                )
                .toList();
    }

    public List<ConsultationCategoryEvidenceView>
    unavailableSelectedViews() {

        return selectedViews
                .stream()
                .filter(
                        ConsultationCategoryEvidenceView
                                ::unavailable
                )
                .toList();
    }

    public boolean hasUnavailableSelectedViews() {

        return !unavailableSelectedViews()
                .isEmpty();
    }

    public boolean allSelectedViewsAvailable() {

        return !requiresCategoryResolution()
                && !hasUnavailableSelectedViews();
    }

    /*
     * =================================================
     * 18차 상태 전달
     * =================================================
     */

    public boolean completeChart() {

        return categoryEvidenceAnalysis
                .completeChart();
    }

    public boolean uncertaintyExists() {

        return categoryEvidenceAnalysis
                .uncertaintyExists();
    }

    /*
     * =================================================
     * Match
     * =================================================
     */

    public int selectedRoutedMatchCount() {

        return selectedViews
                .stream()
                .mapToInt(
                        ConsultationCategoryEvidenceView
                                ::matchCount
                )
                .sum();
    }

    /*
     * =================================================
     * Question Routing 검증
     * =================================================
     */

    private static void validateQuestionRouting(

            ConsultationRequestScopeAnalysis requestScope,

            ConsultationQuestionRoutingAnalysis questionRouting

    ) {

        if (
                requestScope.hasQuestion()
                        != questionRouting.hasQuestion()
        ) {

            throw new IllegalArgumentException(
                    "상담 요청의 자유 질문 상태와 Question Routing 상태가 일치하지 않습니다."
            );
        }

        if (
                !Objects.equals(
                        requestScope.consultationQuestion(),
                        questionRouting.question()
                )
        ) {

            throw new IllegalArgumentException(
                    "상담 요청과 Question Routing이 서로 다른 자유 질문을 참조합니다."
            );
        }

        if (
                requestScope
                        .requiresCategoryResolution()
        ) {

            if (
                    !questionRouting
                            .blindAnalysisReady()
                            && !questionRouting
                            .groundingRequired()
            ) {

                throw new IllegalArgumentException(
                        "Category 미선택 자유 질문은 BLIND_ANALYSIS_READY 또는 GROUNDING_REQUIRED 상태여야 합니다."
                );
            }

            return;
        }

        if (
                requestScope.hasQuestion()
                        && requestScope.hasSelectedCategories()
        ) {

            if (
                    !questionRouting
                            .explicitlyScoped()
            ) {

                throw new IllegalArgumentException(
                        "사용자가 Category를 명시한 자유 질문은 EXPLICITLY_SCOPED 상태여야 합니다."
                );
            }

            if (
                    !questionRouting
                            .routedCategories()
                            .equals(
                                    requestScope
                                            .selectedCategories()
                            )
            ) {

                throw new IllegalArgumentException(
                        "사용자가 선택한 ConsultationCategory와 Question Routing Category가 일치하지 않습니다."
                );
            }

            return;
        }

        if (
                !requestScope.hasQuestion()
                        && !questionRouting.notApplicable()
        ) {

            throw new IllegalArgumentException(
                    "자유 질문이 없는 요청은 NOT_APPLICABLE Question Routing 상태여야 합니다."
            );
        }
    }

    /*
     * =================================================
     * View 검증
     * =================================================
     */

    private static void validateSelectedViews(

            ConsultationRequestScopeAnalysis requestScope,

            ConsultationQuestionRoutingAnalysis questionRouting,

            ConsultationCategoryEvidenceAnalysis evidenceAnalysis,

            List<ConsultationCategoryEvidenceView> selectedViews

    ) {

        /*
         * 자유 질문만 존재하고 Category 미확정.
         */

        if (
                requestScope
                        .requiresCategoryResolution()
        ) {

            if (
                    questionRouting.blindAnalysisReady()
            ) {

                List<ConsultationCategory> expectedBlindCategories =
                        questionRouting.routedCategories();

                if (selectedViews.size() != expectedBlindCategories.size()) {
                    throw new IllegalArgumentException(
                            "BLIND_ANALYSIS_READY Category 수와 selectedViews 수가 일치하지 않습니다."
                    );
                }

                for (ConsultationCategory category : expectedBlindCategories) {
                    ConsultationCategoryEvidenceView expected = evidenceAnalysis.get(category);
                    boolean exists = selectedViews.stream().anyMatch(view -> view == expected);
                    if (!exists) {
                        throw new IllegalArgumentException(
                                "BLIND_ANALYSIS_READY에 필요한 공식 Evidence View가 누락되었습니다: " + category
                        );
                    }
                }

                return;
            }

            if (
                    !selectedViews.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "GROUNDING_REQUIRED 자유 질문 요청에는 selectedViews를 미리 지정할 수 없습니다."
                );
            }

            return;
        }

        if (
                !requestScope
                        .categorySelectionResolved()
        ) {

            throw new IllegalArgumentException(
                    "상담 Category 상태를 결정할 수 없습니다."
            );
        }

        List<ConsultationCategory> expectedCategories =
                requestScope
                        .selectedCategories();

        if (
                selectedViews.size()
                        != expectedCategories.size()
        ) {

            throw new IllegalArgumentException(
                    "선택된 ConsultationCategory 수와 Evidence View 수가 일치하지 않습니다. "
                            + "categories="
                            + expectedCategories.size()
                            + ", views="
                            + selectedViews.size()
            );
        }

        Map<
                ConsultationCategory,
                ConsultationCategoryEvidenceView
                > indexed =
                new EnumMap<>(
                        ConsultationCategory.class
                );

        for (
                ConsultationCategoryEvidenceView view
                : selectedViews
        ) {

            ConsultationCategoryEvidenceView previous =
                    indexed.put(
                            view.category(),
                            view
                    );

            if (
                    previous != null
            ) {

                throw new IllegalArgumentException(
                        "선택된 ConsultationCategory Evidence View가 중복되었습니다: "
                                + view.category()
                );
            }

            ConsultationCategoryEvidenceView canonical =
                    evidenceAnalysis
                            .get(
                                    view.category()
                            );

            if (
                    canonical != view
            ) {

                throw new IllegalArgumentException(
                        "선택된 ConsultationCategory Evidence View가 현재 EvidenceAnalysis의 공식 객체가 아닙니다: "
                                + view.category()
                );
            }
        }

        List<ConsultationCategory> missing =
                new ArrayList<>();

        for (
                ConsultationCategory category
                : expectedCategories
        ) {

            if (
                    !indexed.containsKey(
                            category
                    )
            ) {

                missing.add(
                        category
                );
            }
        }

        if (
                !missing.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "사용자가 요청한 ConsultationCategory Evidence View가 누락되었습니다: "
                            + missing
            );
        }

        for (
                ConsultationCategory category
                : indexed.keySet()
        ) {

            if (
                    !expectedCategories.contains(
                            category
                    )
            ) {

                throw new IllegalArgumentException(
                        "사용자가 요청하지 않은 ConsultationCategory Evidence View가 포함되었습니다: "
                                + category
                );
            }
        }
    }
}