package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PassageCandidateRole;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 6차-7.
 *
 * 木火土金水 전체에
 * 통관 후보 역할을 부여한 분석 결과.
 *
 * PRIMARY는 여러 개 존재할 수 있다.
 *
 * 이유:
 *
 * 서로 다른 상극 병목이
 * 동시에 의미 있게 존재할 수 있기 때문이다.
 *
 * 어느 하나를 최종 선택하는 책임은
 * 6-8 / 6-9에 있다.
 */
public record PassageCandidateAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        PassageNeedAnalysis passageNeedAnalysis,

        RemedyAnalysis remedyAnalysis,

        List<PassageCandidate> candidates,

        boolean completeChart

) {

    public PassageCandidateAnalysis {

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
                passageNeedAnalysis,
                "통관 필요성 분석이 필요합니다."
        );

        Objects.requireNonNull(
                remedyAnalysis,
                "병약 분석이 필요합니다."
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
                    "통관 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                PassageCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 통관 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }
        }

        if (
                passageNeedAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "통관 필요성 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                passageNeedAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "통관 필요성 분석의 일간 오행이 일치하지 않습니다."
            );
        }

        if (
                passageNeedAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "통관 필요성 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                remedyAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "병약 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                remedyAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "병약 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                passageNeedAnalysis.completeChart()
                        != completeChart

                        || remedyAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }

    public List<PassageCandidate>
    primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageCandidate
                                ::primary
                )
                .toList();
    }

    public List<PassageCandidate>
    secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageCandidate
                                ::secondary
                )
                .toList();
    }

    public List<PassageCandidate>
    conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageCandidate
                                ::conditional
                )
                .toList();
    }

    public List<PassageCandidate>
    neededCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageCandidate
                                ::candidate
                )
                .toList();
    }

    public List<PassageCandidate>
    selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        PassageCandidate
                                ::selected
                )
                .toList();
    }

    public Optional<PassageCandidate>
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
                        PassageCandidate
                                ::crossAnalysisConflict
                );
    }

    public boolean hasRemedyAlignment() {

        return candidates
                .stream()
                .filter(
                        PassageCandidate
                                ::candidate
                )
                .anyMatch(
                        PassageCandidate
                                ::remedyAligned
                );
    }

    public boolean hasClimateAlignment() {

        return candidates
                .stream()
                .filter(
                        PassageCandidate
                                ::candidate
                )
                .anyMatch(
                        PassageCandidate
                                ::climateAligned
                );
    }

    public boolean hasTripleAlignment() {

        return candidates
                .stream()
                .filter(
                        PassageCandidate
                                ::candidate
                )
                .anyMatch(
                        PassageCandidate
                                ::bothAnalysesAligned
                );
    }

    /**
     * 의미 있는 통관 필요성은 존재하지만
     * PRIMARY 후보를 아직 만들지 못한 상태.
     *
     * 충돌 조정이 필요한 대표적인 경우다.
     */
    public boolean meaningfulNeedWithoutPrimary() {

        return passageNeedAnalysis
                .meaningfulPassageRequired()

                && primaryCandidates()
                .isEmpty();
    }

    public int countRole(
            PassageCandidateRole role
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