package com.dajeom.model;

import com.dajeom.type.ClimateHealthCandidateRole;
import com.dajeom.type.ClimateHealthIntegrationState;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 6차-8.
 *
 * 조후·병약·통관 통합 분석 결과.
 *
 * 모든 五行이 정확히 한 번씩 존재한다.
 *
 * =================================================
 *
 * PRIMARY
 *
 * 여러 분석이 같은 방향으로 수렴하거나,
 * 단일 분석이 매우 강하게 요구하는
 * 깨끗한 후보.
 *
 * =================================================
 *
 * SECONDARY
 *
 * 의미는 있지만
 * 아직 강한 다중 수렴까지는 아닌 후보.
 *
 * =================================================
 *
 * DEFERRED
 *
 * 필요성은 있지만:
 *
 * - 분석 간 충돌
 * - 조건부
 * - 과다
 *
 * 등으로 6-9까지 결정을 미루는 후보.
 *
 * =================================================
 *
 * REJECTED
 *
 * 현재 6차 조후·병약·통관 관점에서
 * 적극적으로 사용할 이유가 없는 후보.
 *
 * =================================================
 *
 * PRIMARY가 여러 개 존재할 수 있다.
 *
 * 6-9에서 격국·신강신약과 다시 교차검증한 뒤
 * 후보 역할을 정밀화한다.
 */
public record ClimateHealthIntegrationAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        ClimateElementCandidateAnalysis climateAnalysis,

        RemedyAnalysis remedyAnalysis,

        PassageCandidateAnalysis passageAnalysis,

        List<ClimateHealthCandidate> candidates,

        boolean completeChart

) {

    public ClimateHealthIntegrationAnalysis {

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
                climateAnalysis,
                "조후 오행 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                remedyAnalysis,
                "병약 분석이 필요합니다."
        );

        Objects.requireNonNull(
                passageAnalysis,
                "통관 후보 분석이 필요합니다."
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
                    "통합 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                ClimateHealthCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 통합 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }
        }

        if (
                climateAnalysis.dayMaster()
                        != dayMaster

                        || remedyAnalysis.dayMaster()
                        != dayMaster

                        || passageAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "조후·병약·통관 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                passageAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "통관 분석의 일간 오행이 일치하지 않습니다."
            );
        }

        if (
                climateAnalysis.monthBranch()
                        != monthBranch

                        || remedyAnalysis.monthBranch()
                        != monthBranch

                        || passageAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "조후·병약·통관 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                climateAnalysis.completeChart()
                        != completeChart

                        || remedyAnalysis.completeChart()
                        != completeChart

                        || passageAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }

    public List<ClimateHealthCandidate>
    primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthCandidate
                                ::primary
                )
                .toList();
    }

    public List<ClimateHealthCandidate>
    secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthCandidate
                                ::secondary
                )
                .toList();
    }

    public List<ClimateHealthCandidate>
    deferredCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthCandidate
                                ::deferred
                )
                .toList();
    }

    public List<ClimateHealthCandidate>
    rejectedCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthCandidate
                                ::rejected
                )
                .toList();
    }

    public List<ClimateHealthCandidate>
    selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthCandidate
                                ::selected
                )
                .toList();
    }

    public List<ClimateHealthCandidate>
    convergentCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthCandidate
                                ::convergent
                )
                .toList();
    }

    public List<ClimateHealthCandidate>
    conflictedCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthCandidate
                                ::conflicted
                )
                .toList();
    }

    public List<ClimateHealthCandidate>
    conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateHealthCandidate
                                ::conditional
                )
                .toList();
    }

    public Optional<ClimateHealthCandidate>
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

    public boolean hasConvergence() {

        return !convergentCandidates()
                .isEmpty();
    }

    public boolean hasTripleConvergence() {

        return candidates
                .stream()
                .anyMatch(
                        ClimateHealthCandidate
                                ::tripleConvergence
                );
    }

    public boolean hasConflict() {

        return !conflictedCandidates()
                .isEmpty();
    }

    public boolean hasDeferredCandidate() {

        return !deferredCandidates()
                .isEmpty();
    }

    /**
     * 세 분석 중에는 의미 있는 후보가 존재하지만
     * 충돌/조건부 때문에
     * 통합 PRIMARY를 하나도 만들지 못한 상태.
     */
    public boolean unresolvedIntegration() {

        boolean anyActive =
                candidates
                        .stream()
                        .anyMatch(
                                candidate ->
                                        candidate
                                                .activeAxisCount()
                                                > 0
                        );

        return anyActive
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
                                candidate.role()
                                        == role
                )
                .count();
    }

    public int countState(
            ClimateHealthIntegrationState state
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
                                candidate.integrationState()
                                        == state
                )
                .count();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}