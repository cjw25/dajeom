package com.dajeom.calculation;

import com.dajeom.domain.BranchRelation;
import com.dajeom.domain.BranchRelationType;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FourPillars;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.PillarPosition;
import com.dajeom.rule.SajuRuleBook;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class BranchRelationCalculator {

    public List<BranchRelation> calculate(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 네 기둥이 필요합니다."
        );

        List<PositionedPillar> pillars =
                collectPillars(
                        fourPillars
                );

        List<BranchRelation> result =
                new ArrayList<>();

        for (int i = 0; i < pillars.size(); i++) {

            PositionedPillar left =
                    pillars.get(i);

            for (
                    int j = i + 1;
                    j < pillars.size();
                    j++
            ) {

                PositionedPillar right =
                        pillars.get(j);

                EarthlyBranch leftBranch =
                        left.pillar().branch();

                EarthlyBranch rightBranch =
                        right.pillar().branch();

                List<BranchRelationType> types =
                        SajuRuleBook.branchRelations(
                                leftBranch,
                                rightBranch
                        );

                for (BranchRelationType type : types) {

                    result.add(
                            new BranchRelation(
                                    left.position(),
                                    leftBranch,
                                    right.position(),
                                    rightBranch,
                                    type
                            )
                    );
                }
            }
        }

        return List.copyOf(
                result
        );
    }

    private List<PositionedPillar> collectPillars(
            FourPillars fourPillars
    ) {

        List<PositionedPillar> result =
                new ArrayList<>();

        result.add(
                new PositionedPillar(
                        PillarPosition.YEAR,
                        fourPillars.year()
                )
        );

        result.add(
                new PositionedPillar(
                        PillarPosition.MONTH,
                        fourPillars.month()
                )
        );

        result.add(
                new PositionedPillar(
                        PillarPosition.DAY,
                        fourPillars.day()
                )
        );

        if (fourPillars.hasKnownHour()) {

            result.add(
                    new PositionedPillar(
                            PillarPosition.HOUR,
                            fourPillars.hour()
                    )
            );
        }

        return result;
    }

    private record PositionedPillar(
            PillarPosition position,
            Pillar pillar
    ) {
    }
}