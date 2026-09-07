package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 일간이 참여하는 모든 천간오합과
 * 화기격 후보 분석 결과.
 */
public record DayMasterStemTransformationCandidateAnalysis(

        HeavenlyStem dayMaster,

        List<DayMasterStemTransformationCandidate> candidates

) {

    public DayMasterStemTransformationCandidateAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        for (
                DayMasterStemTransformationCandidate candidate
                : candidates
        ) {

            Objects.requireNonNull(
                    candidate,
                    "화기격 후보 목록에 null이 포함될 수 없습니다."
            );

            if (
                    candidate.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "화기격 후보의 일간이 분석 대상 일간과 일치하지 않습니다."
                );
            }
        }
    }

    public boolean hasDayMasterCombination() {

        return !candidates.isEmpty();
    }

    public boolean hasTrueTransformationReviewCandidate() {

        return candidates
                .stream()
                .anyMatch(
                        candidate ->
                                candidate.candidateType()
                                        == DayMasterStemTransformationCandidateType
                                        .TRUE_TRANSFORMATION_REVIEW
                );
    }

    public boolean hasFakeOrIncompleteReviewCandidate() {

        return candidates
                .stream()
                .anyMatch(
                        candidate ->
                                candidate.candidateType()
                                        == DayMasterStemTransformationCandidateType
                                        .FAKE_OR_INCOMPLETE_TRANSFORMATION_REVIEW
                );
    }

    public boolean hasContestedCandidate() {

        return candidates
                .stream()
                .anyMatch(
                        candidate ->
                                candidate.candidateType()
                                        == DayMasterStemTransformationCandidateType
                                        .CONTESTED_TRANSFORMATION_REVIEW
                );
    }

    public boolean requiresTransformationReview() {

        return hasDayMasterCombination();
    }
}