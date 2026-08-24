package com.dajeom.model;

import com.dajeom.type.PersonalityStatementBundleObligation;
import com.dajeom.type.PersonalityStatementContractGuardrail;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 기본성향 문장생성을 위한
 * 최종 Java Contract.
 *
 * =================================================
 * 현재 공식 우선순위
 * =================================================
 *
 * 1.
 * exactTenGodFacts
 *
 * 정확한 10십성 자체의
 * 원국 상태.
 *
 * =================================================
 *
 * 2.
 * bundleContracts
 *
 * 실제 전통 Pattern 및
 * 기존 호환 Finding 구조.
 *
 * =================================================
 *
 * 3.
 * globalBackgroundContexts
 *
 * 직접 성격판정으로 확대하면 안 되는
 * 전역 Context.
 *
 * =================================================
 *
 * 중요한 원칙
 * =================================================
 *
 * 기존 5개 기능축 Bundle이 존재하더라도:
 *
 * 정재 + 편재
 * -> 재성이라는 새로운 성격
 *
 * 정관 + 편관
 * -> 관살이라는 새로운 성격
 *
 * 식신 + 상관
 * -> 식상이라는 새로운 성격
 *
 * 식으로 정확한 10십성을
 * 대체해서는 안 된다.
 *
 * 정확한 10십성은
 * exactTenGodFacts에 독립적으로 보존된다.
 */
public record NatalPersonalityStatementContract(

        NatalPersonalityStatementBundleAnalysis
        bundleAnalysis,

        Optional<NatalPersonalityExactTenGodSelection>
        exactTenGodSelection,

        List<NatalPersonalityExactTenGodFact>
        exactTenGodFacts,

        List<NatalPersonalityStatementBundleContract>
        bundleContracts,

        List<NatalPersonalityStatementSelection>
        globalBackgroundContexts,

        List<PersonalityStatementContractGuardrail>
        guardrails,

        boolean completeChart

) {

    /**
     * =================================================
     * 기존 ContractEngine 호환 생성자
     * =================================================
     *
     * ContractEngine API를 당장 깨뜨리지 않는다.
     *
     * bundleAnalysis
     * ->
     * selectionAnalysis
     * ->
     * statementFactAnalysis
     *
     * 에 이미 보존된 정확한 10십성 Contract를
     * 자동으로 승계한다.
     */
    public NatalPersonalityStatementContract(

            NatalPersonalityStatementBundleAnalysis
                    bundleAnalysis,

            List<NatalPersonalityStatementBundleContract>
                    bundleContracts,

            List<NatalPersonalityStatementSelection>
                    globalBackgroundContexts,

            List<PersonalityStatementContractGuardrail>
                    guardrails,

            boolean completeChart

    ) {

        this(

                bundleAnalysis,

                requireStatementFactAnalysis(
                        bundleAnalysis
                )
                        .exactTenGodSelection(),

                requireStatementFactAnalysis(
                        bundleAnalysis
                )
                        .exactTenGodFacts(),

                bundleContracts,

                globalBackgroundContexts,

                guardrails,

                completeChart
        );
    }

    public NatalPersonalityStatementContract {

        Objects.requireNonNull(
                bundleAnalysis,
                "Personality Statement Bundle Analysis는 필수입니다."
        );

        exactTenGodSelection =
                exactTenGodSelection == null

                        ? Optional.empty()

                        : exactTenGodSelection;

        exactTenGodFacts =
                exactTenGodFacts == null

                        ? List.of()

                        : List.copyOf(
                        exactTenGodFacts
                );

        bundleContracts =
                bundleContracts == null

                        ? List.of()

                        : List.copyOf(
                        bundleContracts
                );

        globalBackgroundContexts =
                globalBackgroundContexts == null

                        ? List.of()

                        : List.copyOf(
                        globalBackgroundContexts
                );

        guardrails =
                guardrails == null

                        ? List.of()

                        : List.copyOf(
                        guardrails
                );

        /*
         * =================================================
         * 원국 완전성
         * =================================================
         */

        if (
                bundleAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Bundle Analysis와 Statement Contract의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Exact TenGod Contract 검증
         * =================================================
         */

        validateExactTenGodContract(

                bundleAnalysis,

                exactTenGodSelection,

                exactTenGodFacts,

                completeChart
        );

        /*
         * =================================================
         * 모든 Bundle은
         * 정확히 하나의 Contract
         * =================================================
         */

        if (
                bundleContracts.size()
                        != bundleAnalysis
                        .bundles()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "모든 Personality Bundle은 "
                            + "정확히 하나의 Contract를 가져야 합니다."
            );
        }

        Set<NatalPersonalityStatementBundle> actualBundles =
                new HashSet<>();

        for (
                NatalPersonalityStatementBundleContract contract
                : bundleContracts
        ) {

            Objects.requireNonNull(
                    contract,
                    "Personality Bundle Contract는 null일 수 없습니다."
            );

            if (
                    contract.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Bundle Contract의 "
                                + "원국 완전성 상태가 일치하지 않습니다."
                );
            }

            if (
                    !actualBundles.add(
                            contract.sourceBundle()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Personality Bundle에 "
                                + "Contract가 중복되었습니다."
                );
            }
        }

        Set<NatalPersonalityStatementBundle> expectedBundles =
                new HashSet<>(
                        bundleAnalysis.bundles()
                );

        if (
                !actualBundles.equals(
                        expectedBundles
                )
        ) {

            throw new IllegalArgumentException(
                    "Statement Contract가 "
                            + "Bundle Analysis의 전체 Bundle을 "
                            + "정확히 보존하지 않습니다."
            );
        }

        /*
         * =================================================
         * Global Background Context
         * =================================================
         */

        Set<NatalPersonalityStatementSelection> actualContexts =
                new HashSet<>(
                        globalBackgroundContexts
                );

        Set<NatalPersonalityStatementSelection> expectedContexts =
                new HashSet<>(
                        bundleAnalysis
                                .globalBackgroundContexts()
                );

        if (
                !actualContexts.equals(
                        expectedContexts
                )
        ) {

            throw new IllegalArgumentException(
                    "Statement Contract의 "
                            + "Global Background Context가 "
                            + "Bundle Analysis와 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Guardrail
         * =================================================
         */

        Set<PersonalityStatementContractGuardrail> guardrailSet =
                EnumSet.noneOf(
                        PersonalityStatementContractGuardrail.class
                );

        for (
                PersonalityStatementContractGuardrail guardrail
                : guardrails
        ) {

            Objects.requireNonNull(
                    guardrail,
                    "Statement Contract Guardrail에는 null을 사용할 수 없습니다."
            );

            if (
                    !guardrailSet.add(
                            guardrail
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Statement Guardrail이 중복되었습니다: "
                                + guardrail
                );
            }
        }

        Set<PersonalityStatementContractGuardrail> requiredGuardrails =
                EnumSet.allOf(
                        PersonalityStatementContractGuardrail.class
                );

        if (
                !guardrailSet.equals(
                        requiredGuardrails
                )
        ) {

            throw new IllegalArgumentException(
                    "Personality Statement Contract에는 "
                            + "정의된 모든 Guardrail이 필요합니다."
            );
        }
    }

    /*
     * =================================================
     * Exact TenGod
     * =================================================
     */

    public boolean hasExactTenGodContract() {

        return exactTenGodSelection
                .isPresent()

                &&
                !exactTenGodFacts
                        .isEmpty();
    }

    public List<NatalPersonalityExactTenGodFact>
    primaryExactTenGodFacts() {

        return exactTenGodFacts
                .stream()
                .filter(
                        NatalPersonalityExactTenGodFact
                                ::primary
                )
                .toList();
    }

    public List<NatalPersonalityExactTenGodFact>
    supportingExactTenGodFacts() {

        return exactTenGodFacts
                .stream()
                .filter(
                        NatalPersonalityExactTenGodFact
                                ::supporting
                )
                .toList();
    }

    public List<NatalPersonalityExactTenGodFact>
    referenceExactTenGodFacts() {

        return exactTenGodFacts
                .stream()
                .filter(
                        NatalPersonalityExactTenGodFact
                                ::internalReference
                )
                .toList();
    }

    /*
     * =================================================
     * Legacy Pattern Bundle
     * =================================================
     */

    public List<NatalPersonalityStatementBundleContract>
    requiredBundleContracts() {

        return bundleContracts
                .stream()
                .filter(
                        NatalPersonalityStatementBundleContract
                                ::required
                )
                .toList();
    }

    public List<NatalPersonalityStatementBundleContract>
    optionalSupportBundleContracts() {

        return bundleContracts
                .stream()
                .filter(
                        contract ->
                                contract.obligation()
                                        == PersonalityStatementBundleObligation
                                        .OPTIONAL_SUPPORT
                )
                .toList();
    }

    public List<NatalPersonalityStatementBundleContract>
    qualifiedBundleContracts() {

        return bundleContracts
                .stream()
                .filter(
                        NatalPersonalityStatementBundleContract
                                ::qualificationRequired
                )
                .toList();
    }

    public List<NatalPersonalityStatementBundleContract>
    contractsWithStrongSelections() {

        return bundleContracts
                .stream()
                .filter(
                        NatalPersonalityStatementBundleContract
                                ::hasStrongSelection
                )
                .toList();
    }

    /*
     * =================================================
     * Exact TenGod Validation
     * =================================================
     */

    private static void validateExactTenGodContract(

            NatalPersonalityStatementBundleAnalysis bundleAnalysis,

            Optional<NatalPersonalityExactTenGodSelection>
                    exactTenGodSelection,

            List<NatalPersonalityExactTenGodFact>
                    exactTenGodFacts,

            boolean completeChart

    ) {

        NatalPersonalityStatementFactAnalysis sourceAnalysis =
                requireStatementFactAnalysis(
                        bundleAnalysis
                );

        if (
                sourceAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Statement Fact Analysis와 최종 Contract의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        Optional<NatalPersonalityExactTenGodSelection>
                expectedSelection =
                sourceAnalysis
                        .exactTenGodSelection();

        /*
         * =================================================
         * Selection 존재 여부
         * =================================================
         */

        if (
                expectedSelection.isPresent()
                        != exactTenGodSelection.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "Statement Fact Analysis와 최종 Contract의 "
                            + "Exact TenGod Selection 존재 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Selection Identity
         * =================================================
         */

        if (
                expectedSelection.isPresent()

                        &&
                        expectedSelection
                                .orElseThrow()
                                != exactTenGodSelection
                                .orElseThrow()
        ) {

            throw new IllegalArgumentException(
                    "최종 Contract가 공식 "
                            + "Exact TenGod Selection Identity를 "
                            + "보존하지 않았습니다."
            );
        }

        List<NatalPersonalityExactTenGodFact> expectedFacts =
                sourceAnalysis
                        .exactTenGodFacts();

        /*
         * =================================================
         * Fact 수
         * =================================================
         */

        if (
                expectedFacts.size()
                        != exactTenGodFacts.size()
        ) {

            throw new IllegalArgumentException(
                    "Statement Fact Analysis와 최종 Contract의 "
                            + "Exact TenGod Fact 수가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Fact Identity + 순서
         * =================================================
         */

        for (
                int index = 0;
                index < expectedFacts.size();
                index++
        ) {

            if (
                    expectedFacts.get(
                            index
                    )
                            != exactTenGodFacts.get(
                            index
                    )
            ) {

                throw new IllegalArgumentException(
                        "최종 Contract가 공식 "
                                + "Exact TenGod Fact Identity와 순서를 "
                                + "보존하지 않았습니다. index="
                                + index
                );
            }
        }
    }

    private static NatalPersonalityStatementFactAnalysis
    requireStatementFactAnalysis(

            NatalPersonalityStatementBundleAnalysis bundleAnalysis

    ) {

        Objects.requireNonNull(
                bundleAnalysis,
                "Personality Statement Bundle Analysis는 필수입니다."
        );

        return bundleAnalysis
                .selectionAnalysis()
                .statementFactAnalysis();
    }
}