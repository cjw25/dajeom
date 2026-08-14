package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemCombinationApplicability;
import com.dajeom.type.HeavenlyStemCombinationType;
import com.dajeom.type.VisibleStemPosition;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 9차-1.
 *
 * 원국 전체 천간합 후보의
 * 기본 적용 조건 분석.
 *
 * =================================================
 *
 * 입력 기반:
 *
 * 8-1 HeavenlyStemCombinationAnalysis
 * +
 * 3차 ElementStrengthAnalysis
 *
 * =================================================
 *
 * 주의:
 *
 * 후보가 하나도 없는 것은 정상이다.
 *
 * 즉:
 *
 * 8-1에서 천간합이 없다면
 * 9-1 candidates도 빈 List다.
 *
 * =================================================
 *
 * 또한 복수 후보를 허용한다.
 *
 * 예:
 *
 * 年己
 * 日甲
 * 時己
 *
 * 라면:
 *
 * 年己-日甲
 * 日甲-時己
 *
 * 두 후보가 모두 남는다.
 *
 * 어느 후보가 우세한지는
 * 9-2에서 처리한다.
 */
public record HeavenlyStemCombinationCandidateAnalysis(

        HeavenlyStem dayMaster,

        HeavenlyStemCombinationAnalysis
        combinationAnalysis,

        ElementStrengthAnalysis
        elementStrengthAnalysis,

        List<HeavenlyStemCombinationCandidate>
        candidates,

        boolean completeChart

) {

    public HeavenlyStemCombinationCandidateAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                combinationAnalysis,
                "8-1 천간합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                elementStrengthAnalysis,
                "3차 오행 세력 분석이 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        if (
                combinationAnalysis.dayMaster()
                        != dayMaster

                        || elementStrengthAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "천간합 분석과 오행 세력 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                combinationAnalysis.completeChart()
                        != completeChart

                        || elementStrengthAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "9-1 입력 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        /*
         * 8-1 Evidence 하나마다
         * 9-1 Candidate 하나가 존재해야 한다.
         */
        if (
                candidates.size()
                        != combinationAnalysis
                        .evidences()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "8-1 천간합 Evidence 수와 9-1 후보 수가 일치하지 않습니다."
            );
        }

        for (
                HeavenlyStemCombinationCandidate candidate
                : candidates
        ) {

            if (
                    !combinationAnalysis
                            .evidences()
                            .contains(
                                    candidate
                                            .sourceEvidence()
                            )
            ) {

                throw new IllegalArgumentException(
                        "9-1 후보가 8-1 원본 Evidence에 존재하지 않습니다."
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

    public List<HeavenlyStemCombinationCandidate>
    strongCandidates() {

        return candidates
                .stream()
                .filter(
                        HeavenlyStemCombinationCandidate
                                ::strong
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCandidate>
    activeCandidates() {

        return candidates
                .stream()
                .filter(
                        HeavenlyStemCombinationCandidate
                                ::active
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCandidate>
    conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        HeavenlyStemCombinationCandidate
                                ::conditional
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCandidate>
    weakCandidates() {

        return candidates
                .stream()
                .filter(
                        HeavenlyStemCombinationCandidate
                                ::weak
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCandidate>
    meaningfulCandidates() {

        return candidates
                .stream()
                .filter(
                        HeavenlyStemCombinationCandidate
                                ::meaningfulCandidate
                )
                .toList();
    }

    public boolean hasStrongCandidate() {

        return !strongCandidates()
                .isEmpty();
    }

    public boolean hasMeaningfulCandidate() {

        return !meaningfulCandidates()
                .isEmpty();
    }

    public boolean hasMultipleCandidates() {

        return candidates
                .size() >= 2;
    }

    public boolean hasMultipleMeaningfulCandidates() {

        return meaningfulCandidates()
                .size() >= 2;
    }

    public List<HeavenlyStemCombinationCandidate>
    dayMasterCandidates() {

        return candidates
                .stream()
                .filter(
                        HeavenlyStemCombinationCandidate
                                ::dayMasterInvolved
                )
                .toList();
    }

    public boolean dayMasterCombined() {

        return !dayMasterCandidates()
                .isEmpty();
    }

    public boolean dayMasterHasMultipleCandidates() {

        return dayMasterCandidates()
                .size() >= 2;
    }

    public List<HeavenlyStemCombinationCandidate>
    candidatesOf(
            HeavenlyStemCombinationType combinationType
    ) {

        if (
                combinationType == null
        ) {

            return List.of();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.combinationType()
                                        == combinationType
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCandidate>
    candidatesOf(
            HeavenlyStemCombinationApplicability applicability
    ) {

        if (
                applicability == null
        ) {

            return List.of();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.applicability()
                                        == applicability
                )
                .toList();
    }

    public List<HeavenlyStemCombinationCandidate>
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

    public List<HeavenlyStemCombinationCandidate>
    candidatesInvolving(
            VisibleStemPosition position
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

    public Optional<HeavenlyStemCombinationCandidate>
    candidateBetween(

            VisibleStemPosition position1,

            VisibleStemPosition position2

    ) {

        if (
                position1 == null
                        || position2 == null
                        || position1 == position2
        ) {

            return Optional.empty();
        }

        return candidates
                .stream()
                .filter(
                        candidate ->
                                candidate.involves(
                                        position1
                                )
                                        && candidate.involves(
                                        position2
                                )
                )
                .findFirst();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}