package com.dajeom.analyzer;

import com.dajeom.model.ElementDistribution;
import com.dajeom.model.ElementStrength;
import com.dajeom.model.HiddenStemAnalysis;
import com.dajeom.model.NatalChartBaseAnalysis;
import com.dajeom.model.PillarAnalysis;
import com.dajeom.rule.StrengthRule;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 원국의 오행 세력을 계산한다.
 *
 * 입력:
 * NatalChartBaseAnalysis
 *
 * 반영 요소:
 *
 * 1. 천간/지지의 기본 오행
 * 2. 지장간
 * 3. 지장간 역할
 * 4. 월지의 계절 영향
 *
 * 아직:
 *
 * - 합화
 * - 충에 의한 변화
 * - 통근 세부 판정
 * - 조후
 * - 용신
 *
 * 등은 반영하지 않는다.
 *
 * 해당 요소들은 이후 Analyzer 단계에서
 * 별도로 확장한다.
 */
@Component
public class ElementStrengthAnalyzer {

    public ElementStrength analyze(
            NatalChartBaseAnalysis baseAnalysis
    ) {

        Objects.requireNonNull(
                baseAnalysis,
                "원국 기초 분석 결과는 필수입니다."
        );

        EnumMap<
                FiveElement,
                Double
                > scores =
                createEmptyScores();

        /*
         * 1.
         * 기존 기본 오행 분포를
         * 표면 세력으로 반영한다.
         */
        addVisibleElements(
                scores,
                baseAnalysis
                        .elementDistribution()
        );

        /*
         * 2.
         * 각 지지의 지장간을 추가 반영한다.
         */
        for (
                PillarAnalysis pillarAnalysis
                : baseAnalysis.pillars()
        ) {

            addHiddenStems(
                    scores,
                    pillarAnalysis
            );
        }

        /*
         * 3.
         * 월령의 계절적 세력을 추가한다.
         *
         * 월지는 일반 지지와 달리
         * 원국 전체의 계절적 환경을
         * 나타내기 때문에 별도 가중치를 준다.
         */
        FiveElement monthElement =
                baseAnalysis
                        .fourPillars()
                        .monthPillar()
                        .branch()
                        .getElement();

        addScore(
                scores,
                monthElement,
                StrengthRule
                        .MONTH_COMMAND_BONUS
        );

        return new ElementStrength(
                scores
        );
    }

    private EnumMap<
            FiveElement,
            Double
            > createEmptyScores() {

        EnumMap<
                FiveElement,
                Double
                > scores =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            scores.put(
                    element,
                    0.0
            );
        }

        return scores;
    }

    /**
     * 천간 + 지지의 기본 오행을 반영한다.
     */
    private void addVisibleElements(
            Map<FiveElement, Double> scores,
            ElementDistribution distribution
    ) {

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            double score =
                    distribution
                            .countOf(
                                    element
                            )
                            * StrengthRule
                            .VISIBLE_ELEMENT_WEIGHT;

            addScore(
                    scores,
                    element,
                    score
            );
        }
    }

    /**
     * 각 지지의 지장간 세력을 반영한다.
     */
    private void addHiddenStems(
            Map<FiveElement, Double> scores,
            PillarAnalysis pillarAnalysis
    ) {

        for (
                HiddenStemAnalysis hiddenStemAnalysis
                : pillarAnalysis.hiddenStems()
        ) {

            FiveElement element =
                    hiddenStemAnalysis
                            .hiddenStem()
                            .stem()
                            .getElement();

            double weight =
                    StrengthRule
                            .hiddenStemWeight(
                                    hiddenStemAnalysis
                                            .hiddenStem()
                                            .role()
                            );

            addScore(
                    scores,
                    element,
                    weight
            );
        }
    }

    private void addScore(
            Map<FiveElement, Double> scores,
            FiveElement element,
            double score
    ) {

        scores.merge(
                element,
                score,
                Double::sum
        );
    }
}