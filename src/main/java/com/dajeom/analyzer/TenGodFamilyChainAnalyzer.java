package com.dajeom.analyzer;

import com.dajeom.model.CompanionCompositeAnalysis;
import com.dajeom.model.OfficerCompositeAnalysis;
import com.dajeom.model.OutputCompositeAnalysis;
import com.dajeom.model.ResourceCompositeAnalysis;
import com.dajeom.model.TenGodDistributionAnalysis;
import com.dajeom.model.TenGodFamilyChainAnalysis;
import com.dajeom.model.TenGodFamilyChainEvidence;
import com.dajeom.model.TenGodFamilyInteractionEvidence;
import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.model.WealthCompositeAnalysis;
import com.dajeom.rule.TenGodFamilyChainRule;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TenGodFamilyChainReasonType;
import com.dajeom.type.TenGodFamilyChainType;
import com.dajeom.type.TenGodFamilyInteractionLevel;
import com.dajeom.type.TenGodFamilyInteractionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-7.
 *
 * 십성 Family 방향성 및 연쇄구조 Analyzer.
 *
 * =================================================
 *
 * 입력:
 *
 * 10-1 분포
 * 10-2 비겁
 * 10-3 식상
 * 10-4 재성
 * 10-5 관살
 * 10-6 인성
 *
 * =================================================
 *
 * 생성 관계:
 *
 * 비겁 -> 식상
 * 식상 -> 재성
 * 재성 -> 관살
 * 관살 -> 인성
 * 인성 -> 비겁
 *
 * =================================================
 *
 * 극 관계:
 *
 * 비겁 -> 재성
 * 재성 -> 인성
 * 인성 -> 식상
 * 식상 -> 관살
 * 관살 -> 비겁
 *
 * =================================================
 *
 * Chain:
 *
 * 식상 -> 재성 -> 관살
 *
 * 재성 -> 관살 -> 인성
 *
 * 식상 -> 재성 -> 관살 -> 인성
 *
 * =================================================
 *
 * 세부 고전 Pattern의 최종 판정은
 * 10-8에서 수행한다.
 */
@Component
public class TenGodFamilyChainAnalyzer {

    private final TenGodFamilyChainRule
            tenGodFamilyChainRule;

    public TenGodFamilyChainAnalyzer(
            TenGodFamilyChainRule tenGodFamilyChainRule
    ) {

        this.tenGodFamilyChainRule =
                Objects.requireNonNull(
                        tenGodFamilyChainRule,
                        "TenGodFamilyChainRule이 필요합니다."
                );
    }

    public TenGodFamilyChainAnalysis analyze(

            TenGodDistributionAnalysis
                    tenGodDistributionAnalysis,

            CompanionCompositeAnalysis
                    companionCompositeAnalysis,

            OutputCompositeAnalysis
                    outputCompositeAnalysis,

            WealthCompositeAnalysis
                    wealthCompositeAnalysis,

            OfficerCompositeAnalysis
                    officerCompositeAnalysis,

            ResourceCompositeAnalysis
                    resourceCompositeAnalysis

    ) {

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

        validateIdentity(
                tenGodDistributionAnalysis,
                companionCompositeAnalysis,
                outputCompositeAnalysis,
                wealthCompositeAnalysis,
                officerCompositeAnalysis,
                resourceCompositeAnalysis
        );

        /*
         * =================================================
         * Family Snapshot
         * =================================================
         */

        FamilySnapshot companion =
                snapshot(
                        tenGodDistributionAnalysis,
                        DayMasterElementRole.COMPANION
                );

        FamilySnapshot output =
                snapshot(
                        tenGodDistributionAnalysis,
                        DayMasterElementRole.OUTPUT
                );

        FamilySnapshot wealth =
                snapshot(
                        tenGodDistributionAnalysis,
                        DayMasterElementRole.WEALTH
                );

        FamilySnapshot officer =
                snapshot(
                        tenGodDistributionAnalysis,
                        DayMasterElementRole.OFFICER
                );

        FamilySnapshot resource =
                snapshot(
                        tenGodDistributionAnalysis,
                        DayMasterElementRole.RESOURCE
                );

        List<TenGodFamilyInteractionEvidence> interactions =
                new ArrayList<>();

        /*
         * =================================================
         * 생 관계
         * =================================================
         */

        addInteraction(
                interactions,
                companion,
                output,
                TenGodFamilyInteractionType.GENERATION
        );

        addInteraction(
                interactions,
                output,
                wealth,
                TenGodFamilyInteractionType.GENERATION
        );

        addInteraction(
                interactions,
                wealth,
                officer,
                TenGodFamilyInteractionType.GENERATION
        );

        addInteraction(
                interactions,
                officer,
                resource,
                TenGodFamilyInteractionType.GENERATION
        );

        addInteraction(
                interactions,
                resource,
                companion,
                TenGodFamilyInteractionType.GENERATION
        );

        /*
         * =================================================
         * 극 관계
         * =================================================
         */

        addInteraction(
                interactions,
                companion,
                wealth,
                TenGodFamilyInteractionType.CONTROL
        );

        addInteraction(
                interactions,
                wealth,
                resource,
                TenGodFamilyInteractionType.CONTROL
        );

        addInteraction(
                interactions,
                resource,
                output,
                TenGodFamilyInteractionType.CONTROL
        );

        addInteraction(
                interactions,
                output,
                officer,
                TenGodFamilyInteractionType.CONTROL
        );

        addInteraction(
                interactions,
                officer,
                companion,
                TenGodFamilyInteractionType.CONTROL
        );

        interactions.sort(
                Comparator
                        .comparing(
                                TenGodFamilyInteractionEvidence
                                        ::interactionType
                        )

                        .thenComparing(
                                TenGodFamilyInteractionEvidence
                                        ::sourceRole
                        )

                        .thenComparing(
                                TenGodFamilyInteractionEvidence
                                        ::targetRole
                        )
        );

        /*
         * =================================================
         * Generation Chain
         * =================================================
         */

        List<TenGodFamilyChainEvidence> chains =
                new ArrayList<>();

        for (
                TenGodFamilyChainType chainType
                : TenGodFamilyChainType.values()
        ) {

            TenGodFamilyChainEvidence chain =
                    buildChain(
                            chainType,
                            interactions
                    );

            if (
                    chain != null
            ) {

                chains.add(
                        chain
                );
            }
        }

        chains.sort(
                Comparator
                        .comparingInt(
                                (
                                        TenGodFamilyChainEvidence chain
                                ) ->
                                        chain.chainLevel()
                                                .rank()
                        )
                        .reversed()

                        .thenComparingInt(
                                chain ->
                                        chain
                                                .chainType()
                                                .ordinal()
                        )
        );

        return new TenGodFamilyChainAnalysis(
                tenGodDistributionAnalysis
                        .dayMaster(),
                tenGodDistributionAnalysis,
                companionCompositeAnalysis,
                outputCompositeAnalysis,
                wealthCompositeAnalysis,
                officerCompositeAnalysis,
                resourceCompositeAnalysis,
                interactions,
                chains,
                tenGodDistributionAnalysis
                        .completeChart()
        );
    }

    /*
     * =================================================
     * Interaction 생성
     * =================================================
     */

    private void addInteraction(

            List<TenGodFamilyInteractionEvidence> results,

            FamilySnapshot source,

            FamilySnapshot target,

            TenGodFamilyInteractionType type

    ) {

        if (
                source.power() <= 0
                        || target.power() <= 0
        ) {

            return;
        }

        if (
                type.generation()
                        && !tenGodFamilyChainRule
                        .validGeneration(
                                source.role(),
                                target.role()
                        )
        ) {

            throw new IllegalArgumentException(
                    "유효하지 않은 십성 Family 생 관계입니다: "
                            + source.role()
                            + " -> "
                            + target.role()
            );
        }

        if (
                type.control()
                        && !tenGodFamilyChainRule
                        .validControl(
                                source.role(),
                                target.role()
                        )
        ) {

            throw new IllegalArgumentException(
                    "유효하지 않은 십성 Family 극 관계입니다: "
                            + source.role()
                            + " -> "
                            + target.role()
            );
        }

        TenGodFamilyInteractionLevel level =
                tenGodFamilyChainRule
                        .determineInteractionLevel(
                                source.shareBasisPoints(),
                                target.shareBasisPoints()
                        );

        List<TenGodFamilyChainReasonType> reasons =
                tenGodFamilyChainRule
                        .buildInteractionReasons(
                                type,
                                level,
                                source.power(),
                                target.power(),
                                source.shareBasisPoints(),
                                target.shareBasisPoints(),
                                source.visible(),
                                target.visible(),
                                source.monthPresent(),
                                target.monthPresent()
                        );

        results.add(
                new TenGodFamilyInteractionEvidence(
                        source.role(),
                        target.role(),
                        type,
                        level,
                        source.power(),
                        target.power(),
                        source.shareBasisPoints(),
                        target.shareBasisPoints(),
                        source.visible(),
                        target.visible(),
                        source.monthPresent(),
                        target.monthPresent(),
                        reasons
                )
        );
    }

    /*
     * =================================================
     * Chain 생성
     * =================================================
     */

    private TenGodFamilyChainEvidence buildChain(

            TenGodFamilyChainType chainType,

            List<TenGodFamilyInteractionEvidence> interactions

    ) {

        List<TenGodFamilyInteractionEvidence> chainInteractions =
                new ArrayList<>();

        List<DayMasterElementRole> roles =
                chainType.roles();

        for (
                int index = 0;
                index < roles.size() - 1;
                index++
        ) {

            DayMasterElementRole source =
                    roles.get(index);

            DayMasterElementRole target =
                    roles.get(index + 1);

            TenGodFamilyInteractionEvidence interaction =
                    findInteraction(
                            interactions,
                            source,
                            target,
                            TenGodFamilyInteractionType.GENERATION
                    );

            /*
             * 연쇄 중 하나라도 실제 Family가 없어
             * Interaction 자체가 존재하지 않으면
             * 해당 Chain은 생성하지 않는다.
             */
            if (
                    interaction == null
            ) {

                return null;
            }

            chainInteractions.add(
                    interaction
            );
        }

        TenGodFamilyInteractionLevel chainLevel =
                tenGodFamilyChainRule
                        .determineChainLevel(
                                chainInteractions
                        );

        List<TenGodFamilyChainReasonType> reasons =
                tenGodFamilyChainRule
                        .buildChainReasons(
                                chainInteractions,
                                chainLevel
                        );

        return new TenGodFamilyChainEvidence(
                chainType,
                chainLevel,
                chainInteractions,
                reasons
        );
    }

    private TenGodFamilyInteractionEvidence findInteraction(

            List<TenGodFamilyInteractionEvidence> interactions,

            DayMasterElementRole source,

            DayMasterElementRole target,

            TenGodFamilyInteractionType type

    ) {

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
                .findFirst()
                .orElse(null);
    }

    /*
     * =================================================
     * Family Snapshot
     * =================================================
     */

    private FamilySnapshot snapshot(

            TenGodDistributionAnalysis analysis,

            DayMasterElementRole role

    ) {

        int power =
                familyPower(
                        analysis,
                        role
                );

        int shareBasisPoints =
                calculateShareBasisPoints(
                        power,
                        analysis
                                .totalExternalTenGodPower()
                );

        return new FamilySnapshot(
                role,
                power,
                shareBasisPoints,
                familyVisible(
                        analysis,
                        role
                ),
                familyMonthPresent(
                        analysis,
                        role
                )
        );
    }

    private int familyPower(

            TenGodDistributionAnalysis analysis,

            DayMasterElementRole role

    ) {

        return switch (
                role
                ) {

            case COMPANION ->
                    analysis
                            .companionFamilyPower();

            case OUTPUT ->
                    analysis
                            .outputFamilyPower();

            case WEALTH ->
                    analysis
                            .wealthFamilyPower();

            case OFFICER ->
                    analysis
                            .officerFamilyPower();

            case RESOURCE ->
                    analysis
                            .resourceFamilyPower();
        };
    }

    private boolean familyVisible(

            TenGodDistributionAnalysis analysis,

            DayMasterElementRole role

    ) {

        return switch (
                role
                ) {

            case COMPANION ->
                    visible(
                            analysis,
                            StructureTenGodType.COMPANION,
                            StructureTenGodType.ROB_WEALTH
                    );

            case OUTPUT ->
                    visible(
                            analysis,
                            StructureTenGodType.EATING_GOD,
                            StructureTenGodType.HURTING_OFFICER
                    );

            case WEALTH ->
                    visible(
                            analysis,
                            StructureTenGodType.DIRECT_WEALTH,
                            StructureTenGodType.INDIRECT_WEALTH
                    );

            case OFFICER ->
                    visible(
                            analysis,
                            StructureTenGodType.DIRECT_OFFICER,
                            StructureTenGodType.SEVEN_KILLINGS
                    );

            case RESOURCE ->
                    visible(
                            analysis,
                            StructureTenGodType.DIRECT_RESOURCE,
                            StructureTenGodType.INDIRECT_RESOURCE
                    );
        };
    }

    private boolean familyMonthPresent(

            TenGodDistributionAnalysis analysis,

            DayMasterElementRole role

    ) {

        return switch (
                role
                ) {

            case COMPANION ->
                    monthPresent(
                            analysis,
                            StructureTenGodType.COMPANION,
                            StructureTenGodType.ROB_WEALTH
                    );

            case OUTPUT ->
                    monthPresent(
                            analysis,
                            StructureTenGodType.EATING_GOD,
                            StructureTenGodType.HURTING_OFFICER
                    );

            case WEALTH ->
                    monthPresent(
                            analysis,
                            StructureTenGodType.DIRECT_WEALTH,
                            StructureTenGodType.INDIRECT_WEALTH
                    );

            case OFFICER ->
                    monthPresent(
                            analysis,
                            StructureTenGodType.DIRECT_OFFICER,
                            StructureTenGodType.SEVEN_KILLINGS
                    );

            case RESOURCE ->
                    monthPresent(
                            analysis,
                            StructureTenGodType.DIRECT_RESOURCE,
                            StructureTenGodType.INDIRECT_RESOURCE
                    );
        };
    }

    private boolean visible(

            TenGodDistributionAnalysis analysis,

            StructureTenGodType first,

            StructureTenGodType second

    ) {

        return summary(
                analysis,
                first
        )
                .visible()

                || summary(
                analysis,
                second
        )
                .visible();
    }

    private boolean monthPresent(

            TenGodDistributionAnalysis analysis,

            StructureTenGodType first,

            StructureTenGodType second

    ) {

        return summary(
                analysis,
                first
        )
                .monthPresent()

                || summary(
                analysis,
                second
        )
                .monthPresent();
    }

    private TenGodPowerSummary summary(

            TenGodDistributionAnalysis analysis,

            StructureTenGodType type

    ) {

        return analysis
                .summaryOf(
                        type
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "10-1 분석에 십성 Summary가 없습니다: "
                                                + type
                                )
                );
    }

    private int calculateShareBasisPoints(

            int familyPower,

            int totalExternalPower

    ) {

        if (
                familyPower <= 0
                        || totalExternalPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                10_000,
                (int) Math.round(
                        familyPower
                                * 10_000.0
                                / totalExternalPower
                )
        );
    }

    /*
     * =================================================
     * 동일 원국 검증
     * =================================================
     */

    private void validateIdentity(

            TenGodDistributionAnalysis distribution,

            CompanionCompositeAnalysis companion,

            OutputCompositeAnalysis output,

            WealthCompositeAnalysis wealth,

            OfficerCompositeAnalysis officer,

            ResourceCompositeAnalysis resource

    ) {

        if (
                companion.tenGodDistributionAnalysis()
                        != distribution

                        || output.tenGodDistributionAnalysis()
                        != distribution

                        || wealth.tenGodDistributionAnalysis()
                        != distribution

                        || officer.tenGodDistributionAnalysis()
                        != distribution

                        || resource.tenGodDistributionAnalysis()
                        != distribution
        ) {

            throw new IllegalArgumentException(
                    "10-2~10-6 분석이 동일한 10-1 TenGodDistributionAnalysis를 참조해야 합니다."
            );
        }

        if (
                companion.dayMaster()
                        != distribution.dayMaster()

                        || output.dayMaster()
                        != distribution.dayMaster()

                        || wealth.dayMaster()
                        != distribution.dayMaster()

                        || officer.dayMaster()
                        != distribution.dayMaster()

                        || resource.dayMaster()
                        != distribution.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "10-1~10-6 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                companion.completeChart()
                        != distribution.completeChart()

                        || output.completeChart()
                        != distribution.completeChart()

                        || wealth.completeChart()
                        != distribution.completeChart()

                        || officer.completeChart()
                        != distribution.completeChart()

                        || resource.completeChart()
                        != distribution.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "10-1~10-6 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }

    /*
     * =================================================
     * 내부 Family Snapshot
     * =================================================
     */

    private record FamilySnapshot(

            DayMasterElementRole role,

            int power,

            int shareBasisPoints,

            boolean visible,

            boolean monthPresent

    ) {

        private FamilySnapshot {

            Objects.requireNonNull(
                    role,
                    "Family role이 필요합니다."
            );

            if (
                    power < 0
            ) {

                throw new IllegalArgumentException(
                        "Family power는 음수일 수 없습니다."
                );
            }

            if (
                    shareBasisPoints < 0
                            || shareBasisPoints > 10_000
            ) {

                throw new IllegalArgumentException(
                        "Family share는 0~10000bp 범위여야 합니다."
                );
            }
        }
    }
}