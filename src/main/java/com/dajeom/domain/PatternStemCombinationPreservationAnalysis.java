package com.dajeom.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * active 天干合이 실제 격국에서
 *
 * "무엇을 묶고 무엇을 남겼는가"
 *
 * 를 정리한 결과.
 *
 * =================================================
 *
 * 여기서 판단 가능한 대표 구조:
 *
 * - 合殺留官
 * - 合官留殺
 * - 合殺存財
 * - 合財存印
 * - 食神合殺而存財
 *
 * =================================================
 *
 * 여전히 별도 문제:
 *
 * - 合化
 * - 化氣가 원국을 실제 지배하는가
 */
public record PatternStemCombinationPreservationAnalysis(

        HeavenlyStem dayMaster,

        PatternStemCombinationActivationAnalysis activationAnalysis,

        List<PatternStemTenGodBindingAnalysis> bindings

) {

    public PatternStemCombinationPreservationAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                activationAnalysis,
                "천간합 Activation 분석은 필수입니다."
        );

        bindings =
                bindings == null
                        ? List.of()
                        : List.copyOf(bindings);

        if (
                activationAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "ActivationAnalysis와 PreservationAnalysis의 일간이 일치하지 않습니다."
            );
        }

        for (
                PatternStemTenGodBindingAnalysis binding
                : bindings
        ) {

            Objects.requireNonNull(
                    binding,
                    "Binding 목록에 null이 포함될 수 없습니다."
            );
        }
    }

    public PatternStemTenGodBindingAnalysis bindingOf(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return bindings
                .stream()
                .filter(
                        binding ->
                                binding.tenGodType()
                                        == type
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "해당 십성의 Binding 분석이 없습니다: "
                                                + type
                                )
                );
    }

    /*
     * =================================================
     * 官殺
     * =================================================
     */

    public boolean sevenKillingsFullyBound() {

        return bindingOf(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        )
                .fullyBound();
    }

    public boolean directOfficerFullyBound() {

        return bindingOf(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        )
                .fullyBound();
    }

    public boolean directOfficerRetained() {

        return bindingOf(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        )
                .hasRetainedVisibleNode();
    }

    public boolean sevenKillingsRetained() {

        return bindingOf(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        )
                .hasRetainedVisibleNode();
    }

    /**
     * 官殺並見 상태에서:
     *
     * 七殺은 전부 active 합에 묶이고
     * 正官은 최소 하나가 남음.
     *
     * → 合殺留官
     */
    public boolean sevenKillingsClearedLeavingDirectOfficer() {

        return sevenKillingsFullyBound()
                &&
                directOfficerRetained();
    }

    /**
     * 官殺並見 상태에서:
     *
     * 正官은 모두 묶이고
     * 七殺은 남음.
     *
     * → 合官留殺
     */
    public boolean directOfficerClearedLeavingSevenKillings() {

        return directOfficerFullyBound()
                &&
                sevenKillingsRetained();
    }

    /**
     * 官殺混雜이 active 합으로 정리됐는가.
     */
    public boolean officerKillingsMixtureCleared() {

        return sevenKillingsClearedLeavingDirectOfficer()
                ||
                directOfficerClearedLeavingSevenKillings();
    }

    /*
     * =================================================
     * 財
     * =================================================
     */

    public boolean wealthRetained() {

        return anyTypeHasRetainedNode(
                MonthCommandPatternCandidateType.DIRECT_WEALTH,
                MonthCommandPatternCandidateType.INDIRECT_WEALTH
        );
    }

    public boolean wealthFullyBound() {

        return allPresentTypesFullyBound(
                MonthCommandPatternCandidateType.DIRECT_WEALTH,
                MonthCommandPatternCandidateType.INDIRECT_WEALTH
        );
    }

    /**
     * 七殺이 모두 묶이고
     * 財가 실제 남음.
     *
     * → 存財而合殺의 구조적 기반.
     */
    public boolean sevenKillingsClearedPreservingWealth() {

        return sevenKillingsFullyBound()
                &&
                wealthRetained();
    }

    /*
     * =================================================
     * 印
     * =================================================
     */

    public boolean resourceRetained() {

        return anyTypeHasRetainedNode(
                MonthCommandPatternCandidateType.DIRECT_RESOURCE,
                MonthCommandPatternCandidateType.INDIRECT_RESOURCE
        );
    }

    /**
     * 財가 모두 active 합에 묶이고
     * 印은 남음.
     *
     * → 合財存印의 구조적 기반.
     */
    public boolean wealthClearedPreservingResource() {

        return wealthFullyBound()
                &&
                resourceRetained();
    }

    /*
     * =================================================
     * 食神合殺
     * =================================================
     */

    /**
     * active 비일간 합 중
     * 食神과 七殺이 직접 같은 occurrence에 있는가.
     */
    public boolean hasActiveEatingGodSevenKillingsCombination() {

        return activationAnalysis
                .activations()
                .stream()
                .filter(
                        PatternStemCombinationActivationOccurrence
                                ::usableAsNonDayMasterRemoval
                )
                .anyMatch(
                        activation ->
                                activation.betweenTenGods(
                                        MonthCommandPatternCandidateType
                                                .EATING_GOD,

                                        MonthCommandPatternCandidateType
                                                .SEVEN_KILLINGS
                                )
                );
    }

    /**
     * 食神이 직접 七殺을 합하고,
     * 모든 투출 七殺이 실제 묶이며,
     * 財는 남아 있는 구조.
     *
     * → 食神合殺存財 검토 기반.
     */
    public boolean eatingGodCombinesSevenKillingsPreservingWealth() {

        return hasActiveEatingGodSevenKillingsCombination()
                &&
                sevenKillingsFullyBound()
                &&
                wealthRetained();
    }

    /*
     * =================================================
     * 공통 helper
     * =================================================
     */

    private boolean anyTypeHasRetainedNode(
            MonthCommandPatternCandidateType... types
    ) {

        return Arrays
                .stream(types)
                .map(this::bindingOf)
                .anyMatch(
                        PatternStemTenGodBindingAnalysis
                                ::hasRetainedVisibleNode
                );
    }

    /**
     * 전달된 여러 십성 중
     * 실제 투출된 타입들만 대상으로 한다.
     *
     * 예:
     *
     * 正財 없음
     * 偏財 하나 있고 FULLY_BOUND
     *
     * → wealthFullyBound = true
     *
     * 반대로 財 자체가 하나도 없으면 false.
     */
    private boolean allPresentTypesFullyBound(
            MonthCommandPatternCandidateType... types
    ) {

        List<PatternStemTenGodBindingAnalysis> present =
                Arrays
                        .stream(types)
                        .map(this::bindingOf)
                        .filter(
                                binding ->
                                        !binding.absent()
                        )
                        .toList();

        if (present.isEmpty()) {

            return false;
        }

        return present
                .stream()
                .allMatch(
                        PatternStemTenGodBindingAnalysis
                                ::fullyBound
                );
    }
}