package com.dajeom.calculation;

import com.dajeom.domain.PatternBranchCombinationAnalysis;
import com.dajeom.domain.PatternBranchCombinationKind;
import com.dajeom.domain.PatternBranchCombinationOccurrence;
import com.dajeom.domain.PatternBranchDisruptionAnalysis;
import com.dajeom.domain.PatternBranchDisruptionOccurrence;
import com.dajeom.domain.PatternBranchDisruptionResolutionAnalysis;
import com.dajeom.domain.PatternBranchDisruptionResolutionOccurrence;
import com.dajeom.domain.PatternBranchDisruptionResolutionReason;
import com.dajeom.domain.PatternBranchDisruptionResolutionState;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 沖 / 刑 / 破 / 害와
 * 六合 / 三合 / 三會를 연결하여
 * "회합 기반 해소 후보"를 계산한다.
 *
 * =================================================
 *
 * 예:
 *
 * 卯酉沖
 * +
 * 卯戌六合
 *
 * → RESOLUTION_CANDIDATE
 *
 * =================================================
 *
 * 子卯刑
 * +
 * 子丑六合
 *
 * → RESOLUTION_CANDIDATE
 *
 * =================================================
 *
 * 卯酉沖
 * +
 * 亥卯未 三合
 *
 * → RESOLUTION_CANDIDATE
 *
 * =================================================
 *
 * 단:
 *
 * 子午沖
 * +
 * 子丑合
 * +
 * 巳酉丑三合
 *
 * 처럼 해소 상대인 丑이
 * 다시 다른 회합에 참여하면:
 *
 * → CONTESTED_REVIEW_REQUIRED
 *
 * =================================================
 *
 * 또한:
 *
 * 巳申刑
 * +
 * 巳申六合
 *
 * 처럼 교란 pair 자체가 동시에 六合인 경우는
 * 자동 해소 후보로 처리하지 않는다.
 *
 * =================================================
 *
 * 이 Calculator는 최종
 *
 * "沖이 사라졌다"
 *
 * 를 확정하지 않는다.
 */
@Component
public class PatternBranchDisruptionResolutionCalculator {

    public PatternBranchDisruptionResolutionAnalysis analyze(

            PatternBranchDisruptionAnalysis disruptionAnalysis,

            PatternBranchCombinationAnalysis combinationAnalysis

    ) {

        Objects.requireNonNull(
                disruptionAnalysis,
                "PatternBranchDisruptionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                combinationAnalysis,
                "PatternBranchCombinationAnalysis는 필수입니다."
        );

        validateSameChart(
                disruptionAnalysis,
                combinationAnalysis
        );

        List<PatternBranchDisruptionResolutionOccurrence> results =
                new ArrayList<>();

        for (
                PatternBranchDisruptionOccurrence disruption
                : disruptionAnalysis.occurrences()
        ) {

            List<PatternBranchCombinationOccurrence> candidates =
                    findResolutionCandidates(
                            disruption,
                            combinationAnalysis
                    );

            EnumSet<PatternBranchDisruptionResolutionReason> reasons =
                    EnumSet.noneOf(
                            PatternBranchDisruptionResolutionReason.class
                    );

            for (
                    PatternBranchCombinationOccurrence candidate
                    : candidates
            ) {

                addCombinationReason(
                        candidate,
                        reasons
                );
            }

            if (candidates.isEmpty()) {

                results.add(
                        new PatternBranchDisruptionResolutionOccurrence(

                                disruption,

                                List.of(),

                                PatternBranchDisruptionResolutionState
                                        .NO_RESOLUTION_CANDIDATE,

                                reasons
                        )
                );

                continue;
            }

            boolean multipleCandidates =
                    candidates.size() > 1;

            if (multipleCandidates) {

                reasons.add(
                        PatternBranchDisruptionResolutionReason
                                .MULTIPLE_RESOLUTION_CANDIDATES
                );
            }

            boolean resolverPartnerCompeted =
                    candidates
                            .stream()
                            .anyMatch(
                                    candidate ->
                                            resolverPartnerParticipatesElsewhere(
                                                    disruption,
                                                    candidate,
                                                    combinationAnalysis
                                            )
                            );

            if (resolverPartnerCompeted) {

                reasons.add(
                        PatternBranchDisruptionResolutionReason
                                .RESOLVER_PARTNER_INVOLVED_IN_OTHER_COMBINATION
                );
            }

            PatternBranchDisruptionResolutionState state =

                    (
                            multipleCandidates
                                    ||
                                    resolverPartnerCompeted
                    )

                            ?

                            PatternBranchDisruptionResolutionState
                                    .CONTESTED_REVIEW_REQUIRED

                            :

                            PatternBranchDisruptionResolutionState
                                    .RESOLUTION_CANDIDATE;

            results.add(
                    new PatternBranchDisruptionResolutionOccurrence(

                            disruption,

                            candidates,

                            state,

                            reasons
                    )
            );
        }

        return new PatternBranchDisruptionResolutionAnalysis(
                results
        );
    }

    /**
     * 교란의 두 endpoint 중 정확히 한쪽만
     * 회합에 포함될 때 해소 후보로 본다.
     *
     * =================================================
     *
     * 卯酉沖
     * +
     * 卯戌合
     *
     * → 卯만 공유
     * → 후보
     *
     * =================================================
     *
     * 卯酉沖
     * +
     * 辰酉合
     *
     * → 酉만 공유
     * → 후보
     *
     * =================================================
     *
     * 巳申刑
     * +
     * 巳申合
     *
     * → 양쪽 모두 공유
     * → 후보 아님
     */
    private List<PatternBranchCombinationOccurrence>
    findResolutionCandidates(

            PatternBranchDisruptionOccurrence disruption,

            PatternBranchCombinationAnalysis combinations

    ) {

        return combinations
                .combinations()
                .stream()
                .filter(
                        combination ->
                                involvesExactlyOneDisruptionEndpoint(
                                        disruption,
                                        combination
                                )
                )
                .toList();
    }

    private boolean involvesExactlyOneDisruptionEndpoint(

            PatternBranchDisruptionOccurrence disruption,

            PatternBranchCombinationOccurrence combination

    ) {

        boolean left =
                combination
                        .involvesPosition(
                                disruption.leftPosition()
                        );

        boolean right =
                combination
                        .involvesPosition(
                                disruption.rightPosition()
                        );

        /*
         * XOR
         */
        return left != right;
    }

    /**
     * 해소 후보에서
     * 교란 endpoint가 아닌 나머지 지지를
     * "resolver partner"라고 본다.
     *
     * 그 partner가 다시 다른 회합에 참여하면
     * 현재 회합이 실제 해소에 집중할 수 있는지
     * 추가 검토가 필요하다.
     */
    private boolean resolverPartnerParticipatesElsewhere(

            PatternBranchDisruptionOccurrence disruption,

            PatternBranchCombinationOccurrence candidate,

            PatternBranchCombinationAnalysis analysis

    ) {

        PillarPosition boundDisruptionPosition =
                boundDisruptionPosition(
                        disruption,
                        candidate
                );

        List<PillarPosition> resolverPartnerPositions =
                candidate
                        .positions()
                        .stream()
                        .filter(
                                position ->
                                        position
                                                != boundDisruptionPosition
                        )
                        .toList();

        for (
                PillarPosition resolverPartnerPosition
                : resolverPartnerPositions
        ) {

            boolean participatesElsewhere =
                    analysis
                            .combinations()
                            .stream()
                            .filter(
                                    other ->
                                            !other.equals(
                                                    candidate
                                            )
                            )
                            .anyMatch(
                                    other ->
                                            other.involvesPosition(
                                                    resolverPartnerPosition
                                            )
                            );

            if (participatesElsewhere) {

                return true;
            }
        }

        return false;
    }

    /**
     * candidate가 교란의 어느 쪽 endpoint를
     * 묶고 있는지 반환.
     */
    private PillarPosition boundDisruptionPosition(

            PatternBranchDisruptionOccurrence disruption,

            PatternBranchCombinationOccurrence candidate

    ) {

        boolean left =
                candidate
                        .involvesPosition(
                                disruption.leftPosition()
                        );

        boolean right =
                candidate
                        .involvesPosition(
                                disruption.rightPosition()
                        );

        if (left == right) {

            throw new IllegalArgumentException(
                    "해소 후보는 교란의 한쪽 endpoint만 포함해야 합니다."
            );
        }

        return left
                ? disruption.leftPosition()
                : disruption.rightPosition();
    }

    private void addCombinationReason(

            PatternBranchCombinationOccurrence combination,

            Set<PatternBranchDisruptionResolutionReason> reasons

    ) {

        PatternBranchCombinationKind kind =
                combination.kind();

        switch (kind) {

            case SIX_HARMONY ->

                    reasons.add(
                            PatternBranchDisruptionResolutionReason
                                    .SIX_HARMONY_RESOLUTION_CANDIDATE
                    );

            case COMPLETE_THREE_HARMONY ->

                    reasons.add(
                            PatternBranchDisruptionResolutionReason
                                    .COMPLETE_THREE_HARMONY_RESOLUTION_CANDIDATE
                    );

            case COMPLETE_THREE_MEETING ->

                    reasons.add(
                            PatternBranchDisruptionResolutionReason
                                    .COMPLETE_THREE_MEETING_RESOLUTION_CANDIDATE
                    );
        }
    }

    private void validateSameChart(

            PatternBranchDisruptionAnalysis disruptions,

            PatternBranchCombinationAnalysis combinations

    ) {

        if (
                !disruptions
                        .branches()
                        .equals(
                                combinations
                                        .branches()
                        )
        ) {

            throw new IllegalArgumentException(
                    "지지 교란 분석과 지지 회합 분석이 서로 다른 원국을 참조하고 있습니다."
            );
        }
    }
}