package com.dajeom.model;

import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TenGodFamilyChainType;
import com.dajeom.type.TenGodFamilyInteractionType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 10차-7.
 *
 * 십성 Family 방향성 및 연쇄구조 최종 분석.
 *
 * =================================================
 *
 * 입력 기반:
 *
 * 10-1
 * TenGodDistributionAnalysis
 *
 * 10-2
 * CompanionCompositeAnalysis
 *
 * 10-3
 * OutputCompositeAnalysis
 *
 * 10-4
 * WealthCompositeAnalysis
 *
 * 10-5
 * OfficerCompositeAnalysis
 *
 * 10-6
 * ResourceCompositeAnalysis
 *
 * =================================================
 *
 * outputs:
 *
 * 1.
 * Family 간 생·극 Interaction
 *
 * 2.
 * 식상→재성→관살→인성
 * Generation Chain
 */
public record TenGodFamilyChainAnalysis(

        HeavenlyStem dayMaster,

        TenGodDistributionAnalysis tenGodDistributionAnalysis,

        CompanionCompositeAnalysis companionCompositeAnalysis,

        OutputCompositeAnalysis outputCompositeAnalysis,

        WealthCompositeAnalysis wealthCompositeAnalysis,

        OfficerCompositeAnalysis officerCompositeAnalysis,

        ResourceCompositeAnalysis resourceCompositeAnalysis,

        List<TenGodFamilyInteractionEvidence> interactions,

        List<TenGodFamilyChainEvidence> chains,

        boolean completeChart

) {

    public TenGodFamilyChainAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                tenGodDistributionAnalysis,
                "10-1 십성 분포 분석이 필요합니다."
        );

        Objects.requireNonNull(
                companionCompositeAnalysis,
                "10-2 비겁 복합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                outputCompositeAnalysis,
                "10-3 식상 복합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                wealthCompositeAnalysis,
                "10-4 재성 복합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                officerCompositeAnalysis,
                "10-5 관살 복합 분석이 필요합니다."
        );

        Objects.requireNonNull(
                resourceCompositeAnalysis,
                "10-6 인성 복합 분석이 필요합니다."
        );

        interactions =
                interactions == null
                        ? List.of()
                        : List.copyOf(
                        interactions
                );

        chains =
                chains == null
                        ? List.of()
                        : List.copyOf(
                        chains
                );

        /*
         * =================================================
         * 일간 동일성
         * =================================================
         */

        if (
                tenGodDistributionAnalysis.dayMaster()
                        != dayMaster

                        || companionCompositeAnalysis.dayMaster()
                        != dayMaster

                        || outputCompositeAnalysis.dayMaster()
                        != dayMaster

                        || wealthCompositeAnalysis.dayMaster()
                        != dayMaster

                        || officerCompositeAnalysis.dayMaster()
                        != dayMaster

                        || resourceCompositeAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "10-1~10-7 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 동일 10-1 분석 참조
         * =================================================
         */

        if (
                companionCompositeAnalysis
                        .tenGodDistributionAnalysis()
                        != tenGodDistributionAnalysis

                        || outputCompositeAnalysis
                        .tenGodDistributionAnalysis()
                        != tenGodDistributionAnalysis

                        || wealthCompositeAnalysis
                        .tenGodDistributionAnalysis()
                        != tenGodDistributionAnalysis

                        || officerCompositeAnalysis
                        .tenGodDistributionAnalysis()
                        != tenGodDistributionAnalysis

                        || resourceCompositeAnalysis
                        .tenGodDistributionAnalysis()
                        != tenGodDistributionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "10-2~10-6이 서로 다른 10-1 분석 결과를 참조하고 있습니다."
            );
        }

        /*
         * =================================================
         * 원국 완전성
         * =================================================
         */

        if (
                tenGodDistributionAnalysis.completeChart()
                        != completeChart

                        || companionCompositeAnalysis.completeChart()
                        != completeChart

                        || outputCompositeAnalysis.completeChart()
                        != completeChart

                        || wealthCompositeAnalysis.completeChart()
                        != completeChart

                        || officerCompositeAnalysis.completeChart()
                        != completeChart

                        || resourceCompositeAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "10-1~10-7 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }

        /*
         * 중복 Interaction 방지.
         */
        for (
                int first = 0;
                first < interactions.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < interactions.size();
                    second++
            ) {

                TenGodFamilyInteractionEvidence a =
                        interactions.get(first);

                TenGodFamilyInteractionEvidence b =
                        interactions.get(second);

                if (
                        a.sourceRole() == b.sourceRole()
                                && a.targetRole() == b.targetRole()
                                && a.interactionType()
                                == b.interactionType()
                ) {

                    throw new IllegalArgumentException(
                            "동일 Family Interaction이 중복되었습니다."
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * Interaction 조회
     * =================================================
     */

    public Optional<TenGodFamilyInteractionEvidence>
    interaction(

            DayMasterElementRole source,

            DayMasterElementRole target,

            TenGodFamilyInteractionType type

    ) {

        if (
                source == null
                        || target == null
                        || type == null
        ) {

            return Optional.empty();
        }

        return interactions
                .stream()
                .filter(
                        interaction ->
                                interaction.matches(
                                        source,
                                        target,
                                        type
                                )
                )
                .findFirst();
    }

    public boolean hasInteraction(

            DayMasterElementRole source,

            DayMasterElementRole target,

            TenGodFamilyInteractionType type

    ) {

        return interaction(
                source,
                target,
                type
        )
                .isPresent();
    }

    public boolean hasMeaningfulInteraction(

            DayMasterElementRole source,

            DayMasterElementRole target,

            TenGodFamilyInteractionType type

    ) {

        return interaction(
                source,
                target,
                type
        )
                .map(
                        TenGodFamilyInteractionEvidence
                                ::meaningful
                )
                .orElse(false);
    }

    public boolean hasStrongInteraction(

            DayMasterElementRole source,

            DayMasterElementRole target,

            TenGodFamilyInteractionType type

    ) {

        return interaction(
                source,
                target,
                type
        )
                .map(
                        TenGodFamilyInteractionEvidence
                                ::strong
                )
                .orElse(false);
    }

    public List<TenGodFamilyInteractionEvidence>
    generationInteractions() {

        return interactions
                .stream()
                .filter(
                        TenGodFamilyInteractionEvidence
                                ::generation
                )
                .toList();
    }

    public List<TenGodFamilyInteractionEvidence>
    controlInteractions() {

        return interactions
                .stream()
                .filter(
                        TenGodFamilyInteractionEvidence
                                ::control
                )
                .toList();
    }

    public List<TenGodFamilyInteractionEvidence>
    meaningfulInteractions() {

        return interactions
                .stream()
                .filter(
                        TenGodFamilyInteractionEvidence
                                ::meaningful
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        (
                                                TenGodFamilyInteractionEvidence evidence
                                        ) ->
                                                evidence.level()
                                                        .rank()
                                )
                                .reversed()
                )
                .toList();
    }

    /*
     * =================================================
     * 주요 생 관계
     * =================================================
     */

    public boolean outputGeneratesWealth() {

        return hasMeaningfulInteraction(
                DayMasterElementRole.OUTPUT,
                DayMasterElementRole.WEALTH,
                TenGodFamilyInteractionType.GENERATION
        );
    }

    public boolean wealthGeneratesOfficer() {

        return hasMeaningfulInteraction(
                DayMasterElementRole.WEALTH,
                DayMasterElementRole.OFFICER,
                TenGodFamilyInteractionType.GENERATION
        );
    }

    public boolean officerGeneratesResource() {

        return hasMeaningfulInteraction(
                DayMasterElementRole.OFFICER,
                DayMasterElementRole.RESOURCE,
                TenGodFamilyInteractionType.GENERATION
        );
    }

    /*
     * =================================================
     * 주요 극 관계
     * =================================================
     */

    public boolean companionControlsWealth() {

        return hasMeaningfulInteraction(
                DayMasterElementRole.COMPANION,
                DayMasterElementRole.WEALTH,
                TenGodFamilyInteractionType.CONTROL
        );
    }

    public boolean wealthControlsResource() {

        return hasMeaningfulInteraction(
                DayMasterElementRole.WEALTH,
                DayMasterElementRole.RESOURCE,
                TenGodFamilyInteractionType.CONTROL
        );
    }

    public boolean resourceControlsOutput() {

        return hasMeaningfulInteraction(
                DayMasterElementRole.RESOURCE,
                DayMasterElementRole.OUTPUT,
                TenGodFamilyInteractionType.CONTROL
        );
    }

    public boolean outputControlsOfficer() {

        return hasMeaningfulInteraction(
                DayMasterElementRole.OUTPUT,
                DayMasterElementRole.OFFICER,
                TenGodFamilyInteractionType.CONTROL
        );
    }

    public boolean officerControlsCompanion() {

        return hasMeaningfulInteraction(
                DayMasterElementRole.OFFICER,
                DayMasterElementRole.COMPANION,
                TenGodFamilyInteractionType.CONTROL
        );
    }

    /*
     * =================================================
     * Chain
     * =================================================
     */

    public List<TenGodFamilyChainEvidence> chainsOf(
            TenGodFamilyChainType chainType
    ) {

        if (
                chainType == null
        ) {

            return List.of();
        }

        return chains
                .stream()
                .filter(
                        chain ->
                                chain.chainType()
                                        == chainType
                )
                .toList();
    }

    public Optional<TenGodFamilyChainEvidence> chainOf(
            TenGodFamilyChainType chainType
    ) {

        return chainsOf(
                chainType
        )
                .stream()
                .findFirst();
    }

    public boolean hasChain(
            TenGodFamilyChainType chainType
    ) {

        return chainOf(
                chainType
        )
                .isPresent();
    }

    public boolean hasMeaningfulChain(
            TenGodFamilyChainType chainType
    ) {

        return chainOf(
                chainType
        )
                .map(
                        TenGodFamilyChainEvidence
                                ::meaningful
                )
                .orElse(false);
    }

    public boolean hasStrongChain(
            TenGodFamilyChainType chainType
    ) {

        return chainOf(
                chainType
        )
                .map(
                        TenGodFamilyChainEvidence
                                ::strong
                )
                .orElse(false);
    }

    public List<TenGodFamilyChainEvidence>
    meaningfulChains() {

        return chains
                .stream()
                .filter(
                        TenGodFamilyChainEvidence
                                ::meaningful
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        (
                                                TenGodFamilyChainEvidence evidence
                                        ) ->
                                                evidence.chainLevel()
                                                        .rank()
                                )
                                .reversed()
                )
                .toList();
    }

    public List<TenGodFamilyChainEvidence>
    strongChains() {

        return chains
                .stream()
                .filter(
                        TenGodFamilyChainEvidence
                                ::strong
                )
                .toList();
    }

    public boolean fullOutputWealthOfficerResourceChain() {

        return hasMeaningfulChain(
                TenGodFamilyChainType
                        .OUTPUT_WEALTH_OFFICER_RESOURCE
        );
    }

    public boolean hourUnknown() {

        return !completeChart;
    }
}