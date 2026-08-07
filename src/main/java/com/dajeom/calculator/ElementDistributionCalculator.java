package com.dajeom.calculator;

import com.dajeom.model.ElementDistribution;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.Pillar;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 사주 원국의 오행 원시 개수를 계산한다.
 *
 * 이 계산은 단순 분포 계산이다.
 * 월령, 통근, 투간, 계절 가중치는 적용하지 않는다.
 */
@Component
public class ElementDistributionCalculator {

    public ElementDistribution calculate(
            FourPillars fourPillars,
            List<HiddenStem> hiddenStems
    ) {
        Map<FiveElement, Integer> visibleStemCounts =
                createEmptyCounts();

        Map<FiveElement, Integer> branchMainCounts =
                createEmptyCounts();

        Map<FiveElement, Integer> hiddenStemCounts =
                createEmptyCounts();

        List<Pillar> pillars = List.of(
                fourPillars.yearPillar(),
                fourPillars.monthPillar(),
                fourPillars.dayPillar(),
                fourPillars.hourPillar()
        );

        for (Pillar pillar : pillars) {
            increment(
                    visibleStemCounts,
                    pillar.stem().getElement()
            );

            increment(
                    branchMainCounts,
                    pillar.branch().getElement()
            );
        }

        for (HiddenStem hiddenStem : hiddenStems) {
            increment(
                    hiddenStemCounts,
                    hiddenStem.stem().getElement()
            );
        }

        Map<FiveElement, Integer> eightCharacterCounts =
                createEmptyCounts();

        for (FiveElement element : FiveElement.values()) {
            int visibleCount =
                    visibleStemCounts.get(element);

            int branchCount =
                    branchMainCounts.get(element);

            eightCharacterCounts.put(
                    element,
                    visibleCount + branchCount
            );
        }

        return new ElementDistribution(
                visibleStemCounts,
                branchMainCounts,
                eightCharacterCounts,
                hiddenStemCounts
        );
    }

    private Map<FiveElement, Integer> createEmptyCounts() {
        EnumMap<FiveElement, Integer> counts =
                new EnumMap<>(FiveElement.class);

        for (FiveElement element : FiveElement.values()) {
            counts.put(element, 0);
        }

        return counts;
    }

    private void increment(
            Map<FiveElement, Integer> counts,
            FiveElement element
    ) {
        counts.compute(
                element,
                (key, currentValue) ->
                        currentValue == null
                                ? 1
                                : currentValue + 1
        );
    }
}