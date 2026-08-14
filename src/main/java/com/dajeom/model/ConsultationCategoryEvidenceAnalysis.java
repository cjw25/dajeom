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
 * 11개 ConsultationCategory에 대한
 * deterministic Evidence View 최종 Aggregate.
 *
 * =================================================
 *
 * Anchor:
 *
 * 18차 LifeDomainAnalysis
 *
 * =================================================
 *
 * 결과:
 *
 * ConsultationCategoryEvidenceView × 11
 *
 * =================================================
 *
 * 모든 Category 객체를 유지한다.
 *
 * 다만:
 *
 * PAST_LUCK
 * FUTURE_LUCK
 * LEGAL_RISK
 *
 * 처럼 현재 입력만으로 만들 수 없는 View는
 * 빈 Evidence를 조작해서 채우지 않고
 * unavailableReason으로 상태를 남긴다.
 *
 * =================================================
 *
 * 이 객체에서도:
 *
 * supportScore
 * cautionScore
 * polarity
 * intensity
 *
 * 를 계산하지 않는다.
 */
public record ConsultationCategoryEvidenceAnalysis(

        LifeDomainAnalysis lifeDomainAnalysis,

        List<ConsultationCategoryEvidenceView> views

) {

    public ConsultationCategoryEvidenceAnalysis {

        Objects.requireNonNull(
                lifeDomainAnalysis,
                "ConsultationCategoryEvidenceAnalysis에는 LifeDomainAnalysis가 필요합니다."
        );

        views =
                views == null
                        ? List.of()
                        : views
                        .stream()
                        .peek(
                                view ->
                                        Objects.requireNonNull(
                                                view,
                                                "ConsultationCategoryEvidenceView는 null일 수 없습니다."
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

        validateAllCategories(
                views
        );
    }

    /*
     * =================================================
     * Category 조회
     * =================================================
     */

    public Optional<ConsultationCategoryEvidenceView>
    find(
            ConsultationCategory category
    ) {

        if (
                category == null
        ) {

            return Optional.empty();
        }

        return views
                .stream()
                .filter(
                        view ->
                                view.category()
                                        == category
                )
                .findFirst();
    }

    public ConsultationCategoryEvidenceView get(
            ConsultationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "ConsultationCategory가 필요합니다."
        );

        return find(
                category
        )
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "ConsultationCategory Evidence View를 찾을 수 없습니다: "
                                                + category
                                )
                );
    }

    /*
     * =================================================
     * Availability
     * =================================================
     */

    public List<ConsultationCategoryEvidenceView>
    availableViews() {

        return views
                .stream()
                .filter(
                        ConsultationCategoryEvidenceView
                                ::available
                )
                .toList();
    }

    public List<ConsultationCategoryEvidenceView>
    unavailableViews() {

        return views
                .stream()
                .filter(
                        ConsultationCategoryEvidenceView
                                ::unavailable
                )
                .toList();
    }

    public boolean allCategoriesAvailable() {

        return unavailableViews()
                .isEmpty();
    }

    /*
     * =================================================
     * 18차 불확실성 전달
     * =================================================
     */

    public boolean completeChart() {

        return lifeDomainAnalysis
                .completeChart();
    }

    public boolean uncertaintyExists() {

        return lifeDomainAnalysis
                .uncertaintyExists()

                || views
                .stream()
                .anyMatch(
                        ConsultationCategoryEvidenceView
                                ::timelineUncertaintyExists
                );
    }

    /*
     * =================================================
     * 전체 Evidence Match
     * =================================================
     *
     * Category 사이에 같은 원국 Match가
     * 재사용될 수 있으므로
     * 여기서 중복 제거하지 않는다.
     *
     * 같은 deterministic Match가:
     *
     * PERSONALITY
     * CAREER
     *
     * 서로 다른 사용자 View에
     * 동시에 사용되는 것은 정상이다.
     */

    public int totalRoutedMatchCount() {

        return views
                .stream()
                .mapToInt(
                        ConsultationCategoryEvidenceView
                                ::matchCount
                )
                .sum();
    }

    /*
     * =================================================
     * 11개 Category 검증
     * =================================================
     */

    private static void validateAllCategories(
            List<ConsultationCategoryEvidenceView> views
    ) {

        if (
                views.size()
                        != ConsultationCategory
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "ConsultationCategoryEvidenceAnalysis에는 11개 ConsultationCategory View가 모두 필요합니다. "
                            + "actual="
                            + views.size()
                            + ", expected="
                            + ConsultationCategory.values().length
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
                : views
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
                        "ConsultationCategory Evidence View가 중복되었습니다: "
                                + view.category()
                );
            }
        }

        List<ConsultationCategory> missing =
                new ArrayList<>();

        for (
                ConsultationCategory category
                : ConsultationCategory.values()
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
                    "ConsultationCategory Evidence View가 누락되었습니다: "
                            + missing
            );
        }
    }
}