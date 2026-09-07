package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * 일반 격국 통합 Calculator의 최종 결과.
 *
 * =================================================
 *
 * ASSESSED
 *
 * → assessment 존재
 *
 * =================================================
 *
 * 나머지:
 *
 * - 격국 변화
 * - 복수 변화
 * - 建祿
 * - 陽刃
 * - 月劫
 *
 * → 일반 8격 assessment 없음
 *
 * =================================================
 */
public record StandardPatternAssessmentResult(

        HeavenlyStem dayMaster,

        MonthCommandPatternSelectionAnalysis
        patternSelection,

        StandardPatternAssessmentPath path,

        Optional<StandardPatternAssessment>
        assessment

) {

    public StandardPatternAssessmentResult {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                patternSelection,
                "월령 격국 선택 결과는 필수입니다."
        );

        Objects.requireNonNull(
                path,
                "통합 격국 판정 경로는 필수입니다."
        );

        assessment =
                assessment == null
                        ? Optional.empty()
                        : assessment;

        if (
                patternSelection.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "격국 선택 결과의 일간이 통합 판정 대상과 일치하지 않습니다."
            );
        }

        assessment.ifPresent(
                result -> {

                    if (
                            result.dayMaster()
                                    != dayMaster
                    ) {

                        throw new IllegalArgumentException(
                                "격국 판정 결과의 일간이 일치하지 않습니다."
                        );
                    }
                }
        );

        validatePath(
                patternSelection,
                path,
                assessment
        );
    }

    public boolean assessed() {

        return path
                == StandardPatternAssessmentPath
                .ASSESSED;
    }

    public boolean patternChangeReviewRequired() {

        return path
                == StandardPatternAssessmentPath
                .PATTERN_CHANGE_REVIEW_REQUIRED;
    }

    public boolean multiplePatternChangeReviewRequired() {

        return path
                == StandardPatternAssessmentPath
                .MULTIPLE_PATTERN_CHANGE_REVIEW_REQUIRED;
    }

    public boolean jianluReview() {

        return path
                == StandardPatternAssessmentPath
                .JIANLU_REVIEW;
    }

    public boolean yangBladeReview() {

        return path
                == StandardPatternAssessmentPath
                .YANG_BLADE_REVIEW;
    }

    public boolean yuejieReview() {

        return path
                == StandardPatternAssessmentPath
                .YUEJIE_REVIEW;
    }

    public boolean companionSpecialPath() {

        return jianluReview()
                ||
                yangBladeReview()
                ||
                yuejieReview();
    }

    private static void validatePath(

            MonthCommandPatternSelectionAnalysis selection,

            StandardPatternAssessmentPath path,

            Optional<StandardPatternAssessment> assessment

    ) {

        switch (path) {

            case ASSESSED -> {

                if (
                        selection.path()
                                != MonthCommandPatternSelectionPath
                                .STANDARD_PRIMARY_REVIEW
                ) {

                    throw new IllegalArgumentException(
                            "ASSESSED 경로에는 STANDARD_PRIMARY_REVIEW가 필요합니다."
                    );
                }

                StandardPatternAssessment result =
                        assessment
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "ASSESSED 경로에는 실제 격국 판정 결과가 필요합니다."
                                                )
                                );

                MonthCommandPatternCandidate provisional =
                        selection
                                .provisionalStandardCandidate()
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "ASSESSED 경로에는 선택된 일반 8격 후보가 필요합니다."
                                                )
                                );

                if (
                        result.patternCandidate()
                                != provisional
                ) {

                    throw new IllegalArgumentException(
                            "실제 판정된 격국 후보와 선택된 provisional 후보가 일치하지 않습니다."
                    );
                }
            }

            case PATTERN_CHANGE_REVIEW_REQUIRED -> {

                requireNoAssessment(
                        assessment
                );

                if (
                        selection.path()
                                != MonthCommandPatternSelectionPath
                                .STANDARD_CHANGE_REVIEW
                ) {

                    throw new IllegalArgumentException(
                            "격국 변화 검토 경로가 Selection 결과와 일치하지 않습니다."
                    );
                }
            }

            case MULTIPLE_PATTERN_CHANGE_REVIEW_REQUIRED -> {

                requireNoAssessment(
                        assessment
                );

                if (
                        selection.path()
                                != MonthCommandPatternSelectionPath
                                .STANDARD_MULTIPLE_CHANGE_REVIEW
                ) {

                    throw new IllegalArgumentException(
                            "복수 격국 변화 경로가 Selection 결과와 일치하지 않습니다."
                    );
                }
            }

            case JIANLU_REVIEW -> {

                requireNoAssessment(
                        assessment
                );

                if (
                        selection.path()
                                != MonthCommandPatternSelectionPath
                                .JIANLU_REVIEW
                ) {

                    throw new IllegalArgumentException(
                            "建祿 경로가 Selection 결과와 일치하지 않습니다."
                    );
                }
            }

            case YANG_BLADE_REVIEW -> {

                requireNoAssessment(
                        assessment
                );

                if (
                        selection.path()
                                != MonthCommandPatternSelectionPath
                                .YANG_BLADE_REVIEW
                ) {

                    throw new IllegalArgumentException(
                            "陽刃 경로가 Selection 결과와 일치하지 않습니다."
                    );
                }
            }

            case YUEJIE_REVIEW -> {

                requireNoAssessment(
                        assessment
                );

                if (
                        selection.path()
                                != MonthCommandPatternSelectionPath
                                .YUEJIE_REVIEW
                ) {

                    throw new IllegalArgumentException(
                            "月劫 경로가 Selection 결과와 일치하지 않습니다."
                    );
                }
            }
        }
    }

    private static void requireNoAssessment(
            Optional<StandardPatternAssessment> assessment
    ) {

        if (assessment.isPresent()) {

            throw new IllegalArgumentException(
                    "일반 8격 Rule을 실행하지 않는 경로에 assessment가 존재할 수 없습니다."
            );
        }
    }
}