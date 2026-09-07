package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 월령 격국 후보의 판정 경로 결과.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * STANDARD_PRIMARY_REVIEW
 * → 본래 MAIN 후보가 현재 단계에서 명확
 *
 * STANDARD_CHANGE_REVIEW
 * → MAIN과 비MAIN 투출 후보를 함께 검토
 *
 * STANDARD_MULTIPLE_CHANGE_REVIEW
 * → 복수 투출로 추가 판정 필요
 *
 * JIANLU / YANG_BLADE / YUEJIE
 * → 일반 8격 확정 로직으로 보내지 않음
 *
 * =================================================
 *
 * 아직:
 *
 * - 정관격 成格
 * - 정재격 成格
 * - 칠살격 成格
 * - 상관격 成格
 * - 파격
 *
 * 등을 결정하지 않는다.
 */
public record MonthCommandPatternSelectionAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        MonthCommandPatternSelectionPath path,

        MonthCommandPatternCandidate primaryCandidate,

        Optional<MonthCommandPatternCandidate>
        provisionalStandardCandidate,

        List<MonthCommandPatternCandidate>
        exposedAlternativeCandidates,

        Set<MonthCommandPatternSelectionReason> reasons

) {

    public MonthCommandPatternSelectionAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                path,
                "격국 판정 경로는 필수입니다."
        );

        Objects.requireNonNull(
                primaryCandidate,
                "월령 MAIN 후보는 필수입니다."
        );

        provisionalStandardCandidate =
                provisionalStandardCandidate == null
                        ? Optional.empty()
                        : provisionalStandardCandidate;

        exposedAlternativeCandidates =
                exposedAlternativeCandidates == null
                        ? List.of()
                        : List.copyOf(
                        exposedAlternativeCandidates
                );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(
                        reasons
                );

        if (
                primaryCandidate.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "MAIN 후보의 일간이 분석 대상과 일치하지 않습니다."
            );
        }

        if (
                primaryCandidate.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "MAIN 후보의 월지가 분석 대상과 일치하지 않습니다."
            );
        }

        if (
                !primaryCandidate.main()
        ) {

            throw new IllegalArgumentException(
                    "primaryCandidate는 반드시 월령 MAIN이어야 합니다."
            );
        }

        for (
                MonthCommandPatternCandidate alternative
                : exposedAlternativeCandidates
        ) {

            Objects.requireNonNull(
                    alternative,
                    "변화 후보 목록에 null이 포함될 수 없습니다."
            );

            if (
                    alternative.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "변화 후보의 일간이 일치하지 않습니다."
                );
            }

            if (
                    alternative.monthBranch()
                            != monthBranch
            ) {

                throw new IllegalArgumentException(
                        "변화 후보의 월지가 일치하지 않습니다."
                );
            }

            if (
                    alternative.main()
            ) {

                throw new IllegalArgumentException(
                        "변화 후보에는 MAIN 지장간을 포함할 수 없습니다."
                );
            }

            if (
                    !alternative.exposed()
            ) {

                throw new IllegalArgumentException(
                        "변화 후보는 실제 천간 투출이 필요합니다."
                );
            }
        }

        validatePath(
                path,
                primaryCandidate,
                provisionalStandardCandidate,
                exposedAlternativeCandidates
        );

        if (reasons.isEmpty()) {

            throw new IllegalArgumentException(
                    "격국 선택 경로에는 최소 하나의 이유가 필요합니다."
            );
        }
    }

    /**
     * 현재 단계에서 일반 8격의
     * 기본 후보가 하나로 명확한가.
     *
     * 최종 成格이라는 뜻은 아니다.
     */
    public boolean hasProvisionalStandardCandidate() {

        return provisionalStandardCandidate
                .isPresent();
    }

    public boolean requiresPatternChangeReview() {

        return path
                == MonthCommandPatternSelectionPath
                .STANDARD_CHANGE_REVIEW

                ||

                path
                        == MonthCommandPatternSelectionPath
                        .STANDARD_MULTIPLE_CHANGE_REVIEW;
    }

    public boolean requiresJianluReview() {

        return path
                == MonthCommandPatternSelectionPath
                .JIANLU_REVIEW;
    }

    public boolean requiresYangBladeReview() {

        return path
                == MonthCommandPatternSelectionPath
                .YANG_BLADE_REVIEW;
    }

    public boolean requiresYuejieReview() {

        return path
                == MonthCommandPatternSelectionPath
                .YUEJIE_REVIEW;
    }

    public boolean companionMonthSpecialPath() {

        return requiresJianluReview()
                ||
                requiresYangBladeReview()
                ||
                requiresYuejieReview();
    }

    private static void validatePath(

            MonthCommandPatternSelectionPath path,

            MonthCommandPatternCandidate primary,

            Optional<MonthCommandPatternCandidate> provisional,

            List<MonthCommandPatternCandidate> alternatives

    ) {

        switch (path) {

            case STANDARD_PRIMARY_REVIEW -> {

                if (
                        !primary
                                .standardEightPatternCandidate()
                ) {

                    throw new IllegalArgumentException(
                            "STANDARD_PRIMARY_REVIEW에는 일반 8격 MAIN 후보가 필요합니다."
                    );
                }

                if (
                        !alternatives.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "STANDARD_PRIMARY_REVIEW에는 투출 변화 후보가 없어야 합니다."
                    );
                }

                MonthCommandPatternCandidate selected =
                        provisional
                                .orElseThrow(
                                        () ->
                                                new IllegalArgumentException(
                                                        "STANDARD_PRIMARY_REVIEW에는 임시 일반격 후보가 필요합니다."
                                                )
                                );

                if (
                        selected != primary
                ) {

                    throw new IllegalArgumentException(
                            "현재 단계의 임시 일반격 후보는 MAIN 후보여야 합니다."
                    );
                }
            }

            case STANDARD_CHANGE_REVIEW -> {

                if (
                        !primary
                                .standardEightPatternCandidate()
                ) {

                    throw new IllegalArgumentException(
                            "STANDARD_CHANGE_REVIEW에는 일반 8격 MAIN 후보가 필요합니다."
                    );
                }

                if (
                        alternatives.size()
                                != 1
                ) {

                    throw new IllegalArgumentException(
                            "STANDARD_CHANGE_REVIEW에는 정확히 하나의 비MAIN 투출 후보가 필요합니다."
                    );
                }

                if (
                        provisional.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "격국 변화 검토 중에는 하나의 일반격을 미리 선택하지 않습니다."
                    );
                }
            }

            case STANDARD_MULTIPLE_CHANGE_REVIEW -> {

                if (
                        !primary
                                .standardEightPatternCandidate()
                ) {

                    throw new IllegalArgumentException(
                            "복수 변화 검토에는 일반 8격 MAIN 후보가 필요합니다."
                    );
                }

                if (
                        alternatives.size()
                                < 2
                ) {

                    throw new IllegalArgumentException(
                            "복수 변화 검토에는 둘 이상의 비MAIN 투출 후보가 필요합니다."
                    );
                }

                if (
                        provisional.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "복수 투출 상태에서 임의로 하나의 격을 선택할 수 없습니다."
                    );
                }
            }

            case JIANLU_REVIEW -> {

                if (
                        primary.candidateType()
                                != MonthCommandPatternCandidateType
                                .MONTH_COMPANION
                ) {

                    throw new IllegalArgumentException(
                            "建祿 경로에는 MONTH_COMPANION 후보가 필요합니다."
                    );
                }

                validateSpecialPath(
                        provisional
                );
            }

            case YANG_BLADE_REVIEW,
                 YUEJIE_REVIEW -> {

                if (
                        primary.candidateType()
                                != MonthCommandPatternCandidateType
                                .MONTH_ROB_WEALTH
                ) {

                    throw new IllegalArgumentException(
                            "陽刃/月劫 경로에는 MONTH_ROB_WEALTH 후보가 필요합니다."
                    );
                }

                validateSpecialPath(
                        provisional
                );
            }
        }
    }

    private static void validateSpecialPath(
            Optional<MonthCommandPatternCandidate> provisional
    ) {

        if (
                provisional.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "建祿/陽刃/月劫 경로에서는 일반 8격 후보를 임시 확정하지 않습니다."
            );
        }
    }
}