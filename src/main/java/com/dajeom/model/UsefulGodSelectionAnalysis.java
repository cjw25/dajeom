package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.UsefulGodCandidateRole;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 7차-8.
 *
 * 여섯 용신법의 후보를 통합하여
 * 실제 주용신 후보를 결정한 결과.
 *
 * =================================================
 *
 * 포함:
 *
 * 7-1 UsefulGodMethodAnalysis
 *
 * 7-2 BalancingUsefulGodAnalysis
 *
 * 7-3 ClimateUsefulGodAnalysis
 *
 * 7-4 StructureUsefulGodAnalysis
 *
 * 7-5 RemedyUsefulGodAnalysis
 *
 * 7-6 PassageUsefulGodAnalysis
 *
 * 7-7 FollowStructureUsefulGodAnalysis
 *
 * =================================================
 *
 * 이번 단계에서 처음으로
 * PRIMARY_USEFUL_GOD가 만들어진다.
 *
 * 그러나:
 *
 * 희신
 * 기신
 *
 * 은 아직 판정하지 않는다.
 */
public record UsefulGodSelectionAnalysis(

        HeavenlyStem dayMaster,

        FiveElement dayMasterElement,

        EarthlyBranch monthBranch,

        UsefulGodMethodAnalysis methodAnalysis,

        BalancingUsefulGodAnalysis balancingAnalysis,

        ClimateUsefulGodAnalysis climateAnalysis,

        StructureUsefulGodAnalysis structureAnalysis,

        RemedyUsefulGodAnalysis remedyAnalysis,

        PassageUsefulGodAnalysis passageAnalysis,

        FollowStructureUsefulGodAnalysis followStructureAnalysis,

        List<UsefulGodCandidate> candidates,

        boolean followStructureMode,

        boolean completeChart

) {

    public UsefulGodSelectionAnalysis {

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
                balancingAnalysis,
                "7-2 억부용신 분석이 필요합니다."
        );

        Objects.requireNonNull(
                climateAnalysis,
                "7-3 조후용신 분석이 필요합니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "7-4 격국용신 분석이 필요합니다."
        );

        Objects.requireNonNull(
                remedyAnalysis,
                "7-5 병약용신 분석이 필요합니다."
        );

        Objects.requireNonNull(
                passageAnalysis,
                "7-6 통관용신 분석이 필요합니다."
        );

        Objects.requireNonNull(
                followStructureAnalysis,
                "7-7 종격용신 분석이 필요합니다."
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
                    "용신 통합 후보에는 木火土金水 전체가 정확히 한 번씩 포함되어야 합니다."
            );
        }

        EnumSet<FiveElement> elements =
                EnumSet.noneOf(
                        FiveElement.class
                );

        for (
                UsefulGodCandidate candidate
                : candidates
        ) {

            if (
                    !elements.add(
                            candidate.element()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일한 용신 통합 후보 오행이 중복되었습니다: "
                                + candidate.element()
                );
            }

            if (
                    candidate.followStructureMode()
                            != followStructureMode
            ) {

                throw new IllegalArgumentException(
                        "용신 후보의 종격 모드가 전체 분석과 일치하지 않습니다."
                );
            }
        }

        validateIdentity(
                dayMaster,
                dayMasterElement,
                monthBranch,
                completeChart,
                methodAnalysis,
                balancingAnalysis,
                climateAnalysis,
                structureAnalysis,
                remedyAnalysis,
                passageAnalysis,
                followStructureAnalysis
        );

        if (
                methodAnalysis.followStructureMode()
                        != followStructureMode

                        || followStructureAnalysis
                        .followStructureMode()
                        != followStructureMode
        ) {

            throw new IllegalArgumentException(
                    "7-1/7-7/7-8의 종격 모드가 일치하지 않습니다."
            );
        }
    }

    private static void validateIdentity(

            HeavenlyStem dayMaster,

            FiveElement dayMasterElement,

            EarthlyBranch monthBranch,

            boolean completeChart,

            UsefulGodMethodAnalysis method,

            BalancingUsefulGodAnalysis balancing,

            ClimateUsefulGodAnalysis climate,

            StructureUsefulGodAnalysis structure,

            RemedyUsefulGodAnalysis remedy,

            PassageUsefulGodAnalysis passage,

            FollowStructureUsefulGodAnalysis follow

    ) {

        if (
                method.dayMaster() != dayMaster
                        || balancing.dayMaster() != dayMaster
                        || climate.dayMaster() != dayMaster
                        || structure.dayMaster() != dayMaster
                        || remedy.dayMaster() != dayMaster
                        || passage.dayMaster() != dayMaster
                        || follow.dayMaster() != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "7차 용신 분석 단계들의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                method.dayMasterElement() != dayMasterElement
                        || balancing.dayMasterElement() != dayMasterElement
                        || climate.dayMasterElement() != dayMasterElement
                        || structure.dayMasterElement() != dayMasterElement
                        || remedy.dayMasterElement() != dayMasterElement
                        || passage.dayMasterElement() != dayMasterElement
                        || follow.dayMasterElement() != dayMasterElement
        ) {

            throw new IllegalArgumentException(
                    "7차 용신 분석 단계들의 일간 오행이 서로 일치하지 않습니다."
            );
        }

        if (
                method.monthBranch() != monthBranch
                        || balancing.monthBranch() != monthBranch
                        || climate.monthBranch() != monthBranch
                        || structure.monthBranch() != monthBranch
                        || remedy.monthBranch() != monthBranch
                        || passage.monthBranch() != monthBranch
                        || follow.monthBranch() != monthBranch
        ) {

            throw new IllegalArgumentException(
                    "7차 용신 분석 단계들의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                method.completeChart() != completeChart
                        || balancing.completeChart() != completeChart
                        || climate.completeChart() != completeChart
                        || structure.completeChart() != completeChart
                        || remedy.completeChart() != completeChart
                        || passage.completeChart() != completeChart
                        || follow.completeChart() != completeChart
        ) {

            throw new IllegalArgumentException(
                    "7차 용신 분석 단계들의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }

    public List<UsefulGodCandidate> primaryCandidates() {

        return candidates
                .stream()
                .filter(
                        UsefulGodCandidate::primaryUsefulGod
                )
                .toList();
    }

    public List<UsefulGodCandidate> supportingCandidates() {

        return candidates
                .stream()
                .filter(
                        UsefulGodCandidate::supportingUsefulGod
                )
                .toList();
    }

    public List<UsefulGodCandidate> deferredCandidates() {

        return candidates
                .stream()
                .filter(
                        UsefulGodCandidate::deferred
                )
                .toList();
    }

    public List<UsefulGodCandidate> selectedCandidates() {

        return candidates
                .stream()
                .filter(
                        UsefulGodCandidate::selected
                )
                .toList();
    }

    public Optional<UsefulGodCandidate> candidateOf(
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

    /**
     * 정확히 하나의 주용신 후보만 존재할 때 반환.
     */
    public Optional<UsefulGodCandidate> singlePrimaryCandidate() {

        List<UsefulGodCandidate> primary =
                primaryCandidates();

        if (
                primary.size() != 1
        ) {

            return Optional.empty();
        }

        return Optional.of(
                primary.get(0)
        );
    }

    public Optional<FiveElement> singlePrimaryElement() {

        return singlePrimaryCandidate()
                .map(
                        UsefulGodCandidate::element
                );
    }

    public List<FiveElement> primaryElements() {

        return primaryCandidates()
                .stream()
                .map(
                        UsefulGodCandidate::element
                )
                .toList();
    }

    public boolean hasPrimaryUsefulGod() {

        return !primaryCandidates()
                .isEmpty();
    }

    public boolean hasSinglePrimaryUsefulGod() {

        return primaryCandidates()
                .size() == 1;
    }

    public boolean hasMultiplePrimaryUsefulGods() {

        return primaryCandidates()
                .size() >= 2;
    }

    public boolean hasSupportingUsefulGod() {

        return !supportingCandidates()
                .isEmpty();
    }

    public boolean hasDeferredCandidate() {

        return !deferredCandidates()
                .isEmpty();
    }

    /**
     * 서로 다른 CORE 방법들이
     * 서로 다른 오행을 주용신 후보로 남긴 경우 등을
     * 추가 검토 대상으로 본다.
     */
    public boolean requiresPrimaryReview() {

        return hasMultiplePrimaryUsefulGods()
                || (
                !hasPrimaryUsefulGod()
                        && hasDeferredCandidate()
        );
    }

    public boolean unresolvedUsefulGodSelection() {

        return !hasPrimaryUsefulGod()
                && !hasSupportingUsefulGod();
    }

    public int countRole(
            UsefulGodCandidateRole role
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