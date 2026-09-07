package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 원국 격국의 최종 통합 결과.
 *
 * =================================================
 *
 * 이 객체부터 상위 해석 계층에서는
 *
 * 正官格인지
 * 財格인지
 * 建祿인지
 * 陽刃인지
 *
 * 를 따로 찾아다닐 필요가 없다.
 *
 * =================================================
 *
 * standardAssessment:
 *
 * 正官 / 七殺 / 財 / 印 / 食神 / 傷官
 *
 * 실제 일반 격국 Rule이 실행되어
 * 판정 결과가 나온 경우에만 존재한다.
 *
 * =================================================
 *
 * companionAnalysis:
 *
 * 建祿 / 月劫 / 陽刃
 *
 * 比劫 월령 경로에서는 반드시 존재한다.
 *
 * =================================================
 *
 * 일반 격국 변화 검토 경로:
 *
 * STANDARD_CHANGE_REVIEW
 * STANDARD_MULTIPLE_CHANGE_REVIEW
 *
 * 위 경로에서는 아직 특정 일반 격국을
 * 실제 Assessment까지 확정하지 않았으므로
 *
 * standardAssessment = empty
 * companionAnalysis   = empty
 *
 * 일 수 있다.
 *
 * 이 경우 status는 반드시 REVIEW_REQUIRED다.
 */
public record NatalPatternDecision(

        HeavenlyStem dayMaster,

        MonthCommandPatternSelectionPath selectionPath,

        NatalPatternType patternType,

        NatalPatternDecisionStatus status,

        Optional<StandardPatternAssessment> standardAssessment,

        Optional<CompanionMonthPatternAnalysis> companionAnalysis,

        Set<NatalPatternDecisionReason> reasons

) {

    public NatalPatternDecision {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                selectionPath,
                "월령 격국 선택 경로는 필수입니다."
        );

        Objects.requireNonNull(
                patternType,
                "최종 격국 타입은 필수입니다."
        );

        Objects.requireNonNull(
                status,
                "최종 격국 상태는 필수입니다."
        );

        standardAssessment =
                standardAssessment == null
                        ? Optional.empty()
                        : standardAssessment;

        companionAnalysis =
                companionAnalysis == null
                        ? Optional.empty()
                        : companionAnalysis;

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(
                        reasons
                );

        boolean standardRoute =
                isStandardRoute(
                        selectionPath
                );

        boolean companionRoute =
                isCompanionRoute(
                        selectionPath
                );

        if (
                standardRoute
                        == companionRoute
        ) {

            throw new IllegalArgumentException(
                    "월령 격국 선택 경로가 표준격 또는 比劫월령 경로 중 하나로 확정되어야 합니다."
            );
        }

        /*
         * =================================================
         * 표준격 경로
         * =================================================
         */
        if (standardRoute) {

            if (companionAnalysis.isPresent()) {

                throw new IllegalArgumentException(
                        "표준 격국 경로에 CompanionMonthPatternAnalysis가 존재할 수 없습니다."
                );
            }

            /*
             * 기본 표준격 판정 경로에서는
             * 실제 StandardPatternAssessment가 필요하다.
             */
            if (
                    selectionPath
                            == MonthCommandPatternSelectionPath
                            .STANDARD_PRIMARY_REVIEW
            ) {

                if (standardAssessment.isEmpty()) {

                    throw new IllegalArgumentException(
                            "STANDARD_PRIMARY_REVIEW 경로에는 StandardPatternAssessment가 필요합니다."
                    );
                }
            }

            /*
             * 격국 변화 / 복수 변화 검토는
             * 아직 실제 assessment가 없는 상태가 정상이다.
             */
            if (
                    selectionPath
                            == MonthCommandPatternSelectionPath
                            .STANDARD_CHANGE_REVIEW

                            ||

                            selectionPath
                                    == MonthCommandPatternSelectionPath
                                    .STANDARD_MULTIPLE_CHANGE_REVIEW
            ) {

                if (standardAssessment.isPresent()) {

                    throw new IllegalArgumentException(
                            "격국 변화 검토 경로에는 확정된 StandardPatternAssessment가 존재할 수 없습니다."
                    );
                }

                if (
                        status
                                != NatalPatternDecisionStatus
                                .REVIEW_REQUIRED
                ) {

                    throw new IllegalArgumentException(
                            "격국 변화 검토 경로의 상태는 REVIEW_REQUIRED여야 합니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * 建祿 / 月劫 / 陽刃
         * =================================================
         */
        if (companionRoute) {

            if (standardAssessment.isPresent()) {

                throw new IllegalArgumentException(
                        "比劫월령 경로에 StandardPatternAssessment가 존재할 수 없습니다."
                );
            }

            if (companionAnalysis.isEmpty()) {

                throw new IllegalArgumentException(
                        "比劫월령 경로에는 CompanionMonthPatternAnalysis가 필요합니다."
                );
            }
        }

        /*
         * =================================================
         * 일간 일관성
         * =================================================
         */
        standardAssessment.ifPresent(
                assessment -> {

                    if (
                            assessment.dayMaster()
                                    != dayMaster
                    ) {

                        throw new IllegalArgumentException(
                                "표준 격국 분석과 NatalPatternDecision의 일간이 일치하지 않습니다."
                        );
                    }
                }
        );

        companionAnalysis.ifPresent(
                companion -> {

                    if (
                            companion.dayMaster()
                                    != dayMaster
                    ) {

                        throw new IllegalArgumentException(
                                "比劫월령 분석과 NatalPatternDecision의 일간이 일치하지 않습니다."
                        );
                    }
                }
        );
    }

    /**
     * 현재 월령 선택 경로가
     * 일반 표준격 계열인가.
     *
     * 실제 StandardPatternAssessment가 아직 없는
     * 변화 검토 경로도 표준격 경로에 포함한다.
     */
    public boolean standardPattern() {

        return isStandardRoute(
                selectionPath
        );
    }

    /**
     * 실제 일반 격국 Assessment가 존재하는가.
     */
    public boolean hasStandardAssessment() {

        return standardAssessment.isPresent();
    }

    /**
     * 建祿 / 月劫 / 陽刃 경로인가.
     */
    public boolean companionMonthPattern() {

        return isCompanionRoute(
                selectionPath
        );
    }

    public boolean formed() {

        return status
                == NatalPatternDecisionStatus.FORMED;
    }

    public boolean formedWithDefect() {

        return status
                == NatalPatternDecisionStatus
                .FORMED_WITH_DEFECT;
    }

    public boolean broken() {

        return status
                == NatalPatternDecisionStatus.BROKEN;
    }

    public boolean brokenWithRescue() {

        return status
                == NatalPatternDecisionStatus
                .BROKEN_WITH_RESCUE;
    }

    public boolean reviewRequired() {

        return status
                == NatalPatternDecisionStatus
                .REVIEW_REQUIRED;
    }

    public boolean unformed() {

        return status
                == NatalPatternDecisionStatus.UNFORMED;
    }

    /**
     * 원국 격국이 실제 해석에 사용할 수 있는 상태인가.
     *
     * BROKEN_WITH_RESCUE도
     * 구조적으로 중요한 격국 결과이므로 포함한다.
     */
    public boolean usableForInterpretation() {

        return formed()
                ||
                formedWithDefect()
                ||
                brokenWithRescue();
    }

    private static boolean isStandardRoute(
            MonthCommandPatternSelectionPath path
    ) {

        return path
                == MonthCommandPatternSelectionPath
                .STANDARD_PRIMARY_REVIEW

                ||

                path
                        == MonthCommandPatternSelectionPath
                        .STANDARD_CHANGE_REVIEW

                ||

                path
                        == MonthCommandPatternSelectionPath
                        .STANDARD_MULTIPLE_CHANGE_REVIEW;
    }

    private static boolean isCompanionRoute(
            MonthCommandPatternSelectionPath path
    ) {

        return path
                == MonthCommandPatternSelectionPath
                .JIANLU_REVIEW

                ||

                path
                        == MonthCommandPatternSelectionPath
                        .YUEJIE_REVIEW

                ||

                path
                        == MonthCommandPatternSelectionPath
                        .YANG_BLADE_REVIEW;
    }
}