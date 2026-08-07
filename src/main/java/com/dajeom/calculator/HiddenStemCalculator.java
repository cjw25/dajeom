package com.dajeom.calculator;

import com.dajeom.constant.HiddenStemTable;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 사주 네 지지의 지장간을 계산한다.
 */
@Component
public class HiddenStemCalculator {

    public List<HiddenStem> calculate(
            FourPillars fourPillars
    ) {
        List<HiddenStem> result =
                new ArrayList<>();

        addHiddenStems(
                result,
                PillarPosition.YEAR_BRANCH,
                fourPillars.yearPillar().branch()
        );

        addHiddenStems(
                result,
                PillarPosition.MONTH_BRANCH,
                fourPillars.monthPillar().branch()
        );

        addHiddenStems(
                result,
                PillarPosition.DAY_BRANCH,
                fourPillars.dayPillar().branch()
        );

        addHiddenStems(
                result,
                PillarPosition.HOUR_BRANCH,
                fourPillars.hourPillar().branch()
        );

        return List.copyOf(result);
    }

    private void addHiddenStems(
            List<HiddenStem> destination,
            PillarPosition position,
            EarthlyBranch branch
    ) {
        HiddenStemTable.get(branch)
                .forEach(definition ->
                        destination.add(
                                new HiddenStem(
                                        position,
                                        definition.stem(),
                                        definition.role()
                                )
                        )
                );
    }
}