package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.RemedyCandidateRole;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 6차-5.
 *
 * 원국 전체 약(藥) 후보 분석 결과.
 *
 * 중요한 원칙:
 *
 * PRIMARY가 여러 개 존재할 수 있다.
 *
 * 이유:
 *
 * 병 구조가 여러 개 존재할 수 있고
 * 서로 다른 병이 서로 다른 오행을
 * 요구할 수 있기 때문이다.
 *
 * 6-5에서 억지로 하나만 선택하지 않는다.
 *
 * 최종 충돌 조정은:
 *
 * 6-8
 * 6-9
 *
 * 에서 수행한다.
 */
public record RemedyAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        PathologyAnalysis pathologyAnalysis,

        ClimateElementCandidateAnalysis climateElementCandidateAnalysis,

        List<RemedyCandidate> candidates,

        List<PathologyEvidence> unresolvedPathologies,

        boolean completeChart

) {

    public RemedyAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                pathologyAnalysis,
                "병 구조 분석이 필요합니다."
        );

        Objects.requireNonNull(
                climateElementCandidateAnalysis,
                "조후 오행 후보 분석이 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        unresolvedPathologies =
                unresolvedPathologies == null
                        ? List.of()
                        : List.copyOf(
                        unresolvedPathologies
                );

        if (
                candidates.size()
                        != FiveElement.values().length
        ) {

            throw new IllegalArgumentException(
                    "약 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                RemedyCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 약 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }
        }

        if (
                pathologyAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "병 구조 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                pathologyAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "병 구조 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                climateElementCandidateAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "조후 후보 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                climateElementCandidateAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "조후 후보 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                pathologyAnalysis.completeChart()
                        != completeChart

                        || climateElementCandidateAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }

    public List<RemedyCandidate>
    primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyCandidate
                                ::primary
                )
                .toList();
    }

    public List<RemedyCandidate>
    secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyCandidate
                                ::secondary
                )
                .toList();
    }

    public List<RemedyCandidate>
    conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyCandidate
                                ::conditional
                )
                .toList();
    }

    public List<RemedyCandidate>
    rejectedCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyCandidate
                                ::rejected
                )
                .toList();
    }

    public List<RemedyCandidate>
    selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        RemedyCandidate
                                ::selected
                )
                .toList();
    }

    public Optional<RemedyCandidate>
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

    public boolean hasConditionalConflict() {

        return conditionalCandidates()
                .stream()
                .anyMatch(
                        RemedyCandidate
                                ::hasConflict
                );
    }

    public boolean hasUnresolvedPathology() {

        return !unresolvedPathologies
                .isEmpty();
    }

    public boolean hasUnresolvedMeaningfulPathology() {

        return unresolvedPathologies
                .stream()
                .anyMatch(
                        PathologyEvidence
                                ::meaningful
                );
    }

    public int unresolvedPathologyCount() {

        return unresolvedPathologies
                .size();
    }

    public int countRole(
            RemedyCandidateRole role
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

    public boolean hourUnknown() {

        return !completeChart;
    }
}