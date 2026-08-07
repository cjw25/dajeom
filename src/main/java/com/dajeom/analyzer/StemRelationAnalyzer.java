package com.dajeom.analyzer;

import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.FourPillars;
import com.dajeom.model.Pillar;
import com.dajeom.model.StemRelation;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.RelationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 원국의 천간합과 천간극을 분석한다.
 */
@Component
public class StemRelationAnalyzer {

    public List<StemRelation> analyze(
            FourPillars fourPillars
    ) {
        List<StemPosition> stems =
                createStemPositions(fourPillars);

        List<StemRelation> result =
                new ArrayList<>();

        for (int firstIndex = 0;
             firstIndex < stems.size();
             firstIndex++) {

            for (int secondIndex = firstIndex + 1;
                 secondIndex < stems.size();
                 secondIndex++) {

                StemPosition first =
                        stems.get(firstIndex);

                StemPosition second =
                        stems.get(secondIndex);

                analyzeCombination(
                        result,
                        first,
                        second
                );

                analyzeControl(
                        result,
                        first,
                        second
                );
            }
        }

        return List.copyOf(result);
    }

    private void analyzeCombination(
            List<StemRelation> destination,
            StemPosition first,
            StemPosition second
    ) {
        StemRelationTable.findCombination(
                        first.stem(),
                        second.stem()
                )
                .ifPresent(combination ->
                        destination.add(
                                new StemRelation(
                                        RelationType
                                                .STEM_COMBINATION,

                                        first.position(),

                                        first.stem(),

                                        second.position(),

                                        second.stem(),

                                        combination
                                                .transformationElement(),

                                        true,

                                        false,

                                        first.stem()
                                                .getChineseCharacter()
                                                + second.stem()
                                                .getChineseCharacter()
                                                + " 천간합이 존재하지만 "
                                                + "4차에서는 합화 후보로만 판정함"
                                )
                        )
                );
    }

    private void analyzeControl(
            List<StemRelation> destination,
            StemPosition first,
            StemPosition second
    ) {
        HeavenlyStem controller;
        HeavenlyStem controlled;

        PillarPosition controllerPosition;
        PillarPosition controlledPosition;

        if (first.stem()
                .getElement()
                .controls(
                        second.stem().getElement()
                )) {

            controller = first.stem();
            controlled = second.stem();

            controllerPosition = first.position();
            controlledPosition = second.position();

        } else if (second.stem()
                .getElement()
                .controls(
                        first.stem().getElement()
                )) {

            controller = second.stem();
            controlled = first.stem();

            controllerPosition = second.position();
            controlledPosition = first.position();

        } else {
            return;
        }

        destination.add(
                new StemRelation(
                        RelationType.STEM_CONTROL,

                        controllerPosition,

                        controller,

                        controlledPosition,

                        controlled,

                        null,

                        false,

                        false,

                        controller.getChineseCharacter()
                                + "이(가) "
                                + controlled.getChineseCharacter()
                                + "을(를) 극함"
                )
        );
    }

    private List<StemPosition> createStemPositions(
            FourPillars fourPillars
    ) {
        return List.of(
                position(
                        PillarPosition.YEAR_STEM,
                        fourPillars.yearPillar()
                ),

                position(
                        PillarPosition.MONTH_STEM,
                        fourPillars.monthPillar()
                ),

                position(
                        PillarPosition.DAY_STEM,
                        fourPillars.dayPillar()
                ),

                position(
                        PillarPosition.HOUR_STEM,
                        fourPillars.hourPillar()
                )
        );
    }

    private StemPosition position(
            PillarPosition position,
            Pillar pillar
    ) {
        return new StemPosition(
                position,
                pillar.stem()
        );
    }

    private record StemPosition(
            PillarPosition position,
            HeavenlyStem stem
    ) {
    }
}