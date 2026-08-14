package com.dajeom.model;

import com.dajeom.type.ClimateElementCandidateRole;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 6차-3.
 *
 * 한난조습 조정 필요성을 기반으로
 * 五行 전체에 조후 후보 역할을 부여한 결과.
 *
 * 모든 FiveElement가 정확히 한 번씩
 * candidates에 포함된다.
 *
 * 중요한 원칙:
 *
 * PRIMARY가 반드시 존재하는 것은 아니다.
 *
 * 예:
 *
 * COLD_DRY
 * +
 * 한난 필요도와 조습 필요도가 동일
 *
 * 같은 경우:
 *
 * FIRE도 한 축을 악화시키고
 * WATER도 다른 축을 악화시키므로
 *
 * 둘 다 CONDITIONAL로 남겨
 * 6-8에서 추가 조정할 수 있다.
 */
public record ClimateElementCandidateAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        ClimateAdjustmentNeedAnalysis adjustmentNeedAnalysis,

        List<ClimateElementCandidate> candidates,

        boolean completeChart

) {

    public ClimateElementCandidateAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                adjustmentNeedAnalysis,
                "조후 필요성 분석이 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        if (
                candidates.size()
                        != FiveElement.values().length
        ) {

            throw new IllegalArgumentException(
                    "조후 오행 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                ClimateElementCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 조후 오행 후보가 중복되었습니다: "
                                + candidate.element()
                );
            }
        }

        if (
                elements.size()
                        != FiveElement.values().length
        ) {

            throw new IllegalArgumentException(
                    "조후 오행 후보에 일부 오행이 누락되었습니다."
            );
        }

        long primaryCount =
                candidates
                        .stream()
                        .filter(
                                ClimateElementCandidate
                                        ::primary
                        )
                        .count();

        if (
                primaryCount > 1
        ) {

            throw new IllegalArgumentException(
                    "6-3 조후 PRIMARY 후보는 최대 하나만 허용합니다."
            );
        }

        if (
                adjustmentNeedAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "조후 필요성 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                adjustmentNeedAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "조후 필요성 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                adjustmentNeedAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 완전성 상태가 조후 필요성 분석과 일치하지 않습니다."
            );
        }

        if (
                !adjustmentNeedAnalysis
                        .adjustmentRequired()

                        && candidates
                        .stream()
                        .anyMatch(
                                ClimateElementCandidate
                                        ::needed
                        )
        ) {

            throw new IllegalArgumentException(
                    "조후 조정이 필요하지 않은 명식에 조후 후보가 생성되었습니다."
            );
        }
    }

    public Optional<ClimateElementCandidate>
    primaryCandidate() {

        return candidates
                .stream()
                .filter(
                        ClimateElementCandidate
                                ::primary
                )
                .findFirst();
    }

    public List<ClimateElementCandidate>
    secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateElementCandidate
                                ::secondary
                )
                .toList();
    }

    public List<ClimateElementCandidate>
    conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateElementCandidate
                                ::conditional
                )
                .toList();
    }

    public List<ClimateElementCandidate>
    neededCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateElementCandidate
                                ::needed
                )
                .toList();
    }

    public List<ClimateElementCandidate>
    directCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateElementCandidate
                                ::directCandidate
                )
                .filter(
                        ClimateElementCandidate
                                ::needed
                )
                .toList();
    }

    public List<ClimateElementCandidate>
    bridgeCandidates() {

        return candidates
                .stream()
                .filter(
                        ClimateElementCandidate
                                ::bridgeCandidate
                )
                .filter(
                        ClimateElementCandidate
                                ::needed
                )
                .toList();
    }

    public Optional<ClimateElementCandidate>
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

    public boolean hasDecisivePrimary() {

        return primaryCandidate()
                .isPresent();
    }

    /**
     * COLD_DRY / HEAT_WET처럼
     * 보정 방향이 경쟁하는데
     * 어느 한 축도 우선하지 않아
     * PRIMARY를 만들지 않은 상태.
     */
    public boolean competingWithoutPrimary() {

        return adjustmentNeedAnalysis
                .correctionConflict()

                && primaryCandidate()
                .isEmpty();
    }

    public boolean adjustmentRequired() {

        return adjustmentNeedAnalysis
                .adjustmentRequired();
    }

    public int countRole(
            ClimateElementCandidateRole role
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