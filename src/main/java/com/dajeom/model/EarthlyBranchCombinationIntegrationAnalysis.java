package com.dajeom.model;

import com.dajeom.type.EarthlyBranchCombinationCandidateKind;
import com.dajeom.type.EarthlyBranchCombinationIntegrationRole;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 9차-8.
 *
 * 六合·三合·方合 후보의
 * 중첩·수렴·경쟁을 최종 조정한 분석.
 *
 * =================================================
 *
 * 입력:
 *
 * EarthlyBranchCombinationObstructionAnalysis
 *
 * =================================================
 *
 * 모든 9-7 Candidate에는
 * 정확히 하나의 9-8 IntegratedCandidate가 존재한다.
 *
 * 후보 자체를 삭제하지 않는다.
 */
public record EarthlyBranchCombinationIntegrationAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranchCombinationObstructionAnalysis
        obstructionAnalysis,

        List<EarthlyBranchCombinationIntegratedCandidate>
        candidates,

        boolean completeChart

) {

    public EarthlyBranchCombinationIntegrationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                obstructionAnalysis,
                "9-7 지지 결합 방해 분석이 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        if (
                obstructionAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "9-7과 9-8 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                obstructionAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "9-7과 9-8 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        if (
                candidates.size()
                        != obstructionAnalysis
                        .evidences()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "9-7 후보 수와 9-8 통합 후보 수가 일치하지 않습니다."
            );
        }

        Set<EarthlyBranchCombinationObstructionEvidence>
                seenSources =
                new HashSet<>();

        for (
                EarthlyBranchCombinationIntegratedCandidate candidate
                : candidates
        ) {

            if (
                    !obstructionAnalysis
                            .evidences()
                            .contains(
                                    candidate
                                            .sourceEvidence()
                            )
            ) {

                throw new IllegalArgumentException(
                        "9-8 통합 후보의 원본이 9-7 분석에 존재하지 않습니다."
                );
            }

            if (
                    !seenSources.add(
                            candidate.sourceEvidence()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 9-7 후보에 대한 9-8 통합 후보가 중복되었습니다."
                );
            }
        }
    }

    public boolean hasCandidate() {

        return !candidates
                .isEmpty();
    }

    public int candidateCount() {

        return candidates
                .size();
    }

    /*
     * =================================================
     * 역할
     * =================================================
     */

    public List<EarthlyBranchCombinationIntegratedCandidate>
    candidatesOfRole(
            EarthlyBranchCombinationIntegrationRole role
    ) {

        if (
                role == null
        ) {

            return List.of();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.role()
                                        == role
                )
                .toList();
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    primaryCandidates() {

        return candidatesOfRole(
                EarthlyBranchCombinationIntegrationRole
                        .PRIMARY
        );
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    supportingCandidates() {

        return candidatesOfRole(
                EarthlyBranchCombinationIntegrationRole
                        .SUPPORTING
        );
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    competingCandidates() {

        return candidatesOfRole(
                EarthlyBranchCombinationIntegrationRole
                        .COMPETING
        );
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    deferredCandidates() {

        return candidatesOfRole(
                EarthlyBranchCombinationIntegrationRole
                        .DEFERRED
        );
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    suppressedCandidates() {

        return candidatesOfRole(
                EarthlyBranchCombinationIntegrationRole
                        .SUPPRESSED
        );
    }

    /*
     * =================================================
     * 실제 적용 후보
     * =================================================
     */

    public List<EarthlyBranchCombinationIntegratedCandidate>
    effectiveCandidates() {

        return candidates
                .stream()
                .filter(
                        EarthlyBranchCombinationIntegratedCandidate
                                ::effective
                )
                .toList();
    }

    public boolean hasEffectiveCandidate() {

        return !effectiveCandidates()
                .isEmpty();
    }

    /*
     * =================================================
     * 수렴
     * =================================================
     */

    public List<EarthlyBranchCombinationIntegratedCandidate>
    candidatesWithConvergence() {

        return candidates
                .stream()
                .filter(
                        EarthlyBranchCombinationIntegratedCandidate
                                ::hasConvergence
                )
                .toList();
    }

    public boolean hasConvergence() {

        return !candidatesWithConvergence()
                .isEmpty();
    }

    /*
     * =================================================
     * 서로 다른 오행 경쟁
     * =================================================
     */

    public List<EarthlyBranchCombinationIntegratedCandidate>
    candidatesWithCompetition() {

        return candidates
                .stream()
                .filter(
                        EarthlyBranchCombinationIntegratedCandidate
                                ::hasCompetition
                )
                .toList();
    }

    public boolean hasCrossElementCompetition() {

        return !candidatesWithCompetition()
                .isEmpty();
    }

    public boolean hasUnresolvedCompetition() {

        return !competingCandidates()
                .isEmpty();
    }

    /*
     * =================================================
     * 후보 종류
     * =================================================
     */

    public List<EarthlyBranchCombinationIntegratedCandidate>
    candidatesOfKind(
            EarthlyBranchCombinationCandidateKind kind
    ) {

        if (
                kind == null
        ) {

            return List.of();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.candidateKind()
                                        == kind
                )
                .toList();
    }

    /*
     * =================================================
     * 목표 오행
     * =================================================
     */

    public List<EarthlyBranchCombinationIntegratedCandidate>
    candidatesTargeting(
            FiveElement element
    ) {

        if (
                element == null
        ) {

            return List.of();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.targetElement()
                                        == element
                )
                .toList();
    }

    public List<EarthlyBranchCombinationIntegratedCandidate>
    effectiveCandidatesTargeting(
            FiveElement element
    ) {

        return candidatesTargeting(
                element
        )
                .stream()
                .filter(
                        EarthlyBranchCombinationIntegratedCandidate
                                ::effective
                )
                .toList();
    }

    public List<FiveElement> primaryElements() {

        return primaryCandidates()
                .stream()
                .map(
                        EarthlyBranchCombinationIntegratedCandidate
                                ::targetElement
                )
                .distinct()
                .toList();
    }

    public List<FiveElement> effectiveElements() {

        return effectiveCandidates()
                .stream()
                .map(
                        EarthlyBranchCombinationIntegratedCandidate
                                ::targetElement
                )
                .distinct()
                .toList();
    }

    /*
     * =================================================
     * 궁위
     * =================================================
     */

    public List<EarthlyBranchCombinationIntegratedCandidate>
    candidatesInvolving(
            EarthlyBranchPosition position
    ) {

        if (
                position == null
        ) {

            return List.of();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.involves(
                                        position
                                )
                )
                .toList();
    }

    /*
     * =================================================
     * 최종 상태
     * =================================================
     */

    public boolean multiplePrimaryElements() {

        return primaryElements()
                .size() >= 2;
    }

    public boolean onlyOnePrimaryElement() {

        return primaryElements()
                .size() == 1;
    }

    public boolean noPrimaryCandidate() {

        return primaryCandidates()
                .isEmpty();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}