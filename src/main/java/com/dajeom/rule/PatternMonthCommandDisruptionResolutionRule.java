package com.dajeom.rule;

import com.dajeom.domain.PatternBranchCombinationKind;
import com.dajeom.domain.PatternBranchDisruptionAnalysis;
import com.dajeom.domain.PatternBranchDisruptionResolutionAnalysis;
import com.dajeom.domain.PatternBranchDisruptionResolutionOccurrence;
import com.dajeom.domain.PatternBranchDisruptionResolutionReason;
import com.dajeom.domain.PatternMonthCommandDisruptionResolutionAnalysis;
import com.dajeom.domain.PatternMonthCommandDisruptionResolutionReason;
import com.dajeom.domain.PatternMonthCommandDisruptionResolutionState;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 월령 전체의 沖 / 刑 / 破 / 害가
 * 회합에 의해 해소될 수 있는지를 정리한다.
 *
 * =================================================
 *
 * NO_DISRUPTION
 *
 * 월령에 애초에 교란 없음.
 *
 * =================================================
 *
 * RESOLVED_BY_COMBINATION
 *
 * 월령에 걸린 모든 교란 occurrence가
 * 각각 경쟁 없는 회합 해소 후보를 갖는다.
 *
 * =================================================
 *
 * PARTIALLY_RESOLVED
 *
 * 일부는 경쟁 없는 해소 후보가 있으나
 * 일부는 해소 후보가 없음.
 *
 * =================================================
 *
 * REVIEW_REQUIRED
 *
 * 해소 후보가:
 *
 * - 다른 회합과 경쟁
 * - 복수 후보 존재
 *
 * 등으로 단순 확정 불가.
 *
 * =================================================
 *
 * UNRESOLVED
 *
 * 월령 교란은 존재하지만
 * 해소 후보가 하나도 없음.
 *
 * =================================================
 *
 * 숫자 / 가중치 / 다수결 없음.
 */
@Component
public class PatternMonthCommandDisruptionResolutionRule {

    public PatternMonthCommandDisruptionResolutionAnalysis assess(

            PatternBranchDisruptionAnalysis disruptionAnalysis,

            PatternBranchDisruptionResolutionAnalysis
                    resolutionAnalysis

    ) {

        Objects.requireNonNull(
                disruptionAnalysis,
                "PatternBranchDisruptionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                resolutionAnalysis,
                "PatternBranchDisruptionResolutionAnalysis는 필수입니다."
        );

        List<PatternBranchDisruptionResolutionOccurrence>
                monthResolutions =
                resolutionAnalysis
                        .monthCommandResolutions();

        EnumSet<PatternMonthCommandDisruptionResolutionReason>
                reasons =
                EnumSet.noneOf(
                        PatternMonthCommandDisruptionResolutionReason.class
                );

        /*
         * =================================================
         * 1. 월령 교란 자체
         * =================================================
         */

        if (
                !disruptionAnalysis
                        .monthCommandDisrupted()
        ) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MONTH_COMMAND_NOT_DISRUPTED
            );

            if (!monthResolutions.isEmpty()) {

                throw new IllegalArgumentException(
                        "월령 교란이 없는데 월령 해소 occurrence가 존재합니다."
                );
            }

            return new PatternMonthCommandDisruptionResolutionAnalysis(

                    disruptionAnalysis.monthBranch(),

                    PatternMonthCommandDisruptionResolutionState
                            .NO_DISRUPTION,

                    List.of(),

                    reasons
            );
        }

        if (
                disruptionAnalysis
                        .monthCommandClashed()
        ) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MONTH_COMMAND_CLASHED
            );
        }

        if (
                disruptionAnalysis
                        .monthCommandPunished()
        ) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MONTH_COMMAND_PUNISHED
            );
        }

        if (
                disruptionAnalysis
                        .monthCommandBroken()
        ) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MONTH_COMMAND_BROKEN
            );
        }

        if (
                disruptionAnalysis
                        .monthCommandHarmed()
        ) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MONTH_COMMAND_HARMED
            );
        }

        /*
         * 월령 교란 occurrence 하나당
         * ResolutionOccurrence 하나가 있어야 한다.
         */
        if (
                monthResolutions.size()
                        != disruptionAnalysis
                        .monthCommandDisruptions()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "월령 교란 occurrence 수와 해소 분석 occurrence 수가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 2. 어떤 종류의 회합 후보가 있는지
         * =================================================
         */

        monthResolutions
                .stream()
                .flatMap(
                        resolution ->
                                resolution
                                        .candidateCombinations()
                                        .stream()
                )
                .forEach(
                        combination -> {

                            if (
                                    combination.kind()
                                            == PatternBranchCombinationKind
                                            .SIX_HARMONY
                            ) {

                                reasons.add(
                                        PatternMonthCommandDisruptionResolutionReason
                                                .SIX_HARMONY_RESOLUTION_PRESENT
                                );
                            }

                            if (
                                    combination.kind()
                                            == PatternBranchCombinationKind
                                            .COMPLETE_THREE_HARMONY
                            ) {

                                reasons.add(
                                        PatternMonthCommandDisruptionResolutionReason
                                                .COMPLETE_THREE_HARMONY_RESOLUTION_PRESENT
                                );
                            }

                            if (
                                    combination.kind()
                                            == PatternBranchCombinationKind
                                            .COMPLETE_THREE_MEETING
                            ) {

                                reasons.add(
                                        PatternMonthCommandDisruptionResolutionReason
                                                .COMPLETE_THREE_MEETING_RESOLUTION_PRESENT
                                );
                            }
                        }
                );

        /*
         * =================================================
         * 3. 경쟁 상태
         * =================================================
         */

        boolean anyContested =
                monthResolutions
                        .stream()
                        .anyMatch(
                                PatternBranchDisruptionResolutionOccurrence
                                        ::contested
                        );

        boolean multipleResolutionCandidates =
                monthResolutions
                        .stream()
                        .anyMatch(
                                resolution ->
                                        resolution
                                                .reasons()
                                                .contains(
                                                        PatternBranchDisruptionResolutionReason
                                                                .MULTIPLE_RESOLUTION_CANDIDATES
                                                )
                        );

        boolean resolverPartnerUsedElsewhere =
                monthResolutions
                        .stream()
                        .anyMatch(
                                resolution ->
                                        resolution
                                                .reasons()
                                                .contains(
                                                        PatternBranchDisruptionResolutionReason
                                                                .RESOLVER_PARTNER_INVOLVED_IN_OTHER_COMBINATION
                                                )
                        );

        if (anyContested) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .CONTESTED_RESOLUTION_EXISTS
            );
        }

        if (multipleResolutionCandidates) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MULTIPLE_RESOLUTION_CANDIDATES_EXIST
            );
        }

        if (resolverPartnerUsedElsewhere) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .RESOLVER_PARTNER_USED_BY_OTHER_COMBINATION
            );
        }

        /*
         * 경쟁 후보가 하나라도 있으면
         * 단순 해소/부분해소로 밀어붙이지 않는다.
         */
        if (anyContested) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MONTH_COMMAND_RESOLUTION_REQUIRES_REVIEW
            );

            return new PatternMonthCommandDisruptionResolutionAnalysis(

                    disruptionAnalysis.monthBranch(),

                    PatternMonthCommandDisruptionResolutionState
                            .REVIEW_REQUIRED,

                    monthResolutions,

                    reasons
            );
        }

        /*
         * =================================================
         * 4. 모든 교란이 경쟁 없는 해소 후보를 가짐
         * =================================================
         */

        boolean allUncontested =
                monthResolutions
                        .stream()
                        .allMatch(
                                PatternBranchDisruptionResolutionOccurrence
                                        ::uncontestedCandidate
                        );

        if (allUncontested) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .ALL_DISRUPTIONS_HAVE_UNCONTESTED_RESOLUTION
            );

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MONTH_COMMAND_RESOLVED_BY_COMBINATION
            );

            return new PatternMonthCommandDisruptionResolutionAnalysis(

                    disruptionAnalysis.monthBranch(),

                    PatternMonthCommandDisruptionResolutionState
                            .RESOLVED_BY_COMBINATION,

                    monthResolutions,

                    reasons
            );
        }

        /*
         * =================================================
         * 5. 일부만 해소 후보 존재
         * =================================================
         */

        boolean anyUncontested =
                monthResolutions
                        .stream()
                        .anyMatch(
                                PatternBranchDisruptionResolutionOccurrence
                                        ::uncontestedCandidate
                        );

        boolean anyNoResolution =
                monthResolutions
                        .stream()
                        .anyMatch(
                                resolution ->
                                        !resolution
                                                .hasResolutionCandidate()
                        );

        if (
                anyUncontested
                        &&
                        anyNoResolution
        ) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .SOME_DISRUPTIONS_RESOLVED
            );

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .SOME_DISRUPTIONS_REMAIN_UNRESOLVED
            );

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MONTH_COMMAND_PARTIALLY_RESOLVED
            );

            return new PatternMonthCommandDisruptionResolutionAnalysis(

                    disruptionAnalysis.monthBranch(),

                    PatternMonthCommandDisruptionResolutionState
                            .PARTIALLY_RESOLVED,

                    monthResolutions,

                    reasons
            );
        }

        /*
         * =================================================
         * 6. 해소 후보 없음
         * =================================================
         */

        boolean noResolutionCandidate =
                monthResolutions
                        .stream()
                        .allMatch(
                                resolution ->
                                        !resolution
                                                .hasResolutionCandidate()
                        );

        if (noResolutionCandidate) {

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .NO_RESOLUTION_CANDIDATE_EXISTS
            );

            reasons.add(
                    PatternMonthCommandDisruptionResolutionReason
                            .MONTH_COMMAND_DISRUPTION_UNRESOLVED
            );

            return new PatternMonthCommandDisruptionResolutionAnalysis(

                    disruptionAnalysis.monthBranch(),

                    PatternMonthCommandDisruptionResolutionState
                            .UNRESOLVED,

                    monthResolutions,

                    reasons
            );
        }

        /*
         * =================================================
         * 7. 방어적 fallback
         * =================================================
         */

        reasons.add(
                PatternMonthCommandDisruptionResolutionReason
                        .MONTH_COMMAND_RESOLUTION_REQUIRES_REVIEW
        );

        return new PatternMonthCommandDisruptionResolutionAnalysis(

                disruptionAnalysis.monthBranch(),

                PatternMonthCommandDisruptionResolutionState
                        .REVIEW_REQUIRED,

                monthResolutions,

                reasons
        );
    }
}