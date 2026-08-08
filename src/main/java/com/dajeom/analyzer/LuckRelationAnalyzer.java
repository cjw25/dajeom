package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.FourPillars;
import com.dajeom.model.LuckBranchInteraction;
import com.dajeom.model.LuckPillarSource;
import com.dajeom.model.LuckStemInteraction;
import com.dajeom.rule.LuckInteractionRule;
import com.dajeom.type.BranchRelationType;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.LuckSourceType;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.StemRelationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 / 대운 / 세운 사이의
 * 천간 및 지지 관계를 분석한다.
 *
 * 원국끼리의 기존 관계는
 * RelationAnalyzer가 이미 분석하므로
 * 여기서는 적어도 하나의 운이
 * 포함된 관계만 생성한다.
 */
@Component
public class LuckRelationAnalyzer {

    public List<LuckStemInteraction>
    analyzeStemInteractions(
            FourPillars fourPillars,
            DecadeLuckPeriod decadeLuckPeriod,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        List<LuckPillarSource> sources =
                createSources(
                        fourPillars,
                        decadeLuckPeriod,
                        annualLuckAnalysis
                );

        List<LuckStemInteraction> result =
                new ArrayList<>();

        for (
                int firstIndex = 0;
                firstIndex < sources.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < sources.size();
                    secondIndex++
            ) {

                LuckPillarSource first =
                        sources.get(
                                firstIndex
                        );

                LuckPillarSource second =
                        sources.get(
                                secondIndex
                        );

                if (
                        !LuckInteractionRule
                                .isRelevantPair(
                                        first,
                                        second
                                )
                ) {

                    continue;
                }

                Optional<FiveElement>
                        combinationElement =
                        StemRelationTable
                                .findCombinationElement(
                                        first
                                                .pillar()
                                                .stem(),
                                        second
                                                .pillar()
                                                .stem()
                                );

                combinationElement.ifPresent(
                        element ->
                                result.add(
                                        new LuckStemInteraction(
                                                StemRelationType
                                                        .COMBINATION,
                                                first,
                                                second,
                                                element
                                        )
                                )
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    public List<LuckBranchInteraction>
    analyzeBranchInteractions(
            FourPillars fourPillars,
            DecadeLuckPeriod decadeLuckPeriod,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        List<LuckPillarSource> sources =
                createSources(
                        fourPillars,
                        decadeLuckPeriod,
                        annualLuckAnalysis
                );

        List<LuckBranchInteraction> result =
                new ArrayList<>();

        analyzePairs(
                sources,
                result
        );

        analyzeTriples(
                sources,
                result
        );

        return List.copyOf(
                result
        );
    }

    private void analyzePairs(
            List<LuckPillarSource> sources,
            List<LuckBranchInteraction> result
    ) {

        for (
                int firstIndex = 0;
                firstIndex < sources.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < sources.size();
                    secondIndex++
            ) {

                LuckPillarSource first =
                        sources.get(
                                firstIndex
                        );

                LuckPillarSource second =
                        sources.get(
                                secondIndex
                        );

                if (
                        !LuckInteractionRule
                                .isRelevantPair(
                                        first,
                                        second
                                )
                ) {

                    continue;
                }

                EarthlyBranch firstBranch =
                        first
                                .pillar()
                                .branch();

                EarthlyBranch secondBranch =
                        second
                                .pillar()
                                .branch();

                Optional<FiveElement>
                        sixCombination =
                        BranchRelationTable
                                .findSixCombinationElement(
                                        firstBranch,
                                        secondBranch
                                );

                sixCombination.ifPresent(
                        element ->
                                result.add(
                                        createPair(
                                                BranchRelationType
                                                        .SIX_COMBINATION,
                                                first,
                                                second,
                                                element
                                        )
                                )
                );

                if (
                        BranchRelationTable
                                .isClash(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType.CLASH,
                                    first,
                                    second,
                                    null
                            )
                    );
                }

                if (
                        BranchRelationTable
                                .isPairPunishment(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType.PUNISHMENT,
                                    first,
                                    second,
                                    null
                            )
                    );
                }

                if (
                        firstBranch == secondBranch
                                && BranchRelationTable
                                .isSelfPunishmentBranch(
                                        firstBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType
                                            .SELF_PUNISHMENT,
                                    first,
                                    second,
                                    null
                            )
                    );
                }

                if (
                        BranchRelationTable
                                .isHarm(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType.HARM,
                                    first,
                                    second,
                                    null
                            )
                    );
                }

                if (
                        BranchRelationTable
                                .isBreak(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType.BREAK,
                                    first,
                                    second,
                                    null
                            )
                    );
                }
            }
        }
    }

    private void analyzeTriples(
            List<LuckPillarSource> sources,
            List<LuckBranchInteraction> result
    ) {

        if (sources.size() < 3) {

            return;
        }

        for (
                int firstIndex = 0;
                firstIndex < sources.size() - 2;
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < sources.size() - 1;
                    secondIndex++
            ) {

                for (
                        int thirdIndex =
                        secondIndex + 1;
                        thirdIndex < sources.size();
                        thirdIndex++
                ) {

                    LuckPillarSource first =
                            sources.get(
                                    firstIndex
                            );

                    LuckPillarSource second =
                            sources.get(
                                    secondIndex
                            );

                    LuckPillarSource third =
                            sources.get(
                                    thirdIndex
                            );

                    List<LuckPillarSource> group =
                            List.of(
                                    first,
                                    second,
                                    third
                            );

                    if (
                            !LuckInteractionRule
                                    .isRelevantGroup(
                                            group
                                    )
                    ) {

                        continue;
                    }

                    EarthlyBranch firstBranch =
                            first.pillar().branch();

                    EarthlyBranch secondBranch =
                            second.pillar().branch();

                    EarthlyBranch thirdBranch =
                            third.pillar().branch();

                    Optional<FiveElement>
                            threeHarmony =
                            BranchRelationTable
                                    .findThreeHarmonyElement(
                                            firstBranch,
                                            secondBranch,
                                            thirdBranch
                                    );

                    threeHarmony.ifPresent(
                            element ->
                                    result.add(
                                            new LuckBranchInteraction(
                                                    BranchRelationType
                                                            .THREE_HARMONY,
                                                    group,
                                                    element
                                            )
                                    )
                    );

                    if (
                            BranchRelationTable
                                    .isThreePunishment(
                                            firstBranch,
                                            secondBranch,
                                            thirdBranch
                                    )
                    ) {

                        result.add(
                                new LuckBranchInteraction(
                                        BranchRelationType
                                                .PUNISHMENT,
                                        group,
                                        null
                                )
                        );
                    }
                }
            }
        }
    }

    private LuckBranchInteraction createPair(
            BranchRelationType type,
            LuckPillarSource first,
            LuckPillarSource second,
            FiveElement combinationElement
    ) {

        return new LuckBranchInteraction(
                type,
                List.of(
                        first,
                        second
                ),
                combinationElement
        );
    }

    private List<LuckPillarSource>
    createSources(
            FourPillars fourPillars,
            DecadeLuckPeriod decadeLuckPeriod,
            AnnualLuckAnalysis annualLuckAnalysis
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        Objects.requireNonNull(
                annualLuckAnalysis,
                "세운 분석 결과는 필수입니다."
        );

        List<LuckPillarSource> result =
                new ArrayList<>();

        result.add(
                new LuckPillarSource(
                        LuckSourceType.NATAL,
                        PillarPosition.YEAR,
                        null,
                        fourPillars.yearPillar()
                )
        );

        result.add(
                new LuckPillarSource(
                        LuckSourceType.NATAL,
                        PillarPosition.MONTH,
                        null,
                        fourPillars.monthPillar()
                )
        );

        result.add(
                new LuckPillarSource(
                        LuckSourceType.NATAL,
                        PillarPosition.DAY,
                        null,
                        fourPillars.dayPillar()
                )
        );

        if (fourPillars.hasHourPillar()) {

            result.add(
                    new LuckPillarSource(
                            LuckSourceType.NATAL,
                            PillarPosition.HOUR,
                            null,
                            fourPillars.hourPillar()
                    )
            );
        }

        if (decadeLuckPeriod != null) {

            result.add(
                    new LuckPillarSource(
                            LuckSourceType.DECADE_LUCK,
                            null,
                            decadeLuckPeriod.order(),
                            decadeLuckPeriod.pillar()
                    )
            );
        }

        result.add(
                new LuckPillarSource(
                        LuckSourceType.ANNUAL_LUCK,
                        null,
                        annualLuckAnalysis.targetYear(),
                        annualLuckAnalysis.pillar()
                )
        );

        return List.copyOf(
                result
        );
    }
}