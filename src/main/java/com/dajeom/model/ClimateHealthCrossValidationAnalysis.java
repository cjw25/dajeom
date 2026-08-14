package com.dajeom.model;

import com.dajeom.type.ClimateHealthCandidateRole;
import com.dajeom.type.ClimateHealthCrossValidationState;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 6차-9.
 *
 * 조후·병약·통관 통합 후보를
 *
 * - 4차 신강신약
 * - 5차 격국
 *
 * 과 교차검증한 결과.
 *
 * 木火土金水 전체가 정확히 한 번씩 존재한다.
 */
public record ClimateHealthCrossValidationAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        StructureAnalysis structureAnalysis,

        ClimateHealthIntegrationAnalysis integrationAnalysis,

        List<ClimateHealthValidatedCandidate> candidates,

        boolean followStructureReview,

        boolean completeChart

) {

    public ClimateHealthCrossValidationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "격국 분석이 필요합니다."
        );

        Objects.requireNonNull(
                integrationAnalysis,
                "6-8 통합 분석이 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        if (
                candidates.size()
                        != FiveElement
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "교차검증 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                ClimateHealthValidatedCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 교차검증 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }
        }

        if (
                structureAnalysis.dayMaster()
                        != dayMaster

                        || integrationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "격국·6-8·6-9의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                integrationAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "6-8 분석의 일간 오행이 일치하지 않습니다."
            );
        }

        if (
                structureAnalysis.monthBranch()
                        != monthBranch

                        || integrationAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "격국·6-8·6-9의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                structureAnalysis.completeChart()
                        != completeChart

                        || integrationAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        boolean expectedFollowReview =
                structureAnalysis
                        .primaryCandidate()
                        .followStructureCandidate();

        if (
                followStructureReview
                        != expectedFollowReview
        ) {

            throw new IllegalArgumentException(
                    "종격 후보 검토 상태가 주격 정보와 일치하지 않습니다."
            );
        }
    }

    public List<ClimateHealthValidatedCandidate>
    primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthValidatedCandidate
                                ::primary
                )
                .toList();
    }

    public List<ClimateHealthValidatedCandidate>
    secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthValidatedCandidate
                                ::secondary
                )
                .toList();
    }

    public List<ClimateHealthValidatedCandidate>
    deferredCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthValidatedCandidate
                                ::deferred
                )
                .toList();
    }

    public List<ClimateHealthValidatedCandidate>
    rejectedCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthValidatedCandidate
                                ::rejected
                )
                .toList();
    }

    public List<ClimateHealthValidatedCandidate>
    cleanlyValidatedCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthValidatedCandidate
                                ::cleanlyValidated
                )
                .toList();
    }

    public List<ClimateHealthValidatedCandidate>
    downgradedCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthValidatedCandidate
                                ::downgraded
                )
                .toList();
    }

    public List<ClimateHealthValidatedCandidate>
    contradictoryCandidates() {

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate
                                        .overallState()
                                        .contradictory()
                )
                .toList();
    }

    public List<ClimateHealthValidatedCandidate>
    strainedCandidates() {

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate
                                        .overallState()
                                        .strained()
                )
                .toList();
    }

    public Optional<ClimateHealthValidatedCandidate>
    candidateOf(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return Optional.empty();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.element()
                                        == element
                )
                .findFirst();
    }

    public boolean hasPrimaryCandidate() {

        return !primaryCandidates()
                .isEmpty();
    }

    public boolean hasMultiplePrimaryCandidates() {

        return primaryCandidates()
                .size() >= 2;
    }

    public boolean hasDowngrade() {

        return !downgradedCandidates()
                .isEmpty();
    }

    public boolean hasContradiction() {

        return !contradictoryCandidates()
                .isEmpty();
    }

    /**
     * 6-8에서는 PRIMARY가 있었지만
     * 6-9 검증 이후 PRIMARY가 모두 사라진 상태.
     */
    public boolean primaryLostAfterValidation() {

        return integrationAnalysis
                .hasPrimaryCandidate()

                && primaryCandidates()
                .isEmpty();
    }

    public boolean unresolvedAfterValidation() {

        boolean anyCandidate =
                candidates
                        .stream()
                        .anyMatch(
                                candidate ->
                                        !candidate.rejected()
                        );

        return anyCandidate
                && primaryCandidates()
                .isEmpty();
    }

    public int countRole(
            ClimateHealthCandidateRole role
    ) {

        if (
                role == null
        ) {

            return 0;
        }

        return (int) candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.validatedRole()
                                        == role
                )
                .count();
    }

    public int countState(
            ClimateHealthCrossValidationState state
    ) {

        if (
                state == null
        ) {

            return 0;
        }

        return (int) candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.overallState()
                                        == state
                )
                .count();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}