package com.dajeom.calculation;

import com.dajeom.domain.FourPillars;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.PillarPosition;
import com.dajeom.domain.StemRelation;
import com.dajeom.domain.StemRelationType;
import com.dajeom.rule.SajuRuleBook;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class StemRelationCalculator {

    public List<StemRelation> calculate(
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

        List<StemRelation> result =
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

                HeavenlyStem leftStem =
                        left.pillar().stem();

                HeavenlyStem rightStem =
                        right.pillar().stem();

                List<StemRelationType> types =
                        SajuRuleBook.stemRelations(
                                leftStem,
                                rightStem
                        );

                for (StemRelationType type : types) {

                    result.add(
                            new StemRelation(
                                    left.position(),
                                    leftStem,
                                    right.position(),
                                    rightStem,
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