package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.ExtremeStrengthStructureType;
import com.dajeom.type.FiveElement;
import com.dajeom.type.FollowStructureUsefulGodCandidateRole;
import com.dajeom.type.FollowWeakDirection;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodType;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-7.
 *
 * 종격용신법으로 木火土金水 전체를 평가한 결과.
 *
 * =================================================
 *
 * 실제 종격이 주격일 때만 활성화된다.
 *
 * 종격 후보가 단순히 4-7 또는 5-8에서
 * 검토되었다는 이유만으로 활성화하지 않는다.
 *
 * 5-9에서 실제 PRIMARY가 종격으로
 * 선택되어야 한다.
 *
 * =================================================
 *
 * 종약
 *
 * FollowWeakDirection:
 *
 * OUTPUT
 * WEALTH
 * OFFICER
 * MIXED
 *
 * =================================================
 *
 * 종강
 *
 * COMPANION
 * RESOURCE
 *
 * 흐름을 사용한다.
 *
 * =================================================
 *
 * 아직 최종 주용신이 아니다.
 */
public record FollowStructureUsefulGodAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        UsefulGodMethodAnalysis methodAnalysis,

        UsefulGodMethodEvidence methodEvidence,

        ExtremeStrengthStructureType followStructureType,

        FollowWeakDirection followWeakDirection,

        List<FollowStructureUsefulGodCandidate> candidates,

        boolean followStructureMode,

        boolean completeChart

) {

    public FollowStructureUsefulGodAnalysis {

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
                "종격용신법 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                followStructureType,
                "극단구조 유형이 필요합니다."
        );

        Objects.requireNonNull(
                followWeakDirection,
                "종약 방향이 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        if (
                methodEvidence.methodType()
                        != UsefulGodMethodType.FOLLOW_STRUCTURE
        ) {

            throw new IllegalArgumentException(
                    "7-7에는 FOLLOW_STRUCTURE 용신법 Evidence가 필요합니다."
            );
        }

        if (
                candidates.size()
                        != FiveElement.values().length
        ) {

            throw new IllegalArgumentException(
                    "종격용신 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                FollowStructureUsefulGodCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 종격용신 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }

            if (
                    candidate.methodApplicability()
                            != methodEvidence.applicability()
            ) {

                throw new IllegalArgumentException(
                        "종격용신 후보의 방법 적용도가 7-1 결과와 일치하지 않습니다."
                );
            }

            if (
                    candidate.followStructureType()
                            != followStructureType
            ) {

                throw new IllegalArgumentException(
                        "종격용신 후보의 종격 유형이 분석 결과와 일치하지 않습니다."
                );
            }

            if (
                    candidate.followWeakDirection()
                            != followWeakDirection
            ) {

                throw new IllegalArgumentException(
                        "종격용신 후보의 종약 방향이 분석 결과와 일치하지 않습니다."
                );
            }
        }

        if (
                methodAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "7-1과 7-7의 일간이 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "7-1과 7-7의 일간 오행이 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "7-1과 7-7의 월지가 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "7-1과 7-7의 원국 완전성 상태가 일치하지 않습니다."
            );
        }

        if (
                methodAnalysis.followStructureMode()
                        != followStructureMode
        ) {

            throw new IllegalArgumentException(
                    "7-1과 7-7의 종격 모드가 일치하지 않습니다."
            );
        }

        if (
                followStructureMode
                        != methodEvidence.applicability().core()
        ) {

            throw new IllegalArgumentException(
                    "종격 모드와 FOLLOW_STRUCTURE 적용도가 일치하지 않습니다."
            );
        }

        if (
                followStructureMode
        ) {

            boolean validFollowType =
                    followStructureType
                            == ExtremeStrengthStructureType
                            .FOLLOW_WEAK_CANDIDATE

                            || followStructureType
                            == ExtremeStrengthStructureType
                            .FOLLOW_STRONG_CANDIDATE;

            if (
                    !validFollowType
            ) {

                throw new IllegalArgumentException(
                        "종격 모드에는 FOLLOW_WEAK_CANDIDATE 또는 FOLLOW_STRONG_CANDIDATE가 필요합니다."
                );
            }
        }
    }

    public UsefulGodMethodApplicability methodApplicability() {

        return methodEvidence.applicability();
    }

    public boolean followWeak() {

        return followStructureType
                == ExtremeStrengthStructureType
                .FOLLOW_WEAK_CANDIDATE;
    }

    public boolean followStrong() {

        return followStructureType
                == ExtremeStrengthStructureType
                .FOLLOW_STRONG_CANDIDATE;
    }

    public List<FollowStructureUsefulGodCandidate> primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        FollowStructureUsefulGodCandidate::primary
                )
                .toList();
    }

    public List<FollowStructureUsefulGodCandidate> secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        FollowStructureUsefulGodCandidate::secondary
                )
                .toList();
    }

    public List<FollowStructureUsefulGodCandidate> conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        FollowStructureUsefulGodCandidate::conditional
                )
                .toList();
    }

    public List<FollowStructureUsefulGodCandidate> selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        FollowStructureUsefulGodCandidate::selected
                )
                .toList();
    }

    public List<FollowStructureUsefulGodCandidate> flowBreakingElements() {

        return candidates
                .stream()
                .filter(
                        FollowStructureUsefulGodCandidate::breaksFollowFlow
                )
                .toList();
    }

    public Optional<FollowStructureUsefulGodCandidate> candidateOf(
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
                                candidate.element() == element
                )
                .findFirst();
    }

    public boolean hasPrimaryCandidate() {

        return !primaryCandidates().isEmpty();
    }

    public boolean hasMultiplePrimaryCandidates() {

        return primaryCandidates().size() >= 2;
    }

    public boolean hasFlowBreakingElement() {

        return !flowBreakingElements().isEmpty();
    }

    public boolean followMethodCore() {

        return methodEvidence
                .applicability()
                .core();
    }

    public boolean followMethodActive() {

        return methodEvidence
                .applicability()
                .applicable();
    }

    public boolean unresolvedFollowSelection() {

        return followStructureMode
                && selectedCandidates().isEmpty();
    }

    public int countRole(
            FollowStructureUsefulGodCandidateRole role
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
                                candidate.role() == role
                )
                .count();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}