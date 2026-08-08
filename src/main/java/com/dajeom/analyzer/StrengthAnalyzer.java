package com.dajeom.analyzer;

import com.dajeom.model.ElementStrength;
import com.dajeom.model.NatalChartBaseAnalysis;
import com.dajeom.model.StrengthAnalysis;
import com.dajeom.rule.StrengthRule;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StrengthLevel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 기본 강약을 분석한다.
 *
 * 기본 흐름:
 *
 * NatalChartBaseAnalysis
 *      ↓
 * ElementStrength
 *      ↓
 * StrengthAnalysis
 *
 * 일간과 같은 오행
 * +
 * 일간을 생해주는 오행
 *
 * 을 일간의 지원 세력으로 본다.
 *
 * 이 분석은 V1 강약 분석이며
 * 이후 통근, 합화, 구조 분석 등의 결과를
 * 추가적으로 반영할 수 있도록 분리되어 있다.
 */
@Component
public class StrengthAnalyzer {

    private final ElementStrengthAnalyzer
            elementStrengthAnalyzer;

    public StrengthAnalyzer(
            ElementStrengthAnalyzer elementStrengthAnalyzer
    ) {

        this.elementStrengthAnalyzer =
                elementStrengthAnalyzer;
    }

    /**
     * 원국 기초 분석만 전달받는 편의 메서드.
     *
     * 내부에서 ElementStrength를 계산한 뒤
     * 실제 강약 분석 메서드로 전달한다.
     */
    public StrengthAnalysis analyze(
            NatalChartBaseAnalysis baseAnalysis
    ) {

        Objects.requireNonNull(
                baseAnalysis,
                "원국 기초 분석 결과는 필수입니다."
        );

        ElementStrength elementStrength =
                elementStrengthAnalyzer.analyze(
                        baseAnalysis
                );

        return analyze(
                baseAnalysis,
                elementStrength
        );
    }

    /**
     * 이미 계산된 ElementStrength를 사용해
     * 강약을 분석한다.
     *
     * NatalChartAnalysisEngine에서는
     * 오행 세력을 중복 계산하지 않기 위해
     * 이 메서드를 사용한다.
     */
    public StrengthAnalysis analyze(
            NatalChartBaseAnalysis baseAnalysis,
            ElementStrength elementStrength
    ) {

        Objects.requireNonNull(
                baseAnalysis,
                "원국 기초 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석 결과는 필수입니다."
        );

        FiveElement dayMasterElement =
                baseAnalysis
                        .dayMaster()
                        .getElement();

        FiveElement resourceElement =
                StrengthRule.resourceElementOf(
                        dayMasterElement
                );

        List<FiveElement> supportingElements =
                StrengthRule.supportingElementsOf(
                        dayMasterElement
                );

        List<FiveElement> challengingElements =
                StrengthRule.challengingElementsOf(
                        dayMasterElement
                );

        double supportScore =
                supportingElements
                        .stream()
                        .mapToDouble(
                                elementStrength::scoreOf
                        )
                        .sum();

        double challengeScore =
                challengingElements
                        .stream()
                        .mapToDouble(
                                elementStrength::scoreOf
                        )
                        .sum();

        double totalScore =
                supportScore
                        + challengeScore;

        double supportRatio;

        if (totalScore <= 0.0) {

            /*
             * 계산 가능한 세력이 전혀 없는
             * 비정상적인 상황에서는 중립값을 사용한다.
             */
            supportRatio =
                    50.0;

        } else {

            supportRatio =
                    (
                            supportScore
                                    / totalScore
                    )
                            * 100.0;
        }

        StrengthLevel level =
                StrengthRule.classify(
                        supportRatio
                );

        return new StrengthAnalysis(
                dayMasterElement,
                resourceElement,
                supportingElements,
                challengingElements,
                supportScore,
                challengeScore,
                supportRatio,
                level,
                baseAnalysis.hasHourPillar(),
                StrengthRule.RULE_VERSION
        );
    }
}