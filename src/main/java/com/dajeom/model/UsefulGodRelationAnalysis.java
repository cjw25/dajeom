package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.UsefulGodRelationRole;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 정밀화 7차-9.
 *
 * 7-8 주용신을 기준으로
 * 木火土金水 전체에 대해:
 *
 * - 용신
 * - 희신
 * - 기신
 * - 조건부
 * - 중립
 *
 * 을 판정한 결과.
 *
 * =================================================
 *
 * 주용신이 복수인 경우
 * 각 주용신과의 관계를 모두 보존한다.
 *
 * =================================================
 *
 * 주용신 자체가 없는 경우
 * 희신·기신을 억지로 확정하지 않는다.
 */
public record UsefulGodRelationAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        UsefulGodSelectionAnalysis selectionAnalysis,

        List<UsefulGodRelationCandidate> candidates,

        boolean completeChart

) {

    public UsefulGodRelationAnalysis {

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
                selectionAnalysis,
                "7-8 주용신 통합 분석이 필요합니다."
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
                    "7-9 희신·기신 분석에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        EnumSet<FiveElement> expectedPrimaryElements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        expectedPrimaryElements.addAll(
                selectionAnalysis.primaryElements()
        );

        for (
                UsefulGodRelationCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 희신·기신 판정 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }

            UsefulGodCandidate source =
                    selectionAnalysis
                            .candidateOf(
                                    candidate.element()
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalArgumentException(
                                                    "7-8 원본 후보를 찾을 수 없습니다: "
                                                            + candidate.element()
                                            )
                            );

            if (
                    source.role()
                            != candidate
                            .sourceCandidate()
                            .role()
            ) {

                throw new IllegalArgumentException(
                        "7-8 원본 후보 역할이 7-9 후보와 일치하지 않습니다."
                );
            }

            Set<FiveElement> actualPrimaryElements =
                    candidate
                            .primaryRelations()
                            .stream()
                            .map(
                                    UsefulGodPrimaryRelationEvidence
                                            ::primaryElement
                            )
                            .collect(
                                    Collectors.toSet()
                            );

            if (
                    !actualPrimaryElements
                            .equals(
                                    expectedPrimaryElements
                            )
            ) {

                throw new IllegalArgumentException(
                        "7-9 후보의 주용신 관계 범위가 7-8 주용신 목록과 일치하지 않습니다: "
                                + candidate.element()
                );
            }
        }

        if (
                selectionAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "7-8과 7-9의 일간이 일치하지 않습니다."
            );
        }

        if (
                selectionAnalysis.dayMasterElement()
                        != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "7-8과 7-9의 일간 오행이 일치하지 않습니다."
            );
        }

        if (
                selectionAnalysis.monthBranch()
                        != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "7-8과 7-9의 월지가 일치하지 않습니다."
            );
        }

        if (
                selectionAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "7-8과 7-9의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }

    public List<UsefulGodRelationCandidate> usefulGodCandidates() {

        return candidates
                .stream()
                .filter(
                        UsefulGodRelationCandidate::usefulGod
                )
                .toList();
    }

    public List<UsefulGodRelationCandidate> favorableGodCandidates() {

        return candidates
                .stream()
                .filter(
                        UsefulGodRelationCandidate::favorableGod
                )
                .toList();
    }

    public List<UsefulGodRelationCandidate> unfavorableGodCandidates() {

        return candidates
                .stream()
                .filter(
                        UsefulGodRelationCandidate::unfavorableGod
                )
                .toList();
    }

    public List<UsefulGodRelationCandidate> conditionalCandidates() {

        return candidates
                .stream()
                .filter(
                        UsefulGodRelationCandidate::conditional
                )
                .toList();
    }

    public List<UsefulGodRelationCandidate> neutralCandidates() {

        return candidates
                .stream()
                .filter(
                        UsefulGodRelationCandidate::neutral
                )
                .toList();
    }

    public List<FiveElement> usefulGodElements() {

        return usefulGodCandidates()
                .stream()
                .map(
                        UsefulGodRelationCandidate::element
                )
                .toList();
    }

    public List<FiveElement> favorableGodElements() {

        return favorableGodCandidates()
                .stream()
                .map(
                        UsefulGodRelationCandidate::element
                )
                .toList();
    }

    public List<FiveElement> unfavorableGodElements() {

        return unfavorableGodCandidates()
                .stream()
                .map(
                        UsefulGodRelationCandidate::element
                )
                .toList();
    }

    public Optional<UsefulGodRelationCandidate> candidateOf(
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

    public boolean hasPrimaryUsefulGod() {

        return selectionAnalysis
                .hasPrimaryUsefulGod();
    }

    public boolean hasSinglePrimaryUsefulGod() {

        return selectionAnalysis
                .hasSinglePrimaryUsefulGod();
    }

    public boolean hasMultiplePrimaryUsefulGods() {

        return selectionAnalysis
                .hasMultiplePrimaryUsefulGods();
    }

    public boolean hasFavorableGod() {

        return !favorableGodCandidates()
                .isEmpty();
    }

    public boolean hasUnfavorableGod() {

        return !unfavorableGodCandidates()
                .isEmpty();
    }

    public boolean hasConditionalRelation() {

        return !conditionalCandidates()
                .isEmpty();
    }

    public boolean unresolvedPrimaryAnchor() {

        return !hasPrimaryUsefulGod();
    }

    public boolean requiresRelationReview() {

        return unresolvedPrimaryAnchor()
                || hasConditionalRelation()
                || selectionAnalysis
                .requiresPrimaryReview();
    }

    public int countRole(
            UsefulGodRelationRole role
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