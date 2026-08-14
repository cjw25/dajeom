package com.dajeom.model;

import com.dajeom.type.BalancingUsefulGodCandidateRole;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-2.
 *
 * 억부용신법으로 木火土金水 전체를
 * 평가한 결과.
 *
 * 모든 FiveElement가 정확히 한 번씩 존재한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 신약이라고:
 *
 * RESOURCE
 * COMPANION
 *
 * 둘을 무조건 같은 PRIMARY로 만들지 않는다.
 *
 * 신강이라고:
 *
 * OUTPUT
 * WEALTH
 * OFFICER
 *
 * 셋을 무조건 같은 PRIMARY로 만들지 않는다.
 *
 * 실제 병 구조와 현재 세력을 이용해
 * 우선 역할을 분리한다.
 *
 * =================================================
 *
 * 종격 주격에서는 일반 억부 후보를
 * CONDITIONAL로 제한한다.
 */
public record BalancingUsefulGodAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        UsefulGodMethodAnalysis methodAnalysis,

        UsefulGodMethodEvidence methodEvidence,

        List<BalancingUsefulGodCandidate> candidates,

        boolean followStructureSuppressed,

        boolean completeChart

) {

    public BalancingUsefulGodAnalysis {

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
                methodAnalysis,
                "7-1 용신법 분석이 필요합니다."
        );

        Objects.requireNonNull(
                methodEvidence,
                "억부용신법 Evidence가 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        if (
                methodEvidence.methodType()
                        != UsefulGodMethodType.BALANCING
        ) {

            throw new IllegalArgumentException(
                    "7-2에는 BALANCING 용신법 Evidence가 필요합니다."
            );
        }

        if (
                candidates.size()
                        != FiveElement
                        .values()
                        .length
        ) {

            throw new IllegalArgumentException(
                    "억부 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                BalancingUsefulGodCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 억부 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }

            if (
                    candidate.methodApplicability()
                            != methodEvidence.applicability()
            ) {

                throw new IllegalArgumentException(
                        "억부 후보의 용신법 적용도가 7-1 결과와 일치하지 않습니다."
                );
            }
        }

        if (
                methodAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "7-1과 7-2의 일간이 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "7-1과 7-2의 일간 오행이 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "7-1과 7-2의 월지가 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "7-1과 7-2의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        if (
                followStructureSuppressed
                        != methodEvidence
                        .suppressedByFollowStructure()
        ) {

            throw new IllegalArgumentException(
                    "종격에 의한 억부 억제 상태가 7-1 결과와 일치하지 않습니다."
            );
        }
    }

    public UsefulGodMethodApplicability
    methodApplicability() {

        return methodEvidence
                .applicability();
    }

    public List<BalancingUsefulGodCandidate>
    primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        BalancingUsefulGodCandidate
                                ::primary
                )
                .toList();
    }

    public List<BalancingUsefulGodCandidate>
    secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        BalancingUsefulGodCandidate
                                ::secondary
                )
                .toList();
    }

    public List<BalancingUsefulGodCandidate>
    conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        BalancingUsefulGodCandidate
                                ::conditional
                )
                .toList();
    }

    public List<BalancingUsefulGodCandidate>
    selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        BalancingUsefulGodCandidate
                                ::selected
                )
                .toList();
    }

    public Optional<BalancingUsefulGodCandidate>
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

    public boolean suppressedByFollowStructure() {

        return followStructureSuppressed;
    }

    public boolean balancingMethodCore() {

        return methodEvidence
                .applicability()
                .core();
    }

    public boolean balancingMethodActive() {

        return methodEvidence
                .applicability()
                .applicable();
    }

    public int countRole(
            BalancingUsefulGodCandidateRole role
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