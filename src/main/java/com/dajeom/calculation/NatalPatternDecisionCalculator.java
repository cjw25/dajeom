package com.dajeom.calculation;

import com.dajeom.domain.CompanionMonthPatternAnalysis;
import com.dajeom.domain.CompanionMonthPatternStatus;
import com.dajeom.domain.CompanionMonthPatternType;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.MonthCommandPatternSelectionPath;
import com.dajeom.domain.NatalPatternDecision;
import com.dajeom.domain.NatalPatternDecisionReason;
import com.dajeom.domain.NatalPatternDecisionStatus;
import com.dajeom.domain.NatalPatternType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternStatus;
import com.dajeom.domain.StandardPatternAssessment;

import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;

/**
 * 표준 6격과
 * 建祿 / 月劫 / 陽刃을
 * 하나의 원국 격국 결과로 통합한다.
 *
 * =================================================
 *
 * 표준 경로:
 *
 * STANDARD_PRIMARY_REVIEW
 * STANDARD_CHANGE_REVIEW
 * STANDARD_MULTIPLE_CHANGE_REVIEW
 *
 * =================================================
 *
 * 比劫 월령:
 *
 * JIANLU_REVIEW
 * YUEJIE_REVIEW
 * YANG_BLADE_REVIEW
 *
 * =================================================
 *
 * 이 Calculator는 하위 격국 Rule을 다시 계산하지 않는다.
 *
 * 이미 계산된 판정을 하나의 공통 결과로
 * routing하는 역할만 한다.
 *
 * 일반 표준격 입력은
 * StandardPatternAssessment를 사용한다.
 */
@Component
public class NatalPatternDecisionCalculator {

    public NatalPatternDecision decide(

            PatternInteractionAnalysis interaction,

            Optional<StandardPatternAssessment> standardAssessment,

            Optional<CompanionMonthPatternAnalysis> companionAnalysis

    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        standardAssessment =
                standardAssessment == null
                        ? Optional.empty()
                        : standardAssessment;

        companionAnalysis =
                companionAnalysis == null
                        ? Optional.empty()
                        : companionAnalysis;

        MonthCommandPatternSelectionPath selectionPath =
                interaction
                        .patternSelection()
                        .path();

        return switch (selectionPath) {

            case STANDARD_PRIMARY_REVIEW,
                 STANDARD_CHANGE_REVIEW,
                 STANDARD_MULTIPLE_CHANGE_REVIEW ->

                    decideStandard(
                            interaction,
                            selectionPath,
                            standardAssessment,
                            companionAnalysis
                    );

            case JIANLU_REVIEW,
                 YUEJIE_REVIEW,
                 YANG_BLADE_REVIEW ->

                    decideCompanion(
                            interaction,
                            selectionPath,
                            standardAssessment,
                            companionAnalysis
                    );
        };
    }

    /*
     * =================================================
     * 표준 6격
     * =================================================
     */

    private NatalPatternDecision decideStandard(

            PatternInteractionAnalysis interaction,

            MonthCommandPatternSelectionPath selectionPath,

            Optional<StandardPatternAssessment> standardAssessment,

            Optional<CompanionMonthPatternAnalysis> companionAnalysis

    ) {

        EnumSet<NatalPatternDecisionReason> reasons =
                EnumSet.noneOf(
                        NatalPatternDecisionReason.class
                );

        reasons.add(
                NatalPatternDecisionReason
                        .STANDARD_PATTERN_ROUTE_SELECTED
        );

        if (companionAnalysis.isPresent()) {

            reasons.add(
                    NatalPatternDecisionReason
                            .SOURCE_ANALYSIS_INCONSISTENT
            );

            throw new IllegalArgumentException(
                    "표준 격국 경로에 CompanionMonthPatternAnalysis가 전달됐습니다."
            );
        }

        /*
         * =================================================
         * 격국 변화 / 복수 변화 REVIEW
         * =================================================
         *
         * 이 경로에서는 실제 StandardPatternAssessment가
         * 없는 것이 정상이다.
         */
        if (standardAssessment.isEmpty()) {

            reasons.add(
                    NatalPatternDecisionReason
                            .STANDARD_ASSESSMENT_MISSING
            );

            if (
                    selectionPath
                            == MonthCommandPatternSelectionPath
                            .STANDARD_MULTIPLE_CHANGE_REVIEW
            ) {

                reasons.add(
                        NatalPatternDecisionReason
                                .MULTIPLE_PATTERN_CHANGE_REVIEW
                );

            } else {

                reasons.add(
                        NatalPatternDecisionReason
                                .PATTERN_CHANGE_REVIEW_NOT_RESOLVED
                );
            }

            /*
             * STANDARD_PRIMARY_REVIEW인데
             * assessment가 없는 것은 정상적인 실행선이 아니다.
             */
            if (
                    selectionPath
                            == MonthCommandPatternSelectionPath
                            .STANDARD_PRIMARY_REVIEW
            ) {

                throw new IllegalArgumentException(
                        "STANDARD_PRIMARY_REVIEW 경로에는 StandardPatternAssessment가 필요합니다."
                );
            }

            /*
             * 아직 최종 변경 격국을 확정하지 않았으므로
             * provisional candidate의 타입만 보존한다.
             *
             * 상태는 반드시 REVIEW_REQUIRED.
             */
            // 변화 후보가 여러 개인 경로에는 provisional candidate가 없다.
            // 월령 본기의 타입만 보존하며 REVIEW_REQUIRED를 유지한다.
            MonthCommandPatternCandidateType provisionalType =
                    interaction
                            .provisionalStandardCandidate()
                            .map(
                                    candidate ->
                                            candidate.candidateType()
                            )
                            .orElse(interaction.primaryCandidate().candidateType());

            return new NatalPatternDecision(

                    interaction.dayMaster(),

                    selectionPath,

                    mapStandardType(
                            provisionalType
                    ),

                    NatalPatternDecisionStatus
                            .REVIEW_REQUIRED,

                    Optional.empty(),

                    Optional.empty(),

                    reasons
            );
        }

        /*
         * =================================================
         * 실제 일반 표준격 Assessment
         * =================================================
         */

        StandardPatternAssessment assessment =
                standardAssessment.get();

        reasons.add(
                NatalPatternDecisionReason
                        .STANDARD_ASSESSMENT_AVAILABLE
        );

        if (
                assessment.dayMaster()
                        != interaction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 StandardPatternAssessment의 일간이 일치하지 않습니다."
            );
        }

        if (
                selectionPath
                        != MonthCommandPatternSelectionPath
                        .STANDARD_PRIMARY_REVIEW
        ) {

            throw new IllegalArgumentException(
                    "격국 변화 검토 경로에는 확정된 StandardPatternAssessment를 전달할 수 없습니다."
            );
        }

        MonthCommandPatternCandidateType candidateType =
                assessment
                        .patternCandidate()
                        .candidateType();

        NatalPatternDecisionStatus status =
                mapStandardStatus(
                        assessment.status(),
                        reasons
                );

        return new NatalPatternDecision(

                interaction.dayMaster(),

                selectionPath,

                mapStandardType(
                        candidateType
                ),

                status,

                Optional.of(
                        assessment
                ),

                Optional.empty(),

                reasons
        );
    }

    /*
     * =================================================
     * 建祿 / 月劫 / 陽刃
     * =================================================
     */

    private NatalPatternDecision decideCompanion(

            PatternInteractionAnalysis interaction,

            MonthCommandPatternSelectionPath selectionPath,

            Optional<StandardPatternAssessment> standardAssessment,

            Optional<CompanionMonthPatternAnalysis> companionAnalysis

    ) {

        EnumSet<NatalPatternDecisionReason> reasons =
                EnumSet.noneOf(
                        NatalPatternDecisionReason.class
                );

        reasons.add(
                NatalPatternDecisionReason
                        .COMPANION_MONTH_PATTERN_ROUTE_SELECTED
        );

        if (standardAssessment.isPresent()) {

            reasons.add(
                    NatalPatternDecisionReason
                            .SOURCE_ANALYSIS_INCONSISTENT
            );

            throw new IllegalArgumentException(
                    "建祿/月劫/陽刃 경로에 StandardPatternAssessment가 전달됐습니다."
            );
        }

        if (companionAnalysis.isEmpty()) {

            reasons.add(
                    NatalPatternDecisionReason
                            .COMPANION_ASSESSMENT_MISSING
            );

            throw new IllegalArgumentException(
                    "建祿/月劫/陽刃 경로에는 CompanionMonthPatternAnalysis가 필요합니다."
            );
        }

        CompanionMonthPatternAnalysis companion =
                companionAnalysis.get();

        reasons.add(
                NatalPatternDecisionReason
                        .COMPANION_ASSESSMENT_AVAILABLE
        );

        if (
                companion.dayMaster()
                        != interaction.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 CompanionMonthPatternAnalysis의 일간이 일치하지 않습니다."
            );
        }

        validateCompanionPath(
                selectionPath,
                companion.patternType()
        );

        NatalPatternDecisionStatus status =
                mapCompanionStatus(
                        companion.status(),
                        reasons
                );

        return new NatalPatternDecision(

                interaction.dayMaster(),

                selectionPath,

                mapCompanionType(
                        companion.patternType()
                ),

                status,

                Optional.empty(),

                Optional.of(
                        companion
                ),

                reasons
        );
    }

    /*
     * =================================================
     * 표준 6격 타입
     * =================================================
     */

    private NatalPatternType mapStandardType(
            MonthCommandPatternCandidateType type
    ) {

        return switch (type) {

            case DIRECT_OFFICER ->
                    NatalPatternType.DIRECT_OFFICER;

            case SEVEN_KILLINGS ->
                    NatalPatternType.SEVEN_KILLINGS;

            case DIRECT_WEALTH ->
                    NatalPatternType.DIRECT_WEALTH;

            case INDIRECT_WEALTH ->
                    NatalPatternType.INDIRECT_WEALTH;

            case EATING_GOD ->
                    NatalPatternType.EATING_GOD;

            case HURTING_OFFICER ->
                    NatalPatternType.HURTING_OFFICER;

            case DIRECT_RESOURCE ->
                    NatalPatternType.DIRECT_RESOURCE;

            case INDIRECT_RESOURCE ->
                    NatalPatternType.INDIRECT_RESOURCE;

            case MONTH_COMPANION,
                 MONTH_ROB_WEALTH ->

                    throw new IllegalArgumentException(
                            "比劫 월령 후보는 표준 6격 타입으로 변환할 수 없습니다: "
                                    + type
                    );
        };
    }

    /*
     * =================================================
     * 표준 상태
     * =================================================
     */

    private NatalPatternDecisionStatus mapStandardStatus(

            PatternStatus status,

            EnumSet<NatalPatternDecisionReason> reasons

    ) {

        return switch (status) {

            case FORMED -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .STANDARD_PATTERN_FORMED
                );

                yield NatalPatternDecisionStatus
                        .FORMED;
            }

            case FORMED_WITH_DEFECT -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .STANDARD_PATTERN_FORMED_WITH_DEFECT
                );

                yield NatalPatternDecisionStatus
                        .FORMED_WITH_DEFECT;
            }

            case BROKEN -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .STANDARD_PATTERN_BROKEN
                );

                yield NatalPatternDecisionStatus
                        .BROKEN;
            }

            case BROKEN_WITH_RESCUE -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .STANDARD_PATTERN_BROKEN_WITH_RESCUE
                );

                yield NatalPatternDecisionStatus
                        .BROKEN_WITH_RESCUE;
            }

            case REVIEW_REQUIRED -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .STANDARD_PATTERN_REVIEW_REQUIRED
                );

                yield NatalPatternDecisionStatus
                        .REVIEW_REQUIRED;
            }
        };
    }

    /*
     * =================================================
     * 比劫 월령 타입
     * =================================================
     */

    private NatalPatternType mapCompanionType(
            CompanionMonthPatternType type
    ) {

        return switch (type) {

            case JIANLU ->
                    NatalPatternType.JIANLU;

            case YUEJIE ->
                    NatalPatternType.YUEJIE;

            case YANG_BLADE ->
                    NatalPatternType.YANG_BLADE;
        };
    }

    /*
     * =================================================
     * 比劫 월령 상태
     * =================================================
     */

    private NatalPatternDecisionStatus mapCompanionStatus(

            CompanionMonthPatternStatus status,

            EnumSet<NatalPatternDecisionReason> reasons

    ) {

        return switch (status) {

            case FORMED -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .COMPANION_PATTERN_FORMED
                );

                yield NatalPatternDecisionStatus
                        .FORMED;
            }

            case FORMED_WITH_DEFECT -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .COMPANION_PATTERN_FORMED_WITH_DEFECT
                );

                yield NatalPatternDecisionStatus
                        .FORMED_WITH_DEFECT;
            }

            case REVIEW_REQUIRED -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .COMPANION_PATTERN_REVIEW_REQUIRED
                );

                yield NatalPatternDecisionStatus
                        .REVIEW_REQUIRED;
            }

            case UNFORMED -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .COMPANION_PATTERN_UNFORMED
                );

                yield NatalPatternDecisionStatus
                        .UNFORMED;
            }

            case NOT_APPLICABLE -> {

                reasons.add(
                        NatalPatternDecisionReason
                                .SOURCE_ANALYSIS_INCONSISTENT
                );

                throw new IllegalArgumentException(
                        "CompanionMonthPatternRule 대상 경로에서 NOT_APPLICABLE 결과가 전달됐습니다."
                );
            }
        };
    }

    private void validateCompanionPath(

            MonthCommandPatternSelectionPath path,

            CompanionMonthPatternType type

    ) {

        boolean valid =
                switch (path) {

                    case JIANLU_REVIEW ->
                            type
                                    == CompanionMonthPatternType
                                    .JIANLU;

                    case YUEJIE_REVIEW ->
                            type
                                    == CompanionMonthPatternType
                                    .YUEJIE;

                    case YANG_BLADE_REVIEW ->
                            type
                                    == CompanionMonthPatternType
                                    .YANG_BLADE;

                    default ->
                            false;
                };

        if (!valid) {

            throw new IllegalArgumentException(
                    "월령 선택 경로와 CompanionMonthPatternType이 일치하지 않습니다. path="
                            + path
                            + ", type="
                            + type
            );
        }
    }
}
