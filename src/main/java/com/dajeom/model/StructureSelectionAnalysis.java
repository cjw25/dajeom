package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.SpecialStructureType;
import com.dajeom.type.StructureCandidateRole;
import com.dajeom.type.StructureTenGodType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-9.
 *
 * 전체 일반격/특수격 후보 가운데:
 *
 * - 주격 후보 1개
 * - 보조격 후보 0~N개
 * - 보류 후보
 * - 제외 후보
 *
 * 를 결정한 결과.
 *
 * 아직 5-10 최종 StructureAnalysis 이전이므로
 * PRIMARY 역시 최종 외부 API 격국값은 아니다.
 */
public record StructureSelectionAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        StructureSelectionCandidate primaryCandidate,

        List<StructureSelectionCandidate> candidates

) {

    public StructureSelectionAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        Objects.requireNonNull(
                primaryCandidate,
                "주격 후보가 필요합니다."
        );

        candidates =
                candidates == null
                        ? List.of()
                        : List.copyOf(
                        candidates
                );

        if (
                candidates.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "격국 후보가 하나 이상 필요합니다."
            );
        }

        long primaryCount =
                candidates
                        .stream()
                        .filter(
                                StructureSelectionCandidate
                                        ::primary
                        )
                        .count();

        if (
                primaryCount != 1
        ) {

            throw new IllegalArgumentException(
                    "주격 후보는 정확히 하나여야 합니다."
            );
        }

        boolean primaryIncluded =
                candidates
                        .stream()
                        .anyMatch(
                                candidate ->
                                        sameIdentity(
                                                candidate,
                                                primaryCandidate
                                        )
                                                && candidate.primary()
                        );

        if (
                !primaryIncluded
        ) {

            throw new IllegalArgumentException(
                    "primaryCandidate가 전체 후보 목록에 포함되어 있지 않습니다."
            );
        }

        boolean duplicate =
                false;

        for (
                int first = 0;
                first < candidates.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < candidates.size();
                    second++
            ) {

                if (
                        sameIdentity(
                                candidates.get(
                                        first
                                ),
                                candidates.get(
                                        second
                                )
                        )
                ) {

                    duplicate =
                            true;

                    break;
                }
            }

            if (
                    duplicate
            ) {

                break;
            }
        }

        if (
                duplicate
        ) {

            throw new IllegalArgumentException(
                    "동일한 격국 후보가 중복되었습니다."
            );
        }
    }

    private static boolean sameIdentity(

            StructureSelectionCandidate first,

            StructureSelectionCandidate second

    ) {

        if (
                first.kind()
                        != second.kind()
        ) {

            return false;
        }

        if (
                first.standard()
        ) {

            return first.standardTenGod()
                    == second.standardTenGod();
        }

        return first.specialType()
                == second.specialType();
    }

    public String primaryNameKorean() {

        return primaryCandidate
                .candidateNameKorean();
    }

    public List<StructureSelectionCandidate>
    secondaryCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureSelectionCandidate
                                ::secondary
                )
                .toList();
    }

    public List<StructureSelectionCandidate>
    deferredCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureSelectionCandidate
                                ::deferred
                )
                .toList();
    }

    public List<StructureSelectionCandidate>
    rejectedCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureSelectionCandidate
                                ::rejected
                )
                .toList();
    }

    public List<StructureSelectionCandidate>
    selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        StructureSelectionCandidate
                                ::selected
                )
                .toList();
    }

    public boolean primaryIsStandard() {

        return primaryCandidate
                .standard();
    }

    public boolean primaryIsSpecial() {

        return primaryCandidate
                .special();
    }

    public Optional<StructureTenGodType>
    primaryStandardTenGod() {

        if (
                !primaryIsStandard()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                primaryCandidate
                        .standardTenGod()
        );
    }

    public Optional<SpecialStructureType>
    primarySpecialType() {

        if (
                !primaryIsSpecial()
        ) {

            return Optional.empty();
        }

        return Optional.of(
                primaryCandidate
                        .specialType()
        );
    }

    public int secondaryCount() {

        return secondaryCandidates()
                .size();
    }

    public int deferredCount() {

        return deferredCandidates()
                .size();
    }

    public int rejectedCount() {

        return rejectedCandidates()
                .size();
    }

    public boolean hasSecondaryStructure() {

        return secondaryCount() > 0;
    }

    public int countRole(
            StructureCandidateRole role
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
}