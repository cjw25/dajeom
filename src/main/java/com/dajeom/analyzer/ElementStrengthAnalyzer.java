package com.dajeom.analyzer;

import com.dajeom.constant.ElementStrengthWeightTable;
import com.dajeom.model.ElementStrength;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.Pillar;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 천간, 지장간 역할, 월지 가중치를 반영해
 * 오행별 세력을 계산한다.
 *
 * 지지 대표 오행과 지장간을 동시에 더하면
 * 같은 기운을 중복 계산할 수 있으므로,
 * 지지의 세력은 지장간을 기준으로 계산한다.
 */
@Component
public class ElementStrengthAnalyzer {

    public ElementStrength analyze(
            FourPillars fourPillars,
            List<HiddenStem> hiddenStems
    ) {
        Map<FiveElement, Double> rawScores =
                createEmptyScores();

        addVisibleStemScores(
                rawScores,
                fourPillars
        );

        addHiddenStemScores(
                rawScores,
                hiddenStems
        );

        double totalScore =
                rawScores.values()
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();

        Map<FiveElement, Double> percentages =
                calculatePercentages(
                        rawScores,
                        totalScore
                );

        FiveElement strongestElement =
                findStrongestElement(rawScores);

        FiveElement weakestElement =
                findWeakestElement(rawScores);

        return new ElementStrength(
                rawScores,
                percentages,
                strongestElement,
                weakestElement,
                round(totalScore),
                ElementStrengthWeightTable.RULE_VERSION
        );
    }

    private void addVisibleStemScores(
            Map<FiveElement, Double> scores,
            FourPillars fourPillars
    ) {
        List<Pillar> pillars = List.of(
                fourPillars.yearPillar(),
                fourPillars.monthPillar(),
                fourPillars.dayPillar(),
                fourPillars.hourPillar()
        );

        for (Pillar pillar : pillars) {
            addScore(
                    scores,
                    pillar.stem().getElement(),
                    ElementStrengthWeightTable
                            .VISIBLE_STEM_WEIGHT
            );
        }
    }

    private void addHiddenStemScores(
            Map<FiveElement, Double> scores,
            List<HiddenStem> hiddenStems
    ) {
        for (HiddenStem hiddenStem : hiddenStems) {
            double roleWeight =
                    ElementStrengthWeightTable
                            .hiddenStemWeight(
                                    hiddenStem.role()
                            );

            double branchMultiplier =
                    hiddenStem.branchPosition()
                            == PillarPosition.MONTH_BRANCH
                            ? ElementStrengthWeightTable
                            .MONTH_BRANCH_MULTIPLIER
                            : ElementStrengthWeightTable
                            .NORMAL_BRANCH_MULTIPLIER;

            double finalWeight =
                    roleWeight * branchMultiplier;

            addScore(
                    scores,
                    hiddenStem
                            .stem()
                            .getElement(),
                    finalWeight
            );
        }
    }

    private Map<FiveElement, Double>
    calculatePercentages(
            Map<FiveElement, Double> rawScores,
            double totalScore
    ) {
        EnumMap<FiveElement, Double> percentages =
                new EnumMap<>(FiveElement.class);

        for (FiveElement element : FiveElement.values()) {
            double score =
                    rawScores.getOrDefault(
                            element,
                            0.0
                    );

            double percentage =
                    totalScore == 0.0
                            ? 0.0
                            : score / totalScore * 100.0;

            percentages.put(
                    element,
                    round(percentage)
            );
        }

        return percentages;
    }

    private FiveElement findStrongestElement(
            Map<FiveElement, Double> scores
    ) {
        return scores.entrySet()
                .stream()
                .max(
                        Map.Entry.comparingByValue()
                )
                .map(Map.Entry::getKey)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "가장 강한 오행을 찾을 수 없습니다."
                        )
                );
    }

    private FiveElement findWeakestElement(
            Map<FiveElement, Double> scores
    ) {
        return scores.entrySet()
                .stream()
                .min(
                        Comparator.comparingDouble(
                                Map.Entry::getValue
                        )
                )
                .map(Map.Entry::getKey)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "가장 약한 오행을 찾을 수 없습니다."
                        )
                );
    }

    private Map<FiveElement, Double>
    createEmptyScores() {
        EnumMap<FiveElement, Double> scores =
                new EnumMap<>(FiveElement.class);

        for (FiveElement element : FiveElement.values()) {
            scores.put(element, 0.0);
        }

        return scores;
    }

    private void addScore(
            Map<FiveElement, Double> scores,
            FiveElement element,
            double amount
    ) {
        scores.compute(
                element,
                (key, currentValue) ->
                        currentValue == null
                                ? amount
                                : currentValue + amount
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0)
                / 100.0;
    }
}