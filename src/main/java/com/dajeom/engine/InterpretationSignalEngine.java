package com.dajeom.engine;

import com.dajeom.analyzer.CategorySignalAnalyzer;
import com.dajeom.model.CategoryInterpretation;
import com.dajeom.model.FourPillars;
import com.dajeom.model.InterpretationSignalAnalysis;
import com.dajeom.model.LuckAnalysis;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.NatalChartAnalysis;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 사주 분석 결과를 카테고리별 해석 신호로 변환한다.
 */
@Component
public class InterpretationSignalEngine {

    public static final String RULE_VERSION =
            "DAJEOM_INTERPRETATION_SIGNAL_V1";

    private final CategorySignalAnalyzer
            categorySignalAnalyzer;

    public InterpretationSignalEngine(
            CategorySignalAnalyzer
                    categorySignalAnalyzer
    ) {
        this.categorySignalAnalyzer =
                categorySignalAnalyzer;
    }

    public InterpretationSignalAnalysis analyze(
            FourPillars fourPillars,
            NatalChartAnalysis natalChartAnalysis,
            LuckAnalysis luckAnalysis,
            LuckInteractionAnalysis
                    luckInteractionAnalysis
    ) {
        List<CategoryInterpretation> categories =
                categorySignalAnalyzer.analyze(
                        fourPillars,
                        natalChartAnalysis,
                        luckAnalysis,
                        luckInteractionAnalysis
                );

        int totalSignalCount =
                categories.stream()
                        .mapToInt(category ->
                                category
                                        .signals()
                                        .size()
                        )
                        .sum();

        return new InterpretationSignalAnalysis(
                categories,
                totalSignalCount,
                RULE_VERSION
        );
    }
}