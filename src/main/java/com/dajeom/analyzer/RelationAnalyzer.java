package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.BranchRelation;
import com.dajeom.model.FourPillars;
import com.dajeom.model.Pillar;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.model.StemRelation;
import com.dajeom.type.BranchRelationType;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.StemRelationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국의 천간/지지 관계를 분석한다.
 *
 * V1 분석:
 *
 * 천간:
 * - 천간 오합
 *
 * 지지:
 * - 육합
 * - 삼합
 * - 충
 * - 형
 * - 자형
 * - 해
 * - 파
 *
 * 이 Analyzer에서는
 * 관계의 존재 여부만 판정한다.
 *
 * 길흉, 합화 성립, 관계의 강도 등은
 * 이후 별도 분석에서 판단한다.
 */
@Component
public class RelationAnalyzer {

    public RelationAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        List<PositionedPillar> pillars =
                createPositionedPillars(
                        fourPillars
                );

        List<StemRelation> stemRelations =
                analyzeStemRelations(
                        pillars
                );

        List<BranchRelation> branchRelations =
                analyzeBranchRelations(
                        pillars
                );

        return new RelationAnalysis(
                stemRelations,
                branchRelations
        );
    }

    /**
     * 시주가 없는 경우
     * 년/월/일만 목록에 포함한다.
     */
    private List<PositionedPillar>
    createPositionedPillars(
            FourPillars fourPillars
    ) {

        List<PositionedPillar> result =
                new ArrayList<>();

        result.add(
                new PositionedPillar(
                        PillarPosition.YEAR,
                        fourPillars.yearPillar()
                )
        );

        result.add(
                new PositionedPillar(
                        PillarPosition.MONTH,
                        fourPillars.monthPillar()
                )
        );

        result.add(
                new PositionedPillar(
                        PillarPosition.DAY,
                        fourPillars.dayPillar()
                )
        );

        if (
                fourPillars.hasHourPillar()
        ) {

            result.add(
                    new PositionedPillar(
                            PillarPosition.HOUR,
                            fourPillars.hourPillar()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 모든 천간 쌍을 비교한다.
     */
    private List<StemRelation>
    analyzeStemRelations(
            List<PositionedPillar> pillars
    ) {

        List<StemRelation> result =
                new ArrayList<>();

        for (
                int firstIndex = 0;
                firstIndex < pillars.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < pillars.size();
                    secondIndex++
            ) {

                PositionedPillar first =
                        pillars.get(
                                firstIndex
                        );

                PositionedPillar second =
                        pillars.get(
                                secondIndex
                        );

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
                                        new StemRelation(
                                                StemRelationType
                                                        .COMBINATION,
                                                first.position(),
                                                second.position(),
                                                first
                                                        .pillar()
                                                        .stem(),
                                                second
                                                        .pillar()
                                                        .stem(),
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

    /**
     * 지지 관계 전체 분석.
     */
    private List<BranchRelation>
    analyzeBranchRelations(
            List<PositionedPillar> pillars
    ) {

        List<BranchRelation> result =
                new ArrayList<>();

        /*
         * 2개 지지 관계.
         */
        analyzeBranchPairs(
                pillars,
                result
        );

        /*
         * 3개 지지 관계.
         */
        analyzeBranchTriples(
                pillars,
                result
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 육합 / 충 / 형 / 자형 / 해 / 파.
     */
    private void analyzeBranchPairs(
            List<PositionedPillar> pillars,
            List<BranchRelation> result
    ) {

        for (
                int firstIndex = 0;
                firstIndex < pillars.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < pillars.size();
                    secondIndex++
            ) {

                PositionedPillar first =
                        pillars.get(
                                firstIndex
                        );

                PositionedPillar second =
                        pillars.get(
                                secondIndex
                        );

                EarthlyBranch firstBranch =
                        first
                                .pillar()
                                .branch();

                EarthlyBranch secondBranch =
                        second
                                .pillar()
                                .branch();

                Optional<FiveElement>
                        sixCombinationElement =
                        BranchRelationTable
                                .findSixCombinationElement(
                                        firstBranch,
                                        secondBranch
                                );

                sixCombinationElement.ifPresent(
                        element ->
                                result.add(
                                        createPairRelation(
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
                            createPairRelation(
                                    BranchRelationType
                                            .CLASH,
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
                            createPairRelation(
                                    BranchRelationType
                                            .PUNISHMENT,
                                    first,
                                    second,
                                    null
                            )
                    );
                }

                if (
                        firstBranch
                                == secondBranch
                                && BranchRelationTable
                                .isSelfPunishmentBranch(
                                        firstBranch
                                )
                ) {

                    result.add(
                            createPairRelation(
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
                            createPairRelation(
                                    BranchRelationType
                                            .HARM,
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
                            createPairRelation(
                                    BranchRelationType
                                            .BREAK,
                                    first,
                                    second,
                                    null
                            )
                    );
                }
            }
        }
    }

    /**
     * 삼합 / 삼형.
     */
    private void analyzeBranchTriples(
            List<PositionedPillar> pillars,
            List<BranchRelation> result
    ) {

        if (
                pillars.size() < 3
        ) {

            return;
        }

        for (
                int firstIndex = 0;
                firstIndex < pillars.size() - 2;
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < pillars.size() - 1;
                    secondIndex++
            ) {

                for (
                        int thirdIndex =
                        secondIndex + 1;
                        thirdIndex < pillars.size();
                        thirdIndex++
                ) {

                    PositionedPillar first =
                            pillars.get(
                                    firstIndex
                            );

                    PositionedPillar second =
                            pillars.get(
                                    secondIndex
                            );

                    PositionedPillar third =
                            pillars.get(
                                    thirdIndex
                            );

                    EarthlyBranch firstBranch =
                            first
                                    .pillar()
                                    .branch();

                    EarthlyBranch secondBranch =
                            second
                                    .pillar()
                                    .branch();

                    EarthlyBranch thirdBranch =
                            third
                                    .pillar()
                                    .branch();

                    Optional<FiveElement>
                            threeHarmonyElement =
                            BranchRelationTable
                                    .findThreeHarmonyElement(
                                            firstBranch,
                                            secondBranch,
                                            thirdBranch
                                    );

                    threeHarmonyElement.ifPresent(
                            element ->
                                    result.add(
                                            createTripleRelation(
                                                    BranchRelationType
                                                            .THREE_HARMONY,
                                                    first,
                                                    second,
                                                    third,
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
                                createTripleRelation(
                                        BranchRelationType
                                                .PUNISHMENT,
                                        first,
                                        second,
                                        third,
                                        null
                                )
                        );
                    }
                }
            }
        }
    }

    private BranchRelation createPairRelation(
            BranchRelationType type,
            PositionedPillar first,
            PositionedPillar second,
            FiveElement combinationElement
    ) {

        return new BranchRelation(
                type,
                List.of(
                        first.position(),
                        second.position()
                ),
                List.of(
                        first
                                .pillar()
                                .branch(),
                        second
                                .pillar()
                                .branch()
                ),
                combinationElement
        );
    }

    private BranchRelation createTripleRelation(
            BranchRelationType type,
            PositionedPillar first,
            PositionedPillar second,
            PositionedPillar third,
            FiveElement combinationElement
    ) {

        return new BranchRelation(
                type,
                List.of(
                        first.position(),
                        second.position(),
                        third.position()
                ),
                List.of(
                        first
                                .pillar()
                                .branch(),
                        second
                                .pillar()
                                .branch(),
                        third
                                .pillar()
                                .branch()
                ),
                combinationElement
        );
    }

    /**
     * Pillar와 그 위치를 함께 들고 다니기 위한
     * Analyzer 내부 전용 데이터.
     */
    private record PositionedPillar(

            PillarPosition position,

            Pillar pillar

    ) {
    }
}